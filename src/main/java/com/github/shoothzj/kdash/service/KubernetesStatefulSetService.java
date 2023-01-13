/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.shoothzj.kdash.service;

import com.github.shoothzj.kdash.module.ContainerInfo;
import com.github.shoothzj.kdash.module.CreateStatefulSetParam;
import com.github.shoothzj.kdash.module.GetStatefulSetResp;
import com.github.shoothzj.kdash.module.ScaleReq;
import com.github.shoothzj.kdash.module.VolumeClaimTemplates;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Affinity;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Scale;
import io.kubernetes.client.openapi.models.V1ScaleSpec;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1StatefulSetSpec;
import io.kubernetes.client.openapi.models.V1StatefulSetStatus;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class KubernetesStatefulSetService {

    private final AppsV1Api appsV1Api;

    public KubernetesStatefulSetService(@Autowired ApiClient apiClient) {
        this.appsV1Api = new AppsV1Api(apiClient);
    }

    public void createNamespacedStatefulSet(String namespace, CreateStatefulSetParam req) throws ApiException {
        // deploy
        V1StatefulSet v1StatefulSet = new V1StatefulSet();
        v1StatefulSet.setApiVersion("apps/v1");
        v1StatefulSet.setKind("StatefulSet");
        Map<String, String> labels = KubernetesUtil.label(req.getStatefulSetName());
        if (req.getLabels() != null) {
            labels.putAll(req.getLabels());
        }

        {
            // meta
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(req.getStatefulSetName());
            v1ObjectMeta.setNamespace(namespace);
            if (req.getLabels().size() != 0) {
                v1ObjectMeta.setLabels(req.getLabels());
            }
            if (req.getAnnotations() != null && req.getAnnotations().size() != 0) {
                v1ObjectMeta.setAnnotations(req.getAnnotations());
            }
            v1StatefulSet.setMetadata(v1ObjectMeta);
        }

        {
            // spec
            V1StatefulSetSpec statefulSetSpec = new V1StatefulSetSpec();
            // spec replicas
            statefulSetSpec.setReplicas(req.getReplicas());

            if (req.getStatefulSetUpdateStrategy() != null) {
                statefulSetSpec.setUpdateStrategy(req.getStatefulSetUpdateStrategy());
            }
            // spec selector
            statefulSetSpec.setSelector(KubernetesUtil.labelSelector(req.getStatefulSetName(), labels));
            // spec template
            V1PodTemplateSpec templateSpec = new V1PodTemplateSpec();
            {
                // object metadata
                V1ObjectMeta objectMeta = new V1ObjectMeta();
                objectMeta.setLabels(KubernetesUtil.label(req.getStatefulSetName()));
                templateSpec.setMetadata(objectMeta);
            }
            // spec template spec
            V1PodSpec v1PodSpec = new V1PodSpec();
            {
                // spec template spec containers
                List<V1Container> v1Containers = KubernetesUtil.singleContainerList(req.getImage(), req.getEnv(),
                        req.getStatefulSetName(), req.getResourceRequirements(), null,
                        KubernetesUtil.v1Probe(req.getLivenessProbe()),
                        KubernetesUtil.v1Probe(req.getReadinessProbe()));
                if (req.getPersistentVolumes() != null) {
                    V1Container v1Container = v1Containers.get(0);
                    List<V1VolumeMount> volumeMounts = new ArrayList<>();
                    for (VolumeClaimTemplates persistentVolume : req.getPersistentVolumes()) {
                        V1VolumeMount v1VolumeMount = new V1VolumeMount();
                        v1VolumeMount.setName(persistentVolume.getVolumeName());
                        v1VolumeMount.setMountPath(persistentVolume.getMountPath());
                        volumeMounts.add(v1VolumeMount);
                    }

                    v1Container.setVolumeMounts(volumeMounts);
                    v1PodSpec.setContainers(v1Containers);
                }
            }
            {
                V1Affinity v1Affinity = new V1Affinity();
                v1Affinity.setPodAffinity(KubernetesUtil.v1PodAffinity(req.getPodAffinityTerms()));
                v1Affinity.setPodAntiAffinity(KubernetesUtil.v1PodAntiAffinity(req.getPodAntiAffinityTerms()));
                v1Affinity.setNodeAffinity(KubernetesUtil.v1NodeAffinity(req.getNodeSelectorRequirement()));
                v1PodSpec.setAffinity(v1Affinity);
            }
            v1PodSpec.setImagePullSecrets(KubernetesUtil.imagePullSecrets(req.getImagePullSecret()));
            templateSpec.setSpec(v1PodSpec);
            statefulSetSpec.setTemplate(templateSpec);
            statefulSetSpec.setPodManagementPolicy(req.getPodManagementPolicy());
            v1StatefulSet.setSpec(statefulSetSpec);

            if (req.getPersistentVolumes() != null) {
                List<V1PersistentVolumeClaim> volumeClaimTemplates = new ArrayList<>();
                for (VolumeClaimTemplates persistentVolume : req.getPersistentVolumes()) {
                    V1PersistentVolumeClaim v1PersistentVolumeClaim = new V1PersistentVolumeClaim();
                    v1PersistentVolumeClaim.setKind("PersistentVolumeClaim");
                    V1ObjectMeta metadata = new V1ObjectMeta();
                    metadata.setNamespace(namespace);
                    metadata.setAnnotations(req.getAnnotations());
                    metadata.setName(persistentVolume.getVolumeName());
                    v1PersistentVolumeClaim.setMetadata(metadata);
                    volumeClaimTemplates.add(v1PersistentVolumeClaim);
                }
                statefulSetSpec.setVolumeClaimTemplates(volumeClaimTemplates);
            }
        }

        appsV1Api.createNamespacedStatefulSet(namespace, v1StatefulSet,
                "true", null, null, null);
    }

    public void createNamespacedStatefulSetByYaml(String namespace, String yamlContent)
            throws IOException, ApiException {
        V1StatefulSet v1StatefulSet = (V1StatefulSet) Yaml.load(yamlContent);
        appsV1Api.createNamespacedStatefulSet(namespace, v1StatefulSet,
                "true", null, null, null);
    }

    public void deleteStatefulSet(String namespace, String statefulSetName) throws ApiException {
        appsV1Api.deleteNamespacedStatefulSet(statefulSetName, namespace, "true",
                null, 30, false, null, null);
    }

    public List<GetStatefulSetResp> getNamespaceStatefulSets(String namespace) throws ApiException {
        V1StatefulSetList v1StatefulSetList = appsV1Api.listNamespacedStatefulSet(namespace, "true",
                null, null, null, null,
                null, null, null, null, null);
        List<GetStatefulSetResp> result = new ArrayList<>();
        for (V1StatefulSet items : v1StatefulSetList.getItems()) {
            result.add(convert(items));
        }
        return result;
    }

    private GetStatefulSetResp convert(V1StatefulSet statefulSet) {
        GetStatefulSetResp statefulSetResp = new GetStatefulSetResp();
        V1ObjectMeta metadata = statefulSet.getMetadata();
        if (metadata != null) {
            statefulSetResp.setStatefulSetName(metadata.getName());
            OffsetDateTime timestamp = metadata.getCreationTimestamp();
            if (timestamp != null) {
                statefulSetResp.setCreationTimestamp(timestamp.format(DateTimeFormatter.ISO_DATE_TIME));
            }
        }
        V1StatefulSetStatus status = statefulSet.getStatus();
        if (status != null) {
            statefulSetResp.setReplicas(status.getReplicas() == null ? 0 : status.getReplicas());
            statefulSetResp.setAvailableReplicas(status.getAvailableReplicas() == null
                    ? 0 : status.getAvailableReplicas());
        }
        V1StatefulSetSpec v1StatefulSetSpec = statefulSet.getSpec();
        if (v1StatefulSetSpec != null) {
            V1PodSpec v1PodSpec = v1StatefulSetSpec.getTemplate().getSpec();
            if (v1PodSpec == null) {
                return statefulSetResp;
            }
            List<V1Container> containers = v1PodSpec.getContainers();
            List<ContainerInfo> containerInfoList = KubernetesUtil.containerInfoList(containers);
            statefulSetResp.setContainerInfoList(containerInfoList);
        }
        return statefulSetResp;
    }

    public void scaleStatefulSet(String namespace, ScaleReq req) throws ApiException {
        V1Scale v1Scale = new V1Scale();
        V1ScaleSpec v1ScaleSpec = new V1ScaleSpec();
        v1ScaleSpec.setReplicas(req.getReplicas());
        v1Scale.setSpec(v1ScaleSpec);
        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.setName(req.getAppName());
        objectMeta.setNamespace(namespace);
        objectMeta.setLabels(KubernetesUtil.label(req.getAppName()));
        v1Scale.setMetadata(objectMeta);
        appsV1Api.replaceNamespacedStatefulSetScale(req.getAppName(), namespace, v1Scale, "true",
                null, null, null);
    }

    public GetStatefulSetResp getNamespaceStatefulSet(String namespace, String statefulSetName) throws ApiException {
        List<GetStatefulSetResp> list = getNamespaceStatefulSets(namespace);
        for (GetStatefulSetResp sts : list) {
            if (Objects.equals(sts.getStatefulSetName(), statefulSetName)) {
                return sts;
            }
        }
        return null;
    }
}
