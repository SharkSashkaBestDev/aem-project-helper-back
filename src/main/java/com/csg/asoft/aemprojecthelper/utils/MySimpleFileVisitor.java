package com.csg.asoft.aemprojecthelper.utils;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class MySimpleFileVisitor extends SimpleFileVisitor<Path> {

    private List<Path> allFiles;

    public MySimpleFileVisitor(final List<Path> allFiles) {
        super();
        this.allFiles = allFiles;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isDirectory() && !file.toAbsolutePath().toString().contains("/target/") && file.getFileName().toString().equals(".content.xml")) {
            if (containsText(String.valueOf(file.toAbsolutePath()))) {
                this.allFiles.add(file.toAbsolutePath());
            }
        }

        return FileVisitResult.CONTINUE;
    }

    private boolean containsText(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        int count = 0;

        while ((line = br.readLine()) != null) {
            if (!StringUtils.isEmpty(line) && line.contains("jcr:primaryType=\"cq:Component\"") ||
                    line.contains("sling:resourceSuperType=")) {
                count++;
            }
        }
        return (count > 0);
    }
}
