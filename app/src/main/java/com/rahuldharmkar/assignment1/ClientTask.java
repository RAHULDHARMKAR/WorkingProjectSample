package com.rahuldharmkar.assignment1;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ClientTask";
    private static final String SERVER_IP = "192.168.1.7"; // Replace with your server's IP address
    private static final int SERVER_PORT = 8081; // Server port

    private String message;

    public ClientTask(String message) {
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            Log.d(TAG, "Connected to server");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send data to server
            out.println(message);

            // Close connection
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

