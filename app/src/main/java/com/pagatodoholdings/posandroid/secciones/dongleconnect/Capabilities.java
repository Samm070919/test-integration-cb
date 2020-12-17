package com.pagatodoholdings.posandroid.secciones.dongleconnect;

import com.pagatodo.sigmalib.emv.PerfilEmvApp;
import com.pagatodoholdings.posandroid.utils.Logger;

public final class Capabilities {
    private static final String TAG = Capabilities.class.getSimpleName();

    private Capabilities() {

    }

    public static String additionalTerminalCapabilitiesCode() {

        int transactionTypeResult = 0x0000;
        int terminalDataInputResult = 0x00;
        int dataOutputResult = 0x0000;

        // Transaction Type Sum
        transactionTypeResult += TRANSACTION_TYPE.CASH.getCode();
        transactionTypeResult += TRANSACTION_TYPE.GOODS.getCode();
        transactionTypeResult += TRANSACTION_TYPE.SERVICES.getCode();
        transactionTypeResult += TRANSACTION_TYPE.CASHBACK.getCode();

        // Terminal Data Input Sum
        terminalDataInputResult += TERMINAL_DATA_INPUT.NUMERIC_KEYS.getCode();
        terminalDataInputResult += TERMINAL_DATA_INPUT.ALPHA_AND_SPECIAL_CHARACTERS.getCode();
        terminalDataInputResult += TERMINAL_DATA_INPUT.COMMAND_KEYS.getCode();
        terminalDataInputResult += TERMINAL_DATA_INPUT.FUNCTION_KEYS.getCode();

        // Data Output Sum
        dataOutputResult += DATA_OUTPUT.PRINT_ATTENDANT.getCode();
        dataOutputResult += DATA_OUTPUT.DISPLAY_ATTENDANT.getCode();
        dataOutputResult += DATA_OUTPUT.CODE_TABLE_1.getCode();

        return Integer.toHexString(transactionTypeResult) +
                Integer.toHexString(terminalDataInputResult) +
                Integer.toHexString(dataOutputResult);
    }

    public enum TRANSACTION_TYPE {
        CASH(0x8000),
        GOODS(0x4000),
        SERVICES(0x2000),
        CASHBACK(0x1000),
        INQUIRY(0x0800),
        TRANSFER(0x0400),
        PAYMENT(0x0200),
        ADMINISTRATIVE(0x0100),
        CASH_DEPOSIT(0x0080);

        private final int transactionType;

        TRANSACTION_TYPE(final int transactionType) {
            this.transactionType = transactionType;
        }

        public int getCode() {
            return this.transactionType;
        }
    }

    public enum TERMINAL_DATA_INPUT {
        NUMERIC_KEYS(0x80),
        ALPHA_AND_SPECIAL_CHARACTERS(0x40),
        COMMAND_KEYS(0x20),
        FUNCTION_KEYS(0x10);

        private final int terminalDataInput;

        TERMINAL_DATA_INPUT(final int terminalDataInput) {
            this.terminalDataInput = terminalDataInput;
        }

        public int getCode() {
            return this.terminalDataInput;
        }
    }

    public enum DATA_OUTPUT {
        PRINT_ATTENDANT(0x8000),
        PRINT_CARDHOLDER(0x4000),
        DISPLAY_ATTENDANT(0x2000),
        DISPLAY_CARDHOLDER(0x1000),
        CODE_TABLE_10(0x0200),
        CODE_TABLE_9(0x0100),
        CODE_TABLE_8(0x0080),
        CODE_TABLE_7(0x0040),
        CODE_TABLE_6(0x0020),
        CODE_TABLE_5(0x0010),
        CODE_TABLE_4(0x0008),
        CODE_TABLE_3(0x0004),
        CODE_TABLE_2(0x0002),
        CODE_TABLE_1(0x0001);

        private int dataOutput;

        DATA_OUTPUT(final int dataOutput) {
            this.dataOutput = dataOutput;
        }

        public int getCode() {
            return this.dataOutput;
        }
    }

    public static String terminalCapabilitiesCode(final PerfilEmvApp perfilesEmv) {

        String codigoCapabilities = "60";

        if(perfilesEmv.getPerfilesEmv() != null) {

            if ( ValidatePerfilEMV.isFailedChip() ) {
                codigoCapabilities = "40";
            }

        int result = 0x00;
        try {
            /*VALIDAMOS EL TIPO DE CONFIGURACION QUE TENDRA EL DONGGLE*/
            if (perfilesEmv.getPerfilesEmv().getCvmPinOnline() == 1) {
                result += CVM_TYPE.PINONLINE.cvmtype() + CVM_TYPE.PLAINTEXTPINICC.cvmtype();
            }

            if (perfilesEmv.getPerfilesEmv().getCvmPinOffline() == 1) {
                result += CVM_TYPE.PINOFFLINE.cvmtype();
            }

            if (perfilesEmv.getPerfilesEmv().getCvmFirma() == 1) {
                result = result + CVM_TYPE.SIGNATURE.cvmtype();

            }/* NO CVM  SOLO  CUANDO LOS DEMAS VALORES ESTAN DESHABILITADOS */

            if (perfilesEmv.getPerfilesEmv().getCvmPinOnline() == 0 && perfilesEmv.getPerfilesEmv().getCvmPinOffline() == 0 && perfilesEmv.getPerfilesEmv().getCvmFirma() == 0) {
                result = result + CVM_TYPE.NOCVM.cvmtype();
            }
            /*AGREGAMOS EL CODIGO DE LA VALIDACION DE PIN */
            final StringBuilder stringBufferCodigoCapabilities = new StringBuilder();
            stringBufferCodigoCapabilities.append(codigoCapabilities).append(Integer.toHexString(result)).append(getSecurityMethods());
            codigoCapabilities = stringBufferCodigoCapabilities.toString();

        } catch (Exception ex) {
            Logger.LOGGER.throwing(TAG, 1, ex, "Error al Configurar las Capacidades del dongle");

            }
        }else {
            return "E0B8C8";
        }

        return codigoCapabilities;
    }

    public static String getSecurityMethods() {
        int result = 0x00;
        try {
            for (final SECURITY_TYPE st : SECURITY_TYPE.values()) {
                result += st.securityType();
            }
        } catch (Exception ex) {
            Logger.LOGGER.throwing(TAG, 1, ex, "Error al Obtener Metodos de Segurridad");
        }
        return Integer.toHexString(result);
    }

    /*DECLARAMOS LOS ENUMERADORES PARA LAS CAPABILITIES DEL DONGGLE*/
    public enum CARD_TYPE {
        BAND(0x40),
        CHIP(0x20);

        private final int cardType;

        CARD_TYPE(final int card) {
            this.cardType = card;
        }

        public int cardType() {
            return cardType;
        }
    }

    public enum CVM_TYPE {
        PLAINTEXTPINICC(0x80),
        PINONLINE(0x40),
        SIGNATURE(0x20),
        PINOFFLINE(0x10),
        NOCVM(0x08);

        private int cvmtype;

        CVM_TYPE(final int cvmtype) {
            this.cvmtype = cvmtype;
        }

        public int cvmtype() {
            return cvmtype;
        }
    }

    public enum SECURITY_TYPE {
        SDA(0x80),
        DDA(0x40),
        CDA(0x08);

        private int securityType;

        SECURITY_TYPE(final int securitytype) {
            this.securityType = securitytype;
        }

        public int securityType() {
            return securityType;
        }
    }

    public static String getTerminalExecute(final PerfilEmvApp perfilEmvApp) {
        final String NumberBase = "000000000000";
        final StringBuilder finalNumber = new StringBuilder(NumberBase);
        try {
            return finalNumber.substring(0, NumberBase.length() - perfilEmvApp.getEmvMonedas().getLimiteOnline().toString().length()) + perfilEmvApp.getEmvMonedas().getLimiteOnline().toString();
        } catch (Exception exe) {
            Logger.LOGGER.throwing(TAG, 1, exe, "Error al obtener el terminalExecuteNumber");

            if (perfilEmvApp.getPerfilesEmv() == null) {

                return "000000000001";
            } else {

                return NumberBase;
            }
        }
    }
}
