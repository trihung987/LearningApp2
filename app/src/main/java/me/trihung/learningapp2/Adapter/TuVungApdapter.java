package me.trihung.learningapp2.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.Entity.TuVung;
import me.trihung.learningapp2.MainActivity;
import me.trihung.learningapp2.My_Interface.InterfaceClickItemTuVungListener;
import me.trihung.learningapp2.R;
import me.trihung.learningapp2.UI.Utils.TextToSpeechUtils;

import java.util.List;

public class TuVungApdapter extends RecyclerView.Adapter<TuVungApdapter.TuVungApdapterHolder>{
    private List<TuVung> tuVungs;
    private Database db;

    private TextToSpeechUtils textToSpeechUtils;
    private InterfaceClickItemTuVungListener interfaceClickItemTuVungListener;

    public TuVungApdapter(Context context,List<TuVung> tuVungs, InterfaceClickItemTuVungListener interfaceClickItemTuVungListener) {
        this.tuVungs = tuVungs;
        this.interfaceClickItemTuVungListener = interfaceClickItemTuVungListener;
        db = new Database(context);
        textToSpeechUtils = new TextToSpeechUtils(context);
    }

    @NonNull
    @Override
    public TuVungApdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tuvung, parent, false);
        return new TuVungApdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TuVungApdapterHolder holder, int position) {
        final TuVung tuVung = tuVungs.get(position);
        if (tuVung == null){
            return;
        }
        holder.tvTiengAnh.setText(tuVung.getTiengAnh()+" "+tuVung.getGroup());
        holder.tvTiengViet.setText(tuVung.getTiengViet());
        holder.chkLuuTuVung.setChecked(db.checkTuVungTrongSoTay(tuVung.getTiengAnh(),MainActivity.getIdND()));
        holder.tvPhienAm.setText(tuVung.getPhienAm());

        holder.sound.setOnClickListener(v->{
            holder.sound.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() ->
                    holder.sound.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            ).start();
            textToSpeechUtils.speak(tuVung.getTiengAnh());
        });

        holder.layout_item_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceClickItemTuVungListener.onClickItemTuVung(tuVung);
            }
        });

        holder.chkLuuTuVung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.luuTuVungVaoSoTay(holder.chkLuuTuVung.getContext(), MainActivity.getIdND(), db.getIdTheoTiengAnh(tuVung.getTiengAnh()));
                } else{
                    db.xoaTuVungKhoiSoTay(holder.chkLuuTuVung.getContext(), MainActivity.getIdND(), db.getIdTheoTiengAnh(tuVung.getTiengAnh()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (tuVungs != null) {
            return tuVungs.size();
        }
        return 0;
    }


    public class TuVungApdapterHolder extends RecyclerView.ViewHolder{
        private androidx.cardview.widget.CardView layout_item_TV;
        private TextView tvTiengAnh;
        private TextView tvTiengViet;
        private TextView tvPhienAm;
        private CheckBox chkLuuTuVung;

        private ImageButton sound;


        public TuVungApdapterHolder(@NonNull View itemView) {
            super(itemView);
            tvTiengAnh = itemView.findViewById(R.id.textTiengAnh);
            tvTiengViet = itemView.findViewById(R.id.textViet);
            layout_item_TV = itemView.findViewById(R.id.layout_item_TV);
            chkLuuTuVung = itemView.findViewById(R.id.chkLuuTuVung);
            tvPhienAm = itemView.findViewById(R.id.textTranscription);
            sound = itemView.findViewById(R.id.sound_button);

        }
    }
}
