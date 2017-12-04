/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.layout.MapLayout;

/**
 *
 * @author wiz
 */
public class Map {
    private MapBlock[] blocks;
    private MapLayout layout;
    private MapArea[] areas;
    
    public MapBlock[] getBlocks() {
        return blocks;
    }

    public void setBlocks(MapBlock[] blocks) {
        this.blocks = blocks;
    }

    public MapLayout getLayout() {
        return layout;
    }

    public void setLayout(MapLayout layout) {
        this.layout = layout;
    }

    public MapArea[] getAreas() {
        return areas;
    }

    public void setAreas(MapArea[] areas) {
        this.areas = areas;
    }


    
}
