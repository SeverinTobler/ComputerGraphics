package jrtr;

import javax.vecmath.Matrix4f;

public class ShapeNode extends Leaf {
	private Shape shape;
	
	public ShapeNode(Shape shape){
		this.shape = shape;
	}

	@Override
	public Matrix4f getTransformation() {
		return shape.getTransformation();
	}

	@Override
	public Shape getShape() {
		return shape;
	}

}
