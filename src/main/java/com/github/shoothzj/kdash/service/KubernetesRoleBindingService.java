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

import com.github.shoothzj.kdash.module.CreateRoleBindingReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1RoleBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesRoleBindingService {

    private RbacAuthorizationV1Api rbacAuthorizationV1Api;

    public KubernetesRoleBindingService(@Autowired ApiClient apiClient) {
        this.rbacAuthorizationV1Api = new RbacAuthorizationV1Api(apiClient);
    }

    public void createRoleBinding(String namespace, CreateRoleBindingReq req) throws ApiException {
        V1RoleBinding v1RoleBinding = new V1RoleBinding();
        v1RoleBinding.setApiVersion("v1");
        v1RoleBinding.setKind("RoleBinding");
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(req.getRoleBindingName());
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setLabels(req.getLabels());
            v1RoleBinding.setMetadata(v1ObjectMeta);
        }
        v1RoleBinding.setRoleRef(req.getRoleRef());
        v1RoleBinding.setSubjects(req.getSubjects());
        rbacAuthorizationV1Api.createNamespacedRoleBinding(namespace, v1RoleBinding, "true",
                null, null, null);
    }

    public void deleteRoleBinding(String namespace, String roleBindingName) throws ApiException {
        rbacAuthorizationV1Api.deleteNamespacedRoleBinding(roleBindingName, namespace, "true", null,
                null, null, null, null);
    }
}
