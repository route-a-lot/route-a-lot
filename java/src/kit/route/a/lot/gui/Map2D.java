package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

//import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.listener.RALListener;
import kit.route.a.lot.gui.event.AddFavEvent;
import kit.route.a.lot.gui.event.NavNodeSelectedEvent;
import kit.route.a.lot.gui.event.PositionEvent;
import kit.route.a.lot.gui.event.ViewChangedEvent;
import kit.route.a.lot.map.rendering.Projection;


public class Map2D extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final long serialVersionUID = 1L;
    //private static Logger logger = Logger.getLogger(Map2D.class);

    private ListenerLists listener;
    ArrayList<Coordinates> navPoints;

    private int oldMousePosX;
    private int oldMousePosY;
    private int popUpXPos;
    private int popUpYPos;
    private Coordinates center;
    private int zoomlevel = 0;
    private Context context;
    private Coordinates topLeft = new Coordinates();
    private Coordinates bottomRight = new Coordinates();

    private Component selectedComponent;

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
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        this.canvas.addMouseWheelListener(this);
        this.add(canvas, BorderLayout.CENTER);

        this.listener = listeners;
        this.navPoints = navPointsList;
        this.center = new Coordinates(0, 0);
        
        startItem = new JMenuItem("als Start");
        startItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates =
                        calculateClickPos(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());
                if (navPoints.size() == 0) {
                    navPoints.add(navPointCoordinates);
                } else {
                    navPoints.set(0, navPointCoordinates);
                }

                NavNodeSelectedEvent navEvent =
                        new NavNodeSelectedEvent(this, navPointCoordinates, navPoints
                                .indexOf(navPointCoordinates), context);
                for (RALListener lis : listener.targetSelected) {
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });

        endItem = new JMenuItem("als Ziel");
        endItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates =
                        calculateClickPos(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());
                if (navPoints.size() == 0) {
                    navPoints.add(new Coordinates());
                    navPoints.add(navPointCoordinates);
                } else if (navPoints.size() == 1) {
                    navPoints.add(navPointCoordinates);
                } else {
                    navPoints.set(navPoints.size() - 1, navPointCoordinates);
                }
                NavNodeSelectedEvent navEvent =
                        new NavNodeSelectedEvent(this, navPointCoordinates, navPoints
                                .indexOf(navPointCoordinates), context);
                for (RALListener lis : listener.targetSelected) {
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });

        stopoverItem = new JMenuItem("als Zwischenhalt");
        stopoverItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates =
                        calculateClickPos(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());
                if (navPoints.size() == 0) {
                    navPoints.add(new Coordinates());
                    navPoints.add(navPointCoordinates);
                    navPoints.add(new Coordinates());
                } else if (navPoints.size() == 1) {
                    navPoints.add(navPointCoordinates);
                    navPoints.add(new Coordinates());
                } else {
                    navPoints.add(navPoints.size() - 1, navPointCoordinates);
                }
                NavNodeSelectedEvent navEvent =
                        new NavNodeSelectedEvent(this, navPointCoordinates, navPoints
                                .indexOf(navPointCoordinates), context);
                for (RALListener lis : listener.targetSelected) {
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });

        favoriteItem = new JMenuItem("als Favorit");
        favoriteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates favoriteCoordinates =
                        calculateClickPos(popUpXPos - canvas.getX(), popUpYPos - canvas.getY());
                AddFavEvent addFavEvent = new AddFavEvent(this, favoriteCoordinates, "", "");
                for (RALListener lis : listener.addFav) {
                    lis.handleRALEvent(addFavEvent);
                }
                repaint();
            }
        });

        navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(favoriteItem); 
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
    
    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            selectedComponent = e.getComponent();
            navNodeMenu.show(e.getComponent(), e.getX(), e.getY());

            popUpXPos = e.getX();
            popUpYPos = e.getY();

            System.out.println("geklickt");
        } else {
            popUpXPos = e.getX();
            popUpYPos = e.getY();
            PositionEvent posEv = new PositionEvent(this, calculateClickPos(popUpXPos, popUpYPos));
            for (RALListener lis : listener.whatWasClicked) {
                lis.handleRALEvent(posEv);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        checkPopup(me);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        checkPopup(me);
        oldMousePosX = me.getX();
        oldMousePosY = me.getY();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        checkPopup(me);
    }    
    
    @Override
    public void mouseDragged(MouseEvent e) {
        getCenter().setLongitude(
                getCenter().getLongitude() - (e.getX() - oldMousePosX) * Projection.getZoomFactor(zoomlevel));
        getCenter().setLatitude(
                getCenter().getLatitude() - (e.getY() - oldMousePosY) * Projection.getZoomFactor(zoomlevel));
        oldMousePosX = e.getX();
        oldMousePosY = e.getY();
        calculateView();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Coordinates clickPos = calculateClickPos(e.getX() - canvas.getX(), e.getY() - canvas.getY());
        int yDiff = e.getY() - canvas.getY() - canvas.getVisibleRect().height / 2;
        int xDiff = e.getX() - canvas.getX() - canvas.getVisibleRect().width / 2;

        float newCenterLat = clickPos.getLatitude() - yDiff * Projection.getZoomFactor(zoomlevel);
        float newCenterLon = clickPos.getLongitude() - xDiff * Projection.getZoomFactor(zoomlevel);
        getCenter().setLatitude(newCenterLat);
        getCenter().setLongitude(newCenterLon);

        setZoomlevel(zoomlevel + e.getWheelRotation());
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        //Coordinates mousePosCoordinates =
        //        calculateClickPos(e.getX() - drawMap.getX(), e.getY() - drawMap.getY());
        // l_position.setText(mousePosCoordinates.toString()); TODO
    }
    
    @Override
    public void mouseExited(MouseEvent arg0) {}
    
    @Override
    public void mouseEntered(MouseEvent arg0) {}
   
    

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, zoomlevel);
        for (RALListener lis : listener.viewChanged) {
            lis.handleRALEvent(viewEvent);
        }
    }//*/

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

        
        //if (context == null) {
            context = new Context2D(topLeft, bottomRight, canvas.getGraphics());
        /*} else {
            context.setTopLeft(topLeft);
            context.setBottomRight(bottomRight);
            context.calculateSize();
        }*/
        ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, zoomlevel);
        for (RALListener lis : listener.viewChanged) {
            lis.handleRALEvent(viewEvent);
        }
    }

    /**
     * Converts pixel coordinates into the eqivalent projected geo reference system coordinates. The pixel
     * origin is top left corner of the map.
     * @param x the horizontal pixel coordinate
     * @param y the vertical pixel coordinate
     * @return eqivalent geo coordinates
     */
    private Coordinates calculateClickPos(int x, int y) {
        Coordinates clickPos = new Coordinates();
        // jedit getVisibleRect -> getBounds
        clickPos.setLatitude(getCenter().getLatitude() + (y - canvas.getBounds().height / 2)
                * Projection.getZoomFactor(zoomlevel));
        clickPos.setLongitude(getCenter().getLongitude() + (x - canvas.getBounds().width / 2)
                * Projection.getZoomFactor(zoomlevel));
        return clickPos;
    }
}
