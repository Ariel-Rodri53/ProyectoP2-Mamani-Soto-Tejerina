package com.tienda.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModeloCompra {
    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost/joze";
        String usuario = "root";
        String contrasena = "";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public ObservableList<Compra> obtenerCompras() throws SQLException {
        ObservableList<Compra> compras = FXCollections.observableArrayList();
        String consulta = "SELECT c.*, p.nombre_proveedor " +
                         "FROM compras c JOIN proveedores p ON c.id_proveedor = p.id_proveedor";
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id_compra"));
                compra.setIdProveedor(rs.getInt("id_proveedor"));
                compra.setNombreProveedor(rs.getString("nombre_proveedor"));
                compra.setFechaRegistro(rs.getString("fechreg_compra"));
                compra.setTotalCompra(rs.getDouble("total_compra"));
                compras.add(compra);
            }
        }
        return compras;
    }

    public List<DetalleCompra> obtenerDetallesCompra(int idCompra) throws SQLException {
        List<DetalleCompra> detalles = new ArrayList<>();
        String consulta = "SELECT dc.*, p.CODIGO_PRODUCTO, p.DESCRIPCION_PRODUCTO " +
                         "FROM detallecompra dc JOIN productos p ON dc.id_producto = p.id_producto " +
                         "WHERE dc.id_compra = ?";
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCompra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleCompra detalle = new DetalleCompra();
                    detalle.setId(rs.getInt("id_detcomp"));
                    detalle.setIdCompra(rs.getInt("id_compra"));
                    detalle.setIdProducto(rs.getInt("id_producto"));
                    detalle.setCodigoProducto(rs.getString("CODIGO_PRODUCTO"));
                    detalle.setDescripcionProducto(rs.getString("DESCRIPCION_PRODUCTO"));
                    detalle.setCantidad(rs.getInt("cantidad_detcomp"));
                    detalle.setCostoUnitario(rs.getDouble("costo_unitario_detcomp"));
                    detalle.setSubtotal(rs.getDouble("subtot_detcomp"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    public ObservableList<Proveedor> obtenerProveedoresActivos() throws SQLException {
        ObservableList<Proveedor> proveedores = FXCollections.observableArrayList();
        String consulta = "SELECT id_proveedor, nombre_proveedor FROM proveedores WHERE estado_proveedor = 1";
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre_proveedor"));
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }

    public ObservableList<Producto> obtenerProductosActivos() throws SQLException {
        ObservableList<Producto> productos = FXCollections.observableArrayList();
        String consulta = "SELECT id_producto, codigo_producto, descripcion_producto, costo_producto " +
                         "FROM productos WHERE estado_producto = 1";
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_producto"));
                producto.setDescripcion(rs.getString("descripcion_producto"));
                producto.setCosto(rs.getDouble("costo_producto"));
                productos.add(producto);
            }
        }
        return productos;
    }

    public void agregarCompra(Compra compra, List<DetalleCompra> detalles) throws SQLException {
        Connection conn = obtenerConexion();
        try {
            conn.setAutoCommit(false);
            // Insertar compra
            String consultaCompra = "INSERT INTO compras (id_proveedor, total_compra, fechreg_compra) VALUES (?, ?, ?)";
            int idCompra;
            try (PreparedStatement stmt = conn.prepareStatement(consultaCompra, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, compra.getIdProveedor());
                stmt.setDouble(2, compra.getTotalCompra());
                stmt.setString(3, compra.getFechaRegistro());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idCompra = rs.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener ID de compra.");
                    }
                }
            }

            // Insertar detalles y actualizar stock
            String consultaDetalle = "INSERT INTO detallecompra (id_compra, id_producto, cantidad_detcomp, costo_unitario_detcomp, subtot_detcomp) VALUES (?, ?, ?, ?, ?)";
            String consultaStock = "UPDATE productos SET stock_producto = stock_producto + ? WHERE id_producto = ?";
            for (DetalleCompra detalle : detalles) {
                try (PreparedStatement stmtDetalle = conn.prepareStatement(consultaDetalle)) {
                    stmtDetalle.setInt(1, idCompra);
                    stmtDetalle.setInt(2, detalle.getIdProducto());
                    stmtDetalle.setInt(3, detalle.getCantidad());
                    stmtDetalle.setDouble(4, detalle.getCostoUnitario());
                    stmtDetalle.setDouble(5, detalle.getSubtotal());
                    stmtDetalle.executeUpdate();
                }
                try (PreparedStatement stmtStock = conn.prepareStatement(consultaStock)) {
                    stmtStock.setInt(1, detalle.getCantidad());
                    stmtStock.setInt(2, detalle.getIdProducto());
                    stmtStock.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}