/*
 * Assignment 5, Computer Graphics
 * Severin Tobler, 14.11.2015
 * 
 */

import jrtr.*;
import jrtr.Light.Type;
import jrtr.glrenderer.*;
import jrtr.gldeferredrenderer.*;

import javax.swing.*;
import java.awt.event.*;
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
	static TransformGroup robot;
	static TransformGroup leftLeg;
	static TransformGroup rightLeg;
	static TransformGroup lowerLeftLeg;
	static TransformGroup lowerRightLeg;
	static TransformGroup leftArm;
	static TransformGroup rightArm;
	static TransformGroup lowerLeftArm;
	static TransformGroup lowerRightArm;
	static TransformGroup head;

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
			material = new Material();
			material.shader = phongShader;
			material.diffuse = new Vector3f(1f, 1f, 1f);
			material.specular = new Vector3f(1f, 1f, 1f);
			material.shininess = 0.5f;
			material.diffuseMap = renderContext.makeTexture();
			try {
				material.diffuseMap.load("../textures/wood.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}



			// Build scene
			TransformGroup sceneGraph = sceneManager.getSceneGraph();

			// floor
			Matrix4f T = new Matrix4f();
			T = new Matrix4f(1,0,0,-5, 0,0,1,0, 0,-1,0,0, 0,0,0,1);
			TransformGroup floorT = new TransformGroup(T);

			shape = new Cube(renderContext, 30, 30, 0.2f);
			shape.setMaterial(material);
			shape.setTransformation(new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,-0.1f, 0,0,0,1));
			ShapeNode floor = new ShapeNode(shape);
			floorT.addChild(floor);

			sceneGraph.addChild(floorT);

			// robot
			shape = new Cube(renderContext, 2,2,2);
			shape.setMaterial(material);
			shape.setTransformation(new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,1, 0,0,0,1));
			ShapeNode skull = new ShapeNode(shape);
			shape = new Cube(renderContext, 2,1,2);
			shape.setMaterial(material);
			shape.setTransformation(new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,-1, 0,0,0,1));
			ShapeNode chest = new ShapeNode(shape);
			shape = new Cube(renderContext, 1,1,1.1f);
			shape.setMaterial(material);
			shape.setTransformation(new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,-0.55f, 0,0,0,1));
			ShapeNode limb = new ShapeNode(shape);


			T = new Matrix4f(1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,0,0,1);
			robot = new TransformGroup(T);

			T = new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,4.2f, 0,0,0,1);
			head = new TransformGroup(T);
			head.addChild(skull);

			T = new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,4.2f, 0,0,0,1);
			TransformGroup torso = new TransformGroup(T);
			torso.addChild(chest);

			T = new Matrix4f(1,0,0,0.5f, 0,1,0,0f, 0,0,1,2.2f, 0,0,0,1);
			leftLeg = new TransformGroup(T);
			leftLeg.addChild(limb);

			T = new Matrix4f(1,0,0,0, 0,1,0,0f, 0,0,1,-1.1f, 0,0,0,1);
			lowerLeftLeg = new TransformGroup(T);
			lowerLeftLeg.addChild(limb);

			T = new Matrix4f(1,0,0,-0.5f, 0,1,0,0f, 0,0,1,2.2f, 0,0,0,1);
			rightLeg = new TransformGroup(T);
			rightLeg.addChild(limb);

			T = new Matrix4f(1,0,0,0, 0,1,0,0f, 0,0,1,-1.1f, 0,0,0,1);
			lowerRightLeg = new TransformGroup(T);
			lowerRightLeg.addChild(limb);

			T = new Matrix4f(1,0,0,1.5f, 0,1,0,0f, 0,0,1,4.2f, 0,0,0,1);
			leftArm = new TransformGroup(T);
			leftArm.addChild(limb);

			T = new Matrix4f(1,0,0,0, 0,1,0,0f, 0,0,1,-1.1f, 0,0,0,1);
			lowerLeftArm = new TransformGroup(T);
			lowerLeftArm.addChild(limb);

			T = new Matrix4f(1,0,0,-1.5f, 0,1,0,0f, 0,0,1,4.2f, 0,0,0,1);
			rightArm = new TransformGroup(T);
			rightArm.addChild(limb);

			T = new Matrix4f(1,0,0,0, 0,1,0,0f, 0,0,1,-1.1f, 0,0,0,1);
			lowerRightArm = new TransformGroup(T);			
			lowerRightArm.addChild(limb);


			leftLeg.addChild(lowerLeftLeg);
			rightLeg.addChild(lowerRightLeg);
			leftArm.addChild(lowerLeftArm);
			rightArm.addChild(lowerRightArm);

			robot.addChild(head);
			robot.addChild(torso);
			robot.addChild(leftArm);
			robot.addChild(rightArm);
			robot.addChild(leftLeg);
			robot.addChild(rightLeg);

			sceneGraph.addChild(robot);
			
			
			// Light
			Light l = new Light();
			l.diffuse = new Vector3f(0f,0f,0f);
			l.specular = new Vector3f(0f,0f,0f);
			l.ambient = new Vector3f(0.2f,0.2f,0.2f);
			LightNode ambient = new LightNode(l);
			sceneGraph.addChild(ambient);
			

			l = new Light();
			l.diffuse = new Vector3f(1f,1f,1f);
			l.specular = new Vector3f(1f,1f,1f);
			l.position = new Vector3f(0f,0f,0f);
			l.type = Type.POINT;
			LightNode torch = new LightNode(l);
			
			T = new Matrix4f(1,0,0,0, 0,1,0,0f, 0,0,1,-1.2f, 0,0,0,1);
			TransformGroup hand = new TransformGroup(T);
			hand.addChild(torch);
			lowerRightArm.addChild(hand);
			
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
		private float alpha=0;
		public void run()
		{
			Matrix4f rotY = new Matrix4f();
			rotY.rotZ(currentstep);
			Matrix4f forward = new Matrix4f();
			forward.setIdentity();
			forward.setTranslation(new Vector3f(0, currentstep*4, 0));

			Matrix4f t = robot.getTransformation();
			t.mul(rotY);
			t.mul(forward);

			alpha = alpha - 4*currentstep;
			if(alpha<0)
				alpha = (float)(alpha + 2*Math.PI);
			
			// legs
			Matrix3f rotX = new Matrix3f();
			rotX.rotX((float)Math.sin(alpha));
			t = leftLeg.getTransformation();
			t.setRotation(rotX);
			rotX.rotX(-(float)Math.abs(Math.sin(alpha)));
			t = lowerLeftLeg.getTransformation();
			t.setRotation(rotX);

			rotX.rotX(-(float)Math.sin(alpha));
			t = rightLeg.getTransformation();
			t.setRotation(rotX);
			rotX.rotX(-(float)Math.abs(Math.sin(alpha)));
			t = lowerRightLeg.getTransformation();
			t.setRotation(rotX);

			// arms
			rotX.rotX(-(float)Math.sin(alpha));
			t = leftArm.getTransformation();
			t.setRotation(rotX);
			rotX.rotX(-(float)Math.sin(alpha));
			t = lowerLeftArm.getTransformation();
			t.setRotation(rotX);

			rotX.rotX((float)Math.sin(alpha));
			t = rightArm.getTransformation();
			t.setRotation(rotX);
			rotX.rotX((float)Math.sin(alpha));
			t = lowerRightArm.getTransformation();
			t.setRotation(rotX);

			// head
			rotX.rotX(-(float)Math.abs(Math.sin(alpha))/2);
			t = head.getTransformation();
			t.setRotation(rotX);

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

				Matrix4f t_airplane = new Matrix4f(c);
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
				renderContext.useShader(phongShader);
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
