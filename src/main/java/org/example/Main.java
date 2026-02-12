package org.example;

import org.example.dev.DevUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;


public class Main {
    // overarching viewModel, which holds logic. linked to the model.
    private static final ViewModel vm = new ViewModel();

    public static void main(String[] args) {
        // enforce that the database actually exists.
        // in actual production, the database will be stored
        // on a server other than the client's computer.
        new DevUtils().rundb();

        // UI can be run asynchronously and load when all the elements have loaded (its own thread)
        SwingUtilities.invokeLater(Main::swingUI);
    }

    // overarching UI JFRAME
    private static void swingUI() {
        setUpGlobalFont();

        JFrame frame = new JFrame(" ♡ ✿ sweetkey ✿ ♡");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 1000);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(loginPanel(frame));
        frame.setVisible(true);
    }

    // LOG IN AND SIGN UP ------------------------------------------------------------------------------------
    // LOG IN
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

    // SIGN-UP
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
        tabs.addTab("Upcoming Work", upcomingWorkTab(tabs, UserDAO.OrderCode.DEFAULT));
        tabs.addTab("Add a Commission", addACommissionTab(tabs));
        tabs.addTab("Stats", statsTab(tabs));
        tabs.addTab("Settings", settingsTab(tabs));

        root.add(header);
        root.add(vspace(15));
        root.add(tabs);

        return root;
    }

    // TABS ------------------------------------------------------------------------------------
    // display user commissions tab
    private static JPanel upcomingWorkTab(JTabbedPane tabs, UserDAO.OrderCode order) {
        System.out.println(order);
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        // get all current commissions as a List
        List<Commission> currentComms = vm.getCurrentUserCommission(order);

        // Selection for display order of the listModel
        JComboBox<String> displayBy = getDisplayOrderJComboBox(tabs, order);

        // propagate the ListModel for the JList with the arraylist of current User commissions
        DefaultListModel<Commission> model = new DefaultListModel<>();
        for (Commission c : currentComms) { model.addElement(c); }
        JList<Commission> list = getCommissionJList(model);


        root.add(displayBy);
        // set the list in a JScrollPane so we can scroll
        JScrollPane scrollPane = new JScrollPane(list);
        root.add(scrollPane);

        JDialog d = new JDialog();

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    d.dispose();
                    d.setContentPane(getCommissionDetailPanel(list.getSelectedValue()));
                    d.setLocation(800, 200);
                    d.setSize(500, 500);
                    d.setVisible(true);
                }
            }
        });

        return root;
    }

    // Add a commission tab
    private static JPanel addACommissionTab(JTabbedPane tabs) {
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
        JComboBox<String> paymentRecF = new JComboBox<>(new String[]{"No", "Yes"});
        JPanel dateOrdered = labeledRow("Date Ordered:", dateOrderedF);
        JPanel dateExpected = labeledRow("Date Expected:", dateExpectedF);
        JPanel paymentRec = labeledRow("Payment Received:", paymentRecF);


        JTextField platformF = new JTextField();
        JTextField costF = new JTextField("0");
        JTextField refF = new JTextField();
        JPanel platform = labeledRow("Platform:", platformF);
        JPanel cost = labeledRow("Cost:", costF);
        JPanel ref = labeledRow("Reference Link:", refF);

        JComboBox<String> statusF = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed"});
        JPanel status = labeledRow("Current Status:", statusF);

        JButton addComm = primaryButton("Add Commission", () -> {
            if (vm.validCost(costF.getText()) && vm.addCommission(new Commission(
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
                    statusF.getSelectedItem().toString())))
            {
                handleF.setText("");
                platformF.setText("");
                dateOrderedF.setDate(Date.from(Instant.now()));
                dateExpectedF.setDate(Date.from(Instant.now()));
                sizeOptionRowF.setSelectedIndex(0);
                costF.setText("");
                descriptionF.setText("");
                refF.setText("");
                paymentRecF.setSelectedIndex(0);
                statusF.setSelectedIndex(0);

                refreshTabAndSwitch(tabs, upcomingWorkTab(tabs, UserDAO.OrderCode.DEFAULT), "Upcoming Work");
            } else {
                JOptionPane.showMessageDialog(tabs, "Please complete all fields.");
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

    // user statistics tab
    private static JPanel statsTab(JTabbedPane tabs) {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel headerText = header("Current Statistics");
        headerText.setFont(headerText.getFont().deriveFont(Font.BOLD, 20));
        JPanel header = centered(headerText);
        header.setMaximumSize(new Dimension(header.getMaximumSize().width, header.getPreferredSize().height));

        // Current late commission qty:
        JLabel currentLateQty = new JLabel("❥·∙     Current Overdue Commissions: "+vm.lateCommissionQty());
        // Earnings ever
        JLabel earnedEver = new JLabel("❥·∙     Earnings of all time: "+NumberFormat.getCurrencyInstance().format(vm.earningsEver()));
        // Earnings this year
        JLabel earnedYear = new JLabel("❥·∙     Earnings this year: "+NumberFormat.getCurrencyInstance().format(vm.earningsYear()));
        // Earnings this month
        JLabel earnedMonth = new JLabel("❥·∙     Earnings this month: "+NumberFormat.getCurrencyInstance().format(vm.earningsMonth()));
        // Earnings the last 7 days
        JLabel earned7 = new JLabel("❥·∙     Earnings the last seven days: "+NumberFormat.getCurrencyInstance().format(vm.earningsLast7()));

        JLabel estWitholding = new JLabel("❥·∙     Recommended witholding: "+NumberFormat.getCurrencyInstance().format(vm.earningsYear()*0.3));

        // Date Joined
        JLabel dateJoined = new JLabel("Joined "+ DateFormat.getDateInstance().format(vm.getDateJoined()));

        root.add(vspace(20));
        root.add((header));
        root.add(vspace(20));
        root.add(new JSeparator());
        root.add(vspace(20));
        root.add(centered(currentLateQty));
        root.add(vspace(5));
        root.add(new JSeparator());
        root.add(vspace(20));
        root.add(centered(earned7));
        root.add(centered(earnedMonth));
        root.add(centered(earnedYear));
        root.add(centered(earnedEver));
        root.add(vspace(5));
        root.add(new JSeparator());
        root.add(vspace(30));
        root.add(centered(estWitholding));
        root.add(vspace(10));
        root.add(new JSeparator());
        root.add(centered(dateJoined));

        return root;
    }

    // user settings tab
    private static JPanel settingsTab(JTabbedPane tabs) {
        JPanel root = columnPanel();
        root.setBorder(BorderFactory.createEmptyBorder(60, 80, 0, 80));

        JTextField changeUsernameF = new JTextField();
        changeUsernameF.setMaximumSize(new Dimension(changeUsernameF.getMaximumSize().width, changeUsernameF.getPreferredSize().height));
        JPanel usernameRowP = labeledRow("Former Username: "+vm.getCurrentUser().getUsername(), (JComponent) Box.createRigidArea(new Dimension(300, 0)));
        JPanel usernameRow = labeledRow("Change Username: ", changeUsernameF);
        JPanel usernameButton = centered(primaryButton("Change Username", new Runnable() {
            @Override
            public void run() {
                // TODO implement
                    if (vm.updateUsername(changeUsernameF.getText())) {
                        JOptionPane.showMessageDialog(tabs, "Username change successful! Please restart to view changes.");
                    } else {
                        JOptionPane.showMessageDialog(tabs, "Username must not be blank.\nUsername cannot be " +
                                "used by another user.\nCannot be your current Username.");
                    }
            }
        }));

        Dimension d = new Dimension(300, usernameButton.getPreferredSize().height);
        usernameButton.setPreferredSize(d);
        usernameButton.setMaximumSize(d);

        JTextField changeEmailF = new JTextField();
        changeEmailF.setMaximumSize(new Dimension(changeUsernameF.getMaximumSize().width, changeUsernameF.getPreferredSize().height));
        JPanel emailRowP = labeledRow("Former Email: "+vm.getCurrentUser().getEmail(), (JComponent) Box.createRigidArea(new Dimension(300, 0)));
        JPanel emailRow = labeledRow("Change Email: ", changeEmailF);
        JPanel emailButton = centered(primaryButton("Change Email", new Runnable() {
            @Override
            public void run() {
                if (vm.updateEmail(changeEmailF.getText())) {
                    JOptionPane.showMessageDialog(tabs, "Email change successful! Please restart to view changes.");
                } else {
                    JOptionPane.showMessageDialog(tabs, "Email must not be blank.\nEmail must contain a valid " +
                            "email address.\nCannot be your current email.");
                }            }
        }));
        emailButton.setPreferredSize(d);
        emailButton.setMaximumSize(d);

        JButton deleteAccountButton = primaryButton("Delete Account", new Runnable() {
                    @Override
                    public void run() {
                        JFrame frame = (JFrame) tabs.getTopLevelAncestor();
                        int n = JOptionPane.showConfirmDialog(frame, "This action cannot be undone.\nContinue anyways?");
                        System.out.println(n);
                        if (n == 0) { // if option for YES is selected...
                            vm.deleteUser();
                            frame.dispose();
                            SwingUtilities.invokeLater(Main::swingUI);
                        }
                    }
                });
        deleteAccountButton.setOpaque(true);
        deleteAccountButton.setForeground(Color.red);
        JPanel deleteAccountP = centered(deleteAccountButton);

        root.add(usernameRowP);
        root.add(usernameRow);
        root.add(usernameButton);
        root.add(vspace(10));
        root.add(new JSeparator());
        root.add(vspace(10));
        root.add(emailRowP);
        root.add(emailRow);
        root.add(emailButton);
        root.add(vspace(300));
        root.add(deleteAccountP);
        return root;
    }

    // SPECIAL ITEMS
    private static JComboBox<String> getDisplayOrderJComboBox(JTabbedPane tabs, UserDAO.OrderCode order) {
        JComboBox<String> displayBy = new JComboBox<>(new String[]{
                "Order By: Soonest Due",
                "Order By: Oldest",
                "Order By: Size",
                "Order By: Currently Overdue",
                "Order By: Default"
        });
        // TODO add order where LATE, where UNPAID, by PROGRESS

        displayBy.setSelectedIndex(vm.getIntFromOrder(order));
        displayBy.addActionListener(_ -> {
            // refresh the tab with the correctly ordered list fetched from the DB
            // in reality, we could sort results locally using less resources. However, for the sake
            // of demonstrating proficiency with SQL queries, we do a new operation instead of using a cache
            refreshTabAndSwitch(tabs, upcomingWorkTab(tabs, vm.getOrder(displayBy.getSelectedIndex())), "Upcoming Work");
        });

        Dimension d = new Dimension(displayBy.getMaximumSize().width, displayBy.getPreferredSize().height);
        displayBy.setToolTipText("Order by: ");
        displayBy.setBorder(null);
        displayBy.setMaximumSize(d);
        return displayBy;
    }

    private static JList<Commission> getCommissionJList(DefaultListModel<Commission> model) {
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
                {
                    panel.setBackground(jList.getSelectionBackground());
                }
            } else {
                panel.setBackground(jList.getBackground());
            }



            panel.setOpaque(true);
            return panel;
        });
        return list;
    }

    private static JPanel getCommissionDetailPanel(Commission c) {
        JPanel panel = columnPanel();
        JLabel header = header("Commission for "+c.getCommissioner_handle());
        JLabel line1 = new JLabel("<html><b>"+c.getSize()+"</b> worth <b>"+NumberFormat.getCurrencyInstance(Locale.US).format(c.getCost())+"</b></html>");
        JLabel line2 = new JLabel("<html>Ordered via <b>"+c.getPlatform()+"</b> on <b>"+c.getDate_ordered().toString()+"</b>, expected <b>"+c.getDate_expected()+"</b></html>");
        JLabel line4;
        if (c.getPaymentReceived()) line4 = new JLabel("<html>Payment <b>received</b></html>"); else line4 = new JLabel("<html>Payment <b>not received</b></html>");
        JLabel line5 = new JLabel("Description: "+c.getDescription());
        JLabel line6 = new JLabel("Reference Link: "+c.getReference_link());
        JLabel line7 = new JLabel("<html>Status: <b>"+c.getStatus()+"</b></html>");

        panel.add(header);
        panel.add(new JSeparator());
        panel.add(vspace(20));
        panel.add(centered(line1));
        panel.add(centered(line2));
        panel.add(centered(line4));
        panel.add(centered(line5));
        panel.add(centered(line6));
        panel.add(centered(line7));
        panel.add(vspace(40));

        return panel;
    }

    // HELPING METHODS ------------------------------------------------------------------------------------
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
        Dimension d = new Dimension(300, b.getPreferredSize().height);
        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.addActionListener(_ -> action.run());
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

    private static void refreshTabAndSwitch(JTabbedPane tabs, JPanel panel, String title) {
        tabs.remove(0);
        tabs.insertTab(title, null, panel, null, 0);
        tabs.setSelectedIndex(0);
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
