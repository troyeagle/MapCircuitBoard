package net.aegistudio.mcb.designer.component.item;

import net.aegistudio.mcb.unit.Torch;

public class TorchComponent extends ComponentItem {
	private static final long serialVersionUID = 1L;

	public TorchComponent() {
		super("assets/torch.png", Torch.INSTANCES[0]);
	}
}
