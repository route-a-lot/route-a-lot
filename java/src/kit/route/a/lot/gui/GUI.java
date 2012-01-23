package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import kit.route.a.lot.common.ContextSW;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.listener.RALListener;
import kit.route.a.lot.map.rendering.Projection;


public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<RALListener> targetSelectedList;
    private ArrayList<RALListener> viewChangedList;
    private ArrayList<RALListener> importOsmFileList;
    private ArrayList<RALListener> optimizeRouteList;
    private ArrayList<RALListener> whatWasClickedList;
    private ArrayList<RALListener> addFavList;
    private ArrayList<RALListener> loadRouteList;
    private ArrayList<RALListener> saveRouteList;
    private ArrayList<RALListener> exportRoutList;
    private ArrayList<RALListener> speedList;
    private ArrayList<RALListener> closeList;
    private ArrayList<RALListener> heightMalusList;
    private ArrayList<RALListener> highwayMalusList;
    private ArrayList<RALListener> importHeightMapList;
    private ArrayList<RALListener> printRouteList;
    private ArrayList<Coordinates> navPointsList;
    
    private JPopupMenu navNodeMenu;
    private JTabbedPane tabbpane;
    
    private JFileChooser importFC;
    private JFileChooser loadRoute;
    private JFileChooser saveRoute;
    private JFileChooser exportRoute;
    private JFileChooser importHeightMap;

    private JButton importOSM;
    private JButton load;
    private JButton save;
    private JButton kmlExport;
    private JButton print;
    private JButton graphics;
    private JButton addTextPoints;
    private JButton optimizeRoute;
    private JButton deleteMapButton;
    private JButton activateMapButton;
    private JButton heightMapManagement;
    
    private JComboBox chooseImportedMap;

    private JLabel l_activeRoute;
    private JLabel l_position;
    private JLabel l_routeText;
    private JLabel l_highwayMalus;
    private JLabel l_heightMalus;
    private JLabel l_speed;
    
    private JList textRoute;
    private JScrollPane textRouteScrollPane;

    private JTextField startPoint;
    private JTextField endPoint;

    private JSlider highwayMalus;
    private JSlider reliefmalus;
    private JSlider scrolling;

    private JPanel statusBar;
    private JPanel mapContents;
    private JPanel map;
    private JPanel drawMap;
    private JPanel mapButtonPanel;
    private JPanel tab1;
    private JPanel tab2;
    private JPanel tab3;

    private JSpinner s_speed;
    
    private JMenuItem startItem;
    private JMenuItem endItem;
    private JMenuItem stopoverItem;
    private JMenuItem favoriteItem;

    private ArrayList<JTextField> alladdedNavPoints;
    private ArrayList<JButton> alladdedButtons;

    private Component selectedComponent;

    private int popUpXPos;
    private int popUpYPos;
    private int key = 0;
    private int oldMousePosX;
    private int oldMousePosY;
    private int currentZoomLevel = 0;
    private String choosenMap;
    private ContextSW context;
    private Coordinates center;
    private Coordinates topLeft = new Coordinates();
    private Coordinates bottomRight = new Coordinates();
    private File importedMapFile;
    private File loadedRouteFile;
    private File savedRouteFile;
    private File exportedRouteFile;
    private File importedHeightMap;
    private DefaultListModel textRouteList;

    public static final int FREEMAPSPACE = 0;
    public static final int POI = 1;
    public static final int FAVORITE = 2;
    
    public GUI(Coordinates center) {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeAllArrayLists();
        this.center = center;
        this.pack();
        this.setVisible(true);

    }
    
    public GUI() {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeAllArrayLists();
        this.center = new Coordinates(0.0f, 0.0f);
        this.pack();
        this.setVisible(true);
    }
    
    private void initializeAllArrayLists() {
        targetSelectedList = new ArrayList<RALListener>();
        viewChangedList = new ArrayList<RALListener>();
        importOsmFileList = new ArrayList<RALListener>();
        whatWasClickedList = new ArrayList<RALListener>();
        addFavList = new ArrayList<RALListener>();
        loadRouteList = new ArrayList<RALListener>();
        saveRouteList = new ArrayList<RALListener>();
        exportRoutList = new ArrayList<RALListener>();
        speedList = new ArrayList<RALListener>();
        closeList = new ArrayList<RALListener>();
        heightMalusList = new ArrayList<RALListener>();
        highwayMalusList = new ArrayList<RALListener>();
        importHeightMapList = new ArrayList<RALListener>();
        printRouteList = new ArrayList<RALListener>();
        navPointsList = new ArrayList<Coordinates>();
    }
    
    //TODO right place??
    public void setView(Coordinates center) {
        this.center = center;
        recalculateView();
        repaint();
    }
    
    private void recalculateView() {
        topLeft.setLatitude(center.getLatitude() - drawMap.getVisibleRect().height * Projection.getZoomFactor(currentZoomLevel) / 2.f);
        topLeft.setLongitude(center.getLongitude() - drawMap.getVisibleRect().width * Projection.getZoomFactor(currentZoomLevel) / 2.f);
        bottomRight.setLatitude(drawMap.getVisibleRect().height * Projection.getZoomFactor(currentZoomLevel) / 2.f + center.getLatitude());
        bottomRight.setLongitude(drawMap.getVisibleRect().width * Projection.getZoomFactor(currentZoomLevel) / 2.f + center.getLongitude());
        context.recalculateSize();
    }
    

    // private BufferedImage mapImage = testImage();

    public void addContents() {

        this.mapButtonPanel = new JPanel();
        mapButtonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));

        mapConstructor();
        
        startItem = new JMenuItem("als Start");
        startItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates = calculateClickPos(popUpXPos-drawMap.getX(), popUpYPos-drawMap.getY());
                if(navPointsList.size() == 0 ) {
                    navPointsList.add(navPointCoordinates);
                } else {
                    navPointsList.set(0, navPointCoordinates);
                }
                
                NavNodeSelectedEvent navEvent = new NavNodeSelectedEvent(this, navPointCoordinates, navPointsList.indexOf(navPointCoordinates), context);
                for(RALListener lis: targetSelectedList){
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });
        
        endItem = new JMenuItem("als Ziel");
        endItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates = calculateClickPos(popUpXPos-drawMap.getX(), popUpYPos-drawMap.getY());
                if(navPointsList.size() == 0 ) {
                    navPointsList.add(new Coordinates());
                    navPointsList.add(navPointCoordinates);
                } else if(navPointsList.size() == 1) {
                    navPointsList.add(navPointCoordinates);
                } else {
                    navPointsList.set(navPointsList.size() - 1, navPointCoordinates);
                }
                NavNodeSelectedEvent navEvent = new NavNodeSelectedEvent(this, navPointCoordinates, navPointsList.indexOf(navPointCoordinates), context);
                for(RALListener lis: targetSelectedList){
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });
        
        stopoverItem = new JMenuItem("als Zwischenhalt");
        stopoverItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates navPointCoordinates = calculateClickPos(popUpXPos-drawMap.getX(), popUpYPos-drawMap.getY());
                if(navPointsList.size() == 0) {
                    navPointsList.add(new Coordinates());
                    navPointsList.add(navPointCoordinates);
                    navPointsList.add(new Coordinates());
                } else if(navPointsList.size() == 1) {
                    navPointsList.add(navPointCoordinates);
                    navPointsList.add(new Coordinates());
                } else {
                    navPointsList.add(navPointsList.size() - 1, navPointCoordinates);
                }
                NavNodeSelectedEvent navEvent = new NavNodeSelectedEvent(this, navPointCoordinates, navPointsList.indexOf(navPointCoordinates), context);
                for(RALListener lis: targetSelectedList){
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });
        
        favoriteItem = new JMenuItem("als Favorit");
        favoriteItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinates favoriteCoordinates = calculateClickPos(popUpXPos-drawMap.getX(), popUpYPos-drawMap.getY());
                AddFavEvent addFavEvent = new AddFavEvent(this, favoriteCoordinates, "", "");
                for(RALListener lis: addFavList) {
                    lis.handleRALEvent(addFavEvent);
                }
                repaint();
            }
        });
        
        this.navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(startItem);
        navNodeMenu.add(endItem);
        navNodeMenu.add(stopoverItem);
        navNodeMenu.add(favoriteItem);

        statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        l_activeRoute = new JLabel();
        l_activeRoute.setText("Route:");
        l_position = new JLabel();
        
        statusBar.add(l_activeRoute);
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(l_position);
        statusBar.add(Box.createHorizontalGlue());

        mapContents = new JPanel();
        mapContents.setLayout(new BorderLayout());

        tabbpane = new JTabbedPane();
        tabbpane.setPreferredSize(new Dimension(this.getWidth() * 2 / 5, this.getHeight()));
        tabbpane.setBackground(Color.LIGHT_GRAY);

        Container contents = this.getContentPane();
        contents.setLayout(new BorderLayout());

        contents.add(tabbpane, BorderLayout.WEST);
        contents.add(statusBar, BorderLayout.SOUTH);
        contents.add(mapContents, BorderLayout.CENTER);
        mapContents.add(mapButtonPanel, BorderLayout.NORTH);
        mapContents.add(map, BorderLayout.CENTER);

        l_routeText = new JLabel();
        l_routeText.setText("Route:");

        load = new JButton();
        load.setText("Laden");
        load.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.loadRouteFileChooser();
            }
        });

        save = new JButton();
        save.setText("Speichern");
        save.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.saveRouteFileChooser();
            }
        });

        kmlExport = new JButton();
        kmlExport.setText("KML-Export");
        kmlExport.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.exportRouteKMLFileChooser();
            }
        });

        print = new JButton();
        print.setText("Ausdrucken");
        print.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //TODO
            }
        });

        graphics = new JButton();
        graphics.setText("2D/3D");

        Hashtable<Integer, JLabel> allScrollingTicks = new Hashtable<Integer, JLabel>();

        allScrollingTicks.put(0, new JLabel("0"));
        allScrollingTicks.put(1, new JLabel("1"));
        allScrollingTicks.put(2, new JLabel("2"));
        allScrollingTicks.put(3, new JLabel("3"));
        allScrollingTicks.put(4, new JLabel("4"));
        allScrollingTicks.put(5, new JLabel("5"));
        allScrollingTicks.put(6, new JLabel("6"));
        allScrollingTicks.put(7, new JLabel("7"));
        allScrollingTicks.put(8, new JLabel("8"));
        allScrollingTicks.put(9, new JLabel("9"));

        scrolling = new JSlider();
        scrolling.setMaximum(9);
        scrolling.setMinimum(0);
        scrolling.setValue(0);
        scrolling.setMajorTickSpacing(1);
        scrolling.setMinorTickSpacing(1);
        scrolling.setLabelTable(allScrollingTicks);
        scrolling.setPaintTicks(true);
        scrolling.setPaintLabels(true);
        scrolling.setSnapToTicks(true);
        scrolling.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent ce) {
                currentZoomLevel = scrolling.getValue();
                
                recalculateView();
                
                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, currentZoomLevel);
                for(RALListener lis: viewChangedList){
                    lis.handleRALEvent(viewEvent);
                }
                repaint();
            }
        });

        mapButtonPanel.add(l_routeText);
        mapButtonPanel.add(load);
        mapButtonPanel.add(save);
        mapButtonPanel.add(kmlExport);
        mapButtonPanel.add(print);
        mapButtonPanel.add(graphics);
        mapButtonPanel.add(scrolling);
        
        createTab1();
        createTab2();
        createTab3();
        
        this.pack();
        this.validate();
        
        // The context needs to be queried / created in the very end.
        context = new ContextSW(topLeft, bottomRight, drawMap.getGraphics());
        recalculateView();
        
        this.addComponentListener(new ComponentListener() {
            
            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void componentResized(ComponentEvent e) {
                recalculateView();
                context.setSurface(drawMap.getGraphics());

                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, currentZoomLevel);
                for(RALListener lis: viewChangedList){
                    lis.handleRALEvent(viewEvent);
                }
                
//                System.out.println("resized");
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        this.addWindowListener(new WindowListener() {
            
            @Override
            public void windowOpened(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowIconified(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeiconified(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeactivated(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowClosing(WindowEvent arg0) {
                // TODO
            }
            
            @Override
            public void windowClosed(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowActivated(WindowEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, currentZoomLevel);
        for(RALListener lis: viewChangedList){
            lis.handleRALEvent(viewEvent);
        }
    }
    
    private void mapConstructor() {
        this.map = new JPanel();
        map.setLayout(new BorderLayout());
        map.setPreferredSize(new Dimension(this.getSize()));
        map.setBackground(Color.WHITE);
        map.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        map.setVisible(true);
        drawMap =  new JPanel();
        drawMap.setPreferredSize(new Dimension(map.getSize()));
        drawMap.setBackground(Color.green);
        drawMap.setVisible(true);
        map.add(drawMap, BorderLayout.CENTER);
        drawMap.addMouseListener(new MouseListener() {

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
            public void mouseExited(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                checkPopup(me);
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
                    for(RALListener lis: whatWasClickedList) {
                        lis.handleRALEvent(posEv);
                    }
                }
            }
        });
        
        drawMap.addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                Coordinates mousePosCoordinates = calculateClickPos(e.getX() - drawMap.getX(), e.getY() - drawMap.getY());
                l_position.setText(mousePosCoordinates.toString());
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int newMousePosX = e.getX();
                int newMousePosY = e.getY();
                int mousePosXDist = newMousePosX - oldMousePosX;
                int mousePosYDist = newMousePosY - oldMousePosY;
                
                float newCenterLongitude = center.getLongitude() - mousePosXDist * Projection.getZoomFactor(currentZoomLevel);
                float newCenterLatitude = center.getLatitude() - mousePosYDist * Projection.getZoomFactor(currentZoomLevel);
                
                oldMousePosX = newMousePosX;
                oldMousePosY = newMousePosY;
                
                center.setLongitude(newCenterLongitude);
                center.setLatitude(newCenterLatitude);
                
                recalculateView();
                
                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, currentZoomLevel);
                for(RALListener lis: viewChangedList){
                    lis.handleRALEvent(viewEvent);
                }
            }
        });

        drawMap.addMouseWheelListener(new MouseWheelListener() {

            int up = 1;
            int down = -1;
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int direction;
                int count = e.getWheelRotation();
                if (count < 0) {
                    direction = up;
                } else {
                    direction = down;
                }
                
                Coordinates clickPos = calculateClickPos(e.getX() - drawMap.getX(), e.getY() - drawMap.getY());
                int yDiff = e.getY() - drawMap.getY() - drawMap.getVisibleRect().height / 2;
                int xDiff = e.getX() - drawMap.getX() - drawMap.getVisibleRect().width / 2;

                currentZoomLevel -= direction;
                currentZoomLevel = currentZoomLevel < 0 ? 0 : currentZoomLevel;

                float newCenterLat = clickPos.getLatitude() - yDiff*Projection.getZoomFactor(currentZoomLevel);
                float newCenterLon = clickPos.getLongitude() - xDiff*Projection.getZoomFactor(currentZoomLevel);
                center.setLatitude(newCenterLat);
                center.setLongitude(newCenterLon);

                recalculateView();
                
                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, currentZoomLevel);
                for(RALListener lis: viewChangedList){
                    lis.handleRALEvent(viewEvent);
                }
                repaint();
            }
        });
    }
    
    private void createTab1() {
        tab1 = new JPanel();
        tabbpane.addTab("Planen", null, tab1, "1");
        // tabbpane.setMnemonicAt(1, KeyEvent.VK_2);
        tab1.setLayout(new GridLayout(0,2));
        startPoint = new JTextField();
        startPoint.setPreferredSize(new Dimension(this.getWidth() * 2 / 5 - 30, 20));
        startPoint.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedStart = startPoint.getText();
            }
        });
        
        endPoint = new JTextField();
        endPoint.setPreferredSize(new Dimension(this.getWidth() * 2 / 5 - 30, 20));
        endPoint.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String selectedEnd = endPoint.getText();
            }
        });
        addTextPoints = new JButton("+");
        
        optimizeRoute = new JButton("Reihenfolge optimieren");
        optimizeRoute.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                /*
                 * Event hier einbauen
                 */
            }
        });

        s_speed = new JSpinner(new SpinnerNumberModel(15, 0, null, 1));
        s_speed.setSize(new Dimension(30, 20));
        s_speed.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent ce) {
                IntEvent intEvent = new IntEvent(GUI.this, Integer.parseInt(s_speed.getValue().toString()));
                for(RALListener lis: speedList) {
                    lis.handleRALEvent(intEvent);
                }
            }
        });
        
        l_speed = new JLabel("hm/h");

        tab1.add(startPoint);
        tab1.add(endPoint);
        tab1.add(addTextPoints);
        tab1.add(optimizeRoute);
        tab1.add(s_speed);
        tab1.add(l_speed);

        alladdedNavPoints = new ArrayList<JTextField>();
        alladdedButtons = new ArrayList<JButton>();
        
        addTextPoints.addActionListener(new ActionListener() {
          
          @Override 
          public void actionPerformed(ActionEvent arg0) {
              JTextField navPointField = new JTextField();
              JButton navPointButton = new JButton();
              alladdedNavPoints.add(navPointField); 
              alladdedButtons.add(navPointButton);
              navPointField.setPreferredSize(new Dimension(startPoint.getWidth()-20,20));
              tab1.add(navPointField); 
              tab1.add(navPointButton);
              navPointField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String selectedPoint = alladdedNavPoints.get(key).getText();
                }
              });
              navPointButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    /*
                    tab1.remove(navPointField);
                    alladdedNavPoints.remove();
                    */
                }
              });
              tab1.validate();
              key++;
           }
        });
    }
    
    private void createTab2() {
        tab2 = new JPanel();
        tabbpane.addTab("Beschreibung", null, tab2, "2");
        // tabbpane.setMnemonicAt(2, KeyEvent.VK_2);
        textRouteList = new DefaultListModel();
        String[] data = {"one", "two", "three", "four", "five", "six", "seve", "eight"};
        textRoute = new JList(textRouteList);
        for(int i = 0; i < data.length; i++) {
            textRouteList.add(i, data[i]);
        }
        textRoute.setPreferredSize(new Dimension(tab2.getSize()));
        textRouteScrollPane = new JScrollPane(textRoute);
        tab2.add(textRoute);
        /*
        textRouteList.add(textRoute.getModel().getSize(), "ende");
        textRouteList.add(0, "anfangawdwadfadwadwadwad");
        textRouteList.set(3, "replaced");
        textRouteList.remove(2);
        */
    }
    
    private void createTab3() {

        tab3 = new JPanel();
        tabbpane.addTab("Karten", null, tab3, "3");
        // tabbpane.setMnemonicAt(3, KeyEvent.VK_2);
        
        tab3.setLayout(new FlowLayout());

        l_highwayMalus = new JLabel("Fernstraßenmalus");
        highwayMalus = new JSlider();
        highwayMalus.setMaximum(5);
        highwayMalus.setMinimum(1);
        highwayMalus.setValue(1);
        highwayMalus.setMajorTickSpacing(1);
        highwayMalus.setMinorTickSpacing(1);
        highwayMalus.setPaintTicks(true);
        highwayMalus.setSnapToTicks(true);
        highwayMalus.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                IntEvent intEvent = new IntEvent(GUI.this, highwayMalus.getValue());
                for(RALListener lis: highwayMalusList) {
                    lis.handleRALEvent(intEvent);
                }
            }
        });
        
        
        l_heightMalus = new JLabel("Reliefmalus");
        reliefmalus = new JSlider();
        reliefmalus.setMaximum(5);
        reliefmalus.setMinimum(1);
        reliefmalus.setValue(1);
        reliefmalus.setMajorTickSpacing(1);
        reliefmalus.setMinorTickSpacing(1);
        reliefmalus.setPaintTicks(true);
        reliefmalus.setSnapToTicks(true);
        reliefmalus.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent arg0) {
                IntEvent intEvent = new IntEvent(GUI.this, reliefmalus.getValue());
                for(RALListener lis: heightMalusList) {
                    lis.handleRALEvent(intEvent);
                }
            }
        });
        
        importOSM = new JButton("Importiere OSM-Karte");
        importOSM.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.importMapFileChooser();
            }
        });
        
        
        chooseImportedMap = new JComboBox();
        chooseImportedMap.setEditable(true);
        chooseImportedMap.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                choosenMap = chooseImportedMap.getSelectedItem().toString();
            }
        });
        
        deleteMapButton = new JButton("Entfernen");
        deleteMapButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String deletedMap = chooseImportedMap.getSelectedItem().toString();
            }
        });
        
        activateMapButton = new JButton("Aktivieren");
        activateMapButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String activateMap = chooseImportedMap.getSelectedItem().toString();
            }
        });
        
        heightMapManagement = new JButton();
        heightMapManagement.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.importHeightMapFileChooser();
            }
        });
        
        tab3.add(l_highwayMalus);
        tab3.add(highwayMalus);
        tab3.add(l_heightMalus);
        tab3.add(reliefmalus);
        tab3.add(importOSM);
        tab3.add(deleteMapButton);
        tab3.add(activateMapButton);
        tab3.add(chooseImportedMap);
    }
    
    private void importMapFileChooser() {
        importFC = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".osm", "osm");
        importFC.setFileFilter(filter);
        int returnValue = importFC.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            importedMapFile = importFC.getSelectedFile();
            PathEvent pathEvent = new PathEvent(GUI.this, importFC.getSelectedFile().getPath());
            for(RALListener lis: importOsmFileList){
                lis.handleRALEvent(pathEvent);
            }
        }
    }
    
    private void importHeightMapFileChooser() {
        importHeightMap = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".hgt", "hgt");
        importHeightMap.setFileFilter(filter);
        int returnValue = importHeightMap.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            importedHeightMap = importHeightMap.getSelectedFile();
            PathEvent pathEvent = new PathEvent(GUI.this, importHeightMap.getSelectedFile().getPath());
            for(RALListener lis: importHeightMapList) {
                lis.handleRALEvent(pathEvent);
            }
        }
    }
    
    private void loadRouteFileChooser() {
        loadRoute = new JFileChooser();
        int returnValue = loadRoute.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            loadedRouteFile = loadRoute.getSelectedFile();
            PathEvent pathEvent =  new PathEvent(GUI.this, loadRoute.getSelectedFile().getPath());
            for(RALListener lis: loadRouteList) {
                lis.handleRALEvent(pathEvent);
            }
        }
    }
    
    private void saveRouteFileChooser() {
        saveRoute = new JFileChooser();
        int returnValue = saveRoute.showSaveDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            savedRouteFile = saveRoute.getSelectedFile();
            PathEvent pathEvent = new PathEvent(GUI.this, saveRoute.getSelectedFile().getPath());
            for(RALListener lis: saveRouteList) {
                lis.handleRALEvent(pathEvent);
            }
        }
    }
    
    private void exportRouteKMLFileChooser() {
        exportRoute = new JFileChooser();
        int returnValue = exportRoute.showDialog(this, "Exportieren");
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            exportedRouteFile = exportRoute.getSelectedFile();
            PathEvent pathEvent = new PathEvent(GUI.this, exportRoute.getSelectedFile().getPath());
            for(RALListener lis: exportRoutList) {
                lis.handleRALEvent(pathEvent);
            }
        }
    }
    
    public void updateGUI() {
        repaint();
    }
    
    public void updateMapChooser(ArrayList<String> maps) {
        chooseImportedMap.removeAllItems();
        
        for(String map : maps) {
            chooseImportedMap.addItem(map);
        }
    }

    private Coordinates calculateClickPos(int x, int y) {
        Coordinates clickPos = new Coordinates();
        clickPos.setLatitude(center.getLatitude() + (y - drawMap.getVisibleRect().height / 2)*Projection.getZoomFactor(currentZoomLevel));
        clickPos.setLongitude(center.getLongitude() + (x - drawMap.getVisibleRect().width / 2)*Projection.getZoomFactor(currentZoomLevel));
        return clickPos;
    }
    
    public void leftClickPOIFav() {
        
    }
    
    public void setSpeed(int speed) {
        s_speed.setValue(speed);
    }
    
    public void setNavPointsOrdered(ArrayList<Coordinates> orderedNavPointsList) {
        navPointsList = orderedNavPointsList;
    }
    
    public void deleteNavNodesFromList(Coordinates coordinates) {
        navPointsList.remove(coordinates);
    }
    
    public void addViewChangedListener(RALListener viewChangedListener) {
        viewChangedList.add(viewChangedListener);
    }
    public void addTargetSelectedListener(RALListener targetSelectedListener) {
        targetSelectedList.add(targetSelectedListener);
    }
    public void addImportOsmFileListener(RALListener importOsmFileListener) {
        importOsmFileList.add(importOsmFileListener);
    }
    
    public void addLoadRouteListener(RALListener loadRouteListener) {
        loadRouteList.add(loadRouteListener);
    }
    
    public void addSaveRouteListener(RALListener saveRouteListener) {
        saveRouteList.add(saveRouteListener);
    }
    
    public void addExportRouteListener(RALListener exportRouteListener) {
        exportRoutList.add(exportRouteListener);
    }
    
    public void addOptimizeRouteListener(RALListener optimizeRouteListener) {
        optimizeRouteList.add(optimizeRouteListener);
    }
    
    public void addWhatWasClickedListener(RALListener whatWasClickedListener) {
        whatWasClickedList.add(whatWasClickedListener);
    }
    
    public void addFavoriteListener(RALListener addFavListener) {
        addFavList.add(addFavListener);
    }
    
    public void addSetSpeedListener(RALListener setSpeedListener) {
        speedList.add(setSpeedListener);
    }
    
    public void addCloseListener(RALListener closeListener) {
        closeList.add(closeListener);
    }
    
    public void addHighwayMalusListener(RALListener highwayMalusListener) {
        highwayMalusList.add(highwayMalusListener);
    }
    
    public void addHeightMalusListener(RALListener heightMalusListener) {
        heightMalusList.add(heightMalusListener);
    }
    
    public void addImportHeightMapListener(RALListener importHeightMapListener) {
        importHeightMapList.add(importHeightMapListener);
    }
    
    public void addPrintRouteListener(RALListener printRouteListener) {
        printRouteList.add(printRouteListener);
        //TODO
    }
}