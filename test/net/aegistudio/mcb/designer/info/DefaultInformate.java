package net.aegistudio.mcb.designer.info;

import java.util.HashMap;

import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;

public class DefaultInformate implements Informate {
	public final HashMap<Class<? extends Component>, Informate> description = new HashMap<>();

	@Override
	public String describe(Cell cell, Component component, Data data) {
		if(component == null) return null;
		Informate current = description.get(component.getClass());
		return current != null? current.describe(cell, component, data) : null;
	}
	
	public <T extends Component> void register(Class<T> clazz, Informate generator) {
		description.put(clazz, generator);
	}
}
