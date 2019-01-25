package com.GeekCloud.common;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;
    private long onePartLength;
    private Path path;

    public String getFilename() {
        return filename;
    }
    public byte[] getData() {
        return data;
    }
    public int getDataSize(){ return data.length;}
    public long getOnePartLength(){return onePartLength;}
    public Path getPath(){return path;}

    public FileMessage(Path path) {
        this.filename = path.getFileName().toString();
        this.path = path;
    }
    public FileMessage(String filename,byte[] data){
        this.filename = filename;
        this.data = data;
    }
    //надо придумать более эффективынй алгоритм оценки и разбиения файла
    public int splitFileMessage(Path path, String sourсe) throws IOException {
        filename = path.getFileName().toString();
        RandomAccessFile raf = new RandomAccessFile(sourсe + filename, "r");
        // оцениваем размер файла (насколько затратна операция сравнения?поменять порядок?)
            if(raf.length() > 10*1024*1024*1024){
                onePartLength = raf.length()/ 4096;
            }else if (raf.length() > 1024*1024*1024 && raf.length() < 10*1024*1024*1024) {
                onePartLength = raf.length()/ 2048;
            }else if (raf.length() > 256*1024*1024 && raf.length() < 1024*1024*1024){
                // разбиваем на части
                onePartLength = raf.length()/ 1024;
            }else if (raf.length() < 256 * 1024 *1024 && raf.length() > 128*1024*1024){
                onePartLength = raf.length()/ 64;
            }else if (raf.length() < 128 * 1024 *1024 && raf.length() > 16*1024*1024){
                onePartLength = raf.length()/ 32;
            }else onePartLength = raf.length() / 8;
        System.out.println("length of one part: " + onePartLength);
        System.out.println("number of parts: " + (int)(raf.length()/ onePartLength));
        // возвращаем длину одной части отправляемого файла
            return (int)raf.length()/(int) onePartLength;
    }
}
