package com.GeekCloud.client;

import com.GeekCloud.common.AbstractMessage;
import com.GeekCloud.common.AuthorizationMessage;
import com.GeekCloud.common.PermissionMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController extends LogInController implements Initializable {
    @FXML
    TextField nickname;
    @FXML
    Button ok;
    String nick;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNewWindow = new CreateNewWindow();
        network = AuthController.getNetwork();
        Thread t = new Thread(() -> {
            while(true){
                try {
                    AbstractMessage am = network.readObject();
                    if (am instanceof PermissionMessage) {
                        System.out.println("pm catched!");
                        if(((PermissionMessage) am).getAccess() == true) {
                            openMainScene();
                            break;
                        }else System.out.println("wrong data or such user already exists");
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();}
            }
        });
        t.setDaemon(true);
        t.start();
    }
    @Override
    public void sendLoginPassword(ActionEvent actionEvent) {
        if (login.getLength() > 0 && password.getLength() >0 && nickname.getLength() > 0 && ok.isArmed()) {
            log = login.getText();
            pass = password.getText();
            nick = nickname.getText();
            AuthorizationMessage am = new AuthorizationMessage(log,pass,nick);
            System.out.println(log + " " + pass + " " + nick);
            network.sendMsg(am);
        }
    }
}
