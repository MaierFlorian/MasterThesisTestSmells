package com.github.MaierFlorian.testsmelldetection.parsing;

import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextFromFileExtractor {

    /**
     * Reads the whole content from a file.
     * @param path
     * @return the content of a file.
     */
    public String getFileContent(String path){
        String content = null;
        try {
            content = Files.readString(Paths.get(path));
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Successfully loaded " + path);
        } catch (IOException e) {
            ControllerFromOutside.writeToLog("[ERROR - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Could not load " + path);
        }
        return content;
    }

    /**
     * Reads the whole content from a file but removes all java comments.
     * @param path
     * @return the content of a file without java comments.
     */
    public String getFileContentWithoutComments(String path){
        String content = getFileContent(path);
        // remove lines starting with "//"
        content = content.replaceAll("(?<=;|\s) *//.*", "");
        // remove everything between multiline comment
        content = content.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", "");
        return content;
    }
}
