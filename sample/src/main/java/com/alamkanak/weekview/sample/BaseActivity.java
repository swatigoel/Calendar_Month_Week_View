package com.alamkanak.weekview.sample;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.Day;
import com.alamkanak.weekview.EventRect;
import com.alamkanak.weekview.ExtendedCalendarView;
import com.alamkanak.weekview.ExtendedCalendarView.OnDayClickListener;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public abstract class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthLoaderListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener,
OnDayClickListener, View.OnClickListener, WeekView.EmptyViewClickListener, ExtendedCalendarView.OnMonthChaneListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private static final int TYPE_MONTH_VIEW = 4;
    private int mWeekViewType = TYPE_MONTH_VIEW;
    private WeekView mWeekView;
    private ExtendedCalendarView calendar;
//    private CompactCalendarView compactCalendarView;
    //private LinearLayout calendarViewLayout;
    private RecyclerView eventList;
    private EventAdapter eventAdapter;
    private AppBarLayout mAppBarLayout;
    //private LinearLayout eventListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_new);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//
//        getSupportActionBar().setCustomView(R.layout.header_layout);
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        //calendarViewLayout = (LinearLayout) findViewById(R.id.calendar_view_layout);
        calendar = (ExtendedCalendarView)findViewById(R.id.calendar);
//        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
      //  eventListLayout = (LinearLayout) findViewById(R.id.event_list_layout);
        eventList = (RecyclerView) findViewById(R.id.event_list);
        eventList.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setNestedScrollingEnabled(eventList,true);

        calendar.setOnDayClickListener(this);

        calendar.setMonthLoaderListener(this);

        calendar.setOnMonthChangeListener(this);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthLoaderListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        mWeekView.setEmptyViewClickListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        if (mWeekViewType == TYPE_MONTH_VIEW) {
            //calendarViewLayout.setVisibility(View.VISIBLE);
//            calendar.setVisibility(View.VISIBLE);
            updateView();
            mWeekView.setVisibility(View.GONE);
//            eventListLayout.setVisibility(View.VISIBLE);
        } else {
           // calendarViewLayout.setVisibility(View.GONE);
//            calendar.setVisibility(View.GONE);
            mWeekView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        if(view!=null) {
            view.setBackgroundResource(R.drawable.normal_day);
        }
        buildEventList(day);
    }

    private void buildEventList(Day day) {
        List<WeekViewEvent> events = day.events;
//        eventListLayout.setVisibility(View.VISIBLE);
        if (events == null || events.size() == 0) {
            eventList.setVisibility(View.GONE);
            findViewById(R.id.empty_label).setVisibility(View.VISIBLE);
            Calendar cal = Calendar.getInstance();
            cal.set(day.getYear(), day.getMonth(), day.getDay());
            showEventDetailsScreen(null, cal);
        } else {
            findViewById(R.id.empty_label).setVisibility(View.GONE);
            eventList.setVisibility(View.VISIBLE);
            if (eventAdapter == null) {
                eventAdapter = new EventAdapter(this, events, this);
                eventList.setAdapter(eventAdapter);
            } else {
                eventAdapter.setData(events);
            }
        }
    }


    @Override
    public void onMonthChange(Calendar cal, List<EventRect> mEventRects) {
//        findViewById(R.id.event_list_layout).setVisibility(View.GONE);
        if (calendar.mAdapter != null) {
            if (calendar.mAdapter.currentDay != null) {
                buildEventList(calendar.mAdapter.currentDay);
            }
        }
        String name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+cal.get(Calendar.YEAR);
        getSupportActionBar().setTitle(name);
    }

    public void updateView() {
        calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
        calendar.getEvents();
        calendar.refreshCalendar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    TextView monthView, weekView, dayView;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.calendar_action);

        // Retrieve the action-view from menu

        View v = MenuItemCompat.getActionView(actionViewItem);

        // Find the button within action-view

        monthView = (TextView) v.findViewById(R.id.month_view);
        monthView.setOnClickListener(this);

        weekView = (TextView) v.findViewById(R.id.week_view);
        weekView.setOnClickListener(this);

        dayView = (TextView) v.findViewById(R.id.day_view);
        dayView.setOnClickListener(this);

        monthView.setBackground(ContextCompat.getDrawable(this, R.drawable.today));
        weekView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
        dayView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            /*case R.id.action_today:
                if (mWeekView.getVisibility() == View.VISIBLE) {
                    mWeekView.goToToday();
                }
                return true;*/
           /* case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    mWeekView.setVisibility(View.VISIBLE);
                    calendar.setVisibility(View.GONE);
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;*/
            /*case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    mWeekView.setVisibility(View.VISIBLE);
                    calendar.setVisibility(View.GONE);
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;*/
            /*case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    mWeekView.setVisibility(View.VISIBLE);
                    calendar.setVisibility(View.GONE);
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_month_view:
                if (mWeekViewType != TYPE_MONTH_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_MONTH_VIEW;
                    mWeekView.setVisibility(View.GONE);
                    calendar.setVisibility(View.VISIBLE);

                    updateView();
                }
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    protected String getEventTitle(Calendar time, Calendar endTime) {
        return String.format("Event of %02d:%02d %s/%d :: %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH),
          endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), endTime.get(Calendar.MONTH)+1, endTime.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        showEventDetailsScreen(event, null);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time, time), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        Toast.makeText(this, "Empty view clicked: " + getEventTitle(time, time), Toast.LENGTH_SHORT).show();
        showEventDetailsScreen(null, time);
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.month_view) {
            if (mWeekViewType != TYPE_MONTH_VIEW) {
                mWeekViewType = TYPE_MONTH_VIEW;
                mWeekView.setVisibility(View.GONE);
                //calendarViewLayout.setVisibility(View.VISIBLE);
//                eventListLayout.setVisibility(View.VISIBLE);
                mAppBarLayout.setVisibility(View.VISIBLE);
//                calendar.setVisibility(View.VISIBLE);
                updateView();
                monthView.setBackground(ContextCompat.getDrawable(this, R.drawable.today));
                weekView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
                dayView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
            }
        } else if (v.getId() == R.id.week_view) {
            if (mWeekViewType != TYPE_WEEK_VIEW) {
                setupDateTimeInterpreter(true);
                mWeekView.setVisibility(View.VISIBLE);
               // calendarViewLayout.setVisibility(View.GONE);
                eventList.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.GONE);
//                calendar.setVisibility(View.GONE);
                mWeekViewType = TYPE_WEEK_VIEW;
                mWeekView.setNumberOfVisibleDays(7);

                getSupportActionBar().setTitle(getTitle());
                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

                monthView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
                weekView.setBackground(ContextCompat.getDrawable(this, R.drawable.today));
                dayView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
            }
        } else if (v.getId() == R.id.day_view) {
            if (mWeekViewType != TYPE_DAY_VIEW) {
                setupDateTimeInterpreter(false);
                mWeekView.setVisibility(View.VISIBLE);
               // calendarViewLayout.setVisibility(View.GONE);
                eventList.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.GONE);
//                calendar.setVisibility(View.GONE);
                mWeekViewType = TYPE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(1);
                getSupportActionBar().setTitle(getTitle());
                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

                monthView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
                weekView.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_day));
                dayView.setBackground(ContextCompat.getDrawable(this, R.drawable.today));
            }
        } else if (v.getId() == android.R.id.text1) {
            WeekViewEvent event = (WeekViewEvent) v.getTag();
            showEventDetailsScreen(event, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            updateView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showEventDetailsScreen(WeekViewEvent event, Calendar startTime) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        Bundle bundle = new Bundle();
        if (event != null) {
            bundle.putSerializable("event",event);
        } else if (startTime != null) {
            bundle.putSerializable("start",startTime);
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }
}
