/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.compressed.StackGraphicsDecoder;
import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapAnimation;
import com.sfc.sf2.map.MapAnimationFrame;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapFlagCopy;
import com.sfc.sf2.map.MapItem;
import com.sfc.sf2.map.MapLayer2Copy;
import com.sfc.sf2.map.MapStepCopy;
import com.sfc.sf2.map.MapWarp;
import com.sfc.sf2.palette.graphics.PaletteDecoder;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
public class SFCDBankManager {
    
    public static MapArea[] importAreas(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        MapArea[] areas = null;
        try {
            List<MapArea> areaList = new ArrayList();
            int cursor = 0;
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
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static MapFlagCopy[] importFlagCopies(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importFlagCopies() - Importing disassembly ...");
        MapFlagCopy[] flagCopies = null;
        try {            
            List<MapFlagCopy> flagCopyList = new ArrayList();
            
            int cursor = 0;
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
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importFlagCopies() - Disassembly imported.");  
        return flagCopies;
    }
    
    public static MapStepCopy[] importStepCopies(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importStepCopies() - Importing disassembly ...");
        MapStepCopy[] stepCopies = null;
        try {
            List<MapStepCopy> stepCopyList = new ArrayList();
            
            int cursor = 0;
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
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importStepCopies() - Disassembly imported.");  
        return stepCopies;
    }
 
    public static MapLayer2Copy[] importLayer2Copies(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importLayer2Copies() - Importing disassembly ...");
        MapLayer2Copy[] layer2Copies = null;
        try {
            List<MapLayer2Copy> layer2CopyList = new ArrayList();
                
            int cursor = 0;
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
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importLayer2Copies() - Disassembly imported.");  
        return layer2Copies;
    }
    
    public static MapWarp[] importWarps(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importWarps() - Importing disassembly ...");
        MapWarp[] warps = null;
        try {
            List<MapWarp> warpList = new ArrayList();
             
            int cursor = 0;
            while(true){
                int triggerX = data[cursor];
                int triggerY = data[cursor+1];
                if((triggerX == -1 && triggerY==-1) || (cursor+8) > data.length){
                    break;
                }
                MapWarp warp = new MapWarp();
                warp.setTriggerX(data[cursor]);
                warp.setTriggerY(data[cursor+1]);
                warp.setDestMap(Byte.toString(data[cursor+3]));
                warp.setDestX(data[cursor+4]);
                warp.setDestY(data[cursor+5]);
                warp.setFacing(Byte.toString(data[cursor+6]));
                warpList.add(warp);
                cursor += 8;
            }
            
            warps = new MapWarp[warpList.size()];
            warps = warpList.toArray(warps);
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importWarps() - Disassembly imported.");  
        return warps;
    }
    
    public static MapItem[] importChestItems(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Importing disassembly ...");
        MapItem[] items = null;
        try {
            List<MapItem> itemList = new ArrayList();
            
            int cursor = 0;
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
                item.setItem(Integer.toString(data[cursor+3]&0xFF));
                itemList.add(item);
                cursor += 4;
            }
            
            items = new MapItem[itemList.size()];
            items = itemList.toArray(items);
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Disassembly imported.");  
        return items;
    }
    
    public static MapItem[] importOtherItems(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Importing disassembly ...");
        MapItem[] items = null;
        try {
            List<MapItem> itemList = new ArrayList();
            
            int cursor = 0;
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
                item.setItem(Integer.toString(data[cursor+3]&0xFF));
                itemList.add(item);
                cursor += 4;
            }
            
            items = new MapItem[itemList.size()];
            items = itemList.toArray(items);
            
        } catch (Exception ex) {
            Logger.getLogger(SFCDBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Disassembly imported.");  
        return items;
    }
    
    public static MapAnimation importAnimation(byte[] data){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - Importing disassembly ...");
        MapAnimation anim = new MapAnimation();
        try {
            MapAnimationFrame[] frames = null;
            List<MapAnimationFrame> frameList = new ArrayList();
              
            int cursor = 0;
            anim.setTileset(getNextWord(data,cursor+0));
            anim.setLength(getNextWord(data,cursor+2));
            cursor = 4;
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
            
        } catch (Exception ex) {
            //Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - WARNING : "+ex.getMessage());
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - Disassembly imported.");  
        return anim;
    }
    
    private static int valueOf(String s){
        s = s.trim();
        if(s.startsWith("$")){
            return Integer.valueOf(s.substring(1),16);
        }else{
            return Integer.valueOf(s);
        }
    }
    
}
