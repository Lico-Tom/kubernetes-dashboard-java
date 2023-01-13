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
import io.kubernetes.client.openapi.models.V1Deployment;

public class DeployConst {

    public static final V1Deployment APISIX = KubernetesYamlUtil.getDeployment("apisix");

    public static final V1Deployment APISIX_DASHBOARD = KubernetesYamlUtil.getDeployment("apisix-dashboard");

    public static final V1Deployment BOOKKEEPER_DASHBOARD = KubernetesYamlUtil.getDeployment("bookkeeper-dashboard");

    public static final V1Deployment BOOKKEEPER_TLS_DASHBOARD =
            KubernetesYamlUtil.getDeployment("bookkeeper-tls-dashboard");

    public static final V1Deployment CASSANDRA_DASHBOARD = KubernetesYamlUtil.getDeployment("cassandra-dashboard");

    public static final V1Deployment ETCD_DASHBOARD = KubernetesYamlUtil.getDeployment("etcd-dashboard");

    public static final V1Deployment GRAFANA = KubernetesYamlUtil.getDeployment("grafana");

    public static final V1Deployment KEEPALIVE = KubernetesYamlUtil.getDeployment("keepalive");

    public static final V1Deployment LOKI = KubernetesYamlUtil.getDeployment("loki");

    public static final V1Deployment MINIO_DASHBOARD = KubernetesYamlUtil.getDeployment("minio-dashboard");

    public static final V1Deployment MYSQL_DASHBOARD = KubernetesYamlUtil.getDeployment("mysql-dashboard");

    public static final V1Deployment NGINX = KubernetesYamlUtil.getDeployment("nginx");

    public static final V1Deployment NGINX_DASHBOARD = KubernetesYamlUtil.getDeployment("nginx-dashboard");

    public static final V1Deployment PROMETHEUS = KubernetesYamlUtil.getDeployment("prometheus");

    public static final V1Deployment PULSAR = KubernetesYamlUtil.getDeployment("pulsar");

    public static final V1Deployment PULSAR_DASHBOARD = KubernetesYamlUtil.getDeployment("pulsar-dashboard");

    public static final V1Deployment PULSAR_PROXY = KubernetesYamlUtil.getDeployment("pulsar-proxy");

    public static final V1Deployment PULSAR_PROXY_TLS = KubernetesYamlUtil.getDeployment("pulsar-proxy-tls");

    public static final V1Deployment PULSAR_TLS = KubernetesYamlUtil.getDeployment("pulsar-tls");

    public static final V1Deployment PULSAR_TLS_DASHBOARD = KubernetesYamlUtil.getDeployment("pulsar-tls-dashboard");

    public static final V1Deployment REDIS_DASHBOARD = KubernetesYamlUtil.getDeployment("redis-dashboard");

    public static final V1Deployment ZOOKEEPER_DASHBOARD = KubernetesYamlUtil.getDeployment("zookeeper-dashboard");

    public static final V1Deployment ZOOKEEPER_TLS_DASHBOARD =
            KubernetesYamlUtil.getDeployment("zookeeper-tls-dashboard");

}
