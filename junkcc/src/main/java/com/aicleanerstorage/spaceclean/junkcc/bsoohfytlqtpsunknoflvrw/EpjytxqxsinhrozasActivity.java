package com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw;

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

public class EpjytxqxsinhrozasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pjytxqxsinhrozas);
     final View xmzaiphaymserwacgedam = findViewById(R.id.lubhfskjxikaehxxyi);
     xmzaiphaymserwacgedam.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tqstramqqfncjmr = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "tvttsbeauhayopr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ijzcneqptjdizptug"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vgulfsbwhrogxofdf = tqstramqqfncjmr.getWritableDatabase();
             try {
                 vgulfsbwhrogxofdf.execSQL("UPDATE ijzcneqptjdizptug SET value = ? WHERE name = ?", new String[]{"ixwdztavjnhlpqlcpawj", "lbyqqrrwvnhptzr"});
             } finally {
                 vgulfsbwhrogxofdf.close();
                 tqstramqqfncjmr.close();
             }
             Intent nzoubkpujnhmqocjwav = new Intent();
             nzoubkpujnhmqocjwav.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.BpivjeapfblzpfufxyrlqActivity");
             startActivity(nzoubkpujnhmqocjwav);
             SQLiteOpenHelper mfxvmxcyvdfnrzcfzqpchb = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "itikxbwmeoyrfihnlveha.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "noclnqzuzxklcvbsoehggmxd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jsjcdjlkppbmlqh = mfxvmxcyvdfnrzcfzqpchb.getWritableDatabase();
             try {
                 jsjcdjlkppbmlqh.execSQL("DELETE FROM noclnqzuzxklcvbsoehggmxd WHERE name = ?", new String[]{"fphkkozmfvnspvufynfh", "xthboffnjlykydnzk"});
             } finally {
                 jsjcdjlkppbmlqh.close();
                 mfxvmxcyvdfnrzcfzqpchb.close();
             }
         }
     });
     final View gqdsddewtntrpggfjwi = findViewById(R.id.biyjvoazxedwsprltjbihg);
     gqdsddewtntrpggfjwi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "jdhppwbgnjwmiceengmmpf.txt");
             if(file0.exists()) file0.delete();
             SQLiteOpenHelper zyrdayhqzlwutvv = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "gsruruvlkkoqpeeozed.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "worutmscxijyqnviihvgnou"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hotxhakvyyrptmtbaszz = zyrdayhqzlwutvv.getWritableDatabase();
             try {
                 hotxhakvyyrptmtbaszz.execSQL("DELETE FROM worutmscxijyqnviihvgnou WHERE name = ?", new String[]{"ibaxppqmrjgynul", "byevcwuvxhesmaoxsbo"});
             } finally {
                 hotxhakvyyrptmtbaszz.close();
                 zyrdayhqzlwutvv.close();
             }
             ScheduledExecutorService qwevokgndecowxey = Executors.newScheduledThreadPool(3);
             qwevokgndecowxey.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(36);
                 }
             });
             qwevokgndecowxey.shutdown();
             Intent pnlmjfjdgdzolgfujzltpbay = new Intent();
             pnlmjfjdgdzolgfujzltpbay.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.CkuvpsyqosgblussuActivity");
             startActivity(pnlmjfjdgdzolgfujzltpbay);
             gqdsddewtntrpggfjwi.animate()
                     .translationX(162f)
                     .translationY(118f)
                     .setDuration(949)
                     .start();
             File file5 = new File(getFilesDir(), "vhazgsymqgkruptkqhly.txt");
             FileReader noujgdnyywlzjiombgfh = null;
             try {
                 if(file5.exists()) {
                     noujgdnyywlzjiombgfh = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (noujgdnyywlzjiombgfh != null) {
                     try {
                         noujgdnyywlzjiombgfh.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper fikhdvmuldeqninf = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "xleojacpbhasauxuz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qrzyhqbqbsrbiijtoutcmplux"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ayafjyfrkgptnraimjberkrje = fikhdvmuldeqninf.getWritableDatabase();
             try {
                 ayafjyfrkgptnraimjberkrje.execSQL("DELETE FROM qrzyhqbqbsrbiijtoutcmplux WHERE name = ?", new String[]{"evefjdgyhzviifyuu", "nnxbvjbrkighgrliayvndlatk"});
             } finally {
                 ayafjyfrkgptnraimjberkrje.close();
                 fikhdvmuldeqninf.close();
             }
             Date emuktclrfxyzsvjrdkulqfvkc = new Date();
             String sdjyrluukvenrpespdm = String.valueOf(emuktclrfxyzsvjrdkulqfvkc.getTime());
             System.currentTimeMillis();
             Intent eueyoyjshlsuttvqlp = new Intent();
             eueyoyjshlsuttvqlp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.CkuvpsyqosgblussuActivity");
             startActivity(eueyoyjshlsuttvqlp);
         }
     });
     final View rqputuvpyqslrhsgyhzyl = findViewById(R.id.temjzghhfryahokco);
     rqputuvpyqslrhsgyhzyl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             rqputuvpyqslrhsgyhzyl.animate()
                     .translationX(109f)
                     .translationY(124f)
                     .setDuration(128)
                     .start();
             File file1 = new File(getFilesDir(), "hxifucnqphulqcnsoizaiuto.txt");
         }
     });
     final View hadsbshqmcwtxiyubcuocgjqd = findViewById(R.id.eupkomlthayzkioktdbww);
     hadsbshqmcwtxiyubcuocgjqd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper oefzgzriairnxepclent = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "nowahpycayekydfzp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "unwapfxnjihrthwah"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bykvlvrnkptsdmmzvblulethc = oefzgzriairnxepclent.getWritableDatabase();
             try {
                 bykvlvrnkptsdmmzvblulethc.execSQL("UPDATE unwapfxnjihrthwah SET value = ? WHERE name = ?", new String[]{"urmfsqaiclbgaasj", "jwilivisvrbpoazourrissmq"});
             } finally {
                 bykvlvrnkptsdmmzvblulethc.close();
                 oefzgzriairnxepclent.close();
             }
             Intent tzuwzwtqmjhdwgydszil = new Intent();
             tzuwzwtqmjhdwgydszil.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.YldyxgjbyuptvpqupActivity");
             startActivity(tzuwzwtqmjhdwgydszil);
             String sosfyelaswcpsoxcbee = "FU7uCsU501hGYX8osoDzohdnPqAqrvu0Y";
             sosfyelaswcpsoxcbee.substring(1);
             sosfyelaswcpsoxcbee.replace('v', 'q');
             Handler vcvihtmckfwcjycakylrtl = new Handler(Looper.getMainLooper());
             vcvihtmckfwcjycakylrtl.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     hadsbshqmcwtxiyubcuocgjqd.setVisibility(View.VISIBLE);
                 }
             }, 32);
             SQLiteOpenHelper jozeaitaqproxglidlhh = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "tfiuwzufdnmbdtdmbvopba.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "znrillyzgqsnsxegzpctesdwt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pdssbrkaotnbhgixkztzca = jozeaitaqproxglidlhh.getWritableDatabase();
             try {
                 pdssbrkaotnbhgixkztzca.execSQL("INSERT INTO znrillyzgqsnsxegzpctesdwt (name, value) VALUES (?, ?)", new String[]{"ogurigpmaiejnjom", "kepdaxjgbgvssnb"});
             } finally {
                 pdssbrkaotnbhgixkztzca.close();
                 jozeaitaqproxglidlhh.close();
             }
             File file5 = new File(getFilesDir(), "zoolfcsqozjnykhnsodidovdq.txt");
             SQLiteOpenHelper zkaalqclakelfadhx = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "hafykpzvlcwnbbekmvho.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pdwsdpyeuffebrqviyd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase btcxlsrdunqotplehwqfmygu = zkaalqclakelfadhx.getWritableDatabase();
             try {
                 btcxlsrdunqotplehwqfmygu.execSQL("DELETE FROM pdwsdpyeuffebrqviyd WHERE name = ?", new String[]{"cqjyalhxbprdeuhqncfx", "nppnvupwvkmfdmmlspuvcp"});
             } finally {
                 btcxlsrdunqotplehwqfmygu.close();
                 zkaalqclakelfadhx.close();
             }
         }
     });
     final View rvukpwctsxvowwturckeksy = findViewById(R.id.idtusxmphoeayczlcu);
     rvukpwctsxvowwturckeksy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             rvukpwctsxvowwturckeksy.animate()
                     .rotation(35f)
                     .setDuration(91)
                     .start();
             SQLiteOpenHelper jlsrojhabbyymfsxp = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "jwyvybwacvcumuaynrerfdfjj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wkrztybzegnrdkwtqyki"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gaydzatzafeacvkhkvvzmehh = jlsrojhabbyymfsxp.getWritableDatabase();
             try {
                 gaydzatzafeacvkhkvvzmehh.execSQL("INSERT INTO wkrztybzegnrdkwtqyki (name, value) VALUES (?, ?)", new String[]{"lyhxxgrpefcrlchsgstochxj", "vjbpgwqxuxtgzzrl"});
             } finally {
                 gaydzatzafeacvkhkvvzmehh.close();
                 jlsrojhabbyymfsxp.close();
             }
             File file2 = new File(getFilesDir(), "myjmzapdpndyppws.txt");
             FileWriter unmskzmilxqhetc = null;
             try {
                 unmskzmilxqhetc = new FileWriter(file2);
                 unmskzmilxqhetc.write("U7XPiiPPmEs80btUpE6UAxGBbfjs37iqUYGncLl6H0uJeqikR466xyTNV4Nrlrer9cfCt9Se4fM6OUn1HRMOjKj3WmE3");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (unmskzmilxqhetc != null) {
                     try {
                         unmskzmilxqhetc.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int maqgpfsfamkehdsf = 51;
             Math.max(maqgpfsfamkehdsf, 926);
             String.valueOf(maqgpfsfamkehdsf);
             ScheduledExecutorService igfvqiipzlwqehljz = Executors.newScheduledThreadPool(1);
             igfvqiipzlwqehljz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(1);
                 }
             });
             igfvqiipzlwqehljz.shutdown();
             File file5 = new File(getFilesDir(), "ebovmsdojxpurrdid.txt");
             String iowqzkgdgqnokizsmpwlxhrr = "mlXhvkUPsNcvP5KaszeCwq5lJiIunH9VXt0Nb1I1iDlogTHZkue9ITsRSYNWWXtOmjmsng9haAk2tnpFHUoSJ5MrJfyV8pkpe7vCk1gYtE0YWaaQ1p3y2p9WTBP5NmXc6YE93Dko8BbkZIVs1FjBJDnzdOnt0dC6weUYsxFwGXZuYWcxe6EYZuymMUFSoa";
             iowqzkgdgqnokizsmpwlxhrr.substring(4);
             iowqzkgdgqnokizsmpwlxhrr.replace('g', 'u');
             ScheduledExecutorService qtfzzghjpytptrwgtxqoniw = Executors.newScheduledThreadPool(3);
             qtfzzghjpytptrwgtxqoniw.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(65);
                 }
             });
             qtfzzghjpytptrwgtxqoniw.shutdown();
             rvukpwctsxvowwturckeksy.animate()
                     .scaleX(0.67658675f)
                     .scaleY(0.67658675f)
                     .setDuration(791)
                     .start();
             int mjjuftzqyyeesjitxxacy = 673;
             Math.max(mjjuftzqyyeesjitxxacy, 700);
             String.valueOf(mjjuftzqyyeesjitxxacy);
         }
     });
     final View tnvibnwqsevpgty = findViewById(R.id.fdlkjupbisyaknbza);
     tnvibnwqsevpgty.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "eogowfxmpambunxulijfyjrfm.txt");
             if(file0.exists()) file0.delete();
             String sxsphuvlilizetorckc = "grrLh0RXpysbuIzWS9JgkqhBtHkHdtprQSGEPhWLhWFN7ltNeUBHvbZDGdNPxd";
             sxsphuvlilizetorckc.substring(2);
             sxsphuvlilizetorckc.replace('h', 'j');
             File file2 = new File(getFilesDir(), "uevpvrzikpailofytasyoifnm.txt");
             FileWriter kozwxpoicqwnalxxkraf = null;
             try {
                 kozwxpoicqwnalxxkraf = new FileWriter(file2);
                 kozwxpoicqwnalxxkraf.write("GRpi730Q17Ta2WBpoJPmNSaM9MnxtWcihFnO8ku9GEiRYoAMbTf3Lz2jKSWBR6KIJVVPkyrWd52PjYzt4txCf4ZIN2o6uJzOWfsjb5gaFlKgbOoLpZCgb7tz4Pejyof85Ae1HvPIvKXxgE9oQBdPw8qHi");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (kozwxpoicqwnalxxkraf != null) {
                     try {
                         kozwxpoicqwnalxxkraf.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             tnvibnwqsevpgty.animate()
                     .alpha(0.4032513f)
                     .setDuration(311)
                     .start();
             Handler ivyuxalyxhaqhbshn = new Handler(Looper.getMainLooper());
             ivyuxalyxhaqhbshn.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     tnvibnwqsevpgty.setVisibility(View.VISIBLE);
                 }
             }, 940);
             tnvibnwqsevpgty.animate()
                     .scaleX(1.149749f)
                     .scaleY(1.149749f)
                     .setDuration(807)
                     .start();
             Intent itbrszoparscxkmvcdkhobh = new Intent();
             itbrszoparscxkmvcdkhobh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.BmmtjnyovksqaitaykcsmffxxbActivity");
             startActivity(itbrszoparscxkmvcdkhobh);
         }
     });
     final View cptvxarkiglsatdvujusgwqd = findViewById(R.id.otfzrjetaxpvrospmdft);
     cptvxarkiglsatdvujusgwqd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent vctbhhvojytwvbqlhizdcbg = new Intent();
             vctbhhvojytwvbqlhizdcbg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.YyjxgueppgryennqActivity");
             startActivity(vctbhhvojytwvbqlhizdcbg);
             cptvxarkiglsatdvujusgwqd.animate()
                     .rotation(150f)
                     .setDuration(749)
                     .start();
             int bxxtaeodrlstcknshxreyk = 206;
             Math.max(bxxtaeodrlstcknshxreyk, 621);
             String.valueOf(bxxtaeodrlstcknshxreyk);
             File file3 = new File(getFilesDir(), "rkxaoukmkkohpomintvxou.txt");
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(EpjytxqxsinhrozasActivity.this, "fqpfytbeqnxexmlhqb", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View qdokwboyktmhygvmib = findViewById(R.id.jcbxhrxvjpqqfybp);
     qdokwboyktmhygvmib.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ktsjqnrogeijxpkylfxh = new Intent();
             ktsjqnrogeijxpkylfxh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.XknfpqrnbxdcvpxlmfzxgolActivity");
             startActivity(ktsjqnrogeijxpkylfxh);
             qdokwboyktmhygvmib.animate()
                     .translationX(59f)
                     .translationY(80f)
                     .setDuration(493)
                     .start();
             int kuuhldznwiijgeqkonqqlror = 834;
             Math.max(kuuhldznwiijgeqkonqqlror, 629);
             String.valueOf(kuuhldznwiijgeqkonqqlror);
             SQLiteOpenHelper stybihwtbzhqaqigrcjb = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "arsdjmqayceogxqjgj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ktnpkmuoszqffygibmch"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qbnpuqzbqpjmfyarnbuwszk = stybihwtbzhqaqigrcjb.getWritableDatabase();
             try {
                 qbnpuqzbqpjmfyarnbuwszk.execSQL("DELETE FROM ktnpkmuoszqffygibmch WHERE name = ?", new String[]{"jeaeokjhhsgvfuzdodihk", "rvwusnnrwfcucajycnk"});
             } finally {
                 qbnpuqzbqpjmfyarnbuwszk.close();
                 stybihwtbzhqaqigrcjb.close();
             }
             Intent pmkuhoismdbpusrxheuipkhco = new Intent();
             pmkuhoismdbpusrxheuipkhco.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.XknfpqrnbxdcvpxlmfzxgolActivity");
             startActivity(pmkuhoismdbpusrxheuipkhco);
             File file5 = new File(getFilesDir(), "dvkwayivilxrwoedoozbnfuk.txt");
             FileReader qbykvojlxsjuykrx = null;
             try {
                 if(file5.exists()) {
                     qbykvojlxsjuykrx = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (qbykvojlxsjuykrx != null) {
                     try {
                         qbykvojlxsjuykrx.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper lvpgsnpvrrcwcrppicpjkrn = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "ozethukahqrxkqrqcbfvip.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pkyueewopzdbqhualvfe"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bkwozdtbchaklxqvjzfib = lvpgsnpvrrcwcrppicpjkrn.getWritableDatabase();
             try {
                 bkwozdtbchaklxqvjzfib.execSQL("DELETE FROM pkyueewopzdbqhualvfe WHERE name = ?", new String[]{"pbtkqranfhbpmqfifkffo", "sxxfmjintfjacnqd"});
             } finally {
                 bkwozdtbchaklxqvjzfib.close();
                 lvpgsnpvrrcwcrppicpjkrn.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(EpjytxqxsinhrozasActivity.this, "hcaotgrdiplmoormqklmqsd", Toast.LENGTH_SHORT).show();
                 }
             });
             int nhidicnixaefzdzyvoipkuluy = 919;
             Math.max(nhidicnixaefzdzyvoipkuluy, 721);
             String.valueOf(nhidicnixaefzdzyvoipkuluy);
             ScheduledExecutorService forccbcdavtbdeqb = Executors.newScheduledThreadPool(3);
             forccbcdavtbdeqb.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(5);
                 }
             });
             forccbcdavtbdeqb.shutdown();
         }
     });
     final View lalcnwfapbrzgjhbje = findViewById(R.id.oizoaxepaakzmwaf);
     lalcnwfapbrzgjhbje.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             lalcnwfapbrzgjhbje.animate()
                     .translationX(40f)
                     .translationY(4f)
                     .setDuration(638)
                     .start();
             Handler vyucfgxawqsylrykneixlq = new Handler(Looper.getMainLooper());
             vyucfgxawqsylrykneixlq.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     lalcnwfapbrzgjhbje.setVisibility(View.VISIBLE);
                 }
             }, 38);
             File file2 = new File(getFilesDir(), "uzpgxfylqvjrmfcffdq.txt");
             FileWriter gqjdoxtmnggbrrwacn = null;
             try {
                 gqjdoxtmnggbrrwacn = new FileWriter(file2);
                 gqjdoxtmnggbrrwacn.write("sGvnhiNYUrScYSf658QovOqY7ZJmzKaq28yoA1MDjsvbsQcSRQYuXvWBuefMOqoC7pqF2ba8qulqA5JBVml41f0fvNbylZjuCdP5Q7AtftdA9iYwqeDwaNfM4RlQ7DsWV9HjuJAz5s6ejMwRCEEzMYW3xxjBWJafOg6NCfj4mhuz7bxiFf3w14OCwqzB");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (gqjdoxtmnggbrrwacn != null) {
                     try {
                         gqjdoxtmnggbrrwacn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             lalcnwfapbrzgjhbje.animate()
                     .scaleX(0.5808047f)
                     .scaleY(0.5808047f)
                     .setDuration(664)
                     .start();
         }
     });
     final View zokyljxymxeeagtqgwbge = findViewById(R.id.rjnvpszxqeusnsd);
     zokyljxymxeeagtqgwbge.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "ugpkzlotcmqmcafi.txt");
             if(file0.exists()) file0.delete();
             SQLiteOpenHelper dtdgbiwjvzzmdpjzwzi = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "ltttqihichjmjmzbqefnwr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "uonczwnrrpqeqjojbyt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bfruujusfwxlgmkhzbszbcnf = dtdgbiwjvzzmdpjzwzi.getWritableDatabase();
             try {
                 bfruujusfwxlgmkhzbszbcnf.execSQL("DELETE FROM uonczwnrrpqeqjojbyt WHERE name = ?", new String[]{"btzipcmicasspureuhiodrnk", "ydfbigytwtjyihjctfda"});
             } finally {
                 bfruujusfwxlgmkhzbszbcnf.close();
                 dtdgbiwjvzzmdpjzwzi.close();
             }
             File file2 = new File(getFilesDir(), "wnycbgbabfsxbdzl.txt");
             if(file2.exists()) file2.delete();
         }
     });
     final View gvxqjufwibkqkxfgxdjuyelrx = findViewById(R.id.ffdoeccaohfsrhfwpktuei);
     gvxqjufwibkqkxfgxdjuyelrx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date dmcktserwmsqlhbo = new Date();
             String szscpcypfwmyxxzdyloenxlcq = String.valueOf(dmcktserwmsqlhbo.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper awgyteygflexisimmb = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "azizxwrdnhqkehsfukyk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ycutebkilkjswhlgfwmadluy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zawcqgskamjigrtagfb = awgyteygflexisimmb.getWritableDatabase();
             try {
                 zawcqgskamjigrtagfb.execSQL("INSERT INTO ycutebkilkjswhlgfwmadluy (name, value) VALUES (?, ?)", new String[]{"hxijqiyfykuggszatfcorkoux", "dumptagyrvjnjscchcrcv"});
             } finally {
                 zawcqgskamjigrtagfb.close();
                 awgyteygflexisimmb.close();
             }
             ScheduledExecutorService qahrnbtnesdaiclnbwpyni = Executors.newScheduledThreadPool(2);
             qahrnbtnesdaiclnbwpyni.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(44);
                 }
             });
             qahrnbtnesdaiclnbwpyni.shutdown();
         }
     });
     final View kvdpahvgbtmvynvdh = findViewById(R.id.owekphnckongilygytzjio);
     kvdpahvgbtmvynvdh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService rashedsmjajvjkqyeyn = Executors.newScheduledThreadPool(2);
             rashedsmjajvjkqyeyn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(30);
                 }
             });
             rashedsmjajvjkqyeyn.shutdown();
             SQLiteOpenHelper hdenjoadldbtaffkwt = new SQLiteOpenHelper(EpjytxqxsinhrozasActivity.this, "vpwsrbytzqwynnrjrbpqfj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pawwfvkikcvyxadnxmdour"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase twrnaeywvenypmnrygdp = hdenjoadldbtaffkwt.getWritableDatabase();
             try {
                 twrnaeywvenypmnrygdp.execSQL("INSERT INTO pawwfvkikcvyxadnxmdour (name, value) VALUES (?, ?)", new String[]{"vclvkonjrmaucnnoutgznv", "hyocppmanmyltqifhh"});
             } finally {
                 twrnaeywvenypmnrygdp.close();
                 hdenjoadldbtaffkwt.close();
             }
             File file2 = new File(getFilesDir(), "azibiisokgfmbloym.txt");
             Intent onjnosboaqmgohgsfnqb = new Intent();
             onjnosboaqmgohgsfnqb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.FfjbkjtqagjmtmvcacieyridaqActivity");
             startActivity(onjnosboaqmgohgsfnqb);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(EpjytxqxsinhrozasActivity.this, "gqcblxshnxplllfnyf", Toast.LENGTH_SHORT).show();
                 }
             });
             File file5 = new File(getFilesDir(), "kqjavcvkoxmjpyqtcpj.txt");
             FileWriter reimwnppzskitliczwiggt = null;
             try {
                 reimwnppzskitliczwiggt = new FileWriter(file5);
                 reimwnppzskitliczwiggt.write("QqC7DzaRnfpjOo0LBkoLGiwY6i3X4Ep0hdUKPVAu");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (reimwnppzskitliczwiggt != null) {
                     try {
                         reimwnppzskitliczwiggt.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             kvdpahvgbtmvynvdh.animate()
                     .alpha(0.99848795f)
                     .setDuration(751)
                     .start();
             Intent rzbkyotnwdddfevxi = new Intent();
             rzbkyotnwdddfevxi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.bsoohfytlqtpsunknoflvrw.FfjbkjtqagjmtmvcacieyridaqActivity");
             startActivity(rzbkyotnwdddfevxi);
         }
     });

xlpmsetbpzcqwfngqkrt();
Olbuvsaypazvyhjfaxciojzzl   tnbzmcdqmgiqnjtbblsz =     new Olbuvsaypazvyhjfaxciojzzl();
tnbzmcdqmgiqnjtbblsz.mrxnxpjytqzsorr ="t5eeQWQaxNg4BKEMnlLPSFiJia0lDyq54WtLWfMG7hhn5iPVRv1JvJ3myPoyOXFJXEe1GFQB2DjiceUIcuULjE92Tb2hw0W7wSccwyqtMcpCgzkalXCj78xw90bPR9PlBbouTmBMHkpkh23mtwkFrR";
tnbzmcdqmgiqnjtbblsz.zvsdwkotdydkvdoqkegkc ="eucitmmkXIjM5XbLjzWmmY3rsuFiJvnuXgItKWyn7XX";
tnbzmcdqmgiqnjtbblsz.hypzfwogoxmomwxfv ="vE4691FTw0xtrIJFNObwDhOENQLgVu01pR46EKsRzrRM55hRUMpNaUwwmFcRSNm21X0uQsZglJlUaGAz6rk4lpydcAfuOrO";
 Toast.makeText(EpjytxqxsinhrozasActivity.this,getString(R.string.ypdkctnsewchslfatzwfagqpr),Toast.LENGTH_SHORT).show();
    }
 void xlpmsetbpzcqwfngqkrt() {
     String mdlzkpxaeaewjdrmapcwrokl = "cog5ZqP9k1mHHce59BieByKBYWj9xz1JOBTPGbi";
     mdlzkpxaeaewjdrmapcwrokl = mdlzkpxaeaewjdrmapcwrokl.substring(3);
     mdlzkpxaeaewjdrmapcwrokl = mdlzkpxaeaewjdrmapcwrokl.toUpperCase();
     ewqdnvuitdjwbxin();
}
 void ewqdnvuitdjwbxin() {
     long qatcmtuvnfzzjvvns = System.currentTimeMillis();
     Date date = new Date(qatcmtuvnfzzjvvns);
     String time = date.toString();
     bcbkxbkuzflqmwhn();
}
 void bcbkxbkuzflqmwhn() {
     String gopsaysbszbauol = "1dSswPEqkcd2Xdb7bUv428YlSuGbxHeUXNjqXaTa2jU1ZWUq6TGaq0LENaNhGQ3PvAs6LbHOOcCOHIcafagDhHPDBAtYp7EBN3PMYX2JYoW0OrvwMMkhgYFbWlFr54uCX0jh1pxDYt8mWWIM1WIus9YAG1epTxjJ9ouggDa2a51kC2EkULwPuv9m";
     gopsaysbszbauol = gopsaysbszbauol.substring(3);
     gopsaysbszbauol = gopsaysbszbauol.toUpperCase();
}}