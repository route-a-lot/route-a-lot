package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.listener.RALListener;
import kit.route.a.lot.gui.event.FavoriteAddedEvent;
import kit.route.a.lot.gui.event.NavNodeSelectedEvent;
import kit.route.a.lot.gui.event.PositionEvent;
import kit.route.a.lot.gui.event.ViewChangedEvent;
import kit.route.a.lot.map.rendering.Projection;


public class Map2D extends JComponent implements MouseMotionListener, MouseWheelListener, ActionListener {

    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Map2D.class);

    private ListenerLists listener;
    ArrayList<Coordinates> navPoints;

    private int oldMousePosX;
    private int oldMousePosY;
    private int popUpXPos;
    private int popUpYPos;
    private Coordinates center;
    private int zoomlevel = 0;
    private Coordinates topLeft = new Coordinates();
    private Coordinates bottomRight = new Coordinates();

    private JPopupMenu navNodeMenu;
    private JMenuItem startItem;
    private JMenuItem endItem;
    private AbstractButton stopoverItem;
    private AbstractButton favoriteItem;

    private JPanel canvas;

    public Map2D(ListenerLists listeners, ArrayList<Coordinates> navPointsList) {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(this.getSize()));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        this.setVisible(true);
        
        // Resize map context:
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateView();
            }
        });

        this.canvas = new JPanel();
        this.canvas.setVisible(true);
        this.canvas.addMouseMotionListener(this);
        this.canvas.addMouseWheelListener(this);
        this.add(canvas, BorderLayout.CENTER);

        this.listener = listeners;
        this.navPoints = navPointsList;
        this.center = new Coordinates(0, 0);
        
        
        // all popup menu handling below ;)
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
                    getCoordinates(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());
                ListenerLists.fireEvent(listener.addFav, new FavoriteAddedEvent(favoriteCoordinates, "", ""));
            }
        });

        navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(favoriteItem); 
        
        this.canvas.addMouseListener(new MouseAdapter() {          
            @Override // used for dragging, relocate?
            public void mousePressed(MouseEvent me) {
                oldMousePosX = me.getX(); 
                oldMousePosY = me.getY();
            }     
            @Override
            public void mouseReleased(MouseEvent me) {
                checkPopup(me);
            }
        });
    }

    public void setCenter(Coordinates center) {
        this.center = center;
    }

    public Coordinates getCenter() {
        return center;
    }

    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = Math.max(zoomlevel, 0);
        calculateView();
    }
    
    /**
     * Called when a popup entry has been clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String label = ((JMenuItem) e.getSource()).getText();
        // TODO better implementation
        int type = label.equals(startItem.getText()) ? 0 : label.equals(endItem.getText()) ? 2 : 1;         
        Coordinates pos = getCoordinates(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());        
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
        ListenerLists.fireEvent(listener.targetSelected, new NavNodeSelectedEvent(pos, navPoints.indexOf(pos)));
        repaint();
    }
    
    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            navNodeMenu.show(e.getComponent(), e.getX(), e.getY());
            System.out.println("geklickt");
        }
        popUpXPos = e.getX();
        popUpYPos = e.getY();
        ListenerLists.fireEvent(listener.whatWasClicked, new PositionEvent(getCoordinates(popUpXPos, popUpYPos)));
    }
      
    @Override
    public void mouseDragged(MouseEvent e) {
        center.setLongitude(center.getLongitude() - (e.getX() - oldMousePosX) * Projection.getZoomFactor(zoomlevel));
        center.setLatitude(center.getLatitude() - (e.getY() - oldMousePosY) * Projection.getZoomFactor(zoomlevel));
        oldMousePosX = e.getX();
        oldMousePosY = e.getY();
        calculateView();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Coordinates clickPos = getCoordinates(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        int yDiff = e.getY() - canvas.getY() - canvas.getVisibleRect().height / 2;
        int xDiff = e.getX() - canvas.getX() - canvas.getVisibleRect().width / 2;
        center.setLatitude(clickPos.getLatitude() - yDiff * Projection.getZoomFactor(zoomlevel));
        center.setLongitude(clickPos.getLongitude() - xDiff * Projection.getZoomFactor(zoomlevel));
        setZoomlevel(zoomlevel + e.getWheelRotation());
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        //Coordinates mousePosCoordinates = getCoordinates(e.getX() - drawMap.getX(), e.getY() - drawMap.getY());
        //l_position.setText(mousePosCoordinates.toString()); TODO
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);      
        /*if (context == null) {
            context = new Context2D(topLeft, bottomRight, canvas.getGraphics());
        } else {
            context.setTopLeft(topLeft);
            context.setBottomRight(bottomRight);
            context.calculateSize();
        }*/       
        ListenerLists.fireEvent(listener.viewChanged,
                new ViewChangedEvent(new Context2D(topLeft, bottomRight, g), zoomlevel));
    }

    /**
     * Derives the new geo coordinates view constraints from the pixel dimensions of the map and subsequently
     * updates the context.
     */
    void calculateView() {
        // jedit getVisibleRect -> getBounds
        topLeft.setLatitude(getCenter().getLatitude() - canvas.getBounds().height
                * Projection.getZoomFactor(zoomlevel) / 2.f);
        topLeft.setLongitude(getCenter().getLongitude() - canvas.getBounds().width
                * Projection.getZoomFactor(zoomlevel) / 2.f);
        bottomRight.setLatitude(canvas.getBounds().height * Projection.getZoomFactor(zoomlevel) / 2.f
                + getCenter().getLatitude());
        bottomRight.setLongitude(canvas.getBounds().width * Projection.getZoomFactor(zoomlevel) / 2.f
                + getCenter().getLongitude());
        repaint();
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
        result.setLatitude(getCenter().getLatitude() + (y - canvas.getBounds().height / 2)
                * Projection.getZoomFactor(zoomlevel));
        result.setLongitude(getCenter().getLongitude() + (x - canvas.getBounds().width / 2)
                * Projection.getZoomFactor(zoomlevel));
        return result;
    }

}
