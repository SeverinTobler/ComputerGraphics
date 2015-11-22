package jrtr;

import java.util.Iterator;
import java.util.Stack;
import javax.vecmath.Matrix4f;

public abstract class Group implements Node {
	@Override
	public Shape getShape() {
		return null;
	}
	
	abstract public void addChild(Node n);
	abstract public void removeChild(Node n);
	abstract public void initRenderItr(Stack<RenderItem> renderItems, Matrix4f Tabove, GraphSceneManager sceneManager, boolean culling);
	abstract public void initLightItr(Stack<Light> lightStack, Matrix4f Tabove);
}
