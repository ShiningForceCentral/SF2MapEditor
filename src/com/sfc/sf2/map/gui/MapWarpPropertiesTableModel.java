/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapWarp;
import com.sfc.sf2.map.block.MapBlock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapWarpPropertiesTableModel extends AbstractTableModel {
    
    private final String[][] tableData;
    private final String[] columns = {"Trigger X", "Trigger Y", "Dest Map", "Dest X", "Dest Y", "Facing"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapWarpPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new String[64][];
        int i = 0;
        MapWarp[] warps = map.getWarps();
        if(warps!=null){
            while(i<warps.length){
                tableData[i] = new String[6];
                tableData[i][0] = Integer.toString(warps[i].getTriggerX(),10);
                tableData[i][1] = Integer.toString(warps[i].getTriggerY(),10);
                tableData[i][2] = warps[i].getDestMap();
                tableData[i][3] = Integer.toString(warps[i].getDestX(),10);
                tableData[i][4] = Integer.toString(warps[i].getDestY(),10);
                tableData[i][5] = warps[i].getFacing();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new String[6];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapWarp> entries = new ArrayList<>();
        for(String[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null){
                MapWarp warp = new MapWarp();
                warp.setTriggerX(Integer.valueOf(entry[0],10));
                warp.setTriggerY(Integer.valueOf(entry[1],10));
                warp.setDestMap(entry[2]);
                warp.setDestX(Integer.valueOf(entry[3],10));
                warp.setDestY(Integer.valueOf(entry[4],10)); 
                warp.setFacing(entry[5]);           
                entries.add(warp);
                if(((byte)warp.getTriggerX())==-1){
                    MapArea mainArea = map.getAreas()[0];
                    int startX = mainArea.getLayer1StartX();
                    int endX = mainArea.getLayer1EndX();
                    MapBlock[] layout = map.getLayout().getBlocks();
                    int y = warp.getTriggerY();
                    for(int x=startX;x<=endX;x++){
                        int flags = layout[y*64+x].getFlags();
                        if((flags&0xC000)!=0xC000){
                            map.setActionFlag(x, y, 0x1000);
                        }
                    }
                }else if(((byte)warp.getTriggerY())==-1){
                    MapArea mainArea = map.getAreas()[0];
                    int startY = mainArea.getLayer1StartY();
                    int endY = mainArea.getLayer1EndY();
                    MapBlock[] layout = map.getLayout().getBlocks();
                    int x = warp.getTriggerX();
                    for(int y=startY;y<=endY;y++){
                        int flags = layout[y*64+x].getFlags();
                        if((flags&0xC000)!=0xC000){
                            map.setActionFlag(x, y, 0x1000);
                        }
                    }                    
                }else{
                    map.setActionFlag(warp.getTriggerX(), warp.getTriggerY(), 0x1000);
                }
            }
        }
        MapWarp[] warps = new MapWarp[entries.size()];
        map.setWarps(entries.toArray(warps));
    }
    
    @Override
    public Class getColumnClass(int column) {
        if(column==2||column==5){
            return String.class;
        }else{
            return Integer.class;
        }
    }    
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = (String)value;
        updateProperties();
        mapPanel.updateWarpDisplay();
        mapPanel.revalidate();
        mapPanel.repaint();
    }    
 
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }    
    
    @Override
    public int getRowCount() {
        return tableData.length;
    }
 
    @Override
    public int getColumnCount() {
        return columns.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public void setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }
    
}
