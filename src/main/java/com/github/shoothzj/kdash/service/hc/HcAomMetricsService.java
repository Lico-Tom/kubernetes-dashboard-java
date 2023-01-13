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
import com.github.shoothzj.kdash.constant.AomMetricsConst;
import com.github.shoothzj.kdash.exception.AomMetricException;
import com.github.shoothzj.kdash.module.hc.AomMetricsPeriod;
import com.huaweicloud.sdk.aom.v2.AomClient;
import com.huaweicloud.sdk.aom.v2.model.Dimension;
import com.huaweicloud.sdk.aom.v2.model.MetricDataPoints;
import com.huaweicloud.sdk.aom.v2.model.MetricDataValue;
import com.huaweicloud.sdk.aom.v2.model.MetricQueryMeritcParam;
import com.huaweicloud.sdk.aom.v2.model.QueryMetricDataParam;
import com.huaweicloud.sdk.aom.v2.model.ShowMetricsDataRequest;
import com.huaweicloud.sdk.aom.v2.model.ShowMetricsDataResponse;
import com.huaweicloud.sdk.aom.v2.model.StatisticValue;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service
public class HcAomMetricsService {

    private final AomClient aomClient;

    public HcAomMetricsService(@Autowired CloudConfig cloudConfig, @Autowired HuaweiCloudConfig config) {
        aomClient = AomClient.newBuilder()
                .withEndpoint(config.aomEndpoint)
                .withCredential(new BasicCredentials()
                        .withAk(cloudConfig.accessKey)
                        .withSk(cloudConfig.secretKey)
                        .withProjectId(config.projectId)
                )
                .build();
    }

    /**
     * query deployment or statefulset average cpu usage
     *
     * @param appName deployment name or statefulset name
     * @param period metric duration
     * @return metric value
     */
    public Double getAppAverageCpuUsage(String appName, AomMetricsPeriod period) {
        ShowMetricsDataResponse metricsData = queryMetricsData(AomMetricsConst.CPU_METRIC_NAME,
                AomMetricsConst.APP_METRIC_DIMENSIONS_NAME,
                appName, period.getValue());
        return getMetricDataValueAverage(metricsData.getMetrics());
    }

    public Double getAppAverageMemoryUsage(String appName, AomMetricsPeriod period) {
        ShowMetricsDataResponse metricsData = queryMetricsData(AomMetricsConst.MEMORY_METRIC_NAME,
                AomMetricsConst.APP_METRIC_DIMENSIONS_NAME,
                appName, period.getValue());
        return getMetricDataValueAverage(metricsData.getMetrics());
    }

    public Double getPodAverageCpuUsage(String podName, AomMetricsPeriod period) {
        ShowMetricsDataResponse metricsData = queryMetricsData(AomMetricsConst.CPU_METRIC_NAME,
                AomMetricsConst.POD_METRIC_DIMENSIONS_NAME,
                podName, period.getValue());
        return getMetricDataValueAverage(metricsData.getMetrics());
    }

    public Double getPodAverageMemoryUsage(String podName, AomMetricsPeriod period) {
        ShowMetricsDataResponse metricsData = queryMetricsData(AomMetricsConst.MEMORY_METRIC_NAME,
                AomMetricsConst.POD_METRIC_DIMENSIONS_NAME,
                podName, period.getValue());
        return getMetricDataValueAverage(metricsData.getMetrics());
    }


    Double getMetricDataValueAverage(List<MetricDataValue> metricData) {
        int count = 0;
        double sum = 0d;
        for (MetricDataValue metricDataValue : metricData) {
            for (MetricDataPoints metricDataPoints : metricDataValue.getDataPoints()) {
                for (StatisticValue statisticValue : metricDataPoints.getStatistics()) {
                    sum += statisticValue.getValue();
                    count++;
                }
            }
        }
        return new BigDecimal(sum / count).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
    }

    private ShowMetricsDataResponse queryMetricsData(String metricName, String targettype, String targetName,
                                                     String timeRange) {
        ShowMetricsDataResponse response;
        try {
            response = aomClient.showMetricsData(new ShowMetricsDataRequest()
                    .withBody(new QueryMetricDataParam()
                            .withPeriod(60)
                            .withStatistics(Collections.singletonList(AomMetricsConst.AVERAGE_METRIC_STATISTICS_TYPE))
                            .withTimerange(timeRange)
                            .withMetrics(Collections.singletonList(new MetricQueryMeritcParam()
                                    .withNamespace(AomMetricsConst.CONTAINER_METRICS_NAMESPACE)
                                    .withDimensions(Collections.singletonList(new Dimension()
                                            .withName(targettype)
                                            .withValue(targetName)))
                                    .withMetricName(metricName)
                            ))
                    ));
            int errorCode = response.getHttpStatusCode();
            if (errorCode != HttpStatus.OK.value()) {
                throw new AomMetricException.RemoteInternalException(String.format("Remote AOM ERROR: %s",
                        response.getErrorMessage()));
            }
        } catch (Exception e) {
            throw new AomMetricException.RemoteInternalException(String.format("Remote AOM ERROR: %s", e.getMessage()));
        }
        return response;
    }

}
