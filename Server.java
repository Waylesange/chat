/**
 * Copyright (c) July 12, 2016. All rights reserved.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 */
class Server {
    private ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
//    private boolean isRunning = false;

    private Server() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            SQLHandler.setConnection();
//            isRunning = true;
//            new Thread(() -> {
//                System.err.println(Thread.currentThread().getName());
//                Scanner scanner = new Scanner(System.in);
//                while (true) {
//                    if (scanner.next().equals("end")) {
//                        isRunning = false;
//                        break;
//                    }
//                }
//            }).start();
            while (true) {
//                if (!isRunning) break;
                System.out.println("Ожидание клиентов...");
                Socket socket = serverSocket.accept();
//                if (!isRunning) break;
                System.out.println("Сокет открыт");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                this.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            SQLHandler.stopConnection();
            System.out.println("Сервер и соединение с БД закрыты");
            System.exit(0);
        } finally {
            SQLHandler.stopConnection();
            System.out.println("Сервер и соединение с БД закрыты"); // не достигается при аварийном выходе!
        }
    }

    void broadcastMessage(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        message = time + " " + message;
        for (ClientHandler clientHandler: clientHandlerArrayList) {
            clientHandler.sendMessage(message);
        }
    }

    private void add(ClientHandler clientHandler) {
        if (!clientHandlerArrayList.contains(clientHandler)) {
            clientHandlerArrayList.add(clientHandler);
        }
    }

    void remove(ClientHandler clientHandler) {
        if (clientHandlerArrayList.contains(clientHandler) && !clientHandler.getName().isEmpty()) {
            broadcastMessage("[" + clientHandler.getName() + "] вышел из комнаты чата");
            System.out.println("[" + clientHandler.getName() + "] вышел из комнаты чата");
            clientHandlerArrayList.remove(clientHandler);
        } else {
            System.out.println("Неавторизованный клиент вышел из комнаты чата");
            clientHandlerArrayList.remove(clientHandler);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
