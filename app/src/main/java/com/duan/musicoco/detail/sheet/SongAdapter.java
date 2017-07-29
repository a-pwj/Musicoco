package com.duan.musicoco.detail.sheet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.duan.musicoco.R;
import com.duan.musicoco.app.SongInfo;
import com.duan.musicoco.app.interfaces.OnThemeChange;
import com.duan.musicoco.preference.Theme;
import com.duan.musicoco.util.ColorUtils;
import com.duan.musicoco.util.StringUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by DuanJiaNing on 2017/7/25.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements OnThemeChange {


    private boolean isCurrentSheetPlaying = false;
    private int currentIndex;
    private Context context;

    private final List<DataHolder> data;

    private int mainTC;
    private int vicTC;
    private final int choiceC;

    private OnMoreClickListener moreClickListener;

    public SongAdapter(Context context, List<DataHolder> data) {
        this.context = context;
        this.data = data;
        this.choiceC = context.getResources().getColor(R.color.item_select_color);
    }

    public interface OnMoreClickListener {
        void onMore(ViewHolder view, DataHolder data);
    }

    public void setOnMoreClickListener(OnMoreClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }

    public DataHolder getItem(int pos) {
        return data.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sheet_songs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DataHolder dataHolder = getItem(position);
        SongInfo info = dataHolder.info;

        if (moreClickListener != null) {
            holder.more.setTag(info);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreClickListener.onMore(holder, dataHolder);
                }
            });
        }
        Glide.with(context)
                .load(info.getAlbum_path())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.default_song)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .crossFade()
                .into(holder.image);

        String number = String.valueOf(position + 1);
        holder.number.setText(number);
        String name = info.getTitle();
        holder.name.setText(name);
        String arts = info.getArtist();
        holder.arts.setText(arts);
        String duration = StringUtils.getGenTimeMS((int) info.getDuration());
        holder.duration.setText(duration);

        bindStatAndColors(holder, position, dataHolder.isFavorite);
    }

    private void bindStatAndColors(ViewHolder holder, int position, boolean isFavorite) {

        int mtc;
        int vtc;
        if (isCurrentSheetPlaying && position == currentIndex) {
            mtc = vtc = choiceC;
            setNumberAsImage(true, holder.number, vtc);
        } else {
            mtc = mainTC;
            vtc = vicTC;
            setNumberAsImage(false, holder.number, vtc);
        }

        holder.name.setTextColor(mtc);
        holder.arts.setTextColor(vtc);
        holder.duration.setTextColor(vtc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isFavorite) {
                holder.favorite.getDrawable().setTint(choiceC);
            } else {
                holder.favorite.getDrawable().setTint(vicTC);
            }

            holder.more.getDrawable().setTint(vtc);
        } else {
            holder.favorite.setVisibility(View.GONE);
        }

    }

    private void setNumberAsImage(boolean b, TextView number, int vtc) {

        if (b) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getDrawable(R.drawable.ic_volume_up_black_24dp);
                if (drawable != null) {
                    drawable.setTint(vtc);
                }
            } else {
                drawable = context.getResources().getDrawable(R.drawable.ic_volume_up_black_24dp);
            }

            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                number.setCompoundDrawables(drawable, null, null, null);
                number.setText("");
            }
        } else {
            number.setTextColor(vtc);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void themeChange(Theme theme, int[] colors) {

        mainTC = colors[0];
        vicTC = colors[1];

        notifyDataSetChanged();
    }

    public void update(Boolean isCurrentSheetPlaying, int currentIndex) {
        this.isCurrentSheetPlaying = isCurrentSheetPlaying;
        this.currentIndex = currentIndex;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ImageView favorite;
        TextView duration;
        TextView number;
        TextView arts;
        ImageButton more;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.sheet_song_item_name);
            image = (ImageView) itemView.findViewById(R.id.sheet_song_item_image);
            favorite = (ImageView) itemView.findViewById(R.id.sheet_song_item_favorite);
            duration = (TextView) itemView.findViewById(R.id.sheet_song_item_duration);
            number = (TextView) itemView.findViewById(R.id.sheet_song_item_number);
            arts = (TextView) itemView.findViewById(R.id.sheet_song_item_arts);
            more = (ImageButton) itemView.findViewById(R.id.sheet_song_item_more);
        }
    }

    public static class DataHolder {
        SongInfo info;
        boolean isFavorite;

        public DataHolder(SongInfo info, boolean isFavorite) {
            this.info = info;
            this.isFavorite = isFavorite;
        }
    }
}