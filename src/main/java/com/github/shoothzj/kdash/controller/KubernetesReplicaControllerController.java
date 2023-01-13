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

import com.github.shoothzj.kdash.module.CreateReplicaReq;
import com.github.shoothzj.kdash.module.GetReplicaResp;
import com.github.shoothzj.kdash.module.ScaleReplicaReq;
import com.github.shoothzj.kdash.service.KubernetesReplicaControllerService;
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
public class KubernetesReplicaControllerController {

    private final KubernetesReplicaControllerService kubernetesReplicaControllerService;

    public KubernetesReplicaControllerController(
            @Autowired KubernetesReplicaControllerService kubernetesReplicaControllerService) {
        this.kubernetesReplicaControllerService = kubernetesReplicaControllerService;
    }

    @PostMapping("/namespaces/{namespace}/replicas")
    public ResponseEntity<Void> createReplicas(@PathVariable String namespace,
                                          @RequestBody CreateReplicaReq req) throws ApiException {
        kubernetesReplicaControllerService.createReplica(namespace, req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("namespaces/{namespace}/replicas/{replicaName}")
    public ResponseEntity<Void> deleteReplicas(@PathVariable String namespace,
                                             @PathVariable String replicaName) throws ApiException {
        kubernetesReplicaControllerService.deleteReplicas(namespace, replicaName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/namespaces/{namespace}/replicas")
    public ResponseEntity<List<GetReplicaResp>> getReplicasList(@PathVariable String namespace) throws ApiException {
        return new ResponseEntity<>(kubernetesReplicaControllerService.getReplicas(namespace), HttpStatus.OK);
    }

    @PutMapping("/namespace/{namespace}/replicas/scale")
    public ResponseEntity<Void> scaleDeployment(@PathVariable String namespace,
                                                @RequestBody ScaleReplicaReq req) throws ApiException {
        kubernetesReplicaControllerService.scaleReplica(namespace, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
