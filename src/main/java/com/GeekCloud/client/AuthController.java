package com.GeekCloud.client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    Button logIn;
    @FXML
    Button signIn;
    private static Network network;
    public static Network getNetwork(){return network;}

    CreateNewWindow createNewWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNewWindow = new CreateNewWindow();
        network = new Network();
        network.start();
        logIn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                pressOnLogIn();
            }
        });
        signIn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                pressOnSignIn();
            }
        });
    }
    @FXML
    public void pressOnLogIn() {
        if (logIn.isManaged()) {
            System.out.println("log in");
            createNewWindow.create("/com/GeekCloud/Login.fxml", "Enter your login/password:", 400,400);
        }
    }
    public void pressOnSignIn() {
        if (signIn.isManaged()) {
            System.out.println("sign in");
            createNewWindow.create("/com/GeekCloud/SignIn.fxml", "Enter your login/password:", 400,600);
        }
    }
}
