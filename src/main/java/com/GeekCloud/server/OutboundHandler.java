package com.GeekCloud.server;

import com.GeekCloud.common.FileMessage;
import com.GeekCloud.common.FileRequest;
import com.GeekCloud.common.PermissionMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class OutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof PermissionMessage){
        PermissionMessage pm = (PermissionMessage)msg;
        System.out.println("access: " + pm.getAccess());
        ctx.writeAndFlush(pm);
        }
        if (msg instanceof FileMessage){
            FileMessage fm = (FileMessage)msg;
            try {
                int numberOfParts = fm.splitFileMessage(fm.getPath(),"server_storage\\"); // объем одной части файла
                RandomAccessFile raf = new RandomAccessFile("server_storage\\" + fm.getFilename(),"r");
                byte[] data = new byte[(int)raf.length()/numberOfParts];
                System.out.println("file size: " + raf.length());
                try{
                    while(raf.read(data) != -1){
                        ctx.writeAndFlush(new FileMessage(fm.getFilename(),data));
                        ReferenceCountUtil.release(msg);
                    }
                    ctx.writeAndFlush(null);
                    System.out.println("file sent successfully");
                }catch (EOFException e){}
            }catch (IOException e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush(fm);
        }
    }
}
