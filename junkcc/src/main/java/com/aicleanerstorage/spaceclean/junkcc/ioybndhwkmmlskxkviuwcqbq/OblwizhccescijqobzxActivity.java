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
import android.widget.Switch;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class OblwizhccescijqobzxActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_blwizhccescijqobzx);
     final View yrgyksdceauktsxonocxw = findViewById(R.id.rmyvalmkefzcbjgdz);
     yrgyksdceauktsxonocxw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "udeourrwytznscgadyir.txt");
             FileReader ytmtgapntnnzvkl = null;
             try {
                 if(file0.exists()) {
                     ytmtgapntnnzvkl = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ytmtgapntnnzvkl != null) {
                     try {
                         ytmtgapntnnzvkl.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent uchivdtqfoxruntwdxypx = new Intent();
             uchivdtqfoxruntwdxypx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.YsejxfgelmlzrzrrslsmuActivity");
             startActivity(uchivdtqfoxruntwdxypx);
             yrgyksdceauktsxonocxw.animate()
                     .scaleX(0.4741522f)
                     .scaleY(0.4741522f)
                     .setDuration(322)
                     .start();
         }
     });
     final View ikqkzeogiycektztu = findViewById(R.id.jysbwyyuaqmbzkvtiyhtta);
     ikqkzeogiycektztu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ikqkzeogiycektztu.animate()
                     .alpha(0.30122703f)
                     .setDuration(943)
                     .start();
             Intent sgvltdgzverthlgtqwwep = new Intent();
             sgvltdgzverthlgtqwwep.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.UnynlucvjuoetwykrcwiteActivity");
             startActivity(sgvltdgzverthlgtqwwep);
             ScheduledExecutorService suyjmhpyawbxtizj = Executors.newScheduledThreadPool(2);
             suyjmhpyawbxtizj.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(54);
                 }
             });
             suyjmhpyawbxtizj.shutdown();
             SQLiteOpenHelper dvvhsiuirumgpjfmags = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "zjhrvzdpewtkrdi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rrznguxpcicnepiheo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gfndqpjojyosmtegoxfbp = dvvhsiuirumgpjfmags.getWritableDatabase();
             try {
                 gfndqpjojyosmtegoxfbp.execSQL("DELETE FROM rrznguxpcicnepiheo WHERE name = ?", new String[]{"xdyfrblzjverehqlfgdagg", "pofrwyptgencaqrkke"});
             } finally {
                 gfndqpjojyosmtegoxfbp.close();
                 dvvhsiuirumgpjfmags.close();
             }
             ikqkzeogiycektztu.animate()
                     .scaleX(0.81766486f)
                     .scaleY(0.81766486f)
                     .setDuration(365)
                     .start();
             ScheduledExecutorService cretajdznhijzfplrami = Executors.newScheduledThreadPool(3);
             cretajdznhijzfplrami.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(40);
                 }
             });
             cretajdznhijzfplrami.shutdown();
             File file6 = new File(getFilesDir(), "zvkvivxwfuykdsmk.txt");
             FileReader gekkeqksmhdhwkn = null;
             try {
                 if(file6.exists()) {
                     gekkeqksmhdhwkn = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (gekkeqksmhdhwkn != null) {
                     try {
                         gekkeqksmhdhwkn.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date vhabbqeytplnlvzam = new Date();
             String vbmmuylogsmrcccsljsxy = String.valueOf(vhabbqeytplnlvzam.getTime());
             System.currentTimeMillis();
         }
     });
     final View znohfpedvowovllpqndq = findViewById(R.id.uluuzjotqsqkmgddh);
     znohfpedvowovllpqndq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ipopjbtyvjosjxscozn = new Intent();
             ipopjbtyvjosjxscozn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.RvfnzpczxqhstsxobjeiActivity");
             startActivity(ipopjbtyvjosjxscozn);
             SQLiteOpenHelper nfprvgrnxfaebhnqeltxa = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "nuvxkzxbixbfkquqhpipqa.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cvymxqfuydvvgziznkoltewrf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase abvjxhvvmrcsohnozyu = nfprvgrnxfaebhnqeltxa.getWritableDatabase();
             try {
                 abvjxhvvmrcsohnozyu.execSQL("UPDATE cvymxqfuydvvgziznkoltewrf SET value = ? WHERE name = ?", new String[]{"tfkvoezuxjcggradcepeuwa", "hhxgulfsuzlkbovziknu"});
             } finally {
                 abvjxhvvmrcsohnozyu.close();
                 nfprvgrnxfaebhnqeltxa.close();
             }
             File file2 = new File(getFilesDir(), "vuhqfublxblahqmsuzvlhz.txt");
             if(file2.exists()) file2.delete();
             Intent skzunbedazfapmyyjal = new Intent();
             skzunbedazfapmyyjal.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.RvfnzpczxqhstsxobjeiActivity");
             startActivity(skzunbedazfapmyyjal);
             Handler edzrmeixfkawqztiyyiefzvtp = new Handler(Looper.getMainLooper());
             edzrmeixfkawqztiyyiefzvtp.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     znohfpedvowovllpqndq.setVisibility(View.VISIBLE);
                 }
             }, 873);
             SQLiteOpenHelper nddifavjtzzlpvqdfpolhje = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "rcwqbqhuglkbxoiegsnhpie.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pnrywdcjgmsltxalhvwbetf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase emwkpllfjjpfzzi = nddifavjtzzlpvqdfpolhje.getWritableDatabase();
             try {
                 emwkpllfjjpfzzi.execSQL("INSERT INTO pnrywdcjgmsltxalhvwbetf (name, value) VALUES (?, ?)", new String[]{"mdvopooiulorvuire", "rscfynosppykommrlu"});
             } finally {
                 emwkpllfjjpfzzi.close();
                 nddifavjtzzlpvqdfpolhje.close();
             }
             Intent ejeqbyuszvjfjrxb = new Intent();
             ejeqbyuszvjfjrxb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.RvfnzpczxqhstsxobjeiActivity");
             startActivity(ejeqbyuszvjfjrxb);
             File file7 = new File(getFilesDir(), "lhpxoixotzsqubkiv.txt");
         }
     });
     final View dthabskwjrtxfcdo = findViewById(R.id.tvkblqgcbdjotufchadhldz);
     dthabskwjrtxfcdo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date kbserhsuddmfeswaixbjulmuo = new Date();
             String hetquuokcyfrhzmiaqo = String.valueOf(kbserhsuddmfeswaixbjulmuo.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "mtxsyzreasldoaqqjouojas.txt");
             FileReader ivmvwigvaapvidltcldmmzle = null;
             try {
                 if(file1.exists()) {
                     ivmvwigvaapvidltcldmmzle = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ivmvwigvaapvidltcldmmzle != null) {
                     try {
                         ivmvwigvaapvidltcldmmzle.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService baiyrgjyxuclppoq = Executors.newScheduledThreadPool(3);
             baiyrgjyxuclppoq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(35);
                 }
             });
             baiyrgjyxuclppoq.shutdown();
             File file3 = new File(getFilesDir(), "wcbxanohdqenluvnh.txt");
             if(file3.exists()) file3.delete();
             SQLiteOpenHelper mstumkhbxancsrmzsupcqahmm = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "ytkhynidpvbqvggnpaolshon.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xeviexiezfxxpvpfkzb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nywddrfdudahfrdvwrg = mstumkhbxancsrmzsupcqahmm.getWritableDatabase();
             try {
                 nywddrfdudahfrdvwrg.execSQL("UPDATE xeviexiezfxxpvpfkzb SET value = ? WHERE name = ?", new String[]{"lczrhykulorzvvvoubvoqv", "slfuqwzidxvmewcsdwqpl"});
             } finally {
                 nywddrfdudahfrdvwrg.close();
                 mstumkhbxancsrmzsupcqahmm.close();
             }
             int kgxyjfkcmhyvwkuypbdnlq = 336;
             Math.max(kgxyjfkcmhyvwkuypbdnlq, 318);
             String.valueOf(kgxyjfkcmhyvwkuypbdnlq);
             Intent akvqohgfdzslwqqtisdkuqmg = new Intent();
             akvqohgfdzslwqqtisdkuqmg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.LbbzdphxtvonyqmyhhbncfwkActivity");
             startActivity(akvqohgfdzslwqqtisdkuqmg);
         }
     });
     final View yxgdpdtfruoakxynmvnf = findViewById(R.id.pqmjellibnsymsaymgawdom);
     yxgdpdtfruoakxynmvnf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "qdpdpsgnmizdvda.txt");
             FileReader suaboyclcoxmhqscdrptd = null;
             try {
                 if(file0.exists()) {
                     suaboyclcoxmhqscdrptd = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (suaboyclcoxmhqscdrptd != null) {
                     try {
                         suaboyclcoxmhqscdrptd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             yxgdpdtfruoakxynmvnf.animate()
                     .rotation(171f)
                     .setDuration(191)
                     .start();
             Handler sozkcfuviabwakwritmsvjt = new Handler(Looper.getMainLooper());
             sozkcfuviabwakwritmsvjt.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     yxgdpdtfruoakxynmvnf.setVisibility(View.VISIBLE);
                 }
             }, 177);
             yxgdpdtfruoakxynmvnf.animate()
                     .scaleX(0.687497f)
                     .scaleY(0.687497f)
                     .setDuration(549)
                     .start();
             SQLiteOpenHelper qffjesyizjprpfim = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "hrqzqqtjcxhvffehicnewuon.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "egiqwcluoqcvjlw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase oljfotdkgxzybiyuzncajjw = qffjesyizjprpfim.getWritableDatabase();
             try {
                 oljfotdkgxzybiyuzncajjw.execSQL("INSERT INTO egiqwcluoqcvjlw (name, value) VALUES (?, ?)", new String[]{"viczkvyzutemfsavb", "gtupcabqusidwecvklsuspc"});
             } finally {
                 oljfotdkgxzybiyuzncajjw.close();
                 qffjesyizjprpfim.close();
             }
             Intent imaqjrzxecrmivqkxgi = new Intent();
             imaqjrzxecrmivqkxgi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.CaauqndplkzazaohftdcxActivity");
             startActivity(imaqjrzxecrmivqkxgi);
         }
     });
     final View bxnbpflijxssxtjqvqg = findViewById(R.id.susscfysfpqzgspnjlunb);
     bxnbpflijxssxtjqvqg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "thiuegdedodtltrd.txt");
             SQLiteOpenHelper nvaqxskqpsgiramkif = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "ftkxhigcanugvanbpvaqnnm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zorzxeforrzdfwdjqhzn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lxwyulqpckkxqrmn = nvaqxskqpsgiramkif.getWritableDatabase();
             try {
                 lxwyulqpckkxqrmn.execSQL("SELECT * FROM zorzxeforrzdfwdjqhzn WHERE name = ?", new String[]{"iofsmsaiyvntjapfozyqmjj", "ymzifholwwrcbgrckox"});
             } finally {
                 lxwyulqpckkxqrmn.close();
                 nvaqxskqpsgiramkif.close();
             }
             Intent nourafqjediogiylrqx = new Intent();
             nourafqjediogiylrqx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.TuxpsnardomufikxqdflbddszxActivity");
             startActivity(nourafqjediogiylrqx);
         }
     });
     final View pszfgseijtcczbfly = findViewById(R.id.gylbbvtmplgarhbcgyvb);
     pszfgseijtcczbfly.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int melyeswcsktscnfb = 692;
             Math.max(melyeswcsktscnfb, 718);
             String.valueOf(melyeswcsktscnfb);
             ScheduledExecutorService ovugfrauagemhdic = Executors.newScheduledThreadPool(4);
             ovugfrauagemhdic.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(29);
                 }
             });
             ovugfrauagemhdic.shutdown();
             pszfgseijtcczbfly.animate()
                     .alpha(0.91464174f)
                     .setDuration(231)
                     .start();
             SQLiteOpenHelper ieqdyhmgxuwnswkuvsn = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "uwdrqpotrhesqnbnhsxbshxyx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fnhwvnhbsdndioyetwkjlry"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lkqvhgpicyrkilkgrdwwthvnw = ieqdyhmgxuwnswkuvsn.getWritableDatabase();
             try {
                 lkqvhgpicyrkilkgrdwwthvnw.execSQL("SELECT * FROM fnhwvnhbsdndioyetwkjlry WHERE name = ?", new String[]{"axxpichlmeqcwpgcrzm", "tlwkqtdcpzdepcjtkaibiq"});
             } finally {
                 lkqvhgpicyrkilkgrdwwthvnw.close();
                 ieqdyhmgxuwnswkuvsn.close();
             }
             pszfgseijtcczbfly.animate()
                     .rotation(306f)
                     .setDuration(646)
                     .start();
             Intent cezjgouxkufasawcaolotsiza = new Intent();
             cezjgouxkufasawcaolotsiza.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.LfosyjluljeatjwgcaycqgActivity");
             startActivity(cezjgouxkufasawcaolotsiza);
         }
     });
     final View ctdgzekfbihjziqunvx = findViewById(R.id.xkpigfdoncnhlqwtmo);
     ctdgzekfbihjziqunvx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ctdgzekfbihjziqunvx.animate()
                     .alpha(0.094885886f)
                     .setDuration(255)
                     .start();
             File file1 = new File(getFilesDir(), "zfjqnclmboheyqdlmtg.txt");
             SQLiteOpenHelper ttlfuwnqodptlipvnxrre = new SQLiteOpenHelper(OblwizhccescijqobzxActivity.this, "hdxkbhxtgyheeiyfojb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ivdxobrpljlessiatqxmeglde"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zywkqjwriiamvsnnvlcn = ttlfuwnqodptlipvnxrre.getWritableDatabase();
             try {
                 zywkqjwriiamvsnnvlcn.execSQL("DELETE FROM ivdxobrpljlessiatqxmeglde WHERE name = ?", new String[]{"wbkzstrgotaqvsukchxt", "njlyfqhijanpustghynlr"});
             } finally {
                 zywkqjwriiamvsnnvlcn.close();
                 ttlfuwnqodptlipvnxrre.close();
             }
             File file3 = new File(getFilesDir(), "nlyxujlshftrpsn.txt");
             Intent inbfrkfvdlykijvcaci = new Intent();
             inbfrkfvdlykijvcaci.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ioybndhwkmmlskxkviuwcqbq.SmsvcptrhomelcajjtborcActivity");
             startActivity(inbfrkfvdlykijvcaci);
             Date bdegbqtoyzkytxpcfzhbnplb = new Date();
             String mgswjtednydthfgrj = String.valueOf(bdegbqtoyzkytxpcfzhbnplb.getTime());
             System.currentTimeMillis();
             File file6 = new File(getFilesDir(), "albgfrftiojlrjseupxfj.txt");
             FileReader shydebbywgjuihgrmwmhc = null;
             try {
                 if(file6.exists()) {
                     shydebbywgjuihgrmwmhc = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (shydebbywgjuihgrmwmhc != null) {
                     try {
                         shydebbywgjuihgrmwmhc.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });

dsxatsxppwhzxxiluwdtcrfq();
Pfoumsrwzfbmtprktobuelgobb   rmrsqlknzqhcitd =     new Pfoumsrwzfbmtprktobuelgobb();
 Toast.makeText(OblwizhccescijqobzxActivity.this,getString(R.string.ygpcppmrqvfclchsz),Toast.LENGTH_SHORT).show();
    }
 void dsxatsxppwhzxxiluwdtcrfq() {
     int wmfxogrvvovjwiyynn = 693;
     double result = Math.sqrt(wmfxogrvvovjwiyynn);
     result = Math.pow(result, 2);
     vtlleqmxwhvnzoqviqlpoc();
}
 void vtlleqmxwhvnzoqviqlpoc() {
     String zzanyaylelbgvabqcemrcpug = "X99AnuOgosmcusnoHtiZkuAI54xQIhH1L9o1kShoHHa02u3IbcqjQ4HwJy7h2UUvQ2T8E";
     zzanyaylelbgvabqcemrcpug = zzanyaylelbgvabqcemrcpug.substring(2);
     zzanyaylelbgvabqcemrcpug = zzanyaylelbgvabqcemrcpug.toUpperCase();
     qholonqksxwkyryfx();
}
 void qholonqksxwkyryfx() {
     long tniqvagiqethapodfbncuvzfs = System.currentTimeMillis();
     Date date = new Date(tniqvagiqethapodfbncuvzfs);
     String time = date.toString();
}}