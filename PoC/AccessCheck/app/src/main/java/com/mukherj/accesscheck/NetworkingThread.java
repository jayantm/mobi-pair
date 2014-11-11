package com.mukherj.accesscheck;

/**
 * Created by mukherj on 11/11/2014.
 */
import java.io.*;
import java.net.*;

import android.util.Log;

public class NetworkingThread extends Thread{
    //PrintStream printStream;
    PrintWriter printStream;
    static String packagename="";
    static int mstr;
    static String message="";
    static boolean clear=false;
    static boolean clearNotifyBool=false;
    public static void getStrings(String appPackage, String appMessage){
        packagename=appPackage;
        message=appMessage;
        mstr=message.toString().indexOf("Missed");
        Log.d("Reciever",packagename);
        Log.d("Reciever","Message "+mstr);
        Log.d("Reciever","Set");
    }
    public static void clear(){
        clear=true;
        Log.d("Reciever","clear true");
    }
    public static void clearNotify(){
        clearNotifyBool=true;
        Log.d("reciever","clear notify true");
    }
    public void run()
    {
        try {
            while (true){
                String host = "10.0.1.55";
                int port = 50030;

                if (packagename!="" || clear || clearNotifyBool){
                    Log.d("Reciever","Packagename from run: "+packagename);
                    Log.d("Reciever","in loop");
                    byte[] message = packagename.getBytes();
                    byte[] clearStr="clear".getBytes();
                    byte[] clearNotifyStr="clear notify".getBytes();
                    byte[] callStr="Missed Call".getBytes();

                    // Get the internet address of the specified host
                    InetAddress address = InetAddress.getByName(host);

                    // Initialize a datagram packet with data and address
                    if (clear){
                        DatagramPacket packet = new DatagramPacket(clearStr, clearStr.length,
                                address, port);
                        DatagramSocket dsocket = new DatagramSocket();
                        dsocket.send(packet);
                        dsocket.close();
                        clear=false;
                        Log.d("Reciever","clear false");
                    }
                    else if (clearNotifyBool){
                        DatagramPacket packet = new DatagramPacket(clearNotifyStr, clearNotifyStr.length, address, port);
                        DatagramSocket dsocket = new DatagramSocket();
                        dsocket.send(packet);
                        dsocket.close();
                        clearNotifyBool=false;
                        Log.d("Reciever","clear false");
                    }
                    else if (packagename.equals("com.android.phone")&&mstr==1){
                        Log.d("Reciever","Missed call if");
                        DatagramPacket packet = new DatagramPacket(callStr, callStr.length, address, port);
                        DatagramSocket dsocket = new DatagramSocket();
                        dsocket.send(packet);
                        dsocket.close();
                    }
                    else{
                        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);

                        // Create a datagram socket, send the packet through it, close it.
                        DatagramSocket dsocket = new DatagramSocket();
                        dsocket.send(packet);
                        dsocket.close();
                    }
                    packagename="";
                    Log.d("Reciever","Done");
                }
                try{
                    Thread.sleep(1000);
                }
                catch (Exception e){
                    System.out.println(e);
                }
            } }catch (Exception e) {
            Log.d("Reciever", e.toString());
        }
    }
};