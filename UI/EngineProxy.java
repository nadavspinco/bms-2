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
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EngineProxy implements EngineInterface {
    private String host = "localhost";
    private int port = 1989;
    private Socket socket;
    private ObjectOutputStream out ;
    private ObjectInputStream in;

    public EngineProxy(String host, int port) {
       try {
           this.socket = new Socket(host,port);
           in = new ObjectInputStream(socket.getInputStream());
           out = new ObjectOutputStream(socket.getOutputStream());
       } catch(UnknownHostException e){
           e.getStackTrace();
       } catch (IOException e){
           e.getStackTrace();
       } catch (Exception e){
           System.out.println("kos emek ars");
       }
    }

    public EngineProxy(){
        try {
            this.socket = new Socket();
            in = (ObjectInputStream) socket.getInputStream();
            out = (ObjectOutputStream) socket.getOutputStream();
        }
        catch (IOException e){
            e.getStackTrace();
        }
    }

    private void sendRequest(ServerRequest serverRequest){
        try {
            out.writeObject(serverRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getAnswer(){
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
        try {
            ServerRequest request = new ServerRequest("isBoatIsPrivate",boatId);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return true; // if case of wrong answer from the server we decided the boat is private.
        }
    }
    // TODO מופעלת בזמן העלת המערכת, אין סיבה שהפרוקסי יתעסק בה
    @Override
    public void fixReferencesAfterImportInnerDetails() {
    }

    @Override
    public boolean isRegistrationAllowedForMember(Registration registration, Member member) {
        try {
            ServerRequest request = new ServerRequest("isRegistrationAllowedForMember",registration, member);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server we decided the registration isn't allowed for the member.
        }
    }

    @Override
    public boolean isRegistrationAllowed(Registration registration) {
        try {
            ServerRequest request = new ServerRequest("isRegistrationAllowed",registration);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server we decided the registration isn't allowed.
        }
    }

    @Override
    public Assignment[] getAssignmentForward(int numOfDays) {
        try {
            ServerRequest request = new ServerRequest("getAssignmentForward",numOfDays);
            sendRequest(request);
            return (Assignment[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void removeMemberFromAssigment(Assignment assignment, Member member, boolean toSplit) {
        ServerRequest request = new ServerRequest("removeMemberFromAssigment", assignment, member, toSplit);
        sendRequest(request);
    }

    @Override
    public Registration[] getValidRegistrationToUnion(Assignment assignment) {
        try {
            ServerRequest request = new ServerRequest("getValidRegistrationToUnion", assignment);
            sendRequest(request);
            return (Registration[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void unionRequestToAssignment(Assignment assignment, Registration registration) {
            ServerRequest request = new ServerRequest("unionRequestToAssignment", assignment, registration);
            sendRequest(request);
    }

    @Override
    public void removeAssignment(Assignment assignment, boolean toDeleteRegistration) {
        ServerRequest request = new ServerRequest("removeAssignment", assignment, toDeleteRegistration);
        sendRequest(request);
    }

    @Override
    public Boat[] getArrayOfValidBoats(Registration registration) {
        try {
            ServerRequest request = new ServerRequest("getArrayOfValidBoats", registration);
            sendRequest(request);
            return (Boat[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void assignBoat(Registration registration, Boat boat) {
        ServerRequest request = new ServerRequest("assignBoat", registration, boat);
        sendRequest(request);
    }

    @Override
    public boolean isAssigmentIsValidForMember(Registration registration, Member member) {
        try {
            ServerRequest request = new ServerRequest("isAssigmentIsValidForMember", registration, member);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server we decided the assignment isn't allowed.
        }
    }

    @Override
    public boolean isLegalAssigment(Registration registration, Boat boat) {
        try {
            ServerRequest request = new ServerRequest("isLegalAssigment", registration, boat);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server we decided the assignment isn't legal.
        }
    }

    @Override
    public Registration[] getMainRegistrationByDays(int numOfDays) {
        try {
            ServerRequest request = new ServerRequest("getMainRegistrationByDays", numOfDays);
            sendRequest(request);
            return (Registration[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public List<Registration> getRegistrationBySpecificDay(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getRegistrationBySpecificDay", date);
            sendRequest(request);
            return (List<Registration>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public List<Registration> getConfirmedRegistrationBySpecificDay(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getConfirmedRegistrationBySpecificDay", date);
            sendRequest(request);
            return (List<Registration>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Assignment[] getAssignmentByDate(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getAssignmentByDate", date);
            sendRequest(request);
            return (Assignment[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Boat getBoatById(String boatId) {
        try {
            ServerRequest request = new ServerRequest("getBoatById", boatId);
            sendRequest(request);
            return (Boat) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public WindowRegistration[] getWindowRegistrations() {
        try {
            ServerRequest request = new ServerRequest("getWindowRegistrations");
            sendRequest(request);
            return (WindowRegistration[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Boat[] getBoatArry() {
        try {
            ServerRequest request = new ServerRequest("getBoatArry");
            sendRequest(request);
            return (Boat[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Member[] getMemberArry() {
        try {
            ServerRequest request = new ServerRequest("getMemberArry");
            sendRequest(request);
            return (Member[]) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void deleteWindowRegistration(WindowRegistration windowRegistration) {
        ServerRequest request = new ServerRequest("deleteWindowRegistration", windowRegistration);
        sendRequest(request);
    }

    @Override
    public String createBoatCode(Boat boat) {
        try {
            ServerRequest request = new ServerRequest("createBoatCode", boat);
            sendRequest(request);
            return (String) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return ""; // if case of wrong answer from the server.
        }
    }

    @Override
    public void removeMember(Member member) {
        ServerRequest request = new ServerRequest("removeMember", member);
        sendRequest(request);
    }

    @Override
    public void removeBoat(Boat boat) {
        ServerRequest request = new ServerRequest("removeBoat", boat);
        sendRequest(request);
    }

    @Override
    public boolean isWindowRegistrationEmpty() {
        try {
            ServerRequest request = new ServerRequest("isWindowRegistrationEmpty");
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server we decided the window registration isn't empty.
        }
    }

    @Override
    public boolean isEmailAlreadyExist(String email) {
        try {
            ServerRequest request = new ServerRequest("isEmailAlreadyExist", email);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return true; // if case of wrong answer from the server we decided the email is already exist.
        }
    }

    @Override
    public void addWindowRegistration(WindowRegistration windowRegistration) {
        ServerRequest request = new ServerRequest("addWindowRegistration", windowRegistration);
        sendRequest(request);
    }

    @Override
    public Member loginMember(String emailInput, String passwordInput) {
        try {
            ServerRequest request = new ServerRequest("loginMember", emailInput, passwordInput);
            sendRequest(request);
            return (Member) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server we decided unsuccessful login.
        }
    }

    @Override
    public boolean isBoatExistBySerial(String boatSerial) {
        try {
            ServerRequest request = new ServerRequest("isBoatExistBySerial", boatSerial);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return true; // if case of wrong answer from the server we decided the boat is exist.
        }
    }

    @Override
    public boolean isMemberExistBySerial(String serial) {
        try {
            ServerRequest request = new ServerRequest("isMemberExistBySerial", serial);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return true; // if case of wrong answer from the server we decided the member is exist.
        }
    }

    // todo
    @Override
    public void addBoat(String boatNameInput, BoatTypeEnum boatTypeInput, boolean isCoastalInput, boolean isWideInput, String serial) {

    }

    @Override
    public void addBoat(Boat boat) {
        ServerRequest request = new ServerRequest("addBoat", boat);
        sendRequest(request);
   }

    @Override
    public void addMember(Member member) {
        ServerRequest request = new ServerRequest("addMember", member);
        sendRequest(request);
    }

    // todo
    @Override
    public void addMember(String name, String phone, String email, String password, int age, String additionalDetails, LevelEnum lvl, boolean isManager, String ID) {

    }

    @Override
    public void changePhoneNumber(Member member, String newPhone) {
        ServerRequest request = new ServerRequest("changePhoneNumber", member, newPhone);
        sendRequest(request);
    }

    @Override
    public void changeName(Member member, String newName) {
        ServerRequest request = new ServerRequest("changeName", member, newName);
        sendRequest(request);
    }

    @Override
    public void changePassword(Member member, String newPassword) {
        ServerRequest request = new ServerRequest("changePassword", member, newPassword);
        sendRequest(request);
    }

    @Override
    public void changeEmail(Member member, String newEmail) throws EmailAlreadyExistException {
        ServerRequest request = new ServerRequest("changeEmail", member, newEmail);
        sendRequest(request);
    }

    @Override
    public void updateMemberAge(Member member, int age) {
        ServerRequest request = new ServerRequest("updateMemberAge", member, age);
        sendRequest(request);
    }

    @Override
    public void updateMemberEndDate(Member member, int numberOfYears) {
        ServerRequest request = new ServerRequest("updateMemberEndDate", member, numberOfYears);
        sendRequest(request);
    }

    @Override
    public void updateMemberLevel(Member member, LevelEnum level) {
        ServerRequest request = new ServerRequest("updateMemberLevel", member, level);
        sendRequest(request);
    }

    @Override
    public void cancelMembersPrivateBoat(Member member) {
        ServerRequest request = new ServerRequest("cancelMembersPrivateBoat", member);
        sendRequest(request);
    }

    @Override
    public void updateBoatName(Boat boat, String name) {
        ServerRequest request = new ServerRequest("updateBoatName", boat, name);
        sendRequest(request);
    }

    @Override
    public void updateIsWide(Boat boat) {
        ServerRequest request = new ServerRequest("updateIsWide", boat);
        sendRequest(request);
    }

    @Override
    public void updateIsCoastal(Boat boat) {
        ServerRequest request = new ServerRequest("updateIsCoastal", boat);
        sendRequest(request);
    }

    @Override
    public void fixBoat(Boat boat) {
        ServerRequest request = new ServerRequest("fixBoat", boat);
        sendRequest(request);
    }

    @Override
    public List<Member> getMemberList() {
        try {
            ServerRequest request = new ServerRequest("getMemberList");
            sendRequest(request);
            return (List<Member>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void disAbleBoat(Boat boat) {
        ServerRequest request = new ServerRequest("disAbleBoat", boat);
        sendRequest(request);
    }

    @Override
    public void removeRegistrationRequestByMember(Registration registration) {
        ServerRequest request = new ServerRequest("removeRegistrationRequestByMember", registration);
        sendRequest(request);
    }

    @Override
    public void addRowerToRegiRequest(Member member, Registration regiRequest) {
        ServerRequest request = new ServerRequest("addRowerToRegiRequest", member, regiRequest);
        sendRequest(request);
    }

    @Override
    public void removeRowerSpecificFromRegiRequest(Member member, Registration regiRequest, boolean toSplitRegistration) {
        ServerRequest request = new ServerRequest("removeRowerSpecificFromRegiRequest", member, regiRequest, toSplitRegistration);
        sendRequest(request);

    }

    @Override
    public void addBoatTypeToRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {
        ServerRequest request = new ServerRequest("addBoatTypeToRegiRequest", boatType, regiRequest);
        sendRequest(request);
    }

    @Override
    public void removeBoatTypeFromRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {
        ServerRequest request = new ServerRequest("removeBoatTypeFromRegiRequest", boatType, regiRequest);
        sendRequest(request);
    }

    @Override
    public List<Registration> getHistoryRegistrationOfMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getHistoryRegistrationOfMember", member);
            sendRequest(request);
            return (List<Registration>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public List<Registration> getFutureRegistrationOfMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getFutureRegistrationOfMember", member);
            sendRequest(request);
            return (List<Registration>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public boolean isRowerAllowToBeAddedToRegistration(LocalDate date, Member member, LocalTime startTime, LocalTime endTime) {
        try {
            ServerRequest request = new ServerRequest("isRowerAllowToBeAddedToRegistration", date, member, startTime, endTime);
            sendRequest(request);
            return (boolean) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return false; // if case of wrong answer from the server the rower isn't allowed to add the registration.
        }
    }

    @Override
    public void addPrivateBoat(Member member, String serialNumBoat) {
        ServerRequest request = new ServerRequest("addPrivateBoat", member, serialNumBoat);
        sendRequest(request);
    }

    @Override
    public List<Registration> getRegiListConfirmedAccordingMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getRegiListConfirmedAccordingMember", member);
            sendRequest(request);
            return (List<Registration>) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Members generateMembersToXml(XmlManagement xmlManagement) {
        try {
            ServerRequest request = new ServerRequest("generateMembersToXml", xmlManagement);
            sendRequest(request);
            return (Members) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Boats generateBoatsToXml(XmlManagement xmlManagement) {
        try {
            ServerRequest request = new ServerRequest("generateBoatsToXml", xmlManagement);
            sendRequest(request);
            return (Boats) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public Activities generateActivitiesToXml(XmlManagement xmlManagement) {
        try {
            ServerRequest request = new ServerRequest("generateActivitiesToXml", xmlManagement);
            sendRequest(request);
            return (Activities) getAnswer();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        finally {
            return null; // if case of wrong answer from the server.
        }
    }

    @Override
    public void cleanAllMembersBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllMembersBecauseImport");
        sendRequest(request);
    }

    @Override
    public void cleanAllBoatsBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllBoatsBecauseImport");
        sendRequest(request);
    }

    @Override
    public void cleanAllWindowRegistarionBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllWindowRegistarionBecauseImport");
        sendRequest(request);
    }
}
