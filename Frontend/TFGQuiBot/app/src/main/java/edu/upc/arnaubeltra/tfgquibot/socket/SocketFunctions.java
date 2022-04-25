package edu.upc.arnaubeltra.tfgquibot.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketFunctions {

    private static SocketFunctions instance;

    BufferedReader input;
    String isAuthorized;

    private SocketConnection socketConnection = SocketConnection.getInstance();

    public static SocketFunctions getInstance() {
        if (instance == null) instance = new SocketFunctions();
        return instance;
    }



    public boolean checkPermissions() {
        Socket socket = socketConnection.socketConnectionServer;
        new Thread(() -> {
            try {
                PrintWriter newUserLogin = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                newUserLogin.println("check_permissions");

                while (isAuthorized == null) {
                    BufferedReader permissionInfo = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    isAuthorized = permissionInfo.readLine();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Log.d("TAG", "checkPermissions: " + isAuthorized);
        try {
            return isAuthorized.equals("true");
        } catch (Exception e) {
            return false;
        }
    }
}
