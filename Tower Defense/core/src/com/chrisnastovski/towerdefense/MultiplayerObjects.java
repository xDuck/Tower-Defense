package com.chrisnastovski.towerdefense;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

/**
 * Created by Chris on 6/14/16.
 */
public class MultiplayerObjects {

    // Register classes to Kryonet
    public static void registerAllClasses(Kryo k) {
        k.register(stringMessage.class);
        k.register(clientHandshake.class);
        k.register(serverHandshake.class);
        k.register(serverInfo.class);
        k.register(Lobby.class);
    }

    // Basic class for sending data in string form
    public static class stringMessage {
        public String message;
    }

    // Sends client info to server on first connect
    public static class clientHandshake {
        public String name;
    }

    public static class clientInfo {
        public String name;
        public Connection connection;
    }

    // Sent by server to inform client of connection status
    public static class serverHandshake {
        public boolean accepted;
        public int connections;
        public String[] Names;
    }

    // Sent by server to allow client to discover
    public static class serverInfo {
        public String name;
        public String[] clientNames;
        public boolean hasPassword;
    }

    // Holds information about the lobby
    public static class Lobby {
        public String ip;
    }
}
