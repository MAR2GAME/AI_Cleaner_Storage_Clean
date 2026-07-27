package com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq;

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
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class BjxntfkxvjdgmlmncgzbrrvezActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_jxntfkxvjdgmlmncgzbrrvez);
     final View hhiefwayggboqywi = findViewById(R.id.bhdyutoglreccqeepdryg);
     hhiefwayggboqywi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService xvgduxnulnpmzvmympsvbgf = Executors.newScheduledThreadPool(1);
             xvgduxnulnpmzvmympsvbgf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(54);
                 }
             });
             xvgduxnulnpmzvmympsvbgf.shutdown();
             hhiefwayggboqywi.animate()
                     .rotation(303f)
                     .setDuration(960)
                     .start();
             SQLiteOpenHelper sugoextjlgtfriylqgd = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "rnvhtzksxlzvzecb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wfroaenlxzmjrhndgiecvvor"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qdppzvnmkzrrtrpktrh = sugoextjlgtfriylqgd.getWritableDatabase();
             try {
                 qdppzvnmkzrrtrpktrh.execSQL("DELETE FROM wfroaenlxzmjrhndgiecvvor WHERE name = ?", new String[]{"fmfkfigqsaxjujkawdmb", "wyuwevyxzfgjurqqtdex"});
             } finally {
                 qdppzvnmkzrrtrpktrh.close();
                 sugoextjlgtfriylqgd.close();
             }
             File file3 = new File(getFilesDir(), "atztyqcxhybviduomhmn.txt");
             Date mdprwkiybwiipiwcgpfxfpxi = new Date();
             String yimhbyimbpfhgkxcbnqzng = String.valueOf(mdprwkiybwiipiwcgpfxfpxi.getTime());
             System.currentTimeMillis();
             File file5 = new File(getFilesDir(), "wztkvofwhueuufv.txt");
             FileReader pknvruqdzwehqojdlyajvlnq = null;
             try {
                 if(file5.exists()) {
                     pknvruqdzwehqojdlyajvlnq = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (pknvruqdzwehqojdlyajvlnq != null) {
                     try {
                         pknvruqdzwehqojdlyajvlnq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View elyalmtaiplrzsvmtjcmlwohz = findViewById(R.id.surkorcwkojognofyyanyha);
     elyalmtaiplrzsvmtjcmlwohz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "ptomvpmbzupnjjmjmjzpsbs.txt");
             FileWriter vuozcclorqlkzbherf = null;
             try {
                 vuozcclorqlkzbherf = new FileWriter(file0);
                 vuozcclorqlkzbherf.write("VshYacxOIDLx6vemSMc");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (vuozcclorqlkzbherf != null) {
                     try {
                         vuozcclorqlkzbherf.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent ifrgkknlkhwmtjolsvzilp = new Intent();
             ifrgkknlkhwmtjolsvzilp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.BiuhkjkifaueyjyixActivity");
             startActivity(ifrgkknlkhwmtjolsvzilp);
             File file2 = new File(getFilesDir(), "tozgfadfczzwdxotwr.txt");
             if(file2.exists()) file2.delete();
             Intent ntznrvjywahtzcvaifilpdkse = new Intent();
             ntznrvjywahtzcvaifilpdkse.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.BiuhkjkifaueyjyixActivity");
             startActivity(ntznrvjywahtzcvaifilpdkse);
         }
     });
     final View lfpdungwgqmagezpowmdptlo = findViewById(R.id.uygcxmyibnyfulleasroa);
     lfpdungwgqmagezpowmdptlo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper upauqpofkytushdv = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "lerpjscgcxftxpowgrucoewl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hjxcpvlvjypbzngbvrqk"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kafglupivxmcsgtlzucrbnhb = upauqpofkytushdv.getWritableDatabase();
             try {
                 kafglupivxmcsgtlzucrbnhb.execSQL("UPDATE hjxcpvlvjypbzngbvrqk SET value = ? WHERE name = ?", new String[]{"hlanevnlmtgjgbmfufjvf", "cisafudtcmbxpsayiil"});
             } finally {
                 kafglupivxmcsgtlzucrbnhb.close();
                 upauqpofkytushdv.close();
             }
             ScheduledExecutorService oqedqotvyjtzqmdjcznydeqf = Executors.newScheduledThreadPool(4);
             oqedqotvyjtzqmdjcznydeqf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(43);
                 }
             });
             oqedqotvyjtzqmdjcznydeqf.shutdown();
         }
     });
     final View swpsiyzmtxsigyo = findViewById(R.id.vbizupwdtvnvfxzkzjclaiopa);
     swpsiyzmtxsigyo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             swpsiyzmtxsigyo.animate()
                     .alpha(0.45183283f)
                     .setDuration(697)
                     .start();
             int vwnufytjziwmhbrghss = 950;
             Math.max(vwnufytjziwmhbrghss, 220);
             String.valueOf(vwnufytjziwmhbrghss);
             swpsiyzmtxsigyo.animate()
                     .scaleX(0.27194262f)
                     .scaleY(0.27194262f)
                     .setDuration(687)
                     .start();
             ScheduledExecutorService duugpqgkgrdrauvtruqgfgd = Executors.newScheduledThreadPool(1);
             duugpqgkgrdrauvtruqgfgd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(72);
                 }
             });
             duugpqgkgrdrauvtruqgfgd.shutdown();
             SQLiteOpenHelper ltdeeekybgzzzwq = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "llfssopnfealbohzltuqwiscw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xosbxtvgiuwmfiqvoxjni"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kuqrrxbionjxyox = ltdeeekybgzzzwq.getWritableDatabase();
             try {
                 kuqrrxbionjxyox.execSQL("INSERT INTO xosbxtvgiuwmfiqvoxjni (name, value) VALUES (?, ?)", new String[]{"bxorgsrtdoxjsmgxtbbazuk", "vsvtxnwlnkpajxaaahw"});
             } finally {
                 kuqrrxbionjxyox.close();
                 ltdeeekybgzzzwq.close();
             }
             File file5 = new File(getFilesDir(), "ughsstpfcvocztfospcvn.txt");
             FileWriter bmktqhgzjcwwvvhihidxh = null;
             try {
                 bmktqhgzjcwwvvhihidxh = new FileWriter(file5);
                 bmktqhgzjcwwvvhihidxh.write("8CTqfgvpHS803ChBAhlwWoNoGqHa6oXjzBdbSh0MXoruenlrTwFsCPoYDmClHkY2HYrschKtwhVlXkSdYjGHMXAJIBeJV39orN7D8KhenNke0tkXJXVJuRpXiyfndcsOcYyPZ8Ckfu9Q4iMi5D2QpyAfvk2zP43HKHHkpITdJmJ0FH545foyhT");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (bmktqhgzjcwwvvhihidxh != null) {
                     try {
                         bmktqhgzjcwwvvhihidxh.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View hfkhsovrvgikfpgmlpxyv = findViewById(R.id.furjpwwktteugrcvrzuvnfl);
     hfkhsovrvgikfpgmlpxyv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "twoowiuagfqjzkhvameahge.txt");
             if(file0.exists()) file0.delete();
             Date vqcreioogxhcpwxeyuybs = new Date();
             String rnvxohcdhitzpzgpphoiz = String.valueOf(vqcreioogxhcpwxeyuybs.getTime());
             System.currentTimeMillis();
         }
     });
     final View lgjmnnzlmdqsoqwdowtny = findViewById(R.id.toghqidvibvmlmhecjltexmsn);
     lgjmnnzlmdqsoqwdowtny.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper ccwzzrlnirevyasrorbwe = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "eupaemnfhlnfqbeltnfrar.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "eznpvqvhzqjegygilh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase okizvywkpdpocfuyuqf = ccwzzrlnirevyasrorbwe.getWritableDatabase();
             try {
                 okizvywkpdpocfuyuqf.execSQL("UPDATE eznpvqvhzqjegygilh SET value = ? WHERE name = ?", new String[]{"yfmusfcbxynikeke", "naoenrnpodmerbjrsbr"});
             } finally {
                 okizvywkpdpocfuyuqf.close();
                 ccwzzrlnirevyasrorbwe.close();
             }
             lgjmnnzlmdqsoqwdowtny.animate()
                     .scaleX(0.054107785f)
                     .scaleY(0.054107785f)
                     .setDuration(169)
                     .start();
             SQLiteOpenHelper egncdunlvlummtqzfyrextz = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "fqhrwzhfarmayegcing.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "idymhclfoqexprohpcpxdhj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xysxiorlshobfgo = egncdunlvlummtqzfyrextz.getWritableDatabase();
             try {
                 xysxiorlshobfgo.execSQL("DELETE FROM idymhclfoqexprohpcpxdhj WHERE name = ?", new String[]{"wbckzzhsbsdbfwugjax", "uibbirndywrjyhgoz"});
             } finally {
                 xysxiorlshobfgo.close();
                 egncdunlvlummtqzfyrextz.close();
             }
             Intent sxdclzvtdspydlujqbeobce = new Intent();
             sxdclzvtdspydlujqbeobce.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WbsdrkcrplhixevhysyfmgreActivity");
             startActivity(sxdclzvtdspydlujqbeobce);
             ScheduledExecutorService ednbwcsbhthfkcbmtevy = Executors.newScheduledThreadPool(1);
             ednbwcsbhthfkcbmtevy.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(4);
                 }
             });
             ednbwcsbhthfkcbmtevy.shutdown();
             Intent tqttrnujyabetpj = new Intent();
             tqttrnujyabetpj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WbsdrkcrplhixevhysyfmgreActivity");
             startActivity(tqttrnujyabetpj);
             File file6 = new File(getFilesDir(), "miziepaaiihptetslair.txt");
             Intent dwianrhbeloejpv = new Intent();
             dwianrhbeloejpv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WbsdrkcrplhixevhysyfmgreActivity");
             startActivity(dwianrhbeloejpv);
         }
     });
     final View eqgyemsfkqkhpytsoy = findViewById(R.id.xqcxkoyvtkvpfitdyy);
     eqgyemsfkqkhpytsoy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             eqgyemsfkqkhpytsoy.animate()
                     .rotation(52f)
                     .setDuration(22)
                     .start();
             SQLiteOpenHelper uiasgcincszduibtwuxqjvi = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "ftltpwsihktexaq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "trpcycduvkgyurkzribeaon"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qimmcnmlbskjcmijyhuqlvqo = uiasgcincszduibtwuxqjvi.getWritableDatabase();
             try {
                 qimmcnmlbskjcmijyhuqlvqo.execSQL("INSERT INTO trpcycduvkgyurkzribeaon (name, value) VALUES (?, ?)", new String[]{"shbdnhtsgpemefbfyjqtih", "fqvbnwhnibtapahbd"});
             } finally {
                 qimmcnmlbskjcmijyhuqlvqo.close();
                 uiasgcincszduibtwuxqjvi.close();
             }
             eqgyemsfkqkhpytsoy.animate()
                     .translationX(174f)
                     .translationY(35f)
                     .setDuration(703)
                     .start();
         }
     });
     final View injcgulybmxqquawgpuzyotw = findViewById(R.id.htazwalhbwfwidwwccqpo);
     injcgulybmxqquawgpuzyotw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date apnuexzlpjmdfqcfbhkouhbyy = new Date();
             String gfehkpzfexwwbpp = String.valueOf(apnuexzlpjmdfqcfbhkouhbyy.getTime());
             System.currentTimeMillis();
             Intent hckcsjyxsfmlpkarrqdveidfe = new Intent();
             hckcsjyxsfmlpkarrqdveidfe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.ExkgrgzssngzrtqwomitnwActivity");
             startActivity(hckcsjyxsfmlpkarrqdveidfe);
             File file2 = new File(getFilesDir(), "umufttjtphihbvlzczf.txt");
             SQLiteOpenHelper tfeygqtruwxkxrbjdcm = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "ldzkerkigicwjyehhepgyly.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "tzokvsgfxeotjfpcexk"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase sgawokjcffqacqqc = tfeygqtruwxkxrbjdcm.getWritableDatabase();
             try {
                 sgawokjcffqacqqc.execSQL("INSERT INTO tzokvsgfxeotjfpcexk (name, value) VALUES (?, ?)", new String[]{"jjrgjbzzxzzhnsxljuidufsfu", "lejbqqncxligragmu"});
             } finally {
                 sgawokjcffqacqqc.close();
                 tfeygqtruwxkxrbjdcm.close();
             }
         }
     });
     final View nxhmesflmarmmmajga = findViewById(R.id.jphddywhenyvpqdrvxj);
     nxhmesflmarmmmajga.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "kfgtbogthkajkhheyjp.txt");
             Intent ufbitfbferlcpblpngktaqe = new Intent();
             ufbitfbferlcpblpngktaqe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MtnjapbrffekwdfqolkuaimaActivity");
             startActivity(ufbitfbferlcpblpngktaqe);
             SQLiteOpenHelper zdhpdkxxdnhqgmjlhxw = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "zgkdflnlcfvnnjh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wjjsacffefhwcwmiwbbusn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase acpxjpyxgvauebvmcpcotl = zdhpdkxxdnhqgmjlhxw.getWritableDatabase();
             try {
                 acpxjpyxgvauebvmcpcotl.execSQL("DELETE FROM wjjsacffefhwcwmiwbbusn WHERE name = ?", new String[]{"dokhkcmtlzrmyna", "atqxdoyomgmsspjidsxzgruy"});
             } finally {
                 acpxjpyxgvauebvmcpcotl.close();
                 zdhpdkxxdnhqgmjlhxw.close();
             }
             Intent gaweqswhyavawvichri = new Intent();
             gaweqswhyavawvichri.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MtnjapbrffekwdfqolkuaimaActivity");
             startActivity(gaweqswhyavawvichri);
             nxhmesflmarmmmajga.animate()
                     .rotation(122f)
                     .setDuration(605)
                     .start();
             SQLiteOpenHelper epzfttdjbykzafmxmrglsjoby = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "xnzlezoakfpfzisr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "thxpaiooxeyqruhuxfjbrj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ouudxughwjtzuxgilsk = epzfttdjbykzafmxmrglsjoby.getWritableDatabase();
             try {
                 ouudxughwjtzuxgilsk.execSQL("DELETE FROM thxpaiooxeyqruhuxfjbrj WHERE name = ?", new String[]{"srzhozmvjguluzydccjtbmc", "ecvfbdejbzbiuxjmuk"});
             } finally {
                 ouudxughwjtzuxgilsk.close();
                 epzfttdjbykzafmxmrglsjoby.close();
             }
             Intent vmdovcurfeaemro = new Intent();
             vmdovcurfeaemro.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MtnjapbrffekwdfqolkuaimaActivity");
             startActivity(vmdovcurfeaemro);
             nxhmesflmarmmmajga.animate()
                     .alpha(0.76396036f)
                     .setDuration(918)
                     .start();
         }
     });
     final View aydczmijneruwjgknmybku = findViewById(R.id.dgpzqyqnkhqkmaeayamaboyr);
     aydczmijneruwjgknmybku.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int opigtysvbdqkordc = 807;
             Math.max(opigtysvbdqkordc, 49);
             String.valueOf(opigtysvbdqkordc);
             SQLiteOpenHelper snrukpzxuctsmvcwdhpdddq = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "zxlnksbvjzeqidiippfytcoh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dvezenhrgdkzfyatbr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase eylbsegtwaxshnnh = snrukpzxuctsmvcwdhpdddq.getWritableDatabase();
             try {
                 eylbsegtwaxshnnh.execSQL("UPDATE dvezenhrgdkzfyatbr SET value = ? WHERE name = ?", new String[]{"hswaghasvcugavs", "lkqsznujzigspkeyuhg"});
             } finally {
                 eylbsegtwaxshnnh.close();
                 snrukpzxuctsmvcwdhpdddq.close();
             }
             aydczmijneruwjgknmybku.animate()
                     .rotation(163f)
                     .setDuration(547)
                     .start();
             File file3 = new File(getFilesDir(), "tukegjeswptzmwdvjwnmxmap.txt");
             aydczmijneruwjgknmybku.animate()
                     .scaleX(0.050712347f)
                     .scaleY(0.050712347f)
                     .setDuration(100)
                     .start();
             Handler oyccvkxyxxqahdn = new Handler(Looper.getMainLooper());
             oyccvkxyxxqahdn.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     aydczmijneruwjgknmybku.setVisibility(View.VISIBLE);
                 }
             }, 210);
             SQLiteOpenHelper bvksecwfwggjgurpvpzlmret = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "orrdrjboregaceetcu.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ydtvjuklgpmaswarl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase iyinsctfmjjbpwas = bvksecwfwggjgurpvpzlmret.getWritableDatabase();
             try {
                 iyinsctfmjjbpwas.execSQL("UPDATE ydtvjuklgpmaswarl SET value = ? WHERE name = ?", new String[]{"hmgynabbdtfxaoaqfswodlom", "ueydchcxzatdqpcsxl"});
             } finally {
                 iyinsctfmjjbpwas.close();
                 bvksecwfwggjgurpvpzlmret.close();
             }
             aydczmijneruwjgknmybku.animate()
                     .rotation(119f)
                     .setDuration(100)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "kmhvraddgzmpvpwbk", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View knnnvpvcdxtrezgmzrq = findViewById(R.id.athevuvxgtnkmslqzpx);
     knnnvpvcdxtrezgmzrq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper ghgonprhtturfjan = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "exzfnniardkxeqcmzfelxphpo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qutmbrzmfosoynktuasvzv"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xwxzjbigupjxzixhnrc = ghgonprhtturfjan.getWritableDatabase();
             try {
                 xwxzjbigupjxzixhnrc.execSQL("DELETE FROM qutmbrzmfosoynktuasvzv WHERE name = ?", new String[]{"ugglcjergszghqbnzezmx", "uhynwlkxiqazqmvv"});
             } finally {
                 xwxzjbigupjxzixhnrc.close();
                 ghgonprhtturfjan.close();
             }
             Intent tifkhesvlxthdnebrjemj = new Intent();
             tifkhesvlxthdnebrjemj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.YlmxpigkiuaercuwwqfdcqActivity");
             startActivity(tifkhesvlxthdnebrjemj);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "zbyhbtcdfnrrvrlqg", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper wvfwzggfxnwyvizw = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "hjxuqqzmmgptkxcpiawbdc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gflgvxqxgeagffhvcvcn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase uzxmaajvwsmwjamrxzzspw = wvfwzggfxnwyvizw.getWritableDatabase();
             try {
                 uzxmaajvwsmwjamrxzzspw.execSQL("DELETE FROM gflgvxqxgeagffhvcvcn WHERE name = ?", new String[]{"rcxuwrrhhyaiaqfozbg", "bjodxtxakhdhqqlhlzyjnewu"});
             } finally {
                 uzxmaajvwsmwjamrxzzspw.close();
                 wvfwzggfxnwyvizw.close();
             }
         }
     });
     final View zojtvjftjqefbczgob = findViewById(R.id.twljsjwqmhmfcdtfuczjmra);
     zojtvjftjqefbczgob.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             zojtvjftjqefbczgob.animate()
                     .scaleX(0.14550793f)
                     .scaleY(0.14550793f)
                     .setDuration(691)
                     .start();
             String hqtutoypjbzkopqbqfzdw = "JlFEk8h6JVTExIrwk2aRMFjlaltiHy34BFPKvmtgmOCgoLx0uJNu2mQSrzbR5uMN3GWFHPE0slNhcPyw9uYVGlXvYle";
             hqtutoypjbzkopqbqfzdw.substring(3);
             hqtutoypjbzkopqbqfzdw.replace('x', 'g');
         }
     });
     final View lmgcbyidsbskjtbqspltwglaw = findViewById(R.id.addcvjvszxmpubvobscydgs);
     lmgcbyidsbskjtbqspltwglaw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date glpiephkbsdtesdouoee = new Date();
             String qxjvkahywyykybaie = String.valueOf(glpiephkbsdtesdouoee.getTime());
             System.currentTimeMillis();
             lmgcbyidsbskjtbqspltwglaw.animate()
                     .alpha(0.3120063f)
                     .setDuration(649)
                     .start();
             Date hnzibmwppebmjyaxgxeyd = new Date();
             String omtdancifmcuosshrlvc = String.valueOf(hnzibmwppebmjyaxgxeyd.getTime());
             System.currentTimeMillis();
             File file3 = new File(getFilesDir(), "wtjjgtnytdtxgqiauqhxbw.txt");
         }
     });
     final View bgqfwmkgukuxspjjcevezvnhe = findViewById(R.id.ywmfypsnxltjodjuedtwjw);
     bgqfwmkgukuxspjjcevezvnhe.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date vveehtamopkotrlmibphwxvmq = new Date();
             String lxskhphmygylnctsmkw = String.valueOf(vveehtamopkotrlmibphwxvmq.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "dhxntwqbyklmmlqvtrboc.txt");
             Intent lvbmphzvpfmjhnbcicte = new Intent();
             lvbmphzvpfmjhnbcicte.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WmzwcgipsorbtzvhActivity");
             startActivity(lvbmphzvpfmjhnbcicte);
             File file3 = new File(getFilesDir(), "sudpekgpylxvcxzwlcxqyk.txt");
             FileWriter xilmvowopimdulebl = null;
             try {
                 xilmvowopimdulebl = new FileWriter(file3);
                 xilmvowopimdulebl.write("9jps1gDAc0AALmH8IaoKh5pc9jNEix71xTl4CKnsXe2LJe3d90Mm2ouTDbXww64k1GlziaHctMFJ939FVAg7L91MwO19abTtMbRX1khrHO8NbYueKvodhWlB10SAkfp5tkJwBogs02FyXRVzPFINMrIF6vaR0neOpSGOen37p6sDNd1");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (xilmvowopimdulebl != null) {
                     try {
                         xilmvowopimdulebl.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent klbggxdkujlndkchaomkyw = new Intent();
             klbggxdkujlndkchaomkyw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WmzwcgipsorbtzvhActivity");
             startActivity(klbggxdkujlndkchaomkyw);
             String viifuvekszjfgltvo = "88TWl2p8bXlv5xd7TsEy8MxUq9WCkt3QnbhrNfkCM2CtQ3bMEkWormcnPv2";
             viifuvekszjfgltvo.substring(4);
             viifuvekszjfgltvo.replace('y', 'a');
             File file6 = new File(getFilesDir(), "hvyisbmdoqrmqqjo.txt");
             FileReader bmfnkheveeyagsozn = null;
             try {
                 if(file6.exists()) {
                     bmfnkheveeyagsozn = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (bmfnkheveeyagsozn != null) {
                     try {
                         bmfnkheveeyagsozn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             String dsfludcknltcgbaifpbhjw = "F46gsx3RETjEIAgW9jVNWhztEbmsMXSoqW";
             dsfludcknltcgbaifpbhjw.substring(4);
             dsfludcknltcgbaifpbhjw.replace('a', 'o');
             Intent ixftqsxepqohrimdb = new Intent();
             ixftqsxepqohrimdb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WmzwcgipsorbtzvhActivity");
             startActivity(ixftqsxepqohrimdb);
         }
     });
     final View nneuyombsreuiptsbzn = findViewById(R.id.wlesesfoxdxravxwi);
     nneuyombsreuiptsbzn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper eiqicxywvqzflszopcnng = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "skaofgpjexxeqtzzpsmssg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iswbddzbljkrrxvpfhf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase cegcdvzojpwiijdimqtuttxw = eiqicxywvqzflszopcnng.getWritableDatabase();
             try {
                 cegcdvzojpwiijdimqtuttxw.execSQL("INSERT INTO iswbddzbljkrrxvpfhf (name, value) VALUES (?, ?)", new String[]{"ngaislenpuexllldfmyixd", "drkhqlqtqimjmbsnejxgjjl"});
             } finally {
                 cegcdvzojpwiijdimqtuttxw.close();
                 eiqicxywvqzflszopcnng.close();
             }
             Handler dpmesqpwoaotfhiuo = new Handler(Looper.getMainLooper());
             dpmesqpwoaotfhiuo.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     nneuyombsreuiptsbzn.setVisibility(View.VISIBLE);
                 }
             }, 466);
             Intent bwizvfwbiaubpleypd = new Intent();
             bwizvfwbiaubpleypd.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.NcjnhrxxqunocylupxqhjfvxdmActivity");
             startActivity(bwizvfwbiaubpleypd);
             nneuyombsreuiptsbzn.animate()
                     .scaleX(0.9474883f)
                     .scaleY(0.9474883f)
                     .setDuration(36)
                     .start();
             File file4 = new File(getFilesDir(), "amweaadrxpaoaihmkhfjqjwh.txt");
             if(file4.exists()) file4.delete();
             int dipqdmaisudeoyrjyhwxsst = 201;
             Math.max(dipqdmaisudeoyrjyhwxsst, 113);
             String.valueOf(dipqdmaisudeoyrjyhwxsst);
         }
     });
     final View okwkugoozkehrqtkri = findViewById(R.id.tmbnbmhtftyjfifs);
     okwkugoozkehrqtkri.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper cunrnpqgfqedbhbi = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "ihhdjhvkwhrtzlxshplxcndzd.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bbhkiinxcywixhqyzm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase asxedwjullbviohkqjyhuxt = cunrnpqgfqedbhbi.getWritableDatabase();
             try {
                 asxedwjullbviohkqjyhuxt.execSQL("SELECT * FROM bbhkiinxcywixhqyzm WHERE name = ?", new String[]{"lxxclfdoktnxlrvq", "xobzsxnlgulaefzzm"});
             } finally {
                 asxedwjullbviohkqjyhuxt.close();
                 cunrnpqgfqedbhbi.close();
             }
             File file1 = new File(getFilesDir(), "uythjgjapexiipzqsne.txt");
             FileReader oolyqyqtoqxpmmbrdeffuz = null;
             try {
                 if(file1.exists()) {
                     oolyqyqtoqxpmmbrdeffuz = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (oolyqyqtoqxpmmbrdeffuz != null) {
                     try {
                         oolyqyqtoqxpmmbrdeffuz.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             okwkugoozkehrqtkri.animate()
                     .translationX(116f)
                     .translationY(142f)
                     .setDuration(980)
                     .start();
             Intent sqvomzpjzrytaujmdijq = new Intent();
             sqvomzpjzrytaujmdijq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.BwilpfwrixaaqkcwgyvActivity");
             startActivity(sqvomzpjzrytaujmdijq);
             File file4 = new File(getFilesDir(), "wzyjlcwibmaonfwe.txt");
             FileWriter bwrnusmuffjdmjxy = null;
             try {
                 bwrnusmuffjdmjxy = new FileWriter(file4);
                 bwrnusmuffjdmjxy.write("9K29WPQEzwGi2ofSqSIw6bwpEDOLGqufdD8qbtIJXD");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (bwrnusmuffjdmjxy != null) {
                     try {
                         bwrnusmuffjdmjxy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService dsmkzfrdbppngdihnekh = Executors.newScheduledThreadPool(2);
             dsmkzfrdbppngdihnekh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(68);
                 }
             });
             dsmkzfrdbppngdihnekh.shutdown();
             SQLiteOpenHelper kmvsajgdvtopfflxwn = new SQLiteOpenHelper(BjxntfkxvjdgmlmncgzbrrvezActivity.this, "qggcstcaaiurwkgxrzbs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bwpjqrofjignionznldaahlr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase cvffiwywrnkcmuu = kmvsajgdvtopfflxwn.getWritableDatabase();
             try {
                 cvffiwywrnkcmuu.execSQL("SELECT * FROM bwpjqrofjignionznldaahlr WHERE name = ?", new String[]{"yrynrwxewqshucruxp", "dcnijxhojdfbiqjy"});
             } finally {
                 cvffiwywrnkcmuu.close();
                 kmvsajgdvtopfflxwn.close();
             }
             okwkugoozkehrqtkri.animate()
                     .rotation(165f)
                     .setDuration(909)
                     .start();
             Date eedinijecdoumjzgtnzrl = new Date();
             String wtrftgitfngjbngrfdebksqms = String.valueOf(eedinijecdoumjzgtnzrl.getTime());
             System.currentTimeMillis();
         }
     });

eirpjbnltrdlxipk();
Mestlqbrmefuguzzeocv   pcvgzutevhndpeabkoeovfist =     new Mestlqbrmefuguzzeocv();
pcvgzutevhndpeabkoeovfist.yasjljzfnggddkrsnagpsjpds ="dOvfAaBcwLQa8dHrT07Or7jLgoDjmPtnRUtxaa2YNmETta7lJP88316K1xBuq5HggFXSGoMZEV1kXhQ3Rd9KRmAwuaPR8KgmRlp76xJX20XlMGefU";
pcvgzutevhndpeabkoeovfist.drgnnngoooiodmuxuudgkcbuu ="7tvMHQRMsDEHojrik34oIv2TWUaMpOT0Y2vTn4fv3fZgPSg39";
pcvgzutevhndpeabkoeovfist.qhftdtzejhzpgkjajwtbxkwo ="n0DEXYeKODEMZrTg1ADIQZBarpQNbyCBG64e08Z5A1Djd1";
pcvgzutevhndpeabkoeovfist.eswcrxjsmevlvhrnkxvhsju ="AwiYHN1qN8hq3FdaaJzrwKKLW6pqv3E2bQVmVVaCdWhwclm8NPPWo5lBygubaOrroVVzBAbphBu8O0eLXILauaKfgGwPazfNDjVBU9C4E3LE18uVmQ94GG79Auj7jhbcD2n24H3FR632lcn00WH4W5BKIxWWdpBfyaXnfzjg";
pcvgzutevhndpeabkoeovfist.zsirpfuywlkdyvuyhysb ="7u6m9ddSYUiegqn9dl2QxhfPaYOtEHaucCRQOKYFxcQY01f8ErxqAPSaApFIT2AftnAMCxfQwOqeirwwt4vD5AHoCYRGnwjjBpxuXxeG4kTSTM727yX9Xxbj1N2XSTEtyUQb2ruJcMeeWRB";
pcvgzutevhndpeabkoeovfist.omzeixirdgkqyjtnrvk ="mgSFlH7sWr0NzWDxLOjltpo7o99Ke02iHrFj7LWDX2ycvloL9";
pcvgzutevhndpeabkoeovfist.orpwjrbgjilkfwxfcctg ="qk2vNFVSFcJ5JhSjYgc1cLdXETflA";
pcvgzutevhndpeabkoeovfist.bouoyecwxxrkmdbadrv ="S04brmdk7DRCuA1mhkzYJpbnQT6f07sWOyzkhN7NuCyosVV2lzwG1bA203zmojtTMV";
pcvgzutevhndpeabkoeovfist.devesywxvnoykfbqpiharrx ="OQwMmSWKvEWTtYMVc9C0ZEUPResUwA49i2TgKW3dXrudaXVtaGT4p2fQ7b2Roy";
pcvgzutevhndpeabkoeovfist.pllxlcdxclsaqcascbl ="xitxSsi2rR1cQqhFzOSH1JnrJ6qS9f6ujCrjT7Lxxf3kDSAFCRYyZbGfspaZ0iUTkU9XHV08duq6bgq9QUV2IzNQNk5P53OXQaB41DGwhQpMTOMNJiiCTUggJHMQLJhHrGaVUdOWKjxWFx8Qx6KriaCaa24LwffngQruvguGAzis0";
pcvgzutevhndpeabkoeovfist.kxjaemmtoumkbauc ="4ktUrWfzHQcBcLyMKnR6ZdIddKhClRBuqvwu2JGqROxg7APWaEGkdhMJdpKSrskmEN76MkSbExEDnHLgvHOXbnoJk39zbbGP0AKpcoO7kGV0p2UH6StmQ163q9gl28MgZpZ8DK1pBrAv";
pcvgzutevhndpeabkoeovfist.opiaerkkchlwtdx ="a8enfRzEmXAK7oWGBGvrpGTACZEwuG2xEnrUYFJXBJA4v3zkKhK0X";
pcvgzutevhndpeabkoeovfist.yxlrktltleyfrcatugvrb ="FR6fHlcJljcBePW1cVY7iS6tVgrCG4oP6BNYfEbz1HJQyeoX7Q7Clce8gIVBBjkxaRWc";
pcvgzutevhndpeabkoeovfist.qltzmoexpmfhvmaamkoxaxhxc ="D2NCtrus1apM77rO";
pcvgzutevhndpeabkoeovfist.kvwbbvjknxooyuntitcjqs ="aLDCO9mIDxYhIyu8RnDWGLvZqKGM15b7zcBSx75R8p4uTHLgwNA1Jwm2RORzRUJh9Dczj4J2r1j4DhDVOw83BdfGlxhAm7LW4NNnWiABCXFFLjFdD61RFKsL4SAkWumk";
 Toast.makeText(BjxntfkxvjdgmlmncgzbrrvezActivity.this,getString(R.string.eisrfrrgqyuxjsl),Toast.LENGTH_SHORT).show();
    }
 void eirpjbnltrdlxipk() {
     String vbcyhlhshvgnrgmzew = "DFpyWCJJ4pj0cvUmttcrkv8DXlMikA1vVHLvFDAY5idXHgORzWK1gMuC4fCLU4rCuoofb5X0MtV9bBN1bEPEkY3w";
     vbcyhlhshvgnrgmzew = vbcyhlhshvgnrgmzew.substring(1);
     vbcyhlhshvgnrgmzew = vbcyhlhshvgnrgmzew.toUpperCase();
     utpnbueqeamseamn();
}
 void utpnbueqeamseamn() {
     String lezlsrwxraxwkfzasgdcjh = "a6wA1o20ysbPRbkzt4BtkZcxd3lzMOstcHZDqGH44GDIvBmb0nxPd";
     lezlsrwxraxwkfzasgdcjh = lezlsrwxraxwkfzasgdcjh.substring(0);
     lezlsrwxraxwkfzasgdcjh = lezlsrwxraxwkfzasgdcjh.toUpperCase();
     qesrhfcqmicgebfyjy();
}
 void qesrhfcqmicgebfyjy() {
     String vbbuivbjwtbpvlqqdophx = "MDtOYC89oJ7gErDgANH3HJZE24";
     vbbuivbjwtbpvlqqdophx = vbbuivbjwtbpvlqqdophx.substring(1);
     vbbuivbjwtbpvlqqdophx = vbbuivbjwtbpvlqqdophx.toUpperCase();
     ygjetncmmhbxwhcbkgoewu();
}
 void ygjetncmmhbxwhcbkgoewu() {
     long ejfsrqvmljshwmfntyomlo = System.currentTimeMillis();
     Date date = new Date(ejfsrqvmljshwmfntyomlo);
     String time = date.toString();
}}