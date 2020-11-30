package edu.bsu.ivanmerkush;

import edu.bsu.ivanmerkush.security.SecurityService;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SingleServerSocket extends Thread{
    private final SecurityService securityService = new SecurityService();
    private Socket clientSocket;
    private BufferedOutputStream bufferedOutputStream;
    private BufferedInputStream bufferedInputStream;
    private BufferedReader in;
    private BufferedWriter out;


    public SingleServerSocket(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
        bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
    }




    public void closeConnection() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                switch (in.readLine()) {
                    case "file":
                        try {
                            String fileName = in.readLine();
                            byte[] bytes = Files.readAllBytes(Paths.get("Server\\src\\main\\resources\\".concat(fileName).concat(".txt")));
                            if (securityService.isKeyGenerated()) {
                                bytes = securityService.encodeText(bytes);
                            }
                            out.write(Base64.getEncoder().encodeToString(bytes).concat("\n"));
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "key":
                        String strKey = in.readLine();
                        securityService.setPublicKey(strKey);
                        break;
                    case "session":
                        byte[] encodedKey = securityService.generateSessionKey();
                        out.write(Base64.getEncoder().encodeToString(encodedKey).concat("\n"));
                        out.flush();
                        break;
                    case "edit":
                        String fileName = in.readLine();

                        byte[] decodedText = Base64.getDecoder().decode(in.readLine());
                        if(securityService.isKeyGenerated()) {
                            decodedText = securityService.decodeText(decodedText);
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream("Server\\src\\main\\resources\\".concat(fileName).concat(".txt"));
                        fileOutputStream.write(decodedText);
                        fileOutputStream.close();
                        break;
                    case "delete":
                        fileName = in.readLine();
                        File filePath = new File("Server\\src\\main\\resources\\".concat(fileName).concat(".txt"));
                        boolean result = filePath.delete();
                        break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection();
        }
    }
}
