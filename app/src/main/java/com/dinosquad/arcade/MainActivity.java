package com.dinosquad.arcade;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(5, 13, 29);
    private final int PANEL = Color.rgb(13, 31, 55);
    private final int BLUE = Color.rgb(20, 142, 255);
    private final int GOLD = Color.rgb(255, 177, 22);
    private LinearLayout root;
    private int maxPlayers = 4;
    private String roomId = "";
    private boolean privateRoom = false;
    private boolean sound = true;
    private boolean touchControls = true;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        home();
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);
        l.setPadding(20, 12, 20, 12);
        return l;
    }

    private Button button(String label, int color, Runnable action) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(Color.WHITE);
        b.setAllCaps(false); b.setTextSize(16);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        b.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 54);
        p.setMargins(0, 6, 0, 6); b.setLayoutParams(p);
        return b;
    }

    private void begin(String subtitle) {
        root = column(); root.setBackgroundColor(BG);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true); scroll.addView(root);
        setContentView(scroll);
        root.addView(text("DINO SQUAD", 34, GOLD, true));
        root.addView(text("CLASSIC ARCADE • TOGETHER AGAIN", 12, Color.LTGRAY, true));
        if (subtitle != null) root.addView(text(subtitle, 20, Color.WHITE, true));
    }

    private void home() {
        begin("Cadillacs and Dinosaurs");
        root.addView(text("Play. Fight. Survive.", 16, Color.rgb(110,190,255), false));
        root.addView(button("▶   START GAME", GOLD, this::gameInfo));
        root.addView(button("♟   MULTIPLAYER", BLUE, this::multiplayer));
        root.addView(button("⚙   SETTINGS", PANEL, this::settings));
        root.addView(button("ⓘ   ABOUT", PANEL, () -> new AlertDialog.Builder(this)
            .setTitle("Dino Squad").setMessage("Android 7+ arcade launcher prototype. Emulator integration and online game synchronization are not included in this starter build.")
            .setPositiveButton("OK", null).show()));
        root.addView(text("Android 7+  •  Touch UI  •  Controller-ready menu", 12, Color.GRAY, false));
    }

    private void gameInfo() {
        begin("START GAME");
        root.addView(text("Game engine is not connected yet.", 18, Color.WHITE, true));
        root.addView(text("This starter app has the menu shell only. To play, the compatible open-source MAME engine must be integrated and tested with your ROM set.", 14, Color.LTGRAY, false));
        root.addView(button("Choose ROM (planned)", PANEL, () -> new AlertDialog.Builder(this)
            .setTitle("Emulator not integrated")
            .setMessage("The current project is a menu prototype and cannot launch the ROM yet.")
            .setPositiveButton("OK", null).show()));
        root.addView(button("← Back", BLUE, this::home));
    }

    private void multiplayer() {
        begin("MULTIPLAYER");
        root.addView(text("Direct phone-hosted rooms • No cloud database", 13, Color.LTGRAY, false));
        root.addView(button("CREATE ROOM", BLUE, this::createRoom));
        root.addView(button("JOIN ROOM", PANEL, this::joinRoom));
        root.addView(button("LOCAL NETWORK NOTES", PANEL, () -> new AlertDialog.Builder(this)
            .setTitle("Connection requirements")
            .setMessage("This prototype does not open a network listener yet. Direct internet hosting requires a reachable host IP/port; CGNAT and firewalls may prevent inbound connections.")
            .setPositiveButton("OK", null).show()));
        root.addView(button("← Back", BLUE, this::home));
    }

    private EditText edit(String hint, boolean password) {
        EditText e = new EditText(this);
        e.setSingleLine(true); e.setHint(hint); e.setTextColor(Color.WHITE); e.setHintTextColor(Color.GRAY);
        e.setPadding(14, 6, 14, 6);
        e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BLUE));
        if (password) e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 50);
        p.setMargins(0, 5, 0, 5); e.setLayoutParams(p);
        return e;
    }

    private void createRoom() {
        begin("CREATE ROOM");
        EditText name = edit("Room name", false);
        root.addView(name);
        Spinner visibility = new Spinner(this);
        visibility.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Public room", "Private room (password required)"}));
        root.addView(visibility);
        EditText pass = edit("Room password (private rooms)", true); root.addView(pass);
        root.addView(text("Maximum players (host counts as one)", 14, Color.WHITE, true));
        Spinner players = new Spinner(this);
        players.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"2 players", "3 players", "4 players"}));
        players.setSelection(2); root.addView(players);
        root.addView(button("CREATE ROOM", GOLD, () -> {
            privateRoom = visibility.getSelectedItemPosition() == 1;
            if (privateRoom && pass.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Private rooms need a password", Toast.LENGTH_SHORT).show(); return;
            }
            maxPlayers = players.getSelectedItemPosition() + 2;
            roomId = String.format(Locale.US, "%06d", new Random().nextInt(900000) + 100000);
            lobby(name.getText().toString().trim().isEmpty() ? "Dino Room" : name.getText().toString(),
                  privateRoom, maxPlayers);
        }));
        root.addView(button("← Back", BLUE, this::multiplayer));
    }

    private void joinRoom() {
        begin("JOIN ROOM");
        EditText id = edit("Room ID", false); root.addView(id);
        EditText pass = edit("Room password if required", true); root.addView(pass);
        root.addView(button("JOIN ROOM", GOLD, () -> new AlertDialog.Builder(this)
            .setTitle("Networking not connected")
            .setMessage("Room joining UI is ready, but this starter build has no actual host discovery or socket protocol. Room IDs are not yet reachable by other phones.")
            .setPositiveButton("OK", null).show()));
        root.addView(button("← Back", BLUE, this::multiplayer));
    }

    private void lobby(String name, boolean isPrivate, int max) {
        begin("ROOM LOBBY");
        root.addView(text(name, 23, Color.WHITE, true));
        root.addView(text("ROOM ID: " + roomId, 20, GOLD, true));
        root.addView(text((isPrivate ? "PRIVATE" : "PUBLIC") + " • 1/" + max + " players", 14, Color.LTGRAY, false));
        root.addView(button("COPY ROOM ID", PANEL, () -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("Dino Squad room ID", roomId));
            Toast.makeText(this, "Room ID copied", Toast.LENGTH_SHORT).show();
        }));
        root.addView(text("Only the host is present. Online connections are not implemented in this prototype.", 13, Color.LTGRAY, false));
        root.addView(button("START GAME (not connected)", GOLD, () -> gameInfo()));
        root.addView(button("CLOSE ROOM", BLUE, this::multiplayer));
    }

    private void settings() {
        begin("SETTINGS");
        Switch soundSwitch = new Switch(this); soundSwitch.setText("Sound"); soundSwitch.setTextColor(Color.WHITE);
        soundSwitch.setChecked(sound); soundSwitch.setPadding(8, 10, 8, 10);
        soundSwitch.setOnCheckedChangeListener((v, checked) -> sound = checked); root.addView(soundSwitch);
        Switch touchSwitch = new Switch(this); touchSwitch.setText("Touch controls"); touchSwitch.setTextColor(Color.WHITE);
        touchSwitch.setChecked(touchControls); touchSwitch.setPadding(8, 10, 8, 10);
        touchSwitch.setOnCheckedChangeListener((v, checked) -> touchControls = checked); root.addView(touchSwitch);
        root.addView(text("Controller input is handled by Android key events when an emulator engine is integrated.", 13, Color.LTGRAY, false));
        root.addView(button("TEST CONTROLLER", PANEL, () -> new AlertDialog.Builder(this)
            .setTitle("Controller support")
            .setMessage("Connect a USB or Bluetooth controller. This prototype detects standard Android gamepad key events, but does not yet map controls to gameplay.")
            .setPositiveButton("OK", null).show()));
        root.addView(button("← Back", BLUE, this::home));
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        int source = event.getSource();
        if ((source & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
            (source & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            return super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }
}
