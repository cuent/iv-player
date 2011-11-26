/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivplayer.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import org.ivplayer.player.Reproductor;

/**
 *
 * @author Edgar
 */
public class Tags {
    
    private File file;
    private AudioFileFormat baseFileFormat;
    private Map properties;
    private Long temp = 0l;
    private int duracion, year = 0;
    private String titulo, autor, album;
    
        //Objecto MediaFile es el que va a contener el archivo elegido
    private MediaFile mediaFile;
    
    private byte[] arrayByte;

    private InputStream in;
    
    private BufferedImage bImageFromConvert;

    private APICID3V2Frame frame[];

    private ID3V2_3_0Tag tag2;
    
    private ID3V1_0Tag Tag1;
    //Metodo para obtener las etiquetas de las musicas
    
    public Tags getTags(String ruta){
        try {
                titulo = autor = album = null;
                duracion = year = 0;
                temp = 0l;
                file = new File(ruta);
                // se obtiene el formato de la cancion
                baseFileFormat = AudioSystem.getAudioFileFormat(file);
                // se obtienen las propiedades de las canciones
                properties = baseFileFormat.properties();
                // Y luego con el metodo get(); se le pasa lo que se quiere obtener
                temp = (Long) properties.get("duration");
                if(temp == null) 
                    temp = 0l;
                System.out.println("temp -> " + temp);
                duracion = (int)(temp / 1000000);
                
                titulo = (String) properties.get("title");
                //Mostrar el titulo  de la cancion en caso de que el tag sea null 
                if(titulo .equals("                              ")){ 
                	titulo = file.getName();
                	titulo=titulo.replace(".mp3", "");
                }
                autor = (String) properties.get("author");
                //renicio la variable autor
                if(autor.equals("                              "))autor="";
                album = (String) properties.get("album");
                //year = Integer.parseInt((String) properties.get("date"));
                
                try {

                    mediaFile = null;
                    arrayByte = null;
                    mediaFile = new MP3File(new File(ruta));

                    if(mediaFile != null)
                        for (Object obj : mediaFile.getTags()) {
                            if (obj  instanceof ID3V2_3_0Tag) {

                                tag2 = (ID3V2_3_0Tag) obj;
                                if (arrayByte == null && tag2.getAPICFrames() != null && tag2.getAPICFrames().length > 0) {
                                    // Simply take the first image that is available.

                                    frame = tag2.getAPICFrames();
                                    for(int i = 0; i<tag2.getAPICFrames().length; i++){
                                        if(frame[i] != null){
                                            arrayByte = frame[i].getPictureData();
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    
                } catch (ID3Exception ex) {
                    System.out.println("problemas con CoverArt");
                }
                
                
               /* System.out.println("Duracion -> " + duracion );
                System.out.println("titulo -> " + titulo);
                System.out.println("autor -> " + autor);
                System.out.println("album -> " + album);
                System.out.println("año -> " + año);*/
                
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this;
    }
    

    public Image getImageCover(){
        if(arrayByte != null){
            try {
                in = new ByteArrayInputStream(arrayByte);
                bImageFromConvert = ImageIO.read(in);
                if(bImageFromConvert != null)
                    return Toolkit.getDefaultToolkit().createImage(bImageFromConvert.getSource());
                return null;
            } catch (IOException ex) {
                System.out.println("problemas en getBufferImageCover");
            }
        }
        return null;
    }
    
    // Metodos getters de las etiquetas
    
    public String getAlbum() {
        return album;
    }

    public String getAutor() {
        return autor;
    }

    public int getYear() {
        return year;
    }

    public int getDuracion() {
        return duracion;
    }

    public String getTitulo() {
        return titulo;
    }
    
    
    
}
