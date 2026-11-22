package com.scriptedmachi.rider;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final int INK = Color.rgb(22, 31, 28);
    private final int GREEN = Color.rgb(32, 139, 72);
    private final int MUTED = Color.rgb(113, 124, 118);
    private LinearLayout root;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        showRequest();
    }

    private GradientDrawable shape(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius);
        if (strokeWidth > 0) d.setStroke(strokeWidth, strokeColor); return d;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD)); return t;
    }
    private void pad(View v, int l, int t, int r, int b) { v.setPadding(l,t,r,b); }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private void showRequest() {
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(Color.rgb(248,250,248));
        root = column(); root.setGravity(Gravity.TOP); pad(root,22,22,22,24); scroll.addView(root);
        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL);
        TextView logo = text("R", 25, Color.rgb(184,243,107), true); logo.setGravity(Gravity.CENTER); logo.setBackground(shape(INK,16,INK,1));
        header.addView(logo, new LinearLayout.LayoutParams(52,52));
        LinearLayout brand = column(); pad(brand,12,0,0,0); brand.addView(text("RIDER",22,INK,true)); brand.addView(text("DRIVER PARTNER",10,MUTED,true));
        header.addView(brand, new LinearLayout.LayoutParams(0,-2,1));
        TextView online = text("●  ONLINE",11,GREEN,true); online.setBackground(shape(Color.rgb(231,246,235),30,Color.TRANSPARENT,0)); pad(online,12,8,12,8); header.addView(online);
        root.addView(header); addSpace(root,28);
        root.addView(text("Good day, Driver",14,MUTED,false)); addSpace(root,5);
        root.addView(text("You have a new ride",25,INK,true)); addSpace(root,18);
        LinearLayout card = column(); card.setBackground(shape(Color.WHITE,24,Color.rgb(231,236,232),1)); pad(card,20,20,20,20);
        LinearLayout tagrow = new LinearLayout(this); tagrow.setGravity(Gravity.CENTER_VERTICAL);
        TextView tag = text("NEW RIDE REQUEST",10,GREEN,true); tag.setBackground(shape(Color.rgb(231,246,235),30,Color.TRANSPARENT,0)); pad(tag,11,7,11,7); tagrow.addView(tag);
        card.addView(tagrow); addSpace(card,18);
        card.addView(text("Rishi",24,INK,true)); card.addView(text("PASSENGER",10,MUTED,true)); addSpace(card,22);
        LinearLayout pickup = new LinearLayout(this); pickup.setGravity(Gravity.TOP);
        TextView picon = text("●",20,GREEN,true); picon.setGravity(Gravity.CENTER); pickup.addView(picon,new LinearLayout.LayoutParams(30,35));
        LinearLayout ptxt = column(); ptxt.addView(text("PICKUP LOCATION",10,MUTED,true)); addSpace(ptxt,4); ptxt.addView(text("Bhagavathi Nagar, Koyambedu",16,INK,true)); pickup.addView(ptxt); card.addView(pickup);
        View line = new View(this); line.setBackgroundColor(Color.rgb(221,229,222)); LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(2,25); lp.setMargins(14,2,0,2); card.addView(line,lp);
        LinearLayout drop = new LinearLayout(this); drop.setGravity(Gravity.TOP);
        TextView dicon = text("●",20,Color.rgb(211,75,75),true); dicon.setGravity(Gravity.CENTER); drop.addView(dicon,new LinearLayout.LayoutParams(30,35));
        LinearLayout dtxt = column(); dtxt.addView(text("DROP LOCATION",10,MUTED,true)); addSpace(dtxt,4); dtxt.addView(text("Bharathiyar Street, Valasaravakkam",16,INK,true)); drop.addView(dtxt); card.addView(drop);
        addSpace(card,22);
        TextView route = text("  ●  Koyambedu     ───────────     Valasaravakkam",11,MUTED,false); route.setBackground(shape(Color.rgb(247,249,247),12,Color.rgb(238,241,238),1)); pad(route,10,15,10,15); card.addView(route);
        addSpace(card,24);
        Button accept = new Button(this); accept.setText("ACCEPT RIDE"); accept.setTextColor(Color.WHITE); accept.setTextSize(15); accept.setTypeface(null,Typeface.BOLD); accept.setAllCaps(false); accept.setBackground(shape(GREEN,16,GREEN,1));
        card.addView(accept,new LinearLayout.LayoutParams(-1,58)); accept.setOnClickListener(v -> showAccepted());
        addSpace(card,10);
        Button cancel = new Button(this); cancel.setText("CANCEL REQUEST"); cancel.setTextColor(Color.rgb(190,65,65)); cancel.setTextSize(14); cancel.setTypeface(null,Typeface.BOLD); cancel.setAllCaps(false); cancel.setBackground(shape(Color.rgb(255,243,243),16,Color.rgb(255,225,225),1));
        card.addView(cancel,new LinearLayout.LayoutParams(-1,52)); cancel.setOnClickListener(v -> showCancelled());
        root.addView(card); addSpace(root,18);
        TextView footer = text("Please review the trip details before responding.",12,MUTED,false); footer.setGravity(Gravity.CENTER); root.addView(footer);
        setContentView(scroll);
    }
    private void showAccepted() { showResult(true); }
    private void showCancelled() { showResult(false); }
    private void showResult(boolean accepted) {
        root = column(); root.setGravity(Gravity.CENTER); root.setBackgroundColor(Color.rgb(248,250,248)); pad(root,28,32,28,32);
        TextView mark = text(accepted ? "✓" : "×",48,Color.WHITE,true); mark.setGravity(Gravity.CENTER); mark.setBackground(shape(accepted ? GREEN : Color.rgb(190,65,65),100,Color.TRANSPARENT,0)); root.addView(mark,new LinearLayout.LayoutParams(100,100));
        addSpace(root,28); TextView title = text(accepted ? "RIDE ACCEPTED!" : "REQUEST CANCELLED",25,INK,true); title.setGravity(Gravity.CENTER); root.addView(title);
        addSpace(root,12); TextView detail = text(accepted ? "You have successfully accepted Rishi’s ride." : "Rishi’s ride request has been cancelled.",16,MUTED,false); detail.setGravity(Gravity.CENTER); root.addView(detail);
        addSpace(root,30);
        Button again = new Button(this); again.setText("BACK TO RIDE REQUEST"); again.setTextColor(Color.WHITE); again.setTextSize(14); again.setTypeface(null,Typeface.BOLD); again.setAllCaps(false); again.setBackground(shape(GREEN,16,GREEN,1)); root.addView(again,new LinearLayout.LayoutParams(-1,56)); again.setOnClickListener(v -> showRequest());
        setContentView(root);
    }
    private void addSpace(LinearLayout parent, int height) { View s = new View(this); parent.addView(s,new LinearLayout.LayoutParams(1,height)); }
}
