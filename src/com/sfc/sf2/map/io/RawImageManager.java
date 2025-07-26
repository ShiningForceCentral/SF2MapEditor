/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.block.io.MetaManager;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.layout.MapLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TiMMy
 */
public class RawImageManager {
    
    public static void exportMapAsRawImage(MapLayout mapLayout, String filepath, int fileFormat){
        try {
            System.out.println("com.sfc.sf2.map.io.ImageManager.exportImage() - Exporting Image files ...");
            com.sfc.sf2.map.block.io.RawImageManager.exportRawImage(mapLayout.getBlocks(), filepath, 64, fileFormat);
            System.out.println("com.sfc.sf2.map.io.ImageManager.exportImage() - Image files exported.");
        } catch (Exception ex) {
            Logger.getLogger(RawImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void exportImageMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath, int fileFormat){
        try {
            System.out.println("com.sfc.sf2.map.io.ImageManager.exportImage() - Exporting Image file ...");
            exportMapAsRawImage(mapPanel.getMap().getLayout(), filepath, fileFormat);
            writeMapLayoutFlagsFile(mapPanel.getMap().getLayout(),flagsPath);
            MetaManager.exportBlockHpTilesFile(mapPanel.getMap().getLayout().getBlocks(), mapPanel.getTilesPerRow()/3, hpTilesPath);
            System.out.println("com.sfc.sf2.map.io.ImageManager.exportImage() - Image file exported.");
        } catch (Exception ex) {
            Logger.getLogger(RawImageManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RawImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
