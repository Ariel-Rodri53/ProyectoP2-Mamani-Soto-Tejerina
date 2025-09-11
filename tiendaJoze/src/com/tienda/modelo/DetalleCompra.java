package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DetalleCompra {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty idCompra;
    private final SimpleIntegerProperty idProducto;
    private final SimpleStringProperty codigoProducto;
    private final SimpleStringProperty descripcionProducto;
    private final SimpleIntegerProperty cantidad;
    private final SimpleDoubleProperty costoUnitario;
    private final SimpleDoubleProperty subtotal;

    public DetalleCompra() {
        this.id = new SimpleIntegerProperty();
        this.idCompra = new SimpleIntegerProperty();
        this.idProducto = new SimpleIntegerProperty();
        this.codigoProducto = new SimpleStringProperty();
        this.descripcionProducto = new SimpleStringProperty();
        this.cantidad = new SimpleIntegerProperty();
        this.costoUnitario = new SimpleDoubleProperty();
        this.subtotal = new SimpleDoubleProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int idCompra) { this.idCompra.set(idCompra); }
    public int getIdProducto() { return idProducto.get(); }
    public void setIdProducto(int idProducto) { this.idProducto.set(idProducto); }
    public String getCodigoProducto() { return codigoProducto.get(); }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto.set(codigoProducto); }
    public String getDescripcionProducto() { return descripcionProducto.get(); }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto.set(descripcionProducto); }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public double getCostoUnitario() { return costoUnitario.get(); }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario.set(costoUnitario); }
    public double getSubtotal() { return subtotal.get(); }
    public void setSubtotal(double subtotal) { this.subtotal.set(subtotal); }
}
