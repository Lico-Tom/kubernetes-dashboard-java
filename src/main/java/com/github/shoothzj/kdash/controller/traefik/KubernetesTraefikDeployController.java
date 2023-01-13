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

package com.github.shoothzj.kdash.controller.traefik;

import com.github.shoothzj.kdash.module.traefik.CreateTraefikReq;
import com.github.shoothzj.kdash.service.traefik.KubernetesTraefikService;
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
@RequestMapping("/api/kubernetes/traefik")
public class KubernetesTraefikDeployController {

    @Autowired
    private KubernetesTraefikService traefikService;

    @PutMapping("/namespace/{namespace}/dashboards")
    public ResponseEntity<Void> createTraefikDashboard(@RequestBody CreateTraefikReq req,
                                                       @PathVariable String namespace) throws ApiException {
        traefikService.createTraefik(namespace, req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/namespace/{namespace}/dashboards/{dashboardName}")
    public ResponseEntity<Void> deleteTraefikDashboard(@PathVariable String namespace,
                                                     @PathVariable String dashboardName) throws ApiException {
        traefikService.deleteTraefik(namespace, dashboardName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
