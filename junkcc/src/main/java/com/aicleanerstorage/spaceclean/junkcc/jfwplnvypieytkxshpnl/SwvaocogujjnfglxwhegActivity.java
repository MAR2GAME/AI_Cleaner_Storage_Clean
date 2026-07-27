package com.aicleanerstorage.spaceclean.junkcc.jfwplnvypieytkxshpnl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.aicleanerstorage.spaceclean.junkcc.R;
import java.lang.Exception;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.widget.ScrollView;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class SwvaocogujjnfglxwhegActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wvaocogujjnfglxwheg);
     final View gauyfolzvmorqjqvbuej = findViewById(R.id.iypjuzrxqfdqkuedlcklz);
     gauyfolzvmorqjqvbuej.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "aolivejpnfojjqfudjkxlzv.txt");
             if(file0.exists()) file0.delete();
             SQLiteOpenHelper gjqqqhhrtglrvhycyzfsmmkd = new SQLiteOpenHelper(SwvaocogujjnfglxwhegActivity.this, "dxfyzvfcszzeftpvi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bxtwgvufwqheohdxaxdb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lxxheocgovpczpq = gjqqqhhrtglrvhycyzfsmmkd.getWritableDatabase();
             try {
                 lxxheocgovpczpq.execSQL("INSERT INTO bxtwgvufwqheohdxaxdb (name, value) VALUES (?, ?)", new String[]{"hrhismjxzhwrmzehadfi", "arkarsfwaufuarcnss"});
             } finally {
                 lxxheocgovpczpq.close();
                 gjqqqhhrtglrvhycyzfsmmkd.close();
             }
             int tozmmdbzjqhixot = 219;
             Math.max(tozmmdbzjqhixot, 269);
             String.valueOf(tozmmdbzjqhixot);
         }
     });
     final View tvxlunsunnulnchmbflhsmmn = findViewById(R.id.lrdwoqtqiucaosno);
     tvxlunsunnulnchmbflhsmmn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tbypbqagcptghoydqbqhaq = new SQLiteOpenHelper(SwvaocogujjnfglxwhegActivity.this, "kwkxanjcxemoyfct.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "duccbitjutnmxyiyxknfkzcdg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mwzkxocealrpshimgeu = tbypbqagcptghoydqbqhaq.getWritableDatabase();
             try {
                 mwzkxocealrpshimgeu.execSQL("DELETE FROM duccbitjutnmxyiyxknfkzcdg WHERE name = ?", new String[]{"lfvdrsjawfupnksgkhyne", "jyejqruzpocqtymfigerb"});
             } finally {
                 mwzkxocealrpshimgeu.close();
                 tbypbqagcptghoydqbqhaq.close();
             }
             Intent wubdvyqjfhfknpnuzt = new Intent();
             wubdvyqjfhfknpnuzt.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.jfwplnvypieytkxshpnl.CxbfqgjuovdeovcizhActivity");
             startActivity(wubdvyqjfhfknpnuzt);
             tvxlunsunnulnchmbflhsmmn.animate()
                     .translationX(64f)
                     .translationY(125f)
                     .setDuration(998)
                     .start();
             SQLiteOpenHelper aedxkdwtfplcylbhkvlmworja = new SQLiteOpenHelper(SwvaocogujjnfglxwhegActivity.this, "ogkrgcrvegzhlxyacvfgylmac.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fzahxjkkzrknrwx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ythijycyvmlikpwhyeg = aedxkdwtfplcylbhkvlmworja.getWritableDatabase();
             try {
                 ythijycyvmlikpwhyeg.execSQL("UPDATE fzahxjkkzrknrwx SET value = ? WHERE name = ?", new String[]{"edecwafwrbpzbtvwj", "dzwocomecolgindzoztodns"});
             } finally {
                 ythijycyvmlikpwhyeg.close();
                 aedxkdwtfplcylbhkvlmworja.close();
             }
             Intent egjmkknrihkxbydtqn = new Intent();
             egjmkknrihkxbydtqn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.jfwplnvypieytkxshpnl.CxbfqgjuovdeovcizhActivity");
             startActivity(egjmkknrihkxbydtqn);
             Date smptcxmvrmbmpcjvaf = new Date();
             String wzxumlerqbnxkah = String.valueOf(smptcxmvrmbmpcjvaf.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper vbksexudlcjjxatqoqbmnz = new SQLiteOpenHelper(SwvaocogujjnfglxwhegActivity.this, "gmsdqtbsyyvviscxxl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qeqgzhxchltopfbvgssvf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rvemqiromqjjnuyuevvhxq = vbksexudlcjjxatqoqbmnz.getWritableDatabase();
             try {
                 rvemqiromqjjnuyuevvhxq.execSQL("UPDATE qeqgzhxchltopfbvgssvf SET value = ? WHERE name = ?", new String[]{"krfzzmmohkgcdgmxwqag", "dfhsdncmfgxhnfisbeiakwcp"});
             } finally {
                 rvemqiromqjjnuyuevvhxq.close();
                 vbksexudlcjjxatqoqbmnz.close();
             }
             ScheduledExecutorService scjtncafkgxapumbcfdbcnd = Executors.newScheduledThreadPool(3);
             scjtncafkgxapumbcfdbcnd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(19);
                 }
             });
             scjtncafkgxapumbcfdbcnd.shutdown();
             Intent ejogjymuxprjqpmufymytqu = new Intent();
             ejogjymuxprjqpmufymytqu.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.jfwplnvypieytkxshpnl.CxbfqgjuovdeovcizhActivity");
             startActivity(ejogjymuxprjqpmufymytqu);
             File file9 = new File(getFilesDir(), "sgdzyghyrxkejbpsonw.txt");
             FileReader ksohdzpdhnjehnn = null;
             try {
                 if(file9.exists()) {
                     ksohdzpdhnjehnn = new FileReader(file9);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ksohdzpdhnjehnn != null) {
                     try {
                         ksohdzpdhnjehnn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View wfadcclfownnxqclchvnw = findViewById(R.id.stroptqmimkdelcwfoy);
     wfadcclfownnxqclchvnw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService ifjpyfmcvgzwgddpxhn = Executors.newScheduledThreadPool(2);
             ifjpyfmcvgzwgddpxhn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(49);
                 }
             });
             ifjpyfmcvgzwgddpxhn.shutdown();
             Date tpsgkesffhtrsoruex = new Date();
             String uquyotwhofbigttswix = String.valueOf(tpsgkesffhtrsoruex.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService saoveljnycqebpzqouywdyebe = Executors.newScheduledThreadPool(4);
             saoveljnycqebpzqouywdyebe.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(78);
                 }
             });
             saoveljnycqebpzqouywdyebe.shutdown();
             File file3 = new File(getFilesDir(), "sjrpaivlqtncjedtrnrqbky.txt");
             Date tauyhnrwajzkkaemeky = new Date();
             String fyxggtvnqydmyqfct = String.valueOf(tauyhnrwajzkkaemeky.getTime());
             System.currentTimeMillis();
             wfadcclfownnxqclchvnw.animate()
                     .translationX(174f)
                     .translationY(92f)
                     .setDuration(745)
                     .start();
         }
     });
     final View isisctygdgzlpxrkgqz = findViewById(R.id.sufconbbmxhwudpcdw);
     isisctygdgzlpxrkgqz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService feoqtmzqxybypof = Executors.newScheduledThreadPool(2);
             feoqtmzqxybypof.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(73);
                 }
             });
             feoqtmzqxybypof.shutdown();
             SQLiteOpenHelper jdlrdmqnxevrhbcxbmdofddo = new SQLiteOpenHelper(SwvaocogujjnfglxwhegActivity.this, "maeuipvakvedhxilcg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "chpxwdgsqazmcfjpj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase cpyxqwwwneayucrczwkgmf = jdlrdmqnxevrhbcxbmdofddo.getWritableDatabase();
             try {
                 cpyxqwwwneayucrczwkgmf.execSQL("UPDATE chpxwdgsqazmcfjpj SET value = ? WHERE name = ?", new String[]{"ejtwxfjnfkgmfrhsmhdveir", "bcskokaurxddvpwhbqwocms"});
             } finally {
                 cpyxqwwwneayucrczwkgmf.close();
                 jdlrdmqnxevrhbcxbmdofddo.close();
             }
         }
     });

jsntgscwechusnffpjnbhsadt();
Fhlglnpmljxgodlwkykcjyittc   tlxomwnxvytwoicnryz =     new Fhlglnpmljxgodlwkykcjyittc();
tlxomwnxvytwoicnryz.rekqlnchjhihgljecatssv ="5lD6gZflYrCXpyfZ3hX1dGviKDaidIOiFIXBFP6cLdsasvlohmUpWJxxsMWRjpHDV6lv7jRZF20LE8jdD2yjYLnDUf5uddD4ryggsVwFEbtl25ZxywRjhu3601cheEAuYNM0nhoZkeLIG1tDt";
tlxomwnxvytwoicnryz.gcbkriatxwoqzmyv ="ndrFDTYwzl7frRZbdQQnwGZraDJOX0mf2At1kx8ZUiEhJlyiXBf8AUjoG2kqTLxhOwDE1VJqj8QST5fjp1eU34p6jscB5Dz9qeaL0w5J3IqHIz3K4F5uoWpS2bkjXjpBFOP3s96SA87W8MUZNH5PN5xYpnSaR0jQURB2YzGIorotAZejet2tjJBWhYLf";
tlxomwnxvytwoicnryz.acwuimtibswzwasfjuzoc ="Zqmp9WeocPc6XGzkfnrmA3rTvfDQAYfM2OgmlryxSxgeGc9jEJX4az6Sgb0W3JLhwiJ1OWfIUkLqIuMyc4X4iemgnsBAhFTc9Ca";
tlxomwnxvytwoicnryz.huxldwsanhfceosfedrt ="2XRKCBvfJfQjgbCWQxPd9UQOoepoMHgzBHcRKI2lUMB";
tlxomwnxvytwoicnryz.wgifagfzphurgdxkhnzoltn ="30a0sUqTF3VQqJNdTOUuLlf6bRvwvbsfoayUMxz9YKro9eQIr";
tlxomwnxvytwoicnryz.navivmtkswptspcaaixjjp ="UucXFQjnCHbJakwwzJ8bzlD0RnIancgyAxCyEtZko6";
tlxomwnxvytwoicnryz.kbgsldnruvhvhvxk ="j4ajFSJ67JBjzFDX0pIHCcNJNla2lUjIOikpfcZGhbKYQ3wVgvSQ3a3C3jgjlaqoxiNTSy9zEuDidEP4TrdejwNMVYllgGgy33";
tlxomwnxvytwoicnryz.ahvlctrlhztoeeoqbyn ="IrGzFLwLyeDYjS1yMAizF6NSx0mqVlw7RLqmLnCDiKWH6azhrY1MwGyYSA3taVXa0voyRjz945NtiU8NaJPIY";
tlxomwnxvytwoicnryz.codxekbkyasjgro ="njpSdhn8AcaAwNgud0XakYW0xf8zoh1TOcZQEgvdWoLMYz8gS5xAAUGZhUQ9ZKw2F4T2WifPsfFWOfoEXAfXC2I4Ye5B0f3iQ0GebulbYpvo7zLZXl4HUAEE2h0fJsGZesZkn1XFzVbIV1XKkXlnSMiYAO3quML06fvv5Mh0QEDPuEvcJ2";
tlxomwnxvytwoicnryz.qgdkjjftwfnnzxyrhrfm ="JOQmpBgFnbFyKSSSJJsbbTZCJmAmPliLFHXSmAIeacR5iuBTen2UxSkTcWh2oipMpQPIl93J2woUjOtomK3VLS9n6LgZpohZxZoOHi9yrjwXJSLIR24ibs2P57F5OS0fZbBnVGdSBVI2JgfF6V4mUMSbtBgELnu36GSN5c6wLWbZjRa4FhLw7fd";
 Toast.makeText(SwvaocogujjnfglxwhegActivity.this,getString(R.string.kvihjxekkjhfrmv),Toast.LENGTH_SHORT).show();
    }
 void jsntgscwechusnffpjnbhsadt() {
     long iumtqvmjuzpnfmjdk = System.currentTimeMillis();
     Date date = new Date(iumtqvmjuzpnfmjdk);
     String time = date.toString();
     panibnuksapyyelhdnath();
}
 void panibnuksapyyelhdnath() {
     int ddomrujgtjbqlsyk = 408;
     double result = Math.sqrt(ddomrujgtjbqlsyk);
     result = Math.pow(result, 2);
}}