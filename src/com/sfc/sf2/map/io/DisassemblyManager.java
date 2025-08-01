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
public class DisassemblyManager {
    
    private static String areasHeader;
    private static String flagCopiesHeader;
    private static String stepCopiesHeader;
    private static String layer2CopiesHeader;
    private static String warpsHeader;
    private static String chestItemsHeader;
    private static String otherItemsHeader;
    private static String animationsHeader;

    public static MapArea[] importAreas(String areasPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        MapArea[] areas = null;
        try {
            List<MapArea> areaList = new ArrayList();
            if(areasPath.endsWith(".asm")){
                File file = new File(areasPath);
                Scanner scan = new Scanner(file);
                MapArea entry = null;
                boolean inHeader = true;
                areasHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("mainLayerStart")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "mainLayerStart");
                        entry = new MapArea();
                        areaList.add(entry);
                        entry.setLayer1StartX(valueOf(params[0]));
                        entry.setLayer1StartY(valueOf(params[1]));
                    }else if(trimmed.startsWith("mainLayerEnd")){
                        String[] params = parseAsmFileLine(trimmed, "mainLayerEnd");
                        entry.setLayer1EndX(valueOf(params[0]));
                        entry.setLayer1EndY(valueOf(params[1]));
                    }else if(trimmed.startsWith("scndLayerFgndStart")){
                        String[] params = parseAsmFileLine(trimmed, "scndLayerFgndStart");
                        entry.setForegroundLayer2StartX(valueOf(params[0]));
                        entry.setForegroundLayer2StartY(valueOf(params[1]));
                    }else if(trimmed.startsWith("scndLayerBgndStart")){
                        String[] params = parseAsmFileLine(trimmed, "scndLayerBgndStart");
                        entry.setBackgroundLayer2StartX(valueOf(params[0]));
                        entry.setBackgroundLayer2StartY(valueOf(params[1]));
                    }else if(trimmed.startsWith("mainLayerParallax")){
                        String[] params = parseAsmFileLine(trimmed, "mainLayerParallax");
                        entry.setLayer1ParallaxX(valueOf(params[0]));
                        entry.setLayer1ParallaxY(valueOf(params[1]));
                    }else if(trimmed.startsWith("scndLayerParallax")){
                        String[] params = parseAsmFileLine(trimmed, "scndLayerParallax");
                        entry.setLayer2ParallaxX(valueOf(params[0]));
                        entry.setLayer2ParallaxY(valueOf(params[1]));
                    }else if(trimmed.startsWith("mainLayerAutoscroll")){
                        String[] params = parseAsmFileLine(trimmed, "mainLayerAutoscroll");
                        entry.setLayer1AutoscrollX(valueOf(params[0]));
                        entry.setLayer1AutoscrollY(valueOf(params[1]));
                    }else if(trimmed.startsWith("scndLayerAutoscroll")){
                        String[] params = parseAsmFileLine(trimmed, "scndLayerAutoscroll");
                        entry.setLayer2AutoscrollX(valueOf(params[0]));
                        entry.setLayer2AutoscrollY(valueOf(params[1]));
                    }else if(trimmed.startsWith("mainLayerType")){
                        String[] params = parseAsmFileLine(trimmed, "mainLayerType");
                        entry.setLayerType(valueOf(params[0]));
                    }else if(trimmed.startsWith("areaDefaultMusic")){
                        String[] params = parseAsmFileLine(trimmed, "areaDefaultMusic");
                        String musicNumber = params[0].replace("[^0-9]", "");
                            entry.setDefaultMusic(params[0]);    //Music might be stored as an index or a named string
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            areasHeader+=line;
                            areasHeader+="\n";
                        }
                    }
                }  
                
            }else{
                int cursor = 0;
                Path areaspath = Paths.get(areasPath);
                byte[] data = Files.readAllBytes(areaspath);
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
                    area.setDefaultMusic(Byte.toString(data[cursor+29]));
                    //System.out.println(area.getDefaultMusic());

                    areaList.add(area);
                    cursor += 30;
                }
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
            if(areasPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(areasHeader);
                asm.append(produceAreasAsm(areas));
                Path path = Paths.get(areasPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] areaBytes = produceAreaBytes(areas);
                Path areasFilepath = Paths.get(areasPath);
                Files.write(areasFilepath,areaBytes);
                System.out.println(areaBytes.length + " bytes into " + areasFilepath); 
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();      
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAreas() - Disassembly exported.");     
    } 
   
    private static String produceAreasAsm(MapArea[] areas){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<areas.length;i++){
            MapArea area = areas[i];
            asm.append("                "+"    mainLayerStart"+"      "+area.getLayer1StartX()+", "+area.getLayer1StartY()+"\n");
            asm.append("                "+"    mainLayerEnd"+"        "+area.getLayer1EndX()+", "+area.getLayer1EndY()+"\n");
            asm.append("                "+"    scndLayerFgndStart"+"  "+area.getForegroundLayer2StartX()+", "+area.getForegroundLayer2StartY()+"\n");
            asm.append("                "+"    scndLayerBgndStart"+"  "+area.getBackgroundLayer2StartX()+", "+area.getBackgroundLayer2StartY()+"\n");
            asm.append("                "+"    mainLayerParallax"+"   "+area.getLayer1ParallaxX()+", "+area.getLayer1ParallaxY()+"\n");
            asm.append("                "+"    scndLayerParallax"+"   "+area.getLayer2ParallaxX()+", "+area.getLayer2ParallaxY()+"\n");
            asm.append("                "+"    mainLayerAutoscroll"+" "+area.getLayer1AutoscrollX()+", "+area.getLayer1AutoscrollY()+"\n");
            asm.append("                "+"    scndLayerAutoscroll"+" "+area.getLayer2AutoscrollX()+", "+area.getLayer2AutoscrollY()+"\n");
            asm.append("                "+"    mainLayerType"+"    "+area.getLayerType()+"\n");
            asm.append("                "+"    areaDefaultMusic"+" "+area.getDefaultMusic()+"\n");
            if(i<(areas.length-1)){
                asm.append("                \n");
            }
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
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
            areaBytes[i*30+29] = Byte.parseByte(area.getDefaultMusic().replace("[^0-9]", ""));
        }
        areaBytes[areaBytes.length-2] = -1;
        areaBytes[areaBytes.length-1] = -1;
        return areaBytes;
    }
    
    
    public static MapFlagCopy[] importFlagCopies(String flagCopiesPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importFlagCopies() - Importing disassembly ...");
        MapFlagCopy[] flagCopies = null;
        try {            
            List<MapFlagCopy> flagCopyList = new ArrayList();
            if(flagCopiesPath.endsWith(".asm")){
                File file = new File(flagCopiesPath);
                Scanner scan = new Scanner(file);
                MapFlagCopy entry = null;
                boolean inHeader = true;
                flagCopiesHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("fbcFlag")){
                        String comment = "";
                        if(trimmed.contains(";")){
                            comment = trimmed.substring(trimmed.indexOf(";")+1);
                            trimmed = trimmed.substring(0,trimmed.indexOf(";"));
                        }
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "fbcFlag");
                        entry = new MapFlagCopy();
                        flagCopyList.add(entry);
                        entry.setComment(comment);
                        entry.setFlag(valueOf(params[0]));
                    }else if(trimmed.startsWith("fbcSource")){
                        String[] params = parseAsmFileLine(trimmed, "fbcSource");
                        entry.setSourceX(valueOf(params[0]));
                        entry.setSourceY(valueOf(params[1]));
                    }else if(trimmed.startsWith("fbcSize")){
                        String[] params = parseAsmFileLine(trimmed, "fbcSize");
                        entry.setWidth(valueOf(params[0]));
                        entry.setHeight(valueOf(params[1]));
                    }else if(trimmed.startsWith("fbcDest")){
                        String[] params = parseAsmFileLine(trimmed, "fbcDest");
                        entry.setDestX(valueOf(params[0]));
                        entry.setDestY(valueOf(params[1]));
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            flagCopiesHeader+=line;
                            flagCopiesHeader+="\n";
                        }
                    }
                }  
                
            }else{
                int cursor = 0;
                Path flagcopiespath = Paths.get(flagCopiesPath);
                byte[] data = Files.readAllBytes(flagcopiespath);
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
            if(flagCopiesPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(flagCopiesHeader);
                asm.append(produceFlagCopiesAsm(flagCopies));
                Path path = Paths.get(flagCopiesPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] flagCopyBytes = produceFlagCopyBytes(flagCopies);
                Path flagCopyFilepath = Paths.get(flagCopiesPath);
                Files.write(flagCopyFilepath,flagCopyBytes);
                System.out.println(flagCopyBytes.length + " bytes into " + flagCopyFilepath); 
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportFlagCopies() - Disassembly exported.");     
    }
   
    private static String produceFlagCopiesAsm(MapFlagCopy[] flagCopies){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<flagCopies.length;i++){
            MapFlagCopy flagCopy = flagCopies[i];
            if(flagCopy.getComment()!=null&&!flagCopy.getComment().isBlank()){
                asm.append("                "+"fbcFlag"+" "+Integer.toString(flagCopy.getFlag())+"             ;"+flagCopy.getComment()+"\n");
            }else{
                asm.append("                "+"fbcFlag"+" "+Integer.toString(flagCopy.getFlag())+"\n");
            }
            asm.append("                "+"  fbcSource"+" "+flagCopy.getSourceX()+", "+flagCopy.getSourceY()+"\n");
            asm.append("                "+"  fbcSize"+"   "+flagCopy.getWidth()+", "+flagCopy.getHeight()+"\n");
            asm.append("                "+"  fbcDest"+"   "+flagCopy.getDestX()+", "+flagCopy.getDestY()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
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
            List<MapStepCopy> stepCopyList = new ArrayList();
            if(stepCopiesPath.endsWith(".asm")){
                File file = new File(stepCopiesPath);
                Scanner scan = new Scanner(file);
                MapStepCopy entry = null;
                boolean inHeader = true;
                stepCopiesHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("sbc ")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "sbc");
                        entry = new MapStepCopy();
                        stepCopyList.add(entry);
                        entry.setTriggerX(valueOf(params[0]));
                        entry.setTriggerY(valueOf(params[1]));
                    }else if(trimmed.startsWith("sbcSource")){
                        String[] params = parseAsmFileLine(trimmed, "sbcSource");
                        entry.setSourceX(valueOf(params[0]));
                        entry.setSourceY(valueOf(params[1]));
                    }else if(trimmed.startsWith("sbcSize")){
                        String[] params = parseAsmFileLine(trimmed, "sbcSize");
                        entry.setWidth(valueOf(params[0]));
                        entry.setHeight(valueOf(params[1]));
                    }else if(trimmed.startsWith("sbcDest")){
                        String[] params = parseAsmFileLine(trimmed, "sbcDest");
                        entry.setDestX(valueOf(params[0]));
                        entry.setDestY(valueOf(params[1]));
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            stepCopiesHeader+=line;
                            stepCopiesHeader+="\n";
                        }
                    }
                }  
                
            }else{
                int cursor = 0;
                Path stepcopiespath = Paths.get(stepCopiesPath);
                byte[] data = Files.readAllBytes(stepcopiespath);
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
            if(stepCopiesPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(stepCopiesHeader);
                asm.append(produceStepCopiesAsm(stepCopies));
                Path path = Paths.get(stepCopiesPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] stepCopyBytes = produceStepCopyBytes(stepCopies);
                Path stepCopyFilepath = Paths.get(stepCopiesPath);
                Files.write(stepCopyFilepath,stepCopyBytes);
                System.out.println(stepCopyBytes.length + " bytes into " + stepCopyFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportStepCopies() - Disassembly exported.");     
    }
   
    private static String produceStepCopiesAsm(MapStepCopy[] stepCopies){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<stepCopies.length;i++){
            MapStepCopy stepCopy = stepCopies[i];
            asm.append("                "+"sbc"+" "+stepCopy.getTriggerX()+", "+stepCopy.getTriggerY()+"\n");
            asm.append("                "+"  sbcSource"+" "+stepCopy.getSourceX()+", "+stepCopy.getSourceY()+"\n");
            asm.append("                "+"  sbcSize"+"   "+stepCopy.getWidth()+", "+stepCopy.getHeight()+"\n");
            asm.append("                "+"  sbcDest"+"   "+stepCopy.getDestX()+", "+stepCopy.getDestY()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
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
            List<MapLayer2Copy> layer2CopyList = new ArrayList();
            if(layer2CopiesPath.endsWith(".asm")){
                File file = new File(layer2CopiesPath);
                Scanner scan = new Scanner(file);
                MapLayer2Copy entry = null;
                boolean inHeader = true;
                layer2CopiesHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("slbc ")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "slbc");
                        entry = new MapLayer2Copy();
                        layer2CopyList.add(entry);
                        entry.setTriggerX(valueOf(params[0]));
                        entry.setTriggerY(valueOf(params[1]));
                    }else if(trimmed.startsWith("slbcSource")){
                        String[] params = parseAsmFileLine(trimmed, "slbcSource");
                        entry.setSourceX(valueOf(params[0]));
                        entry.setSourceY(valueOf(params[1]));
                    }else if(trimmed.startsWith("slbcSize")){
                        String[] params = parseAsmFileLine(trimmed, "slbcSize");
                        entry.setWidth(valueOf(params[0]));
                        entry.setHeight(valueOf(params[1]));
                    }else if(trimmed.startsWith("slbcDest")){
                        String[] params = parseAsmFileLine(trimmed, "slbcDest");
                        entry.setDestX(valueOf(params[0]));
                        entry.setDestY(valueOf(params[1]));
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            layer2CopiesHeader+=line;
                            layer2CopiesHeader+="\n";
                        }
                    }
                }  
                
            }else{    
                int cursor = 0;
                Path layer2copiespath = Paths.get(layer2CopiesPath);
                byte[] data = Files.readAllBytes(layer2copiespath);
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
            if(layer2CopiesPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(layer2CopiesHeader);
                asm.append(produceLayer2CopiesAsm(layer2Copies));
                Path path = Paths.get(layer2CopiesPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] layer2CopyBytes = produceLayer2CopyBytes(layer2Copies);
                Path layer2CopyFilepath = Paths.get(layer2CopiesPath);
                Files.write(layer2CopyFilepath,layer2CopyBytes);
                System.out.println(layer2CopyBytes.length + " bytes into " + layer2CopyFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportLayer2Copies() - Disassembly exported.");     
    }
   
    private static String produceLayer2CopiesAsm(MapLayer2Copy[] layer2Copies){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<layer2Copies.length;i++){
            MapLayer2Copy layer2Copy = layer2Copies[i];
            asm.append("                "+"slbc"+" "+layer2Copy.getTriggerX()+", "+layer2Copy.getTriggerY()+"\n");
            asm.append("                "+"  slbcSource"+" "+layer2Copy.getSourceX()+", "+layer2Copy.getSourceY()+"\n");
            asm.append("                "+"  slbcSize"+"   "+layer2Copy.getWidth()+", "+layer2Copy.getHeight()+"\n");
            asm.append("                "+"  slbcDest"+"   "+layer2Copy.getDestX()+", "+layer2Copy.getDestY()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
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
            List<MapWarp> warpList = new ArrayList();
            if(warpsPath.endsWith(".asm")){
                File file = new File(warpsPath);
                Scanner scan = new Scanner(file);
                MapWarp entry = null;
                boolean inHeader = true;
                warpsHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("mWarp")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "mWarp");
                        entry = new MapWarp();
                        warpList.add(entry);
                        entry.setTriggerX(valueOf(params[0]));
                        entry.setTriggerY(valueOf(params[1]));
                    }else if(trimmed.startsWith("warpNoScroll")){
                        entry.setScrollDirection(null);
                    }else if(trimmed.startsWith("warpScroll")){
                        String[] params = parseAsmFileLine(trimmed, "warpScroll");
                        entry.setScrollDirection(params[0]);
                    }else if(trimmed.startsWith("warpMap")){
                        String[] params = parseAsmFileLine(trimmed, "warpMap");
                        entry.setDestMap(params[0]);
                    }else if(trimmed.startsWith("warpDest")){
                        String[] params = parseAsmFileLine(trimmed, "warpDest");
                        entry.setDestX(valueOf(params[0]));
                        entry.setDestY(valueOf(params[1]));
                    }else if(trimmed.startsWith("warpFacing")){
                        String[] params = parseAsmFileLine(trimmed, "warpFacing");
                        entry.setFacing(params[0]);
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            warpsHeader+=line;
                            warpsHeader+="\n";
                        }
                    }
                }  
                
            }else{  
                int cursor = 0;
                Path warpspath = Paths.get(warpsPath);
                byte[] data = Files.readAllBytes(warpspath);
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
            if(warpsPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(warpsHeader);
                asm.append(produceWarpsAsm(warps));
                Path path = Paths.get(warpsPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] warpBytes = produceWarpBytes(warps);
                Path warpFilepath = Paths.get(warpsPath);
                Files.write(warpFilepath,warpBytes);
                System.out.println(warpBytes.length + " bytes into " + warpFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportWarps() - Disassembly exported.");     
    }
   
    private static String produceWarpsAsm(MapWarp[] warps){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<warps.length;i++){
            MapWarp warp = warps[i];
            asm.append("                "+"mWarp"+" "+warp.getTriggerX()+", "+warp.getTriggerY()+"\n");
            if(warp.getScrollDirection()!=null){
                asm.append("                "+"  warpScroll"+" "+warp.getScrollDirection()+"\n");
            }else{
                asm.append("                "+"  warpNoScroll"+"\n");
            }
            asm.append("                "+"  warpMap"+"    "+warp.getDestMap()+"\n");
            asm.append("                "+"  warpDest"+"   "+warp.getDestX()+", "+warp.getDestY()+"\n");
            asm.append("                "+"  warpFacing"+" "+warp.getFacing()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
    } 
    
    private static byte[] produceWarpBytes(MapWarp[] warps){
        byte[] warpBytes = new byte[warps.length*8+2];
        for(int i=0;i<warps.length;i++){
            MapWarp warp = warps[i];
            warpBytes[i*8] = (byte)warp.getTriggerX();
            warpBytes[i*8+1] = (byte)warp.getTriggerY();
            warpBytes[i*8+3] = (byte)warp.getDestMap().charAt(0);
            warpBytes[i*8+4] = (byte)warp.getDestX();
            warpBytes[i*8+5] = (byte)warp.getDestY();
            warpBytes[i*8+6] = (byte)warp.getFacing().charAt(0);
        }
        warpBytes[warpBytes.length-2] = -1;
        warpBytes[warpBytes.length-1] = -1;
        return warpBytes;
    }      
    
    public static MapItem[] importChestItems(String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Importing disassembly ...");
        MapItem[] items = null;
        try {
            List<MapItem> itemList = new ArrayList();
            if(itemsPath.endsWith(".asm")){
                File file = new File(itemsPath);
                Scanner scan = new Scanner(file);
                MapItem entry = null;
                boolean inHeader = true;
                chestItemsHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("mapItem")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "mapItem");
                        entry = new MapItem();
                        itemList.add(entry);
                        entry.setX(valueOf(params[0]));
                        entry.setY(valueOf(params[1]));
                        entry.setFlag(valueOf(params[2]));
                        entry.setItem(params[3]);
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            chestItemsHeader+=line;
                            chestItemsHeader+="\n";
                        }
                    }
                }  
                
            }else{  
                int cursor = 0;
                Path itemspath = Paths.get(itemsPath);
                byte[] data = Files.readAllBytes(itemspath);
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
            }
            
            items = new MapItem[itemList.size()];
            items = itemList.toArray(items);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Disassembly imported.");  
        return items;
    }
    
    public static void exportChestItems(MapItem[] items, String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Exporting disassembly ...");
        try { 
            if(itemsPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(chestItemsHeader);
                asm.append(produceChestItemsAsm(items));
                Path path = Paths.get(itemsPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] itemBytes = produceChestItemBytes(items);
                Path itemFilepath = Paths.get(itemsPath);
                Files.write(itemFilepath,itemBytes);
                System.out.println(itemBytes.length + " bytes into " + itemFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Disassembly exported.");     
    }
   
    private static String produceChestItemsAsm(MapItem[] items){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<items.length;i++){
            MapItem item = items[i];
            asm.append("                "+"mapItem"+" "+item.getX()+", "+item.getY()+", "+Integer.toString(item.getFlag())+", "+item.getItem()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
    } 
    
    private static byte[] produceChestItemBytes(MapItem[] items){
        byte[] itemBytes = new byte[items.length*4+2];
        for(int i=0;i<items.length;i++){
            MapItem item = items[i];
            itemBytes[i*4] = (byte)item.getX();
            itemBytes[i*4+1] = (byte)item.getY();
            itemBytes[i*4+2] = (byte)item.getFlag();
            itemBytes[i*4+3] = (byte)item.getItem().charAt(0);
        }
        itemBytes[itemBytes.length-2] = -1;
        itemBytes[itemBytes.length-1] = -1;
        return itemBytes;
    }     
    
    public static MapItem[] importOtherItems(String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Importing disassembly ...");
        MapItem[] items = null;
        try {
            List<MapItem> itemList = new ArrayList();
            if(itemsPath.endsWith(".asm")){
                File file = new File(itemsPath);
                Scanner scan = new Scanner(file);
                MapItem entry = null;
                boolean inHeader = true;
                otherItemsHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("mapItem")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "mapItem");
                        entry = new MapItem();
                        itemList.add(entry);
                        entry.setX(valueOf(params[0]));
                        entry.setY(valueOf(params[1]));
                        entry.setFlag(valueOf(params[2]));
                        entry.setItem(params[3]);
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            otherItemsHeader+=line;
                            otherItemsHeader+="\n";
                        }
                    }
                }  
                
            }else{  
                int cursor = 0;
                Path itemspath = Paths.get(itemsPath);
                byte[] data = Files.readAllBytes(itemspath);
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
            }
            
            items = new MapItem[itemList.size()];
            items = itemList.toArray(items);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importItems() - Disassembly imported.");  
        return items;
    }
    
    public static void exportOtherItems(MapItem[] items, String itemsPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Exporting disassembly ...");
        try { 
            if(itemsPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(otherItemsHeader);
                asm.append(produceOtherItemsAsm(items));
                Path path = Paths.get(itemsPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] itemBytes = produceOtherItemBytes(items);
                Path itemFilepath = Paths.get(itemsPath);
                Files.write(itemFilepath,itemBytes);
                System.out.println(itemBytes.length + " bytes into " + itemFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportItems() - Disassembly exported.");     
    }
   
    private static String produceOtherItemsAsm(MapItem[] items){
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<items.length;i++){
            MapItem item = items[i];
            asm.append("                "+"mapItem"+" "+item.getX()+", "+item.getY()+", "+Integer.toString(item.getFlag())+", "+item.getItem()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
    } 
    
    private static byte[] produceOtherItemBytes(MapItem[] items){
        byte[] itemBytes = new byte[items.length*4+2];
        for(int i=0;i<items.length;i++){
            MapItem item = items[i];
            itemBytes[i*4] = (byte)item.getX();
            itemBytes[i*4+1] = (byte)item.getY();
            itemBytes[i*4+2] = (byte)item.getFlag();
            itemBytes[i*4+3] = (byte)item.getItem().charAt(0);
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
            List<MapAnimationFrame> frameList = new ArrayList();
            if(animationPath.endsWith(".asm")){
                File file = new File(animationPath);
                Scanner scan = new Scanner(file);
                MapAnimationFrame entry = null;
                boolean inHeader = true;
                animationsHeader = "";
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String trimmed = line.trim();
                    if(trimmed.startsWith("mapAnimation")){
                        inHeader = false;
                        String[] params = parseAsmFileLine(trimmed, "mapAnimation");
                        anim.setTileset(valueOf(params[0]));
                        anim.setLength(valueOf(params[1]));
                    }else if(trimmed.startsWith("mapAnimEntry")){
                        entry = new MapAnimationFrame();
                        frameList.add(entry);
                        String[] params = parseAsmFileLine(trimmed, "mapAnimEntry");
                        entry.setStart(valueOf(params[0]));
                        entry.setLength(valueOf(params[1]));
                        entry.setDest(valueOf(params[2]));
                        entry.setDelay(valueOf(params[3]));
                    }else if (trimmed.startsWith("endWord")){
                        inHeader = false;
                        break;
                    }else{
                        if(inHeader){
                            animationsHeader+=line;
                            animationsHeader+="\n";
                        }
                    }
                }  
                
            }else{  
                int cursor = 0;
                Path animpath = Paths.get(animationPath);
                byte[] data = Files.readAllBytes(animpath);
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
            }
            
            frames = new MapAnimationFrame[frameList.size()];
            frames = frameList.toArray(frames);
            
            anim.setFrames(frames);
            
        } catch (IOException ex) {
            //Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - WARNING : "+ex.getMessage());
        }
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.importAnimation() - Disassembly imported.");  
        return anim;
    }
    
    public static void exportAnimation(MapAnimation animation, String animationPath){
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAnimation() - Exporting disassembly ...");
        try { 
            if(animationPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(animationsHeader);
                asm.append(produceAnimationAsm(animation));
                Path path = Paths.get(animationPath);
                Files.write(path,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] animBytes = produceAnimBytes(animation);
                Path animFilepath = Paths.get(animationPath);
                Files.write(animFilepath,animBytes);
                System.out.println(animBytes.length + " bytes into " + animFilepath);
            }
        } catch (Exception ex) {
            //Logger.getLogger(com.sfc.sf2.map.layout.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
            //System.out.println(ex);
            System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAnimation() - WARNING : "+ex.getMessage());
        }            
        System.out.println("com.sfc.sf2.map.io.DisassemblyManager.exportAnimation() - Disassembly exported.");     
    }
   
    private static String produceAnimationAsm(MapAnimation animation){
        StringBuilder asm = new StringBuilder();
        asm.append("                "+"mapAnimation"+" "+animation.getTileset()+", "+animation.getLength()+"\n");
        for(int i=0;i<animation.getFrames().length;i++){
            MapAnimationFrame frame = animation.getFrames()[i];
            asm.append("                "+"  mapAnimEntry"+" "+frame.getStart()+", "+frame.getLength()+", $"+frame.getDest()+", "+frame.getDelay()+"\n");
        }
        asm.append("                "+"endWord"+"\n");
        return asm.toString();
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
    
    private static String[] parseAsmFileLine(String line, String startingText) {
        line = line.replace(" ", "");
        int commentIndex = line.indexOf(";");
        if (commentIndex == -1) {
            line = line.substring(startingText.length());
        } else {
            line = line.substring(startingText.length(), commentIndex);
        }
        return line.split(",");
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
