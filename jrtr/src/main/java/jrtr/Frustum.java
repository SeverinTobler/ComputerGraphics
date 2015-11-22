package jrtr;

import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

/**
 * Stores the specification of a viewing frustum, or a viewing
 * volume. The viewing frustum is represented by a 4x4 projection
 * matrix. You will extend this class to construct the projection 
 * matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;
	private float near, far, aspect, fov;
	
	/**
	 * Construct a default viewing frustum. The frustum is given by a 
	 * default 4x4 projection matrix.
	 */
	public Frustum()
	{
		/*
		projectionMatrix = new Matrix4f();
		float f[] = {2.f, 0.f, 0.f, 0.f, 
					 0.f, 2.f, 0.f, 0.f,
				     0.f, 0.f, -1.02f, -2.02f,
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
		*/

		setNear(1f);
		setFar(100.5f);
		setAspect(1f);
		setFov(0.9273f);
	}
	
	public Plain[] getBoundaryPlains(){
		Plain[] boundary = new Plain[6];
		
		Matrix4f inv = new Matrix4f(projectionMatrix);
		
		try{
			inv.invert();
		} catch(Exception e){
			System.err.println("Could not invert matrix!");
		}
		
		Vector4f p0 = new Vector4f(-1,-1,-1,1);
		inv.transform(p0);
		p0.scale(1/p0.w);
		Vector4f p1 = new Vector4f(1,-1,-1,1);
		inv.transform(p1);
		p1.scale(1/p1.w);
		Vector4f p2 = new Vector4f(1,-1,1,1);
		inv.transform(p2);
		p2.scale(1/p2.w);
		Vector4f p3 = new Vector4f(-1,-1,1,1);
		inv.transform(p3);
		p3.scale(1/p3.w);
		Vector4f p4 = new Vector4f(-1,1,-1,1);
		inv.transform(p4);
		p4.scale(1/p4.w);
		Vector4f p5 = new Vector4f(1,1,-1,1);
		inv.transform(p5);
		p5.scale(1/p5.w);
		Vector4f p6 = new Vector4f(1,1,1,1);
		inv.transform(p6);
		p6.scale(1/p6.w);
		Vector4f p7 = new Vector4f(-1,1,1,1);
		inv.transform(p7);
		p7.scale(1/p7.w);
		
		boundary[0] = new Plain(p0,p1,p5);
		boundary[1] = new Plain(p1,p2,p5);
		boundary[2] = new Plain(p7,p6,p3);
		boundary[3] = new Plain(p4,p7,p3);
		boundary[4] = new Plain(p5,p6,p4);
		boundary[5] = new Plain(p1,p0,p2);
		
		return boundary;
	}
	
	public void setNear(float near){
		this.near = near;
		update();
	}
	public void setFar(float far){
		this.far = far;
		update();
	}
	public void setAspect(float aspect){
		this.aspect = aspect;
		update();
	}
	public void setFov(float fov){
		this.fov = fov;
		update();
	}
	public void setFovDec(float fov){
		this.fov = (float) (fov/180*Math.PI);
		update();
	}
	
	
	public float getNear(){
		return near;
	}
	public float getFar(){
		return far;
	}
	public float getAspect(){
		return aspect;
	}
	public float getFov(){
		return fov;
	}
	
	
	/**
	 * Return the 4x4 projection matrix, which is used for example by 
	 * the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	private void update(){
		projectionMatrix = new Matrix4f();
		float f[] = {(float) (1/(aspect*Math.tan(fov/2))), 0.f, 0.f, 0.f, 
					 0.f, (float) (1/Math.tan(fov/2)), 0.f, 0.f,
				     0.f, 0.f, (float) (near+far)/(near-far), (float) (2*near*far/(near-far)),
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
	}
}
