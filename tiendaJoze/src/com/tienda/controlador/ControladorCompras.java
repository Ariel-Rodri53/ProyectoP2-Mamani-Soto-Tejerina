package com.tienda.controlador;

import com.tienda.modelo.Compra;
import com.tienda.modelo.DetalleCompra;
import com.tienda.modelo.ModeloCompra;
import com.tienda.modelo.Proveedor;
import com.tienda.modelo.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.binding.Bindings;
import java.sql.SQLException;

public class ControladorCompras {
    @FXML private ComboBox<Proveedor> comboProveedor;
    @FXML private ComboBox<Producto> comboProducto;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoCostoUnitario;
    @FXML private TableView<DetalleCompra> tablaDetalles;
    @FXML private TableColumn<DetalleCompra, String> columnaCodigoProducto;
    @FXML private TableColumn<DetalleCompra, String> columnaDescripcionProducto;
    @FXML private TableColumn<DetalleCompra, Integer> columnaCantidad;
    @FXML private TableColumn<DetalleCompra, Double> columnaCostoUnitario;
    @FXML private TableColumn<DetalleCompra, Double> columnaSubtotal;
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, Integer> columnaIdCompra;
    @FXML private TableColumn<Compra, String> columnaNombreProveedor;
    @FXML private TableColumn<Compra, String> columnaFechaRegistro;
    @FXML private TableColumn<Compra, Double> columnaTotalCompra;
    @FXML private Button botonAgregarProducto;
    @FXML private Button botonQuitarProducto;
    @FXML private Button botonNuevo;
    @FXML private Button botonGuardar;
    @FXML private Label labelTotalCompra;

    private ModeloCompra modeloCompra;
    private ObservableList<Compra> compras;
    private ObservableList<DetalleCompra> detalles;
    private ObservableList<Proveedor> proveedores;
    private ObservableList<Producto> productos;
    private boolean esNuevaCompra;

    @FXML
    private void initialize() {
        modeloCompra = new ModeloCompra();
        compras = FXCollections.observableArrayList();
        detalles = FXCollections.observableArrayList();
        proveedores = FXCollections.observableArrayList();
        productos = FXCollections.observableArrayList();
        configurarColumnas();
        cargarDatos();
        tablaDetalles.setItems(detalles);
        esNuevaCompra = true;

        // Vinculación del labelTotalCompra
        labelTotalCompra.textProperty().bind(Bindings.createStringBinding(() -> 
            String.format("Total Compra: $%.2f", detalles.stream().mapToDouble(DetalleCompra::getSubtotal).sum()),
            detalles));

        deshabilitarFormulario();
        botonNuevo.setDisable(false);

        tablaCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarCompraEnFormulario(newSelection);
                botonGuardar.setDisable(true);
            } else {
                limpiarFormulario();
                deshabilitarFormulario();
                botonGuardar.setDisable(true);
            }
        });

        comboProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                campoCantidad.setText("1");
                campoCostoUnitario.setText(String.format("%.2f", newValue.getCosto()));
            }
        });

        campoCantidad.textProperty().addListener((obs, oldValue, newValue) -> validarCantidad());
        campoCostoUnitario.textProperty().addListener((obs, oldValue, newValue) -> validarCostoUnitario());
    }

    private void configurarColumnas() {
        columnaIdCompra.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombreProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        columnaFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        columnaTotalCompra.setCellValueFactory(new PropertyValueFactory<>("totalCompra"));
        columnaCodigoProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        columnaDescripcionProducto.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaCostoUnitario.setCellValueFactory(new PropertyValueFactory<>("costoUnitario"));
        columnaSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void cargarDatos() {
        try {
            compras.setAll(modeloCompra.obtenerCompras());
            tablaCompras.setItems(compras);
            proveedores.setAll(modeloCompra.obtenerProveedoresActivos());
            comboProveedor.setItems(proveedores);
            productos.setAll(modeloCompra.obtenerProductosActivos());
            comboProducto.setItems(productos);
        } catch (SQLException e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    @FXML
    private void nuevaCompra() {
        esNuevaCompra = true;
        habilitarFormulario();
        limpiarFormulario();
        botonGuardar.setDisable(false);
    }

    @FXML
    private void guardarCompra() {
        try {
            if (comboProveedor.getValue() == null) {
                mostrarError("Seleccione un proveedor.");
                return;
            }
            if (detalles.isEmpty()) {
                mostrarError("Agregue al menos un producto a la compra.");
                return;
            }

            Compra compra = new Compra();
            compra.setIdProveedor(comboProveedor.getValue().getId());
            compra.setTotalCompra(detalles.stream().mapToDouble(DetalleCompra::getSubtotal).sum());
            compra.setFechaRegistro(java.time.LocalDateTime.now().toString());

            modeloCompra.agregarCompra(compra, detalles);
            mostrarInfo("Compra registrada exitosamente.");

            cargarDatos();
            limpiarFormulario();
            deshabilitarFormulario();
            esNuevaCompra = true;
            botonNuevo.setDisable(false);
        } catch (SQLException e) {
            mostrarError("Error al registrar la compra: " + e.getMessage());
        }
    }

    @FXML
    private void agregarProducto() {
        Producto producto = comboProducto.getValue();
        String cantidadText = campoCantidad.getText().trim();
        String costoText = campoCostoUnitario.getText().trim();
        if (producto == null || cantidadText.isEmpty() || costoText.isEmpty()) {
            mostrarError("Seleccione un producto, especifique una cantidad y un costo unitario.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadText);
            double costoUnitario = Double.parseDouble(costoText);
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor que 0.");
                return;
            }
            if (costoUnitario <= 0) {
                mostrarError("El costo unitario debe ser mayor que 0.");
                return;
            }

            DetalleCompra detalle = new DetalleCompra();
            detalle.setIdProducto(producto.getId());
            detalle.setCodigoProducto(producto.getCodigo());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setCantidad(cantidad);
            detalle.setCostoUnitario(costoUnitario);
            detalle.setSubtotal(cantidad * costoUnitario);
            detalles.add(detalle);

            comboProducto.getSelectionModel().clearSelection();
            campoCantidad.clear();
            campoCostoUnitario.clear();
        } catch (NumberFormatException e) {
            mostrarError("La cantidad y el costo unitario deben ser números válidos.");
        }
    }

    @FXML
    private void quitarProducto() {
        DetalleCompra detalleSeleccionado = tablaDetalles.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla de detalles.");
            return;
        }
        detalles.remove(detalleSeleccionado);
    }

    @FXML
    private void cancelar() {
        limpiarFormulario();
        deshabilitarFormulario();
        esNuevaCompra = true;
        botonNuevo.setDisable(false);
        botonGuardar.setDisable(true);
        tablaCompras.getSelectionModel().clearSelection();
    }

    private void cargarCompraEnFormulario(Compra compra) {
        try {
            comboProveedor.setValue(proveedores.stream()
                .filter(p -> p.getId() == compra.getIdProveedor())
                .findFirst()
                .orElse(null));
            detalles.setAll(modeloCompra.obtenerDetallesCompra(compra.getId()));
        } catch (SQLException e) {
            mostrarError("Error al cargar los detalles de la compra: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        comboProveedor.getSelectionModel().clearSelection();
        comboProducto.getSelectionModel().clearSelection();
        campoCantidad.clear();
        campoCostoUnitario.clear();
        detalles.clear();
    }

    private void validarCantidad() {
        String cantidadText = campoCantidad.getText().trim();
        if (cantidadText.isEmpty()) return;
        try {
            int cantidad = Integer.parseInt(cantidadText);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero positivo.");
        }
    }

    private void validarCostoUnitario() {
        String costoText = campoCostoUnitario.getText().trim();
        if (costoText.isEmpty()) return;
        try {
            double costo = Double.parseDouble(costoText);
            if (costo <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("El costo unitario debe ser un número positivo.");
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
        comboProveedor.setDisable(true);
        comboProducto.setDisable(true);
        campoCantidad.setDisable(true);
        campoCostoUnitario.setDisable(true);
        botonAgregarProducto.setDisable(true);
        botonQuitarProducto.setDisable(true);
        tablaDetalles.setDisable(true);
        botonGuardar.setDisable(true);
    }

    private void habilitarFormulario() {
        comboProveedor.setDisable(false);
        comboProducto.setDisable(false);
        campoCantidad.setDisable(false);
        campoCostoUnitario.setDisable(false);
        botonAgregarProducto.setDisable(false);
        botonQuitarProducto.setDisable(false);
        tablaDetalles.setDisable(false);
        botonGuardar.setDisable(false);
    }
}