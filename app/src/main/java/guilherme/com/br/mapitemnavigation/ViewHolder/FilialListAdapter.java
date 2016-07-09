package guilherme.com.br.mapitemnavigation.ViewHolder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import guilherme.com.br.mapitemnavigation.Application;
import guilherme.com.br.mapitemnavigation.MainActivity;
import guilherme.com.br.mapitemnavigation.POJO.Filial;
import guilherme.com.br.mapitemnavigation.R;

/**
 * Created by guilh on 02/03/2016.
 */
public class FilialListAdapter extends RecyclerView.Adapter<FilialListAdapter.ViewHolderFilial> {

    private Context mContext;
    private MainActivity mActivity;

    private List<Filial> recordSet;


    /***********************************************************************************************
     Constructor
     ***********************************************************************************************/

    public FilialListAdapter(List<Filial> _recordSet, MainActivity _activity){
        mContext = _activity.getBaseContext();
        mActivity = _activity;
        recordSet = _recordSet;
    }


    @Override
    public ViewHolderFilial onCreateViewHolder(ViewGroup parent, int viewType) {
        int cardLayout = R.layout.view_holder_filial;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(cardLayout, parent, false);
        return new ViewHolderFilial(itemView);

    }

    private ViewHolderFilial _holder;

    @Override
    public void onBindViewHolder(ViewHolderFilial holder, final int position) {
        _holder = holder;
        ViewHolderFilial filialViewHolder = (ViewHolderFilial) holder;
        filialViewHolder.setData((Filial) recordSet.get(position));
        //filialViewHolder.setCurrentLat(recordSet.get(position).getLatitude());
        //filialViewHolder.setCurrentLat(recordSet.get(position).getLongitude());
    }


    @Override
    public int getItemCount() {
        //retona o número de itens da lista
        if (recordSet != null){
            return recordSet.size();
        }else{
            return 0;
        }
    }


    /* =============================================================
    ViewHolder
    ===============================================================*/

    public class ViewHolderFilial extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mLogo;
        private TextView mTitulo;
        private TextView mTexto1;
        private TextView mTexto2;
        private ImageView addressButton;
        public Filial filial;
        public RatingBar ratingBar;

        public ViewHolderFilial(View v) {
            super(v);

            CardView mCardView = (CardView) v.findViewById(R.id.cv_item);
            mCardView.setOnClickListener(this);

            mLogo = (ImageView) mCardView.findViewById(R.id.logo);
            mTitulo = (TextView) mCardView.findViewById(R.id.titulo);
            mTexto1 = (TextView) mCardView.findViewById(R.id.texto1);
            mTexto2 = (TextView) mCardView.findViewById(R.id.texto2);
            addressButton = (ImageView) mCardView.findViewById(R.id.addressButton);
            ratingBar = (RatingBar) mCardView.findViewById(R.id.ratingBar);
        }

        public float GetDistanceFromCurrentPosition(double lat1,double lng1, double lat2, double lng2) {
            double earthRadius = 3958.75;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                    * Math.sin(dLng / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = earthRadius * c;
            //int meterConversion = 1609;
            //return new Float(dist * meterConversion).floatValue();
            return new Float(dist).floatValue();
        }

        public void setData(Filial _filial) {
            filial = _filial;

            if(filial.getLoja().getLogo() != null) {
                Uri uri = Uri.parse(filial.getLoja().getLogo());
                Picasso.with(mActivity).load(uri).into(this.mLogo);
            }

            this.mTitulo.setText(filial.getLoja().getNome_fantasia());

            this.mTexto1.setText(filial.getUnidade());
            this.mTitulo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            double lat1 = filial.getLoja().getLatitude();
            double lng1 = filial.getLoja().getLongitude();

            double lat2 = filial.getLatitude();
            double lng2 = filial.getLongitude();

            String distancia = new DecimalFormat("#.##").format(GetDistanceFromCurrentPosition(lat1, lng1, lat2, lng2));

            Log.i("Distancia: ", String.valueOf(distancia));

            this.mTexto2.setText(distancia + " Km");

        }


        public void onClick(View v){
                mActivity.expandMap(filial);
        }

    }


}
