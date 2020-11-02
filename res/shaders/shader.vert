#version 330 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 color;

out vData
{
    vec3 normal;
    vec4 color;
}vertex;

uniform mat4 mvp = mat4(1.0);
uniform float timer = 0.0;
#define PI 3.14159265359

void main()
{
	float y = aPos.y + (0.09*sin(2*PI*aPos.x+timer*2)+0.09*cos(2*PI*aPos.z+timer*2)) * 1;
    if(color.x==2.0)
    {
        gl_Position = mvp * vec4(aPos.x, y, aPos.z, 1.0);
        vertex.color = color;
    }
    else if(color.x==1.0)
    {
        gl_Position = mvp * vec4(aPos.x, aPos.y, aPos.z, 1.0);
        //vertex.color = vec4(vData.normal,1.0);
    }
    else
    {
        gl_Position = mvp * vec4(aPos.x, aPos.y, aPos.z, 1.0);
        vertex.color = color;
    }

}
