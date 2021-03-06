package com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.MainActivity;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.R;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.data.NoteData;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.data.NoteSource;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.data.NoteSourceImpl;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.observer.Observer;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.observer.Publisher;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.ui.NoteListAdapter.NoteListAdapter;
import com.example.notes.note.papers.paper.messages.message.scraps.scrap.letters.letter.memoirs.memoir.ui.NoteListAdapter.OnRecyclerViewClickListener;

public class NotesListFragment extends Fragment {
    public static final String SP_KEY = "SP_KEY";
    public static final String FAVORITE_KEY = "FAVORITE_KEY";
    public static final String DATA_KEY = "DATA_KEY";

    private static NoteData currentNote;
    private static boolean isFavoriteList;
    private static NoteSource data;
    private NoteSource favoriteData;
    private NoteListAdapter noteListAdapter;
    private RecyclerView recyclerView;
    private Publisher publisher;

    public static NoteData getCurrentNote() {
        if (currentNote != null) {
            return currentNote;
        } else {
            return currentNote = new NoteData();
        }
    }

    public static NotesListFragment newInstance(boolean isFavoriteList) {
        NotesListFragment.isFavoriteList = isFavoriteList;
        return new NotesListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        publisher = ((MainActivity) context).getPublisher();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        publisher = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new NoteSourceImpl(getResources());
        if (isFavoriteList) {
            favoriteData = data.getFavoriteData();
            data = favoriteData;//TODO:??????????????. ???????????? ?????????? ??????????????????????, ?? ?????????????????? ???????????? ???? ?????????????? "??????????????????" ????-???? ???????? ?????????????? ????????????
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
        initNotesList(view, isFavoriteList);
        return view;
    }

    //=======================ContentWork================================

    private void initNotesList(View view, boolean isFavoriteList) {
        recyclerView = view.findViewById(R.id.listRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        noteListAdapter = new NoteListAdapter(data, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteListAdapter);
        noteListAdapter.setOnClickListener(new OnRecyclerViewClickListener() {
            @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
            @Override
            public void onRecyclerViewClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_card_view:
                        String currentNoteName = data.getNoteData(position).getTitle();
                        String currentNoteContent = data.getNoteData(position).getNoteContent();
                        byte currentNoteIsFavorite = data.getNoteData(position).isFavorite();
                        currentNote = new NoteData(currentNoteName, currentNoteContent, currentNoteIsFavorite);
                        showContent();
                        break;
                    case R.id.favoriteButton:
                        if (data.getNoteData(position).isFavorite() == NoteData.TRUE) {
                            data.getNoteData(position).setFavorite(NoteData.FALSE);
                        } else
                            data.getNoteData(position).setFavorite(NoteData.TRUE);
                        //writeIsFavorite(requireActivity(), position);
                        //TODO:?????????? ?????????????????????? ???????????????? ???????????? ???????????? ???????????????????????? ?? ?????????????????????? ???? ???????? ????????????,
                        // c SP ???? ????????????????????(data ???????????? ????????????????)
                        if (isFavoriteList) {
                            data.deleteNote(position);
                            //TODO:??????????????, ?????????? ?????????????? ???????????? ???? ?????????? ?? ????????????????????, ?? ???? ????????????????
                            // (???????????? ???????? ??????????????????????, ?????????????? ?????????? ?????????????????????? ???????????? ?? ?????????????? ?????????????? ??????????????, ???????? ?????????????? FavoriteList)
                            noteListAdapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showContent() {
        if (MainActivity.isLandScape()) {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, NoteContentFragment.newInstance(currentNote))
                    .commit();
        } else {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.main_container, NoteContentFragment.newInstance(currentNote))
                    .commit();
        }
    }

    private void showEditFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.main_container, EditFragment.newInstance())
                .commit();
    }

    private void showEditFragment(NoteData note) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.main_container, EditFragment.newInstance(note))
                .commit();
    }

    //========================MainMenuWork==================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notes_list_fragment, menu);
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAdd:
                onActionAddClick();
                break;
            case R.id.actionClearAll:
                data.clearAllNote();
                noteListAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onActionAddClick() {
        showEditFragment();
        publisher.subscribe(new Observer() {
            @Override
            public void updateState(NoteData note) {
                if (isFavoriteList) {
                    note.setFavorite(NoteData.TRUE);
                }
                data.addNote(note);
                noteListAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    //========================ContextMenuWork==================================


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.context_recycler_view, menu);
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = noteListAdapter.getClickContextPosition();
        switch (item.getItemId()) {
            case R.id.action_edit:
                onActionEditClick(position);
                break;
            case R.id.action_delete:
                data.deleteNote(position);
                noteListAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onActionEditClick(int position) {
        showEditFragment(data.getNoteData(position));
        data.deleteNote(position);
        publisher.subscribe(new Observer() {
            @Override
            public void updateState(NoteData note) {//TODO:???? ??????????????????????
                data.addNote(note);
                noteListAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    //====================SharedPreferencesWork=============================================

    /*public static void writeIsFavorite(Context context, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FAVORITE_KEY, data.getNoteData(position).isFavorite());
    }

    public static void writeData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //?????? ???????????????? ?? SP data?
    }

    public void readData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        data = sharedPreferences.getClass(DATA_KEY, new NoteSourceImpl(getResources()));
        for (int i = 0; i < data.size(); i++) {
            data.getNoteData(i).setFavorite(sharedPreferences.getBoolean(FAVORITE_KEY, false));
        }
    }TODO:?????????????? ???? ????????????, ???????? ?????????? ?????? ???????????????????? ?? SP ????????????*/
}