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

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesCustomResourceService {

    private final CustomObjectsApi customObjectsApi;

    public KubernetesCustomResourceService(@Autowired ApiClient apiClient) {
        this.customObjectsApi = new CustomObjectsApi(apiClient);
    }

    public Object createCustomObject(
            String group,
            String version,
            String namespace,
            String plural,
            Object body) throws ApiException {
        return customObjectsApi.createNamespacedCustomObject(group, version,
                namespace, plural, body, "true", null, null);
    }

    public Object deleteCustomObject(
            String group,
            String version,
            String namespace,
            String plural,
            String objectName) throws ApiException {
        return customObjectsApi.deleteNamespacedCustomObject(group, version, namespace, plural, objectName,
                null, null, null, null, null);
    }

    public Object getCustomObject(
            String group,
            String version,
            String namespace,
            String plural,
            String objectName) throws ApiException {
        return customObjectsApi.getNamespacedCustomObject(group, version, namespace, plural, objectName);
    }

    public Object patchCustomObject(
            String group,
            String version,
            String namespace,
            String plural,
            String objectName,
            Object body) throws ApiException {
        return customObjectsApi.patchNamespacedCustomObject(group, version, namespace, plural, objectName, body,
                null, null, null);
    }
}
