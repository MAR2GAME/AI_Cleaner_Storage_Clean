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
import android.widget.TableLayout;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class HzbovwrsexsuaagbalwActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_zbovwrsexsuaagbalw);
     final View wdrlsitiprscxehmed = findViewById(R.id.rovnhkeckzqetri);
     wdrlsitiprscxehmed.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent stjguoomwmaxddcgazmvpydi = new Intent();
             stjguoomwmaxddcgazmvpydi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.QqumdgnejoixwsbweActivity");
             startActivity(stjguoomwmaxddcgazmvpydi);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HzbovwrsexsuaagbalwActivity.this, "vzsocmxkpbhjxbhbsgrtbgw", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper dbmlcdwjakjkyvbgghsp = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "ofsauejusgplojpuzgzq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cfytjmrfbyzzxmxi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bcvjuuoruwchkkk = dbmlcdwjakjkyvbgghsp.getWritableDatabase();
             try {
                 bcvjuuoruwchkkk.execSQL("UPDATE cfytjmrfbyzzxmxi SET value = ? WHERE name = ?", new String[]{"krhzdkamjpmmmkcreryiib", "ddyegsilxzixphtnoztoelo"});
             } finally {
                 bcvjuuoruwchkkk.close();
                 dbmlcdwjakjkyvbgghsp.close();
             }
             Date azupsngjapmsoildnp = new Date();
             String yrkwvpomqfzbptanmytrzid = String.valueOf(azupsngjapmsoildnp.getTime());
             System.currentTimeMillis();
             File file4 = new File(getFilesDir(), "alcekvbgyfeabidzuioxpbmlp.txt");
             FileReader jaqzhfhttrryrav = null;
             try {
                 if(file4.exists()) {
                     jaqzhfhttrryrav = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jaqzhfhttrryrav != null) {
                     try {
                         jaqzhfhttrryrav.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService wvfnvbqewpxkkpg = Executors.newScheduledThreadPool(3);
             wvfnvbqewpxkkpg.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(25);
                 }
             });
             wvfnvbqewpxkkpg.shutdown();
             SQLiteOpenHelper wlegwntlaaodidrbvwvrml = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "yfjuigyppfxfifhmvew.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ecxrxcnnfloeovjwogvt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase uzgxtbowmcznrrboneaitn = wlegwntlaaodidrbvwvrml.getWritableDatabase();
             try {
                 uzgxtbowmcznrrboneaitn.execSQL("INSERT INTO ecxrxcnnfloeovjwogvt (name, value) VALUES (?, ?)", new String[]{"clljeaqkrdcskntwgacar", "baqobboafwgquxhrnlrtcii"});
             } finally {
                 uzgxtbowmcznrrboneaitn.close();
                 wlegwntlaaodidrbvwvrml.close();
             }
             Handler nbkcstgstzfmccu = new Handler(Looper.getMainLooper());
             nbkcstgstzfmccu.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     wdrlsitiprscxehmed.setVisibility(View.VISIBLE);
                 }
             }, 589);
         }
     });
     final View jzawhaewcbecjaruilbletci = findViewById(R.id.qmsnxwxlpllldnokzcmxqdbbm);
     jzawhaewcbecjaruilbletci.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             jzawhaewcbecjaruilbletci.animate()
                     .translationX(39f)
                     .translationY(94f)
                     .setDuration(131)
                     .start();
             Intent vjxaimdmfggeyzro = new Intent();
             vjxaimdmfggeyzro.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.TohtwgmtabvwcgxfytaegkmActivity");
             startActivity(vjxaimdmfggeyzro);
         }
     });
     final View qbysannvyhwkpgix = findViewById(R.id.jhbbfngwlmskgwilsi);
     qbysannvyhwkpgix.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper eotahvonjclvikhth = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "cmjdjehicygojcymxfoko.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bmzpmpargaqylzibana"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qnglknaqecvkekintw = eotahvonjclvikhth.getWritableDatabase();
             try {
                 qnglknaqecvkekintw.execSQL("SELECT * FROM bmzpmpargaqylzibana WHERE name = ?", new String[]{"lrvbmaxnjmhszxddhguhjmbj", "wvdsebqghcuhanaiwlkhc"});
             } finally {
                 qnglknaqecvkekintw.close();
                 eotahvonjclvikhth.close();
             }
             Intent lokbhycneudgbxsaavhncs = new Intent();
             lokbhycneudgbxsaavhncs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.RmpznubdraswskwbyjrwxixActivity");
             startActivity(lokbhycneudgbxsaavhncs);
             File file2 = new File(getFilesDir(), "iiyzcnxobdpzonwrfbclzd.txt");
             FileReader yfmvrtphrtbyxgmr = null;
             try {
                 if(file2.exists()) {
                     yfmvrtphrtbyxgmr = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (yfmvrtphrtbyxgmr != null) {
                     try {
                         yfmvrtphrtbyxgmr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper ppzlazrvmqxufryjhznpyybw = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "iirivftvxyblqkhjyq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ofvvzwrhckwutehkwv"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mauxmympbckmrsfm = ppzlazrvmqxufryjhznpyybw.getWritableDatabase();
             try {
                 mauxmympbckmrsfm.execSQL("SELECT * FROM ofvvzwrhckwutehkwv WHERE name = ?", new String[]{"afxweoujqfcovbjanxurpzqjr", "wqawmfqnurdfonuerlgj"});
             } finally {
                 mauxmympbckmrsfm.close();
                 ppzlazrvmqxufryjhznpyybw.close();
             }
             Intent cwdwostknndxdagydbhiotlf = new Intent();
             cwdwostknndxdagydbhiotlf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.RmpznubdraswskwbyjrwxixActivity");
             startActivity(cwdwostknndxdagydbhiotlf);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HzbovwrsexsuaagbalwActivity.this, "xdapnpjcxxbonppysaaeub", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper yayqqdwejlahzziduojs = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "fwyezlcppaboudvjyeyl.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fblrnszzxcqiebwvlacimb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gcsdwdwnbibedsgcwfdlcxken = yayqqdwejlahzziduojs.getWritableDatabase();
             try {
                 gcsdwdwnbibedsgcwfdlcxken.execSQL("SELECT * FROM fblrnszzxcqiebwvlacimb WHERE name = ?", new String[]{"paimuemcbfebpeuf", "zmazhuepcyhoucjsdvpjxz"});
             } finally {
                 gcsdwdwnbibedsgcwfdlcxken.close();
                 yayqqdwejlahzziduojs.close();
             }
             File file7 = new File(getFilesDir(), "nmvvsxpmwcfcnkuahvpm.txt");
             FileWriter mlvqmvqoihxkehq = null;
             try {
                 mlvqmvqoihxkehq = new FileWriter(file7);
                 mlvqmvqoihxkehq.write("zYdQFqANQcmAYPqLYWHahqtNeFvDwGJUS69HjksAk0Nx4ZGDGHPnR8Ad4mKkjUT7g7qu6r83lXi5vcnntjcYqN2V0phGPZymHnzOI2qlQXnM4k9rhPTP3VQlfL0AzCFzXzPeVxZXW5UP9M5kvR4sf0bf924");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (mlvqmvqoihxkehq != null) {
                     try {
                         mlvqmvqoihxkehq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date zzjpfnlvnerbmqnrakxdchog = new Date();
             String yicejxhchyjrjnszu = String.valueOf(zzjpfnlvnerbmqnrakxdchog.getTime());
             System.currentTimeMillis();
             Intent mjrvopzltbbobwehz = new Intent();
             mjrvopzltbbobwehz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.RmpznubdraswskwbyjrwxixActivity");
             startActivity(mjrvopzltbbobwehz);
         }
     });
     final View hfvwhwbduqhrdkyaw = findViewById(R.id.sflxejeonmaistkfvpwxix);
     hfvwhwbduqhrdkyaw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper jzmdrbomcvqxajwl = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "jnsclduzmxvibyqwzhzpn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lyechauzsijibgqsaad"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase echywzfhknurizrltfhyzolb = jzmdrbomcvqxajwl.getWritableDatabase();
             try {
                 echywzfhknurizrltfhyzolb.execSQL("INSERT INTO lyechauzsijibgqsaad (name, value) VALUES (?, ?)", new String[]{"hjcowzialjgmwmcxsy", "fyiafxjikpjddqz"});
             } finally {
                 echywzfhknurizrltfhyzolb.close();
                 jzmdrbomcvqxajwl.close();
             }
             File file1 = new File(getFilesDir(), "gifshjwuczkkjnrhu.txt");
             FileReader teroelxcwfsqhpein = null;
             try {
                 if(file1.exists()) {
                     teroelxcwfsqhpein = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (teroelxcwfsqhpein != null) {
                     try {
                         teroelxcwfsqhpein.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent hfbgusukuinukokuroyztgk = new Intent();
             hfbgusukuinukokuroyztgk.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.GdpqlapqvlfhtgueActivity");
             startActivity(hfbgusukuinukokuroyztgk);
             SQLiteOpenHelper jlzqwxdmxeamikhlz = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "ghbeytgnbihjdscunc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "enxsrtiarjuyskhpajchpt"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hxqreyddnjnvrcqkdlyfhrbf = jlzqwxdmxeamikhlz.getWritableDatabase();
             try {
                 hxqreyddnjnvrcqkdlyfhrbf.execSQL("SELECT * FROM enxsrtiarjuyskhpajchpt WHERE name = ?", new String[]{"mcuuqahwmgbfufmjafrit", "ktdmzgynjifdrlmakgbzpcch"});
             } finally {
                 hxqreyddnjnvrcqkdlyfhrbf.close();
                 jlzqwxdmxeamikhlz.close();
             }
             File file4 = new File(getFilesDir(), "lfthwpbnjkmwjcykq.txt");
             FileWriter vjikrfylflbjnsazhahaqnx = null;
             try {
                 vjikrfylflbjnsazhahaqnx = new FileWriter(file4);
                 vjikrfylflbjnsazhahaqnx.write("Iz3M5ucxLQa0btNc2doIiQlCULykO4WfD8ZDHnL0UKA");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (vjikrfylflbjnsazhahaqnx != null) {
                     try {
                         vjikrfylflbjnsazhahaqnx.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper feomwkbbzgmuslfdi = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "rdmvromjmvfyoygmfoqk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fhfsatmyxhrlhfa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase flcdnhiskofkvqtpdm = feomwkbbzgmuslfdi.getWritableDatabase();
             try {
                 flcdnhiskofkvqtpdm.execSQL("DELETE FROM fhfsatmyxhrlhfa WHERE name = ?", new String[]{"wggmhtglnzuwasvhwtut", "nwwxhlvrzpdwenmxuktu"});
             } finally {
                 flcdnhiskofkvqtpdm.close();
                 feomwkbbzgmuslfdi.close();
             }
         }
     });
     final View prgrxxvzzdmbjsrfozniwz = findViewById(R.id.omxtugldvhrskrw);
     prgrxxvzzdmbjsrfozniwz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent sbtwkaajxftcrhbyxsto = new Intent();
             sbtwkaajxftcrhbyxsto.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.UdcleriwwmcwogvlcpqzohezwActivity");
             startActivity(sbtwkaajxftcrhbyxsto);
             ScheduledExecutorService mtzdvqondajuyoledhmzd = Executors.newScheduledThreadPool(3);
             mtzdvqondajuyoledhmzd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(28);
                 }
             });
             mtzdvqondajuyoledhmzd.shutdown();
             SQLiteOpenHelper gzotpbcecsndqywfa = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "ybbycdprjipogetnbzsxyf.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qfgbuctfqranmnnwgefb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jtsayuryswslzuchtnbryrq = gzotpbcecsndqywfa.getWritableDatabase();
             try {
                 jtsayuryswslzuchtnbryrq.execSQL("UPDATE qfgbuctfqranmnnwgefb SET value = ? WHERE name = ?", new String[]{"qxaptyfbkxvonjhmqgrncswz", "wtynjmkcbcanhtrlmo"});
             } finally {
                 jtsayuryswslzuchtnbryrq.close();
                 gzotpbcecsndqywfa.close();
             }
             ScheduledExecutorService ozliffmeajxtgotkcz = Executors.newScheduledThreadPool(1);
             ozliffmeajxtgotkcz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(19);
                 }
             });
             ozliffmeajxtgotkcz.shutdown();
             SQLiteOpenHelper pzwwcbfudruvbqoeg = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "axvkwkeltvitnums.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wngmidsxcgsvsjus"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase laqqxfhmknhtifbgviseemuc = pzwwcbfudruvbqoeg.getWritableDatabase();
             try {
                 laqqxfhmknhtifbgviseemuc.execSQL("DELETE FROM wngmidsxcgsvsjus WHERE name = ?", new String[]{"xviqnuvxlfmnzyv", "rdmbymhxdmelwantzupzdda"});
             } finally {
                 laqqxfhmknhtifbgviseemuc.close();
                 pzwwcbfudruvbqoeg.close();
             }
         }
     });
     final View csyymdcpbpvardzmecxzgu = findViewById(R.id.dfzjippzejmiqdmkeyvs);
     csyymdcpbpvardzmecxzgu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int mczrxwkszjnxtwhdxa = 833;
             Math.max(mczrxwkszjnxtwhdxa, 899);
             String.valueOf(mczrxwkszjnxtwhdxa);
             Intent kqighqhoobsugalbupcvhlfda = new Intent();
             kqighqhoobsugalbupcvhlfda.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.OcpcjzehikdfvfqvdrlslmvqActivity");
             startActivity(kqighqhoobsugalbupcvhlfda);
             ScheduledExecutorService jtsxdoqfwwekzbturism = Executors.newScheduledThreadPool(1);
             jtsxdoqfwwekzbturism.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(98);
                 }
             });
             jtsxdoqfwwekzbturism.shutdown();
             csyymdcpbpvardzmecxzgu.animate()
                     .rotation(221f)
                     .setDuration(423)
                     .start();
             Date rezhvxqxdzmjwisa = new Date();
             String ahdbjkbblqsxlxzlxc = String.valueOf(rezhvxqxdzmjwisa.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper yofoklebpsugykhft = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "dhjjrmjzdhzyczn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nnciedkidaxiqylghwtgeqyou"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase siygtwthgkrhqutmkh = yofoklebpsugykhft.getWritableDatabase();
             try {
                 siygtwthgkrhqutmkh.execSQL("SELECT * FROM nnciedkidaxiqylghwtgeqyou WHERE name = ?", new String[]{"qfyrtaigaqslcsyl", "ybhelkykrgfjzludmljjas"});
             } finally {
                 siygtwthgkrhqutmkh.close();
                 yofoklebpsugykhft.close();
             }
             Handler nuqsannvcumqfoii = new Handler(Looper.getMainLooper());
             nuqsannvcumqfoii.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     csyymdcpbpvardzmecxzgu.setVisibility(View.VISIBLE);
                 }
             }, 698);
         }
     });
     final View xbkuomwplcwdgrlfbumfoowm = findViewById(R.id.yxzxbowbqblrnoqjwkdnmdfto);
     xbkuomwplcwdgrlfbumfoowm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "pslulklzhyptyuvpaxwdsp.txt");
             SQLiteOpenHelper messcxokifbcnrpdyykbgiqxs = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "okdahelbwotdjqsztrnyxnm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "kmymespllhjukrknhy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase chhwhmzglouboatobjrbc = messcxokifbcnrpdyykbgiqxs.getWritableDatabase();
             try {
                 chhwhmzglouboatobjrbc.execSQL("DELETE FROM kmymespllhjukrknhy WHERE name = ?", new String[]{"azipcqoqxrbelzlsypuvmppvc", "rfcqnkbbkswohfdbzgpy"});
             } finally {
                 chhwhmzglouboatobjrbc.close();
                 messcxokifbcnrpdyykbgiqxs.close();
             }
             int ctaysmjjokivtmuakrwstxl = 4;
             Math.max(ctaysmjjokivtmuakrwstxl, 496);
             String.valueOf(ctaysmjjokivtmuakrwstxl);
             File file3 = new File(getFilesDir(), "sicqaefvsczuvtenjbf.txt");
             if(file3.exists()) file3.delete();
             SQLiteOpenHelper tuaqlpstbbreeplxftsvveqeh = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "yszwqzwhedmwatenc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "pirqhbquuklnkpmnuqanyewy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bpshgcongxephprgr = tuaqlpstbbreeplxftsvveqeh.getWritableDatabase();
             try {
                 bpshgcongxephprgr.execSQL("INSERT INTO pirqhbquuklnkpmnuqanyewy (name, value) VALUES (?, ?)", new String[]{"ndjfyibrwujhxzjacmmofx", "epdqekbznmwypcbdobdgbyae"});
             } finally {
                 bpshgcongxephprgr.close();
                 tuaqlpstbbreeplxftsvveqeh.close();
             }
             File file5 = new File(getFilesDir(), "xcoxhkdvtmhnwbaj.txt");
             Handler lzqoyfrwprvoexqooiiwo = new Handler(Looper.getMainLooper());
             lzqoyfrwprvoexqooiiwo.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     xbkuomwplcwdgrlfbumfoowm.setVisibility(View.VISIBLE);
                 }
             }, 172);
             SQLiteOpenHelper lepcajoqowokviybtkar = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "vjsshetylzgxudvsrftcoh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hwyfpcjtbplybcmkpvmnxhn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase lmdbxmoxmkouigyn = lepcajoqowokviybtkar.getWritableDatabase();
             try {
                 lmdbxmoxmkouigyn.execSQL("UPDATE hwyfpcjtbplybcmkpvmnxhn SET value = ? WHERE name = ?", new String[]{"revxhptncweircidqk", "rukpiuecnrhfbruiunuoidf"});
             } finally {
                 lmdbxmoxmkouigyn.close();
                 lepcajoqowokviybtkar.close();
             }
             File file8 = new File(getFilesDir(), "zbtbpipsqipbgtcowahd.txt");
             SQLiteOpenHelper vddizxuccycwluqhs = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "szsdxprujokmosphsh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "tlosjhjattgomzfma"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dkyzzucedfwiqtrfr = vddizxuccycwluqhs.getWritableDatabase();
             try {
                 dkyzzucedfwiqtrfr.execSQL("UPDATE tlosjhjattgomzfma SET value = ? WHERE name = ?", new String[]{"vtixioayhzafviwcbykg", "cspsegstflmzujpzq"});
             } finally {
                 dkyzzucedfwiqtrfr.close();
                 vddizxuccycwluqhs.close();
             }
         }
     });
     final View prrylwcwdswxqdsjzn = findViewById(R.id.islocwpkeolftdtfg);
     prrylwcwdswxqdsjzn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper snaxfzthkbqxpbddsqovmqybp = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "uvkzmsquqsajboigifnjk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "miyuoxjfcrqjvusziui"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dnblreqsydfrnrovcqn = snaxfzthkbqxpbddsqovmqybp.getWritableDatabase();
             try {
                 dnblreqsydfrnrovcqn.execSQL("SELECT * FROM miyuoxjfcrqjvusziui WHERE name = ?", new String[]{"ikybqcugxikwlpoozrnpqvygp", "oohdblsygqnqjczvlvpyl"});
             } finally {
                 dnblreqsydfrnrovcqn.close();
                 snaxfzthkbqxpbddsqovmqybp.close();
             }
             ScheduledExecutorService gqonafczezstcnu = Executors.newScheduledThreadPool(1);
             gqonafczezstcnu.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(5);
                 }
             });
             gqonafczezstcnu.shutdown();
             Intent lhdogewfpisqovhbgubzcqx = new Intent();
             lhdogewfpisqovhbgubzcqx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.EwtwfqnxlkznkjkzgmnmActivity");
             startActivity(lhdogewfpisqovhbgubzcqx);
             prrylwcwdswxqdsjzn.animate()
                     .rotation(286f)
                     .setDuration(102)
                     .start();
             ScheduledExecutorService apjvfvfmsnqbxqzatxlkgqz = Executors.newScheduledThreadPool(4);
             apjvfvfmsnqbxqzatxlkgqz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(86);
                 }
             });
             apjvfvfmsnqbxqzatxlkgqz.shutdown();
             Intent hznghsdqtmksakqscwnpvhxk = new Intent();
             hznghsdqtmksakqscwnpvhxk.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.EwtwfqnxlkznkjkzgmnmActivity");
             startActivity(hznghsdqtmksakqscwnpvhxk);
             String dphiopqgmooozznntgzs = "F1jnZy3vM4aPAzPy7wSV1JMBcHvYTjP4fLzITWUFmtqX7LbfD1qKETGOBq8b03rMSoXBi19E0WO2HEO9LDFyOYXySrLP";
             dphiopqgmooozznntgzs.substring(2);
             dphiopqgmooozznntgzs.replace('p', 'k');
         }
     });
     final View kifwkafctppcnfpbv = findViewById(R.id.ckfrqtqfgeqrxpizhjtwi);
     kifwkafctppcnfpbv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int bxnhvglyphdsani = 508;
             Math.max(bxnhvglyphdsani, 568);
             String.valueOf(bxnhvglyphdsani);
             Intent hkmmocovxwjrxwxmsfa = new Intent();
             hkmmocovxwjrxwxmsfa.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.PfmraysxlizwjzccdjyActivity");
             startActivity(hkmmocovxwjrxwxmsfa);
             SQLiteOpenHelper kukrkdirlrfngxfobii = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "tjzejmyahvxvhju.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rivcxcadkjhlcioyiaytumiio"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase iknbldlskibumdhlg = kukrkdirlrfngxfobii.getWritableDatabase();
             try {
                 iknbldlskibumdhlg.execSQL("DELETE FROM rivcxcadkjhlcioyiaytumiio WHERE name = ?", new String[]{"ijggbrgiqyffbrxtsyppn", "cngctfuiqygejjv"});
             } finally {
                 iknbldlskibumdhlg.close();
                 kukrkdirlrfngxfobii.close();
             }
             Handler euxajydwdzxexsno = new Handler(Looper.getMainLooper());
             euxajydwdzxexsno.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     kifwkafctppcnfpbv.setVisibility(View.VISIBLE);
                 }
             }, 953);
             SQLiteOpenHelper lwmalhfguhfvladfnrauqrrt = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "hrgzhiywzuzaswozwls.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "psehpoaotwzvdsecxz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase arwqkpgjnxtffdqymit = lwmalhfguhfvladfnrauqrrt.getWritableDatabase();
             try {
                 arwqkpgjnxtffdqymit.execSQL("DELETE FROM psehpoaotwzvdsecxz WHERE name = ?", new String[]{"trtthsrrtakzirw", "tqeojpflxrzjlulxwwzqvwn"});
             } finally {
                 arwqkpgjnxtffdqymit.close();
                 lwmalhfguhfvladfnrauqrrt.close();
             }
             Intent fzumitkjcxnjhpvzveofslny = new Intent();
             fzumitkjcxnjhpvzveofslny.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.PfmraysxlizwjzccdjyActivity");
             startActivity(fzumitkjcxnjhpvzveofslny);
             SQLiteOpenHelper obdurboymuhfagevdbos = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "psiggkwfymuapgicscuaealuk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fppcodxqxrdvazuhi"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase agkknvgfjaxmpeytnoqmsqi = obdurboymuhfagevdbos.getWritableDatabase();
             try {
                 agkknvgfjaxmpeytnoqmsqi.execSQL("DELETE FROM fppcodxqxrdvazuhi WHERE name = ?", new String[]{"rksuuagfhepekfkqyhrofps", "ulsfhlmoipghyvudfym"});
             } finally {
                 agkknvgfjaxmpeytnoqmsqi.close();
                 obdurboymuhfagevdbos.close();
             }
             Date juxjdqxnfbkzzqodjagknwety = new Date();
             String onzjxljfsnlqguy = String.valueOf(juxjdqxnfbkzzqodjagknwety.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService ztgpcjkfywxqlyspjn = Executors.newScheduledThreadPool(4);
             ztgpcjkfywxqlyspjn.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(83);
                 }
             });
             ztgpcjkfywxqlyspjn.shutdown();
         }
     });
     final View fomttqgmxqepffv = findViewById(R.id.rskchzjoxmfqhrk);
     fomttqgmxqepffv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String shbzddnehwlylevzj = "Mk15I0bcMGbP1ind3skikYsYUPcz2S9vOKtzEVZ";
             shbzddnehwlylevzj.substring(0);
             shbzddnehwlylevzj.replace('x', 'v');
             SQLiteOpenHelper gesnkkqqapcwczgqtmfaqdrav = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "defxnamwcdbztubgq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cqmeubhowqxhgqcnrdp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase opjdfvavzxztulzn = gesnkkqqapcwczgqtmfaqdrav.getWritableDatabase();
             try {
                 opjdfvavzxztulzn.execSQL("DELETE FROM cqmeubhowqxhgqcnrdp WHERE name = ?", new String[]{"vzbjctdwlccryvo", "wuyjyaseznmhylykaxdz"});
             } finally {
                 opjdfvavzxztulzn.close();
                 gesnkkqqapcwczgqtmfaqdrav.close();
             }
             File file2 = new File(getFilesDir(), "jqdkbvsxxpkkhgioaywfemgct.txt");
             FileReader ggvcqexicinehth = null;
             try {
                 if(file2.exists()) {
                     ggvcqexicinehth = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ggvcqexicinehth != null) {
                     try {
                         ggvcqexicinehth.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int ibkrwtoriwsgwlnetrp = 395;
             Math.max(ibkrwtoriwsgwlnetrp, 824);
             String.valueOf(ibkrwtoriwsgwlnetrp);
             ScheduledExecutorService eqhxqousuuumgcsdkhmqnsq = Executors.newScheduledThreadPool(2);
             eqhxqousuuumgcsdkhmqnsq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(83);
                 }
             });
             eqhxqousuuumgcsdkhmqnsq.shutdown();
             int zrfaiooxahwywwhvwmnaqq = 766;
             Math.max(zrfaiooxahwywwhvwmnaqq, 169);
             String.valueOf(zrfaiooxahwywwhvwmnaqq);
             ScheduledExecutorService dfdalfwalicoknbyghf = Executors.newScheduledThreadPool(4);
             dfdalfwalicoknbyghf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(14);
                 }
             });
             dfdalfwalicoknbyghf.shutdown();
             Intent dwohrssblbngxwepskfoewaf = new Intent();
             dwohrssblbngxwepskfoewaf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.MkynwyvbudjctovbgpActivity");
             startActivity(dwohrssblbngxwepskfoewaf);
             SQLiteOpenHelper hvhbqunfnfgmaubauqnncy = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "kaxgiymovsrvayi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vvgrtmqobcdqlzsxcji"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zdcnjdwnaxhrdmjqrlt = hvhbqunfnfgmaubauqnncy.getWritableDatabase();
             try {
                 zdcnjdwnaxhrdmjqrlt.execSQL("DELETE FROM vvgrtmqobcdqlzsxcji WHERE name = ?", new String[]{"ofwvxppafbkgktsnyvjqu", "ayfwkbzmvnrjbzmsfsqway"});
             } finally {
                 zdcnjdwnaxhrdmjqrlt.close();
                 hvhbqunfnfgmaubauqnncy.close();
             }
             Intent fhbkucdudqjgkkbfyusjdfw = new Intent();
             fhbkucdudqjgkkbfyusjdfw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.MkynwyvbudjctovbgpActivity");
             startActivity(fhbkucdudqjgkkbfyusjdfw);
         }
     });
     final View lwhootlihmjamhfun = findViewById(R.id.agkbmhcdcfiwrngnfmrtbl);
     lwhootlihmjamhfun.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper qauoxxrfhsczufptrknvfur = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "xvittsubzytbgqsmhfi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iekiqmlcddrsglurfupdmdbhy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase nhugeaepgnyndcycgnmxidi = qauoxxrfhsczufptrknvfur.getWritableDatabase();
             try {
                 nhugeaepgnyndcycgnmxidi.execSQL("INSERT INTO iekiqmlcddrsglurfupdmdbhy (name, value) VALUES (?, ?)", new String[]{"iunicldtakazvbrhjtsssq", "vaaggzbbkfmrfnzubrtdkmlr"});
             } finally {
                 nhugeaepgnyndcycgnmxidi.close();
                 qauoxxrfhsczufptrknvfur.close();
             }
             lwhootlihmjamhfun.animate()
                     .scaleX(0.84141004f)
                     .scaleY(0.84141004f)
                     .setDuration(600)
                     .start();
         }
     });
     final View viyznyllboixandsacjp = findViewById(R.id.dlukedbfgajehsn);
     viyznyllboixandsacjp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String ovwyesefoczrznunyzejqt = "e6sswqenGTQrirPXAbYHoDDb55CgbRXrwgsonnwbQMX2m8PwWwIH0hGaLxTrvKag4skBYUnHl0axKSFhbDs6YYn29ecgW";
             ovwyesefoczrznunyzejqt.substring(1);
             ovwyesefoczrznunyzejqt.replace('u', 'f');
             Intent ciahcagjpdjdaajataic = new Intent();
             ciahcagjpdjdaajataic.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.RettxnvnszlihacctqrActivity");
             startActivity(ciahcagjpdjdaajataic);
             String ujefsrkqminkxsjmxn = "FCHgQ8iFxlj7UmPMiHTCeRLP4qlNKoCFqkZLTXDbrPo3nImX1E3C";
             ujefsrkqminkxsjmxn.substring(4);
             ujefsrkqminkxsjmxn.replace('x', 's');
             File file3 = new File(getFilesDir(), "qxjxvhxoxxqlrja.txt");
             SQLiteOpenHelper hgpiigpdyrmrifwwpkdjaa = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "ogaxolrebettukrgtsbstn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yetxftqemvdhksedbgo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kkgnldkstnaluuyug = hgpiigpdyrmrifwwpkdjaa.getWritableDatabase();
             try {
                 kkgnldkstnaluuyug.execSQL("SELECT * FROM yetxftqemvdhksedbgo WHERE name = ?", new String[]{"pvqcqwzhybrxuptyxdwtf", "abgzeckoxcjjmrqujjap"});
             } finally {
                 kkgnldkstnaluuyug.close();
                 hgpiigpdyrmrifwwpkdjaa.close();
             }
             String fiujrpolnrgadpmmguetjo = "wvLNoyRuklb4b7EPRRZz5hanQYSjBcvW7BT";
             fiujrpolnrgadpmmguetjo.substring(0);
             fiujrpolnrgadpmmguetjo.replace('e', 'r');
             File file6 = new File(getFilesDir(), "xbyhupweojdmnsft.txt");
             Intent xaofemfwxuqyrhyhjvahxi = new Intent();
             xaofemfwxuqyrhyhjvahxi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.RettxnvnszlihacctqrActivity");
             startActivity(xaofemfwxuqyrhyhjvahxi);
         }
     });
     final View qvcfunbdnnqslokofl = findViewById(R.id.zbrolhxnqbxvnlcdzpinnju);
     qvcfunbdnnqslokofl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler ycuuzujzxyvttcdlkdehze = new Handler(Looper.getMainLooper());
             ycuuzujzxyvttcdlkdehze.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     qvcfunbdnnqslokofl.setVisibility(View.VISIBLE);
                 }
             }, 981);
             Date nktedazmzdfbmyadanok = new Date();
             String ivalhpvqemgdtwxlmdhxu = String.valueOf(nktedazmzdfbmyadanok.getTime());
             System.currentTimeMillis();
             File file2 = new File(getFilesDir(), "qebohluvvzbvqbqkbzsgeki.txt");
         }
     });
     final View koqzxgplsrkklwum = findViewById(R.id.ruycjbsyabhdqmfiwyvpnvzhw);
     koqzxgplsrkklwum.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent orjbnejyqtkziym = new Intent();
             orjbnejyqtkziym.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.HeedovurcfcbwtpjActivity");
             startActivity(orjbnejyqtkziym);
             ScheduledExecutorService ndcyncfarhvvdgwa = Executors.newScheduledThreadPool(3);
             ndcyncfarhvvdgwa.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(41);
                 }
             });
             ndcyncfarhvvdgwa.shutdown();
         }
     });
     final View dmhtowbcjimrpgvexg = findViewById(R.id.cvchhnacanvqdxrvvkzfxrrbc);
     dmhtowbcjimrpgvexg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "lxscmoynczckaaryvfsq.txt");
             dmhtowbcjimrpgvexg.animate()
                     .scaleX(0.11783135f)
                     .scaleY(0.11783135f)
                     .setDuration(191)
                     .start();
             SQLiteOpenHelper rhpczicqjeuoatcg = new SQLiteOpenHelper(HzbovwrsexsuaagbalwActivity.this, "wmwqzoweltwffnfxv.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "evoqyvgysexhxqyqknu"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase slzmurjwgsdphkbkia = rhpczicqjeuoatcg.getWritableDatabase();
             try {
                 slzmurjwgsdphkbkia.execSQL("UPDATE evoqyvgysexhxqyqknu SET value = ? WHERE name = ?", new String[]{"xjbqouezxfpvrndnjgrea", "vuimnsydgijdszzo"});
             } finally {
                 slzmurjwgsdphkbkia.close();
                 rhpczicqjeuoatcg.close();
             }
             Intent sunrnasdqcxpmbhymfmlcdooq = new Intent();
             sunrnasdqcxpmbhymfmlcdooq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.xkqtlgodpruxgfh.MxvnsmnlcvnownwxbhtciwjpsuActivity");
             startActivity(sunrnasdqcxpmbhymfmlcdooq);
             dmhtowbcjimrpgvexg.animate()
                     .translationX(143f)
                     .translationY(43f)
                     .setDuration(276)
                     .start();
         }
     });

pkxahhgvdtfccicjz();
Smtkfqbtndbymeiz   mkgagevtyfcceftscjvobph =     new Smtkfqbtndbymeiz();
mkgagevtyfcceftscjvobph.kzjohrkjdmsrvxscwsh ="ISw4U805qPPFI5GDzdL6A471fl57zC2bidOSvUWxYFAc3yzF0s7bXsVc0OunZN7QRf9RJgqS81kIvk8HOzCUgUCbhVAXsymNBUrJxq1WyHIdKEyydm7leRAorP1OWIJirQHbnBDZAugA3dSYctAIaMCrZ";
mkgagevtyfcceftscjvobph.ymmyljzxkwfsmzuqqiog ="pEiCRZvmST5D9OhqGBVKGfj86ko4YfB5jlt";
mkgagevtyfcceftscjvobph.vyqmqmmjugucggqouytzggya ="EkZFekiMKNnYqxEih4A1qNGbyA7";
mkgagevtyfcceftscjvobph.bhazfulgvajttpvgwncdzdkb ="VVAc8RjXa7Fvb8sO3Zde";
mkgagevtyfcceftscjvobph.kxrptdgzcmsbwhgd ="cqttKhQPs3ESkjKzBRrqSztoI1bJD1W9DRO2YC7XaT8bg30vUWqDCVjoekUS9e33jJ9Mzc9FluoQszbRwjIBZdSfkPVQUZQGGeedBWModJbyUfGzaMkuQCbqe2MnbJsNtqGafk1l8v6uR2wFmKU58FDq0RbEMUVerWnSWtfdjS9VCFE9AyIOhEatjzUFgXP";
mkgagevtyfcceftscjvobph.gutcuzxceulbjcafu ="sZa2ScpoPyGC7RjtKzSKZCHEo9xvOLpHk9Sdu5KRQJCOdSgmoBkDpviURnGNobUf6Kj89lz6OY2vNxRKoGaGK8K1CbV2LlZb525vXXP7qKoSPehP6gGq82ZPsFgfxBnEvFQHWlneeEGaTSvZNOwlmLGDcFs8yDDtukGT1m87S";
mkgagevtyfcceftscjvobph.nmpnjwzoqkpllyq ="yKUmkmoGMEibNa4KAcTek0Um1ZB19D9s7z4smQGKkKtgqaKCIV9GckKNhlMkbMW6LVeqUPyNMOqqiWOxFHDrtX9jxUhWCuxpVntkEyb1K77dTyBs3qPiECNkqe6SuzSrzc3ox5Rm0vXV9HGzdszs5Geuw4";
mkgagevtyfcceftscjvobph.blfnurnhwzbxniicawebqlbt ="86tFECNnMwMLDEB4ltlFBtauuhOpVKLsjOhJjiXHc2uxEhXkiGfC6oep7C0k641m45pN8lFICQnTdbhgwkUK8k9ILhTFgxRYPWpdn11v0imI1ptPCrEVoRwY0kHzzxGZXNX508Q";
mkgagevtyfcceftscjvobph.mrnjqaokfjshwajegbiwxwu ="qYSqogxosi53UFVgRKcF5jnwtppEoKNvP1O";
mkgagevtyfcceftscjvobph.kxqlwnzjtiwfbwjsoafvwodvi ="Y0A9r9yLm8dSOekRUD1x7kMHO7VBgzBNkAwUs07GIyhMNtTBtTfp2DomPZf9XUZFir0wkOz0y3KlQkUY8naALPM9tkXXzZG0wRXgt5INPTFQSsd0HDcTFjd4NqEoucbgyaf9xgYmaInjSYgYbrs3ZUE2SpPrhOLe2UbbqBhFHbj4jnz1IJFufVoGS";
mkgagevtyfcceftscjvobph.vegyhdmcgsbcxubbynyvx ="9Q1XxOOb5AHDUzqqPIdEiHKP3f3";
mkgagevtyfcceftscjvobph.euofulqudbqxzscmfnxkvk ="LWtRl19zteGGuBI4Qf1QkEVyiuV5LWqIMmC5XzJlSwxYCIovldeZ1b4YBMMzHfJrZnfi4HwFCwKRvmZnBPQya7UO3AUcEUDE2KUWttxrN40miKcPMh1p8Q2FyCOWsD7ukrR67I8O7pI8bwgYeYhXr9fZmiCqdApZLw4APPKRUnxJurzzUsVv1l1fdQXI8kG6gl8";
mkgagevtyfcceftscjvobph.dgdppryzfkpqhaopb ="kgelKwuGGNaBcL5FB03rgMYpTO7xs3DCDXIbwJkP2RGbCh57KiiATUHNNAohsdHPAn19wgyEEcq0iaGhoPgKs7cHGcHS9BPEXtS4uZoi80CEaWk";
mkgagevtyfcceftscjvobph.ijysraqacnurimodsnklophyr ="eRIRJNi0gCbN5n0xwbjy56I7Y3NWFUr19qD2uqWWyQOhVp2EGh36b7PIzYKwLQW1EvtQAplfmYOfXGkajchEpgZlg23rOedbyfV4jSsT7Pj0RjFKUpWtT8b09fvBgTWNqeMfbFsMTwJx5cq0W1MLOHr16QbxXd0ktCjU4lKUllwZo";
mkgagevtyfcceftscjvobph.sbejebkqqhqckexgf ="9jKwQppVhSihbbaKlRZLXsAhoLeCkEuV68rcBh0gNZfF4KDeZcj9cL33yLK1vMsvnVkcH8UP5z0j7oQlNsueUtGzfg8styhf7";
mkgagevtyfcceftscjvobph.lglpgmmnshlnbbqzn ="vzbBMXUaabcoFBJniv0Z7E1xFbUqkt4cXYqZSSZTTke32Tvi19t9iv39yFaAW0qJzlsjwTyEOSIObzNLmmwwWmZI0Un3D";
 Toast.makeText(HzbovwrsexsuaagbalwActivity.this,getString(R.string.xmmshuyvldwxcwtqkl),Toast.LENGTH_SHORT).show();
    }
 void pkxahhgvdtfccicjz() {
     TableLayout ptmeganfdvdbhgvk = new TableLayout(HzbovwrsexsuaagbalwActivity.this);
     ptmeganfdvdbhgvk.setVisibility(View.VISIBLE);
     ptmeganfdvdbhgvk.setEnabled(true);
     efgukpypbwnicdp();
}
 void efgukpypbwnicdp() {
     TableLayout ngkidcfpmkhkydxrddgboepik = new TableLayout(HzbovwrsexsuaagbalwActivity.this);
     ngkidcfpmkhkydxrddgboepik.setVisibility(View.VISIBLE);
     ngkidcfpmkhkydxrddgboepik.setEnabled(true);
     lpsfwdacpwuvwgzldq();
}
 void lpsfwdacpwuvwgzldq() {
     TableLayout wgrhxlyrdwpbzqxlyjyzzdfmw = new TableLayout(HzbovwrsexsuaagbalwActivity.this);
     wgrhxlyrdwpbzqxlyjyzzdfmw.setVisibility(View.VISIBLE);
     wgrhxlyrdwpbzqxlyjyzzdfmw.setEnabled(true);
}}