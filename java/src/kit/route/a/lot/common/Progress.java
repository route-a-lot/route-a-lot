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
    
    public Progress createSubProgress(double weight) {
        return new Progress(this, weight);
    }
    
    synchronized public void addProgress(double addProgress) {
        progress += addProgress;
        if (parent != null) {
            parent.addProgress(addProgress * weight);
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
        addProgress(1 - progress); 
        progress = 1;
        if (parent == null) {
            Listener.fireEvent(Listener.PROGRESS, new NumberEvent(100));
        }
    }
    
}
