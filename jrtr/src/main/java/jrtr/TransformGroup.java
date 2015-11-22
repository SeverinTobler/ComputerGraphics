package jrtr;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class TransformGroup extends Group{
	private Matrix4f T;
	private LinkedList<Node> children;


	public TransformGroup(){
		T = new Matrix4f();
		T.setIdentity();
		children = new LinkedList<Node>();
	}

	public TransformGroup(Matrix4f T){
		this.T = new Matrix4f(T);
		children = new LinkedList<Node>();
	}


	@Override
	public Matrix4f getTransformation() {
		return T;
	}

	@Override
	public LinkedList<Node> getChildren() {
		return children;
	}

	@Override
	public void addChild(Node n) {
		children.add(n);
	}

	@Override
	public void removeChild(Node n) {
		children.remove(n);
	}

	@Override
	public void initRenderItr(Stack<RenderItem> renderItems, Matrix4f Tabove, GraphSceneManager sceneManager, boolean culling) {
		Matrix4f T = new Matrix4f(Tabove);
		T.mul(this.T);

		ListIterator<Node> itr = children.listIterator(0);;
		while(itr.hasNext()){
			Node n = itr.next();
			if(n instanceof Group){
				((Group) n).initRenderItr(renderItems, T, sceneManager, culling);
				continue;
			}
			if(n instanceof ShapeNode){
				Matrix4f Ttemp = new Matrix4f(T);
				Ttemp.mul(n.getTransformation());

				if(culling){
					// check if within boundary
					float r = n.getShape().getRadius();
					Vector4f c = new Vector4f(n.getShape().getCentroid());
					Matrix4f object = new Matrix4f(Ttemp);
					Matrix4f camera = new Matrix4f(sceneManager.getCamera().getCameraMatrix());
					camera.mul(object);
					camera.transform(c);

					boolean draw = true;
					Plain[] boundary = sceneManager.getFrustum().getBoundaryPlains();
					for(int i=0; i<boundary.length; i++){
						Vector4f sub = new Vector4f(c.x-boundary[i].point.x, c.y-boundary[i].point.y, c.z-boundary[i].point.z, 0);
						if(dist(sub,boundary[i])>r){
							draw = false;
							continue;
						}
					}

					// draw if necessary
					if(draw)
						renderItems.push(new RenderItem(n.getShape(),Ttemp));
				}else{
					renderItems.push(new RenderItem(n.getShape(),Ttemp));
				}
			}
		}
	}

	private float dist(Vector4f point, Plain plain){
		return point.x*plain.normal.x + point.y*plain.normal.y + point.z*plain.normal.z;
	}

	@Override
	public void initLightItr(Stack<Light> lightStack, Matrix4f Tabove) {
		Matrix4f T = new Matrix4f(Tabove);
		T.mul(this.T);

		ListIterator<Node> itr = children.listIterator(0);;
		while(itr.hasNext()){
			Node n = itr.next();
			if(n instanceof Group){
				((Group) n).initLightItr(lightStack, T);
				continue;
			}
			if(n instanceof LightNode){
				Light l = new Light(((LightNode) n).getLight());
				Vector4f pos = new Vector4f(l.position.x, l.position.y, l.position.z, 1);
				T.transform(pos);
				l.position = new Vector3f(pos.x, pos.y, pos.z);
				lightStack.push(l);
			}
		}
	}

}
