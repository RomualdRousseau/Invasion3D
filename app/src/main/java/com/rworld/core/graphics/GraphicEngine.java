package com.rworld.core.graphics;

import android.opengl.GLSurfaceView;

import com.rworld.core.GameActivity;
import com.rworld.core.GameState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GraphicEngine implements GLSurfaceView.Renderer {

    public final static int WIDTH = 800;
    public final static int HEIGHT = 480;

    public final static int COMMAND_NONE = 0;
    public final static int COMMAND_INIT = 2;
    public final static int COMMAND_DISPOSE = 3;
    public final static int COMMAND_RENDER = 4;
    public final static int COMMAND_FINISH = 1;

    public GL10 gl = null;

    public abstract void enterView2D();

    public abstract void leaveView2D();

    public abstract void drawSurface2D(Surface surface, int x, int y, int witdh, int height);

    public abstract void drawText2D(Font font, int x, int y, float scale, String text);

    public GraphicEngine(GameActivity activity) {
        _activity = activity;
        _view = new GLSurfaceView(activity);
        _view.setRenderer(this);
        _view.setOnTouchListener(activity);
        activity.setContentView(_view);
    }

    public void onPause() {
        _view.onPause();
    }

    public void onResume() {
        _view.onResume();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glEnable(GL10.GL_POINT_SMOOTH);
        gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_FASTEST);

        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        gl.glCullFace(GL10.GL_BACK);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL10.GL_BLEND);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepthf(1.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        final float ratio;
        final float y;
        final float x;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        if (height == 0) {
            ratio = width;
        } else {
            ratio = (float) width / (float) height;
        }
        y = (float) (0.1f * Math.tan(45.0f * Math.PI / 360.0));
        x = ratio * y;
        gl.glFrustumf(-x, x, -y, y, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        _screenRatioX = (float) width / (float) GraphicEngine.WIDTH;
        _screenRatioY = (float) height / (float) GraphicEngine.HEIGHT;
    }

    public void onDrawFrame(GL10 gl) {
        try {
            if (!_finished) {
                synchronized (_lockGraphic) {
                    while (_command == GraphicEngine.COMMAND_NONE) {
                        _lockGraphic.wait();
                    }
                    switch (_command) {
                        case GraphicEngine.COMMAND_INIT:
                            this.gl = gl;
                            _commandState.init(_activity, this);
                            _commandState.ensureAssets(this);
                            this.gl = null;
                            break;
                        case GraphicEngine.COMMAND_DISPOSE:
                            this.gl = gl;
                            _commandState.dispose(this);
                            this.gl = null;
                            break;
                        case GraphicEngine.COMMAND_RENDER:
                            this.gl = gl;
                            _commandState.ensureAssets(this);
                            _commandState.render(this);
                            this.gl = null;
                            break;
                        case GraphicEngine.COMMAND_FINISH:
                            _finished = true;
                            break;
                    }
                    _commandState = null;
                    _command = GraphicEngine.COMMAND_NONE;
                    _lockGraphic.notify();
                }
            }
        } catch (InterruptedException ignore) {
        }
    }

    public void callGraphicCommand(int command, GameState currentState) {
        try {
            synchronized (_lockGraphic) {
                _command = command;
                _commandState = currentState;
                _lockGraphic.notify();
                while (_command > GraphicEngine.COMMAND_NONE) {
                    _lockGraphic.wait();
                }
            }
        } catch (InterruptedException ignore) {
        }
    }

    public void devicePointToVirtualPoint(float x, float y, int[] result) {
        result[0] = (int) (x / _screenRatioX);
        result[1] = (int) (y / _screenRatioY);
    }

    public void virtualRectToDeviceRect(int x, int y, int width, int height, float[] result) {
        result[0] = x * _screenRatioX;
        result[1] = (GraphicEngine.HEIGHT - (height + y)) * _screenRatioY;
        result[2] = width * _screenRatioX;
        result[3] = height * _screenRatioY;
    }

    public IMesh createMesh(MeshLoader loader) {
        return MeshFactory.createInstance(gl, loader, 1.0f, 1.0f, 1.0f);
    }

    public IMesh createMesh(MeshLoader loader, float scaleX, float scaleY, float scaleZ) {
        return MeshFactory.createInstance(gl, loader, scaleX, scaleY, scaleZ);
    }

    public IParticleGenerator createParticleGenerator(int maxParticules, float pointSize) {
        return ParticleGeneratorFactory.createInstance(gl, maxParticules, pointSize);
    }

    private final GameActivity _activity;
    private final GLSurfaceView _view;
    private float _screenRatioX;
    private float _screenRatioY;
    private static final Object _lockGraphic = new Object();
    private int _command = GraphicEngine.COMMAND_NONE;
    private GameState _commandState = null;
    private boolean _finished = false;
}
