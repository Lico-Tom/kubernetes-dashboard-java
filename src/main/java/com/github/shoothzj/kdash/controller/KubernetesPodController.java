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
import com.github.shoothzj.kdash.module.CreatePodReq;
import com.github.shoothzj.kdash.module.GetPodResp;
import com.github.shoothzj.kdash.module.ResourceReq;
import com.github.shoothzj.kdash.service.KubernetesPodService;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesPodController {

    private final KubernetesPodService kubernetesPodService;

    public KubernetesPodController(@Autowired KubernetesPodService kubernetesPodService) {
        this.kubernetesPodService = kubernetesPodService;
    }

    @PostMapping("/namespaces/{namespace}/pods")
    public ResponseEntity<Void> createPod(@PathVariable String namespace,
                                          @RequestBody CreatePodReq req) throws Exception {
        if (BaseReqType.PARAM.equals(req.getType())) {
            kubernetesPodService.createPod(namespace, req.getParam());
        } else if (BaseReqType.YAML.equals(req.getType())) {
            kubernetesPodService.createPodByYaml(namespace, req.getYamlContent());
        } else {
            throw new Exception("unsupportable operation type");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("namespaces/{namespace}/pods/{podName}")
    public ResponseEntity<Void> deletePod(@PathVariable String namespace,
                                          @PathVariable String podName) throws ApiException {
        kubernetesPodService.deletePod(namespace, podName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/namespaces/{namespace}/pods")
    public ResponseEntity<List<GetPodResp>> getPodList(@PathVariable String namespace) throws ApiException {
        return new ResponseEntity<>(kubernetesPodService.getNamespacePods(namespace), HttpStatus.OK);
    }

    @PostMapping("/namespace/{namespace}/pods/{podName}/update-resource")
    public ResponseEntity<Void> updateResource(@PathVariable String namespace,
                                               @PathVariable String podName,
                                               @RequestParam String kind,
                                               @RequestBody ResourceReq req) throws Exception {
        kubernetesPodService.updateResource(namespace, podName, kind, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
