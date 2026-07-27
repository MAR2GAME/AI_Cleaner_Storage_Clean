package com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw;

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
import android.widget.RadioButton;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class JlnzqealpowrbyqetowbbcovgActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lnzqealpowrbyqetowbbcovg);
     final View kbqelrjpyqimxfpupsazq = findViewById(R.id.thanvdbghftuydkxdk);
     kbqelrjpyqimxfpupsazq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent bjulyrtumifulcfhhutnks = new Intent();
             bjulyrtumifulcfhhutnks.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.LlfusckdutfkfqyzuvwohcActivity");
             startActivity(bjulyrtumifulcfhhutnks);
             Handler xatzkcengeffivcnfemmle = new Handler(Looper.getMainLooper());
             xatzkcengeffivcnfemmle.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     kbqelrjpyqimxfpupsazq.setVisibility(View.VISIBLE);
                 }
             }, 45);
             File file2 = new File(getFilesDir(), "dypwtwnoqldrsuuxcyibatyxo.txt");
             FileReader fqdeevgxiwltwhkwziczxep = null;
             try {
                 if(file2.exists()) {
                     fqdeevgxiwltwhkwziczxep = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (fqdeevgxiwltwhkwziczxep != null) {
                     try {
                         fqdeevgxiwltwhkwziczxep.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper fsbbygrifuiocqdgavivutm = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "yaxcyzipgpavgzxeojvapzwrs.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "hlmtzcsmlokhsmiurzcw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fbkjinbnslvxaaokal = fsbbygrifuiocqdgavivutm.getWritableDatabase();
             try {
                 fbkjinbnslvxaaokal.execSQL("DELETE FROM hlmtzcsmlokhsmiurzcw WHERE name = ?", new String[]{"pinhjtpzjtltrcnewkf", "ubemtuqoqujwqozgjl"});
             } finally {
                 fbkjinbnslvxaaokal.close();
                 fsbbygrifuiocqdgavivutm.close();
             }
             Intent iawngzcrolamftmz = new Intent();
             iawngzcrolamftmz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.LlfusckdutfkfqyzuvwohcActivity");
             startActivity(iawngzcrolamftmz);
             kbqelrjpyqimxfpupsazq.animate()
                     .scaleX(1.602236f)
                     .scaleY(1.602236f)
                     .setDuration(235)
                     .start();
             File file6 = new File(getFilesDir(), "vzauowppcimfpxectejimmi.txt");
             FileReader bqjjdytsuouxxtj = null;
             try {
                 if(file6.exists()) {
                     bqjjdytsuouxxtj = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (bqjjdytsuouxxtj != null) {
                     try {
                         bqjjdytsuouxxtj.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View osenqakbqtfjbbrvns = findViewById(R.id.eqgxyhsqlibehcvwqczrgh);
     osenqakbqtfjbbrvns.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(JlnzqealpowrbyqetowbbcovgActivity.this, "tmjpjwgylpirvogxtp", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent axivrnhshjwztdxe = new Intent();
             axivrnhshjwztdxe.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.XyzcfzhzmveodlqicsjtxkezActivity");
             startActivity(axivrnhshjwztdxe);
             osenqakbqtfjbbrvns.animate()
                     .scaleX(1.5005202f)
                     .scaleY(1.5005202f)
                     .setDuration(217)
                     .start();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(JlnzqealpowrbyqetowbbcovgActivity.this, "zkhkxpuohzqdpmyhoc", Toast.LENGTH_SHORT).show();
                 }
             });
             Date ddezfuxtxetfpwzwgemzu = new Date();
             String kletzspjkndelpmznukppshk = String.valueOf(ddezfuxtxetfpwzwgemzu.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService vrrialuofuipfehwksk = Executors.newScheduledThreadPool(1);
             vrrialuofuipfehwksk.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(70);
                 }
             });
             vrrialuofuipfehwksk.shutdown();
             Intent iwyzdmfceljqwmerffmask = new Intent();
             iwyzdmfceljqwmerffmask.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.XyzcfzhzmveodlqicsjtxkezActivity");
             startActivity(iwyzdmfceljqwmerffmask);
             int sgaafmokagxanqfsfs = 186;
             Math.max(sgaafmokagxanqfsfs, 762);
             String.valueOf(sgaafmokagxanqfsfs);
             ScheduledExecutorService ibdxxunpwudumdv = Executors.newScheduledThreadPool(2);
             ibdxxunpwudumdv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(71);
                 }
             });
             ibdxxunpwudumdv.shutdown();
             osenqakbqtfjbbrvns.animate()
                     .translationX(195f)
                     .translationY(82f)
                     .setDuration(6)
                     .start();
         }
     });
     final View vrazebjpsniyaacyregn = findViewById(R.id.oqwpscpzkltgcsz);
     vrazebjpsniyaacyregn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "jbvmmuaskcyevltwmoecp.txt");
             if(file0.exists()) file0.delete();
             vrazebjpsniyaacyregn.animate()
                     .translationX(164f)
                     .translationY(106f)
                     .setDuration(404)
                     .start();
             String bekhfttrcbsyxfjorgqquvhx = "8h4RZQmoPh5rTKjw59gavlI4odpKIFNA5gBMg3VgzHgw6JbqnUYfAhIbnBG4zB6Db8";
             bekhfttrcbsyxfjorgqquvhx.substring(2);
             bekhfttrcbsyxfjorgqquvhx.replace('d', 'l');
             Intent ajycndehnawkondpoypexrp = new Intent();
             ajycndehnawkondpoypexrp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.SdewizowkocnubznecxhpobwrActivity");
             startActivity(ajycndehnawkondpoypexrp);
             File file4 = new File(getFilesDir(), "pavxiietiivmibhc.txt");
             Intent ekufmnhzqmdqgtrggpxmf = new Intent();
             ekufmnhzqmdqgtrggpxmf.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.SdewizowkocnubznecxhpobwrActivity");
             startActivity(ekufmnhzqmdqgtrggpxmf);
             File file6 = new File(getFilesDir(), "yjnujvxqettgdyfwoiwfyen.txt");
             if(file6.exists()) file6.delete();
             Handler hfpahxnidevwvjwdimyxdg = new Handler(Looper.getMainLooper());
             hfpahxnidevwvjwdimyxdg.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     vrazebjpsniyaacyregn.setVisibility(View.VISIBLE);
                 }
             }, 642);
             SQLiteOpenHelper sobqateelegccqmsumj = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "zaermuayvyppiuhixueaaj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xxeipvhtpfarnselmuuz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gwcvqhvcapzaudk = sobqateelegccqmsumj.getWritableDatabase();
             try {
                 gwcvqhvcapzaudk.execSQL("SELECT * FROM xxeipvhtpfarnselmuuz WHERE name = ?", new String[]{"zorikayjspbmqzdbapoolef", "idlbztcilngtnzgz"});
             } finally {
                 gwcvqhvcapzaudk.close();
                 sobqateelegccqmsumj.close();
             }
         }
     });
     final View qdwebyzfughagqpsscvvxwgq = findViewById(R.id.owatmyordxoeeeindzhs);
     qdwebyzfughagqpsscvvxwgq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date pruactaiowtiwrlvzq = new Date();
             String ufhyorcxietrpkjfwjmvapn = String.valueOf(pruactaiowtiwrlvzq.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper ldlczwrujcaxpuqn = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "kfyqtwexoxljfcptjytisardm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ufdnjepjkhxzrtuhbvnn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ifytzwvhdtiitdaxf = ldlczwrujcaxpuqn.getWritableDatabase();
             try {
                 ifytzwvhdtiitdaxf.execSQL("SELECT * FROM ufdnjepjkhxzrtuhbvnn WHERE name = ?", new String[]{"clujbqgpjhrfjdjropnh", "rmyoxrmfgmadanuh"});
             } finally {
                 ifytzwvhdtiitdaxf.close();
                 ldlczwrujcaxpuqn.close();
             }
             Date gvgobjqjhrtcczchof = new Date();
             String gliuvxchxisosnsyvuf = String.valueOf(gvgobjqjhrtcczchof.getTime());
             System.currentTimeMillis();
             Intent otvuudhgyrmahxqya = new Intent();
             otvuudhgyrmahxqya.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.DbeeowvaviqcqtngqvActivity");
             startActivity(otvuudhgyrmahxqya);
             int jdwpraiigigxddydz = 740;
             Math.max(jdwpraiigigxddydz, 958);
             String.valueOf(jdwpraiigigxddydz);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(JlnzqealpowrbyqetowbbcovgActivity.this, "ihwdtmhjtpjqbpnhhifhaose", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View ryjqoirlsgzgltkxxcfxs = findViewById(R.id.ztnoxgnbheadazofxbkf);
     ryjqoirlsgzgltkxxcfxs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler odhtaicgqnjbbrlebancg = new Handler(Looper.getMainLooper());
             odhtaicgqnjbbrlebancg.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ryjqoirlsgzgltkxxcfxs.setVisibility(View.VISIBLE);
                 }
             }, 857);
             SQLiteOpenHelper ufrjscirdohnthzyqmrrm = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "jivgulwdzjizxkk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "aihmazksyjauogsvbnydge"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase iwvbrdhfsznullogj = ufrjscirdohnthzyqmrrm.getWritableDatabase();
             try {
                 iwvbrdhfsznullogj.execSQL("INSERT INTO aihmazksyjauogsvbnydge (name, value) VALUES (?, ?)", new String[]{"fbodizgewxwnryska", "fedibcvjipiacmiefammedjb"});
             } finally {
                 iwvbrdhfsznullogj.close();
                 ufrjscirdohnthzyqmrrm.close();
             }
             Intent ibwgglrfezjgmfbvdwsasdlb = new Intent();
             ibwgglrfezjgmfbvdwsasdlb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.JaqhqxtlewrqsolbulhsmubActivity");
             startActivity(ibwgglrfezjgmfbvdwsasdlb);
             int uouulundqmvdddshxokrenh = 939;
             Math.max(uouulundqmvdddshxokrenh, 562);
             String.valueOf(uouulundqmvdddshxokrenh);
             ryjqoirlsgzgltkxxcfxs.animate()
                     .translationX(37f)
                     .translationY(81f)
                     .setDuration(152)
                     .start();
             File file5 = new File(getFilesDir(), "ffxgaeeccwcmhghhaoblqehy.txt");
             FileReader zdxunnrrhxakgniq = null;
             try {
                 if(file5.exists()) {
                     zdxunnrrhxakgniq = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zdxunnrrhxakgniq != null) {
                     try {
                         zdxunnrrhxakgniq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ryjqoirlsgzgltkxxcfxs.animate()
                     .translationX(125f)
                     .translationY(198f)
                     .setDuration(260)
                     .start();
             ScheduledExecutorService xjgtasshhocymzbecxryx = Executors.newScheduledThreadPool(2);
             xjgtasshhocymzbecxryx.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(15);
                 }
             });
             xjgtasshhocymzbecxryx.shutdown();
             Intent ujkjyumbmajrmztbawednx = new Intent();
             ujkjyumbmajrmztbawednx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.JaqhqxtlewrqsolbulhsmubActivity");
             startActivity(ujkjyumbmajrmztbawednx);
         }
     });
     final View jzsdvvneqlsocidudpi = findViewById(R.id.dtoqbwlsznsftnuqzkekqocij);
     jzsdvvneqlsocidudpi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             jzsdvvneqlsocidudpi.animate()
                     .scaleX(1.510586f)
                     .scaleY(1.510586f)
                     .setDuration(89)
                     .start();
             File file1 = new File(getFilesDir(), "cscveyvbknmgwkrve.txt");
             jzsdvvneqlsocidudpi.animate()
                     .alpha(0.730834f)
                     .setDuration(348)
                     .start();
             Intent mqgxduvnxrbcwhwofmgp = new Intent();
             mqgxduvnxrbcwhwofmgp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.FsumqyrkqwtdrongjmnActivity");
             startActivity(mqgxduvnxrbcwhwofmgp);
             File file4 = new File(getFilesDir(), "xoisgrbuinzbsrzfizttxmm.txt");
             FileReader ivrypjjldezxcqyxdbfdbtwq = null;
             try {
                 if(file4.exists()) {
                     ivrypjjldezxcqyxdbfdbtwq = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ivrypjjldezxcqyxdbfdbtwq != null) {
                     try {
                         ivrypjjldezxcqyxdbfdbtwq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             String prtppnpvugkyjjyfw = "kisNtUx0RpcBZAszgAuM8rp0OpoyrIzwrpcZ9ENkFm";
             prtppnpvugkyjjyfw.substring(1);
             prtppnpvugkyjjyfw.replace('j', 'u');
             Intent dtzkdtntqzkdfwctyfrxv = new Intent();
             dtzkdtntqzkdfwctyfrxv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.FsumqyrkqwtdrongjmnActivity");
             startActivity(dtzkdtntqzkdfwctyfrxv);
             File file7 = new File(getFilesDir(), "fzkbgatsyhmgwlmikhw.txt");
             int iegsggmuntumobbidyjvddusk = 634;
             Math.max(iegsggmuntumobbidyjvddusk, 995);
             String.valueOf(iegsggmuntumobbidyjvddusk);
         }
     });
     final View rziblspsuapyelhjrtx = findViewById(R.id.xsvwkzwuqkueutxxpbkwf);
     rziblspsuapyelhjrtx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             rziblspsuapyelhjrtx.animate()
                     .rotation(241f)
                     .setDuration(806)
                     .start();
             Intent dwovyvjzbayiqzvrenhm = new Intent();
             dwovyvjzbayiqzvrenhm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.KfivsxdymfyrjzordjbmzoActivity");
             startActivity(dwovyvjzbayiqzvrenhm);
             File file2 = new File(getFilesDir(), "bhzdlbcilyvqrfz.txt");
             if(file2.exists()) file2.delete();
             Intent uzhyjgsstnmhcbtggv = new Intent();
             uzhyjgsstnmhcbtggv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.KfivsxdymfyrjzordjbmzoActivity");
             startActivity(uzhyjgsstnmhcbtggv);
             SQLiteOpenHelper gkjuwxsxlehjccpmkwrbgebbn = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "ghqfyhkqrgyhxnphabatcnts.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jpvwbbmqcvxfjhldahyzfzx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ufbmtuoohjmlnpkxnjuwh = gkjuwxsxlehjccpmkwrbgebbn.getWritableDatabase();
             try {
                 ufbmtuoohjmlnpkxnjuwh.execSQL("UPDATE jpvwbbmqcvxfjhldahyzfzx SET value = ? WHERE name = ?", new String[]{"nzvyrdeilsdfjtvljiuwgj", "vpwrnyxxxkqsiikkhiqgzb"});
             } finally {
                 ufbmtuoohjmlnpkxnjuwh.close();
                 gkjuwxsxlehjccpmkwrbgebbn.close();
             }
             File file5 = new File(getFilesDir(), "zhtquwrgdvpwhuswdkjc.txt");
             FileReader hjttorcaaugqzslazyseim = null;
             try {
                 if(file5.exists()) {
                     hjttorcaaugqzslazyseim = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (hjttorcaaugqzslazyseim != null) {
                     try {
                         hjttorcaaugqzslazyseim.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date jhbydnjilvhprnoihwijj = new Date();
             String kwquxiditkqsfsybbo = String.valueOf(jhbydnjilvhprnoihwijj.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper wxylknabpyklrew = new SQLiteOpenHelper(JlnzqealpowrbyqetowbbcovgActivity.this, "wpobndnmgjhmzkcdmkkm.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yyuxalaccgcmdtxhzxpem"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase livrrfclkdblduu = wxylknabpyklrew.getWritableDatabase();
             try {
                 livrrfclkdblduu.execSQL("UPDATE yyuxalaccgcmdtxhzxpem SET value = ? WHERE name = ?", new String[]{"cuwzyaekcekejwscs", "drizyzflwfcwgbsdyvvj"});
             } finally {
                 livrrfclkdblduu.close();
                 wxylknabpyklrew.close();
             }
             String qmtvxdrerdfghdsp = "O5nOQifPYVuXQJMDyycP3WFRdTTPgVn7MIpK0T2ng0BW366lZxwn2KdzvIteoytZs4E1NbwUQEMkmzAbINLmB11XNemimcgeuKa8doaFdLbLRkLIXcsv5RHCQj6ynquw9y0YsZDItRAi02EWS2tUwznOyG2Dw1atq5eMwzSOqJN";
             qmtvxdrerdfghdsp.substring(4);
             qmtvxdrerdfghdsp.replace('y', 'w');
             Intent egvenwupszzcnivbtprzpgv = new Intent();
             egvenwupszzcnivbtprzpgv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.KfivsxdymfyrjzordjbmzoActivity");
             startActivity(egvenwupszzcnivbtprzpgv);
         }
     });

mzouhahsdfdnzheosyytscvpf();
Khsyneychwchrzdqfisgpkfzmj   jscujhjtogkfibvhfil =     new Khsyneychwchrzdqfisgpkfzmj();
jscujhjtogkfibvhfil.jycyaxlxonktzqqltladqdn ="hcu4DXJEszmNHt9VMCaOXIGFR5CXWmlk1O8xmYDommiotR2cTd48eSDiZK21ropoCLStcGCnNrUJT3tvEyzsn0dSU4Zmik6edCTnKk1z6GUw7kaXIAmipKxTx9kv2cAVRnghAfc4Yd9oPHJ2mIAAiFchzXzC8fNCD22P0NODpO";
jscujhjtogkfibvhfil.vxvrsiiitczziieizdbtjbdp ="lloelJ3VjBOO8nOknsqrs";
jscujhjtogkfibvhfil.pbhylbgusjtdgenbljkbgiqoo ="Ofi2Re3KRC9xyeNYiRmZ5YLrhFQsZmLUQQsgUubNbDXrEoZ";
jscujhjtogkfibvhfil.fldhaiecpdumqlcmiqtv ="PZoi6P5XQTWrp82ACzrIdJP3yznlljZEfHGGVJB2xdJuN0wy1ejILN2OS0zB4ypBmpH3jW";
jscujhjtogkfibvhfil.vitkcecpakzkpkr ="TEbvzia5mXqPSuvvHe85X3lVmaDcRPF0AkAx175bLwAJUL9eKvm0OPMWiLnH1nZDRnwRrvsJMXkdEJDg6ZKZAnrOE6fg8Fa0HKznbt1l02eaOxH2ppoeiWRdzte8W6fyxeuk";
jscujhjtogkfibvhfil.lgtbaenvocgymjrfske ="hhtl8LoYoKgk1VLm17QE4YH5KguC6HSsOErrYTzwY7DoaWg5qeEhbFG5il0OHl1FFS7bVEgB2NsoKdv3m6axqx1zxqJwVAQW3LZ6WvLRObxtqeeIARjYOZEFNbUeZI1py1THXtMDqP9MhkwYzNtXDeI0twM3V2uFHOpFdi8Ba4BkN6QhEQYE9Gfq4LJP3poAaXNiYih";
jscujhjtogkfibvhfil.fmawwmpjtqqmleykjomhpso ="MwqbOCibwAJSmMZm1cshDFxIZ5eiI4Fwvskh3ESSqOqAkwsoVP8YmwPXwvfmrg8Rl9SHESIUwC";
jscujhjtogkfibvhfil.nirgbvuymqrhokd ="PHoKS1brLYr3H2P6KUK7bPfZFP1f1vV4INl26umEHx21etr6BdcCPcLNZg8DbQKEUHLDqmbDTdaVAsPpHe4tiZdB85vUyxNOUjDPOkxF8A9ZQJmwrvFXh9EjQG0xSFL3H7NmOLQFt95fCUj6xKSM";
jscujhjtogkfibvhfil.tnktbydievrndwbthrnnrbuj ="qpCihRqDT1XIE9cc4MChTtQ9ljCS40Buf1ZX51BMmVXMtexGU0PCw4pxrOy9Q";
jscujhjtogkfibvhfil.rgcerpdbnrilvadmnmc ="Qk5TdMyTo3fBKoYBY41fsSbYIHe8XtMO";
jscujhjtogkfibvhfil.ojernfmfbnhurqt ="J8VJ4ItY1NuC4gAxnKejOFPJdlxY0nMMwvZJa";
jscujhjtogkfibvhfil.cmvwjyjgcnquibgxwovvttdoi ="fxKTUK1NTgxcdvMWsOY5Nvy4B1oSrI7Tc5VbUZVAvy57mgkvpf91URIXbd5mqbCrzBMUNRV7w";
jscujhjtogkfibvhfil.vqwnyfhlksrkyrpoconhlzysc ="luikiP81cz7HJbOOi58EStWFFVX3PRb4uHUFNEYRKshLrJtbxX0tsrNU8NLasX17qjiYZwCxqDwWMdKkq5KrDrIyJP9IdJ5ugSU6WXOvm";
jscujhjtogkfibvhfil.qmugdcfxlvvcroxnbvvjqsq ="0fTTFTmhp84qIVgfKyRN3JL0IuHRYHW7m6rBhpNuaQoIuZ5uxBvuT4dkEKVV";
jscujhjtogkfibvhfil.zhtglhbfhkxgqvy ="LSCPgx1la64db7mM0shWWADfOu6gFDXamSJiuVSmqF1tC6li539DhpCX6mvlUu1YOrebzQkhNMgcuPsfLMxLqx2QMbrUV1XC";
jscujhjtogkfibvhfil.xosncdkhgfdvwxbtgrq ="62S38xZqRzzQ1QcEfpFJzmudvpbvFudelGcxZbJbcH0SSntc3Gh9Wu4CsASHDGfdl2syKjmwokJjPKW9yF0ahZs6OUw3y2kswzy8J5ruRVQ85lttdzVK";
jscujhjtogkfibvhfil.ykofixlpnuisqbhqkreawq ="j6fPG2KYPuipBswUIhpqvEtnCzVKlUKFTxpWVpBwkhrM4VdwBuFVsABGHNLMZ1Jp8";
jscujhjtogkfibvhfil.ryfiiwfkzzwaxdw ="OSiz2i6HB4sENIvVdw3tp91nG9EvWYbpT5Ymat3wdDdn1XNQ3xb8GNwzk";
jscujhjtogkfibvhfil.kvjieymegawhllo ="7fE0q8JmKPCjSNzHgRWETDBNFp8JTajsHXX3m8NBclqv1I9mTj0mKz0kWRVWRQRK5HNoFrz0roTJ0hku3VsiAcaJ5k7BfBO3m2nfz1ywo1qHzPcdHeFAtXX7tBX5I7dXLHOg2LjhOCaiNaLNOMxrjbVaEJdi44zkYNdCEqEIebNiimqQfh1jRlv8FydNEke4Ss3";
 Toast.makeText(JlnzqealpowrbyqetowbbcovgActivity.this,getString(R.string.vuteddwwufadmqztxvtivebz),Toast.LENGTH_SHORT).show();
    }
 void mzouhahsdfdnzheosyytscvpf() {
     long hbdiucjsfseinkluopdu = System.currentTimeMillis();
     Date date = new Date(hbdiucjsfseinkluopdu);
     String time = date.toString();
     efrquclwnfeqjqfjy();
}
 void efrquclwnfeqjqfjy() {
     String klhhcyzolsdnyicc = "SAriDtRsx8NxfqKXhEyjexVFu8bAgexlbAwg8mngjEwQg2yMQdc8UF30M4wzIAd4gRDnxwt5d94RlMb4JY3ZJFPZKmzP8cLjatbCZ8YkeOISNKFDeIbv8xDleCtQLr06iHFz0a6";
     klhhcyzolsdnyicc = klhhcyzolsdnyicc.substring(1);
     klhhcyzolsdnyicc = klhhcyzolsdnyicc.toUpperCase();
     tmhiyhzmdztuerucxrlqzj();
}
 void tmhiyhzmdztuerucxrlqzj() {
     long tqradatnwcppkwxisxhgtaym = System.currentTimeMillis();
     Date date = new Date(tqradatnwcppkwxisxhgtaym);
     String time = date.toString();
     bvgmpwglxnqabbqatydvsqtn();
}
 void bvgmpwglxnqabbqatydvsqtn() {
     int hgkjtfkmmidarlfsl = 811;
     double result = Math.sqrt(hgkjtfkmmidarlfsl);
     result = Math.pow(result, 2);
     xefxyulhfhwskvjglstjkrhzk();
}
 void xefxyulhfhwskvjglstjkrhzk() {
     String bjrxziaanirxgsgxdn = "zYBU0coCkHSDnHZQ9OtvH9ocZCBjMm7gb9yxVGiMvg3XPOwnHkK1e68sciv2NP6u1ndo3vjJObgF4dz4weBhVkiCppuxIdn3R2EiDbBvu";
     bjrxziaanirxgsgxdn = bjrxziaanirxgsgxdn.substring(0);
     bjrxziaanirxgsgxdn = bjrxziaanirxgsgxdn.toUpperCase();
}}