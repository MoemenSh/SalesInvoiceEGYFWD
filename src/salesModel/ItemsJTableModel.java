
package salesModel;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class ItemsJTableModel extends AbstractTableModel {

    private ArrayList<Items> items;
    private String[] columns = {"No.", "Item Name", "Item Price", "Count", "Item Total"};

    public ItemsJTableModel(ArrayList<Items> items) {
        this.items = items;
    }

    public ArrayList<Items> getItems() {
        return items;
    }
    
    
    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int x) {
        return columns[x];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Items item = items.get(rowIndex);
        
        switch(columnIndex) {
            case 0: return item.getInvoice().getNum();
            case 1: return item.getItem();
            case 2: return item.getPrice();
            case 3: return item.getCount();
            case 4: return item.getItemTotal();
            default : return "";
        }
    }
    
}
