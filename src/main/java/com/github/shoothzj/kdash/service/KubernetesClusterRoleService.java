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

import com.github.shoothzj.kdash.module.CreateClusterRoleReq;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1AggregationRule;
import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesClusterRoleService {

    private RbacAuthorizationV1Api rbacAuthorizationV1Api;

    public KubernetesClusterRoleService(@Autowired ApiClient apiClient) {
        this.rbacAuthorizationV1Api = new RbacAuthorizationV1Api(apiClient);
    }

    public void createClusterRole(String namespace, CreateClusterRoleReq req) throws ApiException {
        V1ClusterRole v1ClusterRole = new V1ClusterRole();
        v1ClusterRole.setApiVersion("v1");
        v1ClusterRole.setKind("ClusterRole");
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(req.getClusterRoleName());
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setLabels(req.getLabels());
            v1ClusterRole.setMetadata(v1ObjectMeta);
        }
        v1ClusterRole.setRules(req.getRules());
        V1AggregationRule v1AggregationRule = new V1AggregationRule();
        v1AggregationRule.setClusterRoleSelectors(req.getClusterRoleSelectors());
        v1ClusterRole.setAggregationRule(v1AggregationRule);
        rbacAuthorizationV1Api.createClusterRole(v1ClusterRole, "true",
                null, null, null);
    }

    public void deleteClusterRole(String clusterRoleName) throws ApiException {
        rbacAuthorizationV1Api.deleteClusterRole(clusterRoleName, "true", null,
                null, null, null, null);
    }
}
