package com.yf.BollDemo;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 9/5 0005.
 */
public class OpenGLSurfaceView extends GLSurfaceView {

    private static final float SCALE = 180.0f / 360.0f;
    private OpenGLRender mRender;
    private Boll mBoll;

    public OpenGLSurfaceView(Context context) {
        this(context, null);
    }

    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        mRender = new OpenGLRender();
        setRenderer(mRender);
        requestFocus();
        setFocusableInTouchMode(true);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private PointF mPrePoint = new PointF(0, 0);
    private PointF mCurrentPoint = new PointF(0, 0);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurrentPoint.x = event.getX();
        mCurrentPoint.y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mBoll.setxAngle(SCALE * (mCurrentPoint.y - mPrePoint.y));
            mBoll.setyAngle(SCALE * (mCurrentPoint.x - mPrePoint.x));
            MatrixState.rotate(mBoll.getyAngle(), 0, 1, 0);// 绕y轴旋转
            MatrixState.rotate(mBoll.getxAngle(), 1, 0, 0);// 绕x轴旋转
        }
        mPrePoint.x = mCurrentPoint.x;
        mPrePoint.y = mCurrentPoint.y;
        return true;
    }

    private class OpenGLRender implements OpenGLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0, 0, 0, 0);

            mBoll = new Boll(OpenGLSurfaceView.this, 1f);

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 10);

            MatrixState.initMatrixArray();
            MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 1, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if (mBoll != null) {
                MatrixState.saveCurrentMatrixArray();

                MatrixState.saveCurrentMatrixArray();
                MatrixState.translate(-1.5f, 0, 0);
                mBoll.drawSelf();
                MatrixState.restoreCurrentMatrixArray();

                MatrixState.saveCurrentMatrixArray();
                MatrixState.translate(1.5f, 0, 0);
                mBoll.drawSelf();
                MatrixState.restoreCurrentMatrixArray();

                MatrixState.restoreCurrentMatrixArray();
            }
        }
    }

}
