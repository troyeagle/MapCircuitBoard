package net.aegistudio.mcb.designer.component.item;

import net.aegistudio.mcb.unit.CommandBlock;

public class CommandBlockComponent extends ComponentItem {
	private static final long serialVersionUID = 1L;

	public CommandBlockComponent() {
		super("assets/command_block.png", new CommandBlock(null));
	}

}
