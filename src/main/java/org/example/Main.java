package org.example;

import org.example.dev.CLIController;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    private static final ViewModel vm = new ViewModel();

    public static void main(String[] args) {
        // UI can be run asynchronously and load when all the elements have loaded (its own thread)
        SwingUtilities.invokeLater(Main::swingUI);
    }

    private static void swingUI() {
        UIManager.put("Label.font", new Font("SF Pro Rounded", Font.PLAIN, 16));

        JFrame frame = new JFrame(" ♡ ✿ sweetkey ✿ ♡");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);

        frame.setContentPane(loginPanel(frame));
        frame.setVisible(true);
    }

    // LOGIN FRAME
    private static JPanel loginPanel(JFrame frame) {
        JPanel root = columnPanel();

        JLabel header = header("Welcome!");

        JTextField username = sized(new JTextField(15));
        JPasswordField password = sized(new JPasswordField(15));

        JButton login = primaryButton("Log In", () -> handleLogin(frame, username, password));

        JLabel signup = linkLabel("First time? Create an account here.", () ->
                swap(frame, signupPanel(frame))
        );

        root.add(header);
        root.add(vspace(20));
        root.add(labeledRow("Username: ", username));
        root.add(vspace(15));
        root.add(labeledRow("Password: ", password));
        root.add(vspace(30));
        root.add(centered(login));
        root.add(vspace(10));
        root.add(signup);

        return root;
    }

    private static void handleLogin(JFrame frame, JTextField username, JPasswordField password) {
        char[] pwd = password.getPassword();
        try {
            // if the username and password are valid, switch the frames.
            if (vm.welcomeUser(username.getText(), pwd)) {
                swap(frame, welcomePanel(vm.getCurrentUser()));
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect credentials.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // make sure no password data lingers....
            Arrays.fill(pwd, '0');
        }
    }

    // SIGN UP
    private static JPanel signupPanel(JFrame frame) {
        JPanel root = columnPanel();

        JLabel header = header("Sign Up!");

        JTextField username = sized(new JTextField(15));
        JPasswordField password = sized(new JPasswordField(15));
        JTextField email = sized(new JTextField(17));

        // on button press, validate and execute the new User submission
        JButton submit = primaryButton("Continue", () -> handleSignup(frame, username, email, password));

        root.add(header);
        root.add(vspace(20));
        root.add(labeledRow("Username: ", username));
        root.add(vspace(15));
        root.add(labeledRow("Password: ", password));
        root.add(vspace(15));
        root.add(labeledRow("Email: ", email));
        root.add(vspace(30));
        root.add(centered(submit));

        return root;
    }

    // validate the new user submission
    private static void handleSignup(JFrame frame, JTextField user, JTextField email, JPasswordField pass) {
        char[] pwd = pass.getPassword();
        try {
            if (vm.addUser(user.getText(), email.getText(), pwd)) {
                swap(frame, loginPanel(frame));
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Invalid input.\nPassword must be 8+ characters with at least one special character.\nEmail must be valid.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Arrays.fill(pwd, '0');
        }
    }

    // WELCOME PAGE
    private static JPanel welcomePanel(User user) {
        JPanel root = columnPanel();

        JLabel header = new JLabel("Welcome, " + user.getUsername() + "!");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setFont(new Font("SF Pro", Font.BOLD, 20));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Upcoming Work", upWork());
        tabs.addTab("Add a Commission", addaC());
        tabs.addTab("Stats", new JPanel());
        tabs.addTab("Settings", new JPanel());

        root.add(header);
        root.add(vspace(15));
        root.add(tabs);


        return root;
    }

    private static JPanel upWork() {
        JPanel upWork = new JPanel();
        ArrayList<Commission> currentComms = vm.getCurrentUserCommission();
        for (Commission c : currentComms) {
            System.out.println(c);
        }
        ListModel model = new ListModel() {
            @Override
            public int getSize() {
                return currentComms.size();
            }

            @Override
            public Object getElementAt(int index) {
                return currentComms.get(index).getId();
            }

            @Override
            public void addListDataListener(ListDataListener l) {

            }

            @Override
            public void removeListDataListener(ListDataListener l) {

            }
        };
        JList list = new JList(model);
        upWork.add(list);
        return upWork;
    }

    private static JPanel addaC() {
        JPanel upWork = columnPanel();
        upWork.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JPanel handle = labeledRow("Commissioner Handle:", new JTextField());
        JPanel sizeOptionsRow = labeledRow("Size:", new JComboBox(new String[]{"Icon", "Bust", "Half-Body", "3/4", "Full-body", "Chibi", "Reference"}));



        JPanel description = labeledRow("Description:", new JTextArea());

        JPanel dateOrdered = labeledRow("Date Ordered:", new DateTextField());
        JPanel dateExpected = labeledRow("Date Expected:", new DateTextField());
        JPanel paymentRec = labeledRow("Completed:", new JComboBox(new String[]{"Yes", "No"}));


        JPanel platform = labeledRow("Platform:", new JTextField());
        JPanel cost = labeledRow("Cost:", new JTextField());
        JPanel ref = labeledRow("Reference Link:", new JTextField());

        JPanel status = labeledRow("Current Status:", new JComboBox(new String[]{"Not Started", "In Progress", "Completed"}));

        JButton addComm = primaryButton("Add Commission", new Runnable() {
            @Override
            public void run() {

            }
        });

        upWork.add(handle);
        upWork.add(sizeOptionsRow);
        upWork.add(vspace(15));
        upWork.add(new JSeparator());
        upWork.add(vspace(15));
        upWork.add(description);
        upWork.add(vspace(15));
        upWork.add(new JSeparator());
        upWork.add(vspace(15));        upWork.add(dateOrdered);
        upWork.add(dateExpected);
        upWork.add(paymentRec);
        upWork.add(vspace(15));
        upWork.add(new JSeparator());
        upWork.add(vspace(15));        upWork.add(platform);
        upWork.add(cost);
        upWork.add(ref);
        upWork.add(vspace(15));
        upWork.add(new JSeparator());
        upWork.add(vspace(15));
        upWork.add(status);
        upWork.add(vspace(15));
        upWork.add(centered(addComm));
        return upWork;
    }


    // HELPING METHODS

    // aligns a panel, gives some top padding.
    private static JPanel columnPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setBorder(BorderFactory.createEmptyBorder(120, 60, 60, 60));
        return p;
    }

    // create a labeled row
    private static JPanel labeledRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }


    // create a header
    private static JLabel header(String text) {
        JLabel h = new JLabel(text);
        h.setFont(new Font("SF Pro Display", Font.BOLD, 40));
        h.setAlignmentX(Component.CENTER_ALIGNMENT);
        return h;
    }

    // create a button
    private static JButton primaryButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("SF Pro Rounded", Font.PLAIN, 14));
        Dimension d = new Dimension(280, b.getPreferredSize().height);
        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.addActionListener(e -> action.run());
        return b;
    }

    // create a link with an action
    private static JLabel linkLabel(String text, Runnable action) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SF Pro Rounded", Font.ITALIC, 16));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return l;
    }

    // create a one component centered jpanel
    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel();
        p.add(c);
        return p;
    }

    // add vertical space
    private static Component vspace(int px) {
        return Box.createRigidArea(new Dimension(0, px));
    }

    // set the size (of a field) to be the maximum size
    private static <T extends JComponent> T sized(T c) {
        c.setMaximumSize(c.getPreferredSize());
        return c;
    }

    // switch between jPanels
    private static void swap(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
