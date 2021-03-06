
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Daniel Krauß, Jan Jacob, Malte Wolff, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.gui;

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.POIDescription;
import kit.ral.common.event.Event;
import kit.ral.common.event.*;
import kit.ral.common.event.TextEvent;
import kit.ral.common.util.MathUtil;
import kit.ral.common.util.StringUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static kit.ral.common.event.Listener.*;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final int END_POSITION = -1;
    
    // ROUTING TAB
    private JPanel routingTab, routingTabTopArea, waypointArea, routingSpeedTab,
                    routingButtonPanel, importOptions, availableMaps;
    private JTextField fieldStartNode, fieldEndNode, navComp;
    private JSpinner fieldSpeed;
    private JPopupMenu popupSearchCompletions;
    private JButton buttonAddNavNode, buttonOptimizeRoute;
    
    // MAP TAB
    private JPanel mapTab;
    private JSlider highwayMalusSlider, reliefMalusSlider;
    private JComboBox listChooseMap; 
    private JButton buttonImportOSM, buttonActivateMap, buttonDeleteMap;
    
    // DESCRIPTION TAB
    /* private JList textRoute;
       private JScrollPane textRouteScrollPane; */
    
    // CENTRAL AREA (BUTTON BAR and MAP)
    private JPanel centralArea;
    private JSlider zoomSlider;
    private Map map;
    
    // STATUS BAR
    private JLabel routeValues, mouseCoordinatesDisplay;

    private JProgressBar progressBar;
    private JButton buttonCancelOperation;

    // NON-COMPONENT ATTRIBUTES
    private JTextField currentNavNodeField; // for popup
    private int currentNavNodeIndex, numNavNodes = 0;
    private long taskStartTime;
    private boolean enterPressed = false;
    private boolean nextNavPoint = false;
    private Icon deleteIcon, selectStartIcon, selectWaypointIcon, selectDestinationIcon, switchIcon;
    private boolean active = false; // indicates whether main thread has finished startup

    /**
     * Creates the GUI window, using the given view center coordinates.
     */
    public GUI() {
        super("Route-A-Lot"); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        installLookAndFeel();
        List<Image> icons = new LinkedList<Image>();     
        icons.add(new ImageIcon(ClassLoader.getSystemResource("icon_sral_16.png")).getImage());
        icons.add(new ImageIcon(ClassLoader.getSystemResource("icon_sral_32.png")).getImage());
        icons.add(new ImageIcon(ClassLoader.getSystemResource("icon_sral_64.png")).getImage());
        setIconImages(icons);

        deleteIcon = new ImageIcon(ClassLoader.getSystemResource("icon_delete.png"));
        switchIcon = new ImageIcon(ClassLoader.getSystemResource("icon_switch.png"));
        selectStartIcon = new ImageIcon(ClassLoader.getSystemResource("icon_greenarrow.png"));
        selectWaypointIcon = new ImageIcon(ClassLoader.getSystemResource("icon_yellowarrow.png"));
        selectDestinationIcon = new ImageIcon(ClassLoader.getSystemResource("icon_redarrow.png"));
        setVisible(true);
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
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.loadRouteFileChooser();
            }
        });
        JButton buttonSaveRoute = new JButton("Speichern");
        buttonSaveRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.saveRouteFileChooser();
            }
        });
        JButton buttonExportKML = new JButton("KML-Export");
        buttonExportKML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.exportRouteKMLFileChooser();
            }
        });
        JButton buttonSwitchMapMode = new JButton("2D/3D");
        buttonSwitchMapMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Listener.fireEvent(SWITCH_MAP_MODE, null);   
            }
        });

        zoomSlider = new JSlider(0, 9, 0);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                map.setZoomlevel(zoomSlider.getValue());
                map.render();
            }
        });
        Listener.addListener(VIEW_CHANGED, new Listener() {
            public void handleEvent(Event e) {
                int level = ((PositionNumberEvent) e).getNumber();
                if (level != zoomSlider.getValue()) {
                    zoomSlider.setValue(level);
                }
            }           
        });
        
        // JButton buttonDrawAreas = new JButton("Draw Areas");
        // buttonDrawAreas.addActionListener(new ActionListener() {
        //     public void actionPerformed(ActionEvent arg0) {
        //         Renderer.drawAreas = !Renderer.drawAreas;
        //         State.getInstance().getActiveRenderer().resetCache();
        //         map.repaint();
        //     }
        // });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(this.getWidth() + 60, 80));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        JPanel routePanel = new JPanel();
        routePanel.setLayout(new BoxLayout(routePanel, BoxLayout.X_AXIS));
        routePanel.setBorder(BorderFactory.createTitledBorder("Route"));
        routePanel.add(buttonLoadRoute);
        routePanel.add(Box.createHorizontalStrut(5));
        routePanel.add(buttonSaveRoute);
        routePanel.add(Box.createHorizontalStrut(5));
        routePanel.add(buttonExportKML);
        JPanel mapPanel =  new JPanel();
        mapPanel.setLayout(new BoxLayout(mapPanel, BoxLayout.X_AXIS));
        mapPanel.setBorder(BorderFactory.createTitledBorder("Anzeige"));
        mapPanel.add(buttonSwitchMapMode);
        mapPanel.add(Box.createHorizontalStrut(10));
        mapPanel.add(zoomSlider);
        // mapPanel.add(Box.createHorizontalStrut(10));
        // mapPanel.add(buttonDrawAreas);
        buttonPanel.add(routePanel, BorderLayout.WEST);
        buttonPanel.add(mapPanel, BorderLayout.EAST);

        // PROGRESS ELEMENTS
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        Listener.addListener(PROGRESS, new Listener() {
            public void handleEvent(Event e) {
                float progress = ((FloatEvent) e).getNumber();
                setActive(progress < 0 || progress >= 100);
                int time = (int)((System.currentTimeMillis() - taskStartTime) / 1000
                                    * ((100 - progress) / progress));
                progressBar.setValue(MathUtil.clip((int) progress, 0, 100));
                progressBar.setString((active) ? "" : progressBar.getValue()
                        + "%, noch " + StringUtil.formatSeconds(time, false));
            }
        });
        buttonCancelOperation = new JButton(deleteIcon);
        buttonCancelOperation.setPreferredSize(new Dimension(20, 15));
        buttonCancelOperation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Listener.fireEvent(CANCEL_OPERATION, null);
            } 
        });

        
        // AREAS
        centralArea = new JPanel(new BorderLayout());
        centralArea.add(buttonPanel, BorderLayout.NORTH);
        centralArea.add(map, BorderLayout.CENTER);

        JTabbedPane tabArea = new JTabbedPane();
        tabArea.setPreferredSize(new Dimension(this.getWidth() * 2 / 5, this.getHeight()));
        createRoutingTab();
        createMapTab();
        tabArea.addTab("Planen", null, routingTab,
                "Start-, Ziel-, Zwischenhalts- und Geschwindigkeitseinstellungen.");
        tabArea.addTab("Karten", null, mapTab,
                "Import von Höhen- und OSM-Karten, Laden von Karten, Einstellung der Maluse.");
        // tabArea.setMnemonicAt(1, KeyEvent.VK_1);
        // tabArea.setMnemonicAt(1, KeyEvent.VK_2);

        
        JPanel progressArea = new JPanel(new BorderLayout());       
        progressArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        progressArea.setMaximumSize(new Dimension(400, 100));
        progressArea.add(progressBar, BorderLayout.CENTER);
        progressArea.add(buttonCancelOperation, BorderLayout.EAST);
        
        // GLASS PANE
        JPanel glass =  new JPanel() {
            private static final long serialVersionUID = 1L;
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getSize().width, getSize().height);
            }
        };
        glass.setLayout(new BoxLayout(glass, BoxLayout.Y_AXIS));
        glass.setOpaque(false);
        glass.setBackground(new Color(0, 0, 0, 80));
        glass.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
            public void mousePressed(MouseEvent e) {
                e.consume();
            }
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }      
        });
        glass.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                e.consume();
            }
            public void keyReleased(KeyEvent e) {
                e.consume();
            }
        });
        glass.setFocusTraversalKeysEnabled(false);
        glass.add(Box.createGlue());
        glass.add(progressArea);
        glass.add(Box.createGlue());
        setGlassPane(glass);
        

        // STATUS BAR
        mouseCoordinatesDisplay = new JLabel(new Coordinates().toString());
        
        routeValues = new JLabel();
        showRouteValues(0, 0);
        
        final JProgressBar memoryConsumption = new JProgressBar(0, 100);
        memoryConsumption.setStringPainted(true);
        memoryConsumption.setMinimumSize(new Dimension(150, 20));
        memoryConsumption.setMaximumSize(new Dimension(150, 20));
        Timer memoryConsumptionTimer = new Timer("memory consumption timer");
        memoryConsumptionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long maxMemory = Runtime.getRuntime().maxMemory();
                long totalMemory = Runtime.getRuntime().totalMemory();
                memoryConsumption.setValue((int) (100 * ((double) totalMemory / maxMemory)));
                memoryConsumption.setString(StringUtil.humanReadableByteCount(totalMemory, true)
                        + " / " + StringUtil.humanReadableByteCount(maxMemory, true));
            }
        }, 200, 500);
        
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusBar.add(Box.createHorizontalStrut(5));
        statusBar.add(new JLabel("Route:"));
        statusBar.add(Box.createHorizontalStrut(10));
        statusBar.add(routeValues);    
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(mouseCoordinatesDisplay);
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(memoryConsumption);
        
        // FRAME LAYOUT
        setLayout(new BorderLayout());
        add(tabArea, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
        add(centralArea, BorderLayout.CENTER);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Listener.fireEvent(CLOSE_APPLICATION, null);
            }
        });
    
        startTask();
    }

    /**
     * Builds the routing tab component and all its sub components, in the process adding all event listeners.
     */
    private void createRoutingTab() {
        // POPUP        
        popupSearchCompletions = new JPopupMenu("Completion");
        popupSearchCompletions.setBackground(Color.WHITE);

        // COMPONENTS
        fieldStartNode = new JTextField();
        fieldStartNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popupSearchCompletions.setVisible(false);
                fieldStartNode.setBackground(Color.red);
                enterPressed = true;
                Listener.fireEvent(ADD_NAVNODE,
                        new TextNumberEvent(fieldStartNode.getText(), currentNavNodeIndex));
            }
        });
        fieldStartNode.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (enterPressed == false) {
                    if (isNavigationKey(e) && popupSearchCompletions.isVisible()) {
                        return;
                    }
                    currentNavNodeField = fieldStartNode;
                    currentNavNodeIndex = 0;
                    if (fieldStartNode.getText().length() > 1) {
                        navComp = fieldStartNode;
                        Listener.fireEvent(LIST_SEARCH_COMPLETIONS,
                                new TextNumberEvent(fieldStartNode.getText() + e.getKeyChar(), 0));
                    } else if (popupSearchCompletions.isVisible()) {
                        popupSearchCompletions.setVisible(false);
                    }
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
            public void actionPerformed(ActionEvent arg0) {
                popupSearchCompletions.setVisible(false);
                fieldEndNode.setBackground(Color.red);
                enterPressed = true;
                Listener.fireEvent(ADD_NAVNODE,
                        new TextNumberEvent(fieldEndNode.getText(), currentNavNodeIndex));
            }
        });
        fieldEndNode.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!enterPressed) {
                    if (isNavigationKey(e) && popupSearchCompletions.isVisible()) {
                        return;
                    }
                    currentNavNodeField = fieldEndNode;
                    if (countNavNodes() == 1) {
                        currentNavNodeIndex = 1;
                    } else {
                        currentNavNodeIndex = countNavNodes();
                    }
                    if (fieldEndNode.getText().length() > 1) {
                        navComp = fieldEndNode;
                        Listener.fireEvent(LIST_SEARCH_COMPLETIONS,
                                new TextNumberEvent(fieldEndNode.getText() + e.getKeyChar(), 2));
                    } else if (popupSearchCompletions.isVisible()) {
                        popupSearchCompletions.setVisible(false);
                    }
                } else {
                    enterPressed = false;
                }
            }
        });

        URL iconFile = ClassLoader.getSystemResource("icon_add.png");
        buttonAddNavNode = (iconFile != null) ? new JButton(new ImageIcon(iconFile)) : new JButton("+");
        buttonAddNavNode.setMaximumSize(new Dimension(50, 20));
        buttonAddNavNode.setAlignmentX(JButton.CENTER_ALIGNMENT);
        buttonAddNavNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if(fieldStartNode.getText().equals("") || fieldEndNode.getText().equals("")) {
                    nextNavPoint = true;
                } else {
                    nextNavPoint = false;
                }
                
                if(waypointArea.getComponentCount() > 0) {
                    for(int i = 0; i < waypointArea.getComponentCount() && !nextNavPoint; i++) {
                        if(((JTextField) ((JPanel) waypointArea.getComponent(i)).getComponent(0)).getText().equals("")) {
                            nextNavPoint = true;
                        } else {
                            nextNavPoint = false;
                        }
                    }
                }
                
                if(!nextNavPoint) {
                    addWaypointField("");
                }
                repaint();
            }
        });

        buttonOptimizeRoute = new JButton("Optimierung");
        buttonOptimizeRoute.setToolTipText("Knopf zur Optimierung der Zwischenhalte.");
//        buttonOptimizeRoute.setFont(new Font("Reihenfolge optimieren", Font.PLAIN, 50));
        buttonOptimizeRoute.setMaximumSize(new Dimension(200, 100));
        buttonOptimizeRoute.setAlignmentX(JButton.CENTER_ALIGNMENT);
        buttonOptimizeRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startTask();
                Listener.fireEvent(OPTIMIZE_ROUTE, null);            
            }
        });

        JPanel speedArea = new JPanel();
        speedArea.setLayout(new BorderLayout());
        JPanel speedInternalArea = new JPanel();
        fieldSpeed = new JSpinner(new SpinnerNumberModel(15, 1, null, 1));
        fieldSpeed.setPreferredSize(new Dimension(50, 20));
        fieldSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                Listener.fireEvent(SET_SPEED,
                        new NumberEvent(Integer.parseInt(fieldSpeed.getValue().toString())));
            }
        });
        speedArea.add(speedInternalArea, BorderLayout.SOUTH);
        speedArea.setBorder(BorderFactory.createTitledBorder("\u00D8 - Geschwindigkeit:"));
        speedInternalArea.add(fieldSpeed);
        speedInternalArea.add(new JLabel("km/h"));

        // AREAS
        routingSpeedTab = new JPanel();
        routingSpeedTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        routingSpeedTab.setLayout(new BoxLayout(routingSpeedTab, BoxLayout.Y_AXIS));
        
        routingTabTopArea = new JPanel();
        routingTabTopArea.setLayout(new BoxLayout(routingTabTopArea, BoxLayout.Y_AXIS));
        routingTabTopArea.setBorder(BorderFactory.createTitledBorder("Navigationspunkte"));

        routingButtonPanel = new JPanel(new BorderLayout());
        routingButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        routingButtonPanel.add(buttonAddNavNode, BorderLayout.WEST);
        routingButtonPanel.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
        routingButtonPanel.add(buttonOptimizeRoute, BorderLayout.EAST);
        
        routingTabTopArea.add(fieldStartNode);
        routingTabTopArea.add(Box.createVerticalStrut(8));
        routingTabTopArea.add(waypointArea);
        routingTabTopArea.add(Box.createVerticalStrut(3));
        routingTabTopArea.add(fieldEndNode);
        routingTabTopArea.add(Box.createVerticalStrut(10));
        routingTabTopArea.add(routingButtonPanel);
        routingTabTopArea.add(Box.createVerticalStrut(10));
        
        routingSpeedTab.add(routingTabTopArea);
        routingSpeedTab.add(Box.createVerticalStrut(5));
        routingSpeedTab.add(speedArea);

        routingTab = new JPanel(new BorderLayout());
        routingTab.add(routingSpeedTab, BorderLayout.NORTH);
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
        highwayMalusSlider = new JSlider(0, 4, 0);
        highwayMalusSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        highwayMalusSlider.setMajorTickSpacing(1);
        highwayMalusSlider.setPaintTicks(true);
        highwayMalusSlider.setSnapToTicks(true);
        highwayMalusSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Listener.fireEvent(SET_HIGHWAY_MALUS,
                        new NumberEvent(highwayMalusSlider.getValue()));
            }
        });

        JLabel captionReliefMalus = new JLabel("Reliefmalus");
        reliefMalusSlider = new JSlider(0, 4, 0);
        reliefMalusSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        reliefMalusSlider.setMajorTickSpacing(1);
        reliefMalusSlider.setPaintTicks(true);
        reliefMalusSlider.setSnapToTicks(true);
        reliefMalusSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Listener.fireEvent(SET_HEIGHT_MALUS,
                        new NumberEvent(reliefMalusSlider.getValue()));
            }
        });

        buttonImportOSM = new JButton("Importiere OSM-Karte");
        buttonImportOSM.setMaximumSize(new Dimension(200, 100));
        buttonImportOSM.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonImportOSM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUI.this.importMapFileChooser();
            }
        });

        buttonDeleteMap = new JButton("Entfernen");
        buttonDeleteMap.setMaximumSize(new Dimension(200, 100));
        buttonDeleteMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonDeleteMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (listChooseMap.getSelectedItem() != null) {
                    Listener.fireEvent(DELETE_IMPORTED_MAP, 
                            new TextEvent(listChooseMap.getSelectedItem().toString()));
                }
            }
        });

        buttonActivateMap = new JButton("Aktivieren");
        buttonActivateMap.setMaximumSize(new Dimension(200, 100));
        buttonActivateMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonActivateMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object item = listChooseMap.getSelectedItem();
                startTask();
                Listener.fireEvent(LOAD_MAP,
                        new TextEvent((item != null) ? item.toString() : ""));
            }
        });

        listChooseMap = new JComboBox();
        listChooseMap.setAlignmentX(JButton.CENTER_ALIGNMENT);
        listChooseMap.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent e) {
                Listener.fireEvent(LIST_IMPORTED_MAPS, null);
            }
            public void ancestorMoved(AncestorEvent e) {}
            public void ancestorRemoved(AncestorEvent e) {}           
        });

        // heightMapManagement = new JButton("Höhendaten - Verwaltung");
        // heightMapManagement.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // GUI.this.importHeightMapFileChooser();
        // }
        // });

        // AREAS
        JPanel mapTabTopArea = new JPanel();
        mapTabTopArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapTabTopArea.setLayout(new BoxLayout(mapTabTopArea, BoxLayout.Y_AXIS));

        JPanel captionHighwayMalusArea = new JPanel();
        captionHighwayMalusArea.add(captionHighwayMalus);
        JPanel captionReliefMalusArea = new JPanel();
        captionReliefMalusArea.add(captionReliefMalus);

        importOptions =  new JPanel();
        importOptions.setLayout(new BoxLayout(importOptions, BoxLayout.Y_AXIS));
        importOptions.setBorder(BorderFactory.createTitledBorder("Importoptionen"));
        
        importOptions.add(Box.createVerticalStrut(5));
        importOptions.add(captionHighwayMalusArea);
        importOptions.add(highwayMalusSlider);
        importOptions.add(captionReliefMalusArea);
        importOptions.add(reliefMalusSlider);
        importOptions.add(Box.createVerticalStrut(20));
        importOptions.add(buttonImportOSM);
        importOptions.add(Box.createVerticalStrut(5));
        
        availableMaps = new JPanel();
        availableMaps.setLayout(new BoxLayout(availableMaps, BoxLayout.Y_AXIS));
        availableMaps.setBorder(BorderFactory.createTitledBorder("Verfügbare Karten"));
        availableMaps.add(Box.createVerticalStrut(5));
        availableMaps.add(listChooseMap);
        availableMaps.add(Box.createVerticalStrut(10));
        availableMaps.add(buttonDeleteMap);
        availableMaps.add(Box.createVerticalStrut(5));
        availableMaps.add(buttonActivateMap);
        availableMaps.add(Box.createVerticalStrut(5));
        
        mapTabTopArea.add(importOptions);
        mapTabTopArea.add(Box.createVerticalStrut(5));
        mapTabTopArea.add(availableMaps);

        mapTab = new JPanel(new BorderLayout());
        mapTab.add(mapTabTopArea, BorderLayout.NORTH);
    }

    /**
     * Opens a dialog for map file selection. Fires an event.
     */
    private void importMapFileChooser() {
        JFileChooser dialog = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".osm", "osm");
        dialog.setFileFilter(filter);
        setCurrentDir(dialog);
        if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            startTask();
            Listener.fireEvent(IMPORT_OSM,
                    new TextEvent(dialog.getSelectedFile().getPath()));
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
        JFileChooser dialog = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".rte", "rte");
        dialog.setFileFilter(filter);
        setCurrentDir(dialog);
        int returnValue = dialog.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Listener.fireEvent(LOAD_ROUTE,
                    new TextEvent(dialog.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for route output file selection. Fires an event.
     */
    private void saveRouteFileChooser() {
        JFileChooser dialog = new JFileChooser();
        setCurrentDir(dialog);
        int returnValue = dialog.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if(dialog.getSelectedFile().getPath().endsWith(".rte")) {
                Listener.fireEvent(SAVE_ROUTE,
                        new TextEvent(dialog.getSelectedFile().getPath()));
            } else {
                Listener.fireEvent(SAVE_ROUTE,
                        new TextEvent(dialog.getSelectedFile().getPath() + ".rte"));
            }
        }
    }

    /**
     * Opens a dialog for kml output file selection. Fires an event.
     */
    private void exportRouteKMLFileChooser() {
        JFileChooser dialog = new JFileChooser();
        setCurrentDir(dialog);
        int returnValue = dialog.showDialog(this, "Exportieren");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if(dialog.getSelectedFile().getPath().endsWith(".kml")) {
                Listener.fireEvent(EXPORT_ROUTE,
                        new TextEvent(dialog.getSelectedFile().getPath()));
            } else {
                Listener.fireEvent(EXPORT_ROUTE,
                        new TextEvent(dialog.getSelectedFile().getPath() + ".kml"));
            }
        }
    }
    
    private void setCurrentDir(JFileChooser dialog) {
        File currentDir = null;
        try {
            currentDir = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.setCurrentDirectory(currentDir);
    }

    /**
     * Adds a new field to the navigation node list.
     * @param text the initial field content
     */
    private void addWaypointField(String text) {
        final JTextField waypointField = new JTextField(text);
        JButton buttonDeleteWaypoint = new JButton(deleteIcon);
        buttonDeleteWaypoint.setPreferredSize(new Dimension(20, 15));
        final JPanel row1 = new JPanel(new BorderLayout());
        final JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        row1.add(waypointField, BorderLayout.CENTER);
        if(waypointArea.getComponentCount() > 0) {
            JButton buttonSwitchWaypoints = new JButton(switchIcon);
            buttonSwitchWaypoints.setPreferredSize(new Dimension(20,15));
            row2.add(buttonSwitchWaypoints);
            final int pos1 = waypointArea.getComponentCount();
            buttonSwitchWaypoints.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Listener.fireEvent(SWITCH_NAV_NODES, new SwitchNavNodesEvent(pos1, pos1 + 1));
                }
            });
        }
        row2.add(buttonDeleteWaypoint);
        row1.add(row2, BorderLayout.EAST);
        row1.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        waypointArea.add(row1);
        routingTab.validate();

        final int pos = waypointArea.getComponentCount();

        waypointField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                waypointField.setBackground(Color.red);
                enterPressed = true;
                Listener.fireEvent(ADD_NAVNODE,
                        new TextNumberEvent(waypointField.getText(), currentNavNodeIndex));
                repaint();
            }
        });

        waypointField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!enterPressed) {
                    if (isNavigationKey(e) && popupSearchCompletions.isVisible()) {
                        return;
                    }
                    currentNavNodeIndex = pos;
                    if (waypointField.getText().length() > 1) {
                        currentNavNodeField = waypointField;
                        navComp = waypointField;
                        Listener.fireEvent(LIST_SEARCH_COMPLETIONS,
                                new TextNumberEvent(waypointField.getText() + e.getKeyChar(), 1));
                    } else if (popupSearchCompletions.isVisible()) {
                        popupSearchCompletions.setVisible(false);
                    }
                }
                enterPressed = false;
            }
        });

        buttonDeleteWaypoint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popupSearchCompletions.setVisible(false);
                waypointArea.remove(row1);
                if ((waypointField.getText().length() != 0) && (countNavNodes() > pos)) {
                    Listener.fireEvent(DELETE_NAVNODE, new NumberEvent(pos));
                }
                repaint();
            }
        });
    }
    
    private boolean isNavigationKey(KeyEvent e) {
        switch (e.getKeyChar()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_TAB:
                return true;
            default:
                return false;
        }
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
    public void setImportedMapsList(List<String> maps, int activeMapIndex) {
        listChooseMap.removeAllItems();
        for (String map : maps) {
            listChooseMap.addItem(map); // TODO keine doppelten Namen
        }
        listChooseMap.setSelectedIndex(activeMapIndex);
    }
   
    /**
     * Changes the geo coordinates view position and subsequently
     * updates the context and redraws the map.
     * @param center the new view center
     */
    public void setView(Coordinates center, int detailLevel) {
        map.setCenter(center);
        map.setZoomlevel(detailLevel);
        if (zoomSlider.getValue() == detailLevel) {
            map.render();
        } else {
            zoomSlider.setValue(detailLevel);
        }
    }

    /**
     * Sets the speed value display.
     * @param speed the new speed value
     */
    public void setSpeed(int speed) {
        fieldSpeed.setValue(speed);
    }

    /**
     * Returns the number of current navnodes.
     * @return the navigation node list size
     */
    public int countNavNodes() {
        return numNavNodes;
    }

    /**
     * Replaces the current navigation node list and updates
     * the fields display correspondingly.
     * @param navNodeList the new navnode list
     */
    public void updateNavNodes(List<Selection> navNodeList) {
        waypointArea.removeAll();
        fieldStartNode.setText("");
        fieldStartNode.setBackground(Color.WHITE);
        fieldEndNode.setText("");
        fieldEndNode.setBackground(Color.WHITE);
        numNavNodes = navNodeList.size();
        if (navNodeList.size() > 0) {
            fieldStartNode.setText(navNodeList.get(0).getName());
        }
        if (navNodeList.size() > 1) {
            fieldEndNode.setText(navNodeList.get(navNodeList.size() - 1).getName());
        }
        for (int i = 1; i < numNavNodes - 1; i++) {
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
    public void showSearchCompletions(List<String> completions, int iconNum) {
        Icon icon = null;
        switch (iconNum) {
            case 0: icon = selectStartIcon; break;
            case 1: icon = selectWaypointIcon; break;
            case 2: icon = selectDestinationIcon; break;
        }
        popupSearchCompletions.setVisible(false);
        if((completions != null) && (completions.size() > 0)){   
            popupSearchCompletions.removeAll();
            for (String completion : completions) {
                final JMenuItem item = new JMenuItem(completion, icon);
                popupSearchCompletions.add(item);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        navComp.setFocusable(false);
                        navComp.setText(item.getText());
                        Listener.fireEvent(ADD_NAVNODE,
                                new TextNumberEvent(item.getText(), currentNavNodeIndex));
                        navComp.setFocusable(true);
                    }
                });
            }
            popupSearchCompletions.show(currentNavNodeField, 0, currentNavNodeField.getHeight());
            navComp.grabFocus();
            repaint();
        }
    }

    /**
     * Displays the given route attributes in the status bar.
     * @param length the route length (in meters)
     * @param duration the route duration using the current average speed (in seconds)
     */
    public void showRouteValues(int length, int duration) {
        String output = String.format("%1$3.1f km / ", length / 1000f);
        routeValues.setText("(" + output + StringUtil.formatSeconds(duration, false) + ")");
    }
    
    /**
     * Displays the given coordinates in the status bar.
     * @param mousePosition the given mouse coordinates
     */
    public void showMouseCoordinates(Coordinates mousePosition) {
        mouseCoordinatesDisplay.setText(mousePosition.toString());
    }

    /**
     * Sets the map mode as indicated by the parameter. Note that a call of this method
     * doesn't change the active renderer, which should be done prior to calling this method.
     * @param render3D whether the map is to be shown in 3D mode
     */
    public void setMapMode(boolean render3D) {
        Coordinates center = map.getCenter();
        int zoomlevel = map.getZoomlevel();       
        centralArea.remove(map);
        map = (render3D) ? new Map3D(this) : new Map2D(this);
        centralArea.add(map, BorderLayout.CENTER);
        centralArea.validate();
        setView(center, zoomlevel);
    }
    
    private void startTask() {
        taskStartTime = System.currentTimeMillis();
    }
    
    private void setActive(boolean value) {
        /*if (active)
        try {
            throw new IllegalStateException();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }*/
        active = value;
        getGlassPane().setVisible(!value);
    }

    private void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
