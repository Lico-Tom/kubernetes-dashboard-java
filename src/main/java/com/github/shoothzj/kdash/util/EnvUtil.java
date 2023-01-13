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

import org.apache.commons.lang3.StringUtils;

public class EnvUtil {

    public static boolean getBoolean(String env, boolean defaultVal) {
        String envVal = System.getenv(env);
        if (StringUtils.isNotEmpty(envVal)) {
            return Boolean.parseBoolean(envVal);
        }
        return defaultVal;
    }

    public static int getInt(String env, int defaultVal) {
        String envVal = System.getenv(env);
        if (StringUtils.isNotEmpty(envVal)) {
            return Integer.parseInt(envVal);
        }
        return defaultVal;
    }

    public static float getFloat(String env, float defaultVal) {
        String envVal = System.getenv(env);
        if (StringUtils.isNotEmpty(envVal)) {
            return Float.parseFloat(envVal);
        }
        return defaultVal;
    }

    public static double getDouble(String env, double defaultVal) {
        String envVal = System.getenv(env);
        if (StringUtils.isNotEmpty(envVal)) {
            return Double.parseDouble(envVal);
        }
        return defaultVal;
    }

    public static String getString(String env, String defaultVal) {
        String envVal = System.getenv(env);
        if (StringUtils.isNotEmpty(envVal)) {
            return envVal;
        }
        return defaultVal;
    }

}
