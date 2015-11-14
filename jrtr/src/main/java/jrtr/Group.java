package jrtr;

public abstract class Group implements Node {
	@Override
	public Shape getShape() {
		return null;
	}
	
	abstract public void addChild(Node n);
	abstract public void removeChild(Node n);
}
