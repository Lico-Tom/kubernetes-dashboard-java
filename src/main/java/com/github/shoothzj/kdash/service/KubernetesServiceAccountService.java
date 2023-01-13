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

import com.github.shoothzj.kdash.module.CreateServiceAccountReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesServiceAccountService {

    private final CoreV1Api coreV1Api;

    public KubernetesServiceAccountService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createServiceAccount(String namespace, CreateServiceAccountReq req) throws ApiException {
        V1ServiceAccount serviceAccount = new V1ServiceAccount();
        serviceAccount.setApiVersion("v1");
        serviceAccount.setKind("ServiceAccount");
        serviceAccount.automountServiceAccountToken(req.isAutomountServiceAccountToken());
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setLabels(req.getLabels());
            v1ObjectMeta.setName(req.getServiceAccountName());
            v1ObjectMeta.setNamespace(namespace);
            serviceAccount.setMetadata(v1ObjectMeta);
        }
        serviceAccount.setImagePullSecrets(req.getImagePullSecrets());
        serviceAccount.setSecrets(req.getSecrets());
        coreV1Api.createNamespacedServiceAccount(namespace, serviceAccount,
                "true", null, null, null);
    }

    public void deleteServiceAccount(String namespace, String serviceAccountName) throws ApiException {
        coreV1Api.deleteNamespacedServiceAccount(serviceAccountName, namespace, "true", null,
                null, null, null, null);
    }
}
