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
import io.kubernetes.client.openapi.models.V1PersistentVolume;

public class PvConst {

    public static final V1PersistentVolume BACKUP_MYSQL_0 =
            KubernetesYamlUtil.getPersistentVolume("backup-mysql-0");

    public static final V1PersistentVolume BACKUP_MYSQL_1 =
            KubernetesYamlUtil.getPersistentVolume("backup-mysql-1");

    public static final V1PersistentVolume DATA_BOOKKEEPER_0 =
            KubernetesYamlUtil.getPersistentVolume("data-bookkeeper-0");

    public static final V1PersistentVolume DATA_BOOKKEEPER_TLS_0 =
            KubernetesYamlUtil.getPersistentVolume("data-bookkeeper-tls-0");

    public static final V1PersistentVolume DATA_LOKI =
            KubernetesYamlUtil.getPersistentVolume("data-loki");

    public static final V1PersistentVolume DATA_MINIO_0 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-0");

    public static final V1PersistentVolume DATA_MINIO_1 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-1");

    public static final V1PersistentVolume DATA_MINIO_2 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-2");

    public static final V1PersistentVolume DATA_MINIO_3 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-3");

    public static final V1PersistentVolume DATA_MINIO_4 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-4");

    public static final V1PersistentVolume DATA_MINIO_5 =
            KubernetesYamlUtil.getPersistentVolume("data-minio-5");

    public static final V1PersistentVolume DATA_MYSQL_0 =
            KubernetesYamlUtil.getPersistentVolume("data-mysql-0");

    public static final V1PersistentVolume DATA_MYSQL_1 =
            KubernetesYamlUtil.getPersistentVolume("data-mysql-1");

    public static final V1PersistentVolume DATA_ZOOKEEPER_0 =
            KubernetesYamlUtil.getPersistentVolume("data-zookeeper-0");

    public static final V1PersistentVolume DATA_ZOOKEEPER_TLS_0 =
            KubernetesYamlUtil.getPersistentVolume("data-zookeeper-tls-0");

    public static final V1PersistentVolume JOURNAL_BOOKKEEPER_0 =
            KubernetesYamlUtil.getPersistentVolume("journal-bookkeeper-0");

    public static final V1PersistentVolume JOURNAL_BOOKKEEPER_TLS_0 =
            KubernetesYamlUtil.getPersistentVolume("journal-bookkeeper-tls-0");
}
