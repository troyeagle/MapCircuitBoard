package net.aegistudio.mcb.designer.grid;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.board.ActualGrid;
import net.aegistudio.mcb.designer.NeoAwtGridComponent;
import net.aegistudio.mcb.designer.info.Informate;
import net.aegistudio.mpp.Interaction;

public class ActualGridEditor extends NeoAwtGridComponent {
	private static final long serialVersionUID = 1L;

	public ActualGridEditor(Informate informate, ActualGrid grid) {
		super(grid);
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				manipulate(me.getPoint(), (x, y, cell) -> {
					boolean right = me.getButton() == MouseEvent.BUTTON3;
					if(cell != null) cell.getComponent().interact(cell, 
								new Interaction(x, y, null, null, null, right));
					repaint();
				});
			}
		});
		
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent me) {
				manipulate(me.getPoint(), (x, y, cell) -> {
					text = informate.describe(cell, 
							cell == null? null : cell.getComponent(), 
							cell == null? null : cell.getData(Data.class));
					currentX = me.getX();
					currentY = me.getY();
					repaint();
				});
			}
		});
	}
	
	public void tick() {
		grid.tick(null);
		grid.paint(paintable);
		repaint();
	}
}
