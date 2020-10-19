package nl.bram91.opengl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils 
{
	
	public static String readAsString(String path)
	{
		InputStream file = Class.class.getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file));
		String result = "";
		try 
		{
			String next;
			while((next = reader.readLine()) != null)
				result += next + "\n";
			reader.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}

}
