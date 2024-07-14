package Intern_Pakage;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, PrintWriter> userWriters = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ServerSocket listener = new ServerSocket(59018);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request and store the username
                out.println("SUBMITNAME");
                username = in.readLine();
                synchronized (userWriters) {
                    userWriters.put(username, out);
                }

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Notify all clients about the new user
                broadcast(username + " has joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                synchronized (userWriters) {
                    userWriters.remove(username);
                }
                broadcast(username + " has left the chat!");
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
