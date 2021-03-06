package net.aegistudio.mcb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.entity.ItemFrame;

public abstract class AbstractCell<G extends Grid, C extends Component> implements Cell, Comparable<Cell> {
	private final int row;		public @Override int getRow() {	return row;	}
	private final int column;	public @Override int getColumn() {	return column;	}
	private final G grid;		public @Override G getGrid() { return grid; }
	
	protected int[] level = new int[Facing.values().length];
	public @Override int getLevel(Facing port) {		return level[port.ordinal()];		}
	public @Override void setLevel(Facing port, int level) {	this.level[port.ordinal()] = level;		}
	
	protected AbstractCell(G grid, int row, int column) {
		this.grid = grid;
		this.row = row;
		this.column = column;
	}

	protected C component;
	public @Override C getComponent() {	return component;	}
	protected void setComponent(C component) {
		if(this.component != component) {
			this.component = component;
			this.component.init(this);
		}
	}
	
	@Override
	public Cell adjacence(Facing port) {	return port.call(row, column, (r, c) -> grid.getCell(r, c));	}

	private Data data;
	@SuppressWarnings("unchecked")
	public @Override <T extends Data> T getData(Class<T> dataClazz) {	return (T)data;	}
	public @Override <T extends Data> void setData(T data) {	this.data = data;	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(InputStream input, ComponentFactory table) throws Exception {
		DataInputStream din = new DataInputStream(input);
		this.component = (C) table.get(din.readShort());
		try {
			this.component.load(this, input);
		}
		catch(Exception e) {
			// Print error when the component fails to load.
			// And init it with a default value.
			e.printStackTrace();
			this.component.init(this);
		}
	}

	@Override
	public void save(OutputStream output, ComponentFactory table) throws Exception {
		DataOutputStream dout = new DataOutputStream(output);
		dout.writeShort(table.id(component));
		component.save(this, output);
	}
	
	public void tick(ItemFrame frame) {
		// Don't tick by default.
	}
	
	public int compareTo(Cell another) {
		int difference = row - another.getRow();
		if(difference == 0) 
			difference = column - another.getColumn();
		return difference;
	}
}
