/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.io;

import com.sfc.sf2.map.MapArea;
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
    
}
