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

import javax.swing.*;


public class GUI extends JFrame implements ActionListener {

    private static final Color colors[] = { Color.BLACK, Color.BLUE,
        Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
        Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK,
        Color.RED, Color.WHITE, Color.YELLOW };

    private JPanel map;
    private JPopupMenu navNodeMenu;
    private JTabbedPane tabbpane;
    private JButton importOSM;
    
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
        this.map = new JPanel();
        map.setPreferredSize(new Dimension(this.getSize()));
        map.setBackground(Color.BLUE);
        
        //Graphics2D g2 = (Graphics2D)map.getGraphics();
        //g2.drawImage(mapImage, map.getX(), map.getY(), null);



        this.navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(makeMenuItem("Start"));
        navNodeMenu.add(makeMenuItem("End"));

        Container contents = this.getContentPane();
        contents.setLayout(new BorderLayout());
        contents.add(map,BorderLayout.CENTER);

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
        
        tabbpane = new JTabbedPane();
        tabbpane.setPreferredSize(new Dimension(this.getWidth()*2/5, this.getHeight()));
        this.add(tabbpane,BorderLayout.WEST);
        
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
