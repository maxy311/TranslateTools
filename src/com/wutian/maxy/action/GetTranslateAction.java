package com.wutian.maxy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.wutian.maxy.tools.GetTranslateHelper;
import com.wutian.maxy.tools.TranslateHelper;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GetTranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        printProjectInfo(project);
//        showDialog(project);

        getTranslate(project);
    }

    private void printProjectInfo(Project project) {
        String name = project.getName();   //TranslateTools
        @SystemIndependent String projectFilePath = project.getProjectFilePath();    ///Users/maxy/Android/IdeaPorjects/TranslateTools/.idea/misc.xml
        @SystemIndependent String basePath = project.getBasePath();   // /Users/maxy/Android/IdeaPorjects/TranslateTools

        System.out.println(name + "   " + projectFilePath + "    " + basePath);
        VirtualFile projectFile = project.getProjectFile();
        String projectFilePath1 = projectFile.getPath();  // /Users/maxy/Android/IdeaPorjects/TranslateTools/.idea/misc.xml
        String name1 = projectFile.getName();   //misc.xml
        System.out.println(name1 + "    " + projectFilePath1);
    }

    private void showDialog(Project project) {
        String text = project.getBasePath() + "    " + project.getName();

        Messages.showMessageDialog(project,
                "Hello, " + text + "!\n I am glad to see you.",
                "Information",
                Messages.getInformationIcon());
    }

    private void getTranslate(Project project) {
        String outPutPath = Messages.showInputDialog(project,
                "Place Input Output Path:",
                "Output Path:",
                Messages.getQuestionIcon());
        @SystemIndependent String basePath = project.getBasePath();
        File file = new File(outPutPath);
        if (!file.exists()) {
            file = new File(basePath + File.pathSeparatorChar + "Translate");
            if (file.exists()) {
                file.mkdir();
            }
        }

        if (!file.exists())
            throw new RuntimeException("Output Path not exist : " + file.getAbsolutePath());

        GetTranslateHelper translateHelper = new GetTranslateHelper(new File(basePath), file);
        translateHelper.startGet();
    }
}
