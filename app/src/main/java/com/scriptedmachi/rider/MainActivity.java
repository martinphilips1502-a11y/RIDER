package com.scriptedmachi.rider;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.animation.ValueAnimator;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int GREEN = Color.rgb(0, 139, 61);
    private static final int DEEP_GREEN = Color.rgb(0, 105, 48);
    private static final int INK = Color.rgb(19, 35, 29);
    private static final int MUTED = Color.rgb(91, 107, 100);
    private static final int PAGE = Color.rgb(247, 250, 248);
    private static final int RED = Color.rgb(218, 42, 54);
    private ValueAnimator pulse;
    private ToneGenerator tones;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Window w = getWindow();
        w.setStatusBarColor(GREEN);
        w.setNavigationBarColor(Color.WHITE);
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        showRequest();
    }

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable shape(int fill, int radius, int stroke, int strokeDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(radius));
        if (strokeDp > 0) d.setStroke(dp(strokeDp), stroke);
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(true);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void pad(View v, int l, int t, int r, int b) {
        v.setPadding(dp(l), dp(t), dp(r), dp(b));
    }

    private void gap(LinearLayout parent, int height) {
        View v = new View(this);
        parent.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void divider(LinearLayout parent, int top, int bottom) {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(226, 234, 229));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(1));
        p.topMargin = dp(top);
        p.bottomMargin = dp(bottom);
        parent.addView(v, p);
    }

    private LinearLayout makePage() {
        LinearLayout page = column();
        page.setBackgroundColor(PAGE);
        return page;
    }

    private void addHeader(LinearLayout page) {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        pad(header, 20, 14, 20, 14);
        header.setBackgroundColor(Color.WHITE);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.rider_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        header.addView(logo, new LinearLayout.LayoutParams(dp(66), dp(52)));

        LinearLayout brand = column();
        pad(brand, 9, 0, 0, 0);
        brand.addView(text("RIDER", 24, INK, true));
        TextView sub = text("DRIVER PARTNER", 10, MUTED, true);
        sub.setLetterSpacing(0.16f);
        brand.addView(sub);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView online = text("●  ONLINE", 12, DEEP_GREEN, true);
        online.setGravity(Gravity.CENTER);
        online.setBackground(shape(Color.rgb(226, 248, 234), 30, Color.TRANSPARENT, 0));
        pad(online, 14, 9, 14, 9);
        header.addView(online);

        page.addView(header);
        divider(page, 0, 0);
    }

    private TextView button(String title, int textColor, int fill, int border) {
        TextView b = text(title, 17, textColor, true);
        b.setGravity(Gravity.CENTER);
        b.setBackground(shape(fill, 17, border, 1));
        b.setClickable(true);
        b.setFocusable(true);
        return b;
    }

    private void addLocation(LinearLayout card, int drawable, String heading,
                             String address, int headingColor) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView pin = new ImageView(this);
        pin.setImageResource(drawable);
        pin.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams pinP = new LinearLayout.LayoutParams(dp(34), dp(40));
        pinP.rightMargin = dp(13);
        row.addView(pin, pinP);

        LinearLayout labels = column();
        labels.addView(text(heading, 14, headingColor, true));
        TextView addressText = text(address, 16, INK, true);
        addressText.setLineSpacing(dp(1), 1.0f);
        labels.addView(addressText);
        row.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(row);
    }

    private void playTone(int tone, int duration, boolean doubleTone) {
        try {
            if (tones != null) tones.release();
            tones = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 78);
            tones.startTone(tone, duration);
            if (doubleTone) {
                handler.postDelayed(() -> {
                    if (tones != null) tones.startTone(ToneGenerator.TONE_PROP_BEEP, 130);
                }, 210);
            }
            handler.postDelayed(() -> {
                if (tones != null) {
                    tones.release();
                    tones = null;
                }
            }, doubleTone ? 500 : 300);
        } catch (Exception ignored) { }
    }

    private void startPulse(TextView accept) {
        stopPulse();
        pulse = ValueAnimator.ofFloat(0f, 1f);
        pulse.setDuration(620);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulse.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            GradientDrawable bg = (GradientDrawable) accept.getBackground();
            int alpha = 125 + Math.round(130 * f);
            int width = 2 + Math.round(3 * f);
            bg.setStroke(dp(width), Color.argb(alpha, 190, 255, 207));
            accept.setElevation(dp(3 + 5 * f));
        });
        pulse.start();
    }

    private void stopPulse() {
        if (pulse != null) {
            pulse.cancel();
            pulse = null;
        }
    }

    private void showRequest() {
        LinearLayout page = makePage();
        addHeader(page);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(PAGE);

        LinearLayout content = column();
        pad(content, 18, 18, 18, 22);

        content.addView(text("Good day, Driver", 17, MUTED, false));
        gap(content, 4);
        content.addView(text("You have a new ride", 27, INK, true));
        gap(content, 16);

        LinearLayout card = column();
        card.setBackground(shape(Color.WHITE, 24, Color.rgb(224, 233, 227), 1));
        pad(card, 17, 17, 17, 17);

        TextView tag = text("♧  NEW RIDE REQUEST", 13, DEEP_GREEN, true);
        tag.setBackground(shape(Color.rgb(226, 248, 234), 28, Color.TRANSPARENT, 0));
        pad(tag, 13, 9, 13, 9);
        card.addView(tag, new LinearLayout.LayoutParams(-2, -2));
        gap(card, 17);

        LinearLayout passenger = new LinearLayout(this);
        passenger.setGravity(Gravity.CENTER_VERTICAL);

        ImageView avatar = new ImageView(this);
        avatar.setImageResource(R.drawable.passenger_avatar);
        avatar.setScaleType(ImageView.ScaleType.FIT_CENTER);
        passenger.addView(avatar, new LinearLayout.LayoutParams(dp(76), dp(76)));

        LinearLayout passengerInfo = column();
        pad(passengerInfo, 13, 0, 0, 0);
        passengerInfo.addView(text("Rishi", 24, INK, true));
        passengerInfo.addView(text("PASSENGER", 11, MUTED, true));
        TextView rating = text("★  4.8  (126 rides)", 13, MUTED, false);
        rating.setTextColor(Color.rgb(87, 101, 94));
        passengerInfo.addView(rating);
        passenger.addView(passengerInfo, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(passenger);

        divider(card, 17, 17);

        addLocation(card, R.drawable.pickup_pin, "Pickup",
                "Bhagavathi Nagar, Koyambedu", DEEP_GREEN);

        View route = new View(this);
        route.setBackgroundColor(Color.rgb(194, 208, 199));
        LinearLayout.LayoutParams routeP = new LinearLayout.LayoutParams(dp(2), dp(22));
        routeP.leftMargin = dp(17);
        card.addView(route, routeP);

        addLocation(card, R.drawable.destination_pin, "Destination",
                "Bharathiyar Street, Valasaravakkam", RED);

        divider(card, 16, 16);

        LinearLayout fare = new LinearLayout(this);
        fare.setGravity(Gravity.CENTER_VERTICAL);
        fare.setBackground(shape(Color.rgb(239, 249, 242), 16, Color.rgb(219, 237, 224), 1));
        pad(fare, 15, 12, 15, 12);

        ImageView wallet = new ImageView(this);
        wallet.setImageResource(R.drawable.wallet_icon);
        wallet.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams walletP = new LinearLayout.LayoutParams(dp(31), dp(31));
        walletP.rightMargin = dp(12);
        fare.addView(wallet, walletP);

        LinearLayout fareText = column();
        fareText.addView(text("Ride Amount", 14, INK, true));
        fareText.addView(text("Passenger fare", 12, MUTED, false));
        fare.addView(fareText, new LinearLayout.LayoutParams(0, -2, 1));
        TextView amount = text("₹125", 27, DEEP_GREEN, true);
        amount.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        fare.addView(amount);
        LinearLayout.LayoutParams fareP = new LinearLayout.LayoutParams(-1, -2);
        fareP.topMargin = dp(17);
        card.addView(fare, fareP);

        gap(card, 19);
        TextView accept = button("✓   ACCEPT RIDE", Color.WHITE, GREEN, GREEN);
        accept.setBackground(shape(GREEN, 17, Color.rgb(190, 255, 207), 3));
        card.addView(accept, new LinearLayout.LayoutParams(-1, dp(62)));
        accept.setOnClickListener(v -> {
            stopPulse();
            playTone(ToneGenerator.TONE_PROP_ACK, 230, false);
            showResult(true);
        });

        gap(card, 11);
        TextView cancel = button("×   CANCEL", RED, Color.rgb(255, 246, 247),
                Color.rgb(255, 202, 207));
        card.addView(cancel, new LinearLayout.LayoutParams(-1, dp(57)));
        cancel.setOnClickListener(v -> {
            stopPulse();
            playTone(ToneGenerator.TONE_PROP_NACK, 220, false);
            showResult(false);
        });

        ImageView bike = new ImageView(this);
        bike.setImageResource(R.drawable.bike_footer);
        bike.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams bikeP = new LinearLayout.LayoutParams(-1, dp(118));
        bikeP.topMargin = dp(8);
        card.addView(bike, bikeP);

        content.addView(card);
        gap(content, 15);
        TextView footer = text("ⓘ  Please review the trip details before responding.", 13, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer);

        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(page);
        startPulse(accept);
        playTone(ToneGenerator.TONE_PROP_BEEP2, 170, true);
    }

    private void showResult(boolean accepted) {
        stopPulse();
        LinearLayout page = makePage();
        addHeader(page);

        LinearLayout body = column();
        body.setGravity(Gravity.CENTER);
        pad(body, 28, 28, 28, 28);
        body.setBackgroundColor(PAGE);

        TextView icon = text(accepted ? "✓" : "×", 52, Color.WHITE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(shape(accepted ? GREEN : RED, 100, Color.TRANSPARENT, 0));
        body.addView(icon, new LinearLayout.LayoutParams(dp(104), dp(104)));
        gap(body, 24);

        TextView heading = text(accepted ? "RIDE ACCEPTED!" : "RIDE CANCELLED",
                25, accepted ? DEEP_GREEN : RED, true);
        heading.setGravity(Gravity.CENTER);
        body.addView(heading);
        gap(body, 10);

        TextView message = text(accepted
                ? "You have successfully accepted Rishi's ride."
                : "Rishi's ride request has been cancelled.", 16, MUTED, false);
        message.setGravity(Gravity.CENTER);
        body.addView(message);
        gap(body, 28);

        TextView back = button("←   BACK TO REQUEST", Color.WHITE, GREEN, GREEN);
        body.addView(back, new LinearLayout.LayoutParams(-1, dp(58)));
        back.setOnClickListener(v -> showRequest());

        page.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(page);
    }

    @Override protected void onDestroy() {
        stopPulse();
        handler.removeCallbacksAndMessages(null);
        if (tones != null) {
            tones.release();
            tones = null;
        }
        super.onDestroy();
    }
}
