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
import com.github.shoothzj.kdash.module.CreateStatefulSetReq;
import com.github.shoothzj.kdash.module.GetStatefulSetResp;
import com.github.shoothzj.kdash.module.ScaleReq;
import com.github.shoothzj.kdash.service.KubernetesStatefulSetService;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesStatefulSetController {

    public KubernetesStatefulSetService kubernetesStatefulSetService;

    public KubernetesStatefulSetController(@Autowired KubernetesStatefulSetService kubernetesStatefulSetService) {
        this.kubernetesStatefulSetService = kubernetesStatefulSetService;
    }

    @PostMapping("/namespace/{namespace}/stateful-sets")
    public ResponseEntity<Void> createStatefulSet(@RequestBody CreateStatefulSetReq req,
                                                  @PathVariable String namespace) throws Exception {
        if (BaseReqType.PARAM.equals(req.getType())) {
            kubernetesStatefulSetService.createNamespacedStatefulSet(namespace, req.getParam());
        } else if (BaseReqType.YAML.equals(req.getType())) {
            kubernetesStatefulSetService.createNamespacedStatefulSetByYaml(namespace, req.getYamlContent());
        } else {
            throw new Exception("unsupportable operation type");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/namespace/{namespace}/stateful-sets/{statefulSetName}")
    public ResponseEntity<Void> deleteDeploy(@PathVariable String namespace,
                                             @PathVariable String statefulSetName) throws ApiException {
        kubernetesStatefulSetService.deleteStatefulSet(namespace, statefulSetName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/namespace/{namespace}/stateful-sets")
    public ResponseEntity<List<GetStatefulSetResp>> getDeployList(@PathVariable String namespace) throws ApiException {
        return new ResponseEntity<>(kubernetesStatefulSetService.getNamespaceStatefulSets(namespace), HttpStatus.OK);
    }

    @PutMapping("/namespace/{namespace}/stateful-sets/scale")
    public ResponseEntity<Void> scaleDeployment(@PathVariable String namespace,
                                                @RequestBody ScaleReq req) throws ApiException {
        kubernetesStatefulSetService.scaleStatefulSet(namespace, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/namespace/{namespace}/stateful-sets/{statefulSetName}/health-check")
    public ResponseEntity<Void> healthCheck(@PathVariable String namespace, @PathVariable String statefulSetName)
            throws ApiException {
        GetStatefulSetResp resp = kubernetesStatefulSetService.getNamespaceStatefulSet(namespace, statefulSetName);
        if (resp == null || resp.getReplicas() != resp.getAvailableReplicas()) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
