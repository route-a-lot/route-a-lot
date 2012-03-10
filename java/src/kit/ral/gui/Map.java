package kit.ral.gui;

import static kit.ral.common.Listener.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kit.ral.common.Coordinates;
import kit.ral.common.Listener;
import kit.ral.common.description.POIDescription;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.common.util.MathUtil;
import kit.ral.gui.event.AddFavoriteEvent;
import kit.ral.gui.event.Event;
import kit.ral.gui.event.PositionNumberEvent;
import kit.ral.gui.event.PositionEvent;


public abstract class Map extends JPanel implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;   
   
    private static final int FREEMAPSPACE = 0, POI = 1, FAVORITE = 2, NAVNODE = 3;
    private static final String
        TEXT_EMPTY = "", TEXT_OK = "OK",
        TEXT_POI = " POI:", TEXT_FAVORITE = " Favorit:", TEXT_NAVNODE = " Navigationspunkt:",
        TEXT_INSERT_NAME = "Name...", TEXT_INSERT_DESCRIPTION = "Beschreibung...",
        TEXT_NAVNODES = "NavNodes",
        TEXT_AS_START = "als Start", TEXT_AS_DESTINATION = "als Ziel", TEXT_AS_WAYPOINT = "als Zwischenhalt", 
        TEXT_AS_FAVORITE = "als Favorit", TEXT_DEL_FAVORITE = "lösche Favorit",
        TEXT_DELETE_NAVNODE = "lösche Navigationspunkt",
        TEXT_DESCRIPTION_NAME = "<html><div width='80px'><u>%1$s</u></div></html>",
        TEXT_DESCRIPTION_BODY = "<html><div width='80px'>%1$s</div></html>";
    
    private static final int MAX_ZOOMLEVEL = 20;
    
    protected int oldMousePosX, oldMousePosY, zoomlevel = 3;
    protected Coordinates center = new Coordinates();
    
    protected GUI gui;
    protected Component canvas;
    
    private JPopupMenu navNodeMenu, descriptionMenu, favoriteMenu;
    private JMenuItem startItem, endItem, stopoverItem;
    private JMenuItem addFavoriteItem, deleteFavoriteItem, deleteNavPoint;
    private JLabel popUpName, labelPOIName, labelPOIDescription;
    private MouseEvent clickEvent;
    
    
    /**
     * Creates a map canvas, including its context menus.
     * @param parentGUI the parent GUI object
     */
    public Map(GUI parentGUI) {
        gui = parentGUI;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        setBackground(Color.BLACK);
        add(canvas = createCanvas(), BorderLayout.CENTER);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addMouseListener(new MouseAdapter() {          
            public void mousePressed(MouseEvent me) {           
                checkPopup(me);
            }     
            public void mouseReleased(MouseEvent me) {
                checkPopup(me);
            }
            public void mouseClicked(MouseEvent me) {
                if(me.getClickCount() == 2 && isMouseButtonPressed(me, 1)) {
                    zoomAtPosition(me.getX(), me.getY(), -1);   
                }   
            }     
        });
               
        Listener.addListener(TILE_RENDERED, new Listener() {
            public void handleEvent(Event e) {
                render();
            }      
        });
        
        // ADD FAVORITE POPUP
        final JTextField favoriteNameField = new JTextField(TEXT_INSERT_NAME);  
        favoriteNameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                favoriteNameField.selectAll();
            }
        });
        final JTextArea favoriteDescriptionField = new JTextArea(TEXT_INSERT_DESCRIPTION, 3, 1);
        favoriteDescriptionField.setBorder(BorderFactory.createEtchedBorder());
        favoriteDescriptionField.setPreferredSize(new Dimension(180, 80));
        favoriteDescriptionField.setFont(favoriteNameField.getFont());
        favoriteDescriptionField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                favoriteDescriptionField.selectAll();
            }
        });
        JButton addFavoriteButton = new JButton(TEXT_OK);
        addFavoriteButton.setMaximumSize(new Dimension(180, 30));
        addFavoriteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = favoriteNameField.getText();
                if(!name.equals(TEXT_INSERT_NAME) && name.length() != 0) {
                    String description = favoriteDescriptionField.getText();
                    if (description.equals(TEXT_INSERT_DESCRIPTION) || description.length() == 0) {
                        description = TEXT_EMPTY;
                    }
                    Listener.fireEvent(ADD_FAVORITE, new AddFavoriteEvent(
                            getPosition(clickEvent.getX(), clickEvent.getY()), name, description));
                }
                favoriteMenu.setVisible(false);
            }
        });  
        
        
        Box favoriteMenuBox = new Box(BoxLayout.Y_AXIS);
        favoriteMenuBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        favoriteMenuBox.add(favoriteNameField);
        favoriteMenuBox.add(Box.createVerticalStrut(3));
        favoriteMenuBox.add(favoriteDescriptionField);
        favoriteMenuBox.add(addFavoriteButton);
        
        favoriteMenu = new JPopupMenu();
        favoriteMenu.setBorder(BorderFactory.createEtchedBorder());
        favoriteMenu.setBackground(Color.WHITE);
        favoriteMenu.add(favoriteMenuBox);
        
        // POI / FAVORITE DESCRIPTION POPUP
        labelPOIDescription = new JLabel();
        labelPOIName = new JLabel();
        labelPOIName.setForeground(Color.WHITE);
        descriptionMenu = new JPopupMenu();
        descriptionMenu.setBackground(Color.WHITE);
        descriptionMenu.add(labelPOIName);
        descriptionMenu.add(labelPOIDescription);
        
        // CONTEXT MENU
        URL startIcon = ClassLoader.getSystemResource("icon_greenarrow.png");
        URL endIcon = ClassLoader.getSystemResource("icon_redarrow.png");
        URL stopoverIcon = ClassLoader.getSystemResource("icon_yellowarrow.png");
        URL favoriteIcon = ClassLoader.getSystemResource("icon_bluecircle.png");
        URL deleteIcon = ClassLoader.getSystemResource("icon_delete.png");
        popUpName = new JLabel();
        popUpName.setForeground(Color.LIGHT_GRAY);
        startItem = new JMenuItem(TEXT_AS_START, new ImageIcon(startIcon));
        endItem = new JMenuItem(TEXT_AS_DESTINATION, new ImageIcon(endIcon));
        stopoverItem = new JMenuItem(TEXT_AS_WAYPOINT, new ImageIcon(stopoverIcon));
        addFavoriteItem = new JMenuItem(TEXT_AS_FAVORITE, new ImageIcon(favoriteIcon));  
        deleteFavoriteItem = new JMenuItem(TEXT_DEL_FAVORITE, new ImageIcon(deleteIcon));
        deleteNavPoint = new JMenuItem(TEXT_DELETE_NAVNODE, new ImageIcon(deleteIcon));
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
            public void actionPerformed(ActionEvent e) {
                favoriteNameField.setText(TEXT_INSERT_NAME);
                favoriteDescriptionField.setText(TEXT_INSERT_DESCRIPTION);
                favoriteMenu.show(canvas, clickEvent.getX(), clickEvent.getY());
            }
        });
        deleteFavoriteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Listener.fireEvent(DELETE_FAVORITE,
                        new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            }
        });
        deleteNavPoint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Listener.fireEvent(DELETE_NAVNODE,
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
     * Sets the zoom level.
     * @param zoomlevel the desired zoom level
     */
    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = MathUtil.clip(zoomlevel, 0, MAX_ZOOMLEVEL);
        Listener.fireEvent(VIEW_CHANGED,
                new PositionNumberEvent(center.clone(), zoomlevel));
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
        
        switch (gui.countNavNodes()) {
            case 0: pos = 0;
                    break;
            case 1: switch (type) {
                        case 0 : pos = 0; break; 
                        default : pos = 1;
                    }
                    break;
            default: switch (type) {
                        case 0 : pos = 0; break;
                        case 1 : pos = gui.countNavNodes() - 1; break;
                        case 2 : pos = gui.countNavNodes();
                        break;
                    }
        }    
        Listener.fireEvent(ADD_NAVNODE,
                new PositionNumberEvent(getPosition(clickEvent.getX(), clickEvent.getY()), pos));
        canvas.repaint();
    }
    
    /**
     * Opens the map context menu if appropriate. Fires a WhatWasClicked event.
     */
    private void checkPopup(MouseEvent me) {  
        oldMousePosX = Integer.MIN_VALUE; // invalidation signal
        clickEvent = me;
        Listener.fireEvent(POSITION_CLICKED,
                new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
    }
      
    /**
     * Adapts the map position.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (descriptionMenu.isVisible()) {
            descriptionMenu.setVisible(false);
        }
        oldMousePosX = e.getX();
        oldMousePosY = e.getY();  
        render();
    }
   
    /**
     * Adapts the zoom level and schedules a map redraw.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoomAtPosition(e.getX(), e.getY(), e.getWheelRotation());
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
     * Redraws the map.
     */
    void render() {
        canvas.repaint(); 
    }
        
    public void passElementType(int itemType) {
        if (hasButtonChanged(clickEvent, 3)) {
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
        } else if (hasButtonChanged(clickEvent, 1)
                && (itemType == FAVORITE || itemType == POI)){
            Listener.fireEvent(SHOW_POI_DESCRIPTION,
                    new PositionEvent(getPosition(clickEvent.getX(), clickEvent.getY())));
            descriptionMenu.show(clickEvent.getComponent(), clickEvent.getX(), clickEvent.getY());
        }
    }
    
    public void passDescription(POIDescription description) {
        labelPOIName.setText(String.format(TEXT_DESCRIPTION_NAME, description.getName()));
        labelPOIDescription.setText(String.format(TEXT_DESCRIPTION_BODY, description.getDescription())); 
    }

    /**
     * Checks whether the given mouse button was pressed when the event was created.
     * @param e the created event
     * @param buttonID the ID of the button to be queried (1..3)
     * @return whether the mouse button was clicked
     */
    protected static boolean isMouseButtonPressed(MouseEvent e, int buttonID) {
        int buttonMask;
        switch (buttonID) {
            case 1: buttonMask = MouseEvent.BUTTON1_DOWN_MASK; break;
            case 2: buttonMask = MouseEvent.BUTTON2_DOWN_MASK; break;
            case 3: buttonMask = MouseEvent.BUTTON3_DOWN_MASK; break;
            default: return false;
        }
        return (e.getModifiersEx() & buttonMask) != 0;
    }
    
    protected static boolean hasButtonChanged(MouseEvent e, int buttonID) {
        return (e.getButton() == buttonID);
    }
    
    
    private void zoomAtPosition(int x, int y, int amount) {
        Coordinates clickDiff = getPosition(x, y).subtract(center);
        int oldZoom = zoomlevel;
        setZoomlevel(zoomlevel + amount);
        if (zoomlevel != oldZoom) {
            center.add(clickDiff.scale((oldZoom > zoomlevel) ? 0.5f : -1));
            render();
        }
    }
}
