package com.tienda.controlador;

import com.tienda.modelo.Venta;
import com.tienda.modelo.DetalleVenta;
import com.tienda.modelo.ModeloVenta;
import com.tienda.modelo.Cliente;
import com.tienda.modelo.Producto;
import com.tienda.modelo.MetodoPago;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.binding.Bindings;

public class ControladorVentas {
    @FXML private TextField campoBusqueda;
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Producto> comboProducto;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoDescuento;
    @FXML private ComboBox<MetodoPago> comboMetodoPago;
    @FXML private ComboBox<String> comboEstadoVenta;
    @FXML private TableView<DetalleVenta> tablaDetalles;
    @FXML private TableColumn<DetalleVenta, String> columnaCodigoProducto;
    @FXML private TableColumn<DetalleVenta, String> columnaDescripcionProducto;
    @FXML private TableColumn<DetalleVenta, Integer> columnaCantidad;
    @FXML private TableColumn<DetalleVenta, Double> columnaPrecioUnitario;
    @FXML private TableColumn<DetalleVenta, Double> columnaDescuento;
    @FXML private TableColumn<DetalleVenta, Double> columnaSubtotal;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> columnaIdVenta;
    @FXML private TableColumn<Venta, String> columnaNombreCliente;
    @FXML private TableColumn<Venta, String> columnaFechaRegistro;
    @FXML private TableColumn<Venta, Double> columnaTotalVenta;
    @FXML private TableColumn<Venta, String> columnaMetodoPago;
    @FXML private TableColumn<Venta, String> columnaEstado;
    @FXML private Button botonAgregarProducto;
    @FXML private Button botonQuitarProducto;
    @FXML private Button botonNuevo;
    @FXML private Button botonGuardar;
    @FXML private Button botonEditar;
    @FXML private Button botonEliminar;
    @FXML private Button botonCancelar;
    @FXML private Label labelTotalVenta;

    private ModeloVenta modeloVenta;
    private Venta ventaSeleccionada;
    private ObservableList<Venta> ventas;
    private FilteredList<Venta> filteredVentas;
    private ObservableList<DetalleVenta> detalles;
    private ObservableList<Cliente> clientes;
    private ObservableList<Producto> productos;
    private ObservableList<MetodoPago> metodosPago;
    private boolean esNuevaVenta;
    private static final PseudoClass INACTIVO_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactivo");

    @FXML
    private void initialize() {
        modeloVenta = new ModeloVenta();
        ventas = FXCollections.observableArrayList();
        filteredVentas = new FilteredList<>(ventas, p -> true);
        detalles = FXCollections.observableArrayList();
        clientes = FXCollections.observableArrayList();
        productos = FXCollections.observableArrayList();
        metodosPago = FXCollections.observableArrayList();
        configurarColumnas();
        cargarDatos();
        configurarBusquedaYFiltro();
        configurarResaltadoInactivos();
        tablaDetalles.setItems(detalles);
        comboFiltroEstado.getItems().addAll("Todos", "pendiente", "completada", "cancelada", "reembolsada", "en_proceso");
        comboFiltroEstado.setValue("Todos");
        comboEstadoVenta.getItems().addAll("pendiente", "completada", "cancelada", "reembolsada", "en_proceso");
        comboEstadoVenta.setValue("pendiente");
        esNuevaVenta = true;

        // Vinculación del labelTotalVenta
        labelTotalVenta.textProperty().bind(Bindings.createStringBinding(() -> 
            String.format("Total Venta: $%.2f", detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum()),
            detalles));

        deshabilitarFormulario();
        botonNuevo.setDisable(false);

        tablaVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ventaSeleccionada = newSelection;
            if (ventaSeleccionada != null) {
                cargarVentaEnFormulario();
                botonEditar.setDisable(false);
                botonEliminar.setDisable(false);
                botonGuardar.setDisable(true);
            } else {
                limpiarFormulario();
                deshabilitarFormulario();
                botonEditar.setDisable(true);
                botonEliminar.setDisable(true);
                botonGuardar.setDisable(true);
            }
        });

        comboProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                campoCantidad.setText("1");
                campoDescuento.setText("0.00");
            }
        });

        campoCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            validarCantidad();
        });

        campoDescuento.textProperty().addListener((obs, oldValue, newValue) -> {
            validarDescuento();
        });
    }

    private void configurarColumnas() {
        columnaIdVenta.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        columnaFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        columnaTotalVenta.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));
        columnaMetodoPago.setCellValueFactory(new PropertyValueFactory<>("nombreMetodoPago"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaCodigoProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        columnaDescripcionProducto.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        columnaDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        columnaSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void cargarDatos() {
        try {
            ventas.setAll(modeloVenta.obtenerVentas());
            tablaVentas.setItems(filteredVentas);
            clientes.setAll(modeloVenta.obtenerClientesActivos());
            comboCliente.setItems(clientes);
            productos.setAll(modeloVenta.obtenerProductosActivos());
            comboProducto.setItems(productos);
            metodosPago.setAll(modeloVenta.obtenerMetodosPagoActivos());
            comboMetodoPago.setItems(metodosPago);
        } catch (SQLException e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void configurarBusquedaYFiltro() {
        campoBusqueda.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        comboFiltroEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        actualizarFiltro();
    }

    private void configurarResaltadoInactivos() {
        tablaVentas.setRowFactory(tv -> new TableRow<Venta>() {
            @Override
            protected void updateItem(Venta venta, boolean empty) {
                super.updateItem(venta, empty);
                pseudoClassStateChanged(INACTIVO_PSEUDO_CLASS, venta != null && !empty && venta.getEstado().equals("cancelada"));
            }
        });
    }

    private void actualizarFiltro() {
        filteredVentas.setPredicate(venta -> {
            boolean busquedaValida = true;
            String terminoBusqueda = campoBusqueda.getText();
            if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
                String lowerCaseFilter = terminoBusqueda.toLowerCase();
                busquedaValida = String.valueOf(venta.getId()).contains(lowerCaseFilter) ||
                                 venta.getNombreCliente().toLowerCase().contains(lowerCaseFilter);
            }

            boolean estadoValido = true;
            String filtroEstado = comboFiltroEstado.getValue();
            if (filtroEstado != null && !filtroEstado.equals("Todos")) {
                estadoValido = venta.getEstado().equals(filtroEstado);
            }

            return busquedaValida && estadoValido;
        });
    }

    @FXML
    private void nuevoVenta() {
        esNuevaVenta = true;
        habilitarFormulario();
        limpiarFormulario();
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
        botonNuevo.setDisable(false);
    }

    @FXML
    private void buscarVenta() {
        cargarDatos();
        actualizarFiltro();
    }

    @FXML
    private void guardarVenta() {
        try {
            // Validaciones
            if (comboCliente.getValue() == null) {
                mostrarError("Seleccione un cliente.");
                return;
            }
            if (comboMetodoPago.getValue() == null) {
                mostrarError("Seleccione un método de pago.");
                return;
            }
            if (detalles.isEmpty()) {
                mostrarError("Agregue al menos un producto a la venta.");
                return;
            }

            Venta venta = esNuevaVenta ? new Venta() : ventaSeleccionada;
            venta.setIdCliente(comboCliente.getValue().getId());
            venta.setIdMetodoPago(comboMetodoPago.getValue().getId());
            venta.setEstado(comboEstadoVenta.getValue());
            venta.setTotalVenta(detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum());

            if (esNuevaVenta) {
                modeloVenta.agregarVenta(venta, detalles);
                mostrarInfo("Venta agregada exitosamente.");
            } else {
                modeloVenta.actualizarVenta(venta, detalles);
                mostrarInfo("Venta actualizada exitosamente.");
            }

            cargarDatos();
            limpiarFormulario();
            deshabilitarFormulario();
            ventaSeleccionada = null;
            esNuevaVenta = true;
            botonNuevo.setDisable(false);
        } catch (SQLException e) {
            mostrarError("Error al " + (esNuevaVenta ? "agregar" : "actualizar") + " la venta: " + e.getMessage());
        }
    }

    @FXML
    private void editarVenta() {
        if (ventaSeleccionada == null) {
            mostrarError("Seleccione una venta de la tabla.");
            return;
        }
        esNuevaVenta = false;
        habilitarFormulario();
        botonNuevo.setDisable(false);
    }

    @FXML
    private void eliminarVenta() {
        if (ventaSeleccionada == null) {
            mostrarError("Seleccione una venta de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de que desea cancelar esta venta?");
        confirmacion.setContentText("ID Venta: " + ventaSeleccionada.getId());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                modeloVenta.eliminarVenta(ventaSeleccionada.getId());
                cargarDatos();
                limpiarFormulario();
                deshabilitarFormulario();
                ventaSeleccionada = null;
                esNuevaVenta = true;
                botonNuevo.setDisable(false);
            } catch (SQLException e) {
                mostrarError("Error al cancelar la venta: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelar() {
        limpiarFormulario();
        deshabilitarFormulario();
        ventaSeleccionada = null;
        esNuevaVenta = true;
        botonNuevo.setDisable(false);
    }

    @FXML
    private void agregarProducto() {
        Producto producto = comboProducto.getValue();
        String cantidadText = campoCantidad.getText().trim();
        String descuentoText = campoDescuento.getText().trim();
        if (producto == null || cantidadText.isEmpty()) {
            mostrarError("Seleccione un producto y especifique una cantidad.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadText);
            double descuento = descuentoText.isEmpty() ? 0.0 : Double.parseDouble(descuentoText);
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor que 0.");
                return;
            }
            if (descuento < 0 || descuento > 100) {
                mostrarError("El descuento debe estar entre 0 y 100.");
                return;
            }
            if (cantidad > producto.getStock()) {
                mostrarError("La cantidad excede el stock disponible (" + producto.getStock() + ").");
                return;
            }
            double precioUnitario = producto.getTipoPrecioProducto().equals("Promocion") ?
                                   producto.getPrecio() * (1 - producto.getDescuentoProducto() / 100) :
                                   producto.getPrecio();
            double precioConDescuento = precioUnitario * (1 - descuento / 100);
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto.getId());
            detalle.setCodigoProducto(producto.getCodigo());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.setSubtotal(cantidad * precioConDescuento);
            detalles.add(detalle);
            comboProducto.getSelectionModel().clearSelection();
            campoCantidad.clear();
            campoDescuento.setText("0.00");
        } catch (NumberFormatException e) {
            mostrarError("La cantidad y el descuento deben ser números válidos.");
        }
    }

    @FXML
    private void quitarProducto() {
        DetalleVenta detalleSeleccionado = tablaDetalles.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla de detalles.");
            return;
        }
        detalles.remove(detalleSeleccionado);
    }

    private void cargarVentaEnFormulario() {
        try {
            comboCliente.setValue(clientes.stream()
                .filter(c -> c.getId() == ventaSeleccionada.getIdCliente())
                .findFirst()
                .orElse(null));
            comboMetodoPago.setValue(metodosPago.stream()
                .filter(m -> m.getId() == ventaSeleccionada.getIdMetodoPago())
                .findFirst()
                .orElse(null));
            comboEstadoVenta.setValue(ventaSeleccionada.getEstado());
            detalles.setAll(modeloVenta.obtenerDetallesVenta(ventaSeleccionada.getId()));
        } catch (SQLException e) {
            mostrarError("Error al cargar los detalles de la venta: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        campoBusqueda.clear();
        comboCliente.getSelectionModel().clearSelection();
        comboProducto.getSelectionModel().clearSelection();
        campoCantidad.clear();
        campoDescuento.setText("0.00");
        comboMetodoPago.getSelectionModel().clearSelection();
        comboEstadoVenta.setValue("pendiente");
        detalles.clear();
    }

    private void validarCantidad() {
        String cantidadText = campoCantidad.getText().trim();
        if (cantidadText.isEmpty()) {
            campoCantidad.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), false);
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadText);
            Producto producto = comboProducto.getValue();
            if (producto != null && cantidad > producto.getStock()) {
                campoCantidad.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
            } else {
                campoCantidad.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), false);
            }
        } catch (NumberFormatException e) {
            campoCantidad.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
        }
    }

    private void validarDescuento() {
        String descuentoText = campoDescuento.getText().trim();
        if (descuentoText.isEmpty()) {
            campoDescuento.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), false);
            return;
        }
        try {
            double descuento = Double.parseDouble(descuentoText);
            if (descuento < 0 || descuento > 100) {
                campoDescuento.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
            } else {
                campoDescuento.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), false);
            }
        } catch (NumberFormatException e) {
            campoDescuento.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
        }
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
        comboCliente.setDisable(true);
        comboProducto.setDisable(true);
        campoCantidad.setDisable(true);
        campoDescuento.setDisable(true);
        comboMetodoPago.setDisable(true);
        comboEstadoVenta.setDisable(true);
        botonAgregarProducto.setDisable(true);
        botonQuitarProducto.setDisable(true);
        tablaDetalles.setDisable(true);
        botonGuardar.setDisable(true);
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
    }

    private void habilitarFormulario() {
        comboCliente.setDisable(false);
        comboProducto.setDisable(false);
        campoCantidad.setDisable(false);
        campoDescuento.setDisable(false);
        comboMetodoPago.setDisable(false);
        comboEstadoVenta.setDisable(false);
        botonAgregarProducto.setDisable(false);
        botonQuitarProducto.setDisable(false);
        tablaDetalles.setDisable(false);
        botonGuardar.setDisable(false);
    }
}