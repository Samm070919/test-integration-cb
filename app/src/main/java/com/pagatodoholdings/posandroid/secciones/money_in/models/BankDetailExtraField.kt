package com.pagatodoholdings.posandroid.secciones.money_in.models

class BankDetailExtraField {
    var label:String = ""
    var name:String = ""
    var value:String = ""

    constructor(label: String, name: String, value: String) {
        this.label = label
        this.name = name
        this.value = value
    }
}