package org.example;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;


public class Main {
    // overarching viewModel, which holds logic. linked to the model.
    private static final ViewModel vm = new ViewModel();

    public static void main(String[] args) {
        // UI can be run asynchronously and load when all the elements have loaded (its own thread)
        SwingUtilities.invokeLater(Main::swingUI);
    }

    private static void swingUI() {
        setUpGlobalFont();

        JFrame frame = new JFrame(" ♡ ✿ sweetkey ✿ ♡");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 1000);
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

    // actions for actual log in
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

    // validate and handle the new user submission
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

    // WELCOME PAGE for a logged-in user
    private static JPanel welcomePanel(User user) {
        JPanel root = columnPanel();
        JLabel header = header("Welcome, "+user.getUsername()+"!");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    // display user commissions tab
    private static JPanel upWork() {
        JPanel root = new JPanel(new BorderLayout());

        // get all current commissions as an ArrayList
        ArrayList<Commission> currentComms = vm.getCurrentUserCommission();

        // propagate the ListModel for the JList with the arraylist of current User commissions
        DefaultListModel<Commission> model = new DefaultListModel<>();
        for (Commission c : currentComms) {
            model.addElement(c);
        }

        JList<Commission> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(-1);
        list.setCellRenderer((jList, c, index, isSelected, hasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JLabel title = new JLabel(c.getSize());
            title.setFont(title.getFont().deriveFont(Font.BOLD));

            JLabel subtitle = new JLabel(
                    "for " + c.getCommissioner_handle() + " via " + c.getPlatform()
            );
            subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
            subtitle.setForeground(Color.GRAY);

            panel.add(title, BorderLayout.NORTH);
            panel.add(subtitle, BorderLayout.SOUTH);

            if (isSelected) {
                panel.setBackground(jList.getSelectionBackground());
            } else {
                panel.setBackground(jList.getBackground());
            }

            panel.setOpaque(true);
            return panel;
        });

        // set the list in a JScrollPane so we can scroll
        JScrollPane scrollPane = new JScrollPane(list);
        root.add(scrollPane, BorderLayout.CENTER);

        return root;
    }

    // Add a commission tab
    private static JPanel addaC() {
        JPanel root = columnPanel();
        root.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JTextField handleF = new JTextField();
        JComboBox<String> sizeOptionRowF = new JComboBox<>(new String[]{"Icon", "Bust", "Half-Body", "3/4", "Full-body", "Chibi", "Reference"});
        JPanel handle = labeledRow("Commissioner Handle:", handleF);
        JPanel sizeOptionsRow = labeledRow("Size:", sizeOptionRowF);

        JTextArea descriptionF = new JTextArea();
        JPanel description = labeledRow("Description:", descriptionF);

        DateTextField dateOrderedF = new DateTextField();
        DateTextField dateExpectedF = new DateTextField();
        JComboBox<String> paymentRecF = new JComboBox<>(new String[]{"Yes", "No"});
        JPanel dateOrdered = labeledRow("Date Ordered:", dateOrderedF);
        JPanel dateExpected = labeledRow("Date Expected:", dateExpectedF);
        JPanel paymentRec = labeledRow("Completed:", paymentRecF);


        JTextField platformF = new JTextField();
        JTextField costF = new JTextField();
        JTextField refF = new JTextField();
        JPanel platform = labeledRow("Platform:", platformF);
        JPanel cost = labeledRow("Cost:", costF);
        JPanel ref = labeledRow("Reference Link:", refF);

        JComboBox<String> statusF = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed"});
        JPanel status = labeledRow("Current Status:", statusF);

        JButton addComm = primaryButton("Add Commission", new Runnable() {
            @Override
            public void run() {
                System.out.println("Running");
                vm.addCommission(new Commission(
                        vm.getCurrentUser().getId(),
                        handleF.getText(),
                        platformF.getText(),
                        dateOrderedF.getDate(),
                        dateExpectedF.getDate(),
                        sizeOptionRowF.getSelectedItem().toString(),
                        Double.parseDouble(costF.getText()),
                        descriptionF.getText(),
                        refF.getText(),
                       (paymentRecF.getSelectedItem() == "Yes"),
                        statusF.getSelectedItem().toString()
                ));
            }
        });

        root.add(handle);
        root.add(sizeOptionsRow);
        root.add(vspace(15));
        root.add(new JSeparator());
        root.add(vspace(15));
        root.add(description);
        root.add(vspace(15));
        root.add(new JSeparator());
        root.add(vspace(15));
        root.add(dateOrdered);
        root.add(dateExpected);
        root.add(paymentRec);
        root.add(vspace(15));
        root.add(new JSeparator());
        root.add(vspace(15));
        root.add(platform);
        root.add(cost);
        root.add(ref);
        root.add(vspace(15));
        root.add(new JSeparator());
        root.add(vspace(15));
        root.add(status);
        root.add(vspace(15));
        root.add(centered(addComm));
        return root;
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
        JLabel l = new JLabel(label);
        row.add(l);
        row.add(field);
        return row;
    }

    // create a header
    private static JLabel header(String text) {
        JLabel h = new JLabel(text);
        h.setFont(h.getFont().deriveFont(Font.BOLD, 30));
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

    // set the global font to every J-Object
    private static void setUpGlobalFont() {
        Font base = new Font("SF Pro Rounded", Font.PLAIN, 14);

        for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(base));
            }
        }
    }
}
