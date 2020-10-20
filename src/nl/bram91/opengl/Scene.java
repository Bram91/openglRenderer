package nl.bram91.opengl;

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
	
	int noOfCells = 20;
	float vertArr[];
	float angle = 0.01f;
	public void test()
	{
		ArrayList<Float> verts = new ArrayList<Float>();
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




		String model = FileUtils.readAsString("/bunny.obj");
		String[] parts = model.split("\n");
		ArrayList<Float> vertexes = new ArrayList();
		zoom = 0;
		int count = 0;
		for (String vertices : parts)
		{
			if (vertices.startsWith("v "))
			{
				count++;
			}
		}
		System.out.println(count);
		for (String vertices : parts) {
			if(vertices.startsWith("v "))
			{
				String cleaned = vertices.replace("v ", "");
				for (String vertex : cleaned.split(" "))
				{
					if(count%4==0)
					{
						vertexes.add(Float.parseFloat(vertex));
					}
					else
					{
						verts.add(Float.parseFloat(vertex));
					}
				}
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

		int[] order = {0,1,2,2,3,0};
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
		}

		vertArr = new float[verts.size()];
		for(int j = 0; j < verts.size(); j++)
		{
			vertArr[j] = verts.get(j);
		}

		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertArr), GL_STATIC_DRAW);
		glBindVertexArray(vao);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
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
		model.translate(new Vector3f(0.0f, 0.0f , -6.2f + zoom)).rotateY(angle).rotateZ(0.2f);
		angle += 0.01;
		shader.bind();
		shader.setFloat("timer", (float)GLFW.glfwGetTime());
	}

	public void setScroll(double scrollY)
	{
		zoom += scrollY;
	}
}
