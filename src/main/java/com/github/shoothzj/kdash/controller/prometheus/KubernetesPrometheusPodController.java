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

package com.github.shoothzj.kdash.controller.prometheus;

import com.github.shoothzj.kdash.module.CreatePodReq;
import com.github.shoothzj.kdash.service.KubernetesPodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/kubernetes/prometheus")
public class KubernetesPrometheusPodController {

    private final KubernetesPodService kubernetesPodService;

    public KubernetesPrometheusPodController(@Autowired KubernetesPodService kubernetesPodService) {
        this.kubernetesPodService = kubernetesPodService;
    }

    @PostMapping("/namespace/{namespace}/pods")
    public ResponseEntity<Void> createPrometheusPod(@PathVariable String namespace,
                                                    @RequestBody CreatePodReq req) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
