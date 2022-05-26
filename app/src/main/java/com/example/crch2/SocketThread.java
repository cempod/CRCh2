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

    public  SocketThread(RecyclerView recyclerView, ArrayList<com.example.crch2.Message> messages){
        this.messages = messages;
        this.recyclerView = recyclerView;
    }

    @Override
    public void run() {

        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                byte[] ipAddr = new byte[] { (byte)5, (byte) 145,(byte) 252, (byte)186 };
                clientSocket = new Socket(InetAddress.getByAddress(ipAddr), 4004); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // писать туда же
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                // если соединение произошло и потоки успешно созданы - мы можем
                //  работать дальше и предложить клиенту что то ввести
                // если нет - вылетит исключение


                            while (!clientSocket.isClosed()) {
                                String serverWord = null; // ждём, что скажет сервер
                                try {
                                    serverWord = in.readLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(serverWord);
                                try {
                                    JSONObject jsonObject = new JSONObject(serverWord);
                                    messages.add(new Message(jsonObject.getString("userName"),jsonObject.getString("message")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent("update");
                                // You can also include some extra data.
                                intent.putExtra("message", "need to update");
                                LocalBroadcastManager.getInstance(recyclerView.getContext()).sendBroadcast(intent);

                            }





            } catch (IOException e) {
                e.printStackTrace();
            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }


    }


    public void send(String name,String message) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
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
}
