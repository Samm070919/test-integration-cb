package com.pagatodoholdings.posandroid.secciones.acquiring.charge;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.databinding.ButtonCheckerBinding;
import com.pagatodoholdings.posandroid.utils.Utilities;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

public class BtnCheckHolder extends RecyclerView.ViewHolder {

    private ButtonCheckerBinding binding;

    BtnCheckHolder(ButtonCheckerBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final BtnCheckHolder.ItemProdData data, final AdqProdListener listener) {
        this.binding.setMenuProd(data.getMenu());
        final String iconPath = data.getIcon();

//        if (!"".equals(iconPath)) {
//            setIcon(iconPath);
//        } else {
//            Drawable drawable = Utilities.getIcono(data.getIcon());
////            Drawable drawable = ContextCompat.getDrawable(this.binding.imgIcon.getContext(), Utilities.getAttributeReference(R.attr.fullcarga));
//            this.binding.imgIcon.setImageDrawable(drawable);
//        }

        Drawable drawable = Utilities.getIcono(iconPath);
        this.binding.imgIcon.setImageDrawable(drawable);
        this.binding.getRoot().setOnClickListener(v -> listener.onClickItem(data));
    }

//    public void setIcon(final String filePathOne) {
//        final Resources resources = binding.getRoot().getResources();
//        int dpW = (int) (resources.getDimension(R.dimen.item_prod_w) / resources.getDisplayMetrics().density);
//        int dpH = (int) (resources.getDimension(R.dimen.item_prod_h) / resources.getDisplayMetrics().density);
//        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpW, resources.getDisplayMetrics());
//        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpH, resources.getDisplayMetrics());
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
//        if (StorageUtil.validarArchivo(StorageUtil.getStoragePath() + filePathOne)) {
//            final Bitmap bitmapOne = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(StorageUtil.getStoragePath() + filePathOne, options), width, height, true);
//            binding.imgIcon.setImageBitmap(bitmapOne);
//        } else {
//            binding.imgIcon.setImageResource(Utilities.getAttributeReference(R.attr.fullcarga));
//        }
//    }

    void selectItem() {
        binding.cardH.setCardBackgroundColor(Color.parseColor("#00A1E1"));
        binding.imgIcon.setColorFilter(Color.WHITE);
        binding.btnAhorro.setTextColor(Color.WHITE);
    }

    void deselectItem() {
        binding.cardH.setCardBackgroundColor(Color.WHITE);
        binding.imgIcon.setColorFilter(null);
        binding.btnAhorro.setTextColor(Color.parseColor("#46606A"));
    }

    public static class ItemProdData {
        private Menu menu;
        private String icon;

        public ItemProdData(Menu menu, String icon) {
            this.menu = menu;
            this.icon = icon;
        }

        public Menu getMenu() {
            return menu;
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

}
