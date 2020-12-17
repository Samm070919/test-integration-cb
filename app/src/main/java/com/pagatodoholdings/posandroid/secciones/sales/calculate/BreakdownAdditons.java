package com.pagatodoholdings.posandroid.secciones.sales.calculate;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BreakdownAdditons implements Additions {

    //    private Double amount;
    private int decimals;
    private BigDecimal bigAmount;
    private BigDecimal bigMontoTotal;
    private BigDecimal bigPropina = BigDecimal.ZERO;
    private BigDecimal bigImpuesto = BigDecimal.ZERO;
    private BigDecimal bigSubTotal = BigDecimal.ZERO;
    private BigDecimal BIG_CENT = BigDecimal.valueOf(100);

    private BreakdownAdditons(BigDecimal monto) {
//        amount = monto.doubleValue();
        this.bigMontoTotal = monto;
        this.bigAmount = monto;

        String isoCode = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();

        if (Country.PERU.getItemIsoCode().equals(isoCode)) {
            decimals = Country.PERU.getNumDecimales();
        } else if (Country.COLOMBIA.getItemIsoCode().equals(isoCode)) {
            decimals = Country.COLOMBIA.getNumDecimales();
        } else {
            decimals = 2;
        }
    }

    public static BreakdownAdditons getInstance(BigDecimal monto) {
        return new BreakdownAdditons(monto);
    }

    @Override
    public BigDecimal getMontotal(String perPropopina) {
//        String valor = "" + (amount + (amount * Double.valueOf(perPropopina.trim().replace("%", "")) / 100));
        BigDecimal bigDecimal = new BigDecimal(perPropopina.trim().replace("%", ""));
        BigDecimal bigPropina = bigAmount.multiply(bigDecimal).divide(BIG_CENT, MathContext.DECIMAL32);
        bigMontoTotal = bigAmount.add(bigPropina)
                .setScale(decimals, RoundingMode.HALF_UP);
        return bigMontoTotal;
    }

    @Override
    public BigDecimal getSubTotal(String perimp) {
//        String valor = String.valueOf((int) (amount - roundValue(Double.valueOf(perimp.replace("%", "")) / 100 + 1)));
        BigDecimal bigPerImp = new BigDecimal(perimp.trim().replace("%", ""));
        BigDecimal bigDem = bigPerImp.add(BIG_CENT);
        BigDecimal bigNum = bigAmount.multiply(BIG_CENT);
        bigSubTotal = bigNum.divide(bigDem, MathContext.DECIMAL32)
                .setScale(decimals, RoundingMode.HALF_DOWN);
        return bigSubTotal;
    }

    @Override
    public String getImpuestos(String per) {
//        String valor = String.valueOf(roundValue((Double.valueOf(per.replace("%", "")) / 100 + 1)));
        BigDecimal bigPer = new BigDecimal(per.trim().replace("%", ""));
        BigDecimal bigNum = bigAmount.multiply(bigPer);
        BigDecimal bigDem = bigPer.add(BIG_CENT);
        bigImpuesto = bigNum.divide(bigDem, MathContext.DECIMAL32)
                .setScale(decimals, RoundingMode.HALF_UP);
        return bigImpuesto.toPlainString();
    }

    @Override
    public String getPropina(String perPro) {
//        String valor = "" + Double.valueOf(perPro.trim().replace("%", "")) / 100 * amount;
        BigDecimal bigDecimal = new BigDecimal(perPro.trim().replace("%", ""));
        bigPropina = bigDecimal.multiply(bigAmount).divide(BIG_CENT, MathContext.DECIMAL32)
                .setScale(decimals, RoundingMode.HALF_UP);
        return bigPropina.toPlainString();
    }

    public BigDecimal getBigAmount() {
        return bigAmount;
    }

    public BigDecimal getBigPropina() {
        return bigPropina;
    }

    public BigDecimal getBigImpuesto() {
        return bigImpuesto;
    }

    public BigDecimal getBigSubTotal() {
        return bigSubTotal;
    }

    public BigDecimal getBigMontoTotal() {
        return bigMontoTotal;
    }

//    private int roundValue(final double div) {
//        return (int) (amount - (amount / div));
//    }
}