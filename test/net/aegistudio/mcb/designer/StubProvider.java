package net.aegistudio.mcb.designer;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import javax.swing.JComponent;

import net.aegistudio.mcb.Air;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.ComponentFactory;

public class StubProvider extends JComponent implements IComponentProvider {
	private static final long serialVersionUID = 1L;
	
	public StubProvider(ComponentFactory table) {
		Toolkit.getDefaultToolkit().addAWTEventListener(ae -> {
			MouseWheelEvent e = (MouseWheelEvent) ae;
			int rotation = e.getWheelRotation();
			List<net.aegistudio.mcb.Component> components = table.all();
			this.tool(table.get((table.id(component) 
					+ (rotation > 0? 1 : components.size() - 1)) % components.size()));
			System.out.println(getSelected());
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}
	
	Component component = Air.INSTANCE;
	public void tool(net.aegistudio.mcb.Component component) {
		this.component = component;
	}
	
	@Override
	public Component getSelected() {
		return component;
	}
}
