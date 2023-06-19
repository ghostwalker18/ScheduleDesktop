import javax.swing.*;
import java.awt.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws Exception{
        DatabaseWorker databaseWorker = DatabaseWorker.getInstance();
        databaseWorker.updateDatabase();
        JFrame app = new JFrame("Расписание");
        app.setPreferredSize(new Dimension(800,500));
        app.setIconImage(Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/images/favicon.gif")));
        app.setContentPane(new MainForm().mainPanel);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.pack();
        app.setVisible(true);
    }
}