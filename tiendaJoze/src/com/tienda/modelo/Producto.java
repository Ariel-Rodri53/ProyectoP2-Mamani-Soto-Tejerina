package com.tienda.modelo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Producto {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty codigo;
    private final SimpleStringProperty descripcion;
    private final SimpleDoubleProperty costo;
    private final SimpleDoubleProperty precio;
    private final SimpleStringProperty talle;
    private final SimpleStringProperty color;
    private final SimpleStringProperty marca;
    private final SimpleStringProperty categoria;
    private final SimpleStringProperty proveedor;
    private final SimpleIntegerProperty stock;
    private final SimpleStringProperty estado;
    private final SimpleStringProperty tipoPrecioProducto;
    private final SimpleDoubleProperty descuentoProducto;

    public Producto() {
        this.id = new SimpleIntegerProperty();
        this.codigo = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
        this.costo = new SimpleDoubleProperty();
        this.precio = new SimpleDoubleProperty();
        this.talle = new SimpleStringProperty();
        this.color = new SimpleStringProperty();
        this.marca = new SimpleStringProperty();
        this.categoria = new SimpleStringProperty();
        this.proveedor = new SimpleStringProperty();
        this.stock = new SimpleIntegerProperty();
        this.estado = new SimpleStringProperty();
        this.tipoPrecioProducto = new SimpleStringProperty();
        this.descuentoProducto = new SimpleDoubleProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public double getCosto() { return costo.get(); }
    public void setCosto(double costo) { this.costo.set(costo); }
    public double getPrecio() { return precio.get(); }
    public void setPrecio(double precio) { this.precio.set(precio); }
    public String getTalle() { return talle.get(); }
    public void setTalle(String talle) { this.talle.set(talle); }
    public String getColor() { return color.get(); }
    public void setColor(String color) { this.color.set(color); }
    public String getMarca() { return marca.get(); }
    public void setMarca(String marca) { this.marca.set(marca); }
    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }
    public String getProveedor() { return proveedor.get(); }
    public void setProveedor(String proveedor) { this.proveedor.set(proveedor); }
    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public String getTipoPrecioProducto() { return tipoPrecioProducto.get(); }
    public void setTipoPrecioProducto(String tipoPrecioProducto) { this.tipoPrecioProducto.set(tipoPrecioProducto); }
    public double getDescuentoProducto() { return descuentoProducto.get(); }
    public void setDescuentoProducto(double descuentoProducto) { this.descuentoProducto.set(descuentoProducto); }
    
    @Override
    public String toString() {
        return codigo.get() + " - " + descripcion.get();
    }
}