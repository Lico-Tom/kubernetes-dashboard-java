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

import org.jetbrains.annotations.Nullable;

public class NumberUtil {
    public static boolean greatEqualThan(@Nullable Double target, @Nullable Double current,
                                         @Nullable Double tolerance) {
        if (target == null || current == null || tolerance == null) {
            throw new IllegalArgumentException("metric compare arguments should not be null");
        }
        return current >= tolerance * target;
    }

    public static boolean lessEqualThan(@Nullable Double target, @Nullable Double current,
                                        @Nullable Double tolerance) {
        if (target == null || current == null || tolerance == null) {
            throw new IllegalArgumentException("metric compare arguments should not be null");
        }
        return current <= tolerance * target;
    }

    public static boolean roundEquals(@Nullable Double target, @Nullable Double current,
                                      @Nullable Double tolerance) {
        if (target == null || current == null || tolerance == null) {
            throw new IllegalArgumentException("metric compare arguments should not be null");
        }
        return current <= target * (1 + tolerance) && current >= target * (1 - tolerance);
    }

}
