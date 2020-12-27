package Logic.Objects;
import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;

import javax.xml.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@XmlRootElement
public class WindowRegistration{
    private ActivityTypeEnum activityType;
    private BoatTypeEnum boatType;
    private LocalTime startTime;
    private LocalTime endTime;

    public WindowRegistration (){}

    public WindowRegistration(ActivityTypeEnum activityTypeEnum, BoatTypeEnum boatType,
                              LocalTime startTimeInput, LocalTime endTimeInput){
        this.boatType = boatType;
        this.activityType = activityTypeEnum;
        this.startTime = startTimeInput;
        this.endTime = endTimeInput;
    }

    public WindowRegistration(LocalTime StartTimeInput, LocalTime EndTimeInput, DayOfWeek day) {
        this(null, null, StartTimeInput, EndTimeInput);
    }

    public WindowRegistration(LocalTime StartTimeInput, LocalTime EndTimeInput){
        this(null,null,StartTimeInput , EndTimeInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (this.hashCode() != o.hashCode())
            return false;
        WindowRegistration that = (WindowRegistration) o;
        return  startTime.equals(that.startTime) &&
                endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityType, startTime, endTime);
    }


    public boolean isOverlapping(WindowRegistration windowRegistration) {
        if (equals(windowRegistration))
            return true;
        return this.getStartTime().isBefore(windowRegistration.getEndTime()) &&
                windowRegistration.getStartTime().isBefore(this.getEndTime());
    }

    public ActivityTypeEnum getActivityType() {
        return activityType;
    }

    public BoatTypeEnum getBoatType() {return boatType;}

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @XmlAttribute
    public void setActivityType(ActivityTypeEnum activityType) {
        this.activityType = activityType;
    }

    @XmlAttribute
    public void setBoatType(BoatTypeEnum boatType) {
        this.boatType = boatType;
    }
    @XmlTransient
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    @XmlTransient
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    @XmlAttribute
    public void setEndTimeXml(String endTime){this.endTime = LocalTime.parse(endTime);}
    @XmlAttribute
    public void setStartTimeXml(String startTime){this.startTime = LocalTime.parse(startTime);}

    public String getStartTimeXml(){return this.startTime.toString();}

    public String getEndTimeXml(){return this.endTime.toString();}
}

