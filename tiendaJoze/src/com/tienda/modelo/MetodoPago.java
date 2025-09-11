package com.tienda.modelo;

   import javafx.beans.property.SimpleIntegerProperty;
   import javafx.beans.property.SimpleStringProperty;

   public class MetodoPago {
       private final SimpleIntegerProperty id;
       private final SimpleStringProperty nombre;
       private final SimpleStringProperty descripcion;
       private final SimpleIntegerProperty requiereTerminal;
       private final SimpleIntegerProperty estado;

       public MetodoPago() {
           this.id = new SimpleIntegerProperty();
           this.nombre = new SimpleStringProperty();
           this.descripcion = new SimpleStringProperty();
           this.requiereTerminal = new SimpleIntegerProperty();
           this.estado = new SimpleIntegerProperty();
       }

       public int getId() { return id.get(); }
       public void setId(int id) { this.id.set(id); }
       public String getNombre() { return nombre.get(); }
       public void setNombre(String nombre) { this.nombre.set(nombre); }
       public String getDescripcion() { return descripcion.get(); }
       public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
       public int getRequiereTerminal() { return requiereTerminal.get(); }
       public void setRequiereTerminal(int requiereTerminal) { this.requiereTerminal.set(requiereTerminal); }
       public int getEstado() { return estado.get(); }
       public void setEstado(int estado) { this.estado.set(estado); }

       @Override
       public String toString() {
           return nombre.get();
       }
   }