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
public class Character {
    private String character;
    private List<String> Comics;
    
    public Character(String character, List<String> Comics) {
        this.character = character;
        this.Comics = Comics;
    }
    
    public String getCharacter() {
        return character;
    }
    
    public List<String> getComics() {
        return Comics;
    }
    
    public void setCharacter(String s) {
        this.character = s;
    }
    
    public void setComics(List<String> l) {
        this.Comics = l;
    }
}
