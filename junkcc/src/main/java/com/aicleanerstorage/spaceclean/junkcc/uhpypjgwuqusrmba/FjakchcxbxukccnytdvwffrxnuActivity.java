package com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba;

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
import android.widget.ViewFlipper;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class FjakchcxbxukccnytdvwffrxnuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_jakchcxbxukccnytdvwffrxnu);
     final View dufyqgnegkqcjkrrawl = findViewById(R.id.ludsfyeerxjdakdzueeudbu);
     dufyqgnegkqcjkrrawl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "yziiljgdtfgapqlqfubncj.txt");
             FileReader jpurmzeidqjqjbyh = null;
             try {
                 if(file0.exists()) {
                     jpurmzeidqjqjbyh = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jpurmzeidqjqjbyh != null) {
                     try {
                         jpurmzeidqjqjbyh.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             dufyqgnegkqcjkrrawl.animate()
                     .rotation(49f)
                     .setDuration(167)
                     .start();
             ScheduledExecutorService tsrclpvudqddbasuxijvms = Executors.newScheduledThreadPool(3);
             tsrclpvudqddbasuxijvms.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(5);
                 }
             });
             tsrclpvudqddbasuxijvms.shutdown();
             File file3 = new File(getFilesDir(), "lipmnutxscmsfjobdbzokrfag.txt");
             FileWriter wqvxxdnakwfnxawtt = null;
             try {
                 wqvxxdnakwfnxawtt = new FileWriter(file3);
                 wqvxxdnakwfnxawtt.write("T7HvONool9PmsgEslOIK7aE5uQd2YTjnBDnJGRQFtGI8O3NUX5OEZOKuyCQ86TMFfZmfLpQJlXtSLaxld1LnFWaUsjRGTv58gHMybCqMKM5wjsd0b983JMaQWm5479U5hht2xj");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wqvxxdnakwfnxawtt != null) {
                     try {
                         wqvxxdnakwfnxawtt.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Handler ttvglcmdpgfjmqajd = new Handler(Looper.getMainLooper());
             ttvglcmdpgfjmqajd.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     dufyqgnegkqcjkrrawl.setVisibility(View.VISIBLE);
                 }
             }, 435);
             Intent mrjcuwqdeogiaie = new Intent();
             mrjcuwqdeogiaie.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.AncvbfmiyfdlynkufsnikbnActivity");
             startActivity(mrjcuwqdeogiaie);
             dufyqgnegkqcjkrrawl.animate()
                     .translationX(118f)
                     .translationY(35f)
                     .setDuration(405)
                     .start();
         }
     });
     final View wbmxqpqqbjnouxgclhl = findViewById(R.id.cmxottprcyulbpuzdzgmmiz);
     wbmxqpqqbjnouxgclhl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String enxrmisuldrzhyeldyusnkcgk = "rxIgokebzBi6bHiRjRnsOwKQWkgQvnoKfVO68xypK1PJv0HrLc5z2s15auubgUhcW27qsCIW6GWZK7I6YZ6AEj4Dq7af0LQgal0DIpKfdgFc0l3YCscINqipzxPFzV126cd";
             enxrmisuldrzhyeldyusnkcgk.substring(2);
             enxrmisuldrzhyeldyusnkcgk.replace('p', 'e');
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(FjakchcxbxukccnytdvwffrxnuActivity.this, "iyuuydqqoqxnwlgghavdufbpo", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper swghwznrikufxrthbjywzb = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "upopbxdrvihgmxecnxsm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kgoxabozgfxbcaghkbonliai"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hosppkdayeaxvdgmgehxnyssz = swghwznrikufxrthbjywzb.getWritableDatabase();
             try {
                 hosppkdayeaxvdgmgehxnyssz.execSQL("UPDATE kgoxabozgfxbcaghkbonliai SET value = ? WHERE name = ?", new String[]{"xbpymyboupotjzzjcx", "iuibeunyvetvpgpwm"});
             } finally {
                 hosppkdayeaxvdgmgehxnyssz.close();
                 swghwznrikufxrthbjywzb.close();
             }
             Date dbnwvsuusoeeribae = new Date();
             String jypompcaqmhypbqw = String.valueOf(dbnwvsuusoeeribae.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper izlqnbbuoxevtlawdtkwsnp = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "vimdnpdnjrihveuhyekgrz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wioidtgljdszwnuxpfdttksu"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase absyatfogvmfazsrbgvperc = izlqnbbuoxevtlawdtkwsnp.getWritableDatabase();
             try {
                 absyatfogvmfazsrbgvperc.execSQL("INSERT INTO wioidtgljdszwnuxpfdttksu (name, value) VALUES (?, ?)", new String[]{"tspnhochvmqbirdmujwb", "ssopdyutoiagplkxrpg"});
             } finally {
                 absyatfogvmfazsrbgvperc.close();
                 izlqnbbuoxevtlawdtkwsnp.close();
             }
             wbmxqpqqbjnouxgclhl.animate()
                     .translationX(191f)
                     .translationY(138f)
                     .setDuration(883)
                     .start();
             File file6 = new File(getFilesDir(), "obnjjepwzcxwifp.txt");
             ScheduledExecutorService rrkosvorrianvsfs = Executors.newScheduledThreadPool(4);
             rrkosvorrianvsfs.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(47);
                 }
             });
             rrkosvorrianvsfs.shutdown();
         }
     });
     final View jfiaafqsiyrzozr = findViewById(R.id.bclscdehbepmqigdfdtxyrq);
     jfiaafqsiyrzozr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ftnmpscubixedth = new Intent();
             ftnmpscubixedth.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.KsntqxdaeioyuqprgvActivity");
             startActivity(ftnmpscubixedth);
             String ryboivkwiqfuyzzlbtffri = "OffEwVemcdzIvwCkiO0o5m96IrxP2lBirpQPktsdT1Eyib7hEoFlPnuYiOr";
             ryboivkwiqfuyzzlbtffri.substring(3);
             ryboivkwiqfuyzzlbtffri.replace('a', 's');
             jfiaafqsiyrzozr.animate()
                     .alpha(0.5737612f)
                     .setDuration(759)
                     .start();
             int zkpxjzurgujozrfrgrjhsfac = 817;
             Math.max(zkpxjzurgujozrfrgrjhsfac, 49);
             String.valueOf(zkpxjzurgujozrfrgrjhsfac);
             File file4 = new File(getFilesDir(), "bvokshtrhtjvnukmtdswcfy.txt");
             FileReader oqmdbggeeahzrhhezgtdmrdmm = null;
             try {
                 if(file4.exists()) {
                     oqmdbggeeahzrhhezgtdmrdmm = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (oqmdbggeeahzrhhezgtdmrdmm != null) {
                     try {
                         oqmdbggeeahzrhhezgtdmrdmm.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View wojziqdguoaqdkrijbba = findViewById(R.id.xlimawsvsohviylbioydboevf);
     wojziqdguoaqdkrijbba.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper cnzyiuptzhdswbdkhazr = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "egoycxfivhlqmussffzfon.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mqtumnushoewfxbpvofn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ivsmiykdatiroezfomkfcx = cnzyiuptzhdswbdkhazr.getWritableDatabase();
             try {
                 ivsmiykdatiroezfomkfcx.execSQL("DELETE FROM mqtumnushoewfxbpvofn WHERE name = ?", new String[]{"fplcwmfuwrpsvunfsvyuj", "igtlgiipwurbeykwmvuo"});
             } finally {
                 ivsmiykdatiroezfomkfcx.close();
                 cnzyiuptzhdswbdkhazr.close();
             }
             wojziqdguoaqdkrijbba.animate()
                     .scaleX(1.8818017f)
                     .scaleY(1.8818017f)
                     .setDuration(977)
                     .start();
             int vhamqzlieaipxvrvotwnrew = 116;
             Math.max(vhamqzlieaipxvrvotwnrew, 50);
             String.valueOf(vhamqzlieaipxvrvotwnrew);
             SQLiteOpenHelper nfrtpobzaigsxsvnv = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "zioxivzoddcetklfemrnexfl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bjpwxoihkdcolkkpdbtymyey"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase aladczhsahbkatgtewzcrcfgo = nfrtpobzaigsxsvnv.getWritableDatabase();
             try {
                 aladczhsahbkatgtewzcrcfgo.execSQL("INSERT INTO bjpwxoihkdcolkkpdbtymyey (name, value) VALUES (?, ?)", new String[]{"hsbdmuqdmqoaoavzaa", "seiyxqbsmhsdpaegijcdp"});
             } finally {
                 aladczhsahbkatgtewzcrcfgo.close();
                 nfrtpobzaigsxsvnv.close();
             }
             Intent gcvrlovrcsogjag = new Intent();
             gcvrlovrcsogjag.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.VrhlrfowmwhdoyeadoerbuwActivity");
             startActivity(gcvrlovrcsogjag);
             wojziqdguoaqdkrijbba.animate()
                     .scaleX(1.2749304f)
                     .scaleY(1.2749304f)
                     .setDuration(331)
                     .start();
             File file6 = new File(getFilesDir(), "uxocfotzgdegypevrou.txt");
             Intent kpuqfmmttqnvmvcurdlnujymv = new Intent();
             kpuqfmmttqnvmvcurdlnujymv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.VrhlrfowmwhdoyeadoerbuwActivity");
             startActivity(kpuqfmmttqnvmvcurdlnujymv);
             wojziqdguoaqdkrijbba.animate()
                     .rotation(71f)
                     .setDuration(734)
                     .start();
             ScheduledExecutorService rgbbawrumdqyjaymbxeeq = Executors.newScheduledThreadPool(3);
             rgbbawrumdqyjaymbxeeq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(25);
                 }
             });
             rgbbawrumdqyjaymbxeeq.shutdown();
         }
     });
     final View vmqtvoizxencsbdezeahhk = findViewById(R.id.gmeubqktxtucxklacy);
     vmqtvoizxencsbdezeahhk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper ezyddsaqjnjytvinilttnf = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "jmmkswqcyjwdrhtacw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nzfwcetproxwyoitohsd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rhmzbickfyrfokbzkdemlz = ezyddsaqjnjytvinilttnf.getWritableDatabase();
             try {
                 rhmzbickfyrfokbzkdemlz.execSQL("UPDATE nzfwcetproxwyoitohsd SET value = ? WHERE name = ?", new String[]{"kmcdmjplcxrwxrdnshpix", "znljdmeccyrdjmi"});
             } finally {
                 rhmzbickfyrfokbzkdemlz.close();
                 ezyddsaqjnjytvinilttnf.close();
             }
             File file1 = new File(getFilesDir(), "kmhmjijoqbxhmgik.txt");
             FileWriter iccalylminhspdbf = null;
             try {
                 iccalylminhspdbf = new FileWriter(file1);
                 iccalylminhspdbf.write("SrAQms4ZIatva2XAXMu9Q5tTN1");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (iccalylminhspdbf != null) {
                     try {
                         iccalylminhspdbf.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService jlfiyesaczbiqzvaepicd = Executors.newScheduledThreadPool(3);
             jlfiyesaczbiqzvaepicd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(26);
                 }
             });
             jlfiyesaczbiqzvaepicd.shutdown();
             int gtqajwssjdacxkuriubzylucs = 703;
             Math.max(gtqajwssjdacxkuriubzylucs, 782);
             String.valueOf(gtqajwssjdacxkuriubzylucs);
             vmqtvoizxencsbdezeahhk.animate()
                     .scaleX(1.3235576f)
                     .scaleY(1.3235576f)
                     .setDuration(340)
                     .start();
             ScheduledExecutorService qwretyjjtmdrpigqmgsyyftw = Executors.newScheduledThreadPool(2);
             qwretyjjtmdrpigqmgsyyftw.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(74);
                 }
             });
             qwretyjjtmdrpigqmgsyyftw.shutdown();
             vmqtvoizxencsbdezeahhk.animate()
                     .alpha(0.08573985f)
                     .setDuration(700)
                     .start();
             Intent werglmyglwykidcyzonwepf = new Intent();
             werglmyglwykidcyzonwepf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.IruknvywhzhhqdxwxgolcActivity");
             startActivity(werglmyglwykidcyzonwepf);
             File file8 = new File(getFilesDir(), "ncunzahgrnbiidv.txt");
             if(file8.exists()) file8.delete();
             Date hhaywrpbiytcwelo = new Date();
             String nonaelaygvahfacylrwalkko = String.valueOf(hhaywrpbiytcwelo.getTime());
             System.currentTimeMillis();
         }
     });
     final View eopxdtzprtnxvme = findViewById(R.id.mfvjnhwjiwhohhdtxn);
     eopxdtzprtnxvme.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date eihndlokysnzlak = new Date();
             String nkvxbjyqxpwrpcegkzrwiug = String.valueOf(eihndlokysnzlak.getTime());
             System.currentTimeMillis();
             Intent uohpoxmszloiqxlry = new Intent();
             uohpoxmszloiqxlry.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.XrtswxatkhlhsemzdjhpeActivity");
             startActivity(uohpoxmszloiqxlry);
             String ldmoaaxiiopltmfmhgqseh = "lPoQZ88o7efOKouOeIb0Vt0H46beEVBz2RYltSJaf0oZTMjyRDNoa1p10My4taAFf96yqS0ZLjdWPJR4QPQFSl44sMnkINSP9dAfAeV16QIdp7bO3BUlCiTOL3MMbqShFimJ4a5mkTtCtVScGI4563F8U4Z58X";
             ldmoaaxiiopltmfmhgqseh.substring(1);
             ldmoaaxiiopltmfmhgqseh.replace('w', 'z');
             File file3 = new File(getFilesDir(), "uzusaedbgnclewhziimjut.txt");
             if(file3.exists()) file3.delete();
             ScheduledExecutorService hjdcjbiimynfztuqclcpfzkab = Executors.newScheduledThreadPool(3);
             hjdcjbiimynfztuqclcpfzkab.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(44);
                 }
             });
             hjdcjbiimynfztuqclcpfzkab.shutdown();
             File file5 = new File(getFilesDir(), "ytdwxmbokcvugaccjsubgk.txt");
             FileReader nnhhyzlmzjnzbemtetkqn = null;
             try {
                 if(file5.exists()) {
                     nnhhyzlmzjnzbemtetkqn = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (nnhhyzlmzjnzbemtetkqn != null) {
                     try {
                         nnhhyzlmzjnzbemtetkqn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper beibjdnqibephmuyjnbamsfmq = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "xptyptdjcopsjjc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fxcfdudknwczkirubu"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fwiscapxwjqcxkwlcnp = beibjdnqibephmuyjnbamsfmq.getWritableDatabase();
             try {
                 fwiscapxwjqcxkwlcnp.execSQL("DELETE FROM fxcfdudknwczkirubu WHERE name = ?", new String[]{"bsqqjilefoepudyrmuzukhxmy", "ngzftrylersfuxqzbcgstx"});
             } finally {
                 fwiscapxwjqcxkwlcnp.close();
                 beibjdnqibephmuyjnbamsfmq.close();
             }
         }
     });
     final View ozmiqktmsifbfbqpbbapyec = findViewById(R.id.kgeazfzddrpmlvejjgwwyafx);
     ozmiqktmsifbfbqpbbapyec.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ozmiqktmsifbfbqpbbapyec.animate()
                     .alpha(0.07762617f)
                     .setDuration(340)
                     .start();
             SQLiteOpenHelper qbrduomkknalnrpbrcqfnw = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "aqriazbqgdkhmkekxjvkrrj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mrqyazfjnlqzjjmd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hbqepojxarecoioafxqiaom = qbrduomkknalnrpbrcqfnw.getWritableDatabase();
             try {
                 hbqepojxarecoioafxqiaom.execSQL("DELETE FROM mrqyazfjnlqzjjmd WHERE name = ?", new String[]{"cuyoljzcriwacterw", "urivigeuqvoajfahkl"});
             } finally {
                 hbqepojxarecoioafxqiaom.close();
                 qbrduomkknalnrpbrcqfnw.close();
             }
             Intent eozsvfcpxjgqvmllnbj = new Intent();
             eozsvfcpxjgqvmllnbj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.CtzvuednjkismrfizcomjtActivity");
             startActivity(eozsvfcpxjgqvmllnbj);
             ozmiqktmsifbfbqpbbapyec.animate()
                     .alpha(0.96916974f)
                     .setDuration(361)
                     .start();
             Intent ebvjwluvlgvunwatqecnj = new Intent();
             ebvjwluvlgvunwatqecnj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.CtzvuednjkismrfizcomjtActivity");
             startActivity(ebvjwluvlgvunwatqecnj);
             String jeyjitvjglmlrsryuy = "tw4jPSDYmtDbpGuA99phqZSj2hmGESUxIwr2ysEg4YMw207WYze40kwCS690EfRKzNZLjmuawVSp2SiyvOj1yedPGKiZD4jOgByrl8TeOAaCej6220QCJSs1";
             jeyjitvjglmlrsryuy.substring(4);
             jeyjitvjglmlrsryuy.replace('h', 'i');
             Intent pveyuklinaoojilfihkb = new Intent();
             pveyuklinaoojilfihkb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.CtzvuednjkismrfizcomjtActivity");
             startActivity(pveyuklinaoojilfihkb);
         }
     });
     final View amyilxebfxhpjecepdpu = findViewById(R.id.sizimtgfollhwqqdaiq);
     amyilxebfxhpjecepdpu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tyoznfcerlmwvgdaojtkjeplx = new SQLiteOpenHelper(FjakchcxbxukccnytdvwffrxnuActivity.this, "zrkpbyfehdzjcufbexjpnuq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jvqxcorqnysqqbsousqlbhffy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zoaghhzswgclihnrrwatz = tyoznfcerlmwvgdaojtkjeplx.getWritableDatabase();
             try {
                 zoaghhzswgclihnrrwatz.execSQL("UPDATE jvqxcorqnysqqbsousqlbhffy SET value = ? WHERE name = ?", new String[]{"pdxyumttjezhgvvtaar", "qfvyhyfcvrlsgst"});
             } finally {
                 zoaghhzswgclihnrrwatz.close();
                 tyoznfcerlmwvgdaojtkjeplx.close();
             }
             String wzapftopyhjqdrzfeujzikp = "1Ym9DdRRxYVnAbOLOQeY2eY9kSsscl06qGifdBK24LuVU9oGCbSlbKK6YmA0Ps2F5";
             wzapftopyhjqdrzfeujzikp.substring(0);
             wzapftopyhjqdrzfeujzikp.replace('n', 'i');
             Intent yiynokquekkemutemiebhmf = new Intent();
             yiynokquekkemutemiebhmf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.DzckkdwjxqqaxfruxhakgiActivity");
             startActivity(yiynokquekkemutemiebhmf);
             amyilxebfxhpjecepdpu.animate()
                     .alpha(0.2570489f)
                     .setDuration(648)
                     .start();
         }
     });

qpqjanomdxoerdgzfcpkzehc();
Ddjgruconzzkdiifnrq   oxakppbwqgeodhtw =     new Ddjgruconzzkdiifnrq();
oxakppbwqgeodhtw.decblyimigckzzmkkxy ="V9uYivHFMeOuT1TmvxnzYTS0QW2J011JnYVzdraWmaE0xYxjzYNslvnBxldYZnH8hSMI2c6GhatVlHbDsSc80zD7MHFGuO5";
oxakppbwqgeodhtw.kkumovzaejnweqlwblhrgr ="a5dbpErNkn4HTdNd7tB0Nrtm6pJ5Mg7NjSjtOcDtZ3wEXBQPPlJqczMnufyjJKKf1TX6qelTpoAUdFOAaKOuh0rBHkmC9rLdB9ZM8vLj8u6Krfzf";
oxakppbwqgeodhtw.ggxtkvqxnsuesfw ="qOnRicbSjjPu4CEoog8ga6DTfs63MUUrAgVPgDWe8bFS8Ehv1IihLdJipA8jmoMX8w6BWcegKftmEh6bvvljVQemYSh3oUk92bLAs8DyBi3dSnSma";
oxakppbwqgeodhtw.mcmmromvbjocrulaogevbj ="rFSWVil9rIpqsV2NWW8ssxoAnTfTCDGzEZM7TPUbc4h87eEQTgjl4D03TNwCMsQj";
oxakppbwqgeodhtw.pfjiaqcpbrqclqyeajlvogfuw ="ASLOIpnxsjgMbgv8MFPcn0zyI3ZTOXwxqj8raNXSoaOrxtIK59iF1qfn4mcwPzIg13Q38YfJFHmHNTErzkwHUPYD0ElOeXcjn8JZFBMndLx5NOOHGxT0zXQOWHWBFMFfCgMt488gbVDsZAC9kaHPrASceWhvCwp8COs333KCAi5OeFizv6tTmCKVqwi8u4";
oxakppbwqgeodhtw.bnietitfjmudbgymywnzf ="0lLvJUYsDAOC97uen3XbVEREU5Keb6e1OjlDHu2WKc0CFV5bGoBMiWW3dTwPqjhEiOOW";
oxakppbwqgeodhtw.nahxffepkvrdxcvbktmd ="bnaGphkV4gmCyIncJB5qL9oJqnCwQu81VIguTlODdTxJfeUWEoh2pfaZaatql4z64XMskvYbKuigcWsVcL29fyN7M9dPJv0aZamVZNyIdQNrhaLpxwXlgA3OKe1lYLgP8ewO19CbNjdSfvUhvu5uiCxpq5a6Xiv2vd4rczER";
oxakppbwqgeodhtw.ujrspvcwngvlmjvjj ="HjXaqCwZNF5IAMMrGQdYqvEpbIK0VTLOePfficqchw64EhRUHFWiwuKXCWVx22zeM7TCYVSfsVNglQtyQ2NgPSzAMvqGkuvnim0yQPlcCrPImZ5to37kfR1DdXiLSXws";
oxakppbwqgeodhtw.xkxbxjmepkbkocfuz ="bgkPqFIBIeXdFaOx9OlWAAxbM8RJnQSpMhCgoHvyz7vqXVsKGwcxl5pBD8EUvpXfz5NXwOzx3yNlXS28UKDqnP6He";
oxakppbwqgeodhtw.gjpkjhasxnxngpwecpfor ="rCBN509JGrggPWHPGPZakOWAUyBvz3jG4XEfXS4MRQRePFKuOlR85kmypiPq0s0g589InOwRIt6VLyJVx33ENRpGUOmEWpH7G00BT18e8Cdpw6JRXjFrfHi1UHDih06";
 Toast.makeText(FjakchcxbxukccnytdvwffrxnuActivity.this,getString(R.string.dfijwpvabjqumslqoxxehbm),Toast.LENGTH_SHORT).show();
    }
 void qpqjanomdxoerdgzfcpkzehc() {
     long fecvyohhmgqzgyjqwfr = System.currentTimeMillis();
     Date date = new Date(fecvyohhmgqzgyjqwfr);
     String time = date.toString();
     yaiirhnyvkvzbjxfpkkoeshoh();
}
 void yaiirhnyvkvzbjxfpkkoeshoh() {
     long aelpuyxfrccxqvx = System.currentTimeMillis();
     Date date = new Date(aelpuyxfrccxqvx);
     String time = date.toString();
}}