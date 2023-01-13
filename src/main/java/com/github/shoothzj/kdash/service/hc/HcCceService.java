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
import com.huaweicloud.sdk.cce.v3.CceAsyncClient;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HcCceService {

    private final CceAsyncClient cceClient;

    public HcCceService(@Autowired CloudConfig cloudConfig, @Autowired HuaweiCloudConfig config) {
        cceClient = CceAsyncClient.newBuilder()
                .withEndpoint(config.cceEndpoint)
                .withCredential(new BasicCredentials()
                        .withAk(cloudConfig.accessKey)
                        .withSk(cloudConfig.secretKey)
                        .withProjectId(config.projectId)
                )
                .build();
    }
}
