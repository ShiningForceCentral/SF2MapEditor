/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapArea;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapAreaPropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"L1 X", "L1 Y", "L1 X'", "L1 Y'", "L2 F X", "L2 F Y", "L2 B X", "L2 B Y", "L1 P X", "L1 P Y"
            , "L2 P X", "L2 P Y", "L1 S X", "L1 S Y", "L2 S X", "L2 S Y", "L1 Type", "Music"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapAreaPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Integer[16][];
        int i = 0;
        MapArea[] areas = map.getAreas();
        if(areas!=null){
            while(i<areas.length){
                tableData[i] = new Integer[18];
                tableData[i][0] = areas[i].getLayer1StartX();
                tableData[i][1] = areas[i].getLayer1StartY();
                tableData[i][2] = areas[i].getLayer1EndX();
                tableData[i][3] = areas[i].getLayer1EndY();
                tableData[i][4] = areas[i].getForegroundLayer2StartX();
                tableData[i][5] = areas[i].getForegroundLayer2StartY();
                tableData[i][6] = areas[i].getBackgroundLayer2StartX();
                tableData[i][7] = areas[i].getBackgroundLayer2StartY();
                tableData[i][8] = areas[i].getLayer1ParallaxX();
                tableData[i][9] = areas[i].getLayer1ParallaxY();
                tableData[i][10] = areas[i].getLayer2ParallaxX();
                tableData[i][11] = areas[i].getLayer2ParallaxY();
                tableData[i][12] = areas[i].getLayer1AutoscrollX();
                tableData[i][13] = areas[i].getLayer1AutoscrollY();
                tableData[i][14] = areas[i].getLayer2AutoscrollX();
                tableData[i][15] = areas[i].getLayer2AutoscrollY();
                tableData[i][16] = areas[i].getLayerType();
                tableData[i][17] = areas[i].getDefaultMusic();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[18];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapArea> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null && entry[7] != null
                    && entry[8] != null && entry[9] != null
                    && entry[10] != null && entry[11] != null
                    && entry[12] != null && entry[13] != null
                    && entry[14] != null && entry[15] != null
                    && entry[16] != null && entry[17] != null
                    && entry[2]>entry[0] && entry[3]>entry[1]){
                MapArea area = new MapArea();
                area.setLayer1StartX(entry[0]);
                area.setLayer1StartY(entry[1]);
                area.setLayer1EndX(entry[2]);
                area.setLayer1EndY(entry[3]);
                area.setForegroundLayer2StartX(entry[4]);
                area.setForegroundLayer2StartY(entry[5]);
                area.setBackgroundLayer2StartX(entry[6]);
                area.setBackgroundLayer2StartY(entry[7]);
                area.setLayer1ParallaxX(entry[8]);
                area.setLayer1ParallaxY(entry[9]);
                area.setLayer2ParallaxX(entry[10]);
                area.setLayer2ParallaxY(entry[11]);
                area.setLayer1AutoscrollX(entry[12]);
                area.setLayer1AutoscrollY(entry[13]);
                area.setLayer2AutoscrollX(entry[14]);
                area.setLayer2AutoscrollY(entry[15]);
                area.setLayerType(entry[16]);
                area.setDefaultMusic(entry[17]);             
                entries.add(area);
            }
        }
        MapArea[] areas = new MapArea[entries.size()];
        map.setAreas(entries.toArray(areas));
    }
    
    @Override
    public Class getColumnClass(int column) {
        return Integer.class;
    }    
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = (Integer)value;
        updateProperties();
        mapPanel.updateAreaDisplay();
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
