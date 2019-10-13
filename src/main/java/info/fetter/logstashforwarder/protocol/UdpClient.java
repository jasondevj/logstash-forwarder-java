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

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UdpClient implements ProtocolAdapter {

    public static final int MAX_PACKET_SIZE = 512;
    private final String server;
    private final DatagramSocket socket;
    private InetAddress address;
    private int port;
    private int timeout;

    public UdpClient(String server, int port, int timeout) throws SocketException, UnknownHostException {
        this.port = port;
        this.timeout = timeout;
        this.server = server;
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(server);
    }

    public static List<String> splitEqually(StringBuilder text) {
        List<String> ret = new ArrayList<String>();

        for (int start = 0; start < text.length(); start += MAX_PACKET_SIZE) {
            ret.add(text.substring(start, Math.min(text.length(), start + MAX_PACKET_SIZE)));
        }
        return ret;
    }

    public int sendEvents(List<Event> eventList) throws AdapterException {

        StringBuilder line = new StringBuilder();
        int length = 0;
        for (Event event : eventList) {
            line.append("host:").append(new String(event.getKeyValues().get("host")));
            line.append(" ");
            line.append(new String(event.getKeyValues().get("line")));
            line.append('\n');
            if (line.length() > MAX_PACKET_SIZE) {
                List<String> stringList = splitEqually(line);
                for (String split : stringList) {
                    length += send(split.getBytes());
                    line = new StringBuilder();
                }
            } else {
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
        return send(line.toString().getBytes());
    }

    private int send(byte[] sendBytes) throws AdapterException {
        DatagramPacket packet = new DatagramPacket(sendBytes, sendBytes.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new AdapterException(e);
        }
        return packet.getLength();
    }

    public void close() {
        socket.close();
    }

    public String getServer() {
        return address.getHostAddress();
    }

    public int getPort() {
        return port;
    }

}
