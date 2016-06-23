package net.aegistudio.mcb.designer.info;

import java.lang.reflect.Field;

import net.aegistudio.mcb.Binary;
import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.Facing;
import net.aegistudio.mcb.layout.LayoutUnitCell;
import net.aegistudio.mcb.layout.LayoutWireCell;
import net.aegistudio.mcb.unit.*;
import net.aegistudio.mcb.wire.*;

public class StatusInformate extends DefaultInformate {
	@Override
	public String describe(Cell cell, Component component, Data data) {
		if(cell != null) {
			StringBuilder textBuilder = new StringBuilder();
			
			textBuilder.append("Name: ");
			String name = cell.getComponent().getClass().getTypeName();
			textBuilder.append(name.substring(1 + name.lastIndexOf('.')));
			
			textBuilder.append("\n");
			textBuilder.append("Location: ");
			textBuilder.append("X=" + cell.getColumn() + ", Y=" + cell.getRow());
			
			textBuilder.append("\n");
			if(cell instanceof LayoutWireCell) {
				String description = describeLayoutWire((LayoutWireCell) cell);
				if(description.length() > 0)
					textBuilder.append("Distance: " + description);
			}
			else {
				textBuilder.append("Voltage: ");
				for(Facing facing : Facing.values()) {
					textBuilder.append(facing.name().charAt(0) + ":");
					textBuilder.append(cell.getLevel(facing));
					textBuilder.append(' ');
				}
			}
			
			String desc = super.describe(cell, component, data);
			if(desc != null) {
				textBuilder.append("\n");
				textBuilder.append(desc);
			}
			return new String(textBuilder);
		}
		return null;
	}
	
	private String describeLayoutWire(LayoutWireCell cell) {
		StringBuilder textBuilder = new StringBuilder();
		LayoutWireCell wire = (LayoutWireCell) cell;
		for(LayoutUnitCell adjacence : wire.allAdjacentUnit()) {
			textBuilder.append("\n");
			textBuilder.append("  (" + adjacence.getColumn() + ", " + adjacence.getRow() + "): ");
			for(Facing facing : Facing.values()) {
				short distance = wire.getDistance(adjacence, facing);
				if(distance == Short.MAX_VALUE) continue;
				textBuilder.append(facing.name().charAt(0) + "=");
				textBuilder.append(distance);
				textBuilder.append(' ');
			}
		}
		return new String(textBuilder);
	}
	
	public static <T> T get(Object obj, Class<T> ftype, String fieldname) {
		try {
			Class<?> clazz = obj.getClass();
			Field field = clazz.getDeclaredField(fieldname);
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			T value = (T) field.get(obj);
			field.setAccessible(false);
			return value;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String repeaterQueue(RepeaterQueue queue, Repeater parent) {
		int queueMask = get(parent, int.class, "queueMask");
		int result = get(queue, byte.class, "queue") & queueMask;
		StringBuilder builder = new StringBuilder();
		for(int i = 7; i >= 0; i --)
			builder.append((result & (1 << i)) != 0? '+' : ((queueMask & (1 << i)) != 0?'-' : ""));
		return new String(builder);
	}
	
	{
		register(Button.class, (a, b, c) -> ((Binary)c) == Binary.TRUE? "Status: Clicked" : "Status: Released");
		register(Lever.class, (a, b, c) -> ((Binary)c) == Binary.TRUE? "Power: On" : "Power: Off");
		register(Torch.class, (a, b, c) -> "Input-Side: " + get(b, Facing.class, "inputSide"));
		register(Repeater.class, (a, b, c) -> "Input-Side: " + get(b, Facing.class, "inputSide") 
										+ "\nLatency: " + get(b, int.class, "tick")
										+ "\nHatched: " + ((Repeater)b).hatched(a)
										+ "\nQueue: " + repeaterQueue((RepeaterQueue)c, (Repeater)b));
		register(Comparator.class, (a, b, c) -> "Input-Side: " + get(b, Facing.class, "inputSide")
										+ "\nSubtractive: " + (get(b, boolean.class, "subtractive")? "Yes" : "No"));
		register(BiInsulatedWire.class, (a, b, c) -> "Input-Sides: " + get(b, Facing.class, "sidea")
										+ ", " + get(b, Facing.class, "sideb"));
		register(MonitorPin.class, (a, b, c) -> "Pin-Type: Output");
		register(OriginatorPin.class, (a, b, c) -> "Pin-Type: Input");
		register(CommandBlock.class, (a, b, c) -> "Command: " + ((CommandBlockData)c).command);
	}
	
	
}
