package com.wutian.maxy.tools;


import com.wutian.maxy.jdom.FileReaderEx;
import com.wutian.maxy.jdom.entity.StrEntity;
import com.wutian.maxy.tools.fileter.StringFileFilter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GetTranslateHelper {
    private File outFile;
    private File projectFile;
    private String projectName;
    private String basePath;
    private String lastTag;
    private String compareDir;

    public GetTranslateHelper(File projectFile, File outFile, String lastTag, String compareDir) {
        this.outFile = outFile;
        this.projectFile = projectFile;
        projectName = projectFile.getName();
        basePath = projectFile.getAbsolutePath();

        this.lastTag = lastTag;
        this.compareDir = compareDir;
    }

    public void startGet() {
        //1. delete origin file;
        checkOutputFile();

        //read values file to map
        Map<String, Map<String, Map<String, StrEntity>>> valuesMap = new HashMap<>(); // module -- file -- strings.
        readStringsToMap(valuesMap, projectFile, "values");

        //read values-XX file to map
        Map<String, Map<String, Map<String, StrEntity>>> valuesXXMap = new HashMap<>();
        readStringsToMap(valuesXXMap, projectFile, isEmptyStr(compareDir) ? compareDir : "values-ar");

        //read last tag values file to map
        Map<String, Map<String, Map<String, StrEntity>>> preValueMap = null;
        if (isEmptyStr(lastTag)) {
            preValueMap = new HashMap<>();
        }


        PrintTranslateToFile printTranslateToFile = new PrintTranslateToFile(projectFile, outFile);
        printTranslateToFile.printToFile(valuesMap, valuesXXMap, preValueMap);
    }


    private void checkOutputFile() {
        if (outFile == null || !outFile.exists())
            throw new RuntimeException("GetTranslateHelper projectFile error");

        for (File file : outFile.listFiles())
            file.delete();
    }

    private void readStringsToMap(Map<String, Map<String, Map<String, StrEntity>>> valuesMap, File file, String valueDir) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles(new StringFileFilter(valueDir))) {
                readStringsToMap(valuesMap, listFile, valueDir);
            }
        } else {
            String fileModule = getFileModule(file);
            Map<String, Map<String, StrEntity>> mapMap = valuesMap.get(fileModule);
            if (mapMap == null)
                mapMap = new HashMap<>();

            Map<String, StrEntity> strEntityMap = FileReaderEx.readStringToMap(file);
            mapMap.put(file.getName(), strEntityMap);
            valuesMap.put(fileModule, mapMap);
        }
    }

    private String getFileModule(File file) {
        String parent = file.getParent();
        String module = parent.replace(basePath, "");
        module = module.replace(File.separator, "_");
        if (module.contains("src"))
            return module.substring(1, module.indexOf("src") - 1);
        else
            return module.substring(1, module.indexOf("res") - 1);
    }

    private boolean isEmptyStr(String str) {
        return str == null || str.trim().length() == 0;
    }
}
