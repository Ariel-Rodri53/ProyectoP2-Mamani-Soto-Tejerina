package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class DetalleVenta {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty idVenta;
    private final SimpleIntegerProperty idProducto;
    private final SimpleStringProperty codigoProducto;
    private final SimpleStringProperty descripcionProducto;
    private final SimpleIntegerProperty cantidad;
    private final SimpleDoubleProperty precioUnitario;
    private final SimpleDoubleProperty descuento;
    private final SimpleDoubleProperty subtotal;

    public DetalleVenta() {
        this.id = new SimpleIntegerProperty();
        this.idVenta = new SimpleIntegerProperty();
        this.idProducto = new SimpleIntegerProperty();
        this.codigoProducto = new SimpleStringProperty();
        this.descripcionProducto = new SimpleStringProperty();
        this.cantidad = new SimpleIntegerProperty();
        this.precioUnitario = new SimpleDoubleProperty();
        this.descuento = new SimpleDoubleProperty();
        this.subtotal = new SimpleDoubleProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public int getIdVenta() { return idVenta.get(); }
    public void setIdVenta(int idVenta) { this.idVenta.set(idVenta); }
    public int getIdProducto() { return idProducto.get(); }
    public void setIdProducto(int idProducto) { this.idProducto.set(idProducto); }
    public String getCodigoProducto() { return codigoProducto.get(); }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto.set(codigoProducto); }
    public String getDescripcionProducto() { return descripcionProducto.get(); }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto.set(descripcionProducto); }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public double getPrecioUnitario() { return precioUnitario.get(); }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario.set(precioUnitario); }
    public double getDescuento() { return descuento.get(); }
    public void setDescuento(double descuento) { this.descuento.set(descuento); }
    public double getSubtotal() { return subtotal.get(); }
    public void setSubtotal(double subtotal) { this.subtotal.set(subtotal); }
}