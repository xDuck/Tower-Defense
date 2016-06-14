package com.chrisnastovski.towerdefense;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chris Nastovski on 6/13/16.
 */
public class MultiplayerHandler {

    // Constants
    private final int UDP_PORT = 54555;
    private final int TCP_PORT = 54777;

    Client client;
    Server server;

    // Server variables
    ArrayList<MultiplayerUtils.clientInfo> clients;
    MultiplayerUtils.serverInfo serverInfo = new MultiplayerUtils.serverInfo();
    String password;

    MultiplayerUtils.Lobby lobby;

    public MultiplayerHandler(){
        clients = new ArrayList<MultiplayerUtils.clientInfo>();
    }

    // Wrapper for starting a non-passworded lobby
    public boolean startLobby(String name) {
        return startLobby(name, null);
    }

    // Starting a lobby
    public boolean startLobby(String name, String password) {

        // Set server vars
        serverInfo.name = name;
        serverInfo.clientNames = new String[0];
        if (password != null) {
            serverInfo.hasPassword = false;
            this.password = null;
        } else {
            serverInfo.hasPassword = true;
            this.password = password;
        }

        // Create server
        server = new Server();
        server.start();
        try {
            server.bind(UDP_PORT, TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();

            // return false if something went wrong
            return false;
        }

        // Register all classes
        Kryo kryo = server.getKryo();
        MultiplayerUtils.registerAllClasses(kryo);

        // Add listener to server
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                serverHandleReceivedData(connection, object);
            }
        });

        return true;
    }


    // Find lobbies on the network
    public ArrayList<MultiplayerUtils.serverInfo> findLobbies() {

        // Keep track of servers that are found
        final ArrayList<MultiplayerUtils.serverInfo> servers = new ArrayList<MultiplayerUtils.serverInfo>();

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
            MultiplayerUtils.registerAllClasses(kryo);

            // Add listener
            pingClient.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    // Server sending basic info. Not using method for security reasons
                    if (object instanceof MultiplayerUtils.serverInfo) {
                        MultiplayerUtils.serverInfo info = new MultiplayerUtils.serverInfo();
                        System.out.println("[CLIENT] Found server: " + info.name + "(" + info.clientNames.length + ")");
                        servers.add(info);
                    }
                }
            });

            // Send ping command
            MultiplayerUtils.stringMessage cmd = new MultiplayerUtils.stringMessage();
            cmd.message = "ping";
            pingClient.sendTCP(cmd);

        }
        return servers;
    }

    // Wrapper for joining localhost lobby
    public boolean joinOwnLobby(String name) {
        MultiplayerUtils.Lobby lobby = new MultiplayerUtils.Lobby();
        lobby.ip = "localhost";
        return joinLobby(lobby, name);
    }


    // Join any lobby
    public boolean joinLobby(MultiplayerUtils.Lobby lobby, String name) {

        // Store the lobby we are connecting to
        this.lobby = lobby;

        // Create client
        client = new Client();
        client.start();
        try {
            client.connect(5000, lobby.ip,  UDP_PORT, TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();

            // Return false if something went wrong
            return false;
        }

        // Register classes for transfer
        Kryo kryo = client.getKryo();
        MultiplayerUtils.registerAllClasses(kryo);

        // Add listener
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                clientHandleReceivedData(connection, object);
            }
        });

        // Send server client's info (response handled in listeners)
        MultiplayerUtils.clientHandshake info = new MultiplayerUtils.clientHandshake();
        info.name = name;
        client.sendTCP(info);
        return true;
    }

    // Non specific handlers
    private void generalHandleReceivedData(Connection connection, Object object) {
        if(object instanceof MultiplayerUtils.stringMessage) {
            MultiplayerUtils.stringMessage msg = (MultiplayerUtils.stringMessage) object;
            String name = findByConnection(connection).name;
            System.out.println("[CLIENT : "+name+"] Message received: " + msg.message);
        }

    }

    // Client specific handlers
    private void clientHandleReceivedData(Connection connection, Object object) {
        generalHandleReceivedData(connection, object);

        // Server is sending us initial connection data
        if(object instanceof MultiplayerUtils.serverHandshake) {
            MultiplayerUtils.serverHandshake info = (MultiplayerUtils.serverHandshake) object;

            // If we were rejected, call the disconnect event
            if(!info.accepted) {
                onClientDisconnected();
                return;
            }

            // Let user know who's here
            System.out.println("[CLIENT] Server information: " + info.connections + " Connections. Users: " + Arrays.toString(info.Names));
        }
    }

    // Server specific handlers
    private void serverHandleReceivedData(Connection connection, Object object) {
        generalHandleReceivedData(connection, object);

        if( object instanceof  MultiplayerUtils.stringMessage) {
            String msg = ((MultiplayerUtils.stringMessage) object).message;

            // Build and send server info
            if(msg.equals("ping")) {
                MultiplayerUtils.serverInfo info = new MultiplayerUtils.serverInfo();
                info.name = serverInfo.name;
                info.clientNames = getClientNames();
                info.hasPassword = serverInfo.hasPassword;
            }
        }

        // Client trying to connect
        if(object instanceof MultiplayerUtils.clientHandshake) {
            MultiplayerUtils.clientHandshake info = (MultiplayerUtils.clientHandshake) object;
            // save the connection
            MultiplayerUtils.clientInfo newClient = new MultiplayerUtils.clientInfo();
            newClient.name = info.name;
            newClient.connection = connection;
            clients.add(newClient);

            // Broadcast message
            // TODO: create method for this
            MultiplayerUtils.stringMessage newUser = new MultiplayerUtils.stringMessage();
            newUser.message = "New user connected! [" + info.name + "]";

            broadcast(newUser);

            System.out.println("[SERVER] User connected: " + info.name);
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
        client.sendTCP(o);
    }

    public MultiplayerUtils.clientInfo findByConnection(Connection c) {
        for(MultiplayerUtils.clientInfo i : clients)
            if (i.connection == c)
                return i;
        return null;
    }

    public void broadcast(Object o) {
        for(MultiplayerUtils.clientInfo c : clients) {
            c.connection.sendTCP(o);
        }
    }

    public String[] getClientNames() {
        String[] names = new String[clients.size()];
        for(int i = 0; i < names.length; i++) {
            names[i] = clients.get(i).name;
        }

        return names;
    }
}
