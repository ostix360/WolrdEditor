package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.audio.*;
import fr.ostix.worldCreator.core.Input;
import fr.ostix.worldCreator.core.resourcesProcessor.GLRequestProcessor;
import fr.ostix.worldCreator.entity.camera.Camera;
import fr.ostix.worldCreator.graphics.MasterRenderer;
import fr.ostix.worldCreator.graphics.particles.MasterParticle;
import fr.ostix.worldCreator.toolBox.OpenGL.DisplayManager;
import fr.ostix.worldCreator.world.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.event.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.opengl.GL.createCapabilities;

public class GLCanvas extends AWTGLCanvas implements KeyListener,MouseWheelListener, MouseListener, MouseMotionListener {
    private final MasterRenderer render;
    private final Camera cam;
    private final World world;
    public static  float mouseDWheel = 0;

    public GLCanvas(GLData data, MasterRenderer render, Camera cam, World world) {
        super(data);
        this.world = world;
        this.render = render;
        this.cam = cam;
    }

    @Override
    public void initGL() {
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        createCapabilities();
        System.out.println("OpenGL version: " + effective.majorVersion + "." +
                effective.minorVersion + " (Profile: " + effective.profile + ")");
        DisplayManager.setHeight(this.getHeight());
        DisplayManager.setWidth(this.getWidth());
        render.init();

        MasterParticle.init(MasterRenderer.getProjectionMatrix());
    }

    @Override
    public void paintGL() {
        world.update();
        cam.move(mouseDWheel);
        mouseDWheel = 0;
        render.renderScene(cam);
        MasterParticle.update(cam);
        MasterParticle.render(cam);
        DisplayManager.setHeight(this.getHeight());
        DisplayManager.setWidth(this.getWidth());
        GL11.glViewport(0, 0, getWidth(), getHeight());
        swapBuffers();
        GLRequestProcessor.executeRequest();
    }

    @Override
    public void repaint() {
        if (SwingUtilities.isEventDispatchThread()) {
            render();
        } else {
            SwingUtilities.invokeLater(this::render);
        }
    }

    public void doDisposeCanvas() {
        AudioManager.cleanUp();
        render.cleanUp();
        MasterParticle.cleanUp();
        super.disposeCanvas();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseDWheel = e.getWheelRotation() * -e.getScrollAmount();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            Input.keysMouse[GLFW_MOUSE_BUTTON_1] = false;
//        }else{
//            Input.keysMouse[GLFW_MOUSE_BUTTON_1] = false;
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Input.keysMouse[GLFW_MOUSE_BUTTON_1] = true;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            Input.keysMouse[GLFW_MOUSE_BUTTON_2] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Input.keysMouse[GLFW_MOUSE_BUTTON_1] = false;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            Input.keysMouse[GLFW_MOUSE_BUTTON_2] = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Input.mouseX = e.getX();
        Input.mouseY = e.getY();
        Input.updateInput();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Input.mouseX = e.getX();
        Input.mouseY = e.getY();
        Input.updateInput();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Input.keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Input.keys[e.getKeyCode()] = false;
    }
}
