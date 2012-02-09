package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.TextEvent;
import kit.route.a.lot.gui.event.TextPositionEvent;


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
//    private JButton deleteRoute;
    
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
    private JPanel tab1_allComponents;
    private JPanel tab1_stopoverPanel;
    // private JPanel tab2;
    private JPanel tab3;
    
    private JPopupMenu popUpTextuelCompletition;

    private JSpinner s_speed;

    private ArrayList<JTextField> alladdedNavPoints;
    private ArrayList<JButton> alladdedButtons;
    private ArrayList<JMenuItem> textuelProposals;
    private ArrayList<String> importedMaps;
   
    private Map map;
    
    private ArrayList<Selection> navPointsList;
    private Listeners listeners;
    
    private int popUpX;
    private int popUpY;
    private int popUpFieldPosition;
    
    private boolean enterPressed = false;
    
    /**
     * Creates the GUI window, using the given view center coordinates.
     * @param listeners 
     * @param view center geo coordinates (possibly mercator projected)
     */
    public GUI(Listeners listeners) {
        super("Route-A-Lot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.navPointsList = new ArrayList<Selection>();
        this.listeners = listeners;
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
                //TODO print button handler
            }
        });

        graphics = new JButton("2D/3D");
        graphics.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent event) {
                Coordinates center = map.getCenter();
                int zoomlevel = map.getZoomlevel();
                Listeners.fireEvent(listeners.switchMapMode, new GeneralEvent());
                mapContents.remove(map);
                map = (map instanceof Map2D) ? new Map3D(map.gui) : new Map2D(map.gui);
                mapContents.add(map, BorderLayout.CENTER); 
                mapContents.validate();
                map.setZoomlevel(zoomlevel);
                setView(center);
            }  
        });

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
        scrolling.setValue(3);
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
        listeners.viewChanged.add(new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent event) {
                scrolling.setValue(Util.clip(map.getZoomlevel(), 0, 9));
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
//        createTab2();
        createTab3();
        
        validate();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Listeners.fireEvent(listeners.close, new GeneralEvent());
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
//        tab1.setLayout(new GridBagLayout());
        tab1_allComponents = new JPanel();
        tab1_allComponents.setLayout(new GridBagLayout());
        tab1.add(tab1_allComponents);
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;
        startPoint = new JTextField();
        startPoint.setPreferredSize(new Dimension(this.getWidth() * 2 / 5 - 30, 20));
        startPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startPoint.setBackground(Color.red);
                enterPressed = true;
                Listeners.fireEvent(listeners.getNavNodeDescription, new TextPositionEvent(startPoint.getText(), 0));
            }
        });
        startPoint.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(enterPressed == false) {
                    popUpX = startPoint.getX();
                    popUpY = startPoint.getY() + startPoint.getHeight();
                    popUpFieldPosition = 0;
                    Listeners.fireEvent(listeners.autoCompletition, new TextEvent(startPoint.getText()));
                } else {
                    enterPressed = false;
                }
            }
        });
        
        endPoint = new JTextField();
        endPoint.setPreferredSize(new Dimension(this.getWidth() * 2 / 5 - 30, 20));
        endPoint.addActionListener(new ActionListener() {       
            @Override
            public void actionPerformed(ActionEvent arg0) {
                endPoint.setBackground(Color.red);
                enterPressed = true;
                Listeners.fireEvent(listeners.getNavNodeDescription, new TextPositionEvent(endPoint.getText(), navPointsList.size()-1));
            }
        });
        endPoint.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(enterPressed == false) {
                    popUpX = endPoint.getX();
                    popUpY = endPoint.getY() + endPoint.getHeight();
                    popUpFieldPosition = navPointsList.size() - 1;
                    Listeners.fireEvent(listeners.autoCompletition, new TextEvent(endPoint.getText()));
                } else {
                    enterPressed = false;
                }
            }
        });
        
        addTextPoints = new JButton("+");
        
        optimizeRoute = new JButton("Reihenfolge optimieren");
        optimizeRoute.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Listeners.fireEvent(listeners.optimizeRoute, new TextEvent("optimize"));
            }
        });

        s_speed = new JSpinner(new SpinnerNumberModel(15, 0, null, 1));
        s_speed.setSize(new Dimension(30, 20));
        s_speed.addChangeListener(new ChangeListener() {    
            @Override
            public void stateChanged(ChangeEvent ce) {
                Listeners.fireEvent(listeners.speed, new NumberEvent(Integer.parseInt(s_speed.getValue().toString())));
            }
        });
        
        l_speed = new JLabel("hm/h");
        
//        deleteRoute = new JButton("Lösche Route");
//        deleteRoute.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                for(int i = navPointsList.size() - 1; i >= 0; i--) {
//                    listener.fireEvent(listener.deleteNavPoint, new NumberEvent(i));
//                }
//                repaint();
//            }
//        });

        tab1_stopoverPanel = new JPanel();
        tab1_stopoverPanel.setLayout(new GridBagLayout());
        
        textuelProposals = new ArrayList<JMenuItem>();
        
        popUpTextuelCompletition = new JPopupMenu("Completition");
        popUpTextuelCompletition.setBackground(Color.WHITE);
        
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 3;
        tab1_allComponents.add(startPoint, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 3;
        tab1_allComponents.add(tab1_stopoverPanel, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 3;
        tab1_allComponents.add(endPoint, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 5;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 1;
        constraint.gridy = 6;
        tab1_allComponents.add(addTextPoints, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 7;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 1;
        constraint.gridy = 8;
        tab1_allComponents.add(optimizeRoute, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 9;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 0;
        constraint.gridy = 10;
        tab1_allComponents.add(s_speed, constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 0;
        constraint.gridy = 11;
        constraint.gridwidth = 3;
        tab1_allComponents.add(Box.createVerticalStrut(10), constraint);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weighty = 0.5;
        constraint.gridx = 1;
        constraint.gridy = 10;
        tab1_allComponents.add(l_speed, constraint);
//        tab1.add(deleteRoute);

        alladdedNavPoints = new ArrayList<JTextField>();
        alladdedButtons = new ArrayList<JButton>();
        
        addTextPoints.addActionListener(new ActionListener() {      
          @Override 
          public void actionPerformed(ActionEvent arg0) {
              addTextfieldButton();
              repaint();
           }
        });
    }
    
    /*
     * Builds the component tab2 and all its sub components,
     * in the process adding all event listeners.
     *./
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
        *./
    }*/
    
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
                Listeners.fireEvent(listeners.highwayMalus, new NumberEvent(highwayMalus.getValue()));
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
                Listeners.fireEvent(listeners.heightMalus, new NumberEvent(reliefmalus.getValue()));
            }
        });
        
        importOSM = new JButton("Importiere OSM-Karte");
        importOSM.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.importMapFileChooser();
            }
        });
        
        importedMaps = new ArrayList<String>();
        
        chooseImportedMap = new JComboBox();
        chooseImportedMap.setEditable(true);
        
        deleteMapButton = new JButton("Entfernen");
        deleteMapButton.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Listeners.fireEvent(listeners.deleteMap, new TextEvent(chooseImportedMap.getSelectedItem().toString()));
            }
        });
        
        activateMapButton = new JButton("Aktivieren");
        activateMapButton.addActionListener(new ActionListener() {     
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Listeners.fireEvent(listeners.loadMap, new TextEvent(chooseImportedMap.getSelectedItem().toString()));
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
        File currentDir = null;
        try {
            currentDir = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        importFC.setCurrentDirectory(currentDir);
        int returnValue = importFC.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.importOsmFile,
                    new TextEvent(importFC.getSelectedFile().getPath()));
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
            Listeners.fireEvent(listeners.importHeightMap,
                    new TextEvent(importHeightMap.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for route file selection. Fires a RAL event.
     */
    private void loadRouteFileChooser() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRoute = new JFileChooser();
        int returnValue = loadRoute.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.loadRoute,
                    new TextEvent(loadRoute.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for route output file selection. Fires a RAL event.
     */
    private void saveRouteFileChooser() {
        saveRoute = new JFileChooser();
        int returnValue = saveRoute.showSaveDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.saveRoute,
                    new TextEvent(saveRoute.getSelectedFile().getPath()));
        }
    }
    
    /**
     * Opens a dialog for kml output file selection. Fires a RAL event.
     */
    private void exportRouteKMLFileChooser() {
        exportRoute = new JFileChooser();
        int returnValue = exportRoute.showDialog(this, "Exportieren");
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.exportRoute,
                    new TextEvent(exportRoute.getSelectedFile().getPath()));
        }
    }
    
    private void addTextfieldButton() {
        GridBagConstraints tab1_constraint = new GridBagConstraints();
        final JTextField navPointField = new JTextField();
        navPointField.setPreferredSize(new Dimension(tab1.getWidth() - 60, 20));
        final JButton navPointButton = new JButton("x");
        alladdedNavPoints.add(navPointField); 
        alladdedButtons.add(navPointButton);
        tab1_constraint.fill = GridBagConstraints.HORIZONTAL;
        tab1_constraint.weighty = 0.5;
        tab1_constraint.gridx = 0;
        tab1_constraint.gridy = alladdedButtons.size() - 1;
        tab1_constraint.gridwidth = 2;
        tab1_stopoverPanel.add(navPointField, tab1_constraint);
        tab1_constraint.fill = GridBagConstraints.HORIZONTAL;
        tab1_constraint.weighty = 0.5;
        tab1_constraint.gridx = 3;
        tab1_constraint.gridy = alladdedButtons.size() - 1;
        tab1_constraint.gridwidth = 1;
        tab1_stopoverPanel.add(navPointButton, tab1_constraint);
        navPointField.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
              for(int i = 0; i < alladdedNavPoints.size(); i++) {
                  if(alladdedNavPoints.get(i) == navPointField) {
                      alladdedNavPoints.get(i).setBackground(Color.red);
                      enterPressed = true;
                      Listeners.fireEvent(listeners.getNavNodeDescription, new TextPositionEvent(alladdedNavPoints.get(i).getText(), i + 1));
                      repaint();
                  }
              }
          }
        });
        navPointField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(enterPressed == false) {
                    popUpX = navPointField.getX();
                    popUpY = navPointField.getY() + navPointField.getHeight();
                    for(int i = 0; i < alladdedNavPoints.size(); i++) {
                        if(alladdedNavPoints.get(i) == navPointField) {
                            popUpFieldPosition = i + 1;
                        }
                    }
                    Listeners.fireEvent(listeners.autoCompletition, new TextEvent(navPointField.getText()));
                } else {
                    enterPressed = false;
                }
            }
        });
        
        navPointButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
              for(int i = 0; i < alladdedButtons.size(); i++) {
                  if(alladdedButtons.get(i) == navPointButton) {
                      if(!alladdedNavPoints.get(i).getText().equals("") && alladdedButtons.size() == navPointsList.size()-2) {
                          Listeners.fireEvent(listeners.deleteNavPoint, new NumberEvent(i+1));
                      } else  {
                          tab1_stopoverPanel.remove(alladdedNavPoints.get(i));
                          tab1_stopoverPanel.remove(alladdedButtons.get(i));
                          alladdedButtons.remove(i);
                          alladdedNavPoints.remove(i);
                      }
                      repaint();
                  }
              }
          }
        });
        tab1.validate();
    }
    
    private void addMenuItem(String name) {
        final JMenuItem item = new JMenuItem(name);
        textuelProposals.add(item);
        popUpTextuelCompletition.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < textuelProposals.size(); i++) {
                    if(textuelProposals.get(i) == item) {
                        Listeners.fireEvent(listeners.addTextualNavPoint,
                                new TextPositionEvent(textuelProposals.get(i).getText(),
                                        popUpFieldPosition));
                    }
                }
            }
        });
    }
    
    /**
     * Redraws the complete GUI.
     */
    public void updateGUI() {
        repaint();
    }
    
    /**
     * Sets the entries shown in the imported map selection field.
     * @param maps the new entries
     */
    public void updateMapChooser(ArrayList<String> maps) {
        for(int i = 0; i < importedMaps.size(); i++) {
            chooseImportedMap.removeItem(chooseImportedMap.getItemAt(i));
        }
        importedMaps = new ArrayList<String>(maps);
        for(String map : maps) {
            chooseImportedMap.addItem(map); //TODO keine doppelten NAmen
        }
    }
    
    public void leftClickPOIFav() {
        
    }
    
    /**
     * Changes the geo coordinates view position and subsequently
     * updates the context and redraws the map.
     * @param center the new view center
     */ 
    public void setView(Coordinates center) {
        map.setCenter(center);
        map.calculateView();
        map.repaint();
    }
    
    public void setSpeed(int speed) {
        s_speed.setValue(speed);
    }
    
    public void setZoomlevel(int zoomlevel) {
        scrolling.setValue(zoomlevel);
    }
    
    public Listeners getListeners() {
        return listeners;
    }
    
    public ArrayList<Selection> getNavPointsList() {
        return navPointsList;
    }
    
    public void updateNavNodes(List<Selection> newNavPointsList) {
        while(alladdedButtons.size() != 0) {
            int i = alladdedButtons.size() - 1;
            tab1_stopoverPanel.remove(alladdedNavPoints.get(i));
            tab1_stopoverPanel.remove(alladdedButtons.get(i));
            alladdedButtons.remove(i);
            alladdedNavPoints.remove(i);
            i--;
        }
        startPoint.setText("");
        startPoint.setBackground(Color.WHITE);
        endPoint.setText("");
        endPoint.setBackground(Color.WHITE);
        this.navPointsList = new ArrayList<Selection>(newNavPointsList);
        if(this.navPointsList.size() - 2 > 0) {
            for(int i = 1; i < newNavPointsList.size() - 1; i++) {
                addTextfieldButton();
                if(newNavPointsList.get(i).getName() == null) {
                    alladdedNavPoints.get(i-1).setText(newNavPointsList.get(i).getPosition().toString());
                } else {
                    alladdedNavPoints.get(i-1).setText(newNavPointsList.get(i).getName());
                }
            }
            if(newNavPointsList.get(0).getName() == null) {
                startPoint.setText(newNavPointsList.get(0).getPosition().toString());
            } else {
                startPoint.setText(this.navPointsList.get(0).getName());
            }
            if(newNavPointsList.get(this.navPointsList.size() - 1).getName() == null) {
                endPoint.setText(newNavPointsList.get(this.navPointsList.size() - 1).getPosition().toString());
            } else {
                endPoint.setText(this.navPointsList.get(this.navPointsList.size() - 1).getName());
            }
        } else if(this.navPointsList.size() == 2) {
            if(newNavPointsList.get(0).getName() == null) {
                startPoint.setText(newNavPointsList.get(0).getPosition().toString());
            } else {
                startPoint.setText(this.navPointsList.get(0).getName());
            }
            if(newNavPointsList.get(1).getName() == null) {
                endPoint.setText(newNavPointsList.get(1).getPosition().toString());
            } else {
                endPoint.setText(this.navPointsList.get(1).getName());
            }
        } else if(this.navPointsList.size() == 1){
            if(newNavPointsList.get(0).getName() == null) {
                startPoint.setText(newNavPointsList.get(0).getPosition().toString());
            } else {
                startPoint.setText(this.navPointsList.get(0).getName());
            }
        }
        repaint();
    }
    
    public void popUpTrigger(int itemType, Coordinates position) {
        map.popUpTriggered(itemType, position);
    }
    
    public void showPOIDescription(POIDescription description) {
        map.showPOIDescription(description);
    }
    
    public void showFavDescription(POIDescription description) {
        map.showFavDescription(description);
    }
    
    public void showNavNodeDescription(String description, int navNodeIndex) {
        alladdedNavPoints.get(navNodeIndex - 1).setText(description);
    }
    
    public void showSearchCompletion(List<String> completion) {
        while(textuelProposals.size() != 0) {
            int i = textuelProposals.size() - 1;
            popUpTextuelCompletition.remove(textuelProposals.get(i));
            popUpTextuelCompletition.remove(textuelProposals.get(i));
            textuelProposals.remove(i);
            textuelProposals.remove(i);
            i--;
        }
        for(int i = 0; i < completion.size(); i++) {
            addMenuItem(completion.get(i));
        }
        popUpTextuelCompletition.show(tab1_allComponents, popUpX, popUpY);
        repaint();
    }
    
    public void showRouteValues(int duration, int length) {
        int hours = duration/3600;
        int minutes = (duration - hours*3600)/60;
        int seconds = duration - (hours * 3600) - (minutes * 60);
        float kilometers = length/1000f;
        if(hours!=0) {
            l_position.setText("(" + kilometers + "km, " + hours + "st " + minutes + "min" + ")");
        } else if(minutes!=0) {
            l_position.setText("(" + kilometers + "km, " + minutes + "min" + ")");
        } else {
            l_position.setText("(" + kilometers + "km, " + seconds + "sek" + ")");
        }
        repaint();
    }
}
