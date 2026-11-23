package com.scriptedmachi.rider;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.animation.ValueAnimator;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int GREEN = Color.rgb(0, 139, 61);
    private static final int DARK_GREEN = Color.rgb(0, 112, 48);
    private static final int INK = Color.rgb(20, 31, 27);
    private static final int MUTED = Color.rgb(105, 119, 112);
    private static final int PAGE = Color.rgb(249, 251, 249);
    private static final int RED = Color.rgb(211, 48, 57);
    private ValueAnimator acceptPulse;
    private ToneGenerator toneGenerator;
    private final Handler soundHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(GREEN);
        getWindow().setNavigationBarColor(Color.WHITE);
        // White status-bar icons on green; dark navigation icons on white.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        showRequest();
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable shape(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (strokeWidth > 0) drawable.setStroke(dp(strokeWidth), strokeColor);
        return drawable;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(true);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private void padding(View view, int left, int top, int right, int bottom) {
        view.setPadding(dp(left), dp(top), dp(right), dp(bottom));
    }

    private void space(LinearLayout parent, int height) {
        View spacer = new View(this);
        parent.addView(spacer, new LinearLayout.LayoutParams(1, dp(height)));
    }

    // Android 15 draws app content behind the status bar for apps targeting API 35.
    // This green strip occupies that inset so the logo starts safely below phone icons.
    private void addStatusBarStrip(LinearLayout page) {
        if (Build.VERSION.SDK_INT >= 35) {
            int resource = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int height = resource > 0 ? getResources().getDimensionPixelSize(resource) : dp(28);
            View strip = new View(this);
            strip.setBackgroundColor(GREEN);
            page.addView(strip, new LinearLayout.LayoutParams(-1, height));
        }
    }

    private LinearLayout createPage() {
        LinearLayout page = column();
        page.setBackgroundColor(PAGE);
        addStatusBarStrip(page);
        return page;
    }

    private void addHeader(LinearLayout parent) {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        padding(header, 20, 15, 20, 15);
        header.setBackgroundColor(Color.WHITE);

        TextView mark = label("R", 30, Color.rgb(184, 243, 107), true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(shape(INK, 15, INK, 1));
        header.addView(mark, new LinearLayout.LayoutParams(dp(52), dp(52)));

        LinearLayout brand = column();
        padding(brand, 12, 0, 0, 0);
        brand.addView(label("RIDER", 25, INK, true));
        brand.addView(label("DRIVER PARTNER", 11, MUTED, true));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView online = label("●  ONLINE", 13, DARK_GREEN, true);
        online.setGravity(Gravity.CENTER);
        online.setBackground(shape(Color.rgb(225, 249, 232), 30, Color.TRANSPARENT, 0));
        padding(online, 15, 10, 15, 10);
        header.addView(online);
        parent.addView(header);

        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(226, 234, 228));
        parent.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
    }

    private TextView actionButton(String title, int textColor, int backgroundColor, int borderColor) {
        TextView button = label(title, 18, textColor, true);
        button.setGravity(Gravity.CENTER);
        button.setBackground(shape(backgroundColor, 16, borderColor, 1));
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private void addLocation(LinearLayout card, int pinDrawable,
                             String heading, String address, int headingColor) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.TOP);

        ImageView pin = new ImageView(this);
        pin.setImageResource(pinDrawable);
        pin.setScaleType(ImageView.ScaleType.FIT_CENTER);
        row.addView(pin, new LinearLayout.LayoutParams(dp(42), dp(48)));

        LinearLayout details = column();
        padding(details, 12, 0, 0, 0);
        details.addView(label(heading, 15, headingColor, true));
        TextView addressView = label(address, 17, INK, true);
        addressView.setLineSpacing(dp(2), 1.0f);
        details.addView(addressView);
        row.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(row);
    }

    private void playRequestSound() {
        playTone(ToneGenerator.TONE_PROP_BEEP2, 180, true);
    }

    private void playFeedbackSound(boolean accepted) {
        playTone(accepted ? ToneGenerator.TONE_PROP_ACK : ToneGenerator.TONE_PROP_NACK, 220, false);
    }

    private void playTone(int tone, int durationMs, boolean doubleBeep) {
        try {
            if (toneGenerator != null) toneGenerator.release();
            toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75);
            toneGenerator.startTone(tone, durationMs);
            if (doubleBeep) {
                soundHandler.postDelayed(() -> {
                    if (toneGenerator != null) toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 140);
                }, 230);
            }
            soundHandler.postDelayed(() -> {
                if (toneGenerator != null) {
                    toneGenerator.release();
                    toneGenerator = null;
                }
            }, doubleBeep ? 520 : 300);
        } catch (Exception ignored) {
            // Sound is an enhancement; the app remains fully usable if audio is unavailable.
        }
    }

    private void startAcceptPulse(TextView button) {
        if (acceptPulse != null) acceptPulse.cancel();
        acceptPulse = ValueAnimator.ofFloat(0f, 1f);
        acceptPulse.setDuration(850);
        acceptPulse.setRepeatCount(ValueAnimator.INFINITE);
        acceptPulse.setRepeatMode(ValueAnimator.REVERSE);
        acceptPulse.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            GradientDrawable background = (GradientDrawable) button.getBackground();
            int alpha = 105 + Math.round(145 * progress);
            int width = 2 + Math.round(3 * progress);
            background.setStroke(dp(width), Color.argb(alpha, 190, 255, 205));
            button.setElevation(dp(3 + 3 * progress));
        });
        acceptPulse.start();
    }

    private void stopAcceptPulse() {
        if (acceptPulse != null) {
            acceptPulse.cancel();
            acceptPulse = null;
        }
    }

    private void showRequest() {
        LinearLayout page = createPage();
        addHeader(page);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(PAGE);

        LinearLayout content = column();
        padding(content, 18, 20, 18, 28);

        content.addView(label("Good day, Driver", 17, MUTED, false));
        space(content, 4);
        TextView title = label("You have a new ride", 29, INK, true);
        content.addView(title);
        space(content, 18);

        LinearLayout card = column();
        card.setBackground(shape(Color.WHITE, 24, Color.rgb(231, 237, 232), 1));
        padding(card, 18, 18, 18, 18);

        TextView requestTag = label("♧  NEW RIDE REQUEST", 14, DARK_GREEN, true);
        requestTag.setBackground(shape(Color.rgb(226, 249, 233), 30, Color.TRANSPARENT, 0));
        padding(requestTag, 14, 10, 14, 10);
        card.addView(requestTag, new LinearLayout.LayoutParams(-2, -2));
        space(card, 20);

        LinearLayout passenger = new LinearLayout(this);
        passenger.setGravity(Gravity.CENTER_VERTICAL);
        ImageView avatar = new ImageView(this);
        avatar.setImageResource(R.drawable.passenger_avatar);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        passenger.addView(avatar, new LinearLayout.LayoutParams(dp(72), dp(72)));

        LinearLayout passengerInfo = column();
        padding(passengerInfo, 14, 0, 0, 0);
        passengerInfo.addView(label("Rishi", 25, INK, true));
        passengerInfo.addView(label("PASSENGER", 12, MUTED, true));
        passenger.addView(passengerInfo, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(passenger);

        View separator = new View(this);
        separator.setBackgroundColor(Color.rgb(232, 237, 233));
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(-1, dp(1));
        separatorParams.topMargin = dp(18);
        separatorParams.bottomMargin = dp(18);
        card.addView(separator, separatorParams);

        addLocation(card, R.drawable.pickup_pin, "Pickup", "Bhagavathi Nagar, Koyambedu", DARK_GREEN);
        View routeLine = new View(this);
        routeLine.setBackgroundColor(Color.rgb(193, 207, 197));
        LinearLayout.LayoutParams routeParams = new LinearLayout.LayoutParams(dp(2), dp(24));
        routeParams.leftMargin = dp(20);
        card.addView(routeLine, routeParams);
        addLocation(card, R.drawable.destination_pin, "Destination", "Bharathiyar Street, Valasaravakkam", RED);

        // Ride amount row: clearly displays the passenger's offered fare.
        LinearLayout fareRow = new LinearLayout(this);
        fareRow.setGravity(Gravity.CENTER_VERTICAL);
        fareRow.setBackground(shape(Color.rgb(242, 249, 244), 14, Color.rgb(218, 235, 222), 1));
        padding(fareRow, 16, 13, 16, 13);
        LinearLayout fareLabels = column();
        fareLabels.addView(label("RIDE AMOUNT", 12, MUTED, true));
        fareLabels.addView(label("Passenger fare", 13, MUTED, false));
        fareRow.addView(fareLabels, new LinearLayout.LayoutParams(0, -2, 1));
        TextView fareAmount = label("₹125", 25, DARK_GREEN, true);
        fareAmount.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        fareRow.addView(fareAmount);
        LinearLayout.LayoutParams fareParams = new LinearLayout.LayoutParams(-1, -2);
        fareParams.topMargin = dp(20);
        card.addView(fareRow, fareParams);

        space(card, 20);
        TextView accept = actionButton("✓   ACCEPT RIDE", Color.WHITE, GREEN, GREEN);
        accept.setBackground(shape(GREEN, 16, Color.argb(170, 190, 255, 205), 2));
        card.addView(accept, new LinearLayout.LayoutParams(-1, dp(62)));
        accept.setOnClickListener(view -> { stopAcceptPulse(); playFeedbackSound(true); showResult(true); });

        space(card, 12);
        TextView cancel = actionButton("×   CANCEL", RED, Color.rgb(255, 245, 245), Color.rgb(255, 205, 208));
        card.addView(cancel, new LinearLayout.LayoutParams(-1, dp(58)));
        cancel.setOnClickListener(view -> { stopAcceptPulse(); playFeedbackSound(false); showResult(false); });

        content.addView(card);
        space(content, 18);
        TextView footer = label("Please review the trip details before responding.", 13, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer);

        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(page);
        startAcceptPulse(accept);
        playRequestSound();
    }

    private void showResult(boolean accepted) {
        LinearLayout page = createPage();
        addHeader(page);

        LinearLayout body = column();
        body.setGravity(Gravity.CENTER);
        padding(body, 28, 30, 28, 30);
        body.setBackgroundColor(PAGE);

        TextView mark = label(accepted ? "✓" : "×", 54, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(shape(accepted ? GREEN : RED, 100, Color.TRANSPARENT, 0));
        body.addView(mark, new LinearLayout.LayoutParams(dp(112), dp(112)));
        space(body, 28);

        TextView title = label(accepted ? "RIDE ACCEPTED!" : "RIDE CANCELLED", 27,
                accepted ? DARK_GREEN : RED, true);
        title.setGravity(Gravity.CENTER);
        body.addView(title);
        space(body, 12);

        TextView message = label(accepted
                ? "You have successfully accepted Rishi's ride."
                : "Rishi's ride request has been cancelled.", 17, MUTED, false);
        message.setGravity(Gravity.CENTER);
        body.addView(message);
        space(body, 32);

        TextView back = actionButton("←   BACK TO REQUEST", Color.WHITE, GREEN, GREEN);
        body.addView(back, new LinearLayout.LayoutParams(-1, dp(60)));
        back.setOnClickListener(view -> showRequest());

        page.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(page);
    }
    @Override
    protected void onDestroy() {
        stopAcceptPulse();
        soundHandler.removeCallbacksAndMessages(null);
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
        super.onDestroy();
    }
}
