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

import com.github.shoothzj.kdash.service.hc.HcEvsService;
import com.huaweicloud.sdk.evs.v2.model.CinderListAvailabilityZonesResponse;
import com.huaweicloud.sdk.evs.v2.model.CreateVolumeRequest;
import com.huaweicloud.sdk.evs.v2.model.CreateVolumeResponse;
import com.huaweicloud.sdk.evs.v2.model.DeleteVolumeRequest;
import com.huaweicloud.sdk.evs.v2.model.DeleteVolumeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/hc/evs")
public class HcEvsController {

    private final HcEvsService evsService;

    public HcEvsController(@Autowired HcEvsService evsService) {
        this.evsService = evsService;
    }

    @PutMapping("volumes")
    public ResponseEntity<CreateVolumeResponse> createHuaweiCloudVolume(@RequestBody CreateVolumeRequest req)
            throws ExecutionException, InterruptedException {
        Future<CreateVolumeResponse> future = evsService.createVolume(req)
                .whenComplete((createVolumeResponse, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException(ex);
                    }
                });
        return new ResponseEntity<>(future.get(), HttpStatus.CREATED);
    }

    @DeleteMapping("volumes/{volumeId}")
    public ResponseEntity<DeleteVolumeResponse> deleteHuaweiCloudVolume(@PathVariable String volumeId)
            throws ExecutionException, InterruptedException {
        DeleteVolumeRequest req = new DeleteVolumeRequest().withVolumeId(volumeId);
        Future<DeleteVolumeResponse> future = evsService.deleteVolume(req)
                .whenComplete((deleteVolumeResponse, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException(ex);
                    }
                });
        return new ResponseEntity<>(future.get(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("volume-az")
    public ResponseEntity<CinderListAvailabilityZonesResponse> getVolumeAz()
            throws ExecutionException, InterruptedException {
        Future<CinderListAvailabilityZonesResponse> future = evsService.getVolumeAz()
                .whenComplete((cinderListAvailabilityZonesResponse, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException(ex);
                    }
                });
        return new ResponseEntity<>(future.get(), HttpStatus.OK);
    }

    @GetMapping("volumes/{volumeName}")
    public ResponseEntity<List<String>> getVolumeByName(@PathVariable String volumeName)
            throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(evsService.listVolumeIdByVolumeName(volumeName).get(), HttpStatus.OK);
    }

}
