package grab.com.thuexetoancau.driver.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;


import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by DatNT on 7/19/2017.
 */

public class ScheduleTripAdapter extends RecyclerView.Adapter<ScheduleTripAdapter.ViewHolder>{
    private Context mContext;
    private List<Trip> arrayTrip;
    private ItemClickListener listener;

    public ScheduleTripAdapter(Context context, ArrayList<Trip> trips) {
        mContext = context;
        this.arrayTrip = trips;
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ScheduleTripAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_schedule_trip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ScheduleTripAdapter.ViewHolder holder, final int position) {
        holder.txtSource.setText(arrayTrip.get(position).getListStopPoints().get(0).getFullPlace());
        int size = arrayTrip.get(position).getListStopPoints().size();
        holder.txtDestination.setText(arrayTrip.get(position).getListStopPoints().get(size -1).getFullPlace());
        DateTimeFormatter dtf;
        if (arrayTrip.get(position).getStartTime().contains(".")) {
            dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }else{
            dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        }
        DateTime dateTime = dtf.parseDateTime(arrayTrip.get(position).getStartTime());
        holder.txtDate.setText(dateTime.getDayOfMonth()+"/"+dateTime.getMonthOfYear()+"/"+dateTime.getYear());
        holder.txtTime.setText(dateTime.getHourOfDay()+":"+dateTime.getMinuteOfHour());
        holder.txtDistance.setText(CommonUtilities.convertToKilometer(arrayTrip.get(position).getDistance()));
        holder.txtCarSize.setText(arrayTrip.get(position).getCarSize()+" chỗ");
        holder.txtPrice.setText(CommonUtilities.convertCurrency(arrayTrip.get(position).getPrice())+" vnđ");
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(arrayTrip.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (arrayTrip == null) return 0;
        else return arrayTrip.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtSource;
        TextView txtDestination;
        TextView txtDate;
        TextView txtTime;
        TextView txtDistance;
        TextView txtCarSize;
        TextView txtPrice;
        CardView layoutRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutRoot = (CardView) itemView.findViewById(R.id.layout_root);
            txtSource = (TextView) itemView.findViewById(R.id.txt_source);
            txtDestination = (TextView) itemView.findViewById(R.id.txt_destination);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            txtDistance = (TextView) itemView.findViewById(R.id.txt_distance);
            txtCarSize = (TextView) itemView.findViewById(R.id.txt_car_type);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        }
    }

    public interface ItemClickListener{
        void onItemClicked(Trip trip);
    }
}