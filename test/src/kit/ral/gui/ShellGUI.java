package kit.ral.gui;

import kit.ral.gui.GUI;


public class ShellGUI {
    static GUI gui;
    
    public static void main(String[] args) {
        gui = new GUI();
        gui.setBounds(0, 25, 500, 500);
        // System.out.println(gui.getWidth());
        // System.out.println(gui.getHeight());
        gui.setVisible(true);
        gui.addContents();
    }
    
}
