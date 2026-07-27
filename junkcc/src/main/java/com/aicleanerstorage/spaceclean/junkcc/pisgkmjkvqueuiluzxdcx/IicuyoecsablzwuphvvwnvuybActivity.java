package com.aicleanerstorage.spaceclean.junkcc.pisgkmjkvqueuiluzxdcx;

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
import android.widget.ToggleButton;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class IicuyoecsablzwuphvvwnvuybActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_icuyoecsablzwuphvvwnvuyb);
     final View vdpgmlqgunoottycbkp = findViewById(R.id.tbalxghskutmtzrufv);
     vdpgmlqgunoottycbkp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "pvqaetknhgjwdrpcsbt.txt");
             if(file0.exists()) file0.delete();
             ScheduledExecutorService tlafwztdgjdlfhuiojx = Executors.newScheduledThreadPool(2);
             tlafwztdgjdlfhuiojx.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(65);
                 }
             });
             tlafwztdgjdlfhuiojx.shutdown();
             File file2 = new File(getFilesDir(), "rcaulcodvgmjrefixvnxpv.txt");
             FileReader wyfshkjqjzesyfppchu = null;
             try {
                 if(file2.exists()) {
                     wyfshkjqjzesyfppchu = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wyfshkjqjzesyfppchu != null) {
                     try {
                         wyfshkjqjzesyfppchu.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper xjedbpdqyecrvvcfe = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "czwmecknukisaemiwruh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gbrbiaebwasxvvsvafux"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jnwegtxjpyxcyiexusj = xjedbpdqyecrvvcfe.getWritableDatabase();
             try {
                 jnwegtxjpyxcyiexusj.execSQL("SELECT * FROM gbrbiaebwasxvvsvafux WHERE name = ?", new String[]{"upeiodiprykrxhjg", "vhjmyoapdmtxdenxfqvwa"});
             } finally {
                 jnwegtxjpyxcyiexusj.close();
                 xjedbpdqyecrvvcfe.close();
             }
             ScheduledExecutorService bfsxjamacdgfvhxzu = Executors.newScheduledThreadPool(4);
             bfsxjamacdgfvhxzu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(59);
                 }
             });
             bfsxjamacdgfvhxzu.shutdown();
             SQLiteOpenHelper fkhzasqchdlepqgzvtx = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "ldsjggopgaajwstqh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hrcuwnxcovbhgod"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hbuepfktfpkksojnnsxphtqe = fkhzasqchdlepqgzvtx.getWritableDatabase();
             try {
                 hbuepfktfpkksojnnsxphtqe.execSQL("UPDATE hrcuwnxcovbhgod SET value = ? WHERE name = ?", new String[]{"rkfvuxeqwteuspwsk", "ogwbhcentpttmxgqfy"});
             } finally {
                 hbuepfktfpkksojnnsxphtqe.close();
                 fkhzasqchdlepqgzvtx.close();
             }
             Handler ovsfjybetlnxvfgyny = new Handler(Looper.getMainLooper());
             ovsfjybetlnxvfgyny.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     vdpgmlqgunoottycbkp.setVisibility(View.VISIBLE);
                 }
             }, 351);
             SQLiteOpenHelper qtplfhxsgkibjckqqqkyousj = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "qarvzsdpxahrllxyhyjszx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kazrqnrnjktwcgw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jbhshixzspznwspb = qtplfhxsgkibjckqqqkyousj.getWritableDatabase();
             try {
                 jbhshixzspznwspb.execSQL("SELECT * FROM kazrqnrnjktwcgw WHERE name = ?", new String[]{"duhupokbrvejrunjghltjpro", "fhvsvrwashcnfpsngcjvhx"});
             } finally {
                 jbhshixzspznwspb.close();
                 qtplfhxsgkibjckqqqkyousj.close();
             }
             String znudpeskslmfattdlj = "qv4LfdKWvJJDpDZsd5HBBLaJcrlwx8SEV64rtROgSyCkcY4UCYh2";
             znudpeskslmfattdlj.substring(1);
             znudpeskslmfattdlj.replace('c', 'r');
             vdpgmlqgunoottycbkp.animate()
                     .rotation(41f)
                     .setDuration(423)
                     .start();
         }
     });
     final View ijiqixowoxyxcocpgpptvsut = findViewById(R.id.kjokxvuizwxfohvtpuxe);
     ijiqixowoxyxcocpgpptvsut.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler gphlqccmojzbpykr = new Handler(Looper.getMainLooper());
             gphlqccmojzbpykr.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ijiqixowoxyxcocpgpptvsut.setVisibility(View.VISIBLE);
                 }
             }, 483);
             Intent xvasuteoptoytnlzcap = new Intent();
             xvasuteoptoytnlzcap.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.pisgkmjkvqueuiluzxdcx.NehetncoqcevuefvwnvdbActivity");
             startActivity(xvasuteoptoytnlzcap);
             ijiqixowoxyxcocpgpptvsut.animate()
                     .rotation(198f)
                     .setDuration(205)
                     .start();
             SQLiteOpenHelper ppnblfigkueivnxyvzomemgh = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "aunsfojcfwetwhmant.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fpwmqbjcrfnjvofllgwnsytp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rzuhmycexmblhokuezfbi = ppnblfigkueivnxyvzomemgh.getWritableDatabase();
             try {
                 rzuhmycexmblhokuezfbi.execSQL("DELETE FROM fpwmqbjcrfnjvofllgwnsytp WHERE name = ?", new String[]{"ugqtbqawtzhuwhxugp", "nnskwklkzpdroodkb"});
             } finally {
                 rzuhmycexmblhokuezfbi.close();
                 ppnblfigkueivnxyvzomemgh.close();
             }
             String jifcrijezurvozyc = "Um0Kyhhp9YbpryIs3DOvcGKEgEFBzbjfFQOU1gOx1MeSUxB7BTBDNYHUmi5j0qeXAz1O5PmImAsmSbT7pEFLukbG5XjsKf1AiVtI03yju4ZhRrvsGdCLB71fytJdXl9spN8kK";
             jifcrijezurvozyc.substring(1);
             jifcrijezurvozyc.replace('c', 'o');
             ScheduledExecutorService tnnphroyeugcmgaahxdwku = Executors.newScheduledThreadPool(1);
             tnnphroyeugcmgaahxdwku.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(57);
                 }
             });
             tnnphroyeugcmgaahxdwku.shutdown();
         }
     });
     final View dbjzbuudqkjofxycvgy = findViewById(R.id.jxbddypdlzdmbwzourpzg);
     dbjzbuudqkjofxycvgy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper vibdsadmzsqizfvytatvjvlxj = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "kcvwugjledpxwrczq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jxmnjmsnpqqnoibahwmtyi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase iinevkepruaenlqtjndjzo = vibdsadmzsqizfvytatvjvlxj.getWritableDatabase();
             try {
                 iinevkepruaenlqtjndjzo.execSQL("SELECT * FROM jxmnjmsnpqqnoibahwmtyi WHERE name = ?", new String[]{"udwyiywlwundfvasqd", "szltqbsvhipbyxspiomcpq"});
             } finally {
                 iinevkepruaenlqtjndjzo.close();
                 vibdsadmzsqizfvytatvjvlxj.close();
             }
             File file1 = new File(getFilesDir(), "aukioqszpfdmidbrymtfa.txt");
             Date yzparkvytdlrcvujstqdo = new Date();
             String tylrgaghkvykwavcnjql = String.valueOf(yzparkvytdlrcvujstqdo.getTime());
             System.currentTimeMillis();
             File file3 = new File(getFilesDir(), "ziofdshhyraxqavyk.txt");
             if(file3.exists()) file3.delete();
             Intent yllewzqkiwiglnmeffqxyjfca = new Intent();
             yllewzqkiwiglnmeffqxyjfca.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.pisgkmjkvqueuiluzxdcx.TgtaeojhlnkpbszbqgmbuActivity");
             startActivity(yllewzqkiwiglnmeffqxyjfca);
             ScheduledExecutorService lfflvxksilbzhsa = Executors.newScheduledThreadPool(4);
             lfflvxksilbzhsa.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(6);
                 }
             });
             lfflvxksilbzhsa.shutdown();
             SQLiteOpenHelper ohdcguyqzbluopouimjuqchi = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "gkdkzyiutrrdotqzs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zbvfhpbqlgdukpxmcrp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tttdichldrhkjqjziinmpcl = ohdcguyqzbluopouimjuqchi.getWritableDatabase();
             try {
                 tttdichldrhkjqjziinmpcl.execSQL("UPDATE zbvfhpbqlgdukpxmcrp SET value = ? WHERE name = ?", new String[]{"tghmkrbigoodxmod", "mzpshnhengbewmrfxee"});
             } finally {
                 tttdichldrhkjqjziinmpcl.close();
                 ohdcguyqzbluopouimjuqchi.close();
             }
         }
     });
     final View sxguowdmeiyiezk = findViewById(R.id.csxybulsuzghjoopxtrpqkzmn);
     sxguowdmeiyiezk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int iwetnjiqobbvgfmdtdjddgq = 11;
             Math.max(iwetnjiqobbvgfmdtdjddgq, 652);
             String.valueOf(iwetnjiqobbvgfmdtdjddgq);
             SQLiteOpenHelper tchqcoseuguomlymimd = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "izvousnblkgszqkgxzv.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mesncedyzantyzvlwmfl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kucjmyxxnrbiasbrjlnbyz = tchqcoseuguomlymimd.getWritableDatabase();
             try {
                 kucjmyxxnrbiasbrjlnbyz.execSQL("DELETE FROM mesncedyzantyzvlwmfl WHERE name = ?", new String[]{"abcklmsdardtkefjrjur", "sgywjabigwyjwjwekn"});
             } finally {
                 kucjmyxxnrbiasbrjlnbyz.close();
                 tchqcoseuguomlymimd.close();
             }
             sxguowdmeiyiezk.animate()
                     .rotation(78f)
                     .setDuration(874)
                     .start();
             Date dvatjuwfnaeoetrmwn = new Date();
             String hijrikbewqmoeytwp = String.valueOf(dvatjuwfnaeoetrmwn.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper skarjvumexczsoru = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "qjzemznvuktsncmkozx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wjexmthtiplmare"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hmzznpabifsrwrsjbwhf = skarjvumexczsoru.getWritableDatabase();
             try {
                 hmzznpabifsrwrsjbwhf.execSQL("UPDATE wjexmthtiplmare SET value = ? WHERE name = ?", new String[]{"uemwtprvcjrcnxgxgcysefgns", "ysiquaoudsctlsgpqplll"});
             } finally {
                 hmzznpabifsrwrsjbwhf.close();
                 skarjvumexczsoru.close();
             }
             Date eufivdhbhmfedjmrtbx = new Date();
             String inxcxksauyawwbed = String.valueOf(eufivdhbhmfedjmrtbx.getTime());
             System.currentTimeMillis();
             File file6 = new File(getFilesDir(), "nzlndjghcqxgubifjpxkcofkh.txt");
         }
     });
     final View pdzmhiftrpvhqko = findViewById(R.id.cqgubcvjvjlzshasqu);
     pdzmhiftrpvhqko.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pdzmhiftrpvhqko.animate()
                     .rotation(86f)
                     .setDuration(254)
                     .start();
             Intent nnxciosxykrymmgafwyv = new Intent();
             nnxciosxykrymmgafwyv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.pisgkmjkvqueuiluzxdcx.ZkxpviperiwihbxorakwlkdActivity");
             startActivity(nnxciosxykrymmgafwyv);
             int ookbgdosirolybmdpxow = 477;
             Math.max(ookbgdosirolybmdpxow, 789);
             String.valueOf(ookbgdosirolybmdpxow);
             SQLiteOpenHelper gwavupmtnhsncixotl = new SQLiteOpenHelper(IicuyoecsablzwuphvvwnvuybActivity.this, "lghodhpvadagggo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fzfsgsnkbidonmja"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ncfsocpnkjtwyxhmtwntvebf = gwavupmtnhsncixotl.getWritableDatabase();
             try {
                 ncfsocpnkjtwyxhmtwntvebf.execSQL("INSERT INTO fzfsgsnkbidonmja (name, value) VALUES (?, ?)", new String[]{"coicpseerpblrjeygwvc", "dytbndjnjxcggmtmnzsaadpi"});
             } finally {
                 ncfsocpnkjtwyxhmtwntvebf.close();
                 gwavupmtnhsncixotl.close();
             }
             Intent ckggndwnryifqgph = new Intent();
             ckggndwnryifqgph.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.pisgkmjkvqueuiluzxdcx.ZkxpviperiwihbxorakwlkdActivity");
             startActivity(ckggndwnryifqgph);
         }
     });

hyddvvhgwzznxmkioq();
Xsgwsoasgwtrilzkefcqtxneb   loyjrwqnzutoxejhbx =     new Xsgwsoasgwtrilzkefcqtxneb();
loyjrwqnzutoxejhbx.krqbthofkinaktvxy ="rtaMW8xm9XCGeSy7ETVVmMe7JF446Gpwu0BkhhpWuRg2TQKhWPICAt7QdW4Su0lmfviPTpRHko6bn4lgMs2Jc7Y6PEs900g9VQzgBS8u3AWyNxQdUFKK5IFeCXDDrFw4yzF";
loyjrwqnzutoxejhbx.xznvfaixpthibujejt ="Z99rJ14C8hrxIYAJGwwyiVuhRupLE5IDwOYUF2inFtIdw5gquMBdi3jPZvauPxKNoaCSVasgb20SyATD64yMq";
loyjrwqnzutoxejhbx.dimmqgimhjqxfmxldzsstxzxm ="o0AVuItt2l5GMtXZONkwjwwcVqY7WCk6iMvaqdf9Jhb2Rdw7usFJbu5I2TFerDVfIXl9MbNyFYojHFz0RhLkUi16NwmdX7Ro1XJq1R9iDxHsWt4vtQ74ZXYcAhqMtTkxs4rOyyv3Iqi7JIqjnLexDTe1vgaGQwsaUsW2tBxMVjiOm4BmPKjaF7ZibXa4kONhsbX";
loyjrwqnzutoxejhbx.njlpjyhtqldagoe ="Lb9KiQ44l2vOp7ayvzsDI9nEWtnSovmV7SM9VaQgIKlgrOMtisHcyyunNY4x8C1lP2ivK9tuFjro3XY5xuiHCP8lWsdsljZwEeTPXYzM0oKOgW2FuupRSymLEgOefdWHHYjrn7WzHsLsROcwcL8HQESSB0P4h8vl0MCytzvyIkregpLKsO5J4dJYZn6Lf";
loyjrwqnzutoxejhbx.ftrkthljglelgjhrtzlumf ="1JmThx9w7HJ7fO92TW3UZbFxFspXrQtFsZ0z4Yru0HYS9";
loyjrwqnzutoxejhbx.jqujnyiktrpkvpdtpypxe ="ihtClOU8NcCIwbbaoij5gz5nLNyrbggA";
loyjrwqnzutoxejhbx.ydxzlmgnyspbabur ="JsVIxiQi2CAHt63MzTbZeyA7hZ9M6QyB0YGIjLFdR93lGA4J8FTklV4WyRnH5ZyYji9WhnksxYM8Fln3pU303APSCuMOSbKrtUKoH9SnOZ8Z3hCIqdiXhUL3RRza37JSyeE6QLRnUVn63XGxxH";
loyjrwqnzutoxejhbx.tyogasujnekzlwsibsflfoxlp ="Cd9qudGQfVnpLWwmzelfEpd9qQbDDIOksv9OR62SvoreDR";
loyjrwqnzutoxejhbx.ymndsxvbqkbpgnfyechrktbni ="2UAXrOhZBwLfCFJNpZfndvoTGRDTRozF5QsFmCQ0KEP5bI0XLwPIRCXnYeMl7kQr1b5wcw6IX8tnajruKYGgTWs0ssyWmn69f898tKa9ckuukZyZnlBcEoBqdEYDWV2Gme1cQLujHA94qFVE3EbE7dRbyvBKvFD9U7kp35P8hOo";
loyjrwqnzutoxejhbx.sqtuphdwaqvwimsyrqajzic ="09OeBmMorwgm1SeA4qVXH0tuEcFRTaorM5axt5BH1sZCN28eYGV84ArKQin8fghKpGFJ3Wg9nyWDjzuoDjkBa0dnn7TwjwOahQedciAjMBUL";
 Toast.makeText(IicuyoecsablzwuphvvwnvuybActivity.this,getString(R.string.gvihjfobcnjkitewe),Toast.LENGTH_SHORT).show();
    }
 void hyddvvhgwzznxmkioq() {
     ToggleButton gppdrnufgwfahxxqslsubk = new ToggleButton(IicuyoecsablzwuphvvwnvuybActivity.this);
     gppdrnufgwfahxxqslsubk.setVisibility(View.VISIBLE);
     gppdrnufgwfahxxqslsubk.setEnabled(false);
     rjpjmlnyishzreao();
}
 void rjpjmlnyishzreao() {
     int obwrcbcrqesptxernmmqry = 584;
     double result = Math.sqrt(obwrcbcrqesptxernmmqry);
     result = Math.pow(result, 2);
     oajpluhojipuowqwn();
}
 void oajpluhojipuowqwn() {
     int wnjzbyisymuftoxaglsyhkrc = 139;
     double result = Math.sqrt(wnjzbyisymuftoxaglsyhkrc);
     result = Math.pow(result, 2);
}}