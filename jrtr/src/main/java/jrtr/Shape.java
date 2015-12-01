package jrtr;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.vecmath.*;

import jrtr.VertexData.Semantic;

/**
 * Represents a 3D object. The shape references its geometry, 
 * that is, a triangle mesh stored in a {@link VertexData} 
 * object, its {@link Material}, and a transformation {@link Matrix4f}.
 */
public class Shape {

	private Material material;
	private VertexData vertexData;
	private Matrix4f t;
	private Vector4f centroid;
	private float radius;
	
	/**
	 * Make a shape from {@link VertexData}. A shape contains the geometry 
	 * (the {@link VertexData}), material properties for shading (a 
	 * refernce to a {@link Material}), and a transformation {@link Matrix4f}.
	 *  
	 *  
	 * @param vertexData the vertices of the shape.
	 */
	public Shape(VertexData vertexData)
	{
		this.vertexData = vertexData;
		t = new Matrix4f();
		t.setIdentity();
		
		material = null;
		
		calculateBoundingShere();
	}
	
	public float getRadius(){
		return radius;
	}
	
	public Vector4f getCentroid(){
		return centroid;
	}
	
	private void calculateBoundingShere(){
		LinkedList<Vector4f> positions = new LinkedList<Vector4f>();
		int indices[] = vertexData.getIndices();
		centroid = new Vector4f(0,0,0,0);
		float k = 0;
		for(int j=0; j<indices.length; j++) {
			int i = indices[j];
			ListIterator<VertexData.VertexElement> itr = vertexData.getElements().listIterator(0);
			while(itr.hasNext()) {
				VertexData.VertexElement e = itr.next();
				if (e.getSemantic() == Semantic.POSITION) {
					Vector4f p = new Vector4f(e.getData()[i*3],e.getData()[i*3+1],e.getData()[i*3+2],1);
					positions.add(p);
					centroid.add(p);
					k++;
				}
			}
		}
		centroid = new Vector4f(centroid.x/k, centroid.y/k, centroid.z/k, 1);
		
		radius = 0;
		ListIterator<Vector4f> itr = positions.listIterator(0);
		while(itr.hasNext()){
			Vector4f v = new Vector4f(itr.next());
			v.sub(centroid);
			if(v.length()>radius)
				radius = v.length();
		}
	}

	public void setVertexData(VertexData vertexData)
	{
		this.vertexData = vertexData;
	}
	public VertexData getVertexData()
	{
		return vertexData;
	}
	
	public void setTransformation(Matrix4f t)
	{
		this.t = t;
	}
	
	public Matrix4f getTransformation()
	{
		return t;
	}
	
	/**
	 * Set a reference to a material for this shape.
	 * 
	 * @param material
	 * 		the material to be referenced from this shape
	 */
	public void setMaterial(Material material)
	{
		this.material = material;
	}

	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public Material getMaterial()
	{
		return material;
	}

}
