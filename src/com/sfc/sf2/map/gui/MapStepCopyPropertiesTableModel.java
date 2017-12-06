/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapFlagCopy;
import com.sfc.sf2.map.MapStepCopy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapStepCopyPropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"Trigger X", "Trigger Y", "Source X", "Source Y", "Width", "Height", "Dest X", "Dest Y"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapStepCopyPropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Integer[16][];
        int i = 0;
        MapStepCopy[] stepCopies = map.getStepCopies();
        if(stepCopies!=null){
            while(i<stepCopies.length){
                tableData[i] = new Integer[8];
                tableData[i][0] = stepCopies[i].getTriggerX();
                tableData[i][1] = stepCopies[i].getTriggerY();
                tableData[i][2] = stepCopies[i].getSourceX();
                tableData[i][3] = stepCopies[i].getSourceY();
                tableData[i][4] = stepCopies[i].getWidth();
                tableData[i][5] = stepCopies[i].getHeight();
                tableData[i][6] = stepCopies[i].getDestX();
                tableData[i][7] = stepCopies[i].getDestY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[8];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapStepCopy> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null && entry[7] != null){
                MapStepCopy stepCopy = new MapStepCopy();
                stepCopy.setTriggerX(entry[0]);
                stepCopy.setTriggerY(entry[1]);
                stepCopy.setSourceX(entry[2]);
                stepCopy.setSourceY(entry[3]);
                stepCopy.setWidth(entry[4]);
                stepCopy.setHeight(entry[5]);
                stepCopy.setDestX(entry[6]);
                stepCopy.setDestY(entry[7]);           
                entries.add(stepCopy);
                map.setActionFlag(stepCopy.getTriggerX(), stepCopy.getTriggerY(), 0x0400);
            }
        }
        MapStepCopy[] stepCopies = new MapStepCopy[entries.size()];
        map.setStepCopies(entries.toArray(stepCopies));
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
        mapPanel.updateStepCopyDisplay();
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
