
package com.tienda.app;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/VistaPrincipal.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/css/estilos.css").toExternalForm());  
        primaryStage.setTitle("Sistema de Venta de Ropa de Mujeres");
        primaryStage.setMaximized(true);
        
         // Agregar el ícono a la ventana
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/icono.jpg")));
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
