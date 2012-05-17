
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Daniel Krauß, Josua Stabenow
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

package kit.ral.gui;

import java.util.List;

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.RouteDescription;


public class GUIHandler {

    private GUI gui;
    
    public GUIHandler() {
        gui = new GUI();
        gui.setBounds(0, 25, 600, 600);
        gui.addContents();
    }
    
    public void updateGUI() {
        gui.updateGUI();
    }

    public void updateMapList(List<String> maps, int activeMapIndex) {
        gui.setImportedMapsList(maps, activeMapIndex);
    }
    
    public void updateNavNodes(List<Selection> navPointsList) {
        gui.updateNavNodes(navPointsList);
    }
    
    public void passElementType(int element){
        gui.passElementType(element);
    }
    
    public void setView(Coordinates center, int detailLevel) {
        gui.setView(center, detailLevel);
    }
    
    public void setSpeed(int speed) {   
        gui.setSpeed(speed);
    }
    
    public void setRouteDecription(RouteDescription description) {
        //TODO
    }
    
    public void passDescription(POIDescription description) {
        gui.passDescription(description);
    }
      
    public void showRouteValues(int length, int duration) {
        gui.showRouteValues(length, duration);
    }
    
    public void showSearchCompletion(List<String> completion, int iconNum) {
        gui.showSearchCompletions(completion, iconNum);
    }
    
    public void setMapMode(boolean render3D) {
        gui.setMapMode(render3D);
    }
}
