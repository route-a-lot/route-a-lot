package kit.route.a.lot.GUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


public class GUI extends JFrame implements ActionListener {

    private static final Color colors[] = { Color.BLACK, Color.BLUE,
        Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
        Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK,
        Color.RED, Color.WHITE, Color.YELLOW };

    private JPopupMenu navNodeMenu;
    private JTabbedPane tabbpane;
    
    private JButton importOSM;
    private JButton load;
    private JButton save;
    private JButton kmlExport;
    private JButton print;
    private JButton graphics;
    
    private JLabel activeRoute;
    private JLabel routeText;
    
    private JSlider scrolling;
    
    private JPanel mapContents;
    private JPanel map;
    private JPanel mapButtonPanel;
    
    private JPanel tab1;
    private JPanel tab2;
    private JPanel tab3;
    
    //private JCheckBox highwayMalus;
    
    private JSlider reliefmalus;
    
    private Component selectedComponent;

    private boolean mouseClicked;
    private int xpos;
    private int ypos;

    public GUI() {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

    }

    //private BufferedImage mapImage = testImage();
    
    public void addContents() {
        
        this.mapButtonPanel = new JPanel();
        mapButtonPanel.setPreferredSize(new Dimension(this.getWidth(), 80));
        
        this.map = new JPanel();
        map.setPreferredSize(new Dimension(this.getSize()));
        map.setBackground(Color.WHITE);
        map.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        
        //Graphics2D g2 = (Graphics2D)map.getGraphics();
        //g2.drawImage(mapImage, map.getX(), map.getY(), null);

        this.navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(makeMenuItem("Start"));
        navNodeMenu.add(makeMenuItem("End"));
        
        this.activeRoute = new JLabel();
        activeRoute.setText("Route:");
        
        mapContents = new JPanel();
        mapContents.setLayout(new BorderLayout());
        
        tabbpane = new JTabbedPane();
        tabbpane.setPreferredSize(new Dimension(this.getWidth()*2/5, this.getHeight()));
        tabbpane.setBackground(Color.LIGHT_GRAY);

        Container contents = this.getContentPane();
        contents.setLayout(new BorderLayout());
        
        contents.add(tabbpane,BorderLayout.WEST);
        contents.add(activeRoute,BorderLayout.SOUTH);
        contents.add(mapContents, BorderLayout.CENTER);
        mapContents.add(mapButtonPanel,BorderLayout.NORTH);
        mapContents.add(map,BorderLayout.CENTER);
        
        routeText = new JLabel();
        routeText.setText("Route:");
        
        load = new JButton();
        load.setText("Laden");
        
        save = new JButton();
        save.setText("Speichern");
        
        kmlExport = new JButton();
        kmlExport.setText("KML-Export");
        
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
        scrolling.setValue(10);
        scrolling.setMajorTickSpacing(1);
        scrolling.setMinorTickSpacing(1);
        scrolling.setLabelTable(allScrollingTicks);
        scrolling.setPaintTicks(true);
        scrolling.setPaintLabels(true);
        scrolling.setSnapToTicks(true);
        
        mapButtonPanel.add(routeText);
        mapButtonPanel.add(load);
        mapButtonPanel.add(save);
        mapButtonPanel.add(kmlExport);
        mapButtonPanel.add(print);
        mapButtonPanel.add(graphics);
        mapButtonPanel.add(scrolling);

        map.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent me) {
                checkPopup(me);

            }

            @Override
            public void mousePressed(MouseEvent me) {
                checkPopup(me);
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
                /*
                xpos = me.getX();
                ypos = me.getY();

                if(xpos > map.getX() && xpos < map.getX()+map.getWidth()
                        && ypos > map.getY() && ypos < map.getY()+map.getHeight()
                        && me.getButton() == 3)
                {
                    mouseClicked = true;
                }else {
                    mouseClicked = false;
                }
                repaint();
                 */
                checkPopup(me);
            }
            private void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    selectedComponent = e.getComponent();
                    navNodeMenu.show(e.getComponent(), e.getX(), e.getY());

                    xpos = e.getX();
                    ypos = e.getY();

                    if(xpos > map.getX() && xpos < map.getX()+map.getWidth()
                            && ypos > map.getY() && ypos < map.getY()+map.getHeight())
                    {
                        mouseClicked = true;
                    }else {
                        mouseClicked = false;
                    }
                }
            }
        });
        MouseWheelListener listener = new MouseWheelListener() {
            int colorCounter;

            int up = 1;

            int down = 2;


            public void mouseWheelMoved(MouseWheelEvent e) {
                int direction;
                int count = e.getWheelRotation();
                if(count < 0) {
                    direction = up;
                } else {
                    direction = down;
                }
                changeBackground(direction);
            }

            private void changeBackground(int direction) {

                if (direction == up) {
                    colorCounter++;
                } else {
                    colorCounter--;
                }

                if (colorCounter == colors.length) {
                    colorCounter = 0;
                } else if (colorCounter < 0) {
                    colorCounter = colors.length - 1;
                }
                map.setBackground(colors[colorCounter]);
            }
        };

        map.addMouseWheelListener(listener);
        
        tab1 = new JPanel();
        
        tab2 = new JPanel();
        
        tab3 = new JPanel();
        
        tabbpane.addTab("Planen", null, tab1, "1");
        //tabbpane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbpane.addTab("Beschreibung", null, tab2, "2");
        //tabbpane.setMnemonicAt(2, KeyEvent.VK_2);
        tabbpane.addTab("Karten", null, tab3, "3");
        //tabbpane.setMnemonicAt(3, KeyEvent.VK_2);
        
        tab3.setLayout(new FlowLayout());
        
        
        
        importOSM = new JButton("Importiere OSM-Karte");
        tab3.add(importOSM);
        
        this.pack();
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.printf("("+xpos+","+ypos+")\n");
        repaint();
    }
    
    /*
    private BufferedImage testImage(){
        BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 150; x++) {
            for(int y = 0; y < 150; y++) {
                image.setRGB(x, y, 100);
            }
        }
        return image;
    }*/

}
