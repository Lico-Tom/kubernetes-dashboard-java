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

import com.github.shoothzj.kdash.module.CreatePodParam;
import com.github.shoothzj.kdash.module.GetPodResp;
import com.github.shoothzj.kdash.module.ResourceReq;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KubernetesPodService {

    private static String jsonStr = "["
            + "{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/resources/limits/cpu\","
            + "\"value\":\"%s\"},"
            + "{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/resources/limits/memory\","
            + "\"value\":\"%s\"},"
            + "{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/resources/requests/memory\","
            + "\"value\":\"%s\"}, "
            + "{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/resources/requests/memory\","
            + "\"value\":\"%s\"}]";

    private final CoreV1Api coreV1Api;

    private final AppsV1Api appsV1Api;

    public KubernetesPodService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
        this.appsV1Api = new AppsV1Api(apiClient);
    }

    public void createPod(String namespace, CreatePodParam req) throws Exception {
        // pod
        V1Pod v1Pod = new V1Pod();
        v1Pod.setApiVersion("v1");
        v1Pod.setKind("Pod");

        {
            // metadata
            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(req.getPodName());
            metadata.setNamespace(namespace);
            metadata.setLabels(KubernetesUtil.label(req.getPodName()));
            v1Pod.setMetadata(metadata);
        }

        {
            // spec
            V1PodSpec v1PodSpec = new V1PodSpec();
            v1PodSpec.setContainers(KubernetesUtil.singleContainerList(req.getImage(),
                    req.getEnv(), req.getPodName(), req.getResourceRequirements()));
            v1PodSpec.setImagePullSecrets(KubernetesUtil.imagePullSecrets(req.getImagePullSecret()));
            v1Pod.setSpec(v1PodSpec);
        }

        coreV1Api.createNamespacedPod(namespace, v1Pod, "true", null, null, null);
    }

    public void deletePod(String namespace, String podName) throws ApiException {
        coreV1Api.deleteNamespacedPod(podName, namespace, "true", null,
                null, null, null, null);
    }

    public List<GetPodResp> getNamespacePods(String namespace) throws ApiException {
        V1PodList podList = coreV1Api.listNamespacedPod(namespace, "true", null, null,
                null, null, null, null, null,
                null, null);
        List<GetPodResp> result = new ArrayList<>();
        for (V1Pod item : podList.getItems()) {
            result.add(convert(item));
        }
        return result;
    }

    public List<String> getNamespacePodIp(String namespace, String labelSelector) throws ApiException {
        V1PodList podList = coreV1Api.listNamespacedPod(namespace, "true", null, null,
                null, labelSelector, null, null, null,
                null, null);
        List<String> result = new ArrayList<>();
        for (V1Pod item : podList.getItems()) {
            V1PodStatus status = item.getStatus();
            if (status != null) {
                result.add(status.getPodIP());
            }
        }
        return result;
    }

    private GetPodResp convert(V1Pod v1Pod) {
        GetPodResp podResp = new GetPodResp();

        V1ObjectMeta metadata = v1Pod.getMetadata();
        if (metadata != null) {
            podResp.setPodName(metadata.getName());
            OffsetDateTime timestamp = metadata.getCreationTimestamp();
            if (timestamp != null) {
                podResp.setCreationTimestamp(timestamp.format(DateTimeFormatter.ISO_DATE_TIME));
            }
        }

        V1PodStatus status = v1Pod.getStatus();
        if (status != null) {
            podResp.setHostIp(status.getHostIP());
            OffsetDateTime startTime = status.getStartTime();
            if (startTime != null) {
                podResp.setStartTime(startTime.format(DateTimeFormatter.ISO_DATE_TIME));
            }
        }

        V1PodSpec spec = v1Pod.getSpec();
        if (spec != null) {
            Map<String, String> nodeSelector = spec.getNodeSelector();
            podResp.setNodeSelector(nodeSelector);
            podResp.setContainerInfoList(KubernetesUtil.containerInfoList(spec.getContainers()));
        }
        return podResp;
    }

    public void createPodByYaml(String namespace, String yamlContent) throws Exception {
        V1Pod v1Pod = (V1Pod) Yaml.load(yamlContent);
        coreV1Api.createNamespacedPod(namespace, v1Pod, "true", null, null, null);
    }
    public void updateResource(String namespace, String podName, String kind, ResourceReq req) throws Exception {
        String body = String.format(jsonStr, req.getLimitCpu(), req.getLimitMem(),
                req.getRequestCpu(), req.getRequestMem());
        if ("deployment".equalsIgnoreCase(kind)) {
            appsV1Api.patchNamespacedDeployment(namespace, podName, new V1Patch(body), "true", null,
                    null, null, null);
        } else if ("statefulSet".equalsIgnoreCase(kind)) {
            appsV1Api.patchNamespacedStatefulSet(namespace, podName, new V1Patch(body), null, null,
                    null, null, null);
        } else {
            throw new Exception(kind + " type changes are not supported.");
        }
    }
}
