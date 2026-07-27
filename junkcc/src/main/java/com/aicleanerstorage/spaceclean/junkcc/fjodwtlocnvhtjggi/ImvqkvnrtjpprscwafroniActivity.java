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
import android.widget.RatingBar;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class ImvqkvnrtjpprscwafroniActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mvqkvnrtjpprscwafroni);
     final View xterkkyikmtutgl = findViewById(R.id.ypbnjencxlecrcxjpllclhaza);
     xterkkyikmtutgl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "lzamtgwvzgemgqhshydc.txt");
             FileReader fqihhwkkizcnirgrdwanhk = null;
             try {
                 if(file0.exists()) {
                     fqihhwkkizcnirgrdwanhk = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (fqihhwkkizcnirgrdwanhk != null) {
                     try {
                         fqihhwkkizcnirgrdwanhk.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService iuzqrquauuobpjtrddwgrd = Executors.newScheduledThreadPool(1);
             iuzqrquauuobpjtrddwgrd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(99);
                 }
             });
             iuzqrquauuobpjtrddwgrd.shutdown();
             xterkkyikmtutgl.animate()
                     .alpha(0.4178596f)
                     .setDuration(108)
                     .start();
             Date jxhygtuvcnvnpqi = new Date();
             String xwtystawekuzeuotyay = String.valueOf(jxhygtuvcnvnpqi.getTime());
             System.currentTimeMillis();
             Handler kqdimmgbldqbghrrmvuywde = new Handler(Looper.getMainLooper());
             kqdimmgbldqbghrrmvuywde.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     xterkkyikmtutgl.setVisibility(View.VISIBLE);
                 }
             }, 809);
             SQLiteOpenHelper nbxadwklvtrhytjjqhd = new SQLiteOpenHelper(ImvqkvnrtjpprscwafroniActivity.this, "iledgqbwpuktzdwb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "npkbrwcmhbwvybgha"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ioaznsrrxbhjsvdrykf = nbxadwklvtrhytjjqhd.getWritableDatabase();
             try {
                 ioaznsrrxbhjsvdrykf.execSQL("SELECT * FROM npkbrwcmhbwvybgha WHERE name = ?", new String[]{"xxnyzwsexlmaothjqmmoyqa", "kxybfctfpbnkwkeoeausmqjm"});
             } finally {
                 ioaznsrrxbhjsvdrykf.close();
                 nbxadwklvtrhytjjqhd.close();
             }
             Intent zpblxitqhcwljvpaoda = new Intent();
             zpblxitqhcwljvpaoda.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.KgmohobzfpgtbzsxActivity");
             startActivity(zpblxitqhcwljvpaoda);
             File file7 = new File(getFilesDir(), "wetssiafnkdjoqewac.txt");
             FileWriter lkizvomsgryzuxwp = null;
             try {
                 lkizvomsgryzuxwp = new FileWriter(file7);
                 lkizvomsgryzuxwp.write("o6j4kkbX9wIGGM0f6dgsRgRzgtoXXHZzJ9Sh");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (lkizvomsgryzuxwp != null) {
                     try {
                         lkizvomsgryzuxwp.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int exlxbccyjedztjitqyn = 780;
             Math.max(exlxbccyjedztjitqyn, 862);
             String.valueOf(exlxbccyjedztjitqyn);
             SQLiteOpenHelper slhjvbpeqieelqifdp = new SQLiteOpenHelper(ImvqkvnrtjpprscwafroniActivity.this, "hkoyccttiqphsmcwtmrignnkl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lyiyxlrkmbczltvbwwca"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase igxpbgvuerqdwalm = slhjvbpeqieelqifdp.getWritableDatabase();
             try {
                 igxpbgvuerqdwalm.execSQL("SELECT * FROM lyiyxlrkmbczltvbwwca WHERE name = ?", new String[]{"zmdwgwhxrcrxtldmijetuhg", "wzsagmydkzniebr"});
             } finally {
                 igxpbgvuerqdwalm.close();
                 slhjvbpeqieelqifdp.close();
             }
         }
     });
     final View csavqizcuwclejlxjof = findViewById(R.id.acjnitmrxhbqegydwtvu);
     csavqizcuwclejlxjof.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent wawhijbltyezbslconenydfo = new Intent();
             wawhijbltyezbslconenydfo.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.AknbvehulkkshnsttdActivity");
             startActivity(wawhijbltyezbslconenydfo);
             csavqizcuwclejlxjof.animate()
                     .rotation(103f)
                     .setDuration(868)
                     .start();
             Intent vpdrlocqhoqjskdwlep = new Intent();
             vpdrlocqhoqjskdwlep.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.AknbvehulkkshnsttdActivity");
             startActivity(vpdrlocqhoqjskdwlep);
             ScheduledExecutorService vgbwtlqehcmlwntkltlfuyhq = Executors.newScheduledThreadPool(4);
             vgbwtlqehcmlwntkltlfuyhq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(40);
                 }
             });
             vgbwtlqehcmlwntkltlfuyhq.shutdown();
             Intent acwulvprffiypmpxg = new Intent();
             acwulvprffiypmpxg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.AknbvehulkkshnsttdActivity");
             startActivity(acwulvprffiypmpxg);
             SQLiteOpenHelper jvhybuxxggmmphuvb = new SQLiteOpenHelper(ImvqkvnrtjpprscwafroniActivity.this, "tbkbhkffdcefexabkgn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nidlpzxnstjmkdbtex"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fkjiqkjijklvgoqepnywzfyn = jvhybuxxggmmphuvb.getWritableDatabase();
             try {
                 fkjiqkjijklvgoqepnywzfyn.execSQL("SELECT * FROM nidlpzxnstjmkdbtex WHERE name = ?", new String[]{"kcxlfzzwotcddve", "cipxrvtxqgnepdqwhckyh"});
             } finally {
                 fkjiqkjijklvgoqepnywzfyn.close();
                 jvhybuxxggmmphuvb.close();
             }
             ScheduledExecutorService jxlvrfnzbopdbavslulyjtgav = Executors.newScheduledThreadPool(3);
             jxlvrfnzbopdbavslulyjtgav.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(91);
                 }
             });
             jxlvrfnzbopdbavslulyjtgav.shutdown();
         }
     });
     final View rjmuypqctqgdkhfvxtpo = findViewById(R.id.fhwrpnvqlccjorm);
     rjmuypqctqgdkhfvxtpo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int aunhvnuxkuoauoxftedrgoee = 453;
             Math.max(aunhvnuxkuoauoxftedrgoee, 587);
             String.valueOf(aunhvnuxkuoauoxftedrgoee);
             ScheduledExecutorService cargiqypjqrhygltucywp = Executors.newScheduledThreadPool(3);
             cargiqypjqrhygltucywp.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(48);
                 }
             });
             cargiqypjqrhygltucywp.shutdown();
             Intent bjinehmitnpjxcxfhcav = new Intent();
             bjinehmitnpjxcxfhcav.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.ScmpdwtlascanlljmgowdActivity");
             startActivity(bjinehmitnpjxcxfhcav);
             File file3 = new File(getFilesDir(), "skbhpyimfkvtpbi.txt");
             Handler ogthfgctzhuvhzk = new Handler(Looper.getMainLooper());
             ogthfgctzhuvhzk.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     rjmuypqctqgdkhfvxtpo.setVisibility(View.VISIBLE);
                 }
             }, 30);
             SQLiteOpenHelper qtxmydevpxqibcxlmbfrrz = new SQLiteOpenHelper(ImvqkvnrtjpprscwafroniActivity.this, "hgwngcmuxctmwxhnbhcsw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xgtbknseeljvhnuuyfcq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase twqjrkhjdgkhiar = qtxmydevpxqibcxlmbfrrz.getWritableDatabase();
             try {
                 twqjrkhjdgkhiar.execSQL("INSERT INTO xgtbknseeljvhnuuyfcq (name, value) VALUES (?, ?)", new String[]{"euqnoeiazeqkjwy", "aaonovcinljpwjluiyexs"});
             } finally {
                 twqjrkhjdgkhiar.close();
                 qtxmydevpxqibcxlmbfrrz.close();
             }
             rjmuypqctqgdkhfvxtpo.animate()
                     .translationX(84f)
                     .translationY(141f)
                     .setDuration(529)
                     .start();
             Intent uyuieywkcfvqkiwdxyvtsbcs = new Intent();
             uyuieywkcfvqkiwdxyvtsbcs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.ScmpdwtlascanlljmgowdActivity");
             startActivity(uyuieywkcfvqkiwdxyvtsbcs);
             int oriqaooclhrmwkr = 241;
             Math.max(oriqaooclhrmwkr, 261);
             String.valueOf(oriqaooclhrmwkr);
         }
     });
     final View mllfurekysndtrhxdndfpb = findViewById(R.id.puzlrzdncmfelctdpewca);
     mllfurekysndtrhxdndfpb.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent mfrhagvzwokabxjconb = new Intent();
             mfrhagvzwokabxjconb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.KlfuekconpqlkdnydjActivity");
             startActivity(mfrhagvzwokabxjconb);
             Date dcoxnhewxglfvstnysrramft = new Date();
             String icjtuydbsfuqxvdjfjajsy = String.valueOf(dcoxnhewxglfvstnysrramft.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService tptvtvqcpcdvjttoazsju = Executors.newScheduledThreadPool(1);
             tptvtvqcpcdvjttoazsju.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(82);
                 }
             });
             tptvtvqcpcdvjttoazsju.shutdown();
             File file3 = new File(getFilesDir(), "xqqblkpdtlyslyqaymvt.txt");
             Handler zkceyfgrtddxbbuczxwrqdca = new Handler(Looper.getMainLooper());
             zkceyfgrtddxbbuczxwrqdca.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     mllfurekysndtrhxdndfpb.setVisibility(View.VISIBLE);
                 }
             }, 532);
             mllfurekysndtrhxdndfpb.animate()
                     .rotation(168f)
                     .setDuration(340)
                     .start();
             int ahueaszjfckrahvnggibswl = 503;
             Math.max(ahueaszjfckrahvnggibswl, 930);
             String.valueOf(ahueaszjfckrahvnggibswl);
             SQLiteOpenHelper ekbiihrcizvcrboijolb = new SQLiteOpenHelper(ImvqkvnrtjpprscwafroniActivity.this, "dmzdmqmyynfdespveuxw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kgisfhmmlarlfzfxqa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mkcfpdedttapckfbx = ekbiihrcizvcrboijolb.getWritableDatabase();
             try {
                 mkcfpdedttapckfbx.execSQL("INSERT INTO kgisfhmmlarlfzfxqa (name, value) VALUES (?, ?)", new String[]{"cvhlopvsdwrboeuxfpblmvizt", "xegrojlnqmlvffojdsg"});
             } finally {
                 mkcfpdedttapckfbx.close();
                 ekbiihrcizvcrboijolb.close();
             }
         }
     });
     final View clqvofdzlbiiihk = findViewById(R.id.ifmabubhkjzcglgcw);
     clqvofdzlbiiihk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date lsfgxpqunpayvvxgtwmbamr = new Date();
             String rucuroeerghbdwm = String.valueOf(lsfgxpqunpayvvxgtwmbamr.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "jozxfyswgssrgheruhjftxmzh.txt");
             FileReader yajyvaonxnvvuoqpxmpmdgyh = null;
             try {
                 if(file1.exists()) {
                     yajyvaonxnvvuoqpxmpmdgyh = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (yajyvaonxnvvuoqpxmpmdgyh != null) {
                     try {
                         yajyvaonxnvvuoqpxmpmdgyh.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date wkaomsjfynhdojebq = new Date();
             String exttdygfcfzjcqyfrhkm = String.valueOf(wkaomsjfynhdojebq.getTime());
             System.currentTimeMillis();
             Intent hsukbhigwwhhdamkfwsjqimjf = new Intent();
             hsukbhigwwhhdamkfwsjqimjf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.EtstpdrbpnvqqbcvticpeyfzrActivity");
             startActivity(hsukbhigwwhhdamkfwsjqimjf);
             String xapfhphnahuezfuklm = "PWv1qChjyKIoBYJFM3DwDs4Wr3MhNjWpg0CnCpPgh5bYlwAI9lHP5cI3R7fH7YP6rGWndBTV6Ps6q1HY1ObbAo02YWKrDS9b1WIeSLwuKI7jhQG9u3yWnrgY0Qu7csw6CL26RdRglXmUDBFxj3vwf9QV94Gu8vu";
             xapfhphnahuezfuklm.substring(1);
             xapfhphnahuezfuklm.replace('z', 'r');
             Intent okxfohujtwnvgdqwtnvvo = new Intent();
             okxfohujtwnvgdqwtnvvo.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.fjodwtlocnvhtjggi.EtstpdrbpnvqqbcvticpeyfzrActivity");
             startActivity(okxfohujtwnvgdqwtnvvo);
             String wylvuepfmryxdmuji = "hvEPBJpeVUSmvUwKrFOIi7kLBXyb17nNNiDQJqMjr55yO8bR";
             wylvuepfmryxdmuji.substring(0);
             wylvuepfmryxdmuji.replace('v', 'r');
             clqvofdzlbiiihk.animate()
                     .alpha(0.39443088f)
                     .setDuration(481)
                     .start();
             File file8 = new File(getFilesDir(), "abofijnfmcqidoolitcvvnkle.txt");
             FileReader dhxbnmrxkzifpzdcw = null;
             try {
                 if(file8.exists()) {
                     dhxbnmrxkzifpzdcw = new FileReader(file8);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (dhxbnmrxkzifpzdcw != null) {
                     try {
                         dhxbnmrxkzifpzdcw.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });

dtkdmpmfvduvfzmnqh();
Rttdidnrycnnsrav   pgdoldwtczjsigvgs =     new Rttdidnrycnnsrav();
pgdoldwtczjsigvgs.malmapnrrngxqxcjtcejjxh ="87kEbBwEKYkhPeJzv7SP8ghZl1KwKLYljDiDvgTuJlHnr1H3UlNvFVQqy5FboOuQXEdb0sDfHJHmATrmoSp2o796oGEOwFRIGcEY3bVfphTe5ExvAUuI2A54cixXA0u7cVB04r6L3qEdxzqiSHwvk4eXa5QuDPaCv5KJ2Er1";
pgdoldwtczjsigvgs.dbyipgbpvtvhviacqhkjvup ="1ZPEyDAmNjj8VFwCZh32YrK5OvhwQScJGU41oXN1k9oFxBGG5Fa6HqNJNGm7qGifpRoWEoq98FKMgU8maA7Yq0T1K84O5OazM0NbvtOVXRNiL9MT";
pgdoldwtczjsigvgs.urhdaawkeaexmqabddnf ="NCvPzL3HSKb5uaVfAXLUFCms44k05YHJ4iNL89FNswnMD5KYOpnViQsB43NIk7egZJfA0dzcyJKtLAGV1s9C5upo1jMmdfIsXumDNMbaGwwjxcthmuXBLpucAv7QtaTpoHfJWcNoENb3OA4gTWNtNuvI1xZqEphtUyPW";
pgdoldwtczjsigvgs.biukhgprtklwyaxwhgnr ="gEHBDBjpfXgYwh6xpeQiftxjGHIRsGs4KOHU5PPf0loVbFdmUvQ7BiIjcrLWXtOqWfW7bdAifHiwv886WQadAxty";
 Toast.makeText(ImvqkvnrtjpprscwafroniActivity.this,getString(R.string.xvpyxovxpjikndudkucx),Toast.LENGTH_SHORT).show();
    }
 void dtkdmpmfvduvfzmnqh() {
     RatingBar zotkjnrzouirlzvcbrblyvg = new RatingBar(ImvqkvnrtjpprscwafroniActivity.this);
     zotkjnrzouirlzvcbrblyvg.setVisibility(View.VISIBLE);
     zotkjnrzouirlzvcbrblyvg.setEnabled(false);
     draltbncrelkrjqawbvnwwa();
}
 void draltbncrelkrjqawbvnwwa() {
     long ssejmiijymbfcrupq = System.currentTimeMillis();
     Date date = new Date(ssejmiijymbfcrupq);
     String time = date.toString();
     joobvtqezozlyvywhsnjonca();
}
 void joobvtqezozlyvywhsnjonca() {
     String qlboctpppowplmskah = "3vU6SVLTmfy7GvHcILyCV7TJvkVJvJECt8jeilFHbzGJ96t2cxhNBYDotDi";
     qlboctpppowplmskah = qlboctpppowplmskah.substring(3);
     qlboctpppowplmskah = qlboctpppowplmskah.toUpperCase();
     wgoxlccepsmigsxupaaxhs();
}
 void wgoxlccepsmigsxupaaxhs() {
     String kegpnegygjbpcuhehqmnznnsa = "ovzQm64sWHxbmyLshOSN5x8M4dyDWzTSNyqgx";
     kegpnegygjbpcuhehqmnznnsa = kegpnegygjbpcuhehqmnznnsa.substring(4);
     kegpnegygjbpcuhehqmnznnsa = kegpnegygjbpcuhehqmnznnsa.toUpperCase();
}}