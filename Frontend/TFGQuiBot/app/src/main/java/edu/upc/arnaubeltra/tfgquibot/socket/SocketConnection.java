package edu.upc.arnaubeltra.tfgquibot.socket;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class SocketConnection {
    private static SocketConnection instance;

    public Socket socketConnectionServer = null;

    private String SERVER_IP = "192.168.101.1";
    private int SERVER_PORT = 5000;

    public static SocketConnection getInstance() {
        if (instance == null) instance = new SocketConnection();
        return instance;
    }

    public void onStartConnection() {
        Thread threadConnectToServer = new Thread(new ConnectToServer());
        threadConnectToServer.start();
    }

    class ConnectToServer implements Runnable {
        @Override
        public void run() {
            try {
                socketConnectionServer = new Socket(SERVER_IP, SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

