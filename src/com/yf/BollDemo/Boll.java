package com.yf.BollDemo;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/5 0005.
 */
public class Boll {
    private static final String TAG = Boll.class.getName();

    private static final int PER_ANGLE = 10;
    private int mVertexCount;
    private FloatBuffer mVertexFloatBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mRadiusHandle;
    private float mRadius;
    private float xAngle;
    private float yAngle;

    public Boll(OpenGLSurfaceView openGLSurfaceView, float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("传入的半径不大于0");
        }
        this.mRadius = radius;
        initVertexData();
        initShader(openGLSurfaceView);
    }

    private void initVertexData() {
        List<Float> vertexArray = new ArrayList<Float>();

        float wNewRadius = 0;
        float wPreRadius = 0;
        float wNewY = -this.mRadius;
        float wPreY = -this.mRadius;
        float[] currentVertexArray = new float[6];// 暂存当前所在经度的两个顶点的坐标，较少计算量
        float[] nextVertexArray = new float[6];// 暂存下一个经度的两个顶点的坐标

        for (int wAngle = -90; wAngle <= 90; wAngle += PER_ANGLE) {// 纬度
            wPreRadius = wNewRadius;
            wNewRadius = (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)));
            wPreY = wNewY;
            wNewY = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle)));

            for (int jAngle = 0; jAngle < 360; jAngle += PER_ANGLE) {// 经度
                // 每次计算下一个的经度下的两个点的位置
                if (jAngle == 0) {
                    currentVertexArray[0] = (float) (wNewRadius * Math.cos(Math.toRadians(jAngle)));
                    currentVertexArray[1] = wNewY;
                    currentVertexArray[2] = (float) (wNewRadius * Math.sin(Math.toRadians(jAngle)));

                    currentVertexArray[3] = (float) (wPreRadius * Math.cos(Math.toRadians(jAngle)));
                    currentVertexArray[4] = wPreY;
                    currentVertexArray[5] = (float) (wPreRadius * Math.sin(Math.toRadians(jAngle)));
                }

                nextVertexArray[0] = (float) (wNewRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                nextVertexArray[1] = wNewY;
                nextVertexArray[2] = (float) (wNewRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));

                nextVertexArray[3] = (float) (wPreRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                nextVertexArray[4] = wPreY;
                nextVertexArray[5] = (float) (wPreRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));

                // 第一个三角形
                for (int i = 0; i < 6; i++) {
                    vertexArray.add(currentVertexArray[i]);
                }
                vertexArray.add(nextVertexArray[3]);
                vertexArray.add(nextVertexArray[4]);
                vertexArray.add(nextVertexArray[5]);

                // 第二个三角形
                vertexArray.add(nextVertexArray[3]);
                vertexArray.add(nextVertexArray[4]);
                vertexArray.add(nextVertexArray[5]);

                vertexArray.add(nextVertexArray[0]);
                vertexArray.add(nextVertexArray[1]);
                vertexArray.add(nextVertexArray[2]);

                vertexArray.add(currentVertexArray[0]);
                vertexArray.add(currentVertexArray[1]);
                vertexArray.add(currentVertexArray[2]);

                // 将下一个经度上的两个点加入到当前
                for (int i = 0; i < 6; i++) {
                    currentVertexArray[i] = nextVertexArray[i];
                }
            }
        }
        // for (int wAngle = -90; wAngle <= 90; wAngle += PER_ANGLE) {// 纬度
        // for (int jAngle = 0; jAngle < 360; jAngle += PER_ANGLE) {// 经度
        // float x0 = (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)) * Math.cos(Math.toRadians(jAngle)));
        // float y0 = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle)));
        // float z0 = (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)) * Math.sin(Math.toRadians(jAngle)));
        //
        // float x1 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)) * Math.cos(Math.toRadians(jAngle
        // + PER_ANGLE)));
        // float y1 = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle)));
        // float z1 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)) * Math.sin(Math.toRadians(jAngle
        // + PER_ANGLE)));
        //
        // float x2 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle + PER_ANGLE)) * Math.cos(Math.toRadians(jAngle
        // + PER_ANGLE)));
        // float y2 = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle + PER_ANGLE)));
        // float z2 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle + PER_ANGLE)) * Math.sin(Math.toRadians(jAngle
        // + PER_ANGLE)));
        //
        // float x3 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle + PER_ANGLE)) * Math.cos(Math.toRadians(jAngle)));
        // float y3 = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle + PER_ANGLE)));
        // float z3 =
        // (float) (this.mRadius * Math.cos(Math.toRadians(wAngle + PER_ANGLE)) * Math.sin(Math.toRadians(jAngle)));
        //
        // vertexArray.add(x0);
        // vertexArray.add(y0);
        // vertexArray.add(z0);
        //
        // vertexArray.add(x1);
        // vertexArray.add(y1);
        // vertexArray.add(z1);
        //
        // vertexArray.add(x3);
        // vertexArray.add(y3);
        // vertexArray.add(z3);
        //
        // vertexArray.add(x1);
        // vertexArray.add(y1);
        // vertexArray.add(z1);
        //
        // vertexArray.add(x2);
        // vertexArray.add(y2);
        // vertexArray.add(z2);
        //
        // vertexArray.add(x3);
        // vertexArray.add(y3);
        // vertexArray.add(z3);
        // }
        // }

        float[] vertexArr = new float[vertexArray.size()];
        for (int i = 0; i < vertexArray.size(); i++) {
            vertexArr[i] = vertexArray.get(i);
        }

        mVertexCount = vertexArray.size() / 3;

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexArray.size() * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexFloatBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexFloatBuffer.put(vertexArr);
        mVertexFloatBuffer.position(0);

    }

    private void initShader(OpenGLSurfaceView openGLSurfaceView) {
        String vertexShaderSource = ShaderUtil.readSourceFromAssetsFile("vertex.c", openGLSurfaceView.getResources());
        if (vertexShaderSource == null) {
            throw new NullPointerException("读取顶点着色器源码失败");
        }

        String fragmentShaderSource =
            ShaderUtil.readSourceFromAssetsFile("fragment.c", openGLSurfaceView.getResources());
        if (fragmentShaderSource == null) {
            throw new NullPointerException("读取片元着色器源码失败");
        }

        mProgram = ShaderUtil.createProgram(vertexShaderSource, fragmentShaderSource);
        if (mProgram == 0) {
            throw new IllegalStateException("创建着色器程序失败");
        }

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "mMVPMatrix");
        mRadiusHandle = GLES20.glGetUniformLocation(mProgram, "uRadius");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrixArray(), 0);
        GLES20.glUniform1f(mRadiusHandle, this.mRadius);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexFloatBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
    }

    public float getxAngle() {
        return xAngle;
    }

    public void setxAngle(float xAngle) {
        this.xAngle = xAngle;
    }

    public float getyAngle() {
        return yAngle;
    }

    public void setyAngle(float yAngle) {
        this.yAngle = yAngle;
    }
}
