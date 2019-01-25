package com.GeekCloud.client;

import com.GeekCloud.common.AbstractMessage;
import com.GeekCloud.common.AuthorizationMessage;
import com.GeekCloud.common.PermissionMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    protected static Network network;
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Button ok;

    String log;
    String pass;

    CreateNewWindow createNewWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        createNewWindow = new CreateNewWindow();
        network = AuthController.getNetwork();
        // слушаем сервер на предмет получения ответа на запрос авторизации
        Thread t = new Thread(() -> {
            while(true){
                try {
                    AbstractMessage am = network.readObject();
                    if (am instanceof PermissionMessage) {
                        System.out.println("pm catched!");
                        if(((PermissionMessage) am).getAccess() == true) {
                            openMainScene();
                            break;
                        }else System.out.println("access denied");
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();}
            }
        });
        t.setDaemon(true);
        t.start();
    }
//   открываем главную часть приложения
    public void openMainScene() {
        Platform.runLater(() -> {
            createNewWindow.create
                    ("/com/GeekCloud/main.fxml",
                            "Choose file to download/upload:",
                            600,800);
        });
    }

    public void sendLoginPassword(ActionEvent actionEvent){
        if (login.getLength() > 0 && password.getLength() >0 && ok.isArmed()) {
            log = login.getText();
            pass = password.getText();
            AuthorizationMessage am = new AuthorizationMessage(log,pass);
            System.out.println(log + " " + pass);
            network.sendMsg(am);
        }
    }
}
