package com.chrisnastovski.towerdefense;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

/**
 * Created by Chris on 6/14/16.
 */
public class MultiplayerObjects {

    // Register classes to Kryonet
    public static void registerAllClasses(Kryo k) {
        // Java classes
        k.register(String[].class);

        // Custom classes
        k.register(stringMessage.class);
        k.register(clientHandshake.class);
        k.register(serverHandshake.class);
        k.register(serverInfo.class);
        k.register(Lobby.class);
    }

    public static class parentObject {
        public String name;
    }

    // Basic class for sending data in string form
    public static class stringMessage extends parentObject{
        public String message;
    }

    // Sends client info to server on first connect
    public static class clientHandshake extends parentObject {

    }

    public static class clientInfo extends parentObject {
        public Connection connection;
    }

    // Sent by server to inform client of connection status
    public static class serverHandshake extends parentObject {
        public boolean accepted;
        public int connections;
        public String[] Names;
    }

    // Sent by server to allow client to discover
    public static class serverInfo extends parentObject {
        public String[] clientNames;
        public boolean hasPassword;
    }

    // Holds information about the lobby
    public static class Lobby extends parentObject {
        public String ip;
    }
}
