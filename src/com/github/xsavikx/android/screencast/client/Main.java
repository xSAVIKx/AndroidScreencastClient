package com.github.xsavikx.android.screencast.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.xsavikx.android.screencast.client.handler.ClientHandler;

public class Main {
	public static void main(String[] args) {
		System.out.println("[agent] Starting ...");

		try {
			if (args.length == 0) {

				throw new RuntimeException("Need >= 1 param");
			}
			int port = Integer.parseInt(args[0]);
			System.out.println("[agent] port=" + port);
			new Main(port).execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	int port;

	public Main(int port) {
		this.port = port;
	}

	public void execute() throws IOException {
		System.out.println("[agent] Main.execute()");
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			System.out.println("[agent] ServerSocket created on port=" + port);
			while (true) {
				final Socket s = ss.accept();
				if (s == null || ss.isClosed())
					break;
				System.out.println("[agent] New client ! ");
				Thread t = new Thread("Client Handler") {
					@Override
					public void run() {
						new ClientHandler(s).start();
						try {
							s.close();
						} catch (IOException ex) {
						}
					}
				};
				t.start();
			}
		} catch (IOException exception) {
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ignored) {

				}
			}
		}
	}
}
