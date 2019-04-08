/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Dawid Bitner & Marcin Krupa
 */

public class MPlayer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        AnchorPane root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        FXMLDocumentController controller = new FXMLDocumentController();
        if (controller.checkResolution()) root.setPrefHeight(608.0);
        
        Scene scene = new Scene(root);
        stage.setTitle("MPlayer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/video-player.png")));
        stage.setResizable(false);
        
        scene.setOnMouseClicked((MouseEvent doubleClicked) -> {
            if(doubleClicked.getButton().equals(MouseButton.PRIMARY)){
                if(doubleClicked.getClickCount() == 2){
                    if (stage.isFullScreen() == false) {
                        stage.setFullScreenExitHint("");
                        stage.setFullScreen(true);
                    }
                    else stage.setFullScreen(false);
                }
            }
        });
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    private int getClickCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}