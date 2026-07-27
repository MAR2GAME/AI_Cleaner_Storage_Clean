package com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb;

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
import android.widget.FrameLayout;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class VhqqjohrqyqbxwjssjfgActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_hqqjohrqyqbxwjssjfg);
     final View gdhnkkjpurvsupz = findViewById(R.id.agevimhyrrmbclmhjaouvmub);
     gdhnkkjpurvsupz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent tkfuqrppttzyhgiewopn = new Intent();
             tkfuqrppttzyhgiewopn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.WmahrkyliqynhkctadedrgeabActivity");
             startActivity(tkfuqrppttzyhgiewopn);
             gdhnkkjpurvsupz.animate()
                     .rotation(190f)
                     .setDuration(24)
                     .start();
             SQLiteOpenHelper iixxaeocpxhkeerwucomxee = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "ckvcyjtdwadbxmg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vjmhcixjsxptqndp"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase memafrvadpagbrh = iixxaeocpxhkeerwucomxee.getWritableDatabase();
             try {
                 memafrvadpagbrh.execSQL("SELECT * FROM vjmhcixjsxptqndp WHERE name = ?", new String[]{"vagxkvaiciblfkcoaj", "uuwrfaqvwlusvviqvgcuenme"});
             } finally {
                 memafrvadpagbrh.close();
                 iixxaeocpxhkeerwucomxee.close();
             }
         }
     });
     final View sttjosursmqcetyhafwexib = findViewById(R.id.fncinykvjrgfycqpybelx);
     sttjosursmqcetyhafwexib.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(VhqqjohrqyqbxwjssjfgActivity.this, "yxtltyshjwliqquyulyxiuv", Toast.LENGTH_SHORT).show();
                 }
             });
             sttjosursmqcetyhafwexib.animate()
                     .scaleX(1.773213f)
                     .scaleY(1.773213f)
                     .setDuration(743)
                     .start();
             File file2 = new File(getFilesDir(), "dzaotztqhhetitlvcfwgu.txt");
             FileReader veqokippeupiguetdwgdccry = null;
             try {
                 if(file2.exists()) {
                     veqokippeupiguetdwgdccry = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (veqokippeupiguetdwgdccry != null) {
                     try {
                         veqokippeupiguetdwgdccry.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper nudswjnjeazrxearzaqlqqi = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "gfudmunwzfhnehlb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "khwwzrmtasmcevmyrsusdmr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase bdddoixnalortvtcflyki = nudswjnjeazrxearzaqlqqi.getWritableDatabase();
             try {
                 bdddoixnalortvtcflyki.execSQL("DELETE FROM khwwzrmtasmcevmyrsusdmr WHERE name = ?", new String[]{"xlhzxtdwmnmpwaotlmbg", "rcxfookrieyalsbwyicwr"});
             } finally {
                 bdddoixnalortvtcflyki.close();
                 nudswjnjeazrxearzaqlqqi.close();
             }
             Intent rymatxhmuhwgmjiplborlqwh = new Intent();
             rymatxhmuhwgmjiplborlqwh.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.BhitjkwxeyojfbrgbyActivity");
             startActivity(rymatxhmuhwgmjiplborlqwh);
             sttjosursmqcetyhafwexib.animate()
                     .translationX(187f)
                     .translationY(119f)
                     .setDuration(619)
                     .start();
             ScheduledExecutorService tvpwjazouiejahgv = Executors.newScheduledThreadPool(1);
             tvpwjazouiejahgv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(93);
                 }
             });
             tvpwjazouiejahgv.shutdown();
         }
     });
     final View klphkmykddspwqc = findViewById(R.id.mqrrwpofndxvqxwnxhjdyg);
     klphkmykddspwqc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             klphkmykddspwqc.animate()
                     .translationX(71f)
                     .translationY(117f)
                     .setDuration(672)
                     .start();
             File file1 = new File(getFilesDir(), "uwmbsnrxdohnjddgx.txt");
             FileWriter aupdmfpmfwyvntrtkkdy = null;
             try {
                 aupdmfpmfwyvntrtkkdy = new FileWriter(file1);
                 aupdmfpmfwyvntrtkkdy.write("mpYmHIwIloHFuy9qg6whWujEhiEgOZPIQD0GGEmIlhf6Ax0D2WQcpUA");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (aupdmfpmfwyvntrtkkdy != null) {
                     try {
                         aupdmfpmfwyvntrtkkdy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             klphkmykddspwqc.animate()
                     .scaleX(1.6153864f)
                     .scaleY(1.6153864f)
                     .setDuration(601)
                     .start();
             Intent xwqtxrnqcldkatiwzpe = new Intent();
             xwqtxrnqcldkatiwzpe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.NasvbtljaaciwzhqrbwlhsActivity");
             startActivity(xwqtxrnqcldkatiwzpe);
             ScheduledExecutorService bwhbyjgossmsgefekuwolddh = Executors.newScheduledThreadPool(1);
             bwhbyjgossmsgefekuwolddh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(95);
                 }
             });
             bwhbyjgossmsgefekuwolddh.shutdown();
         }
     });
     final View cnecdsmmvycebdjdykobkwnxe = findViewById(R.id.lnjzewdfvnkouwfubtrixh);
     cnecdsmmvycebdjdykobkwnxe.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper dirmhqferoonxrrwdcztw = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "jpafgfkxkizcymyeguhk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rafldohjfdnoucbhnhj"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xulwnoaquukvvahbwsmjudffw = dirmhqferoonxrrwdcztw.getWritableDatabase();
             try {
                 xulwnoaquukvvahbwsmjudffw.execSQL("SELECT * FROM rafldohjfdnoucbhnhj WHERE name = ?", new String[]{"kjidzfjqarczhbepskcrpcwmo", "ukhgkbxlfvygikbyeyfozjc"});
             } finally {
                 xulwnoaquukvvahbwsmjudffw.close();
                 dirmhqferoonxrrwdcztw.close();
             }
             String utpdynnahiecysrlcgpnqqvtt = "uwLG3FboTKD4";
             utpdynnahiecysrlcgpnqqvtt.substring(2);
             utpdynnahiecysrlcgpnqqvtt.replace('k', 'h');
             cnecdsmmvycebdjdykobkwnxe.animate()
                     .scaleX(1.2381117f)
                     .scaleY(1.2381117f)
                     .setDuration(243)
                     .start();
             Intent ywqaokjfkqhkpoanvqr = new Intent();
             ywqaokjfkqhkpoanvqr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.GgxfeqngqcprflgzsActivity");
             startActivity(ywqaokjfkqhkpoanvqr);
             SQLiteOpenHelper cqiyrhvvrcfhmqmug = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "iitssuxrgiryfeuxxumzw.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lpmzbiktcxafphsoo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ktqbimogxraavpephpcmapp = cqiyrhvvrcfhmqmug.getWritableDatabase();
             try {
                 ktqbimogxraavpephpcmapp.execSQL("UPDATE lpmzbiktcxafphsoo SET value = ? WHERE name = ?", new String[]{"krycmloggoprsbabukqr", "lmhyyfmxznhrxvrunci"});
             } finally {
                 ktqbimogxraavpephpcmapp.close();
                 cqiyrhvvrcfhmqmug.close();
             }
             File file5 = new File(getFilesDir(), "wxbbuplyoeqhhwfvvbmpsgkt.txt");
             cnecdsmmvycebdjdykobkwnxe.animate()
                     .alpha(0.009749711f)
                     .setDuration(572)
                     .start();
             SQLiteOpenHelper zrjyyqdgcxlxodryorsxudp = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "zabjujpvibgvfnloiksss.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qbhmvurhgtwkandjdyarri"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ocavnaaafilonxoekwyvd = zrjyyqdgcxlxodryorsxudp.getWritableDatabase();
             try {
                 ocavnaaafilonxoekwyvd.execSQL("SELECT * FROM qbhmvurhgtwkandjdyarri WHERE name = ?", new String[]{"smokdayciecqxiofqz", "idmiuetyaalldccmlbyxig"});
             } finally {
                 ocavnaaafilonxoekwyvd.close();
                 zrjyyqdgcxlxodryorsxudp.close();
             }
             ScheduledExecutorService xxynixecussqhmpqly = Executors.newScheduledThreadPool(4);
             xxynixecussqhmpqly.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(35);
                 }
             });
             xxynixecussqhmpqly.shutdown();
             File file9 = new File(getFilesDir(), "gzdroxlxcdslkklgodtg.txt");
             FileWriter wzmxkqspyhqxlsuupwbx = null;
             try {
                 wzmxkqspyhqxlsuupwbx = new FileWriter(file9);
                 wzmxkqspyhqxlsuupwbx.write("7c1K3P7d7YTlpobq127VvNsfopBaLHAuezSvtp0MrIj72mAyzQItHnAO8ZL4cs4Jbik0Gf46Yi3VCWagjVn5Piubn5zWSKUrT7xJZlWVJhl6DYpprgjYp0VylzES9NEyKtmU4McNyejSxi9C12UPmsHhoIdUOjydjy3iNSJLl3");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wzmxkqspyhqxlsuupwbx != null) {
                     try {
                         wzmxkqspyhqxlsuupwbx.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View rqunuuxllxyojvhquxux = findViewById(R.id.melvehhcitnjkejngirqtjygf);
     rqunuuxllxyojvhquxux.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler omonvywsosbcqtqinjhxnq = new Handler(Looper.getMainLooper());
             omonvywsosbcqtqinjhxnq.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     rqunuuxllxyojvhquxux.setVisibility(View.VISIBLE);
                 }
             }, 809);
             File file1 = new File(getFilesDir(), "lhtdexfvgfxsjvdm.txt");
             SQLiteOpenHelper pjryghxnzdclxkdcrfeez = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "veurnlhiajjcjoiixt.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jmcbhnuudpnltxtf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gwyhpzaybldcdfn = pjryghxnzdclxkdcrfeez.getWritableDatabase();
             try {
                 gwyhpzaybldcdfn.execSQL("DELETE FROM jmcbhnuudpnltxtf WHERE name = ?", new String[]{"jggbnpwhkejttwvd", "sbpbfryjjbzutdzr"});
             } finally {
                 gwyhpzaybldcdfn.close();
                 pjryghxnzdclxkdcrfeez.close();
             }
             File file3 = new File(getFilesDir(), "wnazkkbhnrwkgfnvecihwi.txt");
             FileWriter acrfjlaxiqpkxzfana = null;
             try {
                 acrfjlaxiqpkxzfana = new FileWriter(file3);
                 acrfjlaxiqpkxzfana.write("YmJhB13JaZ24ijj0TJvuT05mz2282TVuFLhisJdZIIu0DPsoNb1AbDxTsOlVFUhZnVPJv2JV9pjoEaAQ0FPG5lzVHR2uuhEtbGhgMsgd5hJwRWamBAj1IXxKoTRb4epKQZvfgepoJscaNrFqWabxZnW6SUoOZCEm633lbDnWSjkfJroSPDt8Aw");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (acrfjlaxiqpkxzfana != null) {
                     try {
                         acrfjlaxiqpkxzfana.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View zfvvtoullvaioftjv = findViewById(R.id.kasvfwqscpwstlcxpqgay);
     zfvvtoullvaioftjv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int spaymjvtayktgnnbkgaoevfgd = 691;
             Math.max(spaymjvtayktgnnbkgaoevfgd, 846);
             String.valueOf(spaymjvtayktgnnbkgaoevfgd);
             File file1 = new File(getFilesDir(), "zscacxrgpuwghvrcqq.txt");
             SQLiteOpenHelper jtdtikrmykejagdhcjq = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "npsxmibkgvziibwyxavxiva.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vytnhqkqsrncaozozqn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase axwhlosbxzfyvleelekzt = jtdtikrmykejagdhcjq.getWritableDatabase();
             try {
                 axwhlosbxzfyvleelekzt.execSQL("SELECT * FROM vytnhqkqsrncaozozqn WHERE name = ?", new String[]{"exbqlhbkwaspqcrynangrjp", "setzklsizgqfhnsubhdmwfd"});
             } finally {
                 axwhlosbxzfyvleelekzt.close();
                 jtdtikrmykejagdhcjq.close();
             }
         }
     });
     final View pwvyqdotexgwwnjc = findViewById(R.id.deiimitcwukfpajuf);
     pwvyqdotexgwwnjc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pwvyqdotexgwwnjc.animate()
                     .alpha(0.19413894f)
                     .setDuration(110)
                     .start();
             SQLiteOpenHelper ousomohfoarlwex = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "cknfmlengrzjzeommqogalwgj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wcgxhaavgasggzvmx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fjahciumjecwzjzc = ousomohfoarlwex.getWritableDatabase();
             try {
                 fjahciumjecwzjzc.execSQL("SELECT * FROM wcgxhaavgasggzvmx WHERE name = ?", new String[]{"nsimuqqlkzbxgve", "tulrylfwyzfpxqtbdiuc"});
             } finally {
                 fjahciumjecwzjzc.close();
                 ousomohfoarlwex.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(VhqqjohrqyqbxwjssjfgActivity.this, "fdobhwoksfaafly", Toast.LENGTH_SHORT).show();
                 }
             });
             File file3 = new File(getFilesDir(), "wgcgaloawbcovkbvyooh.txt");
             Intent iyvkrjegqbqfdpfpub = new Intent();
             iyvkrjegqbqfdpfpub.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.JjwnbjnsyzbgajwowwrncActivity");
             startActivity(iyvkrjegqbqfdpfpub);
             int mltjqdujbtirlkvbousi = 686;
             Math.max(mltjqdujbtirlkvbousi, 598);
             String.valueOf(mltjqdujbtirlkvbousi);
             Intent jctweaqnjtldfzvvevqyjeqi = new Intent();
             jctweaqnjtldfzvvevqyjeqi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.JjwnbjnsyzbgajwowwrncActivity");
             startActivity(jctweaqnjtldfzvvevqyjeqi);
         }
     });
     final View qlninydkpcfmnfsyjx = findViewById(R.id.tpbgmyfklxihxeulsdlnhaoe);
     qlninydkpcfmnfsyjx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper cnfhnwpenttnwne = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "ufsnolysqqekexlvji.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "erzofvvntkjdfrd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fhpdvmnggfgjfxgdm = cnfhnwpenttnwne.getWritableDatabase();
             try {
                 fhpdvmnggfgjfxgdm.execSQL("INSERT INTO erzofvvntkjdfrd (name, value) VALUES (?, ?)", new String[]{"tbhimtzhfmqjvtklcxiyfmt", "zumrcbmcunkyoksvnv"});
             } finally {
                 fhpdvmnggfgjfxgdm.close();
                 cnfhnwpenttnwne.close();
             }
             Intent mfbcqrboqukumcbpwplpn = new Intent();
             mfbcqrboqukumcbpwplpn.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.FejzqkhckemqolmepwuActivity");
             startActivity(mfbcqrboqukumcbpwplpn);
             ScheduledExecutorService cfcdfoiswompxcvs = Executors.newScheduledThreadPool(3);
             cfcdfoiswompxcvs.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(83);
                 }
             });
             cfcdfoiswompxcvs.shutdown();
             int zntioisfjbszucl = 171;
             Math.max(zntioisfjbszucl, 705);
             String.valueOf(zntioisfjbszucl);
         }
     });
     final View djcgghcecymfdspyuvtyi = findViewById(R.id.dqlrxpidpleekgzxmnomnkzu);
     djcgghcecymfdspyuvtyi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper jgddihbfjuaefidctzo = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "lmtjyyytzjtxzmocblbnfpyzi.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nebwnrsbjgrfmra"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qyxdsdvglpkcchypfppvt = jgddihbfjuaefidctzo.getWritableDatabase();
             try {
                 qyxdsdvglpkcchypfppvt.execSQL("SELECT * FROM nebwnrsbjgrfmra WHERE name = ?", new String[]{"wybyiuihwhazznbxynubjzm", "qapvhfdmznkxzafo"});
             } finally {
                 qyxdsdvglpkcchypfppvt.close();
                 jgddihbfjuaefidctzo.close();
             }
             File file1 = new File(getFilesDir(), "nyjifufzcttctrkjbbwopp.txt");
             if(file1.exists()) file1.delete();
             djcgghcecymfdspyuvtyi.animate()
                     .rotation(20f)
                     .setDuration(122)
                     .start();
             Intent cibkyxkmxhvbzefsowxnfqgam = new Intent();
             cibkyxkmxhvbzefsowxnfqgam.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.VbveunrwflfuerjhsiActivity");
             startActivity(cibkyxkmxhvbzefsowxnfqgam);
             SQLiteOpenHelper baqxxesleflmemjcm = new SQLiteOpenHelper(VhqqjohrqyqbxwjssjfgActivity.this, "upzaehdlboimyhjtmx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "satmtxysmezsyorum"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xdihmdgfdxtkcuakfowqwovf = baqxxesleflmemjcm.getWritableDatabase();
             try {
                 xdihmdgfdxtkcuakfowqwovf.execSQL("INSERT INTO satmtxysmezsyorum (name, value) VALUES (?, ?)", new String[]{"lrqvysojpyjmgvklx", "xtpezhtkwsnbgpmjjhzqwviw"});
             } finally {
                 xdihmdgfdxtkcuakfowqwovf.close();
                 baqxxesleflmemjcm.close();
             }
             Intent vknwpwmvfvejngnuvxtydvpq = new Intent();
             vknwpwmvfvejngnuvxtydvpq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.wcnwkyrlsklgluvb.VbveunrwflfuerjhsiActivity");
             startActivity(vknwpwmvfvejngnuvxtydvpq);
             Handler aealgvwtbzhppol = new Handler(Looper.getMainLooper());
             aealgvwtbzhppol.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     djcgghcecymfdspyuvtyi.setVisibility(View.VISIBLE);
                 }
             }, 478);
             File file7 = new File(getFilesDir(), "cpjmccmjrizjsymsnbywn.txt");
         }
     });

pwwkjsfszajfshzwxoi();
Isedujwfxletisbpvpw   bacpxorjmptzdtehzc =     new Isedujwfxletisbpvpw();
 Toast.makeText(VhqqjohrqyqbxwjssjfgActivity.this,getString(R.string.fozvkyvktrthfkv),Toast.LENGTH_SHORT).show();
    }
 void pwwkjsfszajfshzwxoi() {
     int uevkghbghcqjavn = 908;
     double result = Math.sqrt(uevkghbghcqjavn);
     result = Math.pow(result, 2);
     plnbixhvilpdblxzgqy();
}
 void plnbixhvilpdblxzgqy() {
     String lyrropfhmqtrkfefzgqqhg = "QcS9P25TxFp9Wq0Rfl5fGE0TOLnM4BRrVcsI5BU0grypKGTVR38LpNtLOlipoxHI18vsALzzdibg9tUg3GutNq3isyrNaQSkZWdhsPLrPsMwvG1au3j1yavQGUgc6Y5J4tMnHoG2lHVyEQ0xqcLFk9HRh3FhCX1YjPTL3YyPxDhqgyxJu1Ke";
     lyrropfhmqtrkfefzgqqhg = lyrropfhmqtrkfefzgqqhg.substring(1);
     lyrropfhmqtrkfefzgqqhg = lyrropfhmqtrkfefzgqqhg.toUpperCase();
}}