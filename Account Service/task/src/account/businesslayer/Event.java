package account.businesslayer;

import javax.persistence.*;
import java.math.BigInteger;

//@Entity
//@Table(name = "logging_event")
//public class Event {
//
//    @Column
//    private BigInteger timestmp;
//
//    @Column
//    private String formatted_message;
//
//    @Column
//    private String logger_name;
//
//    @Column
//    private String level_string;
//
//    @Column
//    private String thread_name;
//    @Column
//    private int reference_flag;
//    @Column
//    private String caller_filename;
//    @Column
//    private String caller_class;
//    @Column
//    private String caller_method;
//    @Column
//    private char caller_line;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int event_id;
//    @Column
//    String arg0;
//    @Column
//    String arg1;
//    @Column
//    String arg2;
//    @Column
//    String arg3;
//
//    public Event() {
//    }
//
//    public BigInteger getTimestmp() {
//        return timestmp;
//    }
//
//    public void setTimestmp(BigInteger timestmp) {
//        this.timestmp = timestmp;
//    }
//
//    public String getFormatted_message() {
//        return formatted_message;
//    }
//
//    public void setFormatted_message(String formatted_message) {
//        this.formatted_message = formatted_message;
//    }
//
//    public String getLogger_name() {
//        return logger_name;
//    }
//
//    public void setLogger_name(String logger_name) {
//        this.logger_name = logger_name;
//    }
//
//    public String getLevel_string() {
//        return level_string;
//    }
//
//    public void setLevel_string(String level_string) {
//        this.level_string = level_string;
//    }
//
//    public String getThread_name() {
//        return thread_name;
//    }
//
//    public void setThread_name(String thread_name) {
//        this.thread_name = thread_name;
//    }
//
//    public int getReference_flag() {
//        return reference_flag;
//    }
//
//    public void setReference_flag(int reference_flag) {
//        this.reference_flag = reference_flag;
//    }
//
//    public String getCaller_filename() {
//        return caller_filename;
//    }
//
//    public void setCaller_filename(String caller_filename) {
//        this.caller_filename = caller_filename;
//    }
//
//    public String getCaller_class() {
//        return caller_class;
//    }
//
//    public void setCaller_class(String caller_class) {
//        this.caller_class = caller_class;
//    }
//
//    public String getCaller_method() {
//        return caller_method;
//    }
//
//    public void setCaller_method(String caller_method) {
//        this.caller_method = caller_method;
//    }
//
//    public char getCaller_line() {
//        return caller_line;
//    }
//
//    public void setCaller_line(char caller_line) {
//        this.caller_line = caller_line;
//    }
//
//    public int getEvent_id() {
//        return event_id;
//    }
//
//    public void setEvent_id(int event_id) {
//        this.event_id = event_id;
//    }
//}
