package mod.vemerion.smartphone.phone.utils;

public class Rectangle {

	public float x, y, width, height;

	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(float x, float y, float size) {
		this(x, y, size, size);
	}

	public boolean contains(float x, float y) {
		return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
	}

	public boolean intersect(Rectangle other) {
		return !(other.x + other.width < x || x + width < other.x || other.y + other.height < y
				|| y + height < other.y);
	}
}
