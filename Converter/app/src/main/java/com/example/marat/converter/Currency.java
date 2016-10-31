package com.example.marat.converter;

import java.math.BigDecimal;

public class Currency {

    double dollarCurrency;
    double euroCurrency;

    String dollarCurrencyString;
    String euroCurrencyString;

    BigDecimal currencyDecimal;

    void setDollarCurrency(String dollarCurrencyString){
        if(!dollarCurrencyString.equals("-1"))
            this.dollarCurrencyString = dollarCurrencyString;
        else this.dollarCurrencyString = "Нет данных";
            this.dollarCurrency = Double.parseDouble(dollarCurrencyString);

    }

    void setEuroCurrency(String euroCurrencyString){
        if(!euroCurrencyString.equals("-1"))
            this.euroCurrencyString = euroCurrencyString;
        else this.euroCurrencyString = "Нет данных";
            this.euroCurrency = Double.parseDouble(euroCurrencyString);
    }

    String getDollarCurrency(){
        return dollarCurrencyString;
    }

    String getEuroCurrency(){
        return euroCurrencyString;
    }

    String convertDollarToEuro(String dollar){
        if(dollarCurrency == -1 || euroCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double doll = Double.parseDouble(dollar);
        currencyDecimal = BigDecimal.valueOf((doll * dollarCurrency)/euroCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }

    String convertEuroToDollar(String euro){
        if(dollarCurrency == -1 || euroCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double eu = Double.parseDouble(euro);
        currencyDecimal = BigDecimal.valueOf((eu * euroCurrency)/dollarCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }

    String convertRubbletoDollar(String rubble){
        if(dollarCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double rub = Double.parseDouble(rubble);
        currencyDecimal = BigDecimal.valueOf(rub/dollarCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }

    String convertDollarToRubble(String dollar){
        if(dollarCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double doll = Double.parseDouble(dollar);
        currencyDecimal = BigDecimal.valueOf(doll * dollarCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }

    String convertRubbleToEuro(String rubble){
        if(euroCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double rub = Double.parseDouble(rubble);
        currencyDecimal = BigDecimal.valueOf(rub/euroCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }

    String convertEuroToRubble(String euro){
        if(euroCurrency == -1){ return "Ошибка! Курса валюты нету!"; }
        double eu = Double.parseDouble(euro);
        currencyDecimal = BigDecimal.valueOf(eu*euroCurrency).setScale(4,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(currencyDecimal);
    }
}
