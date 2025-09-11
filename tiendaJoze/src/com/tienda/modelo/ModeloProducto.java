package com.tienda.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloProducto {
    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost/joze";
        String usuario = "root";
        String contrasena = "";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public boolean existeCodigoProducto(String codigo, int idProductoExcluir) throws SQLException {
        String consulta = "SELECT COUNT(*) FROM productos WHERE codigo_producto = ? AND id_producto != ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, idProductoExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Producto> obtenerProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String consulta = "SELECT p.*, m.nombre_marca, c.nombre_categoria, pr.nombre_proveedor " +
                         "FROM productos p " +
                         "JOIN marcas m ON p.id_marca = m.id_marca " +
                         "JOIN categorias c ON p.id_categoria = c.id_categoria " +
                         "JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_producto"));
                producto.setDescripcion(rs.getString("descripcion_producto"));
                producto.setCosto(rs.getDouble("costo_producto"));
                producto.setPrecio(rs.getDouble("precio_producto"));
                producto.setTalle(rs.getString("talle_producto"));
                producto.setColor(rs.getString("color_producto"));
                producto.setMarca(rs.getString("nombre_marca"));
                producto.setCategoria(rs.getString("nombre_categoria"));
                producto.setProveedor(rs.getString("nombre_proveedor"));
                producto.setStock(rs.getInt("stock_producto"));
                producto.setEstado(rs.getInt("estado_producto") == 1 ? "Activo" : "Inactivo");
                producto.setTipoPrecioProducto(rs.getString("tipo_precio_producto"));
                producto.setDescuentoProducto(rs.getDouble("descuento_producto"));
                productos.add(producto);
            }
        }
        return productos;
    }

    public void agregarProducto(Producto producto) throws SQLException {
        if (existeCodigoProducto(producto.getCodigo(), 0)) {
            throw new SQLException("El código de producto '" + producto.getCodigo() + "' ya existe.");
        }

        String consulta = "INSERT INTO productos (codigo_producto, descripcion_producto, costo_producto, precio_producto, " +
                         "talle_producto, color_producto, id_marca, id_categoria, id_proveedor, stock_producto, estado_producto, tipo_precio_producto, descuento_producto) " +
                         "VALUES (?, ?, ?, ?, ?, ?, (SELECT id_marca FROM marcas WHERE nombre_marca = ?), " +
                         "(SELECT id_categoria FROM categorias WHERE nombre_categoria = ?), " +
                         "(SELECT id_proveedor FROM proveedores WHERE nombre_proveedor = ?), ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getCosto());
            stmt.setDouble(4, producto.getPrecio());
            stmt.setString(5, producto.getTalle());
            stmt.setString(6, producto.getColor());
            stmt.setString(7, producto.getMarca());
            stmt.setString(8, producto.getCategoria());
            stmt.setString(9, producto.getProveedor());
            stmt.setInt(10, producto.getStock());
            stmt.setInt(11, producto.getEstado().equals("Activo") ? 1 : 0);
            stmt.setString(12, producto.getTipoPrecioProducto());
            if (producto.getTipoPrecioProducto().equals("Promocion")) {
                stmt.setDouble(13, producto.getDescuentoProducto());
            } else {
                stmt.setNull(13, java.sql.Types.DECIMAL);
            }
            stmt.executeUpdate();
        }
    }

    public void actualizarProducto(Producto producto) throws SQLException {
        if (existeCodigoProducto(producto.getCodigo(), producto.getId())) {
            throw new SQLException("El código de producto '" + producto.getCodigo() + "' ya está en uso por otro producto.");
        }

        String consulta = "UPDATE productos SET descripcion_producto = ?, costo_producto = ?, precio_producto = ?, " +
                         "talle_producto = ?, color_producto = ?, id_marca = (SELECT id_marca FROM marcas WHERE nombre_marca = ?), " +
                         "id_categoria = (SELECT id_categoria FROM categorias WHERE nombre_categoria = ?), " +
                         "id_proveedor = (SELECT id_proveedor FROM proveedores WHERE nombre_proveedor = ?), " +
                         "stock_producto = ?, estado_producto = ?, tipo_precio_producto = ?, descuento_producto = ? WHERE codigo_producto = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, producto.getDescripcion());
            stmt.setDouble(2, producto.getCosto());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setString(4, producto.getTalle());
            stmt.setString(5, producto.getColor());
            stmt.setString(6, producto.getMarca());
            stmt.setString(7, producto.getCategoria());
            stmt.setString(8, producto.getProveedor());
            stmt.setInt(9, producto.getStock());
            stmt.setInt(10, producto.getEstado().equals("Activo") ? 1 : 0);
            stmt.setString(11, producto.getTipoPrecioProducto());
            if (producto.getTipoPrecioProducto().equals("Promocion")) {
                stmt.setDouble(12, producto.getDescuentoProducto());
            } else {
                stmt.setNull(12, java.sql.Types.DECIMAL);
            }
            stmt.setString(13, producto.getCodigo());
            stmt.executeUpdate();
        }
    }

    public void eliminarProducto(String codigo) throws SQLException {
        String consulta = "UPDATE productos SET estado_producto = 0 WHERE codigo_producto = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, codigo);
            stmt.executeUpdate();
        }
    }

    public List<String> obtenerMarcas() throws SQLException {
        List<String> marcas = new ArrayList<>();
        String consulta = "SELECT nombre_marca FROM marcas WHERE estado_marca = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                marcas.add(rs.getString("nombre_marca"));
            }
        }
        return marcas;
    }

    public List<String> obtenerCategorias() throws SQLException {
        List<String> categorias = new ArrayList<>();
        String consulta = "SELECT nombre_categoria FROM categorias WHERE estado_categoria = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("nombre_categoria"));
            }
        }
        return categorias;
    }

    public List<String> obtenerProveedores() throws SQLException {
        List<String> proveedores = new ArrayList<>();
        String consulta = "SELECT nombre_proveedor FROM proveedores WHERE estado_proveedor = 1";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                proveedores.add(rs.getString("nombre_proveedor"));
            }
        }
        return proveedores;
    }

    public void agregarMarca(String nombre, String descripcion) throws SQLException {
        String consulta = "INSERT INTO marcas (nombre_marca, descripcion_marca, estado_marca) VALUES (?, ?, 1)";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.executeUpdate();
        }
    }

    public void agregarCategoria(String nombre, String descripcion) throws SQLException {
        String consulta = "INSERT INTO categorias (nombre_categoria, descripcion_categoria, estado_categoria) VALUES (?, ?, 1)";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.executeUpdate();
        }
    }
}