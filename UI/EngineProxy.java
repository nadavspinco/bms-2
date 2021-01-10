package UI;

import Logic.*;
import Logic.Enum.BoatTypeEnum;
import Logic.Enum.LevelEnum;
import Logic.Objects.*;
import Logic.jaxb.Activities;
import Logic.jaxb.Boats;
import Logic.jaxb.Members;
import Server.ServerRequest;
import Server.ServerResponse;

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
    private int port = 1888;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public EngineProxy(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, 1989);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            e.getStackTrace();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void sendRequest(ServerRequest serverRequest) {
        try {
            out.writeObject(serverRequest);
//            System.out.println("send method: " + serverRequest.getMethod()); TODO

        } catch (IOException e) {
            throw new ConnectionLostException("connection is lost",e);

        }
    }

    private ServerResponse getServerResponse() {
        Object obj = null;
        ServerResponse response = null;
        try {
            while ((obj = in.readObject()) != null) {
                if (obj instanceof ServerResponse) {
                    response = (ServerResponse) obj;
                    if (response.isSucceed() == false) {
                        response = null;
                    }
//                    System.out.println(response.getReturnValue() + "in server response while"); TODO
                }
//                System.out.println(obj); TODO
                break;
            }

        } catch (IOException e) {
            throw new ConnectionLostException("connection is lost",e);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public List<Boat> getBoatList() {
        try {
            ServerRequest request = new ServerRequest("getBoatList");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            return (List<Boat>) response.getReturnValue();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Registration[] getRegistrationByMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getRegistrationByMember", member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            Registration[] a = (Registration[]) response.getReturnValue();
            if (a == null)  // TODO
                System.out.println("we got null in proxy");
            System.out.println(a.length + " in proxy before return");
            return a;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<WindowRegistration> getWindowRegistrationList() {
        try {
            ServerRequest request = new ServerRequest("getWindowRegistrationList");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            return (List<WindowRegistration>) response.getReturnValue();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isBoatIsPrivate(String boatId) {
        try {
            ServerRequest request = new ServerRequest("isBoatIsPrivate", boatId);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return true; // in case of wrong answer from the server we decided the boat is private.
    }

    // TODO מופעלת בזמן העלת המערכת, אין סיבה שהפרוקסי יתעסק בה
    @Override
    public void fixReferencesAfterImportInnerDetails() {
        //TODO:
    }

    @Override
    public boolean isRegistrationAllowedForMember(Registration registration, Member member) {
        try {
            ServerRequest request = new ServerRequest("isRegistrationAllowedForMember", registration, member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server we decided the registration isn't allowed for the member.
    }

    @Override
    public boolean isRegistrationAllowed(Registration registration) {
        try {
            ServerRequest request = new ServerRequest("isRegistrationAllowed", registration);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server we decided the registration isn't allowed.
    }

    @Override
    public Assignment[] getAssignmentForward(int numOfDays) {
        try {
            ServerRequest request = new ServerRequest("getAssignmentForward", numOfDays);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Assignment[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public void removeMemberFromAssigment(Assignment assignment, Member member, boolean toSplit) {
        ServerRequest request = new ServerRequest("removeMemberFromAssigment", assignment, member, toSplit);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public Registration[] getValidRegistrationToUnion(Assignment assignment) {
        try {
            ServerRequest request = new ServerRequest("getValidRegistrationToUnion", assignment);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Registration[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public void unionRequestToAssignment(Assignment assignment, Registration registration) {
        ServerRequest request = new ServerRequest("unionRequestToAssignment", assignment, registration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void removeAssignment(Assignment assignment, boolean toDeleteRegistration) {
        ServerRequest request = new ServerRequest("removeAssignment", assignment, toDeleteRegistration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public Boat[] getArrayOfValidBoats(Registration registration) {
        try {
            ServerRequest request = new ServerRequest("getArrayOfValidBoats", registration);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Boat[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public void assignBoat(Registration registration, Boat boat) throws InvalidAssignmentException {
        ServerRequest request = new ServerRequest("assignBoat", registration, boat);
        sendRequest(request);
       ServerResponse response =  getServerResponse(); //clean ObjectInputStream
        if(response!=null && response.isSucceed() == false){
            if(response.getReturnValue() instanceof InvalidAssignmentException){
                InvalidAssignmentException e = (InvalidAssignmentException) response.getReturnValue();
                throw e;
            }
        }
    }


    @Override
    public boolean isAssigmentIsValidForMember(Registration registration, Member member) {
        try {
            ServerRequest request = new ServerRequest("isAssigmentIsValidForMember", registration, member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server we decided the assignment isn't allowed.
    }

    @Override
    public boolean isLegalAssigment(Registration registration, Boat boat) {
        try {
            ServerRequest request = new ServerRequest("isLegalAssigment", registration, boat);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server we decided the assignment isn't legal.
    }

    @Override
    public Registration[] getMainRegistrationByDays(int numOfDays) {
        try {
            ServerRequest request = new ServerRequest("getMainRegistrationByDays", numOfDays);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Registration[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public List<Registration> getRegistrationBySpecificDay(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getRegistrationBySpecificDay", date);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Registration>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public List<Registration> getConfirmedRegistrationBySpecificDay(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getConfirmedRegistrationBySpecificDay", date);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Registration>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Assignment[] getAssignmentByDate(LocalDate date) {
        try {
            ServerRequest request = new ServerRequest("getAssignmentByDate", date);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Assignment[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Boat getBoatById(String boatId) {
        try {
            ServerRequest request = new ServerRequest("getBoatById", boatId);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Boat) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public WindowRegistration[] getWindowRegistrations() {
        try {
            ServerRequest request = new ServerRequest("getWindowRegistrations");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (WindowRegistration[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Boat[] getBoatArry() {
        try {
            ServerRequest request = new ServerRequest("getBoatArry");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Boat[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Member[] getMemberArry() {
        Member[] members = null;
        try {
            ServerRequest request = new ServerRequest("getMemberArry");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                System.out.println("not null resposnse!!");
                if (response.getReturnValue() != null)
                    System.out.println("not null return value");

                System.out.println(response.getReturnValue().getClass());
                members = (Member[]) response.getReturnValue();
                System.out.println("after casting");
                System.out.println(members);
                return members;
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.out.println("ClassCastException");
        } catch (Exception e) {
            System.out.println("caught generel ");
            System.out.println(e);
        }
        return members;
    }

    @Override
    public void deleteWindowRegistration(WindowRegistration windowRegistration) {
        ServerRequest request = new ServerRequest("deleteWindowRegistration", windowRegistration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public String createBoatCode(Boat boat) {
        try {
            ServerRequest request = new ServerRequest("createBoatCode", boat);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return ""; // in case of wrong answer from the server.
    }

    @Override
    public void removeMember(Member member) {
        ServerRequest request = new ServerRequest("removeMember", member);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void removeBoat(Boat boat) {
        ServerRequest request = new ServerRequest("removeBoat", boat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public boolean isWindowRegistrationEmpty() {
        try {
            ServerRequest request = new ServerRequest("isWindowRegistrationEmpty");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server we decided the window registration isn't empty.
    }

    @Override
    public boolean isEmailAlreadyExist(String email) {
        try {
            ServerRequest request = new ServerRequest("isEmailAlreadyExist", email);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return true; // in case of wrong answer from the server we decided the email is already exist.
    }

    @Override
    public boolean isMemberAlreadyLoggedIn(String emailInput) {
        try {
            ServerRequest request = new ServerRequest("isMemberAlreadyLoggedIn", emailInput);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return true; //if the response return false on response valid we want to not allow the user to login
    }

    @Override
    public void logout(Member member) {
        System.out.println("here!!!!!!");
        if(socket.isClosed() ){
            try {
                socket = new Socket(host,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ServerRequest request = new ServerRequest("logout", member);
        sendRequest(request);
        ServerResponse response = getServerResponse();
    }

    @Override
    public void addWindowRegistration(WindowRegistration windowRegistration) {
        ServerRequest request = new ServerRequest("addWindowRegistration", windowRegistration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public Member loginMember(String emailInput, String passwordInput) {
        try {
            ServerRequest request = new ServerRequest("loginMember", emailInput, passwordInput);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Member) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server we decided unsuccessful login.
    }

    @Override
    public boolean isBoatExistBySerial(String boatSerial) {
        try {
            ServerRequest request = new ServerRequest("isBoatExistBySerial", boatSerial);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return true; // in case of wrong answer from the server we decided the boat is exist.
    }

    @Override
    public void addRegistration(Registration registration, boolean assignPrivateBoutIfExists) throws InvalidRegistrationException {
        ServerRequest request = new ServerRequest("addRegistration", registration, assignPrivateBoutIfExists);
        sendRequest(request);
        ServerResponse response=getServerResponse();
        if(response!= null&& response.isSucceed() == false){
            if(response.getReturnValue() instanceof InvalidRegistrationException){
                InvalidRegistrationException e = (InvalidRegistrationException) response.getReturnValue();
                throw e;
            }
    //clean ObjectInputStream
        }
    }

    @Override
    public boolean isMemberExistBySerial(String serial) {
        try {
            ServerRequest request = new ServerRequest("isMemberExistBySerial", serial);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return true; // in case of wrong answer from the server we decided the member is exist.
    }

    @Override
    public void addBoat(String boatNameInput, BoatTypeEnum boatTypeInput, boolean isCoastalInput, boolean isWideInput, String serial) {
        ServerRequest request = new ServerRequest("addBoat", boatNameInput, boatTypeInput, isCoastalInput, isWideInput, serial);
        sendRequest(request);
        getServerResponse();
    }

    @Override
    public void addBoat(Boat boat) {
        ServerRequest request = new ServerRequest("addBoat", boat);
        sendRequest(request);
        getServerResponse();//clean ObjectInputStream
    }

    @Override
    public void addMember(Member member) {
        ServerRequest request = new ServerRequest("addMember", member);
        sendRequest(request);
        getServerResponse();//clean ObjectInputStream

    }

    @Override
    public void addMember(String name, String phone, String email, String password, int age, String additionalDetails, LevelEnum lvl, boolean isManager, String ID) {
        ServerRequest request = new ServerRequest("addMember", name, phone, email, password, age, additionalDetails, lvl, isManager, ID);
        sendRequest(request);
        getServerResponse();//clean ObjectInputStream
    }

    @Override
    public void changePhoneNumber(Member member, String newPhone) {
        ServerRequest request = new ServerRequest("changePhoneNumber", member, newPhone);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void changeName(Member member, String newName) {
        ServerRequest request = new ServerRequest("changeName", member, newName);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void changePassword(Member member, String newPassword) {
        ServerRequest request = new ServerRequest("changePassword", member, newPassword);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void changeEmail(Member member, String newEmail) throws EmailAlreadyExistException {
        ServerRequest request = new ServerRequest("changeEmail", member, newEmail);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateMemberAge(Member member, int age) {
        ServerRequest request = new ServerRequest("updateMemberAge", member, age);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateMemberEndDate(Member member, int numberOfYears) {
        ServerRequest request = new ServerRequest("updateMemberEndDate", member, numberOfYears);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateMemberLevel(Member member, LevelEnum level) {
        ServerRequest request = new ServerRequest("updateMemberLevel", member, level);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void cancelMembersPrivateBoat(Member member) {
        ServerRequest request = new ServerRequest("cancelMembersPrivateBoat", member);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateBoatName(Boat boat, String name) {
        ServerRequest request = new ServerRequest("updateBoatName", boat, name);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateIsWide(Boat boat) {
        ServerRequest request = new ServerRequest("updateIsWide", boat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void updateIsCoastal(Boat boat) {
        ServerRequest request = new ServerRequest("updateIsCoastal", boat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void fixBoat(Boat boat) {
        ServerRequest request = new ServerRequest("fixBoat", boat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public List<Member> getMemberList() {
        try {
            ServerRequest request = new ServerRequest("getMemberList");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Member>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public void disAbleBoat(Boat boat) {
        ServerRequest request = new ServerRequest("disAbleBoat", boat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void removeRegistrationRequestByMember(Registration registration) {
        ServerRequest request = new ServerRequest("removeRegistrationRequestByMember", registration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void addRowerToRegiRequest(Member member, Registration regiRequest) {
        ServerRequest request = new ServerRequest("addRowerToRegiRequest", member, regiRequest);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void removeRowerSpecificFromRegiRequest(Member member, Registration regiRequest, boolean toSplitRegistration) {
        ServerRequest request = new ServerRequest("removeRowerSpecificFromRegiRequest", member, regiRequest, toSplitRegistration);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream

    }

    @Override
    public void addBoatTypeToRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {
        ServerRequest request = new ServerRequest("addBoatTypeToRegiRequest", boatType, regiRequest);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void removeBoatTypeFromRegiRequest(BoatTypeEnum boatType, Registration regiRequest) {
        ServerRequest request = new ServerRequest("removeBoatTypeFromRegiRequest", boatType, regiRequest);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public List<Registration> getHistoryRegistrationOfMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getHistoryRegistrationOfMember", member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Registration>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public List<Registration> getFutureRegistrationOfMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getFutureRegistrationOfMember", member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Registration>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public boolean isRowerAllowToBeAddedToRegistration(LocalDate date, Member member, LocalTime startTime, LocalTime endTime) {
        try {
            ServerRequest request = new ServerRequest("isRowerAllowToBeAddedToRegistration", date, member, startTime, endTime);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (boolean) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false; // in case of wrong answer from the server the rower isn't allowed to add the registration.
    }

    @Override
    public void addPrivateBoat(Member member, String serialNumBoat) {
        ServerRequest request = new ServerRequest("addPrivateBoat", member, serialNumBoat);
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public List<Registration> getRegiListConfirmedAccordingMember(Member member) {
        try {
            ServerRequest request = new ServerRequest("getRegiListConfirmedAccordingMember", member);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Registration>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Members generateMembersToXml() {
        try {
            ServerRequest request = new ServerRequest("generateMembersToXml");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Members) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Boats generateBoatsToXml() {
        try {
            ServerRequest request = new ServerRequest("generateBoatsToXml");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Boats) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public Activities generateActivitiesToXml() {
        try {
            ServerRequest request = new ServerRequest("generateActivitiesToXml");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (Activities) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    @Override
    public void cleanAllMembersBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllMembersBecauseImport");
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void cleanAllBoatsBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllBoatsBecauseImport");
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    @Override
    public void cleanAllWindowRegistarionBecauseImport() {
        ServerRequest request = new ServerRequest("cleanAllWindowRegistarionBecauseImport");
        sendRequest(request);
        getServerResponse(); //clean ObjectInputStream
    }

    //  --------------------------------------------XML
    @Override
    public String[] convertBoatsFromXml(String memberDetailsString, boolean toDelete) {
        try {
            ServerRequest request = new ServerRequest("convertBoatsFromXml", memberDetailsString, toDelete);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public String[] convertMembersFromXml(String memberDetailsString, boolean toDelete) {
        try {
            ServerRequest request = new ServerRequest("convertMembersFromXml", memberDetailsString, toDelete);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public String[] convertWindowsFromXml(String activitiesDetailsString, boolean toDelete) {
        try {
            ServerRequest request = new ServerRequest("convertWindowsFromXml", activitiesDetailsString, toDelete);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String[]) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public String exportMembersToString(){
        try {
            ServerRequest request = new ServerRequest("exportMembersToString");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public String exportBoatsToString(){
        try {
            ServerRequest request = new ServerRequest("exportBoatsToString");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public String exportActivitiesToString(){
        try {
            ServerRequest request = new ServerRequest("exportActivitiesToString");
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (String) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }

    public List<Member> memberPartnersSuggestion( Member mainRower) {
        try {
            ServerRequest request = new ServerRequest("memberPartnersSuggestion", mainRower);
            sendRequest(request);
            ServerResponse response = getServerResponse();
            if (response != null) {
                return (List<Member>) response.getReturnValue();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null; // in case of wrong answer from the server.
    }
}