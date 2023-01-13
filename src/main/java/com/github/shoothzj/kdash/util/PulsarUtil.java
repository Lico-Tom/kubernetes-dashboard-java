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

package com.github.shoothzj.kdash.util;

import com.github.shoothzj.kdash.module.CreateDeploymentParam;
import com.github.shoothzj.kdash.module.CreateServiceParam;
import com.github.shoothzj.kdash.module.pulsar.CreatePulsarDashboardReq;
import com.github.shoothzj.kdash.module.pulsar.CreatePulsarReq;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ServicePort;

import java.util.ArrayList;

public class PulsarUtil {

    public static CreateServiceParam service(CreatePulsarReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("pulsar", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("pulsar").port(6650).targetPort(new IntOrString(6650)));
        ports.add(new V1ServicePort().name("pulsar-tls").port(6651).targetPort(new IntOrString(6650)));
        ports.add(new V1ServicePort().name("http").port(8080).targetPort(new IntOrString(8080)));
        ports.add(new V1ServicePort().name("https").port(8081).targetPort(new IntOrString(8081)));
        createServiceParam.setPorts(ports);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }

    public static CreateDeploymentParam deploy(CreatePulsarReq req) {
        CreateDeploymentParam createDeploymentParam = new CreateDeploymentParam();
        createDeploymentParam.setDeploymentName(KubernetesUtil.name("pulsar", req.getName()));
        createDeploymentParam.setImage(req.getImage());
        createDeploymentParam.setEnv(req.getEnv());
        createDeploymentParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createDeploymentParam.setReplicas(req.getReplicas());
        return createDeploymentParam;
    }

    public static CreateServiceParam dashboardService(CreatePulsarDashboardReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("mysql-dashboard", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(10006).targetPort(new IntOrString(10006)));
        createServiceParam.setPorts(ports);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }

    public static CreateDeploymentParam dashboardDeploy(CreatePulsarDashboardReq req) {
        CreateDeploymentParam createDeploymentParam = new CreateDeploymentParam();
        createDeploymentParam.setDeploymentName(KubernetesUtil.name("pulsar-dashboard", req.getName()));
        createDeploymentParam.setImage(req.getImage());
        createDeploymentParam.setEnv(req.getEnv());
        createDeploymentParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createDeploymentParam.setReplicas(req.getReplicas());
        return createDeploymentParam;
    }

}
