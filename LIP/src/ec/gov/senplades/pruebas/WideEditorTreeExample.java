package ec.gov.senplades.pruebas;



//Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html

/*  
* 
* This example works only each Icon has same width.
*
* editing JTextField has minimum 100. (swing1.1beta3) swing#1007
*
*/

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
//


/**
* @version 1.0 11/09/98
*/

public class WideEditorTreeExample extends JFrame {
public WideEditorTreeExample() {
 super("Wide Editor JTree Example");
 String[] strs = { "swing", // 0
     "plaf", // 1
     "basic", // 2
     "metal", // 3
     "JTree" }; // 4
 DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[strs.length];
 for (int i = 0; i < strs.length; i++)
   nodes[i] = new DefaultMutableTreeNode(strs[i]);
 nodes[0].add(nodes[1]);
 nodes[1].add(nodes[2]);
 nodes[1].add(nodes[3]);
 nodes[0].add(nodes[4]);
 JTree tree = new JTree(nodes[0]);
 tree.setEditable(true);

 /*
  * swing1.0.3 tree.setCellEditor(new BasicTreeCellEditor(
  * (BasicTreeCellRenderer)tree.getCellRenderer()) { public void
  * doLayout() { if (editor != null) { Dimension cSize = getSize();
  * Dimension eSize = editor.getPreferredSize(); int n =
  * lastPath.getPathCount(); Rectangle r = new Rectangle(); r =
  * changeTree.getBounds(r); eSize.width = r.width -(editingOffset *n);
  * editor.setSize(eSize); editor.setLocation(editingOffset, 0);
  * editor.setBounds(editingOffset, 0, eSize.width, cSize.height);
  * setSize(new Dimension(eSize.width + editingOffset, cSize.height)); } } } );
  */

 // swing1.1beta3
 tree.setCellEditor(new WideCellEditor(tree,
     (DefaultTreeCellRenderer) tree.getCellRenderer()));
 //

 JScrollPane sp = new JScrollPane(tree);
 getContentPane().add(sp, BorderLayout.CENTER);
}

public static void main(String args[]) {
 try {
     UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
 } catch (Exception evt) {}

 WideEditorTreeExample frame = new WideEditorTreeExample();
 frame.addWindowListener(new WindowAdapter() {
   public void windowClosing(WindowEvent e) {
     System.exit(0);
   }
 });
 frame.setSize(300, 150);
 frame.setVisible(true);
}
}

class WideCellEditor extends DefaultTreeCellEditor {

public WideCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
 this(tree, renderer, null);
}

public WideCellEditor(final JTree tree,
   final DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
 super(tree, renderer, editor);
 editingContainer = new WideEditorContainer();
}

public Component getTreeCellEditorComponent(JTree tree, Object value,
   boolean isSelected, boolean expanded, boolean leaf, int row) {
 Component c = super.getTreeCellEditorComponent(tree, value, isSelected,
     expanded, leaf, row);
 ((WideEditorContainer) editingContainer).setLocalCopy(tree, lastPath,
     offset, editingComponent);
 return c;
}

class WideEditorContainer extends DefaultTreeCellEditor.EditorContainer {
 JTree tree;

 TreePath lastPath;

 int offset;

 Component editingComponent;

 public void doLayout() {
   if (editingComponent != null) {
     Dimension cSize = getSize();
     Dimension eSize = editingComponent.getPreferredSize();
     int n = lastPath.getPathCount();
     Rectangle r = new Rectangle();
     r = tree.getBounds(r);
     eSize.width = r.width - (offset * n);
     editingComponent.setSize(eSize);
     editingComponent.setLocation(offset, 0);
     editingComponent
         .setBounds(offset, 0, eSize.width, cSize.height);
     setSize(new Dimension(eSize.width + offset, cSize.height));
   }
 }

 void setLocalCopy(JTree tree, TreePath lastPath, int offset,
     Component editingComponent) {
   this.tree = tree;
   this.lastPath = lastPath;
   this.offset = offset;
   this.editingComponent = editingComponent;
 }
}
}

