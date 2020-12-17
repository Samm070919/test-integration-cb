package com.pagatodoholdings.posandroid.secciones.formularios.transporte.fcqposmifare;
/**
 *
 * @author ealbertos
 */
public class MifareCommand {

    public String getDoMifareCardParas() {
        return doMifareCardParas;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public int getMafireLen() {
        return mafireLen;
    }

    public String getBlockAddr() {
        return blockAddr;
    }

    public enum KeyType {
        KEY_A("Key A", "60"),
        KEY_B("Key B", "61");

        final String qposKeyCommand;

        final String mifareCommand;

        KeyType(final String qposKeyCommand, final String mifareCommand) {
            this.qposKeyCommand = qposKeyCommand;
            this.mifareCommand = mifareCommand;
        }

        public String getQposKeyCommand() {
            return qposKeyCommand;
        }

        public String getMifareCommand() {
            return mifareCommand;
        }

    }

    public enum Command {
        POLL("01"),
        AUTHENTICATE("02"),
        READ("03"),
        WRITE("04"),
        FINISH("0E"),
        INCREMENT("05add"),
        DECREMENT("05reduce"),
        RESTORE("05restore"),
        TRANSFER("0F"),
        BATCHREAD("10"),
        BATCHWRITE("11");

        final String qposCommand;

        Command(final String qposCommand) {
            this.qposCommand = qposCommand;
        }

        public String getQposCommand() {
            return qposCommand;
        }

    }
    

    private final String doMifareCardParas ;
    private final String keyValue ;
    private final int mafireLen ;
    private final String blockAddr ;

    public MifareCommand(String doMifareCardParas, String keyValue, int mafireLen, String blockAddr) {
        this.doMifareCardParas = doMifareCardParas;
        this.keyValue = keyValue;
        this.mafireLen = mafireLen;
        this.blockAddr = blockAddr;
    }
    

}
