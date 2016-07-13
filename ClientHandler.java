/**
 * Copyright (c) July 12, 2016. All rights reserved.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 */
class ClientHandler implements Runnable {
    private Socket socket = null;
    private Server myServer = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private String name = null;

    ClientHandler(Socket socket, Server myServer) {
        this.socket = socket;
        this.myServer = myServer;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        name = "";
    }

    String getName() {
        return name;
    }

    @Override
    public void run() {
        try {
//            System.err.println("Выполняется поток " + Thread.currentThread().getName());
            while (true) {
                String string = dataInputStream.readUTF();
                // начало блока авторизации
                if (name.isEmpty()) {
                    String[] strings = string.split("\\s+");
                    if (strings.length == 3 && strings[0].equals("authorize")) {
                        String nickname = SQLHandler.getNicknameByLoginAndPassword(strings[1], strings[2]);
                        if (nickname != null) {
                            name = nickname;
                            System.out.println("Клиент [" + name + "] подключен");
                            myServer.broadcastMessage("[" + name + "] вошёл в комнату чата");
                            System.out.println("[" + name + "] вошёл в комнату чата");
                        } else {
                            myServer.remove(this);
                            System.out.println("Неавторизированный клиент отключен");
                            break;
                        }
                    } else break;
                    continue;
                }
                // конец блока авторизации
                if (string.equalsIgnoreCase("end")) break;
                myServer.broadcastMessage("[" + name + "] " + string);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!name.isEmpty()) {
                myServer.remove(this);
                System.out.println("Клиент [" + name + "] отключен");
            }
            try {
                if (socket != null) {
                    socket.close();
                    System.out.println("Сокет закрыт");
//                    System.err.println("Завершается поток " + Thread.currentThread().getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendMessage(String message) {
        try {
            if (socket != null) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
