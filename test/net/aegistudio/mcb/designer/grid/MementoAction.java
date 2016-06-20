package net.aegistudio.mcb.designer.grid;

import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.Grid;

public class MementoAction implements Action {
	int row, column;	Grid grid;
	Runnable runnable;
	public MementoAction(Grid grid, int row, int column, Runnable execute) {
		this.grid = grid;
		this.row = row;
		this.column = column;
		this.runnable = execute;
	}
	
	Component previous;
	Data data;
	public void redo() {
		Cell target = grid.getCell(row, column);
		
		if(target != null) {
			previous = target.getComponent();
			data = target.getData(Data.class);
			if(data != null) data = data.duplicate();
		}
		runnable.run();
	}

	@Override
	public void undo() {
		if(previous != null) {
			grid.setCell(row, column, previous);
			grid.getCell(row, column).setData(data);
		}
		else grid.setCell(row, column, null);
	}
}
