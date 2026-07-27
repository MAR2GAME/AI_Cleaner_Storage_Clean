package com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd;

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
import android.widget.SeekBar;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class JmhbbbfheoxxwvotpssjcvbActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mhbbbfheoxxwvotpssjcvb);
     final View ecdtwsqlbbxoapfq = findViewById(R.id.vzuehogonvtcabzd);
     ecdtwsqlbbxoapfq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler uxlrhblyoliccuiidnlgl = new Handler(Looper.getMainLooper());
             uxlrhblyoliccuiidnlgl.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ecdtwsqlbbxoapfq.setVisibility(View.VISIBLE);
                 }
             }, 932);
             Intent kfrthecskjgzthsdme = new Intent();
             kfrthecskjgzthsdme.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.YbewmarzwcsjaymwakytActivity");
             startActivity(kfrthecskjgzthsdme);
             File file2 = new File(getFilesDir(), "cvtagsplgtfuqavzybqllp.txt");
             FileReader mvvnwqyujtbrtfsdvdhq = null;
             try {
                 if(file2.exists()) {
                     mvvnwqyujtbrtfsdvdhq = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (mvvnwqyujtbrtfsdvdhq != null) {
                     try {
                         mvvnwqyujtbrtfsdvdhq.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper omqvrinsgybfeqkxdziigu = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "cvlzwymtppqvegejhn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qxxsswxtbqyekrpgr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xnrpbrxnysagxvjvjkhhoos = omqvrinsgybfeqkxdziigu.getWritableDatabase();
             try {
                 xnrpbrxnysagxvjvjkhhoos.execSQL("INSERT INTO qxxsswxtbqyekrpgr (name, value) VALUES (?, ?)", new String[]{"iejmmvgkaxdrnwqpcrbjsci", "rldjiaaoabvsjrvbandy"});
             } finally {
                 xnrpbrxnysagxvjvjkhhoos.close();
                 omqvrinsgybfeqkxdziigu.close();
             }
             File file4 = new File(getFilesDir(), "lnotwqarplrmorhh.txt");
             FileReader cncarsreilxwscgdegntsrtc = null;
             try {
                 if(file4.exists()) {
                     cncarsreilxwscgdegntsrtc = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (cncarsreilxwscgdegntsrtc != null) {
                     try {
                         cncarsreilxwscgdegntsrtc.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper iibmrtyjrukzngfne = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "nngwcvzqqonhqsjmpwxpsji.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "skdknxfrkgniscr"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase frmvdatqxmdqwwaprsktsrb = iibmrtyjrukzngfne.getWritableDatabase();
             try {
                 frmvdatqxmdqwwaprsktsrb.execSQL("DELETE FROM skdknxfrkgniscr WHERE name = ?", new String[]{"jnhckravgfwkeslzgkrhy", "ddfwyvlmumfnlhdbvzpsaplcm"});
             } finally {
                 frmvdatqxmdqwwaprsktsrb.close();
                 iibmrtyjrukzngfne.close();
             }
         }
     });
     final View ruipjzwslbkldtxbs = findViewById(R.id.ttfnkgtivkrvnowyvadvatiwa);
     ruipjzwslbkldtxbs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService xwnkgayxbjxtvjf = Executors.newScheduledThreadPool(2);
             xwnkgayxbjxtvjf.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(37);
                 }
             });
             xwnkgayxbjxtvjf.shutdown();
             File file1 = new File(getFilesDir(), "lgajtvdxpuzwysxmv.txt");
             ScheduledExecutorService yjowflqgcwzmqyzpltjwumo = Executors.newScheduledThreadPool(2);
             yjowflqgcwzmqyzpltjwumo.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(46);
                 }
             });
             yjowflqgcwzmqyzpltjwumo.shutdown();
             SQLiteOpenHelper xftunfushunznpfvyrlzgblat = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "uqhwwfftlpjyfumvirrdtkomk.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ivdzwsrsvbspucfb"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gbmggsebagurbcsmjmlqgg = xftunfushunznpfvyrlzgblat.getWritableDatabase();
             try {
                 gbmggsebagurbcsmjmlqgg.execSQL("INSERT INTO ivdzwsrsvbspucfb (name, value) VALUES (?, ?)", new String[]{"ewyvkhlydjjyncex", "nkfkrgbuqxxqyuapaatbgxlx"});
             } finally {
                 gbmggsebagurbcsmjmlqgg.close();
                 xftunfushunznpfvyrlzgblat.close();
             }
             Date qztofhjvnvyakkhqtiysexavr = new Date();
             String yupaoegclgicuiosykia = String.valueOf(qztofhjvnvyakkhqtiysexavr.getTime());
             System.currentTimeMillis();
             Intent ngjuqkjcjfzefmfabtndrjpka = new Intent();
             ngjuqkjcjfzefmfabtndrjpka.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.OwwqqhkdtxnwndfkcizfpzActivity");
             startActivity(ngjuqkjcjfzefmfabtndrjpka);
         }
     });
     final View gndckginxaweqdnwo = findViewById(R.id.egbwvvzsmssynfhoms);
     gndckginxaweqdnwo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper njrmwjvcqvhdlnn = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "ruckncdvmwqemmr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yodyickhysdmpayftbfv"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase qsdillokblqqknd = njrmwjvcqvhdlnn.getWritableDatabase();
             try {
                 qsdillokblqqknd.execSQL("SELECT * FROM yodyickhysdmpayftbfv WHERE name = ?", new String[]{"qflxnjgadijvgotvapccxzsoj", "kantumctjihvmjwopqkog"});
             } finally {
                 qsdillokblqqknd.close();
                 njrmwjvcqvhdlnn.close();
             }
             gndckginxaweqdnwo.animate()
                     .translationX(59f)
                     .translationY(170f)
                     .setDuration(133)
                     .start();
             Intent iatlkngdkozyafqchgdkpjywp = new Intent();
             iatlkngdkozyafqchgdkpjywp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.YwtodzutqsabfazwntzwbvzjlActivity");
             startActivity(iatlkngdkozyafqchgdkpjywp);
             SQLiteOpenHelper pemrpbqviawoyickpqkhlpomm = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "vptncwcwcpksrgujqbwnehcx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "uaxdzokiiekjotraxle"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xzjstkjgoeyeredao = pemrpbqviawoyickpqkhlpomm.getWritableDatabase();
             try {
                 xzjstkjgoeyeredao.execSQL("INSERT INTO uaxdzokiiekjotraxle (name, value) VALUES (?, ?)", new String[]{"aqdssleebcsizhe", "kelpnutyjvgdzgh"});
             } finally {
                 xzjstkjgoeyeredao.close();
                 pemrpbqviawoyickpqkhlpomm.close();
             }
         }
     });
     final View ocoamlmhjdoawlxf = findViewById(R.id.wzimheprfkmwobjuy);
     ocoamlmhjdoawlxf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ocoamlmhjdoawlxf.animate()
                     .rotation(305f)
                     .setDuration(570)
                     .start();
             File file1 = new File(getFilesDir(), "qldduknopyxggmuntdjbylfbl.txt");
             if(file1.exists()) file1.delete();
         }
     });
     final View autimhlieowdxxxiwqhemzyan = findViewById(R.id.oafkdwegyxhblhnz);
     autimhlieowdxxxiwqhemzyan.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             autimhlieowdxxxiwqhemzyan.animate()
                     .rotation(86f)
                     .setDuration(668)
                     .start();
             File file1 = new File(getFilesDir(), "nkuuydzabxtjzyhsve.txt");
         }
     });
     final View qkalmatgpggchakopdhbvbnt = findViewById(R.id.xbvaoihvtyohsdnhjrbmcpo);
     qkalmatgpggchakopdhbvbnt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "silsqjvlpykrnmksgzb.txt");
             Intent cgroouoevsmjkgvd = new Intent();
             cgroouoevsmjkgvd.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.CihqkcycynifeydamvActivity");
             startActivity(cgroouoevsmjkgvd);
             int ilgbmcrumleztmnwyygzkycp = 872;
             Math.max(ilgbmcrumleztmnwyygzkycp, 422);
             String.valueOf(ilgbmcrumleztmnwyygzkycp);
             Intent miadtnilbmnlmyzoljcbiwrl = new Intent();
             miadtnilbmnlmyzoljcbiwrl.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.CihqkcycynifeydamvActivity");
             startActivity(miadtnilbmnlmyzoljcbiwrl);
             qkalmatgpggchakopdhbvbnt.animate()
                     .scaleX(0.8351139f)
                     .scaleY(0.8351139f)
                     .setDuration(729)
                     .start();
             Intent unalshuubpbdjbvfkiwzwsdc = new Intent();
             unalshuubpbdjbvfkiwzwsdc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.CihqkcycynifeydamvActivity");
             startActivity(unalshuubpbdjbvfkiwzwsdc);
             SQLiteOpenHelper rpkktorygvlzelxwnrb = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "actyneloidmzgdzwkocpuj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yvimqwkojjdqkwuqlzxcruyxh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mosbpduofhxcjsxfowicbii = rpkktorygvlzelxwnrb.getWritableDatabase();
             try {
                 mosbpduofhxcjsxfowicbii.execSQL("UPDATE yvimqwkojjdqkwuqlzxcruyxh SET value = ? WHERE name = ?", new String[]{"pxalduefajvnesrjh", "ynyojigsejjzotyfp"});
             } finally {
                 mosbpduofhxcjsxfowicbii.close();
                 rpkktorygvlzelxwnrb.close();
             }
         }
     });
     final View eqbfmrfjvxjpmatohkun = findViewById(R.id.fliplkwlkqfszqhjxytebs);
     eqbfmrfjvxjpmatohkun.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent bewjmsggsdjceus = new Intent();
             bewjmsggsdjceus.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.PcccdtnfudhymnqwoqActivity");
             startActivity(bewjmsggsdjceus);
             String merudtiyhzrvrrjpksgfving = "YB6HDz6bMD1TTIJysZ0e6pzeN0BhcB4mkTWlWsbeAaIsihvP3vJxgDTKnzUECa2TsJcLaQNkeqJA767Dqy0wj98rfSzwS6fCK1i8bB4fJu67YzNcr9twdf3WSgnCH2jGu6CiJMjKkjsJJ1kM6MXzB5lsJcGXSYmBNVtJ2eZcq";
             merudtiyhzrvrrjpksgfving.substring(2);
             merudtiyhzrvrrjpksgfving.replace('j', 'x');
             SQLiteOpenHelper dvfqgnfzkfwtlnxpb = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "lfctkdcloqmzvzhg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "frfxjprtgbtvylzzznqzva"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase zpdzkgryoktkbnaglmk = dvfqgnfzkfwtlnxpb.getWritableDatabase();
             try {
                 zpdzkgryoktkbnaglmk.execSQL("SELECT * FROM frfxjprtgbtvylzzznqzva WHERE name = ?", new String[]{"rowdieqkqmnekwinobny", "jkgepnrndiixszjzjcmbuixuc"});
             } finally {
                 zpdzkgryoktkbnaglmk.close();
                 dvfqgnfzkfwtlnxpb.close();
             }
             eqbfmrfjvxjpmatohkun.animate()
                     .translationX(136f)
                     .translationY(192f)
                     .setDuration(289)
                     .start();
             Intent ojxmkusqxfmmkuhburvwvg = new Intent();
             ojxmkusqxfmmkuhburvwvg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.PcccdtnfudhymnqwoqActivity");
             startActivity(ojxmkusqxfmmkuhburvwvg);
             File file5 = new File(getFilesDir(), "igzgzxcydxbhiddgy.txt");
             Intent vfknlmrhcamiijomblv = new Intent();
             vfknlmrhcamiijomblv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.PcccdtnfudhymnqwoqActivity");
             startActivity(vfknlmrhcamiijomblv);
             String afgguqwdiguxkge = "foUfm1ygEwMtCO9G5Nh2grPtS9mnCdHZzMxFi8foz2xyhkumFgk7k4XP68aFK4cZhxGc3Sv8dEqGcIRna4ycO4Rw2pewz64mqaC1sbP0qWp6FX1481pFfsb2nNPxaNF10dELr08C1";
             afgguqwdiguxkge.substring(0);
             afgguqwdiguxkge.replace('y', 'z');
             eqbfmrfjvxjpmatohkun.animate()
                     .translationX(18f)
                     .translationY(111f)
                     .setDuration(197)
                     .start();
         }
     });
     final View otwxpvxhvwvcwxgonalbq = findViewById(R.id.ezimsyhepignwqlbpefmukvl);
     otwxpvxhvwvcwxgonalbq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "jldmqhhlmyzvhmwrnmcqbi.txt");
             FileReader dneuiixnpciplyovavnsovtu = null;
             try {
                 if(file0.exists()) {
                     dneuiixnpciplyovavnsovtu = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (dneuiixnpciplyovavnsovtu != null) {
                     try {
                         dneuiixnpciplyovavnsovtu.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             otwxpvxhvwvcwxgonalbq.animate()
                     .rotation(46f)
                     .setDuration(193)
                     .start();
             Intent jdsujolycewnnedj = new Intent();
             jdsujolycewnnedj.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.DgnjimgipqytbobimdizybmuwActivity");
             startActivity(jdsujolycewnnedj);
             int uhnttdctnutneavga = 7;
             Math.max(uhnttdctnutneavga, 420);
             String.valueOf(uhnttdctnutneavga);
         }
     });
     final View lxwhxmpdtfkitxdswftc = findViewById(R.id.rvxwxjlloirvlfiywa);
     lxwhxmpdtfkitxdswftc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "lpycbffkxmacgzlcduleknfn.txt");
             Handler lthztktyelpmizmecqr = new Handler(Looper.getMainLooper());
             lthztktyelpmizmecqr.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     lxwhxmpdtfkitxdswftc.setVisibility(View.VISIBLE);
                 }
             }, 827);
             File file2 = new File(getFilesDir(), "pwhwpryjqtruexx.txt");
             FileReader vuqnywgblqjimnrsscr = null;
             try {
                 if(file2.exists()) {
                     vuqnywgblqjimnrsscr = new FileReader(file2);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (vuqnywgblqjimnrsscr != null) {
                     try {
                         vuqnywgblqjimnrsscr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             SQLiteOpenHelper yjjxievppklisfpy = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "eomgvnqcuslbhdxr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lgogtvmmiiswtftbtyuvx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fcsxipewuwzrvyseok = yjjxievppklisfpy.getWritableDatabase();
             try {
                 fcsxipewuwzrvyseok.execSQL("SELECT * FROM lgogtvmmiiswtftbtyuvx WHERE name = ?", new String[]{"vcebjmoayekscne", "ulbbirgrcqomnrswq"});
             } finally {
                 fcsxipewuwzrvyseok.close();
                 yjjxievppklisfpy.close();
             }
             File file4 = new File(getFilesDir(), "osxvutnuwhcsccdnp.txt");
             FileReader psruutshafcudqd = null;
             try {
                 if(file4.exists()) {
                     psruutshafcudqd = new FileReader(file4);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (psruutshafcudqd != null) {
                     try {
                         psruutshafcudqd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent rdklokasuzidfczlaxcxg = new Intent();
             rdklokasuzidfczlaxcxg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.GrfktnmylacszkqvbpxActivity");
             startActivity(rdklokasuzidfczlaxcxg);
         }
     });
     final View qfpjjbubrowahxnzhavtgy = findViewById(R.id.ycuzqfmyenezazqnaexnearhr);
     qfpjjbubrowahxnzhavtgy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "zplnhrmrjddwlbco.txt");
             SQLiteOpenHelper esnyhrxidlmhjrtpn = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "lbkilqbdgmyrbliowht.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qadplowdvnycoknxgz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tappzzesjvrstjpgk = esnyhrxidlmhjrtpn.getWritableDatabase();
             try {
                 tappzzesjvrstjpgk.execSQL("DELETE FROM qadplowdvnycoknxgz WHERE name = ?", new String[]{"tylerlvlakgjwwwpca", "nfxmawvfzkxpdzrparsqwv"});
             } finally {
                 tappzzesjvrstjpgk.close();
                 esnyhrxidlmhjrtpn.close();
             }
             ScheduledExecutorService xabvdcdivmeibym = Executors.newScheduledThreadPool(1);
             xabvdcdivmeibym.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(10);
                 }
             });
             xabvdcdivmeibym.shutdown();
             File file3 = new File(getFilesDir(), "wsykuapzdxmphukzoigg.txt");
             Intent bkclgrfhlqjbcldoz = new Intent();
             bkclgrfhlqjbcldoz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.RattmsbbkqizdrgqActivity");
             startActivity(bkclgrfhlqjbcldoz);
         }
     });
     final View vperhoxxylqhmuicfkwzbbee = findViewById(R.id.iohdqbribcktsrwtyci);
     vperhoxxylqhmuicfkwzbbee.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             vperhoxxylqhmuicfkwzbbee.animate()
                     .rotation(2f)
                     .setDuration(437)
                     .start();
             File file1 = new File(getFilesDir(), "hfdndwbcmckqfcxxkpbo.txt");
             FileReader zigyktjmuogbxlhwy = null;
             try {
                 if(file1.exists()) {
                     zigyktjmuogbxlhwy = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zigyktjmuogbxlhwy != null) {
                     try {
                         zigyktjmuogbxlhwy.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService uhoomzyxqwffzgxrd = Executors.newScheduledThreadPool(2);
             uhoomzyxqwffzgxrd.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(9);
                 }
             });
             uhoomzyxqwffzgxrd.shutdown();
             vperhoxxylqhmuicfkwzbbee.animate()
                     .rotation(299f)
                     .setDuration(798)
                     .start();
             File file4 = new File(getFilesDir(), "mkkuwlofzmksmwclceodrgzy.txt");
             if(file4.exists()) file4.delete();
             String ivfeatpbiruullhtjecagzu = "soRGEHOqJsojd5KADSfNkITwBxqj0wSzPKx83X1ss2udq0nqOFBSSszFArkMNIe8NAAXANLC9De6gkYOxbiCXtjVE8Fb8YeoXutq9ZmRm11fbbL9P0l2lV5Wygk7rEhOJeVx";
             ivfeatpbiruullhtjecagzu.substring(3);
             ivfeatpbiruullhtjecagzu.replace('a', 'y');
             SQLiteOpenHelper agepuqtyvaafonrv = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "uhmsdchbobiwxqicvqtr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "iwemkfnljlzdfrpg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ffoudstgnuajzfhnuagv = agepuqtyvaafonrv.getWritableDatabase();
             try {
                 ffoudstgnuajzfhnuagv.execSQL("INSERT INTO iwemkfnljlzdfrpg (name, value) VALUES (?, ?)", new String[]{"zkzswwqgvhzsjqivwdttvix", "gtlzyfvkdbifqyrlhfublm"});
             } finally {
                 ffoudstgnuajzfhnuagv.close();
                 agepuqtyvaafonrv.close();
             }
             Date lfwqzuqjlybfjmalbqbsgm = new Date();
             String zvefdiwdudimilhmivxozm = String.valueOf(lfwqzuqjlybfjmalbqbsgm.getTime());
             System.currentTimeMillis();
             Intent lnhbacoyueqqtlpdpgrrjai = new Intent();
             lnhbacoyueqqtlpdpgrrjai.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.OipipefaqpwytdosiActivity");
             startActivity(lnhbacoyueqqtlpdpgrrjai);
         }
     });
     final View hdxcxzlvhmarawjvwbyniqvy = findViewById(R.id.qbkbcncaxumohbwbws);
     hdxcxzlvhmarawjvwbyniqvy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler gcjweaxszmdtquub = new Handler(Looper.getMainLooper());
             gcjweaxszmdtquub.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     hdxcxzlvhmarawjvwbyniqvy.setVisibility(View.VISIBLE);
                 }
             }, 19);
             Intent efxeffosmerfcyvykqpb = new Intent();
             efxeffosmerfcyvykqpb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NhzsihxzgfiiatmwActivity");
             startActivity(efxeffosmerfcyvykqpb);
             SQLiteOpenHelper boifecfnuloqbapvrcsded = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "rzhnnkbzjvqojcfjwyiiov.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gknvuiwgqfjkdufqzym"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase akjvjzhkarmctnxdxxgbpgz = boifecfnuloqbapvrcsded.getWritableDatabase();
             try {
                 akjvjzhkarmctnxdxxgbpgz.execSQL("UPDATE gknvuiwgqfjkdufqzym SET value = ? WHERE name = ?", new String[]{"elevhkpomzlwbkomietdwse", "odjkglpuoqekivmyfmntwra"});
             } finally {
                 akjvjzhkarmctnxdxxgbpgz.close();
                 boifecfnuloqbapvrcsded.close();
             }
             Intent xzxobkunokbxbltpudfmip = new Intent();
             xzxobkunokbxbltpudfmip.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NhzsihxzgfiiatmwActivity");
             startActivity(xzxobkunokbxbltpudfmip);
         }
     });
     final View zjhrccwjwvqhmwnidzzeauxf = findViewById(R.id.kjfekaciroorcuwanqe);
     zjhrccwjwvqhmwnidzzeauxf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(JmhbbbfheoxxwvotpssjcvbActivity.this, "cvsuwjbbrdtyhbynixmlz", Toast.LENGTH_SHORT).show();
                 }
             });
             Date trnmxyzihysqifhptwryjftl = new Date();
             String awcgcgcxapicoyxcfjxytbz = String.valueOf(trnmxyzihysqifhptwryjftl.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper rohkhyjatdnruzxsiwknpipsd = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "jrbwtnucldnauxlqojqn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "bsxphsdviuznjkzyqhkg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase huufbgdnhrdzwbmrnx = rohkhyjatdnruzxsiwknpipsd.getWritableDatabase();
             try {
                 huufbgdnhrdzwbmrnx.execSQL("DELETE FROM bsxphsdviuznjkzyqhkg WHERE name = ?", new String[]{"bqfgaqcyanrcvhhgzjvsmzp", "evmqpelhgjjfwsgn"});
             } finally {
                 huufbgdnhrdzwbmrnx.close();
                 rohkhyjatdnruzxsiwknpipsd.close();
             }
             ScheduledExecutorService vqmgpicdkkhqzee = Executors.newScheduledThreadPool(3);
             vqmgpicdkkhqzee.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(24);
                 }
             });
             vqmgpicdkkhqzee.shutdown();
             zjhrccwjwvqhmwnidzzeauxf.animate()
                     .alpha(0.6443516f)
                     .setDuration(449)
                     .start();
         }
     });
     final View vgxhimowccrfhpqgrbbmgvlnf = findViewById(R.id.liobkpswzrifothsmqjlq);
     vgxhimowccrfhpqgrbbmgvlnf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent cixgqokatjkdkwudgyziz = new Intent();
             cixgqokatjkdkwudgyziz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.TvgnyulnnmubziyaghcmActivity");
             startActivity(cixgqokatjkdkwudgyziz);
             File file1 = new File(getFilesDir(), "lifuxruszwysvawrnuybl.txt");
             FileReader yuzgtleuxwjpxfuyxtoqkl = null;
             try {
                 if(file1.exists()) {
                     yuzgtleuxwjpxfuyxtoqkl = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (yuzgtleuxwjpxfuyxtoqkl != null) {
                     try {
                         yuzgtleuxwjpxfuyxtoqkl.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService twtbtzikfryaxwubto = Executors.newScheduledThreadPool(3);
             twtbtzikfryaxwubto.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(58);
                 }
             });
             twtbtzikfryaxwubto.shutdown();
             SQLiteOpenHelper yaztgjsimxkslzdtxvnfbns = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "jxaimmvxyxindgkscrstgcpn.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xbjpnqurfjtdtdurpjjuuhwx"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dgjsnpqgajhtignudtoqvbcu = yaztgjsimxkslzdtxvnfbns.getWritableDatabase();
             try {
                 dgjsnpqgajhtignudtoqvbcu.execSQL("SELECT * FROM xbjpnqurfjtdtdurpjjuuhwx WHERE name = ?", new String[]{"xphsdjkvwwqmoetssqezys", "vanyuuezipvttgixqymqh"});
             } finally {
                 dgjsnpqgajhtignudtoqvbcu.close();
                 yaztgjsimxkslzdtxvnfbns.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(JmhbbbfheoxxwvotpssjcvbActivity.this, "lhmzdfmeiilvgpkonguv", Toast.LENGTH_SHORT).show();
                 }
             });
             File file5 = new File(getFilesDir(), "jlwdxaqgvyewtzyhj.txt");
             FileWriter zjsvqklipurykcgjlszvw = null;
             try {
                 zjsvqklipurykcgjlszvw = new FileWriter(file5);
                 zjsvqklipurykcgjlszvw.write("BOIvq5gR6G42DMwWEWoDY7LHX5XIPXepNIO842BXW2swhRkwmeCfUoIx36dFWA4KUEuMF71OBEL4oCeYAt2bWXZys6PaKInDcVGXMVAipKAlcb3PcZLPJRajO56JXcDnjV7aQWKs4flpivlKF59GTG7H07UAHEb3azBTDKtNr");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zjsvqklipurykcgjlszvw != null) {
                     try {
                         zjsvqklipurykcgjlszvw.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent csuladgunewqodollcalnlaa = new Intent();
             csuladgunewqodollcalnlaa.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.TvgnyulnnmubziyaghcmActivity");
             startActivity(csuladgunewqodollcalnlaa);
             SQLiteOpenHelper jiallhlhadwmgrdyuo = new SQLiteOpenHelper(JmhbbbfheoxxwvotpssjcvbActivity.this, "fjynpraufdbbdpyg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "piynxuqytprjxiaap"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase evzskleclbkibxdfm = jiallhlhadwmgrdyuo.getWritableDatabase();
             try {
                 evzskleclbkibxdfm.execSQL("UPDATE piynxuqytprjxiaap SET value = ? WHERE name = ?", new String[]{"gkrjtpzoufhfxejrzdpake", "wumfpabngnjzfqw"});
             } finally {
                 evzskleclbkibxdfm.close();
                 jiallhlhadwmgrdyuo.close();
             }
         }
     });

hxftwayjljvqfdxr();
Dwgjkgdhuacxkrtay   tdizlrtkaztssmfsyhlkydcf =     new Dwgjkgdhuacxkrtay();
tdizlrtkaztssmfsyhlkydcf.smwxicswmjycyojcguuakfw ="m5ARdrF6i7xOCK9EfaEVVGmKjNSvlckOa6qbiYU7tNz81KagGTMcnMUaWtMv2ee749MYYhhqsMtOS4UNJSyGfdQnJpEIodndfrA58XAHQSXj2i5tsdLUdHGnnrdiAdOGM1z5WZbC1EF4xTlceB84V1s2OH0MwZ";
tdizlrtkaztssmfsyhlkydcf.uvfuasadpbimcpldanxrbht ="6uLcx8Xqj6bKMNxoEKaerpDXq9AoMV5eHpz06QrEm8llmJKJerM3Wx5pDqWupP0YsOmp2soTTsWb5Nnf0Wlngoox7jMI1bys7KpET9M68dnU47Ppj9Cjv9a1A6hSJG";
tdizlrtkaztssmfsyhlkydcf.qomhsdvyhogsbkvtpdugwiey ="pxijcuJddP7Rv0x4vBdfSbEAjkVrjq2";
tdizlrtkaztssmfsyhlkydcf.szrjkadciqrcvefyhpfdyq ="fW3wvyyCMkjITpBajFl6JzmmV2xMoj9ycvsgSyc92L";
 Toast.makeText(JmhbbbfheoxxwvotpssjcvbActivity.this,getString(R.string.kxqhzmmoabhcebyz),Toast.LENGTH_SHORT).show();
    }
 void hxftwayjljvqfdxr() {
     long yjynlavoqadmtvbhzrledix = System.currentTimeMillis();
     Date date = new Date(yjynlavoqadmtvbhzrledix);
     String time = date.toString();
     qunslpqbaaewfbq();
}
 void qunslpqbaaewfbq() {
     long tcegdrbkfjghbnm = System.currentTimeMillis();
     Date date = new Date(tcegdrbkfjghbnm);
     String time = date.toString();
     hgpcxjzctrximxjrtdhdehu();
}
 void hgpcxjzctrximxjrtdhdehu() {
     int kjdmzwakizkhltv = 615;
     double result = Math.sqrt(kjdmzwakizkhltv);
     result = Math.pow(result, 2);
     abnkvhwllwmberd();
}
 void abnkvhwllwmberd() {
     int zwabchghgyjolqdbtfam = 889;
     double result = Math.sqrt(zwabchghgyjolqdbtfam);
     result = Math.pow(result, 2);
     cosmiozhxknkrjtylzlcb();
}
 void cosmiozhxknkrjtylzlcb() {
     SeekBar tkszmphnkpdqicnrjsht = new SeekBar(JmhbbbfheoxxwvotpssjcvbActivity.this);
     tkszmphnkpdqicnrjsht.setVisibility(View.VISIBLE);
     tkszmphnkpdqicnrjsht.setEnabled(false);
}}