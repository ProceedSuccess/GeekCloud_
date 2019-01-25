package com.GeekCloud.server;

import com.GeekCloud.common.PermissionMessage;
import java.sql.*;
// Класс, работающий с БД
public class DataBaseRequestor {
    // Class.forName("org.sqlite.JDBC").getDeclaredConstructor().newInstance();
    // - выполняется при запуске сервера
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    //для SELECT
    static String queryLogin = "SELECT login FROM Users1 where login";
    static String queryPassword = "SELECT password FROM Users1 WHERE password";
    // Для CREATE
    static String createLoginPassword = "INSERT INTO Users1 (id_,login,password,nickname) VALUES";

    static boolean loginIsValid = false;
    static boolean passwordIsValid = false;
    static boolean valid = false;
    // метод работает, в случае, если огин/пароль совпадают
    public static PermissionMessage query(String login, String password){
        try{
            con = DriverManager.getConnection( "jdbc:sqlite:data.db");
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryLogin + "=" +"'" + login + "'");
            // проверка логина
            if (rs.getString(1)
                    .equals((login))){
                loginIsValid = true;
            }
            rs = stmt.executeQuery(queryPassword + "=" +"'" + password+ "'");
            // проверка пароля
            if (rs.getString(1)
                    .equals((password))){
                passwordIsValid = true;
            }
            if (loginIsValid && passwordIsValid){
                System.out.println("login,password are valid");
                valid = true;
            }
            if (!loginIsValid && passwordIsValid){
                System.out.println("no such user");
                valid = false;
            }
        }
        catch (SQLException sqlEx) {
            System.out.println("try again");
            sqlEx.printStackTrace();
        }
//        finally {
//            try { con.close(); } catch(SQLException se) { se.printStackTrace(); }
//            try { stmt.close(); } catch(SQLException se) { se.printStackTrace(); }
//            try { rs.close(); } catch(SQLException se) { se.printStackTrace(); }
//        }
        return new PermissionMessage(valid);
    }
    public static PermissionMessage createAccount(String login, String password, String nickName){
        try{
            con = DriverManager.getConnection( "jdbc:sqlite:data.db");
            stmt = con.createStatement();
            rs = stmt.executeQuery(createLoginPassword + " (" + 4 +  "," + "'" +  login + "'" + ","
                    + "'"+  password + "'" + ","+ "'"+  nickName + "'"+ ");");
            for (int i = 0; i < 4; i++) {
                System.out.println(rs.getString(i));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return new PermissionMessage(true);
    }
}
