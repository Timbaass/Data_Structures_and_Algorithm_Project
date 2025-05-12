package Project.Models;

import Project.Enums.Priority;

public class Call {
    private String callerName;
    private Priority priority;
    private int durationInSeconds;

    public Call(String callerName, Priority priority, int durationInSeconds) {
        this.callerName = callerName;
        this.priority = priority;
        this.durationInSeconds = durationInSeconds;
    }

    public String getCallerName() {
        return callerName;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    @Override
    public String toString() {
        return callerName + " - " + priority + " - " + durationInSeconds + "s";
    }
}
