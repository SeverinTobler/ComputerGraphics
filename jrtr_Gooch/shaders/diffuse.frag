#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;
uniform vec4 lightDirection[MAX_LIGHTS];
uniform vec4 lightPosition[MAX_LIGHTS];
uniform vec3 c_diffuse[MAX_LIGHTS];
uniform int lightType[MAX_LIGHTS];
uniform int nLights;
uniform vec3 k_diffuse;

// Variables passed in from the vertex shader
in float direct;
in vec2 frag_texcoord;
in vec4 frag_position;
in vec4 frag_normal;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	float r = 0;
	vec3 c = vec3(0,0,0);
	for(int i=0; i<nLights; i++){
		
		vec3 c_d = vec3(0,0,0);
		vec4 L = vec4(0,0,0,0);
	
		// directional light source (default light source):
		if(lightType[i]==0){
			c_d = c_diffuse[i];
			L = lightDirection[i];
		}
	
		// point light source:
		if(lightType[i]==1){
			L = lightPosition[i]-frag_position;
			r = sqrt(L.x*L.x + L.y*L.y + L.z*L.z);	// distance to light source
			L = L/r;
			c_d = c_diffuse[i]/(r*r);		
		}
		
		c = c + c_d*k_diffuse*(dot(L,frag_normal));
	}
 
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = vec4(c,1) * texture(myTexture, frag_texcoord);
}

