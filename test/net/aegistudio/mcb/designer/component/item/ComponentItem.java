package net.aegistudio.mcb.designer.component.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.designer.component.Control;
import net.aegistudio.mcb.designer.component.ImageControl;

public class ComponentItem extends Control {
	private static final long serialVersionUID = 1L;

	private static final Dimension size = new Dimension(48, 48);
	private static final String selectedBorder = "assets/hotbar_selector.png";
	
	private boolean isSelected;
	
	private ImageControl border;
	
	protected Component component;
	
	protected final JLabel state;
	
	public ComponentItem(String iconPath, Component component) {
		this.setLayout(null);
	
		this.component = component;
		
		this.state = new JLabel();
		this.state.setOpaque(false);
		this.state.setHorizontalAlignment(SwingConstants.RIGHT);
		this.state.setVerticalAlignment(SwingConstants.BOTTOM);
		this.state.setForeground(Color.WHITE);
		this.state.setFont(new Font("Minecraft Regular", Font.PLAIN, 12));
		this.state.setBounds(0, 20, 40, 20);
		
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
