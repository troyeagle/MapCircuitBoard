package net.aegistudio.mcb.designer.grid;

public class History {
	class RetraceTreeNode {
		Action action;
		RetraceTreeNode next, previous;
	}
	

	RetraceTreeNode current;
	final RetraceTreeNode head = new RetraceTreeNode(); {
		clear();
	}
	
	public synchronized void perform(Action action) {
		synchronized(current) {
			current.next = new RetraceTreeNode();
			current.next.previous = current;
			
			current = current.next;
			current.action = action;
			current.next = head;
			action.redo();
		}
	}
	
	public void redo() {
		if(canRedo()) {
			current = current.next;
			current.action.redo();
		}
	}
	
	public void undo() {
		if(canUndo()) {
			current.action.undo();
			current = current.previous;
		}
	}
	
	public boolean canUndo() {
		return current != head;
	}
	
	public boolean canRedo() {
		return current.next != head;
	}
	
	public void clear() {
		current = head.next = head.previous = head;
	}
}
