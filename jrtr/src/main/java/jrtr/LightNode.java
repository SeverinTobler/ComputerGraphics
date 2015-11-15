package jrtr;

import javax.vecmath.Matrix4f;

public class LightNode extends Leaf {
	Light light;
	
	public LightNode(Light light){
		this.light = light;
	}

	@Override
	public Matrix4f getTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape getShape() {
		return null;
	}

}
