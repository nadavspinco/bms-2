package UI;

import Logic.EmailAlreadyExistException;
import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;
import Logic.Enum.LevelEnum;
import Logic.InvalidRegistrationException;
import Logic.Objects.Member;
import Logic.Objects.Registration;
import Logic.Objects.WindowRegistration;
import Logic.SystemManagement;
import UI.Menus.ManagerMenu;
import UI.Menus.MenuBase;
import UI.Tools.Messager;
import UI.Tools.Validator;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.lang.Math.max;

public class CreatorUI {
    private EngineProxy engineProxy;
    private static Scanner scanner = new Scanner(System.in);

    public CreatorUI(EngineProxy engineProxy) {
        this.engineProxy = engineProxy;
    }

    public void createBoat() {
        String boatName, serialNumber;
        int intInput;
        BoatTypeEnum boatTypeAnswer;

        boolean isCoastalBoatAnswer, isWideAnswer;
        boolean keepRunning= true;


        boatName = Validator.getValidString("Enter boat's name. ");
        intInput = Validator.getIntBetween(1, 11, Messager.chooseBoatType());
        boatTypeAnswer = BoatTypeEnum.convertFromInt(intInput);

        do {
            serialNumber = Validator.getValidDigitsLettersInput("Enter the serial number");
            if (engineProxy.isBoatExistBySerial(serialNumber))
                System.out.println("There is boat with this serial, insert different one.");
            else
                keepRunning = false;
        } while (keepRunning);

        intInput = Validator.getIntBetween(1, 2, "Is a Coastal boat? \n1. Yes.\n2. No.\n");
        isCoastalBoatAnswer = (intInput == 1);
        intInput = Validator.getIntBetween(1, 2, "Is a wide boat? \n1. Yes.\n2. No.\n");
        isWideAnswer = (intInput == 1);
        engineProxy.addBoat(boatName, boatTypeAnswer, isWideAnswer, isCoastalBoatAnswer, serialNumber);
    }

    public void createMember() throws EmailAlreadyExistException {
        String name, email, phone, password, additionalDetails, serialNumber;
        int age, intInput;
        boolean isManager, keepRunning = true;
        LevelEnum lvl;

        name = Validator.getValidString("Enter your name.");
        phone = Validator.getValidDigitsInput("Enter you phone number");
        do {
            password = Validator.getValidDigitsLettersInput("Enter you password- Digits and letters only.");
            if (password.length() < 3)
                System.out.println("the password should 3 digits at least");
        }while(password.length()<3);

        email = Ui.getEmailFromUser();
        if (engineProxy.isEmailAlreadyExist(email))
            throw new EmailAlreadyExistException(String.format("this email already exists"));

        do {
            serialNumber = Validator.getValidDigitsLettersInput("Enter the ID.");
            if (engineProxy.isMemberExistBySerial(serialNumber))
                System.out.println("There is already member with this ID, insert different one.");
            else
                keepRunning = false;
        } while (keepRunning);

        additionalDetails = Validator.getValidString("Write short sentence of additional details about you.\n");
        age = Validator.getIntBetween(12, 99, "Enter your age.\n" + "At least 12 years old.\n");
        intInput = Validator.getIntBetween(1, 2, "Is manager? \n1. Yes.\n2. No.\n");
        isManager = (intInput == 1);
        intInput = Validator.getIntBetween(1, 3, Messager.ChooseMemberLevelMessage());
        lvl = LevelEnum.convertFromInt(intInput);

        engineProxy.addMember(name, phone, email, password, age, additionalDetails, lvl, isManager, serialNumber);
    }

    public void createRegisterRequest(Member member) {
        ManagerMenu managerMenu = new ManagerMenu(engineProxy, member);
        List<Member> rowerList;
        Member reservationFor = whoTheReservationFor(managerMenu,member);
        LocalDate dateChosen = choiceDateForRegistrationRequest();
        WindowRegistration windowRegistrationChosen = choiceWindowRegistration(managerMenu, engineProxy);
        Set<BoatTypeEnum> boatTypeSet = chooseBoatTypeForReservation();
        int biggestBoat = BoatTypeEnum.biggestBoatSize(boatTypeSet);
        LocalTime startTime = windowRegistrationChosen.getStartTime(),
                  endTime = windowRegistrationChosen.getEndTime();

        if(!engineProxy.isRowerAllowToBeAddedToRegistration(dateChosen, reservationFor, startTime, endTime)) {
            System.out.println("the registration is overlapping to to exist window registration of this member, try in a differance time.");
            return;
        }

        if(boatTypeSet.contains(BoatTypeEnum.SingleBoat) && boatTypeSet.size() == 1) {
            rowerList = new ArrayList<>();
            rowerList.add(reservationFor);
            System.out.println("Cannot add more rower because the type of the registration is single boat");
        }
        else
            rowerList = chooseRowersToRegistrationRequest(managerMenu, reservationFor, biggestBoat, dateChosen, startTime, endTime);
        try {
            engineProxy.addRegistration(new Registration(member,rowerList,windowRegistrationChosen,dateChosen,boatTypeSet),true);
            System.out.println("The registration added successfully");
        }
        catch (InvalidRegistrationException e){
            System.out.println("The registration have not added");
        }
    }

    public static LocalDate choiceDateForRegistrationRequest() {
        System.out.println("Choose the wanted day:");
        for (int i = 0; i < 8; i++)
            System.out.println(i+1 + ". " + LocalDate.now().plusDays(i));
        int dayNum = Validator.getIntBetween(1, 8, "");
        LocalDate chosenDay = LocalDate.now().plusDays(dayNum-1);
        return chosenDay;
    }

    public WindowRegistration choiceWindowRegistration(ManagerMenu managerMenu,EngineProxy engineProxy){
        // if there is no window registration, the member could create new one as he wants to.

        if(engineProxy.isWindowRegistrationEmpty()|| engineProxy.getWindowRegistrationList() == null)
            return  MenuBase.createRegistrationWindow(false);

        // the member choices exist window registration.
        int max = managerMenu.showAllRegistrationWindow();
        int chosenWindow = Validator.getIntBetween(1,max,"Choose the wanted window registration.");
        return  engineProxy.getWindowRegistrations()[chosenWindow - 1];
    }

    public Set<BoatTypeEnum> chooseBoatTypeForReservation() {
        boolean keepChoosing = false;
        Set<BoatTypeEnum> boatTypeSet = new HashSet<BoatTypeEnum>();
        do {
            int answer = Validator.getIntBetween(1, 11, Messager.chooseBoatType());
            boatTypeSet.add(BoatTypeEnum.convertFromInt(answer));
            keepChoosing = Validator.trueOrFalseAnswer("Do you want to add another boat type?");
        } while (keepChoosing);
        return boatTypeSet;
    }

    public List<Member> chooseRowersToRegistrationRequest(ManagerMenu managerMenu,Member mainRower, int maxPeople,
                                      LocalDate regiDate, LocalTime regiStartTime, LocalTime regiEndTime) {
        boolean keepChoosing;
        List<Member>  rowersListToRegistration = new ArrayList<Member>();
        rowersListToRegistration.add(mainRower); // add the owner of the registration request

        boolean keepSelecting = Validator.trueOrFalseAnswer("Do you want to add rowers to the registration?");
        if(!keepSelecting)
            return rowersListToRegistration;
        for (int i = 1; i < maxPeople; i++){
            Member member = managerMenu.whatMemberAddToRegistration(mainRower);
            if(rowersListToRegistration.contains(member)){
                System.out.println("This member is already in the list, choose other one.");
                i--;
            }
            else if(!engineProxy.isRowerAllowToBeAddedToRegistration(regiDate, member, regiStartTime, regiEndTime)){
                // check the member doesn't have an overlapping registration window.
                System.out.println("This member has an overlapping registration window, choose other one.");
                i--;
            }
            else
                rowersListToRegistration.add(member);

            if(rowersListToRegistration.size() == maxPeople){
                System.out.println("Cant add more Members \n");
               return rowersListToRegistration;
            }
            keepChoosing = Validator.trueOrFalseAnswer("Do you want to choose another rower?");
            if(!keepChoosing)
                return rowersListToRegistration;
        }
        return rowersListToRegistration;
    }

    private Member whoTheReservationFor(ManagerMenu managerMenu, Member member){
        Member reservationFor;
        boolean sameMember = false;
        boolean resForMe = Validator.trueOrFalseAnswer("Is the reservation for you?");
        if (resForMe)
            reservationFor = member;
        else {
            do {
                reservationFor = managerMenu.whatMemberToActWith("make the reservation for.\n");
                sameMember = member.equals(reservationFor); // make sure the manager did the reservation for other member.
                if (sameMember)
                    System.out.println("You can't choose your self, try other rower.");
            } while (sameMember);
        }
        return reservationFor;
    }

    private WindowRegistration createWindowRegistrationByMember(){ // member create his own window Registration
        boolean tryAgian = false;
        LocalTime startTime, endTime;
        do {
            startTime = Validator.getTime("start");
            endTime = Validator.getTime("end");
            tryAgian = startTime.getHour() >= endTime.getHour();
            if (tryAgian)
                System.out.println("The end time of the activity (hours) should be greater than the start time of the activity (hours), try again");
        }while (tryAgian);


        BoatTypeEnum boatType = Validator.getBoatType();
        ActivityTypeEnum activityType = Validator.getActivityType();

        return new WindowRegistration(activityType, boatType, startTime, endTime);
    }
}

