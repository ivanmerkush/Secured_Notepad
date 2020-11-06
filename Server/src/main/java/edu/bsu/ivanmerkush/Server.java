package edu.bsu.ivanmerkush;

import edu.bsu.ivanmerkush.security.SecurityService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class Server{

    private static final SecurityService ideaAlgorithm = new SecurityService();
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedOutputStream bufferedOutputStream;
    private static BufferedInputStream bufferedInputStream;
    private static BufferedReader bufferedReader;
    private static ByteArrayOutputStream byteArrayOutputStream;
    public Server()  {

    }

    static {

        try{
            serverSocket = new ServerSocket(1099);
            clientSocket = serverSocket.accept();
            bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            bufferedReader.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)   {
        try {

            while (true) {
                if (clientSocket.getInputStream().available() > 0) {
                    switch (bufferedReader.readLine()) {
                        case "file":
                            try {
                                String fileName = bufferedReader.readLine();
                                byte[] bytes = Files.readAllBytes(Paths.get("Server\\src\\main\\resources\\".concat(fileName).concat(".txt")));
                                if(ideaAlgorithm.isKeyGenerated()) {
                                    bytes =  ideaAlgorithm.encodeText(bytes);
                                }
                                bufferedOutputStream.write(bytes);
                                bufferedOutputStream.flush();
                                System.out.println("Successful");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            break;
                        case "key":
                            String strKey = bufferedReader.readLine();
                            ideaAlgorithm.setPublicKey(strKey);
                            System.out.println("Successful");
                            break;
                        case "session":
                            byte[] encodedKey = ideaAlgorithm.generateSessionKey();
                            bufferedOutputStream.write(encodedKey);
                            bufferedOutputStream.flush();
                            break;
                        case "edit":
                            String fileName = bufferedReader.readLine();
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            while(bufferedInputStream.available() <= 0) {

                            }
                            byte[] data = new byte[1024];
                            while (bufferedInputStream.available() > 0) {
                                int read = bufferedInputStream.read(data);
                                byteArrayOutputStream.write(data, 0, read);
                            }
                            byte[] decodedText = ideaAlgorithm.decodeText(byteArrayOutputStream.toByteArray());
                            byteArrayOutputStream.flush();
                            FileOutputStream fileOutputStream = new FileOutputStream("Server\\src\\main\\resources\\".concat(fileName).concat(".txt"));
                            fileOutputStream.write(decodedText);
                            fileOutputStream.close();
                            break;
                        case "delete":
                            fileName = bufferedReader.readLine();
                            File filePath = new File("Server\\src\\main\\resources\\".concat(fileName).concat(".txt"));
                            boolean result = filePath.delete();
                            break;
                    }
                }
            }

        }
        catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex ) {
            ex.printStackTrace();
        }
    }



}

