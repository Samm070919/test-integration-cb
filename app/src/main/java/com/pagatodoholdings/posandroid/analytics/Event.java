package com.pagatodoholdings.posandroid.analytics;

import com.google.firebase.analytics.FirebaseAnalytics;

public enum Event {
    EVENT_LOGOUT("logout"),
    EVENT_LOGIN(FirebaseAnalytics.Event.LOGIN),
    EVENT_LOGIN_ERROR(FirebaseAnalytics.Event.LOGIN + "_fail"),
    EVENT_LOGIN_PCI(FirebaseAnalytics.Event.LOGIN + "_pci"),
    EVENT_LOGIN_PCI_ERROR(FirebaseAnalytics.Event.LOGIN + "_pci_fail"),
    EVENT_SELECTED_MENU_CATEGORIAS("selected_menu_categorias_"),
    EVENT_SELECTED_PRODUCT("selected_product_"),
    EVENT_SELECTED_SUB_MENU("selected_sub_menu_"),
    EVENT_VENTA("venta"),
    EVENT_CONFIG_SELECTED("config_selected_"),
    EVENT_DONGLE_CONNECTED("dongle_connected"),
    EVENT_DONGLE_CONNECT_ERROR("dongle_connect_error"),
    EVENT_BEGIN_UPDATE_FIRMWARE("begin_update_firmware"),
    EVENT_END_UPDATE_FIRMWARE("end_update_firmware"),
    EVENT_FAIL_UPDATE_FIRMWARE("fail_update_firmware"),
    EVENT_COMPLETE_REGISTRO_INTERNO("complete_registro_interno"),
    EVENT_COMPLETE_REGISTRO("complete_registro"),
    EVENT_FAIL_REGISTRO("fail_registro"),
    EVENT_COMPLETE_DESBLOQUEO("complete_desbloqueo"),
    EVENT_FAIL_DESBLOQUEO("fail_desbloqueo");

    //Analytics Bundle keys
    public static final String MENU_CATEGORIA_KEY = "categoria";
    public static final String FAIL_KEY = "fail";

    public final String key;

    Event(final String descripcion) {
        this.key = descripcion;
    }
}
