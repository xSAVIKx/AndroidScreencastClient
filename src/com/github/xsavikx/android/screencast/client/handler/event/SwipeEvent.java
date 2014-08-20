package com.github.xsavikx.android.screencast.client.handler.event;

public class SwipeEvent {
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;

	public SwipeEvent(int fromX, int fromY, int toX, int toY) {
		super();
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

	@Override
	public String toString() {
		return "SwipeEvent [fromX=" + fromX + ", fromY=" + fromY + ", toX="
				+ toX + ", toY=" + toY + "]";
	}

	public int getFromX() {
		return fromX;
	}

	public void setFromX(int fromX) {
		this.fromX = fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public void setFromY(int fromY) {
		this.fromY = fromY;
	}

	public int getToX() {
		return toX;
	}

	public void setToX(int toX) {
		this.toX = toX;
	}

	public int getToY() {
		return toY;
	}

	public void setToY(int toY) {
		this.toY = toY;
	}

}
