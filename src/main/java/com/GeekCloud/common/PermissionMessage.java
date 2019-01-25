package com.GeekCloud.common;

public class PermissionMessage extends AbstractMessage {
    private boolean access;
    public boolean getAccess(){return access;}
    public PermissionMessage(boolean access){
        this.access = access;
    }
}
