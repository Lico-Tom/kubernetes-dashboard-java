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
import com.github.shoothzj.kdash.module.kong.CreateKongReq;
import io.kubernetes.client.openapi.models.V1ServicePort;

import java.util.ArrayList;

public class KongUtil {

    public static CreateDeploymentParam deploy(CreateKongReq req) {
        CreateDeploymentParam param = new CreateDeploymentParam();
        param.setDeploymentName(KubernetesUtil.name("kong", req.getName()));
        param.setImage(req.getImage());
        param.setEnv(req.getEnv());
        param.setResourceRequirements(
                KubernetesUtil.resourceRequirements(req.getCpu(), req.getMemory()));
        param.setReplicas(req.getReplicas());
        return param;
    }

    public static CreateServiceParam service(CreateKongReq req) {
        CreateServiceParam createServiceParam = new CreateServiceParam();
        createServiceParam.setServiceName(KubernetesUtil.name("kong", req.getName()));
        ArrayList<V1ServicePort> ports = new ArrayList<>();
        createServiceParam.setPorts(ports);
        createServiceParam.setClusterIp("None");
        return createServiceParam;
    }
}
