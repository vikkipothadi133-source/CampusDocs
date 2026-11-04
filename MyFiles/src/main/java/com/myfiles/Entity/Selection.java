package com.myfiles.Entity;



public enum Selection {
    Attendance("ATD"),
    Students("STD"),
    Staf("STF"),
    TimeTable("TT"),
    Syllabus("SYB");

    private final String prefix;

    Selection(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
