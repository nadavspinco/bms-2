package UI.Enum;

public enum SubMenuUpdateMemberEnum {
    upDateLevel, upDateAge, upDateEndJoin, cancelPrivateBoat, upDatePhone,addPrivateBoat, back;

    public static SubMenuUpdateMemberEnum convertFromInt(int number) {
        switch(number){
            case 1: { return upDateLevel; }
            case 2: { return upDateAge; }
            case 3: { return upDateEndJoin; }
            case 4: { return cancelPrivateBoat; }
            case 5: { return upDatePhone; }
            case 6: { return addPrivateBoat; }
            case 7: { return back; }
            default:{ return null; }
        }
    }
}
