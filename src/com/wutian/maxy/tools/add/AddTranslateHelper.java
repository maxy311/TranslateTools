package com.wutian.maxy.tools.add;

import com.wutian.maxy.jdom.FileReaderEx;
import com.wutian.maxy.jdom.entity.BaseEntity;
import com.wutian.maxy.jdom.entity.CommentEntity;
import com.wutian.maxy.jdom.entity.LineBreak;
import com.wutian.maxy.jdom.entity.StrEntity;
import com.wutian.maxy.jdom.utils.FileUtils;
import com.wutian.maxy.tools.PrintTranslateToFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddTranslateHelper {
    private File projectFile;
    private File translateFile;

    public AddTranslateHelper(File projectFile, File translateFile) {
        this.projectFile = projectFile;
        this.translateFile = translateFile;
    }

    public void start() {
        checkTranslateDir(translateFile);
        // 1. split file
        slipFile(translateFile);

        File splitFileRootDir = getSplitFileRootDir();
        for (File file : splitFileRootDir.listFiles()) {
            addTranslate(file);
        }
    }

    private void addTranslate(File file) {
        if (!file.isDirectory()) {
            System.out.println("addTranslate Error :: " + file.getName());
            return;
        }

        boolean childIsValues = false;
        File[] childFiles = file.listFiles();
        for (File listFile : childFiles) {
            childIsValues = listFile.getName().startsWith("values");
            if (childIsValues)
                break;
        }

        if (!childIsValues) {
            for (File childFile : childFiles) {
                addTranslate(childFile);
            }
            return;
        }
        File moduleResFile = getProjectModuleResFile(file);
        AddHelper addHelper = new AddHelper();
        addHelper.addTranslate(file, moduleResFile);
    }

    private File getProjectModuleResFile(File file) {
        String filePath = file.getAbsolutePath();
        File splitFileRootDir = getSplitFileRootDir();
        String modulePath = filePath.replace(splitFileRootDir.getAbsolutePath(), "");
        File moduleFile = new File(projectFile, modulePath);

        File resFile = new File(moduleFile, "src/main/res");
        if (!resFile.exists())
            resFile = new File(moduleFile, "res");
        return resFile;
    }

    private void slipFile(File translateFile) {
        File valuesDir = new File(translateFile, "values");
        if (!valuesDir.exists()) {
            throw new RuntimeException("values path error:" + valuesDir.getAbsolutePath());
        }

        // 1 遍历values 目录
        for (File valueXMLFile : valuesDir.listFiles()) {
            if (valueXMLFile.isHidden() || valueXMLFile.isDirectory())
                continue;
            String xmlFileName = valueXMLFile.getName();

            // 2 读取value 文件
            List<BaseEntity> baseEntities = null;
            if (isTransLateFile(valueXMLFile))
                baseEntities = FileReaderEx.readAllStringToList(valueXMLFile);

            // splitFile
            for (File valueXXDir : translateFile.listFiles()) {
                File valueXXFile = new File(valueXXDir, xmlFileName);
                if (!valueXXFile.exists()) {
                    System.out.println(valueXXDir.getName() + " not exist file : " + valueXXFile.getName());
                    continue;
                }

                if (baseEntities == null) {
                    // copy release note file
                    copyFileToReleaseNote(valueXXFile);
                    continue;
                }

                File splitDir = getSplitDir(valueXXFile);
                Map<String, StrEntity> stringStrEntityMap = FileReaderEx.readStringToMap(valueXXFile);
                Set<String> keySet = stringStrEntityMap.keySet();
                BufferedWriter bw = null;
                try {
                    for (int i = 0; i < baseEntities.size(); i++) {
                        BaseEntity baseEntity = baseEntities.get(i);
                        String line = baseEntity.getLineText();
                        if (baseEntity instanceof StrEntity) {
                            StrEntity strEntity = (StrEntity) baseEntity;
                            String stringKey = strEntity.getStringKey();
                            if (keySet.contains(stringKey))
                                line = stringStrEntityMap.get(stringKey).getLineText();
                        } else if (baseEntity instanceof CommentEntity) {
                            CommentEntity commentEntity = (CommentEntity) baseEntity;
                            String lineText = "    " + commentEntity.getLineText();
                            if (lineText.contains(PrintTranslateToFile.WRITE_FILELNMAE_SPLIT)) {
                                String fileName = lineText.replace(PrintTranslateToFile.WRITE_FILELNMAE_SPLIT, "");
                                File splitFile = createSplitFile(splitDir, fileName);
                                if (bw != null) {
                                    writerFooter(bw);
                                    bw.close();
                                }
                                bw = new BufferedWriter(new FileWriter(splitFile));
                                writerHeader(bw);
                                line = null;
                            }
                        } else if (baseEntity instanceof LineBreak) {
                            LineBreak lineBreak = (LineBreak) baseEntity;
                            String lineText = lineBreak.getLineText();
                            if (lineText.contains(PrintTranslateToFile.WRITE_FILELNMAE_SPLIT)) {
                                String fileName = lineText.replace(PrintTranslateToFile.WRITE_FILELNMAE_SPLIT, "").replace("\n", "").trim();
                                File splitFile = createSplitFile(splitDir, fileName);
                                if (bw != null) {
                                    writerFooter(bw);
                                    bw.close();
                                }
                                bw = new BufferedWriter(new FileWriter(splitFile));
                                writerHeader(bw);
                                line = null;
                            }
                        }
                        if (bw != null && line != null) {
                            bw.write(line);
                            bw.flush();
                        }
                    }
                    if (bw != null)
                        writerFooter(bw);
                } catch (Exception e) {
                    System.out.println(e.toString());
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void writerHeader(BufferedWriter bw) throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n");
    }

    public void writerFooter(BufferedWriter bw) throws IOException {
        bw.write("</resources>");
    }

    private boolean isTransLateFile(File file) {
        if (file.isHidden())
            return false;

        if (file.isDirectory())
            return false;
        String fileName = file.getName();

        if (!fileName.endsWith(".xml"))
            return false;
        else if (fileName.toLowerCase().contains("releasenote"))
            return false;
        else if (fileName.toLowerCase().contains("release"))
            return false;
        return true;
    }

    private File createSplitFile(File splitDir, String fileName) throws IOException {
        File file = new File(splitDir, fileName);
        if (!file.exists())
            file.createNewFile();
        return file;
    }

    private File getSplitDir(File xmlFile) {
        String name = xmlFile.getName();
        String valueXXName = xmlFile.getParentFile().getName();
        String replaceName = name.replace("_strings.xml", "");
        replaceName = replaceName.replace("_", "/");
        File valueXXDir = new File(getSplitFileRootDir(), replaceName + File.separator + valueXXName);
        if (!valueXXDir.exists())
            valueXXDir.mkdirs();
        return valueXXDir;
    }

    private void checkTranslateDir(File translateDir) {
        if (!translateDir.exists())
            throw new RuntimeException(translateDir.getName() + " not exists!  " + translateDir.getAbsolutePath());

        File valueDir = new File(translateDir, "values");
        if (!valueDir.exists())
            throw new RuntimeException("value dir not exists::: " + valueDir);
    }

    private File getSplitFileRootDir() {
        File parentFile = translateFile.getParentFile();
        File splitDir = new File(parentFile, projectFile.getName());
        if (splitDir.exists())
            splitDir.mkdir();
        return splitDir;
    }

    private void copyFileToReleaseNote(File releaseNoteFile) {
        File releaseNoteDir = getReleaseNoteDir();
        if (!releaseNoteDir.exists())
            releaseNoteDir.mkdir();
        String valueXXName = releaseNoteFile.getParentFile().getName();
        File valueXXFile = new File(releaseNoteDir, valueXXName);
        if (!valueXXFile.exists())
            valueXXFile.mkdir();

        File file = new File(valueXXFile, releaseNoteFile.getName());
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.fileChannelCopy(releaseNoteFile, file);
    }

    private File getReleaseNoteDir() {
        File parentFile = translateFile.getParentFile();
        File releaseNote = new File(parentFile, "Release_Note");
        if (releaseNote.exists())
            releaseNote.mkdir();
        return releaseNote;
    }
}
