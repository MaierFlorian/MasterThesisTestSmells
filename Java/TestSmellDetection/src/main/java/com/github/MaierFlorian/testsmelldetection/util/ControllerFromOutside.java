package com.github.MaierFlorian.testsmelldetection.util;

import com.github.MaierFlorian.testsmelldetection.gui.HomeController;

public class ControllerFromOutside {

    public static HomeController homeController;

    /**
     * Prints text to a textArea visible on the GUI after clicking the 'Start' button.
     * @param text
     */
    public static void writeToLog(String text){
        Thread thread = new Thread(){
            @Override
            public void run(){
                homeController.writeToStartLogger(text);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables the download Button on the GUI so that the user can download the results of the test smell detection.
     * Should only be enabled after the detection algorithms finished.
     */
    public static void enableDownloadButtonAndResetButton(){
        homeController.enableDownloadCSVButton();
        homeController.enableResetButton();
    }

}
