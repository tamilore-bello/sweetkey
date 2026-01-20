
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import org.example.*;

import javax.swing.*;

void main() {
    // get the database up and running ! we create a UserDAO object, through which we can access various
    // database methods. The first thing we do is run the database.
    DevUtils du = new DevUtils();
    UserDAO ud = new UserDAO();
    ud.rundb();

     // for prototyping and development purposes, we use the internal methods to print all users and all
     // commissions
     //printAllUsers();
     //printAllComms();
    // addUser();
     //welcomeUser();

    swingUI();
}


public void swingUI() {
    Font defaultFont = new Font("SF Pro Rounded", Font.PLAIN, 16);
    UIManager.put("Label.font", defaultFont);
    JFrame jframe = new JFrame(" ♡ ✿ sweetkey ✿ ♡");

    // overarching content pane in the jFrame
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
    jframe.setContentPane(panel);


    // below is components inside the frame !

    // panel for welcome header + log in , everything except
    // header and footer basically
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

    // addint the center container to the overarching panel
    panel.add(Box.createVerticalGlue());
    panel.add(group);
    panel.add(Box.createVerticalGlue());
    panel.add(Box.createVerticalGlue());

    // fixes to the frame itself
    jframe.setBounds(200, 100, 100, 100);
    jframe.setSize(600,800);
    jframe.setVisible(true);
}

// all the current user to log in
public void welcomeUser() {
    Scanner scanner = new Scanner(System.in);
    UserDAO ud = new UserDAO();

    System.out.println("------ LOGIN ------ ");
    System.out.println("ENTER USERNAME: ");
    String username = scanner.nextLine();
    System.out.println("ENTER PASSWORD: ");
    String password = scanner.nextLine();

    User user = ud.fetchUser(username, password);
    if (user != null) {
        System.out.println("\nWELCOME "+user.getUsername());
        System.out.println(user);
        addCommission(user);
    } else {
        System.out.println("INCORRECT CREDENTIALS");
    }
}

@InternalMethod
@Deprecated
public void printAllUsers() {
    DevUtils devUtils = new DevUtils();
    System.out.println("------ ALL USERS ------ ");
    for (User i : devUtils.fetchAllUsers_INTERNAL()) {
        System.out.println(i);
    }
}


@InternalMethod
@Deprecated
public void printAllComms() {
    DevUtils devUtils = new DevUtils();
    System.out.println("------ ALL COMMISSIONS ------ ");
    for (Commission i : devUtils.fetchAllComms_INTERNAL()) {
        System.out.println(i);
    }
}

// TODO
// if a user object is fabricated then this is no longer secure and we can add random commissions to users.
public void addCommission(User user) {
    Scanner scanner = new Scanner(System.in);
    UserDAO ud = new UserDAO();
    String ai = user.getId();
    System.out.println("ENTER THE COMMISSIONER'S HANDLE: ");
    String ch = scanner.nextLine();
    System.out.println("ENTER THE SIZE OF THE COMMISSION: ");
    String s = scanner.nextLine();
    System.out.println("ENTER THE LINK TO THE REFERENCE: ");
    String r = scanner.nextLine();
    System.out.println("HAS PAYMENT BEEN RECEIVED?: ");
    Boolean b = scanner.nextBoolean();

    // OF COURSE, we can add more fields and scan in responses.
    Commission commission = new Commission( ai,
            ch,
            s,
            r,
            b);
    ud.addComm(commission);
}

public void addUser() {
    Scanner scanner = new Scanner(System.in);
    String username;
    String password;

    System.out.println("ENTER USERNAME");
    username = scanner.nextLine();
    System.out.println("ENTER PASSWORD ");
    password = scanner.nextLine();

    User user = new User(username, password);

    UserDAO ud = new UserDAO();
    ud.rundb();
    ud.addUser(user);

}








