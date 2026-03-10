package com.example.ridewise;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DistanceMatrixHelper {

    public interface DistanceCallback {

        void onDistanceCalculated(double distanceInKm);
        void onError(String errorMessage);
    }

    public static void getDistance(String origin, String destination, DistanceCallback callback) {
        // Create final local copies for use inside the thread
        final String originEncoded = origin.replace(" ", "+");
        final String destinationEncoded = destination.replace(" ", "+");

        new Thread(() -> {
            try {
                String apiKey = "AIzaSyDLG_Cp-4ZHO7oH58ivXbiJO4vTssnE1gApackage com.example.ridewise;\n" +
                        "\n" +
                        "import android.os.Handler;\n" +
                        "import android.os.Looper;\n" +
                        "\n" +
                        "import org.json.JSONArray;\n" +
                        "import org.json.JSONObject;\n" +
                        "\n" +
                        "import java.io.BufferedReader;\n" +
                        "import java.io.InputStreamReader;\n" +
                        "import java.net.HttpURLConnection;\n" +
                        "import java.net.URL;\n" +
                        "\n" +
                        "public class DistanceMatrixHelper {\n" +
                        "\n" +
                        "    public interface DistanceCallback {\n" +
                        "        void onDistanceCalculated(double distanceInKm);\n" +
                        "        void onError(String errorMessage);\n" +
                        "    }\n" +
                        "\n" +
                        "    // Use the API key you gave (remember to restrict it after testing)\n" +
                        "    private static final String API_KEY = \"AIzaSyDLG_Cp-4ZHO7oH58ivXbiJO4vTssnE1gA\";\n" +
                        "\n" +
                        "    public static void getDistance(String origin, String destination, DistanceCallback callback) {\n" +
                        "        final String originEnc = origin.replace(\" \", \"+\");\n" +
                        "        final String destEnc = destination.replace(\" \", \"+\");\n" +
                        "\n" +
                        "        new Thread(() -> {\n" +
                        "            try {\n" +
                        "                String urlStr = \"https://maps.googleapis.com/maps/api/distancematrix/json?origins=\"\n" +
                        "                        + originEnc + \"&destinations=\" + destEnc + \"&units=metric&key=\" + API_KEY;\n" +
                        "\n" +
                        "                URL url = new URL(urlStr);\n" +
                        "                HttpURLConnection conn = (HttpURLConnection) url.openConnection();\n" +
                        "                conn.setRequestMethod(\"GET\");\n" +
                        "                conn.setConnectTimeout(10000);\n" +
                        "                conn.setReadTimeout(10000);\n" +
                        "\n" +
                        "                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));\n" +
                        "                StringBuilder sb = new StringBuilder();\n" +
                        "                String line;\n" +
                        "                while ((line = reader.readLine()) != null) sb.append(line);\n" +
                        "                reader.close();\n" +
                        "\n" +
                        "                JSONObject json = new JSONObject(sb.toString());\n" +
                        "                // Check status\n" +
                        "                String status = json.optString(\"status\", \"\"); \n" +
                        "                if (!\"OK\".equals(status)) {\n" +
                        "                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(\"Distance API error: \" + status));\n" +
                        "                    return;\n" +
                        "                }\n" +
                        "\n" +
                        "                JSONArray rows = json.getJSONArray(\"rows\");\n" +
                        "                JSONObject elem = rows.getJSONObject(0).getJSONArray(\"elements\").getJSONObject(0);\n" +
                        "                String elemStatus = elem.optString(\"status\", \"\");\n" +
                        "                if (!\"OK\".equals(elemStatus)) {\n" +
                        "                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(\"Route not found\"));\n" +
                        "                    return;\n" +
                        "                }\n" +
                        "\n" +
                        "                JSONObject distanceObj = elem.getJSONObject(\"distance\");\n" +
                        "                String distText = distanceObj.getString(\"text\"); // e.g., \"12.3 km\"\n" +
                        "                double km = Double.parseDouble(distText.replaceAll(\"[^0-9.]\", \"\"));\n" +
                        "\n" +
                        "                new Handler(Looper.getMainLooper()).post(() -> callback.onDistanceCalculated(km));\n" +
                        "            } catch (Exception e) {\n" +
                        "                e.printStackTrace();\n" +
                        "                new Handler(Looper.getMainLooper()).post(() -> callback.onError(\"Failed to fetch distance\"));\n" +
                        "            }\n" +
                        "        }).start();\n" +
                        "    }\n" +
                        "}\n"; // Replace with your actual API key
                String urlStr = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                        + originEncoded + "&destinations=" + destinationEncoded + "&key=" + apiKey;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray rows = json.getJSONArray("rows");
                JSONObject elements = rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                JSONObject distanceObj = elements.getJSONObject("distance");

                String distanceText = distanceObj.getString("text");
                double distanceInKm = Double.parseDouble(distanceText.replaceAll("[^0-9.]", ""));

                new Handler(Looper.getMainLooper()).post(() -> callback.onDistanceCalculated(distanceInKm));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Failed to fetch distance"));
                e.printStackTrace();
            }
        }).start();
    }
}
