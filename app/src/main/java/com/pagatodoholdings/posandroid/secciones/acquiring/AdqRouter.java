package com.pagatodoholdings.posandroid.secciones.acquiring;

import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeFragment;
import com.pagatodoholdings.posandroid.secciones.acquiring.pairing.DevicePairingFragment;
import com.pagatodoholdings.posandroid.secciones.acquiring.settings.SettingFragment;
import com.pagatodoholdings.posandroid.secciones.acquiring.views.Direction;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesActivity;

import static com.pagatodoholdings.posandroid.secciones.sales.PciSalesActivity.COBRO;
import static com.pagatodoholdings.posandroid.secciones.sales.PciSalesActivity.DEVOLUCION;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_CODE_ADQ;

public class AdqRouter implements AdqContracts.Router {

    private HomeActivity homeActivity;
    private int idContenair = R.id.fl_main_pantalla_completa;

    public AdqRouter(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void showSettings() {
        Fragment fragment = SettingFragment.newInstance();

        this.homeActivity.loadFragment(fragment,idContenair,Direction.FORDWARD,true);
    }

    @Override
    public void showBreakdow() {
        homeActivity.startActivityForResult(PciSalesActivity.createIntent(homeActivity,COBRO), REQUEST_CODE_ADQ);
    }

    @Override
    public void showReturnMoney() {
        homeActivity.startActivity(PciSalesActivity.createIntent(homeActivity,DEVOLUCION));
    }

    @Override
    public void showDongleLink() {
        Fragment fragment = DevicePairingFragment.newInstance();
        homeActivity.loadFragment(fragment,idContenair,Direction.FORDWARD,true);
    }

    @Override
    public void showCharge() {
        homeActivity.loadFragment(ChargeFragment.newInstance(),idContenair,Direction.FORDWARD,true);
    }
}
