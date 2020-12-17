package com.pagatodoholdings.posandroid.secciones.acquiring.charge;

import com.pagatodoholdings.posandroid.secciones.sales.binding.BreakdownData;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;

public class ChargeDataSingleton {

    private static final ChargeDataSingleton ourInstance = new ChargeDataSingleton();

    private String money;
    private String dues;
    private Menu menu;
    private Menu menuDev;
    private String referenciaDevolucion;
    private BreakdownData breakdownData;

    public static ChargeDataSingleton getInstance() {
        return ourInstance;
    }

    public String getReferenciaDevolucion() {
        return referenciaDevolucion;
    }

    public void setReferenciaDevolucion(String referenciaDevolucion) {
        this.referenciaDevolucion = referenciaDevolucion;
    }

    public Menu getMenuDev() {
        return menuDev;
    }

    public void setMenuDev(Menu menuDev) {
        this.menuDev = menuDev;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    private ChargeDataSingleton() {
        money = "0";
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDues() {
        return dues;
    }

    public void setDues(String dues) {
        this.dues = dues;
    }

    public BreakdownData getBreakdownData() {
        return breakdownData;
    }

    public void setBreakdownData(BreakdownData breakdownData) {
        this.breakdownData = breakdownData;
    }
}
