package com.wutian.maxy.jdom;

import com.wutian.maxy.jdom.entity.StrEntity;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileReaderEx {
    public static Map<String, StrEntity> readStringToMap(File file) {
        if (file == null || !file.exists() || file.isDirectory())
            return Collections.emptyMap();

        try {
            Map<String, StrEntity> map = new HashMap<>();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(file);
            Element classElement = document.getRootElement();
            List<Element> stringList = classElement.getChildren();
            for (int i = 0; i < stringList.size(); i++) {
                StrEntity strEntity = new StrEntity(stringList.get(i));
                if (strEntity.isNeedTranslate())
                    map.put(strEntity.getStringKey(), strEntity);
            }
            return map;
        } catch (JDOMException e) {
            System.out.println("readString JDOMException :: " + e.toString());
        } catch (IOException e) {
            System.out.println("readString IOException :: " + e.toString());
        }
        return Collections.emptyMap();
    }
}
