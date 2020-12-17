package com.pagatodoholdings.posandroid.general.abstracts;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.TipoConfiguracion;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.general.interfaces.RetrocesoListener;
import java.util.Objects;

public abstract class AbstractFragment extends Fragment implements OnFailureListener {

    protected static final String TAG = AbstractFragment.class.getSimpleName();
    protected final TipoConfiguracion tipoConfiguracion;
    protected final ConfigManager configManager;
    protected final PreferenceManager preferenceManager;
    protected final BuildManager buildManager;
    protected FirebaseAnalytics firebaseAnalytics;
    protected RetrocesoListener retrocesoListener;
    protected AbstractActivity activity;

    protected static final int ID = 123;

    protected abstract boolean isTomandoBack();

    public AbstractFragment() {
        this.tipoConfiguracion = MposApplication.getInstance().getTipoConfiguracion();
        this.configManager = MposApplication.getInstance().getConfigManager();
        this.preferenceManager = MposApplication.getInstance().getPreferenceManager();
        this.buildManager = MposApplication.getInstance().getBuildManager();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (AbstractActivity) getActivity();
        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_DOWN) {
                return isTomandoBack();
            }
            return false;
        });
    }

    public void setRetrocesoListener(final RetrocesoListener retrocesoListener) {
        this.retrocesoListener = retrocesoListener;
    }

    public void hideViewElements(ViewGroup father) {
        father.setVisibility(View.GONE);

        if (father.getChildCount() > 0) {
            for (int child = 0; child < father.getChildCount(); child++) {
                father.getChildAt(child).setVisibility(View.GONE);

                if (father.getChildAt(child) instanceof ViewGroup) {
                    hideViewElements((ViewGroup) father.getChildAt(child));
                }
            }
        }
    }
}
