#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out float direct;
out vec2 frag_texcoord;
out vec4 frag_position;
out vec4 frag_normal;

void main()
{		
	// Compute diffuse shading contribution
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	//direct = max(dot(modelview * vec4(normal,0), lightDirection[0]),0);
	
	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;
	
	frag_position = modelview * position;
	frag_normal = modelview * vec4(normal,0);

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	//gl_Position = projection * modelview * position;
	gl_Position = projection * frag_position;
}
