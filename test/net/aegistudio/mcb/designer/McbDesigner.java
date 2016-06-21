package net.aegistudio.mcb.designer;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.aegistudio.mcb.ComponentFactory;
import net.aegistudio.mcb.board.ActualGrid;
import net.aegistudio.mcb.board.SpectatedActualGrid;
import net.aegistudio.mcb.designer.grid.ActualGridEditor;
import net.aegistudio.mcb.designer.grid.History;
import net.aegistudio.mcb.designer.grid.LayoutGridEditor;
import net.aegistudio.mcb.designer.info.Informate;
import net.aegistudio.mcb.designer.info.PseudoInformate;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mcb.layout.SpectatedLayoutGrid;
import net.aegistudio.mcb.stdaln.AwtGridComponent;

public class McbDesigner extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public final ComponentFactory table = new ComponentFactory();
	public final StubProvider provider = new StubProvider(table);
	public final Informate informate = new PseudoInformate();
	
	JMenu file;	JMenuItem newFile, openFile, saveFile;
	JMenu edit; JMenuItem menuRedo, menuUndo;	JButton toolRedo, toolUndo;
	JMenu simulate; JMenuItem menuDesign, menuSimulate, menuContinous;
	JButton toolDesign, toolSimulate, toolContinous, toolStep;
	
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
		super.add(provider);
		
		super.add(toolbar);
		
		JMenuBar menubar = new JMenuBar();
		super.setJMenuBar(menubar);
		
		// File Menu.
		file = new JMenu("File");
		menubar.add(file);
		
		// New File.
		newFile = new JMenuItem("New");
		newFile.addActionListener(a -> newGridComponent());
		file.add(newFile);
		file.addSeparator();
		
		JButton newButton = new JButton("New");
		newButton.setToolTipText("New");
		newButton.addActionListener(a -> newGridComponent());
		newButton.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(newButton);

		// Open File.
		openFile = new JMenuItem("Open Layout");
		openFile.addActionListener(a -> openGridComponent());
		openFile.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		file.add(openFile);
		
		JButton openButton = new JButton("Open");
		openButton.setToolTipText("Open");
		openButton.addActionListener(a -> openGridComponent());
		openButton.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(openButton);
		
		// Save File.
		saveFile = new JMenuItem("Save Layout");
		saveFile.addActionListener(a -> saveGridComponent());
		saveFile.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		file.add(saveFile);
		
		JButton saveButton = new JButton("Save");
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
		
		toolUndo = new JButton("Undo");
		toolUndo.setToolTipText("Undo");
		toolUndo.addActionListener(a -> undo());
		toolUndo.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolUndo);
		
		// Redo.
		menuRedo = new JMenuItem("Redo");
		menuRedo.addActionListener(a -> redo());
		menuRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
		edit.add(menuRedo);
		
		toolRedo = new JButton("Redo");
		toolRedo.setToolTipText("Redo");
		toolRedo.addActionListener(a -> redo());
		toolRedo.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolRedo);
		
		toolbar.addSeparator();
		
		// Simulate Menu.
		simulate = new JMenu("Simulate");
		menubar.add(simulate);
		
		// Design.
		menuDesign = new JCheckBoxMenuItem("Design");
		menuDesign.addActionListener(a -> endSimulate());
		menuDesign.setSelected(true);
		simulate.add(menuDesign);
		
		toolDesign = new JButton("Design");
		toolDesign.setToolTipText("Design");
		toolDesign.addActionListener(a -> endSimulate());
		toolDesign.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolDesign);
		
		// Simulate.
		menuSimulate = new JCheckBoxMenuItem("Simulate");
		menuSimulate.addActionListener(a -> beginSimulate());
		simulate.add(menuSimulate);
		
		toolSimulate = new JButton("Simulate");
		toolSimulate.setToolTipText("Simulate");
		toolSimulate.addActionListener(a -> beginSimulate());
		toolSimulate.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolSimulate);
		
		simulate.addSeparator();
		
		menuContinous = new JCheckBoxMenuItem("Continous");
		menuContinous.addActionListener(a -> continousSwitch());
		menuContinous.setSelected(true);
		simulate.add(menuContinous);
		
		toolContinous = new JButton("Cont");
		toolContinous.addActionListener(a -> continousSwitch());
		toolContinous.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolContinous);
		
		toolStep = new JButton("Step");
		toolStep.addActionListener(a -> step());
		toolStep.setSelected(true);
		toolStep.setPreferredSize(TOOL_BAR_SIZE);
		toolbar.add(toolStep);
		
		toolbar.addSeparator();
		
		JMenu help = new JMenu("Help");
		JMenuItem instruction = new JMenuItem("Instruction");
		instruction.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "<html><p>Please follow the instruction:</p>"
				+ "<li><b>Left Click</b>: Place or remove components"
				+ "<li><b>Right Click</b>: Interact with pointing component"
				+ "<li><b>Scroll Up/Down</b>: Select previous/next component"
				+ "</html>", "Usage", JOptionPane.PLAIN_MESSAGE);
		});
		instruction.setAccelerator(KeyStroke.getKeyStroke("F1"));
		help.add(instruction);
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "<html><b>Map Circuit Board</b> - Layout Debugger (beta)"
				+ "<br><b>Author</b>: aegistudio"
				+ "<br><b>Purpose</b>:<br>"
				+ "<li>interactive test of layout"
				+ "<li>design layout</html>", 
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
		

		toolbar.setSize(gridComponent.getWidth(), TOOL_BAR_HEIGHT);
		toolbar.setLocation(0, 0);
		
		this.add(gridComponent);
		if(newComponent instanceof LayoutGridEditor)
			this.layout = (LayoutGridEditor) newComponent;
		
		this.setSize(gridComponent.getWidth(), gridComponent.getHeight() 
				+ this.getJMenuBar().getPreferredSize().height + TOOL_BAR_HEIGHT);
		this.repaint();
	}
	
	/**
	 * Command: New
	 */
	public void newGridComponent() {
		if(!ensureSaved()) return;
		LayoutGrid grid = new SpectatedLayoutGrid(); 
		resetGridComponent(new LayoutGridEditor(informate, grid, provider));
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
			LayoutGrid grid = new LayoutGrid();	grid.load(input, table);
			resetGridComponent(new LayoutGridEditor(informate, grid, provider));
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
			layout.grid.save(output, table);
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
					
					menuSimulate.setSelected(true);
					menuSimulate.setEnabled(false);
					toolSimulate.setEnabled(false);
					
					toolContinous.setVisible(true);
					toolStep.setVisible(true);
					
					menuRedo.setEnabled(false);
					menuUndo.setEnabled(false);
				}
				else {
					menuDesign.setSelected(true);
					menuDesign.setEnabled(false);
					toolDesign.setEnabled(false);
					
					toolContinous.setVisible(false);
					toolStep.setVisible(false);
					
					menuSimulate.setSelected(false);
					menuSimulate.setEnabled(true);
					toolSimulate.setEnabled(true);
					
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
		resetGridComponent(new ActualGridEditor(informate, grid));
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
	
	public static void main(String[] arguments) {
		try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");} catch(Exception e) {}

		McbDesigner frame = new McbDesigner();
		frame.newGridComponent();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.autoRefresh.start();
	}
}
