package net.aegistudio.mcb.designer.component.item;

import java.util.ArrayList;
import java.util.List;

import net.aegistudio.mcb.Facing;
import net.aegistudio.mcb.unit.Repeater;

public class RepeaterComponent extends ComponentItem {
	private static final long serialVersionUID = 1L;
	private static Repeater[] repeaters = Repeater.INSTANCES[Facing.NORTH.ordinal()];
	
	private String text;
	
	public RepeaterComponent(int level) {
		super("assets/repeater.png", repeaters[level]);
		this.setText("lv " + (level + 1));
		this.text = "Tier: " + (level + 1);
	}

	@Override
	public String getText() {
		return this.text;
	}
	
	public static List<RepeaterComponent> getAll() {
		List<RepeaterComponent> components = new ArrayList<>();
		for (int i = 0; i < repeaters.length; i++) {
			components.add(new RepeaterComponent(i));
		}
		return components;
	}
}
