/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.graphics.GraphicsManager;
import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.io.DisassemblyManager;
import com.sfc.sf2.map.io.GifManager;
import com.sfc.sf2.map.io.PngManager;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.MapLayoutManager;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class MapManager {
       
    private MapLayoutManager mapLayoutManager = new MapLayoutManager();
    private Map map;
    
    public void importDisassembly(String incbinPath, String paletteEntriesPath, String tilesetEntriesPath, String tilesetsFilePath, String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath, String chestItemsPath, String otherItemsPath, String animationsPath){
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Importing disassembly ...");
        mapLayoutManager.importDisassemblyFromEntryFiles(incbinPath, paletteEntriesPath, tilesetEntriesPath, tilesetsFilePath, blocksPath, layoutPath);
        MapLayout layout = mapLayoutManager.getLayout();
        MapBlock[] blockset = mapLayoutManager.getBlockset();
        map = new Map();
        map.setLayout(layout);
        map.setBlocks(blockset);
        MapArea[] areas = DisassemblyManager.importAreas(areasPath);
        map.setAreas(areas);
        MapFlagCopy[] flagCopies = DisassemblyManager.importFlagCopies(flagCopiesPath);
        map.setFlagCopies(flagCopies);
        MapStepCopy[] stepCopies = DisassemblyManager.importStepCopies(stepCopiesPath);
        map.setStepCopies(stepCopies);
        MapLayer2Copy[] layer2Copies = DisassemblyManager.importLayer2Copies(layer2CopiesPath);
        map.setLayer2Copies(layer2Copies);
        MapWarp[] warps = DisassemblyManager.importWarps(warpsPath);
        map.setWarps(warps);
        MapItem[] chestItems = DisassemblyManager.importChestItems(chestItemsPath);
        map.setChestItems(chestItems);
        MapItem[] otherItems = DisassemblyManager.importOtherItems(otherItemsPath);
        map.setOtherItems(otherItems);
        MapAnimation animation = DisassemblyManager.importAnimation(animationsPath);
        map.setAnimation(animation);        
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath, String chestItemsPath, String otherItemsPath, String animationsPath){
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Exporting disassembly ...");
        mapLayoutManager.exportDisassembly(blocksPath, layoutPath);
        DisassemblyManager.exportAreas(map.getAreas(), areasPath);
        DisassemblyManager.exportFlagCopies(map.getFlagCopies(), flagCopiesPath);
        DisassemblyManager.exportStepCopies(map.getStepCopies(), stepCopiesPath);
        DisassemblyManager.exportLayer2Copies(map.getLayer2Copies(), layer2CopiesPath);
        DisassemblyManager.exportWarps(map.getWarps(), warpsPath);
        DisassemblyManager.exportChestItems(map.getChestItems(), chestItemsPath);
        DisassemblyManager.exportOtherItems(map.getOtherItems(), otherItemsPath);
        DisassemblyManager.exportAnimation(map.getAnimation(), animationsPath);
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Disassembly exported.");        
    }      
    
    public Map importSFCDBank(byte[] fileData, Tile[] tileset) throws Exception{
        System.out.println("com.sfc.sf2.map.io.SFCDBankManager.importSFCDBank() - Importing SFCD Bank file ...");
    
        final int MAINCPU_WORDRAM_LOADING_OFFSET = 0x23DE00;
        final int SUBCPU_LOADING_OFFSET = 0x10000;
        
        mapLayoutManager = new MapLayoutManager();
        
        try{
                int ptOffset =  ((fileData[0x80+0]&0xFF)<<24) + ((fileData[0x80+1]&0xFF)<<16) + ((fileData[0x80+2]&0xFF)<<8) + ((fileData[0x80+3]&0xFF)) - SUBCPU_LOADING_OFFSET;
                int pointerByte1;
                int pointerByte2;
                int pointerByte3;
                int pointerByte4;
                int pointer;
                int pointerIndex = 1;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                byte[] blockData = new byte[0x1000];
                System.arraycopy(fileData, (int)pointer, blockData, 0, 0x1000);
                pointerIndex = 2;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                byte[] layoutData = new byte[0x1000];
                System.arraycopy(fileData, (int)pointer, layoutData, 0, 0x1000);
                //mapLayoutManager.importSFCDBank(tileset, blockData, layoutData);
                mapLayoutManager.importSFCDBank(tileset, blockData, layoutData);
                map = new Map();
                map.setLayout(mapLayoutManager.getLayout());
                map.setBlocks(mapLayoutManager.getBlockset());                              
                                
                pointerIndex = 3;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                byte[] data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapArea[] areas = com.sfc.sf2.map.io.SFCDBankManager.importAreas(data);
                map.setAreas(areas);
                
                pointerIndex = 4;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapFlagCopy[] flagCopies = com.sfc.sf2.map.io.SFCDBankManager.importFlagCopies(data);
                map.setFlagCopies(flagCopies);
                
                pointerIndex = 5;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapStepCopy[] stepCopies = com.sfc.sf2.map.io.SFCDBankManager.importStepCopies(data);
                map.setStepCopies(stepCopies);
                
                pointerIndex = 6;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapLayer2Copy[] layer2Copies = com.sfc.sf2.map.io.SFCDBankManager.importLayer2Copies(data);
                map.setLayer2Copies(layer2Copies);
                
                pointerIndex = 7;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapWarp[] warps = com.sfc.sf2.map.io.SFCDBankManager.importWarps(data);
                map.setWarps(warps);
                
                pointerIndex = 8;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapItem[] chestItems = com.sfc.sf2.map.io.SFCDBankManager.importChestItems(data);
                map.setChestItems(chestItems);
                
                pointerIndex = 9;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                data = new byte[0x100];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                MapItem[] otherItems = com.sfc.sf2.map.io.SFCDBankManager.importOtherItems(data);
                map.setOtherItems(otherItems);
                
                pointerIndex = 10;
                pointerByte1 = fileData[ptOffset+2+pointerIndex*4+0]&0xFF;
                pointerByte2 = fileData[ptOffset+2+pointerIndex*4+1]&0xFF;
                pointerByte3 = fileData[ptOffset+2+pointerIndex*4+2]&0xFF;
                pointerByte4 = fileData[ptOffset+2+pointerIndex*4+3]&0xFF;
                pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - MAINCPU_WORDRAM_LOADING_OFFSET + ptOffset;
                if(pointer>0){
                    data = new byte[0x100];
                    System.arraycopy(fileData, (int)pointer, data, 0, 0x100);
                    MapAnimation animation = com.sfc.sf2.map.io.SFCDBankManager.importAnimation(data);
                    map.setAnimation(animation);
                }                     
           
        }catch(Exception e){
             System.err.println("com.sfc.sf2.map.io.SFCDBankManager.importSFCDBank() - Error while parsing graphics data : "+e);
             //e.printStackTrace();
             throw e;
        }    
        System.out.println("com.sfc.sf2.map.io.SFCDBankManager.importSFCDBank() - SFCD Bank imported.");
        return map;
    }
    
    public void exportSFCDBank(Tile[] tileset, String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath, String chestItemsPath, String otherItemsPath, String animationsPath){
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Exporting disassembly ...");
        mapLayoutManager.exportSFCDBank(tileset, blocksPath, layoutPath);
        DisassemblyManager.exportAreas(map.getAreas(), areasPath);
        DisassemblyManager.exportFlagCopies(map.getFlagCopies(), flagCopiesPath);
        DisassemblyManager.exportStepCopies(map.getStepCopies(), stepCopiesPath);
        DisassemblyManager.exportLayer2Copies(map.getLayer2Copies(), layer2CopiesPath);
        DisassemblyManager.exportWarps(map.getWarps(), warpsPath);
        DisassemblyManager.exportChestItems(map.getChestItems(), chestItemsPath);
        DisassemblyManager.exportOtherItems(map.getOtherItems(), otherItemsPath);
        if(map.getAnimation()!=null){
            DisassemblyManager.exportAnimation(map.getAnimation(), animationsPath);
        }
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Disassembly exported.");        
    }      
    
    public void exportPngMap(MapPanel mapPanel, String filepath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        PngManager.exportPngMap(mapPanel, filepath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }   
    
    public void exportPngBlockset(MapBlockLayout mapblockLayout, String filepath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        PngManager.exportPngBlockset(mapblockLayout, filepath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }  
    
    public void exportPngMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        MapLayoutLayout mapLayout = new MapLayoutLayout();
        mapLayout.setMapLayout(mapPanel.getMapLayout());
        mapLayout.setBlockset(mapPanel.getBlockset());
        mapLayout.setDrawGrid(false);
        mapLayout.setDrawExplorationFlags(false);
        mapLayout.setDrawInteractionFlags(false);
        PngManager.exportPngMapLayout(mapPanel, filepath, flagsPath, hpTilesPath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }
    
    public void exportGifMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting GIF ...");
        MapLayoutLayout mapLayout = new MapLayoutLayout();
        mapLayout.setMapLayout(mapPanel.getMapLayout());
        mapLayout.setBlockset(mapPanel.getBlockset());
        mapLayout.setDrawGrid(false);
        mapLayout.setDrawExplorationFlags(false);
        mapLayout.setDrawInteractionFlags(false);
        GifManager.exportGifMapLayout(mapPanel, filepath, flagsPath, hpTilesPath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - GIF exported.");       
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
    
}
