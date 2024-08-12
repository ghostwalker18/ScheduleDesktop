package com.ghostwalker18.scheduledesktop;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageView extends JLabel {
    public void setImage(BufferedImage image){
        this.setIcon(new ImageIcon(image));
    }
}
