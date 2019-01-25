package com.GeekCloud.client;

import com.GeekCloud.common.AbstractMessage;
import com.GeekCloud.common.AuthorizationMessage;
import com.GeekCloud.common.FileMessage;
import com.GeekCloud.common.PermissionMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.file.Paths;

public class Network {
    private static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    public void start() {
        try {
            System.out.println("Network started");
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(),1048*1048*64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            System.out.println("auth/filerequest msg sent");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    // отправка файлов
    public static boolean sendFileMsg(FileMessage msg) {
        try {
            int onePartOfMsg = msg.splitFileMessage(msg.getPath(),"client_storage\\"); // объем одной части файла
            RandomAccessFile raf = new RandomAccessFile("client_storage\\" + msg.getFilename(),"r");
            byte[] data = new byte[(int)raf.length()/onePartOfMsg];
            System.out.println("file size: " + raf.length());
            try{
                while(raf.read(data) != -1){
                    out.writeObject(new FileMessage(msg.getFilename(),data));
                 }
                System.out.println("file sent successfully");
                // посылаем серверу сигнал о конце файла
                out.writeObject(null);
                out.close();
                return true;
                }catch (EOFException e){}
        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
     //   System.out.println("msg read");
        return (AbstractMessage) obj;
    }
}
