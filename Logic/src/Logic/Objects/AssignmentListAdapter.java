package Logic.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@XmlRootElement(name="Assignments")
@XmlAccessorType(XmlAccessType.FIELD)
//this class is an adapter for saving the Assignments map to xml
    public class AssignmentListAdapter {
        private List<Assignment> assignmentList;

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }
}

