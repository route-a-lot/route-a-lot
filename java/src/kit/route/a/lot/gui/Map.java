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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.gui.event.AddFavoriteEvent;
import kit.route.a.lot.gui.event.SelectNavNodeEvent;
import kit.route.a.lot.gui.event.PositionEvent;


public abstract class Map extends JPanel implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;   
   
    private static final String TEXT_EMPTY = ""; 
    private static final String TEXT_POI = " POI:"; 
    private static final String TEXT_FAVORITE = " Favorit:";
    private static final String TEXT_NAVNODE = " Navigationspunkt:";
    private static final String TEXT_INSERT_NAME = "Name hier einfügen...";
    private static final String TEXT_INSERT_DESCRIPTION = "Beschreibung hier einfügen...";
    private static final String TEXT_NAVNODES = "NavNodes";
    private static final String TEXT_OK = "OK";
    private static final String TEXT_AS_START = "als Start";
    private static final String TEXT_AS_DESTINATION = "als Ziel";
    private static final String TEXT_AS_WAYPOINT = "als Zwischenhalt";
    private static final String TEXT_AS_FAVORITE = "als Favorit";
    private static final String TEXT_DEL_FAVORITE = "lösche Favorit";
    private static final String TEXT_DEL_NAVNODE = "lösche Navigationspunkt";
    private static final String TEXT_DESCRIPTION_NAME = "<html><div width='80px'><u>%1$s</u></div></html>";
    private static final String TEXT_DESCRIPTION_BODY = "<html><div width='80px'>%1$s</div></html>";
    protected int oldMousePosX;
    protected int oldMousePosY;
    private Coordinates center;
    protected int zoomlevel = 3;
    protected Coordinates topLeft = new Coordinates();
    protected Coordinates bottomRight = new Coordinates();
    
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
        deleteNavPoint = new JMenuItem(TEXT_DEL_NAVNODE);
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
                favoriteMenu.show(canvas, clickEvent.getX() - canvas.getX(),
                        clickEvent.getY() - canvas.getY());
            }
        });
        deleteFavoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Listeners.fireEvent(gui.getListeners().deleteFavPoint,
                        new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            }
        });
        deleteNavPoint.addActionListener(new ActionListener() {         
            @Override
            public void actionPerformed(ActionEvent e) {
                Listeners.fireEvent(gui.getListeners().deleteNavPoint,
                        new PositionEvent(getPosition(clickEvent.getY(), clickEvent.getX())));
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
                    Listeners.fireEvent(gui.getListeners().addFav, new AddFavoriteEvent(
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
        
        switch (gui.getNavPointsList().size()) {
            case 0: pos = 0;
                    break;
            case 1: switch (type) {
                        case 0 : pos = 0; break; 
                        default : pos = 1;
                    }
                    break;
            default: switch (type) {
                        case 0 : pos = 0; break;
                        case 1 : pos = gui.getNavPointsList().size() - 1; break;
                        case 2 : pos = gui.getNavPointsList().size();
                        break;
                    }
        }    
        Listeners.fireEvent(gui.getListeners().targetSelected,
                new SelectNavNodeEvent(getPosition(clickEvent.getX() - canvas.getX(),
                        clickEvent.getY() - canvas.getY()), pos));
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
        Listeners.fireEvent(gui.getListeners().clickPosition,
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
        Coordinates clickPos = getPosition(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        int yDiff = e.getY() - canvas.getY() - canvas.getBounds().height / 2;
        int xDiff = e.getX() - canvas.getX() - canvas.getBounds().width / 2;
        setZoomlevel(zoomlevel + e.getWheelRotation());
        center.setLatitude(clickPos.getLatitude() - yDiff * Projection.getZoomFactor(zoomlevel));
        center.setLongitude(clickPos.getLongitude() - xDiff * Projection.getZoomFactor(zoomlevel));
        calculateView();
    }
    
    /**
     * Displays the cursor geo coordinates in the status bar.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // Coordinates mousePosCoordinates = getCoordinates(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        // Coordinates geoCoordinates = Projection.getProjectionForCurrentMap().localCoordinatesToGeoCoordinates(mousePosCoordinates);
        // gui.l_position.setText(mousePosCoordinates.toString() /*+ " /// " + geoCoordinates.toString()*/);
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
        
    public void triggerPopup(int itemType) {
        descriptionMenu.setVisible(false);
        if (clickEvent.isPopupTrigger()) {
            String name = TEXT_EMPTY;
            switch(itemType) {
                case GUI.POI: name = TEXT_POI; break;
                case GUI.FAVORITE: name = TEXT_FAVORITE; break;
                case GUI.NAVNODE: name = TEXT_NAVNODE; break;
            }
            popUpName.setText(name);
            startItem.setVisible(itemType != GUI.NAVNODE);
            endItem.setVisible(itemType != GUI.NAVNODE);
            stopoverItem.setVisible(itemType != GUI.NAVNODE);
            addFavoriteItem.setVisible(itemType == GUI.FREEMAPSPACE);
            deleteFavoriteItem.setVisible(itemType == GUI.FAVORITE);
            deleteNavPoint.setVisible(itemType == GUI.NAVNODE);
            navNodeMenu.show(clickEvent.getComponent(), clickEvent.getX(), clickEvent.getY());
        } else if (itemType == GUI.FAVORITE || itemType == GUI.POI){
            List<GeneralListener> listeners = (itemType == GUI.POI)
                ? gui.getListeners().poiDescription : gui.getListeners().favDescription;
            Listeners.fireEvent(listeners, new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            descriptionMenu.show(clickEvent.getComponent(), clickEvent.getX(), clickEvent.getY());
        }
    }
    
    public void passDescription(POIDescription description) {
        labelPOIName.setText(String.format(TEXT_DESCRIPTION_NAME, description.getName()));
        labelPOIDescription.setText(String.format(TEXT_DESCRIPTION_BODY, description.getDescription())); 
    }

}
