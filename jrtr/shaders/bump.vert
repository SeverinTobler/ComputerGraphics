#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map and bump map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec3 tangent;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec2 frag_texcoord;	// same as bump map coordinates
out vec4 frag_position;
out vec4 frag_normal;
out vec4 frag_tangent;
out vec4 frag_bitangent;

void main()
{		
	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;
	
	frag_position = modelview * position;
	frag_normal = modelview * vec4(normal,0);
	frag_tangent = modelview * vec4(tangent,0);
	frag_bitangent = modelview * vec4(cross(tangent,normal),0);
	
	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	//gl_Position = projection * modelview * position;
	gl_Position = projection * frag_position;
}
