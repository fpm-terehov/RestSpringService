package controller;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import model.*;

@RestController
public class HomeController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private MeetingScheduleOutputGenerator outputGenerator =
            new MeetingScheduleOutputGenerator(new MeetingScheduler());

    @RequestMapping("/")
    public Transporter greeting(@RequestParam(value="input",
            defaultValue=
                    "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
                    "'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
                    "{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
                    "'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
                    "{'requestTime':'2011-03-16 09:28:23','employeeId':'EMP003'," +
                    "'requestStartTime':'2011-03-22 14:00','meetingLength':'2'}," +
                    "{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP004'," +
                    "'requestStartTime':'2011-03-22 16:00','meetingLength':'1'}," +
                    "{'requestTime':'2011-03-15 17:29:12','employeeId':'EMP005'," +
                    "'requestStartTime':'2011-03-21 16:00','meetingLength':'3'}]") String input,
                                      @RequestParam(value="hours",
                                              defaultValue = "{'officeStartTime':'0900','officeFinishTime':'1730'}") String hours) {
        return new Transporter(outputGenerator.print(input, hours));
    }
}
