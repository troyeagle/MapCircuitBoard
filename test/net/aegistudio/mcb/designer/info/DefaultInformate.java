package net.aegistudio.mcb.designer.info;

import java.util.HashMap;
import java.util.function.BiFunction;

import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.Data;

public class DefaultInformate implements Informate {
	public final HashMap<Class<? extends Component>, 
		BiFunction<Component, Data, String>> description = new HashMap<>();

	@Override
	public String describe(Cell cell, Component component, Data data) {
		if(component == null) return null;
		return description.get(component.getClass())
				.apply(component, data);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> void register(Class<T> clazz, 
			BiFunction<T, Data, String> generator) {
		description.put(clazz, (BiFunction<Component, Data, String>) generator);
	}
}
