/*
 * Assignment 1, Computer Graphics
 * Severin Tobler, 28.09.2015
 * 
 */

import jrtr.*;
import javax.vecmath.*;

public class Airplane {
	Shape body;
	Shape wings;
	Shape propeller1;
	Shape propeller2;
	Matrix4f transformation = new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);
	float propellerAngle;
	
	public Airplane(SimpleSceneManager sceneManager, RenderContext renderContext){
		body = new Cylinder(renderContext, 50, 0.25f, 2);
		wings = new Cube(renderContext, 0.5f, 4f, 0.2f); 
		propeller1 = new Cube(renderContext, 0.05f, 1f, 0.1f);
		propeller2 = new Cube(renderContext, 0.05f, 0.1f, 1f);
		
		sceneManager.addShape(body);
		sceneManager.addShape(wings);
		sceneManager.addShape(propeller1);
		sceneManager.addShape(propeller2);
		
		// build airplane at origin
		setTransformation(transformation);
	}
	
	public void setTransformation(Matrix4f t){
		transformation = t;
		
		Matrix4f t_body = new Matrix4f(t);
		Matrix4f rotY = new Matrix4f();
		rotY.rotY((float) -Math.PI/2);
		t_body.mul(rotY);
		body.setTransformation(t_body);
		
		Matrix4f t_wings = new Matrix4f(t);
		Matrix4f transWing = new Matrix4f();
		transWing.setIdentity();
		transWing.setTranslation(new Vector3f(-1,0,0));
		t_wings.mul(transWing);
		wings.setTransformation(t_wings);
		
		Matrix4f t_propeller = new Matrix4f(t);
		Matrix4f transProp = new Matrix4f();
		transProp.rotX(propellerAngle);
		transProp.setTranslation(new Vector3f(0.025f,0,0));
		t_propeller.mul(transProp);
		propeller1.setTransformation(t_propeller);
		propeller2.setTransformation(t_propeller);
	}
	
	public Matrix4f getTransformation(){
		return transformation;
	}
	
	
	// updates the propeller angle
	public void update(float currentstep){
		propellerAngle = propellerAngle + currentstep;
		
		if(propellerAngle >2*Math.PI)
			propellerAngle = propellerAngle - (float) (2*Math.PI);
	}
	
}
