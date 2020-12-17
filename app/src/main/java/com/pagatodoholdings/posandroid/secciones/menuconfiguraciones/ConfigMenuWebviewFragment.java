package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuWebviewBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigMenuWebviewFragment extends Fragment {

    private static final String TAG = ConfigMenuWebviewFragment.class.getName();
    private static final String RESOURCE = "id_resource";
    private static final String MENU = "nombreMenu";

    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "utf-8";

    //----------UI-------------------------------------------------------
    private FragmentConfigMenuWebviewBinding binding;

    //----- Var ----------------------------------------------------------
    private int resourceid;
    private String nombreMenu;

    public ConfigMenuWebviewFragment() {
        // Required empty public constructor
    }

    public static ConfigMenuWebviewFragment newInstance(final int param1, final String param2) {
        final ConfigMenuWebviewFragment fragment = new ConfigMenuWebviewFragment();
        final Bundle args = new Bundle();
        args.putString(MENU, param2);
        args.putInt(RESOURCE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreMenu = getArguments().getString(MENU);
            resourceid = getArguments().getInt(RESOURCE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        final ProgressBar barProgress;
        binding = FragmentConfigMenuWebviewBinding.inflate(infalter, container, false);
        barProgress = binding.progressBar;
        barProgress.setVisibility(View.VISIBLE);
        final WebView viewWeb = binding.webText;
        viewWeb.setWebViewClient(new WebViewClient());

        viewWeb.setOnLongClickListener(view -> true);
        viewWeb.setLongClickable(false);

        viewWeb.setHapticFeedbackEnabled(false);

        binding.webText.loadData(readRawTextFile(resourceid), MIME_TYPE, ENCODING);
        binding.textoFragment.setText(nombreMenu);

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
        binding.webText.setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    private String readRawTextFile(final int resource) {

        final InputStream inputStream = getActivity().getResources().openRawResource(resource);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String line;
        final StringBuilder text = new StringBuilder();
        try (BufferedReader buf = new BufferedReader(inputStreamReader)) {
            while ((line = buf.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException exc) {
            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
        }
        return text.toString();
    }
}
