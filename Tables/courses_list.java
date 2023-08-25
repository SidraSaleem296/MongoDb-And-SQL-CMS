package Final_DBMS_Project.src.main.java.DB_project.Tables;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class courses_list extends JFrame implements ActionListener {
    DefaultTableModel model = new DefaultTableModel();
    Container cnt = this.getContentPane();
    JTable jtbl = new JTable(model);
    public courses_list(ResultSet Rs) {
        cnt.setBackground(Color.orange);
        this.setTitle("Courses List");
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cnt.setLayout(new FlowLayout(FlowLayout.LEFT));
        model.addColumn("Course ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Discipline");
        try {
            while (Rs.next()) {
                model.addRow(new Object[]{Rs.getInt(1), Rs.getString(2), Rs.getString(3), Rs.getString(4)});
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
