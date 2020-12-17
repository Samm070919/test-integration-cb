package com.pagatodoholdings.posandroid.secciones.dongleconnect;

public class Validationexe extends Throwable {

    //Validaciones actuales
    public enum VALIDATION_ERROR {
        ERROR_EXPIRY_DATE,
        ERROR_FALLBACK,
        ERROR_CASHBACK_PERFIL,
        ERROR_CASHBACK_PAIS,
        ERROR_BINES
    }

    private final VALIDATION_ERROR error;

    public Validationexe(final VALIDATION_ERROR validationError) {
        this.error = validationError;

    }

    @Override
    public String getMessage() {
        if (error == VALIDATION_ERROR.ERROR_FALLBACK) {
            return "Fallback No soportado en esta transaccion ";
        } else if (error == VALIDATION_ERROR.ERROR_EXPIRY_DATE) {
            return "Tarjeta vencida";
        } else if (error == VALIDATION_ERROR.ERROR_CASHBACK_PERFIL) {
            return "CashBack No soportado por el perfil del usuario ";
        } else if (error == VALIDATION_ERROR.ERROR_CASHBACK_PAIS) {
            return "CashBack No soportado, La Tarjeta no permite retiros en este Pais";
        } else if (error == VALIDATION_ERROR.ERROR_BINES) {
            return "Tarjeta Invalida";
        }

        return super.getMessage();
    }
}