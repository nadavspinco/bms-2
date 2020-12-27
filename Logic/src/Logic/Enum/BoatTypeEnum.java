package Logic.Enum;

import Logic.jaxb.BoatType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

import static java.lang.Math.max;


public enum BoatTypeEnum
{
    SingleBoat(),
    DoubleBoat, DoubleBoatPaddle, DoubleCoxed, DoublePaddleCoxed,
    QuartetBoat, QuartetBoatPaddle, QuartetBoatCoxed, QuartetBoatPaddleCoxed,
    OctetBoatCoxed, OctetBoatCoxedPaddle;

    public static BoatTypeEnum convertFromInt(int number){
        switch (number)        {
            case 1: { return SingleBoat; }
            case 2: { return DoubleBoat; }
            case 3: { return DoubleBoatPaddle;}
            case 4: { return DoubleCoxed; }
            case 5: { return DoublePaddleCoxed; }
            case 6: { return QuartetBoat; }
            case 7: { return QuartetBoatPaddle; }
            case 8: { return QuartetBoatCoxed; }
            case 9: { return QuartetBoatPaddleCoxed; }
            case 10: { return OctetBoatCoxed; }
            case 11: { return OctetBoatCoxedPaddle; }
            default:{return null; }
        }
    }

    public static int returnNumOfRowers (BoatTypeEnum boatTypeEnum)
    {
        switch (boatTypeEnum) {
            case SingleBoat:
                return 1;
            case DoubleBoat:
                return 2;
            case  DoubleBoatPaddle:
                return 2;
            case  DoubleCoxed:
                return 3;
            case  DoublePaddleCoxed:
                return 3;
            case QuartetBoat:
                return 4;
            case  QuartetBoatPaddle:
                return 4;
            case  QuartetBoatCoxed:
                return 5;
            case  QuartetBoatPaddleCoxed:
                return 8;
            case  OctetBoatCoxed:
                return 8;
            case  OctetBoatCoxedPaddle:
                return 8;

            default: return 0;
        }

    }

    public static String BoatTypeCode(BoatTypeEnum boatType){
        switch (boatType){
            case SingleBoat: {return "1X";}
            case DoubleBoat: {return "2X";}
            case DoubleBoatPaddle: {return "2-";}
            case DoubleCoxed: {return "2X+";}
            case DoublePaddleCoxed: {return "2+";}
            case QuartetBoat: {return "4X";}
            case QuartetBoatPaddle: {return "4-";}
            case QuartetBoatCoxed: {return "4X+";}
            case QuartetBoatPaddleCoxed: {return "4+";}
            case OctetBoatCoxed: {return "8X";}
            case OctetBoatCoxedPaddle: {return "8X+";}
            default: return null;
        }
    }

    public static int biggestBoatSize(Set<BoatTypeEnum> boatTypeArr) {
        int max = 0, res;
        for (BoatTypeEnum boat : boatTypeArr) {
            if (boat.equals(BoatTypeEnum.SingleBoat))
                res = 1;
            else if (boat.equals(BoatTypeEnum.DoubleBoat) || boat.equals(BoatTypeEnum.DoubleBoatPaddle)
                    || boat.equals(BoatTypeEnum.DoubleCoxed) || boat.equals(BoatTypeEnum.DoublePaddleCoxed))
                res = 2;
            else if (boat.equals(BoatTypeEnum.OctetBoatCoxed) || boat.equals(BoatTypeEnum.OctetBoatCoxedPaddle))
                res = 8;
            else
                res = 4;
            max = max(res, max);
        }
        return max;
    }

    public static int smallestBoatSize(Set<BoatTypeEnum> boatTypeArr) {
        int min = 0, res;
        for (BoatTypeEnum boat : boatTypeArr) {
            if (boat.equals(BoatTypeEnum.SingleBoat))
                res = 1;
            else if (boat.equals(BoatTypeEnum.DoubleBoat) || boat.equals(BoatTypeEnum.DoubleBoatPaddle)
                    || boat.equals(BoatTypeEnum.DoubleCoxed) || boat.equals(BoatTypeEnum.DoublePaddleCoxed))
                res = 2;
            else if (boat.equals(BoatTypeEnum.OctetBoatCoxed) || boat.equals(BoatTypeEnum.OctetBoatCoxedPaddle))
                res = 8;
            else
                res = 4;
            min = Math.min(res, min);
        }
        return min;
    }

    public static BoatTypeEnum convertFromBoatTypeImported(BoatType boatTypeImported){
        switch(boatTypeImported){
            case SINGLE: return BoatTypeEnum.SingleBoat;
            case DOUBLE: return BoatTypeEnum.DoubleBoatPaddle;
            case COXED_DOUBLE: return BoatTypeEnum.DoublePaddleCoxed;
            case PAIR: return BoatTypeEnum.DoubleBoat;
            case COXED_PAIR: return BoatTypeEnum.DoubleCoxed;
            case FOUR: return BoatTypeEnum.QuartetBoat;
            case COXED_FOUR: return BoatTypeEnum.QuartetBoatCoxed;
            case QUAD: return BoatTypeEnum.QuartetBoatPaddle;
            case COXED_QUAD: return BoatTypeEnum.QuartetBoatPaddleCoxed;
            case OCTUPLE: return BoatTypeEnum.OctetBoatCoxedPaddle;
            case EIGHT: return BoatTypeEnum.OctetBoatCoxed;
            default: return null;
        }
    }

    public static BoatType convertToBoatTypeGenerated(BoatTypeEnum boatType){
        switch (boatType){
            case SingleBoat: return BoatType.SINGLE;
            case DoubleBoatPaddle: return BoatType.DOUBLE;
            case DoublePaddleCoxed: return BoatType.COXED_DOUBLE;
            case DoubleBoat: return BoatType.PAIR;
            case DoubleCoxed: return BoatType.COXED_PAIR;
            case QuartetBoat: return BoatType.FOUR;
            case QuartetBoatCoxed: return BoatType.COXED_FOUR;
            case QuartetBoatPaddle: return BoatType.QUAD;
            case QuartetBoatPaddleCoxed: return BoatType.COXED_QUAD;
            case OctetBoatCoxedPaddle: return BoatType.OCTUPLE;
            case OctetBoatCoxed: return BoatType.EIGHT;
            default: return null;
        }
    }

    public static boolean isHasCoxswain(BoatTypeEnum boatType){
        if(boatType.equals(DoublePaddleCoxed) || boatType.equals(DoubleCoxed) || boatType.equals(QuartetBoatCoxed)
                || boatType.equals(QuartetBoatPaddleCoxed) || boatType.equals(OctetBoatCoxedPaddle) || boatType.equals(OctetBoatCoxed))
            return true;
        else
            return false;
    }
}
