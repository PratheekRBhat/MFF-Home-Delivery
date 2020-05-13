package com.example.mffhomedelivery.Common;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

import Model.Category;
import Model.User;

public class Common {
    public static final String USER_REF = "Users";
    public static final String POPULAR_CATEGORIES_REF = "MostPopular";
    public static final String BEST_DEALS_REF = "BestDeals";
    public static final String CATEGORY_REF = "Category";

    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String ORDER_REF = "Orders";

    public static User currentUser;
    public static Category categorySelected;

    public static String formatPrice(Double price) {
        if (price != 0){
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice  = new StringBuilder(df.format(price)).toString();
            return finalPrice;//.replace(".", ",");
        }
        else
            return "0.00";
    }

    //To set the header of the navigation bar.
    public static void setSpanString(String s, String name, TextView txt_user) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(s);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldspan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldspan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        txt_user.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public static String createOrderNumber() {
        return new StringBuilder()
                .append(System.currentTimeMillis()) //Current time in milliseconds.
                .append(Math.abs(new Random().nextInt())) //Random number to block same order at same time.
                .toString();
    }
}
