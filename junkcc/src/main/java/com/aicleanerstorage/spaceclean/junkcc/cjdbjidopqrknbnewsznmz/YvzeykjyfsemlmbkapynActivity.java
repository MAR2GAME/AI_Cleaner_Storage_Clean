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
import android.widget.TextView;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class YvzeykjyfsemlmbkapynActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vzeykjyfsemlmbkapyn);
     final View msckuymsmsczbkjskna = findViewById(R.id.jvukspfapnfmhic);
     msckuymsmsczbkjskna.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "xfhnulwshngqxnyp.txt");
             FileReader azhjscoyprbxmfubamqcazya = null;
             try {
                 if(file0.exists()) {
                     azhjscoyprbxmfubamqcazya = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (azhjscoyprbxmfubamqcazya != null) {
                     try {
                         azhjscoyprbxmfubamqcazya.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent wglbpolvsscszkbwcbk = new Intent();
             wglbpolvsscszkbwcbk.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.JktwgmemeyhgvoziActivity");
             startActivity(wglbpolvsscszkbwcbk);
             Date entlsijzndtrvtfga = new Date();
             String dnkgzauuvgklndymmoeqehx = String.valueOf(entlsijzndtrvtfga.getTime());
             System.currentTimeMillis();
             File file3 = new File(getFilesDir(), "cdbplwpxtvirffhqkzctpmaz.txt");
             SQLiteOpenHelper losbxlwciuliuhgziqoai = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "aqiuzbkuqvxumqtnpodvs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iwrqhifwxsojlsnixknqwge"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yobfkovhbiqtrkseqo = losbxlwciuliuhgziqoai.getWritableDatabase();
             try {
                 yobfkovhbiqtrkseqo.execSQL("DELETE FROM iwrqhifwxsojlsnixknqwge WHERE name = ?", new String[]{"lpksalgxsclzswvfskp", "miygcganshttsctgjxo"});
             } finally {
                 yobfkovhbiqtrkseqo.close();
                 losbxlwciuliuhgziqoai.close();
             }
             ScheduledExecutorService sdsakjogyfjpbwstihtanoy = Executors.newScheduledThreadPool(3);
             sdsakjogyfjpbwstihtanoy.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(68);
                 }
             });
             sdsakjogyfjpbwstihtanoy.shutdown();
             File file6 = new File(getFilesDir(), "chsktpzqnqjxkhtvzsasilbk.txt");
             FileReader fckpglygndqbgqwxetoh = null;
             try {
                 if(file6.exists()) {
                     fckpglygndqbgqwxetoh = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (fckpglygndqbgqwxetoh != null) {
                     try {
                         fckpglygndqbgqwxetoh.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View migurehprtiqayaljxjhgxumn = findViewById(R.id.kphfuiwynyhzgoh);
     migurehprtiqayaljxjhgxumn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent zfxlwkljrtfigcommdjsm = new Intent();
             zfxlwkljrtfigcommdjsm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.EqinezlbmlmdgmfwueddActivity");
             startActivity(zfxlwkljrtfigcommdjsm);
             SQLiteOpenHelper khgmqwyeqydwuujyk = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "uxwghxlkjifdqbu.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jmsowrdqtknpfsaab"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase sotedkcatltxsoqquasy = khgmqwyeqydwuujyk.getWritableDatabase();
             try {
                 sotedkcatltxsoqquasy.execSQL("DELETE FROM jmsowrdqtknpfsaab WHERE name = ?", new String[]{"fbpnsskzjxerekdknzfrkx", "onvqpesbpormtdomvzpl"});
             } finally {
                 sotedkcatltxsoqquasy.close();
                 khgmqwyeqydwuujyk.close();
             }
             migurehprtiqayaljxjhgxumn.animate()
                     .alpha(0.34790903f)
                     .setDuration(869)
                     .start();
             File file3 = new File(getFilesDir(), "fnmxzsxbbascqmmksubao.txt");
             ScheduledExecutorService qjqwnhjmxzufnyoyvmlploswg = Executors.newScheduledThreadPool(1);
             qjqwnhjmxzufnyoyvmlploswg.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(21);
                 }
             });
             qjqwnhjmxzufnyoyvmlploswg.shutdown();
             File file5 = new File(getFilesDir(), "kewaeqoaqwxwvhndimcsn.txt");
             if(file5.exists()) file5.delete();
             migurehprtiqayaljxjhgxumn.animate()
                     .scaleX(1.620185f)
                     .scaleY(1.620185f)
                     .setDuration(400)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(YvzeykjyfsemlmbkapynActivity.this, "jbuubhwwvstnxvja", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent gttlhwzuxrlbftjeggmnntpb = new Intent();
             gttlhwzuxrlbftjeggmnntpb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.EqinezlbmlmdgmfwueddActivity");
             startActivity(gttlhwzuxrlbftjeggmnntpb);
             Handler cgnpomfjtiurpttwnd = new Handler(Looper.getMainLooper());
             cgnpomfjtiurpttwnd.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     migurehprtiqayaljxjhgxumn.setVisibility(View.VISIBLE);
                 }
             }, 761);
         }
     });
     final View bpoutuzabthuxdwah = findViewById(R.id.jyzwqpmjoqncyri);
     bpoutuzabthuxdwah.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String mbmpxscsjpygcrflgerybigp = "akPE6e88vlxTsxb92EI9AnBE1KtDsh7LgzLSAdY5z63pyIU8scCcfgu34HxmqY0gtGBiyw0kFOn19C3u9GV0WG0Rsfh";
             mbmpxscsjpygcrflgerybigp.substring(2);
             mbmpxscsjpygcrflgerybigp.replace('e', 'n');
             File file1 = new File(getFilesDir(), "fctsjlknugqjfvczwaqu.txt");
             FileReader xacdjsidsuzrnapqgqjfhfno = null;
             try {
                 if(file1.exists()) {
                     xacdjsidsuzrnapqgqjfhfno = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (xacdjsidsuzrnapqgqjfhfno != null) {
                     try {
                         xacdjsidsuzrnapqgqjfhfno.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             bpoutuzabthuxdwah.animate()
                     .translationX(198f)
                     .translationY(186f)
                     .setDuration(333)
                     .start();
         }
     });
     final View vxhngdiatdrojuvdbls = findViewById(R.id.hswamollomrrkauojuqpfa);
     vxhngdiatdrojuvdbls.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int ipjozndxzndsnukykgkj = 677;
             Math.max(ipjozndxzndsnukykgkj, 189);
             String.valueOf(ipjozndxzndsnukykgkj);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(YvzeykjyfsemlmbkapynActivity.this, "dmwoxwnvhbaqwedahwdnywew", Toast.LENGTH_SHORT).show();
                 }
             });
             File file2 = new File(getFilesDir(), "juixawkgjhuznasymptvnbn.txt");
             if(file2.exists()) file2.delete();
             vxhngdiatdrojuvdbls.animate()
                     .translationX(119f)
                     .translationY(122f)
                     .setDuration(69)
                     .start();
             String ifrzgeoqkkaqauljf = "pAnlQ7m194jxjL6x2fX2ooUkFTKkvOlm6QDpBhYg6IC2ljj3xrNtzCdhph6e47o";
             ifrzgeoqkkaqauljf.substring(2);
             ifrzgeoqkkaqauljf.replace('e', 's');
             SQLiteOpenHelper mavzmqnudvvawfroc = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "fcmbrizuftaddxvcrdzyta.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yqmpszpmyheycumvernsl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pzgxzgtvaxajunkjns = mavzmqnudvvawfroc.getWritableDatabase();
             try {
                 pzgxzgtvaxajunkjns.execSQL("INSERT INTO yqmpszpmyheycumvernsl (name, value) VALUES (?, ?)", new String[]{"vwtzntdzzkeggnxc", "zmlnqthgocokaqcngovhkcc"});
             } finally {
                 pzgxzgtvaxajunkjns.close();
                 mavzmqnudvvawfroc.close();
             }
             vxhngdiatdrojuvdbls.animate()
                     .alpha(0.5120675f)
                     .setDuration(319)
                     .start();
             Handler copmnkqftlijluvemsgmarisi = new Handler(Looper.getMainLooper());
             copmnkqftlijluvemsgmarisi.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     vxhngdiatdrojuvdbls.setVisibility(View.VISIBLE);
                 }
             }, 436);
             int fkrkhwaqimuptkia = 903;
             Math.max(fkrkhwaqimuptkia, 411);
             String.valueOf(fkrkhwaqimuptkia);
         }
     });
     final View rdjuxfncppffugojsocxg = findViewById(R.id.xzzbbqhzckybbljsb);
     rdjuxfncppffugojsocxg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String wxiqzuibjvbczhkxgkmwrn = "kBOizlM3jPp7VKE60DIsvSvPrIUT3NK7KUXKHFgNUd0F9DwHWmB7edevs1YtnHDHWerU0uxGwJj5lOKHr20MUtUHwlDLSQvEoxt6q3XN9ZdNZsWyZLl6Thf5ZhvO";
             wxiqzuibjvbczhkxgkmwrn.substring(2);
             wxiqzuibjvbczhkxgkmwrn.replace('j', 'm');
             Intent zfipguxkfzyidfdjxjka = new Intent();
             zfipguxkfzyidfdjxjka.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.VcekcfoongaeswevxdwdchnnActivity");
             startActivity(zfipguxkfzyidfdjxjka);
             File file2 = new File(getFilesDir(), "xggbostnzlxsbqxzchni.txt");
             if(file2.exists()) file2.delete();
             Date fkwwsqdoqcqvwkm = new Date();
             String ldrghbgosriwtjqmfgjpjcdd = String.valueOf(fkwwsqdoqcqvwkm.getTime());
             System.currentTimeMillis();
             Handler ggbtuiaevybbqcjg = new Handler(Looper.getMainLooper());
             ggbtuiaevybbqcjg.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     rdjuxfncppffugojsocxg.setVisibility(View.VISIBLE);
                 }
             }, 831);
             Intent vhpmewgzqudomnisivii = new Intent();
             vhpmewgzqudomnisivii.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.VcekcfoongaeswevxdwdchnnActivity");
             startActivity(vhpmewgzqudomnisivii);
         }
     });
     final View ldcwpbfbbknhyqbpyolx = findViewById(R.id.prglvvuehlrwitpetyb);
     ldcwpbfbbknhyqbpyolx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "qtcdfgmbpvurzqddlnvdrirw.txt");
             FileReader uhhdgujsbdmwpikiddlsk = null;
             try {
                 if(file0.exists()) {
                     uhhdgujsbdmwpikiddlsk = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (uhhdgujsbdmwpikiddlsk != null) {
                     try {
                         uhhdgujsbdmwpikiddlsk.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper jhfijggcsjlxktfkoucxatged = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "smnqxmcstyakomgv.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "aqveegfqbehcditdxoqjgdr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hsekoffojjacgmfcak = jhfijggcsjlxktfkoucxatged.getWritableDatabase();
             try {
                 hsekoffojjacgmfcak.execSQL("SELECT * FROM aqveegfqbehcditdxoqjgdr WHERE name = ?", new String[]{"zhwdspxtscjzofp", "fejythtzsnakecpomgqyriogi"});
             } finally {
                 hsekoffojjacgmfcak.close();
                 jhfijggcsjlxktfkoucxatged.close();
             }
             File file2 = new File(getFilesDir(), "azfztwzqiyxzqfineux.txt");
             if(file2.exists()) file2.delete();
             ScheduledExecutorService ilfnzeekmgkmtoh = Executors.newScheduledThreadPool(2);
             ilfnzeekmgkmtoh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(57);
                 }
             });
             ilfnzeekmgkmtoh.shutdown();
             Intent kavdcjmfkouwpiqlklqodka = new Intent();
             kavdcjmfkouwpiqlklqodka.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.ZidasexabywhnpanjaActivity");
             startActivity(kavdcjmfkouwpiqlklqodka);
             SQLiteOpenHelper axofmtdjmscsncyogzvfckn = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "xkeqrjkkxeeocrduw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ldkqsygbhunqhpsxlayq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gmqwaewqbprnpsozagtycm = axofmtdjmscsncyogzvfckn.getWritableDatabase();
             try {
                 gmqwaewqbprnpsozagtycm.execSQL("DELETE FROM ldkqsygbhunqhpsxlayq WHERE name = ?", new String[]{"nnvsqkaxetoteduejj", "kfnazcpbbwndetglzmjgebwx"});
             } finally {
                 gmqwaewqbprnpsozagtycm.close();
                 axofmtdjmscsncyogzvfckn.close();
             }
             int uobevclgcstcsiensblue = 471;
             Math.max(uobevclgcstcsiensblue, 490);
             String.valueOf(uobevclgcstcsiensblue);
             Intent qbiksalzdcpaugkvn = new Intent();
             qbiksalzdcpaugkvn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.ZidasexabywhnpanjaActivity");
             startActivity(qbiksalzdcpaugkvn);
             ScheduledExecutorService oolpivlayrpoesdsqtrdxm = Executors.newScheduledThreadPool(3);
             oolpivlayrpoesdsqtrdxm.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(37);
                 }
             });
             oolpivlayrpoesdsqtrdxm.shutdown();
             SQLiteOpenHelper tafovsvdjudlhgn = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "wwbizvzlaxqnxwmosa.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ejykjutfvenustwruhrrdi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ihbjnoikrmzhddfbvhtu = tafovsvdjudlhgn.getWritableDatabase();
             try {
                 ihbjnoikrmzhddfbvhtu.execSQL("DELETE FROM ejykjutfvenustwruhrrdi WHERE name = ?", new String[]{"zluleofjhtkjglrqhtsjapom", "zjkrjchvfeodhxviyunbai"});
             } finally {
                 ihbjnoikrmzhddfbvhtu.close();
                 tafovsvdjudlhgn.close();
             }
         }
     });
     final View ereaancumplcnlfrngiretwk = findViewById(R.id.jqnbbozdmjzvftff);
     ereaancumplcnlfrngiretwk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper jeixukvqgbyhlfahqpngdubfj = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "pnhbsvfcskrigbikpjwn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "phhkjtsozvukygvlhwwjxirlf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vrohhbrihccukucgncbierzz = jeixukvqgbyhlfahqpngdubfj.getWritableDatabase();
             try {
                 vrohhbrihccukucgncbierzz.execSQL("DELETE FROM phhkjtsozvukygvlhwwjxirlf WHERE name = ?", new String[]{"lzpiqvgrsepsohgymov", "amtcvmwxgxmrgwlanbwajvely"});
             } finally {
                 vrohhbrihccukucgncbierzz.close();
                 jeixukvqgbyhlfahqpngdubfj.close();
             }
             ereaancumplcnlfrngiretwk.animate()
                     .alpha(0.5784086f)
                     .setDuration(790)
                     .start();
             String vfywrjtyvqaroiafmjldejw = "u3nTBq2GV4ycpmwK6PP72wOqIhxpFUBRl2aIvvfrgSssXkLzPM1AGLfrmgBplO2AcB3e8wEnXv8lx9U";
             vfywrjtyvqaroiafmjldejw.substring(1);
             vfywrjtyvqaroiafmjldejw.replace('j', 's');
             File file3 = new File(getFilesDir(), "geaqpgwbpanmeaytdu.txt");
             FileWriter dfdnytxozrgikttabrm = null;
             try {
                 dfdnytxozrgikttabrm = new FileWriter(file3);
                 dfdnytxozrgikttabrm.write("KfNuxO8Qx2s7sjbPqXxyhDT04imnoxFydPjY9dJiQNLIsThQzs8V10C4JtGXrvRdxkKws0eU6T6COgXmf61R84FJ2iqZhaXqsMhkk07fzo7KmQs1Ebj855GtYDTLjH5hoyKBGzBgFCF9iKOaiUjE1mkKyAjjZBXwQya8lNypzKLfP9nBE3ozQoDbM4xTP85OM");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (dfdnytxozrgikttabrm != null) {
                     try {
                         dfdnytxozrgikttabrm.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date vaaehhnvykclzlpf = new Date();
             String fbkvgbgxnqeornoypsspg = String.valueOf(vaaehhnvykclzlpf.getTime());
             System.currentTimeMillis();
         }
     });
     final View ugsycvifjcmmtglll = findViewById(R.id.phdzkfmfwhxcmzmn);
     ugsycvifjcmmtglll.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tklvrytkyfvtshd = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "rspsvkzobgnonpgcqy.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xpfcxvofsauomrfxmhqzczr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase flabcnbqdnecffpyxafjf = tklvrytkyfvtshd.getWritableDatabase();
             try {
                 flabcnbqdnecffpyxafjf.execSQL("UPDATE xpfcxvofsauomrfxmhqzczr SET value = ? WHERE name = ?", new String[]{"jucxueyepmetcpscyt", "bqyqfcnrjdulheiqctpkra"});
             } finally {
                 flabcnbqdnecffpyxafjf.close();
                 tklvrytkyfvtshd.close();
             }
             ugsycvifjcmmtglll.animate()
                     .rotation(33f)
                     .setDuration(953)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(YvzeykjyfsemlmbkapynActivity.this, "rhjoxkjrvruolplblyq", Toast.LENGTH_SHORT).show();
                 }
             });
             File file3 = new File(getFilesDir(), "kheiuhobdqrpmadgwoewvdlkc.txt");
             FileWriter teiwrbqjmzthvhzlqaqsqo = null;
             try {
                 teiwrbqjmzthvhzlqaqsqo = new FileWriter(file3);
                 teiwrbqjmzthvhzlqaqsqo.write("akZz7b3v7e8OnL1yaq3k9JRZmWFiGatUUvV");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (teiwrbqjmzthvhzlqaqsqo != null) {
                     try {
                         teiwrbqjmzthvhzlqaqsqo.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ugsycvifjcmmtglll.animate()
                     .rotation(97f)
                     .setDuration(289)
                     .start();
             Intent fmyaqimmchtuaekemz = new Intent();
             fmyaqimmchtuaekemz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.YahtowfplnpbrfiifmtzoActivity");
             startActivity(fmyaqimmchtuaekemz);
             String enwtwalummgeomn = "5pkxHUHbjKTAQ0yrnK4zldw6jEP9ENiukTfdeGgYOcP2ge2CcanVA5NqsuvlEBmKWZ083KcktIXdDZyNsFUkyz442VvyOajEBrUJKn19N5yTrEwz4PXgrXuIM";
             enwtwalummgeomn.substring(1);
             enwtwalummgeomn.replace('o', 'k');
             Handler quhzgaauuawkunpgjzyidgc = new Handler(Looper.getMainLooper());
             quhzgaauuawkunpgjzyidgc.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ugsycvifjcmmtglll.setVisibility(View.VISIBLE);
                 }
             }, 963);
         }
     });
     final View fnondimbojgnzpskozv = findViewById(R.id.gybfeopsurnzaocjyxorwn);
     fnondimbojgnzpskozv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper smojipdnoywkgaddk = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "cxoirextflmrtlrekiaeesu.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mrezxiowekdblszb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qmlhuvukfhsevjtybjexekkw = smojipdnoywkgaddk.getWritableDatabase();
             try {
                 qmlhuvukfhsevjtybjexekkw.execSQL("INSERT INTO mrezxiowekdblszb (name, value) VALUES (?, ?)", new String[]{"shnazqpjacjtufv", "ubqxalcxkbeswmdexmshwnz"});
             } finally {
                 qmlhuvukfhsevjtybjexekkw.close();
                 smojipdnoywkgaddk.close();
             }
             Intent nlpxqfhenybkoufhiks = new Intent();
             nlpxqfhenybkoufhiks.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.KklvlqrjntvuqoerActivity");
             startActivity(nlpxqfhenybkoufhiks);
             SQLiteOpenHelper yvdhafndvzokvet = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "rpxzzevvdttcxsjomu.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vrjhsgteqjbklvjyjl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase geadrgstxqnnbqf = yvdhafndvzokvet.getWritableDatabase();
             try {
                 geadrgstxqnnbqf.execSQL("DELETE FROM vrjhsgteqjbklvjyjl WHERE name = ?", new String[]{"nfchmcdrscwimtqrgbhfkiayz", "nrvazhyxmowteljpo"});
             } finally {
                 geadrgstxqnnbqf.close();
                 yvdhafndvzokvet.close();
             }
             fnondimbojgnzpskozv.animate()
                     .scaleX(0.9943907f)
                     .scaleY(0.9943907f)
                     .setDuration(586)
                     .start();
             Handler zxdlcoignrikgmxpfcsategsl = new Handler(Looper.getMainLooper());
             zxdlcoignrikgmxpfcsategsl.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     fnondimbojgnzpskozv.setVisibility(View.VISIBLE);
                 }
             }, 766);
         }
     });
     final View zgntrzfuyghrolsrfnogn = findViewById(R.id.pfyhridgwfwkvnbhpt);
     zgntrzfuyghrolsrfnogn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper ryqfroptrrupnlquffpfgd = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "rhopwlqnbytyzoefocjdrefs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ecxqpukqskkqudadutl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase aeojtkhefjfdekxitgm = ryqfroptrrupnlquffpfgd.getWritableDatabase();
             try {
                 aeojtkhefjfdekxitgm.execSQL("UPDATE ecxqpukqskkqudadutl SET value = ? WHERE name = ?", new String[]{"neonvitgbwlyqgnwxclyzem", "thjkuwlejaqehuvdedqlsj"});
             } finally {
                 aeojtkhefjfdekxitgm.close();
                 ryqfroptrrupnlquffpfgd.close();
             }
             int kkfibcnnfoofaaknfk = 236;
             Math.max(kkfibcnnfoofaaknfk, 21);
             String.valueOf(kkfibcnnfoofaaknfk);
             ScheduledExecutorService vqlfoyujmbtztfptouxgi = Executors.newScheduledThreadPool(1);
             vqlfoyujmbtztfptouxgi.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(81);
                 }
             });
             vqlfoyujmbtztfptouxgi.shutdown();
             Intent hyaoqxoekjnpcbi = new Intent();
             hyaoqxoekjnpcbi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.XuknskbesanxlneeroActivity");
             startActivity(hyaoqxoekjnpcbi);
             Date awenaiyjvnvtrkddsf = new Date();
             String vlvtzmapuvqmevd = String.valueOf(awenaiyjvnvtrkddsf.getTime());
             System.currentTimeMillis();
             Intent vtqmpluznifbjwghzdliqtl = new Intent();
             vtqmpluznifbjwghzdliqtl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.XuknskbesanxlneeroActivity");
             startActivity(vtqmpluznifbjwghzdliqtl);
             SQLiteOpenHelper ljafahpneahoudpathkryurnv = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "toxugyqcekpmuufvkiypd.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hpkedsgfzdbbmheo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase uaxgaiggefsbnrnbqjegjru = ljafahpneahoudpathkryurnv.getWritableDatabase();
             try {
                 uaxgaiggefsbnrnbqjegjru.execSQL("DELETE FROM hpkedsgfzdbbmheo WHERE name = ?", new String[]{"rfgzvrmflkqquhaxptwglvw", "qdkkhjpyadmqmuvd"});
             } finally {
                 uaxgaiggefsbnrnbqjegjru.close();
                 ljafahpneahoudpathkryurnv.close();
             }
         }
     });
     final View hdgsnqmkafmbvvyupcg = findViewById(R.id.fxdkznzyrrfjxwrlgobwv);
     hdgsnqmkafmbvvyupcg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "szretbxybcvofyysgrrfzah.txt");
             Date uoyxcubwzsmxsaa = new Date();
             String nfuhutesrcmtincbnt = String.valueOf(uoyxcubwzsmxsaa.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper nugmmajxtymqwmqni = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "jylonwellvpghdnyuiajeiknv.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cujxqnsolgzvbvlkwknristyh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pyaxpxwlqswcbtkuoviyk = nugmmajxtymqwmqni.getWritableDatabase();
             try {
                 pyaxpxwlqswcbtkuoviyk.execSQL("UPDATE cujxqnsolgzvbvlkwknristyh SET value = ? WHERE name = ?", new String[]{"hipkbmaqsfmkhgbogrtfeiu", "ksklxsvqgeecjfp"});
             } finally {
                 pyaxpxwlqswcbtkuoviyk.close();
                 nugmmajxtymqwmqni.close();
             }
             String lzyzirackhsxthizlyjyky = "rNsM8xUPj59";
             lzyzirackhsxthizlyjyky.substring(3);
             lzyzirackhsxthizlyjyky.replace('i', 'd');
             File file4 = new File(getFilesDir(), "tumocawnrdnafvodlgnrpe.txt");
             if(file4.exists()) file4.delete();
         }
     });
     final View qqaqqnuffmqexvkv = findViewById(R.id.tegaxwmbbyuuoqhc);
     qqaqqnuffmqexvkv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService rbmsznfwhmdlaqoasr = Executors.newScheduledThreadPool(1);
             rbmsznfwhmdlaqoasr.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(23);
                 }
             });
             rbmsznfwhmdlaqoasr.shutdown();
             SQLiteOpenHelper nwhablleymiylmpa = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "olyjwoxwdnxmosunbcgq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "niwbindmvxgicnaogfonfp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ikufsnmrxuhktlfp = nwhablleymiylmpa.getWritableDatabase();
             try {
                 ikufsnmrxuhktlfp.execSQL("UPDATE niwbindmvxgicnaogfonfp SET value = ? WHERE name = ?", new String[]{"cduetpzwxnjuwwzrxzjqd", "jyitojckegqwxpdfgqy"});
             } finally {
                 ikufsnmrxuhktlfp.close();
                 nwhablleymiylmpa.close();
             }
             qqaqqnuffmqexvkv.animate()
                     .rotation(13f)
                     .setDuration(957)
                     .start();
             Intent xxgcpkcoqvhcwtjxubj = new Intent();
             xxgcpkcoqvhcwtjxubj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.RpopfshpgmuwtvgzjwlbrhiorActivity");
             startActivity(xxgcpkcoqvhcwtjxubj);
             Date hxsehkrbwgidhvcopycxjdwtu = new Date();
             String ibpslccvcvjjgbhz = String.valueOf(hxsehkrbwgidhvcopycxjdwtu.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper gucxurmevryprcarp = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "didmjrnbvxbpydsqda.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jnnnwwmubcptwetcgjrqep"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wtegxytqicvricta = gucxurmevryprcarp.getWritableDatabase();
             try {
                 wtegxytqicvricta.execSQL("UPDATE jnnnwwmubcptwetcgjrqep SET value = ? WHERE name = ?", new String[]{"dsyiosgykufloowqtccdg", "xdtulwzewwvabvxnxph"});
             } finally {
                 wtegxytqicvricta.close();
                 gucxurmevryprcarp.close();
             }
             Intent szsqidcowoqhrluwxsht = new Intent();
             szsqidcowoqhrluwxsht.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.RpopfshpgmuwtvgzjwlbrhiorActivity");
             startActivity(szsqidcowoqhrluwxsht);
             ScheduledExecutorService bxlarirgjtyqawlda = Executors.newScheduledThreadPool(4);
             bxlarirgjtyqawlda.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(79);
                 }
             });
             bxlarirgjtyqawlda.shutdown();
         }
     });
     final View ywrhjljbmiytrnycyk = findViewById(R.id.nbftttdasobmbqxlympgfiii);
     ywrhjljbmiytrnycyk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent dzfglgqfdwzmsacn = new Intent();
             dzfglgqfdwzmsacn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.GcuiyvivbckqfpgzActivity");
             startActivity(dzfglgqfdwzmsacn);
             File file1 = new File(getFilesDir(), "ebwvwyeowxhkgsgvupexhmkiz.txt");
             FileWriter fiknqhdqtdotmcxlrxwr = null;
             try {
                 fiknqhdqtdotmcxlrxwr = new FileWriter(file1);
                 fiknqhdqtdotmcxlrxwr.write("U46F65WAGNd1d6fnOvQAhud");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (fiknqhdqtdotmcxlrxwr != null) {
                     try {
                         fiknqhdqtdotmcxlrxwr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent uuxzyddrpjnroobtcr = new Intent();
             uuxzyddrpjnroobtcr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.GcuiyvivbckqfpgzActivity");
             startActivity(uuxzyddrpjnroobtcr);
             String dycxfceqxwklhbbsj = "1QWAqzhGbqtTk6lHhFyHjgf9RBsmDIgD2MgMAo4eI575s6SU32EuM4IucbVyVjEs5CZNdXbHrOfWDfjVkarBhKh142GDy5y9p";
             dycxfceqxwklhbbsj.substring(2);
             dycxfceqxwklhbbsj.replace('g', 'o');
         }
     });
     final View mlvrtnrpnecbheh = findViewById(R.id.vtnjattijocnurgyj);
     mlvrtnrpnecbheh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService ptgttiyefpwfptv = Executors.newScheduledThreadPool(1);
             ptgttiyefpwfptv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(79);
                 }
             });
             ptgttiyefpwfptv.shutdown();
             Date gdcgyndabethxfqkqhsgxnoa = new Date();
             String lbkgyfnvhznslgvbpfofnmv = String.valueOf(gdcgyndabethxfqkqhsgxnoa.getTime());
             System.currentTimeMillis();
             File file2 = new File(getFilesDir(), "jtbxogvflpdsvnkxuthbdimh.txt");
             FileReader ktybpgowrvtbxhciiqwt = null;
             try {
                 if(file2.exists()) {
                     ktybpgowrvtbxhciiqwt = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ktybpgowrvtbxhciiqwt != null) {
                     try {
                         ktybpgowrvtbxhciiqwt.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Handler bweigoaxzxficfhajjr = new Handler(Looper.getMainLooper());
             bweigoaxzxficfhajjr.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     mlvrtnrpnecbheh.setVisibility(View.VISIBLE);
                 }
             }, 74);
             SQLiteOpenHelper zyeecybfteucmpqgcarmj = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "nuuqhaksacdyqkscbtoltb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "snriojznhkqwtihmg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase svqjvmpatbxyxhrcjinn = zyeecybfteucmpqgcarmj.getWritableDatabase();
             try {
                 svqjvmpatbxyxhrcjinn.execSQL("UPDATE snriojznhkqwtihmg SET value = ? WHERE name = ?", new String[]{"wpwrbdrsbmhumerqhxe", "bymeowgjpoqfwwlkk"});
             } finally {
                 svqjvmpatbxyxhrcjinn.close();
                 zyeecybfteucmpqgcarmj.close();
             }
         }
     });
     final View plegiifpucfqvgxvecrrlz = findViewById(R.id.krzuwrlvbktrbjg);
     plegiifpucfqvgxvecrrlz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent cnookocmcqejdmmpds = new Intent();
             cnookocmcqejdmmpds.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.GbpoyicexhdlyhcjqtbjinxActivity");
             startActivity(cnookocmcqejdmmpds);
             SQLiteOpenHelper vjynmkdyngfdhjrawkj = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "qrdpdsopzskvlfkalhokhjpe.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zskozslhywzgxihdoyxayz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qodybuwhbhoeskjsfb = vjynmkdyngfdhjrawkj.getWritableDatabase();
             try {
                 qodybuwhbhoeskjsfb.execSQL("INSERT INTO zskozslhywzgxihdoyxayz (name, value) VALUES (?, ?)", new String[]{"yipjcplpgwrmtaxqxrvbgfaka", "koyndnrovpyscaayyd"});
             } finally {
                 qodybuwhbhoeskjsfb.close();
                 vjynmkdyngfdhjrawkj.close();
             }
             File file2 = new File(getFilesDir(), "bkzlzpdyrcygmdbxa.txt");
             FileReader ebovlkvrqspbxmhcikcjmewax = null;
             try {
                 if(file2.exists()) {
                     ebovlkvrqspbxmhcikcjmewax = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ebovlkvrqspbxmhcikcjmewax != null) {
                     try {
                         ebovlkvrqspbxmhcikcjmewax.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date dqncazkukbzrpxjfsnlk = new Date();
             String rcmsnhijgjnstfedelzgj = String.valueOf(dqncazkukbzrpxjfsnlk.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper whmsgsoqioqgupavbl = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "fiacxovzblkuxgulvg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "drjfqxqdcxvwimhontjyqtdh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xyskzbvrnwlgkaazeyd = whmsgsoqioqgupavbl.getWritableDatabase();
             try {
                 xyskzbvrnwlgkaazeyd.execSQL("SELECT * FROM drjfqxqdcxvwimhontjyqtdh WHERE name = ?", new String[]{"zmzsvbpwqjzaupicuhvzpfia", "hunpvjvpzhghioxdqsi"});
             } finally {
                 xyskzbvrnwlgkaazeyd.close();
                 whmsgsoqioqgupavbl.close();
             }
             Intent axyguqogihqoemccuqwmqrdwu = new Intent();
             axyguqogihqoemccuqwmqrdwu.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.GbpoyicexhdlyhcjqtbjinxActivity");
             startActivity(axyguqogihqoemccuqwmqrdwu);
         }
     });
     final View rjhnvvldyofgcshvypupa = findViewById(R.id.ssinkfzrjoahgjvrw);
     rjhnvvldyofgcshvypupa.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent aaavuqtqnyxpegkmjj = new Intent();
             aaavuqtqnyxpegkmjj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.DlmlhszeiozghiatjkdqActivity");
             startActivity(aaavuqtqnyxpegkmjj);
             SQLiteOpenHelper zcvzemshpiuinlodgjhrtme = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "pizoddygfjvsaejievxhjtavr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "sqyxfcaqhnniiwsyta"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gbjwynlgzmbzmdggckgsbbg = zcvzemshpiuinlodgjhrtme.getWritableDatabase();
             try {
                 gbjwynlgzmbzmdggckgsbbg.execSQL("INSERT INTO sqyxfcaqhnniiwsyta (name, value) VALUES (?, ?)", new String[]{"tcliaelwawoyirseqytqm", "sjjbxmvnekkymlcj"});
             } finally {
                 gbjwynlgzmbzmdggckgsbbg.close();
                 zcvzemshpiuinlodgjhrtme.close();
             }
             rjhnvvldyofgcshvypupa.animate()
                     .scaleX(1.8350765f)
                     .scaleY(1.8350765f)
                     .setDuration(586)
                     .start();
         }
     });
     final View ywkgiydoofxkjizfocfhbszn = findViewById(R.id.yvhfnkleoorgvsip);
     ywkgiydoofxkjizfocfhbszn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService mzklallbrdcerzkivf = Executors.newScheduledThreadPool(4);
             mzklallbrdcerzkivf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(22);
                 }
             });
             mzklallbrdcerzkivf.shutdown();
             ywkgiydoofxkjizfocfhbszn.animate()
                     .alpha(0.023483753f)
                     .setDuration(647)
                     .start();
             Handler ayedalqktomochzfunvykso = new Handler(Looper.getMainLooper());
             ayedalqktomochzfunvykso.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ywkgiydoofxkjizfocfhbszn.setVisibility(View.VISIBLE);
                 }
             }, 375);
             File file3 = new File(getFilesDir(), "vmpdxflyjkwyaswpviftnyhem.txt");
             SQLiteOpenHelper ilozkomqwmvkmymtdlvtfbig = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "oqbdbiqplubkekcgy.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wfcaliicqlbxyokixhivlgvj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase geayzwrinzfupkezeyek = ilozkomqwmvkmymtdlvtfbig.getWritableDatabase();
             try {
                 geayzwrinzfupkezeyek.execSQL("SELECT * FROM wfcaliicqlbxyokixhivlgvj WHERE name = ?", new String[]{"skuoobywehjerddlxradsx", "aczoozzghxtzhzaso"});
             } finally {
                 geayzwrinzfupkezeyek.close();
                 ilozkomqwmvkmymtdlvtfbig.close();
             }
             File file5 = new File(getFilesDir(), "ugcdxjwfmcjgemj.txt");
             if(file5.exists()) file5.delete();
             SQLiteOpenHelper umoigmvbfmtyncc = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "reqsybfflfkunaaclpw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pxlebaoojstyghwd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase covnvcbqicgvmlllspxlxcjqx = umoigmvbfmtyncc.getWritableDatabase();
             try {
                 covnvcbqicgvmlllspxlxcjqx.execSQL("UPDATE pxlebaoojstyghwd SET value = ? WHERE name = ?", new String[]{"mpiskdwvucnlgrefsobndtmo", "pluiqctbztidmzrcssbfo"});
             } finally {
                 covnvcbqicgvmlllspxlxcjqx.close();
                 umoigmvbfmtyncc.close();
             }
         }
     });
     final View jtjfnmtxuapxpufhvyfg = findViewById(R.id.vhjzhlymwdnmujmohxi);
     jtjfnmtxuapxpufhvyfg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService bhzikmcqcnjtpsqivwgkzkf = Executors.newScheduledThreadPool(2);
             bhzikmcqcnjtpsqivwgkzkf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(92);
                 }
             });
             bhzikmcqcnjtpsqivwgkzkf.shutdown();
             SQLiteOpenHelper veipaiczzyxhxzretc = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "mkstjhuyqikgwxkldhtyq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iifyytgfhvsheazp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bsdtgebumesecaerbeu = veipaiczzyxhxzretc.getWritableDatabase();
             try {
                 bsdtgebumesecaerbeu.execSQL("DELETE FROM iifyytgfhvsheazp WHERE name = ?", new String[]{"lkrttuuwocrktbvzxlfthqjj", "qpcredqyfuslndjvpedzufryz"});
             } finally {
                 bsdtgebumesecaerbeu.close();
                 veipaiczzyxhxzretc.close();
             }
             String qnzlhtqndtefnlz = "jIpYDWdO8JZyM9DcppKN8P0OWI6QRb1KAbca019lEDBDxZHRwEDNZBnfEsZVfaNEt4PR2i87IFDxqKwROBmng7PMCQITu0dbGArzvl4oESg1ICiywRXJ77ZfLFXAGFXyGEG27pOLVIguLrxjV2RD8eZFUDc6FlkMsumAh09rVLTsLZLXGIhw9qBv";
             qnzlhtqndtefnlz.substring(3);
             qnzlhtqndtefnlz.replace('h', 'c');
             Intent sdnvfdsgopumjcmnkgenhzfg = new Intent();
             sdnvfdsgopumjcmnkgenhzfg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.StsjchkgqcylogekdofjjActivity");
             startActivity(sdnvfdsgopumjcmnkgenhzfg);
             jtjfnmtxuapxpufhvyfg.animate()
                     .alpha(0.103060365f)
                     .setDuration(546)
                     .start();
             SQLiteOpenHelper dpapgzmbukhnnsmztxwljjrpk = new SQLiteOpenHelper(YvzeykjyfsemlmbkapynActivity.this, "axydlvwxplwvqlaicegkgpz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "djqmiyjtqzocwbkotnrd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase agyrlokaynqbkrmifjipuet = dpapgzmbukhnnsmztxwljjrpk.getWritableDatabase();
             try {
                 agyrlokaynqbkrmifjipuet.execSQL("SELECT * FROM djqmiyjtqzocwbkotnrd WHERE name = ?", new String[]{"vksfnkrslhhnehxeiagzklqvn", "zcqbjwzoonquvtzmvbwsl"});
             } finally {
                 agyrlokaynqbkrmifjipuet.close();
                 dpapgzmbukhnnsmztxwljjrpk.close();
             }
             jtjfnmtxuapxpufhvyfg.animate()
                     .translationX(177f)
                     .translationY(167f)
                     .setDuration(233)
                     .start();
         }
     });
     final View ocypgmcjminnalwqo = findViewById(R.id.ppqeuipmunuyuohmtvbhg);
     ocypgmcjminnalwqo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent omfotkuvqjusarxoimacywv = new Intent();
             omfotkuvqjusarxoimacywv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.EppladfmzwkkwxlsActivity");
             startActivity(omfotkuvqjusarxoimacywv);
             ocypgmcjminnalwqo.animate()
                     .alpha(0.9480102f)
                     .setDuration(59)
                     .start();
             Intent kloqdnnihvabswatefrlbn = new Intent();
             kloqdnnihvabswatefrlbn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.cjdbjidopqrknbnewsznmz.EppladfmzwkkwxlsActivity");
             startActivity(kloqdnnihvabswatefrlbn);
         }
     });

judnmbapmarrsgsbnj();
Jkeiorxqqzrdgfcvirvt   pqwhzhlwgmvdxzeiaovrksr =     new Jkeiorxqqzrdgfcvirvt();
pqwhzhlwgmvdxzeiaovrksr.ouzmarwttrvchrd ="d2wB5JgJZQCd7PxlrZj2Ecq4wCzXLQbQv82PqHJpqcUBgsKlvosj6XWfl97TE0txQANhT31";
 Toast.makeText(YvzeykjyfsemlmbkapynActivity.this,getString(R.string.ygypxtxafnpscyfjz),Toast.LENGTH_SHORT).show();
    }
 void judnmbapmarrsgsbnj() {
     int fupwlfpbiwpdqrgmglmtdj = 561;
     double result = Math.sqrt(fupwlfpbiwpdqrgmglmtdj);
     result = Math.pow(result, 2);
}}