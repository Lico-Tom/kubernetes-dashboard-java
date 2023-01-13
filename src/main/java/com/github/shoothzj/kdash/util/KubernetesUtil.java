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

import com.github.shoothzj.kdash.module.ContainerInfo;
import com.github.shoothzj.kdash.module.NodeSelectorRequirement;
import com.github.shoothzj.kdash.module.PodAffinityTerms;
import com.github.shoothzj.kdash.module.Probe;
import com.github.shoothzj.kdash.module.ResourceRequirements;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1ExecAction;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;
import io.kubernetes.client.openapi.models.V1Lifecycle;
import io.kubernetes.client.openapi.models.V1LifecycleHandler;
import io.kubernetes.client.openapi.models.V1LocalObjectReference;
import io.kubernetes.client.openapi.models.V1NodeAffinity;
import io.kubernetes.client.openapi.models.V1NodeSelector;
import io.kubernetes.client.openapi.models.V1NodeSelectorRequirement;
import io.kubernetes.client.openapi.models.V1NodeSelectorTerm;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelector;
import io.kubernetes.client.openapi.models.V1PodAffinity;
import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1PodAntiAffinity;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KubernetesUtil {

    public static String name(@NotNull String component, @Nullable String name) {
        if (StringUtils.isEmpty(name)) {
            return component;
        } else {
            return component + "-" + name;
        }
    }

    public static Map<String, String> label(@NotNull String deployName) {
        Map<String, String> map = new HashMap<>();
        map.put("app", deployName);
        return map;
    }

    public static V1LabelSelector labelSelector(@NotNull String deployName) {
        return labelSelector(deployName, null);
    }

    public static V1LabelSelector labelSelector(@NotNull String deployName, @Nullable Map<String, String> selector) {
        V1LabelSelector labelSelector = new V1LabelSelector();
        Map<String, String> matchLabels = new HashMap<>();
        matchLabels.put("app", deployName);
        if (selector != null) {
            matchLabels.putAll(selector);
        }
        labelSelector.setMatchLabels(matchLabels);
        return labelSelector;
    }

    public static List<V1Container> singleContainerList(@NotNull String image,
                                                        Map<String, String> envMap,
                                                        String name,
                                                        ResourceRequirements resourceRequirements) {
        return singleContainerList(image, null, envMap, name, resourceRequirements,
                null, null, null);
    }

    public static List<V1Container> singleContainerList(String image,
                                                        Map<String, String> envMap,
                                                        @NotNull String name,
                                                        ResourceRequirements resourceRequirements,
                                                        V1Lifecycle v1Lifecycle,
                                                        V1Probe readinessProbe,
                                                        V1Probe livenessProbe) {
        return singleContainerList(image, null, envMap, name, resourceRequirements,
                v1Lifecycle, readinessProbe, livenessProbe);
    }

    public static List<V1Container> singleContainerList(String image,
                                                        @Nullable String imagePullPolicy,
                                                        @Nullable Map<String, String> envMap,
                                                        @NotNull String name,
                                                        @NotNull ResourceRequirements resourceRequirements,
                                                        @Nullable V1Lifecycle v1Lifecycle,
                                                        @Nullable V1Probe readinessProbe,
                                                        @Nullable V1Probe livenessProbe) {
        List<V1Container> containers = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName(name);
        container.setImage(image);
        container.setImagePullPolicy(imagePullPolicy == null ? "Always" : imagePullPolicy);
        if (envMap != null) {
            container.setEnv(envMap.entrySet().stream().map(entry -> {
                V1EnvVar envVar = new V1EnvVar();
                envVar.setName(entry.getKey());
                envVar.setValue(entry.getValue());
                return envVar;
            }).collect(Collectors.toList()));
        }
        container.setResources(resourceRequirements(resourceRequirements));
        container.setLifecycle(v1Lifecycle);
        container.setReadinessProbe(readinessProbe);
        container.setLivenessProbe(livenessProbe);
        containers.add(container);
        return containers;
    }

    public static List<V1Container> singleContainerList(@NotNull V1Container container) {
        List<V1Container> v1Containers = new ArrayList<>();
        v1Containers.add(container);
        return v1Containers;
    }

    public static V1EnvVar v1EnvVar(@Nullable String fieldPath, @Nullable String name) {
        V1EnvVar v1EnvVar = new V1EnvVar();
        V1EnvVarSource v1EnvVarSource = new V1EnvVarSource();
        V1ObjectFieldSelector v1ObjectFieldSelector = new V1ObjectFieldSelector();
        v1ObjectFieldSelector.setFieldPath(fieldPath);
        v1EnvVarSource.setFieldRef(v1ObjectFieldSelector);
        v1EnvVar.setValueFrom(v1EnvVarSource);
        v1EnvVar.setName(name);
        return v1EnvVar;
    }

    public static V1Probe v1Probe(@Nullable Probe probe) {
        if (probe == null) {
            return null;
        }
        V1Probe v1Probe = new V1Probe();
        V1ExecAction execAction = new V1ExecAction();
        execAction.command(probe.getProbeCommand());
        v1Probe.setExec(execAction);
        v1Probe.setFailureThreshold(probe.getFailureThreshold());
        v1Probe.setInitialDelaySeconds(probe.getInitialDelaySeconds());
        v1Probe.setPeriodSeconds(probe.getPeriodSeconds());
        v1Probe.setSuccessThreshold(probe.getSuccessThreshold());
        v1Probe.setTimeoutSeconds(probe.getTimeoutSeconds());
        return v1Probe;
    }

    public static V1Lifecycle v1Lifecycle(@Nullable List<String> postStartCommand,
                                          @Nullable List<String> preStopCommand) {
        V1Lifecycle v1Lifecycle = new V1Lifecycle();
        if (postStartCommand != null) {
            V1LifecycleHandler preStopHandler = new V1LifecycleHandler();
            V1ExecAction preStopExecAction = new V1ExecAction();
            preStopExecAction.command(postStartCommand);
            preStopHandler.setExec(preStopExecAction);
            v1Lifecycle.setPreStop(preStopHandler);
        }
        if (preStopCommand != null) {
            V1LifecycleHandler postStartHandler = new V1LifecycleHandler();
            V1ExecAction postStartExecAction = new V1ExecAction();
            postStartExecAction.command(preStopCommand);
            postStartHandler.setExec(postStartExecAction);
            v1Lifecycle.setPostStart(postStartHandler);
        }
        return v1Lifecycle;
    }

    public static V1PodAntiAffinity v1PodAntiAffinity(@Nullable PodAffinityTerms podAffinityTerms) {
        if (podAffinityTerms == null) {
            return null;
        }
        V1PodAntiAffinity v1PodAntiAffinity = new V1PodAntiAffinity();
        v1PodAntiAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(
                v1PodAffinityTerms(podAffinityTerms));
        return v1PodAntiAffinity;
    }

    public static V1PodAffinity v1PodAffinity(@Nullable PodAffinityTerms podAffinityTerms) {
        if (podAffinityTerms == null) {
            return null;
        }
        V1PodAffinity v1PodAffinity = new V1PodAffinity();
        v1PodAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(
                v1PodAffinityTerms(podAffinityTerms));
        return v1PodAffinity;
    }

    public static V1NodeAffinity v1NodeAffinity(@Nullable NodeSelectorRequirement selectorRequirement) {
        if (selectorRequirement == null) {
            return null;
        }
        V1NodeAffinity v1NodeAffinity = new V1NodeAffinity();
        V1NodeSelector v1NodeSelector = new V1NodeSelector();
        List<V1NodeSelectorTerm> nodeSelectorTerms = new ArrayList<>();
        V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
        List<V1NodeSelectorRequirement> matchExpressions = new ArrayList<>();
        {
            V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
            v1NodeSelectorRequirement.setKey(selectorRequirement.getKey());
            v1NodeSelectorRequirement.setOperator("In");
            List<String> values = new ArrayList<>();
            values.add(selectorRequirement.getValue());
            v1NodeSelectorRequirement.setValues(values);
            matchExpressions.add(v1NodeSelectorRequirement);
        }
        v1NodeSelectorTerm.setMatchExpressions(matchExpressions);
        nodeSelectorTerms.add(v1NodeSelectorTerm);
        v1NodeSelector.setNodeSelectorTerms(nodeSelectorTerms);
        v1NodeAffinity.requiredDuringSchedulingIgnoredDuringExecution(v1NodeSelector);
        return v1NodeAffinity;
    }

    public static List<V1PodAffinityTerm> v1PodAffinityTerms(@Nullable PodAffinityTerms podAffinityTerms) {
        if (podAffinityTerms == null) {
            return null;
        }
        List<V1PodAffinityTerm> v1PodAffinityTerms = new ArrayList<>();
        V1PodAffinityTerm v1PodAffinityTerm = new V1PodAffinityTerm();
        V1LabelSelector v1LabelSelector = new V1LabelSelector();
        {
            List<V1LabelSelectorRequirement> matchExpressions = new ArrayList<>();
            V1LabelSelectorRequirement v1LabelSelectorRequirement = new V1LabelSelectorRequirement();
            v1LabelSelectorRequirement.setKey(podAffinityTerms.getKey());
            v1LabelSelectorRequirement.setOperator("In");
            List<String> values = new ArrayList<>();
            values.add(podAffinityTerms.getValue());
            v1LabelSelectorRequirement.setValues(values);
            matchExpressions.add(v1LabelSelectorRequirement);
            v1LabelSelector.setMatchExpressions(matchExpressions);
        }
        v1PodAffinityTerm.setLabelSelector(v1LabelSelector);
        v1PodAffinityTerm.setTopologyKey(podAffinityTerms.getTopologyKey());

        v1PodAffinityTerms.add(v1PodAffinityTerm);
        return v1PodAffinityTerms;
    }

    public static Map<String, String> envToMap(@Nullable List<V1EnvVar> env) {
        if (env == null) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>();
        for (V1EnvVar v1EnvVar : env) {
            map.put(v1EnvVar.getName(), v1EnvVar.getValue());
        }
        return map;
    }

    public static List<V1LocalObjectReference> imagePullSecrets(@Nullable String imagePullSecret) {
        List<V1LocalObjectReference> result = new ArrayList<>();
        V1LocalObjectReference v1LocalObjectReference = new V1LocalObjectReference();
        v1LocalObjectReference.setName(imagePullSecret);
        result.add(v1LocalObjectReference);
        return result;
    }

    public static List<ContainerInfo> containerInfoList(@NotNull List<V1Container> containers) {
        List<ContainerInfo> containerInfoList = new ArrayList<>(containers.size());
        for (V1Container container : containers) {
            ContainerInfo containerInfo = new ContainerInfo();
            containerInfo.setImage(container.getImage());
            containerInfo.setEnv(KubernetesUtil.envToMap(container.getEnv()));
            containerInfo.setPorts(container.getPorts());
            containerInfoList.add(containerInfo);
        }
        return containerInfoList;
    }

    public static ResourceRequirements resourceRequirements(String cpu, String memory) {
        Map<String, String> resourceMap = Map.of(
                "cpu", cpu,
                "memory", memory
        );
        ResourceRequirements resourceRequirements = new ResourceRequirements();
        resourceRequirements.setLimits(resourceMap);
        resourceRequirements.setRequests(resourceMap);
        return resourceRequirements;
    }

    public static V1ResourceRequirements resourceRequirements(@NotNull ResourceRequirements resourceRequirements) {
        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
        Map<String, Quantity> limitMap = new HashMap<>();
        for (Map.Entry<String, String> entry : resourceRequirements.getLimits().entrySet()) {
            limitMap.put(entry.getKey(), new Quantity(entry.getValue()));
        }
        v1ResourceRequirements.setLimits(limitMap);
        Map<String, Quantity> requestMap = new HashMap<>();
        for (Map.Entry<String, String> entry : resourceRequirements.getRequests().entrySet()) {
            requestMap.put(entry.getKey(), new Quantity(entry.getValue()));
        }
        v1ResourceRequirements.setRequests(requestMap);
        return v1ResourceRequirements;
    }

}
