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

package com.github.shoothzj.kdash.service.bookkeeper;

import com.github.shoothzj.kdash.module.bookkeeper.CreateBookkeeperDashboardReq;
import com.github.shoothzj.kdash.module.bookkeeper.CreateBookkeeperReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
import com.github.shoothzj.kdash.service.KubernetesServiceService;
import com.github.shoothzj.kdash.service.KubernetesStatefulSetService;
import com.github.shoothzj.kdash.util.BookkeeperUtil;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesBookkeeperService {

    @Autowired
    private KubernetesStatefulSetService statefulSetService;

    @Autowired
    private KubernetesDeployService deployService;

    @Autowired
    private KubernetesServiceService serviceService;

    public void createBookkeeper(String namespace, @NotNull CreateBookkeeperReq req) throws ApiException {
        serviceService.createService(namespace, BookkeeperUtil.service(req));
        statefulSetService.createNamespacedStatefulSet(namespace, BookkeeperUtil.statefulSet(req));
    }

    public void deleteBookkeeper(String namespace, @Nullable String name) throws ApiException {
        serviceService.deleteService(namespace, KubernetesUtil.name("bookkeeper", name));
        statefulSetService.deleteStatefulSet(namespace, KubernetesUtil.name("bookkeeper", name));
    }

    public void createDashboard(String namespace, CreateBookkeeperDashboardReq req) throws ApiException {
        serviceService.createService(namespace, BookkeeperUtil.dashboardService(req));
        deployService.createNamespacedDeploy(namespace, BookkeeperUtil.dashboardDeploy(req));
    }

    public void deleteDashboard(String namespace, String dashboardName) throws ApiException {
        deployService.deleteDeploy(namespace, KubernetesUtil.name("bookkeeper-dashboard", dashboardName));
    }
}
