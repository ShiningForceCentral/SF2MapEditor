/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapAnimationFrame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class MapAnimationFramePropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"Start", "Length", "Dest", "Delay"};
    private Map map;
    private MapPanel mapPanel;
    
    public MapAnimationFramePropertiesTableModel(Map map, MapPanel mapPanel) {
        super();
        this.map = map;
        this.mapPanel = mapPanel;
        tableData = new Integer[64][];
        int i = 0;
        MapAnimationFrame[] frames = map.getAnimation().getFrames();
        if(frames!=null){
            while(i<frames.length){
                tableData[i] = new Integer[4];
                tableData[i][0] = frames[i].getStart();
                tableData[i][1] = frames[i].getLength();
                tableData[i][2] = frames[i].getDest();
                tableData[i][3] = frames[i].getDelay();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[4];
            i++;
        }
    }
    
    public void updateProperties() {
        List<MapAnimationFrame> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null){
                MapAnimationFrame frame = new MapAnimationFrame();
                frame.setStart(entry[0]);
                frame.setLength(entry[1]);
                frame.setDest(entry[2]);
                frame.setDelay(entry[3]);          
                entries.add(frame);
            }
        }
        MapAnimationFrame[] frames = new MapAnimationFrame[entries.size()];
        map.getAnimation().setFrames(entries.toArray(frames));
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
