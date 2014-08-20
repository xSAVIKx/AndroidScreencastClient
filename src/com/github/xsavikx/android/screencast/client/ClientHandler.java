package com.github.xsavikx.android.screencast.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler {
	class KeyEvent {
		int action;
		int code;

		public KeyEvent(int action, int code) {
			this.action = action;
			this.code = code;
		}
	}

	class SwipeEvent {
		int fromX;
		int fromY;
		int toX;
		int toY;

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
	}

	class TapEvent {
		int x;
		int y;

		public TapEvent(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "TapEvent [x=" + x + ", y=" + y + "]";
		}

	}

	private Socket s;

	public ClientHandler(Socket s) throws IOException {
		System.out.println("[agent] ClientHandler.ClientHandler()");
		this.s = s;
		Thread tSend = new Thread("Frame Sender") {
			@Override
			public void run() {
				sendFrameBuffer();
			}
		};

		Thread tHandleCmd = new Thread("Command Handler") {
			@Override
			public void run() {
				handleCmd();
			}
		};
		tSend.start();
		tHandleCmd.start();

		try {
			tSend.join();
			tHandleCmd.join();
		} catch (InterruptedException e) {
		}
		try {
			if (s != null)
				s.close();
		} catch (IOException ignored) {
		}
	}

	private KeyEvent getKeyEvent(String[] args) {
		System.out.println("[agent] ClientHandler.getKeyEvent()");
		int action = Integer.parseInt(args[1]);
		int code = Integer.parseInt(args[2]);
		KeyEvent returnKeyEvent = new KeyEvent(action, code);
		return returnKeyEvent;
	}

	// private static MotionEvent getMotionEvent(String[] args) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("getMotionEvent(String[]) - start");
	// }
	//
	// int i = 1;
	// long downTime = Long.parseLong(args[i++]);
	// long eventTime = Long.parseLong(args[i++]);
	// int action = Integer.parseInt(args[i++]);
	// float x = Float.parseFloat(args[i++]);
	// float y = Float.parseFloat(args[i++]);
	// int metaState = Integer.parseInt(args[i++]);
	// MotionEvent returnMotionEvent = MotionEvent.obtain(downTime, eventTime,
	// action, x, y, metaState);
	// if (logger.isDebugEnabled()) {
	// logger.debug("getMotionEvent(String[]) - end");
	// }
	// return returnMotionEvent;
	// }
	private SwipeEvent getSwipeEvent(String[] args) {
		System.out.println("[agent] ClientHandler.getTapEvent()");
		return new SwipeEvent(Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]),
				Integer.parseInt(args[4]));

	}

	private TapEvent getTapEvent(String[] args) {
		System.out.println("[agent] ClientHandler.getTapEvent()");
		TapEvent returnTapEvent = new TapEvent(Integer.parseInt(args[1]),
				Integer.parseInt(args[2]));
		return returnTapEvent;
	}

	private void handleCmd() {
		System.out.println("[agent] ClientHandler.handleCmd()");
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while (true) {
				String line = r.readLine();
				if (line == null) {
					r.close();
					s.close();
					break;
				}
				System.out.println("[agent] Received : " + line);
				try {
					handleCommand(line);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handleCommand(String line) throws IOException {
		System.out.println("[agent] ClientHandler.handleCommand()");
		String[] paramList = line.split("/");
		String type = paramList[0];
		System.out.println("[agent] type=" + type);
		if (type.equals("quit")) {
			s.close();
			System.exit(0);
			return;
		} else if (type.equals("tap")) {
			TapEvent event = getTapEvent(paramList);
			System.out.println("[agent] " + event);
			Runtime.getRuntime().exec(
					String.format("input tap %d %d", event.x, event.y));
			// wm.injectPointerEvent(getMotionEvent(paramList), false);
		} else if (type.equals("swipe")) {
			SwipeEvent event = getSwipeEvent(paramList);
			System.out.println("[agent] " + event);
			Runtime.getRuntime().exec(
					String.format("input swipe %d %d %d %d", event.fromX,
							event.fromY, event.toX, event.toY));
		} else if (type.equals("key")) {
			KeyEvent event = getKeyEvent(paramList);
			System.out.println("[agent] eventCode=" + event.code);
			Runtime.getRuntime().exec(
					String.format("input keyevent %d", event.code));
			// wm.injectKeyEvent(getKeyEvent(paramList), false);
		} else if (type.equals("trackball")) {
			// wm.injectTrackballEvent(getMotionEvent(paramList), false);
		} else {
			throw new RuntimeException("[agent] Invalid type : " + type);
		}

	}

	private void sendFrameBuffer() {
		System.out.println("[agent] ClientHandler.sendFrameBuffer()");
		InputStream is = null;
		try {
			Process p = Runtime.getRuntime().exec(
					"/system/bin/cat /dev/graphics/fb0");
			is = p.getInputStream();
			System.out.println("[agent] Starting sending framebuffer");
			OutputStream os = s.getOutputStream();
			byte[] buff = new byte[336 * 512 * 2];
			while (true) {
				// FileInputStream fos = new
				// FileInputStream("/dev/graphics/fb0");
				int nb = is.read(buff);
				if (nb < -1)
					break;
				// fos.close();
				// System.out.println("[agent] val " + nb);
				os.write(buff, 0, nb);
				Thread.sleep(5);
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
