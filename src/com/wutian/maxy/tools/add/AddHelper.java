package com.wutian.maxy.tools.add;

import com.wutian.maxy.jdom.FileReaderEx;
import com.wutian.maxy.jdom.entity.BaseEntity;
import com.wutian.maxy.jdom.entity.StrEntity;
import com.wutian.maxy.tools.ReplaceSpecialCharUtils;
import com.wutian.maxy.tools.task.Task;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AddHelper implements AddTranslateTask.AddTranslateListener {
    private ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    private List<Task> mTaskList = new ArrayList<>();

    private Map<String, List<BaseEntity>> mValuesData = new HashMap<>();

    public void addTranslate(File translateResFile, File resDir) {
        startCheckPathIsRight(translateResFile);
        startAddTranslate(translateResFile, resDir);
        endThreadPool();
    }

    private boolean startCheckPathIsRight(File translateResFile) {
        File[] valueDirArrays = translateResFile.listFiles();
        for (File valueDir : valueDirArrays) {
            if (valueDir.isHidden())
                continue;
            if (!valueDir.getName().startsWith("values"))
                throw new RuntimeException("Translate File Path Error!");

            for (File file : valueDir.listFiles()) {
                if (file.isDirectory()) {
                    //has error file
                    System.out.println(file.getAbsolutePath());
                    throw new RuntimeException("Translate File Path Error!");
                }
            }
        }
        return true;
    }

    private void startAddTranslate(File translateFileDir, File originDir) {
        if (originDir == null || !originDir.exists())
            return;

        if (translateFileDir == null || !translateFileDir.exists())
            return;

        for (File translateFile : translateFileDir.listFiles()) {
            if (translateFile.isHidden())
                continue;
            if (translateFile.isDirectory()) {
                File origin = new File(originDir, translateFile.getName());
                if (!origin.exists()) {
                    origin.mkdir();
                }
                startAddTranslate(translateFile, origin);
            } else {
                readValuesFile(translateFile, originDir);
                AddTranslateTask addTranslateTask = new AddTranslateTask(this, translateFile, originDir);
                mTaskList.add(addTranslateTask);
                mFixedThreadPool.submit(addTranslateTask);
            }
        }
    }

    private void readValuesFile(File translateFile, File originDir) {
        String translateFileName = translateFile.getName();
        if (mValuesData.containsKey(translateFileName))
            return;


        File resDir = originDir.getParentFile();
        if (!resDir.getName().equals("res"))
            throw new RuntimeException("can not find res Dir :" + resDir.getAbsolutePath());

        File valuesDir = new File(resDir, "values");
        if (valuesDir == null || !valuesDir.exists())
            throw new RuntimeException("can not find values Dir :" + valuesDir.getAbsolutePath());

        File valueFile = new File(valuesDir, translateFileName);
        if (valueFile == null || !valueFile.exists())
            throw new RuntimeException(translateFile.getAbsolutePath() + "     " + "can not find values file :" + valueFile.getAbsolutePath());
        List<BaseEntity> lines = FileReaderEx.readAllStringToList(valueFile);
        mValuesData.put(translateFileName, lines);
    }

    @Override
    public void doAddTranslate(File translateFile, File originDir) {
        File valueXXFile = new File(originDir, translateFile.getName());
        if (valueXXFile.exists()) {
            try {
                valueXXFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<BaseEntity> lines = mValuesData.get(translateFile.getName());
        if (lines.isEmpty())
            throw new RuntimeException("can not get values file: " + translateFile.getName());

        if (!lines.isEmpty()) {
            Map<String, StrEntity> transMap = FileReaderEx.readStringToMap(translateFile);
            Map<String, StrEntity> valueXXMap = FileReaderEx.readStringToMap(valueXXFile);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueXXFile)))) {
                writerHeader(bw);
                for (BaseEntity entity : lines) {
                    String line = entity.getLineText();
                    if (entity instanceof StrEntity) {
                        StrEntity strEntity = (StrEntity) entity;
                        String stringKey = strEntity.getStringKey();
                        if (transMap.containsKey(stringKey)) {
                            line = transMap.get(stringKey).getLineText();
                        } else if (valueXXMap.containsKey(stringKey)) {
                            line = valueXXMap.get(stringKey).getLineText();
                        } else if (!strEntity.isNeedTranslate())
                            continue;
                    }
                    line = ReplaceSpecialCharUtils.replaceSpecialChar(line);
                    bw.write(line);
                }
                writerFooter(bw);
            } catch (IOException e) {
                e.printStackTrace();
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


    private void endThreadPool() {
        if (mTaskList.isEmpty())
            return;
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    int i = 0;
                    try {
                        for (Task task : mTaskList) {
                            if (task.isDone()) {
                                i++;
                                continue;
                            } else
                                break;
                        }

                        if (i == mTaskList.size() && mFixedThreadPool != null) {
                            mFixedThreadPool.shutdownNow();
                            mFixedThreadPool = null;
                            System.out.println("      FixedThreadPool.shutdownNow()      ");
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
