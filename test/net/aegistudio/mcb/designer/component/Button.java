package net.aegistudio.mcb.designer.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Button extends JButton {
	private static final long serialVersionUID = 1L;

	private ImageControl inactive;
	private ImageControl active;
	private ImageControl disabled;
	private boolean isMouseIn;
	
	public Button() {
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBorderPainted(false);
		this.setFocusPainted(false);

		this.inactive = new ImageControl();
		this.active = new ImageControl();
		this.disabled = new ImageControl();
		this.add(this.inactive);
		this.add(this.active);
		this.add(this.disabled);
		
		this.active.setVisible(false);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				isMouseIn = true;
				if (isEnabled()) {
					active.setVisible(true);
					inactive.setVisible(false);
					repaint();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				isMouseIn = false;
				if (isEnabled()) {					
					active.setVisible(false);
					inactive.setVisible(true);
					repaint();
				}
			}
		});
	}
	
	public void setInactiveBackground(String imagePath) {
		this.inactive.setImage(imagePath);
	}
	
	public void setActiveBackground(String imagePath) {
		this.active.setImage(imagePath);
	}
	
	public void setDisabledBackground(String imagePath) {
		this.disabled.setImage(imagePath);
	}
	
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (b) {
			this.disabled.setVisible(false);
			if (isMouseIn) {
				this.active.setVisible(true);
			} else {
				this.inactive.setVisible(true);
			}
		} else {
			this.active.setVisible(false);
			this.inactive.setVisible(false);
			this.disabled.setVisible(true);
		}
		this.repaint();
	}
}
