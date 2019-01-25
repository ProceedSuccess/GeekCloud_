package com.GeekCloud.common;

public class AuthorizationMessage extends AbstractMessage {
    private String login;
    private String password;
    private String nickname;
    public String getLogin(){return login;}
    public String getPassword(){return password;}
    public String getNickname(){return nickname;}

    public AuthorizationMessage(String log, String pass){
        this.login = log;
        this.password = pass;
    }
    public AuthorizationMessage(String log, String pass, String nickname){
        this.login = log;
        this.password = pass;
        this.nickname = nickname;
    }
    public String toString(){
        return (this.login + " " + this.password);
    }
}
