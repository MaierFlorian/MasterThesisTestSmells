module com.github.MaierFlorian.testsmelldetection {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.javaparser.symbolsolver.core;
    requires com.github.javaparser.core;
    requires opencsv;
    requires org.apache.opennlp.tools;


    opens com.github.MaierFlorian.testsmelldetection to javafx.fxml;
    exports com.github.MaierFlorian.testsmelldetection.gui;
    opens com.github.MaierFlorian.testsmelldetection.gui to javafx.fxml;
}