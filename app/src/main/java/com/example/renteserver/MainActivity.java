package com.example.renteserver;


import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int port = 6969;
    TextView output;
    Button button;
    private ServerSocket serverSocket;
    Socket socket;
    boolean connected = false;
    BufferedReader inputStream = null;
    PrintWriter outputStream =null;

    ArrayList<String> dataArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.output);
        button = findViewById(R.id.button);

        output.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                output.append("\n" + e.getMessage());
            }
        }
        button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.purple_200));
    }


    Runnable connect = () -> {

        if (serverSocket != null) {
            try {
                serverSocket.close();
                connected = false;
            } catch (IOException e) {
                output.append("\n" + e.getMessage());
            }
        }

        try {

            String inputString = null;

            serverSocket = new ServerSocket(port);

            output.append("\n" + "Accepting connection on port " + port + ".");



            while (true) {

                socket = serverSocket.accept();
                outputStream = new PrintWriter(socket.getOutputStream());
                inputStream = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                if(!connected){
                    output.append("\n" + "Connection established from: " + socket.getRemoteSocketAddress().toString());
                    output.append("\n");
                    connected = true;
                }

                while ((inputString = inputStream.readLine()) != null) {
                    output.append("\n" + inputString);
                    String[] arrOfStr = inputString.split(":");
                    dataArray.add(arrOfStr[1]);
                }

                socket.shutdownInput();

                String calculatedLoan = String.valueOf(Double.parseDouble(dataArray.get(2))*(Math.pow((1.00+Double.parseDouble(dataArray.get(0))),Double.parseDouble(dataArray.get(1)))));
                dataArray.clear();

                output.append("\nLoan price: " + calculatedLoan + "kr.");
                outputStream.println("The price of the loan is: " + calculatedLoan + "kr.");
                outputStream.flush();
                socket.shutdownOutput();

            }




        } catch (Exception e) {
            output.append("\n" + e.getMessage());
            button.setClickable(true);
            button.setBackgroundColor(getResources().getColor(R.color.purple_200));
        }

    };

    public void startServer(View view) throws InterruptedException {

        button.setClickable(false);
        button.setBackgroundColor(Color.GRAY);


        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        output.append("\ndevice IP is: " + ipAddress);

        new Thread(connect).start();

    }




}