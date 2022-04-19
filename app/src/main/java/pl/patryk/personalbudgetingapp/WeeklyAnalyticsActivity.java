package pl.patryk.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeeklyAnalyticsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount,analyticsFoodAmount,analyticsHouseExpensesAmount,analyticsEntertainmentAmount;
    private TextView analyticsEducationAmount,analyticsCharityAmount,analyticsApparelAmount,analyticsHealthAmount,analyticsPersonalExpensesAmount,analyticsOtherAmount, monthSpentAmount;

    private RelativeLayout linearLayoutFood,linearLayoutTransport,linearLayoutFoodHouse,linearLayoutEntertainment,linearLayoutEducation;
    private RelativeLayout linearLayoutCharity,linearLayoutApparel,linearLayoutHealth,linearLayoutPersonalExp,linearLayoutOther;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent,progress_ratio_edu,progress_ratio_cha, progress_ratio_app,progress_ratio_hea,progress_ratio_per,progress_ratio_oth, monthRatioSpending;
    private ImageView status_Image_transport, status_Image_food,status_Image_house,status_Image_ent,status_Image_edu,status_Image_cha,status_Image_app,status_Image_hea,status_Image_per,status_Image_oth, monthRatioSpending_Image;

    private int traTotal, foodTotal, houseTotal, entTotal, eduTotal, chaTotal, appTotal, heaTotal, perTotal, othTotal, monthTotalSpentAmount;

    private int traRatio, foodRatio, houseRatio, entRatio, eduRatio, chaRatio, appRatio, heaRatio, perRatio, othRatio, monthTotalSpentAmountRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_analytics);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Week Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseExpensesAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalExpensesAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        linearLayoutTransport = findViewById(R.id.linearLayoutTransport);
        linearLayoutFood = findViewById(R.id.linearLayoutFood);
        linearLayoutFoodHouse = findViewById(R.id.linearLayoutFoodHouse);
        linearLayoutEntertainment = findViewById(R.id.linearLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.linearLayoutEducation);
        linearLayoutCharity = findViewById(R.id.linearLayoutCharity);
        linearLayoutApparel = findViewById(R.id.linearLayoutApparel);
        linearLayoutHealth = findViewById(R.id.linearLayoutHealth);
        linearLayoutPersonalExp = findViewById(R.id.linearLayoutPersonalExp);
        linearLayoutOther = findViewById(R.id.linearLayoutOther);

        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_ent);
        progress_ratio_edu = findViewById(R.id.progress_ratio_edu);
        progress_ratio_cha = findViewById(R.id.progress_ratio_cha);
        progress_ratio_app = findViewById(R.id.progress_ratio_app);
        progress_ratio_hea = findViewById(R.id.progress_ratio_hea);
        progress_ratio_per = findViewById(R.id.progress_ratio_per);
        progress_ratio_oth = findViewById(R.id.progress_ratio_oth);

        status_Image_transport = findViewById(R.id.status_Image_transport);
        status_Image_food = findViewById(R.id.status_Image_food);
        status_Image_house = findViewById(R.id.status_Image_house);
        status_Image_ent = findViewById(R.id.status_Image_ent);
        status_Image_edu = findViewById(R.id.status_Image_edu);
        status_Image_cha = findViewById(R.id.status_Image_cha);
        status_Image_app = findViewById(R.id.status_Image_app);
        status_Image_hea = findViewById(R.id.status_Image_hea);
        status_Image_per = findViewById(R.id.status_Image_per);
        status_Image_oth = findViewById(R.id.status_Image_oth);

        anyChartView = findViewById(R.id.anyChartView);

        getTotalTypeWeekExpense("Transport","weekTrans",analyticsTransportAmount,linearLayoutTransport);
        getTotalTypeWeekExpense("Food","weekFood",analyticsFoodAmount,linearLayoutFood);
        getTotalTypeWeekExpense("House","weekHouse",analyticsHouseExpensesAmount,linearLayoutFoodHouse);
        getTotalTypeWeekExpense("Entertainment","weekEnt",analyticsEntertainmentAmount,linearLayoutEntertainment);
        getTotalTypeWeekExpense("Education","weekEdu",analyticsEducationAmount,linearLayoutEducation);
        getTotalTypeWeekExpense("Charity","weekCha",analyticsCharityAmount,linearLayoutCharity);
        getTotalTypeWeekExpense("Apparel and Services","weekApp",analyticsApparelAmount,linearLayoutApparel);
        getTotalTypeWeekExpense("Health","weekHea",analyticsHealthAmount,linearLayoutHealth);
        getTotalTypeWeekExpense("Personal Expenses","weekPer",analyticsPersonalExpensesAmount,linearLayoutPersonalExp);
        getTotalTypeWeekExpense("Other","weekOther",analyticsOtherAmount,linearLayoutOther);
        getTotalWeekSpending();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        loadGraph();
                        setStatusAndImageResource();
                    }
                },
                2000
        );
    }

    private void getTotalTypeWeekExpense(String type, String child, TextView textView, RelativeLayout linearLayout) {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = type+weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        textView.setText("Spent: " + totalAmount);
                    }
                    personalRef.child(child).setValue(totalAmount);
                }
                else {
                    linearLayout.setVisibility(View.GONE);
                    personalRef.child(child).setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTotalWeekSpending(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  dataSnapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                    }
                    totalBudgetAmountTextView.setText("Total week's spending: $ "+ totalAmount);
                    monthSpentAmount.setText("Total Spent: $ "+totalAmount);
                }else {
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getTypeTotalExpanses(DataSnapshot snapshot,String child){
        if (snapshot.hasChild(child))
            return Integer.parseInt(snapshot.child(child).getValue().toString());
        else
            return 0;
    }

    private void getTotalExpanses(DataSnapshot snapshot){
                    traTotal = getTypeTotalExpanses(snapshot,"weekTrans");
                    foodTotal = getTypeTotalExpanses(snapshot,"weekFood");
                    houseTotal = getTypeTotalExpanses(snapshot,"weekHouse");
                    entTotal = getTypeTotalExpanses(snapshot,"weekEnt");
                    eduTotal = getTypeTotalExpanses(snapshot,"weekEdu");
                    chaTotal = getTypeTotalExpanses(snapshot,"weekCha");
                    appTotal = getTypeTotalExpanses(snapshot,"weekApp");
                    heaTotal = getTypeTotalExpanses(snapshot,"weekHea");
                    perTotal = getTypeTotalExpanses(snapshot,"weekPer");
                    othTotal = getTypeTotalExpanses(snapshot,"weekOther");
                    monthTotalSpentAmount = getTypeTotalExpanses(snapshot,"week");
    }


    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    getTotalExpanses(snapshot);

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chaTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("other", othTotal));

                    pie.data(data);
                    pie.title("Week Analytics");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items Spent On")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                }
                else {
                    Toast.makeText(WeeklyAnalyticsActivity.this,"Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRatios(DataSnapshot snapshot){
                    traRatio = getTypeTotalExpanses(snapshot,"weekTransRatio");
                    foodRatio = getTypeTotalExpanses(snapshot,"weekFoodRatio");
                    houseRatio = getTypeTotalExpanses(snapshot,"weekHouseRatio");
                    entRatio = getTypeTotalExpanses(snapshot,"weekEntRatio");
                    eduRatio = getTypeTotalExpanses(snapshot,"weekEduRatio");
                    chaRatio = getTypeTotalExpanses(snapshot,"weekCharRatio");
                    appRatio = getTypeTotalExpanses(snapshot,"weekAppRatio");
                    heaRatio = getTypeTotalExpanses(snapshot,"weekHealthRatio");
                    perRatio = getTypeTotalExpanses(snapshot,"weekPerRatio");
                    othRatio = getTypeTotalExpanses(snapshot,"othRatio");
                    monthTotalSpentAmountRatio = getTypeTotalExpanses(snapshot,"weeklyBudget");
    }

    private void setSpentStatus(float percent, TextView typeSpending, ImageView typeSpendingImage,int ratio){
        if (percent<50){
            typeSpending.setText(percent+" %" +" used of "+ratio + ". Status:");
            typeSpendingImage.setImageResource(R.drawable.green);
        }else if (percent >= 50 && percent <100){
            typeSpending.setText(percent+" %" +" used of "+ratio + ". Status:");
            typeSpendingImage.setImageResource(R.drawable.brown);
        }else {
            typeSpending.setText(percent+" %" +" used of "+ratio + ". Status:");
            typeSpendingImage.setImageResource(R.drawable.red);
        }
    }

    private void setStatusAndImageResource(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() ){

                    getTotalExpanses(snapshot);
                    getRatios(snapshot);

                    if(monthTotalSpentAmountRatio!=0) {
                        float monthPercent = (monthTotalSpentAmount * 100 / monthTotalSpentAmountRatio);
                        setSpentStatus(monthPercent,monthRatioSpending,monthRatioSpending_Image,monthTotalSpentAmountRatio);
                    }
                    if(traRatio!=0) {
                        float transportPercent = (traTotal * 100 / traRatio);
                        setSpentStatus(transportPercent, progress_ratio_transport, status_Image_transport,traRatio);
                    }
                    if(foodRatio!=0) {
                        float foodPercent = (foodTotal * 100 / foodRatio);
                        setSpentStatus(foodPercent, progress_ratio_food, status_Image_food,foodRatio);
                    }
                    if(houseRatio!=0) {
                        float housePercent = (houseTotal * 100 / houseRatio);
                        setSpentStatus(housePercent, progress_ratio_house, status_Image_house,houseRatio);
                    }
                    if(entRatio!=0) {
                        float entPercent = (entTotal * 100 / entRatio);
                        setSpentStatus(entPercent, progress_ratio_ent, status_Image_ent,entRatio);
                    }
                    if(eduRatio!=0) {
                        float eduPercent = (eduTotal * 100 / eduRatio);
                        setSpentStatus(eduPercent, progress_ratio_edu, status_Image_edu,eduRatio);
                    }
                    if(chaRatio!=0) {
                        float chaPercent = (chaTotal * 100 / chaRatio);
                        setSpentStatus(chaPercent, progress_ratio_cha, status_Image_cha,chaRatio);
                    }
                    if(appRatio!=0) {
                        float appPercent = (appTotal * 100 / appRatio);
                        setSpentStatus(appPercent, progress_ratio_app, status_Image_app,appRatio);
                    }
                    if(heaRatio!=0) {
                        float heaPercent = (heaTotal * 100 / heaRatio);
                        setSpentStatus(heaPercent, progress_ratio_hea, status_Image_hea,heaRatio);
                    }
                    if(perRatio!=0) {
                        float perPercent = (perTotal * 100 / perRatio);
                        setSpentStatus(perPercent, progress_ratio_food, status_Image_food,perRatio);
                    }
                    if(othRatio!=0) {
                        float otherPercent = (othTotal * 100 / othRatio);
                        setSpentStatus(otherPercent, progress_ratio_oth, status_Image_oth,othRatio);
                    }
                }
                else {
                    Toast.makeText(WeeklyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}