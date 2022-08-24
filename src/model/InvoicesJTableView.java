
package model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class InvoicesJTableView extends AbstractTableModel {
    private ArrayList<Invoice> invoices;
    private String[] cols = {"No.", "Date", "Customer", "Total"};
    
    public InvoicesJTableView(ArrayList<Invoice> invoices) {
        this.invoices = invoices;
    }
    
    @Override
    public int getRowCount() {
        return invoices.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Invoice invoice = invoices.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return invoice.getNum();
            case 1: return invoice.getDate();
            case 2: return invoice.getCustomer();
            case 3: return invoice.getInvoiceTotal();
            default : return "NoThing Returned";
        }
    }
}
