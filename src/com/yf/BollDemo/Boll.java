package com.yf.BollDemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/5 0005.
 */
public class Boll {

    private static final int PER_ANGLE = 10;

    public Boll(OpenGLSurfaceView openGLSurfaceView, int radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("传入的半径不大于0");
        }
        initVertexData(radius);
        initSharder(openGLSurfaceView);
    }


    private void initVertexData(int radius) {
        List<Float> vertexArray = new ArrayList<Float>();

        float wNewRadius = 0;
        float wPreRadius = 0;
        float wNewY = -radius;
        float wPreY = -radius;
        float[] currentVertexArray = new float[6];//暂存当前所在经度的两个顶点的坐标，较少计算量
        float[] nextVertexArray = new float[6];//暂存下一个经度的两个顶点的坐标

        for (int wAngle = -90; wAngle < 90; wAngle += PER_ANGLE) {// 纬度
            wPreRadius = wNewRadius;
            wNewRadius = (float) (radius * Math.cos(Math.toRadians(wAngle)));
            wPreY = wNewY;
            wNewY = (float) (radius * Math.sin(Math.toRadians(wAngle)));

            for (int jAngle = 0; jAngle < 360; jAngle += PER_ANGLE) {//经度
                //每次计算下一个的经度下的两个点的位置
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

    }

    private void initSharder(OpenGLSurfaceView openGLSurfaceView) {

    }


    public void drawSelf() {

    }


}
