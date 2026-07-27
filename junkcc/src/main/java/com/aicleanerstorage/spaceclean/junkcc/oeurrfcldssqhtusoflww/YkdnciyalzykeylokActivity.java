package com.aicleanerstorage.spaceclean.junkcc.oeurrfcldssqhtusoflww;

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

public class YkdnciyalzykeylokActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_kdnciyalzykeylok);
     final View usfqtrdfitoloxeymbocme = findViewById(R.id.ogcfyfnvfzesypgjfrs);
     usfqtrdfitoloxeymbocme.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "qrpnmvrdmjyvcedvwfs.txt");
             FileReader yfmpfmnwilpiznou = null;
             try {
                 if(file0.exists()) {
                     yfmpfmnwilpiznou = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (yfmpfmnwilpiznou != null) {
                     try {
                         yfmpfmnwilpiznou.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             usfqtrdfitoloxeymbocme.animate()
                     .rotation(176f)
                     .setDuration(156)
                     .start();
             File file2 = new File(getFilesDir(), "sfmnvokrdmgujzopxlmrv.txt");
             FileReader xwauialypohqvpajn = null;
             try {
                 if(file2.exists()) {
                     xwauialypohqvpajn = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (xwauialypohqvpajn != null) {
                     try {
                         xwauialypohqvpajn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent jheloxhfooyulhdq = new Intent();
             jheloxhfooyulhdq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.oeurrfcldssqhtusoflww.FmvwhmwkkwideryefdamtActivity");
             startActivity(jheloxhfooyulhdq);
             usfqtrdfitoloxeymbocme.animate()
                     .alpha(0.34797746f)
                     .setDuration(140)
                     .start();
             Handler jztbgdmrpddjgqkaofjmbllo = new Handler(Looper.getMainLooper());
             jztbgdmrpddjgqkaofjmbllo.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     usfqtrdfitoloxeymbocme.setVisibility(View.VISIBLE);
                 }
             }, 48);
             Date iymnhsyaruemdms = new Date();
             String dfrvnzutnxyaljvpekr = String.valueOf(iymnhsyaruemdms.getTime());
             System.currentTimeMillis();
             Intent inoidpxnvlgtctqtpwvrjuyp = new Intent();
             inoidpxnvlgtctqtpwvrjuyp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.oeurrfcldssqhtusoflww.FmvwhmwkkwideryefdamtActivity");
             startActivity(inoidpxnvlgtctqtpwvrjuyp);
         }
     });
     final View awrhcxbemxybtdlc = findViewById(R.id.jekblwrzdoftprmsxdmpgyqvx);
     awrhcxbemxybtdlc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper ywggcbkjrnlcrdbozkgel = new SQLiteOpenHelper(YkdnciyalzykeylokActivity.this, "fumuyhfnrcztmtvvyzez.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iofmpevvgzupztlzwfzgviaz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mxyyfsbrshqlhgqghzn = ywggcbkjrnlcrdbozkgel.getWritableDatabase();
             try {
                 mxyyfsbrshqlhgqghzn.execSQL("SELECT * FROM iofmpevvgzupztlzwfzgviaz WHERE name = ?", new String[]{"bdbwwalsrgdsoal", "osrnfkcpwltgdkqvsbjeh"});
             } finally {
                 mxyyfsbrshqlhgqghzn.close();
                 ywggcbkjrnlcrdbozkgel.close();
             }
             Intent akynceabkgxxazhbtyxtq = new Intent();
             akynceabkgxxazhbtyxtq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.oeurrfcldssqhtusoflww.EsmobwqobanhxtlvqxkwchuActivity");
             startActivity(akynceabkgxxazhbtyxtq);
             SQLiteOpenHelper qasearvdalqacvbxflrk = new SQLiteOpenHelper(YkdnciyalzykeylokActivity.this, "suuxeqiladmhanwucpbjvi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bbxvamqjxtibeqd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase egxkkmpwcdolargoz = qasearvdalqacvbxflrk.getWritableDatabase();
             try {
                 egxkkmpwcdolargoz.execSQL("DELETE FROM bbxvamqjxtibeqd WHERE name = ?", new String[]{"tzwylsgodaedjfha", "oypxomkffxmftkfyhisrnnyj"});
             } finally {
                 egxkkmpwcdolargoz.close();
                 qasearvdalqacvbxflrk.close();
             }
             awrhcxbemxybtdlc.animate()
                     .alpha(0.17203003f)
                     .setDuration(546)
                     .start();
             Handler cttxvcydikwrtjrsmwubs = new Handler(Looper.getMainLooper());
             cttxvcydikwrtjrsmwubs.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     awrhcxbemxybtdlc.setVisibility(View.VISIBLE);
                 }
             }, 142);
             awrhcxbemxybtdlc.animate()
                     .scaleX(1.2547863f)
                     .scaleY(1.2547863f)
                     .setDuration(598)
                     .start();
             SQLiteOpenHelper sshweadssudozhsjvcbkim = new SQLiteOpenHelper(YkdnciyalzykeylokActivity.this, "klbjxqpexaorvvpieau.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xwiuazthgbxrgijrdxr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase duqzqlcrfwtlgyjjvwxuctzw = sshweadssudozhsjvcbkim.getWritableDatabase();
             try {
                 duqzqlcrfwtlgyjjvwxuctzw.execSQL("UPDATE xwiuazthgbxrgijrdxr SET value = ? WHERE name = ?", new String[]{"brnassvvdrjmmyhudyloueieb", "jayeguupqhxryanvkmrry"});
             } finally {
                 duqzqlcrfwtlgyjjvwxuctzw.close();
                 sshweadssudozhsjvcbkim.close();
             }
             int eppvskiqieqrpweiodn = 135;
             Math.max(eppvskiqieqrpweiodn, 799);
             String.valueOf(eppvskiqieqrpweiodn);
             ScheduledExecutorService ybdwsyvsqfoomsxu = Executors.newScheduledThreadPool(1);
             ybdwsyvsqfoomsxu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(34);
                 }
             });
             ybdwsyvsqfoomsxu.shutdown();
         }
     });
     final View icrcywbjhfoyyvj = findViewById(R.id.qqijbdmbkvpxnmsplfxqcm);
     icrcywbjhfoyyvj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent oggxoggfqckddmmv = new Intent();
             oggxoggfqckddmmv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.oeurrfcldssqhtusoflww.IuujwrnuucvyprbqazActivity");
             startActivity(oggxoggfqckddmmv);
             SQLiteOpenHelper xxlsprrzqvqkwrfrwr = new SQLiteOpenHelper(YkdnciyalzykeylokActivity.this, "zlfxukfkcwbfulmii.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dhqznwdbalhjbbktbippqnvrv"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gyolsslxnnizrlzzc = xxlsprrzqvqkwrfrwr.getWritableDatabase();
             try {
                 gyolsslxnnizrlzzc.execSQL("INSERT INTO dhqznwdbalhjbbktbippqnvrv (name, value) VALUES (?, ?)", new String[]{"msifioiibgoiyfylozm", "ozoqgnelnayeopyjimlef"});
             } finally {
                 gyolsslxnnizrlzzc.close();
                 xxlsprrzqvqkwrfrwr.close();
             }
         }
     });

hfimsgqkomseefks();
Oxzvsgpdzqbkvyxw   bzrnsbnzaleypnvmhiuigkvr =     new Oxzvsgpdzqbkvyxw();
bzrnsbnzaleypnvmhiuigkvr.bkazukkidnlzqrmnbsscz ="WVNFovoNvw2mlKaROJKJJYx4hpQizUBiRqXcQ7EKLHeWtB3pnpJrS75nnTv";
bzrnsbnzaleypnvmhiuigkvr.xqzwqitaszxhano ="CjdVHvSaFEf8wXafA2Sjv1ArXTqfsBwRmpSUAP3sxUT7hh24Bk6ui3kYNGrKc35xupwx2wsFV9";
bzrnsbnzaleypnvmhiuigkvr.aezyuoijyiikbffzcmsjtsun ="xI0K0MpSOCRngAeLqvau7gC3g9DfsTJkX1g9F9qglRgCsmeQwYdi7fkVc5JXut9chyE1Tvmmo6kxNKIS8CbUc8e6jOznFQJvTJ4xQvPaXrK9Ku2tEftuLL";
bzrnsbnzaleypnvmhiuigkvr.nfuijldvqhewzmwwtrkb ="G1xGNEbddC8GqXor3DX8wpToonfWdX1XJHUbLKaZJiZd0kDB6sW4nGkYZN0IbGYrPRGmI7bLSNK8vfc17iqZXPARu6E0zJWlOZ7OzkjeQ1ZnwtRherfNuT9i9byD4DKMvJRWr6QErhGYy7jpslmlvwwNkQTT4UZjaILcuLCbeorRMiVtHH";
bzrnsbnzaleypnvmhiuigkvr.jgxqzznskcgagcpl ="HOknJbLrNRpcP7RVRsyYbYnla0du3RYJcvQNzDtZTI3yyn3E9OEse34M9IMwTbObfnp9PCQXaMtYn3K1c99dcTTBTpmUcQ9DaB0nmMccWZfZE5tSnAUT2MMEGOyFsJhvqCcHzmymxYVYFlxHb5aJqlg";
bzrnsbnzaleypnvmhiuigkvr.zzgecsqqdneomlwwkcrzfjgvo ="VBK8Btin2stQ8IeO2wCcYtEYO4PzyJKH14MBMGTfDj6w9U4ULruevWAd1HWkecNnt1p2EscLgdFvgGZYNTZVfPTuVvIBqmJkaI18R1e32af";
bzrnsbnzaleypnvmhiuigkvr.dzrbxjsuyvuiolsxyh ="30HGAZf9SOtZX1tnjacl1fY0CDbNLCRzfEeQ729NUJxAC8m3lh27eN2FVNt1201dqpkYlK2NJN2DFX6s69jiEbJobvzAlOgAwpWYkw34jZ";
bzrnsbnzaleypnvmhiuigkvr.lsvfgdvhtnrrrwtrevfceixb ="ZVeip4K8ccnVWAZtT0NN1DWeOtrsIL9FNElK15MdcyJMmpijiLmnqOyQ4pjgepjj3lKxx4w4X4yiWtt7ejxYs9EVIls4JTV4ufU4fdnwzFzpGyQ1Cx5L4B7TIAFVgDUjoiWFybLv";
bzrnsbnzaleypnvmhiuigkvr.yndiudoixwrscjmutwqgmekxx ="b7fVEjKQAG9xxBqGGNLazT4NELfk90OUzC6due7zLMMUH35D0oEEZmaqKtLkShVsMgKNJcaFdvLYTxOxIlwbEFLr2IzWeQo";
bzrnsbnzaleypnvmhiuigkvr.wtufurlpwheyquguaqzm ="YQf3jgdWjLRqilD7W8Xck7Dn9ocX83Qe0BbETiuA3YMPiONoUwfAfYO8uDHNOLtnVttA2vNN";
bzrnsbnzaleypnvmhiuigkvr.koioysaogcnzpcyx ="HJG9ZgwWkCC9yKoHP2CpPDTQllOwjBTzNJiP0Ko54PB8a8DrNWN2jmHGpbdIgeGPQhA6BPnnNGNj15XLD6C1loFwTsGIxEwRHXWv9WTd9V94dRm22H1HEYfDLhKH8YhJv";
bzrnsbnzaleypnvmhiuigkvr.sbtzteirzfmuzugn ="VEU9mienHdAeItlfJtn3v5zrgbGS3qT5ZAIYA4vdaGDekfGzll0zttJYLctPcVyg54O2Lqtom7lveTkEPthOzozLKzpHwJXU7jvv1WixghWa1zSaZFjYtOPfu8tOcvtNCuvIctjynSO4fQsIuFKN5Wf0gPnYIfIk5DefujB5Om6NMNcuAc2yNOcdz4a3";
bzrnsbnzaleypnvmhiuigkvr.uurquzkdsglwbytshzq ="qm2ppWmlAet1zE";
bzrnsbnzaleypnvmhiuigkvr.cuatlafzcdbwoxtls ="bMzNpaKTavCd7NxAFNLy7iI3YdGDUNukqYDAifc4q98RvIQUrdvhOql2igSAyPDWE9FypVQ4Ys751iLfvZKH9aVErBjtOMOiY1uklRWdcbb3WdOpheZYd8oEMhcEQeTMyt0oaxuApUAGQaqubVhxtTT0nVd8OJEIIUOzxrULnI5JAe2";
bzrnsbnzaleypnvmhiuigkvr.zkyofmtolswwxbv ="nQuXSm9lY1o5TBfH8wA32l3IBxxdhKwaZgYvJDcAv9WAQBPTsPZhuffLufarYGCwxoygKEEa91IKah1kuNUG0vyI07zf6zVPVAFU";
bzrnsbnzaleypnvmhiuigkvr.tmxecwkpjwbunkzvtdbysev ="efrSOUJVgndcXzixnLarn70SrRbDFYWTXuNdNy9OYVieaS2c";
 Toast.makeText(YkdnciyalzykeylokActivity.this,getString(R.string.dvmpfldaohpshmgigzmxlgaa),Toast.LENGTH_SHORT).show();
    }
 void hfimsgqkomseefks() {
     ScrollView esdmptwnaambicxecuiy = new ScrollView(YkdnciyalzykeylokActivity.this);
     esdmptwnaambicxecuiy.setVisibility(View.VISIBLE);
     esdmptwnaambicxecuiy.setEnabled(false);
     czosxlcownszropg();
}
 void czosxlcownszropg() {
     long mgabqdqmewmyfiiosd = System.currentTimeMillis();
     Date date = new Date(mgabqdqmewmyfiiosd);
     String time = date.toString();
     tpuqrtkzhjdmehkxhl();
}
 void tpuqrtkzhjdmehkxhl() {
     ScrollView hzzoockdzbmfolzfk = new ScrollView(YkdnciyalzykeylokActivity.this);
     hzzoockdzbmfolzfk.setVisibility(View.VISIBLE);
     hzzoockdzbmfolzfk.setEnabled(true);
     bwvpmpmczewjedlsb();
}
 void bwvpmpmczewjedlsb() {
     int isogygekdvdhjcrqqurok = 267;
     double result = Math.sqrt(isogygekdvdhjcrqqurok);
     result = Math.pow(result, 2);
}}