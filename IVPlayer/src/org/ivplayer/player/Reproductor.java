/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivplayer.player;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 *
 * @author Edgar
 */
public class Reproductor implements BasicPlayerListener{
    
    private Double longitudBytes;
    private BasicPlayer basicPlayer;
    private Interfaz interfaz;
    private String ruta;
    private int anterior = 0;
    private float progressUpdate;
    private final int valorSlider = 1000;
    
    // Constructor
    
    public Reproductor(){
        
        // instaciamos el objeto basicPlayer
        basicPlayer = new BasicPlayer();
        // se hace una llamada al metodo addBasicPlayerListener para poder obtener sus eventos
        basicPlayer.addBasicPlayerListener(this);
        
    }
    
    // metodo para establecer la referencia a la interfaz
    // esto es para poder controlar el progreso, metodo que esta mas abajo
    public void setReferenciaInterfaz(Interfaz interfaz){
        this.interfaz = interfaz;
    }
    
    // Metodo Play
    
    public void play(){
        try {
            basicPlayer.play();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Metodo Puase
    
    public void pause(){
        try {
            basicPlayer.pause();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Metodo Stop
    public void stop(){
        try {
            basicPlayer.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo continuar
    
    public void resume(){
        try {
            basicPlayer.resume();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getStatus(){
        return basicPlayer.getStatus();
    }

    // Metodo para abrir el archivos
    
    public void abrirArchivo(String ruta){
        try {
            anterior = 0;
            this.ruta = ruta;
            basicPlayer.open(new File(ruta));
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Establecer el volumen el cual va desde [0.0 , 1.0]
    public void setVolumen(Double v){
        try {
            basicPlayer.setGain(v);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setSeek(Double seek){
        try {
            double valor = interfaz.getValueProgress();
            double valor2 = 1000 / valor;
            double valor3 = interfaz.getEtiquetas().getDuracion() / valor2;
            anterior = (int)valor3;
            basicPlayer.seek( (long)(longitudBytes * (seek * 1.0)) );
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Metodo que se ejecuta una sola vez.
    // utilizado para obtener el tamaño en bytes del archivo
    @Override
    public void opened(Object o, Map map) {
         if (map.containsKey("audio.length.bytes")) {
                longitudBytes = Double.parseDouble(map.get("audio.length.bytes").toString());
         }
    }

    // Metodo para controlar el progreso actualizando el estado segun los bytes de lectura
    
    @Override
    public void progress(int bytesLectura, long l, byte[] bytes, Map map) {
         progressUpdate = (float) (bytesLectura * 1.0f / longitudBytes * 1.0f);
         //actualizar sliderProgress
         interfaz.actualizarProgreso((int) (progressUpdate * valorSlider));
         interfaz.actualizarAuxProgreso(anterior + (Integer.parseInt( ""+(l / 1000000) ) ) );
         interfaz.setTiempoCancion(anterior + ((int) (l / 1000000)));
         
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
    }

    @Override
    public void setController(BasicController bc) {
    }
    
}
