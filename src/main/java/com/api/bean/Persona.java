/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.bean;

/**
 *
 * @author Carlos David Zepeda
 */
public class Persona {
    private String cedula;
    private String nombre;
    private Telefono telefono;
    
    public String getCedula() {
        return cedula;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public Telefono getTelefono() {
        return telefono;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setTelefono(Telefono tel) {
        this.telefono = tel;
    }
}
