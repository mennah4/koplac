package koplac.vyskovnice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import koplac.vyskovnice.entities.Peak;

import java.util.List;

/**
 * Adapter of the peak list
 *
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class PeaksAdapter extends BaseAdapter {

    private Context parentContext;
    private LayoutInflater inflater;

    private List<Peak> peaks;
    private Peak peak;

    private ImageView ivPicture;
    private TextView tvName;

    private final int IV_PICTURE = R.id.ivPicture;
    private final int TV_NAME = R.id.tvName;

    /**
     * Class constructor
     * @param parentContext father context
     * @param peaks         list of peak object
     */
    public PeaksAdapter(Context parentContext, List<Peak> peaks) {
        this.parentContext = parentContext;
        this.inflater = LayoutInflater.from(parentContext);

        this.peaks = peaks;
    }

    /**
     * Class constructor
     * @param parentContext father context
     * @param peakIds       list of peak object
     * @param aux
     */
    public PeaksAdapter(Context parentContext, List<Integer> peakIds, String aux) {
        this.parentContext = parentContext;
        this.inflater = LayoutInflater.from(parentContext);

        this.peaks = Peak.seekPeaks(peakIds);
    }

    /**
     *Returns the element list
     */
    public List<Peak> getPeaks() {
        return this.peaks;
    }

    /**
     * Return the number of elements
     */
    @Override
    public int getCount() {
        return peaks.size();
    }

    /**
     *Returns a elements given its position
     */
    @Override
    public Object getItem(int position) {
        return peaks.get(position);
    }

    /**
     * Returns an element id given its position
     */
    @Override
    public long getItemId(int position) {
        return peaks.get(position).getId();
    }

    /**
     * Fill the list with all the elements
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.schema_peak_list, null);

        peak = peaks.get(position);
        ivPicture = (ImageView) convertView.findViewById(IV_PICTURE);
        tvName = (TextView) convertView.findViewById(TV_NAME);

        ivPicture.setImageResource(peak.getPicture());
        tvName.setText(peak.getName().toString());

        return convertView;
    }
}
