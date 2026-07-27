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

public class PqmgodnqkritunwjbkbljppActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qmgodnqkritunwjbkbljpp);
     final View ogipdbpluzhjuvr = findViewById(R.id.uhssaifqdjlocyds);
     ogipdbpluzhjuvr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper fuvgeojtlszywflqjkxbny = new SQLiteOpenHelper(PqmgodnqkritunwjbkbljppActivity.this, "yexmzoygyulmklcikzem.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "aspvylzgpjqahmaewf"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase izjquuzyxihikeqdjp = fuvgeojtlszywflqjkxbny.getWritableDatabase();
             try {
                 izjquuzyxihikeqdjp.execSQL("INSERT INTO aspvylzgpjqahmaewf (name, value) VALUES (?, ?)", new String[]{"axdvpeomuitzcasynlw", "aidakwvypuzwjaliskesklbhs"});
             } finally {
                 izjquuzyxihikeqdjp.close();
                 fuvgeojtlszywflqjkxbny.close();
             }
             ogipdbpluzhjuvr.animate()
                     .alpha(0.21379608f)
                     .setDuration(328)
                     .start();
             Handler wbybaplpnhhkvtzmgk = new Handler(Looper.getMainLooper());
             wbybaplpnhhkvtzmgk.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     ogipdbpluzhjuvr.setVisibility(View.VISIBLE);
                 }
             }, 49);
             ogipdbpluzhjuvr.animate()
                     .alpha(0.06660521f)
                     .setDuration(276)
                     .start();
         }
     });
     final View qbsedtuptcvximaruezxeqoky = findViewById(R.id.dkwfoigkfvtpdrpgwdcyro);
     qbsedtuptcvximaruezxeqoky.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "strlgptkqxtamxxsuxvusx.txt");
             FileReader jmafefplozvjjtxopcuctly = null;
             try {
                 if(file0.exists()) {
                     jmafefplozvjjtxopcuctly = new FileReader(file0);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (jmafefplozvjjtxopcuctly != null) {
                     try {
                         jmafefplozvjjtxopcuctly.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             qbsedtuptcvximaruezxeqoky.animate()
                     .alpha(0.4067827f)
                     .setDuration(48)
                     .start();
             Intent ahvxivfixrcspvsiiuwma = new Intent();
             ahvxivfixrcspvsiiuwma.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.BoegcqmkhdubnacxstActivity");
             startActivity(ahvxivfixrcspvsiiuwma);
             Date zaaxedxaxpmwtoznxbih = new Date();
             String bafdbmttfpobivduuaff = String.valueOf(zaaxedxaxpmwtoznxbih.getTime());
             System.currentTimeMillis();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(PqmgodnqkritunwjbkbljppActivity.this, "ifrxeezqszmjjtstsemawzinv", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
     final View hcciizclxvarfgqu = findViewById(R.id.rpjzwxftihvteykl);
     hcciizclxvarfgqu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ljtnmqbgblmdcsmx = new Intent();
             ljtnmqbgblmdcsmx.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.ZkyuxhdtwuseuojsvlwnhwActivity");
             startActivity(ljtnmqbgblmdcsmx);
             File file1 = new File(getFilesDir(), "deicoyefrrjosthjcpuzwqy.txt");
             if(file1.exists()) file1.delete();
             ScheduledExecutorService tznjbkcsitskpaok = Executors.newScheduledThreadPool(3);
             tznjbkcsitskpaok.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(5);
                 }
             });
             tznjbkcsitskpaok.shutdown();
             SQLiteOpenHelper jnxpcbnxefnkdnynshpbestbl = new SQLiteOpenHelper(PqmgodnqkritunwjbkbljppActivity.this, "dervmszkolztblud.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "yllvmyutgesfmie"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase vaisgitlsxkkhfpghdg = jnxpcbnxefnkdnynshpbestbl.getWritableDatabase();
             try {
                 vaisgitlsxkkhfpghdg.execSQL("DELETE FROM yllvmyutgesfmie WHERE name = ?", new String[]{"ujjekzyjkaovkrnjzli", "xdlbvdorhchwbqxlpurpojjb"});
             } finally {
                 vaisgitlsxkkhfpghdg.close();
                 jnxpcbnxefnkdnynshpbestbl.close();
             }
             String tbobeyhmmjzlwfhog = "fS0agrRoaT7h9e0t18bWYhBkOvcekuwr0NfbfggZKsXchDXMxccQcrL4b7mbasRPCIPQzi8tQvmEVuwUoesRrglOfuJG70yLoaxvOU0ZgwQxlPEnrTXFdVtuKubt5lnsakNyND5owBhULro75qcafoYU5qhXfpVzd9ILIP4ApxJV6kbQARvN";
             tbobeyhmmjzlwfhog.substring(4);
             tbobeyhmmjzlwfhog.replace('f', 'k');
         }
     });
     final View hqmefdbgeaqcttgoqykqkhmq = findViewById(R.id.uamgrlneawopfwltftyzpwwio);
     hqmefdbgeaqcttgoqykqkhmq.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             SQLiteOpenHelper irilsfgzumdvodzksuyrpd = new SQLiteOpenHelper(PqmgodnqkritunwjbkbljppActivity.this, "fgvqtarogzwivqb.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "vtessigruwyivpkfhqo"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase dhjfhzydifjtnbucswu = irilsfgzumdvodzksuyrpd.getWritableDatabase();
             try {
                 dhjfhzydifjtnbucswu.execSQL("DELETE FROM vtessigruwyivpkfhqo WHERE name = ?", new String[]{"vhwwynspxvkesfjaytqd", "jyhikjupxkegcxl"});
             } finally {
                 dhjfhzydifjtnbucswu.close();
                 irilsfgzumdvodzksuyrpd.close();
             }
             File file1 = new File(getFilesDir(), "ilvuuxmkploseuqeyawu.txt");
             FileWriter gaesvhqtljalxcdxd = null;
             try {
                 gaesvhqtljalxcdxd = new FileWriter(file1);
                 gaesvhqtljalxcdxd.write("R2J6yUol3fBEExdtwXXa5uRJ0R77LFqWWDEnoe5XfFjkkiU8PeC1csykdODYFexWXTrPM8c7a8VLbp5H");
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (gaesvhqtljalxcdxd != null) {
                     try {
                         gaesvhqtljalxcdxd.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             ScheduledExecutorService ddocsdyxkndmljyepggyz = Executors.newScheduledThreadPool(2);
             ddocsdyxkndmljyepggyz.execute(new Runnable() {
                 @Override
                 public void run() {
                     SystemClock.sleep(60);
                 }
             });
             ddocsdyxkndmljyepggyz.shutdown();
             File file3 = new File(getFilesDir(), "njmuclqqykprqklfpvwuqrrd.txt");
             if(file3.exists()) file3.delete();
             Date cutkgxcwbpwahxqddcucdgggu = new Date();
             String ctirfiqrlylzuojspwskxox = String.valueOf(cutkgxcwbpwahxqddcucdgggu.getTime());
             System.currentTimeMillis();
             File file5 = new File(getFilesDir(), "tiaubnvsmcvbprjdeafqnb.txt");
             if(file5.exists()) file5.delete();
             SQLiteOpenHelper mvydxrnljgpzrbwoqnix = new SQLiteOpenHelper(PqmgodnqkritunwjbkbljppActivity.this, "nuaxiigufgczejsjldlugwyyh.db", null, 1) {
                 @Override
                 public void onCreate(SQLiteDatabase db) {
                     db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)", "rvwdyzrvtglcvpkdin"));
                 }
                 @Override
                 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
             };
             SQLiteDatabase tyecomhfimceddahz = mvydxrnljgpzrbwoqnix.getWritableDatabase();
             try {
                 tyecomhfimceddahz.execSQL("INSERT INTO rvwdyzrvtglcvpkdin (name, value) VALUES (?, ?)", new String[]{"vvuxuaqlgugwsaqhdsq", "kuokbgbmbymhcvheds"});
             } finally {
                 tyecomhfimceddahz.close();
                 mvydxrnljgpzrbwoqnix.close();
             }
         }
     });
     final View ssoamkggfsfjortawwyjcyfyi = findViewById(R.id.znixoebxqaiciotigomejcts);
     ssoamkggfsfjortawwyjcyfyi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             File file0 = new File(getFilesDir(), "oaverzabowxsdjxwopulzzd.txt");
             if(file0.exists()) file0.delete();
             ssoamkggfsfjortawwyjcyfyi.animate()
                     .alpha(0.4951954f)
                     .setDuration(10)
                     .start();
             File file2 = new File(getFilesDir(), "klprespkxqlmlsl.txt");
             Intent ihjzhdwfrrbiiurg = new Intent();
             ihjzhdwfrrbiiurg.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.WhcxnndrevaiycybldwijtActivity");
             startActivity(ihjzhdwfrrbiiurg);
             File file4 = new File(getFilesDir(), "orwvocopraihazwbwbegjv.txt");
             ssoamkggfsfjortawwyjcyfyi.animate()
                     .rotation(294f)
                     .setDuration(377)
                     .start();
             File file6 = new File(getFilesDir(), "iegpfwpwfwcuuapolg.txt");
             FileReader ejwmwjpqtzbdyao = null;
             try {
                 if(file6.exists()) {
                     ejwmwjpqtzbdyao = new FileReader(file6);
                     // 读取操作
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } finally {
                 if (ejwmwjpqtzbdyao != null) {
                     try {
                         ejwmwjpqtzbdyao.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
             Intent vpgjzjefhlkphmfgdrrtymdv = new Intent();
             vpgjzjefhlkphmfgdrrtymdv.setClassName(getPackageName(), "com.aicleanerstorage.spaceclean.junkcc.ptakifpafapdvkdtw.WhcxnndrevaiycybldwijtActivity");
             startActivity(vpgjzjefhlkphmfgdrrtymdv);
         }
     });

wviyhpbmjlqjqcyg();
Itpvgcafczmjccdzjsdlbslnoq   erxrqwvsmtrzmmntz =     new Itpvgcafczmjccdzjsdlbslnoq();
erxrqwvsmtrzmmntz.fjfxamhzrbjdmgwisw ="mrc3RXkIkZqvGvTZFpxtQC4Mk3Wit1MQjP3DBR6z8iWBebSaHBCCDBAN3kIP7scmKXEJnNKfFwcYSg";
erxrqwvsmtrzmmntz.jzdlemipcxfpjcnnalzbnlhgz ="MLTqSvDzkSQlznKl1Fc3LkncvYKvGFp";
erxrqwvsmtrzmmntz.yyqwhyknpogrwulpxmfn ="4HJrZwSKHdNg0NE3esImLfdopiQDNDrizz7vdbVjH5HnTw2zoqV5vRP1vbkmpSdZDSVxenh868r9ixcOYPFfBa02gM1vfNELW63jRVM4OKPGorhqCTxdlR04LUaWpUz5rTfZzmRuoHj0ORzQpGx0b7o0JlxpIvPY97cO";
erxrqwvsmtrzmmntz.cytslcrrkrrylquimhwyr ="wG3FLI6Cj3LDSji4lhg4SeXneSmPaSed6hCaFSgZEM5nj95umNbbuXgMiFLC3rv2RCnYtfis1qPtsz3cuurnvjIbvx";
erxrqwvsmtrzmmntz.bquouyhhrsbxzoapnmumkorq ="GkgG8olf9HvCLAKWCN9vX655VDH68oM3FbnPlYQrPlcMmmeQPWAVDThaLeRkZA4IgomOC9ObRu6xF9LIFNEjv2OusgjM4yGzw3ty3ESMPKWiGq2ZfmtUMKB4qoliRGnVmvelqyoJZoMjvrv7WsAYsv13ucQl2F";
erxrqwvsmtrzmmntz.nfiovkvrnsklzxrggcl ="FEk5Tbiy8k9uaoeSLxDvHxsqWZ2Lkr3hiFuRA0tapG25ZgI08nnUg3AQpBeooFcso7z6lJRANYup06RA75Is5WCKre3ijXygArhaq238KkxcOUQ23WeaaMBXfJP5MDd87zEeffc9WwEEU7tjuXksEPZR5WNzOuMyhiSeZa";
erxrqwvsmtrzmmntz.hspqciozyhqxlxjpowtneot ="XFhlBXSdMQHaCjsPH2nedzblcVXWsaNBVDPsXvTP7ALr1DIXR1TpFrmfr2to12be6HgbRgHBsHXYYodxN";
erxrqwvsmtrzmmntz.nysjyvibzgurktluznd ="MFAaKt8C3gwU6tcRfNZCOW4w6y57c";
erxrqwvsmtrzmmntz.cphezkaudtxpcpigdj ="pOhaKG30F88h1mZg4JzqLiwJIRIZ8WRb";
erxrqwvsmtrzmmntz.svmlebnlccnkottuxvo ="Ae7bKPVtNRNcsMzlI0VvPZ5op4IIn7";
erxrqwvsmtrzmmntz.ltizampelitsbxjqcftrnyufi ="DFioPb6en9ZbkDuQZXShA0rJJHoFtpm";
erxrqwvsmtrzmmntz.atgodxephjbpwrwhha ="z55P450KTjh";
erxrqwvsmtrzmmntz.xobbttxywqpdgylcyoqducgjr ="4xh1yY83FDyVT19rHyBWvv2RRHfm44fn264xxW42E0IWU4I3TpSLNGsCzepxgWiXgUAaDSsiWBXYODaRAGPDk2CHLZ6iLyCgD4RhQbrohEfVNP2UWhEVwSB5tWg9356AXsp";
erxrqwvsmtrzmmntz.mdgbcnpfsfmobpfcfqxkbqmfv ="mjRKZWMjFqHxTCdzk2i4epPTQrYmWgK5QH9mLcw02HyEYPsDxVs4YYTX45FZh9wSR";
erxrqwvsmtrzmmntz.ccxrbsgiocqcnyaqqnzmqzbef ="TNPaDrAHCTDz3GV2ssl26Muqz85v7wVYOdIYvwHogM6oej349wgTD1fBtZErDybKsThahECLZt2P3YHa8ay1miXbjHMLBJKZoGDa7U5LIFE2I014cIVNo2S6wM3PY3qZXaLn6hDQZ8srlIbehQ75gMIyFWWfUv4";
 Toast.makeText(PqmgodnqkritunwjbkbljppActivity.this,getString(R.string.gqcwqnybdxjkcdtor),Toast.LENGTH_SHORT).show();
    }
 void wviyhpbmjlqjqcyg() {
     int ncdamwkqqezhtonfoo = 671;
     double result = Math.sqrt(ncdamwkqqezhtonfoo);
     result = Math.pow(result, 2);
     dmvrmtbhksdbpvrqknvczdonh();
}
 void dmvrmtbhksdbpvrqknvczdonh() {
     String upppmhdedabcjimczf = "QYhqUodn5waWDa5yobGzSVJXkxx8uHCz3fTWTT7Indilof6leBL1W6BseKMQ1SFIwAg1tIiCr2AyGIc58i26ifHfFClvfTkXDEFwxtccV9nv5rv3u84X1eEaDSK6jVFcKtknztF3LlkLmvakc4gZgjJcqDlNQg";
     upppmhdedabcjimczf = upppmhdedabcjimczf.substring(3);
     upppmhdedabcjimczf = upppmhdedabcjimczf.toUpperCase();
     pyfilwdxrffaqcg();
}
 void pyfilwdxrffaqcg() {
     long ssmxrhirblddcllxibjqb = System.currentTimeMillis();
     Date date = new Date(ssmxrhirblddcllxibjqb);
     String time = date.toString();
     ttwryqhvrmvvfctyuqa();
}
 void ttwryqhvrmvvfctyuqa() {
     String etkmmmtopaojkkzrhfxfjccn = "hLVK2QMIi66Md8y1r9dDI3OlUDSfIbE6MPPqyxBFfCx6rFD0gXJrVvpPU3W5ArwlDMMyinXPgQQGPNFCwtzq7fFgYVCyvXOSrYgVKxzIMZDbrpIMtl2dtUPdh8p2NEYkBRP7TdUX9oG1Tk1gilxwpHBcqfhnNuu8whTuDYOpXZBt7QdpCR3VeihCRQEdIMPGAnGQUHo";
     etkmmmtopaojkkzrhfxfjccn = etkmmmtopaojkkzrhfxfjccn.substring(2);
     etkmmmtopaojkkzrhfxfjccn = etkmmmtopaojkkzrhfxfjccn.toUpperCase();
}}