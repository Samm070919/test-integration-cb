package com.pagatodoholdings.posandroid.secciones.home;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarruselHomePageAdapter extends PagerAdapter {
    private static final String TAG = CarruselHomePageAdapter.class.getSimpleName();
    private List<Menu> menuCats;
    private Map<String,Integer> logosMap =  new HashMap<>();

    private void inicializarLogos()
    {
        logosMap.put("NoCard",R.drawable.bloqued_card);
        logosMap.put("starbucks",R.drawable.starbucks);
        logosMap.put("cupo_2",R.drawable.cupo_2);
        logosMap.put("Mpos",R.drawable.mpos);
    }

    OnFailureListener onFailureListener = (final Throwable exc)->
            Logger.LOGGER.throwing(TAG, 1, exc, "Error al acceder a la base de datos");

    public CarruselHomePageAdapter(final List<Menu> menus) {
            this.menuCats = menus ;
    }

    @NonNull
    @Override
    public Object instantiateItem(final ViewGroup collection, final int position) {
        final LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.elemento_carrusel, collection, false);
        final ImageView logo = layout.findViewById(R.id.iv_icono_elemento_carrusel);
        inicializarLogos();
                final Menu menuCategorias = menuCats.get(position);

        final String iconPath = getMenuIconPath(menuCategorias);

        if ( !"".equals(iconPath) ) {
            setIcon(iconPath,logo);
        } else {
            logo.setImageResource(Utilities.getAttributeReference(R.attr.fullcarga));
        }
        final Resources resources = layout.getResources();

        int w = 250;
        int h = 150;

        if(position==0)
        {
            w = (int) (resources.getDimension(R.dimen.card_w) / resources.getDisplayMetrics().density);
            h = (int) (resources.getDimension(R.dimen.card_h) / resources.getDisplayMetrics().density);
        }

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, resources.getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, h, resources.getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        logo.setLayoutParams(layoutParams);
                collection.addView(layout);
        return layout;
    }

    public void setIcon(final String filePathOne,final ImageView logo) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        if (StorageUtil.validarArchivo(StorageUtil.getStoragePath() + filePathOne)) {
             Bitmap bitmapOne = BitmapFactory.decodeFile(StorageUtil.getStoragePath() + filePathOne, options);
             logo.setImageBitmap(bitmapOne);
        } else {
            logo.setImageResource(Utilities.getAttributeReference(R.attr.fullcarga));
        }
    }

    public String getMenuIconPath(final Menu currentMenu) {
        if (currentMenu.getIcono() != null) {
            return SigmaBdManager.obtenIcono(currentMenu, onFailureListener);
        }
        return "";
    }

    @Override
    public void destroyItem(final ViewGroup collection, final int position, final Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return menuCats.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object; //NOPMD
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return "";
    }
}
