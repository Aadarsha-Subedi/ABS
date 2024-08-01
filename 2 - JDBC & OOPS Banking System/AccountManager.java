import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    private Connection connection;

    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void credit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.println("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter security pin: ");
        String security_pin = scanner.nextLine();
        try{
            connection.setAutoCommit(false);
            if(account_number != 0){
                String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next() == true){
                    String debit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement2 = connection.prepareStatement(debit_query);
                    preparedStatement2.setDouble(1, amount);
                    preparedStatement2.setLong(2, account_number);
                    int rowsAffected = preparedStatement2.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println(amount + " credit to account!");
                        connection.commit();
                        connection.setAutoCommit(true);
                    }
                    else{
                        System.out.println("Transaction failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }
                else{
                    System.out.println("Invalid pin!");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.println("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter security pin: ");
        String security_pin = scanner.nextLine();
        try{
            connection.setAutoCommit(false);
            if(account_number != 0){
                String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next() == true){
                    double current_balance = resultSet.getDouble("balance");
                    if(current_balance >= amount){
                        String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement2 = connection.prepareStatement(debit_query);
                        preparedStatement2.setDouble(1, amount);
                        preparedStatement2.setLong(2, account_number);
                        int rowsAffected = preparedStatement2.executeUpdate();
                        if(rowsAffected > 0){
                            System.out.println(amount + " debited from account!");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        else{
                            System.out.println("Transaction failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }
                    else{
                        System.out.println("Insufficient balance!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }
                else{
                    System.out.println("Invalid pin!");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void getBalance(long account_number){
        scanner.nextLine();
        System.out.println("Enter security pin: ");
        String security_pin = scanner.nextLine();
        try{
            String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() == true){
                Double balance = resultSet.getDouble("balance");
                System.out.println("Balance: " + balance);
            }
            else{
                System.out.println("Invalid pin!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void transfer_money(long sender_account_number) throws SQLException{
        scanner.nextLine();
        System.out.println("Enter receiver account number: ");
        long receiver_account_number =  scanner.nextLong();
        System.out.println("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter Security Pin: ");
        String secuirty_pin = scanner.nextLine();
        try{
            connection.setAutoCommit(false);
            if(sender_account_number != 0 && receiver_account_number != 0){
                String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, secuirty_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next() == true){
                    double current_balance = resultSet.getDouble("balance");
                    if(amount <= current_balance){
                        String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if(rowsAffected1 > 0 && rowsAffected2 > 0){
                            System.out.println("Transaction successful!");
                            System.out.println(amount + "transfered succesfully!");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        else{
                            System.out.println("Transaction failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }
                    else{
                        System.out.println("Insufficient balance!");
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

}
