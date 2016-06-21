package net.aegistudio.mcb.designer.info;

import net.aegistudio.mcb.unit.*;
import net.aegistudio.mcb.wire.*;

public class NameInformate extends DefaultInformate {
	public NameInformate() {
		super();
		
		register(Button.class, 				(a, b, c) -> "Button");
		register(Lever.class, 				(a, b, c) -> "Lever");
		register(Torch.class, 				(a, b, c) -> "Redstone Torch");
		register(Repeater.class, 			(a, b, c) -> "Redstone Repeater");
		register(Comparator.class, 			(a, b, c) -> "Redstone Comparator");
		register(CommandBlock.class, 		(a, b, c) -> "Command Block");
		register(MonitorPin.class,			(a, b, c) -> "Outbound Pin");
		register(OriginatorPin.class,		(a, b, c) -> "Inbound Pin");
		
		register(FullDirectionalWire.class, (a, b, c) -> "Redstone Wire");
		register(BiInsulatedWire.class,		(a, b, c) -> "Insulated Wire (Bi-Directional)");
	}
}
