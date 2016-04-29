package net.aegistudio.mcb.board;

import net.aegistudio.mcb.Facing;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mcb.unit.Unit;
import net.aegistudio.mcb.wire.Wire;
import net.aegistudio.mpp.algo.Paintable;

public class SpectatedActualGrid extends ActualGrid {
	public SpectatedActualGrid(LayoutGrid layout) {
		super(layout);
	}

	@Override
	public void paint(Paintable paintable) {
		all((r, c, cell, component) -> {
			cell.tick();
			Facing.all(f -> cell.getLevel(f));
		}, Wire.class);
		super.paint(paintable);
	}
	
	public void tick() {
		try {
			this.layout.all((r, c, cell, wire) -> {
				SpectatedActualGrid.this.cells[r][c].tick();
			}, Wire.class);
	
			this.layout.all((r, c, cell, unit) -> {
				((ActualUnitCell)SpectatedActualGrid.this.cells[r][c]).backupLevel();
			}, Unit.class);
			
			this.layout.all((r, c, cell, unit) -> {
				SpectatedActualGrid.this.cells[r][c].tick();
			}, Unit.class);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
