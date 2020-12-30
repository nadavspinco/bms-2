package Logic.Objects;
import Logic.Enum.BoatTypeEnum;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@XmlRootElement(name = "Registration")
public class Registration implements Serializable {
    private Member rowerOfRegistration;
    private List<Member> rowersListInBoat; // if the boat is not a single type.
    private WindowRegistration windowRegistration; // date of the sail.
    private LocalDate orderDate;
    private LocalDateTime activityDate;
    private LocalDateTime endTime;
    private Set<BoatTypeEnum> boatTypes; //

    private boolean IsConfirmed;

    public Registration () {}

    public Registration(Member rowerOfRegistration, List<Member> rowerList, WindowRegistration windowRegistration,
                        LocalDate activityDate, Set<BoatTypeEnum> boatTypeList){
        this.rowerOfRegistration = rowerOfRegistration;
        this.windowRegistration = windowRegistration;
        this.activityDate = activityDate.atTime(windowRegistration.getStartTime());
        this.endTime = activityDate.atTime(windowRegistration.getEndTime());
        if(endTime.isBefore(this.activityDate)){
            this.endTime = this.endTime.plusDays(1);
        }
        this.rowersListInBoat = rowerList;
        this.boatTypes = boatTypeList;
        this.orderDate = LocalDate.now();
        this.IsConfirmed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return getRowerOfRegistration().equals(that.getRowerOfRegistration()) &&
                getWindowRegistration().equals(that.getWindowRegistration()) &&
                getDateOfRegistration().equals(that.getDateOfRegistration()) &&
                getActivityDate().equals(that.getActivityDate()) &&
                Objects.equals(getBoatType(), that.getBoatType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRowerOfRegistration(), getWindowRegistration(), getDateOfRegistration(), getActivityDate(), getBoatType());
    }
    public boolean isOverlapping(Registration registration) {
        return this.activityDate.isBefore(registration.getEndTime()) &&
                registration.getActivityDate().isBefore(this.endTime);
    }

    public LocalDateTime getEndTime(){return endTime;}

    public Member getRowerOfRegistration() {
        return rowerOfRegistration;
    }

    @XmlElement
    public void setRowerOfRegistration(Member rowerOfRegistration) {
        this.rowerOfRegistration = rowerOfRegistration;
    }

    public List<Member> getRowersListInBoat() {
        return rowersListInBoat;
    }

    @XmlElement
    public void setRowersListInBoat(List<Member> rowersListInBoat) {
        this.rowersListInBoat = rowersListInBoat;
    }

    public WindowRegistration getWindowRegistration() {
        return windowRegistration;
    }

    @XmlElement
    public void setWindowRegistration(WindowRegistration windowRegistration) {
        this.windowRegistration = windowRegistration;
    }

    public LocalDate getDateOfRegistration() {
        return orderDate;
    }

    @XmlAttribute
    public void setDateOfRegistrationXML(String dateOfRegistration) {
        this.orderDate = LocalDate.parse(dateOfRegistration);
    }
    public String getDateOfRegistrationXML(){return orderDate.toString();}
    @XmlTransient
    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.orderDate = dateOfRegistration;
    }

    public LocalDateTime getActivityDate() {
        return activityDate;
    }
    @XmlTransient
    public void setActivityDate(LocalDateTime activityDate) {
        this.activityDate = activityDate;
    }
    @XmlAttribute
    public void setActivityDateXML(String activityDate){this.activityDate =LocalDateTime.parse(activityDate);}

    public String getActivityDateXML(){return  this.activityDate.toString();}

    public BoatTypeEnum[] getBoatType() {
        return boatTypes.toArray(new BoatTypeEnum[0]);
    }

    @XmlElement
    public void setBoatType(Set<BoatTypeEnum> boatType) {
        this.boatTypes = boatType;
    }

    public boolean isConfirmed() {
        return IsConfirmed;
    }

    @XmlAttribute
    public void setConfirmed(boolean confirmed) {
        IsConfirmed = confirmed;
    }

    public Set<BoatTypeEnum> getBoatTypesSet() {
        return boatTypes;
    }

    @XmlAttribute
    public void setOrderDateXML(String orderDate) {
        this.orderDate = LocalDate.parse(orderDate);
    }
    public String  getOrderDateXML(){
        return orderDate.toString();
    }
    @XmlTransient
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    @XmlTransient
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @XmlAttribute
    public void setEndTimeXML(String  endTime) {
        this.endTime=LocalDateTime.parse(endTime);
    }
    public String getEndTimeXML() {
        return endTime.toString();
    }

    @XmlElement
    public void setBoatTypes(Set<BoatTypeEnum> boatTypes) {
        this.boatTypes = boatTypes;
    }

    public Set<BoatTypeEnum> getBoatTypes() {
        return boatTypes;
    }
    public LocalDate getOrderDate() {
        return orderDate;
    }

}
