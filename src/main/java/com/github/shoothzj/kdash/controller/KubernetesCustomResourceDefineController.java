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

import com.github.shoothzj.kdash.module.CreateCustomResourceDefinitionReq;
import com.github.shoothzj.kdash.module.GetCustomResourceDefinitionResp;
import com.github.shoothzj.kdash.service.KubernetesCustomResourceDefineService;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesCustomResourceDefineController {

    private final KubernetesCustomResourceDefineService customResourceDefineService;

    public KubernetesCustomResourceDefineController(KubernetesCustomResourceDefineService customResourceDefineService) {
        this.customResourceDefineService = customResourceDefineService;
    }

    @PostMapping("/custom-resource-definitions")
    public ResponseEntity<Void> createCustomResourceDefinition(
            @RequestBody CreateCustomResourceDefinitionReq req) throws ApiException {
        customResourceDefineService.createCustomResourceDefinition(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/custom-resource-definitions")
    public ResponseEntity<List<GetCustomResourceDefinitionResp>> getCustomResourceDefinitionList() throws ApiException {
        return new ResponseEntity<>(customResourceDefineService.getCustomResourceDefinitionList(), HttpStatus.OK);
    }

}
