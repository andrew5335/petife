package kr.co.ainus.peticaexcutor;

import kr.co.ainus.peticaexcutor.type.CommandType;
import kr.co.ainus.peticaexcutor.type.DataType;
import kr.co.ainus.peticaexcutor.type.ModeType;

public class Protocol10Byte extends Protocol{
    public Protocol10Byte(CommandType commandType, ModeType modeType, DataType dataType, byte hour, byte min, byte timer) {
        super(commandType, modeType, dataType, hour, min, timer);
    }

    @Override
    public byte[] getValue() {
        return new byte[]{
                getCommandType().getValue()
                , getModeType().getValue()
                , getDataType().getValue()
                , getHour1()
                , getMin1()
                , getTimer1()
        };
    }
}
