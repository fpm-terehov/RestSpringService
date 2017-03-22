package model;

import com.google.gson.Gson;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MeetingScheduleOutputGenerator {

    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

    private MeetingScheduler meetingScheduler;

    public MeetingScheduleOutputGenerator(MeetingScheduler meetingScheduler) {
        this.meetingScheduler = meetingScheduler;
    }

    public String print(String meetingRequest, String hours){
        MeetingsSchedule meetingsScheduleBooked = meetingScheduler.schedule(meetingRequest, hours);

        Gson gs = new Gson();

        return gs.toJson(buildMeetingScheduleString(meetingsScheduleBooked));
    }

    private ArrayList<String> buildMeetingScheduleString(MeetingsSchedule meetingsScheduleBooked) {
        ArrayList<String> strarr = new ArrayList<>();
        String str;
        for (Map.Entry<LocalDate, Set<Meeting>> meetingEntry : meetingsScheduleBooked.getMeetings().entrySet()) {
            LocalDate meetingDate = meetingEntry.getKey();
            str = dateFormatter.print(meetingDate);
            strarr.add(str);
            Set<Meeting> meetings = meetingEntry.getValue();
            for (Meeting meeting : meetings) {
                str = timeFormatter.print(meeting.getStartTime()) + " " +
                        timeFormatter.print(meeting.getFinishTime()) + " " + meeting.getEmployeeId();
                strarr.add(str);
            }

        }
        return strarr;
    }
}
