
package com.tienda.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloProveedor {
    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost/joze";
        String usuario = "root";
        String contrasena = "";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public boolean existeEmailProveedor(String email, int idProveedorExcluir) throws SQLException {
        String consulta = "SELECT COUNT(*) FROM proveedores WHERE email_proveedor = ? AND id_proveedor != ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, email);
            stmt.setInt(2, idProveedorExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Proveedor> obtenerProveedores() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String consulta = "SELECT * FROM proveedores";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre_proveedor"));
                proveedor.setDireccion(rs.getString("direccion_proveedor"));
                proveedor.setEmail(rs.getString("email_proveedor"));
                proveedor.setTelefono(rs.getString("telefono_proveedor"));
                proveedor.setCuit(rs.getString("cuit_proveedor"));
                proveedor.setEstado(rs.getInt("estado_proveedor") == 1 ? "Activo" : "Inactivo");
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }

    public void agregarProveedor(Proveedor proveedor) throws SQLException {
        if (existeEmailProveedor(proveedor.getEmail(), 0)) {
            throw new SQLException("El email '" + proveedor.getEmail() + "' ya existe.");
        }
        String consulta = "INSERT INTO proveedores (nombre_proveedor, direccion_proveedor, email_proveedor, telefono_proveedor, cuit_proveedor, estado_proveedor) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getDireccion());
            stmt.setString(3, proveedor.getEmail());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getCuit());
            stmt.setInt(6, proveedor.getEstado().equals("Activo") ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public void actualizarProveedor(Proveedor proveedor) throws SQLException {
        if (existeEmailProveedor(proveedor.getEmail(), proveedor.getId())) {
            throw new SQLException("El email '" + proveedor.getEmail() + "' ya está en uso por otro proveedor.");
        }
        String consulta = "UPDATE proveedores SET nombre_proveedor = ?, direccion_proveedor = ?, email_proveedor = ?, " +
                          "telefono_proveedor = ?, cuit_proveedor = ?, estado_proveedor = ? WHERE id_proveedor = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getDireccion());
            stmt.setString(3, proveedor.getEmail());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getCuit());
            stmt.setInt(6, proveedor.getEstado().equals("Activo") ? 1 : 0);
            stmt.setInt(7, proveedor.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarProveedor(int id) throws SQLException {
        String consulta = "UPDATE proveedores SET estado_proveedor = 0 WHERE id_proveedor = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
