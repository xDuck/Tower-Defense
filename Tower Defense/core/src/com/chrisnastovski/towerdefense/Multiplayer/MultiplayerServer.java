package com.chrisnastovski.towerdefense.Multiplayer;

import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerObjects.clientHandshake;
import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerObjects.clientInfo;
import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerObjects.serverHandshake;
import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerObjects.serverInfo;
import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerObjects.stringMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Chris on 6/14/16.
 */
public class MultiplayerServer {

    // Constants
    private final int UDP_PORT = 54555;
    private final int TCP_PORT = 54777;

    serverInfo serverInfo = new serverInfo();
    ArrayList<clientInfo> clients;
    String password;

    Server server;

    public MultiplayerServer() {
        clients = new ArrayList<clientInfo>();
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
        serverInfo.isFull = false;
        serverInfo.maxClients = 2;

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
        MultiplayerObjects.registerAllClasses(kryo);

        // Add listener to server
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                serverHandleReceivedData(connection, object);
            }
        });

        return true;
    }

    // Server specific handlers
    private void serverHandleReceivedData(Connection connection, Object object) {

        if(object instanceof com.chrisnastovski.towerdefense.Units.PathFollower) {
            broadcast(object);
        }

        if( object instanceof  stringMessage) {
            stringMessage msg = ((stringMessage) object);

            // Build and send server info
            if(msg.equals("ping")) {
                serverInfo info = new serverInfo();
                info.name = serverInfo.name;
                info.clientNames = getClientNames();
                info.hasPassword = serverInfo.hasPassword;
                connection.sendTCP(info);
            } else {
                broadcast(msg);
            }
        }

        // Client trying to connect
        if(object instanceof clientHandshake) {
            clientHandshake info = (clientHandshake) object;

            // Server Full
            if(serverInfo.clientNames.length + 1 > serverInfo.maxClients) {
                connection.close();
                return;
            }

            // save the connection
            clientInfo newClient = new clientInfo();
            newClient.name = info.name;
            newClient.connection = connection;
            clients.add(newClient);

            // construct server handshake
            serverHandshake handshake = new serverHandshake();
            handshake.accepted = true;
            handshake.connections = clients.size();
            handshake.Names = getClientNames();
            handshake.playerNumber = getNewPlayerNumber();
            connection.sendTCP(handshake);

            addName(info.name);

            // Broadcast message
            stringMessage newUser = new stringMessage();
            newUser.message = "New user connected! [" + info.name + "]";
            newUser.name = "SERVER";

            broadcast(newUser);

            System.out.println("[SERVER] Client Handshake: " + info.name + " ["+clients.size() + " clients connected]");
        }
    }

    public void broadcast(Object o) {
        for(clientInfo c : clients) {
            c.connection.sendTCP(o);
        }
    }

    public void addName(String n) {
        String[] temp = serverInfo.clientNames;
        serverInfo.clientNames = new String[temp.length+1];

        for(int i = 0; i < temp.length; i++)
            serverInfo.clientNames[i] = temp[i];

        serverInfo.clientNames[temp.length] = n;
    }

    public String[] getClientNames() {
        String[] names = new String[clients.size()];
        for(int i = 0; i < names.length; i++) {
            names[i] = clients.get(i).name;
        }

        return names;
    }

    public int getNewPlayerNumber() {
        return serverInfo.clientNames.length;
    }

}
