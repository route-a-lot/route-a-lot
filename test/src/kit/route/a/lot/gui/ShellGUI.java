package kit.route.a.lot.mGUI;

import kit.route.a.lot.gui.GUI;
import kit.route.a.lot.common.Coordinates;


public class ShellGUI {
    static GUI gui;
    
    public static void main(String[] args) {
        gui = new GUI(new Coordinates());
        gui.setBounds(0, 25, 500, 500);
        System.out.println(gui.getWidth());
        System.out.println(gui.getHeight());
        gui.setVisible(true);
        gui.addContents();
    }
    
}
