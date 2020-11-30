package edu.bsu.ivanmerkush;
import edu.bsu.ivanmerkush.socket.SocketService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Client extends Application {

    private TextArea textArea;
    private final SocketService socketService;
    public Client() {
        socketService = new SocketService();
    }



    @Override
    public void start(Stage primaryStage) {


        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        Menu keyMenu = new Menu("Generate Keys");


        Menu fileMenu = new Menu("File");
        MenuItem getFile = new MenuItem("Get File");
        MenuItem saveFile = new MenuItem("Save File");
        MenuItem createFile = new MenuItem("Create File");
        MenuItem editFile = new MenuItem("Edit File");
        MenuItem deleteFile = new MenuItem("Delete File");
        MenuItem finishEdit = new MenuItem("Finish Edit");
        MenuItem finishCreate = new MenuItem("Finish new file");
        menuBar.getMenus().add(fileMenu);
        fileMenu.getItems().addAll(getFile, saveFile, createFile, finishCreate, editFile, finishEdit, deleteFile);
        finishEdit.setDisable(true);
        finishCreate.setDisable(true);

        MenuItem getKeyRSA = new MenuItem("Generate RSA Key");
        MenuItem getSessionKey = new MenuItem("Get Session Key");
        menuBar.getMenus().add(keyMenu);
        keyMenu.getItems().addAll(getKeyRSA, getSessionKey);


        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setMinWidth(500);
        textArea.setWrapText(true);
        root.setTop(menuBar);
        root.setCenter(textArea);

        primaryStage.setTitle("Notepad");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();

        getFile.setOnAction(event -> {
            textArea.setText(socketService.getText(callDialog("Get file")));
        });

        saveFile.setOnAction(event -> {
            socketService.saveFile();
        });

        createFile.setOnAction(event -> {
            textArea.clear();
            textArea.setEditable(true);
            finishCreate.setDisable(false);
        });

        editFile.setOnAction(event -> {
            textArea.setEditable(true);
            finishEdit.setDisable(false);
        });

        finishEdit.setOnAction(event -> {
            socketService.editFile(textArea.getText());
            textArea.setEditable(false);
            finishEdit.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "File has been edited").showAndWait();
        });

        finishCreate.setOnAction(event -> {
            socketService.createFile(callDialog("Create file"), textArea.getText());
            textArea.setEditable(false);
            finishCreate.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "File has been created").showAndWait();
        });

        deleteFile.setOnAction(event -> {
            socketService.deleteFile();
            textArea.clear();
            new Alert(Alert.AlertType.INFORMATION, "File has been deleted").showAndWait();
        });

        getKeyRSA.setOnAction(event -> {
            socketService.generateKeyRSA();
            getKeyRSA.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "RSA key generated successfully").showAndWait();
        });

        getSessionKey.setOnAction(event -> {
            socketService.getSessionKey();
            getSessionKey.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Session key received from server").showAndWait();
        });

        primaryStage.setOnCloseRequest(event -> {
            try {
                socketService.stopConnection();
                System.out.println("Client closing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    private String callDialog(String messageRequest) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("File Dialog");
        dialog.setHeaderText("Enter name of file");
        ButtonType okButton = new ButtonType(messageRequest, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        TextField fileField = new TextField();

        grid.add(new Label("fileName"), 0, 0);
        grid.add(fileField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return fileField.getText();
            }
            else {
                return null;
            }
        });
        dialog.showAndWait();
        return dialog.getResult();
    }

    public static void main(String[] args) {
        launch(args);
    }
}