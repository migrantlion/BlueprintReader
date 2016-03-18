package visuals;

public class XY {

	double x;
	double y;
	
	public XY() {
		this.x = 0;
		this.y = 0;
	}
	
	public XY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public XY add(XY other) {
		return new XY(this.x+other.x, this.y+other.y);
	}
	
	public void addTo(XY other) {
		this.x += other.x;
		this.y += other.y;
	}
	
	public double mag() {
		return Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2));
	}
	
	public XY neg() {
		return new XY(-this.x, -this.y);
	}
	
	public double distTo(XY other) {
		return (this.add(other.neg())).mag();
	}
}
