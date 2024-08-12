package com.ghostwalker18.scheduledesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageView extends JPanel {
    private BufferedImage image = null;
    /*@Override
    public Dimension getPreferredSize() {
        return new Dimension(350,200);
    }*/

    @Override
    public Dimension getMinimumSize(){
        return new Dimension(10, 10);
    }

    @Override
    public Dimension getMaximumSize(){
        return new Dimension(1000, 1000);
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int width = getParent().getWidth()/ 2;
        int height = getParent().getHeight();
        if(image != null)
            g.drawImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, this);

        this.setSize(new Dimension(width, height));
    }
    public void setImage(BufferedImage image){
        this.image = image;
    }
}
