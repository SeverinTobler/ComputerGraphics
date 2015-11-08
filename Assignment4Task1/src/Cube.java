/*
 * Assignment 1, Computer Graphics
 * Severin Tobler, 28.09.2015
 * (just wrapped the given code into a class)
 */

import jrtr.*;

public class Cube extends Shape {

	public Cube(RenderContext renderContext, float x, float y, float z) {
		super(calculateVertexData(renderContext, x, y, z));
	}


	private static VertexData calculateVertexData(RenderContext renderContext, float xLength, float yLength, float zLength){
		float x = xLength/2, y = yLength/2, z = zLength/2;
		// Make a simple geometric object: a cube

		// The vertex positions of the cube
		float v[] = {-x,-y,z, x,-y,z, x,y,z, -x,y,z,		// front face
				-x,-y,-z, -x,-y,z, -x,y,z, -x,y,-z,	// left face
				x,-y,-z,-x,-y,-z, -x,y,-z, x,y,-z,		// back face
				x,-y,z, x,-y,-z, x,y,-z, x,y,z,		// right face
				x,y,z, x,y,-z, -x,y,-z, -x,y,z,		// top face
				-x,-y,z, -x,-y,-z, x,-y,-z, x,-y,z};	// bottom face

		// The vertex normals 
		float n[] = {0,0,1, 0,0,1, 0,0,1, 0,0,1,			// front face
				-1,0,0, -1,0,0, -1,0,0, -1,0,0,		// left face
				0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,		// back face
				1,0,0, 1,0,0, 1,0,0, 1,0,0,			// right face
				0,1,0, 0,1,0, 0,1,0, 0,1,0,			// top face
				0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0};		// bottom face

		// The vertex colors
		float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
				0,1,0, 0,1,0, 0,1,0, 0,1,0,
				1,1,0, 1,1,0, 1,1,0, 1,1,0,
				0,1,1, 0,1,1, 0,1,1, 0,1,1,
				1,0,1, 1,0,1, 1,0,1, 1,0,1,
				0,0,1, 0,0,1, 0,0,1, 0,0,1};

		// Texture coordinates 
		float uv[] = {0,0, 1,0, 1,1, 0,1,
				0,0, 1,0, 1,1, 0,1,
				0,0, 1,0, 1,1, 0,1,
				0,0, 1,0, 1,1, 0,1,
				0,0, 1,0, 1,1, 0,1,
				0,0, 1,0, 1,1, 0,1};

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = renderContext.makeVertexData(24);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);

		// The triangles (three vertex indices for each triangle)
		int indices[] = {0,2,3, 0,1,2,	// front face
				4,6,7, 4,5,6,			// left face
				8,10,11, 8,9,10,		// back face
				12,14,15, 12,13,14,		// right face
				16,18,19, 16,17,18,		// top face
				20,22,23, 20,21,22};	// bottom face

		vertexData.addIndices(indices);

		return vertexData;
	}


}
