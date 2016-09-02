package com.alamkanak.weekview.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.List;

/**
  Created by mangospring5 on 14/7/16.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

  private final Context mContext;
  private final List<WeekViewEvent> eventList;
  private final View.OnClickListener onClickListener;
  LayoutInflater inflater;

  public EventAdapter(Context context, List<WeekViewEvent> events, View.OnClickListener listener) {
    this.mContext = context;
    this.eventList = new ArrayList<>();
    eventList.addAll(events);
     inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    onClickListener = listener;
  }



  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    return new ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    WeekViewEvent event = eventList.get(position);
    if (event != null) {
      holder.eventTitle.setText(event.getName());
      holder.eventTitle.setBackgroundColor(event.getColor());
      holder.eventTitle.setTag(event);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return eventList.size();
  }



  public void setData(List<WeekViewEvent> events) {
    if (this.eventList == null) {
      this.eventList.clear();
    }
    this.eventList.clear();
    this.eventList.addAll(events);
    notifyDataSetChanged();
  }

  class  ViewHolder extends RecyclerView.ViewHolder{
    TextView eventTitle;
    public ViewHolder(View itemView) {
      super(itemView);
       eventTitle = (TextView) itemView.findViewById(android.R.id.text1);
      eventTitle.setOnClickListener(onClickListener);
    }
  }
}
