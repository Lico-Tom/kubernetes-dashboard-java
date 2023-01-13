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

package com.github.shoothzj.kdash.service.ssh;

import com.github.shoothzj.kdash.module.ssh.CommandReq;
import com.github.shoothzj.kdash.module.ssh.CommandResp;
import com.github.shoothzj.kdash.module.ssh.UserInfo;
import com.github.shoothzj.kdash.ssh.SshClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SshService {

    public List<CommandResp> command(CommandReq req) {
        List<CommandResp> commandResps = new ArrayList<>();
        for (UserInfo user : req.getUsers()) {
            CommandResp commandResp = new CommandResp();
            try (
                    SshClient sshClient = new SshClient(user.getHost(), user.getPort(),
                            user.getUsername(), user.getPassword())
            ) {
                List<String> list = sshClient.execute(req.getCommand(), 30);
                commandResp.setResult(list);
                commandResp.setHost(user.getHost());
            } catch (Exception e) {
                log.error("execute command fail. host[{}] ", user.getHost(), e);
                commandResp.setResult(List.of(e.getMessage()));
                commandResp.setHost(user.getHost());
            }
            commandResps.add(commandResp);
        }
        return commandResps;
    }

}
