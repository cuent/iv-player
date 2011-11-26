/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Interfaz.java
 *
 * Created on 02/11/2011, 05:45:01 PM
 */
package org.ivplayer.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.ivplayer.properties.PlayerProperties;
import org.ivplayer.util.ExtensionFileFilter;
import org.ivplayer.util.FileDrop;
import org.ivplayer.util.PanelCoverArt;
import org.ivplayer.util.Tags;
import org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel;

/**
 *
 * @author Edgar
 */
public class Interfaz extends javax.swing.JFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int PLAY = 0, PAUSE = 1, DETENER = 2;
    
    private int estadoIVPlayer = 0;
    
    // creamos un objeto Reproductor
    private Reproductor ivPlayer = null;
    
    // variables string para almacenar las etiquetas
    private String titulo, artista, album;
    
    // se establece el volumen predeterminado
    private double volumen = 0.5;
    // variable para saber cuando se quiere poner en silencio
    private boolean silencio = false;
    // constantes para manejar el progreso y el volumen en el slider
    private final double maxSliderProgress = 1000.0, maxSliderVolume = 100.0;
    // lista de archivos: para almacenar las rutas de las musicas insertadas
    private List<File> listaFile = new ArrayList<File>();
    
    private List<File> listaAuxFile = new ArrayList<File>();
    
    // se crea una lista de tags para obtener las etiquetas titulo, autor, album
    private List<Tags> listaTags = new ArrayList<Tags>();
    
    private Tags etiquetas = new Tags();
    
    // formatos soportados por la aplicacion
    private String formatoMusica[] = new String[]{".mp3", ".ogg", ".wav"};
    
    private String formatoLista = ".ivl";
    
    private String nombreLista;
    
    private File rutaLista;
    
    private boolean hayCambios = false;
    
    private DefaultListModel model;
    
    // manejador del indice de la musica que se está ejecutando
    private int indexMusic = 0, indexAuxMusic = 0;
    // variables para manejar el tiempo que transcurre una cancion
    private int hora, minuto, segundo, temp;
    // variables para mostrar el tiempo transcurrido
    private String str_hora, str_minuto, str_segundo;
    // variable para saber si un archivo fué abierto, si se ha hecho un seek, y tambien si es orden aleatorio
    private boolean isOpenFile = false, isSeek = false, isShuffle = false, isRepeat = false;
    // estado del reproductor: play, puse, stop....
    //private int estadoIVPlayer = 0;
    // arreglo de imagenes: aqui se guardan las imagenes que cambian como play/pause, volumen/silencio
    private ImageIcon[] arrayImagen = new ImageIcon[6];
    
    private ImageIcon imagenLista = new ImageIcon(getClass().getResource("/Imagenes/song.png"));
    
    private String about;
    
    private PanelCoverArt panelCover;
    
    private JSlider auxSliderProgress;
    
    // filtro para el fileChooser
    private FileFilter filtro;
    
    // variables para controlar que se va a abrir
    // si se va a abrir archivos de musica  = 1
    // si se va a abrir una lista           = 2
    private int abrirTipo = 0;
    
    private ResourceBundle lenguaje;
    private PlayerProperties config;
    private String idioma = "ES";
    
    /** Creates new form Interfaz */
    public Interfaz() {
        initComponents();
        ivPlayer = new Reproductor();
        ivPlayer.setReferenciaInterfaz(this);
        //volumen();
       // labelCover.setIcon(new ImageIcon(getClass().getResource("/Imagenes/coverArt.png")));
       // panelCover.setLayout(new BorderLayout());
       // panelCover.add(labelCover, BorderLayout.CENTER);
        panelImagen.setLayout(new BorderLayout());
        panelCover = new PanelCoverArt(new ImageIcon(getClass().getResource("/Imagenes/coverArt.png")));
        panelImagen.add(panelCover, BorderLayout.CENTER);
        
        arrayImagen[0] = new ImageIcon(getClass().getResource("/Imagenes/pause2.png"));
        arrayImagen[1] = new ImageIcon(getClass().getResource("/Imagenes/play2.png"));
        arrayImagen[2] = new ImageIcon(getClass().getResource("/Imagenes/volume.png"));
        arrayImagen[3] = new ImageIcon(getClass().getResource("/Imagenes/mute.png"));
        arrayImagen[4] = new ImageIcon(getClass().getResource("/Imagenes/pause1.png"));
        arrayImagen[5] = new ImageIcon(getClass().getResource("/Imagenes/play1.png"));
        
        this.setIconImage(new ImageIcon(getClass().getResource("/Imagenes/icono.png")).getImage());
        
        ManejadorEventos manejador = new ManejadorEventos();
        botonPlay_Pause.addActionListener(manejador);
        botonStop.addActionListener(manejador);
        botonBack.addActionListener(manejador);
        botonNext.addActionListener(manejador);
        botonVolume.addActionListener(manejador);
        botonAdd.addActionListener(manejador);
        botonAdd.setEnabled(false);
        
        // actionListener MenuItem
        itemAbrir.addActionListener(manejador);
        itemAbrirLista.addActionListener(manejador);
        itemPlay_Pause.addActionListener(manejador);
        itemStop.addActionListener(manejador);
        itemBack.addActionListener(manejador);
        itemNext.addActionListener(manejador);
        itemGuardar.addActionListener(manejador);
        itemGuardar.setEnabled(false);
        itemGuardarComo.addActionListener(manejador);
        itemAbout.addActionListener(manejador);
        itemSalir.addActionListener(manejador);
        
        
        ListCellRenderer renderer = new CellRenderer();
        jList.setCellRenderer(renderer);
        EventoMouse eventoMouse= new EventoMouse();
        jList.addMouseListener(eventoMouse);
        
        model = new DefaultListModel();
        
        jList.setModel(model);  
        
        sliderProgress.addMouseListener(eventoMouse);
        
        ManejadorStateChanged eventoChange = new ManejadorStateChanged();
        
        sliderProgress.addChangeListener(eventoChange);
        sliderVolume.addChangeListener(eventoChange);
        checkAleatorio.addChangeListener(eventoChange);
        itemAleatorio.addChangeListener(eventoChange);
        itemRepetir.addChangeListener(eventoChange);
        
        auxSliderProgress = new JSlider();

        ButtonGroup group = new ButtonGroup();
        itemES.setSelected(true);
        group.add(itemES);
        group.add(itemEN);
        
        itemES.addActionListener(manejador);
        itemEN.addActionListener(manejador);
        
        
        this.addWindowListener(new java.awt.event.WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                guardarConfig();
                if(hayCambios){
                    int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea guardar los cambios antes de Cerrar?", "Advertencia", JOptionPane.YES_NO_OPTION);
                    if(seleccion == JOptionPane.YES_OPTION){
                        guardarArchivo();
                    }
                }
                System.exit(0);
            }
            
        });
        
        leerConfig("CONFIG");
        
        // Validar con el archivo de configuracion
        cambiarLenguaje(idioma);
        
        // Arrastrar y soltar en la lista
        FileDrop fileDropPanelLista = new FileDrop(panelLista, /*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   boolean entroLista = false;
                boolean entroListaAdd = false;
                for( int i = 0; i < files.length; i++ )
                {   try
                    {   // tambien hay que validar si se arrastra un archivo de musica o lista
                        if(validarExtension(files[i].getCanonicalPath(), formatoMusica)){
                            entroListaAdd = true;
                            if(abrirTipo == 2){
                                hayCambios = true;
                                itemGuardar.setEnabled(true);
                            }
                            etiquetas = new Tags();
                            etiquetas.getTags(files[i].getCanonicalPath());
                            listaTags.add(etiquetas);
                            setEtiquetas(etiquetas);
                            
                            model.addElement(artista + " - " + titulo);  

                            listaFile.add(files[i]);
                            listaAuxFile.add(files[i]);
                            System.out.println(files[i].getCanonicalPath());
                        }
                        if(validarExtension(files[i].getCanonicalPath(), formatoLista)){
                            
                            
                            if(entroLista == false){
                                model.clear();
                                listaFile.clear();
                                listaAuxFile.clear();
                                listaTags.clear();
                                indexMusic = 0;
                                botonAdd.setEnabled(false);
                                hayCambios = false;
                                itemGuardar.setEnabled(false);
                            }
                            
                            leerLista(files[i]);
                            
                            abrirTipo = 2;
                            entroLista = true;
                            entroListaAdd = false;
                        }
                    }   // end try
                
                    catch( java.io.IOException e ) {}
                }   // end for: through each dropped file
                
                if(entroLista){
                    stop();
                    if(listaFile.size() > 0){
                        botonAdd.setEnabled(true);
                        jList.setSelectedIndex(indexMusic);
                        reproductorAbrirArchivo();
                        play();
                    }
                }
                if(entroListaAdd == true && ivPlayer.getStatus() == DETENER)
                    musicaSiguiente();
                if(entroListaAdd == true && abrirTipo == 0){
                        abrirTipo = 3;
                        botonAdd.setEnabled(true);
                        jList.setSelectedIndex(indexMusic);
                        reproductorAbrirArchivo();
                        play();
                }
            }   // end filesDropped
        }); // end FileDrop.Listener
        
        // Arrastrar y soltar en el Panel de la Caratula
        FileDrop fileDropPanelImagen = new FileDrop(panelPrincipal, /*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   model.clear();
                listaFile.clear();
                listaAuxFile.clear();
                listaTags.clear();
                indexMusic = 0;
                botonAdd.setEnabled(false);
                for( int i = 0; i < files.length; i++ )
                {   try
                    {  
                        if(validarExtension(files[i].getCanonicalPath(), formatoMusica)){
                            etiquetas = new Tags();
                            etiquetas.getTags(files[i].getCanonicalPath());
                            listaTags.add(etiquetas);
                            setEtiquetas(etiquetas);
                            model.addElement(artista + " - " + titulo);  
                            
                            listaFile.add(files[i]);
                            listaAuxFile.add(files[i]);
                            System.out.println(files[i].getCanonicalPath());
                        }
                    }   // end try
                    catch( java.io.IOException e ) {}
                }   // end for: through each dropped file
                stop();
                if(listaFile.size() > 0){
                    abrirTipo = 1;
                    botonAdd.setEnabled(true);
                    jList.setSelectedIndex(indexMusic);
                    reproductorAbrirArchivo();
                    play();
                }
                //play();
            }   // end filesDropped
        }); // end FileDrop.Listener
        
        
        
        habilitarComponentes(false);
        
        this.setLocationRelativeTo(null);
    }
    
    class EventoMouse extends MouseAdapter{
        
		public void mousePressed(MouseEvent e){
                     if(e.getClickCount() == 1 && e.getSource() == sliderProgress){
                         isSeek = true;
                     }
                    
                     if(e.getClickCount() == 2){
                         int index = jList.locationToIndex(e.getPoint());
                         ListModel dlm = jList.getModel();
                         Object item = dlm.getElementAt(index);;
                         jList.ensureIndexIsVisible(index);
                         indexMusic=index;
                         reproducirDeLaLista(index);
	    	     }
	    	}
                
                public void mouseReleased(MouseEvent e){
                    if(e.getSource() == sliderProgress){
                                if(isSeek){
                                   Double posValue = (sliderProgress.getValue() * 1.0) / maxSliderProgress;
                                   ivPlayer.setSeek(posValue);
                                   isSeek = false;
                               }
                    }
                }
                
                public void mouseDragged(MouseEvent e){
                     if(e.getSource() == sliderProgress){
                         isSeek = true;
                     }
                }
    }
    
    class CellRenderer implements ListCellRenderer {
        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        protected Color colores[] = new Color[3];
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            colores[0] = new Color(185, 237, 79);
            colores[1] = Color.gray;
            colores[2] = Color.black;
            
            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                            isSelected, cellHasFocus);
            
            
            if(list.getSelectedIndex() == index){
                renderer.setBackground(colores[1]);
            }
            
            if (!isSelected) {
              renderer.setForeground(Color.white);
            }
            if(index == indexMusic){
                renderer.setBackground(colores[0]);
                renderer.setForeground(colores[2]);
                renderer.setIcon(imagenLista);
            }
            list.repaint();
            return renderer;
        }
    }
      // Clase para manejar los eventos de los componentes
    
    class ManejadorEventos implements ActionListener {

        // EstadoIVPlayer con: 0 -> es cuando no esta haciendo nada o (stop)
        //                     1 -> cuando está en play
        //                     2 -> cuando está en pause
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(e.getSource() == itemAbrir){
                abrirArchivo();
            }
            
            if(e.getSource() == itemAbrirLista){
                abrirLista();
            }
            
            if(e.getSource() == botonPlay_Pause || e.getSource() == itemPlay_Pause){
                if(estadoIVPlayer == 0){
                    play();
                }
                else if(estadoIVPlayer == 1){
                    pause();
                }
                else{
                    resume();
                }
            }
            if(e.getSource() == botonStop || e.getSource() == itemStop){
                stop();
            }
            
            if(e.getSource() == botonBack || e.getSource() == itemBack){
                if(isShuffle){
                    anteriorOrdenAleatorio();
                }else{
                    if(indexMusic == 0) indexMusic = listaFile.size() - 1;
                    else{
                        indexMusic--;
                    }
                }
                
                stop();
                jList.setSelectedIndex(indexMusic);
                reproductorAbrirArchivo();
                play();
            }
            
            if(e.getSource() == botonNext || e.getSource() == itemNext){
                stop();
                musicaSiguiente();
            }
            
            if(e.getSource() == botonVolume){
                if(silencio == false){
                    volumen = 0.0;
                    botonVolume.setIcon(arrayImagen[3]);
                    botonVolume.setToolTipText(lenguaje.getString("botonVolumen"));
                    silencio = true;
                }else{
                    // el volumen se establece obteniendo el valor del slider dividido entre
                    // el maxSliderVolume que es 100
                    // esto es porque el volumen va desde 0.0 hasta 1.0
                    volumen = (double)(sliderVolume.getValue() / maxSliderVolume);
                    botonVolume.setIcon(arrayImagen[2]);
                    botonVolume.setToolTipText(lenguaje.getString("botonSilencio"));
                    silencio = false;
                }
                volumen();
            }
            
            if(e.getSource() == botonAdd){
                addArchivo();
            }
            
            if(e.getSource() == itemGuardar){
                guardarLista(rutaLista);
                itemGuardar.setEnabled(false);
            }
            
            if(e.getSource() == itemGuardarComo){
                if(abrirTipo != 2)
                    nombreLista = "nuevaLista.ivl";
                guardarArchivo();
            }
            
            if(itemES.isSelected()){
                if(!idioma.equals("ES")){
                    idioma = "ES";
                    cambiarLenguaje(idioma);
                }
            }
            
            if(itemEN.isSelected()){
                if(!idioma.equals("EN")){
                    idioma = "EN";
                    cambiarLenguaje(idioma);
                }
            }
            
            if(e.getSource() == itemAbout){
                JOptionPane.showMessageDialog(null, about);
            }
            
            
            if(e.getSource() == itemSalir){
                guardarConfig();
                if(hayCambios){
                    int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea guardar los cambios antes de Cerrar?", "Advertencia", JOptionPane.YES_NO_OPTION);
                    if(seleccion == JOptionPane.YES_OPTION){
                        guardarArchivo();
                    }
                }
                System.exit(0);
            }
        }
        
    }
    
    class ManejadorStateChanged implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
            if(e.getSource() == sliderProgress){
                   if((sliderProgress.getValue()) == sliderProgress.getMaximum()){
                        stop();
                        ivPlayer = new Reproductor(); // evitar ruido
                        ivPlayer.setReferenciaInterfaz(Interfaz.this);
                        hora = minuto = segundo = 0;
                        //cancion Siguiente
                        musicaSiguiente();

                    }
            }
            
            if(e.getSource() == sliderVolume){
                if(sliderVolume.getValue() == 0)
                    botonVolume.setIcon(arrayImagen[3]);
                else
                    botonVolume.setIcon(arrayImagen[2]);
                volumen = (double)(sliderVolume.getValue() / maxSliderVolume);
                volumen();
            }
            
            if(e.getSource() == checkAleatorio){
                    if(checkAleatorio.isSelected()){
                        isShuffle = true;
                        itemAleatorio.setSelected(true);
                        Collections.shuffle(listaAuxFile);
                    }else{
                        isShuffle = false;
                        itemAleatorio.setSelected(false);
                    }
            }
            
            if(e.getSource() == itemAleatorio){
                    if(itemAleatorio.isSelected()){
                        isShuffle = true;
                        checkAleatorio.setSelected(true);
                        Collections.shuffle(listaAuxFile);
                    }else{
                        isShuffle = false;
                        checkAleatorio.setSelected(false);
                    }
            }
            
            if(e.getSource() == itemRepetir){
                if(itemRepetir.isSelected()){
                    isRepeat = true;
                }else
                    isRepeat = false;
            }
            
        }
        
    }
    
    private void cambiarLenguaje(String id){
    	//Esto automaticamente cambia el idioma al elegido por el usuario.
    	Locale.setDefault(new Locale(id));
    	lenguaje = ResourceBundle.getBundle("org.ivplayer.properties/");
    	
        this.setTitle(lenguaje.getString("titulo"));
        menuReproducir.setText(lenguaje.getString("reproducir"));
        menuAbrir.setText(lenguaje.getString("abrir"));
        menuLenguaje.setText(lenguaje.getString("lenguaje"));
        about = lenguaje.getString("mensajeAcercaDe");
        
        itemPlay_Pause.setText(lenguaje.getString("reproducir_pausa"));
        itemStop.setText(lenguaje.getString("detener"));
        itemBack.setText(lenguaje.getString("anterior"));
        itemNext.setText(lenguaje.getString("siguiente"));
        itemAleatorio.setText(lenguaje.getString("aleatorio"));
        itemRepetir.setText(lenguaje.getString("repetir"));
        
        itemAbrir.setText(lenguaje.getString("itemAbrirMusica"));
        itemAbrirLista.setText(lenguaje.getString("itemAbrirLista"));
        
        itemGuardar.setText(lenguaje.getString("guardar"));
        itemGuardarComo.setText(lenguaje.getString("guardarComo"));
        
        itemES.setText(lenguaje.getString("espa\u00f1ol"));
        itemEN.setText(lenguaje.getString("ingles"));
        
        itemAbout.setText(lenguaje.getString("acerca"));
        itemSalir.setText(lenguaje.getString("salir"));
        
        labelTituloLista.setText(lenguaje.getString("tituloLista"));
        
        if(estadoIVPlayer == 0 || estadoIVPlayer == 2)
            botonPlay_Pause.setToolTipText(lenguaje.getString("botonReproducir"));
        if(estadoIVPlayer == 1)
            botonPlay_Pause.setToolTipText(lenguaje.getString("botonPausa"));
        
        botonStop.setToolTipText(lenguaje.getString("botonDetener"));
        botonBack.setToolTipText(lenguaje.getString("botonAnterior"));
        botonNext.setToolTipText(lenguaje.getString("botonSiguiente"));
        
        if(silencio == true) botonVolume.setToolTipText(lenguaje.getString("botonVolumen"));
        else
            botonVolume.setToolTipText(lenguaje.getString("botonSilencio"));
        
        botonAdd.setToolTipText(lenguaje.getString("botonA\u00f1adir"));
        checkAleatorio.setText(lenguaje.getString("aleatorio"));
        checkAleatorio.setToolTipText(lenguaje.getString("checkAleatorio"));
    }
    
    private void leerConfig(String id){
        config = new PlayerProperties(id);
        String aux;
        
        volumen = Double.parseDouble(config.getProperty("volumen"));
        volumen();
        sliderVolume.setValue((int)(volumen * 100));
        aux = config.getProperty("estadoVolumen");
        if(Integer.parseInt(aux) == 1)
            botonVolume.setIcon(arrayImagen[3]);
        
        aux = config.getProperty("isAleatorio");
        if(Integer.parseInt(aux) == 1){
            isShuffle = true;
            checkAleatorio.setSelected(true);
            itemAleatorio.setSelected(true);
        }
        
        aux = config.getProperty("isRepetir");
        if(Integer.parseInt(aux) == 1){
            isRepeat = true;
            itemRepetir.setSelected(true);
        }
        //idioma = config.getProperty("idioma");
        if(Locale.getDefault().getLanguage().equals("es"))
        	itemES.setSelected(true);
        else
            itemEN.setSelected(true);
        
            
    }
    
    private void guardarConfig(){
        System.out.println("entro");
        config = new PlayerProperties("CONFIG");
        config.setProperty("volumen", "" + volumen );
        if(silencio)
            config.setProperty("estadoVolumen", ""+1);
        else
            config.setProperty("estadoVolumen", ""+0);
        
        if(isShuffle)
            config.setProperty("isAleatorio", ""+1);
        else
            config.setProperty("isAleatorio", ""+0);
        
        if(isRepeat)
            config.setProperty("isRepetir", ""+1);
        else
            config.setProperty("isRepetir", ""+0);
        
        config.setProperty("idioma", idioma);
        
        try {
            java.io.FileOutputStream salida = new java.io.FileOutputStream(config.getFile().getCanonicalFile().toString().replace("\\", "/"));
            config.store(salida, "---Configuracion---");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
                Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void setImageCover(Image i){
        if(i != null){
            panelCover.setImage(new ImageIcon(i));
        }
        else
            panelCover.setImage(new ImageIcon(getClass().getResource("/Imagenes/coverArt.png")));
    }
    
    public void reproducirDeLaLista(int index){
        stop();
        ivPlayer.abrirArchivo(listaFile.get(index).getAbsolutePath());
        volumen();
        auxSliderProgress.setMaximum(listaTags.get(index).getDuracion());
        setEtiquetas(listaTags.get(index));
        labelTitulo.setText(titulo);
        setImageCover(listaTags.get(index).getImageCover());
        play();
    }
    
    
    private void setEtiquetas(Tags etiquetas){
        if(etiquetas.getTitulo() != null){
            titulo = etiquetas.getTitulo();
        }
        else
            titulo = "";
        
        if(etiquetas.getAutor() != null){
            artista = etiquetas.getAutor();
        }
        else
            artista = "";
        
        if(etiquetas.getAlbum() != null){
            album = etiquetas.getAlbum();
        }
        else
            album = "";
        
    }
    
    private boolean validarExtension(String cadena, String extension){
        if(cadena.toLowerCase().endsWith(extension)) 
            return true;
        return false;
    }
    
    private boolean validarExtension(String cadena, String[] extension){
        for(int i = 0; i<extension.length; i++){
            if(cadena.toLowerCase().endsWith(extension[i])) 
                return true;
        }
        return false;
    }
    
    // Estos metodos de play, pause, resume, stop, volumen, reproductorAbrirArchivo, musicaSiguiente
    // fueron creados ya que estos metodos se repiten o son utilizados por varios metodos
    // y de esta forma no hay que escribir el mismo codigo siempre
    
    public void play(){
        estadoIVPlayer = 1;
        botonPlay_Pause.setIcon(arrayImagen[0]);
        botonPlay_Pause.setToolTipText(lenguaje.getString("botonPausa"));
        itemPlay_Pause.setIcon(arrayImagen[4]);
        //setImageCover(listaTags.get(indexMusic).getImageCover());
        ivPlayer.play();
    }
    
    public void pause(){
        estadoIVPlayer = 2;
        botonPlay_Pause.setIcon(arrayImagen[1]);
        itemPlay_Pause.setIcon(arrayImagen[5]);
        botonPlay_Pause.setToolTipText(lenguaje.getString("botonReproducir"));
        ivPlayer.pause();
    }
    
    public void resume(){
        estadoIVPlayer = 1;
        botonPlay_Pause.setIcon(arrayImagen[0]);
        itemPlay_Pause.setIcon(arrayImagen[4]);
        ivPlayer.resume();
    }
    
    public void stop(){
        estadoIVPlayer = 0;
        botonPlay_Pause.setIcon(arrayImagen[1]);
        itemPlay_Pause.setIcon(arrayImagen[5]);
        botonPlay_Pause.setToolTipText(lenguaje.getString("botonReproducir"));
        hora = minuto = segundo = 0;
        ivPlayer.stop();
        labelTime.setText("00:00");
        // se establece el valor del progreso de la cancion en cero
        sliderProgress.setValue(0);
        auxSliderProgress.setValue(0);
    }
    
    public void volumen(){
        ivPlayer.setVolumen(volumen);
    }
    
    public void reproductorAbrirArchivo(){
        habilitarComponentes(true);
        labelTime.setText("00:00");
        ivPlayer.abrirArchivo(listaFile.get(indexMusic).getAbsolutePath());
        volumen();
        auxSliderProgress.setMaximum(listaTags.get(indexMusic).getDuracion());
        setEtiquetas(listaTags.get(indexMusic));
        labelTitulo.setText(titulo);
        setImageCover(listaTags.get(indexMusic).getImageCover());
    }
    
    // se calcula la musica siguiente
    
    public void musicaSiguiente(){
        if(isShuffle){
            if(isRepeat){
                siguienteOrdenAleatorio();
                
                //stop();
                jList.setSelectedIndex(indexMusic);
                reproductorAbrirArchivo();
                play();
            }else{
                siguienteOrdenAleatorio();
                if(indexAuxMusic != 0){
                    //stop();
                    jList.setSelectedIndex(indexMusic);
                    reproductorAbrirArchivo();
                    play();
                }else{
                    //stop();
                    jList.setSelectedIndex(indexMusic);
                    reproductorAbrirArchivo();
                }
            }
                
        }else{
            if(indexMusic == (listaFile.size() - 1)) indexMusic = 0;
            else{
                indexMusic++;
            }
            
            if(isRepeat){
                //stop();
                jList.setSelectedIndex(indexMusic);
                reproductorAbrirArchivo();
                play();
            }else{
                if(indexMusic != 0){
                    //stop();
                    jList.setSelectedIndex(indexMusic);
                    reproductorAbrirArchivo();
                    play();
                }else{
                    //stop();
                    jList.setSelectedIndex(indexMusic);
                    reproductorAbrirArchivo();
                }
            }
        }

    }
    
    
    public void siguienteOrdenAleatorio(){
        if(indexAuxMusic == (listaAuxFile.size() - 1)) indexAuxMusic = 0;
        else{
            indexAuxMusic++;
        }
        for(int i = 0; i<listaAuxFile.size(); i++){
            if(listaFile.get(i).toString().compareTo(listaAuxFile.get(indexAuxMusic).toString()) == 0){
                indexMusic = i;
                break;
            }
        }
    }
    
    public void anteriorOrdenAleatorio(){
        if(indexAuxMusic == 0) indexAuxMusic = (listaAuxFile.size() - 1);
        else{
            indexAuxMusic--;
        }
        for(int i = 0; i<listaAuxFile.size(); i++){
            if(listaFile.get(i).toString().compareTo(listaAuxFile.get(indexAuxMusic).toString()) == 0){
                indexMusic = i;
                break;
            }
        }
    }
    
    
    // Metodo para abrir el archivo de musica
    
    public void abrirArchivo(){

         File archivos[] = null;
         // se crea un fileChoser
         JFileChooser fileChooser = new JFileChooser();
         // se especifica que solo va a leer archivos
         fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         // titulo de la ventana del fileChooser
         fileChooser.setDialogTitle("Selector de archivos");
         // establecer la multiple seleccion de archivos
         // para poder cargar varias musicas de un directorio
         fileChooser.setMultiSelectionEnabled(true);
         
         // se especifica el filtro a usar
         filtro = new ExtensionFileFilter("MP3, OGG and WAV", new String[] { "mp3", "ogg", "wav" });
         
         // se asigna el tipo de filro
         fileChooser.setFileFilter(filtro);
         
         // se abre la ventana fileChooser
         // y se guarda en resultado
         int resultado = fileChooser.showOpenDialog(this);
         
         // se compara si resultado es igual al valor de la constante APPROVE_OPTION
         // es señal que el usuario le dio a abrir...
         if(resultado == JFileChooser.APPROVE_OPTION){
             if(ivPlayer.getStatus() != DETENER) {
                 stop();
             }
             
             // se obtiene un arreglo de archivos donde estan las musicas
             
             archivos = fileChooser.getSelectedFiles();
             
             // el tipo de como se abrió el archivo
             abrirTipo = 1;
             hayCambios = false;
             // se limpian las listas y variables

             listaFile.clear();
             listaAuxFile.clear();
             listaTags.clear();
             model.clear();
             indexMusic = 0;
             indexAuxMusic = 0;

             
             // aqui se añaden los archivos a la lista
             for(int i = 0; i<archivos.length; i++){
                 listaFile.add(archivos[i]);
                 listaAuxFile.add(archivos[i]);
                 
                 etiquetas = new Tags();
                 etiquetas.getTags(archivos[i].getAbsolutePath());
                 listaTags.add(etiquetas);
                 setEtiquetas(etiquetas);
                 model.addElement(artista + " - " + titulo);  
             }
             
             jList.setSelectedIndex(indexMusic);
             // se llama al metodo reproductorAbrirArchivo que está arriba
             reproductorAbrirArchivo();
             play();
             
             botonAdd.setEnabled(true);
         }
    }
    
    public void addArchivo(){
        
         abrirTipo = 3;
        
         File archivos[] = null;
         // se crea un fileChoser
         JFileChooser fileChooser = new JFileChooser();
         // se especifica que solo va a leer archivos
         fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         // titulo de la ventana del fileChooser
         fileChooser.setDialogTitle("Selector de archivos");
         // establecer la multiple seleccion de archivos
         // para poder cargar varias musicas de un directorio
         fileChooser.setMultiSelectionEnabled(true);
         
         // se especifica el filtro a usar
         filtro = new ExtensionFileFilter("MP3, OGG and WAV", new String[] { "mp3", "ogg", "wav" });
         
         // se asigna el tipo de filro
         fileChooser.setFileFilter(filtro);
         
         // se abre la ventana fileChooser
         // y se guarda en resultado
         int resultado = fileChooser.showOpenDialog(this);
         
         // se compara si resultado es igual al valor de la constante APPROVE_OPTION
         // es señal que el usuario le dio a abrir...
         if(resultado == JFileChooser.APPROVE_OPTION){
             // se obtiene un arreglo de archivos donde estan las musicas
             
             archivos = fileChooser.getSelectedFiles();
             
             if(abrirTipo == 2){
                 hayCambios = true;
                 itemGuardar.setEnabled(true);
             }
             
             
             // aqui se añaden los archivos a la lista
             for(int i = 0; i<archivos.length; i++){
                 listaFile.add(archivos[i]);
                 listaAuxFile.add(archivos[i]);
                 etiquetas = new Tags();
                 etiquetas.getTags(archivos[i].getAbsolutePath());
                 listaTags.add(etiquetas);
                 setEtiquetas(etiquetas);
                 model.addElement(artista + " - " + titulo);  
             }
             
             jList.setSelectedIndex(indexMusic);
             
         }
        
    }
    
    public void abrirLista(){
        
         File archivo = null;
         // se crea un fileChoser
         JFileChooser fileChooser = new JFileChooser();
         // se especifica que solo va a leer archivos
         fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         // titulo de la ventana del fileChooser
         fileChooser.setDialogTitle("Selector de archivos");
         
         // se especifica el filtro a usar
         filtro = new ExtensionFileFilter("ivl", new String[]{"ivl", "IVL"});
         
         // se asigna el tipo de filro
         fileChooser.setFileFilter(filtro);
         
         // se abre la ventana fileChooser
         // y se guarda en resultado
         int resultado = fileChooser.showOpenDialog(this);
         
         // se compara si resultado es igual al valor de la constante APPROVE_OPTION
         // es señal que el usuario le dio a abrir...
         if(resultado == JFileChooser.APPROVE_OPTION){
             
             abrirTipo = 2;
             hayCambios = false;
             
             archivo = fileChooser.getSelectedFile();
             
             listaFile.clear();
             listaAuxFile.clear();
             listaTags.clear();
             model.clear();
             indexMusic = 0;
             indexAuxMusic = 0;
             
             leerLista(archivo);
             
         }
        
    }
    
    private void leerLista(File archivo){
          FileReader fr = null;
          BufferedReader br = null;

          try {
              
             fr = new FileReader (archivo);
             br = new BufferedReader(fr);
             
             nombreLista = archivo.getName();
             rutaLista = archivo;
             // Lectura del fichero
             String ruta;
             File f;
             while((ruta = br.readLine()) != null){
                 f = new File(ruta);
                 listaFile.add(f);
                 listaAuxFile.add(f);
                 
                 etiquetas = new Tags();
                 etiquetas.getTags(f.getAbsolutePath());
                 listaTags.add(etiquetas);
                 setEtiquetas(etiquetas);
                 model.addElement(artista + " - " + titulo);
                 System.out.println(ruta);
             }
             if(model.size() > 0){
                 jList.setSelectedIndex(indexMusic);
                 // se llama al metodo reproductorAbrirArchivo que está arriba
                 reproductorAbrirArchivo();
                 play();
             }
                
          }
          catch(Exception e){
             e.printStackTrace();
          }finally{
             // En el finally cerramos el archivo, para asegurarnos
             // que se cierra tanto si todo va bien como si salta 
             // una excepcion.
             try{                    
                if( null != fr ){   
                   fr.close();     
                }                  
             }catch (Exception e2){ 
                e2.printStackTrace();
             }
          }
    }
    
    public void guardarArchivo(){
         File archivo = null;
         // se crea un fileChoser
         JFileChooser fileChooser = new JFileChooser();
         // se especifica que solo va a leer archivos
         fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         // titulo de la ventana del fileChooser
         fileChooser.setDialogTitle("Guardar archivo");
         
         fileChooser.setSelectedFile(new File(nombreLista));
         // se especifica el filtro a usar
         filtro = new ExtensionFileFilter("ivl", new String[]{"ivl", "IVL"});
         
         // se asigna el tipo de filro
         fileChooser.setFileFilter(filtro);
         
         // se abre la ventana fileChooser
         // y se guarda en resultado
         int resultado = fileChooser.showSaveDialog(this);
         
         // se compara si resultado es igual al valor de la constante APPROVE_OPTION
         // es señal que el usuario le dio a guaradar...
         if(resultado == JFileChooser.APPROVE_OPTION){
             archivo = fileChooser.getSelectedFile();
             nombreLista = archivo.getName();
             rutaLista = archivo;
             guardarLista(archivo);
         }
    }
    
    public void guardarLista(File archivo){
        FileWriter fw = null;
        PrintWriter pw = null;
        try
        {
            fw = new FileWriter(archivo.getCanonicalPath());
            pw = new PrintWriter(fw);

            for (int i = 0; i < listaFile.size(); i++){
                if(i != listaFile.size() - 1)
                    pw.println(listaFile.get(i).getCanonicalPath());
                else
                    pw.print(listaFile.get(i).getCanonicalPath());
            }
            
            hayCambios = false;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fw)
              fw.close();
              JOptionPane.showMessageDialog(null, "La lista se guardó con Éxito.");
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
    }
    
    // se establece el tiempo de la cancion
    // el tiempo es pasado como entero
    // por eso hay que multiplicar los minutos por 60 y luego ese valor hay que restarcelo a t
    // ya que si t = 120 quiere decir que es 00:02:00 dos minutos
    public void setTiempoCancion(int t){
        hora = minuto = segundo = 0;
        while(t >= 60){
            t = t - 60;
            minuto++;
            if(minuto == 60){
                hora++;
            }
        }
        
        segundo = t;

        if(segundo < 10)
            str_segundo = "0" + segundo;
        else
            str_segundo = "" + segundo;
        if(minuto < 10 )
            str_minuto = "0" + minuto;
        else
            str_minuto = "" + minuto;
        if(hora < 10)
            str_hora = "0" + hora;
        else
            str_hora = "" + hora;

        if(hora > 0)
            labelTime.setText(str_hora + ":" + str_minuto + ":" + str_segundo + " ");
        else
            labelTime.setText(str_minuto + ":" + str_segundo);
    }
    
    public void habilitarComponentes(boolean opcion){
        botonPlay_Pause.setEnabled(opcion);
        botonStop.setEnabled(opcion);
        botonBack.setEnabled(opcion);
        botonNext.setEnabled(opcion);
        botonVolume.setEnabled(opcion);
        itemPlay_Pause.setEnabled(opcion);
        itemStop.setEnabled(opcion);
        itemBack.setEnabled(opcion);
        itemNext.setEnabled(opcion);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        panelLista = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();
        jToolBar2 = new javax.swing.JToolBar();
        botonAdd = new javax.swing.JButton();
        checkAleatorio = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        labelTituloLista = new javax.swing.JLabel();
        panelPrincipal = new javax.swing.JPanel();
        panelImagen = new javax.swing.JPanel();
        panelControles = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        labelTime = new javax.swing.JLabel();
        botonStop = new javax.swing.JButton();
        botonBack = new javax.swing.JButton();
        botonPlay_Pause = new javax.swing.JButton();
        botonNext = new javax.swing.JButton();
        botonVolume = new javax.swing.JButton();
        sliderVolume = new javax.swing.JSlider();
        sliderProgress = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        labelTitulo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuReproducir = new javax.swing.JMenu();
        itemPlay_Pause = new javax.swing.JMenuItem();
        itemStop = new javax.swing.JMenuItem();
        itemBack = new javax.swing.JMenuItem();
        itemNext = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        itemAleatorio = new javax.swing.JCheckBoxMenuItem();
        itemRepetir = new javax.swing.JCheckBoxMenuItem();
        menuAbrir = new javax.swing.JMenu();
        itemAbrir = new javax.swing.JMenuItem();
        itemAbrirLista = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        itemGuardar = new javax.swing.JMenuItem();
        itemGuardarComo = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuLenguaje = new javax.swing.JMenu();
        itemES = new javax.swing.JRadioButtonMenuItem();
        itemEN = new javax.swing.JRadioButtonMenuItem();
        itemAbout = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        itemSalir = new javax.swing.JMenuItem();

        jScrollPane1.setViewportView(jTextPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IVPlayer / Version Beta");

        jScrollPane2.setViewportView(jList);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        botonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/add1.png"))); // NOI18N
        botonAdd.setFocusable(false);
        botonAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(botonAdd);

        checkAleatorio.setText("Aleatorio");
        checkAleatorio.setFocusable(false);
        checkAleatorio.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        checkAleatorio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(checkAleatorio);

        labelTituloLista.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelTituloLista.setText("Lista de Reproducción");
        jPanel4.add(labelTituloLista);

        javax.swing.GroupLayout panelListaLayout = new javax.swing.GroupLayout(panelLista);
        panelLista.setLayout(panelListaLayout);
        panelListaLayout.setHorizontalGroup(
            panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelListaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelListaLayout.setVerticalGroup(
            panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelImagenLayout = new javax.swing.GroupLayout(panelImagen);
        panelImagen.setLayout(panelImagenLayout);
        panelImagenLayout.setHorizontalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        panelImagenLayout.setVerticalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 141, Short.MAX_VALUE)
        );

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setFocusable(false);

        labelTime.setText("00:00");
        jToolBar1.add(labelTime);

        botonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/stop2.png"))); // NOI18N
        botonStop.setToolTipText("Detener");
        botonStop.setFocusable(false);
        botonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(botonStop);

        botonBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/back2.png"))); // NOI18N
        botonBack.setToolTipText("Anterior");
        botonBack.setFocusable(false);
        botonBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(botonBack);

        botonPlay_Pause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/play2.png"))); // NOI18N
        botonPlay_Pause.setToolTipText("Reproducir");
        botonPlay_Pause.setFocusable(false);
        botonPlay_Pause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPlay_Pause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(botonPlay_Pause);

        botonNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/next2.png"))); // NOI18N
        botonNext.setToolTipText("Siguiente");
        botonNext.setFocusable(false);
        botonNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(botonNext);

        botonVolume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/volume.png"))); // NOI18N
        botonVolume.setToolTipText("Silencio");
        botonVolume.setFocusable(false);
        botonVolume.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonVolume.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(botonVolume);

        sliderVolume.setBackground(new java.awt.Color(255, 255, 255));
        sliderVolume.setForeground(new java.awt.Color(153, 255, 0));
        sliderVolume.setFocusable(false);
        sliderVolume.setPreferredSize(new java.awt.Dimension(80, 23));
        jToolBar1.add(sliderVolume);

        jPanel1.add(jToolBar1);

        sliderProgress.setBackground(new java.awt.Color(255, 255, 255));
        sliderProgress.setMaximum(1000);
        sliderProgress.setValue(0);
        sliderProgress.setFocusable(false);

        labelTitulo.setFont(new java.awt.Font("Tahoma", 1, 12));
        labelTitulo.setText("IVPlayer");
        jPanel5.add(labelTitulo);

        javax.swing.GroupLayout panelControlesLayout = new javax.swing.GroupLayout(panelControles);
        panelControles.setLayout(panelControlesLayout);
        panelControlesLayout.setHorizontalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelControlesLayout.setVerticalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelImagen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelControles, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelControles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenu1.setText("IVPlayer");

        menuReproducir.setText("Reproducir");

        itemPlay_Pause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/play1.png"))); // NOI18N
        itemPlay_Pause.setText("Reproducir / Pausa");
        menuReproducir.add(itemPlay_Pause);

        itemStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/stop1.png"))); // NOI18N
        itemStop.setText("Detener");
        menuReproducir.add(itemStop);

        itemBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/back1.png"))); // NOI18N
        itemBack.setText("Anterior");
        menuReproducir.add(itemBack);

        itemNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/next1.png"))); // NOI18N
        itemNext.setText("Siguiente");
        menuReproducir.add(itemNext);
        menuReproducir.add(jSeparator4);

        itemAleatorio.setText("Aleatorio");
        menuReproducir.add(itemAleatorio);

        itemRepetir.setText("Repetir");
        menuReproducir.add(itemRepetir);

        jMenu1.add(menuReproducir);

        menuAbrir.setText("Abrir");

        itemAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/open1.png"))); // NOI18N
        itemAbrir.setText("Abrir Música");
        menuAbrir.add(itemAbrir);

        itemAbrirLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/openList.png"))); // NOI18N
        itemAbrirLista.setText("Abrir Lista de Reproduccón");
        menuAbrir.add(itemAbrirLista);

        jMenu1.add(menuAbrir);
        jMenu1.add(jSeparator1);

        itemGuardar.setText("Guardar");
        jMenu1.add(itemGuardar);

        itemGuardarComo.setText("Guardar como");
        jMenu1.add(itemGuardarComo);
        jMenu1.add(jSeparator2);

        menuLenguaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lenguaje.png"))); // NOI18N
        menuLenguaje.setText("Lenguaje");

        itemES.setSelected(true);
        itemES.setText("Español");
        menuLenguaje.add(itemES);

        itemEN.setText("Inglés");
        menuLenguaje.add(itemEN);

        jMenu1.add(menuLenguaje);

        itemAbout.setText("Sobre IVPlayer");
        jMenu1.add(itemAbout);
        jMenu1.add(jSeparator3);

        itemSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/exit1.png"))); // NOI18N
        itemSalir.setText("Salir");
        jMenu1.add(itemSalir);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
// Metodo para actualizar el progreso de la cancion en el slider
    public void actualizarProgreso(int p){
        if(!isSeek)
            sliderProgress.setValue(p);
    }
    
    public void actualizarAuxProgreso(int p){
        if(!isSeek)
            auxSliderProgress.setValue(p);
    }
    
    public int getValueProgress(){
        return sliderProgress.getValue();
    }
    
    public int getAuxValueProgress(){
        return sliderProgress.getValue();
    }
    
    public Tags getEtiquetas(){
        return listaTags.get(indexMusic);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    // se aplica el look and feel a las ventanas
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    UIManager.setLookAndFeel(new SubstanceRavenGraphiteGlassLookAndFeel());
                    new Interfaz().setVisible(true);

                }catch(Exception e){
                    System.out.println("Error en look and Feel");
                    e.printStackTrace();
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAdd;
    private javax.swing.JButton botonBack;
    private javax.swing.JButton botonNext;
    private javax.swing.JButton botonPlay_Pause;
    private javax.swing.JButton botonStop;
    private javax.swing.JButton botonVolume;
    private javax.swing.JCheckBox checkAleatorio;
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JMenuItem itemAbrir;
    private javax.swing.JMenuItem itemAbrirLista;
    private javax.swing.JCheckBoxMenuItem itemAleatorio;
    private javax.swing.JMenuItem itemBack;
    private javax.swing.JRadioButtonMenuItem itemEN;
    private javax.swing.JRadioButtonMenuItem itemES;
    private javax.swing.JMenuItem itemGuardar;
    private javax.swing.JMenuItem itemGuardarComo;
    private javax.swing.JMenuItem itemNext;
    private javax.swing.JMenuItem itemPlay_Pause;
    private javax.swing.JCheckBoxMenuItem itemRepetir;
    private javax.swing.JMenuItem itemSalir;
    private javax.swing.JMenuItem itemStop;
    private javax.swing.JList jList;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelTituloLista;
    private javax.swing.JMenu menuAbrir;
    private javax.swing.JMenu menuLenguaje;
    private javax.swing.JMenu menuReproducir;
    private javax.swing.JPanel panelControles;
    private javax.swing.JPanel panelImagen;
    private javax.swing.JPanel panelLista;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JSlider sliderProgress;
    private javax.swing.JSlider sliderVolume;
    // End of variables declaration//GEN-END:variables
}
