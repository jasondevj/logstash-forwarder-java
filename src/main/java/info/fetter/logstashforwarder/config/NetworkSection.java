package info.fetter.logstashforwarder.config;

/*
 * Copyright 2015 Didier Fetter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.util.List;

public class NetworkSection {
    private List<Server> servers;
    private int timeout;

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "NetworkSection{" +
                "servers=" + servers +
                ", timeout=" + timeout +
                '}';
    }

    public static class Server {
        private String protocol;
        private String connection;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getConnection() {
            return connection;
        }

        public void setConnection(String connection) {
            this.connection = connection;
        }

        @Override
        public String toString() {
            return "Server{" +
                    "protocol='" + protocol + '\'' +
                    ", connection='" + connection + '\'' +
                    '}';
        }
    }
}
