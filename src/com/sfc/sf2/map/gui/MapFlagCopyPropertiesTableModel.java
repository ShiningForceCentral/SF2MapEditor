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
    
    private final Integer[][] tableData;
    private final String[] columns = {"Flag", "Source X", "Source Y", "Width", "Height", "Dest X", "Dest Y"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapFlagCopyPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Integer[16][];
        int i = 0;
        MapFlagCopy[] flagCopies = map.getFlagCopies();
        if(flagCopies!=null){
            while(i<flagCopies.length){
                tableData[i] = new Integer[7];
                tableData[i][0] = flagCopies[i].getFlag();
                tableData[i][1] = flagCopies[i].getSourceX();
                tableData[i][2] = flagCopies[i].getSourceY();
                tableData[i][3] = flagCopies[i].getWidth();
                tableData[i][4] = flagCopies[i].getHeight();
                tableData[i][5] = flagCopies[i].getDestX();
                tableData[i][6] = flagCopies[i].getDestY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[7];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapFlagCopy> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null){
                MapFlagCopy flagCopy = new MapFlagCopy();
                flagCopy.setFlag(entry[0]);
                flagCopy.setSourceX(entry[1]);
                flagCopy.setSourceY(entry[2]);
                flagCopy.setWidth(entry[3]);
                flagCopy.setHeight(entry[4]);
                flagCopy.setDestX(entry[5]);
                flagCopy.setDestY(entry[6]);           
                entries.add(flagCopy);
            }
        }
        MapFlagCopy[] flagCopies = new MapFlagCopy[entries.size()];
        map.setFlagCopies(entries.toArray(flagCopies));
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
