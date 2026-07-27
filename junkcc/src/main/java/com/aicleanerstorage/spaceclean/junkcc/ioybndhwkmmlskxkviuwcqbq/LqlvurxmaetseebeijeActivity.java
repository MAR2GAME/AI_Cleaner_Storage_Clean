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

public class LqlvurxmaetseebeijeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qlvurxmaetseebeije);
     final View vilauyaqebnahydndfjvzgcdy = findViewById(R.id.kbzhlbzzzegrxwroxfqpimm);
     vilauyaqebnahydndfjvzgcdy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent rzipsyulmhbwvuay = new Intent();
             rzipsyulmhbwvuay.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.KlgghcxyqupijqbedjvActivity");
             startActivity(rzipsyulmhbwvuay);
             SQLiteOpenHelper nqqxudtagivhiyycni = new SQLiteOpenHelper(LqlvurxmaetseebeijeActivity.this, "gtocjxywbxxfavces.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "reisofqokfoqxsfs"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jsbtluccsetbdrytziqgn = nqqxudtagivhiyycni.getWritableDatabase();
             try {
                 jsbtluccsetbdrytziqgn.execSQL("DELETE FROM reisofqokfoqxsfs WHERE name = ?", new String[]{"jnovtnrebccwibwi", "qkmtowothajjvxnnbrtld"});
             } finally {
                 jsbtluccsetbdrytziqgn.close();
                 nqqxudtagivhiyycni.close();
             }
             ScheduledExecutorService dkysxjeqvrwtdmfycx = Executors.newScheduledThreadPool(2);
             dkysxjeqvrwtdmfycx.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(77);
                 }
             });
             dkysxjeqvrwtdmfycx.shutdown();
             File file3 = new File(getFilesDir(), "rguxsbdkugtolivleqicyvllh.txt");
             FileReader khxhgefdinopabkpeufyn = null;
             try {
                 if(file3.exists()) {
                     khxhgefdinopabkpeufyn = new FileReader(file3);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (khxhgefdinopabkpeufyn != null) {
                     try {
                         khxhgefdinopabkpeufyn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(LqlvurxmaetseebeijeActivity.this, "luzrumaubytaulgqygryx", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper rweqmxpncarwtpb = new SQLiteOpenHelper(LqlvurxmaetseebeijeActivity.this, "gzrmdyvrpiuuhszchqeebk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rtglrjvpaumxlepmhpbak"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase amcntiwwocvszbebtytsmj = rweqmxpncarwtpb.getWritableDatabase();
             try {
                 amcntiwwocvszbebtytsmj.execSQL("INSERT INTO rtglrjvpaumxlepmhpbak (name, value) VALUES (?, ?)", new String[]{"kvtrigvrduqkxmrrufnh", "dznqkcnotaqcdinmlcz"});
             } finally {
                 amcntiwwocvszbebtytsmj.close();
                 rweqmxpncarwtpb.close();
             }
             File file6 = new File(getFilesDir(), "dezjyqmiuzdggdbzj.txt");
             FileWriter uqxrrwalpvdlgijjyvtwrvgha = null;
             try {
                 uqxrrwalpvdlgijjyvtwrvgha = new FileWriter(file6);
                 uqxrrwalpvdlgijjyvtwrvgha.write("K48ejyMo0PJrADYZtOqEFZBM1WTSR1xuuB7llD7TGhK3VMMv8TTFS99IgT2f9prw432PurobXa9YQLfi5NarMLykUMrgaS0UDOeS32MRIOnIXA9de0hoIVbDUaRewkOheyHJ57D8yQ72wx0k3NRXd6EExW9HeXR8tEdUxU5X7SuKeBWA39Z");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (uqxrrwalpvdlgijjyvtwrvgha != null) {
                     try {
                         uqxrrwalpvdlgijjyvtwrvgha.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper pbsgigmumvjcvfkeduvsj = new SQLiteOpenHelper(LqlvurxmaetseebeijeActivity.this, "bjwflnjrjiowlbaiqhksielps.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "uwcdobgymqwchdytkvzoh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fzgpxofxoiyqyzgbahaorty = pbsgigmumvjcvfkeduvsj.getWritableDatabase();
             try {
                 fzgpxofxoiyqyzgbahaorty.execSQL("UPDATE uwcdobgymqwchdytkvzoh SET value = ? WHERE name = ?", new String[]{"mzkkquhrmacqboyubwbcz", "arfurqqgxkpfjtuagjz"});
             } finally {
                 fzgpxofxoiyqyzgbahaorty.close();
                 pbsgigmumvjcvfkeduvsj.close();
             }
             ScheduledExecutorService btlgvaekofvfiiupopvb = Executors.newScheduledThreadPool(1);
             btlgvaekofvfiiupopvb.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(32);
                 }
             });
             btlgvaekofvfiiupopvb.shutdown();
         }
     });
     final View paabcdnavcadffcdd = findViewById(R.id.rgvoeqbezamcnlbpo);
     paabcdnavcadffcdd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "vtcawbsavcovdislzvrmmq.txt");
             FileWriter pfgsgahahbjjomqpwl = null;
             try {
                 pfgsgahahbjjomqpwl = new FileWriter(file0);
                 pfgsgahahbjjomqpwl.write("3ijft4E2yUGixf");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (pfgsgahahbjjomqpwl != null) {
                     try {
                         pfgsgahahbjjomqpwl.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent gjncqefpqaubhazhisv = new Intent();
             gjncqefpqaubhazhisv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MoeygyausjymlfzyActivity");
             startActivity(gjncqefpqaubhazhisv);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(LqlvurxmaetseebeijeActivity.this, "njuyaxsavyusimomu", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent bijehvwguxswtcbf = new Intent();
             bijehvwguxswtcbf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MoeygyausjymlfzyActivity");
             startActivity(bijehvwguxswtcbf);
             ScheduledExecutorService aoiyqafiihhukqm = Executors.newScheduledThreadPool(1);
             aoiyqafiihhukqm.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(54);
                 }
             });
             aoiyqafiihhukqm.shutdown();
             int rkorcwdxpsrlzbyywofbnzu = 580;
             Math.max(rkorcwdxpsrlzbyywofbnzu, 999);
             String.valueOf(rkorcwdxpsrlzbyywofbnzu);
             File file6 = new File(getFilesDir(), "vkayibjkzikliqqfvi.txt");
             Intent wookahchrzygtkljgbebj = new Intent();
             wookahchrzygtkljgbebj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.MoeygyausjymlfzyActivity");
             startActivity(wookahchrzygtkljgbebj);
             File file8 = new File(getFilesDir(), "kubhmnetxzmkvbyraz.txt");
             FileWriter gomlabatdlrqttehluaunck = null;
             try {
                 gomlabatdlrqttehluaunck = new FileWriter(file8);
                 gomlabatdlrqttehluaunck.write("GI5K3Rmy8rs");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (gomlabatdlrqttehluaunck != null) {
                     try {
                         gomlabatdlrqttehluaunck.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date bsqbbejdhlzenxjnrui = new Date();
             String ivnattmrqiboaaleiil = String.valueOf(bsqbbejdhlzenxjnrui.getTime());
             System.currentTimeMillis();
         }
     });
     final View bcvhvsjzoznfhjflglaagjsx = findViewById(R.id.uuhpwjmnkjjgloqvlwfxd);
     bcvhvsjzoznfhjflglaagjsx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(LqlvurxmaetseebeijeActivity.this, "ruxpfgvgiclihstght", Toast.LENGTH_SHORT).show();
                 }
             });
             File file1 = new File(getFilesDir(), "dlljadatsxecqhiylmpdhz.txt");
             if(file1.exists()) file1.delete();
             bcvhvsjzoznfhjflglaagjsx.animate()
                     .rotation(213f)
                     .setDuration(971)
                     .start();
         }
     });
     final View npvsrbrebcpovenmqqscgghn = findViewById(R.id.mkavvqzcjgghosrrzjgv);
     npvsrbrebcpovenmqqscgghn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(LqlvurxmaetseebeijeActivity.this, "qfqhzlpcsnxokvjh", Toast.LENGTH_SHORT).show();
                 }
             });
             npvsrbrebcpovenmqqscgghn.animate()
                     .alpha(0.86716694f)
                     .setDuration(118)
                     .start();
         }
     });
     final View vozkvgzkuzvrwull = findViewById(R.id.bfbbcwyqxbfnlhzskkza);
     vozkvgzkuzvrwull.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler girqsxkmajkjokfowrxgcmxt = new Handler(Looper.getMainLooper());
             girqsxkmajkjokfowrxgcmxt.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     vozkvgzkuzvrwull.setVisibility(View.VISIBLE);
                 }
             }, 870);
             File file1 = new File(getFilesDir(), "vpncyblktpabugyzh.txt");
             FileReader uwdwjlblxipspptk = null;
             try {
                 if(file1.exists()) {
                     uwdwjlblxipspptk = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (uwdwjlblxipspptk != null) {
                     try {
                         uwdwjlblxipspptk.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper uxrntmjfixmpdjpnns = new SQLiteOpenHelper(LqlvurxmaetseebeijeActivity.this, "udrvaipaeekoxwk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cfnidvtxvzkuxgnbwgzcq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xntpzmycsbnuhwz = uxrntmjfixmpdjpnns.getWritableDatabase();
             try {
                 xntpzmycsbnuhwz.execSQL("INSERT INTO cfnidvtxvzkuxgnbwgzcq (name, value) VALUES (?, ?)", new String[]{"ahjxbrvefowdpgiwkzeirtra", "vheduhvkkfbhveapriiaxl"});
             } finally {
                 xntpzmycsbnuhwz.close();
                 uxrntmjfixmpdjpnns.close();
             }
             vozkvgzkuzvrwull.animate()
                     .scaleX(1.6865033f)
                     .scaleY(1.6865033f)
                     .setDuration(930)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(LqlvurxmaetseebeijeActivity.this, "mpgjzeblwpymiffrxkctonkrf", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View aiwbpszkcvwgxjpd = findViewById(R.id.arfkktypqzcyipqqvja);
     aiwbpszkcvwgxjpd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             aiwbpszkcvwgxjpd.animate()
                     .alpha(0.7826824f)
                     .setDuration(134)
                     .start();
             SQLiteOpenHelper amppswqiknuiczbacoxtxll = new SQLiteOpenHelper(LqlvurxmaetseebeijeActivity.this, "iqamtoceoriahovgzaf.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nvomqkhapzqmvgvjokft"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yduvrenoksmoeeqbbwnriferi = amppswqiknuiczbacoxtxll.getWritableDatabase();
             try {
                 yduvrenoksmoeeqbbwnriferi.execSQL("UPDATE nvomqkhapzqmvgvjokft SET value = ? WHERE name = ?", new String[]{"ymzbupwvzuxoemqxhjulobtz", "cedbfuzxdycsvgmnoqpwhh"});
             } finally {
                 yduvrenoksmoeeqbbwnriferi.close();
                 amppswqiknuiczbacoxtxll.close();
             }
             aiwbpszkcvwgxjpd.animate()
                     .alpha(0.5707962f)
                     .setDuration(433)
                     .start();
             int ooqxqelwoupkhveibdvngjc = 26;
             Math.max(ooqxqelwoupkhveibdvngjc, 783);
             String.valueOf(ooqxqelwoupkhveibdvngjc);
             aiwbpszkcvwgxjpd.animate()
                     .rotation(350f)
                     .setDuration(170)
                     .start();
             Intent penbfeigdmxwkhpeepesc = new Intent();
             penbfeigdmxwkhpeepesc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.WarchbtuygypmdmtsActivity");
             startActivity(penbfeigdmxwkhpeepesc);
         }
     });

xknrqpctbjftyzamm();
Ogeuqsikgzfmahdcxbvvkuu   xladgnvoqonhsmxssdxwcr =     new Ogeuqsikgzfmahdcxbvvkuu();
 Toast.makeText(LqlvurxmaetseebeijeActivity.this,getString(R.string.lpjloqjzrmovqooadxmtsqobo),Toast.LENGTH_SHORT).show();
    }
 void xknrqpctbjftyzamm() {
     ListView lfdwgptyzoflmjsyigohgjv = new ListView(LqlvurxmaetseebeijeActivity.this);
     lfdwgptyzoflmjsyigohgjv.setVisibility(View.VISIBLE);
     lfdwgptyzoflmjsyigohgjv.setEnabled(false);
     gpcczayfititzfxlvygn();
}
 void gpcczayfititzfxlvygn() {
     long bxeuxzdxdsobkwatjjuo = System.currentTimeMillis();
     Date date = new Date(bxeuxzdxdsobkwatjjuo);
     String time = date.toString();
     xbdvkazoxiqprrg();
}
 void xbdvkazoxiqprrg() {
     long dzfqfefenhusalhy = System.currentTimeMillis();
     Date date = new Date(dzfqfefenhusalhy);
     String time = date.toString();
     pexszpqgdjjxjeu();
}
 void pexszpqgdjjxjeu() {
     ListView wfnbqeyeldetuoizwgjordypt = new ListView(LqlvurxmaetseebeijeActivity.this);
     wfnbqeyeldetuoizwgjordypt.setVisibility(View.VISIBLE);
     wfnbqeyeldetuoizwgjordypt.setEnabled(false);
     iwtqzszbsdsooyiuirvvf();
}
 void iwtqzszbsdsooyiuirvvf() {
     int oafpltyqcadhjsebqbfcwem = 167;
     double result = Math.sqrt(oafpltyqcadhjsebqbfcwem);
     result = Math.pow(result, 2);
}}