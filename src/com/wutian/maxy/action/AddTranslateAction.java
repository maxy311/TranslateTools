package com.wutian.maxy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.wutian.maxy.tools.add.AddTranslateHelper;

import java.io.File;

public class AddTranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

//        String translatePath = Messages.showInputDialog(project,
//                "Place Input Translate Path:",
//                "Translate Path:",
//                Messages.getQuestionIcon());
//
//        File translateFile = new File(translatePath);
//        if (!translateFile.exists())
//            throw new RuntimeException("Translate File not exist: " + translateFile.getAbsolutePath());
//

        File projectFile = new File(project.getBasePath());
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper(new File("/Users/maxy/Android/workspace/SHAREit"), new File("/Users/maxy/Desktop/SHAREit_Translate"));
        addTranslateHelper.start();
    }
}
