import java.util.Calendar;

public class Lesson {
    public Calendar date;
    public String lessonNumber;
    public String roomNumber;
    public String times;
    public String group;
    public String subject;
    public String teacher;

    public Lesson() {
        date = Calendar.getInstance();
    }

    public Lesson( Calendar date, String lessonNumber, String roomNumber, String times,
                   String group, String subject, String teacher) {
        this.date = date;
        this.lessonNumber = lessonNumber;
        this.roomNumber = roomNumber;
        this.times = times;
        this.group = group;
        this.subject = subject;
        this.teacher = teacher;
    }
}
