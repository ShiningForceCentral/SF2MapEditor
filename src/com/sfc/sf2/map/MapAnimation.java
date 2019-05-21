/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

import com.sfc.sf2.graphics.Tile;

/**
 *
 * @author wiz
 */
public class MapAnimation {
    
    private int tileset;
    private Tile[] tiles;
    private int length;
    private MapAnimationFrame[] frames;

    public int getTileset() {
        return tileset;
    }

    public void setTileset(int tileset) {
        this.tileset = tileset;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public MapAnimationFrame[] getFrames() {
        return frames;
    }

    public void setFrames(MapAnimationFrame[] frames) {
        this.frames = frames;
    }


 
    
}
