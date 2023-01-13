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
import io.kubernetes.client.openapi.models.V1StatefulSet;

public class StatefulSetConst {

    public static final V1StatefulSet BOOKKEEPER =
            KubernetesYamlUtil.getStatefulSet("bookkeeper");

    public static final V1StatefulSet BOOKKEEPER_TLS =
            KubernetesYamlUtil.getStatefulSet("bookkeeper-tls");

    public static final V1StatefulSet MINIO =
            KubernetesYamlUtil.getStatefulSet("minio");

    public static final V1StatefulSet MYSQL =
            KubernetesYamlUtil.getStatefulSet("mysql");

    public static final V1StatefulSet REDIS =
            KubernetesYamlUtil.getStatefulSet("redis");

    public static final V1StatefulSet ZOOKEEPER =
            KubernetesYamlUtil.getStatefulSet("zookeeper");

    public static final V1StatefulSet ZOOKEEPER_TLS =
            KubernetesYamlUtil.getStatefulSet("zookeeper-tls");
}
