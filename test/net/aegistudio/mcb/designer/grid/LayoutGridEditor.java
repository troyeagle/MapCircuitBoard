package net.aegistudio.mcb.designer.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.Facing;
import net.aegistudio.mcb.designer.IComponentProvider;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mcb.stdaln.AwtGridComponent;
import net.aegistudio.mpp.Interaction;

/**
 * This component is used when designing grid components.
 * It will record any operation performed by user, and save it
 * in memento. So that undo/redo will be available.
 * 
 * @author aegistudio
 *
 */

public class LayoutGridEditor extends AwtGridComponent {
	private static final long serialVersionUID = 1L;
	public final History history = new History();
	
	public LayoutGridEditor(LayoutGrid grid, IComponentProvider provider) {
		super(grid);
		
		// Listen to user input.
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				manipulate(me.getPoint(), (x, y, cell) -> {
					boolean right = me.getButton() == MouseEvent.BUTTON3;
					Runnable todo = null;
					
					if(cell != null) 
						if(right) todo = () -> cell.getComponent().interact(cell,			// Right Click to add or interact.
									new Interaction(x, y, null, null, null, right));
						else todo = () -> grid.setCell(y / 4, x / 4, null);
					else todo = () -> grid.setCell(y / 4, x / 4, provider.getSelected());	// Left click to remove.
					
					if(todo != null)
						history.perform(new MementoAction(grid, y / 4, x / 4, todo) {
							public void redo() {
								super.redo();
								repaint();
							}
							public void undo() {
								super.undo();
								repaint();
							}
						});
				});
			}
		});
		
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent me) {
				manipulate(me.getPoint(), (x, y, cell) -> {
					text = null;
					currentX = me.getX();
					currentY = me.getY();
					
					if(cell != null) {
						StringBuilder textBuilder = new StringBuilder();
						String name = cell.getComponent().getClass().getTypeName();
						textBuilder.append(name.substring(1 + name.lastIndexOf('.')));
						textBuilder.append(" (" + cell.getColumn() + ", " + cell.getRow() + ")");
						textBuilder.append("\n");
						for(Facing facing : Facing.values()) {
							textBuilder.append(facing.name().charAt(0) + ":");
							textBuilder.append(cell.getLevel(facing));
							textBuilder.append(' ');
						}
						
						Object data = cell.getData(Data.class);
						if(data != null) {
							textBuilder.append("\n");
							textBuilder.append("Data: ");
							textBuilder.append(data.toString());
						}
						text = new String(textBuilder);
					}
					repaint();
				});
			}
		});
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		int xFloor = currentX - (currentX % (paintable.size * 4));
		int yFloor = currentY - (currentY % (paintable.size * 4));
		g.setColor(Color.BLUE);
		g.drawRect(xFloor, yFloor, 4 * paintable.size - 1, 4 * paintable.size - 1);
		g.drawRect(xFloor + 1, yFloor + 1, 4 * paintable.size - 3, 4 * paintable.size - 3);
	}
}
