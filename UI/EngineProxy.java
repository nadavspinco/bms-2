package UI;

import Logic.EmailAlreadyExistException;
import Logic.EngineInterface;
import Logic.Enum.BoatTypeEnum;
import Logic.Enum.LevelEnum;
import Logic.Objects.*;
import Logic.XmlManagement;
import Logic.jaxb.Activities;
import Logic.jaxb.Boats;
import Logic.jaxb.Members;
import Server.ServerRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EngineProxy implements EngineInterface {
    private String host = "localhost";
    private int port=1989;
    private Socket socket;
    private ObjectOutputStream out ;
    private ObjectInputStream in;

    public EngineProxy(String host,int port) throws IOException {
        //TODO:

         this.socket = new Socket(this.host,this.port);
         in =(ObjectInputStream) socket.getInputStream();
         out = (ObjectOutputStream) socket.getOutputStream();
    }

    private void sendRequest(ServerRequest serverRequest)   {
        try {
            out.writeObject(serverRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Object getAnswer()
    {
        Object obj = null;
        try {
            while ((obj = in.readObject())==null){
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public boolean isBoatIsPrivate(String boatId) {
        ServerRequest request = new ServerRequest("isBoatIsPrivate",boatId);
        sendRequest(request);

        return false;
    }



    @Override
    public void fixReferencesAfterImportInnerDetails() {

    }

    @Override
    public boolean isRegistrationAllowedForMember(Registration registration, Member member) {
        return false;
    }

    @Override
    public boolean isRegistrationAllowed(Registration registration) {
        return false;
    }

    @Override
    public Assignment[] getAssignmentForward(int numOfDays) {
        return new Assignment[0];
    }

    @Override
    public void removeMemberFromAssigment(Assignment assignment, Member member, boolean toSplit) {

    }

    @Override
    public Registration[] getValidRegistrationToUnion(Assignment assignment) {
        return new Registration[0];
    }

    @Override
    public void unionRequestToAssignment(Assignment assignment, Registration registration) {

    }

    @Override
    public void removeAssignment(Assignment assignment, boolean toDeleteRegistration) {

    }

    @Override
    public Boat[] getArrayOfValidBoats(Registration registration) {
        return new Boat[0];
    }

    @Override
    public void assignBoat(Registration registration, Boat boat) {

    }

    @Override
    public boolean isAssigmentIsValidForMember(Registration registration, Member member) {
        return false;
    }

    @Override
    public boolean isLegalAssigment(Registration registration, Boat boat) {
        return false;
    }

    @Override
    public Registration[] getMainRegistrationByDays(int numOfDays) {
        return new Registration[0];
    }

    @Override
    public List<Registration> getRegistrationBySpecificDay(LocalDate date) {
        return null;
    }

    @Override
    public List<Registration> getConfirmedRegistrationBySpecificDay(LocalDate date) {
        return null;
    }

    @Override
    public Assignment[] getAssignmentByDate(LocalDate date) {
        return new Assignment[0];
    }

    @Override
    public Boat getBoatById(String boatId) {
        return null;
    }

    @Override
    public WindowRegistration[] getWindowRegistrations() {
        return new WindowRegistration[0];
    }

    @Override
    public Boat[] getBoatArry() {
        return new Boat[0];
    }

    @Override
    public Member[] getMemberArry() {
        return new Member[0];
    }

    @Override
    public void deleteWindowRegistration(WindowRegistration windowRegistration) {

    }

    @Override
    public String createBoatCode(Boat boat) {
        return null;
    }

    @Override
    public void removeMember(Member member) {

    }

    @Override
    public void removeBoat(Boat boat) {

    }

    @Override
    public boolean isWindowRegistrationEmpty() {
        return false;
    }

    @Override
    public boolean isEmailAlreadyExist(String email) {
        return false;
    }

    @Override
    public void addWindowRegistration(WindowRegistration windowRegistration) {

    }

    @Override
    public Member loginMember(String emailInput, String passwordInput) {
        return null;
    }

    @Override
    public boolean isBoatExistBySerial(String boatSerial) {
        return false;
    }

    @Override
    public boolean isMemberExistBySerial(String serial) {
        return false;
    }

    @Override
    public void addBoat(String boatNameInput, BoatTypeEnum boatTypeInput, boolean isCoastalInput, boolean isWideInput, String serial) {

    }

    @Override
    public void addBoat(Boat boat) {

    }

    @Override
    public void addMember(Member member) {

    }

    @Override
    public void addMember(String name, String phone, String email, String password, int age, String additionalDetails, LevelEnum lvl, boolean isManager, String ID) {

    }

    @Override
    public void changePhoneNumber(Member member, String newPhone) {

    }

    @Override
    public void changeName(Member member, String newName) {

    }

    @Override
    public void changePassword(Member member, String newPassword) {

    }

    @Override
    public void changeEmail(Member member, String newEmail) throws EmailAlreadyExistException {

    }

    @Override
    public void updateMemberAge(Member member, int age) {

    }

    @Override
    public void updateMemberEndDate(Member member, int numberOfYears) {

    }

    @Override
    public void updateMemberLevel(Member member, LevelEnum level) {

    }

    @Override
    public void cancelMembersPrivateBoat(Member member) {

    }

    @Override
    public void updateBoatName(Boat boat, String name) {

    }

    @Override
    public void updateIsWide(Boat boat) {

    }

    @Override
    public void updateIsCoastal(Boat boat) {

    }

    @Override
    public void fixBoat(Boat boat) {

    }

    @Override
    public List<Member> getMemberList() {
        return null;
    }

    @Override
    public void disAbleBoat(Boat boat) {

    }

    @Override
    public void removeRegistrationRequestByMember(Registration registration) {

    }

    @Override
    public void addRowerToRegiRequest(Member member, Registration regiRequest) {

    }

    @Override
    public void removeRowerSpecificFromRegiRequest(Member member, Registration regiRequest, boolean toSplitRegistration) {

    }

    @Override
    public void addBoatTypeToRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {

    }

    @Override
    public void removeBoatTypeFromRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {

    }

    @Override
    public List<Registration> getHistoryRegistrationOfMember(Member member) {
        return null;
    }

    @Override
    public List<Registration> getFutureRegistrationOfMember(Member member) {
        return null;
    }

    @Override
    public boolean isRowerAllowToBeAddedToRegistration(LocalDate date, Member member, LocalTime startTime, LocalTime endTime) {
        return false;
    }

    @Override
    public void addPrivateBoat(Member member, String serialNumBoat) {

    }

    @Override
    public List<Registration> getRegiListConfirmedAccordingMember(Member member) {
        return null;
    }

    @Override
    public Members generateMembersToXml(XmlManagement xmlManagement) {
        return null;
    }

    @Override
    public Boats generateBoatsToXml(XmlManagement xmlManagement) {
        return null;
    }

    @Override
    public Activities generateActivitiesToXml(XmlManagement xmlManagement) {
        return null;
    }

    @Override
    public void cleanAllMembersBecauseImport() {

    }

    @Override
    public void cleanAllBoatsBecauseImport() {

    }

    @Override
    public void cleanAllWindowRegistarionBecauseImport() {

    }
}
