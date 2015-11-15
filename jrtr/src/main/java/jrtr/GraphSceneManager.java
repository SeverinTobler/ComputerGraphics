package jrtr;

import java.util.LinkedList;
import java.util.Stack;
import javax.vecmath.Matrix4f;

import java.util.Iterator;

public class GraphSceneManager implements SceneManagerInterface {
	private TransformGroup sceneGraph;
	private Camera camera;
	private Frustum frustum;
	private LinkedList<Light> lights;	// delete this

	public GraphSceneManager()
	{
		sceneGraph = new TransformGroup();
		camera = new Camera();
		frustum = new Frustum();
		lights = new LinkedList<Light>();	// delete this
	}
	
	public void addLight(Light light) {		// delete this
		lights.add(light);
	}
	
	public TransformGroup getSceneGraph() {
		return sceneGraph;
	}
	
	@Override
	public SceneManagerIterator iterator() {
		return new GraphSceneManagerItr(this);
	}

	@Override
	public Iterator<Light> lightIterator() {
		return lights.iterator();		// change this
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public Frustum getFrustum() {
		return frustum;
	}
	
	private class GraphSceneManagerItr implements SceneManagerIterator {
		
		public GraphSceneManagerItr(GraphSceneManager sceneManager)
		{
			TStack = new Stack<Matrix4f>();
			shapeStack = new Stack<Shape>();
			Matrix4f identity = new Matrix4f();
			identity.setIdentity();
			sceneManager.sceneGraph.push(shapeStack, TStack, identity);
		}
		
		public boolean hasNext()
		{
			return !shapeStack.isEmpty();
		}
		
		public RenderItem next()
		{
			Shape shape = shapeStack.pop();
			Matrix4f T = TStack.pop();
			return new RenderItem(shape, T);
		}
		
		private Stack<Matrix4f> TStack;
		private Stack<Shape> shapeStack;
	}

}
