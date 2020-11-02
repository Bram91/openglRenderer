package nl.bram91.opengl;

import java.awt.Color;
import java.nio.FloatBuffer;
import nl.bram91.opengl.utils.BufferUtils;
import nl.bram91.opengl.utils.FileUtils;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL15;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Scene
{
	
	private int vao, vbo;
	private ShaderProgram shader;
	
	private Matrix4f projection, model;
	private float zoom = 0.0f;
	
	public Scene()
	{
		test();
		if(config.points)
		{
			shader = new ShaderProgram("shader.vert", "shader.frag", "pointShader.geom");
		}
		else
		{
			shader = new ShaderProgram("shader.vert", "shader.frag", "triangleShader.geom");
		}
		projection = new Matrix4f().perspective(45.0f, (float)Main.width/(float)Main.height, 0.1f, 10000.0f);
		model = new Matrix4f();
	}
	public static float hueToRgb(float p, float q, float t) {
		if (t < 0f)
			t += 1f;
		if (t > 1f)
			t -= 1f;
		if (t < 1f/6f)
			return p + (q - p) * 6f * t;
		if (t < 1f/2f)
			return q;
		if (t < 2f/3f)
			return p + (q - p) * (2f/3f - t) * 6f;
		return p;
	}

	static public float[] hslColor(float h, float s, float l) {
		float r, g, b;

		if (s == 0f) {
			r = g = b = l; // achromatic
		} else {
			float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
			float p = 2 * l - q;
			r = hueToRgb(p, q, h + 1f/3f);
			g = hueToRgb(p, q, h);
			b = hueToRgb(p, q, h - 1f/3f);
		}
		float[] rgb = {r, g, b};
		return rgb;
	}
	private static float HueToRGB(float p, float q, float h)
	{
		if (h < 0) h += 1;

		if (h > 1 ) h -= 1;

		if (6 * h < 1)
		{
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1 )
		{
			return  q;
		}

		if (3 * h < 2)
		{
			return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
		}

		return p;
	}
	public static int[] colorHSLToRGB(float h, float s, float l)
	{
		h /= 64f;
		s /= 8f;
		l /= 128f;

		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f);
		g = Math.min(g, 1.0f);
		b = Math.min(b, 1.0f);

		return new int[]{(int)(r * 255f), (int)(g * 255f), (int)(b * 255f)};
	}

	int noOfCells = 20;
	float vertArr[];
	float angle = 0.01f;
	public void test()
	{
		ArrayList<Float> verts = new ArrayList<Float>();
		ArrayList<Float> cols = new ArrayList<Float>();
//		for(int y = 0; y < noOfCells; y++)
//		{
//			for (int x = 0; x < noOfCells + 1; x++)
//			{
//				if ((y + 1) % 2 != 0)
//				{
//					verts.add((0.0f + x) / noOfCells);
//					verts.add(0.0f);
//					verts.add((0.0f + y) / noOfCells);
//					verts.add((0.0f + x) / noOfCells);
//					verts.add(0.0f);
//					verts.add((1.0f + y) / noOfCells);
//				}
//				else
//				{
//					verts.add((0.0f + (noOfCells - x)) / noOfCells);
//					verts.add(0.0f);
//					verts.add((0.0f + y) / noOfCells);
//					verts.add((0.0f + (noOfCells - x)) / noOfCells);
//					verts.add(0.0f);
//					verts.add((1.0f + y) / noOfCells);
//				}
//			}
//		}



		System.out.println("asd");
		String model = FileUtils.readAsString("/tiles.dat");
		String[] parts = model.split("\n");
		for (String vertices : parts)
		{
			if(vertices.startsWith("c:"))
			{

				String[] arrOfStr = vertices.replace("c:","").split(",");
				if(arrOfStr[1].contains("t:1"))
				{
					cols.add(-1.0f);
					cols.add(-1.0f);
					cols.add(-1.0f);
					cols.add(-1.0f);
				}
				else
				{
					Color col = new Color(Integer.parseInt(arrOfStr[0]));
					System.out.println(col.toString());
					cols.add((float)col.getRed()/255);
					cols.add((float)col.getGreen()/255);
					cols.add((float)col.getBlue()/255);
					cols.add(1.0f);
				}
			}
			else
			{
				System.out.println(vertices);
				for (String vertex : vertices.split(","))
				{
					System.out.println(vertex);
					verts.add(Float.parseFloat(vertex) / 100);
				}
			}
		}
		model = FileUtils.readAsString("/staircase.obj");
		parts = model.split("\n");
		ArrayList<Float> vertexes = new ArrayList();
		zoom = 0;
		for (String vertices : parts) {
			if(vertices.startsWith("v "))
			{
				String cleaned = vertices.replace("v ", "");
				for (String vertex : cleaned.split(" "))
				{
					vertexes.add(Float.parseFloat(vertex));
					cols.add(-1.0f);
					cols.add(-1.0f);
					cols.add(-1.0f);
					cols.add(-1.0f);
				}
			}
		}
		int count = 0;
		for (String vertices : parts) {
			if(vertices.startsWith("f "))
			{
				String cleaned = vertices.replace("f ", "");
				if(count==0)
				{
					System.out.println(cleaned);
				}
				for (String number : cleaned.split(" "))
				{
					try
					{
						for(int i = 0; i<3; i++)
						{
							System.out.println(((Integer.parseInt(number.split("/")[0])-1)*3)+i);
							System.out.println(vertexes.get(((Integer.parseInt(number.split("/")[0])-1)*3)+i));
							verts.add(vertexes.get(((Integer.parseInt(number.split("/")[0])-1)*3)+i)/100);

						}

					}
					catch (Exception e)
					{

					}
				}
				count++;
			}
		}


		//		quad 1 2 3 4
//		==>
//		triangle 1 2 3
//		triangle 3 4 1
//		int[] order = {0,1,2,2,3,0};
//		for(int i = 0; i < cube.length; i+=12)
//		{
//			for(int q = 0; q < 6; q++)
//			{
//				for(int j = 0; j < 3; j++)
//				{
//					try
//					{
//						verts.add((float) cube[i + j + order[q] * 3] / 2);
//					}
//					catch (IndexOutOfBoundsException e)
//					{
//						//
//					}
//				}
//			}
//		}

		/*int[] order = {0,1,2,2,3,0};
		for(int i = 0; i < vertexes.size(); i+=12)
		{
			for(int q = 0; q < 6; q++)
			{
				for(int j = 0; j < 3; j++)
				{
					try
					{
						verts.add((float) vertexes.get(i + j + order[q] * 3));
					}
					catch (IndexOutOfBoundsException e)
					{
						//
					}
				}
			}
		}*/

		vertArr = new float[verts.size()];
		for(int j = 0; j < verts.size(); j++)
		{
			vertArr[j] = verts.get(j);
		}

		vao = glGenVertexArrays();
		vbo = glGenBuffers();
//		vertArr = new float[]
//			{
//				+0.0f, +0.8f,    // Top coordinate
//				-0.8f, -0.8f,    // Bottom-left coordinate
//				+0.8f, -0.8f     // Bottom-right coordinate
//			};

		float[] colors = new float[cols.size()];
		for(int j = 0; j < cols.size(); j++)
		{
			colors[j] = cols.get(j);
		}

		FloatBuffer colorsBuffer = org.lwjgl.BufferUtils.createFloatBuffer(colors.length);
		colorsBuffer.put(colors).flip();


		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		GL15.glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertArr), GL_STATIC_DRAW);

		glBindVertexArray(vao);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		int vboColID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboColID);
		glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);

		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}

	public void render()
	{
		shader.bind();
		shader.setMat4f("mvp", new Matrix4f().mul(projection).mul(model));
		glBindVertexArray(vao);

		glDrawArrays(config.getGeometryMode(), 0, vertArr.length);
		//glDrawArrays(asd[0], 0, vertArr.length);

	}
	
	public void update()
	{
		model.identity();
		model.translate(new Vector3f(0.0f+offsetX, 0.0f+offsetY , -6.2f + zoom)).rotateY(rotateY).rotateZ(rotateZ).rotateX(rotateX);
		angle += 0.01;
		//System.out.println(angle);
		shader.bind();
		shader.setFloat("timer", (float)GLFW.glfwGetTime());
	}

	public void setScroll(double scrollY)
	{
		zoom += scrollY*10;
	}
	float rotateY = 2.5f;
	float rotateZ = 2.2f;
	float rotateX = 1.0f;
	float offsetX = 0.0f;
	float offsetY = 0.0f;
	public void setRotation(int i)
	{
		if(i== 81)//Q
		{
			rotateX-=0.1;
		}
		if(i== 69)//E
		{
			rotateX+=0.1;
		}
		if(i== 87)//W
		{
			rotateY-=0.1;
		}
		if(i== 83)//S
		{
			rotateY+=0.1;
		}
		if(i== 65)//A
		{
			rotateZ-=0.1;
		}
		if(i== 68)//D
		{
			rotateZ+=0.1;
		}
		if(i== 90)//Z
		{
			offsetX -= 2.1;
		}
		if(i== 88)//X
		{
			offsetX += 2.1;
		}
		if(i== 67)//C
		{
			offsetY -= 2.1;
		}
		if(i== 86)//V
		{
			offsetY += 2.1;
		}

	}
}
