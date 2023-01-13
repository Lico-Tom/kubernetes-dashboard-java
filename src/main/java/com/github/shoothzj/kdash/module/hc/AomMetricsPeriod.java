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

package com.github.shoothzj.kdash.module.hc;

public enum AomMetricsPeriod {
    LastOneMinute("-1.-1.1"),
    LastFiveMinutes("-1.-1.5"),
    LastTenMinutes("-1.-1.10"),
    LastTwentyMinutes("-1.-1.20"),
    LastThirtyMinutes("-1.-1.30"),
    LastOneHour("-1.-1.60"),
    ;

    private final String value;

    AomMetricsPeriod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
