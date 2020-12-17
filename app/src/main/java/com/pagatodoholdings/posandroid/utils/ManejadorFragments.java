package com.pagatodoholdings.posandroid.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Fade;

import com.pagatodoholdings.posandroid.R;


public class ManejadorFragments {

    private FragmentManager fragmentManager;

    public ManejadorFragments(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void cargarFragment(final Fragment fragment, final @IdRes int containerId) {
        final androidx.fragment.app.FragmentTransaction transaction =
                fragmentManager.beginTransaction();

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
// are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
// ARE available in the support library (though they don't do anything on API < 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void cargarFragmentWithBackstack(final Fragment fragment, final @IdRes int containerId) {
        final androidx.fragment.app.FragmentTransaction transaction =
                fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void sobreponerFragment(int containerId, Fragment fragment) {
        final androidx.fragment.app.FragmentTransaction transaction =
                fragmentManager.beginTransaction();
        transaction.add(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    @SuppressLint("ResourceType")
    public void cargarFragmentPantallaCompleta(final Fragment fragment, final Activity activity) {
        final View view = activity.findViewById(R.id.fl_main_pantalla_completa);
        final androidx.fragment.app.FragmentTransaction transaction =
                fragmentManager.beginTransaction();
        transaction.replace(view.getId(), fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


}
