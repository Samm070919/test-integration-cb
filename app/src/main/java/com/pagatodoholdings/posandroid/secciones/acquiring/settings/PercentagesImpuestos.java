package com.pagatodoholdings.posandroid.secciones.acquiring.settings;

public class PercentagesImpuestos {

    private Percentage pH;
    private Percentage pM;
    private Percentage pl;

    public PercentagesImpuestos(Percentage pH, Percentage pM, Percentage pl) {
        this.pH = pH;
        this.pM = pM;
        this.pl = pl;
    }

    public Percentage getpH() {
        return pH;
    }

    public void setpH(Percentage pH) {
        this.pH = pH;
    }

    public Percentage getpM() {
        return pM;
    }

    public void setpM(Percentage pM) {
        this.pM = pM;
    }

    public Percentage getPl() {
        return pl;
    }

    public void setPl(Percentage pl) {
        this.pl = pl;
    }
}
