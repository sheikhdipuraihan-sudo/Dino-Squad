package com.dinosquad.arcade;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseClient {
    private static final String URL = "https://ydxpufnqwomcvfgacjft.supabase.co";
    private static final String KEY = "sb_publishable_wkVruAlIq5F_9YnXznUEAA_AFNa_okJ";
    private final ExecutorService pool = Executors.newCachedThreadPool(); private final Handler main = new Handler(Looper.getMainLooper());
    public interface Success { void ok(JSONObject room); } public interface Failure { void fail(String message); }
    private void call(String path, String body, Success success, Failure failure) { pool.execute(() -> { try { HttpURLConnection c=(HttpURLConnection)new URL(URL+path).openConnection(); c.setRequestMethod("POST"); c.setRequestProperty("apikey",KEY); c.setRequestProperty("Authorization","Bearer "+KEY); c.setRequestProperty("Content-Type","application/json"); c.setRequestProperty("Prefer","return=representation"); c.setDoOutput(true); try(OutputStream o=c.getOutputStream()){o.write(body.getBytes(StandardCharsets.UTF_8));} int code=c.getResponseCode(); InputStream in=code>=200&&code<300?c.getInputStream():c.getErrorStream(); StringBuilder s=new StringBuilder(); try(BufferedReader r=new BufferedReader(new InputStreamReader(in))){String line;while((line=r.readLine())!=null)s.append(line);} if(code<200||code>=300) throw new IOException(s.toString()); JSONArray a=new JSONArray(s.toString()); JSONObject r=a.length()>0?a.getJSONObject(0):new JSONObject(); main.post(()->success.ok(r)); } catch(Exception e){main.post(()->failure.fail(clean(e.getMessage())));} }); }
    private String clean(String s){ if(s==null||s.isEmpty()) return "Could not reach Supabase."; try { JSONObject o=new JSONObject(s); return o.optString("message",o.optString("hint",s)); } catch(Exception ignored){return s;} }
    public void createRoom(String name,String host,int max,String password,Success s,Failure f){ try { JSONObject b=new JSONObject().put("p_name",name).put("p_host",host).put("p_max_players",max).put("p_private",!password.trim().isEmpty()).put("p_password",password); call("/rest/v1/rpc/dino_create_room",b.toString(),s,f); }catch(Exception e){f.fail(e.getMessage());} }
    public void joinRoom(String code,String player,String password,Success s,Failure f){ try { call("/rest/v1/rpc/dino_join_room",new JSONObject().put("p_code",code).put("p_player",player).put("p_password",password).toString(),s,f); }catch(Exception e){f.fail(e.getMessage());} }
    public void startRoom(String code,Success s,Failure f){ try { call("/rest/v1/rpc/dino_start_room",new JSONObject().put("p_code",code).toString(),s,f); }catch(Exception e){f.fail(e.getMessage());} }
    public void getRoom(String code,Success s,Failure f){ pool.execute(()->{try{HttpURLConnection c=(HttpURLConnection)new URL(URL+"/rest/v1/dino_rooms?room_code=eq."+URLEncoder.encode(code,"UTF-8")+"&select=*").openConnection();c.setRequestMethod("GET");c.setRequestProperty("apikey",KEY);c.setRequestProperty("Authorization","Bearer "+KEY);int n=c.getResponseCode();if(n<200||n>=300)throw new IOException("Room lookup failed");JSONArray a=new JSONArray(read(c.getInputStream()));if(a.length()==0)throw new IOException("Room not found");main.post(()->s.ok(a.optJSONObject(0)));}catch(Exception e){main.post(()->f.fail(clean(e.getMessage())));}});}
    private String read(InputStream in)throws IOException{StringBuilder s=new StringBuilder();try(BufferedReader r=new BufferedReader(new InputStreamReader(in))){String l;while((l=r.readLine())!=null)s.append(l);}return s.toString();}
}
