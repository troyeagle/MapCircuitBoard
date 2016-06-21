package net.aegistudio.mcb.designer.info;

import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;

public interface Informate {
	public String describe(Cell cell, Component component, Data data);
}
