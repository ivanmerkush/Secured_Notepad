package edu.bsu.ivanmerkush.socket;

import edu.bsu.ivanmerkush.security.SecurityService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

public class SocketService {
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private PrintWriter printWriter;
    private ByteArrayOutputStream buffer;

    private final SecurityService securityService;
    private String currentFile;
    private byte[] currentText;


    public SocketService() {
        this.securityService = new SecurityService();
        startConnection();
    }


    public void startConnection() {
        try {
            Socket clientSocket = new Socket("localhost", 1099);
            printWriter = new PrintWriter(clientSocket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public byte[] getCurrentText() {
        return currentText;
    }

    public void saveFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("Client\\src\\main\\resources\\".concat(currentFile).concat(".txt"));
            fileOutputStream.write(currentText);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editFile(String currentFile, String changedText) {
        try {
            printWriter.println("edit\n".concat(currentFile));
            byte[] bytes = securityService.encodeText(changedText.getBytes());
            printWriter.flush();
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
        } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {

    }

    public void createFile(String fileName, String text) {

        editFile(fileName, text);
    }

//    public String decipher() {
//        String fileName = callDialog("Decode file");
//        byte[] bytes;
//        try {
//            bytes = Files.readAllBytes(Paths.get("Client\\src\\main\\resources\\".concat(fileName).concat(".txt")));
//            return new String(securityService.decodeText(bytes));
//        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
//            e.printStackTrace();
//        }
//        return "Some error happened";
//    }

    public void getSessionKey() {
        try {
            printWriter.println("session");
            printWriter.flush();
            while (bufferedInputStream.available() <= 0) {

            }
            int length = bufferedInputStream.available();
            byte[] bytes = new byte[length];
            bufferedInputStream.read(bytes);
            securityService.setSecretKey(bytes);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void generateKeyRSA() {
        try {
            securityService.generateRSA();
            byte[] bytes = securityService.getPublicKey().getEncoded();
            String strKey = Base64.getEncoder().encodeToString(bytes);
            System.out.println(strKey);
            printWriter.println("key\n".concat(strKey));
            printWriter.flush();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public String getText(String name) {

        try {
            buffer = new ByteArrayOutputStream();
            printWriter.println("file\n".concat(name));
            printWriter.flush();
            while (bufferedInputStream.available() <= 0) {

            }
            byte[] data = new byte[1024];
            while (bufferedInputStream.available() > 0) {
                int read = bufferedInputStream.read(data);
                buffer.write(data, 0, read);
                System.out.println(read);
            }
            byte[] decodedtext = securityService.decodeText(buffer.toByteArray());
            buffer.close();
            currentFile = name;
            currentText = decodedtext;
            return new String(decodedtext);

        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "Some error happened";
    }


}
