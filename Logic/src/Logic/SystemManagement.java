package Logic;
import Logic.Enum.BoatTypeEnum;
import Logic.Enum.LevelEnum;
import Logic.Objects.*;
import Logic.jaxb.Activities;
import Logic.jaxb.Boats;
import Logic.jaxb.Members;
import Logic.jaxb.Timeframe;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.lang.String.format;

@XmlRootElement
public class SystemManagement implements EngineInterface{
    private List<Boat> boatList;
    private List<Member> memberList;
    private Map<LocalDate, List<Registration>> registrationMapToConfirm;
    private List<WindowRegistration> windowRegistrationList;
    private Map<LocalDate, List<Assignment>> assignmentsMap;

    private Member getMemberRef(Member member){
        System.out.println("in getMemberRef");
        for(Member memberRef: memberList){
            if(member.equals(memberRef)){
                return memberRef;
            }
        }
        return null;
    }

    public SystemManagement() {
        boatList = new LinkedList<Boat>();
        memberList = new LinkedList<Member>();
        windowRegistrationList = new LinkedList<WindowRegistration>();
        registrationMapToConfirm =new HashMap<LocalDate, List<Registration>> ();
        assignmentsMap = new HashMap<LocalDate,List<Assignment>>();
        addDummyData();

    }



    public List<WindowRegistration> getWindowRegistrationList() {
        return windowRegistrationList;
    }

    public boolean isBoatIsPrivate(String boatId){
        for (Boat boat : boatList){
            if(boat.getSerialBoatNumber().equals(boatId)){
                return boat.isPrivate();
            }
        }
        return false;
    }



    public void fixReferencesAfterImportInnerDetails() {
        //fix multiple references after importing
        fixRegistrationReferences();
        fixAssignmentsReferences();
    }

    private void fixRegistrationReferences() {
        //fix multiple references after importing
        if(registrationMapToConfirm!= null){
            for (List<Registration> registrations : registrationMapToConfirm.values()){
                registrations.forEach(this::fixRegistration);
            }
        }
    }

    private void fixRegistration(Registration registration) {
        //fix Registration references after importing
        List<Member> oldMemberList = registration.getRowersListInBoat();
        List<Member> newMemberList = new LinkedList<Member>();
        for (Member member: oldMemberList){
           Optional<Member> optionalMember = memberList.stream().filter(memberInList
                   -> memberInList.equals(member)).findFirst();
            if(optionalMember.isPresent()) {
                newMemberList.add(optionalMember.get());
                if(!registration.isConfirmed()) {
                    optionalMember.get().addRegisterRequest(registration);
                }
            }
        }
        registration.setRowersListInBoat(newMemberList);
        Member oldRower = registration.getRowerOfRegistration();
        Optional<Member> optionalMember = memberList.stream().filter(memberInList
                -> memberInList.equals(oldRower)).findFirst();
        optionalMember.ifPresent(registration::setRowerOfRegistration);
    }

    private void fixAssignmentsReferences() {
        if(assignmentsMap!=null){
            for(List<Assignment> assignmentList :assignmentsMap.values()){
                assignmentList.forEach(assignment -> fixAssignment(assignment));
            }
        }

    }

    private void fixAssignment(Assignment assignment) {
        //fix Assignment references after importing
        fixRegistration(assignment.getRegistration());
        fixBoatReference(assignment);
    }

    private void fixBoatReference(Assignment assignment) {
        //fix boat references after importing
        Optional<Boat> optionalBoat = boatList.stream().filter(boatInList-> boatInList.equals(assignment.getBoat())).findFirst();
        optionalBoat.ifPresent(assignment::setBoat);
    }

    public void linkBoatsToMembersAfterImport()
    {
        //link between boats to member after importing from outsource data
        for(Member member: memberList){
            if(member.getHasPrivateBoat())
                for(Boat boat: boatList){
                    if(member.getIdentifyPrivateBoat().equals(boat.getSerialBoatNumber())) {
                        if(boat.getOwnerMember() == null) {
                            addPrivateBoat(member, boat.getSerialBoatNumber());
                        }
                    }
                }
                }
    }
    @XmlElement(name = "Registrations")
    public void setRegistrationList(  RegistrationListAdapter registrationListAdapter) {
        List<Registration> registrationList = registrationListAdapter.getRegistrationList();
        Map<LocalDate, List<Registration>> assignmentsMap = new HashMap<LocalDate, List<Registration>>();
        if(registrationList!=null) {
            for (Registration registration : registrationList) {
                if (assignmentsMap.containsKey(registration.getActivityDate().toLocalDate())) {
                    assignmentsMap.get(registration.getActivityDate().toLocalDate()).add(registration);
                } else {
                    List<Registration> registrations = new LinkedList<Registration>();
                    registrations.add(registration);
                    assignmentsMap.put(registration.getActivityDate().toLocalDate(), registrations);
                }
            }
        }
        this.registrationMapToConfirm =assignmentsMap ;
    }

  @XmlElement(name = "Assignments")
    public void setAssignmentsList(AssignmentListAdapter assignmentListAdapter) {
        //Save the Assignments to xml using Adapter
      List<Assignment> assignmenstList = assignmentListAdapter.getAssignmentList();
      Map<LocalDate, List<Assignment>> assignmentsMap = new HashMap<LocalDate, List<Assignment>>();
      if(assignmenstList != null) {
          for (Assignment assignment : assignmenstList) {

              if (assignmentsMap.containsKey(assignment.getRegistration().getActivityDate().toLocalDate())) {
                  assignmentsMap.get(assignment.getRegistration().getActivityDate().toLocalDate()).add(assignment);
              } else {
                  List<Assignment> assignments = new LinkedList<Assignment>();
                  assignments.add(assignment);
                  assignmentsMap.put(assignment.getRegistration().getActivityDate().toLocalDate(), assignments);
              }
          }
      }
        this.assignmentsMap =assignmentsMap;
    }

    public RegistrationListAdapter getRegistrationList()
    {
        List<Registration> toReturn = new LinkedList<Registration>();
        for (List<Registration> registrationList:registrationMapToConfirm.values()){
            registrationList.forEach(registration -> toReturn.add(registration));
    }
        RegistrationListAdapter registrationListAdapter = new RegistrationListAdapter();
        registrationListAdapter.setRegistrationList(toReturn);
        return registrationListAdapter;
    }

    public AssignmentListAdapter getAssignmentsList(){
        List<Assignment> toReturn = new LinkedList<Assignment>();
        for (List<Assignment> assignmentList: assignmentsMap.values()){
            assignmentList.forEach(assignment -> toReturn.add(assignment));
        }
        AssignmentListAdapter assignmentListAdapter = new AssignmentListAdapter();
        assignmentListAdapter.setAssignmentList(toReturn);
        return assignmentListAdapter;
    }

    public void addDummyData(){
        addMember("eitan","0504081994","e@walla.com",
                "1234",26,"nothing",LevelEnum.WorldClass,true,"a2");
    }

    public boolean isRegistrationAllowedForMember(Registration registration, Member member) {
        //this function return true if a member is valid for registration
       List<Registration> registrationArr = getRegistrationBySpecificDay(registration.getActivityDate().toLocalDate());
       if(registrationArr != null && registrationArr.size() != 0){
           for (Registration registrationExist : registrationArr){
               if(registrationExist.getRowersListInBoat().contains(member) &&
                       registrationExist.isOverlapping(registration)) {
                   return false;
               }
           }
       }
        Assignment [] assignments = getAssignmentByDate(registration.getActivityDate().toLocalDate());
        if(assignments != null && assignments.length != 0) {
            for (Assignment assignment : assignments) {
                if (assignment.getRegistration().getRowersListInBoat().contains(member) &&
                        assignment.getRegistration().isOverlapping(registration)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isRegistrationAllowed(Registration registration)
    {

        //return true if is possible to add this registration
        for (Member member:registration.getRowersListInBoat())
        {
            if(!isRegistrationAllowedForMember(registration,member))
                return false;
        }
        return true;
    }

    public Assignment[] getAssignmentForward(int numOfDays)
    {
        //return an array with all the assignment of today + numOfDays forwards
        LocalDate dateToAdd = LocalDate.now();
        List<Assignment> toReturn = new LinkedList<Assignment>();
        for (int i = 0; i <= numOfDays; i++) {
            if(assignmentsMap.containsKey(dateToAdd)) {
                List<Assignment> registrationPerDate = assignmentsMap.get(dateToAdd);
                registrationPerDate.forEach((assignment -> toReturn.add(assignment)));
            }
            dateToAdd = dateToAdd.plusDays(1);
        }
        return toReturn.toArray(new Assignment[0]);
    }

    public void removeMemberFromAssigment(Assignment assignment, Member member, boolean toSplit){
        assignment = getAssignmentRef(assignment);
       Member memberRef  = getMemberRef(member);
        //remove a member from assignment, if toSplit is true a new registration will be added to the member
            removeAssignment(assignment,true);
            List<Member> updatedMembersList = new LinkedList<Member>();
            assignment.getRegistration().getRowersListInBoat().forEach(memberInList -> {
                if(!memberInList.equals(memberRef)){
                    updatedMembersList.add(memberInList);
                }
            } );
            Registration  updatedRegistration = new Registration(assignment.getRegistration().getRowerOfRegistration(),
                    updatedMembersList,
                    assignment.getRegistration().getWindowRegistration(),
                    assignment.getRegistration().getActivityDate().toLocalDate(),
                    assignment.getRegistration().getBoatTypesSet());
            removeAssignment(assignment,true);
            assignBoat(updatedRegistration,assignment.getBoat());
    if(assignment.getRegistration().getRowersListInBoat().contains(memberRef)) {
        if (toSplit) {
            List<Member> memberList = new LinkedList<Member>();
            memberList.add(member);
            Registration splitedRegistration = new Registration(memberRef, memberList,
                    assignment.getRegistration().getWindowRegistration(),
                    assignment.getRegistration().getActivityDate().toLocalDate()
                    , assignment.getRegistration().getBoatTypesSet());
            try {
                addRegistration(splitedRegistration,false);
            } catch (InvalidRegistrationException e) {
            }
        }
    }
}

    public Registration[] getValidRegistrationToUnion(Assignment assignment)
    {
        Assignment assignmentRef = getAssignmentRef(assignment);
        //Return an array with all the registration You can union with the current assignment
        List<Registration> registrationsToReturn = new LinkedList<Registration>();
        if(registrationMapToConfirm.containsKey(assignmentRef.getRegistration().getActivityDate().toLocalDate())){
            registrationMapToConfirm.get(assignmentRef.getRegistration().getActivityDate().toLocalDate()).
                    forEach(registration -> {
                        if(assignmentRef.isUnionPossible(registration)){
                            registrationsToReturn.add(registration);
                        }
                    });
        }
        return registrationsToReturn.toArray(new Registration[0]);
    }

    public void unionRequestToAssignment(Assignment assignment, Registration registration)
    {
        assignment= getAssignmentRef(assignment);
        registration =getRegistrationRef(registration);
        if(assignment.isUnionPossible(registration))
        {
            List<Member> newListOfMembers = new LinkedList<Member>();
            assignment.getRegistration().getRowersListInBoat().forEach(member -> newListOfMembers.add(member));

            registration.getRowersListInBoat().forEach(member -> newListOfMembers.add(member));
            Registration unionedRegistration =
                    new Registration(assignment.getRegistration().getRowerOfRegistration(),
                    newListOfMembers,
                    assignment.getRegistration().getWindowRegistration(),
                    assignment.getRegistration().getActivityDate().toLocalDate(),
                    assignment.getRegistration().getBoatTypesSet());
            removeAssignment(assignment,true);
            removeRegistration(registration);
            this.assignBoat(unionedRegistration,assignment.getBoat());
        }
    }
    public void removeAssignment(Assignment assignment,boolean toDeleteRegistration)
    {
        assignment = getAssignmentRef(assignment);
        //delete an assignment, if toDeleteRegistration is true the registration will be deleted
        if(assignmentsMap.containsKey(assignment.getRegistration().getActivityDate().toLocalDate())){
            List<Assignment> requestList = assignmentsMap.get(assignment.getRegistration().getActivityDate().toLocalDate());
            requestList.remove(assignment);
            if(requestList.size() == 0){
                assignmentsMap.remove(assignment.getRegistration().getActivityDate().toLocalDate());
            }
            if(!toDeleteRegistration) {
                try {
                    addRegistration(assignment.getRegistration(),false);
                }catch (InvalidRegistrationException e){}

            }
        }
    }
    public Boat[] getArrayOfValidBoats(Registration registration){
        registration = getRegistrationRef(registration);
        List<Boat> validBoatList = new LinkedList<Boat>();
            for (Boat boat : boatList) {
                if(isLegalAssigment(registration,boat)){
                    validBoatList.add(boat);
                }
            }

        return validBoatList.toArray(new Boat[0]);
    }

    public void assignBoat(Registration registration, Boat boat){
        //MAKE AN Assigment for registration if possible with bpat
        registration = getRegistrationRef(registration);
        boat = getBoatRef(boat);
        if (isLegalAssigment(registration,boat)){
            addAssignment(new Assignment(registration,boat));
            registration.setConfirmed(true);
            removeRegistration(registration);
        }
    }

    private void removeRegistration(Registration registration) {
        if(registrationMapToConfirm.containsKey(registration.getActivityDate().toLocalDate())){
            List<Registration> registrationList = registrationMapToConfirm.get(registration.getActivityDate().toLocalDate());
            if(registrationList!= null){
                registrationList.remove(registration);
            }
        }
    }

    private boolean isBoatTypeFit(Registration registration, Boat boat){
        //check if the boat type of a boat is valid for registration
        BoatTypeEnum [] boatTypeRequested =registration.getBoatType();
        if(boatTypeRequested == null || boatTypeRequested.length == 0)
            return true;
        if(registration.getRowersListInBoat().size()> boat.getNumberOfRowersAllowed())
            return false;
         for (BoatTypeEnum boatType:boatTypeRequested){
             if(boatType.equals(boat.getBoatType())){
                 return true;
             }
         }
        return false;
    }

    public boolean isAssigmentIsValidForMember(Registration registration,Member member) {
        registration = getRegistrationRef(registration);
        member = getMemberRef(member);
        //check if we can add the member for this registration
        Assignment[] assignmentForward = getAssignmentForward(7);
        for (Assignment assignment : assignmentForward) {
            if (assignment.getRegistration().getRowersListInBoat().contains(member)) {
                if (assignment.getRegistration().isOverlapping(registration)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLegalAssigment(Registration registration, Boat boat) {
        //return true if we can assign the registration to boat
        registration = getRegistrationRef(registration);
        boat = getBoatRef(boat);
        if(boat.isPrivate() && (boat.getOwnerMember() == null || !boat.getOwnerMember().equals(registration.getRowerOfRegistration()))){
            //if you want to assign Private boat the owner have to make the order
            return false;
        }
        if(boat.isAvailable() == false){
            return false;
        }

        for (Member member: registration.getRowersListInBoat()){
            if(!isAssigmentIsValidForMember(registration,member)){
                return false;
            }
        }

        if(!isBoatTypeFit(registration,boat))
            return false;
        boolean isLegalAssigment = true;
        if (!assignmentsMap.containsKey(registration.getActivityDate().toLocalDate()))
            return true;
        else {
            List<Assignment> requestDayAssigments = assignmentsMap.get(registration.getActivityDate().toLocalDate());
            for (Assignment assignment : requestDayAssigments) {
                if (assignment.getBoat().equals(boat)) {
                    if (assignment.getRegistration().getWindowRegistration()
                            .isOverlapping(registration.getWindowRegistration())) {
                        isLegalAssigment = false;
                        break;
                    }
                }
            }
            return isLegalAssigment;
        }
    }

    public Registration[] getMainRegistrationByDays(int numOfDays) {
        LocalDate dateToAdd = LocalDate.now();
        List<Registration> toReturn = new LinkedList<Registration>();
        for (int i = 0; i <= numOfDays; i++) {
            if(registrationMapToConfirm.containsKey(dateToAdd)) {
                List<Registration> registrationPerDate = registrationMapToConfirm.get(dateToAdd);
                registrationPerDate.forEach((registration -> toReturn.add(registration)));
            }
            dateToAdd = dateToAdd.plusDays(1);
        }
        return toReturn.toArray(new Registration[0]);
    }

    public List<Registration> getRegistrationBySpecificDay(LocalDate date){
        if(registrationMapToConfirm.containsKey(date))
            return Collections.unmodifiableList(registrationMapToConfirm.get(date)) ;
        else
            return null;
    }

    public List<Registration> getConfirmedRegistrationBySpecificDay(LocalDate date){
        if(this.assignmentsMap.containsKey(date)) {
            List<Registration> regiList = null;
            for (Assignment assign : assignmentsMap.get(date))
                regiList.add(assign.getRegistration());
            return Collections.unmodifiableList(regiList);
        }
        else
            return null;
    }

    public void addRegistration(Registration registration, boolean assignPrivateBoutIfExists) throws InvalidRegistrationException {

        if(!isRegistrationAllowed(registration))
        {
            throw new InvalidRegistrationException();
        }
        if(registrationMapToConfirm.containsKey(registration.getActivityDate().toLocalDate())) {
            registrationMapToConfirm.get(registration.getActivityDate().toLocalDate()).add(registration);
        }
        else {
            List<Registration> registrationListToAdd = new LinkedList<Registration>();
            registrationListToAdd.add(registration);
            registrationMapToConfirm.put(registration.getActivityDate().toLocalDate(),registrationListToAdd);
        }

        for(Member member : registration.getRowersListInBoat()){ // add to each member the new register request;
            member.addRegisterRequest(registration);
        }
        if(assignPrivateBoutIfExists && registration.getRowerOfRegistration().getHasPrivateBoat())
        {
           assignPrivateBoat(registration);
        }
    }

    public Assignment[] getAssignmentByDate(LocalDate date){
        //return all assignment of a specific date
        List<Assignment> assignmentByDate = null;
        if(assignmentsMap.containsKey(date)){ //Exception ??
            assignmentByDate = assignmentsMap.get(date);
        }
        if(assignmentByDate != null) {
            return assignmentByDate.toArray(new Assignment[0]);
        }
        else return null;
    }

    private void assignPrivateBoat(Registration registration){
        Boat privateBoat = getBoatById(registration.getRowerOfRegistration().getIdentifyPrivateBoat());
        if(privateBoat!=null) {
            if (isLegalAssigment(registration, privateBoat)) {
                assignBoat(registration, privateBoat);
            }
        }

    }

    public Boat getBoatById(String boatId){
        for (Boat boat: boatList) {
            if(boat.getSerialBoatNumber().equals(boatId))
                return boat;
        }
        return null;
    }

    public WindowRegistration[] getWindowRegistrations(){
        return windowRegistrationList.toArray(new WindowRegistration[0]);
    }
    
    public Boat[] getBoatArry(){
        System.out.println("in getBoatArry");
        Boat[] boatsArr = new Boat[boatList.size()];
        return boatsArr = boatList.toArray(boatsArr);
    }
    
    public Member[] getMemberArry(){
        Member[] membersArr = new Member[memberList.size()];
        return membersArr = memberList.toArray(membersArr);
    }

    public void deleteWindowRegistration(WindowRegistration windowRegistration){
        windowRegistrationList.remove(windowRegistration);
    }



    public String createBoatCode(Boat boat){ // create the boat code according to the table in the doc file
        String wide = "", coastal = "", code;
        if(boat.isWide())
            wide = "wide";
        if (boat.isCoastalBoat())
            coastal = "coastal";
        code = BoatTypeEnum.BoatTypeCode(boat.getBoatType());
        String boatCode = String.format("%s %s %s",code, wide, coastal);
        return boatCode;
    }

    public void removeMember(Member member){
        member = getMemberRef(member);
        if (member.getHasPrivateBoat()){
            for (Boat boat : boatList){ //if the member has a private boat, this boat is not private boat anymore
                if (boat.getSerialBoatNumber() .equals(member.getIdentifyPrivateBoat())){
                    boat.setPrivate(false);
                    boat.setOwnerMember(null);
                }
            }
        }
        // delete the member from all the request registration which parts of
        removerMemberFromFutureRegistration(member);
        // delete the member from all the assigment which parts of
       removerMemberFromFutureAssignments(member);
        memberList.remove(member);
    }

    private void removerMemberFromFutureRegistration(Member member){
        Registration []regisConfirm = getMainRegistrationByDays(7);
        if(regisConfirm == null || regisConfirm.length == 0) {
            return;
        }
        for (Registration regiConfirm :regisConfirm) {
            if (regiConfirm.getRowersListInBoat().contains(member)) {
                if (regiConfirm.getRowersListInBoat().size() == 1) {
                    removeRegistration(regiConfirm);
                } else {
                    removeRowerSpecificFromRegiRequest(member, regiConfirm,false);
                }
            }
        }
    }

    private void removerMemberFromFutureAssignments(Member member){
        Assignment [] assignments = getAssignmentForward(7);
        if(assignments == null || assignments.length ==0)
        {
            return;
        }
        for (Assignment assignment: assignments){
            if(assignment.getRegistration().getRowersListInBoat().contains(member)){
                if(assignment.getRegistration().getRowersListInBoat().size() == 1) {
                    removeAssignment(assignment,true);
                }
                else {
                    removeMemberFromAssigment(assignment, member, false);
                }
            }
        }
    }

    public void removeBoat(Boat boat){
        boat = getBoatRef(boat);
        if(boat.getOwnerMember() != null){ // if the boat has owner
            for (Member member : memberList){
                if(member.equals(boat.getOwnerMember())){
                    member.setHasPrivateBoat(false);
                    member.setIdentifyPrivateBoat(null);
                }
            }
        }
        boatList.remove(boat);
        removeAllFutureAssignmentByBoats(boat);
    }

    private void removeAllFutureAssignmentByBoats(Boat boat){
        Assignment[] assignments = getAssignmentForward(7);
        for(Assignment assignment : assignments){
            if(assignment.getBoat().equals(boat)){
                removeAssignment(assignment,false);
            }
        }
    }

    private void addAssignment(Assignment assignment){

        if (assignmentsMap.containsKey(assignment.getRegistration().getActivityDate().toLocalDate()))
            assignmentsMap.get(assignment.getRegistration().getActivityDate().toLocalDate()).add(assignment);
        else{
            List<Assignment> assignmentsListToAdd = new LinkedList<Assignment>();
            assignmentsListToAdd.add(assignment);
            assignmentsMap.put(assignment.getRegistration().getActivityDate().toLocalDate(),assignmentsListToAdd);
        }
    }

    public boolean isWindowRegistrationEmpty(){
        return windowRegistrationList.isEmpty();
    }

    public boolean isEmailAlreadyExist(String email) {
        email = email.toLowerCase();
        boolean isEmailAlreadyExist = false;
        for (Member member : memberList) {
            if (member.getEmail().equals(email)) {
                isEmailAlreadyExist = true;
                break;
            }
        }
        return isEmailAlreadyExist;
    }

    public void addWindowRegistration(WindowRegistration windowRegistration) {
        windowRegistrationList.add(windowRegistration);
    }

    public Member loginMember(String emailInput, String passwordInput) {
        Member memberToLogin = getMember(emailInput);
        if (memberToLogin != null) {
            if (!(memberToLogin.getPassword().equals(passwordInput))) {
                memberToLogin = null;
            }
        }
        return memberToLogin;
    }

    private Member getMember(String email) {
        email = email.toLowerCase();
        Member memberToReturn = null;
        for (Member currentMember : memberList) {
            if (currentMember.getEmail().equals(email)) {
                memberToReturn = currentMember;
                break;
            }
        }
        return memberToReturn;
    }

    public boolean isBoatExistBySerial(String boatSerial) {
        Boolean valid = false;
        for(Boat boat : boatList){
            if(boat.getSerialBoatNumber().equals(boatSerial))
                return valid = true;
        }
        return valid;
    }

    public boolean isMemberExistBySerial(String serial){
        boolean valid = false;
        for (Member member : memberList){
            if(member.getSerial().equals(serial))
                return valid = true;
        }
        return valid;
    }

    public void addBoat(String boatNameInput, BoatTypeEnum boatTypeInput, boolean isCoastalInput, boolean isWideInput, String serial) {
        Boat newBoat = new Boat(boatNameInput, boatTypeInput, isCoastalInput, isWideInput, serial);
        addBoat(newBoat);
    }

    public void addBoat(Boat boat){
        this.boatList.add(boat);
    }

    public void addMember(Member member){
        this.memberList.add(member);
    }

    public void addMember(String name, String phone, String email, String password, int age,
                          String additionalDetails, LevelEnum lvl, boolean isManager, String ID){
        Member newMember = new Member(name,phone,email,password,age,additionalDetails,lvl,isManager, ID);
        addMember(newMember);
    }

    public void changePhoneNumber(Member member, String newPhone) {
        member = getMemberRef(member);
        member.setPhoneNumber(newPhone);
    }

    public void changeName(Member member, String newName) {
        Member memberRef = getMemberRef(member);
        if(memberRef != null) {
            memberRef.setNameMember(newName);
        }
        else {
            //TODO
        }
    }

    private Assignment getAssignmentRef(Assignment assignment){
        for(Assignment assignmentRef : assignmentsMap.get(assignment.getRegistration().getActivityDate().toLocalDate())){
            if(assignmentRef.equals(assignment)){
                return assignmentRef;
            }
        }
        return null;
    }

    private Registration getRegistrationRef(Registration registration) {
        for (Registration registrationRef : registrationMapToConfirm.get(registration.getActivityDate().toLocalDate())) {
            if (registrationRef.equals(registration)) {
                return registrationRef;
            }
        }
        return null;
    }

    private Boat getBoatRef(Boat boat){

        for(Boat boatRef: boatList){
            if(boat.equals(boatRef)){
                return boatRef;
            }
        }
        return null;
    }


    public List<Boat> getBoatList() {
        return boatList;
    }
    public void changePassword(Member member, String newPassword) {
        member = getMemberRef(member);
        Member memberRef = getMemberRef(member);
        memberRef.setPassword(newPassword);
    }

    public void changeEmail(Member member, String newEmail) throws EmailAlreadyExistException {
        member = getMemberRef(member);
        if (isEmailAlreadyExist(newEmail))
            throw new EmailAlreadyExistException
                    (format("this email already exists %s", member.getEmail()));
        else
            member.setEmail(newEmail);
    }

    public void updateMemberAge(Member member, int age){
        member = getMemberRef(member);
        member.setAge(age);
    }

    public void updateMemberEndDate(Member member, int numberOfYears){
        member = getMemberRef(member);
        LocalDateTime newDate = member.getEndDate();
        member.setEndDate(newDate.plusYears(numberOfYears));
    }

    public void updateMemberLevel(Member member, LevelEnum level){
        member = getMemberRef(member);
        member.setLevel(level);
    }

    public void cancelMembersPrivateBoat(Member member){
        member = getMemberRef(member);
        boolean hasBoat = member.getHasPrivateBoat();
        if(hasBoat){
            for (Boat boat : boatList) {
                if (boat.getSerialBoatNumber().equals(member.getIdentifyPrivateBoat())) {
                    boat.setPrivate(false);
                    boat.setOwnerMember(null);
                }
            }
            member.setHasPrivateBoat(false);
            member.setIdentifyPrivateBoat(null);
        }
        else
            System.out.println("this member doesnt have private boat");
    }

    public void updateBoatName(Boat boat, String name){
        boat = getBoatRef(boat);
        boat.setBoatName(name);
    }

    public void updateIsWide(Boat boat){
        boat = getBoatRef(boat);
        if(boat.isWide())
            boat.setWide(false);
        else
            boat.setWide(true);
    }

    public void updateIsCoastal(Boat boat) {
        boat = getBoatRef(boat);
        if(boat.isCoastalBoat())
            boat.setCoastalBoat(false);
        else
            boat.setCoastalBoat(true);
    }

    public void fixBoat(Boat boat) {
        boat = getBoatRef(boat);
        boat.setAvailable(true);
    }

    public List<Member> getMemberList() {
        //TODO: work with array!!
        return memberList;
    }

    public void disAbleBoat(Boat boat){
        boat = getBoatRef(boat);
        boat.setAvailable(false);
        removeAllFutureAssignmentByBoats(boat);
    }

    public void removeRegistrationRequestByMember(Registration registration){
        registration = getRegistrationRef(registration);
        for (Member member : registration.getRowersListInBoat()) // remove the request from each rower in rowerlist
            member.removeRegistrationRequest(registration);
        registration.setRowerOfRegistration(null);
        removeRegistration(registration); // remove request from main list.
    }

    public void addRowerToRegiRequest(Member member, Registration regiRequest){
        // method to add rower to regi request after any member want to edit this regi request
        member = getMemberRef(member);
        regiRequest = getRegistrationRef(regiRequest);
        regiRequest.getRowersListInBoat().add(member);
        member.addRegisterRequest(regiRequest);
    }

    public void removeRowerSpecificFromRegiRequest(Member member, Registration regiRequest,boolean toSplitRegistration){
        // method to remove rower from regi request after any member want to edit this regi request
        member = getMemberRef(member);
        regiRequest = getRegistrationRef(regiRequest);
        regiRequest.getRowersListInBoat().remove(member);
        member.removeRegistrationRequest(regiRequest);


        // if the rower who deleted is the main rower of the registration, the new main rower will be the next one in the list of rowers.
        if(member.equals(regiRequest.getRowerOfRegistration())){
            regiRequest.setRowerOfRegistration(regiRequest.getRowersListInBoat().get(0));
        }
        if(toSplitRegistration) {
            List<Member> memberList = new LinkedList<Member>();
            memberList.add(member);
            Registration splitedRegistration = new Registration(member, memberList,
                    regiRequest.getWindowRegistration(),
                    regiRequest.getActivityDate().toLocalDate()
                    , regiRequest.getBoatTypesSet());
            try {
                addRegistration(splitedRegistration, false);
            } catch (InvalidRegistrationException e) {
            }
        }
    }

    public void addBoatTypeToRegiRequest(BoatTypeEnum boatType, Registration regiRequest){
        // method to add another boat type to regi request after any member want to edit this regi request
        regiRequest = getRegistrationRef(regiRequest);
        regiRequest.getBoatTypesSet().add(boatType);
    }

    public void removeBoatTypeFromRegiRequest(BoatTypeEnum boatType, Registration regiRequest){
        // method to remove boat type from regi request after any member want to edit this regi request
        regiRequest = getRegistrationRef(regiRequest);
        regiRequest.getBoatTypesSet().remove(boatType);
    }

    public List<Registration> getHistoryRegistrationOfMember(Member member){
        // show the registration past 7 day ago.
        member= getMemberRef(member);
        List<Registration> oldRegiList = new ArrayList<>();
        LocalDateTime todayDate = LocalDateTime.now(),
                  sevenDayEarlier = LocalDateTime.now().minusDays(8);
        for (Registration regi : getRegiListConfirmedAccordingMember(member)){
            if (regi.getActivityDate().isBefore(todayDate) && regi.getActivityDate().isAfter(sevenDayEarlier))
                oldRegiList.add(regi);
        }
        if (oldRegiList.size() == 0)
            oldRegiList = null;

        return oldRegiList;
    }

    public List<Registration> getFutureRegistrationOfMember(Member member){
        member= getMemberRef(member);
        List<Registration> futureRegiList = new ArrayList<>();
        LocalDateTime todayDate = LocalDateTime.now(),
                    sevenDayForward = LocalDateTime.now().plusDays(8);
            for (Registration regi : getRegiListConfirmedAccordingMember(member)){
                if (regi.getActivityDate().isBefore(sevenDayForward) && regi.getActivityDate().isAfter(todayDate))
                    futureRegiList.add(regi);
            }
            return futureRegiList;
        }

    public boolean isRowerAllowToBeAddedToRegistration(LocalDate date, Member member, LocalTime startTime, LocalTime endTime){
        member= getMemberRef(member);
        boolean validity = true;
        for(Registration regi : member.getMineRegistrationRequestNotConfirmed()){
            if(date.equals(regi.getActivityDate().toLocalDate())){
                LocalTime tempStartActivity, tempEndActivity;
                tempStartActivity = regi.getWindowRegistration().getStartTime();
                tempEndActivity = regi.getWindowRegistration().getEndTime();
                if(startTime.isBefore(tempEndActivity) && tempStartActivity.isBefore(endTime)){
                    return validity = false; // member has overlapping registration, therefore won't be able to be added to the registration
                }
            }
        }

        for(Registration regiConfirmed : getRegiListConfirmedAccordingMember(member)){
            if(date.equals(regiConfirmed.getActivityDate().toLocalDate())) {
                LocalTime tempStartActivity, tempEndActivity;
                tempStartActivity = regiConfirmed.getWindowRegistration().getStartTime();
                tempEndActivity = regiConfirmed.getWindowRegistration().getEndTime();
                if (startTime.isBefore(tempEndActivity) && tempStartActivity.isBefore(endTime)) {
                    return validity = false; // member has overlapping registration, therefore won't be able to be added to the registration
                }
            }
        }
        return validity; // there is no overlapping registration, there be able to be add to the this registration
    }

    public void addPrivateBoat(Member member, String serialNumBoat){
        member = getMemberRef(member);
        for (Boat boat : boatList){
            if(boat.getSerialBoatNumber().equals(serialNumBoat)) {
                boat.setPrivate(true);
                boat.setOwnerMember(member);
                member.setHasPrivateBoat(true);
                member.setIdentifyPrivateBoat(serialNumBoat);
                break;
            }
        }
    }

    public List<Registration> getRegiListConfirmedAccordingMember(Member member){
        member = getMemberRef(member);
        List <Registration> regiList = new ArrayList<Registration>();
        LocalDate currentDay = LocalDate.now();
        for (int i = 0; i <= 7 ; i++){
            if(assignmentsMap.containsKey(currentDay)){
                List <Assignment> currentDayAssign = assignmentsMap.get(currentDay);
                for(Assignment assign : currentDayAssign){
                    if(assign.getRegistration().getRowersListInBoat().contains(member))
                        regiList.add(assign.getRegistration());
                }
            }
            currentDay = currentDay.plusDays(1);
        }
        return Collections.unmodifiableList(regiList);
    }

    public Members generateMembersToXml(XmlManagement xmlManagement) {  // generate members from system to xml.
        Members members = new Members();
        for (Member member : memberList){
            Logic.jaxb.Member newMember = xmlManagement.convertMemberToXml(member);
            members.getMember().add(newMember);
        }
        return members;
    }

    public Boats generateBoatsToXml(XmlManagement xmlManagement) {  // generate Boats from system to xml.
        Boats boats = new Boats();
        for (Boat boat : boatList){
            Logic.jaxb.Boat newBoat = xmlManagement.convertBoatToXml(boat);
            boats.getBoat().add(newBoat);
        }
        return boats;
    }

    public Activities generateActivitiesToXml(XmlManagement xmlManagement){
        Activities activities = new Activities();
        for(WindowRegistration window : windowRegistrationList){
            Timeframe newTimeFrame = xmlManagement.convertToTimeFrame(window);
            activities.getTimeframe().add(newTimeFrame);
        }
        return activities;
    }

    @XmlElementWrapper
    @XmlElement(name="Boat")
    public void setBoatList(List<Boat> boatList) {
        this.boatList = boatList;
    }

    @XmlElementWrapper
    @XmlElement(name="Member")
    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    @XmlElementWrapper
    @XmlElement(name="WindowRegistration")
    public void setWindowRegistrationList(List<WindowRegistration> windowRegistrationList) {
        this.windowRegistrationList = windowRegistrationList;
    }

    public void cleanAllMembersBecauseImport(){
        memberList.clear();                     // clear all the members
        for (Boat boat : boatList){             // all the boats are not private
            if(boat.isPrivate()){
                boat.setPrivate(false);
                boat.setOwnerMember(null);
            }
        }
        Assignment[] futureAssigment = getAssignmentForward(7);
        for (Assignment assignment : futureAssigment)
            removeAssignment(assignment,true);

        Registration[] futureRegistration = getMainRegistrationByDays(7);
        for(Registration regi : futureRegistration)
            removeRegistration(regi);
    }

    public void cleanAllBoatsBecauseImport(){
        boatList.clear();                             // clear all the boats
        for (Member member : memberList){             // all the members haven't private boat anymore
            if(member.getHasPrivateBoat()){
                member.setHasPrivateBoat(false);
                member.setIdentifyPrivateBoat(null);
            }
        }
        Assignment[] futureAssigment = getAssignmentForward(7); //clear all assignment which old boat assigned to
        for (Assignment assignment : futureAssigment)
            removeAssignment(assignment,true);
    }

    public void cleanAllWindowRegistarionBecauseImport(){
        windowRegistrationList.clear();
    }
}


