package com.tienda.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloVenta {
    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost/joze";
        String usuario = "root";
        String contrasena = "";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public List<Venta> obtenerVentas() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String consulta = "SELECT v.*, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente, m.nombre_metpag " +
                         "FROM ventas v " +
                         "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                         "JOIN metodospago m ON v.id_metpag = m.id_metpag";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id_venta"));
                venta.setIdCliente(rs.getInt("id_cliente"));
                venta.setNombreCliente(rs.getString("nombre_cliente"));
                venta.setFechaRegistro(rs.getString("fechreg_venta"));
                venta.setTotalVenta(rs.getDouble("total_venta"));
                venta.setIdMetodoPago(rs.getInt("id_metpag"));
                venta.setNombreMetodoPago(rs.getString("nombre_metpag"));
                venta.setEstado(rs.getString("estado_venta"));
                ventas.add(venta);
            }
        }
        return ventas;
    }

    public List<DetalleVenta> obtenerDetallesVenta(int idVenta) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String consulta = "SELECT dv.*, p.codigo_producto, p.descripcion_producto " +
                         "FROM detalleventa dv JOIN productos p ON dv.id_producto = p.id_producto " +
                         "WHERE dv.id_venta = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idVenta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setId(rs.getInt("id_detvent"));
                    detalle.setIdVenta(rs.getInt("id_venta"));
                    detalle.setIdProducto(rs.getInt("id_producto"));
                    detalle.setCodigoProducto(rs.getString("codigo_producto"));
                    detalle.setDescripcionProducto(rs.getString("descripcion_producto"));
                    detalle.setCantidad(rs.getInt("cantidad_detvent"));
                    detalle.setPrecioUnitario(rs.getDouble("pu_detvent"));
                    detalle.setDescuento(rs.getDouble("desc_detvent"));
                    detalle.setSubtotal(rs.getDouble("subtot_detvent"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    public List<Cliente> obtenerClientesActivos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String consulta = "SELECT id_cliente, CONCAT(nombre_cliente, ' ', apellido_cliente) AS nombre_completo " +
                         "FROM clientes WHERE estado_cliente = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNombre(rs.getString("nombre_completo"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public List<Producto> obtenerProductosActivos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String consulta = "SELECT id_producto, codigo_producto, descripcion_producto, precio_producto, stock_producto, tipo_precio_producto, descuento_producto " +
                         "FROM productos WHERE estado_producto = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_producto"));
                producto.setDescripcion(rs.getString("descripcion_producto"));
                producto.setPrecio(rs.getDouble("precio_producto"));
                producto.setStock(rs.getInt("stock_producto"));
                producto.setTipoPrecioProducto(rs.getString("tipo_precio_producto"));
                producto.setDescuentoProducto(rs.getDouble("descuento_producto"));
                productos.add(producto);
            }
        }
        return productos;
    }

    public List<MetodoPago> obtenerMetodosPagoActivos() throws SQLException {
        List<MetodoPago> metodosPago = new ArrayList<>();
        String consulta = "SELECT id_metpag, nombre_metpag FROM metodospago WHERE estado_metpag = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MetodoPago metodoPago = new MetodoPago();
                metodoPago.setId(rs.getInt("id_metpag"));
                metodoPago.setNombre(rs.getString("nombre_metpag"));
                metodosPago.add(metodoPago);
            }
        }
        return metodosPago;
    }

    public void agregarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        Connection conn = obtenerConexion();
        try {
            conn.setAutoCommit(false);
            // Insertar venta
            String consultaVenta = "INSERT INTO ventas (id_cliente, total_venta, id_metpag, estado_venta) VALUES (?, ?, ?, ?)";
            int idVenta;
            try (PreparedStatement stmt = conn.prepareStatement(consultaVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, venta.getIdCliente());
                stmt.setDouble(2, venta.getTotalVenta());
                stmt.setInt(3, venta.getIdMetodoPago());
                stmt.setString(4, venta.getEstado());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVenta = rs.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener ID de venta.");
                    }
                }
            }

            // Insertar detalles y actualizar stock
            String consultaDetalle = "INSERT INTO detalleventa (id_venta, id_producto, cantidad_detvent, pu_detvent, desc_detvent, subtot_detvent) VALUES (?, ?, ?, ?, ?, ?)";
            String consultaStock = "UPDATE productos SET stock_producto = stock_producto - ? WHERE id_producto = ?";
            for (DetalleVenta detalle : detalles) {
                // Verificar stock
                try (PreparedStatement stmtStockCheck = conn.prepareStatement("SELECT stock_producto FROM productos WHERE id_producto = ?")) {
                    stmtStockCheck.setInt(1, detalle.getIdProducto());
                    try (ResultSet rs = stmtStockCheck.executeQuery()) {
                        if (rs.next() && rs.getInt("stock_producto") < detalle.getCantidad()) {
                            throw new SQLException("Stock insuficiente para el producto: " + detalle.getCodigoProducto());
                        }
                    }
                }
                // Insertar detalle
                try (PreparedStatement stmtDetalle = conn.prepareStatement(consultaDetalle)) {
                    stmtDetalle.setInt(1, idVenta);
                    stmtDetalle.setInt(2, detalle.getIdProducto());
                    stmtDetalle.setInt(3, detalle.getCantidad());
                    stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                    stmtDetalle.setDouble(5, detalle.getDescuento());
                    stmtDetalle.setDouble(6, detalle.getSubtotal());
                    stmtDetalle.executeUpdate();
                }
                // Actualizar stock
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

    public void actualizarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        Connection conn = obtenerConexion();
        try {
            conn.setAutoCommit(false);
            // Restaurar stock de detalles anteriores
            List<DetalleVenta> detallesAnteriores = obtenerDetallesVenta(venta.getId());
            String consultaStock = "UPDATE productos SET stock_producto = stock_producto + ? WHERE id_producto = ?";
            for (DetalleVenta detalle : detallesAnteriores) {
                try (PreparedStatement stmtStock = conn.prepareStatement(consultaStock)) {
                    stmtStock.setInt(1, detalle.getCantidad());
                    stmtStock.setInt(2, detalle.getIdProducto());
                    stmtStock.executeUpdate();
                }
            }
            // Eliminar detalles anteriores
            String consultaEliminarDetalles = "DELETE FROM detalleventa WHERE id_venta = ?";
            try (PreparedStatement stmtEliminar = conn.prepareStatement(consultaEliminarDetalles)) {
                stmtEliminar.setInt(1, venta.getId());
                stmtEliminar.executeUpdate();
            }
            // Actualizar venta
            String consultaVenta = "UPDATE ventas SET id_cliente = ?, total_venta = ?, id_metpag = ?, estado_venta = ? WHERE id_venta = ?";
            try (PreparedStatement stmt = conn.prepareStatement(consultaVenta)) {
                stmt.setInt(1, venta.getIdCliente());
                stmt.setDouble(2, venta.getTotalVenta());
                stmt.setInt(3, venta.getIdMetodoPago());
                stmt.setString(4, venta.getEstado());
                stmt.setInt(5, venta.getId());
                stmt.executeUpdate();
            }
            // Insertar nuevos detalles y actualizar stock
            String consultaDetalle = "INSERT INTO detalleventa (id_venta, id_producto, cantidad_detvent, pu_detvent, desc_detvent, subtot_detvent) VALUES (?, ?, ?, ?, ?, ?)";
            for (DetalleVenta detalle : detalles) {
                // Verificar stock
                try (PreparedStatement stmtStockCheck = conn.prepareStatement("SELECT stock_producto FROM productos WHERE id_producto = ?")) {
                    stmtStockCheck.setInt(1, detalle.getIdProducto());
                    try (ResultSet rs = stmtStockCheck.executeQuery()) {
                        if (rs.next() && rs.getInt("stock_producto") < detalle.getCantidad()) {
                            throw new SQLException("Stock insuficiente para el producto: " + detalle.getCodigoProducto());
                        }
                    }
                }
                // Insertar detalle
                try (PreparedStatement stmtDetalle = conn.prepareStatement(consultaDetalle)) {
                    stmtDetalle.setInt(1, venta.getId());
                    stmtDetalle.setInt(2, detalle.getIdProducto());
                    stmtDetalle.setInt(3, detalle.getCantidad());
                    stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                    stmtDetalle.setDouble(5, detalle.getDescuento());
                    stmtDetalle.setDouble(6, detalle.getSubtotal());
                    stmtDetalle.executeUpdate();
                }
                // Actualizar stock
                try (PreparedStatement stmtStock = conn.prepareStatement(consultaStock)) {
                    stmtStock.setInt(1, -detalle.getCantidad());
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

    public void eliminarVenta(int idVenta) throws SQLException {
        Connection conn = obtenerConexion();
        try {
            conn.setAutoCommit(false);
            // Restaurar stock
            List<DetalleVenta> detalles = obtenerDetallesVenta(idVenta);
            String consultaStock = "UPDATE productos SET stock_producto = stock_producto + ? WHERE id_producto = ?";
            for (DetalleVenta detalle : detalles) {
                try (PreparedStatement stmtStock = conn.prepareStatement(consultaStock)) {
                    stmtStock.setInt(1, detalle.getCantidad());
                    stmtStock.setInt(2, detalle.getIdProducto());
                    stmtStock.executeUpdate();
                }
            }
            // Cambiar estado a 'cancelada'
            String consulta = "UPDATE ventas SET estado_venta = 'cancelada' WHERE id_venta = ?";
            try (PreparedStatement stmt = conn.prepareStatement(consulta)) {
                stmt.setInt(1, idVenta);
                stmt.executeUpdate();
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