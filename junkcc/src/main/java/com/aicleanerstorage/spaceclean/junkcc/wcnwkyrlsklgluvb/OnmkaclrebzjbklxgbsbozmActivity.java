package com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb;

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
import android.widget.CheckBox;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class OnmkaclrebzjbklxgbsbozmActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nmkaclrebzjbklxgbsbozm);
     final View wvgmoxvdgmkiosutdhdrntb = findViewById(R.id.uhwkqnvquwugzpbfovpjcvn);
     wvgmoxvdgmkiosutdhdrntb.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent rrvwcovwjodavvjtrfxpacvzw = new Intent();
             rrvwcovwjodavvjtrfxpacvzw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.GaorpmsaoajnjbbgizgtziActivity");
             startActivity(rrvwcovwjodavvjtrfxpacvzw);
             SQLiteOpenHelper lmvnwvoccgooxseelymivt = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "bcmhwbiogyzoihbnzpcstrkvx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "craxrpsxbdqcaagkgwkvcxr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nrwrtnemooeasvqi = lmvnwvoccgooxseelymivt.getWritableDatabase();
             try {
                 nrwrtnemooeasvqi.execSQL("INSERT INTO craxrpsxbdqcaagkgwkvcxr (name, value) VALUES (?, ?)", new String[]{"inlnwetjjsprvzsulw", "gbclaoarlwjdmbkgbtvlxmu"});
             } finally {
                 nrwrtnemooeasvqi.close();
                 lmvnwvoccgooxseelymivt.close();
             }
             Handler fwmajavptisvousw = new Handler(Looper.getMainLooper());
             fwmajavptisvousw.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     wvgmoxvdgmkiosutdhdrntb.setVisibility(View.VISIBLE);
                 }
             }, 716);
             Intent dqwfinuyydbwzney = new Intent();
             dqwfinuyydbwzney.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.GaorpmsaoajnjbbgizgtziActivity");
             startActivity(dqwfinuyydbwzney);
         }
     });
     final View tqqvoernqlhbnhizjhdmkijtl = findViewById(R.id.erynbhdeqrnwtcgohgjtwpyn);
     tqqvoernqlhbnhizjhdmkijtl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent esxrjrgpubfspqb = new Intent();
             esxrjrgpubfspqb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.TdqxxnarwzslaclsykysouActivity");
             startActivity(esxrjrgpubfspqb);
             SQLiteOpenHelper ffwzrjcvmixuyqcryiazd = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "vylhhwjmbmotmvzjws.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iftftlvgwsabnayichcioh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ibxxjuwnptpbsndrajbpbdrmj = ffwzrjcvmixuyqcryiazd.getWritableDatabase();
             try {
                 ibxxjuwnptpbsndrajbpbdrmj.execSQL("UPDATE iftftlvgwsabnayichcioh SET value = ? WHERE name = ?", new String[]{"ekskixnddbtnyriwu", "tzuinelnyadbwms"});
             } finally {
                 ibxxjuwnptpbsndrajbpbdrmj.close();
                 ffwzrjcvmixuyqcryiazd.close();
             }
             tqqvoernqlhbnhizjhdmkijtl.animate()
                     .scaleX(0.41822183f)
                     .scaleY(0.41822183f)
                     .setDuration(666)
                     .start();
             Handler mnqotjhjbxlditqa = new Handler(Looper.getMainLooper());
             mnqotjhjbxlditqa.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     tqqvoernqlhbnhizjhdmkijtl.setVisibility(View.VISIBLE);
                 }
             }, 135);
             File file4 = new File(getFilesDir(), "dbtewjixebrtgfshn.txt");
             if(file4.exists()) file4.delete();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(OnmkaclrebzjbklxgbsbozmActivity.this, "mfmhchyjasdwmirhdkeqwgwtc", Toast.LENGTH_SHORT).show();
                 }
             });
             Date dxqbwzjqvffplmwbpzsncj = new Date();
             String myaswlttulduvfbjghmf = String.valueOf(dxqbwzjqvffplmwbpzsncj.getTime());
             System.currentTimeMillis();
         }
     });
     final View wsbmrcwchuikeuvbyaaor = findViewById(R.id.yauyqqwigfiedsbnudatlede);
     wsbmrcwchuikeuvbyaaor.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             wsbmrcwchuikeuvbyaaor.animate()
                     .rotation(213f)
                     .setDuration(673)
                     .start();
             int dtgaksjkvfdceykodslujpuq = 564;
             Math.max(dtgaksjkvfdceykodslujpuq, 653);
             String.valueOf(dtgaksjkvfdceykodslujpuq);
             Intent howeljidhpzzhcseoald = new Intent();
             howeljidhpzzhcseoald.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.RljynbthwqdcdlzeftActivity");
             startActivity(howeljidhpzzhcseoald);
             Handler sresqsxqvcgxssmtjyrdata = new Handler(Looper.getMainLooper());
             sresqsxqvcgxssmtjyrdata.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     wsbmrcwchuikeuvbyaaor.setVisibility(View.VISIBLE);
                 }
             }, 151);
             String azanwoohffjppqty = "y9RAHAxhBzS72BBNnEdOz0ZfpRRs61sEAKUiCWudxocJ0JxsM9Sh3pgWPtdu2rWgFzhd0ODjOeKHKn7dSkCeU17cNLJoEwfVFDw25gN";
             azanwoohffjppqty.substring(4);
             azanwoohffjppqty.replace('u', 'j');
             SQLiteOpenHelper zadgdhiucftgginxbudhwevaq = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "lhqzlswybqrwxttjtdpfmi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "waiiyvwwsiibhziebwz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rdnyerzomgujjbtdvvcwfp = zadgdhiucftgginxbudhwevaq.getWritableDatabase();
             try {
                 rdnyerzomgujjbtdvvcwfp.execSQL("UPDATE waiiyvwwsiibhziebwz SET value = ? WHERE name = ?", new String[]{"aexwxhhbdyueilgzuxus", "uwyoahiqeiyffpy"});
             } finally {
                 rdnyerzomgujjbtdvvcwfp.close();
                 zadgdhiucftgginxbudhwevaq.close();
             }
         }
     });
     final View bypgyuhopvuugbjx = findViewById(R.id.ujwttptldatavogskpbo);
     bypgyuhopvuugbjx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService tnaugyioehxyyihjvkhk = Executors.newScheduledThreadPool(2);
             tnaugyioehxyyihjvkhk.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(33);
                 }
             });
             tnaugyioehxyyihjvkhk.shutdown();
             File file1 = new File(getFilesDir(), "sbcexvrccykjenqz.txt");
             ScheduledExecutorService hprjbdcuzwgrijgixwuzacu = Executors.newScheduledThreadPool(3);
             hprjbdcuzwgrijgixwuzacu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(25);
                 }
             });
             hprjbdcuzwgrijgixwuzacu.shutdown();
             int rkughxedqzihgefhbaqrmgtnt = 217;
             Math.max(rkughxedqzihgefhbaqrmgtnt, 528);
             String.valueOf(rkughxedqzihgefhbaqrmgtnt);
             Intent rxfsnlzrlcnectq = new Intent();
             rxfsnlzrlcnectq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.AmlthoxvzblnqynfwgdznqavActivity");
             startActivity(rxfsnlzrlcnectq);
         }
     });
     final View bqppzotkijrqbegynrl = findViewById(R.id.woysobhvpuebrnstd);
     bqppzotkijrqbegynrl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "pgyfesktjyvhmpuuebblrddm.txt");
             FileWriter hfauobjvjqnuxoavxoc = null;
             try {
                 hfauobjvjqnuxoavxoc = new FileWriter(file0);
                 hfauobjvjqnuxoavxoc.write("KewSCFcL4tEaydEiJd4KhwOnZrJX5CKHLDaDIsC5zO4Q8DKgP0U4tkTmjdoAUXQqLglEAdvxsuLJybltcEhbRKVguhlGgSTF63GLz5jaki3R3KlmL8fvwe3pW2QR83I91a9PvctUpMadM3x3kfrcF170uOyDhvgeWI2ALMH1jMtPDW6t83u");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (hfauobjvjqnuxoavxoc != null) {
                     try {
                         hfauobjvjqnuxoavxoc.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             String ejaljzyrlcydxthk = "YUauEXw22YJjNveFDQdjret8p2WEeE5OQg5vy92wveBGE6z3MUwTKFsYP9gEoS5rTo0jPOuNs4z2y8NB3Ng5wIm5VuJH9L25mfrj5ZeC8B2Kk2pHZ9pZvaUHqbPuOGLjRlqAv072UrY2B1EPHkSnqA91vnSpXJLBNLCQjesvNcsMHAEsCkjqvP6zZYFaAAaxfQrY0w";
             ejaljzyrlcydxthk.substring(4);
             ejaljzyrlcydxthk.replace('n', 'm');
             ScheduledExecutorService fxjshxywrwqvjkumvgvaay = Executors.newScheduledThreadPool(3);
             fxjshxywrwqvjkumvgvaay.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(41);
                 }
             });
             fxjshxywrwqvjkumvgvaay.shutdown();
             bqppzotkijrqbegynrl.animate()
                     .alpha(0.26740932f)
                     .setDuration(734)
                     .start();
             File file4 = new File(getFilesDir(), "gkrgpqagjfnnvxfpvsirtauqb.txt");
             FileWriter cguhjfakchrxesxblxmyav = null;
             try {
                 cguhjfakchrxesxblxmyav = new FileWriter(file4);
                 cguhjfakchrxesxblxmyav.write("K41MFMl0m1PHykk0MTvC8WhyBECffzeDZafElps8GBvSl97kf0aVBWf3qxoPbMTm4QDUaFPE60CiOA");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (cguhjfakchrxesxblxmyav != null) {
                     try {
                         cguhjfakchrxesxblxmyav.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService ozzmmaihwlmjyucavghsyka = Executors.newScheduledThreadPool(3);
             ozzmmaihwlmjyucavghsyka.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(65);
                 }
             });
             ozzmmaihwlmjyucavghsyka.shutdown();
         }
     });
     final View cdszprlctbxyracgiwxtpwjz = findViewById(R.id.rjrzeutqfcfffufcjsku);
     cdszprlctbxyracgiwxtpwjz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler lfhviuwdvwvvoarikjyh = new Handler(Looper.getMainLooper());
             lfhviuwdvwvvoarikjyh.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     cdszprlctbxyracgiwxtpwjz.setVisibility(View.VISIBLE);
                 }
             }, 252);
             Intent trzyuduipoeoqujjpkerrwr = new Intent();
             trzyuduipoeoqujjpkerrwr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.PshgymqhpfujrtmeoipeqaActivity");
             startActivity(trzyuduipoeoqujjpkerrwr);
         }
     });
     final View jtptgysjgbvvauieb = findViewById(R.id.qqqifmgeiswiyaabvnuoihbq);
     jtptgysjgbvvauieb.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int pgvmqntwlddyvhnd = 723;
             Math.max(pgvmqntwlddyvhnd, 36);
             String.valueOf(pgvmqntwlddyvhnd);
             Intent nucznksjtveqjpcnyvkq = new Intent();
             nucznksjtveqjpcnyvkq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.EfgyzolaqtnivaugtpesActivity");
             startActivity(nucznksjtveqjpcnyvkq);
             int tufhfcrmikzkkpbdlf = 183;
             Math.max(tufhfcrmikzkkpbdlf, 476);
             String.valueOf(tufhfcrmikzkkpbdlf);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(OnmkaclrebzjbklxgbsbozmActivity.this, "bslwngwpssycuqdvx", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper ataphmhsavjznathwbkvz = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "qursiwzbtrbnepvbfl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cpxgtvqjmpwiqjmfkwqkoi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase quenlviyhujigcfc = ataphmhsavjznathwbkvz.getWritableDatabase();
             try {
                 quenlviyhujigcfc.execSQL("UPDATE cpxgtvqjmpwiqjmfkwqkoi SET value = ? WHERE name = ?", new String[]{"niybbvdtvouulhfcui", "udtwazmfdserwnwnhc"});
             } finally {
                 quenlviyhujigcfc.close();
                 ataphmhsavjznathwbkvz.close();
             }
         }
     });
     final View ziqjmhmsggkawvnnprnko = findViewById(R.id.mgajlxhbtyhyfuvj);
     ziqjmhmsggkawvnnprnko.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "cmkwwdatbwfvrfnoonjtri.txt");
             if(file0.exists()) file0.delete();
             int uyesrizccylqgnnlmboiuaaxo = 722;
             Math.max(uyesrizccylqgnnlmboiuaaxo, 436);
             String.valueOf(uyesrizccylqgnnlmboiuaaxo);
             SQLiteOpenHelper zfjlpouiunyropuluu = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "vqcjcyldebyrtzgm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rpdsejikuasuxesgdmmd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rveugfpjscyryxbdngpzci = zfjlpouiunyropuluu.getWritableDatabase();
             try {
                 rveugfpjscyryxbdngpzci.execSQL("DELETE FROM rpdsejikuasuxesgdmmd WHERE name = ?", new String[]{"dclothqqsbaznrcx", "oimttgdctwuwhalxy"});
             } finally {
                 rveugfpjscyryxbdngpzci.close();
                 zfjlpouiunyropuluu.close();
             }
             Intent skevujkfsldppkwscvwltvuoe = new Intent();
             skevujkfsldppkwscvwltvuoe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.UzjelrvwisoqcayoiActivity");
             startActivity(skevujkfsldppkwscvwltvuoe);
             ScheduledExecutorService gbktjmlskknaiwgdyt = Executors.newScheduledThreadPool(1);
             gbktjmlskknaiwgdyt.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(30);
                 }
             });
             gbktjmlskknaiwgdyt.shutdown();
             ziqjmhmsggkawvnnprnko.animate()
                     .translationX(91f)
                     .translationY(163f)
                     .setDuration(491)
                     .start();
             SQLiteOpenHelper hkbjymopzxovhaekwwkurayz = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "tamjosjrwgeruqgphbrs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ctgkmlbyfkgecvoivojvc"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ignvvvdzhdecdaucfcvloraf = hkbjymopzxovhaekwwkurayz.getWritableDatabase();
             try {
                 ignvvvdzhdecdaucfcvloraf.execSQL("SELECT * FROM ctgkmlbyfkgecvoivojvc WHERE name = ?", new String[]{"qixpmhchdkintpqq", "sgcpdvvgioztyjhfzipkvzleq"});
             } finally {
                 ignvvvdzhdecdaucfcvloraf.close();
                 hkbjymopzxovhaekwwkurayz.close();
             }
             ziqjmhmsggkawvnnprnko.animate()
                     .scaleX(1.8699441f)
                     .scaleY(1.8699441f)
                     .setDuration(874)
                     .start();
             SQLiteOpenHelper ztrovhhrvijlodwqtholrb = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "zidevvbbgphvbvzn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ohkwlxbmoxcuhngcgsyb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xwbnqmdsvhkxvzldbycy = ztrovhhrvijlodwqtholrb.getWritableDatabase();
             try {
                 xwbnqmdsvhkxvzldbycy.execSQL("DELETE FROM ohkwlxbmoxcuhngcgsyb WHERE name = ?", new String[]{"llxystqmmidvsciorxebg", "ozmrookkuorgswqfntinn"});
             } finally {
                 xwbnqmdsvhkxvzldbycy.close();
                 ztrovhhrvijlodwqtholrb.close();
             }
         }
     });
     final View dfvufybewbdwpkgcbdnrfmpm = findViewById(R.id.egpompogsexahqgsvozgmspce);
     dfvufybewbdwpkgcbdnrfmpm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent xudpwmyiksbutmep = new Intent();
             xudpwmyiksbutmep.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.OagymcyhwzjummbohmvActivity");
             startActivity(xudpwmyiksbutmep);
             dfvufybewbdwpkgcbdnrfmpm.animate()
                     .rotation(353f)
                     .setDuration(740)
                     .start();
             SQLiteOpenHelper yrouwkxsohedfahq = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "wsqmzhwbizzomibjkliklhqt.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qunigjuolcncrgdm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wlhevikrflgqwwmzgtsrlgj = yrouwkxsohedfahq.getWritableDatabase();
             try {
                 wlhevikrflgqwwmzgtsrlgj.execSQL("INSERT INTO qunigjuolcncrgdm (name, value) VALUES (?, ?)", new String[]{"vtmwxysgtcyaqdwdut", "qbnhzdzlnyauqqaltjnrsff"});
             } finally {
                 wlhevikrflgqwwmzgtsrlgj.close();
                 yrouwkxsohedfahq.close();
             }
             Date gyuuluwvkawicibqzsdbmfl = new Date();
             String brmxdegjqodnrpjdqh = String.valueOf(gyuuluwvkawicibqzsdbmfl.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService iucgxsavdzccheyceszsksd = Executors.newScheduledThreadPool(1);
             iucgxsavdzccheyceszsksd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(75);
                 }
             });
             iucgxsavdzccheyceszsksd.shutdown();
             dfvufybewbdwpkgcbdnrfmpm.animate()
                     .translationX(184f)
                     .translationY(14f)
                     .setDuration(25)
                     .start();
             File file6 = new File(getFilesDir(), "lppmzqbsdcaazdqs.txt");
             FileReader dcqzkbgaqvpsxkvik = null;
             try {
                 if(file6.exists()) {
                     dcqzkbgaqvpsxkvik = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (dcqzkbgaqvpsxkvik != null) {
                     try {
                         dcqzkbgaqvpsxkvik.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View dmbtboupjwnitzgt = findViewById(R.id.luixsvngrozjapkwwbrirccdl);
     dmbtboupjwnitzgt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "zmooalwdgirvqfqcx.txt");
             FileReader pxzwuekemamadgw = null;
             try {
                 if(file0.exists()) {
                     pxzwuekemamadgw = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (pxzwuekemamadgw != null) {
                     try {
                         pxzwuekemamadgw.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             dmbtboupjwnitzgt.animate()
                     .translationX(15f)
                     .translationY(113f)
                     .setDuration(649)
                     .start();
             Intent djwrrncaxxjvwbujefrczs = new Intent();
             djwrrncaxxjvwbujefrczs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.SdbijkajabvkjksowvtlvvxiActivity");
             startActivity(djwrrncaxxjvwbujefrczs);
             SQLiteOpenHelper gxjiibhzabjvygblrujlle = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "svffbymmlgbsuhgh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "joblmzytgsicuxhko"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase piskeqfiufmuixzzu = gxjiibhzabjvygblrujlle.getWritableDatabase();
             try {
                 piskeqfiufmuixzzu.execSQL("DELETE FROM joblmzytgsicuxhko WHERE name = ?", new String[]{"sinweseuncrrhqrwk", "gfxvlnkekmvxxugpd"});
             } finally {
                 piskeqfiufmuixzzu.close();
                 gxjiibhzabjvygblrujlle.close();
             }
             Handler oalburssyabxaxs = new Handler(Looper.getMainLooper());
             oalburssyabxaxs.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     dmbtboupjwnitzgt.setVisibility(View.VISIBLE);
                 }
             }, 422);
         }
     });
     final View gbuxmklejcginrnwdtzpose = findViewById(R.id.oojruzgurxdpxcpacfzndkd);
     gbuxmklejcginrnwdtzpose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper shhvaehluxpukbfughjsbeytv = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "gozadwgagkybcxenzioybvz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ancuktvgqylfrjpqdpfuep"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dhifkxcmktxvnzrvbwygbcs = shhvaehluxpukbfughjsbeytv.getWritableDatabase();
             try {
                 dhifkxcmktxvnzrvbwygbcs.execSQL("DELETE FROM ancuktvgqylfrjpqdpfuep WHERE name = ?", new String[]{"wzorpqsljacwatnbxhm", "rbyjuuedpzhxirkuhijo"});
             } finally {
                 dhifkxcmktxvnzrvbwygbcs.close();
                 shhvaehluxpukbfughjsbeytv.close();
             }
             Handler ksdszrybhfbqyomt = new Handler(Looper.getMainLooper());
             ksdszrybhfbqyomt.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     gbuxmklejcginrnwdtzpose.setVisibility(View.VISIBLE);
                 }
             }, 876);
             Date splttorwbsddpihcgayutz = new Date();
             String gbqanhvdghusdjeerhmptgk = String.valueOf(splttorwbsddpihcgayutz.getTime());
             System.currentTimeMillis();
             gbuxmklejcginrnwdtzpose.animate()
                     .translationX(149f)
                     .translationY(93f)
                     .setDuration(743)
                     .start();
             Handler awxafvnjugkkffsoxoj = new Handler(Looper.getMainLooper());
             awxafvnjugkkffsoxoj.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     gbuxmklejcginrnwdtzpose.setVisibility(View.VISIBLE);
                 }
             }, 695);
             SQLiteOpenHelper qndbsbzdiaiogtfzdebdy = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "mvefvbkbdnnukvcy.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iygatakwvfpgihxxdsupedmkn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gslgrgwyijcvveqfsshnjcqwn = qndbsbzdiaiogtfzdebdy.getWritableDatabase();
             try {
                 gslgrgwyijcvveqfsshnjcqwn.execSQL("UPDATE iygatakwvfpgihxxdsupedmkn SET value = ? WHERE name = ?", new String[]{"xdrknekezofdzdlf", "hhmrynylvhygwulurc"});
             } finally {
                 gslgrgwyijcvveqfsshnjcqwn.close();
                 qndbsbzdiaiogtfzdebdy.close();
             }
             int nmlxecgqlvyldgpghke = 540;
             Math.max(nmlxecgqlvyldgpghke, 424);
             String.valueOf(nmlxecgqlvyldgpghke);
         }
     });
     final View mqtvskovmotkegfkvk = findViewById(R.id.jjcmnltqheuizabxeuoqqa);
     mqtvskovmotkegfkvk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "tnteysburyyjxtrgveu.txt");
             FileWriter ixbytttprjsiwwyqtjtjzr = null;
             try {
                 ixbytttprjsiwwyqtjtjzr = new FileWriter(file0);
                 ixbytttprjsiwwyqtjtjzr.write("iapsjzSmsbQrCh9JgFnmrvIpzRHSsNoqQxUnnr0rOFl94O6KPZlrTLQMgr2ATWjk5elzvHyZgGjmjIdPIXe4flnSs7OxUL43zdSUFcaAs876MENa1RFntHapvZSpiQeqmeOqoMIqYbCun");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ixbytttprjsiwwyqtjtjzr != null) {
                     try {
                         ixbytttprjsiwwyqtjtjzr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper vtqrocxeldilildbm = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "ijnkiefhkgwwxux.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "esxztolqohlsxkijm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nuzjmlwmqpvqagrf = vtqrocxeldilildbm.getWritableDatabase();
             try {
                 nuzjmlwmqpvqagrf.execSQL("UPDATE esxztolqohlsxkijm SET value = ? WHERE name = ?", new String[]{"mgibkahwqcdzenkswwnbtody", "enubtseeyzhmegni"});
             } finally {
                 nuzjmlwmqpvqagrf.close();
                 vtqrocxeldilildbm.close();
             }
             File file2 = new File(getFilesDir(), "lzezbgecfmtcbzklz.txt");
             ScheduledExecutorService lqaxevxjejnnnikdvwtxydzg = Executors.newScheduledThreadPool(3);
             lqaxevxjejnnnikdvwtxydzg.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(33);
                 }
             });
             lqaxevxjejnnnikdvwtxydzg.shutdown();
             mqtvskovmotkegfkvk.animate()
                     .translationX(20f)
                     .translationY(3f)
                     .setDuration(541)
                     .start();
             File file5 = new File(getFilesDir(), "gwvvjxhqkkxrvyfjoaono.txt");
             FileWriter oaseskuovajzambbscjworyg = null;
             try {
                 oaseskuovajzambbscjworyg = new FileWriter(file5);
                 oaseskuovajzambbscjworyg.write("D8MuxCFTXokqXzJPNjl5xrxAHgE7BjRHbfin3aoNedK9bpmLvMzuheycIfrq0z6J8yXQWydBZVfeRINXPoaIK3VgvqA8mO4WsDZhQLfkdEJc7Ld9FWSrW4h7w0BW3Jld0S56myiaTZAVehfUBzG6CgzJLyDt");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (oaseskuovajzambbscjworyg != null) {
                     try {
                         oaseskuovajzambbscjworyg.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent uermfypodmhdwkbebqcfgah = new Intent();
             uermfypodmhdwkbebqcfgah.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.OvjrlenkrluxcqcbActivity");
             startActivity(uermfypodmhdwkbebqcfgah);
             ScheduledExecutorService jzkwkljwzkwxjjgitolvqvu = Executors.newScheduledThreadPool(1);
             jzkwkljwzkwxjjgitolvqvu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(0);
                 }
             });
             jzkwkljwzkwxjjgitolvqvu.shutdown();
             SQLiteOpenHelper dkldcppoaeylvzpgqyv = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "ooklmqqsbhoofzbavmxcr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "loybwddedgadkhdozjvpew"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase blnkeetkphltqjveiiepezkw = dkldcppoaeylvzpgqyv.getWritableDatabase();
             try {
                 blnkeetkphltqjveiiepezkw.execSQL("UPDATE loybwddedgadkhdozjvpew SET value = ? WHERE name = ?", new String[]{"jwesoiymvfckonzkrfomminiu", "lfhawzcwetnyeekmn"});
             } finally {
                 blnkeetkphltqjveiiepezkw.close();
                 dkldcppoaeylvzpgqyv.close();
             }
         }
     });
     final View ymyfjmatslfziccdbnk = findViewById(R.id.rbrjinzpvfoehclxvqpxsqea);
     ymyfjmatslfziccdbnk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date nmgxzvslacgtsqur = new Date();
             String cjgzafspoeephigqycorbpo = String.valueOf(nmgxzvslacgtsqur.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "zerusfddlygfzesiu.txt");
             ScheduledExecutorService wqgarjkucdtsnmbdfsti = Executors.newScheduledThreadPool(3);
             wqgarjkucdtsnmbdfsti.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(3);
                 }
             });
             wqgarjkucdtsnmbdfsti.shutdown();
         }
     });
     final View lulauojziabqobucdi = findViewById(R.id.zcophoyvivfaobjsfxnagbd);
     lulauojziabqobucdi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper zilhpevhupcmqmkobjz = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "kmxaxyhhmyjvcxqxrpuj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jknonzlewvrcbfnchnymsztjo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase swlrijoisrakvmvxrlqeh = zilhpevhupcmqmkobjz.getWritableDatabase();
             try {
                 swlrijoisrakvmvxrlqeh.execSQL("SELECT * FROM jknonzlewvrcbfnchnymsztjo WHERE name = ?", new String[]{"puczyqwefihgegxw", "rlruxmboepeepvpnzgkzyfl"});
             } finally {
                 swlrijoisrakvmvxrlqeh.close();
                 zilhpevhupcmqmkobjz.close();
             }
             File file1 = new File(getFilesDir(), "wibwezlmjbkvnjkrbvclado.txt");
             ScheduledExecutorService bblljjtmxleydxsmppxafn = Executors.newScheduledThreadPool(2);
             bblljjtmxleydxsmppxafn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(3);
                 }
             });
             bblljjtmxleydxsmppxafn.shutdown();
             SQLiteOpenHelper zipporjhrimbzqiujdbgmjkg = new SQLiteOpenHelper(OnmkaclrebzjbklxgbsbozmActivity.this, "vpigkmntlanrpqyoc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wrvribglgepcfthlrexbv"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zsekyansswojzcahx = zipporjhrimbzqiujdbgmjkg.getWritableDatabase();
             try {
                 zsekyansswojzcahx.execSQL("INSERT INTO wrvribglgepcfthlrexbv (name, value) VALUES (?, ?)", new String[]{"jjgejeuupjlmkxdcek", "pztpaaritdfdoghteeo"});
             } finally {
                 zsekyansswojzcahx.close();
                 zipporjhrimbzqiujdbgmjkg.close();
             }
             lulauojziabqobucdi.animate()
                     .alpha(0.22950286f)
                     .setDuration(543)
                     .start();
         }
     });

mxitxcreyretnmh();
Gqnwjtdoqjrplolzqsccqvxc   iqmsujsgaolaulxpixirifu =     new Gqnwjtdoqjrplolzqsccqvxc();
iqmsujsgaolaulxpixirifu.gxhbpiqxecfaxkxlhti ="tLLttOSPTCv29ZCy5Q49RdCktEREeN2JGNBLRba0l1UOwXqatXrtfD0JvJb5blm5CgehKgok5Q9m";
iqmsujsgaolaulxpixirifu.mpltkedwkgplmdskd ="DctuZsGP3RVNIuYifrXh3V1kxWRqpnQz4kezlRfDuSZpSwJDPawbPGebZ";
iqmsujsgaolaulxpixirifu.redtlrsvfzcjadaebbhpurmzk ="8bnYAXHFJbcDrYnIfxRdtug3O9ie2h7Ih5Gb6vaVu5KenRCQB2hSOvLzXxItxS0EmwwYYUYsQAZHDn2qVVOyXzIrSqDlJJA6RHcFN0UjjEH";
iqmsujsgaolaulxpixirifu.gfnyqilewcftjonpsvk ="wigpP27DHbcYAk7VuMQx8gG955OxCvFsRAQLOBcCYTM0PNZZX";
iqmsujsgaolaulxpixirifu.nsqwrpruzshbjmva ="nI6Py4R0nfehwhbTHfWjLndN2Aius88CAc7x7fw2I0fEVjJdUHp2FqdqjVkiUQWap4Dg51iKiadHwUMuSifWECVzYm51gniEIiqJOmSWT6tad";
iqmsujsgaolaulxpixirifu.xlvdrxhosqxjskjlz ="BjNjqw5Zqlg66zhPVnjoyzoxLg0YGjaT4ApFmZWUlotUHneyS8qNF0Q5ORg5elTrGmd5HjZl9ovif5RW1XI5eG7i";
iqmsujsgaolaulxpixirifu.hykgurkkxefkvojtokvvgg ="WnBAXzg6oL2EUSKGua2C4EQdN8sd5MCw6y35P3OoeLhpSBH51DMCUlUjN2YkW3PtLflL7fKNTJb8O3ivpwiadDN2FdyXpjvntRiBS7qTJ0FmtEjwenPQwgppj9kO6Fgr3OMEw97qr3Oqt933nDO2GoAj07yWbpcSyYmc5nBOYVpKeG88hZWk3WpKxOJBSneHqFF1GS";
iqmsujsgaolaulxpixirifu.rqpqksxtwppmpejfe ="TuqI13NHlEirOpMsDMtszffhHjOa1SOPynho13JQPZXQqzyQNAFoczDZOUuq6X7wxoLRJwKn1pyZ3NbsyUG0UE4DdG6mj3mLlEEuT2Ot5M8b5Nzq4ioxC0XRpvCv2FxgZRvfG0dc1GY0pysnwmMsiMuR98ccHF93YilpZ6kFlSK3KVzIm51";
iqmsujsgaolaulxpixirifu.dheplevhwyrsklsy ="CIcQwKxesCGolfrz2KFVKBUrVIifNSco2EKisjh9fT6Ny1PCfR0IPr17aR5AEfAUgidy0HATfRbaIlDhvJARkV9X2RMuDylTHqNSrdSj4PZ5AFfrUHDzluUNXuIQJe4lpcIgue4ZQqncRnwZLgJctIK67EiUZ8ONmfOqiy";
iqmsujsgaolaulxpixirifu.vebkcuwbohraigvippdrojw ="KXnpLgmiyO6JeIv1FWtLYwlvtQxiulcyLrLWj39vVbfpnWwfYXDny1Xky3gTayfj6pR8t5IeNsnBd7KWl8ZHtjAkvtmIqStn0J9XPryyR03gNYnM5Wee1lPNZQY1CeaLzXiO2GAeWYoIAPyxjzXeTt154HVL53Bt6";
iqmsujsgaolaulxpixirifu.oviodwbsrmxasvw ="c7Wpkedqo7QtwZuVV5V6tjiGFjrUpC2vzQUKO20QL3SfXQcRqrQlTfubxL956rkt6hk6VOccC91A3A67WcGvV0JT";
iqmsujsgaolaulxpixirifu.xnaayxeuelwedrhqoqbov ="mN1kNxaat5zkBz6f1r4x7kCafvhsoeOpPGDYIkoF6jJPPwvafvEWJfcWTrJsz0KKd0IneOhJ48oulJH8ubOIJl7E648UCHRNeAZgh1ZHeKo2FfKkIPeb7DJTDmFrmbdw23wJ4nwzYnoOqJasRbqQ7eH5NNNt5fwvYtA2lvVXhDMDzK3gaGwyUlUCV3rJ";
iqmsujsgaolaulxpixirifu.robgswbquuzfpdaohbjebrr ="02VuLQq8Buw7CFBinwNXWpa3vGUPS7P4tuqw2ZEqtrPzKKWS3D6AuR69ft3RuIe72cCd80Hs4wUmVv7spqNsIXk2Qd";
iqmsujsgaolaulxpixirifu.ixvpqrjoyndqbxlntegmfpu ="Ld7GSE8C39E3DEnLPCfDnwDGXr";
iqmsujsgaolaulxpixirifu.ogsadtgnqdswiszpwwbicfu ="fpDUdiLBACqovsKfolor3Gv85HbuK51pQ9xTlOv9VqzqnGMkzfFUy1Byb7x6XEvj3AWG3btthfLNm5Bk3uU4IZghZgD5pXr2ePKVJYAMrNoDG4HJOs0z8OtLI";
iqmsujsgaolaulxpixirifu.dekzvwveutxfauijxogiqtsxv ="DnhvjZL10gM3erPAhq6S4WGm8vXKavdyy09VTZOwynx4evL79URnL9Urp3tWSnQjQspThJpXiJsO";
 Toast.makeText(OnmkaclrebzjbklxgbsbozmActivity.this,getString(R.string.dbcopnlmrptclypuplkn),Toast.LENGTH_SHORT).show();
    }
 void mxitxcreyretnmh() {
     int npsabfjwmprzbrwm = 934;
     double result = Math.sqrt(npsabfjwmprzbrwm);
     result = Math.pow(result, 2);
     czjrxbqdpyyzsskkdta();
}
 void czjrxbqdpyyzsskkdta() {
     String wbdikjoofbqzmntsxe = "HzuhrXMEwEllxZpp9mynXRUsAekHDqka5v8Gq0pXrsJULzFozUvc";
     wbdikjoofbqzmntsxe = wbdikjoofbqzmntsxe.substring(0);
     wbdikjoofbqzmntsxe = wbdikjoofbqzmntsxe.toUpperCase();
     gdxirpwktfdoaffwqbc();
}
 void gdxirpwktfdoaffwqbc() {
     CheckBox hxwxbtticgsoijhrytzuqgzx = new CheckBox(OnmkaclrebzjbklxgbsbozmActivity.this);
     hxwxbtticgsoijhrytzuqgzx.setVisibility(View.VISIBLE);
     hxwxbtticgsoijhrytzuqgzx.setEnabled(true);
     ehgudgjdxytykqryjpsb();
}
 void ehgudgjdxytykqryjpsb() {
     CheckBox nuudmdvozcklokou = new CheckBox(OnmkaclrebzjbklxgbsbozmActivity.this);
     nuudmdvozcklokou.setVisibility(View.VISIBLE);
     nuudmdvozcklokou.setEnabled(false);
}}