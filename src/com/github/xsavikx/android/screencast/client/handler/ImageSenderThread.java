package com.github.xsavikx.android.screencast.client.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ImageSenderThread extends Thread {
	private final Socket s;
	private final static String EXEC_COMMAND = "/system/bin/screencap";

	public ImageSenderThread(Socket s) {
		super("Frame Sender");
		this.s = s;
	}

	@Override
	public void run() {
		System.out.println("[agent] ImageSenderThread.run()");
		InputStream is = null;
		try {
			Process p = Runtime.getRuntime().exec(EXEC_COMMAND);
			is = p.getInputStream();
			System.out.println("[agent] Starting sending framebuffer");
			OutputStream os = s.getOutputStream();
			byte[] buff = new byte[336 * 512 * 2];
			while (true) {
				int nb = is.read(buff);
				if (nb < -1)
					break;
				os.write(buff, 0, nb);
			}
			is.close();
			System.out.println("[agent] End of sending thread");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {

				}
			}
		}
	}
}
