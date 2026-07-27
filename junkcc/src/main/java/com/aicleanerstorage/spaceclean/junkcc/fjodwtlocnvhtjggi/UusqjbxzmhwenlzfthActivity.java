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
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class UusqjbxzmhwenlzfthActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_usqjbxzmhwenlzfth);
     final View hhujvpqwykyshcrhdvne = findViewById(R.id.grcxctifsbyqwsnkgqeoljjn);
     hhujvpqwykyshcrhdvne.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             hhujvpqwykyshcrhdvne.animate()
                     .rotation(194f)
                     .setDuration(168)
                     .start();
             ScheduledExecutorService zppyvudnoffvnwvlizdynshpa = Executors.newScheduledThreadPool(3);
             zppyvudnoffvnwvlizdynshpa.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(47);
                 }
             });
             zppyvudnoffvnwvlizdynshpa.shutdown();
             Intent jdoedxaezoizlnplkpckvw = new Intent();
             jdoedxaezoizlnplkpckvw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.DukaehiagkfstsgaryyActivity");
             startActivity(jdoedxaezoizlnplkpckvw);
             hhujvpqwykyshcrhdvne.animate()
                     .alpha(0.07220584f)
                     .setDuration(664)
                     .start();
             Intent vofbhvewltpqvxmqhdxxi = new Intent();
             vofbhvewltpqvxmqhdxxi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.DukaehiagkfstsgaryyActivity");
             startActivity(vofbhvewltpqvxmqhdxxi);
         }
     });
     final View lrrvhhypglczxquepuajdspd = findViewById(R.id.pvviiqluxcdxittg);
     lrrvhhypglczxquepuajdspd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "tgnrkkrgalttoqhizz.txt");
             if(file0.exists()) file0.delete();
             Handler aavibczsxsoyggzhkjqq = new Handler(Looper.getMainLooper());
             aavibczsxsoyggzhkjqq.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     lrrvhhypglczxquepuajdspd.setVisibility(View.VISIBLE);
                 }
             }, 420);
             Intent thhxzuilyamcoczupcisgq = new Intent();
             thhxzuilyamcoczupcisgq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.VayjyhtsclwbybiomyActivity");
             startActivity(thhxzuilyamcoczupcisgq);
             ScheduledExecutorService ifnxrghnhgwjebcpbxe = Executors.newScheduledThreadPool(2);
             ifnxrghnhgwjebcpbxe.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(1);
                 }
             });
             ifnxrghnhgwjebcpbxe.shutdown();
             File file4 = new File(getFilesDir(), "splcoxjzkaxizkpaptqgoozqy.txt");
             SQLiteOpenHelper vtxgfgccubpxvmhogyr = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "dfzvoowdobkfglcevj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kztfshmnoyjqtejvxabcim"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase uukhzobvywduzcx = vtxgfgccubpxvmhogyr.getWritableDatabase();
             try {
                 uukhzobvywduzcx.execSQL("DELETE FROM kztfshmnoyjqtejvxabcim WHERE name = ?", new String[]{"lqlyfssnygnvpvhvipplkmo", "ajptjboyoagcrxdwxoql"});
             } finally {
                 uukhzobvywduzcx.close();
                 vtxgfgccubpxvmhogyr.close();
             }
             File file6 = new File(getFilesDir(), "rsfdvkeexddqrjnvltvufojx.txt");
             FileReader onagskcjnkzadzajdgjytfdgu = null;
             try {
                 if(file6.exists()) {
                     onagskcjnkzadzajdgjytfdgu = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (onagskcjnkzadzajdgjytfdgu != null) {
                     try {
                         onagskcjnkzadzajdgjytfdgu.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper arybjqfqdbyvrawgv = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "bopvxpitdggdlfxsqtcil.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qfgjpoakcnjutlwhky"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jrpahurjqnozywyi = arybjqfqdbyvrawgv.getWritableDatabase();
             try {
                 jrpahurjqnozywyi.execSQL("DELETE FROM qfgjpoakcnjutlwhky WHERE name = ?", new String[]{"kqvgdamzhokvytkl", "mbedufxmrqxkfdc"});
             } finally {
                 jrpahurjqnozywyi.close();
                 arybjqfqdbyvrawgv.close();
             }
             ScheduledExecutorService adytktrdttooteslef = Executors.newScheduledThreadPool(3);
             adytktrdttooteslef.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(66);
                 }
             });
             adytktrdttooteslef.shutdown();
             File file9 = new File(getFilesDir(), "vfslhdtufgvpsppfh.txt");
             FileWriter hwpxognrydonkar = null;
             try {
                 hwpxognrydonkar = new FileWriter(file9);
                 hwpxognrydonkar.write("Uh6SJ3AqwTaukd6tHQRkMIwWQLV7xKCDKz4vib0EayJ2FF7zlWoQC9n5yDXcBLG88YVwndRoG4O5Tun4APZCXkmXBuBgly2LnkqsxvI1sJYuczFy8YCDhVts0");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (hwpxognrydonkar != null) {
                     try {
                         hwpxognrydonkar.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View jrrfpinknpuitbicpaj = findViewById(R.id.ehjskofsrqbrwvxzrgkwbhg);
     jrrfpinknpuitbicpaj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper jlpetoabsomsnwltvhr = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "wdubzqqdddmesfbw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jjmqkdvnoutljbozb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase irzeeqqddujmjrn = jlpetoabsomsnwltvhr.getWritableDatabase();
             try {
                 irzeeqqddujmjrn.execSQL("INSERT INTO jjmqkdvnoutljbozb (name, value) VALUES (?, ?)", new String[]{"mbiphwdopwtvgzcnoqnwmn", "rijamfkzlhlqnnnfirwtoxgjo"});
             } finally {
                 irzeeqqddujmjrn.close();
                 jlpetoabsomsnwltvhr.close();
             }
             jrrfpinknpuitbicpaj.animate()
                     .translationX(196f)
                     .translationY(122f)
                     .setDuration(625)
                     .start();
             Intent ngpkmcakhltkpiaideserivq = new Intent();
             ngpkmcakhltkpiaideserivq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.ImntksattwdrfcjamqvijekActivity");
             startActivity(ngpkmcakhltkpiaideserivq);
             File file3 = new File(getFilesDir(), "vvgdkjfvvopkspqxpfbwpfx.txt");
             FileReader sfjkujrjakgtmuysaqj = null;
             try {
                 if(file3.exists()) {
                     sfjkujrjakgtmuysaqj = new FileReader(file3);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (sfjkujrjakgtmuysaqj != null) {
                     try {
                         sfjkujrjakgtmuysaqj.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View epegzvhacmzlzlkrukmkun = findViewById(R.id.jklscznwotawcpcvel);
     epegzvhacmzlzlkrukmkun.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "rpimzgftmuifueqcyplu.txt");
             if(file0.exists()) file0.delete();
             ScheduledExecutorService bjtytfuxstjuppvkuoib = Executors.newScheduledThreadPool(2);
             bjtytfuxstjuppvkuoib.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(28);
                 }
             });
             bjtytfuxstjuppvkuoib.shutdown();
             epegzvhacmzlzlkrukmkun.animate()
                     .translationX(91f)
                     .translationY(18f)
                     .setDuration(88)
                     .start();
             File file3 = new File(getFilesDir(), "xgityrhbhccbhfttfrs.txt");
         }
     });
     final View dumqpxoryquexciceagcekh = findViewById(R.id.aelyndjzhtvokfzisz);
     dumqpxoryquexciceagcekh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "wrskwcydgyasenpawumd.txt");
             FileWriter aonjlzffqzgiovtnc = null;
             try {
                 aonjlzffqzgiovtnc = new FileWriter(file0);
                 aonjlzffqzgiovtnc.write("GR0zOXAaS7ATo7FJmadjuW6uDT6");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (aonjlzffqzgiovtnc != null) {
                     try {
                         aonjlzffqzgiovtnc.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService norevrffoudoknhvo = Executors.newScheduledThreadPool(2);
             norevrffoudoknhvo.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(66);
                 }
             });
             norevrffoudoknhvo.shutdown();
         }
     });
     final View gzepcnrubsoomohfgxnovr = findViewById(R.id.ugtxshkmzzykpmjkgdlscp);
     gzepcnrubsoomohfgxnovr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int lhsxqcibbmzxogibzzuvvvd = 747;
             Math.max(lhsxqcibbmzxogibzzuvvvd, 51);
             String.valueOf(lhsxqcibbmzxogibzzuvvvd);
             File file1 = new File(getFilesDir(), "ikccnlijzsmltpsrmazpg.txt");
             if(file1.exists()) file1.delete();
             Intent ynvstansjdhxxzmibpwe = new Intent();
             ynvstansjdhxxzmibpwe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.GpjmfzkmtyknemxzgfActivity");
             startActivity(ynvstansjdhxxzmibpwe);
             SQLiteOpenHelper qjvsmdwdqzhgshevcamy = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "cexqcjkjvviqhqiajrvg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lkijizzojqskqnoongx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase oqrhmtfkazzzacpl = qjvsmdwdqzhgshevcamy.getWritableDatabase();
             try {
                 oqrhmtfkazzzacpl.execSQL("SELECT * FROM lkijizzojqskqnoongx WHERE name = ?", new String[]{"cpqgbhmtaczmzddhcc", "hljttcapizewuwmxzceuvm"});
             } finally {
                 oqrhmtfkazzzacpl.close();
                 qjvsmdwdqzhgshevcamy.close();
             }
             int uglyvbleuwyemkzuocr = 77;
             Math.max(uglyvbleuwyemkzuocr, 340);
             String.valueOf(uglyvbleuwyemkzuocr);
             ScheduledExecutorService vpvmtnxjjqjfuveavdscqoh = Executors.newScheduledThreadPool(2);
             vpvmtnxjjqjfuveavdscqoh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(16);
                 }
             });
             vpvmtnxjjqjfuveavdscqoh.shutdown();
             gzepcnrubsoomohfgxnovr.animate()
                     .alpha(0.22925311f)
                     .setDuration(628)
                     .start();
             ScheduledExecutorService holfuporuogqlyvr = Executors.newScheduledThreadPool(2);
             holfuporuogqlyvr.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(84);
                 }
             });
             holfuporuogqlyvr.shutdown();
         }
     });
     final View qhldahqtkllglveedbmu = findViewById(R.id.lograjcwvtqbgzqo);
     qhldahqtkllglveedbmu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int qzpaqweehmwpmiawohroxfiu = 523;
             Math.max(qzpaqweehmwpmiawohroxfiu, 31);
             String.valueOf(qzpaqweehmwpmiawohroxfiu);
             File file1 = new File(getFilesDir(), "jgyqenzugawpyjta.txt");
             FileReader pohmsfgimfwwobstgjuldy = null;
             try {
                 if(file1.exists()) {
                     pohmsfgimfwwobstgjuldy = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (pohmsfgimfwwobstgjuldy != null) {
                     try {
                         pohmsfgimfwwobstgjuldy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService ksmdveilkdjrreihblwkzss = Executors.newScheduledThreadPool(2);
             ksmdveilkdjrreihblwkzss.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(18);
                 }
             });
             ksmdveilkdjrreihblwkzss.shutdown();
             qhldahqtkllglveedbmu.animate()
                     .rotation(253f)
                     .setDuration(690)
                     .start();
             ScheduledExecutorService xuuizmkixsftkju = Executors.newScheduledThreadPool(4);
             xuuizmkixsftkju.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(63);
                 }
             });
             xuuizmkixsftkju.shutdown();
             File file5 = new File(getFilesDir(), "hcxyahpgwkbzohbgfidzt.txt");
             if(file5.exists()) file5.delete();
             Intent muoaagyknmlfurwsw = new Intent();
             muoaagyknmlfurwsw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.KxsecgbxpqbjbxukujythActivity");
             startActivity(muoaagyknmlfurwsw);
             SQLiteOpenHelper rnzapoaydpusepuvuaej = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "qwnwczsxvvmxkayitsem.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zfaatbotllgrfeyik"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nddmjjuolkzbrzfe = rnzapoaydpusepuvuaej.getWritableDatabase();
             try {
                 nddmjjuolkzbrzfe.execSQL("INSERT INTO zfaatbotllgrfeyik (name, value) VALUES (?, ?)", new String[]{"lavefetfcmhwghnir", "byddqamjfgxzjvvmkfq"});
             } finally {
                 nddmjjuolkzbrzfe.close();
                 rnzapoaydpusepuvuaej.close();
             }
             String hbuoomqzypdzzbaufszak = "CyaJaRYfArjRyHIxYYASznaJ5zQ0v9Ezu7tdq9Yun0h83xSVUJX0WUglHXsJoaXbgX9NQOnHMqMGd1DDTyP8BZQpnKujowpK8PcbA3KLeRG6s0gdc5VHwvQYxAtRt1mDkcsc0inRBXzmQ3l";
             hbuoomqzypdzzbaufszak.substring(4);
             hbuoomqzypdzzbaufszak.replace('a', 's');
             ScheduledExecutorService tbjuzyavibsvwimpmwzdwtwfz = Executors.newScheduledThreadPool(1);
             tbjuzyavibsvwimpmwzdwtwfz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(77);
                 }
             });
             tbjuzyavibsvwimpmwzdwtwfz.shutdown();
         }
     });
     final View gcbiiqlilunhblizwqp = findViewById(R.id.pfgzwolwtvhfhpohhylwsc);
     gcbiiqlilunhblizwqp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper dapekavkvxtvmcmjz = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "vwntbuguowwugkbkgrnqdwrrr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kokpoqxymxypfmttvqpa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bbahsboeskfswxytlvdineft = dapekavkvxtvmcmjz.getWritableDatabase();
             try {
                 bbahsboeskfswxytlvdineft.execSQL("SELECT * FROM kokpoqxymxypfmttvqpa WHERE name = ?", new String[]{"ycrzknudirdhlmiczor", "syaohrknwdipvlffuuvflunsp"});
             } finally {
                 bbahsboeskfswxytlvdineft.close();
                 dapekavkvxtvmcmjz.close();
             }
             File file1 = new File(getFilesDir(), "fodufeqrxwpiafganxaitoeun.txt");
         }
     });
     final View paioipdobomnbkdmwdfomi = findViewById(R.id.baviavlehbfejma);
     paioipdobomnbkdmwdfomi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(UusqjbxzmhwenlzfthActivity.this, "emlatfjtzfybfvebnja", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent nannkmrzperkuzzhglvjno = new Intent();
             nannkmrzperkuzzhglvjno.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.YscykwmdhkayxcystnihActivity");
             startActivity(nannkmrzperkuzzhglvjno);
             paioipdobomnbkdmwdfomi.animate()
                     .rotation(174f)
                     .setDuration(176)
                     .start();
             SQLiteOpenHelper rciezinnsecbhfkbeajimeft = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "pjceefwlpmnbxsufgnlw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zrzxhbagedohcpikafhatgaf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fmcklomgvkkrzmfjvyevghmj = rciezinnsecbhfkbeajimeft.getWritableDatabase();
             try {
                 fmcklomgvkkrzmfjvyevghmj.execSQL("DELETE FROM zrzxhbagedohcpikafhatgaf WHERE name = ?", new String[]{"awanywpljlfzhvkug", "lrlncuuchwtrxtbmb"});
             } finally {
                 fmcklomgvkkrzmfjvyevghmj.close();
                 rciezinnsecbhfkbeajimeft.close();
             }
             Date pkhbpdxltmkdxcuscsemrm = new Date();
             String dxjyrbybxkoyurmwd = String.valueOf(pkhbpdxltmkdxcuscsemrm.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper tgjgzcwoursvfwrkdeehcrhgz = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "oekbhmqjkrjtpvzwjh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dlahkwfsicvflsadvwhbw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase pbmmcchyjxvtmsuled = tgjgzcwoursvfwrkdeehcrhgz.getWritableDatabase();
             try {
                 pbmmcchyjxvtmsuled.execSQL("INSERT INTO dlahkwfsicvflsadvwhbw (name, value) VALUES (?, ?)", new String[]{"jrktelgtwcuzfsg", "vclwsozfebeiibuhxdi"});
             } finally {
                 pbmmcchyjxvtmsuled.close();
                 tgjgzcwoursvfwrkdeehcrhgz.close();
             }
             String lttbnyinexuobll = "RxW0ol2eFGeheqbHxPQUufGnV1qtYHVbrYS6yWDrEuuVmOXUrps1Cp84Vz9hIyVCmT6lKPLnrgN3CMIYQyH93DhZ82I8xPMYVPWyUxH5uOmJKIWWD2rSrDZqP5btxchj3x2HonIxc9dTzRkqcg4cRV7QNZoSrgf";
             lttbnyinexuobll.substring(4);
             lttbnyinexuobll.replace('x', 'a');
         }
     });
     final View vqpmpyqckubpoirombtygj = findViewById(R.id.wkmmphdvarzhbusetayknb);
     vqpmpyqckubpoirombtygj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String wqmyibghaozdcwfnuzko = "3UyoQUm1vdBJmeAVkzJtTWFqHLmqXlevxE1qusqGiDquqsFOmyzzmxS5S0QbFOpj2R0JL";
             wqmyibghaozdcwfnuzko.substring(3);
             wqmyibghaozdcwfnuzko.replace('m', 'n');
             vqpmpyqckubpoirombtygj.animate()
                     .rotation(73f)
                     .setDuration(97)
                     .start();
             File file2 = new File(getFilesDir(), "niezgekiazxvengo.txt");
             FileWriter ejwbnooixpjwjvyvmqgygepyq = null;
             try {
                 ejwbnooixpjwjvyvmqgygepyq = new FileWriter(file2);
                 ejwbnooixpjwjvyvmqgygepyq.write("5zgKKMUYQRoM4VkCTw8BQFGHqimd4pYyxtMhwcpVl2UCLXzs8Ov7FiFA17pW74ewPyFS2auJIKLbjE9i3wAooRCacsGge3xg6mMao4H4toNZwDeAWMzdb54pWtvuxTxVIii8ITmTVLgsUtYx6BW1xOCyA6p7qxU9");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ejwbnooixpjwjvyvmqgygepyq != null) {
                     try {
                         ejwbnooixpjwjvyvmqgygepyq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View dniysttdlwvhthxyqzl = findViewById(R.id.cszgsxftzrolmpsrlx);
     dniysttdlwvhthxyqzl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             dniysttdlwvhthxyqzl.animate()
                     .translationX(71f)
                     .translationY(180f)
                     .setDuration(723)
                     .start();
             SQLiteOpenHelper ufgwduuayyilexwfbo = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "yuutrpkdxtwhwxvjkfwutfdzn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "trflpmhvzfqorhtyjbb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gjvyirwcmiderxmpzqewklc = ufgwduuayyilexwfbo.getWritableDatabase();
             try {
                 gjvyirwcmiderxmpzqewklc.execSQL("DELETE FROM trflpmhvzfqorhtyjbb WHERE name = ?", new String[]{"gyadktvenhbyzva", "cyoaixidddariwztpekyb"});
             } finally {
                 gjvyirwcmiderxmpzqewklc.close();
                 ufgwduuayyilexwfbo.close();
             }
             File file2 = new File(getFilesDir(), "apbphmhqiulgchfojyrcj.txt");
         }
     });
     final View batohrvbxavanxelunxrs = findViewById(R.id.atrhqfytluxhsjjotop);
     batohrvbxavanxelunxrs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             batohrvbxavanxelunxrs.animate()
                     .translationX(55f)
                     .translationY(91f)
                     .setDuration(516)
                     .start();
             ScheduledExecutorService lwsrlwnnogtkrqliyzt = Executors.newScheduledThreadPool(3);
             lwsrlwnnogtkrqliyzt.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(98);
                 }
             });
             lwsrlwnnogtkrqliyzt.shutdown();
             SQLiteOpenHelper ewowketqbngqidygcfzrl = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "oqhymjvzjogxiebzvwtfbgtc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vqpmnwqsuiatlcwgmjpxkgw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yyjipeegoqbukfu = ewowketqbngqidygcfzrl.getWritableDatabase();
             try {
                 yyjipeegoqbukfu.execSQL("DELETE FROM vqpmnwqsuiatlcwgmjpxkgw WHERE name = ?", new String[]{"zjdxbugfjbvbdpegjrs", "bwksjpetizwfqtymffayvqt"});
             } finally {
                 yyjipeegoqbukfu.close();
                 ewowketqbngqidygcfzrl.close();
             }
             Intent ndiguzqeiboolypzkvl = new Intent();
             ndiguzqeiboolypzkvl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.TkbuzpnzrgmrafzntjezwjnActivity");
             startActivity(ndiguzqeiboolypzkvl);
             batohrvbxavanxelunxrs.animate()
                     .alpha(0.25307906f)
                     .setDuration(951)
                     .start();
             File file5 = new File(getFilesDir(), "dxontciozduuybkmcv.txt");
             if(file5.exists()) file5.delete();
             Handler hhnsltmpvqhazeksdieh = new Handler(Looper.getMainLooper());
             hhnsltmpvqhazeksdieh.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     batohrvbxavanxelunxrs.setVisibility(View.VISIBLE);
                 }
             }, 865);
             String wycwggjkcyashkukxtxlz = "lzxsb58yBedyIcEw51sFnZOESxmP9dsd";
             wycwggjkcyashkukxtxlz.substring(4);
             wycwggjkcyashkukxtxlz.replace('t', 'u');
             File file8 = new File(getFilesDir(), "skwiixmgbmhmccser.txt");
             if(file8.exists()) file8.delete();
         }
     });
     final View fahbahaqoxhhafnuxugqi = findViewById(R.id.xeahfsqrjwkbkmkletxzel);
     fahbahaqoxhhafnuxugqi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "dgfjhuxfyhhrukykrgngaf.txt");
             FileWriter qflxgpojyepmtgcmwyydgxyy = null;
             try {
                 qflxgpojyepmtgcmwyydgxyy = new FileWriter(file0);
                 qflxgpojyepmtgcmwyydgxyy.write("KZ0R8ibUofTsfNSzUbBPCp3Vccee86l90N0h2eWlJBE3ZY0nVIU6UbuHNaRp7KWmIp8uIKCYTkRii1DVVpaLaUrhUvU4Uy2cP2xqaMLeT2TfS8QrARmStE8x6y5cGUQBcA5y6UaflgOAiiuNrxu3KX9gKSWMKWm1swDBYoGR4ME80CZIng5ljNT9KITiJH7S");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (qflxgpojyepmtgcmwyydgxyy != null) {
                     try {
                         qflxgpojyepmtgcmwyydgxyy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int btapmnfdmrerhsxigc = 226;
             Math.max(btapmnfdmrerhsxigc, 583);
             String.valueOf(btapmnfdmrerhsxigc);
             Intent touhuyqwjbgsgkvkvnmfnamn = new Intent();
             touhuyqwjbgsgkvkvnmfnamn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.QtsbcyzxmrhgkvhicquoduftesActivity");
             startActivity(touhuyqwjbgsgkvkvnmfnamn);
         }
     });
     final View nwhehkiwudzprpuwnnuqiv = findViewById(R.id.ubckpuezmozlkbdhzsopclls);
     nwhehkiwudzprpuwnnuqiv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int hmozwdnvgnwkjezgzb = 679;
             Math.max(hmozwdnvgnwkjezgzb, 929);
             String.valueOf(hmozwdnvgnwkjezgzb);
             SQLiteOpenHelper dymvznjurtgrjvkpu = new SQLiteOpenHelper(UusqjbxzmhwenlzfthActivity.this, "lqfzvfihlbspudeoobxojd.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kdlrldcpjaumxjcwojqbm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lbhmbmvcckqsdugkkqdrgqx = dymvznjurtgrjvkpu.getWritableDatabase();
             try {
                 lbhmbmvcckqsdugkkqdrgqx.execSQL("SELECT * FROM kdlrldcpjaumxjcwojqbm WHERE name = ?", new String[]{"nrovsjiyfqairtebbjpmssmo", "kwbbekydkfiypvlbhiha"});
             } finally {
                 lbhmbmvcckqsdugkkqdrgqx.close();
                 dymvznjurtgrjvkpu.close();
             }
             nwhehkiwudzprpuwnnuqiv.animate()
                     .rotation(297f)
                     .setDuration(903)
                     .start();
             Date lwroxlqcujfwlnjvmhjzhc = new Date();
             String ptarelsxbfrvrmbcdwsmku = String.valueOf(lwroxlqcujfwlnjvmhjzhc.getTime());
             System.currentTimeMillis();
             Intent vugaiipmaazqsloylubjdrix = new Intent();
             vugaiipmaazqsloylubjdrix.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.GfsmsxknfnkstnhbixkgfjwxwuActivity");
             startActivity(vugaiipmaazqsloylubjdrix);
             nwhehkiwudzprpuwnnuqiv.animate()
                     .rotation(67f)
                     .setDuration(869)
                     .start();
             File file6 = new File(getFilesDir(), "rektspgakjakaiwoezktwf.txt");
             if(file6.exists()) file6.delete();
         }
     });

ylhhyfkvlqwitrzgqfcp();
Prowtrjyhlcltgzggvdflwml   vheyzdsbvzrhflmn =     new Prowtrjyhlcltgzggvdflwml();
vheyzdsbvzrhflmn.jgrkmlcwurfbhclhugfvkev ="c81ZI19pWscwHMF0RVrc5cnqCTlhwcfQ9N6tlpUlIOj52tyLhLmv762L0spcNi1CuMpOJzc4zajEQ2CA5917Ev3";
vheyzdsbvzrhflmn.agvaaamqykkcuqbrcaneme ="cj7JKoJQmWD9oJDxUWpTSGFZ91n6yKL3XaiWrr9RDqNfesSjjjxS9nrh1sLNLBAXd9vHLsvyTz7igYFzc2kgRtYGXZReDicgzEAHaL1XtiX";
vheyzdsbvzrhflmn.sguimqctbdzpilkzorpiex ="TXBNKZbz6LzW1fqojS6Pj1TWmpMlMJysZq2yYZ1DE3nlSNegRZGu86hkeBAYsCfnAXtA4oc2eTkYvdWBtvpabTA1LgsmaS3hzcejpdiI72xRX8";
vheyzdsbvzrhflmn.xjcfznfuxvvhxsslyvcoz ="bI921Os0D9Q8J9QEQIGKxhZEJZOpMYWMpznTBJfaoWnJs9RjbPJWupRjbiv2ssguRITBdPUA5kjZNniDx9T69iChXJwhgTVBdvzx4r3fSmXp";
vheyzdsbvzrhflmn.cszzwxzqsmqgunpjlhqv ="NddT5niWi4k3rp84GT7D69zK6IEVFt2qp7Pw53KAGehz6WZ0kF7TmY9BUZqaJTYELDTEBl8m2JF8EYMTKG3gc";
vheyzdsbvzrhflmn.waexqpbrgbhnhfuzsxqnmd ="97DZYpIcM0zfe2wVQM6jJdpbh957nka65tM9pR7Av8FG5uEYMbHk7EiJsYRw25KLo8UXCeM3tzONxyxts8WjKsEYH1Qi6j1cRqJQyCtG4F2MjSo3EEZWgj9DYwIEqcOz89FwAvoFILQMGSgM";
vheyzdsbvzrhflmn.hcikztarzpzgajprst ="6Nt5CpodIYmndlLmjUArrXWnmDmVhBBFdhUmnt7IXjjKamt6XOHGCcm0MnOQxLvbmNLbpbT5YgAvxFY4Qry0l3FwItvRsXInfKekhn4SEFrU21ersMRouL3NVAilyliqTIdg1TmFlSRij5JFLl1ynvc6AOB0F";
vheyzdsbvzrhflmn.pdymnblgqvgbsmozbmd ="PKNGBYbBoswbTqaVBggDLr9UpG4RN7i7FxBzpBCRGSIOsnGNBLZDlhzPGBwGfwbs5WOeos1raBe3uYPUaIzn0eFbhUoQBkEH8l4qbgMBzAPjNT52p2lCTXwM7V5nCL9S8rwkccIL1omSJ8G7g5vhREMxmYMCxewgk8wj";
vheyzdsbvzrhflmn.syprcbawdvmwjgu ="0jhgtgPFoqUBvisoXTkwbGtN4AXTsUOqNAFMDZHDwEwiipF0hwp2HWiearLQb5H0fvg06w2F3y3n275EhrFGWsHIbOmRwPM9d7Ya5u5AXeZdoJTUwDWyn3IhYwyIthMJHfNkZx4R5WPDi0nabZFHmALSuOLvl1ldqRaZOwFN7wufLHxgj";
vheyzdsbvzrhflmn.waowirmunhegnyttdetg ="otjEp3GDYJINwEMNcRmpeKiREpzSenL3reZKpO19z0UnsM8AHrOoPLi5rP8KTuTWRdoFUiePCECAtoE";
vheyzdsbvzrhflmn.ruuonnxhqdxkkraooenmzqj ="5jP0Ajaqutl0vQTebypNQqcZd40OaJFXeH1yu8pOz4YOEZSdr9cRhvBYlKhU9IgHTzlqmxSdIeCfwYwhjfrIkMeutgxXSKo0pNklxFeOKZlbU6zGiw8lJ5fOXH30C1IGKgymP3MPhQUapJ1gSUqZ55nc6hJl8rdfQH240x75cnDfpGD4";
vheyzdsbvzrhflmn.goztqwueeeeywajxynd ="3TsN3CXUsrPb70Aqyqyi56bOw4PoMTM0gSRvVZULN1Pxy9H5AAlY2EQ2UTOYKL64peVsV1qoMp2odSwLQL1DuPrxhUqhIONZgdhiI9P8ht7MPyCmBh";
 Toast.makeText(UusqjbxzmhwenlzfthActivity.this,getString(R.string.rlxvlalffqkabxazaaaem),Toast.LENGTH_SHORT).show();
    }
 void ylhhyfkvlqwitrzgqfcp() {
     long tihlbozmklbropxafvuwbwjz = System.currentTimeMillis();
     Date date = new Date(tihlbozmklbropxafvuwbwjz);
     String time = date.toString();
     hmoshftnezeooxuoom();
}
 void hmoshftnezeooxuoom() {
     LinearLayout rnrvshdacjphqzuazpvcbo = new LinearLayout(UusqjbxzmhwenlzfthActivity.this);
     rnrvshdacjphqzuazpvcbo.setVisibility(View.VISIBLE);
     rnrvshdacjphqzuazpvcbo.setEnabled(false);
     mcchbjvozhhhpemmn();
}
 void mcchbjvozhhhpemmn() {
     long raufpddmcfsrjhghpab = System.currentTimeMillis();
     Date date = new Date(raufpddmcfsrjhghpab);
     String time = date.toString();
     bblzkhjjcyzgzcbtvwkko();
}
 void bblzkhjjcyzgzcbtvwkko() {
     long eqggjxngowdoaodljdqvgojn = System.currentTimeMillis();
     Date date = new Date(eqggjxngowdoaodljdqvgojn);
     String time = date.toString();
}}