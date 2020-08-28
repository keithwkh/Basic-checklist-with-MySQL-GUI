import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class dashboardFrame {
    public static JFrame dashFrame;
    private static JPanel dashPanel;
    private static Connection con;
    private static int recur_status;
    private static final Locale l = new Locale("en", "SG");
    private static final DateTimeFormatter f = DateTimeFormatter.ofLocalizedDate( FormatStyle.LONG ).withLocale(l);

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        // Code from Online

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnLabel(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

    public static LocalDate recurrenceEndDate (LocalDate sDate, int type, int number){
        LocalDate endDate = sDate;
        for(int i = 0; i < number + 1; i++) {
            switch (type) {
                case 0: endDate = sDate.plusDays(i); break;
                case 1: endDate = sDate.plusDays(i); break;
                case 2: endDate = sDate.plusWeeks(i); break;
                case 3: endDate = sDate.plusMonths(i); break;
            }
        }
        return endDate;
    }

    public dashboardFrame() throws IOException, SQLException {
        dashFrame = new JFrame("Tracker");
        dashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashFrame.setSize(1280, 720);

        // Main panel
        dashPanel = new JPanel();
        dashPanel.setLayout(null);
        dashPanel.setBackground(new Color(250, 250, 250));
        dashFrame.add(dashPanel);

        // Header Panel
        JPanel headerPanel = new dBLayout().headerPanel();
        JButton btnClose = new dBLayout().closeProgram();
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashFrame.dispatchEvent(new WindowEvent(dashFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
        headerPanel.add(btnClose);
        dashPanel.add(headerPanel);

        // View All Panel
        JPanel allPanel = new dBLayout().allPanel();
        JScrollPane tableSrlPanel = new JScrollPane();
        con = checkList.connectSQL(loginFrame.userInput.getText(), new String(loginFrame.passwordInput.getPassword()));
        Statement sm = con.createStatement();
        String column_query = "SELECT taskID as 'Task ID'," +
                " dateCreated as 'Date Created'," +
                " moduleIdx as 'Course Code'," +
                " moduleName as 'Course Title'," +
                " dateDue as 'Date Due'," +
                " taskName as 'Task'," +
                " taskDetail as 'Task Details'," +
                " IF(taskStatus = 0, 'Pending', 'Completed') as 'Status'," +
                " IF(taskPriority = 0, 'Normal', 'Urgent') as 'Priority'" +
                " FROM tasklist ORDER BY taskStatus ASC, dateDue ASC, taskPriority DESC"; // Task Status: Pending = 0, Completed = 1 - See pending urgent tasks first
        ResultSet rs = sm.executeQuery(column_query);
        JTable allTable = new JTable(buildTableModel(rs)){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        allTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (allTable.getSelectedRow() > -1) {
                    // print first column value from selected row
                    System.out.println(allTable.getValueAt(allTable.getSelectedRow(), 0).toString());
                }
            }
        });
        tableSrlPanel.setBounds(0, 0, allPanel.getWidth(), allPanel.getHeight());
        tableSrlPanel.getViewport().setBackground(dashPanel.getBackground());
        allTable.setOpaque(false);
        tableSrlPanel.setViewportView(allTable);

        /// Add Record Panel
        JPanel addPanel = new dBLayout().addPanel();
        JLabel lbTaskID = new dBLayout().quickLabel("Task ID", 10, 10, 120, 25);
        JTextField taskField = new dBLayout().quickTextfield("(New)", lbTaskID.getX() + lbTaskID.getWidth() + 10, lbTaskID.getY(), Math.toIntExact(Math.round((addPanel.getWidth() - lbTaskID.getX() - lbTaskID.getWidth()) / 2)), 25 );
        taskField.setEditable(false);
        JLabel lbDateCreated = new dBLayout().quickLabel("Date Created", taskField.getX() + taskField.getWidth() + 10, lbTaskID.getY(), 120, 25);
        JTextField dateCreatedField = new dBLayout().quickTextfield(LocalDate.now().format(f), lbDateCreated.getX() + lbDateCreated.getWidth() + 10, lbDateCreated.getY(), addPanel.getWidth() - lbDateCreated.getWidth() - lbDateCreated.getX() - 20, 25);
        dateCreatedField.setEditable(false);

        JLabel lbModIdx = new dBLayout().quickLabel("Course Code", lbTaskID.getX(), lbTaskID.getY() + lbTaskID.getHeight() + 10, 120, 25);
        String idxQuery = "SELECT idx FROM moduleIdx";
        ResultSet idxResult = sm.executeQuery(idxQuery);
        ArrayList<String> al_idx = new ArrayList<String>();
        while(idxResult.next()){
            String value = idxResult.getString(1);
            al_idx.add(value);
        }
        String[] indices = new String[al_idx.size() + 1];
        indices[0] = "";
        for(int j = 1; j < al_idx.size() + 1; j++){
            indices[j] = al_idx.get(j - 1);
        }
        JComboBox cbIdx = new JComboBox(indices);
        cbIdx.setBounds(lbModIdx.getX() + lbModIdx.getWidth() + 10, lbModIdx.getY(), addPanel.getWidth() - lbModIdx.getWidth() - lbModIdx.getX() - 20, 25);
        JLabel lbModName = new dBLayout().quickLabel("Course Name", lbTaskID.getX(), lbModIdx.getY() + lbModIdx.getHeight() + 10, 120, 25);
        JTextField modNameField = new dBLayout().quickTextfield("Please select Course Index first.", lbModName.getX() + lbModName.getWidth() + 10, lbModName.getY(), addPanel.getWidth() - lbModName.getX() - lbModName.getWidth() - 20, 25 );
        modNameField.setEditable(false);
        cbIdx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb_idx = (JComboBox) e.getSource();
                String selectedIdx = (String) cb_idx.getSelectedItem();
                String modNameQuery = "SELECT moduleName FROM moduleIdx WHERE idx = '" + selectedIdx + "'";
                try {
                    ResultSet nameResult = sm.executeQuery(modNameQuery);
                    while(nameResult.next()){
                        String value = nameResult.getString(1);
                        modNameField.setText(value);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        JLabel lbTaskName = new dBLayout().quickLabel("Task", lbModName.getX(), lbModName.getY() + lbModName.getHeight() + 10, 120, 25);
        JTextField taskNameField = new dBLayout().quickTextfield("", lbTaskName.getX() + lbTaskName.getWidth() + 10, lbTaskName.getY(), addPanel.getWidth() - lbTaskName.getX() - lbTaskName.getWidth() - 20, 25);
        JLabel lbTaskDetail = new dBLayout().quickLabel("Task Details", lbTaskName.getX(), lbTaskName.getY() + lbTaskName.getHeight() + 10, 120, 25);
        JTextField taskDetailField = new dBLayout().quickTextfield("", lbTaskDetail.getX() + lbTaskDetail.getWidth() + 10, lbTaskDetail.getY(), addPanel.getWidth() - lbTaskDetail.getX() - lbTaskDetail.getWidth() - 20, 25);

        JLabel lbDateDue = new dBLayout().quickLabel("Due Date", lbTaskDetail.getX(), lbTaskDetail.getY() + lbTaskDetail.getHeight() + 10, 120, 25);
        JDateChooser dueDateChooser = new JDateChooser();
        dueDateChooser.setBounds(lbDateDue.getX() + lbDateDue.getWidth() + 10, lbDateDue.getY(), Math.toIntExact(Math.round((addPanel.getWidth() - lbDateDue.getX() - lbDateDue.getWidth())/6)), 25);
        JLabel lbRecur = new dBLayout().quickLabel("Recurrence", dueDateChooser.getX() + dueDateChooser.getWidth() + 10, dueDateChooser.getY(), 80, 25);
        String[] al_recur = new String[] {"None", "Daily", "Weekly", "Monthly"};
        JComboBox cbRecur = new JComboBox(al_recur);
        cbRecur.setBounds(lbRecur.getX() + lbRecur.getWidth() + 10, lbRecur.getY(), Math.toIntExact(Math.round((addPanel.getWidth() - lbDateDue.getX() - lbDateDue.getWidth())/8)), 25);
        cbRecur.setEnabled(false);
        JLabel lbNoRecur = new dBLayout().quickLabel("# of Recurrence", cbRecur.getX() + cbRecur.getWidth() + 10, cbRecur.getY(), 120, 25);
        JTextField noRecurField = new dBLayout().quickTextfield("0", lbNoRecur.getX() + lbNoRecur.getWidth() + 10, lbNoRecur.getY(), Math.toIntExact(Math.round((addPanel.getWidth() - lbDateDue.getX() - lbDateDue.getWidth())/10)), 25);
        noRecurField.setEditable(false);
        JLabel lbEndRecur = new dBLayout().quickLabel("Recurring End Date", noRecurField.getX() + noRecurField.getWidth() + 10, noRecurField.getY(), 150, 25);
        JTextField endRecurField = new dBLayout().quickTextfield("", lbEndRecur.getX() + lbEndRecur.getWidth() + 10, lbEndRecur.getY(), addPanel.getWidth() - lbEndRecur.getWidth() - lbEndRecur.getX() - 20, 25);
        endRecurField.setEditable(false);

        dueDateChooser.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if("date".equals(evt.getPropertyName())){
                    cbRecur.setEnabled(true);
                }
            }
        });

        recur_status = 0;
        cbRecur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JComboBox cb = (JComboBox) e.getSource();
                    String selectedRecurring = (String) cb.getSelectedItem();
                    int number = Integer.parseInt(noRecurField.getText());
                    Instant inst = dueDateChooser.getDate().toInstant();
                    LocalDate dueDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();

                    switch (selectedRecurring) {
                        case "None":
                            recur_status = 0;
                            noRecurField.setText("0");
                            noRecurField.setEditable(false);
                            endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                            break;
                        case "Daily":
                            recur_status = 1;
                            noRecurField.setEditable(true);
                            endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                            break;
                        case "Weekly":
                            recur_status = 2;
                            noRecurField.setEditable(true);
                            endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                            break;
                        case "Monthly":
                            recur_status = 3;
                            noRecurField.setEditable(true);
                            endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                            break;
                    }
                } catch (NullPointerException nullPointerException){
                    nullPointerException.printStackTrace();
                }
            }
        });
        noRecurField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDate(e);
            }

            private void updateDate(DocumentEvent e){
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Integer.parseInt(noRecurField.getText()) >= 0) {
                                String selectedRecurring = (String) cbRecur.getSelectedItem();
                                int number = Integer.parseInt(noRecurField.getText());
                                Instant inst = dueDateChooser.getDate().toInstant();
                                LocalDate dueDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();

                                switch (selectedRecurring) {
                                    case "None":
                                        recur_status = 0;
                                        noRecurField.setText("0");
                                        noRecurField.setEditable(false);
                                        endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                                        break;
                                    case "Daily":
                                        recur_status = 1;
                                        noRecurField.setEditable(true);
                                        endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                                        break;
                                    case "Weekly":
                                        recur_status = 2;
                                        noRecurField.setEditable(true);
                                        endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                                        break;
                                    case "Monthly":
                                        recur_status = 3;
                                        noRecurField.setEditable(true);
                                        endRecurField.setText(recurrenceEndDate(dueDate, recur_status, number).format(f));
                                        break;
                                }
                            }
                        } catch (Exception exception){
                            exception.printStackTrace();
                        }
                    }
                });
            }
        });

        JLabel lbStatus = new dBLayout().quickLabel("Status", lbDateDue.getX(), lbDateDue.getY() + lbDateDue.getHeight() + 10, 120, 25);
        JTextField statusField = new dBLayout().quickTextfield("Pending", lbStatus.getX() + lbStatus.getWidth() + 10, lbStatus.getY(), Math.toIntExact(Math.round((addPanel.getWidth()/2) - lbStatus.getX() - lbStatus.getWidth() - 10)), 25);
        statusField.setEditable(false);

        JLabel lbPriority = new dBLayout().quickLabel("Priority", statusField.getX() + statusField.getWidth() + 10, statusField.getY(), 120, 25);
        String[] al_prior = new String[]{"Normal", "Urgent"};
        JComboBox cbPriority = new JComboBox(al_prior);
        cbPriority.setBounds(lbPriority.getX() + lbPriority.getWidth() + 10, lbPriority.getY(), allPanel.getWidth() - lbPriority.getX() - lbPriority.getWidth() - 20, 25);

        JScrollPane previewPanel = new JScrollPane();
        previewPanel.getViewport().setBackground(Color.white);
        previewPanel.setBounds(0, Math.toIntExact(Math.round(addPanel.getHeight() / 2)), addPanel.getWidth(), Math.toIntExact(Math.round(addPanel.getHeight() /2)));
        ResultSet previewPane = sm.executeQuery(column_query);
        JTable previewTable = new JTable(buildTableModel(previewPane)) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        previewPanel.setViewportView(previewTable);

        JButton btnSubmit = new dBLayout().submitNew();
        btnSubmit.setBounds(allPanel.getWidth() - 120, previewPanel.getY() - 50, 100, 30);
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDate creationDate = LocalDate.now();
                String courseCode = (String) cbIdx.getSelectedItem();
                String courseName = modNameField.getText();
                String taskName = taskNameField.getText();
                String taskDets = taskDetailField.getText();
                Instant dueInstant = dueDateChooser.getDate().toInstant();
                LocalDate dueLocalDate = dueInstant.atZone(ZoneId.systemDefault()).toLocalDate();

                String freq_mode = (String) cbRecur.getSelectedItem();
                int freq = Integer.parseInt(noRecurField.getText());
                String prior = (String) cbPriority.getSelectedItem();
                int prior_type = 0;
                if (!prior.equals("Normal")){prior_type = 1;}

                String newItemQuery = "INSERT INTO tasklist (dateCreated, moduleIdx, moduleName, dateDue, taskName, taskDetail, taskStatus, taskPriority) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                for(int i = 0; i < freq + 1; i++) {
                    try (PreparedStatement statement = con.prepareStatement(newItemQuery)) {
                        statement.setDate(1, java.sql.Date.valueOf(creationDate));
                        statement.setString(2, courseCode);
                        statement.setString(3, courseName);
                        switch (freq_mode) {
                            case "None":
                                statement.setDate(4, java.sql.Date.valueOf(dueLocalDate)); break;
                            case "Daily":
                                statement.setDate(4, java.sql.Date.valueOf(dueLocalDate.plusDays(i))); break;
                            case "Weekly":
                                statement.setDate(4, java.sql.Date.valueOf(dueLocalDate.plusWeeks(i))); break;
                            case "Monthly":
                                statement.setDate(4, java.sql.Date.valueOf(dueLocalDate.plusMonths(i))); break;
                        }
                        statement.setString(5, taskName);
                        statement.setString(6, taskDets);
                        statement.setInt(7, 0);
                        statement.setInt(8, prior_type);
                        statement.executeUpdate();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }

                try {
                    ResultSet refresh = sm.executeQuery(column_query);
                    previewTable.setModel(buildTableModel(refresh));
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }

                cbIdx.setSelectedIndex(0);
                cbPriority.setSelectedIndex(0);
                cbRecur.setSelectedIndex(0);
                cbRecur.setEnabled(false);
                dueDateChooser.setDate(null);
                modNameField.setText("Please select Course Index first.");
                taskDetailField.setText(null);
                taskNameField.setText(null);
                endRecurField.setText(null);
                noRecurField.setText("0");
                taskField.setText(null);

            }
        });

        addPanel.add(lbTaskID); addPanel.add(taskField); addPanel.add(lbDateCreated); addPanel.add(dateCreatedField);
        addPanel.add(lbModIdx); addPanel.add(cbIdx);
        addPanel.add(lbModName); addPanel.add(modNameField);
        addPanel.add(lbTaskName); addPanel.add(taskNameField); addPanel.add(lbTaskDetail); addPanel.add(taskDetailField);
        addPanel.add(lbDateDue); addPanel.add(dueDateChooser);
        addPanel.add(lbRecur); addPanel.add(cbRecur);
        addPanel.add(lbNoRecur); addPanel.add(noRecurField);
        addPanel.add(lbEndRecur); addPanel.add(endRecurField);
        addPanel.add(lbStatus); addPanel.add(statusField);
        addPanel.add(lbPriority); addPanel.add(cbPriority);
        addPanel.add(btnSubmit);

        addPanel.add(previewPanel);
        allPanel.add(tableSrlPanel);
        dashPanel.add(allPanel);
        dashPanel.add(addPanel);

        // Edit Panel
        JPanel editPanel = new dBLayout().editPanel();
        JScrollPane editPane = new JScrollPane();
        editPane.getViewport().setBackground(Color.white);
        editPane.setBounds(0, 0, addPanel.getWidth(), Math.toIntExact(Math.round(addPanel.getHeight() /2)));
        ResultSet editQuery = sm.executeQuery(column_query);
        JTable editTable = new JTable(buildTableModel(editQuery)) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        editPane.setViewportView(editTable);

        JLabel lbTaskId = new dBLayout().quickLabel("Task ID", 10, editPane.getHeight() + 20, 120, 25);
        JTextField taskFielde = new dBLayout().quickTextfield("Select a task!", lbTaskId.getX() + lbTaskId.getWidth() + 10, lbTaskId.getY(), Math.toIntExact(Math.round(editPanel.getWidth()/2 - lbTaskId.getX() - lbTaskId.getWidth())), 25 );
        taskFielde.setEditable(false);
        JLabel lbDateCreatede = new dBLayout().quickLabel("Date Created", taskFielde.getX() + taskFielde.getWidth() + 10, lbTaskId.getY(), 120, 25);
        JTextField dateCreatedFielde = new dBLayout().quickTextfield(LocalDate.now().format(f), lbDateCreatede.getX() + lbDateCreatede.getWidth() + 10, lbDateCreatede.getY(), editPanel.getWidth() - lbDateCreatede.getWidth() - lbDateCreatede.getX() - 20, 25);
        dateCreatedFielde.setEditable(false);

        JLabel lbModIdxe = new dBLayout().quickLabel("Course Code", lbTaskId.getX(), lbTaskId.getY() + lbTaskId.getHeight() + 10, 120, 25);
        JTextField modIdxField = new dBLayout().quickTextfield("", lbModIdxe.getX() + lbModIdxe.getWidth() + 10, lbModIdxe.getY(), Math.toIntExact(Math.round(editPanel.getWidth()/2 - lbModIdxe.getX() - lbModIdxe.getWidth())), 25);
        modIdxField.setEditable(false);
        JLabel lbModNameE = new dBLayout().quickLabel("Course Name", modIdxField.getX() + modIdxField.getWidth() + 10, modIdxField.getY(), 120, 25);
        JTextField modNameFielde = new dBLayout().quickTextfield("", lbModNameE.getX() + lbModNameE.getWidth() + 10, lbModNameE.getY(), editPanel.getWidth() - lbModNameE.getWidth() - lbModNameE.getX() - 20, 25);
        modNameFielde.setEditable(false);

        JLabel lbTaskNameE = new dBLayout().quickLabel("Task Name", lbModIdxe.getX(), lbModIdxe.getY() + lbModIdxe.getHeight() + 10, 120, 25);
        JTextField taskNameFieldE = new dBLayout().quickTextfield("", lbTaskNameE.getX() + lbTaskNameE.getWidth() + 10, lbTaskNameE.getY(), Math.toIntExact(Math.round((editPanel.getWidth()/2) - lbTaskNameE.getWidth() - lbTaskNameE.getX())), 25);
        JLabel lbTaskDetailE = new dBLayout().quickLabel("Task Detail", taskNameFieldE.getX() + taskNameFieldE.getWidth() + 10, taskNameFieldE.getY(), 120, 25);
        JTextField taskDetailFieldE = new dBLayout().quickTextfield("", lbTaskDetailE.getX() + lbTaskDetailE.getWidth() + 10, lbTaskDetailE.getY(), editPanel.getWidth() - lbTaskDetailE.getX() - lbTaskDetailE.getWidth() -20, 25);

        JLabel lbDueDate = new dBLayout().quickLabel("Due Date", lbTaskNameE.getX(), lbTaskNameE.getY() + lbTaskNameE.getHeight() + 10, 120, 25);
        JDateChooser editDueDateChooser = new JDateChooser();
        editDueDateChooser.setBounds(lbDueDate.getX() + lbDueDate.getWidth() + 10, lbDueDate.getY(), Math.toIntExact(Math.round(editPanel.getWidth()/2 - lbDueDate.getWidth() - lbDueDate.getX())), 25);

        JLabel lbPrior = new dBLayout().quickLabel("Priority", editDueDateChooser.getX() + editDueDateChooser.getWidth() + 10, editDueDateChooser.getY(), 120, 25);
        JComboBox cbEditPrior = new JComboBox(al_prior);
        cbEditPrior.setBounds(lbPrior.getX() + lbPrior.getWidth() + 10, lbPrior.getY(), editPanel.getWidth() - lbPrior.getX() - lbPrior.getWidth() - 20, 25);

        JButton btnEdited = new dBLayout().editExisting();
        btnEdited.setBounds(editPanel.getWidth() - 120, cbEditPrior.getY() + cbEditPrior.getHeight() + 20, 100, 25);
        btnEdited.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskId = Integer.parseInt(taskFielde.getText());
                if (!(taskId <= 0)) {
                    String updateQuery = "UPDATE tasklist SET dateDue = ?, taskName = ?, taskDetail = ?, taskPriority = ? WHERE taskID = ?";
                    String taskNameE = taskNameFieldE.getText();
                    String taskDetsE = taskDetailFieldE.getText();
                    Instant dueInstantE = editDueDateChooser.getDate().toInstant();
                    LocalDate dueLocalDateE = dueInstantE.atZone(ZoneId.systemDefault()).toLocalDate();
                    String priorE = (String) cbEditPrior.getSelectedItem();
                    int prior_typeE = 0;
                    if (!priorE.equals("Normal")) {prior_typeE = 1;}

                    try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                        statement.setDate(1, java.sql.Date.valueOf(dueLocalDateE));
                        statement.setString(2, taskNameE);
                        statement.setString(3, taskDetsE);
                        statement.setInt(4, prior_typeE);
                        statement.setInt(5, taskId);
                        statement.executeUpdate();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    try {
                        ResultSet refresh = sm.executeQuery(column_query);
                        editTable.setModel(buildTableModel(refresh));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    taskFielde.setText(null);
                    dateCreatedFielde.setText(null);
                    modIdxField.setText(null);
                    modNameFielde.setText(null);
                    taskNameFieldE.setText(null);
                    taskDetailFieldE.setText(null);
                    editDueDateChooser.setDate(null);
                    cbEditPrior.setSelectedIndex(0);

                }
            }
        });

        JButton btnComplete = new dBLayout().completeTask();
        btnComplete.setBounds(editPanel.getWidth() - 280, btnEdited.getY(), 140, 25);
        btnComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskId = Integer.parseInt(taskFielde.getText());
                if (!(taskId <= 0)) {
                    String updateQuery = "UPDATE tasklist SET taskStatus = ? WHERE taskID = ?";

                    try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                        statement.setInt(1, 1);
                        statement.setInt(2, taskId);
                        statement.executeUpdate();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    try {
                        ResultSet refresh = sm.executeQuery(column_query);
                        editTable.setModel(buildTableModel(refresh));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    taskFielde.setText(null);
                    dateCreatedFielde.setText(null);
                    modIdxField.setText(null);
                    modNameFielde.setText(null);
                    taskNameFieldE.setText(null);
                    taskDetailFieldE.setText(null);
                    editDueDateChooser.setDate(null);
                    cbEditPrior.setSelectedIndex(0);

                }
            }
        });

        JButton btnDelete = new dBLayout().deleteTask();
        btnDelete.setBounds(editPanel.getWidth() - 440, btnEdited.getY(), 140, 25);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskId = Integer.parseInt(taskFielde.getText());
                if (!(taskId <= 0)) {
                    String updateQuery = "DELETE FROM tasklist WHERE taskID = ?";

                    try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                        statement.setInt(1, taskId);
                        statement.executeUpdate();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    try {
                        ResultSet refresh = sm.executeQuery(column_query);
                        editTable.setModel(buildTableModel(refresh));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

                    taskFielde.setText(null);
                    dateCreatedFielde.setText(null);
                    modIdxField.setText(null);
                    modNameFielde.setText(null);
                    taskNameFieldE.setText(null);
                    taskDetailFieldE.setText(null);
                    editDueDateChooser.setDate(null);
                    cbEditPrior.setSelectedIndex(0);

                }
            }
        });

        editTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(editTable.getSelectedRow() >= 0){
                    int taskSelected = (int) editTable.getValueAt(editTable.getSelectedRow(), 0);
                    String editFetchQuery = "SELECT * FROM tasklist WHERE taskID = " + taskSelected;
                    try {
                        ResultSet fetchedResult = sm.executeQuery(editFetchQuery);
                        while(fetchedResult.next()){
                            taskFielde.setText(fetchedResult.getString(1));
                            modIdxField.setText(fetchedResult.getString(3));
                            modNameFielde.setText(fetchedResult.getString(4));
                            editDueDateChooser.setDate(fetchedResult.getDate(5));
                            taskNameFieldE.setText(fetchedResult.getString(6));
                            taskDetailFieldE.setText(fetchedResult.getString(7));
                            switch(fetchedResult.getInt(9)){
                                case 0: cbEditPrior.setSelectedIndex(0); break;
                                case 1: cbEditPrior.setSelectedIndex(1); break;
                            }
                            dateCreatedFielde.setText(fetchedResult.getString(2));
                        }
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        });

        editPanel.add(lbTaskId); editPanel.add(taskFielde); editPanel.add(lbDateCreatede); editPanel.add(dateCreatedFielde);
        editPanel.add(lbModIdxe); editPanel.add(modIdxField); editPanel.add(lbModNameE); editPanel.add(modNameFielde);
        editPanel.add(lbTaskNameE); editPanel.add(taskNameFieldE); editPanel.add(lbTaskDetailE); editPanel.add(taskDetailFieldE);
        editPanel.add(lbDueDate); editPanel.add(editDueDateChooser);
        editPanel.add(lbPrior); editPanel.add(cbEditPrior);
        editPanel.add(btnEdited); editPanel.add(btnComplete); editPanel.add(btnDelete);

        editPanel.add(editPane);
        dashPanel.add(editPanel);
        // Side bar
        JPanel sideBar = new dBLayout().sideBar();

        JButton btnView = new dBLayout().viewButton();
        btnView.addChangeListener(e -> {
            DefaultButtonModel model = (DefaultButtonModel) btnView.getModel();
            if (model.isRollover()){
                btnView.setBackground(new Color(0, 45, 15));
            } else {
                btnView.setBackground(new Color(0, 82, 50));
            }
        });
        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allPanel.setVisible(true);
                try {
                    ResultSet rf = sm.executeQuery(column_query);
                    allTable.setModel(buildTableModel(rf));
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                addPanel.setVisible(false);
                editPanel.setVisible(false);
            }
        });

        JButton btnAdd = new dBLayout().addButton();
        btnAdd.addChangeListener(e -> {
            DefaultButtonModel model = (DefaultButtonModel) btnAdd.getModel();
            if (model.isRollover()){
                btnAdd.setBackground(new Color(0, 45, 15));
            } else {
                btnAdd.setBackground(new Color(0, 82, 50));
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allPanel.setVisible(false);
                addPanel.setVisible(true);
                try {
                    ResultSet updateAdd = sm.executeQuery(column_query);
                    previewTable.setModel(buildTableModel(updateAdd));
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                editPanel.setVisible(false);
            }
        });

        JButton btnEdit = new dBLayout().editButton();
        btnEdit.addChangeListener(e -> {
            DefaultButtonModel model = (DefaultButtonModel) btnEdit.getModel();
            if (model.isRollover()){
                btnEdit.setBackground(new Color(0, 45, 15));
            } else {
                btnEdit.setBackground(new Color(0, 82, 50));
            }
        });
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPanel.setVisible(true);
                try {
                    ResultSet updateEdit = sm.executeQuery(column_query);
                    editTable.setModel(buildTableModel(updateEdit));
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                allPanel.setVisible(false);
                addPanel.setVisible(false);
            }
        });

        sideBar.add(btnView); sideBar.add(btnAdd); sideBar.add(btnEdit);
        dashPanel.add(sideBar);

        // Render dashFrame
        allPanel.setVisible(true); addPanel.setVisible(false); editPanel.setVisible(false);
        dashFrame.setUndecorated(true);
        dashFrame.setLocationRelativeTo(null);
        dashFrame.setVisible(true);
    }

    public static void main(String[] args) throws SQLException, IOException {
        new dashboardFrame();
    }
}


