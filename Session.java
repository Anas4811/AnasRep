
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.time.LocalDate;
class InterfaceUserAdmin extends JFrame implements ActionListener{
    private Connection con;
    JLabel txt = new JLabel("Etes Vous un Client Ou Bien Un Administrateur?");
    JButton bUser = new JButton("Client");
    JButton bAdmin = new JButton("Admin");

    public InterfaceUserAdmin() {
        super("Interface User/Admin");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 1, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(txt);
        p.add(bUser);
        p.add(bAdmin);

        bUser.addActionListener(this);
        bAdmin.addActionListener(this);

        setContentPane(p);
    }

    public static void main(String[] args) {
        InterfaceUserAdmin f = new InterfaceUserAdmin();
        f.setVisible(true);
        f.updateDatabase();
    }

    void updateDatabase() {
        PreparedStatement p = null;
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
            String req = "UPDATE location SET statut = 'NON' WHERE statut = 'Oui' AND end_date < NOW()";
            String req2="UPDATE cars set statut ='A louer'where id IN(Select car_id from location WHERE statut = 'Oui AND end_date < NOW()); ";
            p = con.prepareStatement(req);
            PreparedStatement p2=con.prepareStatement(req2);
            int rowsUpdated = p.executeUpdate();
            p2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            
        } finally {
            try {
                if (p != null) p.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bUser) {
            this.dispose();
            UserLogin userLogin = new UserLogin();
            userLogin.setVisible(true);
        } else if (e.getSource() == bAdmin) {
            this.dispose();
            AdminLogin adminLogin = new AdminLogin();
            adminLogin.setVisible(true);
        }
    }
}

class AdminLogin extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Login");
    JLabel lbl1 = new JLabel("Veuillez entrez votre login");
    JTextField loginField = new JTextField();
    JLabel lbl2 = new JLabel("Veuillez entrez votre mot de passe  ");
    JPasswordField pwdField = new JPasswordField();
    JButton confirmButton = new JButton("Confirm");

    public AdminLogin() {
        super("Admin Login");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 1, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(txt);
        p.add(new JLabel(" "));
        p.add(lbl1);
        p.add(loginField);
        p.add(lbl2);
        p.add(pwdField);
        p.add(confirmButton);

        confirmButton.addActionListener(this);
        setContentPane(p);
    }
    public  int attempts = 0;
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmButton) {
             String username = loginField.getText();
             char[] password = pwdField.getPassword();
             String passwordString = new String(password);

            try {
                String query = "SELECT * FROM Users WHERE login = ? AND pwd = ? AND role='Admin';";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setString(2, passwordString);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    this.dispose();
                    CarUser carUser = new CarUser();
                    carUser.setVisible(true);
                } else {
                    attempts++;
                    if (attempts >= 3) {
                        JOptionPane.showMessageDialog(this, "3 attempts exceeded. Login blocked.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid login. Attempts remaining: " + (3 - attempts), "Login Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
                rs.close();
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }
}

    


}
class CarUser extends JFrame implements ActionListener {
    JLabel txt = new JLabel("Choisir");
    JButton bUser = new JButton("Voir les Clients");
    JButton bcar = new JButton("Ajouter une voiture");
    JButton bcars = new JButton("Voir la liste des Voitures");

    public CarUser() {
        super("JFrame");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4 ,2, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(txt);
        p.add(bUser);
        p.add(bcar);
        p.add(bcars);
        bUser.addActionListener(this);
        bcar.addActionListener(this);
        bcars.addActionListener(this);

        setContentPane(p);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bUser) {
            this.dispose();
            UserList userlist = new UserList();
            userlist.setVisible(true);
        } else if (e.getSource() == bcar) {
            this.dispose();
            Caradd caradd = new Caradd();
            caradd.setVisible(true);
        }
        else if(e.getSource()==bcars){
            this.dispose();
            CarListAdmin list= new CarListAdmin();
            list.setVisible(true);
        }
    }
}
class CarListAdmin extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login_schelma";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";

    public CarListAdmin() {
        super("Car List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(userPanel);
        add(scrollPane, BorderLayout.CENTER);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int x=0;
            String query = "SELECT id,brand,model,age,price_per_day,statut from cars ; ";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    x+=1;
                    int CarId = resultSet.getInt("id");
                    String brand = resultSet.getString("brand");
                    String model = resultSet.getString("model");
                    int age = resultSet.getInt("age");
                    int price_per_day=resultSet.getInt("price_per_day");
                    String statut = resultSet.getString("statut");
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JButton DeleteButton = new JButton("Delete");
                    JLabel txt = new JLabel("car:"+x+"brand: "+brand+" model  "+model +" age  " + age+"price a day  "+price_per_day+"Statut: "+statut);
                    DeleteButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    CarListAdmin.this,
                                    "Are you sure you want to Delete this car ?",
                                    "Confirm Deletion",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                updatecar(CarId);
                                SessionCar.setCarid(CarId);
                                userPanel.remove(rowPanel);
                                userPanel.revalidate();
                                userPanel.repaint();
                                dispose();
                                CarUser dur = new CarUser();
                                dur.setVisible(true);
                            }
                        }
                    });
                    rowPanel.add(txt);
                    rowPanel.add(DeleteButton);

                    userPanel.add(rowPanel);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updatecar(int carId) {
        String deleteLocationQuery = "DELETE FROM location WHERE car_id = ?";
        String deleteCarQuery = "DELETE FROM cars WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);
    
            try (
                PreparedStatement deleteLocationStmt = connection.prepareStatement(deleteLocationQuery);
                PreparedStatement deleteCarStmt = connection.prepareStatement(deleteCarQuery)
            ) {
                deleteLocationStmt.setInt(1, carId);
                deleteLocationStmt.executeUpdate();
                deleteCarStmt.setInt(1, carId);
                deleteCarStmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                JOptionPane.showMessageDialog(this, "Failed to delete car: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, " failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    }
class Caradd extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Ajouter une nouvelle voiture");
    JTextField brand = new JTextField();
    JTextField model= new JTextField();
    JTextField age = new JTextField();
    JTextField prix = new JTextField();
    JButton confirmButton = new JButton("Confirm");
    

    public Caradd() {
        super("Ajout des voitures");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(new JLabel("Ajouter la marque"));
        p.add(brand);
        p.add(new JLabel(" "));
        p.add(new JLabel(" "));
        p.add(new JLabel("Ajouter le model"));
        p.add(model);
        p.add(new JLabel(" "));
        p.add(new JLabel(" "));
        p.add(new JLabel("ajouter l'age de la voiture"));
        p.add(age);
        p.add(new JLabel(" "));
        p.add(new JLabel(" "));
        p.add(new JLabel("ajouter le prix de la voiture"));
        p.add(prix);
        p.add(confirmButton);

        confirmButton.addActionListener(this);
        setContentPane(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmButton) {
            String brand_ = brand.getText();
            String model_ = model.getText();
            String age_= age.getText();
            String prix_ = prix.getText();

            try {
                String query = "INSERT INTO cars(brand,model,age,price_per_day,statut ) Values(?,?,?,?,?)";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, brand_);
                pstmt.setString(2, model_);
                pstmt.setString(3,age_);
                pstmt.setString(4,prix_);
                pstmt.setString(5,"A louer");
                int rs = pstmt.executeUpdate();

                if (rs>=1) {
                    JOptionPane.showMessageDialog(this, "car Added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    CarUser f = new CarUser();
                    f.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid car ", "Error", JOptionPane.ERROR_MESSAGE);
                }
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during adding cars !", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
}


class UserList extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login_schelma";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";

    public UserList() {
        super("User List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        JButton ExitButton= new JButton("Exit");
        userPanel.add(ExitButton);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(userPanel);
        add(scrollPane, BorderLayout.CENTER);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT user_id,login from Users where role ='User' ";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String userName = resultSet.getString("login");

                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JLabel nameLabel = new JLabel(userName);
                    JButton deleteButton = new JButton("Delete");
                    deleteButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(e.getSource()==deleteButton){
                            int confirm = JOptionPane.showConfirmDialog(
                                    UserList.this,
                                    "Are you sure you want to delete " + userName + "?",
                                    "Confirm Deletion",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                deleteUser(userId);
                                userPanel.remove(rowPanel);
                                userPanel.revalidate();
                                userPanel.repaint();
                                
                            }
                        }
                            else if(e.getSource()==ExitButton){
                                dispose();
                                CarUser f= new CarUser();
                                f.setVisible(true);
                            }
                        }
                    });

                    rowPanel.add(nameLabel);
                    rowPanel.add(deleteButton);
                    userPanel.add(rowPanel);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser(int userId) {
        String deleteLocationQuery = "DELETE FROM location WHERE user_id = ?";
        String deleteCarQuery = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);
    
            try (
                PreparedStatement deleteLocationStmt = connection.prepareStatement(deleteLocationQuery);
                PreparedStatement deleteCarStmt = connection.prepareStatement(deleteCarQuery)
            ) {
                deleteLocationStmt.setInt(1, userId);
                deleteLocationStmt.executeUpdate();
                deleteCarStmt.setInt(1, userId);
                deleteCarStmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, " failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
class UserLogin extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Login");
    JLabel lbl1 = new JLabel("Veuillez entrez votre login");
    JTextField loginField = new JTextField();
    JLabel lbl2 = new JLabel("Veuillez entrez votre mot de passe  ");
    JPasswordField pwdField = new JPasswordField();
    JButton signin = new JButton("Sign in");
    JButton confirmButton = new JButton("Confirm");

    public UserLogin() {
        super("User Login");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5, 2, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(txt);
        p.add(new JLabel(" "));
        p.add(lbl1);
        p.add(loginField);
        p.add(lbl2);
        p.add(pwdField);
        p.add(confirmButton);
        p.add(new JLabel("Avez pas de compte?"));
        p.add(signin);
        confirmButton.addActionListener(this);
        signin.addActionListener(this);
        setContentPane(p);
    }
    public int attempts = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmButton) {
            String username = loginField.getText();
            char[] password = pwdField.getPassword();
            String passwordString = new String(password);
    
            try {
                String query = "SELECT * FROM Users WHERE login = ? AND pwd = ? AND role='User';";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setString(2, passwordString);
    
                ResultSet rs = pstmt.executeQuery();
    
                if (rs.next()) {
                    Session.setCredentials(username, passwordString);
                    this.dispose();
                    Userchoose f = new Userchoose();
                    f.setVisible(true);
                } else {
                    attempts++;
                    if (attempts >= 3) {
                        JOptionPane.showMessageDialog(this, "3 attempts exceeded. Login blocked.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                        confirmButton.setEnabled(false); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid login. Attempts remaining: " + (3 - attempts), "Login Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
                pstmt.close();
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    else{
        if (e.getSource() == signin) {
            this.dispose();
            Sign sign_ = new Sign();
            sign_.setVisible(true);
         }
        }
    }
}
// un class session pour recupperer les informations de login  avec methodes get set
public class Session {
    private static String username;
    private static String password;
    private static String email;
    private static String city;

    private Session() {}

    public static void setCredentials(String user, String pass) {
        username = user;
        password = pass;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getEmail() {
        return email;
    }

    public static String getCity() {
        return city;
    }

    public static void updateUsername(String newUsername) {
        username = newUsername;
    }

    public static void updatePassword(String newPassword) {
        password = newPassword;
    }

    public static void updateEmail(String newEmail) {
        email = newEmail;
    }

    public static void updateCity(String newCity) {
        city = newCity;
    }
}

class Sign extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Create your login and password");
    JTextField loginField = new JTextField();
    JPasswordField pwdField = new JPasswordField();
    JTextField villeField = new JTextField();
    JTextField emailField = new JTextField();
    JButton confirmButton = new JButton("Confirm");

    public Sign() {
        super("Sign In");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(new JLabel("Login:"));
        p.add(loginField);
        p.add(new JLabel("Password:"));
        p.add(pwdField);
        p.add(new JLabel("City:"));
        p.add(villeField);
        p.add(new JLabel("Email:"));
        p.add(emailField);
        p.add(confirmButton);

        confirmButton.addActionListener(this);
        setContentPane(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmButton) {
            String username = loginField.getText().trim();
            char[] password = pwdField.getPassword();
            String passwordString = new String(password);
            String ville = villeField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.length == 0 || ville.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO USERS(login, pwd, Ville, email, role) VALUES(?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, passwordString);
                    pstmt.setString(3, ville);
                    pstmt.setString(4, email);
                    pstmt.setString(5, "User");
                    int result = pstmt.executeUpdate();

                    if (result >= 1) {
                        JOptionPane.showMessageDialog(this, "Sign-in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        Session.setCredentials(username, passwordString);
                        this.dispose();
                        Userchoose f= new Userchoose();
                        f.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Sign-in failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

class Userchoose extends JFrame implements  ActionListener {
    JLabel txt = new JLabel();
    JButton extend= new JButton("Extendre la location");
    JButton retourner = new JButton("Retourner une Voiture");
    JButton carlistButton = new JButton("Voir la liste des voitures");
    JButton ModifyInfo= new JButton("Modifier les parametres de login etc");
    public Userchoose(){
        super("JFrame");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        
        p.setLayout(new GridLayout(3, 1, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(txt);
        p.add(extend);
        p.add(retourner);
        p.add(carlistButton);
        p.add(ModifyInfo);
        extend.addActionListener(this);
        retourner.addActionListener(this);
        carlistButton.addActionListener(this);
        ModifyInfo.addActionListener(this);
        setContentPane(p);

    }
     @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==extend){
            this.dispose();
            ExtendreLocation ext = new ExtendreLocation();
            ext.setVisible(true);
        }
        if(e.getSource()==retourner){
            this.dispose();
            ReturnCar  ret=  new ReturnCar();
            ret.setVisible(true);
        }
        if(e.getSource()==carlistButton){
            this.dispose();
            CarList cars = new CarList();
            cars.setVisible(true);
        }
        if(e.getSource()==ModifyInfo){
            this.dispose();
            UserProfile profile = new UserProfile();
            profile.setVisible(true);
        }
    }
}
class UserProfile extends JFrame implements ActionListener {
    private Connection con;
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField emailField = new JTextField();
    private JTextField cityField = new JTextField();
    private JButton updateButton = new JButton("Update Details");
    private JButton backButton = new JButton("Back");

    public UserProfile() {
        super("User Profile");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);

        panel.add(updateButton);
        panel.add(backButton);

        updateButton.addActionListener(this);
        backButton.addActionListener(this);

        setContentPane(panel);

        loadUserDetails();
    }

    private void loadUserDetails() {
        String username = Session.getUsername();
        String password = Session.getPassword();

        try {
            String query = "SELECT login, pwd, email, ville FROM Users WHERE login = ? AND pwd = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("login"));
                passwordField.setText(rs.getString("pwd"));
                emailField.setText(rs.getString("email"));
                cityField.setText(rs.getString("ville"));
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton) {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword());
            String newEmail = emailField.getText().trim();
            String newCity = cityField.getText().trim();

            if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty() || newCity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String query = "UPDATE Users SET login = ?, pwd = ?, email = ?, ville = ? WHERE login = ? AND pwd = ?;";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, newUsername);
                pstmt.setString(2, newPassword);
                pstmt.setString(3, newEmail);
                pstmt.setString(4, newCity);
                pstmt.setString(5, Session.getUsername());
                pstmt.setString(6, Session.getPassword());
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    Session.setCredentials(newUsername, newPassword);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == backButton) {
            this.dispose();
            Userchoose userChoose = new Userchoose();
            userChoose.setVisible(true);
        }
    }
}

class ExtendreLocation extends JFrame implements ActionListener{
    private Connection con;
    JLabel txt = new JLabel();
    JTextField duree = new JTextField();
    JButton confirm = new JButton("Confirmer");
    public ExtendreLocation(){
        super("JFrame");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1, 10, 10));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(new JLabel("entrer le nombre des jours pour toute les voitures"));
        p.add(txt);
        p.add(duree);
        p.add(confirm);
        confirm.addActionListener(this);  
        setContentPane(p);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirm) {
            try {
                String username = Session.getUsername();
                String password = Session.getPassword();
                String duration = duree.getText();
                int durationDays;
                try {
                    durationDays = Integer.parseInt(duration);
                    if (durationDays <= 0) {
                        JOptionPane.showMessageDialog(this, "Veuillez entrer une durée valide!", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Durée invalide. Veuillez entrer un nombre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String req = "SELECT statut FROM location WHERE user_id = (SELECT user_id FROM users WHERE login = ? AND pwd = ?);";
                try (PreparedStatement pstmt = con.prepareStatement(req)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String statut = rs.getString("statut");
    
                            if (statut != null && (statut.equalsIgnoreCase("oui"))) {
                                String req2 = "UPDATE location SET end_date = end_date + INTERVAL ? DAY WHERE user_id = (SELECT user_id FROM users WHERE login = ? AND pwd = ?);";
                                try (PreparedStatement pstmt2 = con.prepareStatement(req2)) {
                                    pstmt2.setInt(1, durationDays);
                                    pstmt2.setString(2, username);
                                    pstmt2.setString(3, password);
    
                                    int rowsAffected = pstmt2.executeUpdate();
                                    if (rowsAffected >= 1) {
                                        JOptionPane.showMessageDialog(this, "Temps ajouté avec succès!, le prix de l'ajout est "+calcPrice(durationDays), "Succès", JOptionPane.INFORMATION_MESSAGE);
                                        this.dispose();
                                        Userchoose f= new Userchoose();
                                        f.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Opération invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Statut non valide ou non disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur de la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private int calcPrice(int n){
        int prix_total =0;
        try{
            
            
            int car_id = SessionCar.getid();
            String req = "SELECT price_per_day FROM cars WHERE id = ? AND statut = 'louée';";
            PreparedStatement p =con.prepareStatement(req);
            p.setInt(1,car_id);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                int x=rs.getInt("price_per_day");
                prix_total = x*n;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Erreur Base donnees lors de calcul des prix");
            
        }
        return prix_total;


    }
}    
class SessionCar {
    private static int  Carid;
    private SessionCar() {}

    public static void setCarid(int id) {
         Carid=id;
    }

    public static int getid() {
        return Carid;
    }

}
class CarList extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login_schelma";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";

    public CarList() {
        super("User List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(userPanel);
        add(scrollPane, BorderLayout.CENTER);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int x=0;
            String query = "SELECT id,brand,model,age,price_per_day from cars where statut='A louer'; ";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    x+=1;
                    int CarId = resultSet.getInt("id");
                    String brand = resultSet.getString("brand");
                    String model = resultSet.getString("model");
                    int age = resultSet.getInt("age");
                    int price_per_day=resultSet.getInt("price_per_day");
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JButton BorrowButton = new JButton("Rent");
                    JLabel txt = new JLabel("car:"+x+"brand: "+brand+" model  "+model +" age  " + age+"price a day  "+price_per_day);
                    BorrowButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    CarList.this,
                                    "Are you sure you want to Rent this car ?",
                                    "Confirm Deletion",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                updatecar(CarId);
                                SessionCar.setCarid(CarId);
                                userPanel.remove(rowPanel);
                                userPanel.revalidate();
                                userPanel.repaint();
                                dispose();
                                DurationRent dur = new DurationRent();
                                dur.setVisible(true);
                            }
                        }
                    });
                    rowPanel.add(txt);
                    rowPanel.add(BorrowButton);

                    userPanel.add(rowPanel);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updatecar(int CarId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String UpdateQuery = "Update cars Set statut='louée' where id =? ;";
            String req ="Update location Set statut='Non' where car_id=? ;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(UpdateQuery)) {
                preparedStatement.setInt(1, CarId);
                preparedStatement.executeUpdate();
                

        }
        try(PreparedStatement p = connection.prepareStatement(req)){
                p.setInt(1,CarId);
                p.executeUpdate();
        }
        catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
class DurationRent extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Choisir la durée de location:");
    JTextField duree = new JTextField(10);
    JButton confirm = new JButton("Confirmer");

    public DurationRent() {
        super("Car Rental Duration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new BorderLayout());
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.add(txt);
        panel.add(duree);
        panel.add(confirm);

        confirm.addActionListener(this);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirm) {
            try {
                String username = Session.getUsername();
                String password = Session.getPassword();
                
                String req1 = "SELECT user_id FROM Users WHERE login = ? AND pwd = ?;";
                try (PreparedStatement pstmt = con.prepareStatement(req1)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int user_id = rs.getInt("user_id");
                            int carId = SessionCar.getid();
                            LocalDate start_date = LocalDate.now();
                            String n = duree.getText();
                            int x;
                            
                            try {
                                x = Integer.parseInt(n);
                                if (x <= 0) {
                                    JOptionPane.showMessageDialog(this, "Veuillez entrer une durée valide!", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this, "Entrée invalide. Veuillez entrer un nombre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            LocalDate end_date = start_date.plusDays(x);
                            String statut = "Oui";
                            String req = "INSERT INTO location(car_id, user_id, start_date, end_date, statut) VALUES (?, ?, ?, ?, ?);";
                            try (PreparedStatement p = con.prepareStatement(req)) {
                                p.setInt(1, carId);
                                p.setInt(2, user_id);
                                p.setDate(3, Date.valueOf(start_date));
                                p.setDate(4, Date.valueOf(end_date));
                                p.setString(5, statut);

                                p.executeUpdate();
                                JOptionPane.showMessageDialog(this, "Location de voiture réussie!, Votre prix total est:"+ calcPrice(x));
                                this.dispose();
                                Userchoose f = new Userchoose();
                                f.setVisible(true);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'insertion des données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    
    }
    private int calcPrice(int n){
        int prix_total =0;
        try{
            
            
            int car_id = SessionCar.getid();
            String req = "SELECT price_per_day FROM cars WHERE id = ? AND statut = 'louée';";
            PreparedStatement p =con.prepareStatement(req);
            p.setInt(1,car_id);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                int x=rs.getInt("price_per_day");
                prix_total = x*n;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Erreur Base donnees lors de calcul des prix");
            
        }
        return prix_total;


    }
}
class ReturnCar extends JFrame implements ActionListener {
    private Connection con;
    JLabel txt = new JLabel("Veuillez retourner la voiture louée");
    JButton b = new JButton("confirmer");
    JButton exit = new JButton("Exit");

    public ReturnCar() {
        super("ReturnCar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schelma",
                "root",
                "12345"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel p = new JPanel();
        p.add(txt);
        p.add(b);
        b.addActionListener(this);
        p.add(exit);
        exit.addActionListener(this);
        setContentPane(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==b){
        try {
            LocalDate now = LocalDate.now();
            String username = Session.getUsername();
            String password = Session.getPassword();

            String req1 = "Select user_id from Users where login=? and pwd=?;";
            PreparedStatement pstmt = con.prepareStatement(req1);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int user_id = rs.getInt("user_id"); 
                String req = "Select end_date from location where user_id=?;";
                PreparedStatement p = con.prepareStatement(req);
                p.setInt(1, user_id);
                ResultSet rs2 = p.executeQuery();

                if (rs2.next()) {
                    Date endDateFromDB = rs2.getDate("end_date"); 
                    LocalDate endDate = endDateFromDB.toLocalDate();

                    if (endDate.isBefore(now)) {
                        JOptionPane.showMessageDialog(this, "Votre temps de location a expiré. Si vous n'avez pas retourné la voiture, vous serez chargé.");
                    } else if (endDate.isAfter(now)) {
                        JOptionPane.showMessageDialog(this, "Vous avez encore du temps pour retourner la voiture.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Retourner la voiture aujourd'hui.");
                    }
                    rs2.close();
                    p.close();
                } else {
                    JOptionPane.showMessageDialog(this, "No rental record found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    else if(e.getSource()==exit){
        this.dispose();
        Userchoose f = new Userchoose();
        f.setVisible(true);
    }
    }
}