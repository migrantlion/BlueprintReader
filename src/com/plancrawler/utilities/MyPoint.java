package com.plancrawler.utilities;

import java.io.Serializable;

public class MyPoint implements Serializable{

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	
	public MyPoint() {
		this.x = 0;
		this.y = 0;
	}
	
	public MyPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public MyPoint(MyPoint copyPoint) {
		this.x = copyPoint.getX();
		this.y = copyPoint.getY();
	}

	public MyPoint add(MyPoint other) {
		return new MyPoint(this.x+other.x, this.y+other.y);
	}
	
	public MyPoint translate(MyPoint other) {
		this.x += other.getX();
		this.y += other.getY();
		return this;
	}
	
	public void scale(double scalar) {
		x *= scalar;
		y *= scalar;
	}
	
	public double mag() {
		return Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2));
	}
	
	public MyPoint normalize() {
		if (mag() == 0)
			return new MyPoint(0,0);
		else {
			MyPoint newPoint = new MyPoint(this.x, this.y);
			newPoint.scale(1/mag());
			return newPoint;
		}
	}
	
	public MyPoint neg() {
		return new MyPoint(-this.x, -this.y);
	}
	
	public double distTo(MyPoint other) {
		return (this.add(other.neg())).mag();
	}
	
	public double dot(MyPoint other) {
		return (this.x * other.getX() + this.y*other.getY());
	}
	
	public static double dist(MyPoint pt1, MyPoint pt2) {
		return pt1.distTo(pt2);
	}
	
	public static double dot(MyPoint pt1, MyPoint pt2) {
		return pt1.dot(pt2);
	}
	
	public static double angleRad(MyPoint pt1, MyPoint pt2) {
		return pt1.angleRad(pt2);
	}
	
	public double angleRad(MyPoint other) {
		return Math.acos(this.dot(other)/(this.mag()*other.mag()));
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setTo(double x, double y) {
		this.x = x;
		this.y = y;		
	}

	public static MyPoint neg(MyPoint point) {
		return new MyPoint(-point.getX(),-point.getY());
	}
}
