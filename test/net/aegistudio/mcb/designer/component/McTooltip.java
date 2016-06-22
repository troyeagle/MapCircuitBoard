package net.aegistudio.mcb.designer.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class McTooltip extends Control {
	private static final long serialVersionUID = 1L;

	private final LabelWithShadow text;
	
	public McTooltip() {
		this.setLayout(null);
		
		this.text = new LabelWithShadow(true);
		this.text.setLocation(10, 10);
		
		this.add(this.text);
	}
	
	public void setText(String text) {
		text = "<html><head><style type=\"text/css\">"
				+ "body { font-family: Minecraft Regular; font-size: 12; }"
				+ "</style></head><body>"
				+ text.replaceAll("(\r\n|\n)", "<br>")
				+ "</body></html>";
		this.text.setText(text);
		Dimension size = this.text.getPreferredSize();
		size.width += 20;
		size.height += 20;
		this.updateSize(size);
	}
	
	public void updateSize(Dimension size) {
		this.setSize(size);
		this.setPreferredSize(size);
	}
	
	@Override
	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		
		if (width < 8 || height < 8) return;
		
		Graphics g2 = g.create();
		g2.setColor(new Color(30, 10, 30));
		g2.fillRect(2, 0, width - 4, 2);
		g2.fillRect(2, height - 2, width - 4, 2);
		g2.fillRect(0, 2, 2, height - 4);
		g2.fillRect(width - 2, 2, 2, height - 4);
		g2.fillRect(4, 4, width - 8, height - 8);
		
		g2.setColor(new Color(45, 10, 100));
		g2.drawRect(2, 2, width - 5, height - 5);
		g2.drawRect(3, 3, width - 7, height - 7);
		
		super.paint(g);
	}
}
