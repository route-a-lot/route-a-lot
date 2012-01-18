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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import kit.route.a.lot.common.Context;
import kit.route.a.lot.controller.RALListener;


public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<RALListener> targetSelectedList;
    private ArrayList<RALListener> viewChangedList;
    private ArrayList<RALListener> importOsmFileList;
    private ArrayList<Coordinates> navPointsList;
    private ArrayList<RALListener> optimizeRouteList;
    
    private JPopupMenu navNodeMenu;
    private JTabbedPane tabbpane;
    
    private JFileChooser importFC;
    private JFileChooser loadRoute;
    private JFileChooser saveRoute;
    private JFileChooser exportRoute;

    private JButton importOSM;
    private JButton load;
    private JButton save;
    private JButton kmlExport;
    private JButton print;
    private JButton graphics;
    private JButton addTextPoints;
    private JButton optimizeRoute;
    
    private JComboBox chooseImportedMap;

    private JLabel l_activeRoute;
    private JLabel l_routeText;
    private JLabel l_highwayMalus;
    private JLabel l_heightMalus;
    private JLabel l_speed;

    private JTextField startPoint;
    private JTextField endPoint;

    private JSlider highwayMalus;
    private JSlider reliefmalus;
    private JSlider scrolling;

    private JPanel mapContents;
    private JPanel map;
    private JPanel drawMap;
    private JPanel mapButtonPanel;
    private JPanel tab1;
    private JPanel tab2;
    private JPanel tab3;

    private JSpinner s_speed;

    private Hashtable<Integer, JTextField> alladdedNavPoints;
    private Hashtable<Integer, JButton> alladdedButtons;

    private Component selectedComponent;

    private int xpos;
    private int ypos;
    private int key = 0;
    private int oldMousePosX;
    private int newMousePosX;
    private int oldMousePosY;
    private int newMousePosY;
    private int currentZoomLevel = 0;
    private int mousePosXDist;
    private int mousePosYDist;
    private float coordinatesWidth;
    private float coordinatesHeight;
    private float coordinatesPixelWidthDifference;
    private float coordinatesPixelHeightDifference;
    private boolean mouseDragged = false;
    private String choosenMap;
    private Context context;
    private Coordinates middle;
    private Coordinates topLeft = new Coordinates();
    private Coordinates bottomRight = new Coordinates();
    private File importedMapFile;
    private File loadedRouteFile;
    private File savedRouteFile;
    private File exportedRouteFile;

    public GUI(Coordinates middle) {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        targetSelectedList = new ArrayList<RALListener>();
        viewChangedList = new ArrayList<RALListener>();
        importOsmFileList = new ArrayList<RALListener>();
        navPointsList = new ArrayList<Coordinates>();
        this.middle = middle;
        this.pack();
        this.setVisible(true);

    }
    
    public GUI() {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        targetSelectedList = new ArrayList<RALListener>();
        viewChangedList = new ArrayList<RALListener>();
        importOsmFileList = new ArrayList<RALListener>();
        navPointsList = new ArrayList<Coordinates>();
        this.middle = new Coordinates(0.0f, 0.0f);
        this.pack();
        this.setVisible(true);
    }
    
    //TODO right place??
    public void setview(Coordinates coor) {
        this.middle = coor;
        repaint();
    }
    

    // private BufferedImage mapImage = testImage();

    public void addContents() {

        this.mapButtonPanel = new JPanel();
        mapButtonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));

        mapConstructor();
        

        this.navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(makeMenuItem("Start"));
        navNodeMenu.add(makeMenuItem("End"));

        this.l_activeRoute = new JLabel();
        l_activeRoute.setText("Route:");

        mapContents = new JPanel();
        mapContents.setLayout(new BorderLayout());

        tabbpane = new JTabbedPane();
        tabbpane.setPreferredSize(new Dimension(this.getWidth() * 2 / 5, this.getHeight()));
        tabbpane.setBackground(Color.LIGHT_GRAY);

        Container contents = this.getContentPane();
        contents.setLayout(new BorderLayout());

        contents.add(tabbpane, BorderLayout.WEST);
        contents.add(l_activeRoute, BorderLayout.SOUTH);
        contents.add(mapContents, BorderLayout.CENTER);
        mapContents.add(mapButtonPanel, BorderLayout.NORTH);
        mapContents.add(map, BorderLayout.CENTER);
        topLeft.setLongitude(middle.getLongitude() - 0.01f);
        topLeft.setLatitude(middle.getLatitude() + 0.01f);
        bottomRight.setLongitude(middle.getLongitude() + 0.01f);
        bottomRight.setLatitude(middle.getLatitude() - 0.01f);

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

        graphics = new JButton();
        graphics.setText("2D/3D");

        Hashtable<Integer, JLabel> allScrollingTicks = new Hashtable<Integer, JLabel>();

        allScrollingTicks.put(1, new JLabel("1"));
        allScrollingTicks.put(2, new JLabel("2"));
        allScrollingTicks.put(3, new JLabel("3"));
        allScrollingTicks.put(4, new JLabel("4"));
        allScrollingTicks.put(5, new JLabel("5"));
        allScrollingTicks.put(6, new JLabel("6"));
        allScrollingTicks.put(7, new JLabel("7"));
        allScrollingTicks.put(8, new JLabel("8"));
        allScrollingTicks.put(9, new JLabel("9"));
        allScrollingTicks.put(10, new JLabel("10"));

        scrolling = new JSlider();
        scrolling.setMaximum(10);
        scrolling.setMinimum(1);
        scrolling.setValue(1);
        scrolling.setMajorTickSpacing(1);
        scrolling.setMinorTickSpacing(1);
        scrolling.setLabelTable(allScrollingTicks);
        scrolling.setPaintTicks(true);
        scrolling.setPaintLabels(true);
        scrolling.setSnapToTicks(true);

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
        topLeft = new Coordinates(0,0);
        bottomRight = new Coordinates(drawMap.getVisibleRect().width, drawMap.getVisibleRect().height);
        context = new ContextSW(topLeft, bottomRight, drawMap.getGraphics());
        calculateCoordinatesDistances();
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("(" + xpos + "," + ypos + ")\n");
                System.out.println("geht");
                calculateCoordinatesDistances();
                System.out.println("geht");
                Coordinates newCoordinates = new Coordinates();
                newCoordinates.setLatitude(topLeft.getLatitude() + (ypos - drawMap.getY())*coordinatesPixelHeightDifference);
                newCoordinates.setLongitude(topLeft.getLongitude() + (xpos - drawMap.getX())*coordinatesPixelWidthDifference);
                System.out.println("geht");
                navPointsList.add(newCoordinates);

                System.out.println("point: " + newCoordinates.getLongitude() + "," + newCoordinates.getLatitude());
                System.out.println("topleft: " + topLeft.getLongitude() + "," + topLeft.getLatitude());
                NavNodeSelectedEvent navEvent = new NavNodeSelectedEvent(this, newCoordinates, navPointsList.indexOf(newCoordinates), context);
                for(RALListener lis: targetSelectedList){
                    lis.handleRALEvent(navEvent);
                }
                repaint();
            }
        });
        return item;
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
    
    public void addOptimizeRouteListener(RALListener optimizeRouteListener) {
        optimizeRouteList.add(optimizeRouteListener);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, 0);
        for(RALListener lis: viewChangedList){
            lis.handleRALEvent(viewEvent);
        }
    }
    
    private void calculateCoordinatesDistances() {
        if(topLeft.getLatitude() > bottomRight.getLatitude()) {
            coordinatesWidth = topLeft.getLatitude() - bottomRight.getLatitude();
        } else {
            coordinatesWidth = bottomRight.getLatitude() - topLeft.getLatitude();
        }
        if(topLeft.getLongitude() > bottomRight.getLongitude()) {
            coordinatesHeight = topLeft.getLongitude() - bottomRight.getLongitude();
        } else {
            coordinatesHeight = bottomRight.getLongitude() - topLeft.getLongitude();
        }
        coordinatesPixelWidthDifference = coordinatesWidth / drawMap.getWidth();
        coordinatesPixelHeightDifference = coordinatesHeight / drawMap.getHeight();
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
        drawMap.setVisible(true);
        map.add(drawMap, BorderLayout.CENTER);
        drawMap.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent me) {
                checkPopup(me);
                newMousePosX = me.getX();
                newMousePosY = me.getY();
                mouseDragged = false;

            }

            @Override
            public void mousePressed(MouseEvent me) {
                checkPopup(me);
                oldMousePosX = me.getX();
                oldMousePosY = me.getY();
                mouseDragged = true;
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

                    xpos = e.getX();
                    ypos = e.getY();
                }
            }
        });
        
        drawMap.addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                newMousePosX = e.getX();
                newMousePosY = e.getY();
                mousePosXDist = newMousePosX - oldMousePosX;
                mousePosYDist = newMousePosY - oldMousePosY;
                
                float newTopLeftLongitude = topLeft.getLongitude() - coordinatesPixelWidthDifference * mousePosXDist;
                float newTopLeftLatitude = topLeft.getLatitude() - coordinatesPixelHeightDifference * mousePosYDist;
                float newBottomRightLongitude = bottomRight.getLongitude() - coordinatesPixelWidthDifference * mousePosXDist;
                float newBottomRightLatitude = bottomRight.getLatitude() - coordinatesPixelHeightDifference * mousePosYDist;
                
                oldMousePosX = newMousePosX;
                oldMousePosY = newMousePosY;
                
                topLeft.setLongitude(newTopLeftLongitude);
                topLeft.setLatitude(newTopLeftLatitude);
                bottomRight.setLongitude(newBottomRightLongitude);
                bottomRight.setLatitude(newBottomRightLatitude);
                
                
                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, 0);
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
                if(direction == up && currentZoomLevel != -4) {
                    currentZoomLevel--;
                    topLeft.setLongitude(topLeft.getLongitude() + coordinatesWidth/4);
                    topLeft.setLatitude(topLeft.getLatitude() + coordinatesHeight/4);
                    bottomRight.setLongitude(bottomRight.getLongitude() - coordinatesWidth/4);
                    bottomRight.setLatitude(bottomRight.getLatitude() - coordinatesHeight/4);
                    
                } else if(direction == down && currentZoomLevel != 4) {
                    currentZoomLevel++;
                    topLeft.setLongitude(topLeft.getLongitude() - coordinatesWidth/2);
                    topLeft.setLatitude(topLeft.getLatitude() - coordinatesHeight/2);
                    bottomRight.setLongitude(bottomRight.getLongitude() + coordinatesWidth/2);
                    bottomRight.setLatitude(bottomRight.getLatitude() + coordinatesHeight/2);
                }
                
                ViewChangedEvent viewEvent = new ViewChangedEvent(this, context, direction);
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
                int newSpeed = 15;
                System.out.println(newSpeed);
            }
        });

        l_speed = new JLabel("hm/h");

        tab1.add(startPoint);
        tab1.add(endPoint);
        tab1.add(addTextPoints);
        tab1.add(optimizeRoute);
        tab1.add(s_speed);
        tab1.add(l_speed);

        alladdedNavPoints = new Hashtable<Integer, JTextField>();
        alladdedButtons = new Hashtable<Integer, JButton>();
        
        addTextPoints.addActionListener(new ActionListener() {
          
          @Override 
          public void actionPerformed(ActionEvent arg0) { 
              key++; 
              alladdedNavPoints.put(key, new JTextField()); 
              alladdedButtons.put(key, new JButton("x"));
              alladdedNavPoints.get(key).setPreferredSize(new Dimension(startPoint.getWidth()-20,20));
              tab1.add(alladdedNavPoints.get(key)); 
              tab1.add(alladdedButtons.get(key));
              alladdedNavPoints.get(key).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String selectedPoint = alladdedNavPoints.get(key).getText();
                }
              });
              alladdedButtons.get(key).addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    alladdedButtons.get(key).setVisible(false);
                    alladdedButtons.get(key).setEnabled(false);
                    alladdedNavPoints.get(key).setVisible(false);
                    alladdedNavPoints.get(key).setEnabled(false);
                }
            });
              tab1.validate();
           }
        });
    }
    
    private void createTab2() {
        tab2 = new JPanel();
        tabbpane.addTab("Beschreibung", null, tab2, "2");
        // tabbpane.setMnemonicAt(2, KeyEvent.VK_2);
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
        
        
        l_heightMalus = new JLabel("Reliefmalus");
        reliefmalus = new JSlider();
        reliefmalus.setMaximum(5);
        reliefmalus.setMinimum(1);
        reliefmalus.setValue(1);
        reliefmalus.setMajorTickSpacing(1);
        reliefmalus.setMinorTickSpacing(1);
        reliefmalus.setPaintTicks(true);
        reliefmalus.setSnapToTicks(true);
        
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
        tab3.add(l_highwayMalus);
        tab3.add(highwayMalus);
        tab3.add(l_heightMalus);
        tab3.add(reliefmalus);
        tab3.add(importOSM);
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
    
    private void loadRouteFileChooser() {
        loadRoute = new JFileChooser();
        int returnValue = loadRoute.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            loadedRouteFile = loadRoute.getSelectedFile();
        }
    }
    
    private void saveRouteFileChooser() {
        saveRoute = new JFileChooser();
        int returnValue = saveRoute.showSaveDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            savedRouteFile = saveRoute.getSelectedFile();
        }
    }
    
    private void exportRouteKMLFileChooser() {
        exportRoute = new JFileChooser();
        int returnValue = exportRoute.showDialog(this, "Exportieren");
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            exportedRouteFile = exportRoute.getSelectedFile();
        }
    }
    
    public void updateGUI() {
        repaint();
    }
    
    public void updateMapChooser(String[] maps) {
        chooseImportedMap.removeAllItems();
        
        for(int i = 0; i < maps.length; i++) {
            chooseImportedMap.addItem(maps[i]);
        }
    }
}