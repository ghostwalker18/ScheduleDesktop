import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

public class MainForm{

    private ScheduleState state;
    private DatabaseWorker databaseWorker = DatabaseWorker.getInstance();
    private JComboBox groupComboBox;
    private JButton clearButton;
    private JComboBox teacherComboBox;
    private JButton clearButton1;
    public JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel chooseGroupLabel;
    private JLabel chooseTeacherLabel;
    private JButton backwardButton;
    private JButton forwardButton;
    private WeekdayButton mondayButton;
    private WeekdayButton tuesdayButton;
    private WeekdayButton wednesdayButton;
    private WeekdayButton thursdayButton;
    private WeekdayButton fridayButton;
    private JPanel schedulePanel;
    private JScrollPane scheduleScroll;

    private void createUIComponents() throws SQLException {
        state = new ScheduleState(new Date(2023 - 1900, 6 - 1, 8));
        mondayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Понедельник");
        state.addObserver(mondayButton);
        tuesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Вторник");
        state.addObserver(tuesdayButton);
        wednesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Среда");
        state.addObserver(wednesdayButton);
        thursdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Четверг");
        state.addObserver(thursdayButton);
        fridayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Пятница");
        state.addObserver(fridayButton);
    }
    public MainForm() throws SQLException{
        scheduleScroll.getVerticalScrollBar().setUnitIncrement(6);
        scheduleScroll.getVerticalScrollBar().setBackground(Color.WHITE);
        scheduleScroll.getVerticalScrollBar().setForeground(new Color(40,158,46));

        clearButton.addActionListener(e -> {
            groupComboBox.setSelectedIndex(0);
            state.setGroup(null);
        });

        clearButton1.addActionListener(e -> {
            teacherComboBox.setSelectedIndex(0);
            state.setTeacher(null);
        });

        groupComboBox.addActionListener(e -> {
            state.setGroup(groupComboBox.getSelectedItem().toString());
        });
        Vector<String> groupNames = databaseWorker.getGroupNames();
        if(groupNames != null){
            groupComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getGroupNames()));
        }

        groupComboBox.setToolTipText("Например: \"A-11\"");

        teacherComboBox.addActionListener(e -> {
            state.setTeacher(teacherComboBox.getSelectedItem().toString());
        });
        Vector<String> teacherNames = databaseWorker.getTeacherNames();
        if(teacherNames != null){
            teacherComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getTeacherNames()));
        }
        teacherComboBox.setToolTipText("Например: \"Иванов И.И\"");

        backwardButton.addActionListener(e -> {
            state.goPreviousWeek();
        });
        backwardButton.setToolTipText("Предыдущая неделя");

        forwardButton.addActionListener(e -> {
            state.goNextWeek();
        });
        forwardButton.setToolTipText("Следующая неделя");
    }
}