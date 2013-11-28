package cloudymoose.childsplay;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;

public class DesktopNotificationService implements NotificationService {

	TrayIcon icon;
	SystemTray tray;

	public DesktopNotificationService() {
		if (SystemTray.isSupported()) {
			File image = new File("resources/ic_launcher.png");
			try {
				icon = new TrayIcon(ImageIO.read(image), "Child's Play");
				SystemTray.getSystemTray().add(icon);
			} catch (IOException e) {
				System.err.println("Requested: " + image.getAbsolutePath());
				e.printStackTrace();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void notifySomething() {
		if (icon == null) {
			Gdx.app.log("DesktopNotificationService", "Notifications not supported (System Tray)");
		} else {
			icon.displayMessage("Play caption", "Play text", MessageType.INFO);
			icon.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

				}
			});
		}

	}

}
