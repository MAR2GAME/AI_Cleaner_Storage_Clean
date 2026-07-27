package com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh;

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
import android.widget.GridView;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class DbhefhsfjypzhxnmxActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bhefhsfjypzhxnmx);
     final View ewubfiioytlmcigfcimazpk = findViewById(R.id.hyylokudoideqytelracvoi);
     ewubfiioytlmcigfcimazpk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper zlbitovzjmbgtkluzlgapvk = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "trvrcczyavvbbaslrbagzs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "uhoqeqybiqxgrkxwl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kngyhhmileezubxsrsbox = zlbitovzjmbgtkluzlgapvk.getWritableDatabase();
             try {
                 kngyhhmileezubxsrsbox.execSQL("DELETE FROM uhoqeqybiqxgrkxwl WHERE name = ?", new String[]{"rhnmyhejibkvrevjh", "cjabflhpyasdqkylfowk"});
             } finally {
                 kngyhhmileezubxsrsbox.close();
                 zlbitovzjmbgtkluzlgapvk.close();
             }
             ScheduledExecutorService gsfmyvtjrvezlalvrxklv = Executors.newScheduledThreadPool(4);
             gsfmyvtjrvezlalvrxklv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(8);
                 }
             });
             gsfmyvtjrvezlalvrxklv.shutdown();
             File file2 = new File(getFilesDir(), "cnblctqzfljiqxzrflahpsx.txt");
             FileWriter ruyvspmwzhpmmlvtffxxnzg = null;
             try {
                 ruyvspmwzhpmmlvtffxxnzg = new FileWriter(file2);
                 ruyvspmwzhpmmlvtffxxnzg.write("KjdjOUbPrUZZjApIulnrhGItTCvSu2zzWn5Kmhzd2tcW3PVYFlPMjjtn92TzrPElTZLNfG329KYZ1JzgAkzDnAmyRiMOyeORp1pGlKKx8hL1KpoqW6HnFTbZe9SQMNVmT3dsReRGT2QgSj92rl7E2tzRWi");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ruyvspmwzhpmmlvtffxxnzg != null) {
                     try {
                         ruyvspmwzhpmmlvtffxxnzg.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper dhqafphslhhdowfxbmp = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "hfavveabttccxgkhhlaw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bfvxfablnzboeiokihzmtzb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase htkegnslxapagkcrjhlkfqmcj = dhqafphslhhdowfxbmp.getWritableDatabase();
             try {
                 htkegnslxapagkcrjhlkfqmcj.execSQL("INSERT INTO bfvxfablnzboeiokihzmtzb (name, value) VALUES (?, ?)", new String[]{"hwoypbzguevxfhyo", "usilmsyazppzzxcixq"});
             } finally {
                 htkegnslxapagkcrjhlkfqmcj.close();
                 dhqafphslhhdowfxbmp.close();
             }
             File file4 = new File(getFilesDir(), "feazlbouolfzvhnq.txt");
             FileReader rpikwjdvvcinijcqqmyakubbr = null;
             try {
                 if(file4.exists()) {
                     rpikwjdvvcinijcqqmyakubbr = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (rpikwjdvvcinijcqqmyakubbr != null) {
                     try {
                         rpikwjdvvcinijcqqmyakubbr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent smqxaejvvyfquex = new Intent();
             smqxaejvvyfquex.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.YqztrtpckjnhfyasypdzpllActivity");
             startActivity(smqxaejvvyfquex);
             File file6 = new File(getFilesDir(), "qgmzwjvpqecoovdmd.txt");
         }
     });
     final View jfxaaakdzwbgqsfxvssysup = findViewById(R.id.mctnrgozgfmkgwnouf);
     jfxaaakdzwbgqsfxvssysup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper fcygzehsdhylsfpskvp = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "dmphzmyiuroavihmnah.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bgdnqahicfprxhilqed"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ozhckfswtwjputskww = fcygzehsdhylsfpskvp.getWritableDatabase();
             try {
                 ozhckfswtwjputskww.execSQL("SELECT * FROM bgdnqahicfprxhilqed WHERE name = ?", new String[]{"nskdlkjoafsihxt", "ujhrogzlwveluyfgxgzwq"});
             } finally {
                 ozhckfswtwjputskww.close();
                 fcygzehsdhylsfpskvp.close();
             }
             Intent verqfyvnrjgkbfmqt = new Intent();
             verqfyvnrjgkbfmqt.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.TrzysfoaiehkcnjmtyshcrbbmyActivity");
             startActivity(verqfyvnrjgkbfmqt);
         }
     });
     final View oygvvemldfiycuqkgoc = findViewById(R.id.uuasvqxzsbgcasc);
     oygvvemldfiycuqkgoc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             oygvvemldfiycuqkgoc.animate()
                     .rotation(177f)
                     .setDuration(615)
                     .start();
             String damogbukgvlnqwkkxpuvgzupn = "Rk6m6y8qCR4UX2bB6QEKQUZCxp5iJ0uJWdDa1L6ktJ1pGxFlaocUcjrLvIDR2UW4zk2DBCHQKE6iqB40ct4OzCJl8YEfhQBoUP2fpAOeR8Dnjz3";
             damogbukgvlnqwkkxpuvgzupn.substring(2);
             damogbukgvlnqwkkxpuvgzupn.replace('f', 'f');
             File file2 = new File(getFilesDir(), "ghpjctghfvmhfcplmeeaagn.txt");
             if(file2.exists()) file2.delete();
         }
     });
     final View tghaamczffdpblr = findViewById(R.id.zvnmaefkkrrcwke);
     tghaamczffdpblr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "livloetmsescajyszwagaiip.txt");
             String sjhnfqlkgovbebnjfp = "bL3zb9fTzJZutUKduRJ1kpU8wtazUd28CMGSClB55sGbVjNeCdBJPisBZJehkTIgd4dVRSQbo5uEaDMCau2Z1nWWo938yL9EK4";
             sjhnfqlkgovbebnjfp.substring(3);
             sjhnfqlkgovbebnjfp.replace('w', 'w');
             Intent fwlnvdslnipwpdoy = new Intent();
             fwlnvdslnipwpdoy.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.HhoijsmelerxstedkhifwtfmwActivity");
             startActivity(fwlnvdslnipwpdoy);
         }
     });
     final View gkkrdylcirnyopxjssoupjzyh = findViewById(R.id.uhmiddvhspwzrgvxedbfmtl);
     gkkrdylcirnyopxjssoupjzyh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent cqtzqszibbagntyfvdmsxtrc = new Intent();
             cqtzqszibbagntyfvdmsxtrc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.AlzptfpeiixcoaiwxActivity");
             startActivity(cqtzqszibbagntyfvdmsxtrc);
             SQLiteOpenHelper smnevjkhrjpkwarmjiddeklq = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "zneomjvbpdramufhljvfef.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kjhybbaxbszylku"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tkjrurpesehelrrvjxld = smnevjkhrjpkwarmjiddeklq.getWritableDatabase();
             try {
                 tkjrurpesehelrrvjxld.execSQL("DELETE FROM kjhybbaxbszylku WHERE name = ?", new String[]{"jukrmceafuvorzvpdulotr", "oikcbnxrpsjpwdnwvuhmrc"});
             } finally {
                 tkjrurpesehelrrvjxld.close();
                 smnevjkhrjpkwarmjiddeklq.close();
             }
             gkkrdylcirnyopxjssoupjzyh.animate()
                     .translationX(77f)
                     .translationY(98f)
                     .setDuration(339)
                     .start();
             File file3 = new File(getFilesDir(), "rhirxethvokxnrd.txt");
             ScheduledExecutorService qlyobwunqqfllltkvqiq = Executors.newScheduledThreadPool(3);
             qlyobwunqqfllltkvqiq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(69);
                 }
             });
             qlyobwunqqfllltkvqiq.shutdown();
             gkkrdylcirnyopxjssoupjzyh.animate()
                     .alpha(0.90048605f)
                     .setDuration(146)
                     .start();
             Intent adcyxfeufovnciajygpwul = new Intent();
             adcyxfeufovnciajygpwul.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.AlzptfpeiixcoaiwxActivity");
             startActivity(adcyxfeufovnciajygpwul);
             SQLiteOpenHelper rmkgctsqijowszxslsfy = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "qphobwmvyvzrpvrivtximg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dfzeesagraymkqbfzilne"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dpptkyeorxjwllodrwda = rmkgctsqijowszxslsfy.getWritableDatabase();
             try {
                 dpptkyeorxjwllodrwda.execSQL("INSERT INTO dfzeesagraymkqbfzilne (name, value) VALUES (?, ?)", new String[]{"kdqrzhzostsywsorq", "qfwsnjfejrwzcxhdo"});
             } finally {
                 dpptkyeorxjwllodrwda.close();
                 rmkgctsqijowszxslsfy.close();
             }
             File file8 = new File(getFilesDir(), "duscuoaugdaxahhdnpd.txt");
             FileReader wdfglpakuqdpcjhqsoyzd = null;
             try {
                 if(file8.exists()) {
                     wdfglpakuqdpcjhqsoyzd = new FileReader(file8);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wdfglpakuqdpcjhqsoyzd != null) {
                     try {
                         wdfglpakuqdpcjhqsoyzd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View cvjflgqrtctvevlpzzwfnctli = findViewById(R.id.zxcrlqqohovvvsddtcvqxu);
     cvjflgqrtctvevlpzzwfnctli.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "hjgthjmfptvtflyeqdeuxqox.txt");
             ScheduledExecutorService wvmjlygacriecjhl = Executors.newScheduledThreadPool(2);
             wvmjlygacriecjhl.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(87);
                 }
             });
             wvmjlygacriecjhl.shutdown();
         }
     });
     final View jqxvkgugomgmxtfutlc = findViewById(R.id.dmrasinbgiuqbmwfgahbrfjk);
     jqxvkgugomgmxtfutlc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "fxjtseahtnzvsaebodr.txt");
             FileReader jbhvikudlsmqpyxykblhnq = null;
             try {
                 if(file0.exists()) {
                     jbhvikudlsmqpyxykblhnq = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jbhvikudlsmqpyxykblhnq != null) {
                     try {
                         jbhvikudlsmqpyxykblhnq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date vxwbotmjrchwlguphb = new Date();
             String kralpeqjkslfcna = String.valueOf(vxwbotmjrchwlguphb.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper dvqukkzmjpcplvfmekhc = new SQLiteOpenHelper(DbhefhsfjypzhxnmxActivity.this, "lvznvjjkhhwtlhvbqwpdtdmwm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wqfvfkyvcwamammj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ohgxbmsmfbnomsqpcsbt = dvqukkzmjpcplvfmekhc.getWritableDatabase();
             try {
                 ohgxbmsmfbnomsqpcsbt.execSQL("UPDATE wqfvfkyvcwamammj SET value = ? WHERE name = ?", new String[]{"yzoazreeqgamuelrnw", "lohxpspaigfuxaoukxqtvmx"});
             } finally {
                 ohgxbmsmfbnomsqpcsbt.close();
                 dvqukkzmjpcplvfmekhc.close();
             }
             jqxvkgugomgmxtfutlc.animate()
                     .translationX(156f)
                     .translationY(44f)
                     .setDuration(547)
                     .start();
             Intent estgjbfinullaptpdz = new Intent();
             estgjbfinullaptpdz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.LlccdvypqnhymanoutaeudzActivity");
             startActivity(estgjbfinullaptpdz);
             File file5 = new File(getFilesDir(), "wggwigpqobwmrnjftobmtkpp.txt");
             FileWriter zrorgrkgvnhfqctywhavz = null;
             try {
                 zrorgrkgvnhfqctywhavz = new FileWriter(file5);
                 zrorgrkgvnhfqctywhavz.write("QnvMt1ievKU1V0Fiaviec7ia0jtj69uhm5HXrTatuTBWv4IiHzzJaSu4OLUPHInTtCCm06dD25RNosZPc20v9GeoRUVYUkASmOsOs0mMsYfQ6ctya40o");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zrorgrkgvnhfqctywhavz != null) {
                     try {
                         zrorgrkgvnhfqctywhavz.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date jsgimydbevjdqvwepxurxt = new Date();
             String ojuxehubnvaqiobmosvhk = String.valueOf(jsgimydbevjdqvwepxurxt.getTime());
             System.currentTimeMillis();
             File file7 = new File(getFilesDir(), "nxwxzzrolaunkitbzawa.txt");
             FileWriter nnmtgoyandchbazvimjlq = null;
             try {
                 nnmtgoyandchbazvimjlq = new FileWriter(file7);
                 nnmtgoyandchbazvimjlq.write("ClSujVxLHIIFjTdWqdwopIHW4TPYrUoPhK0kY7Yw7NoXcSsJO");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (nnmtgoyandchbazvimjlq != null) {
                     try {
                         nnmtgoyandchbazvimjlq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View tewfdvnzmptieoifahyj = findViewById(R.id.kfyiggexxmavlqgbnh);
     tewfdvnzmptieoifahyj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int msajpqiwlybuzeeqevoild = 490;
             Math.max(msajpqiwlybuzeeqevoild, 565);
             String.valueOf(msajpqiwlybuzeeqevoild);
             ScheduledExecutorService jyxfdkldcnfiqjzra = Executors.newScheduledThreadPool(4);
             jyxfdkldcnfiqjzra.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(65);
                 }
             });
             jyxfdkldcnfiqjzra.shutdown();
             tewfdvnzmptieoifahyj.animate()
                     .alpha(0.86713344f)
                     .setDuration(834)
                     .start();
             File file3 = new File(getFilesDir(), "uvcdoxtvbxwqhrbn.txt");
             if(file3.exists()) file3.delete();
             tewfdvnzmptieoifahyj.animate()
                     .rotation(271f)
                     .setDuration(409)
                     .start();
         }
     });
     final View pbidtvznrcxiozvfgdw = findViewById(R.id.eipvewjsdqslpqriusgsozr);
     pbidtvznrcxiozvfgdw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService ruizvhexlmmxjiuyzk = Executors.newScheduledThreadPool(2);
             ruizvhexlmmxjiuyzk.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(98);
                 }
             });
             ruizvhexlmmxjiuyzk.shutdown();
             Intent ysinsntyztirwgheavccfr = new Intent();
             ysinsntyztirwgheavccfr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.AbpsskyvoneiychwActivity");
             startActivity(ysinsntyztirwgheavccfr);
         }
     });

scckyxryiwhnuuozxsafxrk();
Sxrakfjkdjxuwkfjkiup   fybjwwhspxetsrmbdj =     new Sxrakfjkdjxuwkfjkiup();
fybjwwhspxetsrmbdj.urzwyvitemzxwkrolaexek ="2SyWD9drAyEvB4iDHyv8YjXkSXE17YVSGkLHOrcvbbvaEJcw4biH2uosGh6QZGwZbYJtyjUgOnAorU04Y13W0AjtaqNzoIGtwY7jIZ1HcPpg6qaZat8eD3SI25wB3lh8mV0js5jC041nNSUeMuY3W7rkwpvQ7";
fybjwwhspxetsrmbdj.tuysijxglibowwnpfgtcqpaf ="Npmyy1KTWxY8bmD8MPXCcCMaTFYz3bKYTszVMCLvFyFgBAce1DeUqD8zzoSTnapp5WwNrbT7Z8eoiQJNsEe23nut0hzL56Ac5B39";
fybjwwhspxetsrmbdj.ltpllpyniizvizvvwf ="K5J5H0Op9bbA46qOOXQ01RYMpRJV7bftAZbROikZwVwFHlj64j9Lk35LkM9WarJW5PxBKGK8YkPQdp0dOJYhlI3iWKaj8eejMNHFjLY9wQd88B2peHDDgeScsVIA5X3SgIKeL6el5YIN2ElLSs9Nd0AgT9jS";
fybjwwhspxetsrmbdj.asuyprelfxxexagsfhhhvoy ="Smu602zy6UmN6AfTVJTNv27ucaF0O3UuSmcyAbBgEOpXM2sDZXBWYdwJaT6AEAxbWaAl040hPF8uRnWzUa1DHueOVDj2GnAKrqtossyNv7a5HV8jZn0cmb7zCmeh2cW6eSzofB1CNnuKnOw";
fybjwwhspxetsrmbdj.pnlmscleoxrzajzguzsp ="1G5en116xR43HTAvDBdl3BeQwEd4q33UfVvbB174NHIfqgAc5SohAGP0AMGRMe5kOZC58rDnHWBZwcwnDbll2haDkHlGHteeDvHLwXx8WFWKy8YBkLVxZ7Glwunuk9XBWbdHtGoP71";
fybjwwhspxetsrmbdj.xltevbuzcxvmlqmiftpv ="KgEwzdVwNdQNxNS9i3FC2qBWMMGyGnWFMfbV7nhevCKVpr9V7CjjLOO9mH8aukxawJfoWwH8tjecAEuZzTTyV1PpCv1Ef1a9";
fybjwwhspxetsrmbdj.spzcdaoetlyecjspcrrueu ="myohD2FiQflmbuK1o";
fybjwwhspxetsrmbdj.optdzinbybvtulwjzhlu ="nbYw9SLu2K1d5ZODHg5F2gzl3euv8lFIbPLithGLMG1qE5HnrB3MIDTQKW6XfcelBNvIdCLGgvaRy64gEA4w3m3AMmLtbPoUx";
fybjwwhspxetsrmbdj.kuciwnmaoqtcmgywewlmor ="PAjoeRZlRMfdna9p8LUftMVkK4VBJL3HGsMWecoeGQFHQ4x9OZoDOxbyqYKWnxrCNPCR3CbuqfMhporzVsNiCnuCVOvRR";
fybjwwhspxetsrmbdj.vfgrsykuzokavxdqei ="h2SKnE5tCtkigSf67dsEodwgZGm7m99ccA01Mqxu5FAOZlstpGAfuca5vC8lATFxhaSZ6UtBwFO9D3ICJrcWvuthQCogG5RFR5vHdE4QyMVS6aTNygIWbwY0QwVRu3VaKbgJIhfsXS3YURFiWmdsH7jsBjtjR6";
fybjwwhspxetsrmbdj.xyrimmciklnytqbouigcc ="HklZBO70384DTBo8fFHyQ7kGBdhJS8lvqBz3AOVpO3fMh7IkRiZrsLuGuNT8Lcjjdo0j1RRNSOlK92f4JzKXBIMOs4LZEr1y4A8EubuGPUjwWUg4Do0eKjjG9cw1v9ztma3RYd8o94PJ2o327xZJ3UELz7IhQUn9cw3b7FvTWZLAbueHOfP";
fybjwwhspxetsrmbdj.wzojegnovrcnzxkvhrsjw ="sAyFy1POrp6KqC6uHmeXzaE6iAiNptWnqTwlJKh2qiJf7iyLIXPWB6qqcDDfwQidIQutBX9wHnR8tsZtTiMAGwknK6RQ7MLUGQ3Ge4sbWNMFavlfXSOsGDobS48IyPbrNNbxYDKLPNTWloG2j2cCzKMl23sRM9cWeclQOPnbTc4DUdIaaRKyWPCGH1su";
fybjwwhspxetsrmbdj.mwfiazjgdhclalsaclmdypbse ="Fmz7q3FXufzsG3Agk6Es1iT6u2AWgoH60BdMYOdHKYtryT8IRI1WxXRHBtsedsEG106gXLOSCD";
fybjwwhspxetsrmbdj.rezblnwjuhrnpdwpdr ="3vIXa0z44ahF1K7az4vhTwh3wtd4bkRPmRc1mwpABOELRbP1lO4y0b3Gccn7CYUQYDgRC1vg0CX3Yq3v3Fu1tMfkz3M1htvfecX2XzNCp1mRaAdwbUN0QRZIPTL";
fybjwwhspxetsrmbdj.vcvajtlnmpufpsdli ="rVOkPTcOTRTPcveGX9eHsoievfUCznRepyAWPYI8i7vUIhpWJJkK35953pN3KI94ZPZk3D0SgcCwaUCEJb26WIM3zsqwCMAq10IAUtla5kbklmTMwlFTIcIZl0fWo37mCbT8CMX7JBEYxqpuWLBL1YPwBPOmuQ0swHg0XE5FVrPnovvtJGtbQ";
fybjwwhspxetsrmbdj.spcbxbogbwztqkconqob ="ZkFdjr6nGWSITrcnjVDmFWTAIRYKYHbRzE83IXVi5ZkKyOE9S8ZnrUdJb6GFcqNoYkzO1LYNw3uRpZr2QNQILyQF3Mv6nUXBLhYjhRjjBsDQgs2IO7suZQA7nhvvCKFbHP2Yp5hLZU5ttaC7eo77BJvPR52gSVO3AL3b9FHAvgvm2BpB25Nr";
 Toast.makeText(DbhefhsfjypzhxnmxActivity.this,getString(R.string.zqzblwiqrabklyw),Toast.LENGTH_SHORT).show();
    }
 void scckyxryiwhnuuozxsafxrk() {
     GridView fujqziudqmmisbzhvvfu = new GridView(DbhefhsfjypzhxnmxActivity.this);
     fujqziudqmmisbzhvvfu.setVisibility(View.VISIBLE);
     fujqziudqmmisbzhvvfu.setEnabled(true);
     fcdgnqjdsnlmhsoytccww();
}
 void fcdgnqjdsnlmhsoytccww() {
     long tqqmelckibcdrvy = System.currentTimeMillis();
     Date date = new Date(tqqmelckibcdrvy);
     String time = date.toString();
     corewofkbsshsvzcds();
}
 void corewofkbsshsvzcds() {
     int bikmsmtcbhrsedp = 452;
     double result = Math.sqrt(bikmsmtcbhrsedp);
     result = Math.pow(result, 2);
     dtuvnppufuyhfmsujhlqm();
}
 void dtuvnppufuyhfmsujhlqm() {
     String hhgjziyrvwowwxfoanwasdtoc = "cgzXz31JSOaOWTTO6eoFYwOw0DtbWrPXo09YQBIO8ClFm4ihIIMQVd8KJbRsl12ADP0lEZ00WMFPHe4yAVK4QYHDi7qMigptWU5Lzc5C";
     hhgjziyrvwowwxfoanwasdtoc = hhgjziyrvwowwxfoanwasdtoc.substring(1);
     hhgjziyrvwowwxfoanwasdtoc = hhgjziyrvwowwxfoanwasdtoc.toUpperCase();
}}