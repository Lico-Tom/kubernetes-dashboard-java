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
import com.github.shoothzj.kdash.module.CreateStatefulSetParam;
import com.github.shoothzj.kdash.module.zookeeper.CreateZooKeeperDashboardReq;
import com.github.shoothzj.kdash.module.zookeeper.CreateZooKeeperReq;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ServicePort;

import java.util.ArrayList;

public class ZookeeperUtil {

    public static CreateServiceParam service(CreateZooKeeperReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("zookeeper", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(2181).targetPort(new IntOrString(2181)));
        ports.add(new V1ServicePort().name("peer").port(2888).targetPort(new IntOrString(2888)));
        ports.add(new V1ServicePort().name("leader").port(3888).targetPort(new IntOrString(3888)));
        createServiceParam.setPorts(ports);
        createServiceParam.setPublishNotReadyAddresses(true);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }

    public static CreateStatefulSetParam statefulSet(CreateZooKeeperReq req) {
        CreateStatefulSetParam createStatefulSetParam = new CreateStatefulSetParam();
        createStatefulSetParam.setStatefulSetName(KubernetesUtil.name("zookeeper", req.getName()));
        createStatefulSetParam.setImage(req.getImage());
        createStatefulSetParam.setEnv(req.getEnv());
        createStatefulSetParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createStatefulSetParam.setReplicas(req.getReplicas());
        return createStatefulSetParam;
    }

    public static CreateServiceParam dashboardService(CreateZooKeeperDashboardReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("zookeeper-dashboard", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(10002).targetPort(new IntOrString(10002)));
        createServiceParam.setPorts(ports);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }

    public static CreateDeploymentParam dashboardDeploy(CreateZooKeeperDashboardReq req) {
        CreateDeploymentParam createDeploymentParam = new CreateDeploymentParam();
        createDeploymentParam.setDeploymentName(KubernetesUtil.name("zookeeper-dashboard", req.getName()));
        createDeploymentParam.setImage(req.getImage());
        createDeploymentParam.setEnv(req.getEnv());
        createDeploymentParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createDeploymentParam.setReplicas(req.getReplicas());
        return createDeploymentParam;
    }

}
