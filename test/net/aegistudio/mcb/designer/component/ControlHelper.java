package net.aegistudio.mcb.designer.component;

import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class ControlHelper {

	public static Image getImage(String path) {
		if (path == null) return null;
		try {
			return ImageIO.read(new File(path));
		} catch (Exception e) {
			return null;
		}
	}
}
