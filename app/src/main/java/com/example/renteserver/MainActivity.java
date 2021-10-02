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
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static int port = 6969;
    TextView output;
    Button button;
    private ServerSocket serverSocket;
    Socket socket;
    boolean connected = false;
    int counter = 0;

    String annualIntrestRate;
    String numberOfYears;
    String loanAmount;
    String calculatedLoan;

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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }

        try {

            String inputString = null;

            serverSocket = new ServerSocket(port);

            output.append("\n" + "Accepting connection on port " + port + ".");



            while (!Thread.currentThread().isInterrupted()) {

                socket = serverSocket.accept();

                if(!connected){
                    output.append("\n" + "Connection established from: " + socket.getRemoteSocketAddress().toString());
                    output.append("\n");
                    connected = true;
                }

                BufferedReader inputStream = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                inputString = inputStream.readLine();

//                output.append("\nTekst modtaget: " + inputString);

                inputStream.close();

                counter+=1;

                setData(inputString);

            }


        } catch (Exception e) {
            output.append("\n" + e.getMessage());
            button.setClickable(true);
            button.setBackgroundColor(getResources().getColor(R.color.purple_200));
        }

    };

    public void startServer(View view) {

        button.setClickable(false);
        button.setBackgroundColor(Color.GRAY);


        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        output.append("\ndevice IP is: " + ipAddress);

        new Thread(connect).start();

    }


    void setData(String inputString) {
        switch(counter) {
            case 1:
                annualIntrestRate = inputString;
                output.append("\n" + "Annual interest rate: " + annualIntrestRate);
            break;

            case 2:
                numberOfYears = inputString;
                output.append("\n" + "Number of years: " + numberOfYears);
            break;

            case 3:
                loanAmount = inputString;
                output.append("\n" + "Loan amount: " + loanAmount);


                calculatedLoan = String.valueOf(Double.parseDouble(loanAmount)*(Math.pow((1.00+Double.parseDouble(annualIntrestRate)),Double.parseDouble(numberOfYears))));
                output.append("\n" + "calculation: " + calculatedLoan);
            break;

        }
    }




}