package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        int pushcounter = 0;
        String sha = "";
        String message = "";
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            if (pushcounter >= 10)
                break;
            // Get event type

            // Get event type
            String type = event.getString("type");

            if (!type.equals("PushEvent"))
                continue;
            pushcounter += 1;

            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");

            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");

            sb.append("<table class=\"table\">");
            sb.append("<thead>");
            sb.append("<tr>");
            sb.append("<th style=\"width:30%\">Sha</th>");
            sb.append("<th style=\"width:30%\">Message</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            sb.append("<tbody>");
            //sb.append("<tr>");

            for (int a = 0; a < commits.length(); a++) {
                if (!commits.isNull(a)) {
                    sb.append("<tr>");
                    JSONObject insidecommits = commits.getJSONObject(a);
                    sb.append("<td>");
                    sha = insidecommits.getString("sha");
                    message = insidecommits.getString("message");
                    sb.append(sha);
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(message);
                    sb.append("</td>");
                    sb.append("</tr>");
                }
            }
            sb.append("</tbody>");
            sb.append("</table>");


            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");



        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int j = 1; j < 11; j++) {
            String url = BASE_URL + user + "/events?page="  + j;
            //System.out.println(url);
            JSONObject json = Util.queryAPI(new URL(url));
            //System.out.println(json);
            JSONArray events = json.getJSONArray("root");
            for (int i = 0; i < events.length() ; i++) {
                //String type = events.getJSONObject(i).getString("type");
                //if (type.equals("PushEvent")) {
                eventList.add(events.getJSONObject(i));
                //    pushcounter += 1;
                //    System.out.println(events.getJSONObject(i));
                //}
            }

        }
        return eventList;
    }
}