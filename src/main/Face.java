package main;

public class Face {
	int vertexA = -1;
	int vertexB = -1;
	int vertexC = -1;
	
	int faceNormal = -1;
	
	public Face() {
		// TODO Auto-generated constructor stub
	}

	public void addVertices(int[] vertIndices) {
		vertexA = vertIndices[0];
		vertexB = vertIndices[1];
		vertexC = vertIndices[2];
		
	}

}
