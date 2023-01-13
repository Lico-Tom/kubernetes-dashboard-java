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

package com.github.shoothzj.kdash.module;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CreateJobParam extends BaseParam {

    private String jobName;

    private Map<String, String> labels;

    private Map<String, String> annotations;

    private Long activeDeadlineSeconds;

    private Integer backoffLimit = 6;

    private Integer completions;

    // if completions is null, completionMode will be nonIndexed
    private String completionMode;

    private Integer parallelism;

    private Integer ttlSecondsAfterFinished;

    private Boolean manualSelector = false;

    private Boolean suspend = false;

    private String podName;

    private String podImage;

    private Map<String, String> podEnv;

    private String podCpu;

    private String podMemory;

    private String podImagePullSecret;

    // Never or OnFailure
    private String podRestartPolicy = "Never";

    private List<String> command;

    private List<String> args;

    public CreateJobParam() {
    }

}
