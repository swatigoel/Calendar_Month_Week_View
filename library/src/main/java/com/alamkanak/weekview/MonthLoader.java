package com.alamkanak.weekview;

import java.util.Calendar;
import java.util.List;

public class MonthLoader implements WeekViewLoader {

    private MonthLoaderListener mOnMonthLoaderListener;

    public MonthLoader(MonthLoaderListener listener){
        this.mOnMonthLoaderListener = listener;
    }

    @Override
    public double toWeekViewPeriodIndex(Calendar instance){
        return instance.get(Calendar.YEAR) * 12 + instance.get(Calendar.MONTH) + (instance.get(Calendar.DAY_OF_MONTH) - 1) / 30.0;
    }

    @Override
    public List<WeekViewEvent> onLoad(int periodIndex){
        return mOnMonthLoaderListener.onMonthLoad(periodIndex / 12, periodIndex % 12 + 1);
    }

    public MonthLoaderListener getOnMonthLoaderListener() {
        return mOnMonthLoaderListener;
    }

    public void setOnMonthLoaderListener(MonthLoaderListener onMonthLoaderListener) {
        this.mOnMonthLoaderListener = onMonthLoaderListener;
    }

    public interface MonthLoaderListener {
        /**
         * Very important interface, it's the base to load events in the calendar.
         * This method is called three times: once to load the previous month, once to load the next month and once to load the current month.<br/>
         * <strong>That's why you can have three times the same event at the same place if you mess up with the configuration</strong>
         * @param newYear : year of the events required by the view.
         * @param newMonth : month of the events required by the view <br/><strong>1 based (not like JAVA API) --> January = 1 and December = 12</strong>.
         * @return a list of the events happening <strong>during the specified month</strong>.
         */
        List<WeekViewEvent> onMonthLoad(int newYear, int newMonth);
    }
}
