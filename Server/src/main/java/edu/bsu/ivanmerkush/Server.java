package edu.bsu.ivanmerkush;

import edu.bsu.ivanmerkush.security.SecurityService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(1099);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new SingleServerSocket(socket).start();
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }



}


