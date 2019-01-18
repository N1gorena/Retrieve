package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class ObjectMachine {
	
	
	private LinkedList<Vertice> Vertices;
	private Map<Integer,Integer> VertNormal;
	private LinkedList<float[]> Normals;
	private LinkedList<Face> Faces;
	private boolean faceLinked = false;

	public ObjectMachine() {
		Vertices = new LinkedList<Vertice>();
		Normals = new LinkedList<float[]>();
		Faces = new LinkedList<Face>();
		VertNormal = new HashMap<Integer,Integer>();
	}

	public void addVertex(Vertice v) {
		Vertices.add(v);
		
	}

	public void addNormal(float[] normal) {
		Normals.add(normal);
		
	}

	public void addFace(Face newFace) {
		Faces.add(newFace);
		
	}

	public float[] getTriangles() {
		float[] toReturn = null;
		int vertCount = Vertices.size();
		LinkedList<Float> vertexDataOut = new LinkedList<Float>();
		
		
		for(int i = 0 ; i < vertCount ; i++) {
			Vertice vertexDataOg = Vertices.get(i);
			vertexDataOut.add(vertexDataOg.getX());
			vertexDataOut.add(vertexDataOg.getY());
			vertexDataOut.add(vertexDataOg.getZ());
			//transferBuffer = Normals.get(VertNormal.get(i));
			//vertexDataOut.add(transferBuffer[0]);
			//vertexDataOut.add(transferBuffer[1]);
			//vertexDataOut.add(transferBuffer[2]);
		}
		toReturn = new float[vertexDataOut.size()];
		for(int i = 0; i < vertexDataOut.size(); i++) {
			toReturn[i] = vertexDataOut.get(i);
		}
	
		
		return toReturn;
	}

	public int[] getIndices() {
		int[] toReturn = new int[Faces.size()*3];
		for(int i = 0 ; i < Faces.size(); i++) {
			Face f = Faces.get(i);
			toReturn[i*3] = f.getA();
			toReturn[(i*3)+1] = f.getB();
			toReturn[(i*3)+2] = f.getC();
			
		}
		return toReturn;
	}

	public void setVertexNormal(int vertexIndex, int normalIndex) {
		//VertNormal.put(vertexIndex, normalIndex);
		Vertice v = Vertices.get(vertexIndex);
		v.setNormalIndex(normalIndex);
	}

	public static ObjectMachine prepModel(File file) {
		Scanner fileScanner;
		ObjectMachine modelMaker = new ObjectMachine();
		try {
			fileScanner = new Scanner(file);
			
			
			while (fileScanner.hasNextLine()) {
				String fileLine = fileScanner.nextLine();
				String[] tokens = fileLine.split(" ");
				String keyToken = tokens[0];
				if(keyToken.equals("v")) {
					ObjectMachine.Vertice v = modelMaker.new Vertice(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
					/*float[] vertex = new float[3];
					vertex[0] = Float.parseFloat(tokens[1]);
					vertex[1] = Float.parseFloat(tokens[2]);
					vertex[2] = Float.parseFloat(tokens[3]);
					*/
					modelMaker.addVertex(v);
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
	private class Vertice{
		private float xPos = 0;
		private float yPos = 0;
		private float zPos = 0;
		
		private float normalIndex = -1;
		
		private void setNormalIndex(int normalIndex) {
			this.normalIndex = normalIndex;
			
		}
		public Vertice(float x,float y, float z) {
			xPos = x;
			yPos = y;
			zPos = z;
		}
		
		private float getX() {
			return xPos;
		}
		private float getY() {
			return yPos;
		}
		private float getZ() {
			return zPos;
		}
	}
}
