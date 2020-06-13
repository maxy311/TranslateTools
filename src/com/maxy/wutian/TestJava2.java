package com.maxy.wutian;

import b.f.a.S;
import org.eclipse.xtend.lib.macro.file.Path;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestJava2 {
    public static void main(String[] args) {
        String path = "E:\\workspace\\SHAREit";
//        StringReader.readFileAllString(new File(path));
//        test();
        File file = new File(path);
        System.out.println(file.exists() + "   " + path.contains("\\"));
        System.out.println(File.separator + "     " + File.pathSeparator + "        " + File.separatorChar + "       " + File.pathSeparatorChar);
        if (path.contains(File.separator))
            path = path.replaceAll(File.separator, "_");
        System.out.println(path);
    }

    private static void test() {
        String trimLineStr = "<string name=\"localcommon_entry_name_safebox\">Széf</string>";
        trimLineStr = trimLineStr.replaceAll("\n", "aaaaaa");
        String tagName = "string";
        String valuePattern = "((\\\"| )>(\n|(.*))+</" + tagName + ">)";
        System.out.println(valuePattern);
        Pattern compile = Pattern.compile(valuePattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = compile.matcher(trimLineStr);
        String value;
        if (matcher.find()) {
            value = matcher.group();
            value = value.substring(value.indexOf("\">") + 2, value.lastIndexOf("<"));
            value = value.replaceAll("aaaaaa", "\n");
            System.out.println(value);
        } else
            throw new RuntimeException("cannot parse value : " + trimLineStr);
    }
}
