package com.pagatodoholdings.posandroid.secciones.acquiring.settings;

public class AddVelues {

    private PercentagesImpuestos impuestos;
    private PercentagesPropina propina;
    private Percentage perImpSelected;
    private Percentage perProSelected;

    public AddVelues(PercentagesImpuestos impuestos, PercentagesPropina propina, Percentage perImpSelected, Percentage perProSelected) {
        this.impuestos = impuestos;
        this.propina = propina;
        this.perImpSelected = perImpSelected;
        this.perProSelected = perProSelected;
    }

    public PercentagesImpuestos getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(PercentagesImpuestos impuestos) {
        this.impuestos = impuestos;
    }

    public PercentagesPropina getPropina() {
        return propina;
    }

    public void setPropina(PercentagesPropina propina) {
        this.propina = propina;
    }

    public Percentage getPerImpSelected() {
        return perImpSelected;
    }

    public void setPerImpSelected(Percentage perImpSelected) {
        this.perImpSelected = perImpSelected;
    }

    public Percentage getPerProSelected() {
        return perProSelected;
    }

    public void setPerProSelected(Percentage perProSelected) {
        this.perProSelected = perProSelected;
    }
}
