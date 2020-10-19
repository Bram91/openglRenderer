package nl.bram91.opengl;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

public class Main implements Runnable
{
	
	private long window;
	public static int width = 900, height = 900;
	private Thread t;
	public boolean running;
	private Scene scene;
	
	public void start()
	{
		running = true;
		t = new Thread(this);
		t.start();
	}
	
	public void init()
	{
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(!glfwInit())
			throw new IllegalStateException("GLFW failed to init");
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		window = glfwCreateWindow(width, height, "Post Processing effects", 0, 0);
		if(window == 0)
			throw new RuntimeException("Failed to create a window");
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width()-width)/2, (vidmode.height()-height)/2);
		glfwSetScrollCallback(window, scroll_callback);
		glfwMakeContextCurrent(window);
		GL.createCapabilities(); //Generate OpenGL bindings
		GL30.glEnable(GL30.GL_DEPTH_TEST);
		
		scene = new Scene();
		glfwShowWindow(window);
	}
	private GLFWScrollCallback scroll_callback = new GLFWScrollCallback() {
		@Override
		public void invoke(long window, double scrollX, double scrollY) {
			scene.setScroll(scrollY);
		}
	};
	public void render()
	{
		GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		scene.render();
		glfwSwapBuffers(window);
	}
	
	public void run()
	{
		init();
		
		float tps = 60.0f;
		double interval = 1e9/tps;
		double delta = 0;
		long last = System.nanoTime();
		long now;
		
		int frames = 0;
		int updates = 0;
		long timer = System.currentTimeMillis();
		
		GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_CULL_FACE);
		GL30.glClearColor(0.12f, 0.12f, 0.12f, 1.0f);
		while(!glfwWindowShouldClose(window) && running)
		{
			now = System.nanoTime();
			delta += (now - last)/interval;
			last = now;
			
			if(delta >= 1)
			{
				delta--;
				updates++;
				glfwPollEvents();
				scene.update();
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer >= 1000)
			{
				glfwSetWindowTitle(window, "FPS: " + frames + " | UPS: " + updates);
				timer = System.currentTimeMillis();
				frames = 0;
				updates = 0;
			}
		}
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
	public static void main(String[] args) 
	{
		new Main().start();
	}

}
