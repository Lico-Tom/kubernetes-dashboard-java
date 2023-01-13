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

import com.github.shoothzj.kdash.module.CreateConfigmapParam;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class KubernetesConfigmapService {

    private final CoreV1Api coreV1Api;

    public KubernetesConfigmapService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createNamespacedConfigmap(String namespace, CreateConfigmapParam param) throws ApiException {
        V1ConfigMap v1ConfigMap = new V1ConfigMap();
        v1ConfigMap.setKind("ConfigMap");
        v1ConfigMap.setApiVersion("v1");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setNamespace(namespace);
        v1ObjectMeta.setName(param.getConfigmapName());
        v1ConfigMap.setData(param.getData());
        v1ConfigMap.setMetadata(v1ObjectMeta);
        coreV1Api.createNamespacedConfigMap(namespace, v1ConfigMap, "true",
                null, null, null);
    }

    public void createConfigmapByYaml(String namespace, String yamlContent) throws IOException, ApiException {
        V1ConfigMap v1ConfigMap = (V1ConfigMap) Yaml.load(yamlContent);
        coreV1Api.createNamespacedConfigMap(namespace, v1ConfigMap, "true",
                null, null, null);
    }

    public void deleteConfigmap(String namespace, String configmapName) throws ApiException {
        coreV1Api.deleteNamespacedConfigMap(namespace, configmapName, "true", null,
                null, null, null, null);
    }

    public List<V1ConfigMap> getNamespaceConfigmap(String namespace) throws ApiException {
        V1ConfigMapList v1ConfigMapList = coreV1Api.listNamespacedConfigMap(namespace, "ture",
                null, null, null, null, null,
                null, null, null, null);
        return v1ConfigMapList.getItems();
    }
}
