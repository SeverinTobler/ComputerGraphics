/*
 * Assignment 1, Computer Graphics
 * Severin Tobler, 28.09.2015
 * 
 */

import jrtr.*;

// Normals are calculated with the approx. #segments -> inf. Is this ok?

public class Cylinder extends Shape {

	public Cylinder(RenderContext renderContext, int segments, float radius, float hight) {
		super(calculateVertexData(renderContext, segments, radius, hight));
	}


	private static VertexData calculateVertexData(RenderContext renderContext, int s, float r, float hight){
		double angleStep = 2*Math.PI/s;


		// side vertexes
		float[] vSide = new float[s*2*3];
		float[] nSide = new float[s*2*3];
		for(int i=0; i<s; i++){
			// top
			nSide[i*3] = (float) (Math.cos(i*angleStep));
			nSide[i*3+1] = (float) (Math.sin(i*angleStep));

			vSide[i*3] = r*nSide[i*3];
			vSide[i*3+1] = r*nSide[i*3+1];
			vSide[i*3+2] = hight;

			// bottom
			nSide[(s+i)*3] = nSide[i*3];
			nSide[(s+i)*3+1] = nSide[i*3+1];

			vSide[(s+i)*3] = vSide[i*3];
			vSide[(s+i)*3+1] = vSide[i*3+1];
		}

		// top disk
		float[] vTop = new float[(s+1)*3];
		float[] nTop = new float[(s+1)*3];

		vTop[2] = hight;
		nTop[2] = 1;

		for(int i=1; i<s+1; i++){
			vTop[i*3] = vSide[i*3];
			vTop[i*3+1] = vSide[i*3+1];
			vTop[i*3+2] = hight;

			nTop[i*3+2] = 1;
		}

		// bottom disk
		float[] vBottom = new float[(s+1)*3];
		float[] nBottom = new float[(s+1)*3];

		vBottom[2] = 0;
		nBottom[2] = -1;

		for(int i=1; i<s+1; i++){
			vBottom[i*3] = vSide[i*3];
			vBottom[i*3+1] = vSide[i*3+1];
			vBottom[i*3+2] = 0;

			nBottom[i*3+2] = -1;
		}

		// combine vectors
		float[] v = concat(vTop, vBottom);	// vTop starts at 0; vBottom starts at (s+1)*3; vSide starts at (s+1)*6
		v = concat(v, vSide);
		float[] n = concat(nTop, nBottom);
		n = concat(n, nSide);


		// indices
		int indices[] = new int[4*s*3];	// 4 triangles per segment
		indices[0] = 0;
		indices[1] = s;
		indices[2] = 1;

		indices[3] = (s+1)*2;
		indices[4] = (s+1)*2+s-1;
		indices[5] = (s+1)*2+s;

		indices[6] = (s+1)*2+s-1;
		indices[7] = (2*s+1)*2-1;
		indices[8] = (s+1)*2+s;

		indices[9] = (s+1)+0;
		indices[10] = (s+1)+s;
		indices[11] = (s+1)+1;

		for(int i=1; i<s; i++){
			indices[i*12] = 0;
			indices[i*12+1] = i;
			indices[i*12+2] = i+1;

			indices[i*12+3] = (s+1)*2 + i-1;
			indices[i*12+4] = (s+1)*2 + i-1+s;
			indices[i*12+5] = (s+1)*2 + i+s;

			indices[i*12+6] = (s+1)*2 + i-1;
			indices[i*12+7] = (s+1)*2 + i+s;
			indices[i*12+8] = (s+1)*2 + i;

			indices[i*12+9] = (s+1);
			indices[i*12+10] = (s+1) + i+1;
			indices[i*12+11] = (s+1) + i;
		}

		// color
		float c[] = new float[v.length];
		for(int i=0; i<s+1; i=i+2){	// top green
			c[i*3+1] = 1;
		}
		for(int i=s+1; i<(s+1)*2; i=i+2){	// bottom red
			c[i*3] = 1;
		}
		for(int i=(s+1)*2; i<(s+1)*2+2*s; i=i+2){	// side blue
			c[i*3+2] = 1;
		}

		// Texture
		//float uv[] = new float[v.length/3*2];


		VertexData vertexData = renderContext.makeVertexData(v.length/3);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
		//vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);

		vertexData.addIndices(indices);

		return vertexData;
	}


	// from stackoverflow
	private static float[] concat(float[] a, float[] b) {
		int aLen = a.length;
		int bLen = b.length;
		float[] c= new float[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

}
