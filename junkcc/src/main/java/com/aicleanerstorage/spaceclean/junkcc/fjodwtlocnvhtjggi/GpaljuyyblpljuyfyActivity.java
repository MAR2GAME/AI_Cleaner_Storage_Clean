package com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi;

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
import android.widget.TextView;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class GpaljuyyblpljuyfyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_paljuyyblpljuyfy);
     final View slymfcrpykchuwu = findViewById(R.id.gepwqmmwzcjetzfi);
     slymfcrpykchuwu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper qzzexvhuienxsflpytr = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "hkmqjhqliccmubtpjlo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "owflcgpbkucmdozjr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pqsbzjstrnouvmfgqrevmn = qzzexvhuienxsflpytr.getWritableDatabase();
             try {
                 pqsbzjstrnouvmfgqrevmn.execSQL("UPDATE owflcgpbkucmdozjr SET value = ? WHERE name = ?", new String[]{"nfhtanbhagcbvdjtfkai", "ysdfbytcirdancnjizf"});
             } finally {
                 pqsbzjstrnouvmfgqrevmn.close();
                 qzzexvhuienxsflpytr.close();
             }
             slymfcrpykchuwu.animate()
                     .alpha(0.2710889f)
                     .setDuration(945)
                     .start();
             String kdveacayxggdqjxmqiswcil = "PxACBzgTYiuegeOLSmREKzv3CGt2LXhZ16qIedk3dC8dsJkbkb7Mjra8Prqfkmen52k2F04fH2tXvEJkohbI8NIKGnRHr69k4dvL0rKcVmU6eSXWmNzYE2A5nI6gzdGVm1QKQ5dj4M7vfYgTc";
             kdveacayxggdqjxmqiswcil.substring(1);
             kdveacayxggdqjxmqiswcil.replace('e', 'p');
             File file3 = new File(getFilesDir(), "axqsbrjstuhgepihgmna.txt");
             SQLiteOpenHelper scnauminyttfudvneay = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "ivyqhklviubcouzajkaocacq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pshnfqvsbyhjwqnfqeiephwb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jsdrlsijrcrhyaickkvzjb = scnauminyttfudvneay.getWritableDatabase();
             try {
                 jsdrlsijrcrhyaickkvzjb.execSQL("SELECT * FROM pshnfqvsbyhjwqnfqeiephwb WHERE name = ?", new String[]{"fpgrpquefpvovjj", "rsxhehsgdgfdltmgwul"});
             } finally {
                 jsdrlsijrcrhyaickkvzjb.close();
                 scnauminyttfudvneay.close();
             }
         }
     });
     final View cwlsrhkrjqfhgybftnqp = findViewById(R.id.raycsffeilpwpwgv);
     cwlsrhkrjqfhgybftnqp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper lmiqikppoznzqpnobt = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "ayidrdrkyrtkgzlommwhnokr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vmeuzgsgyunwtqfffasfzisa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kwibpqbqzaurijffm = lmiqikppoznzqpnobt.getWritableDatabase();
             try {
                 kwibpqbqzaurijffm.execSQL("SELECT * FROM vmeuzgsgyunwtqfffasfzisa WHERE name = ?", new String[]{"rsoluzijyhbuuesgf", "xwzvcndmkqvyqlajffounq"});
             } finally {
                 kwibpqbqzaurijffm.close();
                 lmiqikppoznzqpnobt.close();
             }
             File file1 = new File(getFilesDir(), "czzammtqseqnucyiyzky.txt");
             FileReader wyhiiivjbhimqnwieey = null;
             try {
                 if(file1.exists()) {
                     wyhiiivjbhimqnwieey = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wyhiiivjbhimqnwieey != null) {
                     try {
                         wyhiiivjbhimqnwieey.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View mpqbiyhynzerncumolq = findViewById(R.id.mafjzefmgkttyqm);
     mpqbiyhynzerncumolq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService wkemfwjiehykwvnfyouco = Executors.newScheduledThreadPool(2);
             wkemfwjiehykwvnfyouco.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(57);
                 }
             });
             wkemfwjiehykwvnfyouco.shutdown();
             Intent uoawbpemvunhpkqhevkdnkq = new Intent();
             uoawbpemvunhpkqhevkdnkq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.YngynduvlpaaygmwqyktkActivity");
             startActivity(uoawbpemvunhpkqhevkdnkq);
             String bzslfkmjbdtscwjtmkhz = "94lyai9ZCRCchDCaJrXrwMVeMZYSKxv50AxL7pzBj0mf07UmfOWQ9eB64rpxzHaRVsCOwJlRBscsDXK9BEs4s3JWMHAwoLYV3pYt";
             bzslfkmjbdtscwjtmkhz.substring(3);
             bzslfkmjbdtscwjtmkhz.replace('g', 'g');
             Handler hmcjjxcjvivywfzkltoegvay = new Handler(Looper.getMainLooper());
             hmcjjxcjvivywfzkltoegvay.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     mpqbiyhynzerncumolq.setVisibility(View.VISIBLE);
                 }
             }, 702);
             Intent pjqrfpghmqoguzo = new Intent();
             pjqrfpghmqoguzo.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.YngynduvlpaaygmwqyktkActivity");
             startActivity(pjqrfpghmqoguzo);
             File file5 = new File(getFilesDir(), "zmiowvufbgkrqvwnuwutcqd.txt");
             FileReader ogrveucfheuzsakjxutujsiix = null;
             try {
                 if(file5.exists()) {
                     ogrveucfheuzsakjxutujsiix = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ogrveucfheuzsakjxutujsiix != null) {
                     try {
                         ogrveucfheuzsakjxutujsiix.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             String uouhrhjdachrwrfwaosqxfoc = "HPaV07TyKcP0JhzmWwugFwbsCzFaiXSO2hbWyAkJWB9fVwa0iWGUUesTVRCIowrdy2ELeKxQMjIbGZC";
             uouhrhjdachrwrfwaosqxfoc.substring(2);
             uouhrhjdachrwrfwaosqxfoc.replace('y', 'z');
             Intent egctyayzdmczqntnuwebsiy = new Intent();
             egctyayzdmczqntnuwebsiy.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.YngynduvlpaaygmwqyktkActivity");
             startActivity(egctyayzdmczqntnuwebsiy);
             File file8 = new File(getFilesDir(), "odmdipfndmuqimjnjhq.txt");
             if(file8.exists()) file8.delete();
             SQLiteOpenHelper ixjdqvggzyzfabil = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "xhacxkuuprwcjgmraeskcur.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bddnjlawlcldrvpusws"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jpwztbsqurltfgzkopjh = ixjdqvggzyzfabil.getWritableDatabase();
             try {
                 jpwztbsqurltfgzkopjh.execSQL("INSERT INTO bddnjlawlcldrvpusws (name, value) VALUES (?, ?)", new String[]{"jzqckapxhjzyxzjg", "mlqdhxodpmirurm"});
             } finally {
                 jpwztbsqurltfgzkopjh.close();
                 ixjdqvggzyzfabil.close();
             }
         }
     });
     final View xniiihhmjsxjtmugecgrse = findViewById(R.id.dnbkesoklpbfatweyn);
     xniiihhmjsxjtmugecgrse.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler ymirtrgzcfsefhsbhhfajherp = new Handler(Looper.getMainLooper());
             ymirtrgzcfsefhsbhhfajherp.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     xniiihhmjsxjtmugecgrse.setVisibility(View.VISIBLE);
                 }
             }, 952);
             String lohltdglxwmzenlai = "fGhVIMiTDFVOiMbSUKRql4";
             lohltdglxwmzenlai.substring(0);
             lohltdglxwmzenlai.replace('e', 'k');
             File file2 = new File(getFilesDir(), "vjqkunnbnymthmryxj.txt");
             FileReader imhhtpjaiucgwjyarfhkio = null;
             try {
                 if(file2.exists()) {
                     imhhtpjaiucgwjyarfhkio = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (imhhtpjaiucgwjyarfhkio != null) {
                     try {
                         imhhtpjaiucgwjyarfhkio.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View fvzplxwfdansxwzuavefsw = findViewById(R.id.wprrgyipditfynzrtvqeyl);
     fvzplxwfdansxwzuavefsw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper wabyjecerurtnxc = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "hjoyjqttdlqkzwx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gaxmbdjzwaotyhpoqsvopsmtp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xqrvokzhayiacfdaabmgv = wabyjecerurtnxc.getWritableDatabase();
             try {
                 xqrvokzhayiacfdaabmgv.execSQL("UPDATE gaxmbdjzwaotyhpoqsvopsmtp SET value = ? WHERE name = ?", new String[]{"doxhbqgnyjghhlkktoqfjobhh", "tgeucrrhvwpneilwotdtlg"});
             } finally {
                 xqrvokzhayiacfdaabmgv.close();
                 wabyjecerurtnxc.close();
             }
             Date ymxlphgrfgiynaodxnwdhlbe = new Date();
             String ypsuevhrxhdvvexuuuuafzjk = String.valueOf(ymxlphgrfgiynaodxnwdhlbe.getTime());
             System.currentTimeMillis();
             File file2 = new File(getFilesDir(), "qlfqyvpduinbyhe.txt");
         }
     });
     final View ikdqvfhywnqipvrri = findViewById(R.id.xpvxhjccthvyfmci);
     ikdqvfhywnqipvrri.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "ixvrddompvytbcrxsygz.txt");
             if(file0.exists()) file0.delete();
             int ljzfubcndxxqrfvsobb = 262;
             Math.max(ljzfubcndxxqrfvsobb, 922);
             String.valueOf(ljzfubcndxxqrfvsobb);
             ikdqvfhywnqipvrri.animate()
                     .alpha(0.13930476f)
                     .setDuration(311)
                     .start();
             Date zkyzftlsttqhivdvjkf = new Date();
             String nkpppepjhumlfefatsj = String.valueOf(zkyzftlsttqhivdvjkf.getTime());
             System.currentTimeMillis();
             ikdqvfhywnqipvrri.animate()
                     .alpha(0.33107716f)
                     .setDuration(146)
                     .start();
             SQLiteOpenHelper pnzjbrecgoqloukdmjptot = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "tgzpldzyqcrnrakemvw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gihppqscdzecofehaq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qvlrdnyuugioxeyhaylrjl = pnzjbrecgoqloukdmjptot.getWritableDatabase();
             try {
                 qvlrdnyuugioxeyhaylrjl.execSQL("INSERT INTO gihppqscdzecofehaq (name, value) VALUES (?, ?)", new String[]{"zlzrrbfbrpqyziedrqez", "lqdkmksmvmqtumlnnebn"});
             } finally {
                 qvlrdnyuugioxeyhaylrjl.close();
                 pnzjbrecgoqloukdmjptot.close();
             }
             File file6 = new File(getFilesDir(), "woumvxjxdwnknprccky.txt");
             FileWriter exdwrnoehejaihzd = null;
             try {
                 exdwrnoehejaihzd = new FileWriter(file6);
                 exdwrnoehejaihzd.write("iro1BHBh4TFloqUDa1MexPj5qKG");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (exdwrnoehejaihzd != null) {
                     try {
                         exdwrnoehejaihzd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date shoysdxjekbbbpo = new Date();
             String nepvkchnnzpqfzcewaf = String.valueOf(shoysdxjekbbbpo.getTime());
             System.currentTimeMillis();
             Handler gswileqowsihsbkiqq = new Handler(Looper.getMainLooper());
             gswileqowsihsbkiqq.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ikdqvfhywnqipvrri.setVisibility(View.VISIBLE);
                 }
             }, 621);
         }
     });
     final View ohthxyyvcoriplm = findViewById(R.id.rcfpumhdsiliydvakkkqoh);
     ohthxyyvcoriplm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent avkboupewgueqakwwsgrm = new Intent();
             avkboupewgueqakwwsgrm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.IztuoqavtnsuhokkxActivity");
             startActivity(avkboupewgueqakwwsgrm);
             int vaddnymijbpkhyxyvddgu = 359;
             Math.max(vaddnymijbpkhyxyvddgu, 58);
             String.valueOf(vaddnymijbpkhyxyvddgu);
             SQLiteOpenHelper erfobdwuybthclbmungdbrab = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "aafewfqzendcyuord.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zcrkdbvmgmwgnyokzi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ybyezkdaxpynrdyxcleu = erfobdwuybthclbmungdbrab.getWritableDatabase();
             try {
                 ybyezkdaxpynrdyxcleu.execSQL("INSERT INTO zcrkdbvmgmwgnyokzi (name, value) VALUES (?, ?)", new String[]{"mzmursgocokiekeucm", "rcqnzeokpdyanrdqmmjptqy"});
             } finally {
                 ybyezkdaxpynrdyxcleu.close();
                 erfobdwuybthclbmungdbrab.close();
             }
             int kkjppxequirlpmpdyofrjz = 979;
             Math.max(kkjppxequirlpmpdyofrjz, 291);
             String.valueOf(kkjppxequirlpmpdyofrjz);
             SQLiteOpenHelper reqdddngrgqpqbbdp = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "nankbxmnsnldtzp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "aunssbxtmdvemcyph"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lslrgtvrnmkcyufynatl = reqdddngrgqpqbbdp.getWritableDatabase();
             try {
                 lslrgtvrnmkcyufynatl.execSQL("DELETE FROM aunssbxtmdvemcyph WHERE name = ?", new String[]{"lspunfkwjvdksyhmln", "rdctwlvrgkebwzzhdseds"});
             } finally {
                 lslrgtvrnmkcyufynatl.close();
                 reqdddngrgqpqbbdp.close();
             }
             ohthxyyvcoriplm.animate()
                     .translationX(161f)
                     .translationY(45f)
                     .setDuration(124)
                     .start();
             Intent lhlxufvwowaqkuazvpkamgza = new Intent();
             lhlxufvwowaqkuazvpkamgza.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.IztuoqavtnsuhokkxActivity");
             startActivity(lhlxufvwowaqkuazvpkamgza);
         }
     });
     final View vpqbhadzbgbbsyo = findViewById(R.id.zfzlvaurhmhpeunzopq);
     vpqbhadzbgbbsyo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent erkjolplognyejmuwyjnigzkb = new Intent();
             erkjolplognyejmuwyjnigzkb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.StdpvvehtlxqeujoncmshwriyActivity");
             startActivity(erkjolplognyejmuwyjnigzkb);
             Date pglzwehctzujopnfxtn = new Date();
             String fttxygtgmpdmbqj = String.valueOf(pglzwehctzujopnfxtn.getTime());
             System.currentTimeMillis();
             Intent deqmeecbwaoipnbhnm = new Intent();
             deqmeecbwaoipnbhnm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.StdpvvehtlxqeujoncmshwriyActivity");
             startActivity(deqmeecbwaoipnbhnm);
             SQLiteOpenHelper nxqgkcftcajmoktmptwdpm = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "hhmpbokduecukrxqwqomuhbo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nvtsreaxbtylgqrbvrl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ifhirtjcesbymnh = nxqgkcftcajmoktmptwdpm.getWritableDatabase();
             try {
                 ifhirtjcesbymnh.execSQL("DELETE FROM nvtsreaxbtylgqrbvrl WHERE name = ?", new String[]{"xdsvtkwudvcfwrds", "rmrwsvjfbxwdysxxk"});
             } finally {
                 ifhirtjcesbymnh.close();
                 nxqgkcftcajmoktmptwdpm.close();
             }
             Intent zeghtpoocqihnpxcwmxjrlwl = new Intent();
             zeghtpoocqihnpxcwmxjrlwl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.StdpvvehtlxqeujoncmshwriyActivity");
             startActivity(zeghtpoocqihnpxcwmxjrlwl);
             String qxocxxldymkqwsywvhkpta = "ApzcTsefW7wq3PmrvHSrlKzPB6wlbb0NQuruNExAFIQ0rCCdEmKsvVzExyffy50wqrWj75ONFZf2DDVFbxhhxoCKGDE4zYOmjCZGGYYrLKuy6Idko87DOcEZUh4MgBekCBZV25Fe11XY2u2xGFGvb0tRywsGc3w1xovp4ziGj6f299redJYvCbruVE";
             qxocxxldymkqwsywvhkpta.substring(2);
             qxocxxldymkqwsywvhkpta.replace('x', 'i');
             vpqbhadzbgbbsyo.animate()
                     .alpha(0.3238265f)
                     .setDuration(84)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(GpaljuyyblpljuyfyActivity.this, "wxuafcdveyffmgoisol", Toast.LENGTH_SHORT).show();
                 }
             });
             Date kixwplbjxktfdohoiyfcbtj = new Date();
             String irdbxwzfjxacjwggjkvhjlt = String.valueOf(kixwplbjxktfdohoiyfcbtj.getTime());
             System.currentTimeMillis();
             vpqbhadzbgbbsyo.animate()
                     .rotation(56f)
                     .setDuration(268)
                     .start();
         }
     });
     final View zyxosfeokiiquds = findViewById(R.id.newaaxqbzkzsqlxgjjsmjoxrc);
     zyxosfeokiiquds.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             zyxosfeokiiquds.animate()
                     .rotation(188f)
                     .setDuration(976)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(GpaljuyyblpljuyfyActivity.this, "uhsdmyfuuotguuiezhb", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View kxwgfipfftquwkjdsidmlbn = findViewById(R.id.jjrhvxxtathgzqul);
     kxwgfipfftquwkjdsidmlbn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService mgxrbozjscmlvkvowpt = Executors.newScheduledThreadPool(2);
             mgxrbozjscmlvkvowpt.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(56);
                 }
             });
             mgxrbozjscmlvkvowpt.shutdown();
             int mzhutvanfyulajvzfsw = 218;
             Math.max(mzhutvanfyulajvzfsw, 133);
             String.valueOf(mzhutvanfyulajvzfsw);
             kxwgfipfftquwkjdsidmlbn.animate()
                     .translationX(81f)
                     .translationY(104f)
                     .setDuration(844)
                     .start();
             ScheduledExecutorService bxpytvsvlmkpffcjieoalm = Executors.newScheduledThreadPool(4);
             bxpytvsvlmkpffcjieoalm.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(67);
                 }
             });
             bxpytvsvlmkpffcjieoalm.shutdown();
             File file4 = new File(getFilesDir(), "ixfazkkyrhkxlckjgfijx.txt");
             if(file4.exists()) file4.delete();
             Intent onvrckddltinhjiwhicndm = new Intent();
             onvrckddltinhjiwhicndm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.GuacafifdwzcfqlghnttosyeActivity");
             startActivity(onvrckddltinhjiwhicndm);
         }
     });
     final View kqtkilhtsnzmcnfje = findViewById(R.id.evsfqsptfutfgpsg);
     kqtkilhtsnzmcnfje.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService zkmohdrfupgxtjjmxnlsijld = Executors.newScheduledThreadPool(2);
             zkmohdrfupgxtjjmxnlsijld.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(59);
                 }
             });
             zkmohdrfupgxtjjmxnlsijld.shutdown();
             File file1 = new File(getFilesDir(), "ysbeoitwncazxlzqunscbby.txt");
             FileWriter dlnbxieyibwczvdrhxjy = null;
             try {
                 dlnbxieyibwczvdrhxjy = new FileWriter(file1);
                 dlnbxieyibwczvdrhxjy.write("E9xmQIsIcSv1gVXuizLZGwjbiqffmpIW3ZPHiZXDb1DWxx5ej0Uj7nndM3W4rRZ1oJlN1FwZUaq7VSexrNddwpeXyJ37nmt7H6D97Jxn7fAvIe0W55E2v81YU78t8cGxGe5tnNXb7Jx62okj69sy504yJBUMSd7Hl8bnV9knqdJqyTYg2jgOPJ9ZrYuhMH6H5DnMy");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (dlnbxieyibwczvdrhxjy != null) {
                     try {
                         dlnbxieyibwczvdrhxjy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent pvwestacecbmreybspa = new Intent();
             pvwestacecbmreybspa.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.GjpsvqnrofsyyrxhihrActivity");
             startActivity(pvwestacecbmreybspa);
             ScheduledExecutorService grgqixaaqnxqxwhiedvthklrw = Executors.newScheduledThreadPool(4);
             grgqixaaqnxqxwhiedvthklrw.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(43);
                 }
             });
             grgqixaaqnxqxwhiedvthklrw.shutdown();
             int ofckgrmenuktejuuoa = 257;
             Math.max(ofckgrmenuktejuuoa, 670);
             String.valueOf(ofckgrmenuktejuuoa);
             kqtkilhtsnzmcnfje.animate()
                     .scaleX(1.1265651f)
                     .scaleY(1.1265651f)
                     .setDuration(384)
                     .start();
             Date fwxwnpxyvconnbe = new Date();
             String edootyvyzotbogxwdnnyipk = String.valueOf(fwxwnpxyvconnbe.getTime());
             System.currentTimeMillis();
             Intent isivhhyurcwhlljwvrqkh = new Intent();
             isivhhyurcwhlljwvrqkh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.GjpsvqnrofsyyrxhihrActivity");
             startActivity(isivhhyurcwhlljwvrqkh);
         }
     });
     final View shqprwcnggqnqkgxileibcjv = findViewById(R.id.cntutclyqdfjmttydoxmvp);
     shqprwcnggqnqkgxileibcjv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService xpjfyiqrpcdbfxgiwqi = Executors.newScheduledThreadPool(4);
             xpjfyiqrpcdbfxgiwqi.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(20);
                 }
             });
             xpjfyiqrpcdbfxgiwqi.shutdown();
             File file1 = new File(getFilesDir(), "fdnifqkpdkgouhyujxja.txt");
             FileWriter xcjezcaqnxnwtxwhtngwpxuqn = null;
             try {
                 xcjezcaqnxnwtxwhtngwpxuqn = new FileWriter(file1);
                 xcjezcaqnxnwtxwhtngwpxuqn.write("7zUuF3jacK95VYn");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (xcjezcaqnxnwtxwhtngwpxuqn != null) {
                     try {
                         xcjezcaqnxnwtxwhtngwpxuqn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             shqprwcnggqnqkgxileibcjv.animate()
                     .scaleX(0.78369427f)
                     .scaleY(0.78369427f)
                     .setDuration(367)
                     .start();
             File file3 = new File(getFilesDir(), "fjsabukygwztebo.txt");
             if(file3.exists()) file3.delete();
             Intent dohrkziylsspruq = new Intent();
             dohrkziylsspruq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.SpnvzobxoqftdhgcvActivity");
             startActivity(dohrkziylsspruq);
             SQLiteOpenHelper vzymzlnerragdyesbp = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "lmqzulgtfpdlhinfvxvdgdukw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mlonjnyswetiraema"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase aydiahddpbrjszxkclbaldd = vzymzlnerragdyesbp.getWritableDatabase();
             try {
                 aydiahddpbrjszxkclbaldd.execSQL("SELECT * FROM mlonjnyswetiraema WHERE name = ?", new String[]{"tacozkwxjrsktrsmrwssdeb", "pohqskihblbqdrktmbhj"});
             } finally {
                 aydiahddpbrjszxkclbaldd.close();
                 vzymzlnerragdyesbp.close();
             }
         }
     });
     final View wbhnsbcvwfzjsyjgxomqonh = findViewById(R.id.puspujodzlwifrx);
     wbhnsbcvwfzjsyjgxomqonh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ndvnhtistncwtdhakqc = new Intent();
             ndvnhtistncwtdhakqc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.WarnigtsoxtggdbzqActivity");
             startActivity(ndvnhtistncwtdhakqc);
             SQLiteOpenHelper pwvlsfmymzvpwgbpt = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "ynxrpqvgnfobtjdycol.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iinlsmycgizdojjvzo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rozdynvttobckeqx = pwvlsfmymzvpwgbpt.getWritableDatabase();
             try {
                 rozdynvttobckeqx.execSQL("UPDATE iinlsmycgizdojjvzo SET value = ? WHERE name = ?", new String[]{"wttoopphmgvtxcqwsnnnlfgo", "ykkaawjsovxxvsgyemte"});
             } finally {
                 rozdynvttobckeqx.close();
                 pwvlsfmymzvpwgbpt.close();
             }
             ScheduledExecutorService samyvfgkztcugthivhedrk = Executors.newScheduledThreadPool(4);
             samyvfgkztcugthivhedrk.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(79);
                 }
             });
             samyvfgkztcugthivhedrk.shutdown();
             File file3 = new File(getFilesDir(), "jusyaecwjsbucqrfgybqwch.txt");
             Intent jvcqatvxnixlupb = new Intent();
             jvcqatvxnixlupb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.WarnigtsoxtggdbzqActivity");
             startActivity(jvcqatvxnixlupb);
             wbhnsbcvwfzjsyjgxomqonh.animate()
                     .translationX(64f)
                     .translationY(181f)
                     .setDuration(448)
                     .start();
             SQLiteOpenHelper tzchnjtkexzrxkwliptwpm = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "ebieryxoyvtgfizmc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rxtftykhoyzsrvtrdbqs"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jjehnunzbfnaxynytvugqe = tzchnjtkexzrxkwliptwpm.getWritableDatabase();
             try {
                 jjehnunzbfnaxynytvugqe.execSQL("SELECT * FROM rxtftykhoyzsrvtrdbqs WHERE name = ?", new String[]{"yygevxawvfslodszliesazzza", "nnasxmccjvqpdhndehizhna"});
             } finally {
                 jjehnunzbfnaxynytvugqe.close();
                 tzchnjtkexzrxkwliptwpm.close();
             }
             Intent lndddjrsygeioajogqa = new Intent();
             lndddjrsygeioajogqa.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.WarnigtsoxtggdbzqActivity");
             startActivity(lndddjrsygeioajogqa);
             SQLiteOpenHelper stpjryojtkhvidbehbebqqw = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "etiyryvlsolimxnjqjnpexdz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hdtrryqsbmvepmawc"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lywrkjlzuzarmwnqpsyyvm = stpjryojtkhvidbehbebqqw.getWritableDatabase();
             try {
                 lywrkjlzuzarmwnqpsyyvm.execSQL("INSERT INTO hdtrryqsbmvepmawc (name, value) VALUES (?, ?)", new String[]{"cksuokkgoknwvbjcexyu", "mjfaiaectypoghvmbb"});
             } finally {
                 lywrkjlzuzarmwnqpsyyvm.close();
                 stpjryojtkhvidbehbebqqw.close();
             }
         }
     });
     final View ssgucavrxeltskrhcw = findViewById(R.id.qaikqhkcmsnfgfxrymojygr);
     ssgucavrxeltskrhcw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent kaglvbwohooducm = new Intent();
             kaglvbwohooducm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.RzqpgvlfkzznagbmActivity");
             startActivity(kaglvbwohooducm);
             ssgucavrxeltskrhcw.animate()
                     .translationX(41f)
                     .translationY(17f)
                     .setDuration(488)
                     .start();
             ScheduledExecutorService hzluoguktcvlravce = Executors.newScheduledThreadPool(1);
             hzluoguktcvlravce.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(21);
                 }
             });
             hzluoguktcvlravce.shutdown();
             SQLiteOpenHelper pkqoqkexokpocikqx = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "orpfcebxjzaclvc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "toabqrlpdyhmczlcynfjswmp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xpahiumabeuinyrrqy = pkqoqkexokpocikqx.getWritableDatabase();
             try {
                 xpahiumabeuinyrrqy.execSQL("UPDATE toabqrlpdyhmczlcynfjswmp SET value = ? WHERE name = ?", new String[]{"qhbadigapnnnzunmabjmmjp", "pvhtucexitauwgu"});
             } finally {
                 xpahiumabeuinyrrqy.close();
                 pkqoqkexokpocikqx.close();
             }
             Handler teqfwwcjkwsdgftzfqfcrtb = new Handler(Looper.getMainLooper());
             teqfwwcjkwsdgftzfqfcrtb.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ssgucavrxeltskrhcw.setVisibility(View.VISIBLE);
                 }
             }, 657);
             SQLiteOpenHelper oxhmszqcucvvqyywkgcbn = new SQLiteOpenHelper(GpaljuyyblpljuyfyActivity.this, "jfpujgasvldbxjlmp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qjhiunbzlasgbhltabamlnqk"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gfuiumtqodqjegrkbxapy = oxhmszqcucvvqyywkgcbn.getWritableDatabase();
             try {
                 gfuiumtqodqjegrkbxapy.execSQL("UPDATE qjhiunbzlasgbhltabamlnqk SET value = ? WHERE name = ?", new String[]{"ekfywaxypmeikwwgbfaegaec", "vrahcsaacpwbphtyowsr"});
             } finally {
                 gfuiumtqodqjegrkbxapy.close();
                 oxhmszqcucvvqyywkgcbn.close();
             }
             File file6 = new File(getFilesDir(), "unnlvytjeesgzphfiqthisc.txt");
         }
     });
     final View cgouxmrxapanrobfkfycoo = findViewById(R.id.bpduvboaknybqvalpiplyy);
     cgouxmrxapanrobfkfycoo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "ljufadznesywlbqomjt.txt");
             ScheduledExecutorService ofcqayscvvrdxpnofbls = Executors.newScheduledThreadPool(4);
             ofcqayscvvrdxpnofbls.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(88);
                 }
             });
             ofcqayscvvrdxpnofbls.shutdown();
         }
     });

abutaioaacagogmn();
Urpdddjzeddouiuqju   hcqadqylptbjplvynpseji =     new Urpdddjzeddouiuqju();
hcqadqylptbjplvynpseji.xevnqskwzinhshcdnvupk ="d9ePoDdDv6Uyv97v26B9xY6aTEYX3HrLlTPciYvug50p6uohNM5I9kWdvC1X2XVsKLcr9dHHJZqNpmJGxJvQ7NbK6Y9o0MU8e7X82tjbelUoIG2UqxKRwXKUEnKnw2ZaHxt2qoIElyak8A8ueGk";
 Toast.makeText(GpaljuyyblpljuyfyActivity.this,getString(R.string.qgqqpogoxqgajsdoypolcasfc),Toast.LENGTH_SHORT).show();
    }
 void abutaioaacagogmn() {
     long xpckddvnnuulzmxnao = System.currentTimeMillis();
     Date date = new Date(xpckddvnnuulzmxnao);
     String time = date.toString();
     qyikqngwrlnvjixakvdk();
}
 void qyikqngwrlnvjixakvdk() {
     TextView ozqzxsrtstrpwxnpsih = new TextView(GpaljuyyblpljuyfyActivity.this);
     ozqzxsrtstrpwxnpsih.setVisibility(View.VISIBLE);
     ozqzxsrtstrpwxnpsih.setEnabled(true);
}}