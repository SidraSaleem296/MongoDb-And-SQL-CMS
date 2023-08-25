package Final_DBMS_Project.src.main.java.DB_project.Tables;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class all_admins extends JFrame implements ActionListener {
    DefaultTableModel model = new DefaultTableModel();
    Container cnt = this.getContentPane();
    JTable jtbl = new JTable(model);
    public all_admins(ResultSet Rs) {
        cnt.setBackground(Color.orange);
        this.setTitle("Admins List");
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cnt.setLayout(new FlowLayout(FlowLayout.LEFT));
        model.addColumn("User ID");
        model.addColumn("Admin Name");
        model.addColumn("Salary");
        model.addColumn("Phone");
        model.addColumn("Shift");
        try {
            while (Rs.next()) {
                model.addRow(new Object[]{Rs.getInt(1), Rs.getString(2), Rs.getInt(3), Rs.getString(4), Rs.getString(5)});
            }
        }
        catch (Exception e){

        }
        JScrollPane pg = new JScrollPane(jtbl);
        JButton close = new JButton("Close");
        close.setBackground(new java.awt.Color(0, 0, 51));
        close.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        close.setForeground(new java.awt.Color(255, 255, 255));
        close.setBorderPainted(false);
        close.addActionListener(this);
        cnt.add(pg);
        cnt.add(close);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equalsIgnoreCase("close")){
            this.dispose();
        }
    }
}
