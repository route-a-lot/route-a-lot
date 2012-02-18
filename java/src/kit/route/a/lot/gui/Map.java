package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;

import javax.swing.*;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.gui.event.AddFavoriteEvent;
import kit.route.a.lot.gui.event.AddNavNodeEvent;
import kit.route.a.lot.gui.event.PositionEvent;
import static kit.route.a.lot.common.Listener.*;


public abstract class Map extends JPanel implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;   
   
    private static final int FREEMAPSPACE = 0, POI = 1, FAVORITE = 2, NAVNODE = 3;
    private static final String
        TEXT_EMPTY = "", TEXT_OK = "OK",
        TEXT_POI = " POI:", TEXT_FAVORITE = " Favorit:", TEXT_NAVNODE = " Navigationspunkt:",
        TEXT_INSERT_NAME = "Name hier einfügen...", TEXT_INSERT_DESCRIPTION = "Beschreibung hier einfügen...",
        TEXT_NAVNODES = "NavNodes",
        TEXT_AS_START = "als Start", TEXT_AS_DESTINATION = "als Ziel", TEXT_AS_WAYPOINT = "als Zwischenhalt", 
        TEXT_AS_FAVORITE = "als Favorit", TEXT_DEL_FAVORITE = "lösche Favorit",
        TEXT_DELETE_NAVNODE = "lösche Navigationspunkt",
        TEXT_DESCRIPTION_NAME = "<html><div width='80px'><u>%1$s</u></div></html>",
        TEXT_DESCRIPTION_BODY = "<html><div width='80px'>%1$s</div></html>";
    
    protected int oldMousePosX, oldMousePosY, zoomlevel = 3;
    protected Coordinates center, topLeft = new Coordinates(), bottomRight = new Coordinates();
    
    protected GUI gui;
    private JPopupMenu navNodeMenu;
    private JPopupMenu descriptionMenu;
    private JPopupMenu favoriteMenu;
    private JMenuItem startItem;
    private JMenuItem endItem;
    private JMenuItem stopoverItem;
    private JMenuItem addFavoriteItem;
    private JMenuItem deleteNavPoint;
    private JMenuItem deleteFavoriteItem;
    private JLabel popUpName;
    private JLabel labelPOIName;
    private JLabel labelPOIDescription;
    private JTextField favoriteNameField;
    private JTextField favoriteDescriptionField;
    private JButton addFavoriteButton;
    private MouseEvent clickEvent;
    protected Component canvas;
    
    /**
     * Creates a map canvas, including its context menu.
     * @param gui.getListener()s the collection of gui.getListener() lists from the gui
     * @param navPointsList the list of navigation nodes from the gui
     */
    public Map(GUI parentGUI) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        setBackground(Color.BLACK);
        gui = parentGUI;
        center = new Coordinates(0, 0);
        
        canvas = createCanvas();
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        add(canvas, BorderLayout.CENTER);
        
        //Context menu:
        popUpName = new JLabel();
        startItem = new JMenuItem(TEXT_AS_START);
        endItem = new JMenuItem(TEXT_AS_DESTINATION);
        stopoverItem = new JMenuItem(TEXT_AS_WAYPOINT);
        addFavoriteItem = new JMenuItem(TEXT_AS_FAVORITE);  
        deleteFavoriteItem = new JMenuItem(TEXT_DEL_FAVORITE);
        deleteNavPoint = new JMenuItem(TEXT_DELETE_NAVNODE);
        startItem.setBackground(Color.WHITE);
        endItem.setBackground(Color.WHITE);
        stopoverItem.setBackground(Color.WHITE);
        addFavoriteItem.setBackground(Color.WHITE);
        deleteFavoriteItem.setBackground(Color.WHITE);
        deleteNavPoint.setBackground(Color.WHITE);
        startItem.addActionListener(this);        
        endItem.addActionListener(this);       
        stopoverItem.addActionListener(this); 
        addFavoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                favoriteNameField.setText("Name hier einfügen...");
                favoriteDescriptionField.setText("Beschreibung hier einfügen...");
                favoriteMenu.show(canvas, clickEvent.getX(), clickEvent.getY());
            }
        });
        deleteFavoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getListeners().fireEvent(DELETE_FAVORITE,
                        new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            }
        });
        deleteNavPoint.addActionListener(new ActionListener() {         
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getListeners().fireEvent(DELETE_NAVNODE,
                        new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            }
        });
        
        navNodeMenu = new JPopupMenu(TEXT_NAVNODES);
        navNodeMenu.setBackground(Color.WHITE);
        navNodeMenu.add(popUpName);
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(addFavoriteItem);
        navNodeMenu.add(deleteFavoriteItem);
        navNodeMenu.add(deleteNavPoint);
        
        labelPOIDescription = new JLabel();
        labelPOIName = new JLabel();
        
        descriptionMenu = new JPopupMenu();
        descriptionMenu.setBackground(Color.WHITE);
        descriptionMenu.add(labelPOIName);
        descriptionMenu.add(labelPOIDescription);
        
        favoriteNameField = new JTextField(TEXT_INSERT_NAME);
        favoriteDescriptionField = new JTextField(TEXT_INSERT_DESCRIPTION);
        addFavoriteButton = new JButton(TEXT_OK);
        addFavoriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = favoriteNameField.getText();
                if(!name.equals(TEXT_INSERT_NAME) && name.length() != 0) {
                    String description = favoriteDescriptionField.getText();
                    if (description.equals(TEXT_INSERT_DESCRIPTION) || description.length() == 0) {
                        description = TEXT_EMPTY;
                    }
                    gui.getListeners().fireEvent(ADD_FAVORITE, new AddFavoriteEvent(
                            getPosition(clickEvent.getX(), clickEvent.getY()), name, description));
                }
                favoriteMenu.setVisible(false);
            }
        });
        
        favoriteMenu = new JPopupMenu();
        favoriteMenu.setBackground(Color.WHITE);
        favoriteMenu.add(favoriteNameField);
        favoriteMenu.add(favoriteDescriptionField);
        favoriteMenu.add(addFavoriteButton);
        
        canvas.addMouseListener(new MouseAdapter() {          
            @Override
            public void mousePressed(MouseEvent me) {
                oldMousePosX = me.getX(); 
                oldMousePosY = me.getY();
                checkPopup(me);
            }     
            @Override
            public void mouseReleased(MouseEvent me) {
                mousePressed(me);
            }
        });
    }
    
    /**
     * Converts pixel coordinates into the equivalent projected geo reference system coordinates. The pixel
     * origin is top left corner of the map.
     * @param x the horizontal pixel coordinate
     * @param y the vertical pixel coordinate
     * @return equivalent geo coordinates
     */
    protected abstract Coordinates getPosition(int x, int y);
    
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
     * Returns the view center in geo coordinates.
     * @return the map center coordinates
     */
    public Coordinates getCenter() {
        return center;
    }
    
    /**
     * Sets the zoom level. Negative values will be treated as 0.
     * @param zoomlevel the desired zoom level
     */
    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = Math.max(zoomlevel, 0);
    }
    
    /**
     * Returns the zoom level.
     */
    public int getZoomlevel() {
        return this.zoomlevel;
    }
    
    /**
     * Called when a popup entry concerning navigation nodes has been clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String label = ((JMenuItem) e.getSource()).getText();
        // TODO better implementation?
        int type = label.equals(startItem.getText()) ? 0 : label.equals(endItem.getText()) ? 2 : 1;
        int pos = 0;
        
        switch (gui.getNavNodeList().size()) {
            case 0: pos = 0;
                    break;
            case 1: switch (type) {
                        case 0 : pos = 0; break; 
                        default : pos = 1;
                    }
                    break;
            default: switch (type) {
                        case 0 : pos = 0; break;
                        case 1 : pos = gui.getNavNodeList().size() - 1; break;
                        case 2 : pos = gui.getNavNodeList().size();
                        break;
                    }
        }    
        gui.getListeners().fireEvent(ADD_NAVNODE,
                new AddNavNodeEvent(getPosition(clickEvent.getX(), clickEvent.getY()), pos));
        canvas.repaint();
    }
    
    /**
     * Opens the map context menu if appropriate. Fires a WhatWasClicked event.
     */
    private void checkPopup(MouseEvent me) {
        /*float deltaX = (clickEvent == null) ? 0 : me.getX() - clickEvent.getX();
        float deltaY = (clickEvent == null) ? 0 : me.getY() - clickEvent.getY();
        System.out.println(deltaX + ", " + deltaY);
        if ((deltaX > 0 && deltaY > 0)
            && ((navNodeMenu.isVisible() && deltaX < navNodeMenu.getWidth() && deltaY < navNodeMenu.getHeight())
                || (descriptionMenu.isVisible() && deltaX < descriptionMenu.getWidth() && deltaY < navNodeMenu.getHeight()))) {
            System.out.println("Cannot open!");
            return;
        }//*/
        clickEvent = me;
        gui.getListeners().fireEvent(POSITION_CLICKED,
                new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
    }
      
    /**
     * Adapts the map position and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        oldMousePosX = e.getX();
        oldMousePosY = e.getY();  
        calculateView();   
    }
   
    /**
     * Adapts the zoom level and schedules a map redraw.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Coordinates clickDiff = getPosition(e.getX(), e.getY()).subtract(center);
        int oldZoom = zoomlevel;
        setZoomlevel(zoomlevel + e.getWheelRotation());
        if (zoomlevel != oldZoom) {
            center.add(clickDiff.scale((oldZoom > zoomlevel) ? 0.5f : -1));
            calculateView();
        }     
    }
    
    /**
     * Displays the cursor geo coordinates in the status bar.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Coordinates coordinates = getPosition(e.getX(), e.getY());
        coordinates = ProjectionFactory.getCurrentProjection().getGeoCoordinates(coordinates);
        gui.showMouseCoordinates(coordinates);
    }

    /**
     * Derives the new geo coordinates view constraints from the pixel dimensions of the map and subsequently
     * updates the context.
     */
    void calculateView() {
        bottomRight = new Coordinates(canvas.getHeight(), canvas.getWidth())
                            .scale(Projection.getZoomFactor(zoomlevel) / 2f);
        topLeft = bottomRight.clone().scale(-1).add(center);
        bottomRight.add(center);
        canvas.repaint(); 
    }
        
    public void passElementType(int itemType) {
        descriptionMenu.setVisible(false);
        if (clickEvent.isPopupTrigger()) {
            String name = TEXT_EMPTY;
            switch(itemType) {
                case POI: name = TEXT_POI; break;
                case FAVORITE: name = TEXT_FAVORITE; break;
                case NAVNODE: name = TEXT_NAVNODE; break;
            }
            popUpName.setText(name);
            startItem.setVisible(itemType != NAVNODE);
            endItem.setVisible(itemType != NAVNODE);
            stopoverItem.setVisible(itemType != NAVNODE);
            addFavoriteItem.setVisible(itemType == FREEMAPSPACE);
            deleteFavoriteItem.setVisible(itemType == FAVORITE);
            deleteNavPoint.setVisible(itemType == NAVNODE);
            navNodeMenu.show(clickEvent.getComponent(), clickEvent.getX(), clickEvent.getY());
        } else if (itemType == FAVORITE || itemType == POI){
            gui.getListeners().fireEvent(SHOW_POI_DESCRIPTION,
                    new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            descriptionMenu.show(clickEvent.getComponent(), clickEvent.getX(), clickEvent.getY());
        }
    }
    
    public void passDescription(POIDescription description) {
        labelPOIName.setText(String.format(TEXT_DESCRIPTION_NAME, description.getName()));
        labelPOIDescription.setText(String.format(TEXT_DESCRIPTION_BODY, description.getDescription())); 
    }

}
