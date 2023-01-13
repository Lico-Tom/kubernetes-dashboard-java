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
import com.github.shoothzj.kdash.module.CreateDeploymentReq;
import com.github.shoothzj.kdash.module.GetDeploymentResp;
import com.github.shoothzj.kdash.module.ScaleReq;
import com.github.shoothzj.kdash.service.KubernetesDeployService;
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
public class KubernetesDeployController {

    public KubernetesDeployService deployService;

    public KubernetesDeployController(@Autowired KubernetesDeployService deployService) {
        this.deployService = deployService;
    }

    @PostMapping("/namespace/{namespace}/deployments")
    public ResponseEntity<Void> createDeployment(@PathVariable String namespace,
                                                 @RequestBody CreateDeploymentReq req) throws Exception {
        if (BaseReqType.PARAM.equals(req.getType())) {
            deployService.createNamespacedDeploy(namespace, req.getParam());
        } else if (BaseReqType.YAML.equals(req.getType())) {
            deployService.createDeploymentByYaml(namespace, req.getYamlContent());
        } else {
            throw new Exception("unsupportable operation type");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/namespace/{namespace}/deployments/{deployName}")
    public ResponseEntity<Void> deleteDeploy(@PathVariable String namespace,
                                             @PathVariable String deployName) throws ApiException {
        deployService.deleteDeploy(namespace, deployName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/namespace/{namespace}/deployments")
    public ResponseEntity<List<GetDeploymentResp>> getDeployList(@PathVariable String namespace) throws ApiException {
        return new ResponseEntity<>(deployService.getNamespaceDeployments(namespace), HttpStatus.OK);
    }

    @PutMapping("/namespace/{namespace}/deployments/scale")
    public ResponseEntity<Void> scaleDeployment(@PathVariable String namespace,
                                                @RequestBody ScaleReq req) throws ApiException {
        deployService.scaleDeploy(namespace, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/namespace/{namespace}/deployments/{deployName}/health-check")
    public ResponseEntity<Void> healthCheck(@PathVariable String namespace, @PathVariable String deployName)
            throws ApiException {
        GetDeploymentResp resp = deployService.getNamespaceDeployment(namespace, deployName);
        if (resp == null || resp.getReplicas() != resp.getAvailableReplicas()) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
