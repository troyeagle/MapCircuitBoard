package net.aegistudio.mcb.designer.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

public class ImageControl extends Control {
	private static final long serialVersionUID = 1L;

	private Image image;
	private ImageStrech strech;
	
	public ImageControl() {
		this((Image) null);
	}
	
	public ImageControl(String path) {
		this(path, ImageStrech.None);
	}
	
	public ImageControl(String path, ImageStrech strech) {
		this(ControlHelper.getImage(path), strech);
	}
	
	public ImageControl(Image image) {
		this(image, ImageStrech.None);
	}
	
	public ImageControl(Image image, ImageStrech strech) {
		this.setOpaque(false);

		this.image = image;
		this.strech = strech;
		this.fitSize();
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public void setImage(String path) {
		this.setImage(ControlHelper.getImage(path));
	}

	public void setImage(Image image) {
		if (this.image != image) {
			this.image = image;
			this.repaint();
		}
	}
	
	public ImageStrech getStrech() {
		return strech;
	}

	public void setStrech(ImageStrech strech) {
		if (this.strech != strech) {
			this.strech = strech;
			this.repaint();			
		}
	}

	public void fitSize() {
		Insets insets = this.getInsets();
		
		int width = insets.left + insets.right;
		int height = insets.top + insets.bottom;
		
		if (this.image != null) {
			width += this.getImageWidth();
			height += this.getImageHeight();
		}
		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));
	}
	
	public int getImageWidth() {
		if (this.image == null) return 0;
		return this.image.getWidth(null);
	}
	
	public int getImageHeight() {
		if (this.image == null) return 0;
		return this.image.getHeight(null);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Insets insets = this.getInsets();
		
		int x, y, width, height;
		
		int controlWidth = this.getWidth() - insets.left - insets.right;
		int controlHeight = this.getHeight() - insets.top - insets.bottom;
		int imageWidth = this.getImageWidth();
		int imageHeight = this.getImageHeight();
		
		switch(this.strech) {
		case Fill:
			width = controlWidth;
			height = controlHeight;
			break;
		case Uniform:
			if (controlWidth * imageHeight >= controlHeight * imageWidth) {
				// control is wider
				width = imageWidth * controlHeight / imageHeight;
				height = controlHeight;
			} else {
				width = controlWidth;
				height = imageHeight * controlWidth / imageWidth;
			}
			break;
		case UniformToFill:
			if (controlWidth * imageHeight >= controlHeight * imageWidth) {
				width = controlWidth;
				height = imageHeight * controlWidth / imageWidth;
			} else {
				width = imageWidth * controlHeight / imageHeight;
				height = controlHeight;
			}
			break;
		default:
			width = imageWidth;
			height = imageHeight;
			break;
		}
		
		if (width < 0) width = 0;
		if (height < 0) height = 0;
		x = (controlWidth - width) / 2 + insets.left;
		y = (controlHeight - height) / 2 + insets.top;

		g.drawImage(this.image, x, y, width, height, null);
	}
}
