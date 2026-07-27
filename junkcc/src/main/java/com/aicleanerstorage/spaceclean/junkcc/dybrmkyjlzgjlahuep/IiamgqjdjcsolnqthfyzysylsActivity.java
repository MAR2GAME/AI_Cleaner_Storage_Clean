package com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep;

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

public class IiamgqjdjcsolnqthfyzysylsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_iamgqjdjcsolnqthfyzysyls);
     final View zggqrbkutbntqvvxb = findViewById(R.id.bwwlgvjfaewmlalmf);
     zggqrbkutbntqvvxb.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent miwibnaweayposfdqd = new Intent();
             miwibnaweayposfdqd.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.SmwafzmbmgwqmvqsetActivity");
             startActivity(miwibnaweayposfdqd);
             File file1 = new File(getFilesDir(), "jtpnsbkvvsoljsoyytggrr.txt");
             FileReader zqwmnamrdbatopgsa = null;
             try {
                 if(file1.exists()) {
                     zqwmnamrdbatopgsa = new FileReader(file1);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zqwmnamrdbatopgsa != null) {
                     try {
                         zqwmnamrdbatopgsa.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View irknlntfjzfkfrzfopddsezwj = findViewById(R.id.tdvrgcbltoepdlpbe);
     irknlntfjzfkfrzfopddsezwj.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Date epvbapuhnldukxyzptez = new Date();
             String exddrqstmirxwzndaiymrqeb = String.valueOf(epvbapuhnldukxyzptez.getTime());
             System.currentTimeMillis();
             File file1 = new File(getFilesDir(), "jqvetavpshoqxbmqedecip.txt");
             if(file1.exists()) file1.delete();
             SQLiteOpenHelper gisytqfjuvwtadd = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "kqanqntvynvjrkkolclwsf.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qweykyfkrtrskmtgaa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase rwkdnczqcbtdnmjc = gisytqfjuvwtadd.getWritableDatabase();
             try {
                 rwkdnczqcbtdnmjc.execSQL("DELETE FROM qweykyfkrtrskmtgaa WHERE name = ?", new String[]{"zibpeyrmlkvnnfubhnqio", "iyrodllljfcvfpiznmjz"});
             } finally {
                 rwkdnczqcbtdnmjc.close();
                 gisytqfjuvwtadd.close();
             }
             Intent qqagzpqwusingugaslz = new Intent();
             qqagzpqwusingugaslz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.GdowdpjqbhggsahynhzzrfbebActivity");
             startActivity(qqagzpqwusingugaslz);
             irknlntfjzfkfrzfopddsezwj.animate()
                     .rotation(39f)
                     .setDuration(406)
                     .start();
             File file5 = new File(getFilesDir(), "pfvxytosufporrjnog.txt");
             FileReader zqxoankcbvwbshuutvjlj = null;
             try {
                 if(file5.exists()) {
                     zqxoankcbvwbshuutvjlj = new FileReader(file5);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (zqxoankcbvwbshuutvjlj != null) {
                     try {
                         zqxoankcbvwbshuutvjlj.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     });
     final View eovyoraentsbzdtyxeh = findViewById(R.id.jsgfkbqfrglpbkxe);
     eovyoraentsbzdtyxeh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ixrdyfdwlshstacadi = new Intent();
             ixrdyfdwlshstacadi.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.YzrevjcckcarexdsvlpqlnsActivity");
             startActivity(ixrdyfdwlshstacadi);
             Date obrntikfegknotvi = new Date();
             String wbtsmodolkfvcrsd = String.valueOf(obrntikfegknotvi.getTime());
             System.currentTimeMillis();
             Handler mlrifdunbtmqkiqicnkqooy = new Handler(Looper.getMainLooper());
             mlrifdunbtmqkiqicnkqooy.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     eovyoraentsbzdtyxeh.setVisibility(View.VISIBLE);
                 }
             }, 293);
             SQLiteOpenHelper iuzlnfykgmbxxwxeolmevdpp = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "skveaxasojemhzhxatzopggcp.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "xphzgafisxoajsxo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yddqobzbvodbbjhes = iuzlnfykgmbxxwxeolmevdpp.getWritableDatabase();
             try {
                 yddqobzbvodbbjhes.execSQL("UPDATE xphzgafisxoajsxo SET value = ? WHERE name = ?", new String[]{"tbjahebgquqlkkhbgz", "xbelalfjjpkbjlqjubi"});
             } finally {
                 yddqobzbvodbbjhes.close();
                 iuzlnfykgmbxxwxeolmevdpp.close();
             }
             eovyoraentsbzdtyxeh.animate()
                     .scaleX(1.6301931f)
                     .scaleY(1.6301931f)
                     .setDuration(13)
                     .start();
         }
     });
     final View evvkgkcwrjeojgsquoeaz = findViewById(R.id.snkxvoisjslpxpzr);
     evvkgkcwrjeojgsquoeaz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int adeicdcttelweajaranbelofn = 212;
             Math.max(adeicdcttelweajaranbelofn, 219);
             String.valueOf(adeicdcttelweajaranbelofn);
             SQLiteOpenHelper ooyregyqgriphcgu = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "ktamvibjylsttapfzzegfdhji.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wgxrsgsdnhbrbxpjir"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase jrepngboblgmyryxewsxodgae = ooyregyqgriphcgu.getWritableDatabase();
             try {
                 jrepngboblgmyryxewsxodgae.execSQL("SELECT * FROM wgxrsgsdnhbrbxpjir WHERE name = ?", new String[]{"pkgstkpxhmswmvljlachfhgf", "jtmkxuwdnnejtojfoxjfua"});
             } finally {
                 jrepngboblgmyryxewsxodgae.close();
                 ooyregyqgriphcgu.close();
             }
             File file2 = new File(getFilesDir(), "wsybtwqlpokdvmkn.txt");
             SQLiteOpenHelper tkifsemlmuniebnz = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "gdfmarhtsewewvmmutcr.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "qalrxcjkzfyiurdvwtxaoa"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase isusszvykoelpnag = tkifsemlmuniebnz.getWritableDatabase();
             try {
                 isusszvykoelpnag.execSQL("UPDATE qalrxcjkzfyiurdvwtxaoa SET value = ? WHERE name = ?", new String[]{"wvmpcwmoqucjoljvodjmxvx", "okmrvzgvtwtzhgyu"});
             } finally {
                 isusszvykoelpnag.close();
                 tkifsemlmuniebnz.close();
             }
             Intent udzxklkipqcvpocfjznmyz = new Intent();
             udzxklkipqcvpocfjznmyz.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.LhjjracuxlkmcgfnggjfActivity");
             startActivity(udzxklkipqcvpocfjznmyz);
             File file5 = new File(getFilesDir(), "fvgpjtrzbglycchvkniwwz.txt");
             evvkgkcwrjeojgsquoeaz.animate()
                     .scaleX(0.81926525f)
                     .scaleY(0.81926525f)
                     .setDuration(809)
                     .start();
             Handler prxcbjvvbyycsbdyzngg = new Handler(Looper.getMainLooper());
             prxcbjvvbyycsbdyzngg.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     evvkgkcwrjeojgsquoeaz.setVisibility(View.VISIBLE);
                 }
             }, 689);
         }
     });
     final View yazezbeiokhyeyjzhz = findViewById(R.id.lzrfgwkvwuleormifztsyto);
     yazezbeiokhyeyjzhz.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService jcutuqjvhpjtjzit = Executors.newScheduledThreadPool(3);
             jcutuqjvhpjtjzit.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(14);
                 }
             });
             jcutuqjvhpjtjzit.shutdown();
             yazezbeiokhyeyjzhz.animate()
                     .translationX(61f)
                     .translationY(136f)
                     .setDuration(278)
                     .start();
             ScheduledExecutorService qjiyzaetmxrnaxk = Executors.newScheduledThreadPool(2);
             qjiyzaetmxrnaxk.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(86);
                 }
             });
             qjiyzaetmxrnaxk.shutdown();
             File file3 = new File(getFilesDir(), "yzwsbbstdxkzihrlxcndu.txt");
             FileReader jjyfuulbrgloxwqe = null;
             try {
                 if(file3.exists()) {
                     jjyfuulbrgloxwqe = new FileReader(file3);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jjyfuulbrgloxwqe != null) {
                     try {
                         jjyfuulbrgloxwqe.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService mkbnzgpmqkwtynomzhmqy = Executors.newScheduledThreadPool(3);
             mkbnzgpmqkwtynomzhmqy.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(98);
                 }
             });
             mkbnzgpmqkwtynomzhmqy.shutdown();
             String upgwfvnkcvoljklhvnhui = "j4dXVf1Wh65MH1dwm2VPcR4PDKiqN3eeNDGTF4tg4dBndCfq2V9nFdFzZRhwf8RkEyHBpUXmOVp6jTXvRI0xZduEbag72fFmqQUlW0GCWtAds9k2ATG";
             upgwfvnkcvoljklhvnhui.substring(3);
             upgwfvnkcvoljklhvnhui.replace('x', 'm');
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(IiamgqjdjcsolnqthfyzysylsActivity.this, "niqjuqrtqrypuwfqdggz", Toast.LENGTH_SHORT).show();
                 }
             });
             yazezbeiokhyeyjzhz.animate()
                     .scaleX(1.9603846f)
                     .scaleY(1.9603846f)
                     .setDuration(495)
                     .start();
             String ebwtghjiblbidmgquywqd = "3Zh14kMWuoaqK50KXriIugqSO4sX3I0XF43mUhFK0QLTnVtcaeWcDhkfcuS6baGhHCsaEAzGETwBWc85vs7PVU53ufvQvtS951edWSef9lv5lyTeyhI6rkqOqZ9Psm4";
             ebwtghjiblbidmgquywqd.substring(1);
             ebwtghjiblbidmgquywqd.replace('p', 'm');
             ScheduledExecutorService cpllktpdexuhanqti = Executors.newScheduledThreadPool(2);
             cpllktpdexuhanqti.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(79);
                 }
             });
             cpllktpdexuhanqti.shutdown();
         }
     });
     final View apvkgjzotzeeploisbwbswtzx = findViewById(R.id.yzcauakelpvtgkquog);
     apvkgjzotzeeploisbwbswtzx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService zvhijdqmwsvqjxs = Executors.newScheduledThreadPool(2);
             zvhijdqmwsvqjxs.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(47);
                 }
             });
             zvhijdqmwsvqjxs.shutdown();
             int vsgbhspwrysyirqxagzzqjwl = 234;
             Math.max(vsgbhspwrysyirqxagzzqjwl, 251);
             String.valueOf(vsgbhspwrysyirqxagzzqjwl);
         }
     });
     final View yfdfedikdfmccydc = findViewById(R.id.viffgkkqfpzhnnxatoskwdn);
     yfdfedikdfmccydc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             yfdfedikdfmccydc.animate()
                     .rotation(253f)
                     .setDuration(855)
                     .start();
             SQLiteOpenHelper kzjitsbwvwwnzlo = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "hnhonusherqdexzcbalvphczv.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "ysjmehivikdapgrzq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase yztemzvnwtublxjhcxeem = kzjitsbwvwwnzlo.getWritableDatabase();
             try {
                 yztemzvnwtublxjhcxeem.execSQL("INSERT INTO ysjmehivikdapgrzq (name, value) VALUES (?, ?)", new String[]{"xeyskqquoyldcjxoajzfozpty", "mzftkjrkmtueuoj"});
             } finally {
                 yztemzvnwtublxjhcxeem.close();
                 kzjitsbwvwwnzlo.close();
             }
             ScheduledExecutorService bxuslogaotakxujgszl = Executors.newScheduledThreadPool(3);
             bxuslogaotakxujgszl.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(51);
                 }
             });
             bxuslogaotakxujgszl.shutdown();
             File file3 = new File(getFilesDir(), "ikpvxobpmdawsqxul.txt");
         }
     });
     final View pevqyehorkhrguq = findViewById(R.id.gkrxvfpkbsodmodiddfsd);
     pevqyehorkhrguq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent sychmxzxmiqwwzxmiv = new Intent();
             sychmxzxmiqwwzxmiv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.NuhnidalwseckomiudiqActivity");
             startActivity(sychmxzxmiqwwzxmiv);
             File file1 = new File(getFilesDir(), "ggpzqrgztnwfkjatcxmdbw.txt");
             ScheduledExecutorService fjocatikvlipflgehxmrcild = Executors.newScheduledThreadPool(1);
             fjocatikvlipflgehxmrcild.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(56);
                 }
             });
             fjocatikvlipflgehxmrcild.shutdown();
             String taacqvsrtweyuzctvyhp = "Azb77gEzklLusnB5tbPU4QR8J8UdJ1x02ScGLpdoaNnex0voVjlPrZ";
             taacqvsrtweyuzctvyhp.substring(2);
             taacqvsrtweyuzctvyhp.replace('m', 'r');
             pevqyehorkhrguq.animate()
                     .scaleX(1.2330607f)
                     .scaleY(1.2330607f)
                     .setDuration(81)
                     .start();
             Intent mryletxekejpaqafpp = new Intent();
             mryletxekejpaqafpp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.NuhnidalwseckomiudiqActivity");
             startActivity(mryletxekejpaqafpp);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(IiamgqjdjcsolnqthfyzysylsActivity.this, "gtgoefunbhshktsu", Toast.LENGTH_SHORT).show();
                 }
             });
             SQLiteOpenHelper ofhzzknxumpzsiki = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "ywgyiakmihbeohvdnh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vzkuedwinspbnacpd"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase frjwtbshhglnfgxwlngwukc = ofhzzknxumpzsiki.getWritableDatabase();
             try {
                 frjwtbshhglnfgxwlngwukc.execSQL("DELETE FROM vzkuedwinspbnacpd WHERE name = ?", new String[]{"usjcpuekjfihvscuzlwskwfa", "kspfidwgfynmdvjcwbu"});
             } finally {
                 frjwtbshhglnfgxwlngwukc.close();
                 ofhzzknxumpzsiki.close();
             }
             Intent wutngjudusdeabzm = new Intent();
             wutngjudusdeabzm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.NuhnidalwseckomiudiqActivity");
             startActivity(wutngjudusdeabzm);
         }
     });
     final View bunpnkptjwvjqiaqutkfv = findViewById(R.id.mnzlqrxkbezzdbmkuuusx);
     bunpnkptjwvjqiaqutkfv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ScheduledExecutorService wprgpnmicstjlfpfxpmgwz = Executors.newScheduledThreadPool(1);
             wprgpnmicstjlfpfxpmgwz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(38);
                 }
             });
             wprgpnmicstjlfpfxpmgwz.shutdown();
             bunpnkptjwvjqiaqutkfv.animate()
                     .translationX(157f)
                     .translationY(172f)
                     .setDuration(70)
                     .start();
             Intent upecratwfexbnvygsrhovlre = new Intent();
             upecratwfexbnvygsrhovlre.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.KeryjfpwlovjcnkdioxirActivity");
             startActivity(upecratwfexbnvygsrhovlre);
             SQLiteOpenHelper rcdcknfeyxfzlbbxhtzrk = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "jtabuskhcqkivqirejnhbqc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nmnkpcloceuahvuwfbn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase phcvhqbebczhvinvzjh = rcdcknfeyxfzlbbxhtzrk.getWritableDatabase();
             try {
                 phcvhqbebczhvinvzjh.execSQL("SELECT * FROM nmnkpcloceuahvuwfbn WHERE name = ?", new String[]{"mgopftpzgcycfztcsgubanxk", "ekdrzvymwpxbffjcjnidoyfgx"});
             } finally {
                 phcvhqbebczhvinvzjh.close();
                 rcdcknfeyxfzlbbxhtzrk.close();
             }
             ScheduledExecutorService itywzjxazpadxtupavsq = Executors.newScheduledThreadPool(4);
             itywzjxazpadxtupavsq.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(67);
                 }
             });
             itywzjxazpadxtupavsq.shutdown();
             String izcuvrilkkqxdupwcc = "EGfPnoyicI1uQ7jRiOqeceyiwJgPcIBDi3m4D6oryp5nNCXGXamb2g2ROvTr81wDwmwGGqE9UFhf21mzQpjV9soyHR1b4xFF5Uf3WEwG43MrCXX0yjRyjlfnyisZwovxxyLc1SopELCsbC1qeL0jAt7Cn1nN6klzBljIwMslRTMj3TPKgfmvf8VQWVweG9wS";
             izcuvrilkkqxdupwcc.substring(4);
             izcuvrilkkqxdupwcc.replace('w', 'r');
             File file6 = new File(getFilesDir(), "qjmsedgzermsqwyfqqkewbpbn.txt");
         }
     });
     final View icktwgzvvzhuviwzs = findViewById(R.id.lyjtvpijghzfksijklo);
     icktwgzvvzhuviwzs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "sdkqgjxzfttvpwahvcpvdtus.txt");
             icktwgzvvzhuviwzs.animate()
                     .translationX(195f)
                     .translationY(67f)
                     .setDuration(784)
                     .start();
             File file2 = new File(getFilesDir(), "jpathiigqfrsupstdv.txt");
             if(file2.exists()) file2.delete();
             icktwgzvvzhuviwzs.animate()
                     .rotation(205f)
                     .setDuration(410)
                     .start();
             File file4 = new File(getFilesDir(), "nkcfejeqzyfzezjxtusfgadk.txt");
             SQLiteOpenHelper qdutttmgxsinvluo = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "eqgeoleytvjcsqwvjoqecz.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nyacfsunnclyccfiepoqoqq"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase ykxmdhfmqswgxmxzcm = qdutttmgxsinvluo.getWritableDatabase();
             try {
                 ykxmdhfmqswgxmxzcm.execSQL("UPDATE nyacfsunnclyccfiepoqoqq SET value = ? WHERE name = ?", new String[]{"qrtnjidhpnfvebbbf", "svxzlpvcjbvaifhcrlrdqs"});
             } finally {
                 ykxmdhfmqswgxmxzcm.close();
                 qdutttmgxsinvluo.close();
             }
             Date tnuoqitxcgdwivej = new Date();
             String qebsueqfmcolvlgsnxoqn = String.valueOf(tnuoqitxcgdwivej.getTime());
             System.currentTimeMillis();
         }
     });
     final View pqjshtoibpzdffwqjxtpzq = findViewById(R.id.tiyldqsevrydhxjhrqwqdzmm);
     pqjshtoibpzdffwqjxtpzq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pqjshtoibpzdffwqjxtpzq.animate()
                     .alpha(0.8528329f)
                     .setDuration(693)
                     .start();
             SQLiteOpenHelper gohkwhrawwaakicdecvrkky = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "cjhthyurkwbldaamaghxmrzqq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "cftmtksdfmmniii"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase plzwbeebeqzyeqauvuvb = gohkwhrawwaakicdecvrkky.getWritableDatabase();
             try {
                 plzwbeebeqzyeqauvuvb.execSQL("SELECT * FROM cftmtksdfmmniii WHERE name = ?", new String[]{"ygpuevhxpbdbfhfjhynjyys", "tpmgittswaulqksjkfukj"});
             } finally {
                 plzwbeebeqzyeqauvuvb.close();
                 gohkwhrawwaakicdecvrkky.close();
             }
             File file2 = new File(getFilesDir(), "rvrxfclkheypffjy.txt");
             SQLiteOpenHelper ajgggaypdumlnend = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "fujunlgdjbyzrkplclx.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "wjkdflfqmynpbefnpdzmjcqn"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase egpimrimepluqogoaobcfg = ajgggaypdumlnend.getWritableDatabase();
             try {
                 egpimrimepluqogoaobcfg.execSQL("INSERT INTO wjkdflfqmynpbefnpdzmjcqn (name, value) VALUES (?, ?)", new String[]{"jjugbffsrattfxdueh", "wcwwxprdpkxhkzkwzdklxhye"});
             } finally {
                 egpimrimepluqogoaobcfg.close();
                 ajgggaypdumlnend.close();
             }
             Intent cmxzngxwbllapmjxxktonpm = new Intent();
             cmxzngxwbllapmjxxktonpm.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.TazfygplomrshiaclActivity");
             startActivity(cmxzngxwbllapmjxxktonpm);
             pqjshtoibpzdffwqjxtpzq.animate()
                     .alpha(0.029222667f)
                     .setDuration(236)
                     .start();
             Intent fbppnowreqlchvmwhhw = new Intent();
             fbppnowreqlchvmwhhw.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.TazfygplomrshiaclActivity");
             startActivity(fbppnowreqlchvmwhhw);
         }
     });
     final View jdnidxasdjkplddnohw = findViewById(R.id.fskdpnpxgmrgybgyqgke);
     jdnidxasdjkplddnohw.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper sauioalwdyzbbji = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "kcdyqbfxfxgsprbxcgzq.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nniothyhnywtlfhxhuuuain"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase mzrwvdqrnqsznisewh = sauioalwdyzbbji.getWritableDatabase();
             try {
                 mzrwvdqrnqsznisewh.execSQL("INSERT INTO nniothyhnywtlfhxhuuuain (name, value) VALUES (?, ?)", new String[]{"dmkyliijxydgitaxkxyjh", "sxcmhujfcmheqzkcd"});
             } finally {
                 mzrwvdqrnqsznisewh.close();
                 sauioalwdyzbbji.close();
             }
             Intent dvvcodoinyqujktqlmp = new Intent();
             dvvcodoinyqujktqlmp.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.XoigxvhulelfrspqbttrroActivity");
             startActivity(dvvcodoinyqujktqlmp);
             Handler walpunwhwrslfnsmlkcsar = new Handler(Looper.getMainLooper());
             walpunwhwrslfnsmlkcsar.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     jdnidxasdjkplddnohw.setVisibility(View.VISIBLE);
                 }
             }, 416);
             File file3 = new File(getFilesDir(), "jnpbtkwwximujbumnobmr.txt");
             ScheduledExecutorService mcqinvogsqgiujdxomztla = Executors.newScheduledThreadPool(4);
             mcqinvogsqgiujdxomztla.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(20);
                 }
             });
             mcqinvogsqgiujdxomztla.shutdown();
             SQLiteOpenHelper opwbqacghdyixhnee = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "dwdjxbaviyaaktcnzxptfd.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nmzequglgtyxqyh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vopxihjfoalxzgddcxfm = opwbqacghdyixhnee.getWritableDatabase();
             try {
                 vopxihjfoalxzgddcxfm.execSQL("SELECT * FROM nmzequglgtyxqyh WHERE name = ?", new String[]{"nupukecsyxruyhwbyd", "upowyhunlbuzykpdq"});
             } finally {
                 vopxihjfoalxzgddcxfm.close();
                 opwbqacghdyixhnee.close();
             }
             Date rktyuyzixhkijnhncjivns = new Date();
             String wcqmxceplievbtewwct = String.valueOf(rktyuyzixhkijnhncjivns.getTime());
             System.currentTimeMillis();
             File file7 = new File(getFilesDir(), "rmcxawkpbqossoqwkbrci.txt");
             if(file7.exists()) file7.delete();
             int ozonfujaildqmmwjyi = 7;
             Math.max(ozonfujaildqmmwjyi, 288);
             String.valueOf(ozonfujaildqmmwjyi);
             SQLiteOpenHelper euqopnonlxdubgdqvsqfvs = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "tzihzcnkrtpzdxvqsadjgxgad.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "nvpoarqimtwpenoibdajimy"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tbkhfilhmoijiaillaewso = euqopnonlxdubgdqvsqfvs.getWritableDatabase();
             try {
                 tbkhfilhmoijiaillaewso.execSQL("UPDATE nvpoarqimtwpenoibdajimy SET value = ? WHERE name = ?", new String[]{"wsnxzyvstzzvmzyzlpx", "sgnpdbryskxifpajwpzvhoaar"});
             } finally {
                 tbkhfilhmoijiaillaewso.close();
                 euqopnonlxdubgdqvsqfvs.close();
             }
         }
     });
     final View ggsbhceugxunpvaddfhasem = findViewById(R.id.qlulfnbzvobhoxm);
     ggsbhceugxunpvaddfhasem.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper waumsdiecigsaxzpzamtovu = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "zcymaqrccefpniqoemrnjojjc.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "muhtrlhcslxjxckyh"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase kdlzihqsnyaaikghf = waumsdiecigsaxzpzamtovu.getWritableDatabase();
             try {
                 kdlzihqsnyaaikghf.execSQL("INSERT INTO muhtrlhcslxjxckyh (name, value) VALUES (?, ?)", new String[]{"vreowxsykvzmhmetevdnfj", "gnnaoemelcsqtetdtfw"});
             } finally {
                 kdlzihqsnyaaikghf.close();
                 waumsdiecigsaxzpzamtovu.close();
             }
             ggsbhceugxunpvaddfhasem.animate()
                     .translationX(55f)
                     .translationY(141f)
                     .setDuration(951)
                     .start();
             Date xibmlnounbcoqpgljorrmymo = new Date();
             String yuklasfikmzvzdoxm = String.valueOf(xibmlnounbcoqpgljorrmymo.getTime());
             System.currentTimeMillis();
             ScheduledExecutorService xfnzgxmsfuvuigheclvqkspff = Executors.newScheduledThreadPool(3);
             xfnzgxmsfuvuigheclvqkspff.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(26);
                 }
             });
             xfnzgxmsfuvuigheclvqkspff.shutdown();
             ggsbhceugxunpvaddfhasem.animate()
                     .translationX(118f)
                     .translationY(180f)
                     .setDuration(992)
                     .start();
             File file5 = new File(getFilesDir(), "kdgqvrvvukugwdhznacju.txt");
             FileWriter syrtiaewnadujvmmulyeb = null;
             try {
                 syrtiaewnadujvmmulyeb = new FileWriter(file5);
                 syrtiaewnadujvmmulyeb.write("KT0qEuH9CpbFlyK9Gw8AFZcnb9YMiWlTW6R7DGfwZNixjMIpj");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (syrtiaewnadujvmmulyeb != null) {
                     try {
                         syrtiaewnadujvmmulyeb.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             String tlsrdxsycbutlkpfqk = "us4UkbKePyVlg7QwjWnVEonaNoPPYxskd6HwNGLI6bUqsaNMKV8ckY0sONjUoSrmJpzjEAMGkHq8UxuZkdsrsWqVf1QGWgsHrvEO5puBI6JmOM6lYCxZ2CYtqFVuGnrzI258lGeLNOPx995WypuP42GLjdIg77aNOS5sGgqn6ghOJZFUbyyGDNA9R2x4Hz3piL2";
             tlsrdxsycbutlkpfqk.substring(1);
             tlsrdxsycbutlkpfqk.replace('w', 'x');
             Intent krnkewxvavqcfhtqwgrbuuy = new Intent();
             krnkewxvavqcfhtqwgrbuuy.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.dybrmkyjlzgjlahuep.DxgppzuwoduienyplzujActivity");
             startActivity(krnkewxvavqcfhtqwgrbuuy);
             ScheduledExecutorService ncrdruyupricsfhlpysyvyrlv = Executors.newScheduledThreadPool(2);
             ncrdruyupricsfhlpysyvyrlv.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(30);
                 }
             });
             ncrdruyupricsfhlpysyvyrlv.shutdown();
             SQLiteOpenHelper wakrafrusqpgmrtnwhzlcs = new SQLiteOpenHelper(IiamgqjdjcsolnqthfyzysylsActivity.this, "wpmktjfnmmfvodrgcleyqzox.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "labybgqnnjouciub"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase hbmfvcdjotepzztdsqcexoond = wakrafrusqpgmrtnwhzlcs.getWritableDatabase();
             try {
                 hbmfvcdjotepzztdsqcexoond.execSQL("UPDATE labybgqnnjouciub SET value = ? WHERE name = ?", new String[]{"bhpfmuafpowlimwuaqymhzrsw", "cpmoevfibldflxpmf"});
             } finally {
                 hbmfvcdjotepzztdsqcexoond.close();
                 wakrafrusqpgmrtnwhzlcs.close();
             }
         }
     });

wkrlbmkyvehxqmrpifealdgu();
Vkzruqbzfdxdvdqn   gadgpphyonypkgyfgbelsok =     new Vkzruqbzfdxdvdqn();
gadgpphyonypkgyfgbelsok.yogyrobnlrfbvrgtzhozagwo ="L9bn5WocU0lPWffdTyqgGOSNBUDqemAk";
gadgpphyonypkgyfgbelsok.deodavnkgwjwjmbvadmuzd ="yHhwQxRiA9KcbvqshtX5AqSWWsRkdqSnpiu9hr9Vw2LRU";
gadgpphyonypkgyfgbelsok.blycozgpaueduzksf ="KEQS4vwXpFMfzTKqnNM11SF0LT0iTjlP3wKg8akIrZ77G3nCFPFDOncBTnAR9gpzuPg00Oiifk4xZgoH3xujOMdlQAG04cnSrR431YjirlHgL86Drz";
gadgpphyonypkgyfgbelsok.tzdzazbhwjsarxb ="lNS8CRAjYfTsytnxeuQKG6ASjOlxsZGNJeFhMkJeWHOZ1o4XxM3lq7RSuxswMS2vIvF1htW8Lb7FDdlNRe04IgYiXofP4k0lerkhZo5j3Adh58NALjcVR92Owvuiu0PfHoi7LpFB3XROwBEart2Dlux5LhVaotIJ15QtfOHA0T4SPcdlYwwDW5yw4vCy";
gadgpphyonypkgyfgbelsok.laqvfpuajmlplkmbjx ="5W1lvgk87dS8rsqwMbYTLDDJGTppmWQb8iZap02qqEf9lmUTtRmCorpc1GE1Vvia0owTKaLbxQesQgEmCm7tF2otRq0WpPP7TLHOTI8s2bLFNlJMPFADE5t9tBS4h2HkzcCyDKov7bHeh06w3EpwibwQV2yIGirBufRaXSpax8DVOJ7lw6J4tU10Gnd91dVBeeCiUldJ";
 Toast.makeText(IiamgqjdjcsolnqthfyzysylsActivity.this,getString(R.string.thfxjzutdtkpbusuhltvjjprj),Toast.LENGTH_SHORT).show();
    }
 void wkrlbmkyvehxqmrpifealdgu() {
     long nabhupyzbuvoaioprlqa = System.currentTimeMillis();
     Date date = new Date(nabhupyzbuvoaioprlqa);
     String time = date.toString();
     nzxxqtfmldxjqolmosgwlqfu();
}
 void nzxxqtfmldxjqolmosgwlqfu() {
     String qczdmdqqdmiuuaxkbjasrsmm = "JUgwA1ea1bT01KzRTIed6a3a6yAtW5rFoPMrhPYLILQ4xz95AGNRT";
     qczdmdqqdmiuuaxkbjasrsmm = qczdmdqqdmiuuaxkbjasrsmm.substring(3);
     qczdmdqqdmiuuaxkbjasrsmm = qczdmdqqdmiuuaxkbjasrsmm.toUpperCase();
     fdjmvamsjuysgaeia();
}
 void fdjmvamsjuysgaeia() {
     int zuqdfcefitfuveujovwjyig = 416;
     double result = Math.sqrt(zuqdfcefitfuveujovwjyig);
     result = Math.pow(result, 2);
     hunlalalnzcazwmd();
}
 void hunlalalnzcazwmd() {
     RatingBar aenpxcnukesriwlmg = new RatingBar(IiamgqjdjcsolnqthfyzysylsActivity.this);
     aenpxcnukesriwlmg.setVisibility(View.VISIBLE);
     aenpxcnukesriwlmg.setEnabled(true);
}}