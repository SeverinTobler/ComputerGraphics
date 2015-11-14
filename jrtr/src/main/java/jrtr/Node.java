package jrtr;

import java.util.LinkedList;
import java.util.Stack;

import javax.vecmath.Matrix4f;

public interface Node {
	public Matrix4f getTransformation();
	public Shape getShape();
	public LinkedList<Node> getChildren();
	public void push(Stack<Shape> shapeStack, Stack<Matrix4f> TStack, Matrix4f Tabove);
}
