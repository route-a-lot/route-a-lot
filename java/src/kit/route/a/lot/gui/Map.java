package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.gui.event.FavoriteAddedEvent;
import kit.route.a.lot.gui.event.NavNodeSelectedEvent;
import kit.route.a.lot.gui.event.PositionEvent;
import kit.route.a.lot.map.rendering.Projection;


public abstract class Map extends JPanel implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;   
    
    protected ListenerLists listener;
    private ArrayList<Coordinates> navPoints;
    
    private int oldMousePosX;
    private int oldMousePosY;
    private int popupXPos;
    private int popupYPos;
    private Coordinates center;
    protected int zoomlevel = 2;
    protected Coordinates topLeft = new Coordinates();
    protected Coordinates bottomRight = new Coordinates();
    
    private GUI gui; // TODO
    private JPopupMenu navNodeMenu;
    private JMenuItem startItem;
    private JMenuItem endItem;
    private AbstractButton stopoverItem;
    private AbstractButton favoriteItem;
    Component canvas;
    
    /**
     * Creates a map canvas, including its context menu.
     * @param listeners the collection of listener lists from the gui
     * @param navPointsList the list of navigation nodes from the gui
     */
    public Map(ListenerLists listeners, ArrayList<Coordinates> navPointsList, GUI gui) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        this.gui = gui;
        listener = listeners;
        navPoints = navPointsList;
        center = new Coordinates(0, 0);
        
        canvas = createCanvas();
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        add(canvas, BorderLayout.CENTER);
        
        //Context menu:
        startItem = new JMenuItem("als Start");
        endItem = new JMenuItem("als Ziel");
        stopoverItem = new JMenuItem("als Zwischenhalt");
        favoriteItem = new JMenuItem("als Favorit");       
        startItem.addActionListener(this);        
        endItem.addActionListener(this);       
        stopoverItem.addActionListener(this); 
        favoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates favoriteCoordinates =
                    getCoordinates(popupXPos - canvas.getX(), popupYPos - canvas.getY());
                ListenerLists.fireEvent(listener.addFav, new FavoriteAddedEvent(favoriteCoordinates, "", ""));
            }
        });
        navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(favoriteItem); 
        
        canvas.addMouseListener(new MouseAdapter() {          
            @Override // used for dragging, relocate?
            public void mousePressed(MouseEvent me) {
                oldMousePosX = me.getX(); 
                oldMousePosY = me.getY();
                checkPopup(me);
            }     
            @Override
            public void mouseReleased(MouseEvent me) {
                checkPopup(me);
            }
        });
    }
    
    /**
     * Creates and returns the underlying map drawing surface.
     * @return the drawing canvas
     */
    protected abstract Component createCanvas();
    
    /**
     * Sets the view center using geo coordinates.
     * Note that this method won't schedule a map redraw.
     * @param center the desired map center
     */
    public void setCenter(Coordinates center) {
        this.center = center;
    }

    /**
     * Sets the zoom level. Negative values will be treated as 0.
     * Subsequently schedules a map redraw.
     * @param zoomlevel the desired zoom level
     */
    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = Math.max(zoomlevel, 0);
        calculateView();
    }
    
    /**
     * Called when a popup entry concerning navigation nodes has been clicked.
     * Correspondingly adds a navigation node to the list.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String label = ((JMenuItem) e.getSource()).getText();
        // TODO better implementation:
        int type = label.equals(startItem.getText()) ? 0 : label.equals(endItem.getText()) ? 2 : 1;         
        Coordinates pos = getCoordinates(popupXPos - canvas.getX(), popupYPos - canvas.getY());        
        switch (navPoints.size()) {
            case 0: if (type == 2) {
                        navPoints.add(new Coordinates());
                    }
                    navPoints.add(pos);
                    break;
            case 1: switch (type) {
                        case 0 : navPoints.set(0, pos); break;
                        default : navPoints.add(pos);
                    }
                    break;
            default: switch (type) {
                        case 0 : navPoints.set(0, pos); break;
                        case 1 : navPoints.add(navPoints.size() - 1, pos); break;
                        case 2 : navPoints.set(navPoints.size() - 1, pos);
                    }
        }    
        ListenerLists.fireEvent(listener.targetSelected,
                new NavNodeSelectedEvent(pos, navPoints.indexOf(pos)));
        canvas.repaint();
    }
    
    /**
     * Opens the map context menu if appropriate. Fires a WhatWasClicked event.
     */
    private void checkPopup(MouseEvent e) {     
        if (e.isPopupTrigger()) {
            popupXPos = e.getX();
            popupYPos = e.getY();
            navNodeMenu.show(e.getComponent(), popupXPos, popupYPos);
            System.out.println("geklickt");
        }
        
        ListenerLists.fireEvent(listener.whatWasClicked, new PositionEvent(getCoordinates(popupXPos, popupYPos)));
    }
      
    /**
     * Adapts the map position and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        center.setLongitude(center.getLongitude() - (e.getX() - oldMousePosX) * Projection.getZoomFactor(zoomlevel));
        center.setLatitude(center.getLatitude() - (e.getY() - oldMousePosY) * Projection.getZoomFactor(zoomlevel));
        oldMousePosX = e.getX();
        oldMousePosY = e.getY();
        calculateView();
    }
   
    /**
     * Adapts the zoom level and schedules a map redraw.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Coordinates clickPos = getCoordinates(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        int yDiff = e.getY() - canvas.getY() - canvas.getBounds().height / 2;
        int xDiff = e.getX() - canvas.getX() - canvas.getBounds().width / 2;
        center.setLatitude(clickPos.getLatitude() - yDiff * Projection.getZoomFactor(zoomlevel));
        center.setLongitude(clickPos.getLongitude() - xDiff * Projection.getZoomFactor(zoomlevel));
        setZoomlevel(zoomlevel + e.getWheelRotation());
    }
    
    /**
     * Displays the cursor geo coordinates in the status bar.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Coordinates mousePosCoordinates = getCoordinates(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        gui.l_position.setText(mousePosCoordinates.toString()); //TODO
    }

    /**
     * Derives the new geo coordinates view constraints from the pixel dimensions of the map and subsequently
     * updates the context.
     */
    void calculateView() {
        // jedit getVisibleRect -> getBounds
        topLeft.setLatitude(center.getLatitude() - canvas.getBounds().height
                * Projection.getZoomFactor(zoomlevel) / 2.f);
        topLeft.setLongitude(center.getLongitude() - canvas.getBounds().width
                * Projection.getZoomFactor(zoomlevel) / 2.f);
        bottomRight.setLatitude(canvas.getBounds().height * Projection.getZoomFactor(zoomlevel) / 2.f
                + center.getLatitude());
        bottomRight.setLongitude(canvas.getBounds().width * Projection.getZoomFactor(zoomlevel) / 2.f
                + center.getLongitude());
        canvas.repaint(); 
    }
    
    /**
     * Converts pixel coordinates into the eqivalent projected geo reference system coordinates. The pixel
     * origin is top left corner of the map.
     * @param x the horizontal pixel coordinate
     * @param y the vertical pixel coordinate
     * @return eqivalent geo coordinates
     */
    private Coordinates getCoordinates(int x, int y) {
        Coordinates result = new Coordinates();
        // jedit getVisibleRect -> getBounds
        result.setLatitude(center.getLatitude() + (y - canvas.getBounds().height / 2)
                * Projection.getZoomFactor(zoomlevel));
        result.setLongitude(center.getLongitude() + (x - canvas.getBounds().width / 2)
                * Projection.getZoomFactor(zoomlevel));
        return result;
    }
    
}
