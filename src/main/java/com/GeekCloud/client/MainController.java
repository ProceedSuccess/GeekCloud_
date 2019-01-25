package com.GeekCloud.client;

import com.GeekCloud.common.AbstractMessage;
import com.GeekCloud.common.FileMessage;
import com.GeekCloud.common.FileRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    TextField tfFileName;
    @FXML
    TextField tfLoadFile;
    @FXML
    ListView<String> clientFilesList;
    @FXML
    ListView<String> serverFilesList;
    @FXML
    Button download;
    @FXML
    Button upload;
   private Network network;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = AuthController.getNetwork();
        System.out.println("initialize main scene");
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
//                    if (am instanceof FileMessage && ((FileMessage) am).getOnePartLength() != 0){
//                        System.out.println(((FileMessage) am).getOnePartLength());
//                    }
                    if (am instanceof FileMessage) {
                        if (am == null) {
                            System.out.println("file catched");
                            continue;}
                        FileMessage fm = (FileMessage) am;
                        FileOutputStream fous = new FileOutputStream("client_storage\\" + ((FileMessage) fm).getFilename(),true);;
                        byte[] b =  fm.getData();
                        fous.write(b);
                      //  fous.close();
//                        Files.write(Paths.get("client_storage\\"
//                                + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
              //  network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        clientFilesList.setItems(FXCollections.observableArrayList());
        serverFilesList.setItems(FXCollections.observableArrayList());
        refreshLocalFilesList();
        refreshServerFilesList();
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0 && download.isArmed()) {
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            System.out.println("file request sent");
            tfFileName.clear();
        }
    }
    public void pressOnUploadBtn(ActionEvent actionEvent) throws IOException {
        if (tfLoadFile.getLength() > 0 && upload.isArmed()) {
            Network.sendFileMsg(new FileMessage(Paths.get("client_storage\\" + tfLoadFile.getText())));
         //   System.out.println(" file message sent");
            tfLoadFile.clear();
            refreshServerFilesList();
        }
    }
    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                clientFilesList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    clientFilesList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public void refreshServerFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                serverFilesList.getItems().clear();
                Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    serverFilesList.getItems().clear();
                    Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverFilesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
