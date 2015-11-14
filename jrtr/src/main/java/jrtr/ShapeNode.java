package jrtr;

import java.util.Stack;

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

	@Override
	public void push(Stack<Shape> shapeStack, Stack<Matrix4f> TStack, Matrix4f Tabove) {
		shapeStack.push(shape);
		Matrix4f T = new Matrix4f(Tabove);
		T.mul(shape.getTransformation());
		TStack.push(T);
	}

}
