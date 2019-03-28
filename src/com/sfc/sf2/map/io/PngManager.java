/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.layout.DefaultLayout;
import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author wiz
 */
public class PngManager {
    
    
    public static void exportPng(MapPanel mapPanel, String filepath){
        try {
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - Exporting PNG files ...");
            writePngFile(mapPanel,filepath);    
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - PNG files exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
    }    
    
    public static void writePngFile(MapPanel mapPanel, String filepath){
        try {
            BufferedImage image = mapPanel.buildImage();
            File outputfile = new File(filepath);
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
       
    
}
