import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

public class Accounts {
 
    private Connection connection;

    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email){
        if(account_exists(email) == false){
            String open_account_query = "INSERT INTO accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.println("Enter full name: ");
            String full_name = scanner.nextLine();
            System.out.println("Enter initial ammount: ");
            Double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter security pin: ");
            String security_pin = scanner.nextLine();
            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected > 0){
                    return account_number;
                }
                else{
                    throw new RuntimeException("Account Creation failed!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Creation failed!");
    }

    public boolean account_exists(String email){
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() == true){
                return true;
            }
            return false;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private long generateAccountNumber(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1");
            if(resultSet.next() == true){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1;
            }
            else{
                return 10000100;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public long getAccount_number(String email){
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() == true){
                return resultSet.getLong("account_number");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account number does not exist!");
    }

}
