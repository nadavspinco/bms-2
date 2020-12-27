package Logic.Enum;

import Logic.jaxb.RowingLevel;

import javax.xml.bind.annotation.XmlRootElement;

public enum LevelEnum
{
    Beginner,
    Professional,
    WorldClass;

    public static LevelEnum convertFromInt(int Number){
        switch (Number)
        {
            case 1: { return Beginner; }
            case 2: { return Professional; }
            case 3: { return WorldClass; }
            default:{ return null; }
        }
    }

    public static LevelEnum convertFromRowingLevel(RowingLevel lvl) {
        switch (lvl) {
            case BEGINNER: { return LevelEnum.Beginner; }
            case INTERMEDIATE: { return LevelEnum.Professional; }
            case ADVANCED: { return LevelEnum.WorldClass; }
            default: return null;
        }
    }

    public static RowingLevel convertToRowingLevel(LevelEnum lvl){
        switch (lvl){
            case Beginner: { return RowingLevel.BEGINNER; }
            case Professional: { return RowingLevel.INTERMEDIATE; }
            case WorldClass: { return RowingLevel.ADVANCED; }
            default: return null;
        }
    }
}
