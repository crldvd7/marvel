/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.bean;

import java.util.List;

/**
 *
 * @author Carlos David Zepeda
 */
public class Colaborator {
    private String last_sync;
    private List<String> editors;
    private List<String> writers;
    private List<String> colorists;
    
    public Colaborator(String last_sync, List<String> e, List<String> w, List<String> c) {
        this.last_sync = last_sync;
        this.editors = e;
        this.writers = w;
        this.colorists = c;
    }
    
    public String getLast_sync() {
        return last_sync;
    }
    
    public List<String> getEditors() {
        return editors;
    }
    
    public List<String> getWriters() {
        return writers;
    }
    
    public List<String> getColorists() {
        return colorists;
    }
    
    public void setLast_sync(String s) {
        this.last_sync = s;
    }
    
    public void setEditors(List<String> l) {
        this.editors = l;
    }
    
    public void setWriters(List<String> l) {
        this.writers = l;
    }
    public void setColorist(List<String> l) {
        this.colorists = l;
    }
}
