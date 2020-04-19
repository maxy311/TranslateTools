package com.wutian.maxy.tools;

import com.wutian.maxy.jdom.FileReaderEx;
import com.wutian.maxy.jdom.entity.StrEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PrintTranslateToFile {
    public static final String WRITE_FILELNMAE_SPLIT = "    //-----------------------------";
    private File projectFile;
    private File outPutFile;
    private String basePath;
    public PrintTranslateToFile(File projectFile, File outPutFile) {
        this.projectFile = projectFile;
        this.outPutFile = outPutFile;
        this.basePath = projectFile.getAbsolutePath();
    }

    public void printToFile(Map<String, Map<String, Map<String, StrEntity>>> valuesMap, Map<String, Map<String, Map<String, StrEntity>>> valuesXXMap, Map<String, Map<String, Map<String, StrEntity>>> preValueMap) {
        Set<String> keySet = valuesMap.keySet();
        for (String model : keySet) {
            //model ----> module
            Map<String, Map<String, StrEntity>> valueFile = valuesMap.get(model);
            Map<String, Map<String, StrEntity>> valueXXFile = valuesXXMap == null ? null : valuesXXMap.get(model);
            Map<String, Map<String, StrEntity>> preValueFile = preValueMap == null ? null : preValueMap.get(model);
            toPrintTranslate(model, valueFile, valueXXFile, preValueFile);
        }
    }

    private void toPrintTranslate(String module, Map<String, Map<String, StrEntity>> valueMap, Map<String, Map<String, StrEntity>> valueXXMap, Map<String, Map<String, StrEntity>> preValueMap) {
        Set<String> keySet = valueMap.keySet();
        Map<String, List<StrEntity>> transValueMap = new HashMap<>();
        Map<String, List<StrEntity>> transZHValueMap = new HashMap<>();
        for (String fileName : keySet) {
            //key ----> fileName
            Map<String, StrEntity> valueFile = valueMap.get(fileName);
            Map<String, StrEntity> valueXXFile = valueXXMap == null ? null : valueXXMap.get(fileName);
            Map<String, StrEntity> preValueFile = preValueMap == null ? null : preValueMap.get(fileName);

            getTranslateData(module, fileName, valueFile, valueXXFile, preValueFile, transValueMap, transZHValueMap);
        }

        if (!transValueMap.isEmpty()) {
            writeTranslateToFile(module, transValueMap, transZHValueMap);
        }
    }

    private void getTranslateData(String module, String fileName, Map<String, StrEntity> valueMap, Map<String, StrEntity> valueXXMap, Map<String, StrEntity> preValueMap, Map<String, List<StrEntity>> transValueMap, Map<String, List<StrEntity>> transZHValueMap) {
        List<StrEntity> list = new ArrayList<>();
        //store keys to find zh values
        List<String> keys = new ArrayList<>();

        if (valueXXMap == null) {
            for (String key : valueMap.keySet()) {
                StrEntity strEntity = valueMap.get(key);
                if (!strEntity.isNeedTranslate())
                    continue;
                keys.add(key);
                list.add(strEntity);
            }

            if (!list.isEmpty()) {
                transValueMap.put(fileName, list);
                transZHValueMap.put(fileName, getZhTranslateData(module, fileName, list, keys));
            }
        } else {
            for (String key : valueMap.keySet()) {
                StrEntity strEntity = valueMap.get(key);
                if (!strEntity.isNeedTranslate())
                    continue;

                if (!valueXXMap.containsKey(key)) {
                    keys.add(key);
                    list.add(strEntity);
                    continue;
                }

                if (preValueMap == null)
                    continue;

                if (!preValueMap.containsKey(key)) {
                    keys.add(key);
                    list.add(strEntity);
                    continue;
                }

                StrEntity preValue = preValueMap.get(key);
                if (!preValue.getLineText().equals(strEntity.getLineText())) {
                    keys.add(key);
                    list.add(strEntity);
                }
            }
            if (!list.isEmpty()) {
                transValueMap.put(fileName, list);
                transZHValueMap.put(fileName, getZhTranslateData(module, fileName, list, keys));
            }
        }
    }

    private List<StrEntity> getZhTranslateData(String module, String fileName, List<StrEntity> list, List<String> zhKeys) {
        if (list.isEmpty())
            return Collections.emptyList();

        //try write values-zh-rCN file
        String moduleRealPath = basePath + File.separator + module.replace("_", File.separator);
        String moduleResPath = moduleRealPath + File.separator + "src" + File.separator + "main" + File.separator + "res";
        File file = new File(moduleResPath);
        if (!file.exists()) {
            moduleResPath = moduleRealPath + File.separator + "res";
            file = new File(moduleResPath);
        }

        File zhDir = new File(file, "values-zh-rCN");
        if (!zhDir.exists()) {
            System.out.println("values-zh-rCN not exists :::: " + zhDir.getAbsolutePath());
            return Collections.emptyList();
        }

        File zhFile = new File(zhDir, fileName);
        if (!zhFile.exists()) {
            System.out.println("zhFile not exists :::: " + zhFile.getAbsolutePath());
            return Collections.emptyList();
        }

        List<StrEntity> zhTranslateList = new ArrayList<>();
        Map<String, StrEntity> zhMaps = FileReaderEx.readStringToMap(file);
        for (String key : zhMaps.keySet()) {
            if (!zhKeys.contains(key))
                continue;
            zhTranslateList.add(zhMaps.get(key));
        }
        return zhTranslateList;
    }

    private void writeTranslateToFile(String module, Map<String, List<StrEntity>> transValueMap, Map<String, List<StrEntity>> transZHValueMap) {
        //write value date
        File valueFile = getModuleFile(module, false);
        writeTranslateToFile(valueFile, transValueMap);

        //write zhValue data
        File zhValueFile = getModuleFile(module, true);
        writeTranslateToFile(zhValueFile, transZHValueMap);
    }

    private void writeTranslateToFile(File file, Map<String, List<StrEntity>> translateData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<resources>\n");

            for (String fileName : translateData.keySet()) {
                bw.write(WRITE_FILELNMAE_SPLIT + fileName);
                bw.newLine();
                bw.flush();
                List<StrEntity> list = translateData.get(fileName);
                for (StrEntity strEntity : list) {
                    if (!strEntity.isNeedTranslate())
                        continue;
                    bw.write("    " + strEntity.getLineText());
                    bw.newLine();
                    bw.flush();
                }

                bw.write("\n\n\n");
            }

            bw.write("</resources>");
        } catch (IOException e1) {
            System.out.println("writeTranslateToFile Error :: " + e1.toString());
        }
    }

    private File getModuleFile(String module, boolean isZHFile) {
        File valueDir = null;
        if (isZHFile)
            valueDir = new File(outPutFile, "values-zh-rCN");
        else
            valueDir = new File(outPutFile, "values");
        if (!valueDir.exists())
            valueDir.mkdir();
        File file = new File(valueDir, module + "_strings.xml");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
        }
        return file;
    }
}
