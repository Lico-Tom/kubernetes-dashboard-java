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

package com.github.shoothzj.kdash.service.cassandra;

import com.github.shoothzj.kdash.module.cassandra.CreateCassandraDashboardReq;
import com.github.shoothzj.kdash.module.cassandra.CreateCassandraReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
import com.github.shoothzj.kdash.service.KubernetesServiceService;
import com.github.shoothzj.kdash.service.KubernetesStatefulSetService;
import com.github.shoothzj.kdash.util.CassandraUtil;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesCassandraService {

    @Autowired
    private KubernetesStatefulSetService statefulSetService;

    @Autowired
    private KubernetesDeployService deployService;

    @Autowired
    private KubernetesServiceService serviceService;

    public void createCassandra(String namespace, @NotNull CreateCassandraReq req) throws ApiException {
        serviceService.createService(namespace, CassandraUtil.service(req));
        statefulSetService.createNamespacedStatefulSet(namespace, CassandraUtil.statefulSet(req));
    }

    public void deleteCassandra(String namespace, @Nullable String name) throws ApiException {
        serviceService.deleteService(namespace, KubernetesUtil.name("cassandra", name));
        statefulSetService.deleteStatefulSet(namespace, KubernetesUtil.name("cassandra", name));
    }

    public void createDashboard(String namespace, CreateCassandraDashboardReq req) throws ApiException {
        serviceService.createService(namespace, CassandraUtil.dashboardService(req));
        deployService.createNamespacedDeploy(namespace, CassandraUtil.dashboardDeploy(req));
    }

    public void deleteDashboard(String namespace, String dashboardName) throws ApiException {
        serviceService.deleteService(namespace, KubernetesUtil.name("cassandra-dashboard", dashboardName));
        deployService.deleteDeploy(namespace, KubernetesUtil.name("cassandra-dashboard", dashboardName));
    }
}
