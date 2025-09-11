
package com.tienda.controlador;

import com.tienda.modelo.Proveedor;
import com.tienda.modelo.ModeloProveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.Optional;

public class ControladorProveedores {
    @FXML private TextField campoBusqueda;
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private TextField campoNombre;
    @FXML private TextField campoDireccion;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoCuit;
    @FXML private RadioButton radioActivo;
    @FXML private RadioButton radioInactivo;
    @FXML private ToggleGroup estadoToggleGroup;
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> columnaNombre;
    @FXML private TableColumn<Proveedor, String> columnaDireccion;
    @FXML private TableColumn<Proveedor, String> columnaEmail;
    @FXML private TableColumn<Proveedor, String> columnaTelefono;
    @FXML private TableColumn<Proveedor, String> columnaCuit;
    @FXML private TableColumn<Proveedor, String> columnaEstado;
    @FXML private javafx.scene.control.Button botonNuevo;
    @FXML private javafx.scene.control.Button botonGuardar;
    @FXML private javafx.scene.control.Button botonEditar;
    @FXML private javafx.scene.control.Button botonEliminar;
    @FXML private javafx.scene.control.Button botonCancelar;

    private ModeloProveedor modeloProveedor;
    private Proveedor proveedorSeleccionado;
    private ObservableList<Proveedor> proveedores;
    private FilteredList<Proveedor> filteredProveedores;
    private boolean esNuevoProveedor;
    private static final PseudoClass INACTIVO_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactivo");

    @FXML
    private void initialize() {
        modeloProveedor = new ModeloProveedor();
        proveedores = FXCollections.observableArrayList();
        filteredProveedores = new FilteredList<>(proveedores, p -> true);
        configurarColumnas();
        cargarProveedores();
        configurarBusquedaYFiltro();
        configurarResaltadoInactivos();
        radioActivo.setSelected(true);
        comboFiltroEstado.getItems().addAll("Todos", "Activos", "Inactivos");
        comboFiltroEstado.setValue("Todos");
        esNuevoProveedor = true;

        deshabilitarFormulario();
        botonNuevo.setDisable(false);

        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            proveedorSeleccionado = newSelection;
            if (proveedorSeleccionado != null) {
                cargarProveedorEnFormulario();
                botonEditar.setDisable(false);
                botonEliminar.setDisable(false);
                botonGuardar.setDisable(true);
            } else {
                limpiarCampos();
                deshabilitarFormulario();
                botonEditar.setDisable(true);
                botonEliminar.setDisable(true);
                botonGuardar.setDisable(true);
            }
        });
    }

    private void configurarColumnas() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnaCuit.setCellValueFactory(new PropertyValueFactory<>("cuit"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarProveedores() {
        try {
            proveedores.setAll(modeloProveedor.obtenerProveedores());
            tablaProveedores.setItems(filteredProveedores);
        } catch (SQLException e) {
            mostrarError("Error al cargar los proveedores: " + e.getMessage());
        }
    }

    private void configurarBusquedaYFiltro() {
        campoBusqueda.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        comboFiltroEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        actualizarFiltro();
    }

    private void configurarResaltadoInactivos() {
        tablaProveedores.setRowFactory(tv -> new TableRow<Proveedor>() {
            @Override
            protected void updateItem(Proveedor proveedor, boolean empty) {
                super.updateItem(proveedor, empty);
                pseudoClassStateChanged(INACTIVO_PSEUDO_CLASS, proveedor != null && !empty && proveedor.getEstado().equals("Inactivo"));
            }
        });
    }

    private void actualizarFiltro() {
        filteredProveedores.setPredicate(proveedor -> {
            boolean busquedaValida = true;
            String terminoBusqueda = campoBusqueda.getText();
            if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
                String lowerCaseFilter = terminoBusqueda.toLowerCase();
                busquedaValida = proveedor.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                                 proveedor.getEmail().toLowerCase().contains(lowerCaseFilter);
            }

            boolean estadoValido = true;
            String filtroEstado = comboFiltroEstado.getValue();
            if (filtroEstado != null && !filtroEstado.equals("Todos")) {
                estadoValido = proveedor.getEstado().equals(filtroEstado.equals("Activos") ? "Activo" : "Inactivo");
            }

            return busquedaValida && estadoValido;
        });
    }

    @FXML
    private void nuevoProveedor() {
        esNuevoProveedor = true;
        habilitarFormulario();
        limpiarCampos();
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
        botonNuevo.setDisable(false);
    }

    @FXML
    private void buscarProveedor() {
        cargarProveedores();
        actualizarFiltro();
    }

    @FXML
    private void guardarProveedor() {
        try {
            String nombre = campoNombre.getText().trim();
            String direccion = campoDireccion.getText().trim();
            String email = campoEmail.getText().trim();
            String telefono = campoTelefono.getText().trim();
            String cuit = campoCuit.getText().trim();

            // Validar campos vacíos
            if (nombre.isEmpty() || direccion.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                mostrarError("Complete todos los campos obligatorios (Nombre, Dirección, Email, Teléfono).");
                return;
            }

            // Validar email formato simple
            if (!email.matches("^[\\w-_.+]+@[\\w-]+\\.[a-z]{2,}$")) {
                mostrarError("El email no tiene un formato válido.");
                return;
            }

            // Verificar unicidad de email
            if (esNuevoProveedor && modeloProveedor.existeEmailProveedor(email, 0)) {
                mostrarError("El email '" + email + "' ya existe.");
                return;
            } else if (!esNuevoProveedor && modeloProveedor.existeEmailProveedor(email, proveedorSeleccionado.getId())) {
                mostrarError("El email '" + email + "' ya está en uso por otro proveedor.");
                return;
            }

            // Crear o actualizar proveedor
            Proveedor proveedor = esNuevoProveedor ? new Proveedor() : proveedorSeleccionado;
            proveedor.setNombre(nombre);
            proveedor.setDireccion(direccion);
            proveedor.setEmail(email);
            proveedor.setTelefono(telefono);
            proveedor.setCuit(cuit.isEmpty() ? null : cuit);
            proveedor.setEstado(radioActivo.isSelected() ? "Activo" : "Inactivo");

            if (esNuevoProveedor) {
                modeloProveedor.agregarProveedor(proveedor);
                mostrarInfo("Proveedor agregado exitosamente.");
            } else {
                modeloProveedor.actualizarProveedor(proveedor);
                mostrarInfo("Proveedor actualizado exitosamente.");
            }

            cargarProveedores();
            limpiarCampos();
            deshabilitarFormulario();
            proveedorSeleccionado = null;
            esNuevoProveedor = true;
            botonNuevo.setDisable(false);
        } catch (SQLException e) {
            mostrarError("Error al " + (esNuevoProveedor ? "agregar" : "actualizar") + " el proveedor: " + e.getMessage());
        }
    }

    @FXML
    private void editarProveedor() {
        if (proveedorSeleccionado == null) {
            mostrarError("Seleccione un proveedor de la tabla.");
            return;
        }
        esNuevoProveedor = false;
        habilitarFormulario();
        botonNuevo.setDisable(false);
    }

    @FXML
    private void eliminarProveedor() {
        if (proveedorSeleccionado == null) {
            mostrarError("Seleccione un proveedor de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este proveedor?");
        confirmacion.setContentText("Nombre: " + proveedorSeleccionado.getNombre());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                modeloProveedor.eliminarProveedor(proveedorSeleccionado.getId());
                cargarProveedores();
                limpiarCampos();
                deshabilitarFormulario();
                proveedorSeleccionado = null;
                esNuevoProveedor = true;
                botonNuevo.setDisable(false);
            } catch (SQLException e) {
                mostrarError("Error al eliminar el proveedor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelar() {
        limpiarCampos();
        deshabilitarFormulario();
        proveedorSeleccionado = null;
        esNuevoProveedor = true;
        botonNuevo.setDisable(false);
    }

    private void cargarProveedorEnFormulario() {
        campoNombre.setText(proveedorSeleccionado.getNombre());
        campoDireccion.setText(proveedorSeleccionado.getDireccion());
        campoEmail.setText(proveedorSeleccionado.getEmail());
        campoTelefono.setText(proveedorSeleccionado.getTelefono());
        campoCuit.setText(proveedorSeleccionado.getCuit());
        radioActivo.setSelected(proveedorSeleccionado.getEstado().equals("Activo"));
        radioInactivo.setSelected(proveedorSeleccionado.getEstado().equals("Inactivo"));
    }

    private void limpiarCampos() {
        campoBusqueda.clear();
        campoNombre.clear();
        campoDireccion.clear();
        campoEmail.clear();
        campoTelefono.clear();
        campoCuit.clear();
        radioActivo.setSelected(true);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void deshabilitarFormulario() {
        campoNombre.setDisable(true);
        campoDireccion.setDisable(true);
        campoEmail.setDisable(true);
        campoTelefono.setDisable(true);
        campoCuit.setDisable(true);
        radioActivo.setDisable(true);
        radioInactivo.setDisable(true);
        botonGuardar.setDisable(true);
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
    }

    private void habilitarFormulario() {
        campoNombre.setDisable(false);
        campoDireccion.setDisable(false);
        campoEmail.setDisable(false);
        campoTelefono.setDisable(false);
        campoCuit.setDisable(false);
        radioActivo.setDisable(false);
        radioInactivo.setDisable(false);
        botonGuardar.setDisable(false);
    }
}
