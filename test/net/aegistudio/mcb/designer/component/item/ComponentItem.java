package net.aegistudio.mcb.designer.component.item;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.designer.component.Control;
import net.aegistudio.mcb.designer.component.ImageControl;
import net.aegistudio.mcb.designer.component.LabelWithShadow;

public class ComponentItem extends Control {
	private static final long serialVersionUID = 1L;

	private static final Dimension size = new Dimension(48, 48);
	private static final String selectedBorder = "assets/hotbar_selector.png";
	
	private boolean isSelected;
	
	private ImageControl border;
	
	protected Component component;
	
	protected final LabelWithShadow state;
	private String text;
	
	public ComponentItem(String iconPath, Component component) {
		this.setLayout(null);
	
		this.component = component;
		
		this.state = new LabelWithShadow(false);
		this.state.setHorizontalAlignment(SwingConstants.RIGHT);
		this.state.setVerticalAlignment(SwingConstants.BOTTOM);
		this.state.setSize(42, 42);
		
		this.add(this.state);
		
		this.setPreferredSize(size);
		this.setSize(size);
		
		ImageControl icon = new ImageControl(iconPath);
		icon.setLocation((size.width - icon.getWidth()) / 2,
				(size.height - icon.getHeight()) / 2);
		this.add(icon);
		
		this.border = new ImageControl(selectedBorder);
		this.border.setVisible(false);
		this.add(this.border);
	}
	
	public void setText(String text) {
		this.text = text;
		text = "<html><head><style type=\"text/css\">"
				+ "body { font-family: Minecraft Regular; font-size: 10; }"
				+ "</style></head><body>"
				+ text
				+ "</body></html>";
		this.state.setText(text);
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setSelected(boolean selected) {
		this.isSelected = selected;
		this.border.setVisible(selected);
		this.repaint();
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	public Component getComponent() {
		return this.component;
	}
}
