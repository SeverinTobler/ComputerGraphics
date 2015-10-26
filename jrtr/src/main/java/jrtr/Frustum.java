package jrtr;

import javax.vecmath.Matrix4f;

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
