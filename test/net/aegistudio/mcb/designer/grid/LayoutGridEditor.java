package net.aegistudio.mcb.designer.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.designer.IComponentProvider;
import net.aegistudio.mcb.designer.NeoAwtGridComponent;
import net.aegistudio.mcb.designer.info.Informate;
import net.aegistudio.mcb.layout.ComponentPlacer;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mpp.Interaction;

/**
 * This component is used when designing grid components.
 * It will record any operation performed by user, and save it
 * in memento. So that undo/redo will be available.
 * 
 * @author aegistudio
 *
 */

public class LayoutGridEditor extends NeoAwtGridComponent {
	private static final long serialVersionUID = 1L;
	public final History history = new History();
	
	public LayoutGridEditor(Informate informate, HashMap<Class<?>, ComponentPlacer> placerMap, LayoutGrid grid, IComponentProvider provider) {
		super(grid);
		
		// Listen to user input.
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				manipulate(me.getPoint(), (x, y, cell) -> {
					boolean right = me.getButton() == MouseEvent.BUTTON3;
					Runnable todo = null;
					
					
					if(cell != null) 
						if(right) {
							ComponentPlacer placer = placerMap.get(cell.getComponent().getClass());
							Player player = new PseudoPlayer(me.isShiftDown() && provider.getSelected() != null?
										new ItemStack(placer.type): new ItemStack(Material.AIR));
							todo = () -> cell.getComponent().interact(cell,			// Right Click to add or interact.
									new Interaction(x, y, player, null, null, right));
						}
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
	
	public void paint(Graphics g) {
		super.paint(g);
		int xFloor = currentX - (currentX % (paintable.size * 4));
		int yFloor = currentY - (currentY % (paintable.size * 4));
		g.setColor(Color.BLUE);
		g.drawRect(xFloor, yFloor, 4 * paintable.size - 1, 4 * paintable.size - 1);
		g.drawRect(xFloor + 1, yFloor + 1, 4 * paintable.size - 3, 4 * paintable.size - 3);
	}
}
