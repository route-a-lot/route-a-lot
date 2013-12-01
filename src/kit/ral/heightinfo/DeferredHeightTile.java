
/**
Copyright (c) 2012, Josua Stabenow
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

package kit.ral.heightinfo;

import java.io.File;

import kit.ral.common.Coordinates;
import kit.ral.io.SRTMLoader;

public class DeferredHeightTile extends RAMHeightTile {

    File source;
    boolean initialized = false;
    
    public DeferredHeightTile(File file, Coordinates origin) {
        super(SRTMLoader.WIDTH, SRTMLoader.HEIGHT, origin);
        source = file;
    }
    
    @Override
    public int getHeight(int x, int y) {
        if (!initialized) {
            loadFromSource();
            initialized = true;
        }
        return super.getHeight(x, y);
    }

    @Override
    public void setHeight(int x, int y, float height) {
        if (!initialized) {
            loadFromSource();
            initialized = true;
        }
        super.setHeight(x, y, height);
    }

    private void loadFromSource() {
        HeightTile tile = (new SRTMLoader()).loadHeightTile(source, origin);
        data = ((RAMHeightTile) tile).data;
    }
    

}
