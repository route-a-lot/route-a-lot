package kit.route.a.lot.GUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;


public class GUI extends JFrame implements ActionListener {

    private static final Color colors[] = { Color.BLACK, Color.BLUE,
        Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
        Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK,
        Color.RED, Color.WHITE, Color.YELLOW };

    private JPanel map;
    private JPopupMenu navNodeMenu;
    private Component selectedComponent;

    private boolean mouseClicked;
    private int xpos;
    private int ypos;

    public GUI() {
        super("Route-A-Lot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.addMouseListener(new MouseListener() {

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
                if(Math.abs(count) > 0) {
                    direction = up;
                } else {
                    direction = down;
                }
                changeBackground(direction);
            }

            private void changeBackground(int direction) {
                map.setBackground(colors[colorCounter]);

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
            }
        };

        this.addMouseWheelListener(listener);
    }

    public void addContents() {
        this.map = new JPanel();
        map.setPreferredSize(new Dimension(this.getSize()));
        map.setBackground(Color.BLUE);



        this.navNodeMenu = new JPopupMenu("NavNodes");
        navNodeMenu.add(makeMenuItem("Start"));
        navNodeMenu.add(makeMenuItem("End"));

        Container contents = this.getContentPane();
        contents.setLayout(new BorderLayout());
        contents.add(map,BorderLayout.CENTER);

        this.pack();
    }

    public void paint(Graphics g) 
    {

        if(mouseClicked){
            map.setBackground(Color.BLUE);
            g.drawString("("+xpos+","+ypos+")",xpos,ypos);
        }

    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}
