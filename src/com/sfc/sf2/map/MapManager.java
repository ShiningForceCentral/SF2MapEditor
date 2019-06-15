/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.graphics.GraphicsManager;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.io.DisassemblyManager;
import com.sfc.sf2.map.io.PngManager;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.MapLayoutManager;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;

/**
 *
 * @author wiz
 */
public class MapManager {
       
    private MapLayoutManager mapLayoutManager = new MapLayoutManager();
    private Map map;
    
    public void importDisassembly(String palettesPath, String tilesetsPath, String tilesetsFilePath, String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath, String chestItemsPath, String otherItemsPath, String animationsPath){
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
    
    public void exportPngMapLayout(MapPanel mapPanel, String filepath, String hpTilesPath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        MapLayoutLayout mapLayout = new MapLayoutLayout();
        mapLayout.setMapLayout(mapPanel.getMapLayout());
        mapLayout.setBlockset(mapPanel.getBlockset());
        mapLayout.setDrawGrid(false);
        mapLayout.setDrawExplorationFlags(false);
        mapLayout.setDrawInteractionFlags(false);
        PngManager.exportPngMapLayout(mapLayout, filepath, hpTilesPath);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
    
    
    
}
