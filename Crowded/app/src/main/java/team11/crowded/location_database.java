package team11.crowded;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class location_database {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
    private static final ArrayList<String> db_locations = new ArrayList<>();
    private static final ArrayList<String> locations = new ArrayList<>();

    private static String get_to_db_location(String location)
    {
        return db_locations.get(locations.indexOf(location));
    }

    public static ArrayList<String> get_locations()
    {
        return locations;
    }

    public static void submit_post(String name, String location, String rating, String comment) {

        Firebase db = new Firebase("https://incandescent-fire-8621.firebaseio.com/");
        Firebase posts = db.child("locations/" + get_to_db_location(location) + "/posts");

        Map<String, String> post = new HashMap<>();
        post.put("name", name);
        post.put("time", format.format(new Date()));
        post.put("rating", rating);
        post.put("votes", "+0");
        post.put("comment", comment);
        posts.push().setValue(post);
    }

    public static void list_location_page(final AppCompatActivity view, final ListView list, final String location) {

        Firebase db = new Firebase("https://incandescent-fire-8621.firebaseio.com/");
        Firebase select = db.child("locations/" + get_to_db_location(location) + "/posts");

        select.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                ArrayList<Map<String, String>> posts = new ArrayList<>();

                for (DataSnapshot post : snapshot.getChildren()) {

                    posts.add((HashMap<String, String>) post.getValue());
                }

                ArrayList<String> post_list = new ArrayList<>();

                if (!posts.isEmpty()) {

                    Collections.sort(posts, new sort_posts(format));

                    for (Map<String, String> p : posts)
                    {
                        String post_info = "";

                        post_info += "time = " + p.get("time") + "\n";
                        post_info += "rating = " + p.get("rating") + "\n";
                        post_info += "votes = " + p.get("votes") + "\n";
                        post_info += "comment = " + p.get("comment") + "\n";
                        post_info += "name = " + p.get("name") + "\n";

                        post_list.add(post_info);
                    }
                }
                else {
                    post_list.add("No posts found");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(view, android.R.layout.simple_list_item_1, post_list);
                list.setAdapter(adapter);

                view.setTitle("Viewing " + location);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }

    public static void list_locations(final AppCompatActivity view, final ListView list) {

        Firebase db = new Firebase("https://incandescent-fire-8621.firebaseio.com/");
        Firebase places = db.child("locations/");

        places.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                locations.clear();
                db_locations.clear();

                // each location
                for (DataSnapshot location : snapshot.getChildren()) {

                    String name = (String) location.child("name").getValue();
                    db_locations.add((String) location.getKey());
                    locations.add(name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(view, android.R.layout.simple_list_item_1, locations);
                list.setAdapter(adapter);

                view.setTitle("Choose a Location");
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }
}