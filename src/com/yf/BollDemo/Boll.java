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
    private FloatBuffer mNormalFloatBuffer;
    // private FloatBuffer m
    private int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mMatrixHandle;
    private int mRadiusHandle;
    private int mNormalHandle;
    private int mLightHandle;
    private int mCameraHandle;
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
        float[] wrapTemp = null;

        for (int wAngle = -90; wAngle <= 90; wAngle += PER_ANGLE) {// 纬度
            wPreRadius = wNewRadius;
            wNewRadius = (float) (this.mRadius * Math.cos(Math.toRadians(wAngle)));
            wPreY = wNewY;
            wNewY = (float) (this.mRadius * Math.sin(Math.toRadians(wAngle)));

            for (int jAngle = 0; jAngle < 360; jAngle += PER_ANGLE) {// 经度
                // // 每次计算下一个的经度下的两个点的位置
                // if (jAngle == 0) {
                // currentVertexArray[0] = (float) (wNewRadius * Math.cos(Math.toRadians(0)));
                // currentVertexArray[1] = wNewY;
                // currentVertexArray[2] = (float) (wNewRadius * Math.sin(Math.toRadians(0)));
                //
                // currentVertexArray[3] = (float) (wPreRadius * Math.cos(Math.toRadians(0)));
                // currentVertexArray[4] = wPreY;
                // currentVertexArray[5] = (float) (wPreRadius * Math.sin(Math.toRadians(0)));
                // }
                //
                // nextVertexArray[0] = (float) (wNewRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                // nextVertexArray[1] = wNewY;
                // nextVertexArray[2] = (float) (wNewRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));
                //
                // nextVertexArray[3] = (float) (wPreRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                // nextVertexArray[4] = wPreY;
                // nextVertexArray[5] = (float) (wPreRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));

                // // 第一个三角形
                // for (int i = 0; i < 6; i++) {
                // vertexArray.add(currentVertexArray[i]);
                // }
                // vertexArray.add(nextVertexArray[3]);
                // vertexArray.add(nextVertexArray[4]);
                // vertexArray.add(nextVertexArray[5]);
                //
                // // 第二个三角形
                // vertexArray.add(nextVertexArray[3]);
                // vertexArray.add(nextVertexArray[4]);
                // vertexArray.add(nextVertexArray[5]);
                //
                // vertexArray.add(nextVertexArray[0]);
                // vertexArray.add(nextVertexArray[1]);
                // vertexArray.add(nextVertexArray[2]);
                //
                // vertexArray.add(currentVertexArray[0]);
                // vertexArray.add(currentVertexArray[1]);
                // vertexArray.add(currentVertexArray[2]);
                /**
                 * 非常注意一个问题：当我们以x轴当作经度起始的话，我们的每个四边形的current为右边的边上的两个顶点，next为左边的两个顶点
                 * 所以上面的顶点顺序是错误的，最后绘制出来的是背面，我们应该绘制正面的
                 */
                // 每次计算下一个的经度下的两个点的位置
                if (jAngle == 0) {
                    currentVertexArray[0] = (float) (wPreRadius * Math.cos(Math.toRadians(0)));
                    currentVertexArray[1] = wPreY;
                    currentVertexArray[2] = (float) (wPreRadius * Math.sin(Math.toRadians(0)));

                    currentVertexArray[3] = (float) (wNewRadius * Math.cos(Math.toRadians(0)));
                    currentVertexArray[4] = wNewY;
                    currentVertexArray[5] = (float) (wNewRadius * Math.sin(Math.toRadians(0)));
                }

                nextVertexArray[0] = (float) (wPreRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                nextVertexArray[1] = wPreY;
                nextVertexArray[2] = (float) (wPreRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));

                nextVertexArray[3] = (float) (wNewRadius * Math.cos(Math.toRadians(jAngle + PER_ANGLE)));
                nextVertexArray[4] = wNewY;
                nextVertexArray[5] = (float) (wNewRadius * Math.sin(Math.toRadians(jAngle + PER_ANGLE)));
                // 第一个三角形
                for (int i = 0; i < 6; i++) {
                    vertexArray.add(currentVertexArray[i]);
                }
                vertexArray.add(nextVertexArray[0]);
                vertexArray.add(nextVertexArray[1]);
                vertexArray.add(nextVertexArray[2]);

                // 第二个三角形
                vertexArray.add(nextVertexArray[0]);
                vertexArray.add(nextVertexArray[1]);
                vertexArray.add(nextVertexArray[2]);

                vertexArray.add(currentVertexArray[3]);
                vertexArray.add(currentVertexArray[4]);
                vertexArray.add(currentVertexArray[5]);

                vertexArray.add(nextVertexArray[3]);
                vertexArray.add(nextVertexArray[4]);
                vertexArray.add(nextVertexArray[5]);

                // 将下一个经度上的两个点加入到当前
                wrapTemp = currentVertexArray;
                currentVertexArray = nextVertexArray;
                nextVertexArray = wrapTemp;
                // for (int i = 0; i < 6; i++) {
                // currentVertexArray[i] = nextVertexArray[i];
                // }
            }
        }

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

        // 因为球的每个顶点在该顶点的的法向量上，所以可以用顶点坐标表示其法向量
        ByteBuffer normalByteBuffer = ByteBuffer.allocateDirect(vertexArray.size() * 4);
        normalByteBuffer.order(ByteOrder.nativeOrder());
        mNormalFloatBuffer = normalByteBuffer.asFloatBuffer();
        mNormalFloatBuffer.put(vertexArr);
        mNormalFloatBuffer.position(0);

        MatrixState.setLightPosition(0, 0, 0);
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
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMatrix");
        mRadiusHandle = GLES20.glGetUniformLocation(mProgram, "uRadius");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        mLightHandle = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
        mCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCameraPosition");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrixArray(), 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, MatrixState.getCurrentMatrixArray(), 0);
        GLES20.glUniform1f(mRadiusHandle, this.mRadius);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexFloatBuffer);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mNormalFloatBuffer);
        GLES20.glUniform3fv(mLightHandle, 1, MatrixState.getLightPositionArray(), 0);
        GLES20.glUniform3fv(mCameraHandle, 1, MatrixState.getCameraPositionArray(), 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

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
