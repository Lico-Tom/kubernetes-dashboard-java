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

import com.github.shoothzj.kdash.module.CreatePersistentVolumeReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeSelector;
import io.kubernetes.client.openapi.models.V1NodeSelectorRequirement;
import io.kubernetes.client.openapi.models.V1NodeSelectorTerm;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeSpec;
import io.kubernetes.client.openapi.models.V1VolumeNodeAffinity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KubernetesPersistentVolumeService {

    private final CoreV1Api coreV1Api;

    public KubernetesPersistentVolumeService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createPersistentVolume(CreatePersistentVolumeReq req) throws ApiException {
        V1PersistentVolume v1PersistentVolume = new V1PersistentVolume();
        v1PersistentVolume.setApiVersion("v1");
        v1PersistentVolume.setKind("PersistentVolume");

        // metadata below
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(req.getName());
        metadata.setLabels(req.getLabels());
        metadata.setAnnotations(req.getAnnotations());
        v1PersistentVolume.setMetadata(metadata);

        // spec below
        V1PersistentVolumeSpec spec = new V1PersistentVolumeSpec();
        spec.setCapacity(req.getCapacity());
        spec.setAccessModes(req.getAccessModes());
        spec.setPersistentVolumeReclaimPolicy(req.getPersistentVolumeReclaimPolicy());
        spec.setStorageClassName(req.getStorageClassName());
        // nodeAffinity convert
        Map<String, List<String>> nodeSelectorDefines = req.getNodeSelectorDefines();
        if (nodeSelectorDefines != null) {
            V1VolumeNodeAffinity v1NodeAffinity = new V1VolumeNodeAffinity();
            V1NodeSelector v1NodeSelector = new V1NodeSelector();
            List<V1NodeSelectorTerm> selectorTerms = new ArrayList<>();
            V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
            for (Map.Entry<String, List<String>> entry : nodeSelectorDefines.entrySet()) {
                V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                v1NodeSelectorRequirement.setKey(entry.getKey());
                v1NodeSelectorRequirement.setOperator("In");
                v1NodeSelectorRequirement.setValues(entry.getValue());
                v1NodeSelectorTerm.addMatchExpressionsItem(v1NodeSelectorRequirement);
            }
            selectorTerms.add(v1NodeSelectorTerm);
            v1NodeSelector.setNodeSelectorTerms(selectorTerms);
            v1NodeAffinity.setRequired(v1NodeSelector);
            spec.setNodeAffinity(v1NodeAffinity);
        }
        spec.setCsi(req.getCsi());
        v1PersistentVolume.setSpec(spec);
        coreV1Api.createPersistentVolume(v1PersistentVolume, "true", null, null, null);
    }

    public void deletePersistentVolume(String name) throws ApiException {
        coreV1Api.deletePersistentVolume(name, "true", null, null, null, null, null);
    }

    public boolean pvExists(String name) throws ApiException {
        V1PersistentVolumeList persistentVolumeList = coreV1Api.listPersistentVolume("true",
                null, null, null, null, null,
                null, null, null, null);
        List<V1PersistentVolume> items = persistentVolumeList.getItems();
        for (V1PersistentVolume item : items) {
            if (item.getMetadata() != null && name.equals(item.getMetadata().getName())) {
                return true;
            }
        }
        return false;
    }
}
