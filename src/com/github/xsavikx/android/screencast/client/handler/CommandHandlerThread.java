package com.github.xsavikx.android.screencast.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import com.github.xsavikx.android.screencast.client.handler.event.KeyEvent;
import com.github.xsavikx.android.screencast.client.handler.event.SwipeEvent;
import com.github.xsavikx.android.screencast.client.handler.event.TapEvent;

public class CommandHandlerThread extends Thread {
	private Socket s;
	private static final String COMMAND_LINE_SEPARATOR = "/";

	public CommandHandlerThread(Socket s) {
		super("Command Handler");
		this.s = s;
	}

	public static enum Command {
		TRACKBALL("trackball"), SWIPE("swipe"), KEY("key"), TAP("tap"), NO_COMMAND(
				""), QUIT("quit");
		private String name;

		private Command(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Command getCommandByName(String name) {
			for (Command command : Command.values()) {
				if (name.equalsIgnoreCase(command.name)) {
					return command;
				}
			}
			return NO_COMMAND;
		}

		@Override
		public String toString() {
			return "Command[name=" + name + "]";
		}
	}

	@Override
	public void run() {
		System.out.println("[agent] CommandHandlerThread.run()");
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while (true) {
				String line = r.readLine();
				if (line == null) {
					r.close();
					break;
				}
				System.out.println("[agent] Received : " + line);
				String[] paramList = line.split(COMMAND_LINE_SEPARATOR);
				if (paramList.length == 0) {
					r.close();
					break;
				}
				Command command = Command.getCommandByName(paramList[0]);
				try {
					handleCommand(command,
							Arrays.copyOfRange(paramList, 1, paramList.length));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException ignored) {

				}
			}
		}
	}

	private KeyEvent getKeyEvent(String[] args) {
		int code = Integer.parseInt(args[0]);
		KeyEvent returnKeyEvent = new KeyEvent(code);
		return returnKeyEvent;
	}

	private SwipeEvent getSwipeEvent(String[] args) {
		return new SwipeEvent(Integer.parseInt(args[0]),
				Integer.parseInt(args[1]), Integer.parseInt(args[2]),
				Integer.parseInt(args[3]));

	}

	private TapEvent getTapEvent(String[] args) {
		TapEvent returnTapEvent = new TapEvent(Integer.parseInt(args[0]),
				Integer.parseInt(args[1]));
		return returnTapEvent;
	}

	private void handleCommand(Command command, String[] args)
			throws IOException {
		System.out.println("[agent] CommandHandlerThread.handleCommand()");
		System.out.println("[agent] " + command);
		switch (command) {
		case KEY: {
			KeyEvent event = getKeyEvent(args);
			System.out.println("[agent] " + event);
			Runtime.getRuntime().exec(
					String.format("input keyevent %d", event.getCode()));
			break;
		}
		case SWIPE: {
			SwipeEvent event = getSwipeEvent(args);
			System.out.println("[agent] " + event);
			Runtime.getRuntime().exec(
					String.format("input swipe %d %d %d %d", event.getFromX(),
							event.getFromY(), event.getToX(), event.getToY()));
			break;
		}
		case TAP: {
			TapEvent event = getTapEvent(args);
			System.out.println("[agent] " + event);
			Runtime.getRuntime()
					.exec(String.format("input tap %d %d", event.getX(),
							event.getY()));
			break;
		}
		case TRACKBALL: {
			break;
		}
		case QUIT: {
			if (s != null) {
				try {
					s.close();
				} catch (IOException ignored) {

				}
			}
			System.out.println("[agent] Exiting...");
			System.exit(0);
			break;
		}
		default: {
			throw new RuntimeException("[agent] Invalid command");
		}
		}
	}
}
