package kit.route.a.lot.common;

import kit.route.a.lot.gui.event.FloatEvent;


public class Progress {
    
    private double weight, progress = 0, lastProgress = 0;
    private long lastTime = 0;
    private Progress parent;
    
    public Progress() {
        this(null, 1);
        Listener.fireEvent(Listener.PROGRESS, new FloatEvent(0));
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
        if (Thread.interrupted()) {
            throw new IllegalStateException();
        }
        if (parent != null) {
            parent.addProgress(addProgress * weight);
        } else {
            long time = System.currentTimeMillis();           
            if ((progress - lastProgress >= 0.01) || time - lastTime >= 1000){
                lastProgress = progress;
                lastTime = time;
                Listener.fireEvent(Listener.PROGRESS,
                        new FloatEvent((float) progress * 100f));
            }
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
            Listener.fireEvent(Listener.PROGRESS, new FloatEvent(100));
        }
    }
    
}
