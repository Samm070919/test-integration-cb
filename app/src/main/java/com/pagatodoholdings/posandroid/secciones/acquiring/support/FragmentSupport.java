package com.pagatodoholdings.posandroid.secciones.acquiring.support;

import android.annotation.SuppressLint;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pagatodoholdings.posandroid.secciones.acquiring.views.Direction;

class FragmentSupport {

    private FragmentManager fragmentManager;

    FragmentSupport(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    void loadFragment(@NonNull Fragment fragment, @IdRes int idContainer){
        @SuppressLint("CommitTransaction") FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(idContainer, fragment, fragment.getTag()).commitAllowingStateLoss();
    }

    void removeFragment(Fragment fragment){

        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.remove(fragment);
        trans.commit();
        fragmentManager.popBackStack();
    }

    protected void loadFragment(@NonNull Fragment fragment, @IdRes int idContainer, @NonNull Direction direction,
                                boolean addToBackStack) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (direction.equals(Direction.FORDWARD)) {
            fragmentTransaction.setCustomAnimations(direction.getEnterAnimation(), direction.getExitAnimation(),
                    Direction.BACK.getEnterAnimation(), Direction.BACK.getExitAnimation());
        } else if (direction.equals(Direction.BACK)) {
            fragmentTransaction.setCustomAnimations(direction.getEnterAnimation(), direction.getExitAnimation(),
                    Direction.FORDWARD.getEnterAnimation(), Direction.FORDWARD.getExitAnimation());
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.replace(idContainer, fragment, fragment.getTag()).commitAllowingStateLoss();

    }
}
