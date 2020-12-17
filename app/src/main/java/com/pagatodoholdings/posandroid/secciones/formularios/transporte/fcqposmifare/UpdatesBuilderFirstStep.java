package com.pagatodoholdings.posandroid.secciones.formularios.transporte.fcqposmifare;



import com.pagatodoholdings.posandroid.utils.Logger;
import net.fullcarga.android.api.formulario.AccionEscritura;
import net.fullcarga.android.api.formulario.FormatoCardWrite;
import net.fullcarga.android.api.formulario.FormatoKeyStore;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Step builder para crear un conversor del Formato de Escritura devuelto por la
 * API del Host a los comandos batch de lectura Mifare para el Dspread QPOS mini
 * <p>
 * Uso:
 * <pre>
 * {@code
  FcQposMifare.batchUpdatesFromFormato()
      .setUid("01020304")
      .setFormatoCardWrite(formatoCardWrite)
      .setKeyStore(keyStore)
      .setMaxBloquesEscrituraPorComando(24)
      .setMaxBufferOutputBytes(502)
      .createCommands();
 }
 * </pre>
 *
 * @author ealbertos
 */
public final class UpdatesBuilderFirstStep {

    private static final String BATCH_WRITECOMMAND = "11";
    private static final int ACCION_ESCRITURA = 2;
    private static final int ACCION_DECREMENT = 4;

    private static final int SECTOR_INICIAL = 0x00;
    private static final int SECTOR_FINAL = 0x0F;

    protected static final int LEN_BYTESMINBATCHCOMMAND = 144 + 6;
    protected static final int DEFAULT_MAXOUTPUTBYTEBUFFER = 502;

    private String uid;

    private UpdatesBuilderFirstStep() {
    }

    public static UpdatesBuilderFirstStep batchUpdatesFromFormato() {
        return new UpdatesBuilderFirstStep();
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
        private FormatoCardWrite updates;

        private BuilderSecondStep(final UpdatesBuilderFirstStep previous) {
            this.uid = previous.uid;
        }

        /**
         * Formato de Esscritua a codificar en comandos batch para el QPOS
         *
         * @param updates FormatoCardRead con los rangos a leer
         * @return el siguiente paso del builder
         */
        public BuilderThirdStep setFormatoCardWrite(final FormatoCardWrite updates) {
            this.updates = updates;
            return new BuilderThirdStep(this);
        }

        public static final class BuilderThirdStep {

            private final String uid;
            private FormatoKeyStore keyStore;
            private final FormatoCardWrite updates;

            private BuilderThirdStep(final BuilderSecondStep previous) {
                this.updates = previous.updates;
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
                private final FormatoCardWrite updates;
                private int maxBufferOutputBytes = DEFAULT_MAXOUTPUTBYTEBUFFER;

                BuilderFourthStep(BuilderThirdStep previous) {
                    this.updates = previous.updates;
                    this.uid = previous.uid;
                    this.keyStore = previous.keyStore;
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

                    // repasa las acciones de escritura en sector
                    lista.addAll(
                            partirEnMaxLenPorComandoBatch(
                                    extraeEscriturasSector(updates.getAccionesEscritura(), keyStore)));
                    lista.addAll(extraeDecrementos(updates.getAccionesEscritura(), keyStore));
                    return lista;
                }

                private List<MifareCommand> partirEnMaxLenPorComandoBatch(
                        final List<String> subrangos) {
                    final List<MifareCommand> listaComandoBatch = new LinkedList<>();
                    // separar en comandos
                    StringBuilder comandoBatch = new StringBuilder();

                    for (final String subrango : subrangos) {
                        Logger.LOGGER.fine("","Longitud subrango: " + subrango.length());
                        comandoBatch.append(subrango);
                        if (comandoBatch.length() > (maxBufferOutputBytes * 2) - LEN_BYTESMINBATCHCOMMAND) {
                            listaComandoBatch.add(addBatchReaderHeader(comandoBatch, uid));
                            comandoBatch = new StringBuilder();
                        }
                    }
                    if (comandoBatch.toString().length() > 0) {
                        listaComandoBatch.add(addBatchReaderHeader(comandoBatch, uid));
                    }
                    return listaComandoBatch;
                }

                private static MifareCommand addBatchReaderHeader(final StringBuilder comandoBatch, final String uid) {
                    return MifareCommandBuilder.builder()
                            .setDoMifareCardParas(
                                    new StringBuffer()
                                            .append(BATCH_WRITECOMMAND)
                                            .append(String.format("%02X", uid.length() / 2))
                                            .append(uid)
                                            .append(comandoBatch.toString()).toString())
                            .createMifareCommand();
                }

                private List<String> extraeEscriturasSector(List<AccionEscritura> updates, FormatoKeyStore keystore) {
                    final List<String> lista = new LinkedList<>();
                    final AccionEscritura[] acciones = filtraAcciones(updates, ACCION_ESCRITURA);

                    for (int sector = SECTOR_INICIAL; sector <= SECTOR_FINAL; sector++) {
                        final StringBuilder command = new StringBuilder();
                        boolean nuevoSector = false;
                        int tipoClaveSector = 0;
                        for (int block = 4 * sector; block < 4 * sector + 4; block++) {
                            if (acciones[block] != null) {
                                nuevoSector = true;
                                command.append(String.format("%02X", block));
                                command.append(acciones[block].getBloqueV().toUpperCase());
                                tipoClaveSector = acciones[block].getTipoClave();
                            } else {
                                command.append("FF");
                            }
                        }
                        if (nuevoSector) {
                            command.insert(0,
                                    String.format("%02X", sector)
                                    + (tipoClaveSector == 'A' ? "60" : "61")
                                    + (tipoClaveSector == 'A'
                                            ? keystore.getClaveA(sector).toUpperCase()
                                            : keystore.getClaveB(sector).toUpperCase()));
                            lista.add(command.toString());
                        }

                    }
                    return lista;

                }

                private AccionEscritura[] filtraAcciones(final List<AccionEscritura> updates, final int tipoAccion) {
                    final List<AccionEscritura> listaUpdates = new LinkedList<>(updates);
                    Collections.sort(listaUpdates, (o1, o2) -> Integer.compare(o1.getBloqueDestino(), o2.getBloqueDestino())
                    );

                    final AccionEscritura[] acciones = new AccionEscritura[64];
                    for (final AccionEscritura accion : listaUpdates) {
                        if (tipoAccion == accion.getTipoAccion()) {
                            acciones[accion.getBloqueDestino()] = accion;
                        }
                    }
                    return acciones;
                }

                private Collection<? extends MifareCommand> extraeDecrementos(final List<AccionEscritura> updates, final FormatoKeyStore keystore) {
                    final List<MifareCommand> lista = new LinkedList<>();
                    final AccionEscritura[] acciones = filtraAcciones(updates, ACCION_DECREMENT);
                    int ultimoSector = -1;
                    for (final AccionEscritura decrement : acciones) {
                        if (decrement != null) {
                            int sectorActual = decrement.getBloqueDestino() / 4;
                            if (sectorActual != ultimoSector) {
                                ultimoSector = sectorActual;
                                // autenticar sector
                                lista.add(MifareCommandBuilder
                                        .builder()
                                        .setDoMifareCardParas(MifareCommand.Command.AUTHENTICATE.qposCommand + MifareCommand.KeyType.KEY_B.qposKeyCommand)
                                        .setKeyValue(decrement.getTipoClave() == 'A'
                                                ? keystore.getClaveA(sectorActual).toUpperCase()
                                                : keystore.getClaveB(sectorActual).toUpperCase())
                                        .setBlockAddr(String.format("%02X", decrement.getBloqueDestino()))
                                        .createMifareCommand());

                            }
                            lista.add(MifareCommandBuilder
                                    .builder()
                                    .setDoMifareCardParas(MifareCommand.Command.DECREMENT.qposCommand)
                                    .setKeyValue(formateaValorDecrementoLittleEndian(decrement))
                                    .setBlockAddr(String.format("%02X", decrement.getBloqueDestino()))
                                    .createMifareCommand());
                        }

                    }
                    return lista;
                }

                private static String formateaValorDecrementoLittleEndian(final AccionEscritura decrement) {
                    int valorDecremento = decrement.getBloqueDestino() == decrement.getBloqueOrigen() ? decrement.getValor() : decrement.getValor() * 2;
                    return String.format("%08X", Integer.reverseBytes(valorDecremento));
                }
            }
        }
    }
}
