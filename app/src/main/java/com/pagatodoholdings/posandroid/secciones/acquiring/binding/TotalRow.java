package com.pagatodoholdings.posandroid.secciones.acquiring.binding;

public class TotalRow {

    private String titleTotal;
    private String amount;
    private boolean idDropdown;

    private TotalRow(){}

    public static TotalRow getInstance(){
        return new TotalRow();
    }

    public TotalRow(String titleTotal, String amount, boolean idDropdown) {
        this.titleTotal = titleTotal;
        this.amount = amount;
        this.idDropdown = idDropdown;
    }

    public String getTitleTotal() {
        return titleTotal;
    }

    public void setTitleTotal(String titleTotal) {
        this.titleTotal = titleTotal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isIdDropdown() {
        return idDropdown;
    }

    public void setIdDropdown(boolean idDropdown) {
        this.idDropdown = idDropdown;
    }

}
