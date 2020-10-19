//#version 330 core
//layout (points) in;
//layout (triangle_strip, max_vertices = 5) out;
//
//void build_house(vec4 position)
//{
//    gl_Position = position + vec4(-0.2, -0.2, 0.0, 0.0);    // 1:bottom-left
//    EmitVertex();
//    gl_Position = position + vec4( 0.2, -0.2, 0.0, 0.0);    // 2:bottom-right
//    EmitVertex();
//    gl_Position = position + vec4(-0.2,  0.2, 0.0, 0.0);    // 3:top-left
//    EmitVertex();
//    gl_Position = position + vec4( 0.2,  0.2, 0.0, 0.0);    // 4:top-right
//    EmitVertex();
//    gl_Position = position + vec4( 0.0,  0.4, 0.0, 0.0);    // 5:top
//    EmitVertex();
//    EndPrimitive();
//}
//
//void main() {
//    build_house(gl_in[0].gl_Position);
//}
#version 330

layout (triangles) in;       //points,lines,lines_adjacency,triangles,triangles_adjacency
//layout (triangle_strip) out; //points,line_strip,triangle_strip
layout (triangle_strip, max_vertices = 3) out;

in vData
{
    vec3 normal;
    vec4 color;
}vertices[];

out fData
{
    vec3 normal;
    vec4 color;
}frag;

void main()
{
    int i;
    for(i = 0;i < gl_in.length();i++)
    {
        frag.normal = vertices[i].normal;
        frag.color = vertices[i].color;
        gl_Position = gl_in[i].gl_Position;
        EmitVertex();
    }
    EndPrimitive();
}