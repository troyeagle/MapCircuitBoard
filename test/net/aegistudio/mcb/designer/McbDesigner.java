package net.aegistudio.mcb.designer;

import java.awt.Color;
import java.awt.Dimension;
//import java.awt.Point;
//import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.bukkit.Material;

import net.aegistudio.mcb.Air;
import net.aegistudio.mcb.Cell;
import net.aegistudio.mcb.Component;
import net.aegistudio.mcb.ComponentFactory;
import net.aegistudio.mcb.Data;
import net.aegistudio.mcb.board.ActualGrid;
import net.aegistudio.mcb.board.SpectatedActualGrid;
import net.aegistudio.mcb.designer.component.ComponentSelector;
import net.aegistudio.mcb.designer.grid.ActualGridEditor;
import net.aegistudio.mcb.designer.grid.History;
import net.aegistudio.mcb.designer.grid.LayoutGridEditor;
import net.aegistudio.mcb.designer.info.DescribeInformate;
import net.aegistudio.mcb.designer.info.Informate;
import net.aegistudio.mcb.designer.info.NameInformate;
import net.aegistudio.mcb.designer.info.StatusInformate;
import net.aegistudio.mcb.layout.ComponentPlacer;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mcb.layout.SpectatedLayoutGrid;
import net.aegistudio.mcb.stdaln.AwtGridComponent;
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

public class McbDesigner extends JFrame implements Informate {
	private static final long serialVersionUID = 1L;
	
	public final ComponentFactory factory = new ComponentFactory();
	public final HashMap<Class<?>, ComponentPlacer> placer = new HashMap<>();
	private void add(ComponentPlacer placer) {
		this.placer.put(placer.component.getClass(), placer);
	}
	
	{
		this.add(new ComponentPlacer(Material.AIR, factory.get(factory.id(Air.class))));
		this.add(new ComponentPlacer(Material.REDSTONE, factory.get(factory.id(FullDirectionalWire.class))));
		factory.all(Torch.class, torch -> this.add(new ComponentPlacer(Material.REDSTONE_TORCH_ON, torch)));
		this.add(new ComponentPlacer(Material.LEVER, factory.get(factory.id(Lever.class))));
		this.add(new ComponentPlacer(Material.WOOD_BUTTON, factory.get(factory.id(Button.class))));
		this.add(new ComponentPlacer(Material.STONE_BUTTON, factory.get(factory.id(MonitorPin.class))));
		this.add(new ComponentPlacer(Material.STONE_BUTTON, factory.get(factory.id(OriginatorPin.class))));
		factory.all(BiInsulatedWire.class, insulated -> this.add(new ComponentPlacer(Material.POWERED_RAIL, insulated)));
		factory.all(Repeater.class, repeater -> this.add(new ComponentPlacer(Material.DIODE, repeater)));
		factory.all(Comparator.class, comparator -> this.add(new ComponentPlacer(Material.REDSTONE_COMPARATOR, comparator)));
		factory.all(CommandBlock.class, command -> this.add(new ComponentPlacer(Material.COMMAND, command)));
	}
	public final ComponentSelector provider;
	
	JMenu file;	JMenuItem newFile, openFile, saveFile;
	JMenu edit; JMenuItem menuRedo, menuUndo;	JButton toolRedo, toolUndo;
	JMenu simulate; JMenuItem menuDesign, menuSimulate, menuContinous;
	JToggleButton toolDesign, toolSimulate; 
	JButton toolStep;
	JToggleButton toolContinous;
	
	JMenu view; JMenuItem menuShowName, menuShowDescription, menuShowStatus;
	JToggleButton toolShowName, toolShowDescription, toolShowStatus;
	
	public static final int TOOL_BAR_HEIGHT = 40;
	public static final Dimension TOOL_BAR_SIZE 
		= new Dimension(TOOL_BAR_HEIGHT, TOOL_BAR_HEIGHT);
	JToolBar toolbar = new JToolBar(); {
		toolbar.setFloatable(false);
	}
	
	public McbDesigner() {
		super("MCB Designer");
		super.setLayout(null);
		super.setResizable(false);
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if(ensureSaved()) {
					setVisible(false);
					dispose();
				}
			}
		});
		
		ComponentSelector itembar = new ComponentSelector(this);
		this.provider = itembar;
		super.add(itembar);
		
		super.add(toolbar);
		toolbar.addSeparator();
		
		JMenuBar menubar = new JMenuBar();
		//super.setJMenuBar(menubar);
		
		// File Menu.
		file = new JMenu("File");
		menubar.add(file);
		
		// New File.
		newFile = new JMenuItem("New");
		newFile.addActionListener(a -> newGridComponent());
		file.add(newFile);
		file.addSeparator();
		
		JButton newButton = new JButton(new ImageIcon("assets/new.png"));
		newButton.setToolTipText("New");
		newButton.addActionListener(a -> newGridComponent());
		newButton.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(newButton);

		// Open File.
		openFile = new JMenuItem("Open Layout");
		openFile.addActionListener(a -> openGridComponent());
		openFile.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		file.add(openFile);
		
		JButton openButton = new JButton(new ImageIcon("assets/open.png"));
		openButton.setToolTipText("Open");
		openButton.addActionListener(a -> openGridComponent());
		openButton.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(openButton);
		
		// Save File.
		saveFile = new JMenuItem("Save Layout");
		saveFile.addActionListener(a -> saveGridComponent());
		saveFile.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		file.add(saveFile);
		
		JButton saveButton = new JButton(new ImageIcon("assets/save.png"));
		saveButton.setToolTipText("Save");
		saveButton.addActionListener(a -> saveGridComponent());
		saveButton.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(saveButton);
		
		toolbar.addSeparator();
		
		// Edit Menu.
		edit = new JMenu("Edit");
		menubar.add(edit);
		
		// Undo.
		menuUndo = new JMenuItem("Undo");
		menuUndo.addActionListener(a -> undo());
		menuUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
		edit.add(menuUndo);
		
		toolUndo = new JButton(new ImageIcon("assets/undo.png")) {
			private static final long serialVersionUID = 1L;

			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				super.setForeground(enabled? Color.BLACK : Color.GRAY);
			}
		};
		toolUndo.setToolTipText("Undo");
		toolUndo.addActionListener(a -> undo());
		toolUndo.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolUndo);
		
		// Redo.
		menuRedo = new JMenuItem("Redo");
		menuRedo.addActionListener(a -> redo());
		menuRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
		edit.add(menuRedo);
		
		toolRedo = new JButton(new ImageIcon("assets/redo.png")){
			private static final long serialVersionUID = 1L;

			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				super.setForeground(enabled? Color.BLACK : Color.GRAY);
			}
		};
		toolRedo.setToolTipText("Redo");
		toolRedo.addActionListener(a -> redo());
		toolRedo.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolRedo);
		
		toolbar.addSeparator();
		
		// Menu
		view = new JMenu("View");
		menubar.add(view);
		
		menuShowName = new JCheckBoxMenuItem("Show name");
		menuShowName.addActionListener(a -> showName());
		view.add(menuShowName);
		
		toolShowName = new JToggleButton(new ImageIcon("assets/show_name.png"));
		toolShowName.addActionListener(a -> showName());
		toolShowName.setToolTipText("Show name");
		toolShowName.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolShowName);
		
		menuShowDescription = new JCheckBoxMenuItem("Show description");
		menuShowDescription.addActionListener(a -> showDescription());
		view.add(menuShowDescription);

		toolShowDescription = new JToggleButton(new ImageIcon("assets/show_description.png"));
		toolShowDescription.addActionListener(a -> showDescription());
		toolShowDescription.setToolTipText("Show description");
		toolShowDescription.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolShowDescription);
		
		menuShowStatus = new JCheckBoxMenuItem("Show status");
		menuShowStatus.addActionListener(a -> showStatus());
		view.add(menuShowStatus);
		
		toolShowStatus = new JToggleButton(new ImageIcon("assets/show_status.png"));
		toolShowStatus.setToolTipText("Show status");
		toolShowStatus.addActionListener(a -> showStatus());
		toolShowStatus.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolShowStatus);
		
		toolbar.addSeparator();
		
		// Simulate Menu.
		simulate = new JMenu("Simulate");
		menubar.add(simulate);
		
		// Design.
		menuDesign = new JCheckBoxMenuItem("Design");
		menuDesign.addActionListener(a -> endSimulate());
		menuDesign.setSelected(true);
		simulate.add(menuDesign);
		
		toolDesign = new JToggleButton(new ImageIcon("assets/design.png"));
		toolDesign.setToolTipText("Design");
		toolDesign.addActionListener(a -> endSimulate());
		toolDesign.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolDesign);
		
		// Simulate.
		menuSimulate = new JCheckBoxMenuItem("Simulate");
		menuSimulate.addActionListener(a -> beginSimulate());
		simulate.add(menuSimulate);
		
		toolSimulate = new JToggleButton(new ImageIcon("assets/simulation.png"));
		toolSimulate.setToolTipText("Simulate");
		toolSimulate.addActionListener(a -> beginSimulate());
		toolSimulate.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolSimulate);
		
		simulate.addSeparator();
		
		menuContinous = new JCheckBoxMenuItem("Continous");
		menuContinous.addActionListener(a -> continousSwitch());
		menuContinous.setSelected(true);
		simulate.add(menuContinous);
		
		toolContinous = new JToggleButton(new ImageIcon("assets/continous.png"));
		toolContinous.setToolTipText("Enable/Disable Continous");
		toolContinous.addActionListener(a -> continousSwitch());
		toolContinous.setPreferredSize(TOOL_BAR_SIZE);
		toolContinous.setSelected(true);
		toolbar.add(toolContinous);
		
		toolStep = new JButton(new ImageIcon("assets/step.png"));
		toolStep.setToolTipText("Step");
		toolStep.addActionListener(a -> step());
		toolStep.setSelected(true);
		toolStep.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolStep);
		
		toolbar.addSeparator();
		
		JMenu help = new JMenu("Help");
		JMenuItem instruction = new JMenuItem("Instruction");
		instruction.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "<html><p>Please follow the instruction:</p>"
				+ "<li><b>Left Click</b>: Place/remove components"
				+ "<li><b>Right Click</b>: Place/interact with pointing component"
				+ "<li><b>Scroll Up/Down</b>: Select previous/next component"
				+ "</html>", "Usage", JOptionPane.PLAIN_MESSAGE);
		});
		instruction.setAccelerator(KeyStroke.getKeyStroke("F1"));
		help.add(instruction);
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "<html><b>Map Circuit Board Designer</b></html>", 
				"About", JOptionPane.INFORMATION_MESSAGE);
		});
		help.add(about);
		menubar.add(help);
		
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_SPACE)
					step();
			}
		});
	}
	
	public final MouseAdapter refreshAdapter = new MouseAdapter() {
		public void mouseReleased(MouseEvent me) {
			updateHistory();
		}
	};
	public LayoutGridEditor layout;
	public AwtGridComponent gridComponent;
	public void resetGridComponent(AwtGridComponent newComponent) {
		if(gridComponent != null) this.remove(gridComponent);
		
		gridComponent = newComponent;
		gridComponent.setLocation(0, TOOL_BAR_HEIGHT);
		gridComponent.addMouseListener(refreshAdapter);
		
		//Point originalLocation = this.isVisible()? 
		//		this.getLocation() : null;

		toolbar.setSize(gridComponent.getWidth(), TOOL_BAR_HEIGHT);
		toolbar.setLocation(0, 0);
		
		this.add(gridComponent);
		if(newComponent instanceof LayoutGridEditor) {
			this.layout = (LayoutGridEditor) newComponent;
			this.provider.setVisible(true);
		}
		else this.provider.setVisible(false);
		
		this.provider.setLocation((gridComponent.getWidth() - this.provider.getWidth()) / 2 , 
				gridComponent.getHeight() + TOOL_BAR_HEIGHT);
		
		this.setSize(gridComponent.getWidth(), gridComponent.getHeight() 
				//+ (provider.isVisible()? provider.getHeight() : 0) 
				+ provider.getHeight()
				+ /*this.getJMenuBar().getPreferredSize().height +*/ TOOL_BAR_HEIGHT);
		
		this.repaint();
	}
	
	/**
	 * Command: New
	 */
	public void newGridComponent() {
		if(!ensureSaved()) return;
		LayoutGrid grid = new SpectatedLayoutGrid(); 
		resetGridComponent(new LayoutGridEditor(this, placer, grid, provider));
		updateHistory();
	}
	
	public final JFileChooser chooser = new JFileChooser(); {
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				if(arg0.isDirectory()) return true;
				return arg0.getName().endsWith(".lyt");
			}

			@Override
			public String getDescription() {
				return "Layout File (*.lyt)";
			}
		});
	};
	
	/**
	 * Command: Open
	 */
	public void openGridComponent() {
		if(!ensureSaved()) return;
		if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) try {
			FileInputStream input = new FileInputStream(chooser.getSelectedFile());
			LayoutGrid grid = new LayoutGrid();	grid.load(input, factory);
			resetGridComponent(new LayoutGridEditor(this, placer, grid, provider));
			updateHistory();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), 
					"Fail to open!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Command: Save
	 * Return: Saved?
	 */
	public boolean saveGridComponent() {
		if(JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) try {
			File save = chooser.getSelectedFile();
			if(!save.getName().endsWith(".lyt")) {
				save = new File(save.getParentFile(), save.getName().concat(".lyt"));
				chooser.setSelectedFile(save);
			}
			
			if(save.exists()) {
				if(JOptionPane.showConfirmDialog(this, "File already exists, are you sure to replace?")
						!= JOptionPane.YES_OPTION) return false;
			}
			FileOutputStream output = new FileOutputStream(save);
			layout.grid.save(output, factory);
			layout.history.clear();
			updateHistory();
			return true;
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), 
					"Fail to save!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return false;
	}
	
	Thread autoRefresh = new Thread() {
		public void run() {
			while(!disposed) try {
				if(gridComponent instanceof ActualGridEditor) {
					if(continous) ((ActualGridEditor)gridComponent).tick();
					
					menuDesign.setSelected(false);
					menuDesign.setEnabled(true);
					
					toolDesign.setEnabled(true);
					toolDesign.setSelected(false);
					
					menuSimulate.setSelected(true);
					menuSimulate.setEnabled(false);
					toolSimulate.setEnabled(false);
					toolSimulate.setSelected(true);
					
					toolContinous.setVisible(true);
					toolStep.setVisible(true);
					
					menuRedo.setEnabled(false);
					menuUndo.setEnabled(false);
					toolRedo.setEnabled(false);
					toolUndo.setEnabled(false);
				}
				else {
					menuDesign.setSelected(true);
					menuDesign.setEnabled(false);
					toolDesign.setEnabled(false);
					toolDesign.setSelected(true);
					
					toolContinous.setVisible(false);
					toolStep.setVisible(false);
					
					menuSimulate.setSelected(false);
					menuSimulate.setEnabled(true);
					
					toolSimulate.setEnabled(true);
					toolSimulate.setSelected(false);
					updateHistory();
				}

				Thread.sleep(200L);
			}
			catch(Throwable e) {
				//e.printStackTrace();
			}
		}
	};
	
	public void dispose() {
		disposed = true;
		super.dispose();
	}
	
	boolean disposed = false;
	boolean continous = true;
	public void beginSimulate() {
		ActualGrid grid = new SpectatedActualGrid((LayoutGrid) this.layout.grid); 
		resetGridComponent(new ActualGridEditor(this, grid));
	}
	
	public void endSimulate() {
		resetGridComponent(layout);
	}
	
	public void step() {
		if(gridComponent instanceof ActualGridEditor)
			((ActualGridEditor) gridComponent).tick();
	}
	
	public void continousSwitch() {
		continous = !continous;
		menuContinous.setSelected(continous);
		toolContinous.setSelected(continous);
	}
	
	public void updateHistory() {
		History history = layout.history;
		setTitle((history.canUndo()? "*MCB Designer" : "MCB Designer"));
		menuRedo.setEnabled(history.canRedo());
		toolRedo.setEnabled(history.canRedo());
		menuUndo.setEnabled(history.canUndo());
		toolUndo.setEnabled(history.canUndo());
	}
	
	public void undo() {
		layout.history.undo();
		updateHistory();
	}
	
	public void redo() {
		layout.history.redo();
		updateHistory();
	}
	
	/**
	 * Only if ensure saved return true,
	 * could you do further steps.
	 */
	public boolean ensureSaved() {
		if(layout == null) return true;
		if(layout.history.canUndo()) {
			int result = JOptionPane.showConfirmDialog(this, "You have unsaved data, would you like to save?", 
					"Saving", JOptionPane.YES_NO_CANCEL_OPTION);
			if(result == JOptionPane.YES_OPTION) return this.saveGridComponent();
			else return JOptionPane.NO_OPTION == result;
		}
		return true;
	}


	public final Informate informate = new StatusInformate();
	public final Informate descrption = new DescribeInformate();
	public final Informate name = new NameInformate();
	@Override
	public String describe(Cell cell, Component component, Data data) {
		StringBuilder builder = new StringBuilder();
		
		if((cell == null && data == null) || showName) {
			String name = this.name.describe(cell, component, data);
			if(name != null) {
				builder.append("\n\n");
				builder.append(name);
			}
		}
		
		if(showDescription) {
			String description = this.descrption.describe(cell, component, data);
			if(description != null) {
				builder.append("\n\n");
				builder.append(description);
			}
		}
		
		if((!(cell == null && data == null)) && showStatus) {
			String status = this.informate.describe(cell, component, data);
			if(status != null) {
				builder.append("\n\n");
				builder.append(status);
			}
		}
		
		String result = new String(builder);
		return result.length() > 0? result.trim() : null;
	}

	boolean showName;
	public void showName() {
		showName = !showName;
		menuShowName.setSelected(showName);
		toolShowName.setSelected(showName);
	}
	
	boolean showDescription;
	public void showDescription() {
		showDescription = !showDescription;
		menuShowDescription.setSelected(showDescription);
		toolShowDescription.setSelected(showDescription);
	}
	
	boolean showStatus;
	public void showStatus() {
		showStatus = !showStatus;
		menuShowStatus.setSelected(showStatus);
		toolShowStatus.setSelected(showStatus);
	}
	
	public static void main(String[] arguments) {
		try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");} catch(Exception e) {}

		McbDesigner frame = new McbDesigner();
		frame.newGridComponent();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.showName();
		frame.autoRefresh.start();
	}
}
