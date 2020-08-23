import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class loginFrame implements ActionListener {
    private static JFrame loginFrame;
    private static JPanel loginPanel;
    private static JLabel userLabel;
    public static JTextField userInput;
    private static JLabel passwordLabel;
    public static JPasswordField passwordInput;
    private static JButton loginButton;
    public static JLabel statusMessage;

    public loginFrame() {
        loginFrame = new JFrame("Tracker");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(426, 240);
        loginPanel = new JPanel();
        loginFrame.add(loginPanel);

        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(250, 250, 250));

        statusMessage = new JLabel("");
        statusMessage.setBounds(85, 20, 255, 25);
        loginPanel.add(statusMessage);

        userLabel = new JLabel("Username");
        userLabel.setBounds(85, 52, 80, 25);
        loginPanel.add(userLabel);
        userInput = new JTextField();
        userInput.setBounds(175, 52, 165, 25);
        userInput.addActionListener(this);
        loginPanel.add(userInput);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(85, 82, 80, 25);
        loginPanel.add(passwordLabel);
        passwordInput = new JPasswordField();
        passwordInput.setBounds(175, 82, 165, 25);
        loginPanel.add(passwordInput);
        passwordInput.addActionListener(this);

        loginButton = new JButton("Login");
        loginButton.setBounds(173, 112, 80, 25);
        loginPanel.add(loginButton);
        loginButton.addActionListener(this);

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new loginFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userInput.getText();
        String password = new String(passwordInput.getPassword());

        try {
            checkList.connectSQL(username, password);
            loginFrame.setVisible(false);
            loginFrame.dispose();
            new dashboardFrame();
        } catch (SQLException | IOException sqlException) {
            sqlException.printStackTrace();
            statusMessage.setText("Login failed. Please try again.");
            statusMessage.setForeground(Color.red);
        }
    }
}
