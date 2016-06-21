package net.aegistudio.mcb.designer.info;

import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.Facing;

public class PseudoInformate implements Informate {
	@Override
	public String describe(Cell cell, Component component, Data data) {
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
			
			if(data != null) {
				textBuilder.append("\n");
				textBuilder.append("Data: ");
				textBuilder.append(data.toString());
			}
			return new String(textBuilder);
		}
		return null;
	}

}
