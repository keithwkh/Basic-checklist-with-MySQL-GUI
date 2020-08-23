import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dBLayout {
    public static JPanel allPanel;

    public JPanel sideBar(){
        JPanel sideBar = new JPanel();
        sideBar.setLayout(null);
        sideBar.setBounds(0, Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())), Math.toIntExact(Math.round(0.15 * dashboardFrame.dashFrame.getWidth())), dashboardFrame.dashFrame.getHeight() - Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())));
        sideBar.setBackground(new Color(0, 105, 60));
        JLabel version_control = new JLabel("V0.0.1", SwingConstants.CENTER);
        version_control.setBounds(0, sideBar.getHeight() - 30, sideBar.getWidth(), 20);
        version_control.setFont(new Font(font.getName(), Font.BOLD, 11));
        version_control.setForeground(Color.white);
        sideBar.add(version_control);
        return sideBar;
    }
    public JPanel headerPanel() throws IOException {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, dashboardFrame.dashFrame.getWidth(), Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())));
        headerPanel.setBackground(new Color(255, 255, 255));

        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMMM yyyy");
        Date date = new Date();
        JLabel systemPrompt = new JLabel(formatter.format(date));
        systemPrompt.setHorizontalAlignment(SwingConstants.RIGHT);
        systemPrompt.setBounds(headerPanel.getWidth() - 155, headerPanel.getHeight() - 30, 150, 30);
        systemPrompt.setFont(new Font(font.getName(), Font.BOLD, 13));

        BufferedImage logo = ImageIO.read(getClass().getResource("img/NTU.jpg"));
        Image tmp = logo.getScaledInstance(sideBar().getWidth(), headerPanel.getHeight(), Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(sideBar().getWidth(), headerPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        JLabel logoLabel = new JLabel(new ImageIcon(dimg));
        logoLabel.setBounds(0, 0, sideBar().getWidth(), headerPanel.getHeight());

        headerPanel.add(logoLabel);
        headerPanel.add(systemPrompt);
        return headerPanel;
    }

    public JPanel allPanel() throws IOException {
        allPanel = new JPanel();
        allPanel.setLayout(null);
        allPanel.setBounds(sideBar().getWidth(), Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())), dashboardFrame.dashFrame.getWidth() - sideBar().getWidth(), dashboardFrame.dashFrame.getHeight() - headerPanel().getHeight());
        allPanel.setVisible(true);
        return allPanel;
    }
    public JPanel addPanel() throws IOException {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(null);
        addPanel.setBounds(sideBar().getWidth(), Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())), dashboardFrame.dashFrame.getWidth() - sideBar().getWidth(), dashboardFrame.dashFrame.getHeight() - headerPanel().getHeight());
        addPanel.setOpaque(false);
        addPanel.setVisible(true);
        return addPanel;
    }
    public JPanel editPanel() throws IOException {
        JPanel editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(sideBar().getWidth(), Math.toIntExact(Math.round(0.1 * dashboardFrame.dashFrame.getHeight())), dashboardFrame.dashFrame.getWidth() - sideBar().getWidth(), dashboardFrame.dashFrame.getHeight() - headerPanel().getHeight());
        editPanel.setVisible(true);
        return editPanel;
    }

    public JButton viewButton(){
        JButton btnView = new JButton("View All");
        btnView.setBounds(0, 0, sideBar().getWidth() + 0, 50);
        btnView.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
        btnView.setForeground(Color.white);
        btnView.setBackground(new Color(0, 82, 50));
        btnView.setBorderPainted(false);
        btnView.setFocusPainted(false);
        return btnView;
    }
    public JButton addButton(){
        JButton btnAdd = new JButton("Add Tasks");
        btnAdd.setBounds(0, viewButton().getY() + viewButton().getHeight(), sideBar().getWidth(), 50);
        btnAdd.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
        btnAdd.setForeground(Color.white);
        btnAdd.setBackground(new Color(0, 82, 50));
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        return btnAdd;
    }
    public JButton editButton(){
        JButton btnEdit = new JButton("Edit Task");
        btnEdit.setBounds(0, addButton().getY() + addButton().getHeight(), sideBar().getWidth(), 50);
        btnEdit.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
        btnEdit.setForeground(Color.white);
        btnEdit.setBackground(new Color(0, 82, 50));
        btnEdit.setBorderPainted(false);
        btnEdit.setFocusPainted(false);
        return btnEdit;
    }
    public JButton submitNew(){
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font(font.getName(), 1, 14));
        btnSubmit.setForeground(Color.white);
        btnSubmit.setBackground(new Color(0, 82, 50));
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        return btnSubmit;
    }
    public JButton editExisting(){
        JButton btnEdit = new JButton("Edit Task");
        btnEdit.setFont(new Font(font.getName(), 1, 14));
        btnEdit.setForeground(Color.white);
        btnEdit.setBackground(new Color(0, 82, 50));
        btnEdit.setBorderPainted(false);
        btnEdit.setFocusPainted(false);
        return btnEdit;
    }
    public JButton completeTask(){
        JButton btnComplete = new JButton("Complete Task");
        btnComplete.setFont(new Font(font.getName(), 1, 14));
        btnComplete.setForeground(Color.white);
        btnComplete.setBackground(new Color(0, 82, 50));
        btnComplete.setBorderPainted(false);
        btnComplete.setFocusPainted(false);
        return btnComplete;
    }
    public JButton deleteTask(){
        JButton btnDelete = new JButton("Delete Task");
        btnDelete.setFont(new Font(font.getName(), 1, 14));
        btnDelete.setForeground(Color.white);
        btnDelete.setBackground(new Color(0, 82, 50));
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);
        return btnDelete;
    }
    public JButton closeProgram() throws IOException {
        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font(font.getName(), font.getStyle(), 10));
        btnClose.setForeground(Color.white);
        btnClose.setBackground(new Color(194, 0, 0));
        btnClose.setBounds(headerPanel().getWidth() - 40, 0, 40, 30);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        return btnClose;
    }

    public JLabel quickLabel(String name, int X, int Y, int width, int height){
        JLabel quickLabel = new JLabel(name);
        quickLabel.setBounds(X, Y, width, height);
        quickLabel.setFont(new Font(font.getName(), Font.BOLD, 14));
        quickLabel.setForeground(new Color(9,9,28));
        return quickLabel;
    }
    public JTextField quickTextfield(String placeholder, int X, int Y, int width, int height){
        JTextField tf = new JTextField(placeholder);
        tf.setBounds(X, Y, width, height);
        tf.setFont(new Font(font.getName(), font.getStyle(), 14));
        tf.setForeground(new Color(9, 9, 28));
        return tf;
    }
}
