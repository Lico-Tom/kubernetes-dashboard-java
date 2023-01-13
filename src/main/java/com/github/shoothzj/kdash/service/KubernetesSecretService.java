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

import com.github.shoothzj.kdash.module.CreateSecretParam;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KubernetesSecretService {

    private final CoreV1Api coreV1Api;

    public KubernetesSecretService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createSecret(String namespace, CreateSecretParam req) throws ApiException {
        V1Secret v1Secret = new V1Secret();
        v1Secret.setKind("Secret");
        v1Secret.setApiVersion("v1");
        v1Secret.setType(req.getType());
        V1ObjectMeta v1ObjectMeta = req.getV1ObjectMeta() == null ? new V1ObjectMeta() : req.getV1ObjectMeta();
        v1ObjectMeta.setName(req.getSecretName());
        v1ObjectMeta.setNamespace(namespace);
        v1ObjectMeta.setLabels(KubernetesUtil.label(req.getSecretName()));
        v1Secret.setMetadata(v1ObjectMeta);
        v1Secret.setImmutable(req.getImmutable());
        v1Secret.setStringData(req.getStringData());
        coreV1Api.createNamespacedSecret(namespace, v1Secret,
                " true", null, null, null);
    }

    public void createSecretByYaml(String namespace, String yamlContent) throws IOException, ApiException {
        V1Secret v1Secret = (V1Secret) Yaml.load(yamlContent);
        coreV1Api.createNamespacedSecret(namespace, v1Secret,
                " true", null, null, null);
    }

    public void deleteSecret(String namespace, String secret) throws ApiException {
        coreV1Api.deleteNamespacedSecret(secret, namespace, "true", null, null,
                null, null, null);
    }

    public V1SecretList getSecretList(String namespace) throws ApiException {
      return coreV1Api.listNamespacedSecret(namespace, "true", null, null,
                null, null, null, null, null,
                null, null);
    }
}
