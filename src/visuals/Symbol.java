package visuals;

public class Symbol {
	// hold the symbol and the ability to draw that symbol on the screen
	
	// type 1 = rect, 2 = ellipse, 3 = circle
	private int type;
	private XY center;
	private XY[] border;
	
	public Symbol(int type, XY center) {
		this.type = type;
		this.center = center;
	}
	
	public Symbol(int type, double x, double y) {
		this(type, new XY(x,y));
	}
	
}
