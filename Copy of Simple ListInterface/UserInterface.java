import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JFrame;

public class UserInterface extends JFrame
                        implements ActionListener {
  
  //The database
  private StockDatabase db;
  
  private JButton sell;        // To decrement (in the model) the stock level of the selected item (in the view)
  private JButton stock;       // To prompt the view to show stock level of the selected item
  private JButton quit;        // As it says
  
  // The view
  private JPanel view;
  private JList stockList = new JList(new DefaultListModel());  // The visible list widget
  private JScrollPane scrollList = new JScrollPane(stockList);  // Embedded in a scrolling window
  
  private Vector indexList;                           // The indices in the DB of displayed list of items

  private JTextArea description = new JTextArea(5,20);  // For item details when requested
  private int showingDetailsOf = -1;             // Index in DB of item to display details of,
                                                 // none initially
  // User interface constructor
  public UserInterface(StockDatabase db) {
    this.db = db;
    setTitle("Controller");
    // Set up input GUI
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Container window = getContentPane();
    window.setLayout(new FlowLayout());     // The default is that JFrame uses BorderLayout

    sell = new JButton("Sell");
    window.add(sell);
    sell.addActionListener(this);
    stock = new JButton("Show stock level");
    window.add(stock);
    stock.addActionListener(this);
    quit = new JButton("Quit");
    window.add(quit);
    quit.addActionListener(this);
    
    // Create view
    view = new JPanel();
    view.setBackground(Color.cyan);
    view.setPreferredSize(new Dimension(450, 100));
    view.add(new JLabel("Items in stock"));
    stockList.setVisibleRowCount(5);
    view.add(scrollList);
    view.add(description);
    window.add(view);
    
    update();                              // Set up display from database initially
    
    // Display the frame
    setSize(500,200);
    setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == sell) {
      int n = getSelected();    // Index of item in the DB, or -1 if none selected
      if (n != -1)  
        db.decrement(n);
    }

    if (e.getSource() == stock) {
      int n = getSelected();    // Index of item in the DB, or -1 if none selected
      if (n != -1)
        showingDetailsOf = n;
    }
    
    if (e.getSource() == quit)
      System.exit(0);
    
    update();
  }
    
  // Re-populate the displayed stock list from the database
  private void update() {
    indexList = db.getInStock();    // Get list of indices of in-stock items in the DB
    DefaultListModel theList = (DefaultListModel) stockList.getModel();  // Get the displayed stock list
    theList.clear();     // Clear all items from the displayed stock list
    // Then re-fill it with in-stock items
    for (Object itemIndex: indexList)   // Process each in-stock item index
      theList.addElement(db.getName((Integer)itemIndex));    // Add name of stock item to the displayed stockList
    showDetails();                        // Update the details display
  }
    
  // Update the details display for the selected item
  private void showDetails() {
    if (showingDetailsOf == -1)
      description.setText("");
    else
      description.setText("Stock level of \n"
                          + db.getName(showingDetailsOf) + "\n"
                          + "is "+db.getStockLevel(showingDetailsOf));
  }
  
  // Return index in the DB of item selected in the stockList, -1 if none selected
  public int getSelected() {
    int listLocation = stockList.getSelectedIndex();  // The selected item or -1
    if (listLocation == -1)
    	return -1;                    // Nothing selected
    else 
    	return (Integer)indexList.elementAt(listLocation);    // Position in the DB; note: auto unboxing of result
  }
  
   
}
