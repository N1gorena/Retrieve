package main;

public class Face {
	private int vertexA = -1;
	private int vertexB = -1;
	private int vertexC = -1;
	
	int faceNormal = -1;
	
	public Face() {
		// TODO Auto-generated constructor stub
	}

	public void addVertices(int[] vertIndices) {
		vertexA = vertIndices[0];
		vertexB = vertIndices[1];
		vertexC = vertIndices[2];
		
	}
	
	public int getA() {
		return vertexA;
	}
	public int getC() {
		return vertexC;
	}
	public int getB() {
		return vertexB;
	}

}
