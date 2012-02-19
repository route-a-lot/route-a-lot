package kit.route.a.lot.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Listener;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.gui.event.Event;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.TextEvent;
import kit.route.a.lot.gui.event.NavNodeNameEvent;
import static kit.route.a.lot.common.Listener.*;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // ROUTING TAB
    private JPanel routingTab, routingTabTopArea, waypointArea;
    private JTextField fieldStartNode, fieldEndNode;
    private JSpinner fieldSpeed;
    private JPopupMenu popupSearchCompletions;
    
    // MAP TAB
    private JPanel mapTab;
    private JSlider highwayMalusSlider, reliefMalusSlider;
    private JComboBox listChooseMap; 
    
    // DESCRIPTION TAB
    /* private JList textRoute;
       private JScrollPane textRouteScrollPane; */
    
    // CENTRAL AREA (BUTTON BAR and MAP)
    private JPanel centralArea;
    private JFileChooser dialogImportOSM, dialogLoadRoute, dialogSaveRoute,
                dialogExportRoute /*, dialogImportHeightmap*/;
    private JSlider zoomSlider;
    private Map map;
    
    // STATUS BAR
    private JLabel routeValues, mouseCoordinatesDisplay;

    private List<String> importedMaps = new ArrayList<String>();
    private List<Selection> navNodeList;
    private Listeners listeners;

    // NON-COMPONENT ATTRIBUTES
    private Point popupPos;
    private int popupIndex;
    private boolean enterPressed = false;
    private boolean active = false; // indicates whether main thread has finished startup

    /**
     * Creates the GUI window, using the given view center coordinates.
     * @param listeners
     * @param view center geo coordinates (possibly mercator projected)
     */
    public GUI(Listeners listeners) {
        super("Route-A-Lot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.navNodeList = new ArrayList<Selection>();
        this.listeners = listeners;
    }

    /**
     * Builds all components and adds them to the GUI.
     */
    public void addContents() {
        // MAP
        map = new Map2D(this);

        // BUTTON PANEL
        JButton buttonLoadRoute = new JButton("Laden");
        buttonLoadRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.loadRouteFileChooser();
            }
        });
        JButton buttonSaveRoute = new JButton("Speichern");
        buttonSaveRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.saveRouteFileChooser();
            }
        });
        JButton buttonExportKML = new JButton("KML-Export");
        buttonExportKML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.exportRouteKMLFileChooser();
            }
        });
        JButton buttonSwitchMapMode = new JButton("2D/3D");
        buttonSwitchMapMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                while (!active); // postbone execution until after startup
                listeners.fireEvent(SWITCH_MAP_MODE, null);   
            }
        });

        zoomSlider = new JSlider(0, 9, 3);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                map.setZoomlevel(zoomSlider.getValue());
                map.calculateView();
            }
        });
        listeners.addListener(VIEW_CHANGED, new Listener() {
            @Override
            public void handleEvent(Event event) {
                zoomSlider.setValue(Util.clip(map.getZoomlevel(), 0, 9));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));
        buttonPanel.add(new JLabel("Route:"));
        buttonPanel.add(buttonLoadRoute);
        buttonPanel.add(buttonSaveRoute);
        buttonPanel.add(buttonExportKML);
        buttonPanel.add(buttonSwitchMapMode);
        buttonPanel.add(zoomSlider);

        // AREAS
        centralArea = new JPanel(new BorderLayout());
        centralArea.add(buttonPanel, BorderLayout.NORTH);
        centralArea.add(map, BorderLayout.CENTER);

        JTabbedPane tabArea = new JTabbedPane();
        tabArea.setPreferredSize(new Dimension(this.getWidth() * 2 / 5, this.getHeight()));
        tabArea.setBackground(Color.LIGHT_GRAY);
        createRoutingTab();
        createMapTab();
        tabArea.addTab("Planen", null, routingTab, "Start-, Ziel-, Zwischenhalts- und Geschwindigkeitseinstellungen.");
        tabArea.addTab("Karten", null, mapTab, "Import von Höhen- und OSM-Karten, Laden von Karten, Einstellung der Maluse.");
        // tabArea.setMnemonicAt(1, KeyEvent.VK_1);
        // tabArea.setMnemonicAt(1, KeyEvent.VK_2);

        routeValues = new JLabel();
        mouseCoordinatesDisplay = new JLabel();
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusBar.add(new JLabel("Route:"));
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(routeValues);
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(mouseCoordinatesDisplay);
        statusBar.add(Box.createHorizontalStrut(10));
        
        // FRAME LAYOUT
        setLayout(new BorderLayout());
        add(tabArea, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
        add(centralArea, BorderLayout.CENTER);
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listeners.fireEvent(CLOSE_APPLICATION, null);
            }
        });
    }

    /**
     * Builds the routing tab component and all its sub components, in the process adding all event listeners.
     */
    private void createRoutingTab() {
        // POPUP        
        popupSearchCompletions = new JPopupMenu("Completion");
        popupSearchCompletions.setBackground(Color.WHITE);

        // COMPONENTS
        JLabel caption = new JLabel("<html><u>Wegpunkte:</u></html>");
        caption.setAlignmentX(JLabel.RIGHT_ALIGNMENT); // should be left alignment, which somehow doesn't work


        fieldStartNode = new JTextField();
        fieldStartNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldStartNode.setBackground(Color.red);
                enterPressed = true;
                listeners.fireEvent(SHOW_NAVNODE_DESCRIPTION,
                        new NavNodeNameEvent(fieldStartNode.getText(), 0));
            }
        });
        fieldStartNode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (enterPressed == false) {
                    popupPos = new Point(fieldStartNode.getX(), 
                            fieldStartNode.getY() + fieldStartNode.getHeight());
                    popupIndex = 0;
                    listeners.fireEvent(LIST_SEARCH_COMPLETIONS,
                            new TextEvent(fieldStartNode.getText()));
                } else {
                    enterPressed = false;
                }
            }
        });

        waypointArea = new JPanel();
        waypointArea.setLayout(new BoxLayout(waypointArea, BoxLayout.Y_AXIS));
        // waypointArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        fieldEndNode = new JTextField();
        fieldEndNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                fieldEndNode.setBackground(Color.red);
                enterPressed = true;
                listeners.fireEvent(SHOW_NAVNODE_DESCRIPTION,
                        new NavNodeNameEvent(fieldEndNode.getText(), navNodeList.size() - 1));
            }
        });
        fieldEndNode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!enterPressed) {
                    popupPos = new Point(fieldEndNode.getX(), 
                            fieldEndNode.getY() + fieldEndNode.getHeight());
                    popupIndex = navNodeList.size() - 1;
                    listeners.fireEvent(LIST_SEARCH_COMPLETIONS,
                            new TextEvent(fieldEndNode.getText()));
                } else {
                    enterPressed = false;
                }
            }
        });

        JButton buttonAddNavNode = new JButton("+");
        buttonAddNavNode.setMaximumSize(new Dimension(200, 100));
        buttonAddNavNode.setAlignmentX(JButton.CENTER_ALIGNMENT);
        buttonAddNavNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                addWaypointField("");
                repaint();
            }
        });

        JButton buttonOptimizeRoute = new JButton("Reihenfolge optimieren");
        buttonOptimizeRoute.setMaximumSize(new Dimension(200, 100));
        buttonOptimizeRoute.setAlignmentX(JButton.CENTER_ALIGNMENT);
        buttonOptimizeRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                listeners.fireEvent(OPTIMIZE_ROUTE, null);
            }
        });

        JPanel speedArea = new JPanel();
        speedArea.setLayout(new BorderLayout());
        JPanel speedInternalArea = new JPanel();
        fieldSpeed = new JSpinner(new SpinnerNumberModel(15, 1, null, 1));
        fieldSpeed.setPreferredSize(new Dimension(50, 20));
        fieldSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                listeners.fireEvent(SET_SPEED,
                        new NumberEvent(Integer.parseInt(fieldSpeed.getValue().toString())));
            }
        });
        speedArea.add(new JLabel("<html><u>Geschwindigkeit (\u00D8):</u></html>"), BorderLayout.NORTH);
        speedArea.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        speedArea.add(speedInternalArea, BorderLayout.SOUTH);
        speedInternalArea.add(fieldSpeed);
        speedInternalArea.add(new JLabel("km/h"));

        // AREAS
        routingTabTopArea = new JPanel();
        routingTabTopArea.setLayout(new BoxLayout(routingTabTopArea, BoxLayout.Y_AXIS));
        routingTabTopArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        routingTabTopArea.add(caption);
        routingTabTopArea.add(Box.createVerticalStrut(10));
        routingTabTopArea.add(fieldStartNode);
        routingTabTopArea.add(Box.createVerticalStrut(8));
        routingTabTopArea.add(waypointArea);
        routingTabTopArea.add(Box.createVerticalStrut(3));
        routingTabTopArea.add(fieldEndNode);
        routingTabTopArea.add(Box.createVerticalStrut(20));
        routingTabTopArea.add(buttonAddNavNode);
        routingTabTopArea.add(Box.createVerticalStrut(5));
        routingTabTopArea.add(buttonOptimizeRoute);
        routingTabTopArea.add(Box.createVerticalStrut(25));
        routingTabTopArea.add(speedArea);

        routingTab = new JPanel(new BorderLayout());
        routingTab.add(routingTabTopArea, BorderLayout.NORTH);
    }

    /*
     * Builds the component tab2 and all its sub components, in the process adding all event listeners../
     * private void createTab2() { tab2 = new JPanel(); tabbpane.addTab("Beschreibung", null, tab2, "2"); //
     * tabbpane.setMnemonicAt(2, KeyEvent.VK_2); textRouteList = new DefaultListModel(); String[] data =
     * {"one", "two", "three", "four", "five", "six", "seve", "eight"}; textRoute = new JList(textRouteList);
     * for(int i = 0; i < data.length; i++) { textRouteList.add(i, data[i]); } textRoute.setPreferredSize(new
     * Dimension(tab2.getSize())); textRouteScrollPane = new JScrollPane(textRoute); tab2.add(textRoute); /*
     * textRouteList.add(textRoute.getModel().getSize(), "ende"); textRouteList.add(0,
     * "anfangawdwadfadwadwadwad"); textRouteList.set(3, "replaced"); textRouteList.remove(2);./ }
     */

    /**
     * Builds the component tab3 and all its sub components, in the process adding all event listeners.
     */
    private void createMapTab() {
        // COMPONENTS
        JLabel captionHighwayMalus = new JLabel("Fernstraßenmalus");
        highwayMalusSlider = new JSlider(1, 5, 1);
        highwayMalusSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        highwayMalusSlider.setMajorTickSpacing(1);
        highwayMalusSlider.setPaintTicks(true);
        highwayMalusSlider.setSnapToTicks(true);
        highwayMalusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                listeners.fireEvent(SET_HIGHWAY_MALUS,
                        new NumberEvent(highwayMalusSlider.getValue()));
            }
        });

        JLabel captionReliefMalus = new JLabel("Reliefmalus");
        reliefMalusSlider = new JSlider(1, 5, 1);
        reliefMalusSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        reliefMalusSlider.setMajorTickSpacing(1);
        reliefMalusSlider.setPaintTicks(true);
        reliefMalusSlider.setSnapToTicks(true);
        reliefMalusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                listeners.fireEvent(SET_HEIGHT_MALUS,
                        new NumberEvent(reliefMalusSlider.getValue()));
            }
        });

        JButton buttonImportOSM = new JButton("Importiere OSM-Karte");
        buttonImportOSM.setMaximumSize(new Dimension(200, 100));
        buttonImportOSM.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonImportOSM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.importMapFileChooser();
            }
        });

        JButton buttonDeleteMap = new JButton("Entfernen");
        buttonDeleteMap.setMaximumSize(new Dimension(200, 100));
        buttonDeleteMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonDeleteMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listeners.fireEvent(DELETE_IMPORTED_MAP,
                        new TextEvent(listChooseMap.getSelectedItem().toString()));
            }
        });

        JButton buttonActivateMap = new JButton("Aktivieren");
        buttonActivateMap.setMaximumSize(new Dimension(200, 100));
        buttonActivateMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonActivateMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listeners.fireEvent(LOAD_MAP,
                        new TextEvent(listChooseMap.getSelectedItem().toString()));
            }
        });

        listChooseMap = new JComboBox();
        listChooseMap.setAlignmentX(JButton.CENTER_ALIGNMENT);
        listChooseMap.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent e) {
                listeners.fireEvent(LIST_IMPORTED_MAPS, null);
            }
            public void ancestorMoved(AncestorEvent e) {}
            public void ancestorRemoved(AncestorEvent e) {}           
        });

        // heightMapManagement = new JButton("Höhendaten - Verwaltung");
        // heightMapManagement.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // GUI.this.importHeightMapFileChooser();
        // }
        // });

        // AREAS
        JPanel mapTabTopArea = new JPanel();
        mapTabTopArea.setLayout(new BoxLayout(mapTabTopArea, BoxLayout.Y_AXIS));
        mapTabTopArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel captionHighwayMalusArea = new JPanel();
        captionHighwayMalusArea.add(captionHighwayMalus);
        JPanel captionReliefMalusArea = new JPanel();
        captionReliefMalusArea.add(captionReliefMalus);

        mapTabTopArea.add(captionHighwayMalusArea);
        mapTabTopArea.add(highwayMalusSlider);
        mapTabTopArea.add(captionReliefMalusArea);
        mapTabTopArea.add(reliefMalusSlider);
        mapTabTopArea.add(Box.createVerticalStrut(20));
        mapTabTopArea.add(buttonImportOSM);
        mapTabTopArea.add(Box.createVerticalStrut(10));
        mapTabTopArea.add(listChooseMap);
        mapTabTopArea.add(Box.createVerticalStrut(10));
        mapTabTopArea.add(buttonDeleteMap);
        mapTabTopArea.add(Box.createVerticalStrut(5));
        mapTabTopArea.add(buttonActivateMap);

        mapTab = new JPanel(new BorderLayout());
        mapTab.add(mapTabTopArea, BorderLayout.NORTH);
    }

    /**
     * Opens a dialog for map file selection. Fires an event.
     */
    private void importMapFileChooser() {
        dialogImportOSM = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".osm", "osm");
        dialogImportOSM.setFileFilter(filter);
        File currentDir = null;
        try {
            currentDir = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialogImportOSM.setCurrentDirectory(currentDir);
        int returnValue = dialogImportOSM.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            listeners.fireEvent(IMPORT_OSM,
                    new TextEvent(dialogImportOSM.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for heightmap file selection. Fires an event.
     */
    /*
     * private void importHeightMapFileChooser() { importHeightMap = new JFileChooser();
     * FileNameExtensionFilter filter = new FileNameExtensionFilter(".hgt", "hgt");
     * importHeightMap.setFileFilter(filter); int returnValue = importHeightMap.showOpenDialog(this);
     * if(returnValue == JFileChooser.APPROVE_OPTION) { Listeners.fireEvent(listeners.importHeightMap, new
     * TextEvent(importHeightMap.getSelectedFile().getPath())); } }
     */

    /**
     * Opens a dialog for route file selection. Fires an event.
     */
    private void loadRouteFileChooser() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogLoadRoute = new JFileChooser();
        int returnValue = dialogLoadRoute.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            listeners.fireEvent(LOAD_ROUTE,
                    new TextEvent(dialogLoadRoute.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for route output file selection. Fires an event.
     */
    private void saveRouteFileChooser() {
        dialogSaveRoute = new JFileChooser();
        int returnValue = dialogSaveRoute.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            listeners.fireEvent(SAVE_ROUTE,
                    new TextEvent(dialogSaveRoute.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for kml output file selection. Fires an event.
     */
    private void exportRouteKMLFileChooser() {
        dialogExportRoute = new JFileChooser();
        int returnValue = dialogExportRoute.showDialog(this, "Exportieren");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            listeners.fireEvent(EXPORT_ROUTE,
                            new TextEvent(dialogExportRoute.getSelectedFile().getPath()));
        }
    }

    /**
     * Adds a new field to the navigation node list.
     * @param text the initial field content
     */
    private void addWaypointField(String text) {
        final JTextField waypointField = new JTextField(text);
        JButton buttonDeleteWaypoint = new JButton("x");
        buttonDeleteWaypoint.setPreferredSize(new Dimension(45, 15));
        final JPanel row = new JPanel(new BorderLayout());
        row.add(waypointField, BorderLayout.CENTER);
        row.add(buttonDeleteWaypoint, BorderLayout.EAST);
        row.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        waypointArea.add(row);
        routingTab.validate();

        final int pos = waypointArea.getComponentCount();

        waypointField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                waypointField.setBackground(Color.red);
                enterPressed = true;
                listeners.fireEvent(SHOW_NAVNODE_DESCRIPTION,
                        new NavNodeNameEvent(waypointField.getText(), pos));
                repaint();
            }
        });

        waypointField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!enterPressed) {
                    popupPos = new Point(waypointField.getX(),
                            waypointField.getY() + waypointField.getHeight());
                    popupIndex = pos;
                    listeners.fireEvent(LIST_SEARCH_COMPLETIONS,
                            new TextEvent(waypointField.getText()));
                }
                enterPressed = false;
            }
        });

        buttonDeleteWaypoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                waypointArea.remove(row);
                if ((waypointField.getText().length() != 0) && (navNodeList.size() > pos)) {
                    listeners.fireEvent(DELETE_NAVNODE, new NumberEvent(pos));
                }
                repaint();
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
    public void updateMapChooser(List<String> maps) {
        for (int i = 0; i < importedMaps.size(); i++) {
            listChooseMap.removeItem(listChooseMap.getItemAt(i));
        }
        importedMaps = new ArrayList<String>(maps);
        for (String map : maps) {
            listChooseMap.addItem(map); // TODO keine doppelten NAmen
        }
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

    /**
     * Sets the speed value display.
     * @param speed the new speed value
     */
    public void setSpeed(int speed) {
        fieldSpeed.setValue(speed);
    }

    /**
     * Sets the zoom level display.
     * @param zoomlevel the new zoom level
     */
    public void setZoomlevel(int zoomlevel) {
        zoomSlider.setValue(zoomlevel);
    }

    /**
     * Returns the listener collection object.
     * @return the listener collection object
     */
    public Listeners getListeners() {
        return listeners;
    }

    /**
     * Returns the list containing the current navnode selection.
     * @return the navigation node list
     */
    public List<Selection> getNavNodeList() {
        return navNodeList;
    }

    /**
     * Replaces the current navigation node list and updates
     * the fields display correspondingly.
     * @param newNavPointsList the new navnode list
     */
    public void updateNavNodes(List<Selection> newNavPointsList) {
        waypointArea.removeAll();
        fieldStartNode.setText("");
        fieldStartNode.setBackground(Color.WHITE);
        fieldEndNode.setText("");
        fieldEndNode.setBackground(Color.WHITE);
        navNodeList = new ArrayList<Selection>(newNavPointsList);
        if (navNodeList.size() > 0) {
            fieldStartNode.setText(navNodeList.get(0).getName());
        }
        if (navNodeList.size() > 1) {
            fieldEndNode.setText(navNodeList.get(navNodeList.size() - 1).getName());
        }
        for (int i = 1; i < newNavPointsList.size() - 1; i++) {
            addWaypointField(navNodeList.get(i).getName());
        }
        repaint();
    }

    /**
     * Called by the Controller after Map has queried a position.
     * Forwards the element type at the position back to Map.
     * @param itemType the element type
     */
    public void passElementType(int itemType) {
        map.passElementType(itemType);
    }

    /**
     * Called by the Controller after Map has queried a POI / favorite
     * description by position. Forwards the description back to Map.
     * @param description the description
     */
    public void passDescription(POIDescription description) {
        map.passDescription(description);
    }

    /**
     * Opens the search completion popup with the given list of suggestions.
     * @param completions the completion suggestions
     */
    public void showSearchCompletions(List<String> completions) {
        popupSearchCompletions.removeAll();
        for (String completion : completions) {
            final JMenuItem item = new JMenuItem(completion);
            popupSearchCompletions.add(item);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listeners.fireEvent(ADD_NAVNODE,
                            new NavNodeNameEvent(item.getText(), popupIndex));
                }
            });
        }
        popupSearchCompletions.show(routingTabTopArea, popupPos.x, popupPos.y);
        repaint();
    }

    /**
     * Displays the given route attributes in the status bar.
     * @param length the route length (in meters)
     * @param duration the route duration using the current average speed (in seconds)
     */
    public void showRouteValues(int length, int duration) {
        int hours = duration / 3600;
        int minutes = (duration - hours * 3600) / 60;
        int seconds = duration - (hours * 3600) - (minutes * 60);
        String output = (length / 1000f) + "km, " + ((hours != 0) ? hours + "h " : "");
        routeValues.setText("(" + output + ((minutes != 0) ? minutes + "min" : seconds + "sek")+ ")");
    }
    
    /**
     * Displays the given coordinates in the status bar.
     * @param mousePosition the given mouse coordinates
     */
    public void showMouseCoordinates(Coordinates mousePosition) {
        mouseCoordinatesDisplay.setText(mousePosition.toString());
    }

    
    /**
     * Called when the main thread has finished operating. Signals the GUI whether it
     * can now respond to AWT events without danger of malsynchronisation.
     * @param active (de-)activates some GUI event responds
     */
    public void setActive(boolean active) {
        this.active = active;   
    }

    public void setMapMode(boolean render3D) {
        Coordinates center = map.getCenter();
        int zoomlevel = map.getZoomlevel();       
        centralArea.remove(map);
        map = (render3D) ? new Map3D(this) : new Map2D(this);
        centralArea.add(map, BorderLayout.CENTER);
        centralArea.validate();
        map.setZoomlevel(zoomlevel);
        setView(center);
    }
}
