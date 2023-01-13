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

package com.github.shoothzj.kdash.config;

import com.github.shoothzj.kdash.util.EnvUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HuaweiCloudConfig {

    @Value("${HUAWEI_CLOUD_PROJECT_ID:}")
    public String projectId;

    public final String region = EnvUtil.getString("HUAWEI_CLOUD_REGION", "cn-north-4");

    public final String aomEndpoint = "aom." + region + ".myhuaweicloud.com";

    public final String cceEndpoint = "cce." + region + ".myhuaweicloud.com";

    public final String disEndpoint = "dis." + region + ".myhuaweicloud.com";

    public final String functiongraphEndpoint = "functiongraph." + region + ".myhuaweicloud.com";

    public final String ecsEndpoint = "ecs." + region + ".myhuaweicloud.com";

    public final String evsEndpoint = "evs." + region + ".myhuaweicloud.com";

    public final String ltsEndpoint = "lts." + region + ".myhuaweicloud.com";

    public final String swrEndpoint = "swr." + region + ".myhuaweicloud.com";

}
