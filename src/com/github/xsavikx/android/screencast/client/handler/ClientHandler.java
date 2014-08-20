package com.github.xsavikx.android.screencast.client.handler;

import java.net.Socket;

public class ClientHandler {

	private Socket s;
	private ImageSenderThread tSend;
	private CommandHandlerThread tHandleCmd;

	public void start() {
		System.out.println("[agent] ClientHandler.ClientHandler()");
		tSend.start();
		tHandleCmd.start();
		try {
			tHandleCmd.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ClientHandler(Socket s) {
		this.s = s;
		this.tSend = new ImageSenderThread(this.s);
		this.tHandleCmd = new CommandHandlerThread(this.s);
	}
}
