package com.pagatodoholdings.posandroid.utils;

import android.os.Environment;

public final class Constantes {

    public static final String UPDATE_APK = "UPDATE_APK";
    public static final String ID_APP = "ID_APP";
    public static final int TIEMPO_ENTRE_CLICKS = 1000;
    public static final String VERSION_DATA_BASE = "1";
    public static final String SQL_CONNECTOR = "jdbc:sqlite3://";
    public static final int DECIMALES_INICIALES = 2;

    //---------------SharedPreferences-------------------------------------
    public static final String PREFERENCE_SETTINGS = "PREFERENCE_SETTINGS";

    //---------------Timers ---------------------------------------------//
    public static final int TIEMPO_TRANSICIONES = 300;
    public static final int TIEMPO_CARGA_FRAGMENTS = 600;
    public static final int TIEMPO_INACTIVIDAD_DEFAULT = 18000000;

    public static final String REGISTRO_INTERNO = "REGISTRO_INTERNO";
    public static final String REGISTRO_EXTERNO = "REGISTRO_EXTERNO";
    public static final String COLOR_APP_AZUL = "AZUL";

    //----------------------Tipo Operacion--------------------------------
    public static final String VENTA = "V"; //valor de prueba
    public static final String DEVOLUCION = "D"; //valor de prueba
    public static final String CONSULTA_X = "X"; //valor de prueba
    public static final String CONSULTA_Z = "Z"; //valor de prueba
    public static final String SINCRONIZACION  = "J"; //valor de prueba
    public static final String CIERRE = "CIERRE";

    //------------------------Impresora-------------------------------------
    public static final String TICKET_SMALL = "¸";
    public static final String TICKET_NORMAL = "‗";
    public static final String TICKET_MEDIUM = "░";
    public static final String TICKET_LARGE = "▓";


    public static final int NOPAPER = 3;
    public static final int LOWBATTERY = 4;
    public static final int PRINTVERSION = 5;
    public static final int PRINTBARCODE = 6;
    public static final int PRINTQRCODE = 7;
    public static final int PRINTPAPERWALK = 8;
    public static final int PRINTFORMATTEXT = 9;
    public static final int PRINTTEXT = 17;
    public static final int CANCELPROMPT = 10;
    public static final int PRINTERR = 11;
    public static final int OVERHEAT = 12;
    public static final int PRINTPICTURE = 14;
    public static final int NOBLACKBLOCK = 15;
    public static final int PRINTPICTURECONTENT = 16;

    public static final boolean LAUNCHER = true;

    //------------------------Update apk-----------------------
    public static final String NOMBRE_APK = "apk_update.apk";
    public static final String ACTION_UPDATE_END = "com.pagatodoholdings.posandroid.UPDATE_END";

    public static final String PREFIJOIMAGEN = "ImageProfile.png";

    //QR
    public static final int RC_SCAN = 1444;
    public static final int REQUEST_CODE_ADQ = 117;
    public static final int RESULT_CODE_ADQ = 54;
    private Constantes() {
    }


    public static final String NOTIFICATIONS_CHANNEL_ID = "channel_id";
    public static final String NOTIFICATIONS_CHANNEL_NAME = "channel_name";

    public static final int FORMA_PAGO_TARJETA = 1;
    public static final int FORMA_PAGO_PSE = 2;
    public static final String FORMA_PAGO_COF = "FORMA_PAGO_COF";
    public static final String FORMA_PAGO_PSE_CAD = "FORMA_PAGO_PSE";

    public static final String TARJETA_MASTER_CARD = "MASTERCARD";
    public static final String TARJETA_VISA = "VISA";
    public static final String TARJETA_DINNER = "DINER";
    public static final String TARJETA_AMEX = "AMERICAN EXPRESS";

    public static final String IS_REGISTER = "IS_REGISTER";
    public static final int NON_ACTIVITY_IS_CALLING = 0;
    public static final int REQUEST_ADD_ACCOUNT_BY_MENU = 111;
    public static final int REQUEST_ADD_CARD_BY_MENU = 222;
    public static final int REQUEST_DATOS_NEGOCIO_BY_MENU = 333;
    public static final int REQUEST_COMPRA_KIT_BY_MENU = 444;
    public static final String ACTIVITY_CODE_KEY = "ActivityCode";
    public static final int REQUEST_ADD_CARD_BY_COMPRA_KIT = 555;
    public static final int REQUEST_REGISTERPROFESIONISTA_BY_BUTTON = 666;
    public static final int REQUEST_COMPRA_KIT_BY_COBRAR = 777;
    public static final int REQUEST_COMPRA_KIT_BY_MINEGOCIO = 888;
    public static final String SKIP_FIRST_PAGE = "SKIP_FIRST_PAGE";
}
