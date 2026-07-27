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
import android.widget.DatePicker;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class AumbpqnpfxrclaoocnnatlwzgActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_umbpqnpfxrclaoocnnatlwzg);
     final View tdgfugbboaixzziiyduuyjyu = findViewById(R.id.veoopmkoymacuvvduugegzrv);
     tdgfugbboaixzziiyduuyjyu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper nrkfqprzjecglylfuqaypemiq = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "qfjjugekdllzlmwmurktbqi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ossuosusbpezhisswsgamfh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dwkalqcfwuvrxyu = nrkfqprzjecglylfuqaypemiq.getWritableDatabase();
             try {
                 dwkalqcfwuvrxyu.execSQL("UPDATE ossuosusbpezhisswsgamfh SET value = ? WHERE name = ?", new String[]{"xbjngfdwahpyigogfxahj", "inskiqgssfwjhoyxn"});
             } finally {
                 dwkalqcfwuvrxyu.close();
                 nrkfqprzjecglylfuqaypemiq.close();
             }
             tdgfugbboaixzziiyduuyjyu.animate()
                     .rotation(253f)
                     .setDuration(12)
                     .start();
             SQLiteOpenHelper tlqmcgtymvmuxrrbtqe = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "pkttfykdcigvmvc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "zndoqwkrgrpqwljxhwaf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fglvngvefhcbdavpqqcmcjrki = tlqmcgtymvmuxrrbtqe.getWritableDatabase();
             try {
                 fglvngvefhcbdavpqqcmcjrki.execSQL("SELECT * FROM zndoqwkrgrpqwljxhwaf WHERE name = ?", new String[]{"efvkvkyfuafzbakkqlwualn", "vxdxueiuurczjbbmqaab"});
             } finally {
                 fglvngvefhcbdavpqqcmcjrki.close();
                 tlqmcgtymvmuxrrbtqe.close();
             }
         }
     });
     final View maagnhnfukiegsrvoigd = findViewById(R.id.qoirgfqxxwawinfgvfsm);
     maagnhnfukiegsrvoigd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "zuetnmymkdiydswrbhz.txt");
             Intent dvjituyfmtdzubrzvnpwsl = new Intent();
             dvjituyfmtdzubrzvnpwsl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.PrbjyhhcqagzunojoxlbtpzActivity");
             startActivity(dvjituyfmtdzubrzvnpwsl);
             maagnhnfukiegsrvoigd.animate()
                     .scaleX(1.9607824f)
                     .scaleY(1.9607824f)
                     .setDuration(634)
                     .start();
             File file3 = new File(getFilesDir(), "fqhcclqbcjltikztmio.txt");
             if(file3.exists()) file3.delete();
             Intent dfxlqwkzttyhefqhjeigxbxtk = new Intent();
             dfxlqwkzttyhefqhjeigxbxtk.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.PrbjyhhcqagzunojoxlbtpzActivity");
             startActivity(dfxlqwkzttyhefqhjeigxbxtk);
             File file5 = new File(getFilesDir(), "xvdygiysajtboxqhkaw.txt");
             FileReader hibbohkhtoetljjsad = null;
             try {
                 if(file5.exists()) {
                     hibbohkhtoetljjsad = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (hibbohkhtoetljjsad != null) {
                     try {
                         hibbohkhtoetljjsad.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View wnfyalxtqdpxkatuqu = findViewById(R.id.yvreyxoexaxrwtnqg);
     wnfyalxtqdpxkatuqu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tnwghlpyrblcdlffpcjzhpzs = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "vnlypckysaskzunnyhne.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "immcduazientcitudt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vdmaobivjfcamityolbbh = tnwghlpyrblcdlffpcjzhpzs.getWritableDatabase();
             try {
                 vdmaobivjfcamityolbbh.execSQL("DELETE FROM immcduazientcitudt WHERE name = ?", new String[]{"qrsfeqeclmctxhqcaswx", "sqwupqicgliysuskwrq"});
             } finally {
                 vdmaobivjfcamityolbbh.close();
                 tnwghlpyrblcdlffpcjzhpzs.close();
             }
             int exkxdqrwyzksfiavxfgecaxun = 949;
             Math.max(exkxdqrwyzksfiavxfgecaxun, 55);
             String.valueOf(exkxdqrwyzksfiavxfgecaxun);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(AumbpqnpfxrclaoocnnatlwzgActivity.this, "gbnbxsjeooobrhp", Toast.LENGTH_SHORT).show();
                 }
             });
             Date msjkoxszxtgzerhck = new Date();
             String ifbwepevrwbuhqfpiwvzfqmv = String.valueOf(msjkoxszxtgzerhck.getTime());
             System.currentTimeMillis();
         }
     });
     final View eayuotnhechlkge = findViewById(R.id.tautkqbbdbsscpwwflvlxol);
     eayuotnhechlkge.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler wcxsdzukewxkmcvr = new Handler(Looper.getMainLooper());
             wcxsdzukewxkmcvr.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     eayuotnhechlkge.setVisibility(View.VISIBLE);
                 }
             }, 54);
             Intent evirdiuintuughlji = new Intent();
             evirdiuintuughlji.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.IbevjtigpywllaclrqsciActivity");
             startActivity(evirdiuintuughlji);
             ScheduledExecutorService ogqqqvkbimjwakwvlzyarrz = Executors.newScheduledThreadPool(1);
             ogqqqvkbimjwakwvlzyarrz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(50);
                 }
             });
             ogqqqvkbimjwakwvlzyarrz.shutdown();
             eayuotnhechlkge.animate()
                     .scaleX(0.058915377f)
                     .scaleY(0.058915377f)
                     .setDuration(587)
                     .start();
             File file4 = new File(getFilesDir(), "zpbruualamdfpozyufzpenxr.txt");
             SQLiteOpenHelper nbzqyrfyjwrpugfb = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "gruzxasxawelqirdtxryuork.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "isyfbklfhgtmelkkhutieky"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tfkxotbkgyvhwykztypowt = nbzqyrfyjwrpugfb.getWritableDatabase();
             try {
                 tfkxotbkgyvhwykztypowt.execSQL("UPDATE isyfbklfhgtmelkkhutieky SET value = ? WHERE name = ?", new String[]{"nhxuxeypoxfljizvrkj", "oybzlnwiluoyigxdct"});
             } finally {
                 tfkxotbkgyvhwykztypowt.close();
                 nbzqyrfyjwrpugfb.close();
             }
             Intent dguxqtiemqlebdh = new Intent();
             dguxqtiemqlebdh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.IbevjtigpywllaclrqsciActivity");
             startActivity(dguxqtiemqlebdh);
             ScheduledExecutorService vrhslkwszahrsfjcxml = Executors.newScheduledThreadPool(1);
             vrhslkwszahrsfjcxml.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(87);
                 }
             });
             vrhslkwszahrsfjcxml.shutdown();
             File file8 = new File(getFilesDir(), "hsyfwjpiqgswkyrwelgfpi.txt");
             if(file8.exists()) file8.delete();
             eayuotnhechlkge.animate()
                     .alpha(0.9242547f)
                     .setDuration(925)
                     .start();
         }
     });
     final View oefyilcpjjfcvzgukqbyu = findViewById(R.id.qntxtteujovsjwxleln);
     oefyilcpjjfcvzgukqbyu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date jfvkkdgdwijmgjgmxudlistsb = new Date();
             String joyxvhzupjqzadsraduirsqmr = String.valueOf(jfvkkdgdwijmgjgmxudlistsb.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "iemexjcalgifmkdmj.txt");
         }
     });
     final View nvdrbriysbgwfrrscqfqbo = findViewById(R.id.vnsxksadobnqhgklx);
     nvdrbriysbgwfrrscqfqbo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String jxusvrdfinjzyiuph = "0jm53QLetwiePSED3qd20IW1aQeBIcryJwtqnoGullAf3L3tmNa3tNvG2FhLwVjDJxHaNTN37KEc214GiIRMgBC1OSBHQciyKeK29LHWkrBZOjNdC4MJrqBaOesgamz2v07TvRTOd22XDY0AlfEQYYXYzfa6YzcMMPODP1D1LTtcSW9mqeuQRi0k5";
             jxusvrdfinjzyiuph.substring(1);
             jxusvrdfinjzyiuph.replace('s', 'h');
             SQLiteOpenHelper damllflpzolpgsoufsmrw = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "icnofijylkjktnhuuxzm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "mqpkjhpjgnutdkvreswik"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase udhwbkmswzhjtlvpvryfxse = damllflpzolpgsoufsmrw.getWritableDatabase();
             try {
                 udhwbkmswzhjtlvpvryfxse.execSQL("SELECT * FROM mqpkjhpjgnutdkvreswik WHERE name = ?", new String[]{"ijcjyaebhuooecumxolo", "gnsktdzvqgatxbnusaz"});
             } finally {
                 udhwbkmswzhjtlvpvryfxse.close();
                 damllflpzolpgsoufsmrw.close();
             }
             int zeokbpedjaytpurfahw = 598;
             Math.max(zeokbpedjaytpurfahw, 909);
             String.valueOf(zeokbpedjaytpurfahw);
         }
     });
     final View weqmqjtkylmkniyyfexo = findViewById(R.id.ttlvpupbfhfiulskwzagd);
     weqmqjtkylmkniyyfexo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent hlagawrzjnkuzstbqacyay = new Intent();
             hlagawrzjnkuzstbqacyay.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.RpkuuowuduwvfeuzgjbysfActivity");
             startActivity(hlagawrzjnkuzstbqacyay);
             Date rgqgpzkfirlgjvqlpcdn = new Date();
             String qjxefbizjfvrwfx = String.valueOf(rgqgpzkfirlgjvqlpcdn.getTime());
             System.currentTimeMillis();
             weqmqjtkylmkniyyfexo.animate()
                     .translationX(165f)
                     .translationY(57f)
                     .setDuration(602)
                     .start();
             SQLiteOpenHelper kzeyehlrowrzolxqrlg = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "qtyfedjxndubhabuyfrxg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ioxdxgdsvbgaetgcrnjlrrb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ytflapfmsxtweqadopb = kzeyehlrowrzolxqrlg.getWritableDatabase();
             try {
                 ytflapfmsxtweqadopb.execSQL("INSERT INTO ioxdxgdsvbgaetgcrnjlrrb (name, value) VALUES (?, ?)", new String[]{"asrljppzxnsxtucnhyqmuih", "rxcpbwceiqtwvipqonsahgq"});
             } finally {
                 ytflapfmsxtweqadopb.close();
                 kzeyehlrowrzolxqrlg.close();
             }
             weqmqjtkylmkniyyfexo.animate()
                     .scaleX(0.6672518f)
                     .scaleY(0.6672518f)
                     .setDuration(257)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(AumbpqnpfxrclaoocnnatlwzgActivity.this, "qnuqtblhmcgcrgtyruk", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper vzefbhrlwbcdjtu = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "xvjwontrqlrjacdmavmhu.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wkuhqwffwwlanqsobweklvlx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xulznmyymbdbjty = vzefbhrlwbcdjtu.getWritableDatabase();
             try {
                 xulznmyymbdbjty.execSQL("DELETE FROM wkuhqwffwwlanqsobweklvlx WHERE name = ?", new String[]{"aekyvhgvnvhybwvr", "chudlosqykwowfeoodh"});
             } finally {
                 xulznmyymbdbjty.close();
                 vzefbhrlwbcdjtu.close();
             }
             Intent gmtlxtjsjpgjumvwox = new Intent();
             gmtlxtjsjpgjumvwox.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.RpkuuowuduwvfeuzgjbysfActivity");
             startActivity(gmtlxtjsjpgjumvwox);
             Date exjnisfsarwkjuilsquifgqlg = new Date();
             String qralmmpzxvlzrpsgf = String.valueOf(exjnisfsarwkjuilsquifgqlg.getTime());
             System.currentTimeMillis();
             Intent yvccgeetzucckvjxuvujxons = new Intent();
             yvccgeetzucckvjxuvujxons.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.RpkuuowuduwvfeuzgjbysfActivity");
             startActivity(yvccgeetzucckvjxuvujxons);
         }
     });
     final View ornzgnpkiglxaidhr = findViewById(R.id.fmsswqpznkxkxdyssauvhrxf);
     ornzgnpkiglxaidhr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "xqyiutqgryjbnrupott.txt");
             if(file0.exists()) file0.delete();
             int uvypkkrfahebuystxtnlmzl = 841;
             Math.max(uvypkkrfahebuystxtnlmzl, 182);
             String.valueOf(uvypkkrfahebuystxtnlmzl);
         }
     });
     final View bicicrwmwyoruwwwsvbyel = findViewById(R.id.eisnipuwrzxavktywpcwhbynt);
     bicicrwmwyoruwwwsvbyel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ejqghywfobyapvshwc = new Intent();
             ejqghywfobyapvshwc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.SegcaipkbgujfcshsodeinaoActivity");
             startActivity(ejqghywfobyapvshwc);
             File file1 = new File(getFilesDir(), "vbmmhcsylpgvjlhxjtxco.txt");
             FileReader arqlkobyzjgwtmvgiptaqa = null;
             try {
                 if(file1.exists()) {
                     arqlkobyzjgwtmvgiptaqa = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (arqlkobyzjgwtmvgiptaqa != null) {
                     try {
                         arqlkobyzjgwtmvgiptaqa.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent gsfhfflyajlttgywzfn = new Intent();
             gsfhfflyajlttgywzfn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.SegcaipkbgujfcshsodeinaoActivity");
             startActivity(gsfhfflyajlttgywzfn);
             SQLiteOpenHelper spzvhpzgjbtcwujc = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "alvslwlsrxgucngpsmdxih.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nsdlrxmcfkschov"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wpujymcjnhlwudcezui = spzvhpzgjbtcwujc.getWritableDatabase();
             try {
                 wpujymcjnhlwudcezui.execSQL("DELETE FROM nsdlrxmcfkschov WHERE name = ?", new String[]{"vleyfpemstxqdnvuaojlbws", "hjdzvloxnhtzvfclvwadzmmd"});
             } finally {
                 wpujymcjnhlwudcezui.close();
                 spzvhpzgjbtcwujc.close();
             }
             bicicrwmwyoruwwwsvbyel.animate()
                     .scaleX(0.116372585f)
                     .scaleY(0.116372585f)
                     .setDuration(906)
                     .start();
             ScheduledExecutorService eszwrhdmpcyknazoks = Executors.newScheduledThreadPool(1);
             eszwrhdmpcyknazoks.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(91);
                 }
             });
             eszwrhdmpcyknazoks.shutdown();
             Intent xldnqzeaewlgyygyctzpppff = new Intent();
             xldnqzeaewlgyygyctzpppff.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.SegcaipkbgujfcshsodeinaoActivity");
             startActivity(xldnqzeaewlgyygyctzpppff);
             ScheduledExecutorService qocdfnkwtxmkrfo = Executors.newScheduledThreadPool(4);
             qocdfnkwtxmkrfo.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(6);
                 }
             });
             qocdfnkwtxmkrfo.shutdown();
             Intent oimsiajugzgbxnkb = new Intent();
             oimsiajugzgbxnkb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.SegcaipkbgujfcshsodeinaoActivity");
             startActivity(oimsiajugzgbxnkb);
             int cauhahgqylclvwbqmzoz = 117;
             Math.max(cauhahgqylclvwbqmzoz, 638);
             String.valueOf(cauhahgqylclvwbqmzoz);
         }
     });
     final View evrmsqbfdhxgvhulvceeqdjl = findViewById(R.id.gyafxpmmwgaquvednqppijf);
     evrmsqbfdhxgvhulvceeqdjl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int mbjwblthecktqfjipzuhsxy = 269;
             Math.max(mbjwblthecktqfjipzuhsxy, 439);
             String.valueOf(mbjwblthecktqfjipzuhsxy);
             Intent vlchvvjngxntcnpvmtrxzkcso = new Intent();
             vlchvvjngxntcnpvmtrxzkcso.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.OlrtvuwvvfworeaeieuecqiActivity");
             startActivity(vlchvvjngxntcnpvmtrxzkcso);
         }
     });
     final View cnjoaafmrupqwhlnunaibwr = findViewById(R.id.wuhfosvwohbabkdfje);
     cnjoaafmrupqwhlnunaibwr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date rsjbotlbtkohmsxlox = new Date();
             String vspntjnblhwjcxhgaxopx = String.valueOf(rsjbotlbtkohmsxlox.getTime());
             System.currentTimeMillis();
             Intent orkagkkhgfuewkvo = new Intent();
             orkagkkhgfuewkvo.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.VzebxhuhffwommyisujqrbActivity");
             startActivity(orkagkkhgfuewkvo);
             String xdhzmqfdnhhmuxioelsuyls = "oEhNtHM9EWo6BNjGeQ4hyMhxjAIMgcrNZUwSNAweomqeOt";
             xdhzmqfdnhhmuxioelsuyls.substring(3);
             xdhzmqfdnhhmuxioelsuyls.replace('m', 'z');
             SQLiteOpenHelper kjmefwbpzboqnkbg = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "txxyubneynurzwm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hhcydyxoiyddbpljgpofj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lvgrdeffzwfkfhz = kjmefwbpzboqnkbg.getWritableDatabase();
             try {
                 lvgrdeffzwfkfhz.execSQL("INSERT INTO hhcydyxoiyddbpljgpofj (name, value) VALUES (?, ?)", new String[]{"fhcpssywxsutinauws", "otyhexvqkcsgisbqpu"});
             } finally {
                 lvgrdeffzwfkfhz.close();
                 kjmefwbpzboqnkbg.close();
             }
             ScheduledExecutorService xiiyildwpfdmbsudinl = Executors.newScheduledThreadPool(3);
             xiiyildwpfdmbsudinl.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(80);
                 }
             });
             xiiyildwpfdmbsudinl.shutdown();
         }
     });
     final View yzphzywndkocpyz = findViewById(R.id.vyfoqfjrledumkimujiou);
     yzphzywndkocpyz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper gkwhpvuvtatvjdbf = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "cnfqahznxutzqitvxxpmhmx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ijgkyberqgqiwhlsnucvhvc"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tnuslsjvwbwqgfhjxak = gkwhpvuvtatvjdbf.getWritableDatabase();
             try {
                 tnuslsjvwbwqgfhjxak.execSQL("SELECT * FROM ijgkyberqgqiwhlsnucvhvc WHERE name = ?", new String[]{"jvlvsqdbpfjvbykfaqxxgyy", "wlhbhdjlcdakoqhh"});
             } finally {
                 tnuslsjvwbwqgfhjxak.close();
                 gkwhpvuvtatvjdbf.close();
             }
             String cjycxsezjqiopwqexenmddqvz = "1oanSeCx5mvvC1lqJqk63EDH40TcbDVh1T0VCOMlW7la9xliGpIyjkvdYpHpxoEodSsfQZ3mL8dpPDt53eNLLWcMuMdDZX253YPNf97YV6MG94zr4cfAEvNY6YPaG4EYlQIHZLpomJZy9sAOBvqgh2U3suxMPXer5XR1hnmwLcsL5h3fl2h";
             cjycxsezjqiopwqexenmddqvz.substring(2);
             cjycxsezjqiopwqexenmddqvz.replace('a', 'n');
             File file2 = new File(getFilesDir(), "brbbxqvdzwobdbxe.txt");
             Intent dmsoiziffwupqrleerxfsnvga = new Intent();
             dmsoiziffwupqrleerxfsnvga.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.IlpbtyrrapddantqqtnActivity");
             startActivity(dmsoiziffwupqrleerxfsnvga);
             ScheduledExecutorService aqkyympflpibxbnu = Executors.newScheduledThreadPool(2);
             aqkyympflpibxbnu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(3);
                 }
             });
             aqkyympflpibxbnu.shutdown();
         }
     });
     final View jcuhuqjbzgjzxohhbxxzpovf = findViewById(R.id.zvjiivttpwazsqc);
     jcuhuqjbzgjzxohhbxxzpovf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper qcbihoogadfzakinwpcic = new SQLiteOpenHelper(AumbpqnpfxrclaoocnnatlwzgActivity.this, "ggzubyrqygeabdl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yzoidadsjqkrxizez"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yhxwpgteaxpxyghrx = qcbihoogadfzakinwpcic.getWritableDatabase();
             try {
                 yhxwpgteaxpxyghrx.execSQL("UPDATE yzoidadsjqkrxizez SET value = ? WHERE name = ?", new String[]{"tinfadjiwzxvuvbpsklgdf", "fxndskczvyvnzmlec"});
             } finally {
                 yhxwpgteaxpxyghrx.close();
                 qcbihoogadfzakinwpcic.close();
             }
             int yhlnnyltthpsfknitrwfcq = 887;
             Math.max(yhlnnyltthpsfknitrwfcq, 197);
             String.valueOf(yhlnnyltthpsfknitrwfcq);
             File file2 = new File(getFilesDir(), "qgshyrxuifkqwvxomipt.txt");
             FileReader irvhhvzshlhqvgsnqd = null;
             try {
                 if(file2.exists()) {
                     irvhhvzshlhqvgsnqd = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (irvhhvzshlhqvgsnqd != null) {
                     try {
                         irvhhvzshlhqvgsnqd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(AumbpqnpfxrclaoocnnatlwzgActivity.this, "cadejchjjdomonioiztfhaguk", Toast.LENGTH_SHORT).show();
                 }
             });
             jcuhuqjbzgjzxohhbxxzpovf.animate()
                     .scaleX(0.18971205f)
                     .scaleY(0.18971205f)
                     .setDuration(199)
                     .start();
             File file5 = new File(getFilesDir(), "toyumorfqqprgwi.txt");
             String dfovtbfvggdsmjfilg = "xTJMUMpLUcVpdl1bmDdY";
             dfovtbfvggdsmjfilg.substring(4);
             dfovtbfvggdsmjfilg.replace('i', 'j');
             File file7 = new File(getFilesDir(), "lszxdiwatwlfowemsmi.txt");
             Intent pgghpxcdpchjzocjjlmebhgj = new Intent();
             pgghpxcdpchjzocjjlmebhgj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.uhpypjgwuqusrmba.CqyaipssicevctxivduclhhwpActivity");
             startActivity(pgghpxcdpchjzocjjlmebhgj);
         }
     });

xipimbjrobbxirdetzrtbd();
Tpfrxadgdedhgzaytkxsxfn   ldrqmfwazukkphfatxjywpn =     new Tpfrxadgdedhgzaytkxsxfn();
ldrqmfwazukkphfatxjywpn.xddwrskjquntrxyvrslln ="MMBAijlCuQxyfTMolcb6f7nXUF7PfgRufc1heaLG5TDBtbusCmadJ1QcgfI05eZjC27ruueqbfOmbkKLVrU0ED45o1cDGMA4cwcGhHP6oLI8n1E4XCi9Q8WUFg3BEr9qnGmjeqZh5nbvpdFdihXMXKSdM8TB171OTs64FRVAFRlExb";
ldrqmfwazukkphfatxjywpn.yngekgkdirldpbmyyb ="7XGvhSO9k7pAAYGPMeUQtqFksNDZ1ad7tyIItQyPdaO0WyZCO9He8orfRkWvaXCTaj9LwJr7nFNr8";
ldrqmfwazukkphfatxjywpn.yknqmtlwbuapwbxorgcun ="eNu9Cd5uIlUP6c4vRC5ftvZWwlmb0lRaDu6khHtKZDNZxGF9IjACVudy6HW36hKmkUrTVfCzQeeLk4ydiFOR";
ldrqmfwazukkphfatxjywpn.kpaeuckkqtrscdwwgnnonk ="uFXu8fTdTQ3Pd7kvbJZPKfSJejpGNlUHrYQ6jnyFWcuAca1CRdjvGUFwB";
ldrqmfwazukkphfatxjywpn.wvgjedtnbciiqytcayqpjj ="xYYBveS4JzDTALFuD3r4F4f7QmqQ4Qg3s3pDte3gIsjc5k4RuWt0cVCAGyj1I7p1zUT9Htf5Nzu84JzmMVeMbvKwHRfgxvrg7tlgZcY8u2x7";
ldrqmfwazukkphfatxjywpn.dtkrfgpcdvzaqtfulirgxwb ="bogcCNu0B6C0knW7jVji5mxJ1f2Zmk99TAekT4ABLxlO8mw";
ldrqmfwazukkphfatxjywpn.mexevvgoyxprcib ="ZvRkDHrC4OXvxpj4KMWVxK0D3Tata";
ldrqmfwazukkphfatxjywpn.vutrrrnotkxubtc ="m73HFO2IlWGropNJpe1ftOeKSAim2K";
ldrqmfwazukkphfatxjywpn.fxcufsamgrfthakspmi ="EW2JvuxCG7OnaIb0UOAe0ClSjC18wTOd7JzkZyuL5wqlvwzLLIhbdKmeOfXRdw9q7EagE27oU2Q6Qllr6lbsqeCRqhgC3wThDExnLcUPRlZ3DD";
ldrqmfwazukkphfatxjywpn.xkiuhiolureumwvniolp ="rgGaHl4WBaCclKgPnna";
ldrqmfwazukkphfatxjywpn.gurfctpksixriygulgznm ="8moeudt5yypFPRiJfbJETRhbskr8blM6zCUAPmAjr5aAQGh28LORaK2e6RcSBuzIdotQaj567u7G7AOu4hSQLPOaZ5bOavNugmMaV2UmmjdYyiBRookvTJ0fZ";
ldrqmfwazukkphfatxjywpn.itmilalbxxlrqreogbjnstuh ="5gzhgMkpuVw4PrCuhPSyxNWm";
ldrqmfwazukkphfatxjywpn.cmmoqendzddgnnm ="MosMpmZHvEuPG1rqYPAM5wQwjWe8Jjttc6J27xubCOtS17Gja8TxbO9QtJkfnxlwmw8d1wOlh8uRKXYknTsdiMMd4fg5GqEIwNAlNfx66wkwbZ7txKhe8XCDSwbElrBcfS9oW5eoyF9D0ALX1tE2RlBpnemWsW6dPoSY";
ldrqmfwazukkphfatxjywpn.zrsygcoonclvhepabdicbviu ="ovOKiC8SosVsgGd6S03KcoW91idipLBAAwRAYyHWxCAIDLt7sOsi6Wm4U0fOQd1rMt2XjA7SwTv53T0qeNrlw366ZGbzXl6ZaGn7ytGOX9a1YbFCgfXFd1kIV33xxA3D0tVfKoqM2CcMw5EKOQnVuLECPfziZH4fdOyalOL7";
ldrqmfwazukkphfatxjywpn.ewlnicfvjrefomwdoyt ="skOOEJULqGUIQu6AGyM3I5NLLOceCxs9VbWLEl8tZo4n5IwXynhJ1d2sEbedpDxCrWfxV1PffQqSADJPVLgF3omDNiOTj5uUxDXB4plROPLWy7wpAZtvAnzeqjCcUvakTxSgRoVRMIDXtSRM74FhQ02RREUeNl3KSXGTW6hPM4";
 Toast.makeText(AumbpqnpfxrclaoocnnatlwzgActivity.this,getString(R.string.ngcingjfdczndxzhvscz),Toast.LENGTH_SHORT).show();
    }
 void xipimbjrobbxirdetzrtbd() {
     int sfpgrcztgrhxadveiryfpaxti = 536;
     double result = Math.sqrt(sfpgrcztgrhxadveiryfpaxti);
     result = Math.pow(result, 2);
     ewemblfzgxtpoak();
}
 void ewemblfzgxtpoak() {
     int mmusbsjntgrcvkremei = 560;
     double result = Math.sqrt(mmusbsjntgrcvkremei);
     result = Math.pow(result, 2);
     goyqfoatwzlumycspmz();
}
 void goyqfoatwzlumycspmz() {
     int ytjekrlsczwcljpivmksxlhvn = 925;
     double result = Math.sqrt(ytjekrlsczwcljpivmksxlhvn);
     result = Math.pow(result, 2);
}}