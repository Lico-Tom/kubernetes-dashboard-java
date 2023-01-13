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

import com.github.shoothzj.kdash.module.CreateClusterRoleBindingReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesClusterRoleBindingService {

    private RbacAuthorizationV1Api rbacAuthorizationV1Api;

    public KubernetesClusterRoleBindingService(@Autowired ApiClient apiClient) {
        this.rbacAuthorizationV1Api = new RbacAuthorizationV1Api(apiClient);
    }

    public void createClusterRoleBinding(CreateClusterRoleBindingReq req) throws ApiException {
        V1ClusterRoleBinding v1ClusterRoleBinding = new V1ClusterRoleBinding();
        v1ClusterRoleBinding.setApiVersion("v1");
        v1ClusterRoleBinding.setKind("ClusterRoleBinding");
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(req.getClusterRoleBindingName());
            v1ObjectMeta.setNamespace(req.getNamespace());
            v1ObjectMeta.setLabels(req.getLabels());
            v1ClusterRoleBinding.setMetadata(v1ObjectMeta);
        }
        v1ClusterRoleBinding.setRoleRef(req.getV1RoleRef());
        v1ClusterRoleBinding.setSubjects(req.getSubjects());
        rbacAuthorizationV1Api.createClusterRoleBinding(v1ClusterRoleBinding, "true",
                null, null, null);
    }

    public void deleteClusterRoleBinding(String clusterRoleBindingName) throws ApiException {
        rbacAuthorizationV1Api.deleteClusterRoleBinding(clusterRoleBindingName, "true", null,
                null, null, null, null);
    }
}
