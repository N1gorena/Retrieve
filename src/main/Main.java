package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import com.sun.prism.impl.BufferUtil;

import camara.CameraListener;
import shaderBuilder.ProgramLinker;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.*;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

	private static ArrayList<StowBox> objects = new ArrayList<StowBox>();
	
	public static void main(String[] args) {
		StowBox s1 = new StowBox(-5.0f,0.0f,0.0f);
		StowBox s2 = new StowBox(5.0f,0.0f,0.0f);
		objects.add(s1);
		objects.add(s2);
		
		GLFWErrorCallback.createPrint(System.err).set();
		glfwInit();
		glfwDefaultWindowHints();
	
		long l = glfwCreateWindow(1200, 900, "Hello World!", NULL, NULL);
		glfwMakeContextCurrent(l);
		glfwShowWindow(l);
		
		CameraListener cam = new CameraListener();
		
		glfwSetKeyCallback(l,cam);
		glfwSetCursorPosCallback(l, cam.getMause());
		glfwSetInputMode(l, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		GL.createCapabilities();
		glClearColor(1.0f,0.0f,0.0f,0.0f);
		
		ProgramLinker program = new ProgramLinker("src\\files\\VertexShader","src\\files\\FragmentShader");
		
		ObjectMachine manModel = prepModel("src\\files\\freeSmoothMan.obj");
		ObjectMachine storageModel = prepModel("src\\files\\cube.obj");
		
		//IMPORTANT, IMPORTANT, IMPORTANT
		int[] VAOs = new int[3];
		GL30.glGenVertexArrays(VAOs);
		
		
		int[] VBOs = new int[2];
		GL30.glGenBuffers(VBOs);
		int manVBO = VBOs[0];
		int manEBO = VBOs[1];
		
		float vertices[] = {
			     0.5f,  0.5f, 0.0f,  // top right
			     0.5f, -0.5f, 0.0f,  // bottom right
			    -0.5f, -0.5f, 0.0f,  // bottom left
			    -0.5f,  0.5f, 0.0f   // top left 
			};
		int indices[] = {  // note that we start from 0!
			    0, 1, 3,   // first triangle
			    1, 2, 3    // second triangle
			};  
		
		vertices = storageModel.getTriangles();
		indices = storageModel.getIndices();
		
		GL30.glBindVertexArray(VAOs[0]);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, manVBO);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER,vertices,GL30.GL_STATIC_DRAW);
		
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, manEBO);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
		
		GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		GL30.glEnableVertexAttribArray(0);
		GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL30.GL_UNSIGNED_INT,0);
		GL30.glBindVertexArray(0);
		
		GL30.glBindVertexArray(VAOs[1]);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, manVBO);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER,vertices,GL30.GL_STATIC_DRAW);
		
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, manEBO);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
		
		GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		GL30.glEnableVertexAttribArray(0);
		GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL30.GL_UNSIGNED_INT,0);
		GL30.glBindVertexArray(0);
		
		
		Matrix4f box1Location = new Matrix4f().translate(-5.0f, 0.0f, 0.0f);
		FloatBuffer box1Buffer = BufferUtils.createFloatBuffer(16);
		box1Location.get(box1Buffer);
		
		Matrix4f box2Location = new Matrix4f().translate(5.0f, 0.0f, 0.0f);
		FloatBuffer box2Buffer = BufferUtils.createFloatBuffer(16);
		box2Location.get(box2Buffer);
		
		while ( !glfwWindowShouldClose(l)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			
			GL30.glUseProgram(program.getProgram());
			int camLoc = GL30.glGetUniformLocation(program.getProgram(), "camera");
			int transLocation = GL30.glGetUniformLocation(program.getProgram(),"locationMatrix" );
			GL30.glUniformMatrix4fv(camLoc, false, cam.getCamara());
			
			
			GL30.glBindVertexArray(VAOs[0]);
			GL30.glUniformMatrix4fv(transLocation, false, box1Buffer);
			GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL30.GL_UNSIGNED_INT,0);
			
			GL30.glBindVertexArray(VAOs[1]);
			GL30.glUniformMatrix4fv(transLocation, false, box2Buffer);
			GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL30.GL_UNSIGNED_INT,0);
			
			
			//Gotta be quicker
			glfwSwapBuffers(l); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
			//MyEvents
			for(int i = 0; i < objects.size() ; i++) {
				Vector3f result = new Vector3f();;
				cam.getPosition().sub(objects.get(i).getPosition(), result);
				if(result.length() <= 2.0f && glfwGetKey(l,GLFW_KEY_F) == GLFW_PRESS) {
					System.out.println("here");
				}
			}
		}
	}
	
	public static ObjectMachine prepModel(String fileLoc) {
		File manFile = new File(fileLoc);
		Scanner fileScanner;
		ObjectMachine modelMaker = new ObjectMachine();
		try {
			fileScanner = new Scanner(manFile);
			
			
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
					vertIndices[0] = Integer.parseInt(faceTokens[1]) - 1;
					vertIndices[1] = Integer.parseInt(faceTokens[3]) - 1;
					vertIndices[2] = Integer.parseInt(faceTokens[5]) - 1;
					newFace.addVertices(vertIndices);
					
					
					modelMaker.setVertexNormal(Integer.parseInt(faceTokens[1])-1,Integer.parseInt(faceTokens[2])-1);
					modelMaker.setVertexNormal(Integer.parseInt(faceTokens[3])-1,Integer.parseInt(faceTokens[4])-1);
					modelMaker.setVertexNormal(Integer.parseInt(faceTokens[5])-1,Integer.parseInt(faceTokens[6])-1);
					
					modelMaker.addFace(newFace);
				}
				
				
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
		return modelMaker;
	}

}
