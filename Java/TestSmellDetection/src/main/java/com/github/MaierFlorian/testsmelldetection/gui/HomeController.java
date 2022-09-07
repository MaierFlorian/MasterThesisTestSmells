package com.github.MaierFlorian.testsmelldetection.gui;

import com.github.MaierFlorian.testsmelldetection.data.Configuration;
import com.github.MaierFlorian.testsmelldetection.logic.SmellDetector;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class HomeController {

    @FXML
    private Label detectorSelectFileLabel;
    @FXML
    private Label detectorOptionLabel;
    @FXML
    private Label detectorStartLabel;

    @FXML
    private TextArea detectorStartTextArea;

    @FXML
    private ListView detectorFileList;

    @FXML
    private Pane overviewPane;
    @FXML
    private Pane overviewTestSmellsPane;
    @FXML
    private Pane TestSmellsATPane;
    @FXML
    private Pane TestSmellsLTPane;
    @FXML
    private Pane TestSmellsCTLPane;
    @FXML
    private Pane TestSmellsARPane;
    @FXML
    private Pane TestSmellsRGTPane;
    @FXML
    private Pane overviewToolUsagePane;
    @FXML
    private Pane detectorPane;
    @FXML
    private Pane detectorFilePane;
    @FXML
    private Pane detectorOptionPane;
    @FXML
    private Pane detectorStartPane;

    @FXML
    private Label overviewTestSmellsLabel;
    @FXML
    private Label overviewToolUsageLabel;
    @FXML
    private Label testSmellsATLabel;
    @FXML
    private Label testSmellsLTLabel;
    @FXML
    private Label testSmellsCTLLabel;
    @FXML
    private Label testSmellsARLabel;
    @FXML
    private Label testSmellsRGTLabel;

    @FXML
    private Button overviewButton;
    @FXML
    private Button detectorButton;
    @FXML
    private Button detectorNextButton;
    @FXML
    private Button detectorPrevButton;
    @FXML
    private Button detectorStartButton;
    @FXML
    private Button downloadCSVButton;
    @FXML
    private Button detectorResetButton;

    @FXML
    private ToggleButton OptionATButton;
    @FXML
    private ToggleButton OptionATButton1;
    @FXML
    private ToggleButton OptionATButton2;
    @FXML
    private ToggleButton OptionATButton3;
    @FXML
    private ToggleButton OptionATButton4;
    @FXML
    private ToggleButton OptionATButton5;
    @FXML
    private ToggleButton OptionLTButton;
    @FXML
    private ToggleButton OptionCTLButton;
    @FXML
    private ToggleButton OptionCTLButton1;
    @FXML
    private ToggleButton OptionRGTButton;
    @FXML
    private ToggleButton OptionARButton;
    @FXML
    private ToggleButton OptionETButton;
    @FXML
    private ToggleButton OptionLzTButton;

    @FXML
    private TextField linesNumberInput;
    @FXML
    private TextField assertionsNumberInput;


    private int detectorIndex = 0;
    private final int DETECTOR_INDEX_MAX = 2;


    @FXML
    public void initialize() {
        // force the field to be numeric only
        linesNumberInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    linesNumberInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue == ""){
                    linesNumberInput.setText("1");
                }
            }
        });
        assertionsNumberInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    assertionsNumberInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue == ""){
                    assertionsNumberInput.setText("1");
                }
            }
        });
    }

    @FXML
    protected void onExitButtonClick(){
        Platform.exit();
    }
    @FXML
    protected void onOverviewButtonClick() {
        overviewPane.setVisible(true);
        detectorPane.setVisible(false);

        overviewButton.setStyle("-fx-background-color: #95939b");
        detectorButton.setStyle("-fx-background-color: #86838e");
    }
    @FXML
    protected void onDetectorButtonClick() {
        overviewPane.setVisible(false);
        detectorPane.setVisible(true);

        overviewButton.setStyle("-fx-background-color: #86838e");
        detectorButton.setStyle("-fx-background-color: #95939b");
    }
    @FXML
    protected void onValidatorButtonClick() {
        overviewPane.setVisible(false);
        detectorPane.setVisible(false);

        overviewButton.setStyle("-fx-background-color: #86838e");
        detectorButton.setStyle("-fx-background-color: #86838e");
    }
    @FXML
    protected void onDetectorNextButtonClick() {
        if(detectorIndex < DETECTOR_INDEX_MAX) {
            detectorIndex++;
            detectionPaneIndex(detectorIndex);
        }
    }
    @FXML
    protected void onDetectorPrevButtonClick() {
        if(detectorIndex > 0) {
            detectorIndex--;
            detectionPaneIndex(detectorIndex);
        }
    }

    private void detectionPaneIndex(int detectorIndex){
        switch (detectorIndex){
            case 0:
                detectorPrevButton.setDisable(true);
                detectorNextButton.setDisable(false);
                detectorStartButton.setDisable(true);
                detectorFilePane.setVisible(true);
                detectorOptionPane.setVisible(false);
                detectorStartPane.setVisible(false);
                detectorSelectFileLabel.setStyle("-fx-background-color: #7b7a7f");
                detectorOptionLabel.setStyle("-fx-background-color: #555459");
                detectorStartLabel.setStyle("-fx-background-color: #555459");
                break;
            case 1:
                detectorPrevButton.setDisable(false);
                if(detectorFileList.getItems().size() == 0)
                    detectorNextButton.setDisable(true);
                else
                    detectorNextButton.setDisable(false);
                detectorStartButton.setDisable(true);
                detectorFilePane.setVisible(false);
                detectorOptionPane.setVisible(true);
                detectorStartPane.setVisible(false);
                detectorSelectFileLabel.setStyle("-fx-background-color: #555459");
                detectorOptionLabel.setStyle("-fx-background-color: #7b7a7f");
                detectorStartLabel.setStyle("-fx-background-color: #555459");
                break;
            case 2:
                detectorPrevButton.setDisable(false);
                detectorNextButton.setDisable(true);
                detectorStartButton.setDisable(false);
                detectorFilePane.setVisible(false);
                detectorOptionPane.setVisible(false);
                detectorStartPane.setVisible(true);
                detectorSelectFileLabel.setStyle("-fx-background-color: #555459");
                detectorOptionLabel.setStyle("-fx-background-color: #555459");
                detectorStartLabel.setStyle("-fx-background-color: #7b7a7f");
                Configuration.getInstance().setNumberLines(Integer.parseInt(linesNumberInput.getText()));
                break;
        }
    }

    @FXML
    protected void onDetectorFileButtonClick(){
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java Files", "*.java"));
        List<File> selectedFiles = fc.showOpenMultipleDialog(null);

        if (selectedFiles != null){
            for (int i = 0; i < selectedFiles.size(); i++) {
                detectorFileList.getItems().add(selectedFiles.get(i).getAbsolutePath());
                Configuration.getInstance().addFile(selectedFiles.get(i));
            }
        }
    }

    @FXML
    protected void onDetectorFileClearButtonClick(){
        detectorFileList.getItems().clear();
        Configuration.getInstance().clearFiles();
    }

    @FXML
    protected void onOptionATButtonClick(){
        changeOptionButton(OptionATButton);
        Configuration.getInstance().setAnonymousTest(OptionATButton.isSelected());
    }
    @FXML
    protected void onOptionATButton1Click(){
        changeOptionButton(OptionATButton1);
        Configuration.getInstance().setTest_methodname(OptionATButton1.isSelected());
    }
    @FXML
    protected void onOptionATButton2Click(){
        changeOptionButton(OptionATButton2);
        Configuration.getInstance().setMethod_state_behaviour(OptionATButton2.isSelected());
    }
    @FXML
    protected void onOptionATButton3Click(){
        changeOptionButton(OptionATButton3);
        Configuration.getInstance().setSpecificRequirement(OptionATButton3.isSelected());
    }
    @FXML
    protected void onOptionATButton4Click(){
        changeOptionButton(OptionATButton4);
        Configuration.getInstance().setInputStateWorkflow_output(OptionATButton4.isSelected());
    }
    @FXML
    protected void onOptionATButton5Click(){
        changeOptionButton(OptionATButton5);
        Configuration.getInstance().setAction_conditionState(OptionATButton5.isSelected());
    }
    @FXML
    protected void onOptionLTButtonClick(){
        changeOptionButton(OptionLTButton);
        Configuration.getInstance().setLongTest(OptionLTButton.isSelected());
    }
    @FXML
    protected void onOptionCTLButtonClick(){
        changeOptionButton(OptionCTLButton);
        Configuration.getInstance().setConditionalTestLogic(OptionCTLButton.isSelected());
    }
    @FXML
    protected void onOptionCTLButton1Click(){
        changeOptionButton(OptionCTLButton1);
        Configuration.getInstance().setOnlyPrint(OptionCTLButton1.isSelected());
    }
    @FXML
    protected void onOptionRGTButtonClick(){
        changeOptionButton(OptionRGTButton);
        Configuration.getInstance().setRottenGreenTest(OptionRGTButton.isSelected());
    }
    @FXML
    protected void onOptionARButtonClick(){
        changeOptionButton(OptionARButton);
        Configuration.getInstance().setAssertionRoulette(OptionARButton.isSelected());
    }
    @FXML
    protected void onOptionETButtonClick(){
        changeOptionButton(OptionETButton);
        Configuration.getInstance().setEagerTest(OptionETButton.isSelected());
    }
    @FXML
    protected void onOptionLzTButtonClick(){
        changeOptionButton(OptionLzTButton);
        Configuration.getInstance().setLazyTest(OptionLzTButton.isSelected());
    }

    private void changeOptionButton(ToggleButton tb){
        if(tb.isSelected()){
            tb.setStyle("-fx-background-radius: 30; -fx-background-color: green");
            tb.setText("On");
        }
        else{
            tb.setStyle("-fx-background-radius: 30; -fx-background-color: red");
            tb.setText("Off");
        }
    }

    @FXML
    protected void onLinesNumberInput(){
        if(linesNumberInput.getText() != "")
            Configuration.getInstance().setNumberLines(Integer.parseInt(linesNumberInput.getText()));
    }

    @FXML
    protected void onAssertionsNumberInput(){
        if(assertionsNumberInput.getText() != "")
            Configuration.getInstance().setNumberAssertions(Integer.parseInt(assertionsNumberInput.getText()));
    }

    @FXML
    protected void onStartButtonClick(){
        detectorPrevButton.setDisable(true);
        detectorNextButton.setDisable(true);
        detectorStartButton.setDisable(true);
        detectorResetButton.setDisable(true);

        Thread thread = new Thread(){
            @Override
            public void run(){
                SmellDetector.getInstance().executeSmellDetector();
            }
        };
        thread.start();
    }

    public void enableDownloadCSVButton(){
        downloadCSVButton.setDisable(false);
    }

    public void enableResetButton(){
        detectorResetButton.setDisable(false);
    }

    @FXML
    protected void onDownloadCSVButtonClick(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        SmellDetector.getInstance().createCSV(selectedDirectory.getAbsolutePath());
    }

    public void writeToStartLogger(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                detectorStartTextArea.appendText("\n" + text);
            }
        });
    }

    @FXML
    public void onDetectorResetButtonClick(){
        detectorIndex = 0;
        detectorPrevButton.setDisable(true);
        detectorNextButton.setDisable(false);
        detectorStartButton.setDisable(true);
        detectorFilePane.setVisible(true);
        detectorOptionPane.setVisible(false);
        detectorStartPane.setVisible(false);
        detectorSelectFileLabel.setStyle("-fx-background-color: #7b7a7f");
        detectorOptionLabel.setStyle("-fx-background-color: #555459");
        detectorStartLabel.setStyle("-fx-background-color: #555459");
        downloadCSVButton.setDisable(true);
        detectorStartTextArea.setText("");

        onDetectorFileClearButtonClick();
    }

    @FXML
    public void onTestSmellButtonClick(){
        overviewTestSmellsLabel.setStyle("-fx-background-color: #7b7a7f");
        overviewToolUsageLabel.setStyle("-fx-background-color: #555459");
        overviewTestSmellsPane.setVisible(true);
        overviewToolUsagePane.setVisible(false);
    }

    @FXML
    public void onToolUsageButtonClick(){
        overviewTestSmellsLabel.setStyle("-fx-background-color: #555459");
        overviewToolUsageLabel.setStyle("-fx-background-color: #7b7a7f");
        overviewTestSmellsPane.setVisible(false);
        overviewToolUsagePane.setVisible(true);
    }

    @FXML
    public void onTestSmellATButtonClick(){
        testSmellsATLabel.setStyle("-fx-background-color: #555459; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsLTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsCTLLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsARLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsRGTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        TestSmellsATPane.setVisible(true);
        TestSmellsLTPane.setVisible(false);
        TestSmellsCTLPane.setVisible(false);
        TestSmellsARPane.setVisible(false);
        TestSmellsRGTPane.setVisible(false);
    }

    @FXML
    public void onTestSmellLTButtonClick(){
        testSmellsATLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsLTLabel.setStyle("-fx-background-color: #555459; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsCTLLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsARLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsRGTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        TestSmellsATPane.setVisible(false);
        TestSmellsLTPane.setVisible(true);
        TestSmellsCTLPane.setVisible(false);
        TestSmellsARPane.setVisible(false);
        TestSmellsRGTPane.setVisible(false);
    }

    @FXML
    public void onTestSmellCTLButtonClick(){
        testSmellsATLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsLTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsCTLLabel.setStyle("-fx-background-color: #555459; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsARLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsRGTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        TestSmellsATPane.setVisible(false);
        TestSmellsLTPane.setVisible(false);
        TestSmellsCTLPane.setVisible(true);
        TestSmellsARPane.setVisible(false);
        TestSmellsRGTPane.setVisible(false);
    }

    @FXML
    public void onTestSmellARButtonClick(){
        testSmellsATLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsLTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsCTLLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsARLabel.setStyle("-fx-background-color: #555459; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsRGTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        TestSmellsATPane.setVisible(false);
        TestSmellsLTPane.setVisible(false);
        TestSmellsCTLPane.setVisible(false);
        TestSmellsARPane.setVisible(true);
        TestSmellsRGTPane.setVisible(false);
    }

    @FXML
    public void onTestSmellRGTButtonClick(){
        testSmellsATLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsLTLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsCTLLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsARLabel.setStyle("-fx-background-color: #7b7a7f; -fx-border-color: #555459; -fx-border-radius: 2px;");
        testSmellsRGTLabel.setStyle("-fx-background-color: #555459; -fx-border-color: #555459; -fx-border-radius: 2px;");
        TestSmellsATPane.setVisible(false);
        TestSmellsLTPane.setVisible(false);
        TestSmellsCTLPane.setVisible(false);
        TestSmellsARPane.setVisible(false);
        TestSmellsRGTPane.setVisible(true);
    }
}