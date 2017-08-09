package grab.com.thuexetoancau.driver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;


/**
 * Created by DatNT on 7/19/2017.
 */

public class BookingAroundAdapter extends RecyclerView.Adapter<BookingAroundAdapter.ViewHolder> {
    private Context mContext;
    private List<Trip> arrayTrip;
    private OnItemClickListener listener;

    public BookingAroundAdapter(Context context, ArrayList<Trip> vehicle) {
        mContext = context;
        this.arrayTrip = vehicle;
    }

    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_car_booking, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       holder.txtSource.setText(arrayTrip.get(position).getListStopPoints().get(0).getFullPlace());
        int size = arrayTrip.get(position).getListStopPoints().size();
        holder.txtDestination.setText(arrayTrip.get(position).getListStopPoints().get(size-1).getFullPlace());
        holder.txtCarSize.setText(arrayTrip.get(position).getCarSize()+" chỗ");
        holder.txtTripType.setText(CommonUtilities.getTripType(arrayTrip.get(position).getTripType()));
        holder.txtCustomer.setText(arrayTrip.get(position).getCustomerName());
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


    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtSource;
        TextView txtDestination;
        TextView txtCarSize;
        TextView txtTripType;
        ImageView imgAccept;
        TextView txtCustomer;
        TextView txtStartTime;
        TextView txtBackTime;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSource = (TextView) itemView.findViewById(R.id.txt_from);
            txtDestination = (TextView) itemView.findViewById(R.id.txt_to);
            txtCarSize = (TextView) itemView.findViewById(R.id.txt_car_type);
            txtTripType = (TextView) itemView.findViewById(R.id.txt_trip_type);
            txtCustomer = (TextView) itemView.findViewById(R.id.txt_customer_name);
            imgAccept = (ImageView) itemView.findViewById(R.id.btn_accept);
        }
    }

    public interface OnItemClickListener{
        void onClicked(Trip trip);
    }
}