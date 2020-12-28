package UI.Menus;

import Logic.EmailAlreadyExistException;
import Logic.Objects.Member;
import Logic.Objects.Registration;
import Logic.SystemManagement;
import UI.CreatorUI;
import UI.EngineProxy;
import UI.Enum.MemberMenuOptionEnum;
import UI.Enum.RegistrationWindowMenuEnum;
import UI.ObjectsUpdater;
import UI.Tools.Messager;
import UI.Tools.Validator;
import UI.Ui;

import java.util.List;

public class MemberMenu extends MenuBase {
    private Member memberLoggedIn;
    private MemberMenuOptionEnum optionChosen;


    public MemberMenu( SystemManagement systemManagement,Member theMember) {
        super(systemManagement,theMember);
        memberLoggedIn = super.member;
        this.systemManagement = systemManagement;
    }

    public void memberMenuRun() {
        boolean keepRunning = false;
        int chosenOption;
        while (!keepRunning) {
            chosenOption = Validator.getIntBetween(1,8, Messager.memberMenuPrintMessage());
            optionChosen = MemberMenuOptionEnum.convertFromInt(chosenOption);
            keepRunning = (optionChosen == MemberMenuOptionEnum.Exit);
            if (!keepRunning)
                menu(optionChosen);
        }
        xmlManagement.exportSystemManagementDetails(systemManagement);
    }

    public void menu (MemberMenuOptionEnum optionChosen){
        String newName, newPhoneNumber, newPassword, newEmail;
        switch (optionChosen){
            case ChangeName:{
                newName = Validator.getValidString("Enter your new name.\n");
                EngineProxy e = new EngineProxy("localhost",1989);
                e.changeName(memberLoggedIn,newName);
//                systemManagement.changeName(memberLoggedIn, newName);
                break;
            }
            case ChangePhone:{
                newPhoneNumber = Validator.getValidDigitsInput("Enter your new Phone number.\n");
                systemManagement.changePhoneNumber(memberLoggedIn, newPhoneNumber);
                break;
            }
            case ChangeEmail:{
                try {
                    newEmail = Ui.getEmailFromUser();
                    systemManagement.changeEmail(memberLoggedIn, newEmail);
                }
                catch (EmailAlreadyExistException e){
                    System.out.println(e.getMessage());
                }
                break;
            }
            case ChangePass:{
                newPassword = Validator.getValidDigitsLettersInput("Enter your new password");
                systemManagement.changePassword(memberLoggedIn, newPassword);
                break;
            }
            case FutureRegistration:{
                showFutureRegistration((memberLoggedIn));
                break;
            }
            case HistoryRegistration:{
                showHistoryRegistration(memberLoggedIn);
                break;
            }
            case ManageRegisterRequest: {
                RegistrationWindowMenuEnum registrationWindowMenuSelection;
                System.out.println(Messager.getMangeRegistrationMenu());
                registrationWindowMenuSelection = getRegistrationWindowMenuSelection();
                if (memberLoggedIn.getMineRegistrationRequestNotConfirmed().size() == 0 &&
                        registrationWindowMenuSelection != RegistrationWindowMenuEnum.addRegistrationWindow){
                    System.out.println("You dont have registration request, add one first.");
                    break; // if the member doesnt have registration request
                }
                mangeMemberRequestSwitcher(registrationWindowMenuSelection);
                break;
            }
            default:
                break;
        }
    }

    public void showHistoryRegistration(Member member){
        List<Registration> historyRegistration = systemManagement.getHistoryRegistrationOfMember(member);
        if( historyRegistration == null)
            System.out.println("You didn't have any registration activity at the past seven days ago.");
        else
            historyRegistration.forEach(r -> printRegistration(r));
    }

    public void showFutureRegistration(Member member){
        List<Registration> futureRegistration = systemManagement.getFutureRegistrationOfMember(member);
        if( futureRegistration == null || futureRegistration.size() == 0)
            System.out.println("You didn't have any future registration activity.");
        else
            futureRegistration.forEach(r -> printRegistration(r));
    }

    public void mangeMemberRequestSwitcher(RegistrationWindowMenuEnum option){
        switch (option){
            case addRegistrationWindow:{
                CreatorUI create = new CreatorUI(systemManagement);
                create.createRegisterRequest(memberLoggedIn);
                break;
            }
            case editRegistrationWindow:{
                ObjectsUpdater objectsUpdater = new ObjectsUpdater(systemManagement, new ManagerMenu(systemManagement,memberLoggedIn));
                objectsUpdater.updateRegistrationRequest(memberLoggedIn);
                break;
            }
            case deleteRegistrationWindow:{
                Registration regiToRemove = whatRegistrationToActWith(member.getMineRegistrationRequestNotConfirmed(),"remove");
                systemManagement.removeRegistrationRequestByMember(regiToRemove);
                break;
            }
            case showAllRegistrationWindow:{
                List<Registration> regis = memberLoggedIn.getMineRegistrationRequestNotConfirmed();
                regis.forEach(MemberMenu::showPersonalityRegistrationRequest);
                break;
            }
            default: break;
        }
    }
}
