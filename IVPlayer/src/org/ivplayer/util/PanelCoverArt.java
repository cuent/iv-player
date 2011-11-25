
package org.ivplayer.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;

/**
 *
 * @author Edgar
 */
public class PanelCoverArt extends javax.swing.JPanel {

    private ImageIcon image;
    private final int dimensionImage = 150;
    private Dimension dimension;
    private int a, b, x, y;
    
    /** Creates new form PanelCoverArt */
    public PanelCoverArt(ImageIcon image) {
        initComponents();
        //this.setBackground(Color.BLACK);
        this.image = image;
        this.setSize(dimensionImage, dimensionImage);
    }



    public void setImage(ImageIcon image){
        this.image = image;
        this.repaint();
    }

    public Dimension centrarImage(){
        a = this.getWidth() / 2;
        b = dimensionImage / 2;
        x = a - b;

        a = this.getHeight() / 2;
        y = a - b;
        return (dimension = new Dimension(x, y));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        centrarImage();
        g.drawImage(image.getImage(), dimension.width, dimension.height, dimensionImage, dimensionImage, this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
