package kit.route.a.lot.gui.event;


public class TextPositionEvent extends GeneralEvent {

    private String text;
    private int position;

    public TextPositionEvent(String text, int position) {
        this.text = text;
        this.position = position;
    }
 
    public String getText() {
        return text;
    }
    
    public int getPosition() {
        return position;
    }
}