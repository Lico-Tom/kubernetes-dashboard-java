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

import com.github.shoothzj.kdash.module.CreatePersistentVolumeClaimReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KubernetesPersistentVolumeClaimService {

    private final CoreV1Api coreV1Api;

    public KubernetesPersistentVolumeClaimService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createPersistentVolumeClaim(String namespace, CreatePersistentVolumeClaimReq req) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = new V1PersistentVolumeClaim();
        v1PersistentVolumeClaim.setApiVersion("v1");
        v1PersistentVolumeClaim.setKind("PersistentVolumeClaim");

        // metadata below
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(req.getName());
        metadata.setNamespace(namespace);
        metadata.setLabels(req.getLabels());
        metadata.setAnnotations(req.getAnnotations());
        v1PersistentVolumeClaim.setMetadata(metadata);

        // spec below
        V1PersistentVolumeClaimSpec spec = new V1PersistentVolumeClaimSpec();
        spec.setAccessModes(req.getAccessModes());
        spec.setStorageClassName(req.getStorageClassName());
        spec.setVolumeName(req.getVolumeName());
        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
        v1ResourceRequirements.setRequests(req.getRequests());
        spec.setResources(v1ResourceRequirements);
        v1PersistentVolumeClaim.setSpec(spec);
        coreV1Api.createNamespacedPersistentVolumeClaim(namespace, v1PersistentVolumeClaim, "true", null,
                null, null);
    }

    public void deletePersistentVolumeClaim(String namespace, String name) throws ApiException {
        coreV1Api.deleteNamespacedPersistentVolumeClaim(name, namespace, "true",
                null, null, null, null, null);
    }

    public boolean pvcExists(String namespace, String name) throws ApiException {
        V1PersistentVolumeClaimList persistentVolumeClaimList = coreV1Api.listNamespacedPersistentVolumeClaim(
                namespace, "true",
                null, null, null, null, null,
                null, null, null, null);
        List<V1PersistentVolumeClaim> items = persistentVolumeClaimList.getItems();
        for (V1PersistentVolumeClaim item : items) {
            if (item.getMetadata() != null && name.equals(item.getMetadata().getName())) {
                return true;
            }
        }
        return false;
    }

}
