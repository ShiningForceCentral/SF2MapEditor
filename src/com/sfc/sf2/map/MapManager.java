/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.MapBlockManager;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.gui.MapPanel;
import com.sfc.sf2.map.io.DisassemblyManager;
import com.sfc.sf2.map.io.RawImageManager;
import com.sfc.sf2.map.layout.DisassemblyException;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.MapLayoutManager;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;

/**
 *
 * @author wiz
 */
public class MapManager {
    
    private MapBlockManager mapBlockManager = new MapBlockManager();
    private MapLayoutManager mapLayoutManager = new MapLayoutManager();
    private Map map;
    
    public void importDisassembly(String incbinPath, String paletteEntriesPath, String tilesetEntriesPath, String tilesetsFilePath, String blocksPath, String layoutPath, String areasPath, String flagCopiesPath, String stepCopiesPath, String layer2CopiesPath, String warpsPath, String chestItemsPath, String otherItemsPath, String animationsPath)
            throws DisassemblyException {
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
    
    public void exportPngMap(Map map, String filepath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        RawImageManager.exportMapAsRawImage(map.getLayout(), filepath, com.sfc.sf2.graphics.io.RawImageManager.FILE_FORMAT_PNG);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }
    
    public void exportPngBlockset(MapBlockLayout mapblockLayout, String filepath, String hpFilepath, int blocksPerRow) {
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        mapBlockManager.setBlocks(map.getBlocks());
        mapBlockManager.exportPng(filepath, hpFilepath, blocksPerRow);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }  
    
    public void exportGifBlockset(MapBlockLayout mapblockLayout, String filepath, String hpFilepath, int blocksPerRow) {
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting GIF ...");
        mapBlockManager.setBlocks(map.getBlocks());
        mapBlockManager.exportGif(filepath, hpFilepath, blocksPerRow);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - GIF exported.");       
    }
    
    public void exportPngMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting PNG ...");
        RawImageManager.exportImageMapLayout(mapPanel, filepath, flagsPath, hpTilesPath, com.sfc.sf2.graphics.io.RawImageManager.FILE_FORMAT_PNG);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - PNG exported.");       
    }
    
    public void exportGifMapLayout(MapPanel mapPanel, String filepath, String flagsPath, String hpTilesPath){
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - Exporting GIF ...");
        RawImageManager.exportImageMapLayout(mapPanel, filepath, flagsPath, hpTilesPath, com.sfc.sf2.graphics.io.RawImageManager.FILE_FORMAT_GIF);
        System.out.println("com.sfc.sf2.maplayout.MapEditor.exportPng() - GIF exported.");       
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
    
}
