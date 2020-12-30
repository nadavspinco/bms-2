package Logic.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class Assignment implements Serializable {
    private Registration registration;
    private Boat boat;

    public Assignment(){}

    public Assignment(Registration registration, Boat boat) {
        this.registration = registration;
        this.boat = boat;
    }

    public void addRower(Member member)
    {
        List<Member> registration = getRegistration().getRowersListInBoat();
        if(registration.size() < boat.getNumberOfRowersAllowed())
        {
            registration.add(member);
        }
    }

    public boolean isUnionPossible(Registration registration)
    {
        if(!this.registration.getWindowRegistration().equals(registration.getWindowRegistration())){
            return false;
        }
        else if (registration.getRowersListInBoat().size() +
                this.registration.getRowersListInBoat().size() > boat.getNumberOfRowersAllowed())
            return false;
        else if(registration.getBoatTypesSet()!= null &&registration.getBoatTypesSet().size()!=0
              && ! registration.getBoatTypesSet().contains(boat.getBoatType()))
            return false;
        return true;
    }

    @XmlElement
    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    @XmlElement
    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public Registration getRegistration() {
        return registration;
    }

    public Boat getBoat() {
        return boat;
    }
}
