package model;

import org.joda.time.LocalTime;

/**
 * Created by Dima on 22.03.2017.
 */
public class OfficeWorkingHours {
    private String officeStartTime;

    private String officeFinishTime;

    public String getOfficeFinishTime() {
        return officeFinishTime;
    }

    public void setOfficeFinishTime(String officeFinishTime) {
        this.officeFinishTime = officeFinishTime;
    }

    public String getOfficeStartTime() {
        return officeStartTime;
    }

    public void setOfficeStartTime(String officeStartTime) {
        this.officeStartTime = officeStartTime;
    }
}
