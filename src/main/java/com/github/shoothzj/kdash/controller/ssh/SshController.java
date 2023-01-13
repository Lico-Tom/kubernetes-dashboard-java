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

package com.github.shoothzj.kdash.controller.ssh;

import com.github.shoothzj.kdash.module.ssh.CommandReq;
import com.github.shoothzj.kdash.module.ssh.CommandResp;
import com.github.shoothzj.kdash.service.ssh.SshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/kubernetes/ssh")
public class SshController {

    private final SshService sshService;

    public SshController(@Autowired SshService sshService) {
        this.sshService = sshService;
    }

    @PostMapping("/command")
    public ResponseEntity<List<CommandResp>> command(@RequestBody CommandReq req) {
        return new ResponseEntity<>(sshService.command(req), HttpStatus.OK);
    }
}
