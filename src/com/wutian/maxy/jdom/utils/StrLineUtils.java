package com.wutian.maxy.jdom.utils;

public class StrLineUtils {
    private boolean isNotTranslate(String str) {
        if (str.contains("translate") || str.contains("translatable") || str.contains("translatable"))
            return true;
        if (str.contains("\">@string/"))
            return true;
        if (str.contains("<string name=\"app_name\">"))
            return true;
        return false;
    }
}
