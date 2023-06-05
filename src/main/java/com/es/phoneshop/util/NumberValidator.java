package com.es.phoneshop.util;

import jakarta.servlet.http.HttpServletRequest;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class NumberValidator {
    public static int getQuantityIfValid(HttpServletRequest request) throws ParseException {
        String quantityStr = request.getParameter("quantity");
        Locale locale = request.getLocale();

        return getQuantityIfValid(quantityStr, locale);
    }

    public static int getQuantityIfValid(String quantityStr, Locale locale) throws ParseException {
        if (Double.parseDouble(quantityStr) % 1 != 0) {
            throw new NumberFormatException();
        }

        NumberFormat format = NumberFormat.getInstance(locale);
        int quantity = format.parse(quantityStr).intValue();
        if (quantity < 1) {
            throw new NumberFormatException();
        }

        return quantity;
    }

    public static Long parseProductId(HttpServletRequest req) {
        String productInfo = req.getPathInfo().substring(1);
        return Long.valueOf(productInfo);
    }
}
