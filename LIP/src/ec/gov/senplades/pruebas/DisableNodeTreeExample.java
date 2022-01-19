package ec.gov.senplades.pruebas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @version 1.1 04/30/99
 */
public class DisableNodeTreeExample extends JFrame {
  public DisableNodeTreeExample() {
    super("DisableNode TreeExample");

    String[] strs = { "swing", // 0
        "plaf", // 1
        "basic", // 2
        "metal", // 3
        "JTree", // 4
        "plaf", // 5
        "motif" }; // 6

    DisabledNode[] nodes = new DisabledNode[strs.length];
    for (int i = 0; i < strs.length; i++) {
      nodes[i] = new DisabledNode(strs[i]);
    }
    nodes[0].add(nodes[1]);
    nodes[1].add(nodes[2]);
    nodes[1].add(nodes[3]);
    nodes[0].add(nodes[4]);
    nodes[0].add(nodes[5]);
    nodes[5].add(nodes[6]);
    nodes[3].setEnabled(false);
    nodes[5].setEnabled(false);

    final JTree tree = new JTree(nodes[0]) {
      public boolean isPathEditable(TreePath path) {
        DisabledNode node = (DisabledNode) path.getLastPathComponent();
        if (node.isEnabled()) {
          return isEditable();
        } else {
          return false;
        }
      }
    };

    DisabledRenderer renderer = new DisabledRenderer();
    renderer.setDisabledLeafIcon(new DisabledLeafIcon());
    renderer.setDisabledOpenIcon(new DisabledFolderIcon());
    renderer.setDisabledClosedIcon(new DisabledFolderIcon());
    tree.setCellRenderer(renderer);
    tree.setEditable(true);

    JScrollPane sp = new JScrollPane(tree);
    ButtonPanel bp = new ButtonPanel(tree);
    getContentPane().add(sp, BorderLayout.CENTER);
    getContentPane().add(bp, BorderLayout.EAST);
  }

  class ButtonPanel extends JPanel {
    JTree tree;

    DefaultTreeModel model;

    ButtonPanel(JTree tree) {
      this.tree = tree;
      model = (DefaultTreeModel) tree.getModel();
      setLayout(new GridLayout(2, 1));
      setBorder(new TitledBorder("Change Node"));
      JButton enabledButton = new JButton("Enabled");
      JButton disabledButton = new JButton("Disabled");
      add(enabledButton);
      add(disabledButton);
      enabledButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setNodeEnabled(true);
        }
      });
      disabledButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setNodeEnabled(false);
        }
      });
    }

    private void setNodeEnabled(boolean enabled) {
      TreePath[] path = tree.getSelectionPaths();
      if (path == null)
        return;
      DisabledNode node = null;
      for (int i = 0; i < path.length; i++) {
        node = (DisabledNode) path[i].getLastPathComponent();
        //if (node.isRoot()) {
        //  System.out.println("refused: root node");
        //} else {
        int beforeChildCount = node.getChildCount();
        node.setEnabled(enabled);
        int afterChildCount = node.getChildCount();
        if (beforeChildCount == afterChildCount) {
          model.nodeChanged(node);
        } else {
          model.reload();
        }
        //}
      }
    }
  }

  public static void main(String args[]) {
    
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
    
    DisableNodeTreeExample frame = new DisableNodeTreeExample();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    frame.setSize(300, 180);
    frame.setVisible(true);
  }
}

class DisabledRenderer extends DefaultTreeCellRenderer {
  protected Icon disabledLeafIcon;

  protected Icon disabledOpenIcon;

  protected Icon disabledClosedIcon;

  public DisabledRenderer() {
    this(new GraydIcon(UIManager.getIcon("Tree.leafIcon")), new GraydIcon(
        UIManager.getIcon("Tree.openIcon")), new GraydIcon(UIManager
        .getIcon("Tree.closedIcon")));
  }

  public DisabledRenderer(Icon leafIcon, Icon openIcon, Icon closedIcon) {
    setDisabledLeafIcon(leafIcon);
    setDisabledOpenIcon(openIcon);
    setDisabledClosedIcon(closedIcon);
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {
    String stringValue = tree.convertValueToText(value, sel, expanded,
        leaf, row, hasFocus);
    setText(stringValue);
    if (sel) {
      setForeground(getTextSelectionColor());
    } else {
      setForeground(getTextNonSelectionColor());
    }

    boolean treeIsEnabled = tree.isEnabled();
    boolean nodeIsEnabled = ((DisabledNode) value).isEnabled();
    boolean isEnabled = (treeIsEnabled && nodeIsEnabled);
    setEnabled(isEnabled);

    if (isEnabled) {
      selected = sel;
      if (leaf) {
        setIcon(getLeafIcon());
      } else if (expanded) {
        setIcon(getOpenIcon());
      } else {
        setIcon(getClosedIcon());
      }
    } else {
      selected = false;
      if (leaf) {
        if (nodeIsEnabled) {
          setDisabledIcon(getLeafIcon());
        } else {
          setDisabledIcon(disabledLeafIcon);
        }
      } else if (expanded) {
        if (nodeIsEnabled) {
          setDisabledIcon(getOpenIcon());
        } else {
          setDisabledIcon(disabledOpenIcon);
        }
      } else {
        if (nodeIsEnabled) {
          setDisabledIcon(getClosedIcon());
        } else {
          setDisabledIcon(disabledClosedIcon);
        }
      }
    }
    return this;
  }

  public void setDisabledLeafIcon(Icon icon) {
    disabledLeafIcon = icon;
  }

  public void setDisabledOpenIcon(Icon icon) {
    disabledOpenIcon = icon;
  }

  public void setDisabledClosedIcon(Icon icon) {
    disabledClosedIcon = icon;
  }
}

class DisabledNode extends DefaultMutableTreeNode {
  protected boolean enabled;

  public DisabledNode() {
    this(null, true, true);
  }

  public DisabledNode(Object userObject) {
    this(userObject, true, true);
  }

  public DisabledNode(Object userObject, boolean allowsChildren) {
    this(userObject, allowsChildren, true);
  }

  public DisabledNode(Object userObject, boolean allowsChildren,
      boolean enabled) {
    super(userObject, allowsChildren);
    this.enabled = enabled;
  }

  public int getChildCount() {
    if (enabled) {
      return super.getChildCount();
    } else {
      return 0;
    }
  }

  public boolean isLeaf() {
    return (super.getChildCount() == 0);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}

class DisabledLeafIcon implements Icon {
  int width = 16;

  int height = 16;

  int additionalHeight = 4;

  public void paintIcon(Component c, Graphics g, int x, int y) {
    int right = width - 1;
    int bottom = height + 1;

    g.setColor(c.getBackground());
    g.fillRect(0, 0, width, getIconHeight());

    // Draw frame
    g.setColor(MetalLookAndFeel.getControlDisabled());
    g.drawLine(2, 2, 2, bottom); // left
    g.drawLine(2, 2, right - 4, 2); // top
    g.drawLine(2, bottom, right - 1, bottom); // bottom
    g.drawLine(right - 1, 8, right - 1, bottom); // right
    g.drawLine(right - 6, 4, right - 2, 8); // slant 1
    g.drawLine(right - 5, 3, right - 4, 3); // part of slant 2
    g.drawLine(right - 3, 4, right - 3, 5); // part of slant 2
    g.drawLine(right - 2, 6, right - 2, 7); // part of slant 2
  }

  public int getIconWidth() {
    return width;
  }

  public int getIconHeight() {
    return height + additionalHeight;
  }
}

class DisabledFolderIcon implements Icon {
  int width = 16;

  int height = 16;

  int additionalHeight = 2;

  public void paintIcon(Component c, Graphics g, int x, int y) {
    int right = width - 1;
    int bottom = height - 2;

    g.setColor(c.getBackground());
    g.fillRect(0, 0, width, getIconHeight());

    // Draw tab
    g.setColor(MetalLookAndFeel.getControlDisabled());
    g.drawLine(right - 5, 2, right, 2);
    g.drawLine(right - 6, 3, right - 6, 4);
    g.drawLine(right, 3, right, 4);

    // Draw outline
    g.setColor(MetalLookAndFeel.getControlDisabled());
    g.drawLine(0, 5, 0, bottom); // left side
    g.drawLine(1, 4, right - 7, 4); // first part of top
    g.drawLine(right - 6, 5, right - 1, 5); // second part of top
    g.drawLine(right, 4, right, bottom); // right side
    g.drawLine(0, bottom, right, bottom); // bottom
  }

  public int getIconWidth() {
    return width;
  }

  public int getIconHeight() {
    return height + additionalHeight;
  }
}

class GraydIcon implements Icon {
  Icon icon;

  Image image;

  public GraydIcon(Icon icon) {
    this.icon = icon;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (image == null) {
      Image orgImage = c.createImage(getIconWidth(), getIconHeight());
      Graphics imageG = orgImage.getGraphics();
      Color background = c.getBackground();
      imageG.setColor(background);
      imageG.fillRect(0, 0, getIconWidth(), getIconHeight());

      icon.paintIcon(c, imageG, x, y);

      ImageFilter colorfilter = new GrayFilter();
      image = c.createImage(new FilteredImageSource(orgImage.getSource(),
          colorfilter));

    }
    g.drawImage(image, x, y, null);
  }

  public int getIconWidth() {
    return icon.getIconWidth();
  }

  public int getIconHeight() {
    return icon.getIconHeight();
  }

  class GrayFilter extends RGBImageFilter {

    public GrayFilter() {
      // If I set ture, black is gone?!
      //canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
      int r = (rgb & 0xff0000) >> 16;
      int g = (rgb & 0x00ff00) >> 8;
      int b = (rgb & 0x0000ff);
      int iy = (int) (r + g + b) / 3;
      iy = Math.min(255, iy);
      return ((rgb & 0xff000000) | (iy << 16) | (iy << 8) | iy);
    }
  }

}