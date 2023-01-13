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

package com.github.shoothzj.kdash.module.hc;

import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerDataVolume;
import com.huaweicloud.sdk.ecs.v2.model.PostPaidServerRootVolume;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HcCreateServersRequest {

    private String name;

    private String subnetId;

    private String vpcId;

    private String flavorId;

    private String imageId;

    /**
     * when rootVolumeSize is 0, min_disk in image will be default value
     */
    private int rootVolumeSize;

    private PostPaidServerRootVolume.VolumetypeEnum rootVolumeType;

    private int dataVolumeSize;

    private PostPaidServerDataVolume.VolumetypeEnum dataVolumeType;

    /**
     * when need eip ,set this param in 1~2000
     */
    private int eipBandWithInMb;

    private String eipType = "5_bgp";

    public HcCreateServersRequest() {
    }

}
