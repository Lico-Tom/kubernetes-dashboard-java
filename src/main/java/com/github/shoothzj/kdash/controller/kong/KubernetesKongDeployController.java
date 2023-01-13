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

package com.github.shoothzj.kdash.controller.kong;

import com.github.shoothzj.kdash.module.kong.CreateKongReq;
import com.github.shoothzj.kdash.service.kong.KubernetesKongService;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/kubernetes/kong")
public class KubernetesKongDeployController {

    private final KubernetesKongService kongService;

    public KubernetesKongDeployController(@Autowired KubernetesKongService kongService) {
        this.kongService = kongService;
    }

    @PutMapping("/namespace/{namespace}/deployments")
    public ResponseEntity<Void> createKong(@PathVariable String namespace,
                                           @RequestBody CreateKongReq req) throws ApiException {
        kongService.createKong(namespace, req);
    return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/namespace/{namespace}/deployments/{deployName}")
    public ResponseEntity<Void> deleteKong(@PathVariable String namespace,
                                           @PathVariable String deployName) throws ApiException {
        kongService.deleteKong(namespace, deployName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
