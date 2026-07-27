package com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz;

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
import android.widget.FrameLayout;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class VmyzbpyssuxtsjapyhActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myzbpyssuxtsjapyh);
     final View fhvvtetccsnxngwgeokrgk = findViewById(R.id.hkfluzhmynzdfcvn);
     fhvvtetccsnxngwgeokrgk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper esqnvkfwvwtnfjei = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "nkzxdoimpdobofzt.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ukbwiekghfdqfxmcvpxjiogm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ftsjtmtxgpmsilrotkgfobtzl = esqnvkfwvwtnfjei.getWritableDatabase();
             try {
                 ftsjtmtxgpmsilrotkgfobtzl.execSQL("UPDATE ukbwiekghfdqfxmcvpxjiogm SET value = ? WHERE name = ?", new String[]{"krcmycepufezwovxdmjbmr", "bhpmddpcmmeumyqzsr"});
             } finally {
                 ftsjtmtxgpmsilrotkgfobtzl.close();
                 esqnvkfwvwtnfjei.close();
             }
             Intent ntjbbozczvwprgamwr = new Intent();
             ntjbbozczvwprgamwr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.PgsvybvsokjwapmaxulxoeywpaActivity");
             startActivity(ntjbbozczvwprgamwr);
         }
     });
     final View iplmwogzrmcjhzj = findViewById(R.id.vufoaomcqfwmcrcmt);
     iplmwogzrmcjhzj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent vkxgpanfgxjyfuaduhvjxcx = new Intent();
             vkxgpanfgxjyfuaduhvjxcx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.GbzibvupdzjnqrvuvxActivity");
             startActivity(vkxgpanfgxjyfuaduhvjxcx);
             File file1 = new File(getFilesDir(), "cbvzndzfqmghqkbkkdnez.txt");
             FileReader zufxeiyabowcnxpivgpshxui = null;
             try {
                 if(file1.exists()) {
                     zufxeiyabowcnxpivgpshxui = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zufxeiyabowcnxpivgpshxui != null) {
                     try {
                         zufxeiyabowcnxpivgpshxui.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View aiqmqnbxqhvcznbembv = findViewById(R.id.wphheizrjabojld);
     aiqmqnbxqhvcznbembv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent jxjrhgeehcbnkuvzxlqumhpik = new Intent();
             jxjrhgeehcbnkuvzxlqumhpik.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.AilhigabsuteuuthyvvhrvrActivity");
             startActivity(jxjrhgeehcbnkuvzxlqumhpik);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(VmyzbpyssuxtsjapyhActivity.this, "laepvrjkttdljqli", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View mvdifedsofxltkpi = findViewById(R.id.jdrmfusziwwhuhtcfydwto);
     mvdifedsofxltkpi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date njvbmievgbeyhrpkq = new Date();
             String ggogyqqltwhypdeuuxbjmya = String.valueOf(njvbmievgbeyhrpkq.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService yxtwhdldlhzglbkhpzn = Executors.newScheduledThreadPool(2);
             yxtwhdldlhzglbkhpzn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(82);
                 }
             });
             yxtwhdldlhzglbkhpzn.shutdown();
         }
     });
     final View xpvdusqfzubekjhaw = findViewById(R.id.qzyqbmsowemxpnbghcnf);
     xpvdusqfzubekjhaw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date kclsjharzvqafulwajwfzvqbo = new Date();
             String fihxzzjzoeggciiduwbyudthd = String.valueOf(kclsjharzvqafulwajwfzvqbo.getTime());
             System.currentTimeMillis();
             xpvdusqfzubekjhaw.animate()
                     .scaleX(0.36582208f)
                     .scaleY(0.36582208f)
                     .setDuration(480)
                     .start();
             File file2 = new File(getFilesDir(), "nbwfzbivgzoivdbjk.txt");
             FileWriter iwbepfnsvfebfwszsqbpodwm = null;
             try {
                 iwbepfnsvfebfwszsqbpodwm = new FileWriter(file2);
                 iwbepfnsvfebfwszsqbpodwm.write("quWFIOcpDvwTo1ojf3LzWJAHF4LQkGnlb5MIE0SxgnZSzbt8ISsCj5MHvDQub92U8NBo8SL05AkgKeMgOAg28aY0FIy85s4KGGHgyv8MgwqPdctb78xaq73exdr0yxAZ6tEB3ZocqyVIGQrPA5J3d7d4HhnNonU4hmaRXVS4oO8FNsJUGGU");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (iwbepfnsvfebfwszsqbpodwm != null) {
                     try {
                         iwbepfnsvfebfwszsqbpodwm.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             xpvdusqfzubekjhaw.animate()
                     .scaleX(1.3690959f)
                     .scaleY(1.3690959f)
                     .setDuration(373)
                     .start();
             File file4 = new File(getFilesDir(), "rfesjtolelmevhhxf.txt");
             FileWriter btvroaxwihqneoxtojiexlis = null;
             try {
                 btvroaxwihqneoxtojiexlis = new FileWriter(file4);
                 btvroaxwihqneoxtojiexlis.write("qJSKD3YvmUAPYMHy8zxDX9kJZ6ssG7S1kFnPTLYdzlqJjtJwbzgHAjpBxjKt5U7Q8Q5rSuQhsTarAyq0tEwxV8HUHo98FnRelniZ1wSthLqRbLwCCNp0dQn8pZb9hEWXd9tvjGNHE");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (btvroaxwihqneoxtojiexlis != null) {
                     try {
                         btvroaxwihqneoxtojiexlis.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date fpxlvfgloknzxudigeh = new Date();
             String yqsermneqispwaafqfgufpvi = String.valueOf(fpxlvfgloknzxudigeh.getTime());
             System.currentTimeMillis();
             xpvdusqfzubekjhaw.animate()
                     .alpha(0.15170813f)
                     .setDuration(8)
                     .start();
             File file7 = new File(getFilesDir(), "okgdwpsoiummdcfzsvlqbqili.txt");
             if(file7.exists()) file7.delete();
             Date rlfhyprcgxydrinyhej = new Date();
             String bscmigylfwwqbdqomthyqdt = String.valueOf(rlfhyprcgxydrinyhej.getTime());
             System.currentTimeMillis();
             xpvdusqfzubekjhaw.animate()
                     .rotation(31f)
                     .setDuration(736)
                     .start();
         }
     });
     final View yqppnfkkczlmkzroxg = findViewById(R.id.tbbhnxwtmcdsdrbunukujadi);
     yqppnfkkczlmkzroxg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             yqppnfkkczlmkzroxg.animate()
                     .rotation(28f)
                     .setDuration(482)
                     .start();
             Intent gfareeyymnaqhkqtlgpjslmem = new Intent();
             gfareeyymnaqhkqtlgpjslmem.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.UvoqvedfqtsgehpgvkrActivity");
             startActivity(gfareeyymnaqhkqtlgpjslmem);
             yqppnfkkczlmkzroxg.animate()
                     .alpha(0.31622165f)
                     .setDuration(737)
                     .start();
             SQLiteOpenHelper sezrgwgpdeyllocipmifxen = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "yplbfwwolenkrkgdqrsinj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xcllyeqstifwsfzocodmzzxde"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jchovfaowfzlyceauytqbvqah = sezrgwgpdeyllocipmifxen.getWritableDatabase();
             try {
                 jchovfaowfzlyceauytqbvqah.execSQL("UPDATE xcllyeqstifwsfzocodmzzxde SET value = ? WHERE name = ?", new String[]{"auyhrmhhxsmnezjgcrpxabi", "ayifjcowtkdxmuxdsxrjqofz"});
             } finally {
                 jchovfaowfzlyceauytqbvqah.close();
                 sezrgwgpdeyllocipmifxen.close();
             }
             yqppnfkkczlmkzroxg.animate()
                     .translationX(139f)
                     .translationY(33f)
                     .setDuration(492)
                     .start();
             ScheduledExecutorService elfuiqmxrklinpdzquoprtpfe = Executors.newScheduledThreadPool(1);
             elfuiqmxrklinpdzquoprtpfe.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(21);
                 }
             });
             elfuiqmxrklinpdzquoprtpfe.shutdown();
             int gzjtfeiykamhosdzu = 444;
             Math.max(gzjtfeiykamhosdzu, 733);
             String.valueOf(gzjtfeiykamhosdzu);
         }
     });
     final View npxbjnmdfdageduxat = findViewById(R.id.ztncylthqpsxgerkhlx);
     npxbjnmdfdageduxat.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(VmyzbpyssuxtsjapyhActivity.this, "bslnuzedylzcvuucuxy", Toast.LENGTH_SHORT).show();
                 }
             });
             Date xrpdgfeqzggnwjw = new Date();
             String bheqyaijmqbzhmgoq = String.valueOf(xrpdgfeqzggnwjw.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper yijrgaluefdaeznpks = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "scohpaacjvdrsebezovg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fvrsdqusyukzsqomfkw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ttltvdchwocnccvgagukmod = yijrgaluefdaeznpks.getWritableDatabase();
             try {
                 ttltvdchwocnccvgagukmod.execSQL("UPDATE fvrsdqusyukzsqomfkw SET value = ? WHERE name = ?", new String[]{"ydtwmwnigknpxkyrlszcu", "vbgikzxzymdaqrclysy"});
             } finally {
                 ttltvdchwocnccvgagukmod.close();
                 yijrgaluefdaeznpks.close();
             }
             ScheduledExecutorService zcnbzyvnbucktpzqgwxe = Executors.newScheduledThreadPool(3);
             zcnbzyvnbucktpzqgwxe.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(36);
                 }
             });
             zcnbzyvnbucktpzqgwxe.shutdown();
             SQLiteOpenHelper itohpnmbnwylycepmpgbb = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "gboqyixkcrgmngesy.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "sdvyjdtmxzrlhnp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase oianyyjxuhljtprfel = itohpnmbnwylycepmpgbb.getWritableDatabase();
             try {
                 oianyyjxuhljtprfel.execSQL("UPDATE sdvyjdtmxzrlhnp SET value = ? WHERE name = ?", new String[]{"evckdivwpjtfizpeyqldlhbm", "claxfwdpmsxhtfsj"});
             } finally {
                 oianyyjxuhljtprfel.close();
                 itohpnmbnwylycepmpgbb.close();
             }
             int lacuumtytppzvyneufa = 872;
             Math.max(lacuumtytppzvyneufa, 657);
             String.valueOf(lacuumtytppzvyneufa);
         }
     });
     final View bebvvfuijvlzkmksbrnk = findViewById(R.id.rugggqsgthuhnfdobpefs);
     bebvvfuijvlzkmksbrnk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int mrhpnvdmzmsjnxnttyvdhkudu = 333;
             Math.max(mrhpnvdmzmsjnxnttyvdhkudu, 870);
             String.valueOf(mrhpnvdmzmsjnxnttyvdhkudu);
             ScheduledExecutorService boehdokexkpbygugy = Executors.newScheduledThreadPool(1);
             boehdokexkpbygugy.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(65);
                 }
             });
             boehdokexkpbygugy.shutdown();
             bebvvfuijvlzkmksbrnk.animate()
                     .translationX(12f)
                     .translationY(35f)
                     .setDuration(739)
                     .start();
         }
     });
     final View rovfybpqppwyarlcjoelpi = findViewById(R.id.ouulpxmjeifjxtjhgcojegs);
     rovfybpqppwyarlcjoelpi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper bjaozqphadzlyjv = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "zblbaedshklywbhf.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "niwzeutxrvjpokbfoeywrdyi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zvnqtgvkwjkfygogskuibezcw = bjaozqphadzlyjv.getWritableDatabase();
             try {
                 zvnqtgvkwjkfygogskuibezcw.execSQL("INSERT INTO niwzeutxrvjpokbfoeywrdyi (name, value) VALUES (?, ?)", new String[]{"zhdwdzitmnnsrwwmvf", "rtngcesoiykqwrrao"});
             } finally {
                 zvnqtgvkwjkfygogskuibezcw.close();
                 bjaozqphadzlyjv.close();
             }
             Date jujkxruqyccxvdgfb = new Date();
             String nxcvyoronnazxbwdgifuy = String.valueOf(jujkxruqyccxvdgfb.getTime());
             System.currentTimeMillis();
             File file2 = new File(getFilesDir(), "zdphvwsclertspo.txt");
             if(file2.exists()) file2.delete();
             SQLiteOpenHelper durpwsszoqjckxibxammw = new SQLiteOpenHelper(VmyzbpyssuxtsjapyhActivity.this, "wivqbjuaegshltntzpbuia.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "sajuffgmbxuxlkgjwqojlg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nubvjjwwlsynloparnpeeh = durpwsszoqjckxibxammw.getWritableDatabase();
             try {
                 nubvjjwwlsynloparnpeeh.execSQL("INSERT INTO sajuffgmbxuxlkgjwqojlg (name, value) VALUES (?, ?)", new String[]{"bwgobvckyuvamiaoz", "sxrmcfwjcxvgbqhmshnaosd"});
             } finally {
                 nubvjjwwlsynloparnpeeh.close();
                 durpwsszoqjckxibxammw.close();
             }
             Intent xxwzjvjxorlklzbqnwfoyvtte = new Intent();
             xxwzjvjxorlklzbqnwfoyvtte.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.DengvhzeajoxwkenvltwrtqplActivity");
             startActivity(xxwzjvjxorlklzbqnwfoyvtte);
             Date kepmwpdvvskhhcdvdo = new Date();
             String npszpivxgizgzxefek = String.valueOf(kepmwpdvvskhhcdvdo.getTime());
             System.currentTimeMillis();
             Intent rmnwzjbwyhnthngotsl = new Intent();
             rmnwzjbwyhnthngotsl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.DengvhzeajoxwkenvltwrtqplActivity");
             startActivity(rmnwzjbwyhnthngotsl);
             ScheduledExecutorService nlynvikomgpbkhdeyicay = Executors.newScheduledThreadPool(4);
             nlynvikomgpbkhdeyicay.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(2);
                 }
             });
             nlynvikomgpbkhdeyicay.shutdown();
         }
     });
     final View dubuuwgotmauglbinsizlj = findViewById(R.id.nrskxguhgfnabjbzakg);
     dubuuwgotmauglbinsizlj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date gedjbsjwidfymthtf = new Date();
             String jyftehckjotniisazgnpsxj = String.valueOf(gedjbsjwidfymthtf.getTime());
             System.currentTimeMillis();
             Intent sqokncihtaammogxbhkly = new Intent();
             sqokncihtaammogxbhkly.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.DyulbmzwwlqfhiinelActivity");
             startActivity(sqokncihtaammogxbhkly);
             File file2 = new File(getFilesDir(), "vjsdemkdbdcitkjfktkmio.txt");
             FileReader rieyhwaevzdxgptdknyt = null;
             try {
                 if(file2.exists()) {
                     rieyhwaevzdxgptdknyt = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (rieyhwaevzdxgptdknyt != null) {
                     try {
                         rieyhwaevzdxgptdknyt.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int oisywfkehyvivqmw = 905;
             Math.max(oisywfkehyvivqmw, 318);
             String.valueOf(oisywfkehyvivqmw);
             Intent kpmdydizfrxwgii = new Intent();
             kpmdydizfrxwgii.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.DyulbmzwwlqfhiinelActivity");
             startActivity(kpmdydizfrxwgii);
             int wmcomtznhbrnqzwvxxtmfaycb = 728;
             Math.max(wmcomtznhbrnqzwvxxtmfaycb, 150);
             String.valueOf(wmcomtznhbrnqzwvxxtmfaycb);
             ScheduledExecutorService glsfofulkomuzikrrxfm = Executors.newScheduledThreadPool(2);
             glsfofulkomuzikrrxfm.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(37);
                 }
             });
             glsfofulkomuzikrrxfm.shutdown();
         }
     });

dghzsbxfqbzuojoiiao();
Zbdtdvuzpaaelhfxrfqw   bagolmyunwitpea =     new Zbdtdvuzpaaelhfxrfqw();
bagolmyunwitpea.btfjfaxegnymfikmmvujg ="l6T60OgqOCFi8rz6Co9nIyHVyxcaZm9mtPdBnR8ul4OojGoyRR046LKx9ebaaBWoYlDB2x8vIv9JSDZCDXYRSzjMxarKOJ";
bagolmyunwitpea.dnzlefgrxdssucdrzvv ="JMaDpxXl1uGmgvfsgOhvuMCAyFIrcLDQqyPHQedF37K3z";
bagolmyunwitpea.jrcndubopezshargddmx ="dap9erpJBl6iZaVLf";
bagolmyunwitpea.qjtqhvfyoljvjwqafmliqnwgh ="tJy7cyXOhsTmOb3gxJxyFt6WrDkOjLR5Mlq01PMPUwg6B1ayXXB4PkOUtoyIGF90EBnQh16ejVhpbjDvQoRotjlynBAM2wIkMAI8h";
 Toast.makeText(VmyzbpyssuxtsjapyhActivity.this,getString(R.string.uqtdypzsfducqzkz),Toast.LENGTH_SHORT).show();
    }
 void dghzsbxfqbzuojoiiao() {
     int dmpcdsdwihfzmcmbfyxy = 873;
     double result = Math.sqrt(dmpcdsdwihfzmcmbfyxy);
     result = Math.pow(result, 2);
}}