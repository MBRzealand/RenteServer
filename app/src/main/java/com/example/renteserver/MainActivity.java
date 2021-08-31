package com.example.renteserver;


import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
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

    static int port = 7890;
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
    }


    Runnable connect = () -> {

        try {

            String inputString = null;

            serverSocket = new ServerSocket(port);

            output.append("\nAccepting connection on port " + port + ".");



            while (true) {

                socket = serverSocket.accept();

                if(!connected){
                    output.append("\nConnection established from: " + socket.getRemoteSocketAddress().toString());
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
            output.append(e.getMessage());
        }
    };

    public void startServer(View view) {

//        button.setVisibility(View.GONE);


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
                output.append("\nAnnual interest rate: " + annualIntrestRate);
            break;

            case 2:
                numberOfYears = inputString;
                output.append("\nNumber of years: " + numberOfYears);
            break;

            case 3:
                loanAmount = inputString;
                output.append("\nLoan amount: " + loanAmount);


                calculatedLoan = String.valueOf(Double.parseDouble(loanAmount)*(Math.pow((1.00+Double.parseDouble(annualIntrestRate)),Double.parseDouble(numberOfYears))));
                output.append("\ncalculation: " + calculatedLoan);
            break;

        }
    }




}