package net.aegistudio.mcb.designer;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.Popup;
import javax.swing.PopupFactory;

import net.aegistudio.mcb.Grid;
import net.aegistudio.mcb.designer.component.McTooltip;
import net.aegistudio.mcb.stdaln.AwtGridComponent;

public class NeoAwtGridComponent extends AwtGridComponent {
	public NeoAwtGridComponent(Grid grid) {
		super(grid);
	}

	private static final long serialVersionUID = 1L;
	
	public McTooltip toolTip = new McTooltip();
	public Popup popup;
	
	public void paint(Graphics g) {
		paintable.setGraphics(g);
		this.grid.paint(paintable);
		
		if(text != null) {
			toolTip.setText(text);
	
			if(popup == null) {
				Point location = this.getLocationOnScreen();
				popup = PopupFactory.getSharedInstance()
							.getPopup(this, toolTip, 
									location.x + currentX, 
									location.y + currentY);
				popup.show();
			}
		} else if(popup != null) {
			popup.hide();
			popup = null;
		}
	}
	
	public void setVisible(boolean v) {
		super.setVisible(v);
		if(popup != null) {
			popup.hide();
			popup = null;
		}
	}
}
