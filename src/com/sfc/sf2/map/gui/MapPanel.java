/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.map.gui;

import com.sfc.sf2.map.Map;
import com.sfc.sf2.map.MapArea;
import com.sfc.sf2.map.MapFlagCopy;
import com.sfc.sf2.map.MapLayer2Copy;
import com.sfc.sf2.map.MapStepCopy;
import com.sfc.sf2.map.MapWarp;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.gui.BlockSlotPanel;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.layout.MapLayout;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author wiz
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener {
    
    private static final int DEFAULT_TILES_PER_ROW = 64*3;
    
    private static final int ACTION_CHANGE_BLOCK_VALUE = 0;
    private static final int ACTION_CHANGE_BLOCK_FLAGS = 1;
    private static final int ACTION_MASS_COPY = 2;
    
    public static final int MODE_BLOCK = 0;
    public static final int MODE_OBSTRUCTED = 1;
    public static final int MODE_STAIRS = 2;
    public static final int MODE_WARP = 3;
    public static final int MODE_BARREL = 4;
    public static final int MODE_VASE = 5;
    public static final int MODE_TABLE = 6;
    public static final int MODE_TRIGGER = 7;
        
    public static final int DRAW_MODE_EXPLORATION_FLAGS = 1;
    public static final int DRAW_MODE_INTERACTION_FLAGS = 1<<1;
    public static final int DRAW_MODE_GRID = 1<<2;
    public static final int DRAW_MODE_AREAS = 1<<3;
    public static final int DRAW_MODE_FLAG_COPIES = 1<<4;
    public static final int DRAW_MODE_STEP_COPIES = 1<<5;
    public static final int DRAW_MODE_LAYER2_COPIES = 1<<6;
    public static final int DRAW_MODE_WARPS = 1<<7;
    public static final int DRAW_MODE_ITEMS = 1<<8;
    public static final int DRAW_MODE_TRIGGERS = 1<<9;
    public static final int DRAW_MODE_ACTION_FLAGS = 1<<10;
    public static final int DRAW_MODE_ALL = (1<<11)-1;
    
    int lastMapX = 0;
    int lastMapY = 0;
    
    BlockSlotPanel leftSlot = null;
    private boolean isOnActionsTab = true;
    
    private int currentMode = 0;
    private int togglesDrawMode = 0;
    private int selectedTabsDrawMode = 0;
    
    private MapBlock selectedBlock0;
    MapBlock[][] copiedBlocks;
    
    private List<int[]> actions = new ArrayList<int[]>();
    
    private int tilesPerRow = DEFAULT_TILES_PER_ROW;
    private Map map;
    private MapLayout layout;
    private MapBlock[] blockset;
    private int currentDisplaySize = 1;
    
    private BufferedImage currentImage;
    private BufferedImage previewImage;
    private int previewIndex;
    private boolean redraw = true;
    private int renderCounter = 0;
    
    private BufferedImage mapGridImage;
    private BufferedImage mapAreasImage;
    private BufferedImage mapFlagCopiesImage;
    private BufferedImage mapStepCopiesImage;
    private BufferedImage mapLlayer2CopiesImage;
    private BufferedImage mapWarpsImage;
    private BufferedImage mapItemsImage;
    private BufferedImage mapTtriggersImage;
    private BufferedImage leftUpstairsImage;
    private BufferedImage rightUpstairsImage;
    private BufferedImage tableImage;
    private BufferedImage chestImage;
    private BufferedImage barrelImage;
    private BufferedImage vaseImage;
    private BufferedImage triggerImage;
    private BufferedImage obstructedImage;
    
    
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private TitledBorder titledBorder = null;
    private JPanel titledPanel = null;

    public MapPanel() {
        MapBlockLayout.selectedBlockIndex0 = -1;
        MapBlockLayout.selectedBlockIndex1 = -1;
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
   
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.drawImage(buildImage(), 0, 0, this);
        g.drawImage(buildPreviewImage(), lastMapX*24, lastMapY*24, this);
    }
    
    public BufferedImage buildImage(){
        if(redraw){
            currentImage = buildMapImage(this.map,this.tilesPerRow, false);
            setSize(currentImage.getWidth(), currentImage.getHeight());
        }
        return currentImage;
    }
    
    public BufferedImage buildMapImage(Map map, int tilesPerRow, boolean pngExport){
        renderCounter++;
        System.out.println("Map render "+renderCounter);
        this.map = map;
        this.layout = map.getLayout();
        if(pngExport){
            redraw = true;
        }
        if(redraw){
            MapBlock[] blocks = layout.getBlocks();
            int imageHeight = 64*3*8;
            Color[] palette = blocks[0].getTiles()[0].getPalette();
            //palette[0] = new Color(255, 255, 255, 0);
            IndexColorModel icm = buildIndexColorModel(palette);
            currentImage = new BufferedImage(tilesPerRow*8, imageHeight , BufferedImage.TYPE_BYTE_INDEXED,icm);
            Graphics graphics = currentImage.getGraphics();            
            for(int y=0;y<64;y++){
                for(int x=0;x<64;x++){
                    MapBlock block = blocks[y*64+x];
                    BufferedImage blockImage = getBlockImage(block, icm);
                    BufferedImage explorationFlagImage = block.getExplorationFlagImage();
                    BufferedImage interactionFlagImage = block.getInteractionFlagImage();
                    graphics.drawImage(blockImage, x*3*8, y*3*8, null);
                    if(shouldDraw(DRAW_MODE_EXPLORATION_FLAGS)){
                        int explorationFlags = block.getFlags()&0xC000;
                        if(explorationFlagImage==null){
                            explorationFlagImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = (Graphics2D) explorationFlagImage.getGraphics();
                            switch (explorationFlags) {
                                case 0xC000:
                                    g2.drawImage(getObstructedImage(), 0, 0, null);
                                    break;
                                case 0x8000:
                                    g2.drawImage(getRightUpstairsImage(), 0, 0, null);
                                    break;
                                case 0x4000:
                                    g2.drawImage(getLeftUpstairsImage(), 0, 0, null);
                                    break;
                                default:
                                    break;
                            }
                            block.setExplorationFlagImage(explorationFlagImage);
                        }
                        graphics.drawImage(explorationFlagImage, x*3*8, y*3*8, null); 
                    }
                }
            } 
            if(shouldDraw(DRAW_MODE_GRID)) {
                graphics.drawImage(getMapGridImage(), 0, 0, null);
            }
            if(shouldDraw(DRAW_MODE_AREAS)) {
                graphics.drawImage(getMapAreasImage(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_FLAG_COPIES)) {
                graphics.drawImage(getMapFlagCopies(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_STEP_COPIES)) {
                graphics.drawImage(getMapStepCopiesImage(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_LAYER2_COPIES)) {
                graphics.drawImage(getMapLayer2CopiesImage(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_WARPS)) {
                graphics.drawImage(getMapWarpsImage(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_ITEMS)) {
                graphics.drawImage(getMapItemsImage(),0,0,null);
            }
            if(shouldDraw(DRAW_MODE_TRIGGERS)) {
                graphics.drawImage(getMapTriggersImage(),0,0,null);
            }
            if(!pngExport){
                currentImage = resize(currentImage);
                redraw = false;
            }
            graphics.dispose();
        }
                  
        return currentImage;
    }
    
    private BufferedImage buildPreviewImage() {
        BufferedImage preview = null;
        
        switch (currentMode) {
            case MODE_BLOCK:
                if (MapBlockLayout.selectedBlockIndex0 == -1) {
                    previewImage = null;
                    previewIndex = -1;
                }
                else if (previewImage == null || previewIndex != MapBlockLayout.selectedBlockIndex0) {
                    previewIndex = MapBlockLayout.selectedBlockIndex0;
                    selectedBlock0 = blockset[previewIndex];
                    //"layout" is not MapBlockLayout. How to get that?
                    if (selectedBlock0 != null) {
                        Color[] palette = selectedBlock0.getTiles()[0].getPalette();
                        IndexColorModel icm = buildIndexColorModel(palette);
                        preview = getBlockImage(selectedBlock0, icm);
                    }
                }
                break;
            case MODE_OBSTRUCTED :
                preview = getObstructedImage();
                break;
            case MODE_STAIRS :
                preview = getLeftUpstairsImage();
                break;
            case MODE_WARP :
                //What does this even do?
                preview = getMapWarpsImage();
                break;
            case MODE_BARREL :
                preview = getBarrelImage();
                break;
            case MODE_VASE :
                preview = getVaseImage();
                break;
            case MODE_TABLE :
                preview = getTableImage();
                break;
            case MODE_TRIGGER :
                preview = getTriggerImage();
                break;
                
            default:
                preview = null;
                break;
        }
        
        if (preview != null) {
            previewImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D)previewImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.drawImage(preview, 0, 0, null);
            graphics.setColor(Color.YELLOW);
            graphics.setStroke(new BasicStroke(2));
            graphics.drawRect(0,0, 24, 24);
            graphics.dispose();
        }
        
        return previewImage;
    }
    
    private BufferedImage getMapFlagCopies(){
        if(mapFlagCopiesImage==null){
            mapFlagCopiesImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapFlagCopiesImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(MapFlagCopy flagCopy : map.getFlagCopies()){ 
                g2.setColor(Color.CYAN);
                int width = flagCopy.getWidth();
                int heigth = flagCopy.getHeight();
                g2.drawRect(flagCopy.getSourceX()*24 + 3,flagCopy.getSourceY()*24+3, width*24-6, heigth*24-6);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(flagCopy.getDestX()*24 + 3, flagCopy.getDestY()*24+3, width*24-6, heigth*24-6);
            }
            g2.dispose();
        }
        return mapFlagCopiesImage;
    }
    
    private BufferedImage getMapStepCopiesImage(){
        if(mapStepCopiesImage==null){
            mapStepCopiesImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapStepCopiesImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(MapStepCopy stepCopy : map.getStepCopies()){ 
                g2.setColor(Color.WHITE);
                g2.drawRect(stepCopy.getTriggerX()*24,stepCopy.getTriggerY()*24, 24, 24);
                g2.setColor(Color.CYAN);
                int width = stepCopy.getWidth();
                int heigth = stepCopy.getHeight();
                g2.drawRect(stepCopy.getSourceX()*24 + 3,stepCopy.getSourceY()*24+3, width*24-6, heigth*24-6);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(stepCopy.getDestX()*24 + 3, stepCopy.getDestY()*24+3, width*24-6, heigth*24-6);
            }
            g2.dispose();
        }
        return mapStepCopiesImage;
    }
    
    private BufferedImage getMapLayer2CopiesImage(){
        if(mapLlayer2CopiesImage==null){
            mapLlayer2CopiesImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapLlayer2CopiesImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(MapLayer2Copy layer2Copy : map.getLayer2Copies()){ 
                g2.setColor(Color.WHITE);
                g2.drawRect(layer2Copy.getTriggerX()*24,layer2Copy.getTriggerY()*24, 24, 24);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(layer2Copy.getTriggerX()*24,(layer2Copy.getTriggerY()+1)*24, 24, 24);
                g2.setColor(Color.CYAN);
                int width = layer2Copy.getWidth();
                int heigth = layer2Copy.getHeight();
                if(layer2Copy.getSourceX()>=0 && layer2Copy.getSourceX()<64 && layer2Copy.getSourceY()>=0 && layer2Copy.getSourceY()<64){
                    g2.drawRect(layer2Copy.getSourceX()*24 + 3,layer2Copy.getSourceY()*24+3, width*24-6, heigth*24-6);
                }
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(layer2Copy.getDestX()*24 + 3, layer2Copy.getDestY()*24+3, width*24-6, heigth*24-6);
            }
            g2.dispose();
        }
        return mapLlayer2CopiesImage;
    }
    
    private BufferedImage getMapWarpsImage(){
        if(mapWarpsImage==null){
            mapWarpsImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapWarpsImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(MapWarp warp : map.getWarps()){ 
                g2.setColor(Color.CYAN);
                if(warp.getTriggerX()==-1){
                    MapArea mainArea = map.getAreas()[0];
                    int startX = mainArea.getLayer1StartX();
                    int endX = mainArea.getLayer1EndX();
                    MapBlock[] layout = map.getLayout().getBlocks();
                    int y = warp.getTriggerY();
                    for(int x=startX;x<=endX;x++){
                        int flags = layout[y*64+x].getFlags();
                        if((flags&0xC000)!=0xC000 && (flags&0x1000)==0x1000){
                            g2.drawRect(x*24,y*24, 24, 24);
                        }
                    }
                }else if(warp.getTriggerY()==-1){
                    MapArea mainArea = map.getAreas()[0];
                    int startY = mainArea.getLayer1StartY();
                    int endY = mainArea.getLayer1EndY();
                    MapBlock[] layout = map.getLayout().getBlocks();
                    int x = warp.getTriggerX();
                    for(int y=startY;y<=endY;y++){
                        int flags = layout[y*64+x].getFlags();
                        if((flags&0xC000)!=0xC000 && (flags&0x1000)==0x1000){
                            g2.drawRect(x*24,y*24, 24, 24);
                        }
                    }
                }else{
                    g2.drawRect(warp.getTriggerX()*24,warp.getTriggerY()*24, 24, 24);
                }
            }
            g2.dispose();
        }
        return mapWarpsImage;
    }
    

    private BufferedImage getMapItemsImage(){
        if(mapItemsImage==null){
            mapItemsImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapItemsImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(int y=0;y<64;y++){
                for(int x=0;x<64;x++){
                    MapBlock block = map.getLayout().getBlocks()[y*64+x];
                    int itemFlag = block.getFlags()&0x3C00;
                    switch (itemFlag) {       
                        case 0x1800: // chest
                            g2.drawRect(x*24,y*24, 24, 24);
                            break;
                        case 0x1C00: // search
                            g2.drawRect(x*24,y*24, 24, 24);
                            break;
                        case 0x2C00: // vase
                            g2.drawImage(getVaseImage(),x*24,y*24, null);
                            break;
                        case 0x2800: // table
                            g2.drawImage(getTableImage(),x*24,y*24, null);
                            break;
                        case 0x3000: // barrel
                            g2.drawImage(getBarrelImage(),x*24,y*24, null);
                            break;
                        default:
                            break;
                    }
                }
            }
            g2.dispose();
        }
        return mapItemsImage;
    } 
        
    private BufferedImage getMapTriggersImage(){
        if(mapTtriggersImage==null){
            mapTtriggersImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapTtriggersImage.getGraphics();
            for(int y=0;y<64;y++){
                for(int x=0;x<64;x++){
                    g2.drawImage(getMapTriggersImage(),x*24,y*24, null);
                }
            }
            g2.dispose();
        }
        return mapTtriggersImage;
    }
    
    private BufferedImage getMapAreasImage(){
        if(mapAreasImage==null){
            mapAreasImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapAreasImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            for(MapArea area : map.getAreas()){ 
                g2.setColor(Color.WHITE);
                int width = area.getLayer1EndX() - area.getLayer1StartX() + 1;
                int heigth = area.getLayer1EndY() - area.getLayer1StartY() + 1;
                g2.drawRect(area.getLayer1StartX()*24 + 3, area.getLayer1StartY()*24+3, width*24-6, heigth*24-6);
                g2.setColor(Color.LIGHT_GRAY);
                if(area.getForegroundLayer2StartX()!=0 || area.getForegroundLayer2StartY() != 0){
                    g2.drawRect(area.getForegroundLayer2StartX()*24 + 3, area.getForegroundLayer2StartY()*24+3, width*24-6, heigth*24-6);
                }
                if(area.getBackgroundLayer2StartX()!=0 || area.getBackgroundLayer2StartY() != 0){
                    g2.drawRect(area.getBackgroundLayer2StartX()*24 + 3, area.getBackgroundLayer2StartY()*24+3, width*24-6, heigth*24-6);
                }
            }
            g2.dispose();
        }
        return mapAreasImage;
    }
    
    private BufferedImage getMapGridImage(){
        if(mapGridImage==null){
            mapGridImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapGridImage.getGraphics(); 
            g2.setColor(Color.BLACK);
            for(int i=0;i<64;i++){
                g2.drawLine(3*8+i*3*8, 0, 3*8+i*3*8, 3*8*64-1);
                g2.drawLine(0, 3*8+i*3*8, 3*8*64-1, 3*8+i*3*8);
            }
        }
        return mapGridImage;
    }
    
    private BufferedImage getBlockImage(MapBlock block, IndexColorModel icm) {
        BufferedImage blockImage = block.getImage();
        if(blockImage==null){
            blockImage = new BufferedImage(3*8, 3*8 , BufferedImage.TYPE_BYTE_INDEXED, icm);
            Graphics blockGraphics = blockImage.getGraphics();                    
            blockGraphics.drawImage(block.getTiles()[0].getImage(), 0*8, 0*8, null);
            blockGraphics.drawImage(block.getTiles()[1].getImage(), 1*8, 0*8, null);
            blockGraphics.drawImage(block.getTiles()[2].getImage(), 2*8, 0*8, null);
            blockGraphics.drawImage(block.getTiles()[3].getImage(), 0*8, 1*8, null);
            blockGraphics.drawImage(block.getTiles()[4].getImage(), 1*8, 1*8, null);
            blockGraphics.drawImage(block.getTiles()[5].getImage(), 2*8, 1*8, null);
            blockGraphics.drawImage(block.getTiles()[6].getImage(), 0*8, 2*8, null);
            blockGraphics.drawImage(block.getTiles()[7].getImage(), 1*8, 2*8, null);
            blockGraphics.drawImage(block.getTiles()[8].getImage(), 2*8, 2*8, null);
            block.setImage(blockImage);
            blockGraphics.dispose();
        }
        
        return blockImage;
    }    
    
    private BufferedImage getObstructedImage(){
        if(obstructedImage==null){
            obstructedImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) obstructedImage.getGraphics();  
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            Line2D line1 = new Line2D.Double(6, 6, 18, 18);
            g2.draw(line1);
            Line2D line2 = new Line2D.Double(6, 18, 18, 6);
            g2.draw(line2);  
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            line1 = new Line2D.Double(6, 6, 18, 18);
            g2.draw(line1);
            line2 = new Line2D.Double(6, 18, 18, 6);
            g2.draw(line2);
            g2.dispose();
        }
        return obstructedImage;
    }
    
    private BufferedImage getLeftUpstairsImage(){
        if(leftUpstairsImage==null){
            leftUpstairsImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) leftUpstairsImage.getGraphics();  
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
            Line2D line1 = new Line2D.Double(3, 3, 21, 21);
            g2.draw(line1);
            g2.dispose();
        }
        return leftUpstairsImage;
    }
    
    private BufferedImage getRightUpstairsImage(){
        if(rightUpstairsImage==null){
            rightUpstairsImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) rightUpstairsImage.getGraphics();  
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
            Line2D line1 = new Line2D.Double(3, 21, 21, 3);
            g2.draw(line1);
            g2.dispose();
        }
        return rightUpstairsImage;
    }     
    
    private BufferedImage getChestImage(){
        if(chestImage==null){
            chestImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) chestImage.getGraphics();  
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(1, 1, 21, 21);
            g2.dispose();
        }
        return chestImage;
    } 
    
    private BufferedImage getTableImage(){
        if(tableImage==null){
            tableImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) tableImage.getGraphics();  
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(8, 8, 16, 8);
            g2.drawLine(12, 8, 12, 16);
            g2.dispose();
        }
        return tableImage;
    } 
    
    private BufferedImage getBarrelImage(){
        if(barrelImage==null){
            barrelImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) barrelImage.getGraphics();  
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(3));
            //g2.drawRoundRect(6, 4, 12, 16, 8, 8);
            g2.drawOval(6, 4, 12, 16);
            g2.dispose();
        }
        return barrelImage;
    } 
    
    private BufferedImage getVaseImage(){
        if(vaseImage==null){
            vaseImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) vaseImage.getGraphics(); 
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            //g2.drawRoundRect(6, 4, 12, 16, 8, 8);
            g2.drawOval(6, 4, 12, 16);
            g2.dispose();
        }
        return vaseImage;
    } 
        
    private BufferedImage getTriggerImage(){
        if(triggerImage==null){
            triggerImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) mapTtriggersImage.getGraphics();
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.GREEN);
            g2.drawRect(0,0, 24, 24);
            g2.dispose();
        }
        return triggerImage;
    }
    
    private IndexColorModel buildIndexColorModel(Color[] colors){
        byte[] reds = new byte[16];
        byte[] greens = new byte[16];
        byte[] blues = new byte[16];
        byte[] alphas = new byte[16];
        //reds[0] = (byte)0xFF;
        //greens[0] = (byte)0xFF;
        //blues[0] = (byte)0xFF;
        for(int i=0;i<16;i++){
            reds[i] = (byte)colors[i].getRed();
            greens[i] = (byte)colors[i].getGreen();
            blues[i] = (byte)colors[i].getBlue();
            alphas[i] = (byte)0xFF;
        }
        alphas[0] = 0;
        IndexColorModel icm = new IndexColorModel(4,16,reds,greens,blues,alphas);
        return icm;
    }    
    
    public void resize(int size){
        this.currentDisplaySize = size;
        currentImage = resize(currentImage);
    }
    
    private BufferedImage resize(BufferedImage image){
        BufferedImage newImage = new BufferedImage(image.getWidth()*currentDisplaySize, image.getHeight()*currentDisplaySize, BufferedImage.TYPE_BYTE_INDEXED, (IndexColorModel)image.getColorModel());
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, image.getWidth()*currentDisplaySize, image.getHeight()*currentDisplaySize, null);
        g.dispose();
        return newImage;
    }    
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public void setTilesPerRow(int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;
    }

    public int getCurrentDisplaySize() {
        return currentDisplaySize;
    }

    public void setCurrentDisplaySize(int currentDisplaySize) {
        this.currentDisplaySize = currentDisplaySize;
        redraw = true;
    }

    public MapLayout getMapLayout() {
        return layout;
    }

    public void setMapLayout(MapLayout layout) {
        this.layout = layout;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {
        lastMapX = -1;
        lastMapY = -1;
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if (!isOnActionsTab)
            return;
        int x = e.getX() / (currentDisplaySize * 3*8);
        int y = e.getY() / (currentDisplaySize * 3*8);
        switch (currentMode) {
            case MODE_BLOCK :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if(MapBlockLayout.selectedBlockIndex0!=-1){
                            setBlockValue(x, y, MapBlockLayout.selectedBlockIndex0);
                            if(selectedBlock0!=null && selectedBlock0.getIndex()==MapBlockLayout.selectedBlockIndex0){
                                setFlagValue(x, y, selectedBlock0.getFlags());
                            }
                        }else{
                            int height = copiedBlocks.length;
                            int width = copiedBlocks[0].length;
                            int[] action = new int[4+2*height*width];
                            action[0] = ACTION_MASS_COPY;
                            int blockIndex = y*64+x;
                            action[1] = blockIndex;
                            action[2] = width;
                            action[3] = height;
                            for(int j=0;j<height;j++){
                                for(int i=0;i<width;i++){
                                    if((blockIndex+j*64+i)<4096 && ((blockIndex%64)+i)<64){
                                        MapBlock previousBlock = layout.getBlocks()[blockIndex+j*64+i];
                                        action[4+2*(j*width+i)] = previousBlock.getIndex();
                                        int origFlags = previousBlock.getFlags();
                                        action[4+2*(j*width+i)+1] = origFlags;
                                        MapBlock newBlock = new MapBlock();
                                        MapBlock modelBlock = copiedBlocks[j][i];
                                        newBlock.setIndex(modelBlock.getIndex());
                                        newBlock.setFlags((0xC000 & modelBlock.getFlags()) + (0x3C00 & origFlags));
                                        newBlock.setTiles(modelBlock.getTiles());
                                        layout.getBlocks()[blockIndex+j*64+i] = newBlock; 
                                    }else{
                                        action[4+2*(j*width+i)] = -1;
                                        action[4+2*(j*width+i)+1] = -1;
                                    }
                                }
                            }
                            actions.add(action);
                            redraw = true;
                        }
                        break;
                    case MouseEvent.BUTTON2:
                        lastMapX = x;
                        lastMapY = y;
                        break;
                    case MouseEvent.BUTTON3:
                        setBlockValue(x, y, MapBlockLayout.selectedBlockIndex1);
                        break;
                    default:
                        break;
                } 
                break;
            case MODE_OBSTRUCTED :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        setFlagValue(x, y, 0xC000);
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        break;
                    case MouseEvent.BUTTON3:
                        setFlagValue(x, y, 0x0000);
                        break;
                    default:
                        break;
                }
                break;
            case MODE_STAIRS :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        setFlagValue(x, y, 0x4000);
                        break;
                    case MouseEvent.BUTTON2:
                        setFlagValue(x, y, 0x0000);
                        break;
                    case MouseEvent.BUTTON3:
                        setFlagValue(x, y, 0x8000);
                        break;
                    default:
                        break;
                }
                break;
            case MODE_WARP :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        map.setActionFlag(x, y, 0x1000);
                        this.mapWarpsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        this.mapWarpsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON3:
                        map.setActionFlag(x, y, 0x0000);
                        this.mapWarpsImage = null;
                        this.redraw = true;
                        break;
                    default:
                        break;
                }
                break;
            case MODE_BARREL :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        map.setActionFlag(x, y, 0x3000);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON3:
                        map.setActionFlag(x, y, 0x0000);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    default:
                        break;
                }
                break;
            case MODE_VASE :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        map.setActionFlag(x, y, 0x2C00);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON3:
                        map.setActionFlag(x, y, 0x0000);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    default:
                        break;
                }
                break;
            case MODE_TABLE :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        map.setActionFlag(x, y, 0x2800);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON3:
                        map.setActionFlag(x, y, 0x0000);
                        this.mapItemsImage = null;
                        this.redraw = true;
                        break;
                    default:
                        break;
                }
                break;
            case MODE_TRIGGER :
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        map.setActionFlag(x, y, 0x1400);
                        this.mapTtriggersImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON2:
                        clearFlagValue(x, y);
                        this.mapTtriggersImage = null;
                        this.redraw = true;
                        break;
                    case MouseEvent.BUTTON3:
                        map.setActionFlag(x, y, 0x0000);
                        this.mapTtriggersImage = null;
                        this.redraw = true;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        this.repaint();
        //System.out.println("Map press "+e.getButton()+" "+x+" - "+y);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!isOnActionsTab)
            return;
        int x = e.getX() / (currentDisplaySize * 3*8);
        int y = e.getY() / (currentDisplaySize * 3*8);
        switch (e.getButton()) {
            case MouseEvent.BUTTON2:
                if(currentMode==MODE_BLOCK){                
                    if(x==lastMapX && y==lastMapY){
                        selectedBlock0 = layout.getBlocks()[y*64+x];
                        MapBlockLayout.selectedBlockIndex0 = selectedBlock0.getIndex();
                        updateLeftSlot(selectedBlock0);
                    }else{
                        /* Mass copy */
                        int xStart;
                        int xEnd;
                        int yStart;
                        int yEnd;
                        if(x>lastMapX){
                            xStart = lastMapX;
                            xEnd = x;
                        }else{
                            xStart = x;
                            xEnd = lastMapX;
                        }
                        if(y>lastMapY){
                            yStart = lastMapY;
                            yEnd = y;
                        }else{
                            yStart = y;
                            yEnd = lastMapY;
                        }
                        int width = xEnd - xStart + 1;
                        int height = yEnd - yStart + 1;
                        copiedBlocks = new MapBlock[height][width];
                        for(int j=0;j<height;j++){
                            for(int i=0;i<width;i++){
                                copiedBlocks[j][i] = layout.getBlocks()[(yStart+j)*64+xStart+i];
                            }
                        }

                        MapBlockLayout.selectedBlockIndex0 = -1;

                        BufferedImage img = new BufferedImage(3*8,3*8,BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = (Graphics2D) img.getGraphics();
                        g2.setColor(Color.BLACK);
                        for(int i=0;3+i*3<3*8;i++){
                            g2.drawLine(3+i*3, 0, 3+i*3, 3*8-1);
                            g2.drawLine(0, 3+i*3, 3*8-1, 3+i*3);
                        }
                        g2.dispose();
                        leftSlot.setBlockImage(img);
                        leftSlot.revalidate();
                        leftSlot.repaint(); 
                    }
                }
                break;
                
            default:
                break;
        }         
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {        
        int x = e.getX() / (currentDisplaySize * 3*8);
        int y = e.getY() / (currentDisplaySize * 3*8);
        
        if(x!=lastMouseX||y!=lastMouseY){
            lastMouseX=x;
            lastMouseY=y;
            titledBorder = (TitledBorder)(titledPanel.getBorder());
            titledBorder.setTitle("Cursor : "+x+","+y);
            titledPanel.revalidate();
            titledPanel.repaint();
            //System.out.println("New cursor pos : "+x+","+y);
            
            lastMapX = x;
            lastMapY = y;
            this.revalidate();
            this.repaint();
        }
        
    }
    
    private void updateLeftSlot(MapBlock block){
        BufferedImage img = new BufferedImage(3*8,3*8,BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(block.getImage(), 0, 0, null);
        g.drawImage(block.getExplorationFlagImage(), 0, 0, null);
        g.dispose();
        leftSlot.setBlockImage(img);
        leftSlot.revalidate();
        leftSlot.repaint(); 
    }
    
    public void setBlockValue(int x, int y, int value){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getIndex()!=value){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_VALUE;
            action[1] = y*64+x;
            action[2] = block.getIndex();
            block.setIndex(value);
            block.setImage(null);
            block.setTiles(blockset[block.getIndex()].getTiles());
            actions.add(action);
            redraw = true;
        }
    }
    
    public void setFlagValue(int x, int y, int value){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getFlags()!=value){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_FLAGS;
            action[1] = y*64+x;
            int origFlags = block.getFlags();
            action[2] = origFlags;
            int newFlags = (0xC000 & value) + (0x3C00 & origFlags);
            block.setFlags(newFlags);
            block.setExplorationFlagImage(null);
            actions.add(action);
            redraw = true;
        }
    }
    
    public void clearFlagValue(int x, int y){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getFlags()!=0){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_FLAGS;
            action[1] = y*64+x;
            int origFlags = block.getFlags();
            action[2] = origFlags;
            int newFlags = 0;
            block.setFlags(newFlags);
            block.setExplorationFlagImage(null);
            actions.add(action);
            clearFlagImages();
            redraw = true;
        }
    }
    
    public void clearFlagImages(){
        mapFlagCopiesImage = null;
        mapStepCopiesImage = null;
        mapLlayer2CopiesImage = null;
        mapWarpsImage = null;
    }
    
    public void revertLastAction(){
        if(actions.size()>0){
            int[] action = actions.get(actions.size()-1);
            switch (action[0]) {
                case ACTION_CHANGE_BLOCK_VALUE:
                    {
                        MapBlock block = layout.getBlocks()[action[1]];
                        block.setIndex(action[2]);
                        block.setImage(null);
                        block.setTiles(blockset[block.getIndex()].getTiles());
                        actions.remove(actions.size()-1);
                        redraw = true;
                        this.repaint();
                        break;
                    }
                case ACTION_CHANGE_BLOCK_FLAGS:
                    {
                        MapBlock block = layout.getBlocks()[action[1]];
                        block.setFlags(action[2]);               
                        block.setExplorationFlagImage(null);
                        block.setInteractionFlagImage(null);
                        actions.remove(actions.size()-1);
                        redraw = true;
                        this.repaint();
                        break;
                    }
                case ACTION_MASS_COPY:
                    int blockIndex = action[1];
                    int width = action[2];
                    int height = action[3];
                    for(int j=0;j<height;j++){
                        for(int i=0;i<width;i++){
                            int value = action[4+2*(j*width+i)];
                            int flags = action[4+2*(j*width+i)+1];
                            if(value != -1 && flags != -1){
                                MapBlock block = new MapBlock();
                                block.setIndex(value);
                                block.setFlags(flags);
                                block.setTiles(blockset[block.getIndex()].getTiles());
                                layout.getBlocks()[blockIndex+j*64+i] = block;
                            }
                        }
                    }   actions.remove(actions.size()-1);
                    redraw = true;
                    this.repaint();
                    break;
                default:
                    break;
            }
        }
    }

    public MapBlock[] getBlockset() {
        return blockset;
    }

    public void setBlockset(MapBlock[] blockset) {
        this.blockset = blockset;
    }  

    public MapBlock getSelectedBlock0() {
        return selectedBlock0;
    }

    public void setSelectedBlock0(MapBlock selectedBlock0) {
        this.selectedBlock0 = selectedBlock0;
    }

    public List<int[]> getActions() {
        return actions;
    }

    public void setActions(List<int[]> actions) {
        this.actions = actions;
    }

    public boolean isRedraw() {
        return redraw;
    }

    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
        this.redraw=true;
    }

    public BlockSlotPanel getLeftSlot() {
        return leftSlot;
    }

    public void setLeftSlot(BlockSlotPanel leftSlot) {
        this.leftSlot = leftSlot;
    }
    
    public void updateAreaDisplay(){
        mapAreasImage = null;
        this.redraw = true;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
    
    public void updateFlagCopyDisplay(){
        mapFlagCopiesImage = null;
        this.redraw = true;
    }
    
    public void updateStepCopyDisplay(){
        mapStepCopiesImage = null;
        this.redraw = true;
    }
    
    public void updateLayer2CopyDisplay(){
        mapLlayer2CopiesImage = null;
        this.redraw = true;
    }

    public void setWarpsImage(BufferedImage warpsImage) {
        this.mapWarpsImage = warpsImage;
    }
    
    public void updateWarpDisplay(){
        mapWarpsImage = null;
        this.redraw = true;
    }
    
    public void updateItemDisplay(){
        mapItemsImage = null;
        this.redraw = true;
    }

    public void setItemsImage(BufferedImage itemsImage) {
        this.mapItemsImage = itemsImage;
    }
    
    private boolean shouldDraw(int drawFlag) {
        return isDrawMode_Toggles(drawFlag) || isDrawMode_Tabs(drawFlag) || isDrawMode_Actions(drawFlag);
    }

    public boolean isDrawMode_Toggles(int drawFlag) {
        return (togglesDrawMode & drawFlag) > 0;
    }

    public void setDrawMode_Toggles(int drawFlag, boolean on) {
        if (drawFlag == DRAW_MODE_ALL)
            togglesDrawMode = on ? drawFlag : 0;    
        else if (on)
            togglesDrawMode = (togglesDrawMode | drawFlag);
        else
            togglesDrawMode = (togglesDrawMode & ~drawFlag);
        this.redraw=true;
        
        if (on) {
            previewImage = null;
            previewIndex = -1;
        }
    }

    public boolean isDrawMode_Tabs(int drawFlag) {
        return (selectedTabsDrawMode & drawFlag) > 0;
    }

    public void setDrawMode_Tabs(int drawFlag, boolean on) {
        if (drawFlag == DRAW_MODE_ALL)
            selectedTabsDrawMode = on ? drawFlag : 0;        
        else if (on)
            selectedTabsDrawMode = (selectedTabsDrawMode | drawFlag);
        else
            selectedTabsDrawMode = (selectedTabsDrawMode | ~drawFlag);
        this.redraw=true;
    }

    public boolean isDrawMode_Actions(int drawFlag) {
        if (!isOnActionsTab)
            return false;
        
        switch (currentMode)
        {
            case MODE_OBSTRUCTED:
            case MODE_STAIRS:
                return (DRAW_MODE_EXPLORATION_FLAGS & drawFlag) > 0;
            case MODE_BARREL:
            case MODE_VASE:
            case MODE_TABLE:
                return (DRAW_MODE_ITEMS & drawFlag) > 0;
            case MODE_WARP:
                return (DRAW_MODE_WARPS & drawFlag) > 0;
            case MODE_TRIGGER:
                return (DRAW_MODE_TRIGGERS & drawFlag) > 0;
            default:
                return false;
        }
    }
    
    public boolean getIsOnActionsTab() {        
        return isOnActionsTab;
    }
    
    public void setIsOnActionsTab(boolean isOnActionsTab) {
        this.isOnActionsTab = isOnActionsTab;
    }

    public void setTriggersImage(BufferedImage triggersImage) {
        this.mapTtriggersImage = triggersImage;
    }

    public TitledBorder getTitledBorder() {
        return titledBorder;
    }

    public void setTitledBorder(TitledBorder titledBorder) {
        this.titledBorder = titledBorder;
    }

    public JPanel getTitledPanel() {
        return titledPanel;
    }

    public void setTitledPanel(JPanel titledPanel) {
        this.titledPanel = titledPanel;
    }
    
    
    
}
