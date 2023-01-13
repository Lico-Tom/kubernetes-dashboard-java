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


import com.github.shoothzj.kdash.module.CreateChaosMeshCrdObjectReq;
import com.github.shoothzj.kdash.module.chaosmesh.BaseChaosSpec;
import com.github.shoothzj.kdash.module.chaosmesh.Chaos;
import com.github.shoothzj.kdash.module.chaosmesh.ChaosSelector;
import com.github.shoothzj.kdash.module.chaosmesh.SelectMode;
import com.github.shoothzj.kdash.module.chaosmesh.networkchaos.NetworkChaosSpec;

import java.util.ArrayList;
import java.util.List;

public class ChaosMeshUtil {

    public static <T, Spec extends BaseChaosSpec<T>> void checkBaseBody(Chaos<T, Spec> chaos) {
        BaseChaosSpec<T> spec = chaos.getSpec();
        String mode = spec.getMode();
        if (!mode.equals(SelectMode.ALL)
                && !mode.equals(SelectMode.FIXED_NUMBER)
                && !mode.equals(SelectMode.FIXED_PERCENT)
                && !mode.equals(SelectMode.RANDOM_MAX_PERCENT)
                && !mode.equals(SelectMode.RANDOM_ONE)) {
            throw new IllegalArgumentException(String.format("attribute mode value %s is illegal", mode));
        }
    }

    public static void checkNetworkBody(Chaos<NetworkChaosSpec.NetworkChaosAction, NetworkChaosSpec> chaos) {
        checkBaseBody(chaos);
        NetworkChaosSpec networkChaosSpec = chaos.getSpec();
        NetworkChaosSpec.NetworkChaosAction action = networkChaosSpec.getAction();
        switch (action) {
            case loss -> {
                if (networkChaosSpec.getLoss() == null) {
                    throw new IllegalArgumentException("body 'loss' should not be empty");
                }
            }
            case duplicate -> {
                if (networkChaosSpec.getDuplicate() == null) {
                    throw new IllegalArgumentException("body 'duplicate' should not be empty");
                }
            }
            case corrupt -> {
                if (networkChaosSpec.getCorrupt() == null) {
                    throw new IllegalArgumentException("body 'corrupt' should not be empty");
                }
            }
            case delay -> {
                if (networkChaosSpec.getDelay() == null) {
                    throw new IllegalArgumentException("body 'delay' should not be empty");
                }
            }
            default -> throw new UnsupportedOperationException(String.format("%s not support yet", action));
        }
    }

    public static <T, Spec extends BaseChaosSpec<T>> void convert(
            CreateChaosMeshCrdObjectReq req,
            Chaos<T, Spec> chaos) {
        Chaos.Metadata metadata = new Chaos.Metadata();
        metadata.setName(req.getName());
        metadata.setNamespace(req.getNamespaces());
        chaos.setMetadata(metadata);
        BaseChaosSpec<T> spec = chaos.getSpec();
        if (spec == null) {
            throw new IllegalArgumentException("set spec before convert");
        }
        spec.setDuration(req.getDuration());
        spec.setMode(req.getMode());
        spec.setValue(req.getValue());
        ChaosSelector chaosSelector = new ChaosSelector();
        List<String> namespaces = new ArrayList<>();
        namespaces.add(req.getTargetNamespace());
        chaosSelector.setLabelSelectors(req.getLabelSelectors());
        chaosSelector.setNamespaces(namespaces);
        spec.setSelector(chaosSelector);
    }
}
