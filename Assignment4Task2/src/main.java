
import jrtr.*;
import jrtr.Light.Type;
import jrtr.glrenderer.*;
import jrtr.gldeferredrenderer.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class main
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static Shader phongShader;
	static Material material1;
	static Material material2;
	static Material material3;
	static SimpleSceneManager sceneManager;
	static Shape shape1;
	static Shape shape2;
	static Shape shape3;
	static float currentstep, basicstep;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. Here we construct
	 * a simple 3D scene and start a timer task to generate an animation.
	 */ 
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
					
			// Make a scene manager and add the object
			sceneManager = new SimpleSceneManager();
			
			VertexData vertexData;
			ObjReader objReader = new ObjReader();
			
			try {
				vertexData = objReader.read("../obj/teapot.obj", 2f, renderContext);
				shape1 = new Shape(vertexData);
				sceneManager.addShape(shape1);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
						
			shape2 = new Cube(renderContext,1f,1f,1f);
			sceneManager.addShape(shape2);
			
			try {
				vertexData = objReader.read("../obj/dragon_smooth.obj", 2f, renderContext);
				shape3 = new Shape(vertexData);
				sceneManager.addShape(shape3);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// add light sources, in world coordinates!! [ST]
			// x points right, y points up, z points outside
			Light l = new Light();
			
			l.diffuse = new Vector3f(0f,0f,1f);
			l.direction = new Vector3f(0f,0f,1f);
			//sceneManager.addLight(l);
			l = new Light();
			l.diffuse = new Vector3f(1f,0f,0f);
			l.direction = new Vector3f(0f,0f,-1f);
			//sceneManager.addLight(l);
			l = new Light();
			l.diffuse = new Vector3f(1f,1f,1f);
			l.position = new Vector3f(0f,0f,2f);
			l.type = Type.POINT;
			sceneManager.addLight(l);
			l = new Light();
			l.diffuse = new Vector3f(1f,1f,1f);
			l.position = new Vector3f(-3f,-3f,-1f);
			l.type = Type.POINT;
			sceneManager.addLight(l);
			l = new Light();
			l.diffuse = new Vector3f(1f,0f,0f);
			l.specular = new Vector3f(0f,0f,1f);
			l.position = new Vector3f(3f,3f,1.5f);
			l.type = Type.POINT;
			sceneManager.addLight(l);
			
			
			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
			
			// Load some more shaders
		    phongShader = renderContext.makeShader();
		    try {
		    	phongShader.load("../jrtr/shaders/phong.vert", "../jrtr/shaders/phong.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }

		    // Make a material that can be used for shading
			material1 = new Material();
			material1.specular = new Vector3f(0.2f, 0.2f, 0.2f);
			material1.shininess = 0.5f;
			material1.shader = phongShader;
			material1.diffuseMap = renderContext.makeTexture();
			try {
				material1.diffuseMap.load("../textures/plant.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}
			
			material2 = new Material();
			material2.shader = phongShader;
			material2.diffuse = new Vector3f(0.5f, 0.5f, 0.5f);
			material2.specular = new Vector3f(0.5f, 0.5f, 0.5f);
			material2.shininess = 0.5f;
			material2.diffuseMap = renderContext.makeTexture();
			try {
				material2.diffuseMap.load("../textures/wood.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}

			material3 = new Material();
			material3.shader = phongShader;
			material3.diffuse = new Vector3f(0.5f, 0.5f, 0.5f);
			material3.specular = new Vector3f(0.5f, 0.5f, 0.5f);
			material3.shininess = 1f;
			material3.diffuseMap = renderContext.makeTexture();
			try {
				material3.diffuseMap.load("../textures/wood.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}
			
			// Register a timer task
		    Timer timer = new Timer();
		    basicstep = 0.01f;
		    currentstep = basicstep;
		    timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}
	}

	
	
	/**
	 * A timer task that generates an animation. This task triggers
	 * the redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		public void run()
		{
			// Update transformation by rotating with angle "currentstep"
    		Matrix4f t = shape1.getTransformation();
    		shape1.setTransformation(t);
    		
    		t = new Matrix4f(shape2.getTransformation());
    		t.setTranslation(new Vector3f(3,3,0));
    		shape2.setTransformation(t);
    		

    		t = new Matrix4f(shape3.getTransformation());
    		t.setTranslation(new Vector3f(-3,-3,-2));
    		shape3.setTransformation(t);
			
    		// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
		}
	}

	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener
	{
    	public void mousePressed(MouseEvent e) {}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
	}
	
	/**
	 * A key listener for the main window. Use this to process key events.
	 * Currently this provides the following controls:
	 * 's': stop animation
	 * 'p': play animation
	 * '+': accelerate rotation
	 * '-': slow down rotation
	 * 'd': default shader
	 * 'n': shader using surface normals
	 * 'm': use a material for shading
	 */
	public static class SimpleKeyListener implements KeyListener
	{
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyChar())
			{
				case 's': {
					// Stop animation
					currentstep = 0;
					break;
				}
				case 'p': {
					// Resume animation
					currentstep = basicstep;
					break;
				}
				case '+': {
					// Accelerate roation
					currentstep += basicstep;
					break;
				}
				case '-': {
					// Slow down rotation
					currentstep -= basicstep;
					break;
				}
				/*
				case 'n': {
					// Remove material from shape, and set "normal" shader
					shape1.setMaterial(null);
					renderContext.useShader(normalShader);
					break;
				}
				case 'd': {
					// Remove material from shape, and set "default" shader
					shape1.setMaterial(null);
					renderContext.useDefaultShader();
					break;
				}
				*/
				case 'm': {
					// Set a material for more complex shading of the shape
					if(shape1.getMaterial() == null) {
						shape1.setMaterial(material1);
						shape2.setMaterial(material2);
						shape3.setMaterial(material3);
					} else
					{
						shape1.setMaterial(null);
						shape2.setMaterial(null);
						shape3.setMaterial(null);
						renderContext.useDefaultShader();
					}
					break;
				}
			}
			
			// Trigger redrawing
			renderPanel.getCanvas().repaint();
		}
		
		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
        {
        }

	}
	
	/**
	 * The main function opens a 3D rendering window, implemented by the class
	 * {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then called backed 
	 * for initialization automatically. It then constructs a simple 3D scene, 
	 * and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse and key listener
	    renderPanel.getCanvas().addMouseListener(new SimpleMouseListener());
	    renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);   	    	    
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
