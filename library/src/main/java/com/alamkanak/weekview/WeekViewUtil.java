package com.alamkanak.weekview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jesse on 6/02/2016.
 */
public class WeekViewUtil {

    public static long eventId = 0;

    // id with events
    // month with events
    //by month by day
    public static HashMap<String, WeekViewEvent> masterEvents = new HashMap();
    public static HashMap<String, List<WeekViewEvent>> monthMasterEvents = new HashMap();

    /////////////////////////////////////////////////////////////////
    //
    //      Helper methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Checks if two times are on the same day.
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns a calendar instance at the start of this day
     * @return the calendar instance
     */
    public static Calendar today(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    public static void sortAndCacheEvents(List<WeekViewEvent> events, List<EventRect> mEventRects) {
        sortEvents(events);
        for (WeekViewEvent event : events) {
            cacheEvent(event, mEventRects);
        }
    }

    private static void sortEvents(List<WeekViewEvent> events) {
        Collections.sort(events, new Comparator<WeekViewEvent>() {
            @Override
            public int compare(WeekViewEvent event1, WeekViewEvent event2) {
                long start1 = event1.getStartTime().getTimeInMillis();
                long start2 = event2.getStartTime().getTimeInMillis();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
                if (comparator == 0) {
                    long end1 = event1.getEndTime().getTimeInMillis();
                    long end2 = event2.getEndTime().getTimeInMillis();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }
                return comparator;
            }
        });
    }

    private static void cacheEvent(WeekViewEvent event, List<EventRect> mEventRects) {
        if(event.getStartTime().compareTo(event.getEndTime()) >= 0)
            return;
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for(WeekViewEvent splitedEvent: splitedEvents){
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }
    }
}
