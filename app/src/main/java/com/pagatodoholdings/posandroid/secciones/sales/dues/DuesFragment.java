package com.pagatodoholdings.posandroid.secciones.sales.dues;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.DuesFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeDataSingleton;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;

public class DuesFragment extends PciSalesFragmentSupport<DuesFragmentBinding> implements View.OnClickListener{

    public static DuesFragment newInstance(){
        return new DuesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dues_fragment,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.hideAllToolbar();
        binding.cardH.setOnClickListener(this);
        binding.cardView3.setOnClickListener(this);
        binding.cardView4.setOnClickListener(this);
        binding.cardView5.setOnClickListener(this);
        binding.cardView6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_h:
                ChargeDataSingleton.getInstance().setDues("1");
                activity.getManager().setCuotas(1);
                break;
            case R.id.cardView5:
                ChargeDataSingleton.getInstance().setDues("6");
                activity.getManager().setCuotas(6);
                break;
            case R.id.cardView4:
                ChargeDataSingleton.getInstance().setDues("9");
                activity.getManager().setCuotas(9);
                break;
            case R.id.cardView6:
                ChargeDataSingleton.getInstance().setDues("12");
                activity.getManager().setCuotas(12);
                break;
            case R.id.cardView3:
                ChargeDataSingleton.getInstance().setDues("3");
                activity.getManager().setCuotas(3);
                break;
            default:
                break;
        }
        activity.removeFragment(this);
    }
}
