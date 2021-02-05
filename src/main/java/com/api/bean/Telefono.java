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
public class Telefono {
    private String numero;
    private String operador;
    
    public String getNumero() {
        return numero;
    }
    
    public String getOperador() {
        return operador;
    }
    
    public void setNumero(String num) {
        this.numero = num;
    }
    
    public void setOperador(String operador) {
        this.operador = operador;
    }
}
