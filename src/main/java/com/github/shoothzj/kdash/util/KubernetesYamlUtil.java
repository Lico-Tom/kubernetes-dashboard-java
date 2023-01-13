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

package com.github.shoothzj.kdash.util;

import com.github.shoothzj.kdash.module.Kind;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KubernetesYamlUtil {

    public static V1ConfigMap getConfigMap(String name) {
        return get(Kind.ConfigMap, name, V1ConfigMap.class);
    }

    public static V1DaemonSet getDaemonSet(String name) {
        return get(Kind.DaemonSet, name, V1DaemonSet.class);
    }

    public static V1Deployment getDeployment(String name) {
        return get(Kind.Deployment, name, V1Deployment.class);
    }

    public static V1PersistentVolume getPersistentVolume(String name) {
        return get(Kind.Pv, name, V1PersistentVolume.class);
    }

    public static V1PersistentVolumeClaim getPersistentVolumeClaim(String name) {
        return get(Kind.Pvc, name, V1PersistentVolumeClaim.class);
    }

    public static V1Secret getSecret(String name) {
        return get(Kind.Secret, name, V1Secret.class);
    }

    public static V1Service getService(String name) {
        return get(Kind.Service, name, V1Service.class);
    }

    public static V1StatefulSet getStatefulSet(String name) {
        return get(Kind.StatefulSet, name, V1StatefulSet.class);
    }

    public static <T> T get(Kind kind, String name, Class<T> clazz) {
        return Yaml.loadAs(getContent(kind, name), clazz);
    }

    public static String getContent(Kind kind, String name) {
        File file = getFileNoEx(getKindDir(kind) + File.separator + name + ".yaml");
        try {
            return IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getKindDir(Kind kind) {
        return "src" + File.separator + "main" + File.separator + "resources"
                + File.separator + kind.name().toLowerCase();
    }

    private static File getFileNoEx(String location) {
        try {
            return ResourceUtils.getFile(location);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
