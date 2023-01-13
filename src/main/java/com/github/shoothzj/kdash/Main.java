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

package com.github.shoothzj.kdash;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Main {

    private final String staticPath;

    {
        String aux = System.getenv("STATIC_PATH");
        if (aux == null) {
            staticPath = "";
        } else if (aux.endsWith(File.separator)) {
            staticPath = aux;
        } else {
            staticPath = aux + File.separator;
        }
    }

    @Bean
    RouterFunction<ServerResponse> homePageRouter() {
        return RouterFunctions.route(RequestPredicates.GET("/"),
                request -> ServerResponse.ok().bodyValue(new FileSystemResource(staticPath + "index.html")));
    }

    @Bean
    RouterFunction<ServerResponse> staticResourceRouter() {
        return RouterFunctions.resources("/**", new FileSystemResource(staticPath));
    }

    @Bean
    ApiClient createApiClient() throws IOException {
        String kubeConfigPath = System.getenv("KUBERNETES_KUBE_CONFIG_PATH");
        final ApiClient apiClient;
        if (StringUtils.isNotEmpty(kubeConfigPath)) {
            apiClient = ClientBuilder.kubeconfig(
                            KubeConfig.loadKubeConfig(
                                    new FileReader(kubeConfigPath, StandardCharsets.UTF_8)))
                    .build();
        } else {
            apiClient = Config.defaultClient();
        }
        Configuration.setDefaultApiClient(apiClient);
        return apiClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
