package com.pagatodoholdings.posandroid;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pagatodoholdings.posandroid.componentesvista.animaciones.AnimacionAlfa;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.ActivitySplashBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.inicio.InicioActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosUsuarioRegistroCliente;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.EMAIL;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class SplashActivity extends AbstractActivity {//NOPMD //NOSONAR Nivel de herencia mayor de 5

    //----------UI-------------------------------------------------------

    //-----Inst ----------------------------------------------------------

    //----- Var ----------------------------------------------------------
    private static final int TIEMPO_ANIMACION_SPLASH = 1200;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
    private static final String TAG = SplashActivity.class.getSimpleName() ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        getAndroidId();
        if (intent.getBooleanExtra(getString(R.string.error_fatal_key), false)) {
            despliegaModal(true, false, getString(R.string.app_name), "Hubo un Error", SplashActivity.this::empiezaSplash);
        } else {
            empiezaSplash();
            iniciarCanalNotificaciones();
        }
    }

    private void getAndroidId(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            try {
                MposApplication.getInstance().setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            }catch (SecurityException e){
                LOGGER.warning("SECURITY EXCEPTION", e.hashCode(),
                        "HUBO UN ERROR AL OBTENER EL ID DEL DISPOSITIVO: " +e.getMessage());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Auth Firebase

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Log.d(TAG, "signIn:success");
        }else{
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            Toast.makeText(SplashActivity.this, "Auth Firebase SUCCESS",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    });

        }
    }

    private void iniciarCanalNotificaciones() {
        mAuth = FirebaseAuth.getInstance();
        checkGooglePlayServices();

        initFirebaseCloudMessage();

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,"M_CH_ID")
                        .setSmallIcon(R.drawable.ic_push_notification)
                        .setContentTitle("Wallet")
                        .setContentText("Bienvenido")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);


        final NotificationManager notificationManager  =  createNotificationChannel ();
        if(notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    private void empiezaSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            validarPermisos();
        }else {
            iniciaSplash();
        }
    }

    private void cargaUiSplash() {
        final ActivitySplashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        binding.txtVersion.setText(MessageFormat.format("Versión: {0}", configManager.getVersionName()));
        preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));
        DatosUsuarioRegistroCliente.getInstace().setSerie(preferenceManager.getValue(NUMERO_SERIE,""));
        setTheme(R.style.AppTheme);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void validarPermisos() {
        final List<String> permissionsList = new ArrayList<>();
        agregarPermiso(permissionsList, Manifest.permission.CAMERA);
        agregarPermiso(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
        agregarPermiso(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION);
        agregarPermiso(permissionsList, Manifest.permission.READ_PHONE_STATE);
        agregarPermiso(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        agregarPermiso(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!permissionsList.isEmpty()) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }else {
            iniciaSplash();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void agregarPermiso(final List<String> permissionsList, final String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            shouldShowRequestPermissionRationale(permission);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            final Map<String, Integer> perms = new HashMap<>();
            llenaListaPermisos(perms, permissions, grantResults);
            if (!isTienePermisos(perms)){
                boolean isExistePermisoNegado = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (!shouldShowRequestPermissionRationale(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isExistePermisoNegado = true;
                    }
                }
                if (isExistePermisoNegado) {
                    despliegaModal(true, true, getString(R.string.app_name), getString(R.string.permisos_warning),
                            new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                @Override
                                public void onCancel() {
                                    finish();
                                }

                                @Override
                                public void onAccept() {
                                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:" + getPackageName()));
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    return;
                }
                despliegaModal(true, true, getString(R.string.app_name), getString(R.string.permisos_warning),
                        new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                            @Override
                            public void onCancel() {
                                finish();
                            }

                            @Override
                            public void onAccept() {
                                validarPermisos();
                            }
                        });
            }else {
                iniciaSplash();
            }
        }
    }

    private boolean isTienePermisos(final Map<String, Integer> permissions) {
        return permissions.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                permissions.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void llenaListaPermisos(final Map<String, Integer> perms, final String[] permissions, final int... grantResults) {
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
    }

    private void iniciaSplash() {
        cargaUiSplash();
        new AnimacionAlfa(findViewById(R.id.iv_logo), 1.0f, TIEMPO_ANIMACION_SPLASH, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animator) {
                findViewById(R.id.iv_logo).setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(final Animator animator) {
                if (preferenceManager.isExiste(EMAIL)) {
                    cambiaDeActividad(LoginActivity.class);
                }else {
                    cambiaDeActividad(InicioActivity.class);
                }
            }

            @Override
            public void onAnimationCancel(final Animator animator) { /*Nothing*/ }

            @Override
            public void onAnimationRepeat(final Animator animator) { /*Nothing*/ }
        });
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        finish();
    }

    @Override
    protected void initUi() {
        //metodo vacio, se necesita inicializar despues para el caso de los permisos
    }

    private NotificationManager createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String channelId = getString(R.string.default_notification_channel_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_id);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }else
            return null;

    }

    void checkGooglePlayServices()
    {
        GoogleApiAvailability.getInstance()
                .makeGooglePlayServicesAvailable(this)
                .addOnSuccessListener((Void ignored)->LOGGER.fine(TAG,"makeGooglePlayServicesAvailable().onSuccess()")
                ).addOnFailureListener(this,(Exception exe)-> {
                LOGGER.warning(TAG,1,exe.getMessage());
                Toast.makeText(SplashActivity.this,
                        "Google Play services upgrade required",
                        Toast.LENGTH_SHORT).show();
                // can't proceed without GPS; quit
                SplashActivity.this.finish(); // this causes a crash
        });
    }


    public void initFirebaseCloudMessage(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener((Task<InstanceIdResult> task)-> {
                        if (!task.isSuccessful()) {
                            LOGGER.fine(TAG,"getInstanceId failed");
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.ToastToken);
                        Log.d(TAG, msg
                                + "  " + token);
                });
    }






}