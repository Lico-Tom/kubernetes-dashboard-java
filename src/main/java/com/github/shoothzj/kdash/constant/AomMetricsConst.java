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

package com.github.shoothzj.kdash.constant;

public class AomMetricsConst {

    // metric dimensions, deployment or statefulset
    public static final String APP_METRIC_DIMENSIONS_NAME = "appName";

    // metric dimensions, pod
    public static final String POD_METRIC_DIMENSIONS_NAME = "podName";

    public static final String CPU_METRIC_NAME = "cpuUsage";

    public static final String MEMORY_METRIC_NAME = "memUsage";

    // PAAS.CONTAINER:　
    // PAAS.NODE:
    // PAAS.SLA:
    // PAAS.AGGR:
    // CUSTOMMETRICS:
    public static final String CONTAINER_METRICS_NAMESPACE = "PAAS.CONTAINER";

    public static final String NODE_METRICS_NAMESPACE = "PAAS.NODE";

    // available statistics value：maximum,minimum,sum,average,sampleCount

    public static final String AVERAGE_METRIC_STATISTICS_TYPE = "average";

    public static final String MAXIMUM_METRIC_STATISTICS_TYPE = "maximum";

    public static final String MINIMUM_METRIC_STATISTICS_TYPE = "minimum";

}
