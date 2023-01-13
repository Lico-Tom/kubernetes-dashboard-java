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

import com.github.shoothzj.kdash.module.CreateCustomResourceDefinitionReq;
import com.github.shoothzj.kdash.module.CustomResourceDefinitionNames;
import com.github.shoothzj.kdash.module.CustomResourceDefinitionVersion;
import com.github.shoothzj.kdash.module.GetCustomResourceDefinitionResp;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ApiextensionsV1Api;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinition;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionList;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionNames;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionSpec;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionVersion;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KubernetesCustomResourceDefineService {

    private final ApiextensionsV1Api apiextensionsV1Api;

    public KubernetesCustomResourceDefineService(@Autowired ApiClient apiClient) {
        this.apiextensionsV1Api = new ApiextensionsV1Api(apiClient);
    }

    public void createCustomResourceDefinition(CreateCustomResourceDefinitionReq req) throws ApiException {
        V1CustomResourceDefinition customResourceDefinition = new V1CustomResourceDefinition();
        customResourceDefinition.setApiVersion("apiextensions.k8s.io/v1");
        customResourceDefinition.setKind("CustomResourceDefinition");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(req.getName());
        customResourceDefinition.setMetadata(metadata);
        V1CustomResourceDefinitionSpec v1CustomResourceDefinitionSpec = new V1CustomResourceDefinitionSpec();
        v1CustomResourceDefinitionSpec.setGroup(req.getGroup());
        v1CustomResourceDefinitionSpec.setScope(req.getScope());
        v1CustomResourceDefinitionSpec.setVersions(req.getVersions().stream().map(this::convert).toList());
        {
            V1CustomResourceDefinitionNames customResourceDefinitionNames = new V1CustomResourceDefinitionNames();
            CustomResourceDefinitionNames names = req.getNames();
            customResourceDefinitionNames.setPlural(names.getPlural());
            customResourceDefinitionNames.setSingular(names.getSingular());
            customResourceDefinitionNames.setKind(names.getKind());
            v1CustomResourceDefinitionSpec.setNames(customResourceDefinitionNames);
        }
        customResourceDefinition.setSpec(v1CustomResourceDefinitionSpec);
        apiextensionsV1Api.createCustomResourceDefinition(customResourceDefinition,
                "true", null, null, null);
    }

    public List<GetCustomResourceDefinitionResp> getCustomResourceDefinitionList() throws ApiException {
        V1CustomResourceDefinitionList definitionList = apiextensionsV1Api.listCustomResourceDefinition("true",
                null, null, null, null, null, null,
                null, 30, false);
        List<V1CustomResourceDefinition> definitionListItems = definitionList.getItems();
        return definitionListItems.stream().map(this::convert).toList();
    }

    private V1CustomResourceDefinitionVersion convert(CustomResourceDefinitionVersion version) {
        V1CustomResourceDefinitionVersion v1CustomResourceDefinitionVersion = new V1CustomResourceDefinitionVersion();
        v1CustomResourceDefinitionVersion.setName(version.getName());
        v1CustomResourceDefinitionVersion.setServed(version.isServed());
        v1CustomResourceDefinitionVersion.setStorage(version.isStorage());
        return v1CustomResourceDefinitionVersion;
    }

    private GetCustomResourceDefinitionResp convert(V1CustomResourceDefinition v1CustomResourceDefinition) {
        GetCustomResourceDefinitionResp resp = new GetCustomResourceDefinitionResp();
        V1ObjectMeta metadata = v1CustomResourceDefinition.getMetadata();
        if (metadata != null) {
            resp.setName(metadata.getName());
        }
        return resp;
    }

}
