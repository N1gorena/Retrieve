package main;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ObjectMachine {
	
	private LinkedList<float[]> Vertices;
	private Map<Integer,Integer> VertNormal;
	private LinkedList<float[]> Normals;
	private LinkedList<Face> Faces;
	private boolean faceLinked = false;

	public ObjectMachine() {
		Vertices = new LinkedList<float[]>();
		Normals = new LinkedList<float[]>();
		Faces = new LinkedList<Face>();
		VertNormal = new HashMap<Integer,Integer>();
	}

	public void addVertex(float[] vertex) {
		Vertices.add(vertex);
		
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
			float[] transferBuffer = Vertices.get(i);
			vertexDataOut.add(transferBuffer[0]);
			vertexDataOut.add(transferBuffer[1]);
			vertexDataOut.add(transferBuffer[2]);
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
		VertNormal.put(vertexIndex, normalIndex);
		
	}

}
