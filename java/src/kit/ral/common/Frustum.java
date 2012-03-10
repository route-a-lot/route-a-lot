package kit.ral.common;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;


public class Frustum {

    private final int RIGHT = 0, LEFT = 1, BOTTOM = 2, TOP = 3, BACK = 4, FRONT = 5;
    private final int A = 0, B = 1, C = 2, D = 3;

    float[][] planes = new float[6][4];

    public Frustum(GL gl) {
        float[] proj = new float[16];
        float[] modelview = new float[16];
        float[] clips = new float[16];
        // get projection matrix and modelview matrix from OpenGL
        FloatBuffer projMatrixBuffer = FloatBuffer.wrap(proj);
        FloatBuffer modelMatrixBuffer = FloatBuffer.wrap(modelview);
        gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, projMatrixBuffer);
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelMatrixBuffer);
        // retrieve clipping plane matrix = m * p
        clips[0] = modelview[0] * proj[0] + modelview[1] * proj[4] + modelview[2] * proj[8] + modelview[3] * proj[12];
        clips[1] = modelview[0] * proj[1] + modelview[1] * proj[5] + modelview[2] * proj[9] + modelview[3] * proj[13];
        clips[2] = modelview[0] * proj[2] + modelview[1] * proj[6] + modelview[2] * proj[10] + modelview[3] * proj[14];
        clips[3] = modelview[0] * proj[3] + modelview[1] * proj[7] + modelview[2] * proj[11] + modelview[3] * proj[15];
        clips[4] = modelview[4] * proj[0] + modelview[5] * proj[4] + modelview[6] * proj[8] + modelview[7] * proj[12];
        clips[5] = modelview[4] * proj[1] + modelview[5] * proj[5] + modelview[6] * proj[9] + modelview[7] * proj[13];
        clips[6] = modelview[4] * proj[2] + modelview[5] * proj[6] + modelview[6] * proj[10] + modelview[7] * proj[14];
        clips[7] = modelview[4] * proj[3] + modelview[5] * proj[7] + modelview[6] * proj[11] + modelview[7] * proj[15];
        clips[8] = modelview[8] * proj[0] + modelview[9] * proj[4] + modelview[10] * proj[8] + modelview[11] * proj[12];
        clips[9] = modelview[8] * proj[1] + modelview[9] * proj[5] + modelview[10] * proj[9] + modelview[11] * proj[13];
        clips[10] = modelview[8] * proj[2] + modelview[9] * proj[6] + modelview[10] * proj[10] + modelview[11] * proj[14];
        clips[11] = modelview[8] * proj[3] + modelview[9] * proj[7] + modelview[10] * proj[11] + modelview[11] * proj[15];
        clips[12] = modelview[12] * proj[0] + modelview[13] * proj[4] + modelview[14] * proj[8] + modelview[15] * proj[12];
        clips[13] = modelview[12] * proj[1] + modelview[13] * proj[5] + modelview[14] * proj[9] + modelview[15] * proj[13];
        clips[14] = modelview[12] * proj[2] + modelview[13] * proj[6] + modelview[14] * proj[10] + modelview[15] * proj[14];
        clips[15] = modelview[12] * proj[3] + modelview[13] * proj[7] + modelview[14] * proj[11] + modelview[15] * proj[15];
        // calculate frustum planes
        planes[RIGHT][A] = clips[3] - clips[0];
        planes[RIGHT][B] = clips[7] - clips[4];
        planes[RIGHT][C] = clips[11] - clips[8];
        planes[RIGHT][D] = clips[15] - clips[12];
        normalizePlane(RIGHT);

        planes[LEFT][A] = clips[3] + clips[0];
        planes[LEFT][B] = clips[7] + clips[4];
        planes[LEFT][C] = clips[11] + clips[8];
        planes[LEFT][D] = clips[15] + clips[12];
        normalizePlane(LEFT);

        planes[BOTTOM][A] = clips[3] + clips[1];
        planes[BOTTOM][B] = clips[7] + clips[5];
        planes[BOTTOM][C] = clips[11] + clips[9];
        planes[BOTTOM][D] = clips[15] + clips[13];
        normalizePlane(BOTTOM);

        planes[TOP][A] = clips[3] - clips[1];
        planes[TOP][B] = clips[7] - clips[5];
        planes[TOP][C] = clips[11] - clips[9];
        planes[TOP][D] = clips[15] - clips[13];
        normalizePlane(TOP);

        planes[BACK][A] = clips[3] - clips[2];
        planes[BACK][B] = clips[7] - clips[6];
        planes[BACK][C] = clips[11] - clips[10];
        planes[BACK][D] = clips[15] - clips[14];
        normalizePlane(BACK);

        planes[FRONT][A] = clips[3] + clips[2];
        planes[FRONT][B] = clips[7] + clips[6];
        planes[FRONT][C] = clips[11] + clips[10];
        planes[FRONT][D] = clips[15] + clips[14];
        normalizePlane(FRONT);
    }
    
    public boolean isPointWithin(float[] point) {
        for (int i = 0; i < 6; i++) {
            if (planes[i][A] * point[0]
              + planes[i][B] * point[1]
              + planes[i][C] * point[2]
              + planes[i][D] <= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isSphereWithin(float[] center, float radius) {
        for (int i = 0; i < 6; i++) {
            if (planes[i][A] * center[0] + planes[i][B] * center[1] + planes[i][C] * center[2]
                    + planes[i][D] <= -radius) {
                return false;
            }
        }
        return true;
    }

    public boolean isBoxWithin(float[] origin, float[] dimensions) {
        for (int i = 0; i < 6; i++) {
            if (planes[i][A] * (origin[0] - dimensions[0])
              + planes[i][B] * (origin[1] - dimensions[1])
              + planes[i][C] * (origin[2] - dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] + dimensions[0])
              + planes[i][B] * (origin[1] - dimensions[1])
              + planes[i][C] * (origin[2] - dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] - dimensions[0])
              + planes[i][B] * (origin[1] + dimensions[1])
              + planes[i][C] * (origin[2] - dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] + dimensions[0])
              + planes[i][B] * (origin[1] + dimensions[1])
              + planes[i][C] * (origin[2] - dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] - dimensions[0])
              + planes[i][B] * (origin[1] - dimensions[1])
              + planes[i][C] * (origin[2] + dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] + dimensions[0])
              + planes[i][B] * (origin[1] - dimensions[1])
              + planes[i][C] * (origin[2] + dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] - dimensions[0])
              + planes[i][B] * (origin[1] + dimensions[1])
              + planes[i][C] * (origin[2] + dimensions[2])
              + planes[i][D] > 0)
                continue;
            if (planes[i][A] * (origin[0] + dimensions[0])
              + planes[i][B] * (origin[1] + dimensions[1])
              + planes[i][C] * (origin[2] + dimensions[2])
              + planes[i][D] > 0)
                continue;
            return false;
        }
        return true;
    }

    private void normalizePlane(int planeID) {
        float magnitude =
                (float) Math.sqrt(planes[planeID][A] * planes[planeID][A] + planes[planeID][B]
                        * planes[planeID][B] + planes[planeID][C] * planes[planeID][C]);
        planes[planeID][A] = planes[planeID][A] / magnitude;
        planes[planeID][B] = planes[planeID][B] / magnitude;
        planes[planeID][C] = planes[planeID][C] / magnitude;
        planes[planeID][D] = planes[planeID][D] / magnitude;
    }
}
