
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import org.example.*;

import javax.swing.*;
static ViewModel vm = new ViewModel();

void main() {
    // get the database up and running ! we create a UserDAO object, through which we can access various
    // database methods. The first thing we do is run the database.
    ViewModel vm = new ViewModel();
    swingUI();
}


private void swingUI() {
    Font defaultFont = new Font("SF Pro Rounded", Font.PLAIN, 16);
    UIManager.put("Label.font", defaultFont);
    JFrame jframe = new JFrame(" ♡ ✿ sweetkey ✿ ♡");

    // overarching content pane in the jFrame
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
    jframe.setContentPane(panel);


    JPanel afterpanel = getWelcomeGroup();
    afterpanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
    // below is components inside the frame !

    // panel for welcome header + log in , everything except
    // header and footer basically
    JPanel welcomeGroup = getLoginGroup(panel, jframe, afterpanel);

    // add the center container to the overarching panel
    panel.add(Box.createVerticalGlue());
    panel.add(welcomeGroup);
    panel.add(Box.createVerticalGlue());
    panel.add(Box.createVerticalGlue());

    // fixes to the frame itself
    jframe.setBounds(200, 100, 100, 100);
    jframe.setSize(600,800);
    jframe.setVisible(true);
}

private static JPanel getLoginGroup(JPanel panel, JFrame jframe, JPanel afterpanel) {
    JPanel group = new JPanel();
    group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
    group.setAlignmentX(Component.CENTER_ALIGNMENT);

    // header
    JPanel header_row = new JPanel();
    header_row.setLayout(new BoxLayout(header_row, BoxLayout.X_AXIS));
    JLabel header_text = new JLabel("Welcome!");
    header_text.setFont(new Font("SF Pro Display", Font.BOLD, 40));
    header_row.add(header_text);

    // username row
    JPanel username_row = new JPanel();
    username_row.setLayout(new BoxLayout(username_row, BoxLayout.X_AXIS));

    username_row.setAlignmentX(Component.CENTER_ALIGNMENT);
    JLabel username_text = new JLabel("Username: ");
    JTextField username_field = new JTextField(15);
    username_field.setMaximumSize(new Dimension(username_field.getPreferredSize().width, username_field.getPreferredSize().height));
    username_row.add(username_text);
    username_row.add(username_field);

    // password row
    JPanel password_row = new JPanel();
    password_row.setLayout(new BoxLayout(password_row, BoxLayout.X_AXIS));
    password_row.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel password_text = new JLabel("Password: ");
    JPasswordField password_field = new JPasswordField(15);
    password_field.setMaximumSize(new Dimension(password_field.getPreferredSize().width, password_field.getPreferredSize().height));
    password_row.add(password_text);
    password_row.add(password_field);

    // Button row
    JPanel buttonRow = new JPanel();
    buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
    buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton button = new JButton("Log In");
    button.setFont(new Font("SF Pro Rounded", Font.PLAIN, 14));
    Dimension buttonSize = new Dimension(280, button.getPreferredSize().height);
    button.setMinimumSize(buttonSize);
    button.setPreferredSize(buttonSize);
    button.setMaximumSize(buttonSize);
    buttonRow.add(Box.createHorizontalGlue());
    buttonRow.add(button);
    buttonRow.add(Box.createHorizontalGlue());

    // on click action!
    button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("click registered! "+Date.from(Instant.now()));

            panel.setVisible(false);
            jframe.setContentPane(afterpanel);
        }
    });

    // adding everything to the center container
    group.add(header_row);
    group.add(Box.createRigidArea(new Dimension(0, 20))); // 20px vertical space
    group.add(Box.createRigidArea(new Dimension(0, 15))); // space between header and username
    group.add(username_row);
    group.add(Box.createRigidArea(new Dimension(0, 15))); // space between header and username
    group.add(password_row);
    group.add(Box.createRigidArea(new Dimension(0, 15))); // space between header and username
    group.add(buttonRow);
    return group;
}
private static JPanel getWelcomeGroup() {
    JPanel afterpanel = new JPanel();
    afterpanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

    return afterpanel;
}










