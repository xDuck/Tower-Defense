package com.chrisnastovski.towerdefense;

import com.chrisnastovski.towerdefense.MultiplayerObjects.Lobby;
import com.chrisnastovski.towerdefense.MultiplayerObjects.clientHandshake;
import com.chrisnastovski.towerdefense.MultiplayerObjects.serverHandshake;
import com.chrisnastovski.towerdefense.MultiplayerObjects.serverInfo;
import com.chrisnastovski.towerdefense.MultiplayerObjects.stringMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Created by Chris on 6/14/16.
 */
public class MultiplayerClient {

    // Constants
    private final int UDP_PORT = 54555;
    private final int TCP_PORT = 54777;

    Client client;
    Lobby lobby;

    Connection serverConn;

    public MultiplayerClient() {
        // Create client
        client = new Client();
        client.start();
    }

    // Find lobbies on the network
    public ArrayList<serverInfo> findLobbies() {

        // Keep track of servers that are found
        final ArrayList<serverInfo> servers = new ArrayList<serverInfo>();

        // Get list of ip's on the network that are open on the ports
        List<InetAddress> inets;
        try {
            inets = client.discoverHosts(UDP_PORT, 5000);
        } catch (NullPointerException e) {
            System.out.println("[CLIENT] No servers found!");
            return servers;
        }

        // ping every server for their information
        for(InetAddress addr : inets) {
            // Create temporary client
            Client pingClient = new Client();
            pingClient.start();
            try {
                System.out.println("[CLIENT] Pinging " + addr.getHostAddress() + "...");
                pingClient.connect(5000, addr.getHostAddress(),  UDP_PORT, TCP_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Register all classes
            Kryo kryo = pingClient.getKryo();
            MultiplayerObjects.registerAllClasses(kryo);

            // Add listener
            pingClient.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    // Server sending basic info. Not using method for security reasons
                    if (object instanceof serverInfo) {
                        serverInfo info = new serverInfo();
                        System.out.println("[CLIENT] Found server: " + info.name + "(" + info.clientNames.length + ")");
                        servers.add(info);
                    }
                }
            });

            // Send ping command
            stringMessage cmd = new stringMessage();
            cmd.message = "ping";
            pingClient.sendTCP(cmd);

        }
        return servers;
    }

    // Wrapper for joining localhost lobby
    public boolean joinOwnLobby(String name) {
        Lobby lobby = new Lobby();
        lobby.ip = "localhost";
        return joinLobby(lobby, name);
    }


    // Join any lobby
    public boolean joinLobby(Lobby lobby, String name) {

        // Store the lobby we are connecting to
        this.lobby = lobby;

        try {
            client.connect(5000, lobby.ip,  UDP_PORT, TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();

            // Return false if something went wrong
            return false;
        }

        // Register classes for transfer
        Kryo kryo = client.getKryo();
        MultiplayerObjects.registerAllClasses(kryo);

        // Add listener
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                clientHandleReceivedData(connection, object);
            }
        });

        // Send server client's info (response handled in listeners)
        clientHandshake info = new clientHandshake();
        info.name = name;
        client.sendTCP(info);

        return true;
    }

    // Client specific handlers
    private void clientHandleReceivedData(Connection connection, Object object) {

        if(object instanceof PathFollower) {
            MainMenu.followers.add((PathFollower) object);
        }
        if(object instanceof stringMessage) {
            stringMessage msg = (stringMessage) object;
            System.out.println("[CLIENT] Message received from "+msg.name+": " + msg.message);
        }

        // Server is sending us initial connection data
        if(object instanceof serverHandshake) {
            serverHandshake info = (serverHandshake) object;

            // If we were rejected, call the disconnect event
            if(!info.accepted) {
                onClientDisconnected();
                return;
            }

            serverConn = connection;
            // Let user know who's here
            System.out.println("[CLIENT] Server Handshake: " + info.connections + " Connections. Users: " + Arrays.toString(info.Names));
        }
    }

    // Whenever client is disconnected, this method is called
    public void onClientDisconnected() {
        try {
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(Object o) {

        System.out.println(client.isConnected());
        client.sendTCP(o);
    }

}
