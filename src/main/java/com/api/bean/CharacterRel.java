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
public class CharacterRel {
    private String last_sync;
    private List<com.api.bean.Character> characters;
    
    public CharacterRel(String last_sync, List<com.api.bean.Character> characteres) {
        this.characters = characteres;
        this.last_sync = last_sync;
    }
    
    public String getLast_sync() {
        return last_sync;
    }
    
    public List<com.api.bean.Character> getCharacters() {
        return characters;
    }
    
    public void setLast_sync(String s) {
        this.last_sync = s;
    }
    
    public void setCharacters(List<com.api.bean.Character> l) {
        this.characters = l;
    }
}
