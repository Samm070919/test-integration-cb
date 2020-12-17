package com.pagatodoholdings.posandroid.secciones.formularios.transporte.fcqposmifare;

import net.fullcarga.android.api.formulario.AccionLectura;
import net.fullcarga.android.api.formulario.FormatoCardRead;
import net.fullcarga.android.api.formulario.FormatoKeyStore;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Step builder para crear un conversor del Formato de Lectura devuelto por la
 * API del Host a los comandos batch de lectura Mifare para el Dspread QPOS mini
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
 * @author ealbertos
 */
public final class ReadsBuilderFirstStep {

    private static final String BATCH_READCOMMAND = "10";

    protected static final int LEN_BYTESMINBATCHCOMMAND = 18;
    protected static final int DEFAULT_MAXOUTPUTBYTEBUFFER = 502;

    private String uid;

    private ReadsBuilderFirstStep() {
    }

    public static ReadsBuilderFirstStep batchReadsFromFormato() {
        return new ReadsBuilderFirstStep();
    }

    /**
     * UID de la tarjeta Mifare objetivo en hex String la longitud debe de ser
     * par
     *
     * @param uid UID de la tarjeta Mifare objetivo en hex String
     * @return el siguiente paso del builder
     */
    public BuilderSecondStep setUid(final String uid) {
        this.uid = uid.toUpperCase();
        return new BuilderSecondStep(this);
    }

    public static final class BuilderSecondStep {

        private final String uid;
        private FormatoCardRead rangosLectura;

        private BuilderSecondStep(final ReadsBuilderFirstStep previous) {
            this.uid = previous.uid;
        }

        /**
         * Formato de Rangos de lectura a codificar en comandos batch para el
         * QPOS
         *
         * @param rangosLectura FormatoCardRead con los rangos a leer
         * @return el siguiente paso del builder
         */
        public BuilderThirdStep setFormatoCardRead(final FormatoCardRead rangosLectura) {
            this.rangosLectura = rangosLectura;
            return new BuilderThirdStep(this);
        }

        public static final class BuilderThirdStep {

            private final String uid;
            private FormatoKeyStore keyStore;
            private final FormatoCardRead rangosLectura;

            private BuilderThirdStep(final BuilderSecondStep previous) {
                this.rangosLectura = previous.rangosLectura;
                this.uid = previous.uid;
            }

            /**
             * Formato Keystore con claves A y B a utilizar
             *
             * @param keyStore FormatoKeyStore con las claves para la tarjeta
             * @return el siguiente paso del builder
             */
            public BuilderFourthStep setKeyStore(final FormatoKeyStore keyStore) {
                this.keyStore = keyStore;
                return new BuilderFourthStep(this);
            }

            public static final class BuilderFourthStep {

                private final String uid;
                private final FormatoKeyStore keyStore;
                private final FormatoCardRead rangosLectura;
                private int maxBloquesLecturaPorComando = 24;
                private int maxBufferOutputBytes = DEFAULT_MAXOUTPUTBYTEBUFFER;

                BuilderFourthStep(BuilderThirdStep previous) {
                    this.rangosLectura = previous.rangosLectura;
                    this.uid = previous.uid;
                    this.keyStore = previous.keyStore;
                }

                /**
                 * Parametro opcional indica cuantos bloques máximos se pueden
                 * pedir por comando batch generado
                 *
                 * @param maxBloquesLecturaPorComando numero máximo de bloques
                 * a pedir por comando batch
                 * @return este builder para terminar de configurar
                 */
                public BuilderFourthStep setMaxBloquesLecturaPorComando(final int maxBloquesLecturaPorComando) {
                    this.maxBloquesLecturaPorComando = maxBloquesLecturaPorComando;
                    return this;
                }

                /**
                 * Parametro opcional indica la capacidad maxima en bytes del
                 * buffer de salida Si se excede el buffer se parten los
                 * comandos en dos
                 *
                 * @param maxBufferOutputBytes numero de byte máximo por
                 * comando batch completo
                 * @return este builder para terminar de configurar
                 */
                public BuilderFourthStep setMaxBufferOutputBytes(final int maxBufferOutputBytes) {
                    this.maxBufferOutputBytes = maxBufferOutputBytes;
                    return this;
                }

                
                /**
                 * Llamada final del builder. Crea la lista de comandos con la
                 * configuracion proporcionada
                 *
                 * @return lista de comandos batch lista para ser enviados uno a
                 * uno con qpos.doMifare
                 */
                public List<MifareCommand> createCommands() {
                    final List<MifareCommand> lista = new LinkedList<>();

                    for(final String cadena:create()) {
                        lista.add(MifareCommandBuilder
                                .builder()
                                .setDoMifareCardParas(cadena)
                                .createMifareCommand());
                    }
                    return lista;
                }
                
                /**
                 * Llamada final del builder. Crea la lista de comandos con la
                 * configuracion proporcionada
                 *
                 * @return lista de comandos batch lista para ser enviados uno a
                 * uno con qpos.doMifare
                 */
                public List<String> create() {
                    final List<String> lista = new LinkedList<>();
                    final List<String> subrangos = new LinkedList<>();
                    for (final AccionLectura rango : rangosLectura.getAccionesLectura()) {
                        subrangos.addAll(getSubrangos(rango, keyStore));
                    }
                    return partirEnMaxBloquesPorComandoBatch(subrangos, lista);
                }

                private List<String> partirEnMaxBloquesPorComandoBatch(
                        final List<String> subrangos,
                        final List<String> listaComandoBatch) {
                    // separar en comandos
                    int bloquesEnComando = 0;
                    StringBuilder comandoBatch = new StringBuilder();
                    for (final String subrango : subrangos) {
                        bloquesEnComando += (4 - cuentaFF(subrango.substring(16, 24)));
                        comandoBatch.append(subrango);
                        if (bloquesEnComando > maxBloquesLecturaPorComando - 3
                                || comandoBatch.length() > (maxBufferOutputBytes * 2) - LEN_BYTESMINBATCHCOMMAND) {
                            listaComandoBatch.add(addBatchReaderHeader(comandoBatch, uid));
                            comandoBatch = new StringBuilder();
                            bloquesEnComando = 0;
                        }
                    }
                    if (bloquesEnComando > 0) {
                        listaComandoBatch.add(addBatchReaderHeader(comandoBatch, uid));
                    }
                    return listaComandoBatch;
                }

                private static String addBatchReaderHeader(final StringBuilder comandoBatch, final String uid) {
                    return new StringBuffer()
                            .append(BATCH_READCOMMAND)
                            .append(String.format("%02X", uid.length() / 2))
                            .append(uid)
                            .append(comandoBatch.toString()).toString();
                }

                private int cuentaFF(final String input) {
                    return input.split("FF", -1).length - 1;
                }

                private static List<String> getSubrangos(final AccionLectura rango,
                        FormatoKeyStore keystore) {

                    final List<String> lista = new LinkedList<>();
                    int sectorInicial = rango.getBloqueIni() / 4;
                    int sectorFinal = rango.getBloqueFin() / 4;

                    for (int sector = sectorInicial; sector <= sectorFinal; sector++) {
                        final StringBuilder command = new StringBuilder();
                        command.append(String.format("%02X", sector))
                                .append(rango.getTipoClave() == 'A' ? "60" : "61")
                                .append(rango.getTipoClave() == 'A'
                                        ? keystore.getClaveA(sector).toUpperCase()
                                        : keystore.getClaveB(sector).toUpperCase());
                        for (int block = 4 * sector; block < 4 * sector + 4; block++) {
                            if (block >= rango.getBloqueIni() && block <= rango.getBloqueFin()) {
                                command.append(String.format("%02X", block));
                            } else {
                                command.append("FF");
                            }
                        }
                        lista.add(command.toString());
                    }

                    return lista;
                }

            }
        }

    }

}
