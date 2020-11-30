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
    private Socket clientSocket;

    private BufferedReader in;
    private BufferedWriter out;

    private PrintWriter printWriter;

    private final SecurityService securityService;
    private String currentFile;
    private byte[] currentText;


    public SocketService() {
        this.securityService = new SecurityService();
        startConnection();
    }


    public void startConnection() {
        try {
            clientSocket = new Socket("localhost", 1099);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() throws IOException {

        in.close();
        out.close();
        printWriter.close();
        clientSocket.close();
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
            byte[] bytes = changedText.getBytes();
            if(securityService.isKeyGenerated()) {
                bytes = securityService.encodeText(bytes);
            }
            String str = "edit\n".concat(currentFile).concat("\n").concat(Base64.getEncoder().encodeToString(bytes));
            printWriter.println(str);
            printWriter.flush();
            currentText = changedText.getBytes();
            saveFile();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        File filePath = new File("Client\\src\\main\\resources\\".concat(currentFile).concat(".txt"));
        filePath.delete();
        printWriter.println("delete\n".concat(currentFile));
        printWriter.flush();
    }

    public void createFile(String fileName, String text) {
        this.currentFile = fileName;
        editFile(fileName, text);
    }

    public void getSessionKey() {
        try {
            printWriter.println("session");
            printWriter.flush();
            byte[] bytes = Base64.getDecoder().decode(in.readLine());
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
            printWriter.println("key\n".concat(strKey));
            printWriter.flush();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public String getText(String name) {

        try {
            printWriter.println("file\n".concat(name));
            printWriter.flush();
            byte[] decodedtext = Base64.getDecoder().decode(in.readLine());
            if(securityService.isKeyGenerated()) {
               decodedtext = securityService.decodeText(decodedtext);
            }
            currentFile = name;
            currentText = decodedtext;
            return new String(decodedtext);

        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "Some error happened";
    }


}
