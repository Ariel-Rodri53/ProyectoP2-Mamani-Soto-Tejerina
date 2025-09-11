package com.tienda.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class ControladorPrincipal {
    @FXML private StackPane panelContenido;
    @FXML private BorderPane panelPrincipal;
    @FXML private Button botonProductos;
    @FXML private Button botonVentas;
    @FXML private Button botonClientes;
    @FXML private Button botonProveedores;
    @FXML private Button botonCompras; // Nuevo botón

    @FXML private void manejarSalir() {
        System.exit(0);
    }

    @FXML private void manejarAcercaDe() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Sistema de Venta de Ropa de Mujeres");
        alert.setContentText("Desarrollado con JavaFX y MySQL");
        alert.showAndWait();
    }

    @FXML private void mostrarGestionProductos() {
        cargarVista("/resources/fxml/VistaGestionProductos.fxml");
    }

    @FXML private void mostrarGestionVentas() {
        cargarVista("/resources/fxml/VistaGestionVentas.fxml");
    }

    @FXML private void mostrarGestionClientes() {
        cargarVista("/resources/fxml/VistaGestionClientes.fxml");
    }

    @FXML private void mostrarGestionProveedores() {
        cargarVista("/resources/fxml/VistaGestionProveedores.fxml");
    }

    @FXML private void mostrarGestionCompras() {
        cargarVista("/resources/fxml/VistaGestionCompras.fxml");
    }

    private void cargarVista(String rutaFxml) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaFxml));
            panelContenido.getChildren().clear();
            panelContenido.getChildren().add(vista);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}