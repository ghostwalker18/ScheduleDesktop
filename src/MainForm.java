import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

public class MainForm{

    private ScheduleState state;
    private Theme theme = new DefaulTheme();
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
        UIManager.put("ToolTip.background", theme.getBackgroundColor());
        UIManager.put("ToolTip.foreground", theme.getAccentColor());
        scheduleScroll.getVerticalScrollBar().setUnitIncrement(6);
        scheduleScroll.getVerticalScrollBar().setBackground(theme.getBackgroundColor());
        scheduleScroll.getVerticalScrollBar().setForeground(theme.getPrimaryColor());

        clearButton.addActionListener(e -> {
            groupComboBox.setSelectedIndex(0);
            state.setGroup(null);
        });

        clearButton1.addActionListener(e -> {
            teacherComboBox.setSelectedIndex(0);
            state.setTeacher(null);
        });

        groupComboBox.addActionListener(e -> {
            if(groupComboBox.getSelectedIndex() !=0){
                state.setGroup(groupComboBox.getSelectedItem().toString());
            }
            else{
                state.setGroup(null);
            }

        });
        Vector<String> groupNames = databaseWorker.getGroupNames();
        if(groupNames != null){
            groupComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getGroupNames()));
        };
        groupComboBox.insertItemAt("Не выбрано",0);
        groupComboBox.setSelectedIndex(0);
        groupComboBox.setToolTipText("Например: \"A-11\"");

        teacherComboBox.addActionListener(e -> {
            if(teacherComboBox.getSelectedIndex() != 0){
                state.setTeacher(teacherComboBox.getSelectedItem().toString());
            }
            else{
                state.setTeacher(null);
            }
        });
        Vector<String> teacherNames = databaseWorker.getTeacherNames();
        if(teacherNames != null){
            teacherComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getTeacherNames()));
        };
        teacherComboBox.insertItemAt("Не выбрано", 0);
        teacherComboBox.setSelectedIndex(0);
        teacherComboBox.setToolTipText("Например: \"Иванов И.И\"");

        backwardButton.addActionListener(e -> {
            state.goPreviousWeek();
        });
        backwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    state.goPreviousWeek();
            }
        });
        backwardButton.setToolTipText("Предыдущая неделя");

        forwardButton.addActionListener(e -> {
            state.goNextWeek();
        });
        forwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    state.goNextWeek();
            }
        });
        forwardButton.setToolTipText("Следующая неделя");
    }
}