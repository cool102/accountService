package account.businesslayer;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table (name="events")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column private LocalDateTime date;
    @Column private String action;
    @Column private String subject;
    @Column private String object;
    @Column private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SecurityEvent() {
    }

    public SecurityEvent(LocalDateTime date, String action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        if (subject.matches("[a-zA-Z]+@acme.com")) {
            this.subject = subject.toLowerCase();
        }
        else {
            this.subject = subject;
        }
        this.object = object;
        this.path = path;
    }
}
