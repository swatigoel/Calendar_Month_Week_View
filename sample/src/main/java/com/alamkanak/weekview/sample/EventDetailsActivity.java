package com.alamkanak.weekview.sample;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.Day;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.alamkanak.weekview.WeekViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener, MonthLoader.MonthLoaderListener{

  WeekViewEvent event;
  boolean isEditMode;
  private EditText titleView;
  private TextView startDateView;
  private TextView startTimeView;
  private TextView endDateView;
  private TextView endTimeView;
  private EditText locationView;
  private EditText agendaView;
  private WeekViewLoader mWeekViewLoader;

  private Calendar originalStartTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.event_details_layout);

    Intent intent = getIntent();
    if (intent.getExtras() != null) {
      Bundle bundle = intent.getExtras();
      if (bundle.containsKey("event")) {
        event = (WeekViewEvent) bundle.get("event");
      }

      if (bundle.containsKey("start")) {
        originalStartTime = (Calendar) bundle.get("start");
      }
    }

    titleView = (EditText) findViewById(R.id.title);
    startDateView = (TextView) findViewById(R.id.start_date);
    startTimeView = (TextView) findViewById(R.id.start_time);
    endDateView = (TextView) findViewById(R.id.end_date);
    endTimeView = (TextView) findViewById(R.id.end_time);

    locationView = (EditText) findViewById(R.id.location);

    agendaView = (EditText) findViewById(R.id.agenda);

    if (event != null) {
      isEditMode = false;
    } else {
      isEditMode = true;
    }

    mWeekViewLoader = new MonthLoader(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (event != null) {
      setData();
      setStartEndTime(event.getStartTime(), event.getEndTime());
      createReadOnlyView();
    } else {
      if (originalStartTime == null) {
        originalStartTime = Calendar.getInstance();
        originalStartTime.setTimeInMillis(System.currentTimeMillis());
      }
      Calendar endTime = (Calendar) originalStartTime.clone();
      endTime.setTimeInMillis(originalStartTime.getTimeInMillis() + (1000 * 60 * 60 * 2));
      setStartEndTime(originalStartTime, endTime);
      createEditableView();
    }
    findViewById(R.id.save).setVisibility(View.VISIBLE);
    findViewById(R.id.save).setOnClickListener(this);
  }

  private void setData() {
    findViewById(R.id.title_layout).setBackgroundColor(event.getColor());
    titleView.setText(event.getName());
    findViewById(R.id.color_icon).setBackgroundColor(event.getColor());
    locationView.setText(event.getLocation());
    agendaView.setText(event.getAgenda());
  }

  private void setStartEndTime(Calendar start, Calendar end) {
    String startTime = formatTime(start);
    int commaIndex = startTime.lastIndexOf(",");
    if (commaIndex != -1) {
      String date = startTime.substring(0, commaIndex);
      String time = startTime.substring(commaIndex+1, startTime.length());
      startDateView.setText(date);
      startTimeView.setText(time);
    } else {
      startDateView.setText(startTime);
    }
    String endTime = formatTime(end);
    commaIndex = endTime.lastIndexOf(",");
    if (commaIndex != -1) {
      String date = endTime.substring(0, commaIndex);
      String time = endTime.substring(commaIndex+1, endTime.length());
      endDateView.setText(date);
      endTimeView.setText(time);
    } else {
      endDateView.setText(endTime);
    }
  }

  private String formatTime(Calendar time) {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy,hh:mm a");
//    Mon, Aug 15, 2016, 06:00 PM06:00 PM
    String msg = sdf.format(time.getTime());
    /*msg += String.format("%02d:%02d", time.get(Calendar.HOUR),
      time.get(Calendar.MINUTE));
    String AM_PM = " AM";
    if (time.get(Calendar.AM_PM) == Calendar.PM) {
      AM_PM = " PM";
    }
    msg += AM_PM;*/
    return msg;
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.save) {
      String tag = (String) v.getTag();
      if (tag != null) {
        if (tag.equals("edit")) {
          createEditableView();
        } else if (tag.equals("save")) {
          String title = titleView.getText().toString();
          if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Please enter event title", Toast.LENGTH_SHORT).show();
            return;
          }

          String location = locationView.getText().toString();
          if (location == null || location.isEmpty()) {
            Toast.makeText(this, "Please enter event location", Toast.LENGTH_SHORT).show();
            return;
          }

          Calendar startTime = originalStartTime;
          if (startTime == null) {
            startTime.setTimeInMillis(System.currentTimeMillis());
          }
          Calendar endTime = (Calendar) startTime.clone();
          endTime.setTimeInMillis(startTime.getTimeInMillis() + (1000 * 60 * 60 * 2));

          WeekViewEvent createdEvent;

          if (event == null) {
            createdEvent = new WeekViewEvent(WeekViewUtil.eventId++, title, startTime, endTime);
          } else {
            createdEvent = new WeekViewEvent(event.getId(), title, startTime, endTime);
          }
          createdEvent.setColor(ContextCompat.getColor(this, R.color.event_color_01));
          createdEvent.setLocation(location);

          String agenda = agendaView.getText().toString();
          if (agenda != null && !agenda.isEmpty()) {
            createdEvent.setAgenda(agenda);
          }

          if (event != null && !event.getStartTime().equals(startTime)) {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(event.getStartTime());
            int year = periodToFetch / 12;
            int month = periodToFetch % 12 + 1;
            String monthKey = "" + (month - 1) + "-" + year;

            List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);

            if (eventListByMonth != null && eventListByMonth.contains(event)) {
              eventListByMonth.remove(event);
            }
            WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);
          }


          WeekViewUtil.masterEvents.put("" + createdEvent.getId(), createdEvent);

          int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(startTime);
          int year = periodToFetch / 12;
          int month = periodToFetch % 12 + 1;
          String monthKey = "" + (month - 1) + "-" + year;

          List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);
          if (eventListByMonth == null) {
            eventListByMonth = new ArrayList<>();
          }
          eventListByMonth.add(createdEvent);
          WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);
          setResult(RESULT_OK);
          finish();
        }
      }
    } else if (v.getId() == R.id.start_date_time) {

    } else if (v.getId() == R.id.end_date_time) {

    }
  }

  @Override
  public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth) {
    return null;
  }

  private void createReadOnlyView() {
    titleView.setEnabled(false);
    startDateView.setEnabled(false);
    startTimeView.setEnabled(false);
    endDateView.setEnabled(false);
    endTimeView.setEnabled(false);
    locationView.setEnabled(false);
    agendaView.setEnabled(false);
    findViewById(R.id.save).setTag("edit");
    ((Button) findViewById(R.id.save)).setText("Edit");
  }

  private void createEditableView() {
    titleView.setEnabled(true);
    startDateView.setEnabled(true);
    startTimeView.setEnabled(true);
    endDateView.setEnabled(true);
    endTimeView.setEnabled(true);
    locationView.setEnabled(true);
    agendaView.setEnabled(true);
    findViewById(R.id.save).setTag("save");
    ((Button) findViewById(R.id.save)).setText("Save");
    findViewById(R.id.start_date_time).setOnClickListener(this);
    findViewById(R.id.end_date_time).setOnClickListener(this);
  }
}
