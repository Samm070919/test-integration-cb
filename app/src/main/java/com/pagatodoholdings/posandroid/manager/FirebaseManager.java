package com.pagatodoholdings.posandroid.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class FirebaseManager {
    private static final String TAG = FirebaseManager.class.getSimpleName();
    private static Subject<String> sSmsCodeObservable;
    private static Disposable sSubscription;
    //Si ya se tiene un número de verificación recibido, no tiene caso pedir otro
    private static String sPhoneCode = null;
    private static String sPhoneNumber = "";
    private static String sCountryCode = "";

    private FirebaseManager() {
//        static class
    }

    /**
     * Este método unicamente manda a llamar el evento de validar el teléfono, no regresa ningún
     * tipo de dato, es para hacer el proceso más rápido
     *
     * @param countryCode ejemplo: "+52"verifyPhoneNumber
     * @param phoneNumber ejemplo: "5512345678"
     * @param timeout     Tiempo en segundos para el timeout
     * @param activity    la actividad de la clase
     */
    @SuppressLint("CheckResult")
    public static void startPhoneVerification(
            final String countryCode,
            final String phoneNumber,
            final long timeout,
            final Activity activity) {
        sPhoneNumber = phoneNumber;
        sCountryCode = countryCode;
        if (sPhoneCode == null) {
            Log.d(TAG, "Llamando conexión a firebase observable");
            sSmsCodeObservable = AsyncSubject.create();
            sendSmsCode(countryCode + phoneNumber, timeout, activity);
        } else {
            Log.d(TAG, "Un código ya fue recibido: " + sPhoneCode);
            sSmsCodeObservable.onNext(sPhoneCode);
            sSmsCodeObservable.onComplete();
        }
    }

    /**
     * Este método te permite suscribirte a escuchar el código recibido por firebase sin tener
     * que manejar el ciclo de vida de tu suscripción, ya que se destruye a sí misma una vez que
     * termina. (No se destruye a sí misma si nadie se ha suscrito).
     * <p>
     * Si ya se ha recibido un código de verificación, No se repite el proceso de firebase, el códi
     * go se manda directamente sin más preámbulo. Se desecha la suscripción anterior antes de crear
     * una nueva (si es que existía una)
     *
     * @param onNext  Aquí debes implementar lo que harás con el código
     * @param onError Aquí debes implementar cómo manejar el error
     */
    public static void subscribeToPhoneVerification(Consumer<String> onNext, Consumer<Throwable> onError) {
        if (sSmsCodeObservable == null) {
            return;
        }

        sSubscription = sSmsCodeObservable.subscribe(onNext, onError, FirebaseManager::disposeSubscription);
    }

    public static String getLastPhoneNumberUsed() {
        return sPhoneNumber;
    }

    public static String getLastCountryCodeUsed() {
        return sCountryCode;
    }

    private static void disposeSubscription() {
        sSubscription.dispose();
    }

    private static void sendSmsCode(String numberWithPlus, long timeout, Activity activity) {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa")) {
            overcomeVal(numberWithPlus);
        }

        FirebaseAuth.getInstance().setLanguageCode("es");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numberWithPlus,
                timeout,
                TimeUnit.SECONDS,
                activity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "Verificacion completada");
                        String smsCode = phoneAuthCredential.getSmsCode();

                        if (smsCode != null) {
                            sPhoneCode = smsCode;
                            sSmsCodeObservable.onNext(smsCode);
                        } else {
                            sSmsCodeObservable.onError(new Exception("Código no fue obtenido"));
                        }
                        sSmsCodeObservable.onComplete();
                    }

                    @Override
                    public void onVerificationFailed(@NotNull FirebaseException exception) {
                        Log.e(TAG, "onVerificationFailed: ", exception);
                        sSmsCodeObservable.onError(new Exception(exception.getMessage()));
                        sSmsCodeObservable.onComplete();
                    }

                    @Override
                    public void onCodeSent(@NotNull String verificationId, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Log.d(TAG, "onCodeSent");
                        super.onCodeSent(verificationId, forceResendingToken);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NotNull String verificationId) {
                        Log.d(TAG, "onCodeAutoRetrievalTimeOut() called with: verificationId = [" + verificationId + "]");
                        sSmsCodeObservable.onError(new Exception("Se agotó el tiempo de espera"));
                        sSmsCodeObservable.onComplete();
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                    }
                });
    }

    public static void enablePushNotifications(final String pais, final String tpvcod) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                LOGGER.fine(TAG, "Token from Firebase: " + task.getException());
                                return;
                            }
                            final String token = task.getResult().getToken();
                            final String appId = MposApplication.getInstance().getConfigManager().getNotificacionApplicationId().replace("_", ".");
                            LOGGER.fine(TAG, "Token from Firebase: " + token);
                            Map<String, Object> map = new HashMap<>();
                            map.put("created", new Date());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef =
                                    db.collection(MposApplication.getInstance().getString(
                                            R.string.wallet_notificacion_path_1,
                                            appId.substring(
                                                    0,
                                                    appId.length() - 3
                                            ),
                                            pais
                                    )).document(tpvcod);
                            docRef.get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    DocumentSnapshot document = task12.getResult();
                                    if (!document.exists()) {
                                        Logger.LOGGER.fine(TAG, "No such document");
                                        CollectionReference reference =
                                                db.collection(
                                                        MposApplication.getInstance().getString(
                                                                R.string.wallet_notificacion_path_1,
                                                                appId.substring(
                                                                        0,
                                                                        appId.length() - 3)
                                                                ,
                                                                pais
                                                        )
                                                );
                                        reference.document(tpvcod)
                                                .set(map);
                                    }
                                } else {
                                    Logger.LOGGER.fine(TAG, "get failed with " + task12.getException());
                                }
                            });

                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("token", token);
                            db.collection(MposApplication.getInstance().getString(
                                    R.string.wallet_notificacion_path_2,
                                    appId.substring(
                                            0,
                                            appId.length() - 3),
                                    pais,
                                    tpvcod
                            ))
                                    .document("Dispositivo")
                                    .set(map1)
                                    .addOnSuccessListener(aVoid -> LOGGER.fine(TAG, "Document added"))
                                    .addOnFailureListener(e -> LOGGER.fine(TAG, "Error adding doc" + e));
                        }
                ).addOnFailureListener(e -> Logger.LOGGER.fine(TAG, "Error: " + e.getMessage()));
    }

    private static void overcomeVal(final String numberWithPlus) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("+525576959467", "123456"); // Mexico
        hashMap.put("+573213839171", "445566"); // Colombia
        hashMap.put("+51955444066", "112233"); // Peru

        if (hashMap.containsKey(numberWithPlus)) {
            FirebaseAuth.getInstance().getFirebaseAuthSettings()
                    .setAutoRetrievedSmsCodeForPhoneNumber(numberWithPlus, hashMap.get(numberWithPlus));
        }
    }
}
