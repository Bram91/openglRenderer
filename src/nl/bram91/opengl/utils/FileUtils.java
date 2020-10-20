package nl.bram91.opengl.utils;

import java.io.*;

public class FileUtils 
{
	
	public static String readAsString(String path)
	{
		InputStream file = null;
		try {
			final String dir = System.getProperty("user.dir");
			file = new FileInputStream(new File(dir+"\\res"+path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
