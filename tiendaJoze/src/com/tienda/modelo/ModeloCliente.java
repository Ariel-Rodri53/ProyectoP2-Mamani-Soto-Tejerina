package com.tienda.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloCliente {
    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost/joze";
        String usuario = "root";
        String contrasena = "";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public boolean existeTelefonoCliente(String telefono, int idClienteExcluir) throws SQLException {
        String consulta = "SELECT COUNT(*) FROM clientes WHERE telefono_cliente = ? AND id_cliente != ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, telefono);
            stmt.setInt(2, idClienteExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Cliente> obtenerClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String consulta = "SELECT * FROM clientes";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setApellido(rs.getString("apellido_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setDireccion(rs.getString("direccion_cliente"));
                cliente.setFechaRegistro(rs.getString("fechreg_cliente"));
                cliente.setFechaModificacion(rs.getString("fechmod_cliente"));
                cliente.setEstado(rs.getInt("estado_cliente") == 1 ? "Activo" : "Inactivo");
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public void agregarCliente(Cliente cliente) throws SQLException {
        if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() && existeTelefonoCliente(cliente.getTelefono(), 0)) {
            throw new SQLException("El teléfono '" + cliente.getTelefono() + "' ya existe.");
        }
        String consulta = "INSERT INTO clientes (nombre_cliente, apellido_cliente, telefono_cliente, direccion_cliente, estado_cliente) " +
                         "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setInt(5, cliente.getEstado().equals("Activo") ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() && existeTelefonoCliente(cliente.getTelefono(), cliente.getId())) {
            throw new SQLException("El teléfono '" + cliente.getTelefono() + "' ya está en uso por otro cliente.");
        }
        String consulta = "UPDATE clientes SET nombre_cliente = ?, apellido_cliente = ?, telefono_cliente = ?, " +
                         "direccion_cliente = ?, estado_cliente = ? WHERE id_cliente = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setInt(5, cliente.getEstado().equals("Activo") ? 1 : 0);
            stmt.setInt(6, cliente.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarCliente(int id) throws SQLException {
        String consulta = "UPDATE clientes SET estado_cliente = 0 WHERE id_cliente = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}