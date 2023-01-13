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

import com.github.shoothzj.kdash.module.CreateReplicaReq;
import com.github.shoothzj.kdash.module.GetReplicaResp;
import com.github.shoothzj.kdash.module.ScaleReplicaReq;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ReplicationController;
import io.kubernetes.client.openapi.models.V1ReplicationControllerList;
import io.kubernetes.client.openapi.models.V1ReplicationControllerSpec;
import io.kubernetes.client.openapi.models.V1ReplicationControllerStatus;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class KubernetesReplicaControllerService {

    private final CoreV1Api coreV1Api;

    public KubernetesReplicaControllerService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createReplica(String namespace, CreateReplicaReq req) throws ApiException {
        V1ReplicationController replicationController = new V1ReplicationController();
        replicationController.setApiVersion("v1");

        {
            // add metadata
            V1ObjectMeta metaData = new V1ObjectMeta();
            metaData.setName(req.getReplicaName());
            metaData.setLabels(KubernetesUtil.label(req.getReplicaName()));
            metaData.setNamespace(namespace);
            replicationController.setMetadata(metaData);
        }
        {
            // add spec
            V1ReplicationControllerSpec controllerSpec = new V1ReplicationControllerSpec();
            controllerSpec.setReplicas(req.getReplicas());

            controllerSpec.setSelector(KubernetesUtil.label(req.getReplicaName()));
            // spec template
            V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
            V1PodSpec v1PodSpec = new V1PodSpec();
            v1PodSpec.setContainers(KubernetesUtil.singleContainerList(req.getImage(), req.getEnv(),
                    req.getReplicaName(), req.getResourceRequirements()));
            v1PodSpec.setImagePullSecrets(KubernetesUtil.imagePullSecrets(req.getImagePullSecret()));
            v1PodTemplateSpec.setSpec(v1PodSpec);

            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setName(req.getReplicaName());
            v1ObjectMeta.setLabels(KubernetesUtil.label(req.getReplicaName()));
            v1PodTemplateSpec.setMetadata(v1ObjectMeta);

            controllerSpec.setTemplate(v1PodTemplateSpec);
            replicationController.setSpec(controllerSpec);
        }
        coreV1Api.createNamespacedReplicationController(namespace, replicationController, "true",
                null, null, null);
    }

    public void createReplicaByYaml(String namespace, String yamlContent) throws ApiException, IOException {
        V1ReplicationController replication = (V1ReplicationController) Yaml.load(yamlContent);
        coreV1Api.createNamespacedReplicationController(namespace, replication, "true",
                null, null, null);
    }

    public void deleteReplicas(String namespace, String replicaName) throws ApiException {
        coreV1Api.deleteNamespacedReplicationController(replicaName, namespace, "true", null,
                null, null, null, null);
    }

    public List<GetReplicaResp> getReplicas(String namespace) throws ApiException {
        List<GetReplicaResp> replicaResps = new ArrayList<>();
        V1ReplicationControllerList controllerList = coreV1Api.listNamespacedReplicationController(namespace,
                "true", null, null, null, null,
                null, null, null, 30, null);
        for (V1ReplicationController item : controllerList.getItems()) {
            replicaResps.add(convert(item));
        }
        return replicaResps;
    }

    private GetReplicaResp convert(V1ReplicationController controller) {
        GetReplicaResp replicaResp = new GetReplicaResp();
        V1ObjectMeta metadata = controller.getMetadata();
        if (metadata != null) {
            replicaResp.setReplicaName(metadata.getName());
            OffsetDateTime timestamp = metadata.getCreationTimestamp();
            if (timestamp != null) {
                replicaResp.setCreationTimestamp(timestamp.format(DateTimeFormatter.ISO_DATE_TIME));
            }
        }
        V1ReplicationControllerStatus status = controller.getStatus();
        if (status != null) {
            replicaResp.setReplicas(status.getReplicas() == null ? 0 : status.getReplicas());
            replicaResp.setAvailableReplicas(status.getAvailableReplicas() == null ? 0 : status.getAvailableReplicas());
        }
        V1ReplicationControllerSpec spec = controller.getSpec();
        if (spec != null) {
            V1PodTemplateSpec template = spec.getTemplate();
            if (template == null) {
                return replicaResp;
            }
            V1PodSpec v1PodSpec = template.getSpec();
            if (v1PodSpec == null) {
                return replicaResp;
            }
            List<V1Container> containers = v1PodSpec.getContainers();
            replicaResp.setContainerInfoList(KubernetesUtil.containerInfoList(containers));
        }
        return replicaResp;
    }

    public void scaleReplica(String namespace, ScaleReplicaReq req) throws ApiException {
        V1ReplicationController replicationController = new V1ReplicationController();
        V1ReplicationControllerSpec v1ScaleSpec = new V1ReplicationControllerSpec();
        v1ScaleSpec.setReplicas(req.getReplicas());
        replicationController.setSpec(v1ScaleSpec);
        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.setName(req.getReplicaName());
        objectMeta.setNamespace(namespace);
        objectMeta.setLabels(KubernetesUtil.label(req.getReplicaName()));
        replicationController.setMetadata(objectMeta);
        coreV1Api.replaceNamespacedReplicationController(req.getReplicaName(), namespace, replicationController,
                "true", null, null, null);
    }
}
