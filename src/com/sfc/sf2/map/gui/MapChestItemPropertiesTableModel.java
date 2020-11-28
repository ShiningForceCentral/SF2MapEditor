/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapItem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapChestItemPropertiesTableModel extends AbstractTableModel {
    
    private final Object[][] tableData;
    private final String[] columns = {"X", "Y", "Flag", "Item"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapChestItemPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Object[64][];
        int i = 0;
        MapItem[] items = map.getChestItems();
        if(items!=null){
            while(i<items.length){
                tableData[i] = new Object[4];
                tableData[i][0] = items[i].getX();
                tableData[i][1] = items[i].getY();
                tableData[i][2] = items[i].getFlag();
                tableData[i][3] = items[i].getItem();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Object[4];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapItem> entries = new ArrayList<>();
        for(Object[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null){
                MapItem item = new MapItem();
                item.setX((int)entry[0]);
                item.setY((int)entry[1]);
                item.setFlag((int)entry[2]);
                item.setItem((String)entry[3]);          
                entries.add(item);
                map.setActionFlag(item.getX(), item.getY(), 0x1800);
            }
        }
        MapItem[] items = new MapItem[entries.size()];
        map.setChestItems(entries.toArray(items));
    }
    
    @Override
    public Class getColumnClass(int column) {
        if(column==3){
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
        tableData[row][col] = value;
        updateProperties();
        mapPanel.updateItemDisplay();
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
