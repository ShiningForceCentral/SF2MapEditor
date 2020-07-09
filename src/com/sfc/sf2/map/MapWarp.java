/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map;

/**
 *
 * @author wiz
 */
public class MapWarp {
    
    private int triggerX;
    private int triggerY;
    private String scrollDirection;
    private String destMap;
    private int destX;
    private int destY;
    private String facing;

    public int getTriggerX() {
        return triggerX;
    }

    public void setTriggerX(int triggerX) {
        this.triggerX = triggerX;
    }

    public int getTriggerY() {
        return triggerY;
    }

    public void setTriggerY(int triggerY) {
        this.triggerY = triggerY;
    }

    public String getDestMap() {
        return destMap;
    }

    public void setDestMap(String destMap) {
        this.destMap = destMap;
    }

    public int getDestX() {
        return destX;
    }

    public void setDestX(int destX) {
        this.destX = destX;
    }

    public int getDestY() {
        return destY;
    }

    public void setDestY(int destY) {
        this.destY = destY;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public String getScrollDirection() {
        return scrollDirection;
    }

    public void setScrollDirection(String scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

 
    
}
