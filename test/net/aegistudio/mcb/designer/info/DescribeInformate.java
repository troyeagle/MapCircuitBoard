package net.aegistudio.mcb.designer.info;

import java.util.HashMap;

import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.unit.Button;
import net.aegistudio.mcb.unit.CommandBlock;
import net.aegistudio.mcb.unit.Comparator;
import net.aegistudio.mcb.unit.Lever;
import net.aegistudio.mcb.unit.MonitorPin;
import net.aegistudio.mcb.unit.OriginatorPin;
import net.aegistudio.mcb.unit.Repeater;
import net.aegistudio.mcb.unit.Torch;
import net.aegistudio.mcb.wire.BiInsulatedWire;
import net.aegistudio.mcb.wire.FullDirectionalWire;

/**
 * Describe what does these item do in minecraft.
 * @author aegistdio
 */

public class DescribeInformate extends DefaultInformate {
	public HashMap<Class<? extends Component>, String> description = new HashMap<>();
	public void describe(Class<? extends Component> clazz, String descriptionLine) {
		if(!description.containsKey(clazz)) {
			super.register(clazz, (cell, component, data) -> description.get(clazz));
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
		
		describe(Lever.class,				"Resembles lever in Minecraft."				);
		describe(Lever.class,				"When you click, it will switch "			);
		describe(Lever.class,				"between on and off. "						);	
		
		describe(Torch.class,				"Resemble redstone torch in "				);
		describe(Torch.class,				"Minecraft, however DOESN'T need"			);
		describe(Torch.class,				"a block to attach as power input. "		);
		
		describe(Repeater.class,			"Resemble redstone repeater in "			);
		describe(Repeater.class,			"Minecraft, right click to change"			);
		describe(Repeater.class,			"direction, and shift + right click "		);
		describe(Repeater.class,			"to change latency tier."					);
		
		describe(Comparator.class,			"Resemble redstone comparator in "			);
		describe(Comparator.class,			"Minecraft, right click to change"			);
		describe(Comparator.class,			"direction, and shift + right click "		);
		describe(Comparator.class,			"to switch between subtractive and "		);
		describe(Comparator.class,			"truncative."								);
		
		describe(MonitorPin.class,			"Original component, could take "			);
		describe(MonitorPin.class,			"voltage from the board, and transfer"		);
		describe(MonitorPin.class,			"it to adjacent blocks. Right click "		);
		describe(MonitorPin.class,			"to switch to output pin."					);
		
		describe(OriginatorPin.class,		"Original component, could take "			);
		describe(OriginatorPin.class,		"voltage from adjacent boards, and "		);
		describe(OriginatorPin.class,		"transfer it other components on  "			);
		describe(OriginatorPin.class,		"board. Right click to switch to "			);
		describe(OriginatorPin.class,		"input pin."								);
		
		describe(FullDirectionalWire.class,	"Resemble redstone wire in Minecraft."		);
		describe(FullDirectionalWire.class,	"The power input from one side will "		);
		describe(FullDirectionalWire.class,	"attenuate 1 level and output to other "	);
		describe(FullDirectionalWire.class,	"sides."									);
		

		describe(BiInsulatedWire.class,		"Original component, could insulate "		);
		describe(BiInsulatedWire.class,		"power from getting into the wire "			);
		describe(BiInsulatedWire.class,		"if it desn't come from the input "			);
		describe(BiInsulatedWire.class,		"sides."									);
	}
}
