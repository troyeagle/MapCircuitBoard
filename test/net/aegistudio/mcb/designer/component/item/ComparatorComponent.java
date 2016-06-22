package net.aegistudio.mcb.designer.component.item;

import java.util.ArrayList;
import java.util.List;

import net.aegistudio.mcb.unit.Comparator;

public class ComparatorComponent extends ComponentItem {
	private static final long serialVersionUID = 1L;

	public ComparatorComponent(boolean subtractive) {
		super("assets/comparator.png", Comparator.INSTANCES[0][subtractive ? 1 : 0]);
		this.state.setText(subtractive ? "on" : "off");
	}

	public static List<ComparatorComponent> getAll() {
		List<ComparatorComponent> components = new ArrayList<>();
		components.add(new ComparatorComponent(false));
		components.add(new ComparatorComponent(true));
		return components;
	}
}
