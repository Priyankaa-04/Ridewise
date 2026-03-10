package com.example.ridewise.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.ridewise.model.RideOption;

public class AppIntents {
    public static void openProvider(Context ctx, RideOption option) {
        // Simple fallback: open Play Store search for the provider name
        try {
            String queryUrl = "https://www.google.com/search?q=" + Uri.encode(option.getProvider() + " app");
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(queryUrl));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
