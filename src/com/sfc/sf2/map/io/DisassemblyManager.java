/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.map.MapAnimation;
import com.sfc.sf2.map.MapAnimationFrame;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapFlagCopy;
import com.sfc.sf2.map.MapItem;
import com.sfc.sf2.map.MapLayer2Copy;
import com.sfc.sf2.map.MapStepCopy;
import com.sfc.sf2.map.MapWarp;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    

    public static MapArea[] importAreas(String areasPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        MapArea[] areas = null;
        try {
            int cursor = 0;
            Path areaspath = Paths.get(areasPath);
            byte[] data = Files.readAllBytes(areaspath);
            List<MapArea> areaList = new ArrayList();
            while(true){
                int layer1StartX = getNextWord(data, cursor);
                if(layer1StartX == -1 || (cursor+30) > data.length){
                    break;
                }
                MapArea area = new MapArea();
                area.setLayer1StartX(getNextWord(data,cursor+0));
                //System.out.println(area.getLayer1StartX());
                area.setLayer1StartY(getNextWord(data,cursor+2));
                //System.out.println(area.getLayer1StartY());
                area.setLayer1EndX(getNextWord(data,cursor+4));
                //System.out.println(area.getLayer1EndX());
                area.setLayer1EndY(getNextWord(data,cursor+6));
                //System.out.println(area.getLayer1EndY());
                area.setForegroundLayer2StartX(getNextWord(data,cursor+8));
                //System.out.println(area.getForegroundLayer2StartX());
                area.setForegroundLayer2StartY(getNextWord(data,cursor+10));
                //System.out.println(area.getForegroundLayer2StartY());
                area.setBackgroundLayer2StartX(getNextWord(data,cursor+12));
                //System.out.println(area.getBackgroundLayer2StartX());
                area.setBackgroundLayer2StartY(getNextWord(data,cursor+14));
                //System.out.println(area.getBackgroundLayer2StartY());
                area.setLayer1ParallaxX(getNextWord(data,cursor+16));
                //System.out.println(area.getLayer1ParallaxX());
                area.setLayer1ParallaxY(getNextWord(data,cursor+18));
                //System.out.println(area.getLayer1ParallaxY());
                area.setLayer2ParallaxX(getNextWord(data,cursor+20));
                //System.out.println(area.getLayer2ParallaxX());
                area.setLayer2ParallaxY(getNextWord(data,cursor+22));
                //System.out.println(area.getLayer2ParallaxY());
                area.setLayer1AutoscrollX(data[cursor+24]);
                //System.out.println(area.getLayer1AutoscrollX());
                area.setLayer1AutoscrollY(data[cursor+25]);
                //System.out.println(area.getLayer1AutoscrollY());
                area.setLayer2AutoscrollX(data[cursor+26]);
                //System.out.println(area.getLayer2AutoscollX());
                area.setLayer2AutoscrollY(data[cursor+27]);
                //System.out.println(area.getLayer2AutoscrollY());
                area.setLayerType(data[cursor+28]);
                //System.out.println(area.getLayerType());
                area.setDefaultMusic(data[cursor+29]);
                //System.out.println(area.getDefaultMusic());
                
                areaList.add(area);
                cursor += 30;
            }
            
            areas = new MapArea[areaList.size()];
            areas = areaList.toArray(areas);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAreas() - Disassembly imported.");  
        return areas;
    }
    
    private static short getNextWord(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor+1]);
        bb.put(data[cursor]);
        short s = bb.getShort(0);
        //System.out.println("Next input word = $"+Integer.toString(s, 16)+" / "+Integer.toString(s, 2));
        return s;
    }    
    
    public static void exportAreas(MapArea[] areas, String areasPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAreas() - Exporting disassembly ...");
        try { 
            byte[] areaBytes = produceAreaBytes(areas);
            Path areasFilepath = Paths.get(areasPath);
            Files.write(areasFilepath,areaBytes);
            System.out.println(areaBytes.length + " bytes into " + areasFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAreas() - Disassembly exported.");     
    }
    
    private static byte[] produceAreaBytes(MapArea[] areas){
        byte[] areaBytes = new byte[areas.length*30+2];
        for(int i=0;i<areas.length;i++){
            MapArea area = areas[i];
            areaBytes[i*30+1] = (byte)area.getLayer1StartX();
            areaBytes[i*30+3] = (byte)area.getLayer1StartY();
            areaBytes[i*30+5] = (byte)area.getLayer1EndX();
            areaBytes[i*30+7] = (byte)area.getLayer1EndY();
            areaBytes[i*30+9] = (byte)area.getForegroundLayer2StartX();
            areaBytes[i*30+11] = (byte)area.getForegroundLayer2StartY();
            areaBytes[i*30+13] = (byte)area.getBackgroundLayer2StartX();
            areaBytes[i*30+15] = (byte)area.getBackgroundLayer2StartY();
            areaBytes[i*30+16] = (byte)((area.getLayer1ParallaxX()&0xFF00)>>8);
            areaBytes[i*30+17] = (byte)(area.getLayer1ParallaxX()&0xFF);
            areaBytes[i*30+18] = (byte)((area.getLayer1ParallaxY()&0xFF00)>>8);
            areaBytes[i*30+19] = (byte)(area.getLayer1ParallaxY()&0xFF);
            areaBytes[i*30+20] = (byte)((area.getLayer2ParallaxX()&0xFF00)>>8);
            areaBytes[i*30+21] = (byte)(area.getLayer2ParallaxX()&0xFF);
            areaBytes[i*30+22] = (byte)((area.getLayer2ParallaxY()&0xFF00)>>8);
            areaBytes[i*30+23] = (byte)(area.getLayer2ParallaxY()&0xFF);
            areaBytes[i*30+24] = (byte)area.getLayer1AutoscrollX();
            areaBytes[i*30+25] = (byte)area.getLayer1AutoscrollY();
            areaBytes[i*30+26] = (byte)area.getLayer2AutoscrollX();
            areaBytes[i*30+27] = (byte)area.getLayer2AutoscrollY();
            areaBytes[i*30+28] = (byte)area.getLayerType();
            areaBytes[i*30+29] = (byte)area.getDefaultMusic();
        }
        areaBytes[areaBytes.length-2] = -1;
        areaBytes[areaBytes.length-1] = -1;
        return areaBytes;
    }
    
    
    public static MapFlagCopy[] importFlagCopies(String flagCopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importFlagCopies() - Importing disassembly ...");
        MapFlagCopy[] flagCopies = null;
        try {
            int cursor = 0;
            Path flagcopiespath = Paths.get(flagCopiesPath);
            byte[] data = Files.readAllBytes(flagcopiespath);
            List<MapFlagCopy> flagCopyList = new ArrayList();
            while(true){
                int destX = getNextWord(data, cursor);
                if(destX == -1 || (cursor+8) > data.length){
                    break;
                }
                MapFlagCopy flagCopy = new MapFlagCopy();
                flagCopy.setFlag(getNextWord(data,cursor+0));
                flagCopy.setSourceX(data[cursor+2]);
                flagCopy.setSourceY(data[cursor+3]);
                flagCopy.setWidth(data[cursor+4]);
                flagCopy.setHeight(data[cursor+5]);
                flagCopy.setDestX(data[cursor+6]);
                flagCopy.setDestY(data[cursor+7]);
                flagCopyList.add(flagCopy);
                cursor += 8;
            }
            
            flagCopies = new MapFlagCopy[flagCopyList.size()];
            flagCopies = flagCopyList.toArray(flagCopies);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importFlagCopies() - Disassembly imported.");  
        return flagCopies;
    }
    
    public static void exportFlagCopies(MapFlagCopy[] flagCopies, String flagCopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportFlagCopies() - Exporting disassembly ...");
        try { 
            byte[] flagCopyBytes = produceFlagCopyBytes(flagCopies);
            Path flagCopyFilepath = Paths.get(flagCopiesPath);
            Files.write(flagCopyFilepath,flagCopyBytes);
            System.out.println(flagCopyBytes.length + " bytes into " + flagCopyFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportFlagCopies() - Disassembly exported.");     
    }
    
    private static byte[] produceFlagCopyBytes(MapFlagCopy[] flagCopies){
        byte[] flagCopyBytes = new byte[flagCopies.length*8+2];
        for(int i=0;i<flagCopies.length;i++){
            MapFlagCopy flagCopy = flagCopies[i];
            flagCopyBytes[i*8] = (byte)((flagCopy.getFlag()&0xFF00)>>8);
            flagCopyBytes[i*8+1] = (byte)(flagCopy.getFlag()&0xFF);
            flagCopyBytes[i*8+2] = (byte)flagCopy.getSourceX();
            flagCopyBytes[i*8+3] = (byte)flagCopy.getSourceY();
            flagCopyBytes[i*8+4] = (byte)flagCopy.getWidth();
            flagCopyBytes[i*8+5] = (byte)flagCopy.getHeight();
            flagCopyBytes[i*8+6] = (byte)flagCopy.getDestX();
            flagCopyBytes[i*8+7] = (byte)flagCopy.getDestY();
        }
        flagCopyBytes[flagCopyBytes.length-2] = -1;
        flagCopyBytes[flagCopyBytes.length-1] = -1;
        return flagCopyBytes;
    }      
    
    public static MapStepCopy[] importStepCopies(String stepCopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importStepCopies() - Importing disassembly ...");
        MapStepCopy[] stepCopies = null;
        try {
            int cursor = 0;
            Path stepcopiespath = Paths.get(stepCopiesPath);
            byte[] data = Files.readAllBytes(stepcopiespath);
            List<MapStepCopy> stepCopyList = new ArrayList();
            while(true){
                int triggerX = data[cursor];
                if(triggerX == -1 || (cursor+8) > data.length){
                    break;
                }
                MapStepCopy stepCopy = new MapStepCopy();
                stepCopy.setTriggerX(data[cursor]);
                stepCopy.setTriggerY(data[cursor+1]);
                stepCopy.setSourceX(data[cursor+2]);
                stepCopy.setSourceY(data[cursor+3]);
                stepCopy.setWidth(data[cursor+4]);
                stepCopy.setHeight(data[cursor+5]);
                stepCopy.setDestX(data[cursor+6]);
                stepCopy.setDestY(data[cursor+7]);
                stepCopyList.add(stepCopy);
                cursor += 8;
            }
            
            stepCopies = new MapStepCopy[stepCopyList.size()];
            stepCopies = stepCopyList.toArray(stepCopies);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importStepCopies() - Disassembly imported.");  
        return stepCopies;
    }
    
    public static void exportStepCopies(MapStepCopy[] stepCopies, String stepCopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportStepCopies() - Exporting disassembly ...");
        try { 
            byte[] stepCopyBytes = produceStepCopyBytes(stepCopies);
            Path stepCopyFilepath = Paths.get(stepCopiesPath);
            Files.write(stepCopyFilepath,stepCopyBytes);
            System.out.println(stepCopyBytes.length + " bytes into " + stepCopyFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportStepCopies() - Disassembly exported.");     
    }
    
    private static byte[] produceStepCopyBytes(MapStepCopy[] stepCopies){
        byte[] stepCopyBytes = new byte[stepCopies.length*8+2];
        for(int i=0;i<stepCopies.length;i++){
            MapStepCopy stepCopy = stepCopies[i];
            stepCopyBytes[i*8] = (byte)stepCopy.getTriggerX();
            stepCopyBytes[i*8+1] = (byte)stepCopy.getTriggerY();
            stepCopyBytes[i*8+2] = (byte)stepCopy.getSourceX();
            stepCopyBytes[i*8+3] = (byte)stepCopy.getSourceY();
            stepCopyBytes[i*8+4] = (byte)stepCopy.getWidth();
            stepCopyBytes[i*8+5] = (byte)stepCopy.getHeight();
            stepCopyBytes[i*8+6] = (byte)stepCopy.getDestX();
            stepCopyBytes[i*8+7] = (byte)stepCopy.getDestY();
        }
        stepCopyBytes[stepCopyBytes.length-2] = -1;
        stepCopyBytes[stepCopyBytes.length-1] = -1;
        return stepCopyBytes;
    }         
    
    public static MapLayer2Copy[] importLayer2Copies(String layer2CopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importLayer2Copies() - Importing disassembly ...");
        MapLayer2Copy[] layer2Copies = null;
        try {
            int cursor = 0;
            Path layer2copiespath = Paths.get(layer2CopiesPath);
            byte[] data = Files.readAllBytes(layer2copiespath);
            List<MapLayer2Copy> layer2CopyList = new ArrayList();
            while(true){
                int triggerX = data[cursor];
                if(triggerX == -1 || (cursor+8) > data.length){
                    break;
                }
                MapLayer2Copy layer2Copy = new MapLayer2Copy();
                layer2Copy.setTriggerX(data[cursor]);
                layer2Copy.setTriggerY(data[cursor+1]);
                layer2Copy.setSourceX(data[cursor+2]);
                layer2Copy.setSourceY(data[cursor+3]);
                layer2Copy.setWidth(data[cursor+4]);
                layer2Copy.setHeight(data[cursor+5]);
                layer2Copy.setDestX(data[cursor+6]);
                layer2Copy.setDestY(data[cursor+7]);
                layer2CopyList.add(layer2Copy);
                cursor += 8;
            }
            
            layer2Copies = new MapLayer2Copy[layer2CopyList.size()];
            layer2Copies = layer2CopyList.toArray(layer2Copies);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importLayer2Copies() - Disassembly imported.");  
        return layer2Copies;
    }
    
    public static void exportLayer2Copies(MapLayer2Copy[] layer2Copies, String layer2CopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportLayer2Copies() - Exporting disassembly ...");
        try { 
            byte[] layer2CopyBytes = produceLayer2CopyBytes(layer2Copies);
            Path layer2CopyFilepath = Paths.get(layer2CopiesPath);
            Files.write(layer2CopyFilepath,layer2CopyBytes);
            System.out.println(layer2CopyBytes.length + " bytes into " + layer2CopyFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportLayer2Copies() - Disassembly exported.");     
    }
    
    private static byte[] produceLayer2CopyBytes(MapLayer2Copy[] layer2Copies){
        byte[] layer2CopyBytes = new byte[layer2Copies.length*8+2];
        for(int i=0;i<layer2Copies.length;i++){
            MapLayer2Copy layer2Copy = layer2Copies[i];
            layer2CopyBytes[i*8] = (byte)layer2Copy.getTriggerX();
            layer2CopyBytes[i*8+1] = (byte)layer2Copy.getTriggerY();
            layer2CopyBytes[i*8+2] = (byte)layer2Copy.getSourceX();
            layer2CopyBytes[i*8+3] = (byte)layer2Copy.getSourceY();
            layer2CopyBytes[i*8+4] = (byte)layer2Copy.getWidth();
            layer2CopyBytes[i*8+5] = (byte)layer2Copy.getHeight();
            layer2CopyBytes[i*8+6] = (byte)layer2Copy.getDestX();
            layer2CopyBytes[i*8+7] = (byte)layer2Copy.getDestY();
        }
        layer2CopyBytes[layer2CopyBytes.length-2] = -1;
        layer2CopyBytes[layer2CopyBytes.length-1] = -1;
        return layer2CopyBytes;
    }        
    
    public static MapWarp[] importWarps(String warpsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importWarps() - Importing disassembly ...");
        MapWarp[] warps = null;
        try {
            int cursor = 0;
            Path warpspath = Paths.get(warpsPath);
            byte[] data = Files.readAllBytes(warpspath);
            List<MapWarp> warpList = new ArrayList();
            while(true){
                int triggerX = data[cursor];
                int triggerY = data[cursor+1];
                if((triggerX == -1 && triggerY==-1) || (cursor+8) > data.length){
                    break;
                }
                MapWarp warp = new MapWarp();
                warp.setTriggerX(data[cursor]);
                warp.setTriggerY(data[cursor+1]);
                warp.setDestMap(data[cursor+3]);
                warp.setDestX(data[cursor+4]);
                warp.setDestY(data[cursor+5]);
                warp.setFacing(data[cursor+6]);
                warpList.add(warp);
                cursor += 8;
            }
            
            warps = new MapWarp[warpList.size()];
            warps = warpList.toArray(warps);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importWarps() - Disassembly imported.");  
        return warps;
    }
    
    public static void exportWarps(MapWarp[] warps, String warpsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportWarps() - Exporting disassembly ...");
        try { 
            byte[] warpBytes = produceWarpBytes(warps);
            Path warpFilepath = Paths.get(warpsPath);
            Files.write(warpFilepath,warpBytes);
            System.out.println(warpBytes.length + " bytes into " + warpFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportWarps() - Disassembly exported.");     
    }
    
    private static byte[] produceWarpBytes(MapWarp[] warps){
        byte[] warpBytes = new byte[warps.length*8+2];
        for(int i=0;i<warps.length;i++){
            MapWarp warp = warps[i];
            warpBytes[i*8] = (byte)warp.getTriggerX();
            warpBytes[i*8+1] = (byte)warp.getTriggerY();
            warpBytes[i*8+3] = (byte)warp.getDestMap();
            warpBytes[i*8+4] = (byte)warp.getDestX();
            warpBytes[i*8+5] = (byte)warp.getDestY();
            warpBytes[i*8+6] = (byte)warp.getFacing();
        }
        warpBytes[warpBytes.length-2] = -1;
        warpBytes[warpBytes.length-1] = -1;
        return warpBytes;
    }      
    
    public static MapItem[] importItems(String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Importing disassembly ...");
        MapItem[] items = null;
        try {
            int cursor = 0;
            Path itemspath = Paths.get(itemsPath);
            byte[] data = Files.readAllBytes(itemspath);
            List<MapItem> itemList = new ArrayList();
            while(true){
                int triggerX = data[cursor];
                int triggerY = data[cursor+1];
                if((triggerX == -1 && triggerY==-1) || (cursor+4) > data.length){
                    break;
                }
                MapItem item = new MapItem();
                item.setX(data[cursor]);
                item.setY(data[cursor+1]);
                item.setFlag(data[cursor+2]&0xFF);
                item.setItem(data[cursor+3]&0xFF);
                itemList.add(item);
                cursor += 4;
            }
            
            items = new MapItem[itemList.size()];
            items = itemList.toArray(items);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Disassembly imported.");  
        return items;
    }
    
    public static void exportItems(MapItem[] items, String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Exporting disassembly ...");
        try { 
            byte[] itemBytes = produceItemBytes(items);
            Path itemFilepath = Paths.get(itemsPath);
            Files.write(itemFilepath,itemBytes);
            System.out.println(itemBytes.length + " bytes into " + itemFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Disassembly exported.");     
    }
    
    private static byte[] produceItemBytes(MapItem[] items){
        byte[] itemBytes = new byte[items.length*8+2];
        for(int i=0;i<items.length;i++){
            MapItem item = items[i];
            itemBytes[i*4] = (byte)item.getX();
            itemBytes[i*4+1] = (byte)item.getY();
            itemBytes[i*4+2] = (byte)item.getFlag();
            itemBytes[i*4+3] = (byte)item.getItem();
        }
        itemBytes[itemBytes.length-2] = -1;
        itemBytes[itemBytes.length-1] = -1;
        return itemBytes;
    }       
    
    public static MapAnimation importAnimation(String animationPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - Importing disassembly ...");
        MapAnimation anim = new MapAnimation();
        try {
            MapAnimationFrame[] frames = null;
            int cursor = 0;
            Path animpath = Paths.get(animationPath);
            byte[] data = Files.readAllBytes(animpath);
            anim.setTileset(getNextWord(data,cursor+0));
            anim.setLength(getNextWord(data,cursor+2));
            cursor = 4;
            List<MapAnimationFrame> frameList = new ArrayList();
            while(true){
                int start = getNextWord(data,cursor+0);
                if(start == -1 || (cursor+8) > data.length){
                    break;
                }
                MapAnimationFrame frame = new MapAnimationFrame();
                frame.setStart(getNextWord(data,cursor+0));
                frame.setLength(getNextWord(data,cursor+2));
                frame.setDest(getNextWord(data,cursor+4));
                frame.setDelay(getNextWord(data,cursor+6));
                frameList.add(frame);
                cursor += 8;
            }
            
            frames = new MapAnimationFrame[frameList.size()];
            frames = frameList.toArray(frames);
            
            anim.setFrames(frames);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - Disassembly imported.");  
        return anim;
    }
    
    public static void exportAnimation(MapAnimation animation, String animationPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAnimation() - Exporting disassembly ...");
        try { 
            byte[] animBytes = produceAnimBytes(animation);
            Path animFilepath = Paths.get(animationPath);
            Files.write(animFilepath,animBytes);
            System.out.println(animBytes.length + " bytes into " + animFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAnimation() - Disassembly exported.");     
    }
    
    private static byte[] produceAnimBytes(MapAnimation anim){
        byte[] animBytes = new byte[anim.getFrames().length*8+6];
        animBytes[0] = (byte)((anim.getTileset()&0xFF00)>>8);
        animBytes[1] = (byte)(anim.getTileset()&0xFF);
        animBytes[2] = (byte)((anim.getLength()&0xFF00)>>8);
        animBytes[3] = (byte)(anim.getLength()&0xFF);
        for(int i=0;i<anim.getFrames().length;i++){
            MapAnimationFrame frame = anim.getFrames()[i];
            animBytes[4+i*8] = (byte)((frame.getStart()&0xFF00)>>8);
            animBytes[4+i*8+1] = (byte)((frame.getStart()&0xFF));
            animBytes[4+i*8+2] = (byte)((frame.getLength()&0xFF00)>>8);
            animBytes[4+i*8+3] = (byte)((frame.getLength()&0xFF));
            animBytes[4+i*8+4] = (byte)((frame.getDest()&0xFF00)>>8);
            animBytes[4+i*8+5] = (byte)((frame.getDest()&0xFF));
            animBytes[4+i*8+6] = (byte)((frame.getDelay()&0xFF00)>>8);
            animBytes[4+i*8+7] = (byte)((frame.getDelay()&0xFF));
        }
        animBytes[animBytes.length-2] = -1;
        animBytes[animBytes.length-1] = -1;
        return animBytes;
    }   
    
}
