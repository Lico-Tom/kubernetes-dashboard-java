/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.shoothzj.kdash.ssh;

import com.github.shoothzj.kdash.util.ShellUtil;
import com.github.shoothzj.kdash.util.StringTool;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

@Slf4j
public class SshClient implements AutoCloseable {

    private static final Pattern cmdEndPattern = Pattern.compile("((.*#)|(.*])|(.*]\\$)|(.*\\(yes/no\\)\\?))\\s$");

    private final JSch jSch;

    private Session session;

    private ChannelShell channel;

    private InputStream inputStream;

    private OutputStream outputStream;

    public SshClient(String host, int port, String username, String password) throws Exception {
        this.jSch = new JSch();
        login(host, port, username, password);
    }

    private void login(String host, int port, String username, String password) throws Exception {
        log.info("login to host {} port {} as {}", host, port, username);
        session = jSch.getSession(username, host, port);
        session.setPassword(password);
        Properties properties = new Properties();
        properties.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.connect();
        channel = (ChannelShell) session.openChannel("shell");
        channel.connect(30_000);
        inputStream = channel.getInputStream();
        outputStream = channel.getOutputStream();
    }

    public String executeOneLineReturn(String cmd, int timeoutSeconds) throws Exception {
        List<String> stringList = execute(cmd, timeoutSeconds);
        if (stringList.size() != 1) {
            return "";
        }
        return stringList.get(0);
    }

    public List<String> execute(String cmd, int timeoutSeconds, Object... args) throws Exception {
        return this.execute(String.format(cmd, args), timeoutSeconds);
    }

    /**
     * @param cmd
     * @param timeoutSeconds
     * @return
     * @throws Exception
     */
    public List<String> execute(String cmd, int timeoutSeconds) throws Exception {
        outputStream.write((cmd + "\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        StringBuilder stringBuilder = new StringBuilder();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutSeconds * 1000L) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String content = ShellUtil.removeColor(new String(bytes, StandardCharsets.UTF_8));
            stringBuilder.append(content);
            String aux = stringBuilder.toString();
            // output contains part of src cmd, continue
            if (cmd.contains(aux)) {
                Thread.sleep(100L);
                continue;
            }
            if (StringTool.anyLineMatch(aux, cmdEndPattern)) {
                break;
            } else {
                Thread.sleep(100L);
            }
        }
        String str = stringBuilder.toString();
        // compat with centos7
        if (str.contains("ast login")) {
            str = str.substring(str.indexOf(session.getUserName()));
        }
        List<String> strings = Arrays.asList(str.split("\\n"));
        log.debug("execute over, result is {}", str);
        return ShellUtil.deleteFirstLastLine(strings);
    }

    public void sftp(String srcFile, String remotePath) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.put(srcFile, remotePath);
        if (!channel.isClosed()) {
            channel.disconnect();
        }
    }

    public void sftpFromRemote(String remotePath, String dstPath) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.get(remotePath, dstPath);
        if (!channel.isClosed()) {
            channel.disconnect();
        }
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}
