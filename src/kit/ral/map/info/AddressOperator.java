
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Josua Stabenow
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

package kit.ral.map.info;

import kit.ral.common.Selection;
import kit.ral.map.MapElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public interface AddressOperator {

    /**
     * Operation suggestCompletions
     * 
     * @param expression
     *            -
     * @return List<String>
     */
    List<String> getCompletions(String expression);

    /**
     * Operation select
     * 
     * @param address
     *            -
     * @return Selection
     */
    Selection select(String address);

    /**
     * Operation add
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    void add(MapElement element);
    
    /**
     * 
     */
    void compactify();
    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void loadFromInput(DataInput input) throws IOException;

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void saveToOutput(DataOutput output) throws IOException;

}
