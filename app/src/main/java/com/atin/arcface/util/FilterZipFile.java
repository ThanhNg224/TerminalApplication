package com.atin.arcface.util;

import java.io.File;
import java.io.FilenameFilter;

public class FilterZipFile implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".zip");
    }
}
