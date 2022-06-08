package com.example.crch2;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SocketThread extends Thread{
    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private int pingTimer ;
    private ConnectionManager connectionManager;
    private String name;

    public  SocketThread(RecyclerView recyclerView, ArrayList<com.example.crch2.Message> messages, ConnectionManager connectionManager, String name){
        this.messages = messages;
        this.recyclerView = recyclerView;
        this.pingTimer = 0;
        this.connectionManager = connectionManager;
        this.name = name;
    }

    @Override
    public void run() {
        startPingTester();
        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
               // byte[] ipAddr = new byte[] { (byte)5, (byte) 145,(byte) 195, (byte)121};
                byte[] ipAddr = new byte[] { (byte)5, (byte) 180,(byte) 136, (byte)188};
                clientSocket = new Socket(InetAddress.getByAddress(ipAddr), 4004); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // писать туда же
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                connectionManager.setOnlineStatus(true);
                // если соединение произошло и потоки успешно созданы - мы можем
                //  работать дальше и предложить клиенту что то ввести
                // если нет - вылетит исключение


                getLastMessages();


                            while (!clientSocket.isClosed()) {
                                String serverWord = null; // ждём, что скажет сервер
                                try {
                                    serverWord = in.readLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(serverWord);
                                if(serverWord!=null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(serverWord);
                                        switch (jsonObject.getString("requestType")) {
                                            case ("sendMessage"):
                                                messages.add(new Message(jsonObject.getString("userName"), jsonObject.getString("message")));

                                                break;
                                            case ("lastMessages"):
                                                updateLastMessages(jsonObject);
                                                break;
                                            case("ping"):pingTimer=0;
                                            break;
                                            case("sendOnline"):
                                                messages.add(new Message(jsonObject.getString("userName"), "В сети",1));

                                                break;
                                            case("sendOffline"):
                                                messages.add(new Message(jsonObject.getString("userName"), "Не в сети",1));
                                                break;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    reconnect();
                                    break;

                                }
                                Intent intent = new Intent("update");
                                // You can also include some extra data.
                                intent.putExtra("message", "need to update");
                                LocalBroadcastManager.getInstance(recyclerView.getContext()).sendBroadcast(intent);

                            }





            } catch (IOException e) {
                e.printStackTrace();
                delayedReconnect();
            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                connectionManager.setOnlineStatus(false);
                if(clientSocket!=null && !clientSocket.isClosed()){
                    clientSocket.close();

                    in.close();
                    out.close();
                }

            }
        } catch (IOException e) {
            System.err.println(e);

        }


    }

    private void delayedReconnect() {
        Runnable task = new Runnable() {
            public void run() {


                    try {

                        Thread.sleep(3000);
                        reconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void reconnect() {
        connectionManager.setOnlineStatus(false);
        Intent intent = new Intent("reconnect");
        // You can also include some extra data.
        intent.putExtra("message", "need to reconnect");
        LocalBroadcastManager.getInstance(recyclerView.getContext()).sendBroadcast(intent);
        System.out.println("Переподключение");
        close();
    }

    private void startPingTester() {
        Runnable task = new Runnable() {
            public void run() {
                while(true){
                //System.out.println("Жду пингов");

                    try {
                        pingTimer++;
                        if(pingTimer>30){
                           reconnect();
                            break;
                        }else{
                            if(pingTimer<0){
                                break;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    private void updateLastMessages(JSONObject jsonObject) {
        messages.clear();
        int count = 0;
        try {
           count =  jsonObject.getInt("lastMessagesCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i<count;i++){
            try {
                String name = jsonObject.getString("name"+Integer.toString(i));
                String message = jsonObject.getString("message"+Integer.toString(i));
                messages.add(new Message(name,message));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent("update");
        // You can also include some extra data.
        intent.putExtra("message", "need to update");
        LocalBroadcastManager.getInstance(recyclerView.getContext()).sendBroadcast(intent);
    }


    public void send(String message) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("requestType","sendMessage");
                        jsonObject.put("userName",name);
                        jsonObject.put("message",message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.write(jsonObject+"\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();

    }

    public void getLastMessages() {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("requestType","getLastMessages");
                        jsonObject.put("count",100);
                        jsonObject.put("name",name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.write(jsonObject+"\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();

    }

    public void close(){
        pingTimer=-10;
        try {
           if(clientSocket!=null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
