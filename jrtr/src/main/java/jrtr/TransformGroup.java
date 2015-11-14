package jrtr;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

import javax.vecmath.Matrix4f;

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
	public void push(Stack<Shape> shapeStack, Stack<Matrix4f> TStack, Matrix4f Tabove) {
		Matrix4f T = new Matrix4f(Tabove);
		T.mul(this.T);
		
		ListIterator<Node> itr = children.listIterator(0);;
		while(itr.hasNext()){
			itr.next().push(shapeStack, TStack, T);
		}
	}

}
