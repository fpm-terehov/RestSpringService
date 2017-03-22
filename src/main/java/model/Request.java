package model;

/**
 * Created by Dima on 22.03.2017.
 */
public class Request {
    private String requestTime;
    private String employeeId;
    private String requestStartTime;
    private String meetingLength;

    public String getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(String requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getMeetingLength() {
        return meetingLength;
    }

    public void setMeetingLength(String meetingLength) {
        this.meetingLength = meetingLength;
    }

    public Request(Request request){
        this.employeeId = request.getEmployeeId();
        this.meetingLength = request.getMeetingLength();
        this.requestStartTime = request.getRequestStartTime();
        this.meetingLength = request.getMeetingLength();
    }
}
