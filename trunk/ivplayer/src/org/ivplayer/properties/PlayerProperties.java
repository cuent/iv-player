/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivplayer.properties;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edgar
 */
public class PlayerProperties extends java.util.Properties{
    
    java.io.File f;
    
    public PlayerProperties(String id){
        
        if(id.equals("ES")){
            getProperties("_ES.properties");
        }else if(id.equals("EN")){
            getProperties("_EN.properties");
        }
        
        if(id.equals("CONFIG")){
            getProperties("config.properties");
        }
        
    }
    
    private void getProperties(String idioma) {
        try {
            f = new java.io.File(this.getClass().getResource("config.properties").getFile().replace("%20", " "));
            this.load(getClass().getResourceAsStream(idioma));
        } catch (IOException ex) {
            Logger.getLogger(PlayerProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public java.io.File getFile(){
        return f;
    }
    
}
