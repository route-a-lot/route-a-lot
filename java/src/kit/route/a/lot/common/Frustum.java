package kit.route.a.lot.common;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;


public class Frustum {

    private final int RIGHT = 0;
    private final int LEFT = 1;
    private final int BOTTOM = 2;
    private final int TOP = 3;
    private final int BACK = 4;
    private final int FRONT = 5;
    private final int A = 0;
    private final int B = 1;
    private final int C = 2;
    private final int D = 3;

    float[][] frustum = new float[6][4];

    public Frustum(GL gl) {
        float[] projM = new float[16];
        float[] modM = new float[16];
        float[] clip = new float[16];
        FloatBuffer pm = FloatBuffer.wrap(projM);
        FloatBuffer mm = FloatBuffer.wrap(projM);
        gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, pm);
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, mm);
        // Projektions & Modelviewmatrix multipilizeren um die "clipping-Planes" zu bekommen
        clip[0] = modM[0] * projM[0] + modM[1] * projM[4] + modM[2] * projM[8] + modM[3] * projM[12];
        clip[1] = modM[0] * projM[1] + modM[1] * projM[5] + modM[2] * projM[9] + modM[3] * projM[13];
        clip[2] = modM[0] * projM[2] + modM[1] * projM[6] + modM[2] * projM[10] + modM[3] * projM[14];
        clip[3] = modM[0] * projM[3] + modM[1] * projM[7] + modM[2] * projM[11] + modM[3] * projM[15];
        clip[4] = modM[4] * projM[0] + modM[5] * projM[4] + modM[6] * projM[8] + modM[7] * projM[12];
        clip[5] = modM[4] * projM[1] + modM[5] * projM[5] + modM[6] * projM[9] + modM[7] * projM[13];
        clip[6] = modM[4] * projM[2] + modM[5] * projM[6] + modM[6] * projM[10] + modM[7] * projM[14];
        clip[7] = modM[4] * projM[3] + modM[5] * projM[7] + modM[6] * projM[11] + modM[7] * projM[15];
        clip[8] = modM[8] * projM[0] + modM[9] * projM[4] + modM[10] * projM[8] + modM[11] * projM[12];
        clip[9] = modM[8] * projM[1] + modM[9] * projM[5] + modM[10] * projM[9] + modM[11] * projM[13];
        clip[10] = modM[8] * projM[2] + modM[9] * projM[6] + modM[10] * projM[10] + modM[11] * projM[14];
        clip[11] = modM[8] * projM[3] + modM[9] * projM[7] + modM[10] * projM[11] + modM[11] * projM[15];
        clip[12] = modM[12] * projM[0] + modM[13] * projM[4] + modM[14] * projM[8] + modM[15] * projM[12];
        clip[13] = modM[12] * projM[1] + modM[13] * projM[5] + modM[14] * projM[9] + modM[15] * projM[13];
        clip[14] = modM[12] * projM[2] + modM[13] * projM[6] + modM[14] * projM[10] + modM[15] * projM[14];
        clip[15] = modM[12] * projM[3] + modM[13] * projM[7] + modM[14] * projM[11] + modM[15] * projM[15];
        // Die Seiten des frustums aus der oben berechneten clippingmatrix extrahieren
        frustum[RIGHT][A] = clip[3] - clip[0];
        frustum[RIGHT][B] = clip[7] - clip[4];
        frustum[RIGHT][C] = clip[11] - clip[8];
        frustum[RIGHT][D] = clip[15] - clip[12];
        normalizePlane(RIGHT);

        frustum[LEFT][A] = clip[3] + clip[0];
        frustum[LEFT][B] = clip[7] + clip[4];
        frustum[LEFT][C] = clip[11] + clip[8];
        frustum[LEFT][D] = clip[15] + clip[12];
        normalizePlane(LEFT);

        frustum[BOTTOM][A] = clip[3] + clip[1];
        frustum[BOTTOM][B] = clip[7] + clip[5];
        frustum[BOTTOM][C] = clip[11] + clip[9];
        frustum[BOTTOM][D] = clip[15] + clip[13];
        normalizePlane(BOTTOM);

        frustum[TOP][A] = clip[3] - clip[1];
        frustum[TOP][B] = clip[7] - clip[5];
        frustum[TOP][C] = clip[11] - clip[9];
        frustum[TOP][D] = clip[15] - clip[13];
        normalizePlane(TOP);

        frustum[BACK][A] = clip[3] - clip[2];
        frustum[BACK][B] = clip[7] - clip[6];
        frustum[BACK][C] = clip[11] - clip[10];
        frustum[BACK][D] = clip[15] - clip[14];
        normalizePlane(BACK);

        frustum[FRONT][A] = clip[3] + clip[2];
        frustum[FRONT][B] = clip[7] + clip[6];
        frustum[FRONT][C] = clip[11] + clip[10];
        frustum[FRONT][D] = clip[15] + clip[14];
        normalizePlane(FRONT);
    }
    
    public boolean isPointWithin(float[] point) {
        for (int i = 0; i < 6; i++) {
            if (frustum[i][A] * point[0] + frustum[i][B] * point[1] + frustum[i][C] * point[2]
                    + frustum[i][D] <= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isSphereWithin(float[] center, float radius) {
        for (int i = 0; i < 6; i++) {
            if (frustum[i][A] * center[0] + frustum[i][B] * center[1] + frustum[i][C] * center[2]
                    + frustum[i][D] <= -radius) {
                return false;
            }
        }
        return true;
    }

    public boolean isBoxWithin(float[] origin, float[] dimensions) {
        for (int i = 0; i < 6; i++) {
            if (frustum[i][A] * (origin[0] - dimensions[0]) + frustum[i][B] * (origin[1] - dimensions[1])
                    + frustum[i][C] * (origin[2] - dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] + dimensions[0]) + frustum[i][B] * (origin[1] - dimensions[1])
                    + frustum[i][C] * (origin[2] - dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] - dimensions[0]) + frustum[i][B] * (origin[1] + dimensions[1])
                    + frustum[i][C] * (origin[2] - dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] + dimensions[0]) + frustum[i][B] * (origin[1] + dimensions[1])
                    + frustum[i][C] * (origin[2] - dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] - dimensions[0]) + frustum[i][B] * (origin[1] - dimensions[1])
                    + frustum[i][C] * (origin[2] + dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] + dimensions[0]) + frustum[i][B] * (origin[1] - dimensions[1])
                    + frustum[i][C] * (origin[2] + dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] - dimensions[0]) + frustum[i][B] * (origin[1] + dimensions[1])
                    + frustum[i][C] * (origin[2] + dimensions[2]) + frustum[i][D] > 0)
                continue;
            if (frustum[i][A] * (origin[0] + dimensions[0]) + frustum[i][B] * (origin[1] + dimensions[1])
                    + frustum[i][C] * (origin[2] + dimensions[2]) + frustum[i][D] > 0)
                continue;
            return false;
        }
        return true;
    }

    private void normalizePlane(int planeID) {
        float magnitude =
                (float) Math.sqrt(frustum[planeID][A] * frustum[planeID][A] + frustum[planeID][B]
                        * frustum[planeID][B] + frustum[planeID][C] * frustum[planeID][C]);
        frustum[planeID][A] = frustum[planeID][A] / magnitude;
        frustum[planeID][B] = frustum[planeID][B] / magnitude;
        frustum[planeID][C] = frustum[planeID][C] / magnitude;
        frustum[planeID][D] = frustum[planeID][D] / magnitude;
    }
}
