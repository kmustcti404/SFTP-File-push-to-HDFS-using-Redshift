package com.sftp.client;

import com.jcraft.jsch.*;

import java.sql.*;
import java.util.*;

public class RedshiftWrite {

    static String redshiftUrl;
    static String masterUsername;
    static String password;


    static Connection connection = null;
    static Statement statement = null;

    public static void write(Long filestamp){


        try {
            Class.forName("com.amazon.redshift.jdbc41.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", masterUsername);
            properties.setProperty("password", password);
            connection = DriverManager.getConnection(redshiftUrl, properties);
            // Further code to follow

            statement = connection.createStatement();
            // Create a query to use.
            String creatTable = "create table if not exists sftp (filestamp BIGINT)";

            String maxValue = "insert into sftp values("+filestamp+")";

            statement.execute(creatTable);

            statement.execute(maxValue);


        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    public static Long read(){

        Long filestamp = null;
        try {
            Class.forName("com.amazon.redshift.jdbc41.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", masterUsername);
            properties.setProperty("password", password);
            connection = DriverManager.getConnection(redshiftUrl, properties);
            // Further code to follow

            statement = connection.createStatement();
            // Create a query to use.
            String creatTable = "select nvl(max(filestamp),0) as filestamp from sftp";


            ResultSet res = statement.executeQuery(creatTable);

            System.out.println("fetching last maximum file timestamp");

            while(res.next()){
                filestamp=res.getLong("filestamp");
            }

        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return filestamp;
    }

}
