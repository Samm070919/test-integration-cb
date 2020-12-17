package com.pagatodoholdings.posandroid.utils;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Esta clase te permite crear un timer para time outs.
 */
public class TimerUtil {

    private Disposable mUserSubscription;
    private Disposable mDisposableSubscription;
    private Observable<Long> mObservableInterval;

    /**
     * Crea un timer, te regresará el tiempo sobrante según la unidad de tiempo que le pases.
     * Si el timer llega a 0 este se desechará a sí mismo
     * Cuando ya no necesites el timer o la actividad o fragmento que lo estaba usando ya no está
     * siendo utilizado. Desecha el timer llamando {@link #disposeTimer()}
     *
     * @param time            El tiempo total del timer
     * @param timeUnit        TimeUnit, mili segundos, segundos, etc
     * @param onRemainingTime Este es el callback que te regresa el tiempo que te queda
     */
    public void createCountdownTimer(final long time, TimeUnit timeUnit, Consumer<Long> onRemainingTime) {
        if (time < 0) {
            return;
        }

        mObservableInterval = Observable
                .interval(1, timeUnit)
                .map(elapsedTime -> time - elapsedTime);

        final Subject<Long> subject = PublishSubject.create();

        mUserSubscription = subject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                onRemainingTime,
                        throwable -> {},
                        this::disposeTimer);

        mDisposableSubscription = mObservableInterval
                .subscribe(elapsedTime -> {
                    subject.onNext(elapsedTime);
                    if (elapsedTime == 0) {
                        subject.onComplete();
                    }
                });
    }

    /**
     * Esta es una llamada segura, ya que verifica que exista un timer vigente antes de intentar
     * eliminarla
     */
    public void disposeTimer() {
        if (mUserSubscription != null && !mUserSubscription.isDisposed()) {
            mUserSubscription.dispose();
        }

        if (mDisposableSubscription != null && !mDisposableSubscription.isDisposed()) {
            mDisposableSubscription.dispose();
        }

        mObservableInterval = null;
    }
}
