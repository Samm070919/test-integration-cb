package com.pagatodoholdings.posandroid.secciones.acquiring.support;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import com.pagatodoholdings.posandroid.secciones.acquiring.views.Direction;

public abstract class ActivitySupport extends AppCompatActivity {//NOSONAR

    private FragmentSupport fragmentSupport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentSupport = new FragmentSupport(getSupportFragmentManager());
    }

    public void loadFragment(@NonNull Fragment fragment, @IdRes int idContainer) {
        fragmentSupport.loadFragment(fragment, idContainer);
    }

    public void loadFragment(@NonNull Fragment fragment, @IdRes int idContainer,@NonNull Direction direction,
                             boolean addToBackStack) {
            fragmentSupport.loadFragment(fragment, idContainer,direction,addToBackStack);
    }

    public void removeFragment(Fragment fragment){
        fragmentSupport.removeFragment(fragment);
    }

}
