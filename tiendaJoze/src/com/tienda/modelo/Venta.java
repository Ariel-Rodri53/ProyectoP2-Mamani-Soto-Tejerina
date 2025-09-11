package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Venta {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty idCliente;
    private final SimpleStringProperty nombreCliente;
    private final SimpleStringProperty fechaRegistro;
    private final SimpleDoubleProperty totalVenta;
    private final SimpleIntegerProperty idMetodoPago;
    private final SimpleStringProperty nombreMetodoPago;
    private final SimpleStringProperty estado;

    public Venta() {
        this.id = new SimpleIntegerProperty();
        this.idCliente = new SimpleIntegerProperty();
        this.nombreCliente = new SimpleStringProperty();
        this.fechaRegistro = new SimpleStringProperty();
        this.totalVenta = new SimpleDoubleProperty();
        this.idMetodoPago = new SimpleIntegerProperty();
        this.nombreMetodoPago = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public int getIdCliente() { return idCliente.get(); }
    public void setIdCliente(int idCliente) { this.idCliente.set(idCliente); }
    public String getNombreCliente() { return nombreCliente.get(); }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente.set(nombreCliente); }
    public String getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public double getTotalVenta() { return totalVenta.get(); }
    public void setTotalVenta(double totalVenta) { this.totalVenta.set(totalVenta); }
    public int getIdMetodoPago() { return idMetodoPago.get(); }
    public void setIdMetodoPago(int idMetodoPago) { this.idMetodoPago.set(idMetodoPago); }
    public String getNombreMetodoPago() { return nombreMetodoPago.get(); }
    public void setNombreMetodoPago(String nombreMetodoPago) { this.nombreMetodoPago.set(nombreMetodoPago); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
}