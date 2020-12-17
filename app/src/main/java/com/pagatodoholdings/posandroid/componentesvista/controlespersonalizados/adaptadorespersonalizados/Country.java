package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import com.pagatodoholdings.posandroid.R;

public enum Country {
    ARGENTINA("Argentina", "AR", R.drawable.ic_argentina, "032", "## | #### | ####", 0, true, "", "", ""),
    CHILE("Chile", "CL", R.drawable.ic_chile, "152", "# | ########", 0, true, "", "", ""),
    COLOMBIA("Colombia", "CO", R.drawable.ic_colombia, "170", "### | ### | ####", 0, true, "+57 (1) 745 4252", "contactoco@yaganaste.com", "COP"),
    ECUADOR("Ecuador", "EC", R.drawable.ic_ecuador, "218", "# | ########", 0, true, "", "", ""),
    MEXICO("México", "MX", R.drawable.ic_mexico, "484", "## | #### | ####", 2, true, "", "", "$"),
    PERU("Perú", "PE", R.drawable.ic_peru, "604", "### | ### | ###", 2, false, "+51 (01) 611 3560", "contactope@yaganaste.com", "S/"),
    ESPANA("España", "ES", R.drawable.ic_bandera_esp, "724", "### | ## | ## | ##", 0, true, "", "", ""),
    BRASIL("Brasil", "BR", R.drawable.ic_bandera_bra, "076", "", 0, true, "", "", "");

    private String itemIsoCode;
    private String itemName;
    private String itemCode;
    private int itemRes;
    private String itemMask;
    private int numDecimales;
    private boolean isPSE;
    private String contactPhone;
    private String contactEmail;
    private String currency;

    Country(String itemName, String itemCode, Integer itemRes, String isoCode, String mask, int numDecimales, boolean isPSE, String contactPhone, String contactEmail, String currency) {
        this.itemIsoCode = isoCode;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.itemRes = itemRes;
        this.itemMask = mask;
        this.numDecimales = numDecimales;
        this.isPSE = isPSE;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.currency = currency;
    }

    public String getItemIsoCode() {
        return itemIsoCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getItemRes() {
        return itemRes;
    }

    public String getItemMask(){return itemMask;}

    public int getNumDecimales() {
        return numDecimales;
    }

    public static Country getCountry(String code) {
        switch (code) {
            case "032": //ARGENTINA
                return Country.ARGENTINA;
            case "170": //COLOMBIA
                return Country.COLOMBIA;
            case "152": //CHILE
                return Country.CHILE;
            case "218": //ECUADOR
                return Country.ECUADOR;
            case "484": //MEXICO
                return Country.MEXICO;
            case "604": //PERU
                return Country.PERU;
            default:
                break;
        }
        return Country.COLOMBIA;
    }

    public boolean isPSE() {
        return isPSE;
    }

    public void setPSE(boolean PSE) {
        isPSE = PSE;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
