package model;

import com.google.gson.Gson;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;

import static java.lang.Integer.parseInt;

public class MeetingScheduler {

	private DateTimeFormatter dateFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");
	private DateTimeFormatter separatedTimeFormatter = DateTimeFormat
			.forPattern("HH:mm");

	/**
	 * 
	 * @param meetingRequest
	 * @return
	 * @throws
	 */
	public MeetingsSchedule schedule(String meetingRequest, String hours) {
		try {
			if (meetingRequest == null && hours == null || meetingRequest.isEmpty() && hours.isEmpty()) {
				System.err
						.println(" Employee has requested for booking is not a valid input");
				return null;
			}

			Gson gson = new Gson();
			Request[] requests = gson.fromJson(meetingRequest, Request[].class);
			OfficeWorkingHours workHours = gson.fromJson(hours, OfficeWorkingHours.class);

			LocalTime officeStartTime = new LocalTime(
					parseInt(workHours.getOfficeStartTime().substring(0, 2)),
					parseInt(workHours.getOfficeStartTime().substring(2, 4)));
			LocalTime officeFinishTime = new LocalTime(
					parseInt(workHours.getOfficeFinishTime().substring(0, 2)),
					parseInt(workHours.getOfficeFinishTime().substring(2, 4)));

			Map<LocalDate, Set<Meeting>> meetings = new TreeMap<LocalDate, Set<Meeting>>();

			for (Request request : requests) {
				LocalDate meetingDate = dateFormatter
						.parseLocalDate(request.getRequestStartTime().split(" ")[0]);

				Meeting meeting = extractMeeting(request, officeStartTime, officeFinishTime);
				if (meeting != null) {
					if (meetings.containsKey(meetingDate)) {
						meetings.get(meetingDate).remove(meeting);
						meetings.get(meetingDate).add(meeting);
					} else {
						Set<Meeting> meetingsForDay = new TreeSet<Meeting>();
						meetingsForDay.add(meeting);
						meetings.put(meetingDate, meetingsForDay);
					}
				}
			}

			return new MeetingsSchedule(officeStartTime, officeFinishTime,
					meetings);
		} catch (Exception e) {
			return null;
		}

	}

	private Meeting extractMeeting(Request request, LocalTime officeStartTime, LocalTime officeFinishTime) {
		String employeeId = request.getEmployeeId();

		LocalTime meetingStartTime = separatedTimeFormatter
				.parseLocalTime(request.getRequestStartTime().split(" ")[1]);
		LocalTime meetingFinishTime = new LocalTime(
				meetingStartTime.getHourOfDay(),
				meetingStartTime.getMinuteOfHour())
				.plusHours(parseInt(request.getMeetingLength()));

		if (meetingTimeOutsideOfficeHours(officeStartTime, officeFinishTime,
				meetingStartTime, meetingFinishTime)) {
			System.err.println("EmployeeId:: " + employeeId
					+ " has requested booking which is outside office hour.");
			return null;
		} else {
			return new Meeting(employeeId, meetingStartTime, meetingFinishTime);

		}
	}

	private boolean meetingTimeOutsideOfficeHours(LocalTime officeStartTime,
			LocalTime officeFinishTime, LocalTime meetingStartTime,
			LocalTime meetingFinishTime) {
		return meetingStartTime.isBefore(officeStartTime)
				|| meetingStartTime.isAfter(officeFinishTime)
				|| meetingFinishTime.isAfter(officeFinishTime)
				|| meetingFinishTime.isBefore(officeStartTime);
	}
}
