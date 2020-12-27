package Logic.Objects;

import com.sun.xml.internal.txw2.annotation.XmlElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@XmlRootElement(name="Registrations")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrationListAdapter {
//this class is an adapter for saving the Registration map to xml
    public List<Registration> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList(List<Registration> registrationList) {
        this.registrationList = registrationList;
    }

    private List<Registration> registrationList;

}

