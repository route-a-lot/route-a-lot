package kit.route.a.lot.common;

import kit.route.a.lot.gui.event.NumberEvent;


public class Progress {
    
    private double weight, progress = 0, lastProgress = 0;
    private Progress parent;
    
    public Progress() {
        this(null, 1);
        Listener.fireEvent(Listener.PROGRESS, new NumberEvent(0));
    }
    
    public Progress(Progress parent, double weight) {
        this.parent = parent;
        this.weight = weight;
    }
    
    public Progress sub(double weight) {
        return new Progress(this, weight);
    }
    
    synchronized public void add(double addProgress) {
        progress += addProgress;
        if (parent != null) {
            parent.add(addProgress * weight);
        } else if (progress - lastProgress >= 0.01){
            lastProgress = progress;
            Listener.fireEvent(Listener.PROGRESS,
                    new NumberEvent((int)(progress * 100)));
        }
    }
    
    /*@Override
    protected void finalize() {
        finish();
    }*/

    public void finish() {
        add(1 - progress); 
        progress = 1;
        if (parent == null) {
            Listener.fireEvent(Listener.PROGRESS, new NumberEvent(100));
        }
    }
    
}
