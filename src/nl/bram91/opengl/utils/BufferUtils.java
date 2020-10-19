package nl.bram91.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BufferUtils 
{
	
	public static FloatBuffer createFloatBuffer(float[] arr)
	{
		FloatBuffer result = ByteBuffer.allocateDirect(arr.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		result.put(arr);
		result.flip();
		return result;
	}

}
