package com.GeekCloud.server;

import com.GeekCloud.client.Network;
import com.GeekCloud.common.AuthorizationMessage;
import com.GeekCloud.common.FileMessage;
import com.GeekCloud.common.FileRequest;
import com.GeekCloud.common.PermissionMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class InboundHandler extends ChannelInboundHandlerAdapter {
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FileOutputStream fous;
        //поток для записи входящих файлов на диск
        try {
            if (msg == null) {
                return;
            }
            // запрос на скачивание файла с сервера
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get("server_storage\\" + fr.getFilename()))) {
                    //отправляем пустое сообщение исходящему хэндлеру
                    FileMessage fm = new FileMessage(Paths.get("server_storage\\" + fr.getFilename()));
                    System.out.println("file name: " + fm.getFilename());
                    ctx.writeAndFlush(fm);
                }
                ReferenceCountUtil.release(msg);
            }
            // получение файла от клиента (проверить, кушает ли создание каждый раз
            // FileOutputStream память
            if (msg instanceof FileMessage) {
                if (msg == null) return;
                fous = new FileOutputStream("server_storage\\" + ((FileMessage) msg).getFilename(),true);
                byte[] b = ((FileMessage) msg).getData();
                fous.write(b);
                fous.close();
                ctx.flush();
            }
            //сообщение с логином/паролем
            if (msg instanceof AuthorizationMessage){
                PermissionMessage pm;
                if (((AuthorizationMessage) msg).getNickname() != null){
                    System.out.println("nickname is "+((AuthorizationMessage) msg).getNickname());
                    pm = DataBaseRequestor.createAccount(((AuthorizationMessage) msg).getLogin(),
                            ((AuthorizationMessage) msg).getPassword(),((AuthorizationMessage) msg).getNickname());
                    System.out.println(pm.getAccess());
                }
                else {pm = DataBaseRequestor.query(((AuthorizationMessage) msg).getLogin(),
                                        ((AuthorizationMessage) msg).getPassword());}
                ctx.writeAndFlush(pm);
                ReferenceCountUtil.release(msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}


