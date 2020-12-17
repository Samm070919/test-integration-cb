package com.pagatodoholdings.posandroid.secciones.dongleconnect;

import com.pagatodo.sigmalib.EmvManager;
import com.pagatodoholdings.posandroid.utils.Logger;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.EmvRangoBinCuotas;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.PerfilesEmv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class ValidatePerfilEMV {

    private static final String TAG = ValidatePerfilEMV.class.getSimpleName();
    private static boolean sIsFailedChip;

    private ValidatePerfilEMV() {
    }

    public static boolean isFailedChip() {
        return sIsFailedChip;
    }

    public static void setFailedChip(final boolean isFailedChip) {
        sIsFailedChip = isFailedChip;
    }

    public static void validatefallback(final PerfilesEmv perfilesEmv, final String serviceCode, final boolean fallbackActivado) throws Validationexe {
        Logger.LOGGER.info(TAG, serviceCode);
        if (!fallbackActivado && perfilesEmv.getChkPermiteFallback() == 1 && isChipcard(serviceCode)) {
            throw new Validationexe(Validationexe.VALIDATION_ERROR.ERROR_FALLBACK);
        }
    }

    public static boolean isChipcard(final String serviceCode) {
        return serviceCode.charAt(0) == '2' || serviceCode.charAt(0) == '6';
    }

    public static boolean validateNIPifnecessary(final String serviceCode) {

        return serviceCode.charAt(2) == '0' || serviceCode.charAt(2) == '3' || serviceCode.charAt(2) == '5' || serviceCode.charAt(2) == '6' || serviceCode.charAt(2) == '7';
    }

    public static void validateDateOfExpiry(final PerfilesEmv perfilesEmv, final String expireDate) throws Validationexe {
        if (perfilesEmv != null && perfilesEmv.getChkFechaCaducidad() == 1) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy", Locale.getDefault());
            simpleDateFormat.setLenient(false);
            try {
                if (simpleDateFormat.parse(expireDate).before(new Date())) {
                    throw new Validationexe(Validationexe.VALIDATION_ERROR.ERROR_EXPIRY_DATE);
                }
            } catch (final ParseException exception) {
                Logger.LOGGER.throwing(TAG, 1, exception, "Error al Obtener la Fecha de Vencimiento");
            }
        }
    }

    public static boolean validateBinCuotas(final PerfilesEmv perfilesEmv, final String pan) {
        final List<EmvRangoBinCuotas> rangoCuotas = EmvManager.getRangoCuotas(perfilesEmv.getLstCuotasMes());

        if (!rangoCuotas.isEmpty()) {
            int bin = Integer.parseInt(pan.substring(0, 6));

            for (final EmvRangoBinCuotas cuota : rangoCuotas) {
                int minBin = Integer.parseInt(cuota.getMinbin());
                int maxBin = Integer.parseInt(cuota.getMaxbin());

                if (bin >= minBin && bin <= maxBin) {
                    return true;
                }
            }
        }

        return false;
    }
}
