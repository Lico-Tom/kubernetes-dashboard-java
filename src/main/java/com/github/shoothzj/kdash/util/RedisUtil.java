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
import com.github.shoothzj.kdash.module.redis.CreateRedisDashboardReq;
import com.github.shoothzj.kdash.module.redis.CreateRedisReq;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ServicePort;

import java.util.ArrayList;

public class RedisUtil {

    public static CreateServiceParam service(CreateRedisReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("redis", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(6379).targetPort(new IntOrString(6379)));
        createServiceParam.setPorts(ports);
        return createServiceParam;
    }

    public static CreateStatefulSetParam statefulSet(CreateRedisReq req) {
        CreateStatefulSetParam createStatefulSetParam = new CreateStatefulSetParam();
        createStatefulSetParam.setStatefulSetName(KubernetesUtil.name("redis", req.getName()));
        createStatefulSetParam.setImage(req.getImage());
        createStatefulSetParam.setEnv(req.getEnv());
        createStatefulSetParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createStatefulSetParam.setReplicas(req.getReplicas());
        return createStatefulSetParam;
    }

    public static CreateServiceParam dashboardService(CreateRedisDashboardReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("redis-dashboard", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(10004).targetPort(new IntOrString(10004)));
        createServiceParam.setPorts(ports);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }

    public static CreateDeploymentParam dashboardDeploy(CreateRedisDashboardReq req) {
        CreateDeploymentParam createDeploymentParam = new CreateDeploymentParam();
        createDeploymentParam.setDeploymentName(KubernetesUtil.name("redis-dashboard", req.getName()));
        createDeploymentParam.setImage(req.getImage());
        createDeploymentParam.setEnv(req.getEnv());
        createDeploymentParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createDeploymentParam.setReplicas(req.getReplicas());
        return createDeploymentParam;
    }

}
