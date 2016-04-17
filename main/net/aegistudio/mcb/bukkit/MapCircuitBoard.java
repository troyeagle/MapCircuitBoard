package net.aegistudio.mcb.bukkit;

import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.aegistudio.mcb.Air;
import net.aegistudio.mcb.ComponentFactory;
import net.aegistudio.mcb.unit.Button;
import net.aegistudio.mcb.unit.Lever;
import net.aegistudio.mcb.unit.Torch;
import net.aegistudio.mcb.wire.FullDirectionalWire;
import net.aegistudio.mpp.export.CanvasCommandHandle;
import net.aegistudio.mpp.export.PluginCanvasRegistry;
import net.aegistudio.mpp.export.PluginCanvasService;
import net.aegistudio.mpp.export.PluginCommandService;

public class MapCircuitBoard extends JavaPlugin {
	public ComponentFactory factory;
	
	public ComponentFactory getComponentTable() {
		return this.factory;
	}
	
	public PluginCanvasService canvasService;
	public PluginCommandService commandService;
	public TreeMap<Integer, PluginCanvasRegistry<SchemeCanvas>> schemes;
	public TreeMap<Integer, PluginCanvasRegistry<CircuitBoardCanvas>> circuit;
	
	public ComponentPlaceListener placeListener
			= new ComponentPlaceListener();
	
	public int internalTick = 1;
	
	public void onEnable() {
		factory = new ComponentFactory();
		placeListener.add(new ComponentPlacer(Material.AIR, factory.get(factory.id(Air.class))));
		placeListener.add(new ComponentPlacer(Material.REDSTONE, factory.get(factory.id(FullDirectionalWire.class))));
		factory.all(Torch.class, torch -> placeListener.add(new ComponentPlacer(Material.REDSTONE_TORCH_ON, torch)));
		placeListener.add(new ComponentPlacer(Material.LEVER, factory.get(factory.id(Lever.class))));
		placeListener.add(new ComponentPlacer(Material.WOOD_BUTTON, factory.get(factory.id(Button.class))));
		
		try {
			canvasService = super.getServer().getServicesManager()
					.getRegistration(PluginCanvasService.class).getProvider();
			
			schemes = new TreeMap<Integer, PluginCanvasRegistry<SchemeCanvas>>();
			canvasService.register(this, "scheme", (context) -> new SchemeCanvas(this, context));
			
			circuit = new TreeMap<Integer, PluginCanvasRegistry<CircuitBoardCanvas>>();
			canvasService.register(this, "redstone", (context) -> new CircuitBoardCanvas(this, context));
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		
		try {
			commandService = super.getServer().getServicesManager()
					.getRegistration(PluginCommandService.class).getProvider();
			commandService.registerCreate(this, "create/scheme", "scheme", 
					new CanvasCommandHandle<MapCircuitBoard, SchemeCanvas>() {
				public @Override String description() {		return "create a circuit scheme!";		}
				
				public @Override boolean handle(MapCircuitBoard arg0, CommandSender arg1, String[] arg2, SchemeCanvas arg3) {
					return true;
				}
				
				public @Override String paramList() {	return "";	}
			});
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}
}