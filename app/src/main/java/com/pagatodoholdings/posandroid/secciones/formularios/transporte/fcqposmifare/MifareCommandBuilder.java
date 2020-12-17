package com.pagatodoholdings.posandroid.secciones.formularios.transporte.fcqposmifare;

public class MifareCommandBuilder {

    private String doMifareCardParas;
    private String keyValue = "";
    private int mafireLen = 0;
    private String blockAddr;

    private MifareCommandBuilder() {
    }

    public static final MifareCommandBuilder builder() {
        return new MifareCommandBuilder();
    }

    public MifareCommandBuilder setDoMifareCardParas(String doMifareCardParas) {
        this.doMifareCardParas = doMifareCardParas;
        return this;
    }

    public MifareCommandBuilder setKeyValue(String keyValue) {
        this.keyValue = keyValue;
        return this;
    }

    public MifareCommandBuilder setMafireLen(int mafireLen) {
        this.mafireLen = mafireLen;
        return this;
    }

    public MifareCommandBuilder setBlockAddr(String blockAddr) {
        this.blockAddr = blockAddr;
        return this;
    }

    public MifareCommand createMifareCommand() {
        return new MifareCommand(doMifareCardParas, keyValue, mafireLen, blockAddr);
    }

}
