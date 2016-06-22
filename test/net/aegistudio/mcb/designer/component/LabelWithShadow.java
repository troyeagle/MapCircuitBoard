package net.aegistudio.mcb.designer.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class LabelWithShadow extends Control {
	private static final long serialVersionUID = 1L;

	private final JLabel text;
	private final JLabel shadow;
	
	private boolean autoFitSize;
	
	private int dx;
	private int dy;
	
	public LabelWithShadow(boolean autoFitSize) {
		this.setLayout(null);
		
		this.text = new JLabel();
		this.text.setForeground(Color.WHITE);
		this.text.setOpaque(false);
		
		this.shadow = new JLabel();
		this.shadow.setForeground(Color.DARK_GRAY);
		this.shadow.setOpaque(false);
		
		this.add(this.text);
		this.add(this.shadow);
		
		this.setShadowReleativeLocation(2, 2);
		
		this.autoFitSize = autoFitSize;
		if (autoFitSize) {
			this.fitSize();
		}
	}
	
	public void fitSize() {
		Dimension size = this.text.getPreferredSize();
		if (size.width != 0 && size.height != 0) {
			size.width += this.dx;
			size.height += this.dy;
		}
		this.setSize(size);
		this.setPreferredSize(size);
	}
	
	public void setShadowReleativeLocation(int x, int y) {
		this.dx = Math.abs(x);
		this.dy = Math.abs(y);
		int tx = 0, ty = 0;
		int sx = 0, sy = 0;
		if (x >= 0) {
			sx = x;
		} else {
			tx = -x;
		}
		if (y >= 0) {
			sy = y;
		} else {
			ty = -y;
		}
		this.text.setLocation(tx, ty);
		this.shadow.setLocation(sx, sy);
		if (this.autoFitSize) {
			this.fitSize();
		} else {
			this.setTextSize(this.getWidth(), this.getHeight());
		}
	}
	
	public void setText(String text) {
		this.text.setText(text);
		this.shadow.setText(text);
		
		if (this.autoFitSize) {
			this.fitSize();
		}
	}
	
	public String getText() {
		return this.text.getText();
	}
	
	public void setTextFont(Font f) {
		if (f != null) {
			this.text.setFont(f);
			this.shadow.setFont(f);			
		}
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.setTextSize(width, height);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		this.setTextSize(width, height);
	}
	
	private void setTextSize(int width, int height) {
		width = width > this.dx ? width - this.dx : 0;
		height = height > this.dy ? height - this.dy : 0;
		this.text.setSize(width, height);
		this.shadow.setSize(width, height);
	}
	
	public void setTextColor(Color c) {
		this.text.setForeground(c);
	}
	
	public void setShadowColor(Color c) {
		this.shadow.setForeground(c);
	}
	
	public void setHorizontalAlignment(int alignment) {
		this.text.setHorizontalAlignment(alignment);
		this.shadow.setHorizontalAlignment(alignment);
	}
	
	public void setVerticalAlignment(int alignment) {
		this.text.setVerticalAlignment(alignment);
		this.shadow.setVerticalAlignment(alignment);
	}
}
