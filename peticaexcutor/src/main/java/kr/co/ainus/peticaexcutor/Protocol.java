package kr.co.ainus.peticaexcutor;

import kr.co.ainus.peticaexcutor.type.CommandType;
import kr.co.ainus.peticaexcutor.type.DataType;
import kr.co.ainus.peticaexcutor.type.ModeType;

abstract public class Protocol {
    private CommandType commandType;
    private ModeType modeType;
    private DataType dataType;
    private byte hour1;
    private byte min1;
    private byte timer1;

    public Protocol(CommandType commandType, ModeType modeType, DataType dataType, byte hour1, byte min1, byte timer1) {
        this.commandType = commandType;
        this.modeType = modeType;
        this.dataType = dataType;
        this.hour1 = hour1;
        this.min1 = min1;
        this.timer1 = timer1;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public ModeType getModeType() {
        return modeType;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public byte getHour1() {
        return hour1;
    }

    public void setHour1(byte hour1) {
        this.hour1 = hour1;
    }

    public byte getMin1() {
        return min1;
    }

    public void setMin1(byte min1) {
        this.min1 = min1;
    }

    public byte getTimer1() {
        return timer1;
    }

    public void setTimer1(byte timer1) {
        this.timer1 = timer1;
    }

    abstract public byte[] getValue();
}

