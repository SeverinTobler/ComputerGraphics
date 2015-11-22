package jrtr;

import javax.vecmath.*;
/**
 * Stores the properties of a light source.
 */
public class Light {
	
	// Types of light sources
	public enum Type {
		DIRECTIONAL, POINT, SPOT
	}

	public Light()
	{
		// Default light parameters
		direction = new Vector3f(0.f,0.f,1.f);
		position = new Vector3f(0.f,0.f,1.f);
		type = Type.DIRECTIONAL;
		diffuse = new Vector3f(1.f,1.f,1.f);
		ambient = new Vector3f(0.f,0.f,0.f);
		specular = new Vector3f(1.f,1.f,1.f);
		attenuation = new Vector3f();
		spotDirection = new Vector3f(0.f,0.f,1.f);
		spotExponent = 0.f;
		spotCutoff = 180.f;
	}
	
	public Light(Light other)
	{
		// Copy a Light object
		direction = new Vector3f(other.direction);
		position = new Vector3f(other.position);
		type = other.type;
		diffuse = new Vector3f(other.diffuse);
		ambient = new Vector3f(other.ambient);
		specular = new Vector3f(other.specular);
		attenuation = new Vector3f(other.attenuation);
		spotDirection = new Vector3f(other.spotDirection);
		spotExponent = other.spotExponent;
		spotCutoff = other.spotCutoff;
	}

	public Vector3f direction;
	public Vector3f position;
	public Vector3f diffuse;
	public Vector3f specular;
	public Vector3f ambient;
	public Vector3f attenuation;
	public Vector3f spotDirection;
	public float spotExponent;
	public float spotCutoff;
	public Type type;
}