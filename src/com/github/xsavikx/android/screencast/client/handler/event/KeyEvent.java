package com.github.xsavikx.android.screencast.client.handler.event;

public class KeyEvent {
	private int code;

	public KeyEvent(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "KeyEvent [code=" + code + "]";
	}

}
