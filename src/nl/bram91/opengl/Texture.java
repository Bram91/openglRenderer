package nl.bram91.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class Texture 
{
	
	private int texture;
	public int width, height;
	
	//Only supports PNG images
	public Texture(String path)
	{
		ByteBuffer imgData = null;
		try
		{
			imgData = loadImage("" + path);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
		}
		
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, imgData);
		
		stbi_image_free(imgData); //frees up resources
		glBindTexture(GL_TEXTURE_2D, 0); //unbind
	}
	
	private ByteBuffer loadImage(String path)
	{
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer imgData;
		//Aquire texture data using STB (lovely library)
		try(MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			imgData = stbi_load(path, w, h, channels, 0);
			if(imgData == null)
			{
				throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
			}
			width = w.get(0);
			height = h.get(0);
		}
		return imgData;
	}
	
	public void destory()
	{
		GL11.glDeleteTextures(texture);
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, texture);
	}
	
	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}
