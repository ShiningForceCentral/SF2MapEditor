/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.layout.DefaultLayout;
import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    
    
    public static void exportPngMap(MapPanel mapPanel, String filepath){
        try {
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - Exporting PNG files ...");
            writePngMapFile(mapPanel,filepath);    
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - PNG files exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public static void writePngMapFile(MapPanel mapPanel, String filepath){
        try {
            BufferedImage image = mapPanel.buildImage(mapPanel.getMap(),mapPanel.getTilesPerRow(),true);
            File outputfile = new File(filepath);
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
       
    
    public static void exportPngBlockset(MapBlockLayout mapBlockLayout, String filepath){
        try {
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - Exporting PNG files ...");
            writePngBlocksetFile(mapBlockLayout,filepath);    
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - PNG files exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public static void writePngBlocksetFile(MapBlockLayout mapBlockLayout, String filepath){
        try {
            BufferedImage image = mapBlockLayout.buildImage(mapBlockLayout.getBlocks(),mapBlockLayout.getTilesPerRow(),true);
            File outputfile = new File(filepath);
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    

    public static void exportPngMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath){
        try {
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - Exporting PNG file ...");
            writePngMapLayoutFile(mapPanel,filepath);  
            writeMapLayoutFlagsFile(mapPanel.getMap().getLayout(),flagsPath);
            writeMapHpTilesFile(mapPanel.getMap().getLayout(),hpTilesPath);    
            System.out.println("com.sfc.sf2.map.io.PngManager.exportPng() - PNG file exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
    }    
    
    public static void writePngMapLayoutFile(MapPanel mapPanel, String filepath){
        try {
            BufferedImage image = mapPanel.buildImage(mapPanel.getMap(),mapPanel.getTilesPerRow(),true);
            File outputfile = new File(filepath);
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }    
    
    public static void writeMapLayoutFlagsFile(MapLayout mapLayout, String filepath){
        try {
            File outputfile = new File(filepath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
            StringBuilder sb = new StringBuilder();
            for(int y=0;y<64;y++){
                for(int x=0;x<64;x++){
                    int blockIndex = y*64+x;
                    int flags = mapLayout.getBlocks()[blockIndex].getFlags() >> 8;
                    String flagsString = Integer.toHexString(flags);
                    while(flagsString.length()<2){
                        flagsString="0"+flagsString;
                    }
                    //System.out.println(y+":"+x+"->"+blockIndex+":"+flagsString);
                    sb.append(flagsString);
                }
                sb.append("\n");
            }
            bw.write(sb.toString());
            bw.close();
            System.out.println("Map layout flags file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }     
    
    public static void writeMapHpTilesFile(MapLayout mapLayout, String filepath){
        try {
            File outputfile = new File(filepath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
            StringBuilder sb = new StringBuilder();
            for(int y=0;y<64*3;y++){
                for(int x=0;x<64;x++){
                    int blockIndex = (y/3)*64+x;
                    //System.out.println(y+":"+x+"->"+blockIndex+"->"+(int)((y%3)*3+0)+","+(int)((y%3)*3+1)+","+(int)((y%3)*3+2));
                    sb.append((mapLayout.getBlocks()[blockIndex].getTiles()[(y%3)*3+0].isHighPriority())?"H":"L");
                    sb.append((mapLayout.getBlocks()[blockIndex].getTiles()[(y%3)*3+1].isHighPriority())?"H":"L");
                    sb.append((mapLayout.getBlocks()[blockIndex].getTiles()[(y%3)*3+2].isHighPriority())?"H":"L");
                }
                sb.append("\n");
            }
            bw.write(sb.toString());
            bw.close();
            System.out.println("HP Tiles file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }    
}
