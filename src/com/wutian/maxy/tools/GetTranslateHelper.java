package com.wutian.maxy.tools;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetTranslateHelper {
    private File outFile;
    private File projectFile;

    public GetTranslateHelper(File projectFile, File outFile) {
        this.outFile = outFile;
        this.projectFile = projectFile;
    }

    public void startGet() {
        mainFileCheck();
        File[] projectSubFile = projectFile.listFiles(FileFilters.sFileFilters);
        List<File> files = new ArrayList<>();
        for (File subFile : projectSubFile) {
            files.add(subFile);
        }
        printFileDir(files);
    }

    private void printFileDir(List<File> files) {
        File textFile = new File(outFile, "text.xml");
        if (textFile.exists()) {
            try {
                textFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter fileWriter = new FileWriter(textFile)){
            for (File file : files) {
                fileWriter.write(file.getName() + "       " + file.getAbsolutePath());
                fileWriter.write("\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mainFileCheck() {
        if (projectFile == null || !projectFile.exists())
            throw new RuntimeException("GetTranslateHelper projectFile error");

        if (outFile == null || !outFile.exists())
            throw new RuntimeException("GetTranslateHelper projectFile error");

        for (File file : outFile.listFiles())
            file.delete();
    }

}
