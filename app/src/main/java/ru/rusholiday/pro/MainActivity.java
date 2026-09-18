package ru.rusholiday.pro;
import android.app.*; import android.os.*; import android.content.*; import android.content.pm.PackageManager; import android.graphics.Color; import android.view.*; import android.widget.*; import java.text.*; import java.util.*;

public class MainActivity extends Activity {
 Calendar cal=Calendar.getInstance(); LinearLayout list; TextView monthTitle; boolean dark=false;
 @Override public void onCreate(Bundle b){super.onCreate(b); dark=getPreferences(0).getBoolean("dark",false); build(); if(Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},10);}
 void build(){
  int bg=dark?Color.rgb(18,20,24):Color.rgb(246,248,252), fg=dark?Color.WHITE:Color.rgb(25,30,38);
  LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(18,18,18,12); root.setBackgroundColor(bg);
  LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
  TextView title=t("Праздники России",24,fg); top.addView(title,new LinearLayout.LayoutParams(0,70,1));
  Button set=btn("⚙",fg); top.addView(set,new LinearLayout.LayoutParams(60,60)); root.addView(top);
  monthTitle=t("",20,fg); root.addView(monthTitle);
  LinearLayout nav=new LinearLayout(this); Button prev=btn("‹",fg), next=btn("›",fg), year=btn(""+cal.get(Calendar.YEAR),fg);
  nav.addView(prev,new LinearLayout.LayoutParams(60,55)); nav.addView(year,new LinearLayout.LayoutParams(0,55,1)); nav.addView(next,new LinearLayout.LayoutParams(60,55)); root.addView(nav);
  GridLayout week=new GridLayout(this); week.setColumnCount(7); String[] ws={"Пн","Вт","Ср","Чт","Пт","Сб","Вс"}; for(String s:ws){TextView z=t(s,12,Color.GRAY); z.setGravity(17); week.addView(z,new ViewGroup.LayoutParams(-1,40));} root.addView(week);
  GridLayout grid=new GridLayout(this); grid.setColumnCount(7); root.addView(grid,new LinearLayout.LayoutParams(-1,0,1)); list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); ScrollView sv=new ScrollView(this); sv.addView(list); root.addView(sv,new LinearLayout.LayoutParams(-1,230));
  prev.setOnClickListener(v->{cal.add(Calendar.MONTH,-1); refresh(grid,fg);}); next.setOnClickListener(v->{cal.add(Calendar.MONTH,1); refresh(grid,fg);}); year.setOnClickListener(v->pickYear(grid,fg));
  set.setOnClickListener(v->settings());
  setContentView(root); refresh(grid,fg);
 }
 TextView t(String s,float size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 Button btn(String s,int fg){Button b=new Button(this);b.setText(s);b.setTextColor(fg);b.setTextSize(18);return b;}
 void refresh(GridLayout g,int fg){
  g.removeAllViews(); String[] m={"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
  monthTitle.setText(m[cal.get(Calendar.MONTH)]+" "+cal.get(Calendar.YEAR));
  List<Holiday> hs=Holidays.forYear(cal.get(Calendar.YEAR)); Map<Integer,Holiday> map=new HashMap<>();
  for(Holiday h:hs){try{int d=Integer.parseInt(h.date.substring(8)); if(Integer.parseInt(h.date.substring(5,7))-1==cal.get(Calendar.MONTH))map.put(d,h);}catch(Exception e){}}
  Calendar first=(Calendar)cal.clone(); first.set(Calendar.DAY_OF_MONTH,1); int dow=first.get(Calendar.DAY_OF_WEEK); int shift=(dow==Calendar.SUNDAY?6:dow-Calendar.MONDAY);
  for(int i=0;i<shift;i++)g.addView(new TextView(this),cellParams());
  int max=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
  for(int d=1;d<=max;d++){ final int day=d; TextView c=t(""+d,15,fg); c.setGravity(17); c.setPadding(2,2,2,2); Holiday h=map.get(d); if(h!=null){c.setBackgroundColor(Color.rgb(255,226,226)); c.setTextColor(Color.rgb(190,40,55)); c.setOnClickListener(v->detail(h));} else if(isWeekend(day)){c.setBackgroundColor(dark?Color.rgb(45,45,52):Color.rgb(238,241,247));} g.addView(c,cellParams());}
  list.removeAllViews(); ArrayList<Holiday> month=new ArrayList<>(); for(Holiday h:hs)if(h.date.substring(5,7).equals(String.format("%02d",cal.get(Calendar.MONTH)+1)))month.add(h);
  for(Holiday h:month){TextView v=t("  "+h.date.substring(8)+"  •  "+h.title,15,fg); v.setPadding(8,6,8,6); v.setOnClickListener(x->detail(h)); list.addView(v);}
 }
 boolean isWeekend(int d){Calendar x=(Calendar)cal.clone();x.set(Calendar.DAY_OF_MONTH,d);int w=x.get(Calendar.DAY_OF_WEEK);return w==Calendar.SATURDAY||w==Calendar.SUNDAY;}
 GridLayout.LayoutParams cellParams(){GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=0;p.height=58;p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);p.rowSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);p.setMargins(2,2,2,2);return p;}
 void detail(Holiday h){new AlertDialog.Builder(this).setTitle(h.title).setMessage(h.date+"\nКатегория: "+h.category).setPositiveButton("Закрыть",null).setNeutralButton("В избранное",(d,w)->getPreferences(0).edit().putString("fav",h.date).apply()).show();}
 void pickYear(GridLayout g,int fg){String[] ys=new String[15];int base=cal.get(Calendar.YEAR)-7;for(int i=0;i<15;i++)ys[i]=""+(base+i);new AlertDialog.Builder(this).setTitle("Выберите год").setItems(ys,(d,which)->{cal.set(Calendar.YEAR,base+which);refresh(g,fg);}).show();}
 void settings(){
  final String[] items={"🔔 Напоминать о праздниках","🌙 Тёмная тема","ℹ️ О приложении"};
  new AlertDialog.Builder(this).setTitle("Настройки").setItems(items,(d,w)->{
   if(w==0)scheduleTomorrow();
   if(w==1){dark=!dark;getPreferences(0).edit().putBoolean("dark",dark).apply();build();}
   if(w==2)new AlertDialog.Builder(this).setTitle("Праздники России 4.0").setMessage("Календарь федеральных и памятных дат. Работает без интернета.").setPositiveButton("OK",null).show();
  }).show();
 }
 void scheduleTomorrow(){
  List<Holiday> hs=Holidays.forYear(cal.get(Calendar.YEAR)); Calendar now=Calendar.getInstance(); now.add(Calendar.DAY_OF_YEAR,1); String ds=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(now);
  for(Holiday h:hs)if(h.date.equals(ds)){AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);Intent i=new Intent(this,HolidayReceiver.class);i.putExtra("title",h.title);PendingIntent pi=PendingIntent.getBroadcast(this,1234,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60000,pi);Toast.makeText(this,"Напоминание установлено на 1 минуту для проверки",Toast.LENGTH_LONG).show();return;}
  Toast.makeText(this,"На завтра праздник не найден в базе.",Toast.LENGTH_SHORT).show();
 }
}
