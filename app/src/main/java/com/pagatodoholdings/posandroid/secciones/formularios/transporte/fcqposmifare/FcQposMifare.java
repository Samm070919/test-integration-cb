package com.pagatodoholdings.posandroid.secciones.formularios.transporte.fcqposmifare;

import net.fullcarga.android.api.util.HexUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Clase de utilidad con utilidades para convertir comandos y datos entre la API
 * Host y los comandos doMifare del QPOS mini Dspread
 *
 * @author ealbertos
 */
public class FcQposMifare {

    private static final Charset CHARSET_ISO8859 = StandardCharsets.ISO_8859_1;

    private FcQposMifare() {
        // clase de utilidad no instaciable
    }

    /**
     * Crea los datos para el campo 60 en respuesta a un parametro de acciones
     * de lecturas en batch. El metodo excluye los bloques leidos a cero para
     * optimizar las comunicaciones.
     *
     * @param responses lista de respuestas recibidas
     * @return devuelve los datos para el campo 60 como un String
     */
    public static String readsResponseToParametro60(final List<String> responses) {
        final StringBuilder tramaHexString = new StringBuilder();
        int numeroBloques = 0;
        for (String response : responses) {
            final String[] datos = response.split(":");
            if (!"00000000000000000000000000000000".equals(datos[2])) {
                numeroBloques++;
                tramaHexString.append(datos[0]).append(datos[2]);
            }
        }

        tramaHexString.insert(0, String.format("%02X", numeroBloques));
        tramaHexString.insert(0, String.format("%04d", tramaHexString.length() / 2));

        return new String(HexUtil.hex2byte(tramaHexString.toString(), CHARSET_ISO8859), CHARSET_ISO8859);

    }

    /**
     * Step builder para crear un conversor del Formato de Lectura devuelto por
     * la API del Host a los comandos batch de lectura Mifare para el Dspread
     * QPOS mini
     * <p>
     * Uso:
     * <pre>
     * {@code
     *  FcQposMifare.batchReadsFromFormato()
     *      .setUid("01020304")
     *      .setFormatoCardRead(intervalosLectura)
     *      .setKeyStore(keyStore)
     *      .setMaxBloquesLecturaPorComando(24)
     *      .setMaxBufferOutputBytes(502)
     *      .create();
     * }
     * </pre>
     *
     * @return El primer paso del builder,
     */
    public static final ReadsBuilderFirstStep batchReadsFromFormato() {
        return ReadsBuilderFirstStep.batchReadsFromFormato();
    }

    /**
     * Step builder para crear un conversor del Formato de Lectura devuelto por
     * la API del Host a los comandos batch de lectura Mifare para el Dspread
     * QPOS mini
     * <p>
     * Uso:
     * <pre>
     * {@code
     *  FcQposMifare.batchReadsFromFormato()
     *      .setUid("01020304")
     *      .setFormatoCardRead(intervalosLectura)
     *      .setKeyStore(keyStore)
     *      .setMaxBloquesLecturaPorComando(24)
     *      .setMaxBufferOutputBytes(502)
     *      .create();
     * }
     * </pre>
     *
     * @return El primer paso del builder,
     */
    public static final UpdatesBuilderFirstStep batchUpdatesFromFormato() {
        return UpdatesBuilderFirstStep.batchUpdatesFromFormato();
    }

}
