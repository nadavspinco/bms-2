package UI.Menus;

import Logic.Objects.*;
import Logic.*;
import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;
import Logic.jaxb.Activities;
import Logic.jaxb.Boats;
import Logic.jaxb.Members;
import UI.CreatorUI;
import UI.EngineProxy;
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

    public ManagerMenu(EngineProxy engineProxy, Member member) {
        super(engineProxy, member);
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
//        xmlManagement.exportSystemManagementDetails(systemManagement); TODO
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
        Assignment [] assignmentsByDate =  super.engineProxy.getAssignmentByDate(date);
        showAssignments(assignmentsByDate);
    }

    private void deleteRowerFromAssignment() {
        LocalDate date = CreatorUI.choiceDateForRegistrationRequest();
        Assignment[] assignments = super.engineProxy.getAssignmentByDate(date);
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
                    super.engineProxy.removeMemberFromAssigment( chosenAssiment, chosenMember,toSplitRequest);
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
        Assignment[] assignmentsByDate = super.engineProxy.getAssignmentByDate(date);
        if (assignmentsByDate != null && assignmentsByDate.length != 0) {
            Assignment chosenAssigment = choseAssignment(assignmentsByDate);
            Registration[] registrationsOptions = super.engineProxy.getValidRegistrationToUnion(chosenAssigment);
            if (registrationsOptions.length != 0) {
                ShowRegistrationRequest(registrationsOptions);
                int selectedRegistration = Validator.getIntBetween(1, registrationsOptions.length, "please chose");
                boolean toKeepChanges= Validator.trueOrFalseAnswer("to keep changes");
                if(toKeepChanges) {
                    super.engineProxy.unionRequestToAssignment(chosenAssigment, registrationsOptions[selectedRegistration - 1]);
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
        Assignment[] assignmentsByDate = super.engineProxy.getAssignmentByDate(date);
        if (assignmentsByDate != null && assignmentsByDate.length != 0) {
            Assignment chosenAssignment = choseAssignment(assignmentsByDate);
            boolean toRemoveRegistration = Validator.trueOrFalseAnswer("to Remove Registration also?\n ");
            boolean toRemove = Validator.trueOrFalseAnswer("are you sure you want to save the canges\n? ");
            if (toRemove) {
                super.engineProxy.removeAssignment(chosenAssignment, toRemoveRegistration);
            }
        }
        else {
            System.out.println("no Assignment found on this day");
        }
    }

    private void showAssignmentByDate(LocalDate date) {
        Assignment[] assignmentsByDate = super.engineProxy.getAssignmentByDate(date);
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
        Registration[] registrations = super.engineProxy.getMainRegistrationByDays(7);
        if (registrations==null || registrations.length == 0) {
            System.out.println("no Registration found ");
        }
        else {
            ShowRegistrationRequest(registrations);
            int registrationSelection = getRegistrationSelection(registrations.length);
            Boat[] legalBoats =
                    super.engineProxy.getArrayOfValidBoats(registrations[registrationSelection - 1]);
            if (legalBoats.length == 0) {
                System.out.println("no legal found boats for this Registration");
            }
            else {
                showBoatsArray(legalBoats);
                int selectedBoat = Validator.getIntBetween(1, legalBoats.length, "please select a bout");
                boolean toSaveChanges = Validator.trueOrFalseAnswer("to save changes?");
                if (toSaveChanges) {
                    super.engineProxy.assignBoat(registrations[registrationSelection - 1], legalBoats[selectedBoat - 1]);
                }
            }
        }
    }

    private int getRegistrationSelection(int numOfRegistration) {
        return Validator.getIntBetween(1, numOfRegistration, "choose from the following Registration ");
    }

    private void manageBoatListSwitcher(SubMenuManageBoatListEnum option) {
        CreatorUI creator = new CreatorUI(engineProxy);
        ObjectsUpdater updater = new ObjectsUpdater(engineProxy, this);
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
                    super.engineProxy.removeBoat(boat);
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
        CreatorUI creator = new CreatorUI(engineProxy);
        ObjectsUpdater updater = new ObjectsUpdater(engineProxy, this);
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
            System.out.println("cant delete yourself (the logged in member");
            return;
        }
        boolean toKeep = Validator.trueOrFalseAnswer("are you sure you want to delete this member? ");
        if(toKeep) {
            super.engineProxy.removeMember(memberToDelete);
        }
    }

    public void showAllMembersToScreen() {
        Member[] membersArr = super.engineProxy.getMemberArry();
        if (membersArr.length == 0 || membersArr == null)
            System.out.println("There are no members in the system");

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
        Boat[] boatsArr = super.engineProxy.getBoatArry();
        if (boatsArr.length == 0 || boatsArr == null)
            System.out.println("There are no boat in the system");

        for (int i = 0; i < boatsArr.length; i++)
            System.out.println((i + 1) + ". " + getBoatDetails(boatsArr[i]));
    }

    public Boat whatBoatToActWith(String actMsg) {

        List<Boat> boatsArr = super.engineProxy.getBoatList();
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
        List<Member> membersArr = super.engineProxy.getMemberList();
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
        WindowRegistration[] windowRegistrations = super.engineProxy.getWindowRegistrations();
        showRegistrationWindow(windowRegistrations);
        WindowRegistration windowRegistration = selectWindowRegistration(windowRegistrations);
        if (windowRegistration != null) {
            showRegistrationWindowByDetail(windowRegistration);
            int toDelete = Validator.getIntBetween(1, 2, "are you sure you want to delete this window? \n1.Yes 2.No\n");
            if (toDelete == 1)
                super.engineProxy.deleteWindowRegistration(windowRegistration);
            else
                System.out.println("nothing deleted!");
        }
    }

    private void editRegistrationWindow() {
        WindowRegistration[] windowRegistrations = super.engineProxy.getWindowRegistrations();
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
                super.engineProxy.deleteWindowRegistration(windowRegistration);
                super.engineProxy.addWindowRegistration(updatedWindow);
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
        registrationList = super.engineProxy.getConfirmedRegistrationBySpecificDay(dateOfRegistration);
        if (registrationList != null)
            registrationList.forEach(registration -> printRegistration(registration));
        else
            System.out.println("There are no assignment in this day.");
    }

    public void showRegistrationsBySpecificDay() {
        LocalDate dateOfRegistration;
        List<Registration> registrationList;
        dateOfRegistration = CreatorUI.choiceDateForRegistrationRequest();
        registrationList = super.engineProxy.getRegistrationBySpecificDay(dateOfRegistration);
        if (registrationList != null)
            registrationList.forEach(registration -> printRegistration(registration));
        else
            System.out.println("There are no registration in this day.");
    }

    public void showNextSevenDaysRegistration() {
        Registration[] registrationList;
        registrationList = super.engineProxy.getMainRegistrationByDays(7);
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
        Assignment[] assignments = super.engineProxy.getAssignmentForward(7);
        if (assignments.length != 0){
            for(Assignment assignment : assignments)
                printRegistration(assignment.getRegistration());
        }
        else
            System.out.println("There are no registration request in this day.");
    }

    public void xmlMenu() {
        try {
            int importOrExport = Validator.getIntBetween(1, 3, Messager.chooseImportExportMessage());
                if (importOrExport == 1) {
                    int whatImport = Validator.getIntBetween(1, 4, Messager.importXmlMenu());
                    xmlImportSwitcher(whatImport);
                }
                else if(importOrExport == 2) {
                    int whatExport = Validator.getIntBetween(1,4, Messager.exportXmlMenu());
                    xmlExportSwitcher(whatExport);
                }
                else
                    return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void xmlImportSwitcher(int whatImport) throws Exception {
        boolean toDelete = false;
        if(whatImport == 4)
            return;

        System.out.println("Please enter a legal xml file name:");
        String xmlDetails, fileName = input.nextLine();
        String[] wrongDetails;
        toDelete = Validator.trueOrFalseAnswer("Do you want to delete existed details in the system? \n");
        if (!Validator.checkLegalFileName(fileName))
            return;
        switch (whatImport) {
            case 1: {
                xmlDetails = engineProxy.readXmlAsStringFromFile(fileName);
                wrongDetails = engineProxy.convertMembersFromXml(xmlDetails, toDelete);
                wrongDetailsFromXmlMsg(wrongDetails); // if were wrong details in xml notify the user
                break;
            }
            case 2: {
                xmlDetails = engineProxy.readXmlAsStringFromFile(fileName);
                wrongDetails = engineProxy.convertBoatsFromXml(xmlDetails, toDelete);
                wrongDetailsFromXmlMsg(wrongDetails); // if were wrong details in xml notify the user
                break;
            }
            case 3: {
                xmlDetails = engineProxy.readXmlAsStringFromFile(fileName);
                wrongDetails = engineProxy.convertWindowsFromXml(xmlDetails, toDelete);
                wrongDetailsFromXmlMsg(wrongDetails); // if were wrong details in xml notify the user
                break;
            }
            default: break;
        }
    }

    public void xmlExportSwitcher(int whatExport) throws Exception {
        if(whatExport == 4)
            return;
        System.out.println("Please enter a legal xml file export to:");
        String xmlDetails = null, filePath = input.nextLine();
        if (!Validator.checkLegalFileName(filePath))
            return;
        switch (whatExport) {
            case 1: {
                xmlDetails = super.engineProxy.exportMembersToString();
                engineProxy.writeXmlStringToFile(filePath, xmlDetails);
                System.out.println("Export has done successfully");
                break;
            }
            case 2: {
                xmlDetails = super.engineProxy.exportBoatsToString();
                engineProxy.writeXmlStringToFile(filePath, xmlDetails);
                System.out.println("Export has done successfully");
                break;
            }
            case 3: {
                xmlDetails = super.engineProxy.exportActivitiesToString();
                engineProxy.writeXmlStringToFile(filePath, xmlDetails);
                System.out.println("Export has done successfully");
                break;
            }
            default: break;
        }
    }

    public void manageRegistrationRequestByManager(){
        Registration[] regiList = super.engineProxy.getMainRegistrationByDays(7);
        if(regiList == null || regiList.length == 0){
            System.out.println("There are no registration request");
            return;
        }
        Registration regi = whatRegistrationToActWith(Arrays.asList(regiList), "manage with");
        int answerFunc = Validator.getIntBetween(1,4,Messager.manageReqiRequestByManager());

        switch (answerFunc){
            case 1:        // REMOVE MEMBER FROM REGISTRATION
                if(regi.getRowersListInBoat().size() == 1){
                    System.out.println("Last rower left in the registration and therefore cannot the last rower cannot be deleted");
                    break;
                }
                Member memberToRemove = removeMemberFromRegistrationRequest(regi);
                boolean toSplitRegistration = Validator.trueOrFalseAnswer("to Split Registration for this memebr? ");
                boolean remove = Validator.trueOrFalseAnswer("Are you sure to keep the changes?");
                if (remove)
                    super.engineProxy.removeRowerSpecificFromRegiRequest(memberToRemove,regi,toSplitRegistration);
                break;

            case 2:         // REMOVE REGISTRATION
                boolean removeTwo = Validator.trueOrFalseAnswer("Are you sure remove the registration request");
                if (removeTwo)
                    super.engineProxy.removeRegistrationRequestByMember(regi);
                break;

            case 3:       // Add Boat Type
                ObjectsUpdater updater = new ObjectsUpdater(engineProxy,this);
                BoatTypeEnum newBoatType = updater.addBoatTypeToRegiRequestUI(regi);
                super.engineProxy.addBoatTypeToRegiRequest(newBoatType, regi);
                break;

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

    public void wrongDetailsFromXmlMsg(String[] detailsName){
        if (detailsName != null && detailsName.length > 0){
            System.out.println("Found a wrong schema's detail in the file:");
            for (String detail : detailsName)
                System.out.println(detail);
        }
        System.out.println("Import has done successfully");
    }
}

