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

package com.github.shoothzj.kdash.service.apisix;

import com.github.shoothzj.kdash.module.apisix.CreateApiSixReq;
import com.github.shoothzj.kdash.module.apisix.CreateApiSixDashboardReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
import com.github.shoothzj.kdash.service.KubernetesServiceService;
import com.github.shoothzj.kdash.util.ApiSixUtil;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesApiSixService {

    @Autowired
    private KubernetesDeployService deployService;

    @Autowired
    private KubernetesServiceService serviceService;

    public void createApiSix(String namespace, CreateApiSixReq req) throws ApiException {
        deployService.createNamespacedDeploy(namespace, ApiSixUtil.deploy(req));
    }

    public void deleteApiSix(String namespace, String name) throws ApiException {
        deployService.deleteDeploy(namespace, name);
    }

    public void createDashboard(String namespace, CreateApiSixDashboardReq req) throws ApiException {
        serviceService.createService(namespace, ApiSixUtil.dashboardService(req));
        deployService.createNamespacedDeploy(namespace, ApiSixUtil.dashboardDeploy(req));
    }

    public void deleteDashboard(String namespace, String name) throws ApiException {
        serviceService.deleteService(namespace, "apisix-dashboard");
        deployService.deleteDeploy(namespace, "apisix-dashboard");
    }

}
