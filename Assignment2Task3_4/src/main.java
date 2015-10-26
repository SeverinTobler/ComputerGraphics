
import jrtr.*;
import jrtr.glrenderer.*;
import jrtr.gldeferredrenderer.*;

import javax.swing.*;

import java.awt.Point;
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
	static SimpleSceneManager sceneManager;
	static Shape shape;
	static Shape airplane;
	static float currentstep, basicstep;
	static ObjReader objReader;
	static boolean mouseActive;
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

			VertexData vertexData = createLandscape(50, 6, renderContext);
			//VertexData vertexData = createCube();
			VertexData vertexDataAirplane;
			objReader = new ObjReader();
			try {
				vertexDataAirplane = objReader.read("/Users/Severin/_Uni Bern/3. Semester/Computer Graphics/Eclipse Workspace/obj/airplane.obj", 1f, renderContext);
			} catch (IOException e1) {
				vertexDataAirplane = createCube();
				e1.printStackTrace();
			}


			// Make a scene manager and add the object
			sceneManager = new SimpleSceneManager();
			shape = new Shape(vertexData);
			airplane = new Shape(vertexDataAirplane);
			
			sceneManager.addShape(shape);
			sceneManager.addShape(airplane);
			
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

		private VertexData createLandscape(float size, int iterations, RenderContext renderContext){
			int n = (int) Math.pow(2, iterations)+1;	// number of vertex of square side
			double[][] z = new double[n][n];

			double h = 1;
			float grayLevel = 6;
			float whiteLevel = 8;
			// initial values:
			z[0][0] = 12;
			z[0][n-1] = 9;
			z[n-1][n-1] = 0;
			z[n-1][0] = 0;

			// iterative part:
			for(int k=0; k<iterations; k++){
				double d = 0.7*Math.pow(2, -h);	// damping
				int m = (int) Math.pow(2, k);
				int fullStep = (n-1)/m;
				int halfStep = fullStep/2;

				// square step:
				for(int i=0; i<m; i++){
					for(int j=0; j<m; j++){
						z[i*fullStep+halfStep][j*fullStep+halfStep] = (z[i*fullStep][j*fullStep] + z[i*fullStep][(j+1)*fullStep] + z[(i+1)*fullStep][j*fullStep] + z[(i+1)*fullStep][(j+1)*fullStep])/4 + (Math.random()*2 - 1)*d;		
					}
				}

				// diamond step:
				for(int i=0; i<m; i++){
					for(int j=0; j<m; j++){
						if(i == 0)	// upper
							z[0][j*fullStep + halfStep] = (z[0][j*fullStep] + z[halfStep][j*fullStep+halfStep] + z[0][(j+1)*fullStep])/3 + (Math.random()*2 - 1)*d;
						else
							z[i*fullStep][j*fullStep + halfStep] = (z[i*fullStep][j*fullStep] + z[i*fullStep+halfStep][j*fullStep+halfStep] + z[i*fullStep][(j+1)*fullStep] + z[i*fullStep-halfStep][j*fullStep+halfStep])/4 + (Math.random()*2 - 1)*d;
						if(j == 0)	// left
							z[i*fullStep + halfStep][0] = (z[(i+1)*fullStep][0] + z[i*fullStep+halfStep][halfStep] + z[i*fullStep][0])/3 + (Math.random()*2 - 1)*d;
						else
							z[i*fullStep + halfStep][j*fullStep] = (z[i*fullStep][j*fullStep] + z[(i+1)*fullStep][j*fullStep] + z[i*fullStep+halfStep][j*fullStep+halfStep] + z[i*fullStep+halfStep][j*fullStep-halfStep])/4 + (Math.random()*2 - 1)*d;
						if(i == m-1)	// lower
							z[(i+1)*fullStep][j*fullStep + halfStep] = (z[(i+1)*fullStep][j*fullStep] + z[i*fullStep+halfStep][j*fullStep+halfStep] + z[(i+1)*fullStep][(j+1)*fullStep])/3 + (Math.random()*2 - 1)*d;
						else
							z[(i+1)*fullStep][j*fullStep + halfStep] = (z[(i+1)*fullStep][j*fullStep] + z[i*fullStep+halfStep][j*fullStep+halfStep] + z[(i+1)*fullStep][(j+1)*fullStep] + z[(i+1)*fullStep+halfStep][j*fullStep+halfStep])/4 + (Math.random()*2 - 1)*d;

						if(j == m-1)	// right
							z[i*fullStep+halfStep][(j+1)*fullStep] = (z[i*fullStep+halfStep][j*fullStep+halfStep] + z[(i+1)*fullStep][(j+1)*fullStep] + z[i*fullStep][(j+1)*fullStep])/3 + (Math.random()*2 - 1)*d;
						else
							z[i*fullStep+halfStep][(j+1)*fullStep] = (z[i*fullStep+halfStep][j*fullStep+halfStep] + z[(i+1)*fullStep][(j+1)*fullStep] + z[i*fullStep+halfStep][(j+1)*fullStep+halfStep] + z[i*fullStep][(j+1)*fullStep])/4 + (Math.random()*2 - 1)*d;
					}
				}
			}

			// vertex
			float s = size/n;
			float d = size/2;	// distance origin
			float[] v = new float[3*n*n];
			float[] c = new float[v.length];
			float[] norm = new float[v.length];
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					v[3*(i*n+j)] = s*j-d;
					v[3*(i*n+j)+1] = s*i-d;
					v[3*(i*n+j)+2] = (float) z[i][j];

					c[3*(i*n+j)+1] = 0.6f;
					if(z[i][j] > grayLevel){
						c[3*(i*n+j)] = 0.5f;
						c[3*(i*n+j)+1] = 0.5f;
						c[3*(i*n+j)+2] = 0.5f;
					}
					if(z[i][j] > whiteLevel){
						c[3*(i*n+j)] = 1;
						c[3*(i*n+j)+1] = 1;
						c[3*(i*n+j)+2] = 1;
					}

					norm[3*(i*n+j)+2] = 1;
				}
			}

			for(int i=1; i<n-1; i++){
				for(int j=1; j<n-1; j++){
					float length = (float) Math.sqrt((z[i][j-1] - z[i][j+1])*(z[i][j-1] - z[i][j+1]) + (z[i-1][j] - z[i+1][j])*(z[i-1][j] - z[i+1][j]) + 2*s*2*s);
					norm[3*(i*n+j)] = (float) (z[i][j-1] - z[i][j+1])/length;
					norm[3*(i*n+j)+1] = (float) (z[i-1][j] - z[i+1][j])/length;
					norm[3*(i*n+j)+2] = 2*s/length;
				}
			}


			int[] indices = new int[6*(n-1)*(n-1)];
			for(int i=0; i<n-1; i++){
				for(int j=0; j<n-1; j++){
					indices[6*(i*(n-1)+j)] = i*n+j;
					indices[6*(i*(n-1)+j)+1] = (i+1)*n+j;
					indices[6*(i*(n-1)+j)+2] = (i+1)*n+j+1;

					indices[6*(i*(n-1)+j)+3] = i*n+j;
					indices[6*(i*(n-1)+j)+4] = (i+1)*n+j+1;
					indices[6*(i*(n-1)+j)+5] = i*n+j+1;
				}
			}

			VertexData vertexData = renderContext.makeVertexData(v.length/3);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(norm, VertexData.Semantic.NORMAL, 3);
			vertexData.addIndices(indices);
			return vertexData;
		}


		private VertexData createCube(){
			// Make a simple geometric object: a cube

			// The vertex positions of the cube
			float v[] = {-1,-1,1, 1,-1,1, 1,1,1, -1,1,1,		// front face
					-1,-1,-1, -1,-1,1, -1,1,1, -1,1,-1,	// left face
					1,-1,-1,-1,-1,-1, -1,1,-1, 1,1,-1,		// back face
					1,-1,1, 1,-1,-1, 1,1,-1, 1,1,1,		// right face
					1,1,1, 1,1,-1, -1,1,-1, -1,1,1,		// top face
					-1,-1,1, -1,-1,-1, 1,-1,-1, 1,-1,1};	// bottom face

			// The vertex normals 
			float n[] = {0,0,1, 0,0,1, 0,0,1, 0,0,1,			// front face
					-1,0,0, -1,0,0, -1,0,0, -1,0,0,		// left face
					0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,		// back face
					1,0,0, 1,0,0, 1,0,0, 1,0,0,			// right face
					0,1,0, 0,1,0, 0,1,0, 0,1,0,			// top face
					0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0};		// bottom face

			// The vertex colors
			float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
					0,1,0, 0,1,0, 0,1,0, 0,1,0,
					1,0,0, 1,0,0, 1,0,0, 1,0,0,
					0,1,0, 0,1,0, 0,1,0, 0,1,0,
					0,0,1, 0,0,1, 0,0,1, 0,0,1,
					0,0,1, 0,0,1, 0,0,1, 0,0,1};

			// Texture coordinates 
			float uv[] = {0,0, 1,0, 1,1, 0,1,
					0,0, 1,0, 1,1, 0,1,
					0,0, 1,0, 1,1, 0,1,
					0,0, 1,0, 1,1, 0,1,
					0,0, 1,0, 1,1, 0,1,
					0,0, 1,0, 1,1, 0,1};

			// Construct a data structure that stores the vertices, their
			// attributes, and the triangle mesh connectivity
			VertexData vertexData = renderContext.makeVertexData(24);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);

			// The triangles (three vertex indices for each triangle)
			int indices[] = {0,2,3, 0,1,2,			// front face
					4,6,7, 4,5,6,			// left face
					8,10,11, 8,9,10,		// back face
					12,14,15, 12,13,14,	// right face
					16,18,19, 16,17,18,	// top face
					20,22,23, 20,21,22};	// bottom face

			vertexData.addIndices(indices);
			return vertexData;
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
				


				dt = new Vector4f(0, 0, -4, 0);
				t_airplane.transform(dt);
				t.add(dt);
				t_airplane.setColumn(3,t);
				t_airplane.setColumn(0, z);
				x.negate();
				t_airplane.setColumn(2, x);
				airplane.setTransformation(t_airplane);
				
			}

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
		public void mouseEntered(MouseEvent e) {
			mouseActive = true;
		}
		public void mouseExited(MouseEvent e) {
			mouseActive = false;
		}
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
