
package com.tienda.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Proveedor {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty direccion;
    private final SimpleStringProperty email;
    private final SimpleStringProperty telefono;
    private final SimpleStringProperty cuit;
    private final SimpleStringProperty estado;

    public Proveedor() {
        this.id = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.direccion = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.cuit = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public String getCuit() { return cuit.get(); }
    public void setCuit(String cuit) { this.cuit.set(cuit); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    
    @Override
    public String toString() {
        return nombre.get();
    }
}

