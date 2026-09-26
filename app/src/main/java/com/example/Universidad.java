package com.example;

import java.io.Serializable;

/**
 * Entidad que representa una Universidad para el taller académico.
 * Maneja los datos de la institución: id, nombre y sitio web (www).
 */
public class Universidad implements Serializable {

    // Propiedades privadas
    private String id;
    private String nombre;
    private String www;

    /**
     * Constructor vacío requerido para inicialización por defecto.
     */
    public Universidad() {
    }

    /**
     * Constructor con todos los parámetros (usado al consultar o editar).
     *
     * @param id     Identificador único de la universidad.
     * @param nombre Nombre oficial de la universidad.
     * @param www    Dirección web / URL de la universidad.
     */
    public Universidad(String id, String nombre, String www) {
        this.id = id;
        this.nombre = nombre;
        this.www = www;
    }

    /**
     * Constructor de conveniencia sin ID (usado antes de insertar en la base de datos).
     *
     * @param nombre Nombre oficial de la universidad.
     * @param www    Dirección web / URL de la universidad.
     */
    public Universidad(String nombre, String www) {
        this.nombre = nombre;
        this.www = www;
    }

    // Métodos Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    @Override
    public String toString() {
        return "Universidad{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", www='" + www + '\'' +
                '}';
    }
}
