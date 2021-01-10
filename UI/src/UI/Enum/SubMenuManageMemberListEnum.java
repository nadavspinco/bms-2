package UI.Enum;

public enum SubMenuManageMemberListEnum {
    addMember, removeMember, updateMember, showAllMembers, back;

    public static SubMenuManageMemberListEnum convertFromInt(int number){
        switch(number){
            case 1: { return addMember; }
            case 2: { return removeMember; }
            case 3: { return updateMember; }
            case 4: { return showAllMembers; }
            case 5: { return back;}

            default:{ return null; }
        }
    }
}
