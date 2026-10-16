package com.dinosquad.arcade;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.os.StrictMode;

public class MainActivity extends Activity {
    private static final int BG=Color.rgb(5,13,29), PANEL=Color.rgb(13,31,55), BLUE=Color.rgb(20,142,255), GOLD=Color.rgb(255,177,22), PICK_ROM=42;
    private LinearLayout root; private String roomCode=""; private JSONObject room; private boolean sound=true, touchControls=true; private final SupabaseClient cloud=new SupabaseClient();
    @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().getDecorView().setSystemUiVisibility(5894);home();}
    private TextView text(String v,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setPadding(8,8,8,8);if(bold)t.setTypeface(null,1);return t;}
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setGravity(Gravity.CENTER);l.setPadding(20,12,20,12);return l;}
    private Button button(String label,int color,Runnable action){Button b=new Button(this);b.setText(label);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setTextSize(16);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));b.setOnClickListener(v->action.run());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,54);p.setMargins(0,6,0,6);b.setLayoutParams(p);return b;}
    private void begin(String subtitle){root=column();root.setBackgroundColor(BG);ScrollView s=new ScrollView(this);s.setFillViewport(true);s.addView(root);setContentView(s);root.addView(text("DINO SQUAD",34,GOLD,true));root.addView(text("CLASSIC ARCADE • TOGETHER AGAIN",12,Color.LTGRAY,true));if(subtitle!=null)root.addView(text(subtitle,20,Color.WHITE,true));}
    private EditText edit(String hint,boolean password){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setTextColor(Color.WHITE);e.setHintTextColor(Color.GRAY);e.setPadding(14,6,14,6);e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BLUE));if(password)e.setInputType(0x81);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,50);p.setMargins(0,5,0,5);e.setLayoutParams(p);return e;}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private void home(){begin("Cadillacs and Dinosaurs");root.addView(text("Play solo now, or create a room for friends.",16,Color.rgb(110,190,255),false));root.addView(button("▶   PLAY SOLO",GOLD,this::solo));root.addView(button("▣   PLAY SUPPLIED ROM",BLUE,this::pickRom));root.addView(button("♟   MULTIPLAYER",BLUE,this::multiplayer));root.addView(button("⚙   SETTINGS",PANEL,this::settings));root.addView(button("ⓘ   DEVELOPER: RAIHAN",PANEL,this::developerInfo));root.addView(button("↗   SUPPORT / EMULATOR LINK",PANEL,this::openAdLink));root.addView(text("Android 7+ • touch controls • USB/Bluetooth gamepads",12,Color.GRAY,false));}
    private void developerInfo(){new AlertDialog.Builder(this).setTitle("Dino Squad Developer").setMessage("Developed by Raihan\nraihan.sites.bd").setPositiveButton("Open website",(d,w)->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://raihan.sites.bd")))).setNegativeButton("Close",null).show();}
    private void openAdLink(){startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://asiafilm.org/4/90cc2ce73f5daf47690eaa29106accba")));}
    private void solo(){setContentView(new SoloView());}
    private void pickRom(){
        try {
            File rom=new File(getFilesDir(),"dino(1).zip");
            try(InputStream in=getAssets().open("dino(1).zip");FileOutputStream out=new FileOutputStream(rom)){byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);}
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
            Intent i=new Intent(this,com.seleuco.mame4droid.MAME4droid.class); i.setData(Uri.fromFile(rom)); startActivity(i);
        } catch(Exception noHandler){new AlertDialog.Builder(this).setTitle("Dino Squad emulator error").setMessage("The bundled emulator could not start the supplied dino(1).zip. Play Solo is still available.").setPositiveButton("Play solo",(d,w)->solo()).show();}
    }
    private void multiplayer(){begin("MULTIPLAYER");root.addView(text("Cloud rooms powered by Supabase • room state is polled safely",13,Color.LTGRAY,false));root.addView(button("CREATE ROOM",BLUE,this::createRoom));root.addView(button("JOIN ROOM",PANEL,this::joinRoom));root.addView(button("← Back",BLUE,this::home));}
    private void createRoom(){begin("CREATE ROOM");EditText name=edit("Room name",false),host=edit("Your player name",false),pass=edit("Password (optional)",true);root.addView(name);root.addView(host);root.addView(pass);Spinner players=new Spinner(this);players.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"2 players","3 players","4 players"}));players.setSelection(2);root.addView(players);root.addView(button("CREATE ROOM",GOLD,()->{toast("Connecting…");cloud.createRoom(name.getText().toString(),host.getText().toString(),players.getSelectedItemPosition()+2,pass.getText().toString(),r->{room=r;try{roomCode=r.getString("room_code");}catch(Exception ignored){}lobby();},this::cloudError);}));root.addView(button("← Back",BLUE,this::multiplayer));}
    private void joinRoom(){begin("JOIN ROOM");EditText code=edit("6-digit room code",false),player=edit("Your player name",false),pass=edit("Password if required",true);root.addView(code);root.addView(player);root.addView(pass);root.addView(button("JOIN ROOM",GOLD,()->{toast("Connecting…");cloud.joinRoom(code.getText().toString(),player.getText().toString(),pass.getText().toString(),r->{room=r;roomCode=code.getText().toString().trim();lobby();},this::cloudError);}));root.addView(button("← Back",BLUE,this::multiplayer));}
    private void lobby(){begin("ROOM LOBBY");try{root.addView(text(room.getString("room_name"),23,Color.WHITE,true));root.addView(text("ROOM CODE: "+room.getString("room_code"),20,GOLD,true));JSONArray p=room.getJSONArray("players");root.addView(text(room.optString("status","waiting").toUpperCase(Locale.US)+" • "+p.length()+"/"+room.getInt("max_players")+" players",14,Color.LTGRAY,false));}catch(Exception ignored){}root.addView(button("COPY ROOM CODE",PANEL,()->{ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newPlainText("Dino Squad room code",roomCode));toast("Room code copied");}));root.addView(button("REFRESH PLAYERS",PANEL,()->refreshLobby(false)));root.addView(button("START GAME",GOLD,()->cloud.startRoom(roomCode,r->{room=r;solo();},this::cloudError)));root.addView(button("CLOSE / BACK",BLUE,this::multiplayer));}
    private void refreshLobby(boolean silent){cloud.getRoom(roomCode,r->{room=r;lobby();},e->{if(!silent)cloudError(e);});}
    private void cloudError(String error){new AlertDialog.Builder(this).setTitle("Multiplayer unavailable").setMessage(error).setPositiveButton("OK",null).show();}
    private void settings(){begin("SETTINGS");Switch a=new Switch(this);a.setText("Sound");a.setTextColor(Color.WHITE);a.setChecked(sound);a.setOnCheckedChangeListener((v,c)->sound=c);root.addView(a);Switch b=new Switch(this);b.setText("Touch controls");b.setTextColor(Color.WHITE);b.setChecked(touchControls);b.setOnCheckedChangeListener((v,c)->touchControls=c);root.addView(b);root.addView(text("PLAY SUPPLIED ROM launches only the bundled dino(1).zip ROM set. If no emulator is installed, PLAY SOLO remains available.",13,Color.LTGRAY,false));root.addView(button("TEST CONTROLLER",PANEL,()->toast("Use a USB or Bluetooth controller, then press any button.")));root.addView(button("← Back",BLUE,this::home));}
    @Override public boolean dispatchKeyEvent(KeyEvent e){int src=e.getSource();if((src&InputDevice.SOURCE_GAMEPAD)!=0||(src&InputDevice.SOURCE_JOYSTICK)!=0){if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_BACK)return true;}return super.dispatchKeyEvent(e);}
    private class SoloView extends View{Paint p=new Paint(3);float x=160,y=0,vy=0;long last=System.currentTimeMillis();RectF dino=new RectF();SoloView(){super(MainActivity.this);setFocusable(true);requestFocus();}protected void onDraw(Canvas c){long now=System.currentTimeMillis();float dt=Math.min(.05f,(now-last)/1000f);last=now;y+=vy*dt;vy+=1200*dt;if(y>0){y=0;vy=0;}c.drawColor(Color.rgb(18,35,48));p.setColor(Color.rgb(37,78,62));c.drawRect(0,getHeight()-120,getWidth(),getHeight(),p);p.setColor(GOLD);dino.set(x,getHeight()-190+y,x+58,getHeight()-120+y);c.drawRoundRect(dino,12,12,p);p.setColor(Color.WHITE);c.drawCircle(x+42,getHeight()-168+y,5,p);p.setColor(Color.WHITE);p.setTextSize(22);c.drawText("SOLO TRAINING RUN",24,36,p);p.setTextSize(15);c.drawText("Tap / press A or SPACE to jump",24,62,p);p.setTextSize(13);c.drawText("BACK: return to menu",24,getHeight()-24,p);postInvalidateOnAnimation();}public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){if(e.getY()<80&&e.getX()<160){home();return true;}vy=-560;return true;}return true;}public boolean onKeyDown(int k,KeyEvent e){if(k==KeyEvent.KEYCODE_SPACE||k==KeyEvent.KEYCODE_BUTTON_A||k==KeyEvent.KEYCODE_DPAD_UP){vy=-560;return true;}if(k==KeyEvent.KEYCODE_BACK){home();return true;}return true;}}
}
