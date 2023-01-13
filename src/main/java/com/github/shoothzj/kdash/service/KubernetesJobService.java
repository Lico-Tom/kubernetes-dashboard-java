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

import com.github.shoothzj.kdash.module.CreateJobParam;
import com.github.shoothzj.kdash.module.ResourceRequirements;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1JobSpec;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class KubernetesJobService {

    private final BatchV1Api batchV1Api;

    public KubernetesJobService(@Autowired ApiClient apiClient) {
        this.batchV1Api = new BatchV1Api(apiClient);
    }

    public void createJob(String namespace, CreateJobParam param) throws ApiException {
        V1Job job = new V1Job();
        job.setApiVersion("apps/v1");
        job.setKind("Job");

        // metadata
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(param.getJobName());
        v1ObjectMeta.setNamespace(namespace);
        Map<String, String> labels = KubernetesUtil.label(param.getJobName());
        labels.putAll(param.getLabels());
        v1ObjectMeta.setLabels(labels);
        v1ObjectMeta.setAnnotations(param.getAnnotations());
        job.setMetadata(v1ObjectMeta);

        // spec
        V1JobSpec spec = new V1JobSpec();
        spec.setSelector(new V1LabelSelector().matchLabels(labels));
        spec.setActiveDeadlineSeconds(param.getActiveDeadlineSeconds());
        spec.setManualSelector(param.getManualSelector());
        spec.setSuspend(param.getSuspend());
        spec.setBackoffLimit(param.getBackoffLimit());
        spec.setCompletions(param.getCompletions());
        spec.setCompletionMode(param.getCompletionMode());
        spec.setParallelism(param.getParallelism());
        spec.setTtlSecondsAfterFinished(param.getTtlSecondsAfterFinished());
        // spec template, necessary
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta podMetadata = new V1ObjectMeta();
        podMetadata.setNamespace(namespace);
        podMetadata.setName(param.getPodName());
        template.setMetadata(podMetadata);
        V1PodSpec podSpec = new V1PodSpec();
        ResourceRequirements podResourceRequirements = new ResourceRequirements();
        Map<String, String> resources = Map.of("cpu", param.getPodCpu(), "memory", param.getPodMemory());
        podResourceRequirements.setLimits(resources);
        podResourceRequirements.setRequests(resources);
        podSpec.setContainers(KubernetesUtil.singleContainerList(
                param.getPodImage(), param.getPodEnv(), param.getPodName(), podResourceRequirements)
        );
        V1Container container = podSpec.getContainers().get(0);
        container.setCommand(param.getCommand());
        container.setArgs(param.getArgs());
        podSpec.setImagePullSecrets(KubernetesUtil.imagePullSecrets(param.getPodImagePullSecret()));
        if (!"Never".equals(param.getPodRestartPolicy()) && !"OnFailure".equals(param.getPodRestartPolicy())) {
            throw new IllegalArgumentException("PodRestartPolicy in job can only be Never or OnFailure");
        }
        podSpec.setRestartPolicy(param.getPodRestartPolicy());
        template.setSpec(podSpec);
        spec.setTemplate(template);
        job.setSpec(spec);

        batchV1Api.createNamespacedJob(namespace, job, "true", null, null, null);
    }

    public void createJobByYaml(String namespace, String yamlContent) throws IOException, ApiException {
        V1Job v1Job = (V1Job) Yaml.load(yamlContent);
        batchV1Api.createNamespacedJob(namespace, v1Job, "true", null, null, null);
    }

    public void deleteJob(String namespace, String name) throws ApiException {
        batchV1Api.deleteNamespacedJob(name, namespace, "true", null, null,
                null, null, null);
    }

    public V1JobList getJobList(String namespace) throws ApiException {
        return batchV1Api.listNamespacedJob(namespace, "true", null, null, null,
                null, null, null, null, null, null);
    }
}
