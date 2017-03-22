package model;

import com.google.gson.Gson;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MeetingScheduleSystemTest {

	private String request;
	private String hours;

	private MeetingScheduler meetingScheduler;
	private MeetingScheduleOutputGenerator outputGenerator;

	@org.junit.Before
	public void setUp() throws Exception {
		meetingScheduler = new MeetingScheduler();
		outputGenerator = new MeetingScheduleOutputGenerator(new MeetingScheduler());

	}

	@Test
	public void shouldParseOfficeHours() {
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";
		request = "[]";

		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);
		assertEquals(bookings.getOfficeStartTime().getHourOfDay(), 9);
		assertEquals(bookings.getOfficeStartTime().getMinuteOfHour(), 0);
		assertEquals(bookings.getOfficeFinishTime().getHourOfDay(), 17);
		assertEquals(bookings.getOfficeFinishTime().getMinuteOfHour(), 30);
	}

	@Test
	public void shouldParseMeetingRequest() {
		request = "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";
		MeetingsSchedule bookings = meetingScheduler.schedule(request,hours);

		LocalDate meetingDate = new LocalDate(2011, 3, 21);

		assertEquals(1, bookings.getMeetings().get(meetingDate).size());
		Meeting meeting = bookings.getMeetings().get(meetingDate)
				.toArray(new Meeting[0])[0];
		assertEquals("EMP002", meeting.getEmployeeId());
		assertEquals(9, meeting.getStartTime().getHourOfDay());
		assertEquals(0, meeting.getStartTime().getMinuteOfHour());
		assertEquals(11, meeting.getFinishTime().getHourOfDay());
		assertEquals(0, meeting.getFinishTime().getMinuteOfHour());
	}

	@Test
	public void noPartOfMeetingMayFallOutsideOfficeHours() {
		request = "[{'requestTime':'2011-03-15 17:29:12','employeeId':'EMP005',"
				+ "'requestStartTime':'2011-03-21 16:00','meetingLength':'3'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";
		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);

		assertEquals(0, bookings.getMeetings().size());

	}

	@Test
	public void shouldProcessMeetingsInChronologicalOrderOfSubmission() {
		request = "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";

		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);

		LocalDate meetingDate = new LocalDate(2011, 3, 21);

		assertEquals(1, bookings.getMeetings().get(meetingDate).size());
		Meeting meeting = bookings.getMeetings().get(meetingDate)
				.toArray(new Meeting[0])[0];
		assertEquals("EMP002", meeting.getEmployeeId());
		assertEquals(9, meeting.getStartTime().getHourOfDay());
		assertEquals(0, meeting.getStartTime().getMinuteOfHour());
		assertEquals(11, meeting.getFinishTime().getHourOfDay());
		assertEquals(0, meeting.getFinishTime().getMinuteOfHour());
	}

	@Test
	public void shouldGroupMeetingsChronologically() {
		request = "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP004'," +
				"'requestStartTime':'2011-03-22 16:00','meetingLength':'1'}," +
				"{'requestTime':'2011-03-16 09:28:23','employeeId':'EMP003'," +
				"'requestStartTime':'2011-03-22 14:00','meetingLength':'2'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";

		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);
		LocalDate meetingDate = new LocalDate(2011, 3, 22);

		assertEquals(1, bookings.getMeetings().size());
		assertEquals(2, bookings.getMeetings().get(meetingDate).size());
		Meeting[] meetings = bookings.getMeetings().get(meetingDate)
				.toArray(new Meeting[0]);

		assertEquals("EMP003", meetings[0].getEmployeeId());
		assertEquals(14, meetings[0].getStartTime().getHourOfDay());
		assertEquals(0, meetings[0].getStartTime().getMinuteOfHour());
		assertEquals(16, meetings[0].getFinishTime().getHourOfDay());
		assertEquals(0, meetings[0].getFinishTime().getMinuteOfHour());

		assertEquals("EMP004", meetings[1].getEmployeeId());
		assertEquals(16, meetings[1].getStartTime().getHourOfDay());
		assertEquals(0, meetings[1].getStartTime().getMinuteOfHour());
		assertEquals(17, meetings[1].getFinishTime().getHourOfDay());
		assertEquals(0, meetings[1].getFinishTime().getMinuteOfHour());
	}

	@Test
	public void meetingsShouldNotOverlap() {
		request = "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";

		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);
		LocalDate meetingDate = new LocalDate(2011, 3, 21);

		assertEquals(1, bookings.getMeetings().size());
		assertEquals(1, bookings.getMeetings().get(meetingDate).size());
		Meeting[] meetings = bookings.getMeetings().get(meetingDate)
				.toArray(new Meeting[0]);
		assertEquals("EMP002", meetings[0].getEmployeeId());
		assertEquals(9, meetings[0].getStartTime().getHourOfDay());
		assertEquals(0, meetings[0].getStartTime().getMinuteOfHour());
		assertEquals(11, meetings[0].getFinishTime().getHourOfDay());
		assertEquals(0, meetings[0].getFinishTime().getMinuteOfHour());
	}

	@Test
	public void emptyInputDataShouldEndWithNull() {
		request = null;
		hours = null;
		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);
		assertEquals(null, bookings);
	}

	@Test
	public void invalidInputDataShouldEndWithNull() {
		request = "{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'}";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";
		MeetingsSchedule bookings = meetingScheduler.schedule(request, hours);
		
		assertEquals(null, bookings);
	}

	
	@Test
	public void shouldPrintMeetingSchedule() {
		String meetingRequest = "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
				"'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-16 09:28:23','employeeId':'EMP003'," +
				"'requestStartTime':'2011-03-22 14:00','meetingLength':'2'}," +
				"{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP004'," +
				"'requestStartTime':'2011-03-22 16:00','meetingLength':'1'}," +
				"{'requestTime':'2011-03-15 17:29:12','employeeId':'EMP005'," +
				"'requestStartTime':'2011-03-21 16:00','meetingLength':'3'}]";
		hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";

		String actualoutput = outputGenerator.print(meetingRequest, hours);

		ArrayList<String> expectedOutput = new ArrayList<>();
		expectedOutput.add("2011-03-21");
		expectedOutput.add("09:00 11:00 EMP002");
		expectedOutput.add("2011-03-22");
		expectedOutput.add("14:00 16:00 EMP003");
		expectedOutput.add("16:00 17:00 EMP004");
		Gson gson = new Gson();

		assertEquals(gson.toJson(expectedOutput), actualoutput);

	}

}
