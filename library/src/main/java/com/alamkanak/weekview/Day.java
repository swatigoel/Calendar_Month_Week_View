package com.alamkanak.weekview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.BaseAdapter;

//import com.mpc.cache.OTEventCache;
//import com.mpc.storage.OTEventDBTable;
//import com.mpc.storage.OTMasterDBManager;

public class Day implements Serializable{
	
	int startDay;
	int monthEndDay;
	int day;
	int year;
	int month;
	transient Context context;
	transient BaseAdapter adapter;
	int currentDay;
	public List<WeekViewEvent> events = null;
	
	public Day(Context context,int day, int year, int month){
		this.day = day;
		this.year = year;
		this.month = month;
		this.context = context;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end);
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}
	
	/**
	 * Add an event to the day
	 * 
	 * @param event
	 */
	//public void addEvent(OTEventModel event){
	//	events.add(event);
	//}
	
	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void setStartDay(int startDay){
		this.startDay = startDay;
		new GetEvents().execute();
	}
	
	public void setCurrentDay(int currentDay){
		this.currentDay = currentDay;
	}
	
	public int getStartDay(){
		return startDay;
	}
	
	public int getNumOfEvenets(){
		if (events == null) {
			return 0;
		}
		return events.size();
	}
	
	/**
	 * Returns a list of all the colors on a day
	 * 
	 * @return list of colors
	 */
	public Set<Integer> getColors(){
		Set<Integer> colors = new HashSet<Integer>();
		//for(OTEventModel event : events){
		//	colors.add(event.getColor());
		//}
		
		return colors;
	}
	
	/**
	 * Get all the events on the day
	 * 
	 * @return list of events
	 */
	//public OTModelVector<OTEventModel> getEvents(){
	//	return events;
	//}
	
	public void setAdapter(BaseAdapter adapter){
		this.adapter = adapter;
	}
	
	private class GetEvents extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			SQLiteDatabase db = null;
			try {
				//db = new OTMasterDBManager(context).getReadableDatabase();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				long currentDate = cal.getTime().getTime();
				//OTEventDBTable.loadEventsToCacheByCurrentDate(db, currentDate);
				//events.addAll(OTEventCache.getInstance().eventsList);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.close();
				}
			}
			/*Cursor c = context.getContentResolver().query(CalendarProvider.CONTENT_URI,new String[] {CalendarProvider.ID,CalendarProvider.EVENT,
					CalendarProvider.DESCRIPTION,CalendarProvider.EVENTTYPE,CalendarProvider.START,CalendarProvider.END,CalendarProvider.COLOR},"?>="+CalendarProvider.START_DAY+" AND "+ CalendarProvider.END_DAY+">=?",
					new String[] {String.valueOf(startDay),String.valueOf(startDay)}, null);
			if(c != null){
				if(c.moveToFirst()){
					do{
						OTEventModel event = new OTEventModel(c.getString(1), c.getString(0));
						event.setEventNotes(c.getString(2));
						String type = c.getString(3);
						OTEventType eventType = new OTEventType(type);
						eventType.setDrawable(eventType.getDraweble(type));
						event.setStartTime(c.getLong(4));
						event.setEndTime(c.getLong(5));
						event.setColor(c.getInt(6));
						event.setEventType(eventType);
						events.add(event);
						OTEventCache.getInstance().addEvent(event);
					}while(c.moveToNext());	
				}	
				c.close();
			}*/
			
			return null;
		}
		
		protected void onPostExecute(Void par){
			if(adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "day: "+day+", month: "+month+", year: "+year;
	}

	public void addDayEvent(WeekViewEvent event) {
		if (events.size() > 1) {
			for (WeekViewEvent e : events) {
				if (e.getId() == event.getId()) {
					events.remove(e);
					break;
				}
			}
			events.add(event);
		} else {
			events.add(event);
		}
	}

}
