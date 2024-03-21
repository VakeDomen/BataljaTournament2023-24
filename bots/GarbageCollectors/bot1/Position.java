/**
 * This class server no other purpose than to hold 2 integer values named x and y that represent a 2-dimensional position on a plane
 */
public class Position {
	public int x;
	public int y;
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the X coordinate of the position
	 * @return {@code int} value of the X coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the Y coordinate of the position
	 * @return {@code int} value of the Y coordinate
	 */
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return String.format("{X:%d,Y:%d}", x, y);
	}
}
