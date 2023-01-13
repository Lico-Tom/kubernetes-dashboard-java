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

package com.github.shoothzj.kdash.service;

import com.github.shoothzj.kdash.module.CreateNetworkChaosCrdObjectReq;
import com.github.shoothzj.kdash.module.chaosmesh.BaseChaosSpec;
import com.github.shoothzj.kdash.module.chaosmesh.Chaos;
import com.github.shoothzj.kdash.module.chaosmesh.ChaosKind;
import com.github.shoothzj.kdash.module.chaosmesh.ChaosMeshConst;
import com.github.shoothzj.kdash.module.chaosmesh.networkchaos.NetworkChaosSpec;
import com.github.shoothzj.kdash.util.ChaosMeshUtil;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChaosMeshService {

    private final KubernetesCustomResourceService customResourceService;

    public ChaosMeshService(@Autowired KubernetesCustomResourceService customResourceService) {
        this.customResourceService = customResourceService;
    }

    public void injectNetworkChaos(CreateNetworkChaosCrdObjectReq networkChaosCrdObjectReq) throws ApiException {
        Chaos<NetworkChaosSpec.NetworkChaosAction, NetworkChaosSpec> chaos = new Chaos<>(ChaosKind.networkchaos);
        NetworkChaosSpec networkChaosSpec = new NetworkChaosSpec();
        chaos.setSpec(networkChaosSpec);
        ChaosMeshUtil.convert(networkChaosCrdObjectReq, chaos);
        networkChaosSpec.setLoss(networkChaosCrdObjectReq.getLoss());
        networkChaosSpec.setDelay(networkChaosCrdObjectReq.getDelay());
        networkChaosSpec.setDuplicate(networkChaosCrdObjectReq.getDuplicate());
        networkChaosSpec.setCorrupt(networkChaosCrdObjectReq.getCorrupt());
        networkChaosSpec.setAction(networkChaosCrdObjectReq.getAction());
        ChaosMeshUtil.checkNetworkBody(chaos);
        createChaosMeshCrdObject(chaos);
    }

    public <T, Spec extends BaseChaosSpec<T>> void createChaosMeshCrdObject(Chaos<T, Spec> chaos) throws ApiException {
        customResourceService.createCustomObject(ChaosMeshConst.CHAOS_MESH_GROUP, ChaosMeshConst.CHAOS_MESH_CRD_VERSION,
                chaos.getMetadata().getNamespace(), String.valueOf(chaos.getKind()), chaos);
    }

    public void releaseLossInjection(String namespace, String objectName) throws ApiException {
        deleteChaosMeshCrdObject(namespace, String.valueOf(ChaosKind.networkchaos), objectName);
    }

    private void deleteChaosMeshCrdObject(String namespace, String plural, String objectName) throws ApiException {
        customResourceService.deleteCustomObject(ChaosMeshConst.CHAOS_MESH_GROUP, ChaosMeshConst.CHAOS_MESH_CRD_VERSION,
                namespace, plural, objectName);
    }
}
