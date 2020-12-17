package com.pagatodoholdings.posandroid.utils;

import com.pagatodoholdings.posandroid.MposApplication;
import net.fullcarga.android.api.data.StanProvider;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.STAN;

/**
 * @author dsoria
 * Mocks usados por la API Sigma para el manejo de stan en la trasacciones de tipo Venta pointY devolucion
 */
public class StanProviderMock implements StanProvider, Serializable {

    protected AtomicLong stanAtomicLong;
    protected long ultimo;

    public StanProviderMock() {
        //Nothing
        //Nothing to do
    }

    @Override
    public long getNextStan() {
        stanAtomicLong = new AtomicLong(Long.parseLong(MposApplication.getInstance().getPreferenceManager().getValue(STAN, "0"), 10));
        ultimo = calcularNext();
        return ultimo;
    }

    private long calcularNext() {
        final long stan = stanAtomicLong.incrementAndGet();
        if (stan == 999999) {
            stanAtomicLong.set(0);
            return stanAtomicLong.get();
        } else {
            return stan;
        }
    }

    @Override
    public long getUltimo() {
        return ultimo;
    }
}
