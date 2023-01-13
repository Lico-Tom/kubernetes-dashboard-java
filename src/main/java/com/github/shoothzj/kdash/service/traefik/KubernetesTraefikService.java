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

package com.github.shoothzj.kdash.service.traefik;

import com.github.shoothzj.kdash.module.traefik.CreateTraefikReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
import com.github.shoothzj.kdash.util.TraefikUtil;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubernetesTraefikService {

    @Autowired
    private KubernetesDeployService deployService;

    public void createTraefik(String namespace, CreateTraefikReq req) throws ApiException {
        deployService.createNamespacedDeploy(namespace, TraefikUtil.deploy(req));
    }

    public void deleteTraefik(String namespace, String dashboardName) throws ApiException {
        deployService.deleteDeploy(namespace, dashboardName);
    }
}
