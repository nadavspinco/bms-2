package UI.Menus;

import Logic.Objects.*;
import Logic.*;
import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;
import Logic.jaxb.Activities;
import Logic.jaxb.Boats;
import Logic.jaxb.Members;
import Logic.jaxb.Timeframe;
import UI.CreatorUI;
import UI.Enum.*;
import UI.ObjectsUpdater;
import UI.Tools.Messager;
import UI.Tools.Validator;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static UI.Enum.SubMenuAssignment.*;

public class ManagerMenu extends MenuBase {
    private ManagerMenuOptionEnum optionChosen;

    public static Scanner input = new Scanner(System.in);

    public ManagerMenu(SystemManagement systemManagement, Member member) {
        super(systemManagement, member);
    }

    public void managerMenuRun() {
        int chosenOption;
        boolean keepRunning = false;
        while (!keepRunning) {

            chosenOption = Validator.getIntBetween(1, 9, Messager.managerMenuPrintMessage());
            optionChosen = ManagerMenuOptionEnum.convertFromInt(chosenOption);
            keepRunning = (optionChosen == ManagerMenuOptionEnum.Exit);
            if (!keepRunning)
                mainManagerMenu(optionChosen);
        }
        xmlManagement.exportSystemManagementDetails(systemManagement);
    }

    private void mainManagerMenu(ManagerMenuOptionEnum optionChosen) {
        int chosenOption;
        switch (optionChosen) {
            case ManageBoatList: {
                SubMenuManageBoatListEnum subMenuOption;
                chosenOption = Validator.getIntBetween(1, 5, Messager.chooseSubMenuManageBoatListMessage());
                subMenuOption = SubMenuManageBoatListEnum.convertFromInt(chosenOption);
                manageBoatListSwitcher(subMenuOption);
                break;
            }
            case ManageMemberList: {
                SubMenuManageMemberListEnum subMenuOption;
                chosenOption = Validator.getIntBetween(1, 5, Messager.chooseSubMenuManageMemberListMessage());
                subMenuOption = SubMenuManageMemberListEnum.convertFromInt(chosenOption);
                manageMemberListSwitcher(subMenuOption);
                break;
            }
            case ManageRegistrationWindow: {
                mangeRegistrationWindow();
                break;
            }
            case ManageRegistrationRequest: {
                manageRegistrationRequestByManager();
                break;
            }
            case ShowAllRegistrationNeedToSchedule: {
                showRegistrationRequest();
                break;
            }
            case ShowAllConfirmRegistrationRequest: {
                showConfirmedRegistrationByPredicated();
                break;
            }
            case AssignmentSubMenu: {
                runAssignmentSubMenu();
                break;
            }
            case xmlMenu: {
                xmlMenu();
                break;
            }
            default:
                break;
        }
    }

    public void runAssignmentSubMenu() {
        SubMenuAssignment subMenuAssignment;
        do {
            System.out.println(String.format("%s\n%s\n%s\n%s\n%s\n%s\n",
                    "1.Union Assignment and request",
                    "2.Show Assigment By Date",
                    "3.Remove Assignment",
                    "4.Remove Rowar form Assignment ",
                    "5.assign Boat To Registration",
                    "6.Exit"));

            subMenuAssignment = getSubMenuAssignmentSelection();
            performAssignmentSubMenu(subMenuAssignment);
        } while (subMenuAssignment != Exit);
    }

    private SubMenuAssignment getSubMenuAssignmentSelection() {
        return SubMenuAssignment.convertFromInt(Validator.getIntBetween(1, 6, "please make a selection"));
    }

    public void performAssignmentSubMenu(SubMenuAssignment subMenuAssignment) {
        switch (subMenuAssignment) {
            case UnionAssignmentAndRegistration:
                unionAssignmentAndRegistration();
                break;
            case ShowAssigmentByDate:
                showAssignmentsByDate ();
                break;
            case RemoveAssignment:

                removeAssignment();
                break;
            case DeleteRowerFromAssignment:
                deleteRowerFromAssignment();
                break;
            case AssignBoat:
                assignBoatToRegistration();
                break;
        }
    }

    private void showAssignmentsByDate() {
        LocalDate date = CreatorUI.choiceDateForRegistrationRequest();
        Assignment [] assignmentsByDate =  systemManagement.getAssignmentByDate(date);
        showAssignments(assignmentsByDate);
    }

    private void deleteRowerFromAssignment() {
        LocalDate date = CreatorUI.choiceDateForRegistrationRequest();
        Assignment[] assignments = systemManagement.getAssignmentByDate(date);
        if(assignments== null || assignments.length == 0) {
            System.out.println("no Assignments found on this day");
        }
        else {
            showAssignments(assignments);
            Assignment chosenAssiment = choseAssignment(assignments);
            List<Member> assigmentMember = chosenAssiment.getRegistration().getRowersListInBoat();
            if(assigmentMember != null && assigmentMember.size() > 1){
                showMembers(assigmentMember);
                Member chosenMember =choseMember(assigmentMember);
                boolean  toSplitRequest = Validator.trueOrFalseAnswer("keep this request for the rowar?");
                boolean toKeepChanges = Validator.trueOrFalseAnswer("to keep changes?");
                if(toKeepChanges){
                    systemManagement.removeMemberFromAssigment( chosenAssiment, chosenMember,toSplitRequest);
                }
            }
            else
            {
                if(assigmentMember != null &&assigmentMember.size() == 1){
                    System.out.println("only one member, remove is not Rowar is not possible");
                }
            }
        }
    }

    private void unionAssignmentAndRegistration() {
        LocalDate date = CreatorUI.choiceDateForRegistrationRequest();
        Assignment[] assignmentsByDate = systemManagement.getAssignmentByDate(date);
        if (assignmentsByDate != null && assignmentsByDate.length != 0) {
            Assignment chosenAssigment = choseAssignment(assignmentsByDate);
            Registration[] registrationsOptions = systemManagement.getValidRegistrationToUnion(chosenAssigment);
            if (registrationsOptions.length != 0) {
                ShowRegistrationRequest(registrationsOptions);
                int selectedRegistration = Validator.getIntBetween(1, registrationsOptions.length, "please chose");
                boolean toKeepChanges= Validator.trueOrFalseAnswer("to keep changes");
                if(toKeepChanges) {
                    systemManagement.unionRequestToAssignment(chosenAssigment, registrationsOptions[selectedRegistration - 1]);
                }
            } else {
                System.out.println("no Registration found that are valid to union");
            }
        } else {
            System.out.println("no Assignment found on this day");
        }
    }

    private Assignment choseAssignment(Assignment[] assignments) {
        showAssignments(assignments);
        int chosenIndex = Validator.getIntBetween(1, assignments.length, "please make a selction\n");
        return assignments[chosenIndex - 1];
    }

    private void removeAssignment() {
        LocalDate date = CreatorUI.choiceDateForRegistrationRequest();
        Assignment[] assignmentsByDate = systemManagement.getAssignmentByDate(date);
        if (assignmentsByDate != null && assignmentsByDate.length != 0) {
            Assignment chosenAssignment = choseAssignment(assignmentsByDate);
            boolean toRemoveRegistration = Validator.trueOrFalseAnswer("to Remove Registration also?\n ");
            boolean toRemove = Validator.trueOrFalseAnswer("are you sure you want to save the canges\n? ");
            if (toRemove) {
                systemManagement.removeAssignment(chosenAssignment, toRemoveRegistration);
            }
        }
        else {
            System.out.println("no Assignment found on this day");
        }
    }

    private void showAssignmentByDate(LocalDate date) {
        Assignment[] assignmentsByDate = systemManagement.getAssignmentByDate(date);
        if (assignmentsByDate == null || assignmentsByDate.length == 0) {
            System.out.println("no Assignment found on this day");
        } else {
            showAssignments(assignmentsByDate);
        }
    }

    private void showAssignments(Assignment[] assignments) {
        int iAssignment = 1;
        if (assignments == null){
            System.out.println("There are no assigment in this day.");
            return;
        }
        for (Assignment assignment : assignments) {
            System.out.println("---------------------------------");
            System.out.println(String.format("%d.\n", iAssignment));
            showAssignment(assignment);
            System.out.println("---------------------------------");
            iAssignment++;
        }
    }

    private void showAssignment(Assignment assignment) {
        printRegistration(assignment.getRegistration());
        System.out.println(getBoatDetails(assignment.getBoat()));
    }

    private void assignBoatToRegistration() {
        Registration[] registrations = systemManagement.getMainRegistrationByDays(7);
        if (registrations==null || registrations.length == 0) {
            System.out.println("no Registration found ");
        }
        else {
            ShowRegistrationRequest(registrations);
            int registrationSelection = getRegistrationSelection(registrations.length);
            Boat[] legalBoats =
                    systemManagement.getArrayOfValidBoats(registrations[registrationSelection - 1]);
            if (legalBoats.length == 0) {
                System.out.println("no legal found boats for this Registration");
            }
            else {
                showBoatsArray(legalBoats);
                int selectedBoat = Validator.getIntBetween(1, legalBoats.length, "please select a bout");
                boolean toSaveChanges = Validator.trueOrFalseAnswer("to save changes?");
                if (toSaveChanges) {
                    systemManagement.assignBoat(registrations[registrationSelection - 1], legalBoats[selectedBoat - 1]);
                }
            }
        }
    }

    private int getRegistrationSelection(int numOfRegistration) {
        return Validator.getIntBetween(1, numOfRegistration, "choose from the following Registration ");
    }



    private void manageBoatListSwitcher(SubMenuManageBoatListEnum option) {
        CreatorUI creator = new CreatorUI(systemManagement);
        ObjectsUpdater updater = new ObjectsUpdater(systemManagement, this);
        switch (option) {
            case addBoat: {
                creator.createBoat();
                break;
            }
            case removeBoat: {
                Boat boat = whatBoatToActWith("remove");
                if(boat == null){
                    return;
                }
                boolean toDelete = Validator.trueOrFalseAnswer("save changes? all future assignment will be deleted!");
                if(toDelete) {
                    systemManagement.removeBoat(boat);
                }
                break;
            }
            case updateBoat: {
                updater.updaterBoat();
                break;
            }
            case showAllBoats: {
                showAllBoatsToScreen();
                break;
            }
            default:
                break;
        }
    }

    private void manageMemberListSwitcher(SubMenuManageMemberListEnum option) {
        CreatorUI creator = new CreatorUI(systemManagement);
        ObjectsUpdater updater = new ObjectsUpdater(systemManagement, this);
        switch (option) {
            case addMember: {
                try {
                    creator.createMember();
                } catch (EmailAlreadyExistException e) {
                    System.out.println(e.getMessage());
                }
                break;
            }
            case removeMember: {
              removeMember();
              break;
            }
            case updateMember: {
                updater.updaterMember();
                break;
            }
            case showAllMembers: {
                showAllMembersToScreen();
                break;
            }
            default:
                break;
        }
    }

    private void removeMember() {
        Member memberToDelete = whatMemberToActWith("remove");
        if(memberToDelete.equals(member)){
            System.out.println("cant delete the current memebr!!!");
            return;
        }
        boolean toKeep = Validator.trueOrFalseAnswer("are you sure you want to delete this memebr? ");
        if(toKeep) {
            systemManagement.removeMember(memberToDelete);
        }
    }

    public void showAllMembersToScreen() {
        Member[] membersArr = systemManagement.getMemberArry();
        for (int i = 0; i < membersArr.length; i++)
            System.out.println((i + 1) + ". " + showMemberDetails(membersArr[i]));
    }

    private void showBoatsArray(Boat[] boats) {
        int iBoat = 1;
        for (Boat boat : boats) {
            System.out.println((iBoat) + ". " + getBoatDetails(boat));
            iBoat++;
        }
    }

    private void showAllBoatsToScreen() {

        Boat[] boatsArr = systemManagement.getBoatArry();
        for (int i = 0; i < boatsArr.length; i++)
            System.out.println((i + 1) + ". " + getBoatDetails(boatsArr[i]));
    }

    public Boat whatBoatToActWith(String actMsg) {

        List<Boat> boatsArr = systemManagement.getBoatList();
        if(boatsArr == null || boatsArr.size() == 0){
            System.out.println("no boats found\n");
            return null;
        }

        System.out.println(String.format("Choose what boat you want to %s by the number near to.", actMsg));
        showAllBoatsToScreen();
        int numberIndex = Validator.getIntBetween(1, boatsArr.size(), "");
        return boatsArr.get(numberIndex - 1);
    }

    public Member whatMemberToActWith(String actMsg) {
        List<Member> membersArr = systemManagement.getMemberList();
        System.out.println(String.format("Choose what Member you want to %s by the number near to.", actMsg));
        showAllMembersToScreen();
        int numberIndex = Validator.getIntBetween(1, membersArr.size(), "");
        return membersArr.get(numberIndex - 1);
    }

    private void mangeRegistrationWindow() {
        RegistrationWindowMenuEnum registrationWindowMenuSelection;
        do {
            System.out.println(Messager.getMangeRegistrationWindowMenu());
            registrationWindowMenuSelection = getRegistrationWindowMenuSelection();
            executeRegistrationWindowMenuSelection(registrationWindowMenuSelection);
        } while (registrationWindowMenuSelection != RegistrationWindowMenuEnum.back);
    }

    private void executeRegistrationWindowMenuSelection(RegistrationWindowMenuEnum registrationWindowMenuSelection) {
        switch (registrationWindowMenuSelection) {
            case addRegistrationWindow:
                addNewRegistrationWindow();
                break;
            case editRegistrationWindow:
                editRegistrationWindow();
                break;
            case deleteRegistrationWindow:
                deleteRegistrationWindow();
                break;
            case showAllRegistrationWindow:
                showAllRegistrationWindow();
                break;
            case back:
        }
    }

    private void deleteRegistrationWindow() {
        WindowRegistration[] windowRegistrations = systemManagement.getWindowRegistrations();
        showRegistrationWindow(windowRegistrations);
        WindowRegistration windowRegistration = selectWindowRegistration(windowRegistrations);
        if (windowRegistration != null) {
            showRegistrationWindowByDetail(windowRegistration);
            int toDelete = Validator.getIntBetween(1, 2, "are you sure you want to delete this window? \n1.Yes 2.No\n");
            if (toDelete == 1)
                systemManagement.deleteWindowRegistration(windowRegistration);
            else
                System.out.println("nothing deleted!");
        }
    }

    private void editRegistrationWindow() {
        WindowRegistration[] windowRegistrations = systemManagement.getWindowRegistrations();
        showRegistrationWindow(windowRegistrations);
        WindowRegistration windowRegistration = selectWindowRegistration(windowRegistrations);
        if (windowRegistration != null) {
            editSelectedRegistrationWindow(windowRegistration);
        }
    }

    private void editSelectedRegistrationWindow(WindowRegistration windowRegistration) {
        ActivityTypeEnum activityType;
        BoatTypeEnum boatType;
        LocalTime startTime, endTime;
        boolean toUpdateWindowRegistration = false, keepRunning = true;

        boolean toEditActivityType = Validator.trueOrFalseAnswer("would you like to edit the ActivityType?");
        if (toEditActivityType) {
            activityType = Validator.getActivityType();
            toUpdateWindowRegistration = true;
        }
        else {
            activityType = windowRegistration.getActivityType();
        }
        boolean toEditBoatType = Validator.trueOrFalseAnswer( "would you like to edit the BoatType?");
        if (toEditBoatType) {
            boatType = Validator.getBoatType();
            toUpdateWindowRegistration = true;
        } else
            boatType = windowRegistration.getBoatType();

        boolean toEditTime = Validator.trueOrFalseAnswer("would you like to edit start and end time?");
        if (toEditTime) {
            do {
                startTime = Validator.getTime("start");
                endTime = Validator.getTime("end");
                toUpdateWindowRegistration = true;
                if(!startTime.isBefore(endTime))
                    System.out.println("The \"start time\" should starts before \"end time\", try again.");
                else
                    keepRunning = false;
            }while (keepRunning);
        }
        else {
            startTime = windowRegistration.getStartTime();
            endTime = windowRegistration.getEndTime();
        }

        if (toUpdateWindowRegistration) {
            boolean toSaveChanges = Validator.trueOrFalseAnswer("to save changes?");
            if (toSaveChanges) {
                WindowRegistration updatedWindow = new WindowRegistration(activityType, boatType, startTime, endTime);
                systemManagement.deleteWindowRegistration(windowRegistration);
                systemManagement.addWindowRegistration(updatedWindow);
            } else {
                System.out.println("nothing changed!");
            }
        }
    }

    public void showRegistrationRequest() {
        int answer;
        String msg = "Do you want to sort the registrations request according to: \n1. Next 7 days. \n2. Specific day.\n";
        answer = Validator.getIntBetween(1, 2, msg);
        if (answer == 1)
            showNextSevenDaysRegistration();
        else   // by specific day
            showRegistrationsBySpecificDay();
    }

    public void showConfirmedRegistrationByPredicated() {
        int answer;
        String msg = "Do you want to sort the confirmed-registrations according to: \n1. Next 7 days. \n2. Specific day.\n";
        answer = Validator.getIntBetween(1, 2, msg);
        if (answer == 1)
            showNextSevenDaysConfirmedRegistration();
        else   // by specific day
            showConfirmedRegistrationsBySpecificDay();
    }

    public void showConfirmedRegistrationsBySpecificDay() {     // confirmed registration
        LocalDate dateOfRegistration;
        List<Registration> registrationList;
        dateOfRegistration = CreatorUI.choiceDateForRegistrationRequest();
        registrationList = systemManagement.getConfirmedRegistrationBySpecificDay(dateOfRegistration);
        if (registrationList != null)
            registrationList.forEach(registration -> printRegistration(registration));
        else
            System.out.println("There are no assignment in this day.");
    }

    public void showRegistrationsBySpecificDay() {
        LocalDate dateOfRegistration;
        List<Registration> registrationList;
        dateOfRegistration = CreatorUI.choiceDateForRegistrationRequest();
        registrationList = systemManagement.getRegistrationBySpecificDay(dateOfRegistration);
        if (registrationList != null)
            registrationList.forEach(registration -> printRegistration(registration));
        else
            System.out.println("There are no registration in this day.");
    }

    public void showNextSevenDaysRegistration() {
        Registration[] registrationList;
        registrationList = systemManagement.getMainRegistrationByDays(7);
        if (registrationList.length != 0) {
            for (int i = 0; i < registrationList.length; i++) {
                System.out.print(i+1 + ".\t");
                printRegistration(registrationList[i]);
            }
        }
        else
            System.out.println("There are no registration request in this day.");
    }

    public void showNextSevenDaysConfirmedRegistration() {
        Assignment[] assignments = systemManagement.getAssignmentForward(7);
        if (assignments.length != 0){
            for(Assignment assignment : assignments)
                printRegistration(assignment.getRegistration());
        }
        else
            System.out.println("There are no registration request in this day.");
    }

    public void xmlMenu() {
        try {
            XmlManagement xmlManagement = new XmlManagement(systemManagement);
            int importOrExport = Validator.getIntBetween(1, 3, Messager.chooseImportExportMessage());
                if (importOrExport == 1) {
                    int whatImport = Validator.getIntBetween(1, 4, Messager.importXmlMenu());
                    xmlImportSwitcher(whatImport, xmlManagement);
                }
                else if(importOrExport == 2) {
                    int whatExport = Validator.getIntBetween(1,4, Messager.exportXmlMenu());
                    xmlExportSwitcher(whatExport, xmlManagement);
                }
                else
                    return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void xmlImportSwitcher(int whatImport, XmlManagement xmlManagement) throws Exception {
        boolean toDelete = false;
        if(whatImport == 4)
            return;

        System.out.println("Please enter a legal xml file name:");
        String fileName = input.nextLine();
        toDelete = Validator.trueOrFalseAnswer("Do you want to delete existed details in the system? \n");
        if (!xmlManagement.checkLegalFileName(fileName))
            return;
        switch (whatImport) {
            case 1: {
                convertMembersFromXml(xmlManagement, fileName, toDelete);
                break;
            }
            case 2: {
                convertBoatsFromXml(xmlManagement, fileName, toDelete);
                break;
            }
            case 3: {
                convertWindowsFromXml(xmlManagement, fileName, toDelete);
                break;
            }
            default: break;
        }
    }

    public void xmlExportSwitcher(int whatImport, XmlManagement xmlManagement) throws Exception {
        if(whatImport == 4)
            return;

        System.out.println("Please enter a legal xml file export to:");
        String filePath = input.nextLine();
        if (!xmlManagement.checkLegalFileName(filePath))
            return;
        switch (whatImport) {
            case 1: {
                Members members = systemManagement.generateMembersToXml(xmlManagement);
                xmlManagement.exportMembers(members, filePath);
                break;
            }
            case 2: {
                Boats boats = systemManagement.generateBoatsToXml(xmlManagement);
                xmlManagement.exportBoats(boats,filePath);
                break;
            }
            case 3: {
                Activities activities = systemManagement.generateActivitiesToXml(xmlManagement);
                xmlManagement.exportActivities(activities,filePath);
                break;
            }
            default: break;
        }
    }

    // input from the xml the members and add them to system.
    public void convertMembersFromXml(XmlManagement xmlManagement, String fileName, boolean toDelete) {
        try {
            if (toDelete) {  // if the manager want to delete all the member's date in the system
                systemManagement.cleanAllMembersBecauseImport();
            }
            Members membersXml = xmlManagement.loadXmlMembers(fileName);
            for (Logic.jaxb.Member memberL : membersXml.getMember()) {
                if (!xmlManagement.checkMemberLEmailNameEmpty(memberL)) {             // check email&name arent empty
                    if(Validator.isValidEmailAddress(memberL.getEmail())) {             // check email concept is valid
                        if (!xmlManagement.checkMemberLAlreadyExist(memberL))        // check member is not already exist
                            xmlManagement.createMemberFromImport(memberL);           // create the generate member to the system
                        else
                            System.out.println(memberL.getName() + " is already exist in the system.");
                    }
                    else
                        System.out.println("Invalid email concept");
                } else
                    System.out.println("Found a schema's detail with empty email or name.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        systemManagement.linkBoatsToMembersAfterImport();
    }

    // input from the xml the boats and add them to system.
    public void convertBoatsFromXml(XmlManagement xmlManagement, String fileName, boolean toDelete) {
        try {
            if(toDelete) {// if the manager want to delete all the boat's date in the system
                systemManagement.cleanAllBoatsBecauseImport();
            }
            Boats boatsXml = xmlManagement.loadXmlBoats(fileName);
            for (Logic.jaxb.Boat boatL : boatsXml.getBoat()){
                if (!xmlManagement.checkBoatLAlreadyExist(boatL)){
                    xmlManagement.createBoatFromImport(boatL);
                }
                else
                    System.out.println("Found a schema's detail with existed boat in the system.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        systemManagement.linkBoatsToMembersAfterImport();
    }

    // input from the xml the windows registration and add them to system.
    public void convertWindowsFromXml(XmlManagement xmlManagement, String fileName, boolean toDelete) {
        try {
            if (toDelete) { // if the manager want to delete all the windows registration date in the system
                systemManagement.cleanAllWindowRegistarionBecauseImport();
            }
            Activities activitiesXml = xmlManagement.loadXmlActivities(fileName);
            for (Timeframe window : activitiesXml.getTimeframe()) {
                if (!xmlManagement.checkActivitiesTimeAlreadyExist(window)) {
                    if (LocalTime.parse(window.getStartTime()).isBefore(LocalTime.parse(window.getEndTime())))
                        xmlManagement.createWindowRegistration(window);
                    else
                        System.out.println("This isn't possible the start time begins after end time of the window.");
                }
                else
                    System.out.println("Found a schema's detail with existed time frame in the system.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void manageRegistrationRequestByManager(){
        Registration[] regiList = systemManagement.getMainRegistrationByDays(7);
        if(regiList == null || regiList.length == 0){
            System.out.println("There are no registration request");
            return;
        }

        Registration regi = whatRegistrationToActWith(Arrays.asList(regiList), "manage with");
        int answerFunc = Validator.getIntBetween(1,4,Messager.manageReqiRequestByManager());

        switch (answerFunc){
            case 1:{        // REMOVE MEMBER FROM REGISTRATION
                if(regi.getRowersListInBoat().size() == 1){
                    System.out.println("Last rower left in the registration and therefore cannot the last rower cannot be deleted");
                    break;
                }
                Member memberToRemove = removeMemberFromRegistrationRequest(regi);
                boolean toSplitRegistration = Validator.trueOrFalseAnswer("to Split Registration for this memebr? ");
                boolean remove = Validator.trueOrFalseAnswer("Are you sure to keep the changes?");
                if (remove)
                     systemManagement.removeRowerSpecificFromRegiRequest(memberToRemove,regi,toSplitRegistration);
                break;
            }
            case 2:{        // REMOVE REGISTRATION
                boolean remove = Validator.trueOrFalseAnswer("Are you sure remove the registration request");
                if (remove)
                    systemManagement.removeRegistrationRequestByMember(regi);
                break;
            }
            case 3:{        // Add Boat Type
                ObjectsUpdater updater = new ObjectsUpdater(systemManagement,this);
                BoatTypeEnum newBoatType = updater.addBoatTypeToRegiRequestUI(regi);
                systemManagement.addBoatTypeToRegiRequest(newBoatType, regi);
                break;
            }
            default: break;
        }
    }

    public Member removeMemberFromRegistrationRequest(Registration regi){
        System.out.println("What member do you want to remove according the number near to.");
        int index = 0, answer;
        for (Member member : regi.getRowersListInBoat()){
            System.out.print(++index +".\t" + showMemberDetails(member));
        }
        answer = Validator.getIntBetween(1,index,"");
        return regi.getRowersListInBoat().get(answer - 1);
    }
}

