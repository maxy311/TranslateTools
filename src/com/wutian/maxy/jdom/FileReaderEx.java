package com.wutian.maxy.jdom;

import com.wutian.maxy.jdom.entity.BaseEntity;
import com.wutian.maxy.jdom.entity.CommentEntity;
import com.wutian.maxy.jdom.entity.LineBreak;
import com.wutian.maxy.jdom.entity.StrEntity;
import org.jdom.*;
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

    public static List<BaseEntity> readAllStringToList(File file) {
        if (file == null || !file.exists() || file.isDirectory())
            return Collections.EMPTY_LIST;

        try {
            List<BaseEntity> baseEntities = new ArrayList<>();

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(file);
            Element classElement = document.getRootElement();
            List<Content> contents = classElement.getContent();
            System.out.println(contents.size());
            for (Content content : contents) {
                if (content instanceof Element) {
                    StrEntity strEntity = new StrEntity((Element) content);
                    baseEntities.add(strEntity);
                    System.out.print(strEntity.getValue().contains("&#038;"));
                    System.out.print(strEntity.getLineText());
                } else if (content instanceof Comment) {
                    CommentEntity commentEntity = new CommentEntity((Comment) content);
                    baseEntities.add(commentEntity);
                    System.out.print(commentEntity.getLineText());
                } else {
                    LineBreak lineBreak = new LineBreak(content);
                    baseEntities.add(lineBreak);
                    System.out.print(lineBreak.getLineText());
                }
            }
            return baseEntities;
        } catch (JDOMException e) {
            System.out.println("readString JDOMException :: " + e.toString());
        } catch (IOException e) {
            System.out.println("readString IOException :: " + e.toString());
        }
        return Collections.EMPTY_LIST;
    }
}
