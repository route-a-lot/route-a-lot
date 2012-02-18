package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
    private boolean active = false; // indicates whether main thread has finished startup

    public static final int FREEMAPSPACE = 0;
    public static final int POI = 1;
    public static final int FAVORITE = 2;
    public static final int NAVNODE = 3;

    private JFileChooser importFC;
    private JFileChooser loadRoute;
    private JFileChooser saveRoute;
    private JFileChooser exportRoute;
    // private JFileChooser importHeightMap;

    private JComboBox listChooseMap;

    protected JLabel routeValues;

    // private JList textRoute;
    // private JScrollPane textRouteScrollPane;

    private JTextField fieldStartNode;
    private JTextField fieldEndNode;

    private JSlider highwayMalusSlider;
    private JSlider reliefMalusSlider;
    private JSlider zoomSlider;

    private JPanel centralArea;
    private JPanel routingTab;
    private JPanel routingTabTopArea;
    private JPanel waypointArea;
    private JPanel mapTab;

    private JPopupMenu popupTextualCompletion;

    private JSpinner fieldSpeed;

    private List<JMenuItem> textualProposals = new ArrayList<JMenuItem>();
    private List<String> importedMaps = new ArrayList<String>();

    private Map map;

    private List<Selection> navPointsList;
    private Listeners listeners;

    private int popUpX;
    private int popUpY;
    private int popUpFieldPosition;

    private boolean enterPressed = false;

    /**
     * Creates the GUI window, using the given view center coordinates.
     * 
     * @param listeners
     * @param view
     *            center geo coordinates (possibly mercator projected)
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
        JButton buttonSwitchGraphics = new JButton("2D/3D");
        buttonSwitchGraphics.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                while (!active); // postbone execution until after startup
                Coordinates center = map.getCenter();
                int zoomlevel = map.getZoomlevel();
                Listeners.fireEvent(listeners.switchMapMode, new GeneralEvent());
                centralArea.remove(map);
                map = (map instanceof Map2D) ? new Map3D(map.gui) : new Map2D(map.gui);
                centralArea.add(map, BorderLayout.CENTER);
                centralArea.validate();
                map.setZoomlevel(zoomlevel);
                setView(center);
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
        listeners.viewChanged.add(new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent event) {
                zoomSlider.setValue(Util.clip(map.getZoomlevel(), 0, 9));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));
        buttonPanel.add(new JLabel("Route:"));
        buttonPanel.add(buttonLoadRoute);
        buttonPanel.add(buttonSaveRoute);
        buttonPanel.add(buttonExportKML);
        buttonPanel.add(buttonSwitchGraphics);
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
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusBar.add(new JLabel("Route:"));
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(routeValues);
        statusBar.add(Box.createHorizontalGlue());

        // FRAME LAYOUT
        setLayout(new BorderLayout());
        add(tabArea, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
        add(centralArea, BorderLayout.CENTER);
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Listeners.fireEvent(listeners.close, new GeneralEvent());
            }
        });
    }

    /**
     * Builds the routing tab component and all its sub components, in the process adding all event listeners.
     */
    private void createRoutingTab() {
        // POPUP
        popupTextualCompletion = new JPopupMenu("Completion");
        popupTextualCompletion.setBackground(Color.WHITE);

        // COMPONENTS
        JLabel caption = new JLabel("<html><u>Wegpunkte:</u></html>");
        caption.setAlignmentX(JLabel.RIGHT_ALIGNMENT); // should be left alignment, which somehow doesn't work


        fieldStartNode = new JTextField();
        fieldStartNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldStartNode.setBackground(Color.red);
                enterPressed = true;
                Listeners.fireEvent(listeners.getNavNodeDescription,
                        new TextPositionEvent(fieldStartNode.getText(), 0));
            }
        });
        fieldStartNode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (enterPressed == false) {
                    popUpX = fieldStartNode.getX();
                    popUpY = fieldStartNode.getY() + fieldStartNode.getHeight();
                    popUpFieldPosition = 0;
                    Listeners.fireEvent(listeners.autoCompletion,
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
                Listeners.fireEvent(listeners.getNavNodeDescription,
                        new TextPositionEvent(fieldEndNode.getText(), navPointsList.size() - 1));
            }
        });
        fieldEndNode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!enterPressed) {
                    popUpX = fieldEndNode.getX();
                    popUpY = fieldEndNode.getY() + fieldEndNode.getHeight();
                    popUpFieldPosition = navPointsList.size() - 1;
                    Listeners.fireEvent(listeners.autoCompletion,
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
                Listeners.fireEvent(listeners.optimizeRoute, new GeneralEvent());
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
                Listeners.fireEvent(listeners.speed,
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
                Listeners.fireEvent(listeners.highwayMalus,
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
                Listeners.fireEvent(listeners.heightMalus,
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
                Listeners.fireEvent(listeners.deleteMap,
                        new TextEvent(listChooseMap.getSelectedItem().toString()));
            }
        });

        JButton buttonActivateMap = new JButton("Aktivieren");
        buttonActivateMap.setMaximumSize(new Dimension(200, 100));
        buttonActivateMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonActivateMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Listeners.fireEvent(listeners.loadMap,
                        new TextEvent(listChooseMap.getSelectedItem().toString()));
            }
        });

        listChooseMap = new JComboBox();
        listChooseMap.setAlignmentX(JButton.CENTER_ALIGNMENT);

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
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.importOsmFile, new TextEvent(importFC.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for heightmap file selection. Fires a RAL event.
     */
    /*
     * private void importHeightMapFileChooser() { importHeightMap = new JFileChooser();
     * FileNameExtensionFilter filter = new FileNameExtensionFilter(".hgt", "hgt");
     * importHeightMap.setFileFilter(filter); int returnValue = importHeightMap.showOpenDialog(this);
     * if(returnValue == JFileChooser.APPROVE_OPTION) { Listeners.fireEvent(listeners.importHeightMap, new
     * TextEvent(importHeightMap.getSelectedFile().getPath())); } }
     */

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
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.loadRoute, new TextEvent(loadRoute.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for route output file selection. Fires a RAL event.
     */
    private void saveRouteFileChooser() {
        saveRoute = new JFileChooser();
        int returnValue = saveRoute.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners.fireEvent(listeners.saveRoute, new TextEvent(saveRoute.getSelectedFile().getPath()));
        }
    }

    /**
     * Opens a dialog for kml output file selection. Fires a RAL event.
     */
    private void exportRouteKMLFileChooser() {
        exportRoute = new JFileChooser();
        int returnValue = exportRoute.showDialog(this, "Exportieren");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Listeners
                    .fireEvent(listeners.exportRoute, new TextEvent(exportRoute.getSelectedFile().getPath()));
        }
    }

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

        final int pos = waypointArea.getComponentCount() / 2;

        waypointField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                waypointField.setBackground(Color.red);
                enterPressed = true;
                Listeners.fireEvent(listeners.getNavNodeDescription,
                        new TextPositionEvent(waypointField.getText(), pos + 1));
                repaint();
            }
        });

        waypointField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (!enterPressed) {
                    popUpX = waypointField.getX();
                    popUpY = waypointField.getY() + waypointField.getHeight();
                    popUpFieldPosition = pos + 1;
                    Listeners.fireEvent(listeners.autoCompletion, new TextEvent(waypointField.getText()));
                }
                enterPressed = false;
            }
        });

        buttonDeleteWaypoint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                waypointArea.remove(row);
                if ((waypointField.getText().length() != 0) && (navPointsList.size() > pos + 1)) {
                    Listeners.fireEvent(listeners.deleteNavPoint, new NumberEvent(pos + 1));
                }
                repaint();
            }
        });
    }

    private void addMenuItem(String name) {
        final JMenuItem item = new JMenuItem(name);
        textualProposals.add(item);
        popupTextualCompletion.add(item);
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < textualProposals.size(); i++) {
                    if (textualProposals.get(i) == item) {
                        Listeners.fireEvent(listeners.addTextualNavPoint, new TextPositionEvent(
                                textualProposals.get(i).getText(), popUpFieldPosition));
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
     * 
     * @param maps
     *            the new entries
     */
    public void updateMapChooser(ArrayList<String> maps) {
        for (int i = 0; i < importedMaps.size(); i++) {
            listChooseMap.removeItem(listChooseMap.getItemAt(i));
        }
        importedMaps = new ArrayList<String>(maps);
        for (String map : maps) {
            listChooseMap.addItem(map); // TODO keine doppelten NAmen
        }
    }

    public void leftClickPOIFav() {

    }

    /**
     * Changes the geo coordinates view position and subsequently updates the context and redraws the map.
     * 
     * @param center
     *            the new view center
     */
    public void setView(Coordinates center) {
        map.setCenter(center);
        map.calculateView();
        map.repaint();
    }

    public void setSpeed(int speed) {
        fieldSpeed.setValue(speed);
    }

    public void setZoomlevel(int zoomlevel) {
        zoomSlider.setValue(zoomlevel);
    }


    public Listeners getListeners() {
        return listeners;
    }

    public List<Selection> getNavPointsList() {
        return navPointsList;
    }


    public void updateNavNodes(List<Selection> newNavPointsList) {
        waypointArea.removeAll();
        fieldStartNode.setText("");
        fieldStartNode.setBackground(Color.WHITE);
        fieldEndNode.setText("");
        fieldEndNode.setBackground(Color.WHITE);
        navPointsList = new ArrayList<Selection>(newNavPointsList);
        if (navPointsList.size() > 0) {
            fieldStartNode.setText(navPointsList.get(0).getName());
        }
        if (navPointsList.size() > 1) {
            fieldEndNode.setText(navPointsList.get(navPointsList.size() - 1).getName());
        }
        for (int i = 1; i < newNavPointsList.size() - 1; i++) {
            addWaypointField(navPointsList.get(i).getName());
        }
        repaint();
    }

    public void popUpTrigger(int itemType) {
        map.triggerPopup(itemType);
    }

    public void passDescription(POIDescription description) {
        map.passDescription(description);
    }


    public void showNavNodeDescription(String description, int navNodeIndex) {
        ((JTextField) waypointArea.getComponent((navNodeIndex - 1) * 2)).setText(description);
    }

    public void showSearchCompletion(List<String> completion) {
        while (textualProposals.size() != 0) {
            int i = textualProposals.size() - 1;
            popupTextualCompletion.remove(textualProposals.get(i));
            popupTextualCompletion.remove(textualProposals.get(i));
            textualProposals.remove(i);
            textualProposals.remove(i);
            i--;
        }
        for (int i = 0; i < completion.size(); i++) {
            addMenuItem(completion.get(i));
        }
        popupTextualCompletion.show(routingTabTopArea, popUpX, popUpY);
        repaint();
    }

    public void showRouteValues(int duration, int length) {
        int hours = duration / 3600;
        int minutes = (duration - hours * 3600) / 60;
        int seconds = duration - (hours * 3600) - (minutes * 60);
        float kilometers = length / 1000f;
        if (hours != 0) {
            routeValues.setText("(" + kilometers + "km, " + hours + "st " + minutes + "min" + ")");
        } else if (minutes != 0) {
            routeValues.setText("(" + kilometers + "km, " + minutes + "min" + ")");
        } else {
            routeValues.setText("(" + kilometers + "km, " + seconds + "sek" + ")");
        }
        repaint();
    }


    public void setActive(boolean active) {
        this.active = active;
    }
}
