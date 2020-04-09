package com.wutian.maxy.tools;

import java.io.File;
import java.io.FileFilter;

public class FileFilters {
    public static FileFilter sFileFilters = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isHidden())
                return false;
            String fileName = file.getName();
            if (fileName.endsWith(".sh"))
                return false;
            else if (fileName.endsWith(".properties"))
                return false;
            else if (fileName.endsWith(".gradle"))
                return false;
            else if (fileName.endsWith(".bat"))
                return false;
            return true;
        }
    };
}
