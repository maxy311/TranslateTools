package com.wutian.maxy.tools;

import com.wutian.maxy.jdom.FileReaderEx;
import com.wutian.maxy.tools.add.AddTranslateHelper;

import java.io.File;

public class TestJava {
    public static void main(String[] args) {
//        File file = new File("/Users/maxy/Android/workspace/SHAREit/App/src/main/res/values/strings.xml");
//        FileReaderEx.readAllStringToList(file);
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper(new File("/Users/maxy/Android/workspace/SHAREit"), new File("/Users/maxy/Desktop/SHAREit_Translate"));
        addTranslateHelper.start();
    }
}
