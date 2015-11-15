package jrtr;

import java.util.Stack;
import javax.vecmath.Matrix4f;

public abstract class Group implements Node {
	@Override
	public Shape getShape() {
		return null;
	}
	
	abstract public void addChild(Node n);
	abstract public void removeChild(Node n);
	abstract public void push(Stack<Shape> shapeStack, Stack<Matrix4f> TStack, Matrix4f Tabove);
}
