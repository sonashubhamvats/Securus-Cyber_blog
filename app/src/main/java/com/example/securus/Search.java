package com.example.securus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void onClickSearch()
    {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfUserIds.clear();
                searchResults.clear();
                templistOfNames.clear();
                tempListOfEmaild.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {

                    User u=dataSnapshot.getValue(User.class);
                    String userId=dataSnapshot.getKey();
                    String[] name_part=nameSearch.getText().toString().split(" ");
                    for(String str:name_part)
                    {
                        try {

                            String fnameSplit[]=u.firstName.split(" ");
                            if(str.compareToIgnoreCase(u.lastName)==0||str.compareToIgnoreCase(fnameSplit[0])==0||(fnameSplit.length>1?str.compareToIgnoreCase(fnameSplit[1])==0:false))
                            {
                                templistOfNames.add(u.firstName+" "+u.lastName);
                                tempListOfEmaild.add(u.EmailId);
                                listOfUserIds.add(dataSnapshot.getKey());

                            }

                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                if(templistOfNames.size()>0)
                {
                    getCountAfterNameAndEmail(templistOfNames,tempListOfEmaild,0);

                }
                else
                {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("!!!!!!")
                            .setMessage("No match found")
                            .setPositiveButton("Ok", null)
                            .show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void getCountAfterNameAndEmail(ArrayList<String> ln,ArrayList<String> le,int index)
    {
        //adding no of posts
        FirebaseDatabase.getInstance().getReference().child("UserPosts").child(listOfUserIds.get(index)).child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    searchResult s=new searchResult();
                    s.setNamee(ln.get(index));
                    s.setEmail(le.get(index));

                    int count=(int)Double.parseDouble(task.getResult().getValue().toString());
                    s.setNoofPosts(count);
                    if(index==(ln.size()-1))
                    {
                        searchResults.add(s);
                        try {
                            searchAdapter=new myAdaptor(getActivity(),searchResults);
                            list.setAdapter(searchAdapter);

                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        searchResults.add(s);
                        getCountAfterNameAndEmail(ln,le,(index+1));
                    }
                }
            }
        });

    }

    ArrayList<searchResult> searchResults=new ArrayList<searchResult>();
    ArrayList<String> listOfUserIds=new ArrayList<String>();
    ArrayList<String> templistOfNames=new ArrayList<String>();
    ArrayList<String> tempListOfEmaild=new ArrayList<String>();
    myAdaptor searchAdapter;
    ListView list;
    EditText nameSearch;
    Button buttonSearch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Toast.makeText(getActivity(), "In search", Toast.LENGTH_SHORT).show();
        FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null)
        {
            Account.curr_userId=u.getUid();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.listOFNames);
        nameSearch=getView().findViewById(R.id.searchName);
        buttonSearch=getView().findViewById(R.id.searchButton);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearch();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                redirectToAccount(position);
            }
        });
    }

    class myAdaptor extends ArrayAdapter<searchResult>
    {

        myAdaptor(Context c,ArrayList<searchResult> n)
        {
            super(c,0,n);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.search_results_layout, parent, false);
            }
            searchResult s=getItem(position);
            TextView nameOfThePerson=view.findViewById(R.id.nameOfThePersonSearch);
            TextView emailOfThePerson=view.findViewById(R.id.mailOfThePersonSearch);
            TextView NoofPosts=view.findViewById(R.id.noofpostssearch);
            nameOfThePerson.setText(s.getNamee());
            emailOfThePerson.setText(s.getEmail());
            String temp=s.getPosts()+" posts";
            NoofPosts.setText(temp);

            return view;
        }
    }

    private void redirectToAccount(int i)
    {
        Account.curr_userId=listOfUserIds.get(i);


        Account accFrag= new Account();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, accFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

}