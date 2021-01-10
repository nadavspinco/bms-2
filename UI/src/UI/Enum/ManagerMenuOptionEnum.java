package UI.Enum;

public enum ManagerMenuOptionEnum
{
    ManageBoatList, ManageMemberList,ManageRegistrationWindow, ManageRegistrationRequest,
    ShowAllRegistrationNeedToSchedule, ShowAllConfirmRegistrationRequest,Exit,AssignmentSubMenu, xmlMenu;

    public static ManagerMenuOptionEnum convertFromInt(int number){
        switch(number){
            case 1: { return ManageBoatList; }
            case 2: { return ManageMemberList; }
            case 3: { return ManageRegistrationWindow; }
            case 4: { return ManageRegistrationRequest; }
            case 5: { return ShowAllRegistrationNeedToSchedule; }
            case 6: { return ShowAllConfirmRegistrationRequest; }
            case 7: { return AssignmentSubMenu; }
            case 8: {return xmlMenu;}
            case 9: {return Exit;}
            default:{ return null; }
        }
    }
}