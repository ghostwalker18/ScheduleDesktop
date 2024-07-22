package com.ghostwalker18.scheduledesktop;

import org.sqlite.JDBC;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DatabaseWorker {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final String SCHEME = "jdbc:sqlite:";
    private static final String DATABASE_URL = SCHEME + DatabaseWorker.class.getResource("/database/testDB");
    private static DatabaseWorker instance = null;

    public static synchronized DatabaseWorker getInstance() throws SQLException{
        if(instance == null){
            instance = new DatabaseWorker();
        }
        return instance;
    }

    private Connection connection;
    private  DatabaseWorker() throws SQLException{
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(DATABASE_URL);
    }

    public void updateDatabase(){
        try(Statement statement = connection.createStatement()){
            statement.executeQuery("CREATE TABLE tblSchedule(updateTime TEXT, lessonDate TEXT, lessonNumber INT, lessonTime TEXT, subjectName TEXT, teacherName TEXT, roomNumber INT, groupName TEXT, PRIMARY KEY(lessonDate, lessonTime, subjectName, groupName)) IF NOT EXIST");
        }catch (SQLException e){

        }

    }

    public Date lastTimeUpdated(){
        return null;
    }
    public Vector<String[]> getDaySchedule(Calendar date, String group, String teacher){
        String query = "SELECT lessonNumber, lessonTime, subjectName, teacherName, roomNumber FROM tblSchedule WHERE lessonDate = '" + dateFormat.format(date.getTime()) +"'";
        if(group != null){
            query += (" AND groupName = '" + group + "'");
        }
        if(teacher != null){
            query += (" AND teacherName = '" + teacher +"'");
        }
        query += " ORDER BY lessonNumber ASC";
        try(Statement statement = connection.createStatement()){
            ResultSet queryResult = statement.executeQuery(query);
            Vector<String[]> result = new Vector<>();
            while(queryResult.next()){
                result.add(new String[]{
                        String.valueOf(queryResult.getInt("lessonNumber")),
                        queryResult.getString("lessonTime"),
                        queryResult.getString("subjectName"),
                        queryResult.getString("teacherName"),
                        String.valueOf(queryResult.getInt("roomNumber"))
                });
            };
            return result;
        } catch (SQLException e){
            return null;
        }
    }

    public Vector<String> getGroupNames(){
        try (Statement statement = connection.createStatement()){
            ResultSet queryResult = statement.executeQuery("SELECT DISTINCT groupName FROM tblSchedule ORDER BY groupName ASC");
            Vector<String> result = new Vector<>();
            while(queryResult.next()){
                result.add(queryResult.getString("groupName"));
            }
            return result;
        } catch (SQLException e){
            return null;
        }
    }

    public Vector<String> getTeacherNames(){
        try (Statement statement = connection.createStatement()){
            ResultSet queryResult = statement.executeQuery("SELECT DISTINCT teacherName FROM tblSchedule ORDER BY teacherName ASC");
            Vector<String> result = new Vector<>();
            while(queryResult.next()){
                result.add(queryResult.getString("teacherName"));
            }
            return result;
        } catch (SQLException e){
            return null;
        }
    }
}