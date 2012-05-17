
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.common;

import kit.ral.common.event.FloatEvent;
import kit.ral.common.event.Listener;


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
