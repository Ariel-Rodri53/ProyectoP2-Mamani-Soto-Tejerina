package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Compra {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty idProveedor;
    private final SimpleStringProperty nombreProveedor;
    private final SimpleStringProperty fechaRegistro;
    private final SimpleDoubleProperty totalCompra;

    public Compra() {
        this.id = new SimpleIntegerProperty();
        this.idProveedor = new SimpleIntegerProperty();
        this.nombreProveedor = new SimpleStringProperty();
        this.fechaRegistro = new SimpleStringProperty();
        this.totalCompra = new SimpleDoubleProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public int getIdProveedor() { return idProveedor.get(); }
    public void setIdProveedor(int idProveedor) { this.idProveedor.set(idProveedor); }
    public String getNombreProveedor() { return nombreProveedor.get(); }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor.set(nombreProveedor); }
    public String getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public double getTotalCompra() { return totalCompra.get(); }
    public void setTotalCompra(double totalCompra) { this.totalCompra.set(totalCompra); }
}