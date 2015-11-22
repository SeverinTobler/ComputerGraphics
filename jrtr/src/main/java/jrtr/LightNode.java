package jrtr;

import javax.vecmath.Matrix4f;

public class LightNode extends Leaf {
	Light light;
	
	public LightNode(Light light){
		this.light = light;
	}

	@Override
	public Matrix4f getTransformation() {
		Matrix4f T = new Matrix4f();
		T.setIdentity();
		T.setTranslation(light.position);
		return T;
	}

	@Override
	public Shape getShape() {
		return null;
	}
	
	public Light getLight() {
		return light;
	}

}
