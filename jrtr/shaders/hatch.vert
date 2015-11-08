#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;

// Output variables for fragment shader
out vec4 frag_position;
out vec4 frag_normal;

void main()
{
	frag_position = model * position;
	frag_normal = model * vec4(normal,0);
	
	gl_Position = projection * view * frag_position;
}
