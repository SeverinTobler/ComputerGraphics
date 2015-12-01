import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class Bezier {
	private Matrix4f[] C;
	
	public Bezier(){
	}
	
	public boolean drawCurve(int numSegments, float[] controlPoints){
		if((numSegments-1)*3+4 != controlPoints.length/2)
			return false;
		
		Matrix4f B = new Matrix4f(-1,3,-3,1, 3,-6,3,0, -3,3,0,0, 1,0,0,0);
		C = new Matrix4f[numSegments];
		for(int i=0; i<numSegments; i++){
			Matrix4f G = new Matrix4f(controlPoints[i*6], controlPoints[i*6+2], controlPoints[i*6+4], controlPoints[i*6+6], 
						controlPoints[i*6+1], controlPoints[i*6+3], controlPoints[i*6+5], controlPoints[i*6+7],
						0,0,0,0,
						0,0,0,0);
			C[i] = new Matrix4f();
			C[i].mul(G, B);
		}
		
		return true;
	}
	
	public BezierOut getShapeData(int numPoints, int numRotStep){
		float eps = 0.001f;
		Vector4f[] points  = new Vector4f[numPoints];
		Vector4f[] normals  = new Vector4f[numPoints];
		for(int i=0; i<numPoints; i++){
			// points
			float u = (float)(C.length*i)/(numPoints-1);
			points[i] = interpolate(u);
			
			// normals
			normals[i] = new Vector4f();
			if(i != numPoints-1){
				Vector4f point = interpolate(u+eps);	// forward interpolation
				normals[i].sub(point, points[i]);
			}else{
				Vector4f point = interpolate(u-eps);	// backward interpolation for last point
				normals[i].sub(points[i], point);
			}
			// compute normalized normal from tangent
			float temp = normals[i].x;
			normals[i].x = normals[i].y;
			normals[i].y = -temp;
			normals[i].scale(1/normals[i].length());
			
		}
		
		
		
		numRotStep = numRotStep+1;
		float[] c = new float[3*numPoints*numRotStep];
		float[] n = new float[3*numPoints*numRotStep];
		float[] v = new float[3*numPoints*numRotStep];
		for(int i=0; i<numRotStep; i++){
			float angle = (float)(2*Math.PI*i/(numRotStep-1));
			Matrix4f rotY = new Matrix4f();
			rotY.rotY(angle);
			for(int j=0; j<numPoints; j++){
				Vector4f point = new Vector4f(points[j]);
				rotY.transform(point);
				v[(i*numPoints+j)*3] = point.x;
				v[(i*numPoints+j)*3+1] = point.y;
				v[(i*numPoints+j)*3+2] = point.z;
				
				Vector4f normal = new Vector4f(normals[j]);
				rotY.transform(normal);
				n[(i*numPoints+j)*3] = normal.x;
				n[(i*numPoints+j)*3+1] = normal.y;
				n[(i*numPoints+j)*3+2] = normal.z;
				
				c[(i*numPoints+j)*3+1] = (i*numPoints+j)%2;
			}
		}
		
		int[] indices = new int[(numPoints-1)*(numRotStep-1)*6];
		for(int i=0; i<numRotStep-1; i++){
			for(int j=0; j<numPoints-1; j++){
				// first triangle
				indices[(i*(numPoints-1)+j)*6] = i*numPoints+j;
				indices[(i*(numPoints-1)+j)*6+1] = i*numPoints+j+numPoints;
				indices[(i*(numPoints-1)+j)*6+2] = i*numPoints+j+1;
				// second triangle
				indices[(i*(numPoints-1)+j)*6+3] = i*numPoints+j+numPoints;
				indices[(i*(numPoints-1)+j)*6+4] = i*numPoints+j+numPoints+1;
				indices[(i*(numPoints-1)+j)*6+5] = i*numPoints+j+1;
			}
		}
		
		float[] texture = new float[numPoints*numRotStep*2];
		for(int i=0; i<numRotStep; i++){
			for(int j=0; j<numPoints; j++){
				texture[(i*numPoints+j)*2] = ((float)i)/(numRotStep-1);
				texture[(i*numPoints+j)*2+1] = ((float)j)/(numPoints-1);
			}
		}
		
		BezierOut output = new BezierOut();
		output.v = v;
		output.c = c;
		output.n = n;
		output.uv = texture;
		output.indices = indices;
		return output;
	}
	
	private Vector4f interpolate(float u){
		float t = u-((int)u);
		if(u == C.length){	// last point
			u = u-1;
			t = 1;
		}
		
		Vector4f point  = new Vector4f(t*t*t, t*t, t, 1);
		C[(int)u].transform(point);
		point.w = 1;
		return point;
	}
	
}
