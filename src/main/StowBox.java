package main;

import org.joml.Vector3f;

public class StowBox {
	private Vector3f position;
	
	public StowBox(float f, float g, float h) {
		position = new Vector3f(f,g,h);
	}

	public Vector3f getPosition() {
		return position;
	}

}
