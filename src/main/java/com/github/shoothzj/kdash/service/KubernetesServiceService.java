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

import com.github.shoothzj.kdash.module.CreateServiceParam;
import com.github.shoothzj.kdash.module.GetServiceResp;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class KubernetesServiceService {


    private final CoreV1Api coreV1Api;

    public KubernetesServiceService(@Autowired ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public void createService(String namespace, CreateServiceParam req) throws ApiException {
        final V1Service v1Service = new V1Service();
        {
            final V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setNamespace(namespace);
            v1ObjectMeta.setName(req.getServiceName());
            v1ObjectMeta.setLabels(req.getServiceLabels());
            v1ObjectMeta.setClusterName(req.getServiceClusterName());
            v1Service.setMetadata(v1ObjectMeta);
        }
        {
            final V1ServiceSpec v1ServiceSpec = new V1ServiceSpec();
            v1ServiceSpec.setPorts(req.getPorts());
            v1ServiceSpec.setClusterIP(req.getClusterIp());
            v1ServiceSpec.setSelector(req.getServiceSelector());
            v1ServiceSpec.setPublishNotReadyAddresses(req.isPublishNotReadyAddresses());
            v1Service.setSpec(v1ServiceSpec);
        }
        coreV1Api.createNamespacedService(namespace, v1Service, "true",
                null, null, null);
    }

    public void createServiceByYaml(String namespace, String yamlContent) throws IOException, ApiException {
        V1Service v1Service = (V1Service) Yaml.load(yamlContent);
        coreV1Api.createNamespacedService(namespace, v1Service, "true", null, null, null);
    }

    public void deleteService(String namespace, String serviceName) throws ApiException {
        coreV1Api.deleteNamespacedService(serviceName, namespace, "true",
                null, null, null,
                null, null);
    }

    public List<GetServiceResp> getService(String namespaces) throws ApiException {
        V1ServiceList serviceList = coreV1Api.listNamespacedService(namespaces, "true",
                null, null, null,
                null, null, null,
                null, 15, null);
        List<GetServiceResp> serviceResps = new ArrayList<>();
        for (V1Service item : serviceList.getItems()) {
            GetServiceResp serviceResp = new GetServiceResp();
            serviceResp.setServiceKind(item.getKind());

            // metadata
            V1ObjectMeta serviceMeta = item.getMetadata();
            if (serviceMeta != null) {
                serviceResp.setServiceName(serviceMeta.getName());
                serviceResp.setServiceLabel(serviceMeta.getLabels());
                serviceResp.setServiceClusterName(serviceMeta.getClusterName());
            }

            // spec
            V1ServiceSpec spec = item.getSpec();
            if (spec != null) {
                serviceResp.setServiceSelector(spec.getSelector());
                serviceResp.setServiceClusterIP(spec.getClusterIP());
            }
            serviceResps.add(serviceResp);
        }
        return serviceResps;
    }

}
