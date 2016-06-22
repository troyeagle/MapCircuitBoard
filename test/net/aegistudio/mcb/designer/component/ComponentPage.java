package net.aegistudio.mcb.designer.component;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import net.aegistudio.mcb.designer.component.item.AirComponent;
import net.aegistudio.mcb.designer.component.item.ComponentItem;

public class ComponentPage extends Control {
	private static final long serialVersionUID = 1L;
	
	private final int pageSize;
	private int componentCount;

	private ComponentSelector owner;
	
	private ComponentItem[] components;
	private ComponentItem selected;
	
	public ComponentPage(ComponentSelector owner, int maxPageSize) {
		this.pageSize = maxPageSize;
		
		this.owner = owner;
		
		this.components = new ComponentItem[maxPageSize];
		for (int i = 0; i < maxPageSize; i++) {
			int j = i;
			ComponentItem component = new AirComponent();
			component.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (!component.isSelected()) {
						owner.setSelectedInPage(j);
					}
				}
			});
			
			this.components[i] = component;
			this.add(component);
		}
		this.componentCount = 0;

		this.setLayout(new FlowLayout(FlowLayout.LEFT, -8, 0));
		this.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		this.setSize(372, 48);
	}
	
	public boolean addComponent(ComponentItem component) {
		if (component == null || this.componentCount >= this.pageSize) return false;
		
		int i = this.componentCount;
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!component.isSelected()) {
					owner.setSelectedInPage(i);
				}
			}
		});
		this.remove(this.components[this.componentCount]);
		this.add(component, this.componentCount);
		this.components[this.componentCount] = component;
		this.componentCount++;
		
		return true;
	}
	
	public int getPageSize() {
		return this.pageSize;
	}
	
	public ComponentItem getSelected() {
		return this.selected;
	}
	
	public void setSelected(int index) {
		this.setSelected(this.components[index]);
	}
	
	public void setSelected(ComponentItem component) {
		for (ComponentItem c : this.components) {
			c.setSelected(false);
		}
		this.selected = component;
		this.selected.setSelected(true);
	}
}
