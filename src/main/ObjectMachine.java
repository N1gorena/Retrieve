package main;

import java.util.LinkedList;

public class ObjectMachine {
	
	private LinkedList<float[]> Vertices;
	private LinkedList<float[]> Normals;
	private LinkedList<Face> Faces;

	public ObjectMachine() {
		Vertices = new LinkedList<float[]>();
		Normals = new LinkedList<float[]>();
		Faces = new LinkedList<Face>();
		
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

}
