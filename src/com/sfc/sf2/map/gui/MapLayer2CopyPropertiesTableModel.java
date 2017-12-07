/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapLayer2Copy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapLayer2CopyPropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"Trigger X", "Trigger Y", "Source X", "Source Y", "Width", "Height", "Dest X", "Dest Y"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapLayer2CopyPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Integer[16][];
        int i = 0;
        MapLayer2Copy[] layer2Copies = map.getLayer2Copies();
        if(layer2Copies!=null){
            while(i<layer2Copies.length){
                tableData[i] = new Integer[8];
                tableData[i][0] = layer2Copies[i].getTriggerX();
                tableData[i][1] = layer2Copies[i].getTriggerY();
                tableData[i][2] = layer2Copies[i].getSourceX();
                tableData[i][3] = layer2Copies[i].getSourceY();
                tableData[i][4] = layer2Copies[i].getWidth();
                tableData[i][5] = layer2Copies[i].getHeight();
                tableData[i][6] = layer2Copies[i].getDestX();
                tableData[i][7] = layer2Copies[i].getDestY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[8];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapLayer2Copy> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null && entry[7] != null){
                MapLayer2Copy layer2Copy = new MapLayer2Copy();
                layer2Copy.setTriggerX(entry[0]);
                layer2Copy.setTriggerY(entry[1]);
                layer2Copy.setSourceX(entry[2]);
                layer2Copy.setSourceY(entry[3]);
                layer2Copy.setWidth(entry[4]);
                layer2Copy.setHeight(entry[5]);
                layer2Copy.setDestX(entry[6]);
                layer2Copy.setDestY(entry[7]);           
                entries.add(layer2Copy);
                map.setActionFlag(layer2Copy.getTriggerX(), layer2Copy.getTriggerY(), 0x0800);
                map.setActionFlag(layer2Copy.getTriggerX(), layer2Copy.getTriggerY()+1, 0x0C00);
            }
        }
        MapLayer2Copy[] layer2Copies = new MapLayer2Copy[entries.size()];
        map.setLayer2Copies(entries.toArray(layer2Copies));
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
        mapPanel.updateLayer2CopyDisplay();
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
