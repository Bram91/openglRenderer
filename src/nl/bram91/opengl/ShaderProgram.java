package nl.bram91.opengl;

import nl.bram91.opengl.utils.FileUtils;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram
{
	private int program;
	private Map<String, Integer> locations = new HashMap<String, Integer>(); //collect locations to save computation time
	
	public ShaderProgram(String vPath, String fPath, String gPath)
	{
		int vert = compile("/shaders/" + vPath, ShaderType.VERT);
		int frag = compile("/shaders/" + fPath, ShaderType.FRAG);
		int geom = compile("/shaders/" + gPath, ShaderType.GEOM);
		
		program = glCreateProgram();
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		glAttachShader(program, geom);
		glValidateProgram(program);
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
		{
			System.err.println("ERROR LINKING PROGRAM");
			System.err.println(glGetProgramInfoLog(program));
		}
		
		//Free resources
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		glDetachShader(program, geom);
		glDeleteShader(vert);
		glDeleteShader(frag);
		glDeleteShader(geom);
	}
	
	private int compile(String path, ShaderType type)
	{
		int shader = 0;
		if(type == ShaderType.VERT)
			shader = glCreateShader(GL_VERTEX_SHADER);
		else if(type == ShaderType.FRAG)
			shader = glCreateShader(GL_FRAGMENT_SHADER);
		else if(type == ShaderType.GEOM)
			shader = glCreateShader(GL_GEOMETRY_SHADER);
		
		GL20.glShaderSource(shader, FileUtils.readAsString(path));
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.err.println("ERROR COMPILING " + type + " SHADER");
			System.err.println(glGetShaderInfoLog(shader));
		}
		
		return shader;
	}
	
	public void unbind()
	{
		glUseProgram(0);
	}
	
	public void bind()
	{
		glUseProgram(program);
	}
	
	public int getLocation(String name)
	{
		//If not in map yet.
		if(!locations.containsKey(name))
		{
			int loc = glGetUniformLocation(program, name);
			if(loc == -1) //means invalid location
			{
				System.err.println("Invalid uniform name: '" + name +"'");
				return -1;
			}
			locations.put(name, loc);
		}
		return locations.get(name);
	}
	
	//Remember to have a program in use before setting uniforms!
	public void setVec3(String name, Vector3f v)
	{
		glUniform3f(getLocation(name), v.x, v.y, v.z);
	}
	
	public void setInt(String name, int v)
	{
		glUniform1i(getLocation(name), v);
	}
	
	public void setFloat(String name, float v)
	{
		glUniform1f(getLocation(name), v);
	}
	
	public void setMat4f(String name, Matrix4f mat)
	{
		try(MemoryStack stack = MemoryStack.stackPush())
		{
			glUniformMatrix4fv(getLocation(name), false, mat.get(stack.mallocFloat(16)));
		}
	}
	
	private enum ShaderType
	{
		VERT, FRAG, GEOM
	}

}
