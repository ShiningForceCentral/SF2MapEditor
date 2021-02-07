/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapFlagCopy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapFlagCopyPropertiesTableModel extends AbstractTableModel {
    
    private final Object[][] tableData;
    private final String[] columns = {"Flag", "Comment", "Source X", "Source Y", "Width", "Height", "Dest X", "Dest Y"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapFlagCopyPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Object[256][];
        int i = 0;
        MapFlagCopy[] flagCopies = map.getFlagCopies();
        if(flagCopies!=null){
            while(i<flagCopies.length){
                tableData[i] = new Object[8];
                tableData[i][0] = flagCopies[i].getFlag();
                tableData[i][1] = flagCopies[i].getComment();
                tableData[i][2] = flagCopies[i].getSourceX();
                tableData[i][3] = flagCopies[i].getSourceY();
                tableData[i][4] = flagCopies[i].getWidth();
                tableData[i][5] = flagCopies[i].getHeight();
                tableData[i][6] = flagCopies[i].getDestX();
                tableData[i][7] = flagCopies[i].getDestY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[8];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapFlagCopy> entries = new ArrayList<>();
        for(Object[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null){
                MapFlagCopy flagCopy = new MapFlagCopy();
                flagCopy.setFlag((int)entry[0]);
                flagCopy.setComment((String)entry[1]);
                flagCopy.setSourceX((int)entry[2]);
                flagCopy.setSourceY((int)entry[3]);
                flagCopy.setWidth((int)entry[4]);
                flagCopy.setHeight((int)entry[5]);
                flagCopy.setDestX((int)entry[6]);
                flagCopy.setDestY((int)entry[7]);           
                entries.add(flagCopy);
            }
        }
        MapFlagCopy[] flagCopies = new MapFlagCopy[entries.size()];
        map.setFlagCopies(entries.toArray(flagCopies));
    }
    
    @Override
    public Class getColumnClass(int column) {
        if(column == 1){
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
        mapPanel.updateFlagCopyDisplay();
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
