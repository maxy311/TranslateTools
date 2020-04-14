package com.wutian.maxy.jdom.utils;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static void fileChannelCopy(File src, File dst) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
        } finally {
            close(inChannel);
            close(outChannel);
        }
    }

    public static void close(Closeable cursor) {
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
        }
    }
}
