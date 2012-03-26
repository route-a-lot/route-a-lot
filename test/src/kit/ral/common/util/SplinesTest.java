package kit.ral.common.util;
/*
 * i        |0      |1      |2      |3
 * -------------------------------------
 * xi       |0      |1      |3      |4
 * yi       |-1     |2      |12     |19
 * dxi      |1      |2      |1      |-
 * dyi      |3      |10     |7      |-
 * lambdai  |0      |2/3    |1/3    |-
 * sigmai   |-      |1/3    |2/3    |0
 * ri       |0      |4      |4      |0
 * momentsi |0      |3/2    |3/2    |0
 * di       |-1     |2      |12     |
 * ci       |11/4   |7/2    |1      |3/2
 * bi       |0      |3/4    |3/4    |-
 * ai       |1/4    |0      |-1/4   |-
 * */

public class SplinesTest {

    
    public static void main(String[] args) {
        float[] x = {0f,1f,3f,4f};
        float[] y = {-1f, 2f, 12f, 19f};
        Splines spline = new Splines(x, y);
        
        //lambda,mu un r
        float[] mu = spline.getMu();
        float[] lambda = spline.getLambda();
        float[] r = spline.getR();
        for(int i = 0; i < x.length; i++){
                System.out.println("mu"+i+": "+mu[i]);
                System.out.println("lambda"+i+": "+lambda[i]);
                System.out.println("r"+i+": "+r[i]);    
        }
        
        //Koeffizienten
        float[] deltaX = spline.getDeltaX();
        float[] deltaY = spline.getDeltaY();
        float[] moments = spline.getMoments();
        float[] a = spline.getA();
        float[] b = spline.getB();
        float[] c = spline.getC();
        float[] d = spline.getD();
        for(int i = 0; i < x.length; i++){
                System.out.println("a"+i+": "+a[i]);
                System.out.println("b"+i+": "+b[i]);
                System.out.println("c"+i+": "+c[i]);
                System.out.println("d"+i+": "+d[i]);
                System.out.println("deltaX"+i+": "+ deltaX[i]);
                System.out.println("deltaY"+i+": "+ deltaY[i]);
                System.out.println("moments"+i+": "+ moments[i]);
        }


    }

}
