package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public interface Node {
	public Matrix4f getTransformation();
	public Shape getShape();
	public LinkedList<Node> getChildren();
}
