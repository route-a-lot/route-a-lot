package kit.ral.common.util;

import java.util.Arrays;

public class Splines {

    private float[] x;
    private float[] y;
    private float[] deltaX;
    private float[] deltaY;
    private float[] mu;
    private float[] lambda;
    private float[] r;
    private float[] moments;
    private float[] a;
    private float[] b;
    private float[] c;
    private float[] d;
    private int dimensions;

        public Splines(float[] x, float[] y){
            this.x = x;
            this.y = y;
            this.dimensions = x.length;
            this.deltaX = new float[dimensions];
            this.deltaY = new float[dimensions];
            this.mu = new float[dimensions];
            this.lambda = new float[dimensions];
            this.r = new float[dimensions];
            this.a = new float[dimensions];
            this.b = new float[dimensions];
            this.c = new float[dimensions];
            this.d = new float[dimensions];
            createDeltas(x,y);
            createVectors(x,y);
            moments = calculateMoments();
            calculateCoeff();
        }


        private void createVectors(float[] x, float[] y){       
            lambda[0] = 0f;
            mu[dimensions - 1] = 0f;
            r[0] = 0f;
            r[dimensions - 1] = 0f; 
            
            for(int i = 1; i < dimensions -1; i++){
                lambda[i] = deltaX[i]/(deltaX[i-1] + deltaX[i]);
                mu[i] = 1 - lambda[i];
                r[i] = 6/(deltaX[i-1] + deltaX[i])
                        * (deltaY[i]/deltaX[i] - deltaY[i-1]/deltaX[i-1]);
            }       


        }

        private void createDeltas(float[] x, float[] y){
            for(int i = 0; i < dimensions-1; i++ ){
                deltaX[i] = x[i+1] - x[i];  
                deltaY[i] = y[i+1] - y[i];
            }
        }

        private float[] calculateMoments(){
            TDMatrix matrix = new TDMatrix(mu, lambda, r);
                return matrix.calculateX();
        }

        private void calculateCoeff(){
            for(int i = 0; i < dimensions-1; i++){
                d[i] = y[i];
                c[i] = (deltaY[i]/deltaX[i]) - 0.16666667f*deltaX[i]*(2f*moments[i] + moments[i+1]);
                b[i] = 0.5f * moments[i];
                a[i] = 0.16666667f*(moments[i+1] - moments[i])/deltaX[i];
            }
        
        }
        
        public void setX(float[] x){
            this.x = x;
        }
        
        public float[] getX(){
            return this.x;
        }
        
        public void setY(float[] y){
            this.y = y;
        }

        public float[] getY(){
            return y;
        }

        public float[] getDeltaX(){
            return deltaX;
        }
        
        public float[] getDeltaY(){
            return deltaY;
        }

        public float[] getMu(){
            return mu;
        }

        public float[] getLambda(){
            return lambda;
        }

        public float[] getR(){
            return r;
        }
        
        public float[] getMoments(){
            return this.moments;
        }

        public float[] getA(){
            return a;
        }

        public float[] getB(){
            return b;
        }

        public float[] getC(){
            return c;
        }

        public float[] getD(){
            return d;
        }
        /*--------------------------------------------------------------*/
        class TDMatrix{
        
        private float[] b;
        private float[] a;
        private float[] d;
        private float[] s;
        private float[] delta;
        private float[] sigma;  
        private int dimensions;

        public TDMatrix(float[]a, float[]b, float[]s ){
            this.a = a;
            this.b = b;
            this.s = s;
            this.dimensions = s.length;
            d = new float[dimensions];
            Arrays.fill(d, 2.0f);
            delta = new float[dimensions];
            sigma = new float[dimensions];
            createvectors();
        
        }

        private void createvectors(){
                float[] l = new float[dimensions]; 
                delta[0] = d[0];
                System.out.println("delta0"+delta[0]);
                sigma[0] = s[0];
                for(int i = 1; i < dimensions; i++){
                    l[i]= a[i]/delta[i-1];
                    System.out.println("l"+i+": "+l[i]);
                    delta[i] = d[i] - l[i]*b[i-1];
                     System.out.println("delta"+i+": "+delta[i]);
                    sigma[i] = s[i] - l[i]*sigma[i-1];
                    System.out.println("sigma"+i+": "+sigma[i]);

                }
        }

        public float[] calculateX(){
            float[] x = new float[dimensions];
            x[dimensions - 1] = (sigma[dimensions-1]/delta[dimensions -1]);
            for( int i = (dimensions - 2); i >= 0; i--){
                x[i] = (sigma[i] - b[i]* x[i+1])/delta[i];
            }
            return x;   
        }
        }//end TDMatrix
        /*--------------------------------------------------------------------*/
    
    
}
