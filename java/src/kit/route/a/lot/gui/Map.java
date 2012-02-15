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
import kit.route.a.lot.gui.event.AddFavoriteEvent;
import kit.route.a.lot.gui.event.DeleteFavoriteEvent;
import kit.route.a.lot.gui.event.SelectNavNodeEvent;
import kit.route.a.lot.gui.event.PositionEvent;


public abstract class Map extends JPanel implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;   
    
    protected int oldMousePosX;
    protected int oldMousePosY;
    private int popupXPos;
    private int popupYPos;
    private int oldPopUpXPos;
    private int oldPopUpYPos;
    private int newPopUpXPos;
    private int newPopUpYPos;
    private Coordinates center;
    private Coordinates popUpPosition;
    private boolean popupTrigger = false;
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
    private JLabel showPoiName;
    private JLabel showPoiDescription;
    private JTextField favoriteNameField;
    private JTextField favoriteDescriptionField;
    private JButton addFavoriteButton;
    private MouseEvent clickEvent;
    private Component canvas;
    
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
        startItem = new JMenuItem("als Start");
        endItem = new JMenuItem("als Ziel");
        stopoverItem = new JMenuItem("als Zwischenhalt");
        addFavoriteItem = new JMenuItem("als Favorit");  
        deleteFavoriteItem = new JMenuItem("lösche Favorit");
        deleteNavPoint = new JMenuItem("lösche Navigationspunkt");
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
                favoriteMenu.show(canvas, popupXPos - canvas.getX(), popupYPos - canvas.getY());
            }
        });
        deleteFavoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Listeners.fireEvent(gui.getListeners().deleteFavPoint, new DeleteFavoriteEvent(popUpPosition));
            }
        });
        deleteNavPoint.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Listeners.fireEvent(gui.getListeners().deleteNavPoint, new PositionEvent(popUpPosition));
            }
        });
        
        navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.setBackground(Color.WHITE);
        navNodeMenu.add(popUpName);
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(addFavoriteItem);
        navNodeMenu.add(deleteFavoriteItem);
        navNodeMenu.add(deleteNavPoint);
        
        showPoiDescription = new JLabel();
        showPoiName = new JLabel();
        
        descriptionMenu = new JPopupMenu();
        descriptionMenu.setBackground(Color.WHITE);
        descriptionMenu.add(showPoiName);
        descriptionMenu.add(showPoiDescription);
        
        favoriteNameField = new JTextField("Name hier einfügen...");
        favoriteDescriptionField = new JTextField("Beschreibung hier einfügen...");
        addFavoriteButton = new JButton("Ok");
        addFavoriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!favoriteNameField.getText().equals("Name hier einfügen...") && !favoriteNameField.getText().equals("")) {
                    if(favoriteDescriptionField.getText().equals("Beschreibung hier einfügen...") || favoriteDescriptionField.getText().equals("")) {
                        Listeners.fireEvent(gui.getListeners().addFav, new AddFavoriteEvent(popUpPosition, favoriteNameField.getText(), ""));
                    } else {
                        Listeners.fireEvent(gui.getListeners().addFav, new AddFavoriteEvent(popUpPosition, favoriteNameField.getText(), favoriteDescriptionField.getText()));
                    }
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
            @Override // used for dragging, relocate?
            public void mousePressed(MouseEvent me) {
                popupXPos = me.getX();
                popupYPos = me.getY();
                oldMousePosX = me.getX(); 
                oldMousePosY = me.getY();
                newPopUpXPos = me.getX();
                newPopUpYPos = me.getY();
                checkPopup(me);
                oldPopUpXPos = me.getX();
                oldPopUpYPos = me.getY();
            }     
            @Override
            public void mouseReleased(MouseEvent me) {
                mousePressed(me);
            }
            @Override
            public void mouseClicked(MouseEvent me) {
                mousePressed(me);
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
                new SelectNavNodeEvent(getCoordinates(popupXPos - canvas.getX(), popupYPos - canvas.getY()), pos));
        canvas.repaint();
    }
    
    /**
     * Opens the map context menu if appropriate. Fires a WhatWasClicked event.
     */
    private void checkPopup(MouseEvent e) {
        clickEvent = e;
        Listeners.fireEvent(gui.getListeners().clickPosition, new PositionEvent(getCoordinates(popupXPos, popupYPos)));
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
        Coordinates clickPos = getCoordinates(e.getX() - canvas.getX(), e.getY() - canvas.getY());
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
     * Converts pixel coordinates into the equivalent projected geo reference system coordinates. The pixel
     * origin is top left corner of the map.
     * @param x the horizontal pixel coordinate
     * @param y the vertical pixel coordinate
     * @return equivalent geo coordinates
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
    
    public void popUpTriggered(int itemType, Coordinates position) {
        popUpPosition = position;
        descriptionMenu.setVisible(false);
        if (clickEvent.isPopupTrigger()) {
            popupTrigger = true;
            descriptionMenu.setVisible(false);
            switch(itemType) {
                case 0: popUpName.setText("");
                    break;
                case 1: popUpName.setText(" POI:");
                    break;
                case 2: popUpName.setText(" Favorit");
                    break;
                case 3: popUpName.setText(" Navigationspunkt:");
                    break;
            }
            startItem.setVisible(itemType == 0 || itemType == 1 || itemType == 2);
            endItem.setVisible(itemType == 0 || itemType == 1 || itemType == 2);
            stopoverItem.setVisible(itemType == 0 || itemType == 1 || itemType == 2);
            addFavoriteItem.setVisible(itemType == 0);
            deleteFavoriteItem.setVisible(itemType == 2);
            deleteNavPoint.setVisible(itemType == 3);
            navNodeMenu.show(clickEvent.getComponent(), popupXPos, popupYPos);
        } else if(popupTrigger == false && ((oldPopUpXPos >= newPopUpXPos - 2 && oldPopUpXPos <= newPopUpXPos + 2)
                || (oldPopUpYPos == newPopUpYPos - 2 && oldPopUpYPos == newPopUpYPos + 2))){
            descriptionMenu.setVisible(false);
            popupXPos = clickEvent.getX();
            popupYPos = clickEvent.getY();
            switch(itemType) {
                case 1: 
//                    showPoiName.setText("POI");
//                    showPoiDescription.setText("<html><div width='80px'>"+"safwadsw afwadwa swafafad sawd"+"</div></html>");
//                    descriptionMenu.show(clickEvent.getComponent(), popupXPos, popupYPos);
                    Listeners.fireEvent(gui.getListeners().poiDescription, new PositionEvent(position));
                    break;
                case 2:
                    Listeners.fireEvent(gui.getListeners().favDescription, new PositionEvent(position));
                    break;
                default: showPoiDescription.setText("");
                    showPoiName.setText("");
                    descriptionMenu.setVisible(false);
                    break;
            }
        } else {
            popupTrigger = false;
        }
    }
    
    public void showPOIDescription(POIDescription poiDescription) {
        showPoiName.setText("<html><div width='80px'><u>" + poiDescription.getName() + "</u></div></html>");
        showPoiDescription.setText("<html><div width='80px'>" + poiDescription.getDescription() + "</div></html>");
        descriptionMenu.show(clickEvent.getComponent(), popupXPos, popupYPos);
    }
    
    public void showFavDescription(POIDescription favDescription) {
        showPoiName.setText("<html><div width='160px'><u>" + favDescription.getName() + "</u></div></html>");
        showPoiDescription.setText("<html><div width='80px'>" + favDescription.getDescription() + "</div></html>");
        descriptionMenu.show(clickEvent.getComponent(), popupXPos, popupYPos);
    }

}
