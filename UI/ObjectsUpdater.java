package UI;

import Logic.Objects.Boat;
import Logic.Enum.BoatTypeEnum;
import Logic.Enum.LevelEnum;
import Logic.Objects.Member;
import Logic.Objects.Registration;
import Logic.SystemManagement;
import UI.Enum.SubMenuRegiRequest;
import UI.Enum.SubMenuUpdateBoatEnum;
import UI.Enum.SubMenuUpdateMemberEnum;
import UI.Menus.ManagerMenu;
import UI.Tools.Messager;
import UI.Tools.Validator;

import java.util.List;
import java.util.Scanner;

public class ObjectsUpdater {
    private ManagerMenu managerMenu;
    private EngineProxy engineProxy;
    private static Scanner scanner = new Scanner(System.in);

    public ObjectsUpdater(EngineProxy engineProxy, ManagerMenu managerMenu) {
        this.managerMenu = managerMenu;
        this.engineProxy = engineProxy;
    }

    public void updaterMember(){
        SubMenuUpdateMemberEnum optionChosen;
        Member theMember =  managerMenu.whatMemberToActWith("update");
        optionChosen = SubMenuUpdateMemberEnum.convertFromInt(Validator.getIntBetween(1,7, Messager.memberFieldToUpdate()));
        if (optionChosen == SubMenuUpdateMemberEnum.back)
            return;
        updateMemberSwitcher(theMember, optionChosen);
    }

    public void updaterBoat(){
        SubMenuUpdateBoatEnum optionChosen;
        Boat theBoat =  managerMenu.whatBoatToActWith("update");
        if(theBoat == null) {
            return;
        }
        optionChosen = SubMenuUpdateBoatEnum.convertFromInt(Validator.getIntBetween(1,6,Messager.boatFieldToUpdate()));
        if (optionChosen == SubMenuUpdateBoatEnum.back)
            return;
        updateBoatSwitcher(theBoat, optionChosen);
    }

    private void updateMemberSwitcher(Member member, SubMenuUpdateMemberEnum optionChosen){
        switch (optionChosen){
            case upDateAge:{
                int age = Validator.getIntBetween(12,99, "Enter the age.");
                engineProxy.updateMemberAge(member, age);
                break;
            }
            case upDateEndJoin:{
                int years = Validator.getIntBetween(1,99,"How many years will you want to extend?");
                engineProxy.updateMemberEndDate(member, years);
                break;
            }
            case upDateLevel:{
                LevelEnum lvl = LevelEnum.convertFromInt(Validator.getIntBetween(1,3,Messager.ChooseMemberLevelMessage()));
                engineProxy.updateMemberLevel(member, lvl);
                break;
            }
            case upDatePhone:{
                String PhoneNumber = Validator.getValidDigitsInput("Enter phone Number.");
                engineProxy.changePhoneNumber(member, PhoneNumber);
                break;
            }
            case cancelPrivateBoat:{
                engineProxy.cancelMembersPrivateBoat(member);
                break;
            }
            case addPrivateBoat:{
                if(member.getHasPrivateBoat()){
                    System.out.println("this member already has a private boat.");
                    break;
                }
                String serial = managerMenu.addPrivateBoatUI();
                if(!engineProxy.isBoatIsPrivate(serial)) {
                    boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes?\n all future assignment of this boat will be deleted!\n");
                    if(toKeepChanges) {
                        engineProxy.addPrivateBoat(member, serial);
                    }
                }
                else {
                    System.out.println("sorry this boat is already private !!\n");
                }
                break;
            }
            default: break;
        }
    }

    private void updateBoatSwitcher(Boat boat, SubMenuUpdateBoatEnum optionChosen){
        switch (optionChosen){
            case updateName:{
                String newName = Validator.getValidString("Enter new name.");
                boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes?");
                if(toKeepChanges) {
                    engineProxy.updateBoatName(boat, newName);
                }
                break;
            }
            case updateIsWide:{
                boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes?");
                if(toKeepChanges) {
                    engineProxy.updateIsWide(boat);
                }
                break;
            }
            case updateIsCoastal:{
                boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes?");
                if(toKeepChanges) {
                    engineProxy.updateIsCoastal(boat);
                }
                break;
            }
            case fixBoat:{
                boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes?");
                if(toKeepChanges) {
                    engineProxy.fixBoat(boat);
                }
                break;
            }
            case disableBoat:{
                boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes? \nall future assignment will be deleted!");
                if(toKeepChanges) {
                    engineProxy.disAbleBoat(boat);
                }
                break;
            }
            default: break;
        }
    }

    public void updateRegistrationRequest(Member member){
        SubMenuRegiRequest optionChosen;
        Registration registration = managerMenu.whatRegistrationToActWith(member.getMineRegistrationRequestNotConfirmed(), "edit");
        optionChosen = SubMenuRegiRequest.convertFromInt(Validator.getIntBetween(1,5, Messager.subMenuRegiRequestMessge()));
        if (optionChosen == SubMenuRegiRequest.back)
            return;
        updateRegistrationRequestSwitcher(registration, optionChosen);
    }

    public void updateRegistrationRequestSwitcher(Registration registration,SubMenuRegiRequest optionChosen){
        switch (optionChosen){
            case AddRower:{
                addRowerToRegiRequestUI(registration);
                break;
            }
            case RemoveRower:{
                removeRowerFromRegiRequestUI(registration);
                break;
            }
            case AddBoatType:{
                BoatTypeEnum newBoatType = addBoatTypeToRegiRequestUI(registration);
                engineProxy.addBoatTypeToRegiRequest(newBoatType, registration);
                break;
            }
            case RemoveBoatType:{
                removeBoatTypeFromRegiRequestUI(registration);
                break;
            }
            default: break;
        }
    }

    private void removeBoatTypeFromRegiRequestUI(Registration registration) {
        int index = 1 , answer;
        boolean keepChoosing = false;
        if (registration.getBoatTypesSet().size() < 2) {
            System.out.println("There is minimal of boat types wanted in the registration and hence can't remove one.");
            return;
        }
        index = 1;
        System.out.println("\nThese are the boats type in the registration:");
        for (BoatTypeEnum boat : registration.getBoatTypesSet())
            System.out.println(index++ + ". " + boat);
        answer = Validator.getIntBetween(1, registration.getBoatTypesSet().size(),
                "What boat type do you want to remove? according the number near to.");
        BoatTypeEnum removeBoatType = (BoatTypeEnum) registration.getBoatTypesSet().toArray()[answer - 1];
        engineProxy.removeBoatTypeFromRegiRequest(removeBoatType,registration);
    }

    public BoatTypeEnum addBoatTypeToRegiRequestUI(Registration registration){
        boolean keepChoosing = false;
        BoatTypeEnum newBoatType;
        do {
            int answer = Validator.getIntBetween(1, 11, Messager.chooseBoatType());
            newBoatType = BoatTypeEnum.convertFromInt(answer);
            if(registration.getBoatTypesSet().contains(newBoatType)){
                System.out.println("Boat type has already been selected, try another one");
                keepChoosing = true;
            }
            keepChoosing = false; // the boat wasn't selected.
        } while(keepChoosing);
        return newBoatType;
    }

    private void addRowerToRegiRequestUI(Registration registration){ // what rower to add in UI
        int max = BoatTypeEnum.biggestBoatSize(registration.getBoatTypesSet());
        if (registration.getRowersListInBoat().size() == max){
            System.out.println("Can't add another rowers. number of rowers is equal to biggest boat type capacity.");
            return;
        }
        Member member = managerMenu.whatMemberToActWith("add to registration request.");
        if(registration.getRowersListInBoat().contains(member))
            System.out.println("This member is already in the list, choose other one.");
        else if(!engineProxy.isRowerAllowToBeAddedToRegistration(registration.getActivityDate().toLocalDate(), member,
                registration.getWindowRegistration().getStartTime(), registration.getWindowRegistration().getEndTime()))
            System.out.println("This member has an overlapping registration window, choose other one.");
        else
            engineProxy.addRowerToRegiRequest(member,registration);
    }

    private void removeRowerFromRegiRequestUI(Registration registration){   // remove the rower
        if (registration.getRowersListInBoat().size() < 2){
            System.out.println("Can't remove rowers. There is minimal number of rowers in the registration request.");
            return;
        }
        Member member = whatRowerRemoveFromRegiRequestList(registration.getRowersListInBoat());
        boolean toKeepChanges = Validator.trueOrFalseAnswer("are you sure you want to keep changes? all future assignment will be deleted!");
        if(toKeepChanges) {
            engineProxy.removeRowerSpecificFromRegiRequest(member, registration, false); // method in the logic that remove the rower
        }
    }

    private Member whatRowerRemoveFromRegiRequestList(List<Member> memberList){ // select the rower to remove.
        int index = 1, memberIndex;
        System.out.println("What member do you want to remove from registration list.");
        for (Member member : memberList) {
            System.out.println(index++ +". " + managerMenu.showMemberDetails(member));
            System.out.println("\n");;
        }
        memberIndex = Validator.getIntBetween(1,memberList.size(),"");
        return memberList.get(memberIndex - 1);
    }
}
