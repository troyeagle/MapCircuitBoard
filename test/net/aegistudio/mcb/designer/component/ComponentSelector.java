package net.aegistudio.mcb.designer.component;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import net.aegistudio.mcb.Air;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.ComponentFactory;
import net.aegistudio.mcb.designer.IComponentProvider;
import net.aegistudio.mcb.designer.component.item.BiInsulatedWireComponent;
import net.aegistudio.mcb.designer.component.item.ButtonComponent;
import net.aegistudio.mcb.designer.component.item.CommandBlockComponent;
import net.aegistudio.mcb.designer.component.item.ComparatorComponent;
import net.aegistudio.mcb.designer.component.item.ComponentItem;
import net.aegistudio.mcb.designer.component.item.PinComponent;
import net.aegistudio.mcb.designer.component.item.RepeaterComponent;
import net.aegistudio.mcb.designer.component.item.TorchComponent;
import net.aegistudio.mcb.designer.component.item.WireComponent;
import net.aegistudio.mcb.designer.info.DescribeInformate;
import net.aegistudio.mcb.designer.info.Informate;
import net.aegistudio.mcb.designer.info.NameInformate;


public class ComponentSelector extends Control implements IComponentProvider {
	private static final long serialVersionUID = 1L;

	static ComponentFactory table = new ComponentFactory();
	
	private final List<ComponentItem> components;
	private final List<ComponentPage> pages;
	
	private boolean f3On;
	
	private int currentPage = -1;
	private int indexInPage = -1;
	
	private static final int pageSize = 9;
	private int pageCount;
	
	private final Control componentPanel;
	
	private final Button previousPageButton;
	private final Button nextPageButton;
	
	private final Informate nameProvider;
	private final Informate descriptionProvider;
	
	private final PopupFactory popupFactory;
	private Popup tip;
	private final McTooltip tipBoard;
	
	public ComponentSelector() {
		this.setLayout(null);
		
		this.nameProvider = new NameInformate();
		this.descriptionProvider = new DescribeInformate();
		this.popupFactory = new PopupFactory();
		this.tipBoard = new McTooltip();
		
		ImageControl background = new ImageControl("assets/hotbar.png");
		background.setLocation(2, 2);

		this.componentPanel = new Control() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (!this.isEnabled()) {					
					Graphics g2 = g.create();
					g2.setColor(new Color(0x33, 0x33, 0x33, 0xC0));
					g2.fillRect(0, 0, this.getWidth(), this.getHeight());
					g2.dispose();
				}
			}
		};
		
		this.componentPanel.setLayout(null);
		this.componentPanel.setBounds(24, 0, 364 + 4, 48);
		this.add(this.componentPanel);
		
		this.components = new ArrayList<>();
		this.pages = new ArrayList<>();
		
		this.initPages();
		this.componentPanel.add(background);
		
		this.previousPageButton = new Button();
		this.previousPageButton.setInactiveBackground("assets/page_up.png");
		this.previousPageButton.setActiveBackground("assets/page_up_active.png");
		this.previousPageButton.setDisabledBackground("assets/page_up_disabled.png");
		this.previousPageButton.setBounds(2, 9, 20, 30);
		this.previousPageButton.addActionListener(e -> this.previousPage());
		this.add(this.previousPageButton);
		
		this.nextPageButton = new Button();
		this.nextPageButton.setInactiveBackground("assets/page_down.png");
		this.nextPageButton.setActiveBackground("assets/page_down_active.png");
		this.nextPageButton.setDisabledBackground("assets/page_down_disabled.png");
		this.nextPageButton.setBounds(364 + 4 + 24 + 2, 9, 20, 30);
		this.nextPageButton.addActionListener(e -> this.nextPage());
		if (this.pageCount <= 1) {
			this.previousPageButton.setEnabled(false);
			this.nextPageButton.setEnabled(false);
		}
		this.add(this.nextPageButton);
		
		this.registerEvent();
		this.setSelected(0);
		
		Dimension size = new Dimension(24 * 2 + 364 + 4, 48);
		this.setSize(size);
		this.setPreferredSize(size);
	}
	
	private void initPages() {
		this.components.add(new WireComponent());
		this.components.add(new TorchComponent());
		this.components.add(new ButtonComponent());
		this.components.add(new PinComponent());
		this.components.add(new BiInsulatedWireComponent());
		this.components.addAll(RepeaterComponent.getAll());
		this.components.addAll(ComparatorComponent.getAll());
		this.components.add(new CommandBlockComponent());
		
		ComponentPage page = null;
		
		for (ComponentItem c : this.components) {
			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if (!f3On) {
						String name = getName(c.getComponent());
						String text = c.getText();
						if (text != null && !text.isEmpty()) {
							name += " (" + text + ")";
						}
						
						tipBoard.setText(name);
						
						Point p = c.getLocationOnScreen();
						Dimension size = c.getSize();
						p.x += size.width / 2 - tipBoard.getWidth() / 2;
						p.y -= tipBoard.getHeight();
						
						showTooltip(p.x, p.y);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if (!f3On) {
						disposeTooltip();
					}
				}
			};
			c.addMouseListener(adapter);
			
			if (page == null || !page.addComponent(c)) {
				page = new ComponentPage(this, pageSize);
				this.pages.add(page);
				this.componentPanel.add(page);
				
				page.addComponent(c);
			}
		}
		
		this.pageCount = pages.size();
	}
	
	private void registerEvent() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.addAWTEventListener(event -> {
			MouseWheelEvent e = (MouseWheelEvent) event;
			e.consume();
			int rotation = e.getWheelRotation();
			if (e.isControlDown()) {
				if (rotation > 0) {
					this.nextPage();
				} else {
					this.previousPage();
				}
			}
			else {
				if (rotation > 0)
					this.nextComponent();
				else
					this.previousComponent();
			}
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		
		toolkit.addAWTEventListener(event -> {
			KeyEvent e = (KeyEvent) event;
			
			switch (e.getID()) {
			case KeyEvent.KEY_TYPED: // 不考虑输入法……
				char c = e.getKeyChar();
				if (c >= '1' && c <= '9') {
					this.setSelectedInPage(c - '1');
				} else if (c == ',' || c == '[') {
					this.previousPage();
				} else if (c == '.' || c == ']') {
					this.nextPage();
				}
				break;
				
			case KeyEvent.KEY_PRESSED:
				switch (e.getKeyCode()) {
				case KeyEvent.VK_PAGE_UP:
					this.previousPage();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					this.nextPage();
					break;
				case KeyEvent.VK_F3:
					this.f3On = !this.f3On;
					this.updateDetailTooltip();
					break;
				}
				break;
			}
		}, AWTEvent.KEY_EVENT_MASK);
	}
	
	private void setSelectedInPageImpl(int index) {
		if (!this.isEnabled()) return;
		if (index >= pageSize || index < 0) return;
		
		this.indexInPage = index;
		this.getCurrentPage().setSelected(index);
		this.updateDetailTooltip();
		this.repaint();
	}
	
	public void setSelected(int index) {
		this.setCurrentPageImpl(this.getPageIndex(index));
		this.setSelectedInPageImpl(this.getIndexInPage(index));
	}

	public void setSelectedInPage(int index) {
		if (index >= pageSize || index < 0) return;
		this.setSelectedInPageImpl(index);
	}
	
	private void setCurrentPageImpl(int index) {
		if (!this.isEnabled()) return;
		if (index >= this.pageCount || index < 0 || this.currentPage == index) return;
		
		this.currentPage = index;
		for (ComponentPage page : this.pages) {
			page.setVisible(false);
		}
		
		this.getCurrentPage().setVisible(true);
		this.repaint();
	}
	
	public void setCurrentPage(int index) {
		if (index >= this.pageCount || index < 0 || this.currentPage == index) return;
		
		this.setCurrentPageImpl(index);
		this.setSelectedInPageImpl(this.indexInPage);
	}
	
	private int getPageIndex(int itemIndex) {
		return itemIndex / pageSize;
	}
	
	private int getIndexInPage(int itemIndex) {
		return itemIndex % pageSize;
	}
	
	private ComponentPage getCurrentPage() {
		return this.pages.get(this.currentPage);
	}

	public void nextPage() {
		this.setCurrentPage((this.currentPage + 1) % this.pageCount);
	}
	
	public void previousPage() {
		this.setCurrentPage((this.currentPage + this.pageCount - 1) % this.pageCount);
	}
	
	public void nextComponent() {
		this.setSelectedInPage((this.indexInPage + 1) % pageSize); 
	}
	
	public void previousComponent() {
		this.setSelectedInPage((this.indexInPage + pageSize - 1) % pageSize);
	}

	private void updateDetailTooltip() {
		this.disposeTooltip();
		if (this.f3On) {
			ComponentItem c = this.getSelectedComponent();
			Component component = c.getComponent();
			if (component == Air.INSTANCE) return;
			
			Point p = c.getLocationOnScreen();
			
			String text;
			try {
				text = this.descriptionProvider.describe(null, c.getComponent(), null);	
			} catch (Exception e) {
				text = this.getName(component) + ":\nNo description for it :(";
			}
			
			this.tipBoard.setText(text);
			p.x += c.getWidth() / 2 - tipBoard.getWidth() / 2;
			p.y -= tipBoard.getHeight();

			this.showTooltip(p.x, p.y);
		}
	}
	
	private void showTooltip(int x, int y) {
		this.disposeTooltip();
		this.tip = this.popupFactory.getPopup(this, this.tipBoard, x, y);
		this.tip.show();
	}
	
	private void disposeTooltip() {
		if (this.tip != null)  {
			this.tip.hide();
			this.tip = null;
		}
	}
	
	private String getName(Component c) {
		String name;
		try {
			name = this.nameProvider.describe(null, c, null);					
		} catch (Exception e2) {
			name = null;
		}
		if (name == null) {
			name = c.getClass().getSimpleName();
		}
		return name;
	}
	
	private ComponentItem getSelectedComponent() {
		return this.pages.get(this.currentPage).getSelected();
	}
	
	@Override
	public Component getSelected() {
		return this.getSelectedComponent().getComponent();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.componentPanel.setEnabled(enabled);
		this.previousPageButton.setEnabled(enabled);
		this.nextPageButton.setEnabled(enabled);
	}

	public static void main(String[] args) {
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, new File("assets/mcfont.ttf"));
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ComponentSelector c = new ComponentSelector();
		Control empty = new Control();
		empty.setPreferredSize(new Dimension(800, 400));
		Control container = new Control();
		container.add(c);
		frame.add(container, BorderLayout.SOUTH);
		frame.add(empty);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
