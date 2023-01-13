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

package com.github.shoothzj.kdash.controller;

import com.github.shoothzj.kdash.module.BaseReqType;
import com.github.shoothzj.kdash.module.CreateConfigmapReq;
import com.github.shoothzj.kdash.service.KubernetesConfigmapService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesConfigmapController {

    private final KubernetesConfigmapService kubernetesConfigmapService;

    public KubernetesConfigmapController(@Autowired KubernetesConfigmapService kubernetesConfigmapService) {
        this.kubernetesConfigmapService = kubernetesConfigmapService;
    }

    @PostMapping("/namespace/{namespace}/configmap")
    public ResponseEntity<Void> createNamespacedConfigmap(@PathVariable String namespace,
                                                 @RequestBody CreateConfigmapReq req) throws Exception {
        if (BaseReqType.PARAM.equals(req.getType())) {
            kubernetesConfigmapService.createNamespacedConfigmap(namespace, req.getParam());
        } else if (BaseReqType.YAML.equals(req.getType())) {
            kubernetesConfigmapService.createConfigmapByYaml(namespace, req.getYamlContent());
        } else {
            throw new Exception("unsupportable operation type");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/namespace/{namespace}/configmap/{configmapName}")
    public ResponseEntity<Void> deleteConfigmap(@PathVariable String namespace,
                                             @PathVariable String configmapName) throws ApiException {
        kubernetesConfigmapService.deleteConfigmap(namespace, configmapName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/namespace/{namespace}/configmap")
    public ResponseEntity<List<V1ConfigMap>> getDeployList(@PathVariable String namespace) throws ApiException {
        return new ResponseEntity<>(kubernetesConfigmapService.getNamespaceConfigmap(namespace), HttpStatus.OK);
    }
}
