/**
 * Copyright (c) July 12, 2016. All rights reserved.
 */

import java.sql.*;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 */
class SQLHandler {
    private static Connection connection = null;

    static void setConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void stopConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static String getNicknameByLoginAndPassword(String login, String password) {
        String string = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT nickname FROM main WHERE login = ? AND password = ?;"
            );
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                string = resultSet.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return string;
    }
}
