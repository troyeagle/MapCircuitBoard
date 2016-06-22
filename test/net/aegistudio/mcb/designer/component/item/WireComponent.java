package net.aegistudio.mcb.designer.component.item;

import net.aegistudio.mcb.wire.FullDirectionalWire;

public class WireComponent extends ComponentItem {
	private static final long serialVersionUID = 1L;

	public WireComponent() {
		super("assets/wire.png", FullDirectionalWire.INSTANCE);
	}

}
