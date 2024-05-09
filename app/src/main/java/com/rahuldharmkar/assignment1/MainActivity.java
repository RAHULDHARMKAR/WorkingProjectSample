package com.rahuldharmkar.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private int value = 100; // Initial value

    // ClientTask instance for sending data to the server
    private ClientTask clientTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Initialize and execute ClientTask
        clientTask = new ClientTask();
        clientTask.execute();
        // Set onClickListener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update value and update TextView
                value--;
                updateTextView();
                // Send updated value to server via ClientTask in background
                clientTask.sendDataInBackground(String.valueOf(value));
            }
        });
    }

    // Method to update the TextView
    private void updateTextView() {
        textView.setText(String.valueOf(value));
    }

    // AsyncTask to handle server communication
    private class ClientTask extends AsyncTask<Void, Void, Void> {

        private Socket socket;
        private PrintWriter printWriter;

        // Server IP address and port
        private static final String SERVER_IP = "192.168.1.7"; // Replace with your server's IP
        private static final int SERVER_PORT = 8081; // Replace with your server's port

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Connect to the server
                socket = new Socket(SERVER_IP, SERVER_PORT);
                printWriter = new PrintWriter(socket.getOutputStream(), true);

                // Start listening for messages from the server
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    // Extract numerical value from the message and update TextView
                    int newValue = extractNumericValue(message);
                    if (newValue != Integer.MIN_VALUE) {
                        final int finalValue = newValue;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView(finalValue);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Method to update the TextView
        private void updateTextView(int newValue) {
            textView.setText(String.valueOf(newValue));
        }

        // Method to send data to the server
        public void sendData(final String data) {
            // Send data to the server
            if (printWriter != null) {
                printWriter.println(data);
            }
        }

        // Method to send data to the server in the background
        public void sendDataInBackground(final String data) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(data);
                }
            }).start();
        }

        // Method to extract numerical value from the message
        private int extractNumericValue(String message) {
            int value = Integer.MIN_VALUE;
            try {
                String[] parts = message.split(":");
                if (parts.length >= 2) {
                    value = Integer.parseInt(parts[1].trim());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return value;
        }
    }


}