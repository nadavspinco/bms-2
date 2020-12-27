package Logic.Objects;
import Logic.Enum.LevelEnum;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@XmlRootElement
public class Member{
    private LevelEnum level;
    private String memberSerial;
    private String nameMember;
    private int age;
    private LocalDateTime joinDate;
    private LocalDateTime endDate;
    private String additionalDetails;
    private Boolean hasPrivateBoat;
    private Boolean isManager;
    private String identifyPrivateBoat;
    private String phoneNumber;
    private String email;
    private String password;
    @XmlTransient
    private final List<Registration> mineRegistrationRequest = new LinkedList<>();

    public Member () {}

    public Member(String NameMemberInput, String phoneInput, String emailInput, String passwordInput, int ageInput,
                  String additionalDetailsInput, LevelEnum levelInput, Boolean isManagerInput, String inputID) {
        this.nameMember = NameMemberInput;
        this.phoneNumber = phoneInput;
        this.email = emailInput.toLowerCase();
        this.password = passwordInput;
        this.age = ageInput;
        this.additionalDetails = additionalDetailsInput;
        this.hasPrivateBoat = false;
        this.identifyPrivateBoat = null;
        this.level = levelInput;
        this.isManager = isManagerInput;
        this.joinDate = LocalDateTime.now();
        this.endDate = joinDate.plusYears(1);
        this.memberSerial = inputID;
    }

    public Member(String name, String email, String password, String id){
        this.nameMember = name;
        this.email = email.toLowerCase();
        this.password = password;
        this.memberSerial = id;
        this.hasPrivateBoat = false;
        this.isManager = false;
    }

    @XmlAttribute
    public void setMemberSerial(String memberSerial) {
        this.memberSerial = memberSerial;
    }

    @XmlAttribute
    public void setIdentifyPrivateBoat(String identifyPrivateBoat) {
        this.identifyPrivateBoat = identifyPrivateBoat;
    }

    @XmlAttribute
    public void setNameMember(String nameMember) {
        this.nameMember = nameMember;
    }

    @XmlAttribute
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @XmlAttribute
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    @XmlAttribute
    public void setPassword(String password) {
        this.password = password;
    }

    @XmlAttribute
    public void setAge(int age) {
        this.age = age;
    }

    @XmlAttribute
    public void setEndDateXML(String  endDate) {
        this.endDate = LocalDateTime.parse(endDate);
    }

    public String getEndDateXML() {
        return this.endDate.toString();
    }

    @XmlTransient
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @XmlAttribute
    public void setHasPrivateBoat(Boolean hasPrivateBoat) {
        this.hasPrivateBoat = hasPrivateBoat;
    }

    public String getSerial() {
        return memberSerial;
    }

    public void addRegisterRequest(Registration registration) {
        mineRegistrationRequest.add(registration);
    }

    public String getNameMember() {
        return nameMember;
    }

    public int getAge() {
        return age;
    }

    public LevelEnum getLevel() {
        return level;
    }

    @XmlAttribute
    public void setLevel(LevelEnum i_Identify)
    {
        level = i_Identify;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Boolean getHasPrivateBoat() {
        return hasPrivateBoat;
    }

    public String getIdentifyPrivateBoat() {
        return identifyPrivateBoat;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }


    public boolean getIsManager() {
        return isManager;
    }

    public List<Registration> getMineRegistrationRequestNotConfirmed() {
        return Collections.unmodifiableList(mineRegistrationRequest);
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    @XmlAttribute
    public void setIsManager(boolean manager) {
        isManager = manager;
    }

    public void removeRegistrationRequest(Registration registration){
        mineRegistrationRequest.remove(registration);
    }

    @XmlAttribute
    public void setJoinDateXML(String joinDate) {
        this.joinDate = LocalDateTime.parse(joinDate);
    }

    public String getJoinDateXML(){
        return this.joinDate.toString();
    }
    @XmlTransient
    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    @XmlAttribute
    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public String getMemberSerial() {
        return memberSerial;
    }

    public Boolean getManager() {
        return isManager;
    }

    public List<Registration> getMineRegistrationRequest() {
        return mineRegistrationRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return getMemberSerial().equals(member.getMemberSerial());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMemberSerial());
    }
}



