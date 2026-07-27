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
import android.widget.ProgressBar;
import android.view.View;
import android.widget.TextView;
import java.lang.System;
import android.widget.Toast;
import java.util.Date;

public class HmfoluupiarfmohojuacrbjActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mfoluupiarfmohojuacrbj);
     final View zolvswtartfvivliphnei = findViewById(R.id.aewlzofdjazbeesiia);
     zolvswtartfvivliphnei.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ioayqnrrloqpifsvb = new Intent();
             ioayqnrrloqpifsvb.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.LmgksyjzgnrodwodslsActivity");
             startActivity(ioayqnrrloqpifsvb);
             Date yaqhswdedhyxbrlvljqbc = new Date();
             String ofdjusfvzetmrgqmswe = String.valueOf(yaqhswdedhyxbrlvljqbc.getTime());
             System.currentTimeMillis();
             Handler zhgmtrycbihjwbnnldjtrhxk = new Handler(Looper.getMainLooper());
             zhgmtrycbihjwbnnldjtrhxk.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     zolvswtartfvivliphnei.setVisibility(View.VISIBLE);
                 }
             }, 95);
             Date icratwwxvwhnwyoqzvqjp = new Date();
             String yixvkblewepdnvgueqsh = String.valueOf(icratwwxvwhnwyoqzvqjp.getTime());
             System.currentTimeMillis();
             File file4 = new File(getFilesDir(), "irywsnzavgumucqaf.txt");
             ScheduledExecutorService vbyvlbofwabqzlkrspy = Executors.newScheduledThreadPool(1);
             vbyvlbofwabqzlkrspy.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(7);
                 }
             });
             vbyvlbofwabqzlkrspy.shutdown();
         }
     });
     final View auvhkwwwmeornqocpbzmyc = findViewById(R.id.kwahzunbpwrpfuz);
     auvhkwwwmeornqocpbzmyc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper onyvkwiqyfbwalaphud = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "dflmizpyawrcgkudlbme.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wusvwggrjviunpwutyaidc"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vbjytxnhepyknaulvgl = onyvkwiqyfbwalaphud.getWritableDatabase();
             try {
                 vbjytxnhepyknaulvgl.execSQL("INSERT INTO wusvwggrjviunpwutyaidc (name, value) VALUES (?, ?)", new String[]{"imaaxmnaixkrnaxfkvadknu", "zljriwntocgocdi"});
             } finally {
                 vbjytxnhepyknaulvgl.close();
                 onyvkwiqyfbwalaphud.close();
             }
             ScheduledExecutorService nulbjxuiglwijpo = Executors.newScheduledThreadPool(4);
             nulbjxuiglwijpo.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(20);
                 }
             });
             nulbjxuiglwijpo.shutdown();
             File file2 = new File(getFilesDir(), "jviqmjvebfykytiakadbdwb.txt");
             if(file2.exists()) file2.delete();
             SQLiteOpenHelper fiywvbaudmhcbffkheuaao = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "mpbdjetvtfyepviavdjtwq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "eahmnnvhwaaqjnneuvwinmlw"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hubfxmdfivjsqqrthiy = fiywvbaudmhcbffkheuaao.getWritableDatabase();
             try {
                 hubfxmdfivjsqqrthiy.execSQL("INSERT INTO eahmnnvhwaaqjnneuvwinmlw (name, value) VALUES (?, ?)", new String[]{"syqybumtqjivmryqevhw", "hbtjfjoqgnovaqqvxukqrxmzn"});
             } finally {
                 hubfxmdfivjsqqrthiy.close();
                 fiywvbaudmhcbffkheuaao.close();
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "wrzghyjxlyszyhxpinbupl", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper pvejkhpthwcyzmgseocxrqwo = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "vlkvwirzpeqkvepkjo.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "oadywfzdtllsvhrgcz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase oquojiolxgemwgmdw = pvejkhpthwcyzmgseocxrqwo.getWritableDatabase();
             try {
                 oquojiolxgemwgmdw.execSQL("UPDATE oadywfzdtllsvhrgcz SET value = ? WHERE name = ?", new String[]{"esbhlhilgztkykcodyssyo", "knxkohujubrpexzqkgp"});
             } finally {
                 oquojiolxgemwgmdw.close();
                 pvejkhpthwcyzmgseocxrqwo.close();
             }
             Intent zgwoworywdjxksax = new Intent();
             zgwoworywdjxksax.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.AssyxfdyjewbimtjtxkActivity");
             startActivity(zgwoworywdjxksax);
             File file7 = new File(getFilesDir(), "awslebvwsuaulnybnbeqev.txt");
             FileReader qoiqkeasieslmznsrwwmr = null;
             try {
                 if(file7.exists()) {
                     qoiqkeasieslmznsrwwmr = new FileReader(file7);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (qoiqkeasieslmznsrwwmr != null) {
                     try {
                         qoiqkeasieslmznsrwwmr.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View plvaudfirvajcakteeaspiveu = findViewById(R.id.salweslgftlqndhynojiu);
     plvaudfirvajcakteeaspiveu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent cpwhigjokedwingoggmfxans = new Intent();
             cpwhigjokedwingoggmfxans.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.PxzulxfyhebfiquvunActivity");
             startActivity(cpwhigjokedwingoggmfxans);
             File file1 = new File(getFilesDir(), "wqntwcqrcjeevxqdvwsgthwlx.txt");
             ScheduledExecutorService dtfuxzlrqjamtuhzucbrwvbui = Executors.newScheduledThreadPool(4);
             dtfuxzlrqjamtuhzucbrwvbui.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(64);
                 }
             });
             dtfuxzlrqjamtuhzucbrwvbui.shutdown();
             plvaudfirvajcakteeaspiveu.animate()
                     .translationX(126f)
                     .translationY(112f)
                     .setDuration(325)
                     .start();
             String xkrnndjvwvraorjachl = "UMTjcd1m8En3kxacIlhaNKZqC6QZG6EzU7TShUJwdiNPNx";
             xkrnndjvwvraorjachl.substring(3);
             xkrnndjvwvraorjachl.replace('x', 'j');
             SQLiteOpenHelper pfwfcwvpbuymyvxo = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "ydyzygxezmfxlxmjtlpafnst.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xhffryexpugxkmdjrlmuwzsim"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase fmmvwbomqadlfnzoflqwteji = pfwfcwvpbuymyvxo.getWritableDatabase();
             try {
                 fmmvwbomqadlfnzoflqwteji.execSQL("INSERT INTO xhffryexpugxkmdjrlmuwzsim (name, value) VALUES (?, ?)", new String[]{"evhypjhbpbhswmsoog", "wsojzuvzrtdcuupumrsmjvb"});
             } finally {
                 fmmvwbomqadlfnzoflqwteji.close();
                 pfwfcwvpbuymyvxo.close();
             }
             String xsxnwkvklyeeoifzlclisx = "iMGc51BafvWwopRowJZ9wKwXw0Qz62AZg5CbZXBshjXf7hxs2hTZ2uVxaJ1tYrhd0ewJQMUQjkhttmo7J5wgjoeFksZYqGvs8gBPecewcDzmyJgywl2pn8BBb3xnbVh2zrggJLzKlaCHUtxb5gdya7i7XR69hCHPdFNx9AA4zPMCxeWuqpZGJYq";
             xsxnwkvklyeeoifzlclisx.substring(4);
             xsxnwkvklyeeoifzlclisx.replace('u', 'n');
             File file7 = new File(getFilesDir(), "upibhfpjrqpvtzoktcpegfu.txt");
             FileReader kzxiniwhqleqehl = null;
             try {
                 if(file7.exists()) {
                     kzxiniwhqleqehl = new FileReader(file7);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (kzxiniwhqleqehl != null) {
                     try {
                         kzxiniwhqleqehl.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View pcaalunsiipzumqmgvgwkv = findViewById(R.id.vwilalwmxlpewewirvt);
     pcaalunsiipzumqmgvgwkv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper dixppuplqywweziwafsxkxepy = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "zatpzspfbcqqoeeskqccsq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "umhhcenwefzrfkus"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase xomlorjfdcegaxanbzcjr = dixppuplqywweziwafsxkxepy.getWritableDatabase();
             try {
                 xomlorjfdcegaxanbzcjr.execSQL("SELECT * FROM umhhcenwefzrfkus WHERE name = ?", new String[]{"zumjvcnkwlfbrrxpqavupkgqj", "fcyaybpxlhvsrbmuz"});
             } finally {
                 xomlorjfdcegaxanbzcjr.close();
                 dixppuplqywweziwafsxkxepy.close();
             }
             ScheduledExecutorService jftraekpagtfxkeb = Executors.newScheduledThreadPool(3);
             jftraekpagtfxkeb.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(52);
                 }
             });
             jftraekpagtfxkeb.shutdown();
             pcaalunsiipzumqmgvgwkv.animate()
                     .rotation(90f)
                     .setDuration(401)
                     .start();
             Intent yeyqxokavlvneqhgkzxlx = new Intent();
             yeyqxokavlvneqhgkzxlx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.TxzqcupiqzjnljdhniqActivity");
             startActivity(yeyqxokavlvneqhgkzxlx);
             SQLiteOpenHelper julyauilpcbzlijwedlduto = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "folgcyqwbozlrpyon.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "jkpdgsgnxmjgmmqbo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wobpelwitucvzwkkewtxtsjps = julyauilpcbzlijwedlduto.getWritableDatabase();
             try {
                 wobpelwitucvzwkkewtxtsjps.execSQL("SELECT * FROM jkpdgsgnxmjgmmqbo WHERE name = ?", new String[]{"bmgwyqldstxsgzeehfy", "zrcyykbrtjehuadsgqr"});
             } finally {
                 wobpelwitucvzwkkewtxtsjps.close();
                 julyauilpcbzlijwedlduto.close();
             }
         }
     });
     final View fgmekcxvpytkqybuaayvm = findViewById(R.id.zyjzhsktzqukxfgps);
     fgmekcxvpytkqybuaayvm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "zljrpoigqtrfehtkyjug.txt");
             Intent reoorozvyjtvdtnhjga = new Intent();
             reoorozvyjtvdtnhjga.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.AkfyzcrzrlpwipwvtgkActivity");
             startActivity(reoorozvyjtvdtnhjga);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "iodugtgdybfvrrorieisvptd", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper mxpvqauwqkylfywxygnhrxjei = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "kigakffmcbyeylbyuhpomijr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "huzbzkytniuzefeliitk"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase sjsjokkwygykppep = mxpvqauwqkylfywxygnhrxjei.getWritableDatabase();
             try {
                 sjsjokkwygykppep.execSQL("INSERT INTO huzbzkytniuzefeliitk (name, value) VALUES (?, ?)", new String[]{"cmbkfcchlcvwpourgxwuzn", "lotqastrrbbiwlymlswox"});
             } finally {
                 sjsjokkwygykppep.close();
                 mxpvqauwqkylfywxygnhrxjei.close();
             }
             File file4 = new File(getFilesDir(), "yvafidvwkpqydrokqfxyimi.txt");
             if(file4.exists()) file4.delete();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "kyuqibwuffxmyaopywhtjg", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View yurjcaslitdhlso = findViewById(R.id.fmirwgsebgylrmtjj);
     yurjcaslitdhlso.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper srhzfhiolesbskin = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "hmhqogptujpbtnqbqjlfijh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fczazkfcldxobgctxqugqm"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ayckaemwzfjtubr = srhzfhiolesbskin.getWritableDatabase();
             try {
                 ayckaemwzfjtubr.execSQL("INSERT INTO fczazkfcldxobgctxqugqm (name, value) VALUES (?, ?)", new String[]{"euqnxkpiwizpnuu", "talverbfwomngxqbidkl"});
             } finally {
                 ayckaemwzfjtubr.close();
                 srhzfhiolesbskin.close();
             }
             File file1 = new File(getFilesDir(), "qteqrztxckfehrs.txt");
             FileReader xzretdxjvaetxrkrslvo = null;
             try {
                 if(file1.exists()) {
                     xzretdxjvaetxrkrslvo = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (xzretdxjvaetxrkrslvo != null) {
                     try {
                         xzretdxjvaetxrkrslvo.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int gvjdwqvgrvxfqzcwmb = 790;
             Math.max(gvjdwqvgrvxfqzcwmb, 492);
             String.valueOf(gvjdwqvgrvxfqzcwmb);
             SQLiteOpenHelper eckbfujbqgzomxwnprmhe = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "szxejecnfrxovgpp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "smbfajtghrnoikrxlrvobl"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase euvbzllwjyijdtsyuxthkhgsa = eckbfujbqgzomxwnprmhe.getWritableDatabase();
             try {
                 euvbzllwjyijdtsyuxthkhgsa.execSQL("DELETE FROM smbfajtghrnoikrxlrvobl WHERE name = ?", new String[]{"izkvapvcxprsaija", "wmsjcfakhbllurbjoxaabwqf"});
             } finally {
                 euvbzllwjyijdtsyuxthkhgsa.close();
                 eckbfujbqgzomxwnprmhe.close();
             }
             int ytwjhxfqhadhvwcmgfkryeivi = 340;
             Math.max(ytwjhxfqhadhvwcmgfkryeivi, 374);
             String.valueOf(ytwjhxfqhadhvwcmgfkryeivi);
             File file5 = new File(getFilesDir(), "vmpkwsfczslnzyatkoewn.txt");
             FileWriter wmvwwhltwzkopqgdqfv = null;
             try {
                 wmvwwhltwzkopqgdqfv = new FileWriter(file5);
                 wmvwwhltwzkopqgdqfv.write("0SnYCv0IOI1TeS4QZO46TxLr1bwWtPjMIJ7YzT1RQWrJbSHej94PQJBWBZAAJh0mmQskit6AHV5eaY");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (wmvwwhltwzkopqgdqfv != null) {
                     try {
                         wmvwwhltwzkopqgdqfv.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent bnrahvpwrjrpvthoxdclgbcs = new Intent();
             bnrahvpwrjrpvthoxdclgbcs.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.ObjdbunhfharobcpplmetActivity");
             startActivity(bnrahvpwrjrpvthoxdclgbcs);
         }
     });
     final View drvoozupeqraqmbs = findViewById(R.id.fyasfullwjbhahvx);
     drvoozupeqraqmbs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService pnirdoetayqwsvypryxwr = Executors.newScheduledThreadPool(3);
             pnirdoetayqwsvypryxwr.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(35);
                 }
             });
             pnirdoetayqwsvypryxwr.shutdown();
             File file1 = new File(getFilesDir(), "nzfwazlvjlpqzlrglecff.txt");
             FileWriter hitumlkmpuqkrsa = null;
             try {
                 hitumlkmpuqkrsa = new FileWriter(file1);
                 hitumlkmpuqkrsa.write("fZcVqVwrZN5PdJWFBejXQ3jzLBS");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (hitumlkmpuqkrsa != null) {
                     try {
                         hitumlkmpuqkrsa.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             drvoozupeqraqmbs.animate()
                     .scaleX(1.8319798f)
                     .scaleY(1.8319798f)
                     .setDuration(689)
                     .start();
         }
     });
     final View mmwvqqibxwxialep = findViewById(R.id.riyokfjyswptinvduhnco);
     mmwvqqibxwxialep.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "qbxbzxwmgztezjtudmldleflq.txt");
             FileWriter llqwgrnqgyhvroydddznnvvjo = null;
             try {
                 llqwgrnqgyhvroydddznnvvjo = new FileWriter(file0);
                 llqwgrnqgyhvroydddznnvvjo.write("jkwVvUpg3kXMdnE99z8R3DHoc3iTVyq1USroUAsWWHWrsR8gtPeT1oIrnNm5x0n5k9hJ3jhoL22ub4JIvpiEtEGdmUNRqN421Y11rqGullxJdSZAtpa13poPL3gxSTeLuLrEabqRB6N14aPGxW44NP8P7KF");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (llqwgrnqgyhvroydddznnvvjo != null) {
                     try {
                         llqwgrnqgyhvroydddznnvvjo.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "jdudmkmxvujbfgganx", Toast.LENGTH_SHORT).show();
                 }
             });
             Intent jnswpsrgpgbxblrcczhk = new Intent();
             jnswpsrgpgbxblrcczhk.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.JdqrgplanjuxbsexdtobmiActivity");
             startActivity(jnswpsrgpgbxblrcczhk);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "imywchoebiiyndwtkpl", Toast.LENGTH_SHORT).show();
                 }
             });
             File file4 = new File(getFilesDir(), "ogffwffotpthllbx.txt");
             FileWriter qlgdxxlvouaviix = null;
             try {
                 qlgdxxlvouaviix = new FileWriter(file4);
                 qlgdxxlvouaviix.write("1TlNzg1SeccHvvesFttP3QT9zJlKqeOkUw2jaO5CQdYGKgI5ZccusaNi26r");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (qlgdxxlvouaviix != null) {
                     try {
                         qlgdxxlvouaviix.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             mmwvqqibxwxialep.animate()
                     .translationX(171f)
                     .translationY(90f)
                     .setDuration(987)
                     .start();
             File file6 = new File(getFilesDir(), "unzkrebwbrmiwtsresgpz.txt");
             FileWriter vbakxbgutlvxgxlpvfu = null;
             try {
                 vbakxbgutlvxgxlpvfu = new FileWriter(file6);
                 vbakxbgutlvxgxlpvfu.write("zSd9OUl7P84TpmNKVu8ZLR7yNfbCkFfHovEv5MNcR5YHzhazmRzXJdJANVCeyNmXGPRGZs5i5oObkqxBnUmzyl90OpMRtrDsr4DL6njEUBElGMhgu9KJNlYsb3o8bOLrBsIz0UVJwDVOj");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (vbakxbgutlvxgxlpvfu != null) {
                     try {
                         vbakxbgutlvxgxlpvfu.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             mmwvqqibxwxialep.animate()
                     .scaleX(0.39739966f)
                     .scaleY(0.39739966f)
                     .setDuration(549)
                     .start();
             Intent xwaggajvevhyvejxijqrwz = new Intent();
             xwaggajvevhyvejxijqrwz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.JdqrgplanjuxbsexdtobmiActivity");
             startActivity(xwaggajvevhyvejxijqrwz);
             mmwvqqibxwxialep.animate()
                     .translationX(113f)
                     .translationY(112f)
                     .setDuration(606)
                     .start();
         }
     });
     final View nsnlwyaxglrplqxewybegzexj = findViewById(R.id.fogsnuhqcjjexebjgecqdbkxl);
     nsnlwyaxglrplqxewybegzexj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler qwiukixfsynxnwhlgsrmgadzh = new Handler(Looper.getMainLooper());
             qwiukixfsynxnwhlgsrmgadzh.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     nsnlwyaxglrplqxewybegzexj.setVisibility(View.VISIBLE);
                 }
             }, 335);
             Intent ryssqapnazvqqdebolu = new Intent();
             ryssqapnazvqqdebolu.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.OyrvjpeqcpbskwjemwtwpzekovActivity");
             startActivity(ryssqapnazvqqdebolu);
             String osyfgnkgzzndhmalykss = "KvHZJ62ve5o27z4kV6lL5RfSzkIS9prGqlUq7U7U6GGtveg0Mjo8EpSafqYrFmI7c6eE33ZmxsiCg6LX3tNpoVExCzkEJ9bgmM5ZoxDCs2mpbbboDWXCL";
             osyfgnkgzzndhmalykss.substring(3);
             osyfgnkgzzndhmalykss.replace('t', 'a');
             File file3 = new File(getFilesDir(), "jfgaowfwwqyxpfjtuja.txt");
             nsnlwyaxglrplqxewybegzexj.animate()
                     .rotation(214f)
                     .setDuration(909)
                     .start();
             File file5 = new File(getFilesDir(), "odnlagapdwltjtilg.txt");
             FileReader gfrpiydcjvxgzprk = null;
             try {
                 if(file5.exists()) {
                     gfrpiydcjvxgzprk = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (gfrpiydcjvxgzprk != null) {
                     try {
                         gfrpiydcjvxgzprk.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             int agczqpnrwtraxdqckame = 505;
             Math.max(agczqpnrwtraxdqckame, 637);
             String.valueOf(agczqpnrwtraxdqckame);
             SQLiteOpenHelper ocpfvcozaoqwxrrqgq = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "uifnhuwfbipzrrkkroqiasfjy.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "dgycjhlaltoantdjnupdktna"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase awlwwrlggkioknvgmmgy = ocpfvcozaoqwxrrqgq.getWritableDatabase();
             try {
                 awlwwrlggkioknvgmmgy.execSQL("INSERT INTO dgycjhlaltoantdjnupdktna (name, value) VALUES (?, ?)", new String[]{"lhkfdrxgzhsqsijcvokxvqzvp", "abdaknekdbtipgde"});
             } finally {
                 awlwwrlggkioknvgmmgy.close();
                 ocpfvcozaoqwxrrqgq.close();
             }
         }
     });
     final View krinjmushoahhojaha = findViewById(R.id.dtlmxthinukluybqnvtnwz);
     krinjmushoahhojaha.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Handler hpvxdqnmffhwwfhiypbaz = new Handler(Looper.getMainLooper());
             hpvxdqnmffhwwfhiypbaz.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     krinjmushoahhojaha.setVisibility(View.VISIBLE);
                 }
             }, 604);
             int pqyqdhjjcyipepwjkikbqvk = 993;
             Math.max(pqyqdhjjcyipepwjkikbqvk, 49);
             String.valueOf(pqyqdhjjcyipepwjkikbqvk);
             krinjmushoahhojaha.animate()
                     .scaleX(0.32299972f)
                     .scaleY(0.32299972f)
                     .setDuration(86)
                     .start();
             Intent polvtwvfwykusoisbfkynnr = new Intent();
             polvtwvfwykusoisbfkynnr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.DjmseerdkgehphgdbhwActivity");
             startActivity(polvtwvfwykusoisbfkynnr);
             Date yhcunzzjrvznyksyeokhgqkjv = new Date();
             String ggizjnvvjohooucrlvyfnpj = String.valueOf(yhcunzzjrvznyksyeokhgqkjv.getTime());
             System.currentTimeMillis();
         }
     });
     final View kdnoepbyzijlekpbh = findViewById(R.id.ahyyfxmaejsltwalgliukcx);
     kdnoepbyzijlekpbh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper xvfkgqkkigolmouzl = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "xuiamfrqolczuuzabvz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "clkjwhqcewfljtel"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase gbmupkxmlpbwjllfoyzdsdou = xvfkgqkkigolmouzl.getWritableDatabase();
             try {
                 gbmupkxmlpbwjllfoyzdsdou.execSQL("DELETE FROM clkjwhqcewfljtel WHERE name = ?", new String[]{"kvaasjjoxpmapdwlz", "xcjqkexteiicmqo"});
             } finally {
                 gbmupkxmlpbwjllfoyzdsdou.close();
                 xvfkgqkkigolmouzl.close();
             }
             Date rvwlecstmugkhpxpcvsxhqwlr = new Date();
             String gkanfusrebsluhxdkezo = String.valueOf(rvwlecstmugkhpxpcvsxhqwlr.getTime());
             System.currentTimeMillis();
             Intent ohetwonuummrdyuxirlnceok = new Intent();
             ohetwonuummrdyuxirlnceok.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.YwbifmkakanibozmivwlymbActivity");
             startActivity(ohetwonuummrdyuxirlnceok);
             kdnoepbyzijlekpbh.animate()
                     .rotation(111f)
                     .setDuration(188)
                     .start();
             File file4 = new File(getFilesDir(), "gfduxizszxkisfynvfmbxljat.txt");
             SQLiteOpenHelper ynkvewezmntqonxzixphiid = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "xxgomkjkozwgubxiihjh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "fcramogiewxypfqbhbkz"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jkdckibljemqlouulb = ynkvewezmntqonxzixphiid.getWritableDatabase();
             try {
                 jkdckibljemqlouulb.execSQL("UPDATE fcramogiewxypfqbhbkz SET value = ? WHERE name = ?", new String[]{"wmztutudewzriicjxgkh", "giackerjechpxywpf"});
             } finally {
                 jkdckibljemqlouulb.close();
                 ynkvewezmntqonxzixphiid.close();
             }
         }
     });
     final View oexgukgcsfbawgbpqwxsqlr = findViewById(R.id.lrknkrwxnfrzsgurnt);
     oexgukgcsfbawgbpqwxsqlr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ehpavwzcotuqgatvawtxruq = new Intent();
             ehpavwzcotuqgatvawtxruq.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.XnlmewaumtycywrvActivity");
             startActivity(ehpavwzcotuqgatvawtxruq);
             Date mishselggizqqas = new Date();
             String uyjcpgmocbtoncxemx = String.valueOf(mishselggizqqas.getTime());
             System.currentTimeMillis();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "kmwppvlsjqhpkbakvrektyt", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper vbexuskmlxjwpmtfyk = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "zueetrxbnpqelann.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cgeycxlgnzayrkzch"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tvnpbwegzliefozscttztzk = vbexuskmlxjwpmtfyk.getWritableDatabase();
             try {
                 tvnpbwegzliefozscttztzk.execSQL("SELECT * FROM cgeycxlgnzayrkzch WHERE name = ?", new String[]{"bmkloyvcktrazdicdmstyzd", "koodojufqzkmfjlgccjuqfq"});
             } finally {
                 tvnpbwegzliefozscttztzk.close();
                 vbexuskmlxjwpmtfyk.close();
             }
             Intent wpplvfxvqlsvcdvjuub = new Intent();
             wpplvfxvqlsvcdvjuub.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.XnlmewaumtycywrvActivity");
             startActivity(wpplvfxvqlsvcdvjuub);
         }
     });
     final View lzcyawocpbitmamhv = findViewById(R.id.xvyilxndagomklwapecj);
     lzcyawocpbitmamhv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper tlbbygyghnuoflk = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "hgabtzknulucldlwj.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "doqusdshjjppmvdlci"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dickyrgkumdgatpeivxecb = tlbbygyghnuoflk.getWritableDatabase();
             try {
                 dickyrgkumdgatpeivxecb.execSQL("INSERT INTO doqusdshjjppmvdlci (name, value) VALUES (?, ?)", new String[]{"bzabmbgfwlfojcemnyyjbgbqk", "asnhpstmbhqqbdcbrtaorvk"});
             } finally {
                 dickyrgkumdgatpeivxecb.close();
                 tlbbygyghnuoflk.close();
             }
             String wqbrijierjocxqyyrlfuzj = "uhjtE5mZvA9XJUOil4rU35ezjE6Gp8xThmp6OsZ9jrOBtqGHmsjXWIo5Q5cGxeVKnPwZq4CQVWeTMKqaGUOlSJkgf0npGruo29pYeua5UiifDe3416Tss9w7BmWy9";
             wqbrijierjocxqyyrlfuzj.substring(4);
             wqbrijierjocxqyyrlfuzj.replace('l', 'h');
             SQLiteOpenHelper wnakxovhlnqmncyphvydwcjza = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "rinzpbjobshzsfhaujbrmhhlg.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gbwrmsxiqfliwgnelpg"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase uwsrmlieeazqhdksajvtr = wnakxovhlnqmncyphvydwcjza.getWritableDatabase();
             try {
                 uwsrmlieeazqhdksajvtr.execSQL("INSERT INTO gbwrmsxiqfliwgnelpg (name, value) VALUES (?, ?)", new String[]{"pjbpxnkaysznipikz", "ydxvxcyssjfvwbrbsrseiya"});
             } finally {
                 uwsrmlieeazqhdksajvtr.close();
                 wnakxovhlnqmncyphvydwcjza.close();
             }
             Intent kkcjhkkkelaoaofacqqmctph = new Intent();
             kkcjhkkkelaoaofacqqmctph.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NfoexcykptwfbekxbhpActivity");
             startActivity(kkcjhkkkelaoaofacqqmctph);
             SQLiteOpenHelper cbrfqaytzabtxbn = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "izwqslavevxnjlpoalara.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "gyyhmdfwisnrhooootq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase wkdaewsrymxcwksqt = cbrfqaytzabtxbn.getWritableDatabase();
             try {
                 wkdaewsrymxcwksqt.execSQL("INSERT INTO gyyhmdfwisnrhooootq (name, value) VALUES (?, ?)", new String[]{"tiyuyojjvhbviqijbicb", "swzbmdpvihihwudin"});
             } finally {
                 wkdaewsrymxcwksqt.close();
                 cbrfqaytzabtxbn.close();
             }
             Intent xpbodowudrojalac = new Intent();
             xpbodowudrojalac.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NfoexcykptwfbekxbhpActivity");
             startActivity(xpbodowudrojalac);
             File file6 = new File(getFilesDir(), "idrlnbqkjtpqtejywjdd.txt");
             if(file6.exists()) file6.delete();
             Intent mfacyaeezanvqsv = new Intent();
             mfacyaeezanvqsv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NfoexcykptwfbekxbhpActivity");
             startActivity(mfacyaeezanvqsv);
         }
     });
     final View njevjsgzfdjbczifhfwkew = findViewById(R.id.dvfxgjmertfgpquufvaevyr);
     njevjsgzfdjbczifhfwkew.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date jsflvjrrezztlrz = new Date();
             String ufoyycgihfmqehzadcfiyp = String.valueOf(jsflvjrrezztlrz.getTime());
             System.currentTimeMillis();
             SQLiteOpenHelper irexaecllnzgufjfhgay = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "soxmemguqwcjxyqfdbhriqnz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "etxqwxxclteevyja"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase deidlgcumdfjsahugsjtu = irexaecllnzgufjfhgay.getWritableDatabase();
             try {
                 deidlgcumdfjsahugsjtu.execSQL("SELECT * FROM etxqwxxclteevyja WHERE name = ?", new String[]{"lqhvcijcplhxusdanhf", "gylafgsdccgortub"});
             } finally {
                 deidlgcumdfjsahugsjtu.close();
                 irexaecllnzgufjfhgay.close();
             }
             Intent znpmxmwgqkxuclgxzzssel = new Intent();
             znpmxmwgqkxuclgxzzssel.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.BimxgirwukytjjhvpsuesfdtufActivity");
             startActivity(znpmxmwgqkxuclgxzzssel);
         }
     });
     final View vsborbmcslftmesf = findViewById(R.id.btcqohwlywdqolmofouiwet);
     vsborbmcslftmesf.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent vajxolqnawzuukc = new Intent();
             vajxolqnawzuukc.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.NmmsrmrzoecjcysfwcActivity");
             startActivity(vajxolqnawzuukc);
             String prwjdpqhlhkvyxubcnm = "sB7d1KyWjjbFVVSZROarL2gBBLInEubJGSp8aaXsjNGMbKD2A6G2FUOZCYgZm5gq1Zjoib5dmueJy2ibI4I3HdCHKy2ynY5";
             prwjdpqhlhkvyxubcnm.substring(3);
             prwjdpqhlhkvyxubcnm.replace('d', 'b');
         }
     });
     final View uetojjhlrgvvobwddw = findViewById(R.id.lkacuwjuqoistpejlbmgna);
     uetojjhlrgvvobwddw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String frknzvmebkelwbsmr = "hrCewwMGqYmmUTAtpMEGoQt4G63lbysgVgtV1NHOHqkbJXzFN4eW9YLey74VvIUzHZbazQdAvnZnDe891bdItguA";
             frknzvmebkelwbsmr.substring(2);
             frknzvmebkelwbsmr.replace('u', 'f');
             ScheduledExecutorService krjygyybgcnynitjyxdh = Executors.newScheduledThreadPool(3);
             krjygyybgcnynitjyxdh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(93);
                 }
             });
             krjygyybgcnynitjyxdh.shutdown();
             Intent aetkbqmbtofegwvtsjrcr = new Intent();
             aetkbqmbtofegwvtsjrcr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.HmlaatoxtfarwfnmrkActivity");
             startActivity(aetkbqmbtofegwvtsjrcr);
         }
     });
     final View ivzlnwomqhagvvpstkapvg = findViewById(R.id.dzehysxpttejkjxg);
     ivzlnwomqhagvvpstkapvg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date rystyozwefirjhiuuccbsp = new Date();
             String ckxtfzgagoinsrxpq = String.valueOf(rystyozwefirjhiuuccbsp.getTime());
             System.currentTimeMillis();
             Intent itvdhlyyhbqdbytnwykquqge = new Intent();
             itvdhlyyhbqdbytnwykquqge.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.LyiitfcuiiigbblhhxlkrcActivity");
             startActivity(itvdhlyyhbqdbytnwykquqge);
             Date bmdhzyzdbzurxwett = new Date();
             String nuyvfkmaxqipgxzbcm = String.valueOf(bmdhzyzdbzurxwett.getTime());
             System.currentTimeMillis();
             Intent enejyuoaiozgkydbhr = new Intent();
             enejyuoaiozgkydbhr.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.phykhsqghwxbcmuabd.LyiitfcuiiigbblhhxlkrcActivity");
             startActivity(enejyuoaiozgkydbhr);
             SQLiteOpenHelper rtndjkjwqfslyrjt = new SQLiteOpenHelper(HmfoluupiarfmohojuacrbjActivity.this, "beurifxbhlnvevb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "lzzvqrgrfmquokrbd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase punhnetbuklrgavnkamnv = rtndjkjwqfslyrjt.getWritableDatabase();
             try {
                 punhnetbuklrgavnkamnv.execSQL("SELECT * FROM lzzvqrgrfmquokrbd WHERE name = ?", new String[]{"mlqmvlzwtfhmiql", "vnxxssacewccphix"});
             } finally {
                 punhnetbuklrgavnkamnv.close();
                 rtndjkjwqfslyrjt.close();
             }
             File file5 = new File(getFilesDir(), "dhldmldvvspoplawk.txt");
             int txlrnsgasmpfityerm = 290;
             Math.max(txlrnsgasmpfityerm, 179);
             String.valueOf(txlrnsgasmpfityerm);
             File file7 = new File(getFilesDir(), "nujklxqslafmgwzrhijqqdxyd.txt");
             if(file7.exists()) file7.delete();
         }
     });
     final View sulvwyixjyparup = findViewById(R.id.tuiaicdbpnztnrdbxefjhhska);
     sulvwyixjyparup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "lespkgfrcdemccpxq", Toast.LENGTH_SHORT).show();
                 }
             });
             String gzjxmdmzqsbidpwvvfaweu = "4HjBXZ85E9Prkgb1logCAzNFnhxwnwCzKU77aEA9sfMHcxaqSfpefX9z8pfdWCFvByCdeKwvuvRiK7gLgUughhxyh7u";
             gzjxmdmzqsbidpwvvfaweu.substring(3);
             gzjxmdmzqsbidpwvvfaweu.replace('n', 'f');
             sulvwyixjyparup.animate()
                     .rotation(354f)
                     .setDuration(877)
                     .start();
             ScheduledExecutorService jjnsqutjsoatjrqllyh = Executors.newScheduledThreadPool(1);
             jjnsqutjsoatjrqllyh.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(55);
                 }
             });
             jjnsqutjsoatjrqllyh.shutdown();
             File file4 = new File(getFilesDir(), "jsqdjowiyivlwcmtpbkk.txt");
             FileWriter ffhcnpbulmaalrbcjzwrdxfoj = null;
             try {
                 ffhcnpbulmaalrbcjzwrdxfoj = new FileWriter(file4);
                 ffhcnpbulmaalrbcjzwrdxfoj.write("54xcxsCCXK7PbuB8704ms4gp1fNSYskBcNcmPrLW29D2");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ffhcnpbulmaalrbcjzwrdxfoj != null) {
                     try {
                         ffhcnpbulmaalrbcjzwrdxfoj.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Date xedymcghftzacypcapodnzru = new Date();
             String jvetslcrkqbissyi = String.valueOf(xedymcghftzacypcapodnzru.getTime());
             System.currentTimeMillis();
         }
     });
     final View ncobyqwsrxjkdsjhpioefitlm = findViewById(R.id.ulxutfiohjomyeewq);
     ncobyqwsrxjkdsjhpioefitlm.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this, "gsnrjcphwecntumcfgrt", Toast.LENGTH_SHORT).show();
                 }
             });
             File file1 = new File(getFilesDir(), "seuijmfgxpixppbokem.txt");
             if(file1.exists()) file1.delete();
         }
     });

kcqjqzjcwkilrwpmctsvk();
Ygjicmarpwtdpzodnmjupfs   ovfwjpcfzondckzz =     new Ygjicmarpwtdpzodnmjupfs();
ovfwjpcfzondckzz.xstghvbwswcwfhxrrex ="686bO8otYiqVPP8HFw2A9aLJTqcBY7Fr3x8qeALBtqLzJlI2ghrF5";
ovfwjpcfzondckzz.bwabzdmpmjxwvjshym ="mjSq46pkqZNN1Kjq2gAyCjvLtnou5cVCR";
ovfwjpcfzondckzz.dmsbeypakytdacn ="7UQAzy77oqPtLZfLmjVhF1ivqFjjLOFbCZsHIXzrzAML0aFnCnMJCjnJ3hnVilsCmeNpSWLcnIEsMrBTkVwRa5D4AizsyyT4qpXusIMUg1XAyKEaQvVh3LhfK8G6hnnOzwAS2HwopaAmAEebaNvrH9YdFKwXlgXtgm2nSIYTbIH476AHYARo";
ovfwjpcfzondckzz.fjzvxvapoqxajka ="BhbSX1iseV1xdrSMmDUUCDjsQZ1tDlSRwUEd3gpk";
ovfwjpcfzondckzz.duynitzaqgdxnfdg ="81pWKUaX3xUWK3ChnPb";
ovfwjpcfzondckzz.pnznpyndpbbspmcuhmg ="PEz4YUOy7z64RDMAwdTIryHUuflI7gujqWaSbOQ9SNP3YGc8Lafz2IvJa1GvqomF";
ovfwjpcfzondckzz.abixmspaezkjtvsze ="ivZWYJ6inJq8zSFt8v6NcVKWCnlOykoPzk3y7GtPuiZciSUihShkKR1WXom424BDl0AfTSXlvZMv3d8YIP03qRo0ypEr5ewqpSj8TaNLgWell4iAdTVl1j52Dzlf";
ovfwjpcfzondckzz.jauzlgulgrcyifpb ="AvlAoTmYZJbfoGCmyHUCSJgyhPF4U7NAuxdOGyQ7bxhE4ShWWNspFDaaqfGXduwlYjkok9klRs05S2fYsBvaVDDDWXJGKlHpWjCH277Vh2gAbf5818173NDibTKJ5jc9NBLXKI0kt9pb5IS7fvDAEl2RbqRbTmP6tkvaRuTPhbhn4Qele61HjDOvsx";
ovfwjpcfzondckzz.shmqsqvbwmrnewjqkwmaql ="xSaOcOU9KsOF7Xyiza9k7SpPCYlaw728FHVM1hg8N5zPD2k9z3RZbZOUlqUdJktcS8DTG0CNgk13A6dfoStTCE5X8OMstHot";
ovfwjpcfzondckzz.kfpgkzegpvosdrrbq ="U4piqKkIEVQfxx2ERKXrHZDZEIQNgi";
ovfwjpcfzondckzz.npgulxwpdeymjlrwqwjstpwut ="GWbJcSTLZBBbnaG2cJVyniuJkoBHIlEI4u1q0Utcy4vQ68Izkbi6jxPkofPbB2WEZpePZQT4yAr0dz3rXA3uUkEPRzJAxyAqVea3FFEKN1vyscwfD9p";
ovfwjpcfzondckzz.iphoqovalblbwwn ="XgLyuzYqL9TZKKm8xdiZcmNIzdHoMiQNZDJTtyKKiXXEC6BuX0BZpCtZdOca";
ovfwjpcfzondckzz.zymdmhabtuyulgicns ="q57hxEzdoJ1n";
ovfwjpcfzondckzz.cxqwhoqxiverfgekzaf ="CD9sISm4ZSCYLQyVq8pODEygftGIALhCaezbxGxlBcHbDIWTzVz1uOPojRgikB20jp2HMq7x6PFQKkW2xYyqfmUxFY";
ovfwjpcfzondckzz.dccltfmwqtulikpbs ="SJH1oRS8Gcun0WuggCKVqwRM5kLaM63eCNU6cKQv0ozWlf4R72IwnsOF9K4XpbxEEvLqKkapd027invAKjtO7Nnl5GriKQ0dnu3p6zYQiNUZDTfGS3YIpyxZG2n32Rk31w9KLtYhrJ1R3yujsTKn8zMj5Cbxw497FQ9H5M8SX";
ovfwjpcfzondckzz.fwcrxganronpqjkljacj ="AaSg2gQr7tK02Ig5rFAHQEwDpekF2IVo0ZldCfPjGf6S6syGKmZgZS3788Gmv9mRdqgcam9zAhJd0AWXdFjrYu3RvxFxXi3H0u3WqxST3mzTgS51oPoP8nuHDbGdgJAf6R4tvKspmInfBv8CWzWipdxRTYZT77uAfekJH";
ovfwjpcfzondckzz.suuqowzniwesaeaalqgwgc ="massksDOzcOWiI93fDtmyrIdgXIpBernknIIyysBnxWncQwTtl88DdiLC8gVSzlWp2yzvdBQCaFxM";
ovfwjpcfzondckzz.rbkpckjjefcyfujelsuam ="7ni35Y0NXNLTjD7FxmPH3LvaczeICdGwdoS2e49a5DxxkMyeRF6csjiyzi96ab086MMrn9SjgFR8UO4avNfPTOlJMTH7HKT25MvAZ6WoOtXDOVjsGWuAyNu54GhfZyHQVUOvG8fjER3L";
 Toast.makeText(HmfoluupiarfmohojuacrbjActivity.this,getString(R.string.yhwbzvwoyhuuavsbsvetyrzbw),Toast.LENGTH_SHORT).show();
    }
 void kcqjqzjcwkilrwpmctsvk() {
     String hdliimycsyxyzbuttatnomv = "mPwUxRdSJBt1S3QFS0DJ8GTw2zTChW";
     hdliimycsyxyzbuttatnomv = hdliimycsyxyzbuttatnomv.substring(4);
     hdliimycsyxyzbuttatnomv = hdliimycsyxyzbuttatnomv.toUpperCase();
     jldsecefxpqbhzrqinxkem();
}
 void jldsecefxpqbhzrqinxkem() {
     ProgressBar yeoeugtjwjoghhdvcd = new ProgressBar(HmfoluupiarfmohojuacrbjActivity.this);
     yeoeugtjwjoghhdvcd.setVisibility(View.VISIBLE);
     yeoeugtjwjoghhdvcd.setEnabled(false);
     pqtagmbivjluiitv();
}
 void pqtagmbivjluiitv() {
     int lancckadkdswpetou = 841;
     double result = Math.sqrt(lancckadkdswpetou);
     result = Math.pow(result, 2);
     qiipqqiunjvgdcti();
}
 void qiipqqiunjvgdcti() {
     ProgressBar hvtqldmxgzosykhp = new ProgressBar(HmfoluupiarfmohojuacrbjActivity.this);
     hvtqldmxgzosykhp.setVisibility(View.VISIBLE);
     hvtqldmxgzosykhp.setEnabled(false);
}}