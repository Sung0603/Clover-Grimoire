package com.example.clovergrimoire;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private final List<Character> characterList;
    private Context context;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(Character character);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Character character);
    }

    // Construtor principal utilizado na MainActivity com suporte a clique e exclusão por clique longo
    public CharacterAdapter(List<Character> characterList, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.characterList = characterList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    // Construtor com Listener simples
    public CharacterAdapter(List<Character> characterList, OnItemClickListener listener) {
        this.characterList = characterList;
        this.listener = listener;
    }

    // Construtor Padrão
    public CharacterAdapter(Context context, List<Character> characterList) {
        this.context = context;
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character_card, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);

        holder.tvName.setText(character.getName());
        holder.tvDetails.setText(character.getRace() + " • " + character.getHierarchy() + " (Nv. " + character.getLevel() + ")");
        holder.tvStats.setText("HP: " + character.getCurrentHp() + "/" + character.getMaxHp() +
                " | MP: " + character.getCurrentMp() + "/" + character.getMaxMp());

        if (character.getImageUri() != null) {
            try {
                holder.ivAvatar.setImageURI(character.getImageUri());
            } catch (Exception e) {
                holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Clique simples no Card para abrir a Ficha de Detalhes
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(character);
            } else if (context != null) {
                Intent intent = new Intent(context, CharacterDetailActivity.class);
                intent.putExtra("EXTRA_ID", character.getId());
                intent.putExtra("EXTRA_NAME", character.getName());
                intent.putExtra("EXTRA_RACE", character.getRace());
                intent.putExtra("EXTRA_HIERARCHY", character.getHierarchy());
                intent.putExtra("EXTRA_MAGIC", character.getMagicType());
                intent.putExtra("EXTRA_LEVEL", character.getLevel());
                if (character.getImageUri() != null) {
                    intent.putExtra("EXTRA_IMAGE_URI", character.getImageUri().toString());
                }
                context.startActivity(intent);
            }
        });

        // Clique longo no Card para acionar opção de exclusão
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(character);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName, tvDetails, tvStats;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_card_avatar);
            tvName = itemView.findViewById(R.id.tv_card_name);
            tvDetails = itemView.findViewById(R.id.tv_card_details);
            tvStats = itemView.findViewById(R.id.tv_card_stats);
        }
    }
}