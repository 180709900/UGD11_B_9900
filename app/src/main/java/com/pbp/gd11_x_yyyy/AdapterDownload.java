package com.pbp.gd11_x_yyyy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.Status;
import com.pbp.gd11_x_yyyy.ui.download.UtilityPR;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class AdapterDownload extends RecyclerView.Adapter<AdapterDownload.adapterDownloadViewHolder> {

    private List<Download> listDownload;
    private Context context;
    private View view;
    private  int index;
    private static String dirPath;
    private int idDownload;
    public AdapterDownload(Context context, List<Download> listDownload) {
        this.context            = context;
        this.listDownload           = listDownload;
    }



    @NonNull
    @Override
    public AdapterDownload.adapterDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.download_item, parent, false);
        PRDownloader.initialize(view.getContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(view.getContext(), config);
        dirPath= UtilityPR.getRootDirPath(view.getContext());
        return new AdapterDownload.adapterDownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDownload.adapterDownloadViewHolder holder, int position) {
        final Download download = listDownload.get(position);
        holder.tvNamaPertama.setText(download.getNamaFile());
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PRDownloader.cancel(idDownload);
            }
        });
        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Status.RUNNING == PRDownloader.getStatus(idDownload)) {
                    PRDownloader.pause(idDownload);
                    return;
                }
                holder.pb.setIndeterminate(true);
                holder.pb.getIndeterminateDrawable().setColorFilter(Color.BLUE,
                        PorterDuff.Mode.SRC_IN);
                if (Status.PAUSED == PRDownloader.getStatus(idDownload)) {
                    PRDownloader.resume(idDownload);
                    return;
                }
                idDownload =
                        PRDownloader.download(download.getUrl(),
                                dirPath, download.getNamaFile())
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {
                                        holder.pb.setIndeterminate(false);
                                        holder.btnStart.setEnabled(true);
                                        holder.btnCancel.setEnabled(true);
                                        holder.btnStart.setText("Hentikan");
                                        FancyToast.makeText(context,"Download dimulai!",FancyToast.LENGTH_SHORT,FancyToast.INFO,false).show();
                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {
                                        holder.btnStart.setText("Teruskan");
                                        FancyToast.makeText(context,"Download dihentikan sementara!",FancyToast.LENGTH_SHORT,FancyToast.INFO,false).show();
                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {
                                        holder.btnStart.setEnabled(true);
                                        holder.btnCancel.setEnabled(false);
                                        holder.btnStart.setText("Download");
                                        holder.tvNamaPertama.setText("");
                                        idDownload=0;
                                        holder.pb.setProgress(0);
                                        holder.pb.setIndeterminate(false);
                                        FancyToast.makeText(context,"File batal didownload !",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        holder.tvNamaPertama.setText(UtilityPR.getProgressDisplayLine(progress.currentBytes,
                                                progress.totalBytes));
                                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                        holder.pb.setProgress((int) progressPercent);
                                        holder.pb.setIndeterminate(false);
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        holder.btnStart.setEnabled(false);
                                        holder.btnCancel.setEnabled(false);
                                        holder.btnStart.setBackgroundColor(Color.GRAY);
                                        holder.btnCancel.setText("Berhasil");
                                        holder.btnStart.setText("Downloaded");
                                        FancyToast.makeText(context,"File berhasil didownload!",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();
                                    }
                                    @Override
                                    public void onError(Error error) {
                                        holder.btnStart.setEnabled(true);
                                        holder.btnCancel.setEnabled(false);
                                        holder.btnStart.setText("Download");
                                        holder.tvNamaPertama.setText("");
                                        idDownload=0;
                                        holder.pb.setIndeterminate(false);
                                        holder.pb.setProgress(0);
                                        FancyToast.makeText(context,"Kesalahan Jaringan!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    }
                                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return (listDownload != null) ? listDownload.size() : 0;
    }

    public class adapterDownloadViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamaPertama;
        private Button btnCancel ,btnStart;
        private ProgressBar pb;

        public adapterDownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaPertama = itemView.findViewById(R.id.tvNamaPertama);
            pb = itemView.findViewById(R.id.pb1);
            btnCancel=itemView.findViewById(R.id.btnCancelPertama);
            btnStart=itemView.findViewById(R.id.btnStartPertama);

        }
    }

}
