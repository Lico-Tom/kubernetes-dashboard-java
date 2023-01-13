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

package com.github.shoothzj.kdash.service.hc;

import com.github.shoothzj.kdash.config.CloudConfig;
import com.github.shoothzj.kdash.config.HuaweiCloudConfig;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.evs.v2.EvsAsyncClient;
import com.huaweicloud.sdk.evs.v2.model.CinderListAvailabilityZonesRequest;
import com.huaweicloud.sdk.evs.v2.model.CinderListAvailabilityZonesResponse;
import com.huaweicloud.sdk.evs.v2.model.CreateVolumeRequest;
import com.huaweicloud.sdk.evs.v2.model.CreateVolumeResponse;
import com.huaweicloud.sdk.evs.v2.model.DeleteVolumeRequest;
import com.huaweicloud.sdk.evs.v2.model.DeleteVolumeResponse;
import com.huaweicloud.sdk.evs.v2.model.ListVolumesRequest;
import com.huaweicloud.sdk.evs.v2.model.ListVolumesResponse;
import com.huaweicloud.sdk.evs.v2.model.VolumeDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class HcEvsService {

    private final EvsAsyncClient evsClient;

    public HcEvsService(@Autowired CloudConfig cloudConfig, @Autowired HuaweiCloudConfig config) {
        evsClient = EvsAsyncClient.newBuilder()
                .withEndpoint(config.evsEndpoint)
                .withCredential(new BasicCredentials()
                        .withAk(cloudConfig.accessKey)
                        .withSk(cloudConfig.secretKey)
                        .withProjectId(config.projectId)
                )
                .build();
    }

    public CompletableFuture<CreateVolumeResponse> createVolume(CreateVolumeRequest request) {
        return evsClient.createVolumeAsync(request);
    }

    public CompletableFuture<DeleteVolumeResponse> deleteVolume(DeleteVolumeRequest request) {
        return evsClient.deleteVolumeAsync(request);
    }

    public CompletableFuture<CinderListAvailabilityZonesResponse> getVolumeAz() {
        return evsClient.cinderListAvailabilityZonesAsync(new CinderListAvailabilityZonesRequest());
    }

    public CompletableFuture<ListVolumesResponse> listVolumeByVolumeName(String name) {
        ListVolumesRequest request = new ListVolumesRequest();
        request.setName(name);
        return evsClient.listVolumesAsync(request);
    }

    public CompletableFuture<List<String>> listVolumeIdByVolumeName(String name) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        this.listVolumeByVolumeName(name).thenAcceptAsync(listVolumes -> {
            List<String> list = listVolumes.getVolumes().stream()
                    .filter(volumeDetail -> name.equals(volumeDetail.getName()))
                    .map(VolumeDetail::getId)
                    .collect(Collectors.toList());
            future.complete(list);
        });
        return future;
    }
}
