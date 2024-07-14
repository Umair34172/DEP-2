package Intern_Pakage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Chat Client");
    private JTextField textField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient(String serverAddress) throws Exception {
        Socket socket = new Socket(serverAddress, 59018);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

        new Thread(new IncomingReader()).start();
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line.startsWith("SUBMITNAME")) {
                        String username = JOptionPane.showInputDialog(
                                frame,
                                "Enter a username:",
                                "Username",
                                JOptionPane.PLAIN_MESSAGE);
                        out.println(username);
                    } else {
                        messageArea.append(line + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient("127.0.0.1");
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.textField.setEditable(true);
    }
}
