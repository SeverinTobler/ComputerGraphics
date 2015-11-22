package jrtr;

import java.util.Stack;
import javax.vecmath.Matrix4f;

import java.util.Iterator;

public class GraphSceneManager implements SceneManagerInterface {
	private TransformGroup sceneGraph;
	private Camera camera;
	private Frustum frustum;
	private boolean culling;

	public GraphSceneManager()
	{
		sceneGraph = new TransformGroup();
		camera = new Camera();
		frustum = new Frustum();
		culling = true;
	}
	
	public TransformGroup getSceneGraph() {
		return sceneGraph;
	}
	
	public void setCulling(boolean culling){
		this.culling = culling;
	}
	public boolean getCulling(){
		return culling;
	}
	
	@Override
	public SceneManagerIterator iterator() {
		return new GraphSceneManagerItr(this);
	}

	@Override
	public Iterator<Light> lightIterator() {
		return new LightItr(this);
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public Frustum getFrustum() {
		return frustum;
	}
	
	private class LightItr implements Iterator {
		
		public LightItr(GraphSceneManager sceneManager){
			lightStack = new Stack<Light>();
			Matrix4f identity = new Matrix4f();
			identity.setIdentity();
			sceneManager.sceneGraph.initLightItr(lightStack, identity);
		}

		@Override
		public boolean hasNext() {
			return !lightStack.isEmpty();
		}

		@Override
		public Object next() {
			return lightStack.pop();
		}
		private Stack<Light> lightStack;
	}
	
	private class GraphSceneManagerItr implements SceneManagerIterator {
		
		public GraphSceneManagerItr(GraphSceneManager sceneManager)
		{
			renderItems = new Stack<RenderItem>();
			Matrix4f identity = new Matrix4f();
			identity.setIdentity();
			sceneManager.sceneGraph.initRenderItr(renderItems, identity, sceneManager, culling);
		}
		
		public boolean hasNext()
		{
			return !renderItems.isEmpty();
		}
		
		public RenderItem next()
		{
			return renderItems.pop();
		}
		
		private Stack<RenderItem> renderItems;
	}

}
