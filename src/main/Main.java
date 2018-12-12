package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.*;
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

	public static void main(String[] args) {
		GLFWErrorCallback.createPrint(System.err).set();
		glfwInit();
		glfwDefaultWindowHints();
	
		long l = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		glfwMakeContextCurrent(l);
		glfwShowWindow(l);
		
		GLFWKeyCallback keyCallback;
		
		glfwSetKeyCallback(l,keyCallback = new GLFWKeyCallback(){

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(l, true);
				}
				
			}
			
		});
		
		GL.createCapabilities();
		glClearColor(1.0f,0.0f,0.0f,0.0f);
		
		int vertexShader = -1;
		int fragmentShader = -1;
		Scanner shaderScanner;
		int[] success = new int[1];
		
		vertexShader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
		StringBuilder vertexSrc = new StringBuilder();
		File vertexFile = new File("src\\files\\VertexShader");
		try {
			shaderScanner = new Scanner(vertexFile);
			while(shaderScanner.hasNextLine()) {
				vertexSrc.append(shaderScanner.nextLine()).append(System.getProperty("line.separator"));
			}
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		GL30.glShaderSource(vertexShader, vertexSrc.toString());
		GL30.glCompileShader(vertexShader);
		
		GL30.glGetShaderiv(vertexShader, GL30.GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			System.out.println("VERT::ERROR::" + GL30.glGetShaderInfoLog(vertexShader));	
		}
		
		fragmentShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
		StringBuilder fragSrc = new StringBuilder();
		File fragFile = new File("src\\files\\FragmentShader");
		try {
			shaderScanner = new Scanner(fragFile);
			while(shaderScanner.hasNextLine()) {
				fragSrc.append(shaderScanner.nextLine()).append(System.getProperty("line.separator"));
			}
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		GL30.glShaderSource(fragmentShader , fragSrc.toString());
		GL30.glCompileShader(fragmentShader );
		
		GL30.glGetShaderiv(fragmentShader , GL30.GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			System.out.println("FRAG::ERROR::" + GL30.glGetShaderInfoLog(fragmentShader ));	
		}
		
		int mainProgram = GL30.glCreateProgram();
		GL30.glAttachShader(mainProgram, vertexShader);
		GL30.glAttachShader(mainProgram, fragmentShader);
		GL30.glLinkProgram(mainProgram);
		GL30.glGetProgramiv(mainProgram, GL30.GL_LINK_STATUS, success);
		
		if(success[0] == 0) {
			System.out.println("PROGRAMLINK::ERROR::"+GL30.glGetProgramInfoLog(mainProgram));
		}
		
		//ObjectMachine manModel = prepModel("src\\files\\freeSmoothMan.obj");
		ObjectMachine storageModel = prepModel("src\\files\\cube.obj");
		
		//IMPORTANT, IMPORTANT, IMPORTANT
		int[] VBOs = new int[2];
		GL30.glGenBuffers(VBOs);
		int manVBO = VBOs[0];
		int cubeVBO = VBOs[1];
		
		float vertices[] = {
			    -0.5f, -0.5f, 0.0f,
			     0.5f, -0.5f, 0.0f,
			     0.0f,  0.5f, 0.0f
			};
		
		
		
		
		
		
		while ( !glfwWindowShouldClose(l)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			
			GL30.glUseProgram(mainProgram);
			
			GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, manVBO);
			GL30.glBufferData(GL30.GL_ARRAY_BUFFER,vertices,GL30.GL_STATIC_DRAW);
			GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
			GL30.glEnableVertexAttribArray(0);
			GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);
			
			glfwSwapBuffers(l); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	
	public static ObjectMachine prepModel(String fileLoc) {
		File manFile = new File(fileLoc);
		Scanner fileScanner;
		
		try {
			fileScanner = new Scanner(manFile);
			ObjectMachine modelMaker = new ObjectMachine();
			
			while (fileScanner.hasNextLine()) {
				String fileLine = fileScanner.nextLine();
				String[] tokens = fileLine.split(" ");
				String keyToken = tokens[0];
				if(keyToken.equals("v")) {
					float[] vertex = new float[3];
					vertex[0] = Float.parseFloat(tokens[1]);
					vertex[1] = Float.parseFloat(tokens[2]);
					vertex[2] = Float.parseFloat(tokens[3]);
					modelMaker.addVertex(vertex);
				}
				else if(keyToken.equals("vn")) {
					float[] normal = new float[3];
					normal[0] = Float.parseFloat(tokens[1]);
					normal[1] = Float.parseFloat(tokens[2]);
					normal[2] = Float.parseFloat(tokens[3]);
					modelMaker.addNormal(normal);
					
				}
				else if(keyToken.equals("f")) {
					String[] faceTokens = fileLine.split("(\\s|\\/+)");
					
					Face newFace = new Face();
					
					int[] vertIndices = new int[3];
					vertIndices[0] = Integer.parseInt(faceTokens[1]);
					vertIndices[1] = Integer.parseInt(faceTokens[3]);
					vertIndices[2] = Integer.parseInt(faceTokens[5]);
					newFace.addVertices(vertIndices);
					
					
					modelMaker.addFace(newFace);
				}
				fileScanner.close();
				return modelMaker;
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return null;
	}

}
