/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mplayer;
 
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.animation.FadeTransition;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.layout.*;
 
/**
 *
 * @author Dawid Bitner & Marcin Krupa
 */
 
public class FXMLDocumentController implements Initializable {

    static Parent load(URL resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static Parent load(String fxmlDocumentControllerjava) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    private String hrs, sec, min, hrsFull, minFull, secFull, fullTime;
    private String filePath;
    public MediaPlayer mediaPlayer = null;
    private File [] listOfFiles;
    private ArrayList<String> filesURI = new ArrayList<>();
    
    public int index = 0, stackSize = 0;
    
    @FXML
    private Label displayTime;
    
    @FXML
    private Label currentlyPlaying;
    
    @FXML
    private Pane descriptionPanel;
    
    @FXML
    private ListView<String> playlist = new ListView<>();
    
    @FXML
    private MediaView mediaView;
   
    @FXML
    private Slider volumeSlider;
   
    @FXML
    private Slider progSlider;

    @FXML
    private Button fsButton;
    
    @FXML
    protected boolean isPlaying = false;
    
    @FXML
    private VBox controlBar;
    
    @FXML
    protected boolean checkResolution () {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        
        return (width/height)<1.7;
    }
    
    @FXML
    private void playFile(String file) {
        playlist.setVisible(false);
        if (isPlaying == true) mediaPlayer.dispose();
        isPlaying=true;
        Media media = new Media(file);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        if (checkResolution()) mediaView.setY(50.0);
           
        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty hight = mediaView.fitHeightProperty();
                   
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
        hight.bind(Bindings.selectDouble(mediaView.sceneProperty(),"hight"));
        
        mediaPlayer.play();
           
        progSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            progSlider.setMax(mediaPlayer.getTotalDuration().toMillis());
        });
           
        volumeSlider.setValue(mediaPlayer.getVolume()*100);
           
        volumeSlider.valueProperty().addListener((Observable observable) -> {
            mediaPlayer.setVolume(volumeSlider.getValue()/100);
        });
           
        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            progSlider.setValue(newValue.toMillis());
        });

        progSlider.setOnMousePressed((MouseEvent event1) -> {
            mediaPlayer.pause();
        });
            
        progSlider.setOnMouseClicked((MouseEvent event1) -> {
            mediaPlayer.seek(Duration.millis(progSlider.getValue()));
            mediaPlayer.play();
        });

        progSlider.setOnMouseReleased((MouseEvent event1) -> {
            mediaPlayer.seek(Duration.millis(progSlider.getValue()));
            mediaPlayer.play();
        });
            
        mediaPlayer.currentTimeProperty().addListener((observable) -> {
            hrsFull = String.valueOf(((int) mediaPlayer.getTotalDuration().toHours()));
            if (Integer.valueOf(hrsFull)  < 10) hrsFull="0"+hrsFull;
            minFull = String.valueOf(((int) mediaPlayer.getTotalDuration().toMinutes())%60);
            if (Integer.valueOf(minFull)  < 10) minFull="0"+minFull;
            secFull = String.valueOf(((int) mediaPlayer.getTotalDuration().toSeconds())%60);
            if (Integer.valueOf(secFull)  < 10) secFull="0"+secFull;
            fullTime = String.format("%s:%s:%s", hrsFull, minFull, secFull);
        });
            
        mediaPlayer.currentTimeProperty().addListener((Observable observable) -> {
            hrs = String.valueOf(((int) mediaPlayer.getCurrentTime().toHours()));
            if (Integer.valueOf(hrs) < 10) hrs="0"+hrs;
            min = String.valueOf(((int) mediaPlayer.getCurrentTime().toMinutes())%60);
            if (Integer.valueOf(min) < 10) min="0"+min;
            sec = String.valueOf(((int) mediaPlayer.getCurrentTime().toSeconds())%60);
            if (Integer.valueOf(sec) < 10) sec="0"+sec;
                        
            displayTime.setText(hrs+":"+min+":"+sec+"/"+fullTime);
        });

    }
    
    @FXML
    private void addSingleFile(ActionEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select mp4 file", "*.mp4", "*.mp3");
               
        fileChooser.getExtensionFilters().add(filter);
        
        File file = fileChooser.showOpenDialog(null);
        
        playlist.setCellFactory(TextFieldListCell.forListView());
        playlist.getItems().add(index, file.getName());
        filesURI.add(file.toURI().toString());
        index++; 
    }
    
    @FXML
    private void addFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(null);
        
        //filter na rozszerzenia plików bierze pod uwagę tylko .mp3 i .mp4
        listOfFiles = folder.listFiles((File folder1, String name) -> {
            return name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".mp4");
        } );
        
        playlist.setCellFactory(TextFieldListCell.forListView());
        
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                playlist.getItems().add(index, listOfFile.getName());
                filesURI.add(listOfFile.toURI().toString());
                index++;
            }
        }
        System.out.println(playlist.getItems().get(2));
    }
    
    @FXML
    public void selectItem(MouseEvent click) {
        if (click.getClickCount() == 2) {
            
           int currentItemSelected = playlist.getSelectionModel().getSelectedIndex();
           
           currentlyPlaying.setText(playlist.getSelectionModel().getSelectedItem());
           playFile(filesURI.get(currentItemSelected));
        }
    }
    
    @FXML
    private void pause(ActionEvent event){
        isPlaying = false;
        mediaPlayer.pause();
    }
   
    @FXML
    private void play(ActionEvent event){
        isPlaying = true;
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }
   
    @FXML
    private void stop(ActionEvent event){
        isPlaying = false;
        mediaPlayer.stop();
    }
   
    @FXML
    private void fast(ActionEvent event){
        mediaPlayer.setRate(1.5);
    }    
 
    @FXML
    private void faster(ActionEvent event){
        mediaPlayer.setRate(2);
    }
   
    @FXML
    private void slow(ActionEvent event){
        mediaPlayer.setRate(.75);
    }
 
    @FXML
    private void slower(ActionEvent event){
        mediaPlayer.setRate(.5);
    }
    
    
    @FXML
    private void exit(ActionEvent event){
        System.exit(0);
    }
    
    @FXML
    private void fullScreen(ActionEvent event) {
        Stage stage = (Stage) fsButton.getScene().getWindow();
        if (stage.isFullScreen()) stage.setFullScreen(false);
        else {
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
        }
       
    }
    
    @FXML
    private void openListView (ActionEvent event) {
        if (playlist.isVisible()) playlist.setVisible(false);
            else playlist.setVisible(true);
    }
    
    @FXML
    private void cBarOnMouseEntered (MouseEvent event) {
        if (controlBar.getOpacity()==0) {
            FadeTransition ft = new FadeTransition(Duration.millis(150), controlBar);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }
    
    @FXML
    private void cBarOnMouseExited (MouseEvent event) {
        FadeTransition ft = new FadeTransition(Duration.millis(150), controlBar);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
    }
    
    @FXML
    private void PaneOnMouseEntered (MouseEvent event) {
        if (descriptionPanel.getOpacity()==0) {
            FadeTransition ft = new FadeTransition(Duration.millis(150), descriptionPanel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }
    
    @FXML
    private void PaneOnMouseExited (MouseEvent event) {
        FadeTransition ft = new FadeTransition(Duration.millis(150), descriptionPanel);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
    }
    
    @FXML
    private void KeyPressed (KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.SPACE){
            if (isPlaying == false) {
                mediaPlayer.play();
                isPlaying = true;
            } else {
                mediaPlayer.pause();
                isPlaying = false;
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}