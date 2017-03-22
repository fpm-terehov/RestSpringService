package controller;

import model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void noParamGreetingShouldReturnDefaultMessage() throws Exception {

        String meetingRequest =
                "[{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP001'," +
                        "'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
                        "{'requestTime':'2011-03-16 12:34:56','employeeId':'EMP002'," +
                        "'requestStartTime':'2011-03-21 09:00','meetingLength':'2'}," +
                        "{'requestTime':'2011-03-16 09:28:23','employeeId':'EMP003'," +
                        "'requestStartTime':'2011-03-22 14:00','meetingLength':'2'}," +
                        "{'requestTime':'2011-03-17 10:17:06','employeeId':'EMP004'," +
                        "'requestStartTime':'2011-03-22 16:00','meetingLength':'1'}," +
                        "{'requestTime':'2011-03-15 17:29:12','employeeId':'EMP005'," +
                        "'requestStartTime':'2011-03-21 16:00','meetingLength':'3'}]";
        String hours = "{'officeStartTime':'0900','officeFinishTime':'1730'}";

        MeetingScheduleOutputGenerator outputGenerator = new MeetingScheduleOutputGenerator(new MeetingScheduler());
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(outputGenerator.print(meetingRequest,hours)));
    }

    @Test
    public void paramGreetingShouldReturnTailoredMessage() throws Exception {

        String expectedOutput = "[\"2011-03-21\",\"09:00 11:00 EMP002\",\"16:00 19:00 EMP005\",\"2011-03-22\",\"14:00 16:00 EMP003\",\"16:00 17:00 EMP004\"]";

        this.mockMvc.perform(get("/").param("hours", "{'officeStartTime':'0900','officeFinishTime':'2030'}"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(expectedOutput));
    }

}
