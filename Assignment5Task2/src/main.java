/*
 * Assignment 5, Computer Graphics
 * Severin Tobler, 14.11.2015
 * 
 */

import jrtr.*;
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
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;
	static GraphSceneManager sceneManager;
	static Shape shape;
	static float currentstep, basicstep;
	static boolean forwards;
	static boolean backwards;
	static boolean yawP;
	static boolean yawN;
	static boolean rollP;
	static boolean rollN;
	static boolean pitchP;
	static boolean pitchN;

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

			// Make a scene manager
			sceneManager = new GraphSceneManager();

			// Build scene
			TransformGroup sceneGraph = sceneManager.getSceneGraph();
			
			
			VertexData vertexData;
			ObjReader objReader = new ObjReader();
			try {
				vertexData = objReader.read("/Users/Severin/Uni Bern/3. Semester/Computer Graphics/Eclipse Workspace/obj/Teapot.obj", 2f, renderContext);
			} catch (IOException e1) {
				vertexData = renderContext.makeVertexData(0);
				e1.printStackTrace();
			}
			shape = new Shape(vertexData);
			ShapeNode pod = new ShapeNode(shape);
			
			Matrix4f T = new Matrix4f();
			T = new Matrix4f(1,0,0,-5, 0,0,1,0, 0,-1,0,0, 0,0,0,1);
			TransformGroup grid = new TransformGroup(T);

			for(int i=0; i<100; i++){
				for(int j=0; j<100 ; j++){
					T = new Matrix4f(1,0,0,4*i, 0,0,-1,4*j, 0,1,0,0, 0,0,0,1);
					TransformGroup element = new TransformGroup(T);
					element.addChild(pod);
					grid.addChild(element);
				}
			}
			
			sceneGraph.addChild(grid);


			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);

			// Load some more shaders
			normalShader = renderContext.makeShader();
			try {
				normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
			} catch(Exception e) {
				System.out.print("Problem with shader:\n");
				System.out.print(e.getMessage());
			}

			diffuseShader = renderContext.makeShader();
			try {
				diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
			} catch(Exception e) {
				System.out.print("Problem with shader:\n");
				System.out.print(e.getMessage());
			}

			// Make a material that can be used for shading
			material = new Material();
			material.shader = diffuseShader;
			material.diffuseMap = renderContext.makeTexture();
			try {
				material.diffuseMap.load("../textures/plant.jpg");
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
			
			// camera
			camera();
			// Trigger redrawing of the render window
			renderPanel.getCanvas().repaint(); 
		}

		private void camera(){
			float d_angle = 0.01f;
			float d_distance = 0.1f;

			float trans = 0, alpha = 0, beta = 0, gamma = 0;

			if(forwards)
				trans = trans + d_distance;
			if(backwards)
				trans = trans - d_distance;
			if(yawP)
				beta = beta + d_angle;
			if(yawN)
				beta = beta - d_angle;
			if(pitchP)
				alpha = alpha + d_angle;
			if(pitchN)
				alpha = alpha - d_angle;
			if(rollP)
				gamma = gamma + d_angle;
			if(rollN)
				gamma = gamma - d_angle;

			if(!(trans == 0 && alpha == 0 && beta == 0 && gamma == 0)){
				Matrix4f rot = new Matrix4f();
				Vector4f x = new Vector4f();
				Vector4f y = new Vector4f();
				Vector4f z = new Vector4f();
				Vector4f t = new Vector4f();
				Vector4f dt = new Vector4f(0,0,-trans,0);
				Matrix4f c = sceneManager.getCamera().getCameraMatrix();
				c.invert();
				c.getColumn(0, x);
				c.getColumn(1, y);
				c.getColumn(2, z);

				rot.setIdentity();
				rot.setRotation(new AxisAngle4f(x.x, x.y, x.z, alpha));
				rot.transform(y);
				rot.transform(z);

				rot.setIdentity();
				rot.setRotation(new AxisAngle4f(y.x, y.y, y.z, beta));
				rot.transform(x);
				rot.transform(z);

				rot.setIdentity();
				rot.setRotation(new AxisAngle4f(z.x, z.y, z.z, gamma));
				rot.transform(x);
				rot.transform(y);

				c.setColumn(0,x);
				c.setColumn(1,y);
				c.setColumn(2,z);
				c.getColumn(3, t);
				c.transform(dt);
				t.add(dt);
				c.setColumn(3,t);

				c.invert();
				sceneManager.getCamera().setCameraMatrix(c);
			}
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
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_W: {
					forwards = true;
					break;
				}
				case KeyEvent.VK_S: {
					backwards = true;
					break;
				}
				case (char) KeyEvent.VK_UP: {
					pitchP = true;
					break;
				}
				case (char) KeyEvent.VK_DOWN: {
					pitchN = true;
					break;
				}
				case KeyEvent.VK_A: {
					yawP = true;
					break;
				}
				case KeyEvent.VK_D: {
					yawN = true;
					break;
				}
				case (char) KeyEvent.VK_LEFT: {
					rollP = true;
					break;
				}
				case (char) KeyEvent.VK_RIGHT: {
					rollN = true;
					break;
				}
				case (char) KeyEvent.VK_N: {
					// Remove material from shape, and set "normal" shader
					shape.setMaterial(null);
					renderContext.useShader(normalShader);
					break;
				}
				case (char) KeyEvent.VK_M: {
					// Remove material from shape, and set "default" shader
					shape.setMaterial(null);
					renderContext.useDefaultShader();
					break;
				}
				}
				// Trigger redrawing
				renderPanel.getCanvas().repaint();
			}

			public void keyReleased(KeyEvent e)
			{
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_W: {
					forwards = false;
					break;
				}
				case KeyEvent.VK_S: {
					backwards = false;
					break;
				}
				case KeyEvent.VK_A: {
					yawP = false;
					break;
				}
				case KeyEvent.VK_D: {
					yawN = false;
					break;
				}
				case (char) KeyEvent.VK_UP: {
					pitchP = false;
					break;
				}
				case (char) KeyEvent.VK_DOWN: {
					pitchN = false;
					break;
				}
				case (char) KeyEvent.VK_LEFT: {
					rollP = false;
					break;
				}
				case (char) KeyEvent.VK_RIGHT: {
					rollN = false;
					break;
				}
				}

				// Trigger redrawing
				renderPanel.getCanvas().repaint();
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
