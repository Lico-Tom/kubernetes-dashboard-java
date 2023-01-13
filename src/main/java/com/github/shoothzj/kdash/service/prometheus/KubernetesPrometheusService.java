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

package com.github.shoothzj.kdash.service.prometheus;

import com.github.shoothzj.kdash.module.prometheus.CreatePrometheusReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
import com.github.shoothzj.kdash.service.KubernetesServiceService;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import com.github.shoothzj.kdash.util.PrometheusUtil;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesPrometheusService {

    @Autowired
    private KubernetesDeployService deployService;

    @Autowired
    private KubernetesServiceService serviceService;

    public void createPrometheus(String namespace, CreatePrometheusReq req) throws Exception {
        serviceService.createService(namespace, PrometheusUtil.service(req));
        deployService.createNamespacedDeploy(namespace, PrometheusUtil.deploy(req));
    }

    public void deletePrometheus(String namespace, String name) throws ApiException {
        serviceService.deleteService(namespace, KubernetesUtil.name("prometheus", name));
        deployService.deleteDeploy(namespace, KubernetesUtil.name("prometheus", name));
    }

}
