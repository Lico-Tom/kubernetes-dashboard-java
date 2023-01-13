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

import com.github.shoothzj.kdash.util.KubernetesYamlUtil;
import io.kubernetes.client.openapi.models.V1Service;

public class ServiceConst {

    public static final V1Service BOOKKEEPER_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("bookkeeper-dashboard-nodeport");

    public static final V1Service BOOKKEEPER_HEADLESS =
            KubernetesYamlUtil.getService("bookkeeper-headless");

    public static final V1Service BOOKKEEPER_TLS_HEADLESS =
            KubernetesYamlUtil.getService("bookkeeper-tls-headless");

    public static final V1Service CASSANDRA_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("cassandra-dashboard-nodeport");

    public static final V1Service GRAFANA_NODEPORT =
            KubernetesYamlUtil.getService("grafana-nodeport");

    public static final V1Service KAFKA_HEADLESS =
            KubernetesYamlUtil.getService("kafka-headless");

    public static final V1Service LOKI_NODEPORT =
            KubernetesYamlUtil.getService("loki-nodeport");

    public static final V1Service MINIO_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("minio-dashboard-nodeport");

    public static final V1Service MINIO_HEADLESS =
            KubernetesYamlUtil.getService("minio-headless");

    public static final V1Service MYSQL_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("mysql-dashboard-nodeport");

    public static final V1Service MYSQL_HEADLESS =
            KubernetesYamlUtil.getService("mysql-headless");

    public static final V1Service PROMETHEUS_NODEPORT =
            KubernetesYamlUtil.getService("prometheus-nodeport");

    public static final V1Service PULSAR_HEADLESS =
            KubernetesYamlUtil.getService("pulsar-headless");

    public static final V1Service PULSAR_PROXY_NODEPORT =
            KubernetesYamlUtil.getService("pulsar-proxy-nodeport");

    public static final V1Service PULSAR_PROXY_TLS_NODEPORT =
            KubernetesYamlUtil.getService("pulsar-proxy-tls-nodeport");

    public static final V1Service PULSAR_TLS_HEADLESS =
            KubernetesYamlUtil.getService("pulsar-tls-headless");

    public static final V1Service REDIS_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("redis-dashboard-nodeport");

    public static final V1Service REDIS_HEADLESS =
            KubernetesYamlUtil.getService("redis-headless");

    public static final V1Service ZOOKEEPER_DASHBOARD_NODEPORT =
            KubernetesYamlUtil.getService("zookeeper-dashboard-nodeport");

    public static final V1Service ZOOKEEPER_HEADLESS =
            KubernetesYamlUtil.getService("zookeeper-headless");

    public static final V1Service ZOOKEEPER_TLS_HEADLESS =
            KubernetesYamlUtil.getService("zookeeper-tls-headless");
}
