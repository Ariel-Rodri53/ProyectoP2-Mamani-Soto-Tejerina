package com.tienda.controlador;

import com.tienda.modelo.Cliente;
import com.tienda.modelo.ModeloCliente;
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

public class ControladorClientes {
    @FXML private TextField campoBusqueda;
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private TextField campoNombre;
    @FXML private TextField campoApellido;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoDireccion;
    @FXML private RadioButton radioActivo;
    @FXML private RadioButton radioInactivo;
    @FXML private ToggleGroup estadoToggleGroup;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> columnaNombre;
    @FXML private TableColumn<Cliente, String> columnaApellido;
    @FXML private TableColumn<Cliente, String> columnaTelefono;
    @FXML private TableColumn<Cliente, String> columnaDireccion;
    @FXML private TableColumn<Cliente, String> columnaFechaRegistro;
    @FXML private TableColumn<Cliente, String> columnaFechaModificacion;
    @FXML private TableColumn<Cliente, String> columnaEstado;
    @FXML private javafx.scene.control.Button botonNuevo;
    @FXML private javafx.scene.control.Button botonGuardar;
    @FXML private javafx.scene.control.Button botonEditar;
    @FXML private javafx.scene.control.Button botonEliminar;
    @FXML private javafx.scene.control.Button botonCancelar;

    private ModeloCliente modeloCliente;
    private Cliente clienteSeleccionado;
    private ObservableList<Cliente> clientes;
    private FilteredList<Cliente> filteredClientes;
    private boolean esNuevoCliente;
    private static final PseudoClass INACTIVO_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactivo");

    @FXML
    private void initialize() {
        modeloCliente = new ModeloCliente();
        clientes = FXCollections.observableArrayList();
        filteredClientes = new FilteredList<>(clientes, p -> true);
        configurarColumnas();
        cargarClientes();
        configurarBusquedaYFiltro();
        configurarResaltadoInactivos();
        radioActivo.setSelected(true);
        comboFiltroEstado.getItems().addAll("Todos", "Activos", "Inactivos");
        comboFiltroEstado.setValue("Todos");
        esNuevoCliente = true;

        deshabilitarFormulario();
        botonNuevo.setDisable(false);

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            clienteSeleccionado = newSelection;
            if (clienteSeleccionado != null) {
                cargarClienteEnFormulario();
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
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnaFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        columnaFechaModificacion.setCellValueFactory(new PropertyValueFactory<>("fechaModificacion"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarClientes() {
        try {
            clientes.setAll(modeloCliente.obtenerClientes());
            tablaClientes.setItems(filteredClientes);
        } catch (SQLException e) {
            mostrarError("Error al cargar los clientes: " + e.getMessage());
        }
    }

    private void configurarBusquedaYFiltro() {
        campoBusqueda.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        comboFiltroEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        actualizarFiltro();
    }

    private void configurarResaltadoInactivos() {
        tablaClientes.setRowFactory(tv -> new TableRow<Cliente>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                pseudoClassStateChanged(INACTIVO_PSEUDO_CLASS, cliente != null && !empty && cliente.getEstado().equals("Inactivo"));
            }
        });
    }

    private void actualizarFiltro() {
        filteredClientes.setPredicate(cliente -> {
            boolean busquedaValida = true;
            String terminoBusqueda = campoBusqueda.getText();
            if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
                String lowerCaseFilter = terminoBusqueda.toLowerCase();
                busquedaValida = cliente.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                                 cliente.getApellido().toLowerCase().contains(lowerCaseFilter) ||
                                 (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(lowerCaseFilter));
            }

            boolean estadoValido = true;
            String filtroEstado = comboFiltroEstado.getValue();
            if (filtroEstado != null && !filtroEstado.equals("Todos")) {
                estadoValido = cliente.getEstado().equals(filtroEstado.equals("Activos") ? "Activo" : "Inactivo");
            }

            return busquedaValida && estadoValido;
        });
    }

    @FXML
    private void nuevoCliente() {
        esNuevoCliente = true;
        habilitarFormulario();
        limpiarCampos();
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
        botonNuevo.setDisable(false);
    }

    @FXML
    private void buscarCliente() {
        cargarClientes();
        actualizarFiltro();
    }

    @FXML
    private void guardarCliente() {
        try {
            String nombre = campoNombre.getText().trim();
            String apellido = campoApellido.getText().trim();
            String telefono = campoTelefono.getText().trim();
            String direccion = campoDireccion.getText().trim();

            // Validar campos vacíos
            if (nombre.isEmpty() || apellido.isEmpty()) {
                mostrarError("Complete los campos obligatorios (Nombre, Apellido).");
                return;
            }

            // Validar límites de longitud
            if (nombre.length() > 50) {
                mostrarError("El nombre no puede exceder los 50 caracteres.");
                return;
            }
            if (apellido.length() > 50) {
                mostrarError("El apellido no puede exceder los 50 caracteres.");
                return;
            }
            if (!telefono.isEmpty() && telefono.length() > 20) {
                mostrarError("El teléfono no puede exceder los 20 caracteres.");
                return;
            }

            // Validar formato de teléfono (si se proporciona)
            if (!telefono.isEmpty() && !telefono.matches("\\d+")) {
                mostrarError("El teléfono debe contener solo números.");
                return;
            }

            // Verificar unicidad de teléfono (si se proporciona)
            if (!telefono.isEmpty() && esNuevoCliente && modeloCliente.existeTelefonoCliente(telefono, 0)) {
                mostrarError("El teléfono '" + telefono + "' ya existe.");
                return;
            } else if (!telefono.isEmpty() && !esNuevoCliente && modeloCliente.existeTelefonoCliente(telefono, clienteSeleccionado.getId())) {
                mostrarError("El teléfono '" + telefono + "' ya está en uso por otro cliente.");
                return;
            }

            // Crear o actualizar cliente
            Cliente cliente = esNuevoCliente ? new Cliente() : clienteSeleccionado;
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setTelefono(telefono.isEmpty() ? null : telefono);
            cliente.setDireccion(direccion.isEmpty() ? null : direccion);
            cliente.setEstado(radioActivo.isSelected() ? "Activo" : "Inactivo");

            if (esNuevoCliente) {
                modeloCliente.agregarCliente(cliente);
                mostrarInfo("Cliente agregado exitosamente.");
            } else {
                modeloCliente.actualizarCliente(cliente);
                mostrarInfo("Cliente actualizado exitosamente.");
            }

            cargarClientes();
            limpiarCampos();
            deshabilitarFormulario();
            clienteSeleccionado = null;
            esNuevoCliente = true;
            botonNuevo.setDisable(false);
        } catch (SQLException e) {
            mostrarError("Error al " + (esNuevoCliente ? "agregar" : "actualizar") + " el cliente: " + e.getMessage());
        }
    }

    @FXML
    private void editarCliente() {
        if (clienteSeleccionado == null) {
            mostrarError("Seleccione un cliente de la tabla.");
            return;
        }
        esNuevoCliente = false;
        habilitarFormulario();
        botonNuevo.setDisable(false);
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarError("Seleccione un cliente de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este cliente?");
        confirmacion.setContentText("Nombre: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                modeloCliente.eliminarCliente(clienteSeleccionado.getId());
                cargarClientes();
                limpiarCampos();
                deshabilitarFormulario();
                clienteSeleccionado = null;
                esNuevoCliente = true;
                botonNuevo.setDisable(false);
            } catch (SQLException e) {
                mostrarError("Error al eliminar el cliente: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelar() {
        limpiarCampos();
        deshabilitarFormulario();
        clienteSeleccionado = null;
        esNuevoCliente = true;
        botonNuevo.setDisable(false);
    }

    private void cargarClienteEnFormulario() {
        campoNombre.setText(clienteSeleccionado.getNombre());
        campoApellido.setText(clienteSeleccionado.getApellido());
        campoTelefono.setText(clienteSeleccionado.getTelefono());
        campoDireccion.setText(clienteSeleccionado.getDireccion());
        radioActivo.setSelected(clienteSeleccionado.getEstado().equals("Activo"));
        radioInactivo.setSelected(clienteSeleccionado.getEstado().equals("Inactivo"));
    }

    private void limpiarCampos() {
        campoBusqueda.clear();
        campoNombre.clear();
        campoApellido.clear();
        campoTelefono.clear();
        campoDireccion.clear();
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
        campoApellido.setDisable(true);
        campoTelefono.setDisable(true);
        campoDireccion.setDisable(true);
        radioActivo.setDisable(true);
        radioInactivo.setDisable(true);
        botonGuardar.setDisable(true);
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
    }

    private void habilitarFormulario() {
        campoNombre.setDisable(false);
        campoApellido.setDisable(false);
        campoTelefono.setDisable(false);
        campoDireccion.setDisable(false);
        radioActivo.setDisable(false);
        radioInactivo.setDisable(false);
        botonGuardar.setDisable(false);
    }
}