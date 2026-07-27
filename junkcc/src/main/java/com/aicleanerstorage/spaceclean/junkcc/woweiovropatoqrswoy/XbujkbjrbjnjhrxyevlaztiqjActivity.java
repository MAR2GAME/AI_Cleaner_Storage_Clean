package com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy;

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

public class XbujkbjrbjnjhrxyevlaztiqjActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bujkbjrbjnjhrxyevlaztiqj);
     final View vmynjvfqzyycnne = findViewById(R.id.svxglaihzkldaqhcbfapcyte);
     vmynjvfqzyycnne.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "ftaydavsutgaunwpyct.txt");
             FileReader jtgdjsuemdlhxkckb = null;
             try {
                 if(file0.exists()) {
                     jtgdjsuemdlhxkckb = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jtgdjsuemdlhxkckb != null) {
                     try {
                         jtgdjsuemdlhxkckb.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date huwwgdjaclecyhgbdrqv = new Date();
             String seklabmsnxxqdjurcyblgrzaq = String.valueOf(huwwgdjaclecyhgbdrqv.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper yciyfnhuprjifosguabihl = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "jacctdpckkyfrvhjkybxitr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "oqkonkrlkmxihencua"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wqvtdwmbtuwizfmozsmwtnwl = yciyfnhuprjifosguabihl.getWritableDatabase();
             try {
                 wqvtdwmbtuwizfmozsmwtnwl.execSQL("INSERT INTO oqkonkrlkmxihencua (name, value) VALUES (?, ?)", new String[]{"mwqjdkgfpzmpidzssclqd", "zmeuyozcpcmfnswdywkn"});
             } finally {
                 wqvtdwmbtuwizfmozsmwtnwl.close();
                 yciyfnhuprjifosguabihl.close();
             }
             Date eohkzlfhkykcmwscwm = new Date();
             String lzmcmyjxauyxucvkm = String.valueOf(eohkzlfhkykcmwscwm.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper ypoeavqvuuylkygpbmkbyfdx = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "qdcbdfrjzogbgptdlncceishl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "oirgyevygdofotfxvqiskybxf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qyujqdbhcrsovmtpe = ypoeavqvuuylkygpbmkbyfdx.getWritableDatabase();
             try {
                 qyujqdbhcrsovmtpe.execSQL("INSERT INTO oirgyevygdofotfxvqiskybxf (name, value) VALUES (?, ?)", new String[]{"tbqmstfxhafecrldlnd", "kreesfvaidskxsokgwujr"});
             } finally {
                 qyujqdbhcrsovmtpe.close();
                 ypoeavqvuuylkygpbmkbyfdx.close();
             }
             vmynjvfqzyycnne.animate()
                     .rotation(127f)
                     .setDuration(576)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "vdtzzirtncnebazfxadf", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent vqrcfzlokkrnuxspbvgi = new Intent();
             vqrcfzlokkrnuxspbvgi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.LirbeozdtjwyncilmsidznswzjActivity");
             startActivity(vqrcfzlokkrnuxspbvgi);
         }
     });
     final View qiklouiqrzxboqtblbpwwm = findViewById(R.id.hcrdgfqevjuzeqdcsvrofnp);
     qiklouiqrzxboqtblbpwwm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "vvvzyffelxipeddyldnpcl.txt");
             if(file0.exists()) file0.delete();
             Intent zwfbscxdcbtoswjhbj = new Intent();
             zwfbscxdcbtoswjhbj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.WchoipymoedlfojcdyqActivity");
             startActivity(zwfbscxdcbtoswjhbj);
             ScheduledExecutorService bwcitutqpqmhbjbcusjfbriu = Executors.newScheduledThreadPool(4);
             bwcitutqpqmhbjbcusjfbriu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(7);
                 }
             });
             bwcitutqpqmhbjbcusjfbriu.shutdown();
             Intent ktjhsucrbnfdmwsakotwcdl = new Intent();
             ktjhsucrbnfdmwsakotwcdl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.WchoipymoedlfojcdyqActivity");
             startActivity(ktjhsucrbnfdmwsakotwcdl);
             Date qehjpvprmxfkfupune = new Date();
             String vetmgxpzgvtefhmnezazxbocg = String.valueOf(qehjpvprmxfkfupune.getTime());
             System.currentTimeMillis();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "mvcuslgtxzbeiwytwlzjtwe", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View rkxtiochcxkcdxpx = findViewById(R.id.tbrkfdqtgshqjuearekunduf);
     rkxtiochcxkcdxpx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper nqfnrvqezyvaldkbxdgi = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "ogujdtryfpaoetkivjjejzl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kruhnloyjeglxehhritoyw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bpxpctdicbgopttjav = nqfnrvqezyvaldkbxdgi.getWritableDatabase();
             try {
                 bpxpctdicbgopttjav.execSQL("UPDATE kruhnloyjeglxehhritoyw SET value = ? WHERE name = ?", new String[]{"qibgugftthhhvzm", "mmymaljroncunxiefjjmcz"});
             } finally {
                 bpxpctdicbgopttjav.close();
                 nqfnrvqezyvaldkbxdgi.close();
             }
             Date oxoysxyrjhyibkuhldnkdx = new Date();
             String etknjcehfhsvvga = String.valueOf(oxoysxyrjhyibkuhldnkdx.getTime());
             System.currentTimeMillis();
             Intent vtlopumhxhllcevba = new Intent();
             vtlopumhxhllcevba.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.GjoybzmgccoxtogpasjkfActivity");
             startActivity(vtlopumhxhllcevba);
             File file3 = new File(getFilesDir(), "qbxoiujkvfgdbfjanspvx.txt");
             if(file3.exists()) file3.delete();
             ScheduledExecutorService buernsoxdwjdpxl = Executors.newScheduledThreadPool(4);
             buernsoxdwjdpxl.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(38);
                 }
             });
             buernsoxdwjdpxl.shutdown();
         }
     });
     final View rpzsjhohylxbrazq = findViewById(R.id.ufejcnpsiixnhnbcy);
     rpzsjhohylxbrazq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper lvrzdaccumbscgjmzhvcxxam = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "ndaaanxzoaoozwejbzzndhdhz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zjeiehpswcdgpmayyzl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hzyggywihiinemxvflrdajz = lvrzdaccumbscgjmzhvcxxam.getWritableDatabase();
             try {
                 hzyggywihiinemxvflrdajz.execSQL("UPDATE zjeiehpswcdgpmayyzl SET value = ? WHERE name = ?", new String[]{"ytyojzagvsnoxmi", "skzeozqildjsdfztmkakhhfld"});
             } finally {
                 hzyggywihiinemxvflrdajz.close();
                 lvrzdaccumbscgjmzhvcxxam.close();
             }
             Intent obbfmwiuewqfvorlwezqpks = new Intent();
             obbfmwiuewqfvorlwezqpks.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.FznjqnxhqyndmlmnwhlauaActivity");
             startActivity(obbfmwiuewqfvorlwezqpks);
             int kcngfpvvdkdppugazybyorvxx = 348;
             Math.max(kcngfpvvdkdppugazybyorvxx, 996);
             String.valueOf(kcngfpvvdkdppugazybyorvxx);
             ScheduledExecutorService okrmqqnnhigfdjxuwd = Executors.newScheduledThreadPool(2);
             okrmqqnnhigfdjxuwd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(95);
                 }
             });
             okrmqqnnhigfdjxuwd.shutdown();
         }
     });
     final View kyhrnlnwnfyouqfuspiaq = findViewById(R.id.carfpaevclgdiajn);
     kyhrnlnwnfyouqfuspiaq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent igvuxrugozroiqgjntmuvs = new Intent();
             igvuxrugozroiqgjntmuvs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.OuecgqenyfisqcnaqhzvzActivity");
             startActivity(igvuxrugozroiqgjntmuvs);
             SQLiteOpenHelper rwgzcxchyyvmickwo = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "kovnlwtvaorkhjfmp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wiesijarpkxixntia"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase thrwxsninpjwwzkapurtotz = rwgzcxchyyvmickwo.getWritableDatabase();
             try {
                 thrwxsninpjwwzkapurtotz.execSQL("INSERT INTO wiesijarpkxixntia (name, value) VALUES (?, ?)", new String[]{"ajwzfzoacgmvwswrmfina", "datwfvwfbhcxwdmeyo"});
             } finally {
                 thrwxsninpjwwzkapurtotz.close();
                 rwgzcxchyyvmickwo.close();
             }
             File file2 = new File(getFilesDir(), "ctwzaxruicilflpml.txt");
             ScheduledExecutorService immmewkaummmfdmuvjsaobn = Executors.newScheduledThreadPool(4);
             immmewkaummmfdmuvjsaobn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(2);
                 }
             });
             immmewkaummmfdmuvjsaobn.shutdown();
             kyhrnlnwnfyouqfuspiaq.animate()
                     .rotation(17f)
                     .setDuration(976)
                     .start();
             ScheduledExecutorService lvuoocrckeyhlfzkq = Executors.newScheduledThreadPool(3);
             lvuoocrckeyhlfzkq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(34);
                 }
             });
             lvuoocrckeyhlfzkq.shutdown();
             kyhrnlnwnfyouqfuspiaq.animate()
                     .alpha(0.78895617f)
                     .setDuration(74)
                     .start();
             int cffjjhzcdbyulhnpmnoohwz = 681;
             Math.max(cffjjhzcdbyulhnpmnoohwz, 627);
             String.valueOf(cffjjhzcdbyulhnpmnoohwz);
         }
     });
     final View qmkpiartnuvpsaamjtwdoh = findViewById(R.id.gsvnopnpewebwcvbewrahknjg);
     qmkpiartnuvpsaamjtwdoh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             qmkpiartnuvpsaamjtwdoh.animate()
                     .scaleX(1.3930374f)
                     .scaleY(1.3930374f)
                     .setDuration(534)
                     .start();
             int hnrsvfovcvzwulzlcuwdejt = 77;
             Math.max(hnrsvfovcvzwulzlcuwdejt, 651);
             String.valueOf(hnrsvfovcvzwulzlcuwdejt);
             qmkpiartnuvpsaamjtwdoh.animate()
                     .translationX(105f)
                     .translationY(68f)
                     .setDuration(275)
                     .start();
             File file3 = new File(getFilesDir(), "aiavmtzbtosdirvbvccbrj.txt");
             Intent toguwmgsgdpnsmucjilzlklj = new Intent();
             toguwmgsgdpnsmucjilzlklj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.BppxfeayqqbezillxtotoActivity");
             startActivity(toguwmgsgdpnsmucjilzlklj);
             Handler tqvfsoyyshrtpxefndnoo = new Handler(Looper.getMainLooper());
             tqvfsoyyshrtpxefndnoo.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     qmkpiartnuvpsaamjtwdoh.setVisibility(View.VISIBLE);
                 }
             }, 432);
             qmkpiartnuvpsaamjtwdoh.animate()
                     .scaleX(0.79042697f)
                     .scaleY(0.79042697f)
                     .setDuration(164)
                     .start();
         }
     });
     final View kngtsqnugqbcrwpvdocbquzkc = findViewById(R.id.ryzcddupnasdnkzi);
     kngtsqnugqbcrwpvdocbquzkc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "vhvdbfujpxteaxmchvaxs.txt");
             Intent xcuckcqjumkqnvpttqerdxswy = new Intent();
             xcuckcqjumkqnvpttqerdxswy.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.SlvkahqfozruhwrpyedwdnActivity");
             startActivity(xcuckcqjumkqnvpttqerdxswy);
             SQLiteOpenHelper juylmcerkdhytdmthztk = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "cieoavneywswwnvum.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qstjfgziippxhndknfivln"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yadqkfvhrjwjjwq = juylmcerkdhytdmthztk.getWritableDatabase();
             try {
                 yadqkfvhrjwjjwq.execSQL("DELETE FROM qstjfgziippxhndknfivln WHERE name = ?", new String[]{"xufxmsulnupqnzuolgjsk", "fvltydignfoyabugldjmq"});
             } finally {
                 yadqkfvhrjwjjwq.close();
                 juylmcerkdhytdmthztk.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "rzhicwbbfduvldkoz", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent xdlryskacwaenfybjseodt = new Intent();
             xdlryskacwaenfybjseodt.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.SlvkahqfozruhwrpyedwdnActivity");
             startActivity(xdlryskacwaenfybjseodt);
         }
     });
     final View tampyrmqvfedppujhitrhqkhh = findViewById(R.id.fjmsuilyywvycwhanmmh);
     tampyrmqvfedppujhitrhqkhh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "mwenbbehgxdajiqgvo.txt");
             FileReader jerzvdirhzltlthhqieunwsb = null;
             try {
                 if(file0.exists()) {
                     jerzvdirhzltlthhqieunwsb = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jerzvdirhzltlthhqieunwsb != null) {
                     try {
                         jerzvdirhzltlthhqieunwsb.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper qnovqcmwjvauuaojggudbcmq = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "obypfwhtwpokbatgbljer.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "uxkamnmaupdjczt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dvvaoywvotevtarbayy = qnovqcmwjvauuaojggudbcmq.getWritableDatabase();
             try {
                 dvvaoywvotevtarbayy.execSQL("SELECT * FROM uxkamnmaupdjczt WHERE name = ?", new String[]{"vejzopmrnkfndescrpmvs", "vqxhmothmscsaszud"});
             } finally {
                 dvvaoywvotevtarbayy.close();
                 qnovqcmwjvauuaojggudbcmq.close();
             }
             tampyrmqvfedppujhitrhqkhh.animate()
                     .translationX(88f)
                     .translationY(22f)
                     .setDuration(97)
                     .start();
             Intent jnvlmpvfofgmglozlrngxjx = new Intent();
             jnvlmpvfofgmglozlrngxjx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.NvygdexchwxycwdgActivity");
             startActivity(jnvlmpvfofgmglozlrngxjx);
         }
     });
     final View grxxzmyviocfifnhnnvyeak = findViewById(R.id.mmyuhublzgfjuewsrchtmf);
     grxxzmyviocfifnhnnvyeak.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String kiznwxgixjmhchhn = "40bHczIRuaxa88pc4wmsdJhNiTaMVI13RdO06SRdIWfMRzTQ40SCIhUpbyh0XmrIH7nTGmqEniXnGuhTRE71aEq3dAd8KhXs0ZGWoz58trsxhuijszpXXEqOlXYbWAuH6RDlYV1AHPeYBt1IjukiEpgWzDOO1JAnoYwhHsTrKGBwnSD";
             kiznwxgixjmhchhn.substring(1);
             kiznwxgixjmhchhn.replace('x', 'm');
             Intent ltbgmbzzbilldmmzamh = new Intent();
             ltbgmbzzbilldmmzamh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.JvuglyecgmayzcxmvqoefpActivity");
             startActivity(ltbgmbzzbilldmmzamh);
             File file2 = new File(getFilesDir(), "ngyhikrblkxnuqjdluwa.txt");
             Date kthklayyvzzkzjkggsr = new Date();
             String yijxvvcfkpnhvuhv = String.valueOf(kthklayyvzzkzjkggsr.getTime());
             System.currentTimeMillis();
             File file4 = new File(getFilesDir(), "gvfznrnxyavoqajehaqnf.txt");
             Date luqlpsgzfnhhtackybkstvrv = new Date();
             String dfeggnleaiuiuyvparfk = String.valueOf(luqlpsgzfnhhtackybkstvrv.getTime());
             System.currentTimeMillis();
             Handler tisdbkqrvufwmfldh = new Handler(Looper.getMainLooper());
             tisdbkqrvufwmfldh.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     grxxzmyviocfifnhnnvyeak.setVisibility(View.VISIBLE);
                 }
             }, 656);
             grxxzmyviocfifnhnnvyeak.animate()
                     .translationX(97f)
                     .translationY(7f)
                     .setDuration(295)
                     .start();
             File file8 = new File(getFilesDir(), "jtdojuqxbnphefsejvdpuvpjj.txt");
             Intent cecfnusbjhfxofgzg = new Intent();
             cecfnusbjhfxofgzg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.JvuglyecgmayzcxmvqoefpActivity");
             startActivity(cecfnusbjhfxofgzg);
         }
     });
     final View ddbydvfcnndrtndlozsmsuj = findViewById(R.id.jfugshojceulphnon);
     ddbydvfcnndrtndlozsmsuj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date zfzxhsksurafiedkwedfcac = new Date();
             String zjvwldphmxglxautqggb = String.valueOf(zfzxhsksurafiedkwedfcac.getTime());
             System.currentTimeMillis();
             Intent hhnhlkurejsrrvbs = new Intent();
             hhnhlkurejsrrvbs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.FahirgijipkyiliyActivity");
             startActivity(hhnhlkurejsrrvbs);
             ScheduledExecutorService hpaesnyakqfcdeifehouz = Executors.newScheduledThreadPool(4);
             hpaesnyakqfcdeifehouz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(62);
                 }
             });
             hpaesnyakqfcdeifehouz.shutdown();
             ddbydvfcnndrtndlozsmsuj.animate()
                     .alpha(0.7779429f)
                     .setDuration(734)
                     .start();
             Intent ohtmnyrqqzhtlyndultzchub = new Intent();
             ohtmnyrqqzhtlyndultzchub.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.FahirgijipkyiliyActivity");
             startActivity(ohtmnyrqqzhtlyndultzchub);
             SQLiteOpenHelper ogveozgzyocmrwvd = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "ddvscjyawiwmrzlumxuyp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kxjjlknehtdubwkll"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mnyhasvmtiryjzkgj = ogveozgzyocmrwvd.getWritableDatabase();
             try {
                 mnyhasvmtiryjzkgj.execSQL("DELETE FROM kxjjlknehtdubwkll WHERE name = ?", new String[]{"netnosnfsheptagxue", "pscypmxukzgzdeybvz"});
             } finally {
                 mnyhasvmtiryjzkgj.close();
                 ogveozgzyocmrwvd.close();
             }
             String bskllhbsunayocfnf = "oSeRpGzn5xIuI0wt4x2D7983c65FuU0KFywrxbW4FhU3kh2ykHLLOFT2pjVQu3LOOxu1Veqn5R2EUB5hgJPwVXzNkDdW43EwU8idDITcJH4J5gHP2JOErA25X2qGWBZ4OnD9Id1iyjIaIBd3MVmZlV4egEtIVMSyyhqmv0QL1LHdfRAfP";
             bskllhbsunayocfnf.substring(3);
             bskllhbsunayocfnf.replace('o', 'l');
             Handler fxvswbetqhbjjjxahh = new Handler(Looper.getMainLooper());
             fxvswbetqhbjjjxahh.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ddbydvfcnndrtndlozsmsuj.setVisibility(View.VISIBLE);
                 }
             }, 283);
             File file8 = new File(getFilesDir(), "vipmasymnwcmvsyhhbuibli.txt");
             Intent uhinjfyxkaiatfldyxzkjeb = new Intent();
             uhinjfyxkaiatfldyxzkjeb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.FahirgijipkyiliyActivity");
             startActivity(uhinjfyxkaiatfldyxzkjeb);
         }
     });
     final View nmojppsrwlmdbfymzohg = findViewById(R.id.vrzernjuqnfswfxwyyovgd);
     nmojppsrwlmdbfymzohg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             nmojppsrwlmdbfymzohg.animate()
                     .scaleX(1.5803682f)
                     .scaleY(1.5803682f)
                     .setDuration(293)
                     .start();
             SQLiteOpenHelper lbdbjtqiflwsnxbigqmcirq = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "lqpjpzuhoguqtscdn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "psdqwcrdleidvfo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wdreywgsyhvexlrtaxvk = lbdbjtqiflwsnxbigqmcirq.getWritableDatabase();
             try {
                 wdreywgsyhvexlrtaxvk.execSQL("DELETE FROM psdqwcrdleidvfo WHERE name = ?", new String[]{"ijytbivnzpoqafwbwtlektrfi", "ppxeeksdpfbnuaza"});
             } finally {
                 wdreywgsyhvexlrtaxvk.close();
                 lbdbjtqiflwsnxbigqmcirq.close();
             }
             ScheduledExecutorService uinasdvrjoweyajwekkows = Executors.newScheduledThreadPool(3);
             uinasdvrjoweyajwekkows.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(74);
                 }
             });
             uinasdvrjoweyajwekkows.shutdown();
             SQLiteOpenHelper vgmzpcikddidlqnhqjdnjf = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "anusijxepbbnuwptzno.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "sawkyglajegkzheireeynqkb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jycibrxnilhwtph = vgmzpcikddidlqnhqjdnjf.getWritableDatabase();
             try {
                 jycibrxnilhwtph.execSQL("SELECT * FROM sawkyglajegkzheireeynqkb WHERE name = ?", new String[]{"ynterwvtdlhboonyw", "qfpzprvwuvrdolnkx"});
             } finally {
                 jycibrxnilhwtph.close();
                 vgmzpcikddidlqnhqjdnjf.close();
             }
             Date bqxajwbpqwseaojqyfqlguftu = new Date();
             String svmomjgiscvvsexpyp = String.valueOf(bqxajwbpqwseaojqyfqlguftu.getTime());
             System.currentTimeMillis();
             Handler xlycqernnanrfaurok = new Handler(Looper.getMainLooper());
             xlycqernnanrfaurok.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     nmojppsrwlmdbfymzohg.setVisibility(View.VISIBLE);
                 }
             }, 126);
             String lprldkfgxltcmsbrxtvh = "jAeIyiQgfcGgK8KU7R3rKWzbyfvAH7Mx7aN8mQXHx3NJmA4tOr6bgUIylJgAHQRjN2dKloiTufa8qp8VwTayVwnF24b8C1t1IvbBqJjD8";
             lprldkfgxltcmsbrxtvh.substring(0);
             lprldkfgxltcmsbrxtvh.replace('c', 'y');
             SQLiteOpenHelper eewsuohcptnconwqj = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "xturorfditztkqhk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vzwoiuejqpmkzjsfbd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kyommpdlinnoenlim = eewsuohcptnconwqj.getWritableDatabase();
             try {
                 kyommpdlinnoenlim.execSQL("SELECT * FROM vzwoiuejqpmkzjsfbd WHERE name = ?", new String[]{"haiidbfsehqybcu", "pbpbgvaxcvjtqyk"});
             } finally {
                 kyommpdlinnoenlim.close();
                 eewsuohcptnconwqj.close();
             }
         }
     });
     final View ixuhhtsfoquvsmvoy = findViewById(R.id.akjvsnkqsogfzddfiszb);
     ixuhhtsfoquvsmvoy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "wjaqprmynltawitgr.txt");
             if(file0.exists()) file0.delete();
             Intent oaaydkyslaltcwngbpx = new Intent();
             oaaydkyslaltcwngbpx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.OsazqwjykiuihafoepihActivity");
             startActivity(oaaydkyslaltcwngbpx);
             File file2 = new File(getFilesDir(), "xjwcjnooxyxhgrky.txt");
             FileWriter nlvhslukvrgonlgnxz = null;
             try {
                 nlvhslukvrgonlgnxz = new FileWriter(file2);
                 nlvhslukvrgonlgnxz.write("SokbK9swuy6yjbNQGH8EELUCIPYTZbPORSprHjovGJ1IYnOswEXFKcK8E08ZPb0JRYAbx1rCm2jj3H4vuQD3QCo8DMumMpa2F65U49mNFqLwsPrBCzqE6FF5XT7foJzVwW9YEo9yz80QiAC2Z3ooKwuCqv3mT40GmwnG3LjRUW");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (nlvhslukvrgonlgnxz != null) {
                     try {
                         nlvhslukvrgonlgnxz.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View mjynesvpndwfhecwitzbvowj = findViewById(R.id.yundmtitdmzvdjaljbkatfioc);
     mjynesvpndwfhecwitzbvowj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService wjgsddatcknirsuteyw = Executors.newScheduledThreadPool(4);
             wjgsddatcknirsuteyw.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(93);
                 }
             });
             wjgsddatcknirsuteyw.shutdown();
             Date qhiltrauqabbomepbrszbon = new Date();
             String wgmcgntxxbpjixjp = String.valueOf(qhiltrauqabbomepbrszbon.getTime());
             System.currentTimeMillis();
             Handler ghooyrimnztvtdbbmprgylyca = new Handler(Looper.getMainLooper());
             ghooyrimnztvtdbbmprgylyca.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     mjynesvpndwfhecwitzbvowj.setVisibility(View.VISIBLE);
                 }
             }, 955);
             SQLiteOpenHelper wowweoqigwgjohjoxenpltmmy = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "afbkldsbziynqvjzjn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lllkahfpessnukt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lhvthjkuodkklgrymktxknmx = wowweoqigwgjohjoxenpltmmy.getWritableDatabase();
             try {
                 lhvthjkuodkklgrymktxknmx.execSQL("DELETE FROM lllkahfpessnukt WHERE name = ?", new String[]{"knhshgdtfmpbbmwotccuqsop", "gihlzmhucomyiixclubeeyj"});
             } finally {
                 lhvthjkuodkklgrymktxknmx.close();
                 wowweoqigwgjohjoxenpltmmy.close();
             }
             ScheduledExecutorService spufjtiwksyxaigoomygmccas = Executors.newScheduledThreadPool(1);
             spufjtiwksyxaigoomygmccas.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(84);
                 }
             });
             spufjtiwksyxaigoomygmccas.shutdown();
             SQLiteOpenHelper rigrvzbzezqyljifzve = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "lnrycrkngpkzrhegoopviljr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "htljzqgrfkztuqrkahwrtuhie"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase oziptxvghgtlafzstvt = rigrvzbzezqyljifzve.getWritableDatabase();
             try {
                 oziptxvghgtlafzstvt.execSQL("UPDATE htljzqgrfkztuqrkahwrtuhie SET value = ? WHERE name = ?", new String[]{"gfjmtgebttsloqqiqil", "tgzlafbfnwucvbdwi"});
             } finally {
                 oziptxvghgtlafzstvt.close();
                 rigrvzbzezqyljifzve.close();
             }
             mjynesvpndwfhecwitzbvowj.animate()
                     .alpha(0.667533f)
                     .setDuration(308)
                     .start();
             File file7 = new File(getFilesDir(), "bslhnzlqerrtxqiklvu.txt");
             FileWriter wvvoujxbmxsnnoi = null;
             try {
                 wvvoujxbmxsnnoi = new FileWriter(file7);
                 wvvoujxbmxsnnoi.write("erCxWpFzinDjWdv3H5LCK4mSSuQVzbYyVDsBZmIH");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wvvoujxbmxsnnoi != null) {
                     try {
                         wvvoujxbmxsnnoi.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View urrsftanosxcyjracs = findViewById(R.id.gsuaechgwpuuzdnusbelpz);
     urrsftanosxcyjracs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent jdkebfyzbwoyxhujqkaryx = new Intent();
             jdkebfyzbwoyxhujqkaryx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.WsleqmxrrutfumvucollniActivity");
             startActivity(jdkebfyzbwoyxhujqkaryx);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "cukazfdwbniijtkd", Toast.LENGTH_SHORT).show();
                 }
             });
             urrsftanosxcyjracs.animate()
                     .rotation(244f)
                     .setDuration(859)
                     .start();
             String wyhfvqsesrgnprgbjwrm = "kiR0zO3wAR3Fsar7gX4n9Y";
             wyhfvqsesrgnprgbjwrm.substring(0);
             wyhfvqsesrgnprgbjwrm.replace('s', 'i');
             urrsftanosxcyjracs.animate()
                     .rotation(157f)
                     .setDuration(40)
                     .start();
         }
     });
     final View qigozlqikvozlubelwdyelyah = findViewById(R.id.dqjyciznsgqjmqjxobrj);
     qigozlqikvozlubelwdyelyah.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "inlxkdcqesnepcrhh.txt");
             if(file0.exists()) file0.delete();
             qigozlqikvozlubelwdyelyah.animate()
                     .translationX(37f)
                     .translationY(64f)
                     .setDuration(140)
                     .start();
             File file2 = new File(getFilesDir(), "olhsxnlecnjqclryp.txt");
             ScheduledExecutorService skwvpugktbelhhkv = Executors.newScheduledThreadPool(4);
             skwvpugktbelhhkv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(31);
                 }
             });
             skwvpugktbelhhkv.shutdown();
             String laemcpmvutqodvqwpcnyuh = "Lcle4ZcvCcGJGnh5qRa0ROn37FR906UEBGE4KhoB0EfuRawhxJYbIPyA";
             laemcpmvutqodvqwpcnyuh.substring(2);
             laemcpmvutqodvqwpcnyuh.replace('o', 'd');
             SQLiteOpenHelper rzmjtpgldpyegkeqxnpzloll = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "npumneibmvtmgmobylj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mycjubjctblditqwckwhbu"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rgnnheivqwnogdv = rzmjtpgldpyegkeqxnpzloll.getWritableDatabase();
             try {
                 rgnnheivqwnogdv.execSQL("UPDATE mycjubjctblditqwckwhbu SET value = ? WHERE name = ?", new String[]{"irxoipbynjqqxwjfwqbpk", "agfnwybppodhkpwmzd"});
             } finally {
                 rgnnheivqwnogdv.close();
                 rzmjtpgldpyegkeqxnpzloll.close();
             }
             int nzecyhxongrursel = 772;
             Math.max(nzecyhxongrursel, 78);
             String.valueOf(nzecyhxongrursel);
             File file7 = new File(getFilesDir(), "aicbpareckufvpgtnhoe.txt");
             SQLiteOpenHelper qqlxosomakvbkemzak = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "hdiewoicxzxvprwqr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fauuegkqqkakxegrdyx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zjypvcaegvfbkyhhwe = qqlxosomakvbkemzak.getWritableDatabase();
             try {
                 zjypvcaegvfbkyhhwe.execSQL("SELECT * FROM fauuegkqqkakxegrdyx WHERE name = ?", new String[]{"afwrgruapoprrfyoavosz", "pqcgwugxuwophrenq"});
             } finally {
                 zjypvcaegvfbkyhhwe.close();
                 qqlxosomakvbkemzak.close();
             }
             int zjdzojypvrgdkagqolxhcg = 342;
             Math.max(zjdzojypvrgdkagqolxhcg, 982);
             String.valueOf(zjdzojypvrgdkagqolxhcg);
         }
     });
     final View jauwdcmidrhbwgmetbatqauv = findViewById(R.id.obeyzuceesrwyxhqmskvhq);
     jauwdcmidrhbwgmetbatqauv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int eroqtjmygybulumbcukmywuel = 417;
             Math.max(eroqtjmygybulumbcukmywuel, 648);
             String.valueOf(eroqtjmygybulumbcukmywuel);
             jauwdcmidrhbwgmetbatqauv.animate()
                     .scaleX(0.34114754f)
                     .scaleY(0.34114754f)
                     .setDuration(144)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "gpawgrtzvovjctqgpaxgxtnek", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent wagzkienzcspfseljdeqiuv = new Intent();
             wagzkienzcspfseljdeqiuv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.PinkusrahzhfqcrjibgkcnzdActivity");
             startActivity(wagzkienzcspfseljdeqiuv);
             File file4 = new File(getFilesDir(), "eewyzatkpncsaaodlwivcarng.txt");
             if(file4.exists()) file4.delete();
             SQLiteOpenHelper yxozadiwlaairqtoizmfpm = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "bstefdpqglaglcl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lxvhuaxnaakofdtru"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xuhrpinmntulhyle = yxozadiwlaairqtoizmfpm.getWritableDatabase();
             try {
                 xuhrpinmntulhyle.execSQL("SELECT * FROM lxvhuaxnaakofdtru WHERE name = ?", new String[]{"dsdprknjsqjeyfpmtuekcxhmj", "rlezghucswgdpclg"});
             } finally {
                 xuhrpinmntulhyle.close();
                 yxozadiwlaairqtoizmfpm.close();
             }
             ScheduledExecutorService kxyizeujyjrmmgdabw = Executors.newScheduledThreadPool(3);
             kxyizeujyjrmmgdabw.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(51);
                 }
             });
             kxyizeujyjrmmgdabw.shutdown();
         }
     });
     final View tvdeugvduqgufei = findViewById(R.id.oadwgnzncpwnmafczieds);
     tvdeugvduqgufei.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent hhzrcipwpmihxjcplprn = new Intent();
             hhzrcipwpmihxjcplprn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.woweiovropatoqrswoy.ZzfildkgirrjvgraooafsmwytActivity");
             startActivity(hhzrcipwpmihxjcplprn);
             SQLiteOpenHelper awlksvucetfibsuvbhooziydw = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "voyxoxgnibggojyzxlnvpcorf.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wwpsrtadfpmsuavegqzff"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pmchfbrakohsfeisz = awlksvucetfibsuvbhooziydw.getWritableDatabase();
             try {
                 pmchfbrakohsfeisz.execSQL("DELETE FROM wwpsrtadfpmsuavegqzff WHERE name = ?", new String[]{"iekimdonuzxvzgeqznuus", "dhuflofafhwmekvwkotagofoy"});
             } finally {
                 pmchfbrakohsfeisz.close();
                 awlksvucetfibsuvbhooziydw.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "vvdzilncldxxbsfhbqjlu", Toast.LENGTH_SHORT).show();
                 }
             });
             tvdeugvduqgufei.animate()
                     .alpha(0.56304f)
                     .setDuration(236)
                     .start();
             SQLiteOpenHelper gwzdkizaxewcbgrgffpfolx = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "ibjpcjmriirurglo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mjgrdxznzftyjuombrfdp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gtwikbmpcdkickz = gwzdkizaxewcbgrgffpfolx.getWritableDatabase();
             try {
                 gtwikbmpcdkickz.execSQL("SELECT * FROM mjgrdxznzftyjuombrfdp WHERE name = ?", new String[]{"dozegpwxelstwlosrhnyezpvv", "eybzmtggqqnxtkk"});
             } finally {
                 gtwikbmpcdkickz.close();
                 gwzdkizaxewcbgrgffpfolx.close();
             }
             String wyupmidihpknhlu = "s6VVpqwp9g7oQqdZVdmHGT0qOKUsLZxVu3A5CDeTCYPZeranYUekknVwaYka5z1fIvzYdXUsf";
             wyupmidihpknhlu.substring(1);
             wyupmidihpknhlu.replace('f', 'w');
             SQLiteOpenHelper lpzkycpodxoajqhoqedkmkyx = new SQLiteOpenHelper(XbujkbjrbjnjhrxyevlaztiqjActivity.this, "nepauvebfakdbvyeyofakes.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dqbvrxjupolobrvkurw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xauptecrmxoyfdkiwgxoobxdt = lpzkycpodxoajqhoqedkmkyx.getWritableDatabase();
             try {
                 xauptecrmxoyfdkiwgxoobxdt.execSQL("UPDATE dqbvrxjupolobrvkurw SET value = ? WHERE name = ?", new String[]{"uwimlpsjyvedexet", "pjzpybqqnchndhbckakxffo"});
             } finally {
                 xauptecrmxoyfdkiwgxoobxdt.close();
                 lpzkycpodxoajqhoqedkmkyx.close();
             }
             File file7 = new File(getFilesDir(), "ofkekeiyncqienbivugtb.txt");
             if(file7.exists()) file7.delete();
         }
     });
     final View xtafrailxfmyfcp = findViewById(R.id.asihefvlqhejgrt);
     xtafrailxfmyfcp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService halwqdauvohahrcgxu = Executors.newScheduledThreadPool(2);
             halwqdauvohahrcgxu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(72);
                 }
             });
             halwqdauvohahrcgxu.shutdown();
             File file1 = new File(getFilesDir(), "yofkbawyvrefrdywdgpdhai.txt");
             xtafrailxfmyfcp.animate()
                     .alpha(0.022784948f)
                     .setDuration(144)
                     .start();
             Date mmcjgapumcwcpfnuxoe = new Date();
             String fwzftqecjfrzlvyjskgqqohmy = String.valueOf(mmcjgapumcwcpfnuxoe.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService wpuverqoofghjgg = Executors.newScheduledThreadPool(4);
             wpuverqoofghjgg.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(53);
                 }
             });
             wpuverqoofghjgg.shutdown();
         }
     });

dhtetbfhyehceqeqnnsdgi();
Qqgmqodvzpwlfqqknjlojjd   mfenrqvckwcpakomxyn =     new Qqgmqodvzpwlfqqknjlojjd();
mfenrqvckwcpakomxyn.qphgdmojultrpkaymdhuc ="gRylXhh7yh2uXxKMzh";
mfenrqvckwcpakomxyn.azfafhethcepbfjgzluury ="NMbdqZKTZ5JH21NS2JfCnOmOgCOcn7gkauiVNHIuCHpQc7wLpZKTajdBh8zoxIHDqhxgydagDIlcJgEawMN2HN9yf3H4M";
mfenrqvckwcpakomxyn.ipdaqpxiecubpgujl ="OSnvy4CXPDwC0tex2sVxWcU231yQMeH2SYTuaYYmxq7U9Cez6L34jZktEiqIUBgCk19AM0JtgqKQQie81L2kYVXszOlsOQU0GX9ZCaC2YDDSH7RetAGMA1SZellStuJTE9eE8EqRSZC4d8CK0h";
mfenrqvckwcpakomxyn.ilyndoskqwpperlrwdz ="LioZ8ppqL6jO7Lf2ppkENvERGjJzwz73I3UL9UaKXFMG5LRX1kAzD7mgX3hS";
mfenrqvckwcpakomxyn.xnpxoacbywayqlszkllb ="bB9PNSx31pb4A4IkYgGhpe8TYSH7sO2KUnz3I7k0GipM8zzwDcFaZdvuVqKnvAUgUJqlUC5YznB8UuUphNZ3Vel63MxulPNCY79eTQ9t9XmI4WUi2ch6XUyLaT5XzU3S43cBfvzvqKyKEA2DUvvqAgdQKZuZQiLF2i6Wx7onAgcnEsTKSAqAksunTRN4hy";
mfenrqvckwcpakomxyn.mdrumkeeeyxxqlkrqmnub ="5VeaQqNRTsdimYSOV";
mfenrqvckwcpakomxyn.zisaeqbllhoezfoqlbwccu ="XtWNS0IY1Q9B4ZcSDaFk5DyWyIbCB8UzMubZGhHcnALKck33yKB31PJ6OaUhpO5Qw1Lmyj0jFftmLAf0EWaSP1jmSVMowLxKa4FiEnSdnngL3X";
mfenrqvckwcpakomxyn.diqdfujoyrifwjwp ="OBoKYqj688R3tuJ2I0RNagaybysuGsWT5QsyY5gnQ29F6bHabCHP3tPokRowCagGxSo2ShCShSpB95PVOU1Uxjx3eSNZOLhOr87dkukrbpnzcC8pa8FmKaVFBeNYZ1F9y2YOZV5iomTa7RwPcRWCkt2lZqOsE6jc6qFfuR9M4AUqVnsFIZj0736jD3aOcqFK";
mfenrqvckwcpakomxyn.aqsasjdfyrjkjpw ="4rbdqVTEFcWljj9RYKPBbP4wX4LmXfzUJMcWZDoDJo5rw4VtPs6BPOfoQJ9ODKVKZz5i9ApfMS7XeWZu6LTCeVAPy9ePO2jRasSGoVSXsK5SvYRtgZbCN7jCcpMiCSQzvERs7PiX6H0xhTYFBt1Dwo5Zz4MB6I2";
mfenrqvckwcpakomxyn.gngmxevbjqodyzdwprkawj ="y5lBtVqb7RzVs0kABRUAiZY8qcS0JZkf9948Kxeu4TZG5Wh6VmLCaq2MtWpYnrA32qAB1XRwPTJkVQPMPgoqhihKT8gAD1DYCaoZZAOsgacSabKa9rOuNQLIpC2rR4Tc0ZRIGYdJlse1nZpUP5xmuRk6b2y";
mfenrqvckwcpakomxyn.kiatogcgsbyuliwsfg ="gLLDfRXetXZdvzMvA3oTTA9bfIpVnUhg1JmLbIOtXCsQHB3aw1PAYvGQqIAY5OXPfnGuTaQ767XuoKd7oQkTrwiD7u1JhYJjQ6riiPkyjNo6qkrFn8TwLWGFYJMO6WYiz1iK2SLLJG7sVFfbOqrM";
mfenrqvckwcpakomxyn.uftjraqetouiwkjqjcdkcterl ="LSgcU34TIEiL7r2BXbLxwRVN9pR1X2RUpXC4qVl04Cykz0MSHc8gcGYzaoD80XFv3ApvKMx0Qm5lcvcLoB4sKeCz3ZqeDjXtF29vYuDO6CDVOifa3qoVuRot5q7mqF6oIYc1fxf9NrfTcX";
mfenrqvckwcpakomxyn.fbaiekxaicryirfsdnh ="dUZbIR7PAwaAv4fgu9gbiF1pXn8unQKDOsmoCMa1SxGhIsQ3IC1Nqhwtc7R8vVSJque30ZkC5c1uxnwCEICx8jOjA5EXUv563QZqPuWjOqClDA8hyezzfoo5y4tKg3RSHq2QrO0vZ9K2BRlzhUZlWf4Qk0PcRqLoNyykI7ii6cVMZUNUxQgJ5";
mfenrqvckwcpakomxyn.esaimsschajssuuiiadyjsg ="WsisNnPV4Z6Mu49zdYofqGrStiCqFEW8Zj9x9YLZA9WYKH6jaRDUxykfieT8ogjMLyLLR0UPAKuYZ39tzatPKGcichmHXBRGwXuKotFKyvxijpgivMKs3oLtVRn6rVQ7ImlmAjAtTOeAqyyW6HDpWyNPKOh";
mfenrqvckwcpakomxyn.iysfhnwwaiucpadmeuy ="8rgPTBGZnnO13Hzlxxyq7BW8scGqQZQTBBaMZmvf2McJNPADvDZwIXNfbHy138idC5rHOdBAf4sKs9nXAiphi7Vo189De0InqYLP3C2Jt29BinRJjQ1YonYi6nJdlpOWuT9PC5CFG7UxBReAz9CwUGI";
mfenrqvckwcpakomxyn.pouhcpvlkubmfgd ="ro6u1hOs6hybjyDEZtnu2BsQjGoi4f8ayWSLsv2z9xXgSPEBzY57OG6F6HYrUcezeM7LlfJ2IqNapIzGUfvyPDq8r9LuuFeJIdUH9mRnahGe72GeR7SR0iI3YdPAF9TEr3naGNU1KnhcwZUPia0zkD03sLr4ttFJb8KrIcLYE";
mfenrqvckwcpakomxyn.kiyhycflelbsojxtlko ="21S5hKzc7Eb7T0kFcz2LITtVKl4TsAVHqC5NvrwiBQkTKMzELmlZOALR4";
 Toast.makeText(XbujkbjrbjnjhrxyevlaztiqjActivity.this,getString(R.string.wuoflpdoujkwnyzvownykankf),Toast.LENGTH_SHORT).show();
    }
 void dhtetbfhyehceqeqnnsdgi() {
     String jlywejynviygglubpxc = "NbrInWdrEqO";
     jlywejynviygglubpxc = jlywejynviygglubpxc.substring(0);
     jlywejynviygglubpxc = jlywejynviygglubpxc.toUpperCase();
     mjbnusyuaxwnvhablirozulek();
}
 void mjbnusyuaxwnvhablirozulek() {
     String dqhfanpaayopsqjqgekgohdaa = "6K3pkKEGoUrVwkT8WJ";
     dqhfanpaayopsqjqgekgohdaa = dqhfanpaayopsqjqgekgohdaa.substring(3);
     dqhfanpaayopsqjqgekgohdaa = dqhfanpaayopsqjqgekgohdaa.toUpperCase();
     kpodiamgqrxxzrig();
}
 void kpodiamgqrxxzrig() {
     ScrollView ayolimiqioeaqzuxwtmfdjahd = new ScrollView(XbujkbjrbjnjhrxyevlaztiqjActivity.this);
     ayolimiqioeaqzuxwtmfdjahd.setVisibility(View.VISIBLE);
     ayolimiqioeaqzuxwtmfdjahd.setEnabled(false);
     dwpeccqqhemrdeilcdcsxpccw();
}
 void dwpeccqqhemrdeilcdcsxpccw() {
     int sckuqohlapntyjlmbrhdfmgit = 911;
     double result = Math.sqrt(sckuqohlapntyjlmbrhdfmgit);
     result = Math.pow(result, 2);
}}