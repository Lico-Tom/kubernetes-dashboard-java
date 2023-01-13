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

import com.github.shoothzj.kdash.module.CreateRoleReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesRoleService {

    private RbacAuthorizationV1Api rbacAuthorizationV1Api;

    public KubernetesRoleService(@Autowired ApiClient apiClient) {
        this.rbacAuthorizationV1Api = new RbacAuthorizationV1Api(apiClient);
    }

    public void createRole(String namespace, CreateRoleReq req) throws ApiException {
        V1Role v1Role = new V1Role();
        v1Role.setApiVersion("v1");
        v1Role.setKind("Role");
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(req.getRoleName());
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setLabels(req.getLabels());
            v1Role.setMetadata(v1ObjectMeta);
        }
        v1Role.setRules(req.getRules());
        rbacAuthorizationV1Api.createNamespacedRole(namespace, v1Role, "true",
                null, null, null);
    }

    public void deleteRole(String namespace, String roleName) throws ApiException {
        rbacAuthorizationV1Api.deleteNamespacedRole(roleName, namespace, "true", null,
                null, null, null, null);
    }
}
