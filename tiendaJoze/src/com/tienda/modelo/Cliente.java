package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Cliente {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty apellido;
    private final SimpleStringProperty telefono;
    private final SimpleStringProperty direccion;
    private final SimpleStringProperty fechaRegistro;
    private final SimpleStringProperty fechaModificacion;
    private final SimpleStringProperty estado;

    public Cliente() {
        this.id = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.apellido = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.direccion = new SimpleStringProperty();
        this.fechaRegistro = new SimpleStringProperty();
        this.fechaModificacion = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public String getApellido() { return apellido.get(); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public String getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public String getFechaModificacion() { return fechaModificacion.get(); }
    public void setFechaModificacion(String fechaModificacion) { this.fechaModificacion.set(fechaModificacion); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    
    @Override
    public String toString() {
        return nombre.get();
    }
}