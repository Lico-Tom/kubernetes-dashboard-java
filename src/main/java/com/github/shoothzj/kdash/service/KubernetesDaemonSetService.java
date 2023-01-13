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

import com.github.shoothzj.kdash.module.CreateDaemonSetParam;
import com.github.shoothzj.kdash.util.KubernetesUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1DaemonSetSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class KubernetesDaemonSetService {

    private final AppsV1Api appsV1Api;

    public KubernetesDaemonSetService(@Autowired ApiClient apiClient) {
        this.appsV1Api = new AppsV1Api(apiClient);
    }


    public void createNamespacedDaemonSet(String namespace, CreateDaemonSetParam param) throws ApiException {
        V1DaemonSet v1DaemonSet = new V1DaemonSet();
        v1DaemonSet.setKind("DaemonSet");
        v1DaemonSet.setApiVersion("V1");
        {
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setName(param.getDaemonSetName());
            v1ObjectMeta.setLabels(KubernetesUtil.label(param.getDaemonSetName()));
            v1DaemonSet.setMetadata(v1ObjectMeta);
        }
        {
            V1DaemonSetSpec v1DaemonSetSpec = new V1DaemonSetSpec();
            v1DaemonSetSpec.setSelector(KubernetesUtil.labelSelector(param.getDaemonSetName(), null));
            v1DaemonSetSpec.setTemplate(param.getTemplate());
            v1DaemonSet.setSpec(v1DaemonSetSpec);
        }
        appsV1Api.createNamespacedDaemonSet(namespace, v1DaemonSet, "true",
                null, null, null);
    }

    public void createConfigmapByYaml(String namespace, String yamlContent) throws IOException, ApiException {
        V1DaemonSet v1DaemonSet = (V1DaemonSet) Yaml.load(yamlContent);
        appsV1Api.createNamespacedDaemonSet(namespace, v1DaemonSet, "true",
                null, null, null);
    }

    public void deleteDaemonSet(String namespace, String daemonSetName) throws ApiException {
        appsV1Api.deleteNamespacedDaemonSet(namespace, daemonSetName, "true", null,
                null, null, null, null);
    }

    public List<V1DaemonSet> getNamespaceDaemonSet(String namespace) throws ApiException {
        V1DaemonSetList v1DaemonSetList = appsV1Api.listNamespacedDaemonSet(namespace, null,
                null, null, null, null,
                null, null, null, null, null);
        return v1DaemonSetList.getItems();
    }
}
