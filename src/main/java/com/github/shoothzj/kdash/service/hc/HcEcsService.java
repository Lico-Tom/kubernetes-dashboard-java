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
import com.github.shoothzj.kdash.module.hc.HcCreateServersRequest;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.ecs.v2.EcsAsyncClient;
import com.huaweicloud.sdk.ecs.v2.model.ChangeServerOsWithCloudInitOption;
import com.huaweicloud.sdk.ecs.v2.model.ChangeServerOsWithCloudInitRequest;
import com.huaweicloud.sdk.ecs.v2.model.ChangeServerOsWithCloudInitRequestBody;
import com.huaweicloud.sdk.ecs.v2.model.ChangeServerOsWithCloudInitResponse;
import com.huaweicloud.sdk.ecs.v2.model.CreatePostPaidServersRequest;
import com.huaweicloud.sdk.ecs.v2.model.CreatePostPaidServersRequestBody;
import com.huaweicloud.sdk.ecs.v2.model.CreatePostPaidServersResponse;
import com.huaweicloud.sdk.ecs.v2.model.DeleteServersRequest;
import com.huaweicloud.sdk.ecs.v2.model.DeleteServersRequestBody;
import com.huaweicloud.sdk.ecs.v2.model.ListServersDetailsRequest;
import com.huaweicloud.sdk.ecs.v2.model.ListServersDetailsResponse;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServer;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerDataVolume;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerEip;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerEipBandwidth;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerNic;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerPublicip;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerRootVolume;
import com.huaweicloud.sdk.ecs.v2.model.ServerDetail;
import com.huaweicloud.sdk.ecs.v2.model.ServerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class HcEcsService {
    private final EcsAsyncClient ecsClient;

    public HcEcsService(@Autowired CloudConfig cloudConfig, @Autowired HuaweiCloudConfig config) {
        ecsClient = EcsAsyncClient.newBuilder()
                .withEndpoint(config.ecsEndpoint)
                .withCredential(new BasicCredentials()
                        .withAk(cloudConfig.accessKey)
                        .withSk(cloudConfig.secretKey)
                        .withProjectId(config.projectId)
                )
                .build();
    }

    public CompletableFuture<ListServersDetailsResponse> getEcsServerList(ListServersDetailsRequest request) {
        return ecsClient.listServersDetailsAsync(request);
    }

    public CompletableFuture<Void> changeos(String serverName, String imageId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ecsClient.listServersDetailsAsync(new ListServersDetailsRequest().withName(serverName)).whenComplete(
                ((listServersDetailsResponse, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException(ex);
                    }
                    List<String> serverIds = listServersDetailsResponse.getServers().stream()
                            .map(ServerDetail::getId).toList();
                    List<CompletableFuture<ChangeServerOsWithCloudInitResponse>> tasks = new ArrayList<>();
                    for (String serverId : serverIds) {
                        ChangeServerOsWithCloudInitRequest changeServerOsRequest =
                                new ChangeServerOsWithCloudInitRequest();
                        changeServerOsRequest.setServerId(serverId);
                        ChangeServerOsWithCloudInitRequestBody body = new ChangeServerOsWithCloudInitRequestBody();
                        ChangeServerOsWithCloudInitOption option = new ChangeServerOsWithCloudInitOption();
                        option.setImageid(imageId);
                        option.setMode("withStopServer");
                        body.setOsChange(option);
                        changeServerOsRequest.setBody(body);
                        tasks.add(ecsClient.changeServerOsWithCloudInitAsync(changeServerOsRequest));
                    }
                    for (CompletableFuture<ChangeServerOsWithCloudInitResponse> task : tasks) {
                        try {
                            task.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    future.complete(null);
                }));
        return future;
    }

    public CompletableFuture<CreatePostPaidServersResponse> createServer(HcCreateServersRequest req) {
        CreatePostPaidServersRequest createServersRequest = new CreatePostPaidServersRequest();
        CreatePostPaidServersRequestBody body = new CreatePostPaidServersRequestBody();
        PostPaidServer server = new PostPaidServer();
        server.setName(req.getName());
        server.addDataVolumesItem(new PostPaidServerDataVolume()
                .withSize(req.getDataVolumeSize())
                .withVolumetype(req.getDataVolumeType()));
        server.setVpcid(req.getVpcId());
        server.setNics(List.of(new PostPaidServerNic()
                .withSubnetId(req.getSubnetId())));
        server.setFlavorRef(req.getFlavorId());
        server.setImageRef(req.getImageId());
        server.setRootVolume(new PostPaidServerRootVolume()
                .withSize(req.getDataVolumeSize())
                .withVolumetype(req.getRootVolumeType()));
        body.setServer(server);
        createServersRequest.setBody(body);
        if (req.getEipBandWithInMb() != 0) {
            server.setPublicip(new PostPaidServerPublicip()
                    .withDeleteOnTermination(true)
                    .withEip(new PostPaidServerEip()
                            .withIptype(req.getEipType())
                            .withBandwidth(new PostPaidServerEipBandwidth()
                                    .withChargemode("traffic")
                                    .withSharetype(PostPaidServerEipBandwidth.SharetypeEnum.PER)
                                    .withSize(req.getEipBandWithInMb()))));
        }
        return ecsClient.createPostPaidServersAsync(createServersRequest);
    }

    public CompletableFuture<Void> deleteServer(String name) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ecsClient.listServersDetailsAsync(new ListServersDetailsRequest().withName(name))
                .whenComplete((resp, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException(ex);
                    }
                    List<ServerId> serverIdList = resp.getServers().stream().map(
                            serverDetail -> new ServerId().withId(serverDetail.getId())).toList();
                    DeleteServersRequest deleteServersRequest = new DeleteServersRequest()
                            .withBody(new DeleteServersRequestBody().withServers(serverIdList));
                    try {
                        ecsClient.deleteServersAsync(deleteServersRequest).get();
                    } catch (Throwable e) {
                        future.completeExceptionally(e);
                        throw new RuntimeException(e);
                    }
                });
        return future;
    }
}
