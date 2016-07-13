/**
 * Copyright (c) July 12, 2016. All rights reserved.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 */
class Client extends JFrame {
    private JTextArea jTextArea = null;
    private JTextField jTextField = null;
    private Socket socket = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private Thread thread = null;

    private Client() {
        // Создать окно
        setTitle("Чат");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Создать меню
        JMenuBar jMenuBar = new JMenuBar();
        setJMenuBar(jMenuBar);

        JMenu jMenuFile = new JMenu("Файл");
        jMenuBar.add(jMenuFile);

        JMenuItem jMenuItemSettings = new JMenuItem("Настройки...");
        jMenuFile.add(jMenuItemSettings);
        jMenuFile.addSeparator();
        JMenuItem jMenuItemExit = new JMenuItem("Выход");
        jMenuItemExit.addActionListener(e -> {
            try {
                if (socket != null) {
                    dataOutputStream.writeUTF("end");
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    dataInputStream.close();
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        });
        jMenuFile.add(jMenuItemExit);

        // Создать многострочное текстовое поле
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);

        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        add(jScrollPane, BorderLayout.CENTER);

        // Создать панель ввода и отправки сообщений
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        jTextField = new JTextField();
        jTextField.addActionListener(e -> {
            if (!jTextField.getText().trim().isEmpty()) {
                sendMessage();
            } else {
                jTextField.setText("");
            }
        });

        JButton jbSend = new JButton("Отправить");
        jbSend.addActionListener(e -> {
            if (!jTextField.getText().trim().isEmpty()) {
                sendMessage();
            } else {
                jTextField.setText("");
            }
            jTextField.grabFocus();
        });

        textFieldPanel.add(jTextField, BorderLayout.CENTER);
        textFieldPanel.add(jbSend, BorderLayout.EAST);
        add(textFieldPanel, BorderLayout.SOUTH);

        // Создать панель авторизации
        JPanel authorizationPanel = new JPanel(new GridLayout());
        JTextField jtfLogin = new JTextField();

        JTextField jtfPassword = new JTextField();
        jtfLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jtfPassword.grabFocus();
                }
            }
        });
        jtfPassword.addActionListener(e -> {
            connect("authorize " + jtfLogin.getText() + " " + jtfPassword.getText());
            jTextField.grabFocus();
        });

        JButton jbAuthorize = new JButton("Авторизовать");
        jbAuthorize.addActionListener(e -> {
            connect("authorize " + jtfLogin.getText() + " " + jtfPassword.getText());
            jTextField.grabFocus();
        });
        jbAuthorize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connect("authorize " + jtfLogin.getText() + " " + jtfPassword.getText());
                    jTextField.grabFocus();
                }
            }
        });

        authorizationPanel.add(jtfLogin);
        authorizationPanel.add(jtfPassword);
        authorizationPanel.add(jbAuthorize);
        add(authorizationPanel, BorderLayout.NORTH);

        // Обработать события, связанные с окном
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                jtfLogin.grabFocus();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    if (socket != null) {
                        dataOutputStream.writeUTF("end");
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        dataInputStream.close();
                        socket.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Отобразить окно
        setVisible(true);
    }

    /**
     * Создаёт сокет для передачи данных авторизации
     * @param string строка авторизации
     */
    private void connect(String string) {
        if (thread == null) {
            try {
                final String SERVER_ADDRESS = "localhost"; // мой IP: 77.123.215.44
                final int SERVER_PORT = 8189;              // IP преподавателя: 83.221.205.67
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(string);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            thread = new Thread(() -> {
                try {
                    while (true) {
                        String message = dataInputStream.readUTF();
                        if (message.equalsIgnoreCase("end")) break;
//                        jTextArea.append(Thread.currentThread().getName() + '\n');
                        jTextArea.append(message + '\n');
                        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    thread = null;
                }
            });
//            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Отправляет сообщение из однострочного текстового поля в выходной поток сокета
     * и делает однострочное текстовое поле пустым
     */
    private void sendMessage() {
        try {
            if (thread != null) {
                dataOutputStream.writeUTF(jTextField.getText());
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        jTextField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
