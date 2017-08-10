package grab.com.thuexetoancau.driver.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import grab.com.thuexetoancau.driver.R;

/**
 * Created by DatNT on 8/10/2017.
 */

public class CustomerInfoLayout extends LinearLayout {
    private Context mContext;

    public CustomerInfoLayout(Context context) {
        super(context);
        this.mContext = context;
        initLayout();
    }

    public CustomerInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public CustomerInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.layout_customer_information, this, true);
    }
}
