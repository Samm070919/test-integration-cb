package com.pagatodoholdings.posandroid.secciones.money_in.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank

class MoneyInViewModel(application: Application): AndroidViewModel(application)  {
    var bankListLiveData:MutableLiveData<List<Bank>> = MutableLiveData<List<Bank>>()

    fun setBankList(bankList: List<Bank>) {
        bankListLiveData.value = bankList
    }

}