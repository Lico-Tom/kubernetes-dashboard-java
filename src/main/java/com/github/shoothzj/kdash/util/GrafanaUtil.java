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
import com.github.shoothzj.kdash.module.grafana.CreateGrafanaReq;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ServicePort;

import java.util.ArrayList;

public class GrafanaUtil {

    public static CreateServiceParam service(CreateGrafanaReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("grafana", req.getName()));
        createServiceParam.setClusterIp("None");
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort().name("client").port(3000).targetPort(new IntOrString(3000)));
        createServiceParam.setPorts(ports);
        return createServiceParam;
    }

    public static CreateDeploymentParam deploy(CreateGrafanaReq req) {
        CreateDeploymentParam createDeploymentParam = new CreateDeploymentParam();
        createDeploymentParam.setDeploymentName(KubernetesUtil.name("grafana", req.getName()));
        createDeploymentParam.setImage(req.getImage());
        createDeploymentParam.setEnv(req.getEnv());
        createDeploymentParam.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        createDeploymentParam.setReplicas(req.getReplicas());
        return createDeploymentParam;
    }
}
