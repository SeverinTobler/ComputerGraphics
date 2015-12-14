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
uniform sampler2D myNormalMap;	// for bump
uniform mat4 modelview;	// for bump

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in vec4 frag_position;
in vec4 frag_normal;
in vec4 frag_tangent;
in vec4 frag_bitangent;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		

	vec4 n = modelview * mat4(frag_tangent,frag_bitangent,frag_normal,vec4(0,0,0,0)) * texture(myNormalMap, frag_texcoord);
	n = normalize(n);
	n = frag_normal;

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
			r = length(L);	// distance to light source
			L = L/r;
			c_d = c_diffuse[i]/(r*r);		
		}
		
		c = c + c_d*k_diffuse*(max(dot(L,n),0));
	}
 
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = vec4(c,1) * texture(myTexture, frag_texcoord);
	//frag_shaded = texture(myTexture, frag_texcoord);
	//frag_shaded = texture(myNormalMap, frag_texcoord);
	
}

