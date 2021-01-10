package UI.Enum;

public enum MemberMenuOptionEnum
{
     ChangeName, ChangePhone,ChangeEmail, ChangePass, FutureRegistration, HistoryRegistration, ManageRegisterRequest, Exit;

    public static MemberMenuOptionEnum convertFromInt(int number){
        switch (number){
            case 1: { return ChangeName; }
            case 2: { return ChangePhone; }
            case 3: { return ChangeEmail; }
            case 4: { return ChangePass; }
            case 5: { return FutureRegistration; }
            case 6: { return HistoryRegistration; }
            case 7: { return ManageRegisterRequest; }
            case 8: { return Exit; }
            default:{return null; }
        }
    }
}
