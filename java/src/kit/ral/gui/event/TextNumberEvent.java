package kit.route.a.lot.gui.event;


public class TextNumberEvent extends Event {

    private String text;
    private int number;

    public TextNumberEvent(String text, int number) {
        this.text = text;
        this.number = number;
    }
    
    public String getText() {
        return text;
    }
    
    public int getNumber() {
        return number;
    }
}