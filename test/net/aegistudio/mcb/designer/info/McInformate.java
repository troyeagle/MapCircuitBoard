package net.aegistudio.mcb.designer.info;

import java.util.HashMap;

import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.unit.Button;
import net.aegistudio.mcb.unit.CommandBlock;

/**
 * Describe what does these item do in minecraft.
 * @author aegistdio
 */

public class McInformate extends DefaultInformate {
	public HashMap<Class<? extends Component>, String> description = new HashMap<>();
	public void describe(Class<? extends Component> clazz, String descriptionLine) {
		if(!description.containsKey(clazz)) {
			super.register(clazz, (component, data) -> description.get(clazz));
			description.put(clazz, descriptionLine);
		}
		else description.put(clazz, description.get(clazz) + "\n" + descriptionLine);
	}
	
	{
		describe(Button.class, 				"Resembles button in Minecraft."			);
		describe(Button.class, 				"Will be powered when you click,"			);
		describe(Button.class, 				"and recover after some time. "				);
		
		describe(CommandBlock.class, 		"A small but powerful embedded"				);
		describe(CommandBlock.class, 		"command block. Open a command"				);
		describe(CommandBlock.class, 		"block GUI when right click."				);
	}
	
	public static void main(String[] arguments) {
		System.out.println(new McInformate().describe(null, Button.INSTANCE, null));
	}
}
