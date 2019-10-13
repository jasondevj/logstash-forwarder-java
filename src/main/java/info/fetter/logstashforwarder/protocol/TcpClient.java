package info.fetter.logstashforwarder.protocol;

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

import info.fetter.logstashforwarder.Event;
import info.fetter.logstashforwarder.ProtocolAdapter;
import info.fetter.logstashforwarder.util.AdapterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class TcpClient implements ProtocolAdapter {

    private final String server;
    private int port;
    private int timeout;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TcpClient(String server, int port, int timeout) throws IOException {
        this.port = port;
        this.timeout = timeout;
        this.server = server;
        clientSocket = new Socket(server, port);
        connect();
    }

    private void connect() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    public int sendEvents(List<Event> eventList) throws AdapterException {

        StringBuilder line = new StringBuilder();
        int length = 0;
        for (Event event : eventList) {
            for (Map.Entry<String, byte[]> entry : event.getKeyValues().entrySet()) {
                line.append(entry.getKey()).append(':').append(new String(entry.getValue())).append(' ');
            }
            line.append('\n');
            if (line.length() > 100240) {
                length += send(line);
                line = new StringBuilder();
            }
        }
        if (line.length() > 0) {
            length += send(line);
        }
        return length;
    }

    private int send(StringBuilder line) throws AdapterException {
        if (clientSocket.isBound() && clientSocket.isConnected()) {
            out.println(line);
        } else {
            throw new AdapterException("Connection closed");
        }
        return 0;
    }

    public void close() {
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

}
