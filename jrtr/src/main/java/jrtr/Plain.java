package jrtr;

import javax.vecmath.Vector4f;

public class Plain {
	public Vector4f point;
	public Vector4f normal;
	
	Plain(Vector4f p1, Vector4f p2, Vector4f p3){
		point = new Vector4f(p1);
		Vector4f v1 = new Vector4f(p1);
		v1.sub(p2);
		Vector4f v2 = new Vector4f(p1);
		v2.sub(p3);
		normal = normal(v1, v2);
	}
	
	private Vector4f normal(Vector4f a, Vector4f b){
		Vector4f normal = new Vector4f(a.y*b.z-b.y*a.z, a.z*b.x-b.z*a.x, a.x*b.y-b.x*a.y, 0);
		float l = (float) Math.sqrt(normal.x*normal.x + normal.y*normal.y + normal.z*normal.z);
		return new Vector4f(normal.x/l, normal.y/l, normal.z/l, 0);
	}
}
