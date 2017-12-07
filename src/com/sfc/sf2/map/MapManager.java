/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.graphics.GraphicsManager;
import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.MapBlockManager;
import com.sfc.sf2.map.io.DisassemblyManager;
import com.sfc.sf2.map.io.PngManager;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.MapLayoutManager;
import com.sfc.sf2.palette.PaletteManager;
import java.awt.Color;

/**
 *
 * @author wiz
 */
public class MapManager {
       
    private MapLayoutManager mapLayoutManager = new MapLayoutManager();
    private Map map;
    
    public void importDisassembly(String palettesPath, String tilesetsPath, String tilesetsFilePath, String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath){
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Importing disassembly ...");
        mapLayoutManager.importDisassembly(palettesPath, tilesetsPath, tilesetsFilePath, blocksPath, layoutPath);
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
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath){
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Exporting disassembly ...");
        mapLayoutManager.exportDisassembly(blocksPath, layoutPath);
        DisassemblyManager.exportAreas(map.getAreas(), areasPath);
        DisassemblyManager.exportFlagCopies(map.getFlagCopies(), flagCopiesPath);
        DisassemblyManager.exportStepCopies(map.getStepCopies(), stepCopiesPath);
        DisassemblyManager.exportLayer2Copies(map.getLayer2Copies(), layer2CopiesPath);
        DisassemblyManager.exportWarps(map.getWarps(), warpsPath);
        System.out.println("com.sfc.sf2.map.MapManager.importDisassembly() - Disassembly exported.");        
    }      
    
    public void exportPng(String filepath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        PngManager.exportPng(map, filepath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
    
    
    
}
