package com.maxy.wutian;

import com.maxy.wutian.get.GetTranslateHelper;

import java.io.File;


public class TestJava {
    private static final String PROJECT_PATH = "E:\\workspace\\SHAREit";
    private static final String DESKTOP_PATH = "E:\\";
    public static void main(String[] args) {
        File file = new File(PROJECT_PATH);
        File deskFile = new File(DESKTOP_PATH);
        GetTranslateHelper getSHAREitTranslate = new GetTranslateHelper(file, deskFile, null, null);
        getSHAREitTranslate.startGetTranslate();
    }
}
