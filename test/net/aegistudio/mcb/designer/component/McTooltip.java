package net.aegistudio.mcb.designer.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;

public class McTooltip extends Control {
	private static final long serialVersionUID = 1L;

	private final LabelWithShadow text;
	
	private final LabelWithShadow addition;
	
	private String style;
	private int fontSize;
	
	private String rawText;
	private String rawAddition;
	
	public McTooltip() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.text = new LabelWithShadow(true);
		
		this.addition = new LabelWithShadow(true);
		this.addition.setTextColor(Color.GRAY);
		this.addition.setShadowReleativeLocation(1, 1);
		
		this.setFontSize(12);
		
		this.add(this.text);
		this.add(this.addition, BorderLayout.SOUTH);
	}
	
	public void setFontSize(int size) {
		if (this.fontSize != size) {
			this.fontSize = size;
			this.style = "body { font-family: Minecraft Regular; font-size: " + size + "; }";
			this.setText(this.rawText);
			this.setAdditionText(this.rawAddition);
		}
	}
	
	public int getFontSize() {
		return this.fontSize;
	}
	
	public void setText(String text) {
		this.rawText = text;
		text = this.format(text);
		this.text.setText(text);
		this.setSize(this.getPreferredSize());
	}
	
	public void setAdditionText(String text) {
		this.rawAddition = text;
		text = this.format(text);
		this.addition.setText(text);
		this.setSize(this.getPreferredSize());
	}
	
	public String getAddtionText() {
		return this.rawAddition;
	}
	
	public String getText() {
		return this.rawText;
	}
	
	private String format(String text) {
		if (text == null) return "";
		
		return "<html><head><style type=\"text/css\">"
				+ this.style
				+ "</style></head><body>"
				+ text.replaceAll("(\r\n|\n)", "<br>")
					.replaceAll("[ ]", "&nbsp;")
				+ "</body></html>";
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
