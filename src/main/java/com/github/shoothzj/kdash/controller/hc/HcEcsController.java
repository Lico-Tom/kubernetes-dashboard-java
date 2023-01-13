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

package com.github.shoothzj.kdash.controller.hc;

import com.github.shoothzj.kdash.module.hc.HcCreateServersRequest;
import com.github.shoothzj.kdash.service.hc.HcEcsService;
import com.huaweicloud.sdk.ecs.v2.model.ListServersDetailsRequest;
import com.huaweicloud.sdk.ecs.v2.model.ListServersDetailsResponse;
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

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/hc/ecs")
public class HcEcsController {

    private final HcEcsService ecsService;

    public HcEcsController(@Autowired HcEcsService ecsService) {
        this.ecsService = ecsService;
    }

    @PutMapping("servers")
    public ResponseEntity<Void> createServer(@RequestBody HcCreateServersRequest req)
            throws ExecutionException, InterruptedException {
        ecsService.createServer(req).get();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("servers/{serverName}")
    public ResponseEntity<Void> deleteServer(@PathVariable String serverName)
            throws ExecutionException, InterruptedException {
        ecsService.deleteServer(serverName).get();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("servers/{serverName}/changeos/{imageId}")
    public ResponseEntity<Void> changeos(@PathVariable String imageId, @PathVariable String serverName)
            throws ExecutionException, InterruptedException {
        ecsService.changeos(serverName, imageId).get();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("servers/detail")
    public ResponseEntity<ListServersDetailsResponse> getServers() throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(ecsService.getEcsServerList(new ListServersDetailsRequest()).get(), HttpStatus.OK);
    }

}
