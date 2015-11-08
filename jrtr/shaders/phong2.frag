#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;
uniform vec4 lightDirection[MAX_LIGHTS];
uniform vec4 lightPosition[MAX_LIGHTS];
uniform vec3 c_diffuse[MAX_LIGHTS];
uniform vec3 c_specular[MAX_LIGHTS];
uniform vec3 c_ambient[MAX_LIGHTS];
uniform int lightType[MAX_LIGHTS];
uniform int nLights;
uniform vec4 camera;
uniform float phong_exponent;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in vec4 frag_position;
in vec4 frag_normal;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	float r = 0;
	vec3 c = vec3(0,0,0);
	vec4 tex = texture(myTexture, frag_texcoord);
	vec3 k_diffuse = vec3(tex);
	vec3 k_ambient = vec3(tex);
	vec3 k_specular = vec3(1,1,1)*max(tex.x+tex.y+tex.z, 1);
	
	for(int i=0; i<nLights; i++){
		
		vec3 c_s = vec3(0,0,0);
		vec3 c_d = vec3(0,0,0);
		vec4 L = vec4(0,0,0,0);
	
		// directional light source (default light source):
		if(lightType[i]==0){
			c_d = c_diffuse[i];
			c_s = c_specular[i];
			L = -lightDirection[i];
		}
	
		// point light source:
		if(lightType[i]==1){
			L = lightPosition[i]-frag_position;
			r = length(L);	// distance to light source
			L = L/r;
			c_d = c_diffuse[i]/(r*r);	
			c_s = c_specular[i]/(r*r);
		}
		
		vec4 R = reflect(L, frag_normal);
		vec4 e = camera - frag_position;
		c = c + c_d*k_diffuse*(dot(L,frag_normal)) + c_s*k_specular*(pow(max(dot(R,e),0), phong_exponent)) + c_ambient[i]*k_ambient;
	}
 
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = vec4(c,1) * tex;
}

