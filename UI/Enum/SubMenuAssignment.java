package UI.Enum;

public enum SubMenuAssignment {
    AssignBoat,RemoveAssignment, DeleteRowerFromAssignment ,UnionAssignmentAndRegistration,ShowAssigmentByDate,Exit;

    public static SubMenuAssignment convertFromInt(int selection)
    {
        switch (selection)
        {
            case 5:
                return AssignBoat;
            case 2:
                return ShowAssigmentByDate;
            case 4:
                return DeleteRowerFromAssignment;
            case 1:
                return UnionAssignmentAndRegistration;
            case 3:
                return RemoveAssignment;
            case 6:
                return Exit;
        }
        return null;
    }
}
