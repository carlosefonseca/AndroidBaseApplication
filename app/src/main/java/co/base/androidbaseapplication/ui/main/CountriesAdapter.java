package co.base.androidbaseapplication.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.base.androidbaseapplication.R;
import co.base.androidbaseapplication.model.entities.Country;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountryViewHolder> {

    private List<Country> mCountries;

    public interface OnItemClickListener {
        void onCountryItemClicked(Country country);
    }

    private OnItemClickListener mOnItemClickListener;

    @Inject
    public CountriesAdapter() {
        mCountries = new ArrayList<>();
    }

    public void setCountries(List<Country> countries) {
        validateCountriesCollection(countries);
        mCountries = countries;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private void validateCountriesCollection(List<Country> countriesCollection) {
        if (countriesCollection == null) {
            throw new IllegalArgumentException("The list cannot be null");
        }
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CountryViewHolder holder, int position) {
        final Country country = mCountries.get(position);
        holder.nameTextView.setText(country.getmCountryName());
        Picasso.with(holder.itemView.getContext())
                .load("http://www.geognos.com/api/en/countries/flag/"
                        + country.getmCountryCode() + ".png")
                //.error(R.drawable.__leak_canary_icon)
                .into(holder.flagView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CountriesAdapter.this.mOnItemClickListener != null) {
                    CountriesAdapter.this.mOnItemClickListener.onCountryItemClicked(country);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.view_flag) ImageView flagView;
        @Bind(R.id.text_name) TextView nameTextView;

        public CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
