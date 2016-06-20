package net.aegistudio.mcb.designer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.aegistudio.mcb.ComponentFactory;
import net.aegistudio.mcb.designer.grid.History;
import net.aegistudio.mcb.designer.grid.LayoutGridEditor;
import net.aegistudio.mcb.layout.LayoutGrid;
import net.aegistudio.mcb.layout.SpectatedLayoutGrid;
import net.aegistudio.mcb.stdaln.AwtGridComponent;

public class McbDesigner extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public final ComponentFactory table = new ComponentFactory();
	public final StubProvider provider = new StubProvider(table);
	
	JMenu file;	JMenuItem newFile, openFile, saveFile;
	JMenu edit; JMenuItem menuRedo, menuUndo;
	
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
		
		JMenuBar menubar = new JMenuBar();
		super.setJMenuBar(menubar);
		
		file = new JMenu("File");
		menubar.add(file);
		
		newFile = new JMenuItem("New");
		newFile.addActionListener(a -> newGridComponent());
		file.add(newFile);
		file.addSeparator();
		
		openFile = new JMenuItem("Open Layout");
		openFile.addActionListener(a -> openGridComponent());
		openFile.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		file.add(openFile);
		
		saveFile = new JMenuItem("Save Layout");
		saveFile.addActionListener(a -> saveGridComponent());
		saveFile.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		file.add(saveFile);
		
		edit = new JMenu("Edit");
		menubar.add(edit);
		
		menuUndo = new JMenuItem("Undo");
		menuUndo.addActionListener(a -> undo());
		menuUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
		edit.add(menuUndo);
		
		menuRedo = new JMenuItem("Redo");
		menuRedo.addActionListener(a -> redo());
		menuRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
		edit.add(menuRedo);
		
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
		gridComponent.setLocation(0, 0);
		gridComponent.addMouseListener(refreshAdapter);
		
		this.add(gridComponent);
		if(newComponent instanceof LayoutGridEditor)
			this.layout = (LayoutGridEditor) newComponent;
		
		this.setSize(gridComponent.getWidth(), gridComponent.getHeight() 
				+ this.getJMenuBar().getPreferredSize().height);
		this.repaint();
	}
	
	/**
	 * Command: New
	 */
	public void newGridComponent() {
		if(!ensureSaved()) return;
		LayoutGrid grid = new SpectatedLayoutGrid(); 
		resetGridComponent(new LayoutGridEditor(grid, provider));
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
			resetGridComponent(new LayoutGridEditor(grid, provider));
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
	
	public void updateHistory() {
		History history = layout.history;
		setTitle((history.canUndo()? "*MCB Designer" : "MCB Designer"));
		menuRedo.setEnabled(history.canRedo());
		menuUndo.setEnabled(history.canUndo());
	}
	
	public void undo() {
		layout.history.undo();
		updateHistory();
	}
	
	public void redo() {
		layout.history.undo();
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
	}
}
