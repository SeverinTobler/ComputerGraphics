
import jrtr.*;
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
	static Shader normalShader;
	static Shader diffuseShader;
	static Shader bumpShader;
	static Material stone;
	static Material leather;
	static Material metal;
	static Material orange;
	static Material glass;
	static SimpleSceneManager sceneManager;
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

			Bezier bezier = new Bezier();
			// Schale
			float[] controlPoints = {0,0, 0.25f,0, 1.75f,0, 2,0, 2.5f,0, 2,0.5f, 2.5f,0.5f, 2.75f,0.5f , 2.75f,0.5f, 3,0.5f,	// bottom
					3.1f,0.5f, 3.1f,0.6f, 3,0.6f, 2.75f,0.6f, 2.65f,0.6f, 2.4f,0.6f, 1.9f,0.6f, 2.4f,0.1f, 0.9f,0.1f, 0.65f,0.1f, 0.25f,0.1f, 0,0.1f};	// top
			if(!bezier.drawCurve( controlPoints))
				System.out.println("control points do not match with number of segments");
			BezierOut data = bezier.getShapeData(200, 200);
			VertexData vertexData = renderContext.makeVertexData(data.v.length/3);
			vertexData.addElement(data.c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(data.v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(data.n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(data.uv, VertexData.Semantic.TEXCOORD, 2);
			vertexData.addElement(data.t, VertexData.Semantic.TANGENT, 3);
			vertexData.addIndices(data.indices);
			Shape plate = new Shape(vertexData);
			
			// Weinflasche
			controlPoints = new float[] {0,0, 0.25f,0, 0.75f,0, 1,0, 1.25f,0, 1,0.25f, 1,3, 1,3.5f, 0.3f,3.5f, 0.3f,4, 0.3f,4.1f, 0,4, 0,4};
			if(!bezier.drawCurve( controlPoints))
				System.out.println("control points do not match with number of segments");
			data = bezier.getShapeData(200, 200);
			vertexData = renderContext.makeVertexData(data.v.length/3);
			vertexData.addElement(data.c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(data.v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(data.n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(data.uv, VertexData.Semantic.TEXCOORD, 2);
			vertexData.addElement(data.t, VertexData.Semantic.TANGENT, 3);
			vertexData.addIndices(data.indices);
			Shape bottle = new Shape(vertexData);			
			
			// Apfel
			controlPoints = new float[] {0,0.1f, 0.2f,0, 0,0, 0.2f,0, 1.5f,0, 2,2,  0.3f,2 ,0,1.8f, 0.3f,1.8f, 0,1.8f};
			if(!bezier.drawCurve( controlPoints))
				System.out.println("control points do not match with number of segments");
			data = bezier.getShapeData(200, 200);
			vertexData = renderContext.makeVertexData(data.v.length/3);
			vertexData.addElement(data.c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(data.v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(data.n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(data.uv, VertexData.Semantic.TEXCOORD, 2);
			vertexData.addElement(data.t, VertexData.Semantic.TANGENT, 3);
			vertexData.addIndices(data.indices);
			shape = new Shape(vertexData);

			
			Shape floor = new Cube(renderContext, 10f, 0.1f, 10);
			
			// Make a scene manager and add the object
			sceneManager = new SimpleSceneManager();
			sceneManager.addShape(shape);
			sceneManager.addShape(floor);
			sceneManager.addShape(plate);
			sceneManager.addShape(bottle);

			// create light
			Light l = new Light();
			l.diffuse = new Vector3f(1f,1f,1f);
			l.direction = new Vector3f(0f,0f,1f);
			sceneManager.addLight(l);

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

			bumpShader = renderContext.makeShader();
			try {
				bumpShader.load("../jrtr/shaders/bump.vert", "../jrtr/shaders/bump.frag");
			} catch(Exception e) {
				System.out.print("Problem with shader:\n");
				System.out.print(e.getMessage());
			}
			
			// Make a material that can be used for shading
			glass = new Material();
			glass.shader = diffuseShader;
			glass.diffuseMap = renderContext.makeTexture();
			try {
				glass.diffuseMap.load("../textures/glass.jpg");
			} catch(Exception e) {
				System.out.print("Could not load texture (glass).\n");
				System.out.print(e.getMessage());
			}
			
			stone = new Material();
			stone.shader = bumpShader;
			stone.diffuseMap = renderContext.makeTexture();
			stone.normalMap = renderContext.makeTexture();
			try {
				stone.diffuseMap.load("../textures/stone.jpg");
				stone.normalMap.load("../textures/stone_norm.jpg");
			} catch(Exception e) {
				System.out.print("Could not load texture (stone).\n");
				System.out.print(e.getMessage());
			}

			leather = new Material();
			leather.shader = bumpShader;
			leather.diffuseMap = renderContext.makeTexture();
			leather.normalMap = renderContext.makeTexture();
			try {
				leather.diffuseMap.load("../textures/leather9_DIFFUSE.jpg");
				leather.normalMap.load("../textures/leather9_NORMAL.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture (leather).\n");
				System.out.print(e.getMessage());
			}

			orange = new Material();
			orange.shader = bumpShader;
			orange.diffuseMap = renderContext.makeTexture();
			orange.normalMap = renderContext.makeTexture();
			try {
				orange.diffuseMap.load("../textures/orange.jpg");
				orange.normalMap.load("../textures/orange_norm.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture (orange).\n");
				System.out.print(e.getMessage());
			}
			
			metal = new Material();
			metal.shader = bumpShader;
			metal.diffuseMap = renderContext.makeTexture();
			metal.normalMap = renderContext.makeTexture();
			try {
				metal.diffuseMap.load("../textures/metal.jpg");
				metal.normalMap.load("../textures/metal_norm.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture (metal).\n");
				System.out.print(e.getMessage());
			}

			// Register a timer task
			Timer timer = new Timer();
			basicstep = 0.01f;
			currentstep = basicstep;
			timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
			
			
			// build scene
			floor.setMaterial(stone);
			Matrix4f t = floor.getTransformation();
			t.m13 = -0.1f;
			
			plate.setMaterial(metal);
			
			bottle.setMaterial(glass);
			t = bottle.getTransformation();
			t.m03 = 3.5f;
			t.m23 = 3.5f;
			
			shape.setMaterial(orange);
			t = shape.getTransformation();
			t.m13 = 0.1f;
			t.m03 = -0.3f;
			t.m23 = 0.1f;
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
				if(shape.getMaterial() == null) {
					shape.setMaterial(orange);
				} else
				{
					shape.setMaterial(null);
					renderContext.useDefaultShader();
				}
				break;
			}
			}

			// Trigger redrawing
			//renderPanel.getCanvas().repaint();
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
			//renderPanel.getCanvas().repaint();
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
