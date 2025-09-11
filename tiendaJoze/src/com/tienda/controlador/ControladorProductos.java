package com.tienda.controlador;

import com.tienda.modelo.Producto;
import com.tienda.modelo.ModeloProducto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.Scene;

public class ControladorProductos {
    @FXML private TextField campoBusqueda;
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private ComboBox<String> comboFiltroTipoPrecio;
    @FXML private TextField campoCodigo;
    @FXML private TextField campoDescripcion;
    @FXML private TextField campoCosto;
    @FXML private TextField campoPrecio;
    @FXML private TextField campoTalle;
    @FXML private TextField campoColor;
    @FXML private ComboBox<String> comboMarca;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private ComboBox<String> comboProveedor;
    @FXML private TextField campoStock;
    @FXML private RadioButton radioActivo;
    @FXML private RadioButton radioInactivo;
    @FXML private ToggleGroup estadoToggleGroup;
    @FXML private ComboBox<String> comboTipoPrecioProducto;
    @FXML private TextField campoDescuentoProducto;
    @FXML private Label labelDescuentoProducto;
    @FXML private Label labelPrecioFinal;
    @FXML private TextField textoPrecioFinal;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> columnaCodigo;
    @FXML private TableColumn<Producto, String> columnaDescripcion;
    @FXML private TableColumn<Producto, Double> columnaCosto;
    @FXML private TableColumn<Producto, Double> columnaPrecio;
    @FXML private TableColumn<Producto, String> columnaTalle;
    @FXML private TableColumn<Producto, String> columnaColor;
    @FXML private TableColumn<Producto, String> columnaMarca;
    @FXML private TableColumn<Producto, String> columnaCategoria;
    @FXML private TableColumn<Producto, String> columnaProveedor;
    @FXML private TableColumn<Producto, Integer> columnaStock;
    @FXML private TableColumn<Producto, String> columnaEstado;
    @FXML private TableColumn<Producto, String> columnaTipoPrecioProducto;
    @FXML private TableColumn<Producto, Double> columnaDescuentoProducto;
    @FXML private Button botonNuevo;
    @FXML private Button botonGuardar;
    @FXML private Button botonEditar;
    @FXML private Button botonEliminar;
    @FXML private Button botonCancelar;

    private ModeloProducto modeloProducto;
    private Producto productoSeleccionado;
    private ObservableList<Producto> productos;
    private FilteredList<Producto> filteredProductos;
    private boolean esNuevoProducto;
    private static final PseudoClass INACTIVO_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactivo");
    private static final PseudoClass PROMOCION_PSEUDO_CLASS = PseudoClass.getPseudoClass("promocion");
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    @FXML
    private void initialize() {
        modeloProducto = new ModeloProducto();
        productos = FXCollections.observableArrayList();
        filteredProductos = new FilteredList<>(productos, p -> true);
        configurarColumnas();
        cargarCombos();
        cargarProductos();
        configurarBusquedaYFiltro();
        configurarResaltadoInactivosYPromociones();
        radioActivo.setSelected(true);
        comboFiltroEstado.getItems().addAll("Todos", "Activos", "Inactivos");
        comboFiltroEstado.setValue("Todos");
        comboFiltroTipoPrecio.getItems().addAll("Todos", "Normal", "Promocion", "Liquidacion");
        comboFiltroTipoPrecio.setValue("Todos");
        comboTipoPrecioProducto.getItems().addAll("Normal", "Promocion", "Liquidacion");
        comboTipoPrecioProducto.setValue("Normal");
        esNuevoProducto = true;

        deshabilitarFormulario();
        botonNuevo.setDisable(false);

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            productoSeleccionado = newSelection;
            if (productoSeleccionado != null) {
                cargarProductoEnFormulario();
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

        comboMarca.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().equalsIgnoreCase("Nueva Marca")) {
                try {
                    agregarNuevaMarca();
                    comboMarca.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    mostrarError("Error al abrir diálogo de nueva marca: " + e.getMessage());
                }
            }
        });

        comboCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().equalsIgnoreCase("Nueva Categoría")) {
                try {
                    agregarNuevaCategoria();
                    comboCategoria.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    mostrarError("Error al abrir diálogo de nueva categoría: " + e.getMessage());
                }
            }
        });

        // Mostrar y habilitar campo de descuento y precio final según tipo de precio
        comboTipoPrecioProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            boolean esPromocion = "Promocion".equals(newValue);
            labelDescuentoProducto.setVisible(esPromocion);
            campoDescuentoProducto.setVisible(esPromocion);
            campoDescuentoProducto.setDisable(!esPromocion || !botonEditar.isDisable()); // Deshabilitar si no está en modo edición
            labelPrecioFinal.setVisible(esPromocion);
            textoPrecioFinal.setVisible(esPromocion);
            if (!esPromocion) {
                campoDescuentoProducto.clear();
                textoPrecioFinal.clear();
            } else {
                actualizarPrecioFinal();
            }
        });

        // Actualizar precio final y validar descuento en tiempo real
        campoPrecio.textProperty().addListener((obs, oldValue, newValue) -> actualizarPrecioFinal());
        campoDescuentoProducto.textProperty().addListener((obs, oldValue, newValue) -> {
            validarDescuentoEnTiempoReal();
            actualizarPrecioFinal();
        });
    }

    private void actualizarPrecioFinal() {
        if ("Promocion".equals(comboTipoPrecioProducto.getValue())) {
            try {
                double precio = Double.parseDouble(campoPrecio.getText().trim());
                double descuento = campoDescuentoProducto.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(campoDescuentoProducto.getText().trim());
                double precioFinal = precio * (1 - descuento / 100);
                textoPrecioFinal.setText(String.format("%.2f", precioFinal));
            } catch (NumberFormatException e) {
                textoPrecioFinal.setText("0.00");
            }
        } else {
            textoPrecioFinal.setText("");
        }
    }

    private void validarDescuentoEnTiempoReal() {
        String descuentoText = campoDescuentoProducto.getText().trim();
        if (descuentoText.isEmpty()) {
            campoDescuentoProducto.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
            return;
        }
        try {
            double descuento = Double.parseDouble(descuentoText);
            campoDescuentoProducto.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, descuento < 0 || descuento > 100);
        } catch (NumberFormatException e) {
            campoDescuentoProducto.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
        }
    }

    private void configurarColumnas() {
        columnaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaTalle.setCellValueFactory(new PropertyValueFactory<>("talle"));
        columnaColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        columnaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        columnaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnaProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaTipoPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("tipoPrecioProducto"));
        columnaDescuentoProducto.setCellValueFactory(new PropertyValueFactory<>("descuentoProducto"));
    }

    private void cargarCombos() {
        try {
            ObservableList<String> marcas = FXCollections.observableArrayList(modeloProducto.obtenerMarcas());
            marcas.add("Nueva Marca");
            comboMarca.setItems(marcas);

            ObservableList<String> categorias = FXCollections.observableArrayList(modeloProducto.obtenerCategorias());
            categorias.add("Nueva Categoría");
            comboCategoria.setItems(categorias);

            comboProveedor.getItems().addAll(modeloProducto.obtenerProveedores());
        } catch (SQLException e) {
            mostrarError("Error al cargar los combos: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(modeloProducto.obtenerProductos());
            tablaProductos.setItems(filteredProductos);
        } catch (SQLException e) {
            mostrarError("Error al cargar los productos: " + e.getMessage());
        }
    }

    private void configurarBusquedaYFiltro() {
        campoBusqueda.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        comboFiltroEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        comboFiltroTipoPrecio.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        actualizarFiltro();
    }

    private void configurarResaltadoInactivosYPromociones() {
        tablaProductos.setRowFactory(tv -> new TableRow<Producto>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);
                pseudoClassStateChanged(INACTIVO_PSEUDO_CLASS, producto != null && !empty && producto.getEstado().equals("Inactivo"));
                pseudoClassStateChanged(PROMOCION_PSEUDO_CLASS, producto != null && !empty && producto.getTipoPrecioProducto().equals("Promocion"));
            }
        });
    }

    private void actualizarFiltro() {
        filteredProductos.setPredicate(producto -> {
            boolean busquedaValida = true;
            String terminoBusqueda = campoBusqueda.getText();
            if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
                String lowerCaseFilter = terminoBusqueda.toLowerCase();
                busquedaValida = producto.getCodigo().toLowerCase().contains(lowerCaseFilter) ||
                                 producto.getDescripcion().toLowerCase().contains(lowerCaseFilter);
            }

            boolean estadoValido = true;
            String filtroEstado = comboFiltroEstado.getValue();
            if (filtroEstado != null && !filtroEstado.equals("Todos")) {
                estadoValido = producto.getEstado().equals(filtroEstado.equals("Activos") ? "Activo" : "Inactivo");
            }

            boolean tipoPrecioValido = true;
            String filtroTipoPrecio = comboFiltroTipoPrecio.getValue();
            if (filtroTipoPrecio != null && !filtroTipoPrecio.equals("Todos")) {
                tipoPrecioValido = producto.getTipoPrecioProducto().equals(filtroTipoPrecio);
            }

            return busquedaValida && estadoValido && tipoPrecioValido;
        });
    }

    @FXML
    private void nuevoProducto() {
        esNuevoProducto = true;
        habilitarFormulario();
        limpiarCampos();
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
        botonNuevo.setDisable(false);
    }

    @FXML
    private void buscarProducto() {
        cargarProductos();
        actualizarFiltro();
    }

    @FXML
    private void guardarProducto() {
        try {
            // Validaciones de campos
            String codigo = campoCodigo.getText().trim();
            String descripcion = campoDescripcion.getText().trim();
            String costoText = campoCosto.getText().trim();
            String precioText = campoPrecio.getText().trim();
            String talle = campoTalle.getText().trim();
            String color = campoColor.getText().trim();
            String marca = comboMarca.getValue();
            String categoria = comboCategoria.getValue();
            String proveedor = comboProveedor.getValue();
            String stockText = campoStock.getText().trim();
            String tipoPrecioProducto = comboTipoPrecioProducto.getValue();
            String descuentoProductoText = campoDescuentoProducto.getText().trim();

            // Validar campos vacíos
            if (codigo.isEmpty() || descripcion.isEmpty() || costoText.isEmpty() || precioText.isEmpty() ||
                talle.isEmpty() || color.isEmpty() || marca == null || categoria == null || proveedor == null ||
                stockText.isEmpty() || marca.equals("Nueva Marca") || categoria.equals("Nueva Categoría") || tipoPrecioProducto == null) {
                mostrarError("Complete todos los campos obligatorios.");
                return;
            }
            if (tipoPrecioProducto.equals("Promocion") && descuentoProductoText.isEmpty()) {
                mostrarError("El porcentaje de descuento es obligatorio para productos en promoción.");
                return;
            }

            // Validar límites de longitud
            if (codigo.length() > 45) {
                mostrarError("El código del producto no puede exceder los 45 caracteres.");
                return;
            }
            if (descripcion.length() > 255) {
                mostrarError("La descripción no puede exceder los 255 caracteres.");
                return;
            }
            if (talle.length() > 10) {
                mostrarError("El talle no puede exceder los 10 caracteres.");
                return;
            }
            if (color.length() > 30) {
                mostrarError("El color no puede exceder los 30 caracteres.");
                return;
            }

            // Validar valores numéricos
            double costo;
            double precio;
            int stock;
            double descuentoProducto = 0.0;
            try {
                costo = Double.parseDouble(costoText);
                precio = Double.parseDouble(precioText);
                stock = Integer.parseInt(stockText);
                if (tipoPrecioProducto.equals("Promocion")) {
                    descuentoProducto = Double.parseDouble(descuentoProductoText);
                    if (descuentoProducto < 0 || descuentoProducto > 100) {
                        mostrarError("El porcentaje de descuento debe estar entre 0 y 100.");
                        return;
                    }
                }
                if (costo < 0 || precio < 0 || stock < 0) {
                    mostrarError("Costo, precio y stock no pueden ser negativos.");
                    return;
                }

                // Validaciones según tipo de precio
                if (tipoPrecioProducto.equals("Normal")) {
                    if (precio < costo * 1.10) {
                        mostrarError("Para precio normal, el precio debe ser al menos un 10% mayor al costo.");
                        return;
                    }
                } else if (tipoPrecioProducto.equals("Promocion")) {
                    // Calcular precio base
                    double precioBase = precio / (1 - descuentoProducto / 100);
                    if (precioBase < costo * 1.10) {
                        mostrarError("El precio base (antes del descuento) debe ser al menos un 10% mayor al costo.");
                        return;
                    }
                    if (precio < costo) {
                        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar Promoción");
                        confirmacion.setHeaderText("Precio menor al costo");
                        confirmacion.setContentText("El precio (" + String.format("%.2f", precio) + ") es menor al costo (" + String.format("%.2f", costo) + "). ¿Desea continuar con la promoción?");
                        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                            return;
                        }
                    }
                } // Liquidación: sin restricciones de precio
            } catch (NumberFormatException e) {
                mostrarError("Costo, precio, stock y (si aplica) descuento deben ser valores numéricos válidos.");
                return;
            }

            // Verificar si el código es único
            if (esNuevoProducto && modeloProducto.existeCodigoProducto(codigo, 0)) {
                mostrarError("El código de producto '" + codigo + "' ya existe.");
                return;
            } else if (!esNuevoProducto && modeloProducto.existeCodigoProducto(codigo, productoSeleccionado.getId())) {
                mostrarError("El código de producto '" + codigo + "' ya está en uso por otro producto.");
                return;
            }

            // Crear o actualizar el producto
            Producto producto = esNuevoProducto ? new Producto() : productoSeleccionado;
            producto.setCodigo(codigo);
            producto.setDescripcion(descripcion);
            producto.setCosto(costo);
            producto.setPrecio(precio);
            producto.setTalle(talle);
            producto.setColor(color);
            producto.setMarca(marca);
            producto.setCategoria(categoria);
            producto.setProveedor(proveedor);
            producto.setStock(stock);
            producto.setEstado(radioActivo.isSelected() ? "Activo" : "Inactivo");
            producto.setTipoPrecioProducto(tipoPrecioProducto);
            producto.setDescuentoProducto(tipoPrecioProducto.equals("Promocion") ? descuentoProducto : 0.0);

            // Guardar en la base de datos
            if (esNuevoProducto) {
                modeloProducto.agregarProducto(producto);
                mostrarInfo("Producto agregado exitosamente.");
            } else {
                modeloProducto.actualizarProducto(producto);
                mostrarInfo("Producto actualizado exitosamente.");
            }

            cargarProductos();
            limpiarCampos();
            deshabilitarFormulario();
            productoSeleccionado = null;
            esNuevoProducto = true;
            botonNuevo.setDisable(false);
        } catch (SQLException e) {
            mostrarError("Error al " + (esNuevoProducto ? "agregar" : "actualizar") + " el producto: " + e.getMessage());
        }
    }

    @FXML
    private void editarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla.");
            return;
        }
        esNuevoProducto = false;
        habilitarFormulario();
        botonNuevo.setDisable(false);
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este producto?");
        confirmacion.setContentText("Código: " + productoSeleccionado.getCodigo());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                modeloProducto.eliminarProducto(productoSeleccionado.getCodigo());
                cargarProductos();
                limpiarCampos();
                deshabilitarFormulario();
                productoSeleccionado = null;
                esNuevoProducto = true;
                botonNuevo.setDisable(false);
            } catch (SQLException e) {
                mostrarError("Error al eliminar el producto: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelar() {
        limpiarCampos();
        deshabilitarFormulario();
        productoSeleccionado = null;
        esNuevoProducto = true;
        botonNuevo.setDisable(false);
    }

    @FXML
    private void agregarNuevaMarca() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nueva Marca");

            VBox dialogVBox = new VBox(10);
            dialogVBox.setStyle("-fx-padding: 10;");
            TextField campoNombre = new TextField();
            campoNombre.setPromptText("Nombre de la Marca");
            TextArea campoDescripcion = new TextArea();
            campoDescripcion.setPromptText("Descripción (opcional)");
            campoDescripcion.setPrefRowCount(3);
            Button botonGuardar = new Button("Guardar");
            Button botonCancelar = new Button("Cancelar");

            HBox botones = new HBox(10, botonGuardar, botonCancelar);
            dialogVBox.getChildren().addAll(new Label("Agregar Nueva Marca"), campoNombre, campoDescripcion, botones);

            botonGuardar.setOnAction(e -> {
                String nombre = campoNombre.getText().trim();
                String descripcion = campoDescripcion.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarError("El nombre de la marca es obligatorio.");
                    return;
                }
                try {
                    modeloProducto.agregarMarca(nombre, descripcion.isEmpty() ? null : descripcion);
                    cargarCombos();
                    mostrarInfo("Marca agregada exitosamente.");
                    dialog.close();
                } catch (SQLException ex) {
                    mostrarError("Error al agregar la marca: " + ex.getMessage());
                }
            });

            botonCancelar.setOnAction(e -> dialog.close());

            Scene dialogScene = new Scene(dialogVBox, 300, 200);
            dialogScene.getStylesheets().add(getClass().getResource("/resources/css/estilos.css").toExternalForm());
            dialog.setScene(dialogScene);
            dialog.showAndWait();
        } catch (Exception e) {
            mostrarError("Error al crear el diálogo de nueva marca: " + e.getMessage());
        }
    }

    @FXML
    private void agregarNuevaCategoria() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nueva Categoría");

            VBox dialogVBox = new VBox(10);
            dialogVBox.setStyle("-fx-padding: 10;");
            TextField campoNombre = new TextField();
            campoNombre.setPromptText("Nombre de la Categoría");
            TextArea campoDescripcion = new TextArea();
            campoDescripcion.setPromptText("Descripción (opcional)");
            campoDescripcion.setPrefRowCount(3);
            Button botonGuardar = new Button("Guardar");
            Button botonCancelar = new Button("Cancelar");

            HBox botones = new HBox(10, botonGuardar, botonCancelar);
            dialogVBox.getChildren().addAll(new Label("Agregar Nueva Categoría"), campoNombre, campoDescripcion, botones);

            botonGuardar.setOnAction(e -> {
                String nombre = campoNombre.getText().trim();
                String descripcion = campoDescripcion.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarError("El nombre de la categoría es obligatorio.");
                    return;
                }
                try {
                    modeloProducto.agregarCategoria(nombre, descripcion.isEmpty() ? null : descripcion);
                    cargarCombos();
                    mostrarInfo("Categoría agregada exitosamente.");
                    dialog.close();
                } catch (SQLException ex) {
                    mostrarError("Error al agregar la categoría: " + ex.getMessage());
                }
            });

            botonCancelar.setOnAction(e -> dialog.close());

            Scene dialogScene = new Scene(dialogVBox, 300, 200);
            dialogScene.getStylesheets().add(getClass().getResource("/resources/css/estilos.css").toExternalForm());
            dialog.setScene(dialogScene);
            dialog.showAndWait();
        } catch (Exception e) {
            mostrarError("Error al crear el diálogo de nueva categoría: " + e.getMessage());
        }
    }

    private void cargarProductoEnFormulario() {
        campoCodigo.setText(productoSeleccionado.getCodigo());
        campoDescripcion.setText(productoSeleccionado.getDescripcion());
        campoCosto.setText(String.valueOf(productoSeleccionado.getCosto()));
        campoPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
        campoTalle.setText(productoSeleccionado.getTalle());
        campoColor.setText(productoSeleccionado.getColor());
        comboMarca.setValue(productoSeleccionado.getMarca());
        comboCategoria.setValue(productoSeleccionado.getCategoria());
        comboProveedor.setValue(productoSeleccionado.getProveedor());
        campoStock.setText(String.valueOf(productoSeleccionado.getStock()));
        radioActivo.setSelected(productoSeleccionado.getEstado().equals("Activo"));
        radioInactivo.setSelected(productoSeleccionado.getEstado().equals("Inactivo"));
        comboTipoPrecioProducto.setValue(productoSeleccionado.getTipoPrecioProducto());
        boolean esPromocion = "Promocion".equals(productoSeleccionado.getTipoPrecioProducto());
        labelDescuentoProducto.setVisible(esPromocion);
        campoDescuentoProducto.setVisible(esPromocion);
        campoDescuentoProducto.setDisable(true); // Deshabilitado al seleccionar
        labelPrecioFinal.setVisible(esPromocion);
        textoPrecioFinal.setVisible(esPromocion);
        campoDescuentoProducto.setText(esPromocion ? String.valueOf(productoSeleccionado.getDescuentoProducto()) : "");
        actualizarPrecioFinal();
        validarDescuentoEnTiempoReal();
    }

    private void limpiarCampos() {
        campoBusqueda.clear();
        campoCodigo.clear();
        campoDescripcion.clear();
        campoCosto.clear();
        campoPrecio.clear();
        campoTalle.clear();
        campoColor.clear();
        comboMarca.getSelectionModel().clearSelection();
        comboCategoria.getSelectionModel().clearSelection();
        comboProveedor.getSelectionModel().clearSelection();
        campoStock.clear();
        radioActivo.setSelected(true);
        comboTipoPrecioProducto.setValue("Normal");
        campoDescuentoProducto.clear();
        labelDescuentoProducto.setVisible(false);
        campoDescuentoProducto.setVisible(false);
        campoDescuentoProducto.setDisable(true);
        labelPrecioFinal.setVisible(false);
        textoPrecioFinal.setVisible(false);
        textoPrecioFinal.setText("");
        campoDescuentoProducto.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
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
        campoCodigo.setDisable(true);
        campoDescripcion.setDisable(true);
        campoCosto.setDisable(true);
        campoPrecio.setDisable(true);
        campoTalle.setDisable(true);
        campoColor.setDisable(true);
        comboMarca.setDisable(true);
        comboCategoria.setDisable(true);
        comboProveedor.setDisable(true);
        campoStock.setDisable(true);
        radioActivo.setDisable(true);
        radioInactivo.setDisable(true);
        comboTipoPrecioProducto.setDisable(true);
        labelDescuentoProducto.setVisible(false);
        campoDescuentoProducto.setVisible(false);
        campoDescuentoProducto.setDisable(true);
        labelPrecioFinal.setVisible(false);
        textoPrecioFinal.setVisible(false);
        textoPrecioFinal.setText("");
        campoDescuentoProducto.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
        botonGuardar.setDisable(true);
        botonEditar.setDisable(true);
        botonEliminar.setDisable(true);
    }

    private void habilitarFormulario() {
        campoCodigo.setDisable(false);
        campoDescripcion.setDisable(false);
        campoCosto.setDisable(false);
        campoPrecio.setDisable(false);
        campoTalle.setDisable(false);
        campoColor.setDisable(false);
        comboMarca.setDisable(false);
        comboCategoria.setDisable(false);
        comboProveedor.setDisable(false);
        campoStock.setDisable(false);
        radioActivo.setDisable(false);
        radioInactivo.setDisable(false);
        comboTipoPrecioProducto.setDisable(false);
        boolean esPromocion = "Promocion".equals(comboTipoPrecioProducto.getValue());
        labelDescuentoProducto.setVisible(esPromocion);
        campoDescuentoProducto.setVisible(esPromocion);
        campoDescuentoProducto.setDisable(!esPromocion); // Habilitar solo si es promoción
        labelPrecioFinal.setVisible(esPromocion);
        textoPrecioFinal.setVisible(esPromocion);
        actualizarPrecioFinal();
        validarDescuentoEnTiempoReal();
        botonGuardar.setDisable(false);
    }
}