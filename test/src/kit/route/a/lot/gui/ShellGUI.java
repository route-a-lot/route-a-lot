package kit.route.a.lot.gui;

import kit.route.a.lot.common.Listener;
import kit.route.a.lot.gui.GUI;


public class ShellGUI {
    static GUI gui;
    
    public static void main(String[] args) {
        gui = new GUI(new Listeners(Listener.TYPE_COUNT));
        gui.setBounds(0, 25, 500, 500);
        // System.out.println(gui.getWidth());
        // System.out.println(gui.getHeight());
        gui.setVisible(true);
        gui.addContents();
    }
    
}
