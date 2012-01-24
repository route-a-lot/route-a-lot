package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.listener.RALListener;
import kit.route.a.lot.gui.event.NavNodeSelectedEvent;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.PathEvent;


public class GUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    public static final int FREEMAPSPACE = 0;
    public static final int POI = 1;
    public static final int FAVORITE = 2;
    public static final int NAVNODE = 3;
    
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
    private JButton deleteRoute;
    
    private JComboBox chooseImportedMap;

    private JLabel l_activeRoute;
    protected JLabel l_position;
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
    private JPanel mapButtonPanel;
    private JPanel tab1;
    private JPanel tab2;
    private JPanel tab3;

    private JSpinner s_speed;

    private ArrayList<JTextField> alladdedNavPoints;
    private ArrayList<JButton> alladdedButtons;
   
    private Map map;
    
    private int key = 0;
    private String choosenMap;
    private File importedMapFile;
    private File loadedRouteFile;
    private File savedRouteFile;
    private File exportedRouteFile;
    private File importedHeightMap;
    private DefaultListModel textRouteList;
    
    private ArrayList<Coordinates> navPointsList;
    private ListenerLists listener;
    
    /**
     * Creates the GUI window, using the given view center coordinates.
     * @param listener 
     * @param view center geo coordinates (possibly mercator projected)
     */
    public GUI(ListenerLists listener) {
        super("Route-A-Lot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        navPointsList = new ArrayList<Coordinates>();
        this.listener = listener;
    }
    
    /**
     * Changes the geo coordinates view position and subsequently
     * updates the context and redraws the map.
     * @param center the new view center
     */ //TODO right place??  
    public void setView(Coordinates center) {
        map.setCenter(center);
        map.calculateView();
        map.repaint();
    }
    
    /**
     * Builds all components and adds them to the GUI.
     */
    public void addContents() {
        mapButtonPanel = new JPanel();
        mapButtonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));

        map = new Map2D(this);
        
        statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        l_activeRoute = new JLabel("Route:");
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

        Container contents = getContentPane();
        contents.setLayout(new BorderLayout());

        contents.add(tabbpane, BorderLayout.WEST);
        contents.add(statusBar, BorderLayout.SOUTH);
        contents.add(mapContents, BorderLayout.CENTER);
        mapContents.add(mapButtonPanel, BorderLayout.NORTH);
        mapContents.add(map, BorderLayout.CENTER);
        
        l_routeText = new JLabel("Route:");

        load = new JButton("Laden");
        load.addActionListener(new ActionListener() {        
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.loadRouteFileChooser();
            }
        });

        save = new JButton("Speichern");
        save.addActionListener(new ActionListener() {          
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.saveRouteFileChooser();
            }
        });

        kmlExport = new JButton("KML-Export");
        kmlExport.addActionListener(new ActionListener() {          
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.exportRouteKMLFileChooser();
            }
        });

        print = new JButton("Ausdrucken");
        print.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //TODO
            }
        });

        graphics = new JButton("2D/3D");

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
                map.setZoomlevel(scrolling.getValue());
                map.calculateView();
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
        
        pack();
        validate();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent arg0) {
                listener.fireEvent(listener.close, new PathEvent("closed"));
            }  
        });
        this.pack();
    }
       
    /**
     * Builds the component tab1 and all its sub components,
     * in the process adding all event listeners.
     */
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
                listener.fireEvent(listener.optimizeRoute, new PathEvent("optimize"));
            }
        });

        s_speed = new JSpinner(new SpinnerNumberModel(15, 0, null, 1));
        s_speed.setSize(new Dimension(30, 20));
        s_speed.addChangeListener(new ChangeListener() {    
            @Override
            public void stateChanged(ChangeEvent ce) {
                NumberEvent intEvent = new NumberEvent(Integer.parseInt(s_speed.getValue().toString()));
                for(RALListener lis: listener.speed) {
                    lis.handleRALEvent(intEvent);
                }
            }
        });
        
        l_speed = new JLabel("hm/h");
        
        deleteRoute = new JButton("Lösche Route");
        deleteRoute.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                for(int i = 0; i < alladdedButtons.size(); i++) {
                    tab1.remove(alladdedButtons.get(i));
                    tab1.remove(alladdedNavPoints.get(i));
                    alladdedButtons.remove(i);
                    alladdedNavPoints.remove(i);
                }
                for(int i = navPointsList.size() - 1; i >= 0; i--) {
                    navPointsList.remove(i);
                    listener.fireEvent(listener.deleteNavPointList, new NumberEvent(i));
                }
                repaint();
            }
        });

        tab1.add(startPoint);
        tab1.add(endPoint);
        tab1.add(addTextPoints);
        tab1.add(optimizeRoute);
        tab1.add(s_speed);
        tab1.add(l_speed);
        tab1.add(deleteRoute);

        alladdedNavPoints = new ArrayList<JTextField>();
        alladdedButtons = new ArrayList<JButton>();
        
        addTextPoints.addActionListener(new ActionListener() {      
          @Override 
          public void actionPerformed(ActionEvent arg0) {
              final JTextField navPointField = new JTextField();
              final JButton navPointButton = new JButton("x");
              alladdedNavPoints.add(navPointField); 
              alladdedButtons.add(navPointButton);
              tab1.add(navPointField); 
              tab1.add(navPointButton);
              navPointField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    for(int i = 0; i < alladdedNavPoints.size(); i++) {
                        if(alladdedNavPoints.get(i) == navPointField) {
                            ListenerLists.fireEvent(listener.addTextuelNavPointList, new PathEvent(alladdedNavPoints.get(i).toString()));
                            repaint();
                        }
                    }
                }
              });
              navPointButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    for(int i = 0; i < alladdedButtons.size(); i++) {
                        if(alladdedButtons.get(i) == navPointButton) {
                            tab1.remove(alladdedNavPoints.get(i));
                            tab1.remove(alladdedButtons.get(i));
                            alladdedButtons.remove(i);
                            alladdedNavPoints.remove(i);
                            navPointsList.remove(i + 1);
                            ListenerLists.fireEvent(listener.deleteNavPointList, new NumberEvent(i + 1));
                            repaint();
                        }
                    }
                }
              });
              tab1.validate();
              key++;
           }
        });
        
        listener.targetSelected.add(new RALListener() {
            @Override
            public void handleRALEvent(EventObject event) {
                int index = ((NavNodeSelectedEvent) event).getIndex();
                Coordinates pos = ((NavNodeSelectedEvent) event).getPosition();
                if( index == 0) {
                    startPoint.setText(pos.toString());
                } else if(index == navPointsList.size() - 1) {
                    endPoint.setText(pos.toString());
                } else {
                    while(alladdedNavPoints.size() < navPointsList.size() - 2) {
                        
                        final JTextField navPointField = new JTextField();
                        final JButton navPointButton = new JButton("x");
                        alladdedNavPoints.add(navPointField); 
                        alladdedButtons.add(navPointButton);
                        tab1.add(navPointField); 
                        tab1.add(navPointButton);
                        navPointField.addActionListener(new ActionListener() {
                          @Override
                          public void actionPerformed(ActionEvent arg0) {
                              for(int i = 0; i < alladdedNavPoints.size(); i++) {
                                  if(alladdedNavPoints.get(i) == navPointField) {
                                      repaint();
                                  }
                              }
                          }
                        });
                        navPointButton.addActionListener(new ActionListener() {
                          
                          @Override
                          public void actionPerformed(ActionEvent arg0) {
                              for(int i = 0; i < alladdedButtons.size(); i++) {
                                  if(alladdedButtons.get(i) == navPointButton) {
                                      tab1.remove(alladdedNavPoints.get(i));
                                      tab1.remove(alladdedButtons.get(i));
                                      alladdedButtons.remove(i);
                                      alladdedNavPoints.remove(i);
                                      repaint();
                                  }
                              }
                          }
                        });
                        tab1.validate();
                        key++;
                    }
                    for(int i = 0; i < alladdedNavPoints.size(); i++) {
                        alladdedNavPoints.get(i).setText(navPointsList.get(i + 1).toString());
                    }
                }
            }         
        });
    }
    
    /**
     * Builds the component tab2 and all its sub components,
     * in the process adding all event listeners.
     */
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
    
    /**
     * Builds the component tab3 and all its sub components,
     * in the process adding all event listeners.
     */
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
                NumberEvent intEvent = new NumberEvent(highwayMalus.getValue());
                for(RALListener lis: listener.highwayMalus) {
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
                NumberEvent intEvent = new NumberEvent(reliefmalus.getValue());
                for(RALListener lis: listener.heightMalus) {
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
                listener.fireEvent(listener.loadMapList, new PathEvent(chooseImportedMap.getSelectedItem().toString()));
            }
        });
        
        
//        heightMapManagement = new JButton("Höhendaten - Verwaltung");
//        heightMapManagement.addActionListener(new ActionListener() {     
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                GUI.this.importHeightMapFileChooser();
//            }
//        });
        
        tab3.add(l_highwayMalus);
        tab3.add(highwayMalus);
        tab3.add(l_heightMalus);
        tab3.add(reliefmalus);
        tab3.add(importOSM);
        tab3.add(deleteMapButton);
        tab3.add(activateMapButton);
        tab3.add(chooseImportedMap);
//        tab3.add(heightMapManagement);
    }
    
    /**
     * Opens a dialog for map file selection. Fires a RAL event.
     */
    private void importMapFileChooser() {
        importFC = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".osm", "osm");
        importFC.setFileFilter(filter);
        int returnValue = importFC.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            importedMapFile = importFC.getSelectedFile();
            ListenerLists.fireEvent(listener.importOsmFile,
                    new PathEvent(importFC.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for heightmap file selection. Fires a RAL event.
     */
    private void importHeightMapFileChooser() {
        importHeightMap = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".hgt", "hgt");
        importHeightMap.setFileFilter(filter);
        int returnValue = importHeightMap.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            importedHeightMap = importHeightMap.getSelectedFile();
            ListenerLists.fireEvent(listener.importHeightMap,
                    new PathEvent(importHeightMap.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for route file selection. Fires a RAL event.
     */
    private void loadRouteFileChooser() {
        loadRoute = new JFileChooser();
        int returnValue = loadRoute.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            loadedRouteFile = loadRoute.getSelectedFile();
            ListenerLists.fireEvent(listener.loadRoute,
                    new PathEvent(loadRoute.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for route output file selection. Fires a RAL event.
     */
    private void saveRouteFileChooser() {
        saveRoute = new JFileChooser();
        int returnValue = saveRoute.showSaveDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            savedRouteFile = saveRoute.getSelectedFile();
            ListenerLists.fireEvent(listener.saveRoute,
                    new PathEvent(loadRoute.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for kml output file selection. Fires a RAL event.
     */
    private void exportRouteKMLFileChooser() {
        exportRoute = new JFileChooser();
        int returnValue = exportRoute.showDialog(this, "Exportieren");
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            exportedRouteFile = exportRoute.getSelectedFile();
            ListenerLists.fireEvent(listener.exportRoute,
                    new PathEvent(loadRoute.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Redraws the complete GUI.
     */
    public void updateGUI() {
        repaint();
    }
    
    /**
     * Sets the entries shown in the imported map selcetion field.
     * @param maps the new entries
     */
    public void updateMapChooser(ArrayList<String> maps) {
        chooseImportedMap.removeAllItems();   
        for(String map : maps) {
            chooseImportedMap.addItem(map);
        }
    }
    
    public void leftClickPOIFav() {
        
    }
    
    public void setSpeed(int speed) {
        s_speed.setValue(speed);
    }
    
    public void setNavPointsOrdered(ArrayList<Coordinates> orderedNavPointsList) {
        navPointsList = orderedNavPointsList; 
    }
    
    public void setZoomlevel(int zoomlevel) {
        scrolling.setValue(zoomlevel);
    }
    
    public void deleteNavNodesFromList(Coordinates coordinates) {
        navPointsList.remove(coordinates);
    }
    
    public ListenerLists getListener() {
        return listener;
    }
    
    public ArrayList<Coordinates> getNavPointsList() {
        return navPointsList;
    }
}
