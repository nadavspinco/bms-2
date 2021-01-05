package UI.Menus;

import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;
import Logic.Objects.Boat;
import Logic.Objects.Member;
import Logic.Objects.Registration;
import Logic.Objects.WindowRegistration;
import Logic.SystemManagement;
import Logic.XmlManagement;
import UI.EngineProxy;
import UI.Enum.RegistrationWindowMenuEnum;
import UI.Tools.Validator;
import com.sun.xml.internal.ws.api.pipe.Engine;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class MenuBase {
    protected Member member;
//    protected XmlManagement xmlManagement;
    protected EngineProxy engineProxy;

    public MenuBase(EngineProxy engineProxy, Member member) {
        this.member = member;
//        this.xmlManagement = new XmlManagement(systemManagement);
        this.engineProxy = engineProxy;
    }

    protected void showRegistrationWindowByDetail(WindowRegistration windowRegistration){
        String boatTypeMsg = null;
        if(windowRegistration.getBoatType() != null)
            boatTypeMsg = String.format("with optional boat type: %s.",windowRegistration.getBoatType());
        else
            boatTypeMsg = "without optional boat type.";
        String windowRegistrationInfo = String.format("The activity is %s begins at %s - %s, %s",
                windowRegistration.getActivityType(), windowRegistration.getStartTime(), windowRegistration.getEndTime(),boatTypeMsg);
        System.out.println(windowRegistrationInfo);
    }

    protected void printRegistration(Registration registration) {
        System.out.println(String.format("Date: %s.",registration.getActivityDate().toLocalDate()));
        showRegistrationWindowByDetail(registration.getWindowRegistration());
        System.out.println("Registration by: " + registration.getRowerOfRegistration().getNameMember());
        System.out.println("Rowars:");
        for (Member member: registration.getRowersListInBoat())
            System.out.format("-%s\n",showMemberDetails(member));
        if(registration.getBoatType() != null)
            System.out.print("requested bout type: ");
        for (BoatTypeEnum boatType: registration.getBoatType()) {
            System.out.print(boatType +", ");
        }
        System.out.println("\n");
    }

    protected WindowRegistration selectWindowRegistration(WindowRegistration [] windowRegistrations){
        WindowRegistration selectedWindow = null;
        if (windowRegistrations == null || windowRegistrations.length == 0) {
            System.out.println("there is no window Registration ");
        }
        else{
            int toEdit = Validator.getIntBetween(1, windowRegistrations.length,
                    "please make a selection which window");
            selectedWindow = windowRegistrations[toEdit - 1];
        }
        return selectedWindow;
    }

    protected String createBoatCode(Boat boat){ // create the boat code according to the table in the doc file
        String wide = "", coastal = "", code;
        if(boat.isWide())
            wide = " wide";
        if (boat.isCoastalBoat())
            coastal = " coastal";
        code = BoatTypeEnum.BoatTypeCode(boat.getBoatType());
        String boatCode = String.format("%s%s%s",code, wide, coastal);
        return boatCode;
    }

    protected void ShowRegistrationRequest (Registration[] registrations)
    {
        int iRegistrations = 1;
        if(registrations == null || registrations.length == 0) {
            System.out.println("there is no Registration");
            return;
        }
        for (Registration registration :registrations) {
            System.out.println("----------------------------------");
            System.out.print(String.format("%d.\t",iRegistrations));
            printRegistration(registration);
            iRegistrations++;
            System.out.println("----------------------------------");
        }
    }

    public static WindowRegistration createRegistrationWindow(boolean toAskForBoutType)
    {
        LocalTime startTime ;
        LocalTime endTime ;
        BoatTypeEnum boatType = null;
        boolean keepRunning = true;
        do {
            startTime = Validator.getTime("start");
            endTime = Validator.getTime("end");

            if(!startTime.isBefore(endTime))
                System.out.println("The \"start time\" should starts before \"end time\", try again.");
            else
                keepRunning = false;
        }while (keepRunning);
        if(toAskForBoutType) {
             boatType = Validator.getBoatType();
        }
        ActivityTypeEnum activityType = Validator.getActivityType();
        WindowRegistration windowRegistration = new WindowRegistration(activityType, boatType, startTime, endTime);
        return windowRegistration;
    }

    protected void addNewRegistrationWindow() {
        WindowRegistration windowRegistration = createRegistrationWindow(true);
        engineProxy.addWindowRegistration(windowRegistration);
    }

    protected int showRegistrationWindow(WindowRegistration[] windowRegistrations){
        int currentIndex = 1; //index for selection;

        for (WindowRegistration windowRegistration : windowRegistrations){
            System.out.print(String.format("%d. ",currentIndex));
            showRegistrationWindowByDetail(windowRegistration);
            currentIndex++;
        }
        return currentIndex-1; // return the max number of windowRegistrations
    }
    public int showAllRegistrationWindow() {    // return the max number of windowRegistrations
        WindowRegistration [] windowRegistrations = engineProxy.getWindowRegistrations();
        if(windowRegistrations == null || windowRegistrations.length == 0){
            System.out.println("no window registration found");
            return 0;
        }
        return showRegistrationWindow(windowRegistrations);
    }

    protected RegistrationWindowMenuEnum getRegistrationWindowMenuSelection() {
        int windowSelection = Validator.getIntBetween(1, 5, "please make a selection\n"); //5 == exit == null
        return RegistrationWindowMenuEnum.ConvertFromInt(windowSelection);
    }

    protected static void showPersonalityRegistrationRequest(Registration regi){
        String isConfirm = null;
        if(regi.isConfirmed())
            isConfirm = "and request has been confirmed";
        else
            isConfirm = "and request not confirmed yet";
        String registrationDetail = String.format("%s at %s from %s until %s %s.", regi.getWindowRegistration().getActivityType(),
                regi.getActivityDate(), regi.getWindowRegistration().getStartTime(),regi.getWindowRegistration().getEndTime(), isConfirm);
        System.out.println(registrationDetail);
    }

    public Registration whatRegistrationToActWith(List<Registration> regiList, String actMsg){
        System.out.println(String.format("Choose what registration you want to %s by the number near to.", actMsg));
        int index = 1;
        for(Registration regi : regiList){
            System.out.print(index++ + ". ");
            printRegistration(regi);
        }
        int regiIndex = Validator.getIntBetween(1,regiList.size(),"");
        return regiList.get(regiIndex - 1);
    }

    public String addPrivateBoatUI() {
        String serialBoat;
        boolean keepMoving = true;
        do {
            serialBoat = Validator.getValidDigitsLettersInput("Enter the serial number of the boat");
            keepMoving = engineProxy.isBoatExistBySerial(serialBoat);
            if(!keepMoving)
                System.out.println("There is no exist boat with this serial number, try again.");
        } while (!keepMoving);

        return serialBoat;
    }

    public String getBoatDetails(Boat boat){
        String isPrivate = null,isAble = null, boatDetails, boatCode = null;
        boatCode = createBoatCode(boat);
        if (boat.isPrivate())
            isPrivate = "is private boat";
        else
            isPrivate = "isn't private boat";
        if(boat.isAvailable())
            isAble = "and able to use.";
        else
            isAble = "and disable to use.";

        boatDetails = String.format("The boat \"%s\" %s, id: %s %s %s","" +
                boat.getBoatName(), boatCode, boat.getSerialBoatNumber(), isPrivate, isAble);
        return boatDetails;
    }

    protected void showMembers(List<Member> memberList)
    {
        if(memberList!=null && memberList.size()!=0) {
            int iMember = 1;
            for (Member member : memberList) {
                System.out.println("----------------------------------------");
                System.out.println(String.format("%d \n",iMember));
                System.out.println(showMemberDetails(member));
                System.out.println("----------------------------------------");
                iMember++;
            }
        }
    }

    public Member choseMember(List<Member> memberList){
        Member toReturnMember= null;
        if(memberList!=null && memberList.size()!=0){
            int selection = Validator.getIntBetween(1,memberList.size(),"please choose member");
            toReturnMember = memberList.get(selection-1);
        }
        return toReturnMember;
    }

    public String showMemberDetails(Member member){
        String isManager = null, memberDetails, beginDate, endDate;
        beginDate = member.getJoinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        endDate = member.getEndDate().format((DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (member.getIsManager())
            isManager = "manager";
        else
            isManager = "member";
        memberDetails = String.format("The %s %s,email: %s, ID: %s with date membership: %s until %s.","" +
                isManager, member.getNameMember(),member.getEmail(), member.getSerial(), beginDate, endDate);
        return memberDetails;
    }

}

