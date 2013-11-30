
/**
Copyright (c) 2012, Jan Jacob, Yvonne Braun, Josua Stabenow
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

package kit.ral.io;

import java.io.File;

import kit.ral.common.Coordinates;


public class SRTMTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SRTMLoaderProto loader = new SRTMLoaderProto();
        File datei = new File("test/resources/Daten");
        loader.load(datei);
        //// System.out.println("test: "+test);    



/*--------------------------Test1-------------------------------------------*/
        Coordinates coordinates = new Coordinates(49.0030802f,8.3672265f);
        System.out.println("Höhe an der Stelle "+ coordinates + ": " +
                                loader.getHeightMap().getHeight(coordinates) );
/*----------------------------Test2------------------------------------------*/
        coordinates = new Coordinates(49.0055222f,8.4034558f);
        System.out.println("Höhe an der Stelle "+ coordinates + ": " +
                               loader.getHeightMap().getHeight(coordinates) );
/*-----------------------------Test3------------------------------------------*/
        coordinates = new Coordinates(48.9980814f,8.44314f);
        System.out.println("Höhe an der Stelle "+ coordinates + ": " +
                               loader.getHeightMap().getHeight(coordinates) );





    }

}
