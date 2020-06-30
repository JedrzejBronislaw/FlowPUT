package jedrzejbronislaw.flowmeasure.tools;

import javafx.application.Platform;

public class Delay {

	public static void action(int delay, Runnable action) {
		new Thread(() -> {
			sleep(delay);
			Injection.run(action);
		}).start();
	}

	public static void viewAction(int delay, Runnable action) {
		new Thread(() -> {
			sleep(delay);
			Platform.runLater(action);
		}).start();
	}

	private static void sleep(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
