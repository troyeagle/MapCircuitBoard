package net.aegistudio.mcb.designer.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

import javax.swing.JLabel;

public class Sign extends Control {
	private static final long serialVersionUID = 1L;

	private final ImageControl background;
	private final JLabel text;
	private final Dimension defaultSize;
	
	public Sign() {
		this.setLayout(new BorderLayout(0, 0));
		
		this.text = new JLabel();
		this.text.setLocation(10, 10);
		this.text.setForeground(Color.WHITE);
		this.text.setOpaque(false);

		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, new File("assets/mcfont.ttf"));
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(f);
		} catch (Exception e) {
			System.out.println("fail");
			e.printStackTrace();
		}
		
		this.background = new ImageControl("assets/signs.png", ImageStrech.Fill);
		
		this.defaultSize = background.getSize();

		text.setSize(this.defaultSize);
		this.setSize(this.defaultSize);
		this.setPreferredSize(this.defaultSize);
		
		this.add(this.text);
		this.add(this.background);
	}
	
	public void setText(String text) {
		text = "<html><head><style type=\"text/css\">"
				+ "body { font-family: Minecraft Regular; font-size: 12; }"
				+ "</head><body>"
				+ text.replaceAll("(\r\n|\n)", "<br>")
				+ "</body></html>";
		this.text.setText(text);
		Dimension size = this.text.getPreferredSize();
		if (size.height + 20 < this.defaultSize.height) {
			size.height = this.defaultSize.height;
		} else {
			size.height += 20;
		}
		if (size.width + 20 < this.defaultSize.width) {
			size.width = this.defaultSize.width;
		} else {
			size.width += 20;
		}
		this.updateSize(size);
	}
	
	public void updateSize(Dimension size) {
		this.setSize(size);
		this.setPreferredSize(size);
		this.background.setSize(size);
		this.text.setSize(size.width - 20, size.height - 20);
	}
}
