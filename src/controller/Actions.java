package controller;

import model.Invoice;
import model.InvoicesJTableView;
import model.Items;
import model.ItemsJTableModel;
import view.InvoiceGrid;
import view.InvoiceJFrameView;
import view.ItemsGrid;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Actions implements ActionListener, ListSelectionListener {

    private InvoiceJFrameView jFrame;
    private InvoiceGrid invGrid;
    private ItemsGrid itemsGrid;

    public Actions(InvoiceJFrameView frame) {
        this.jFrame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create New Item":
                createNewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineOK":
                createLineOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = jFrame.getInvoiceTable().getSelectedRow();
        if (selectedIndex != -1) {
            Invoice currentInvoice = jFrame.getInvoices().get(selectedIndex);
            jFrame.getInvoiceNumLabel().setText("" + currentInvoice.getNum());
            jFrame.getInvoiceDateLabel().setText(currentInvoice.getDate());
            jFrame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
            jFrame.getInvoiceTotalLabel().setText("" + currentInvoice.getInvoiceTotal());
            ItemsJTableModel itemsTableModel = new ItemsJTableModel(currentInvoice.getItems());
            jFrame.getLineTable().setModel(itemsTableModel);
            itemsTableModel.fireTableDataChanged();
        }
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        try {
            int result = fc.showOpenDialog(jFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                Path path = Paths.get(file.getAbsolutePath());
                List<String> headerLines = Files.readAllLines(path);               
                ArrayList<Invoice> invoicesArray = new ArrayList<>();
                for (String headerLine : headerLines) {
                    try {
                        String[] headerParts = headerLine.split(",");
                        int invoiceNum = Integer.parseInt(headerParts[0]);
                        String invoiceDate = headerParts[1];
                        String customerName = headerParts[2];

                        Invoice invoice = new Invoice(invoiceNum, invoiceDate, customerName);
                        invoicesArray.add(invoice);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(jFrame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                result = fc.showOpenDialog(jFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    Path linePath = Paths.get(lineFile.getAbsolutePath());
                    List<String> itemLine = Files.readAllLines(linePath);
                    System.out.println("Lines have been read");
                    for (String lineLine : itemLine) {
                        try {
                            String lineParts[] = lineLine.split(",");
                            int invoiceNum = Integer.parseInt(lineParts[0]);
                            String itemName = lineParts[1];
                            double itemPrice = Double.parseDouble(lineParts[2]);
                            int count = Integer.parseInt(lineParts[3]);
                            Invoice inv = null;
                            for (Invoice invoice : invoicesArray) {
                                if (invoice.getNum() == invoiceNum) {
                                    inv = invoice;
                                    break;
                                }
                            }

                            Items line = new Items(itemName, itemPrice, count, inv);
                            inv.getItems().add(line);
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(jFrame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    System.out.println("Check point");
                }
                jFrame.setInvoices(invoicesArray);
                InvoicesJTableView invoicesTableModel = new InvoicesJTableView(invoicesArray);
                jFrame.setInvoicesTableModel(invoicesTableModel);
                jFrame.getInvoiceTable().setModel(invoicesTableModel);
                jFrame.getInvoicesTableModel().fireTableDataChanged();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(jFrame, "Cannot read file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        ArrayList<Invoice> invoices = jFrame.getInvoices();
        String headers = "";
        String lines = "";
        for (Invoice invoice : invoices) {
            String invCSV = invoice.getAsCSV();
            headers += invCSV;
            headers += "\n";

            for (Items item : invoice.getItems()) {
                String lineCSV = item.getAsCSV();
                lines += lineCSV;
                lines += "\n";
            }
        }
        System.out.println("Check point");
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(jFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter fW = new FileWriter(headerFile);
                fW.write(headers);
                fW.flush();
                fW.close();
                result = fc.showSaveDialog(jFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lfw = new FileWriter(lineFile);
                    lfw.write(lines);
                    lfw.flush();
                    lfw.close();
                }
            }
        } catch (Exception e) {

        }
    }

    private void createNewInvoice() {
        invGrid = new InvoiceGrid(jFrame);
        invGrid.setVisible(true);
    }

    private void deleteInvoice() {
        int row = jFrame.getInvoiceTable().getSelectedRow();
        if (row != -1) {
            jFrame.getInvoices().remove(row);
            jFrame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createNewItem() {
        itemsGrid = new ItemsGrid(jFrame);
        itemsGrid.setVisible(true);
    }

    private void deleteItem() {
        int row = jFrame.getLineTable().getSelectedRow();

        if (row != -1) {
            ItemsJTableModel linesTableModel = (ItemsJTableModel) jFrame.getLineTable().getModel();
            linesTableModel.getItems().remove(row);
            linesTableModel.fireTableDataChanged();
            jFrame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createInvoiceCancel() {
        invGrid.setVisible(false);
        invGrid.dispose();
        invGrid = null;
    }

    private void createInvoiceOK() {
        String date = invGrid.getInvDateField().getText();
        String cust = invGrid.getCustNameField().getText();
        int num = jFrame.getNextInvoiceNum();
        try {
            String[] dateForm = date.split("-");  
            if (dateForm.length < 3) {
                JOptionPane.showMessageDialog(jFrame, "Wrong format of date", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateForm[0]);
                int month = Integer.parseInt(dateForm[1]);
                int year = Integer.parseInt(dateForm[2]);
                if (day > 31 || month > 12) {
                    JOptionPane.showMessageDialog(jFrame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Invoice invoice = new Invoice(num, date, cust);
                    jFrame.getInvoices().add(invoice);
                    jFrame.getInvoicesTableModel().fireTableDataChanged();
                    invGrid.setVisible(false);
                    invGrid.dispose();
                    invGrid = null;
                }
            }
        } catch (Exception x) {
            JOptionPane.showMessageDialog(jFrame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void createLineOK() {
        String item = itemsGrid.getItemNameField().getText();
        String countStr = itemsGrid.getItemCountField().getText();
        String priceStr = itemsGrid.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = jFrame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            Invoice invoice = jFrame.getInvoices().get(selectedInvoice);
            Items line = new Items(item, price, count, invoice);
            invoice.getItems().add(line);
            ItemsJTableModel linesTableModel = (ItemsJTableModel) jFrame.getLineTable().getModel();
            //linesTableModel.getLines().add(line);
            linesTableModel.fireTableDataChanged();
            jFrame.getInvoicesTableModel().fireTableDataChanged();
        }
        itemsGrid.setVisible(false);
        itemsGrid.dispose();
        itemsGrid = null;
    }

    private void createLineCancel() {
        itemsGrid.setVisible(false);
        itemsGrid.dispose();
        itemsGrid = null;
    }

}
