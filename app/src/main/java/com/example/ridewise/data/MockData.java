package com.example.ridewise.data;

import com.example.ridewise.model.RideOption;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<RideOption> getRideOptions() {
        List<RideOption> list = new ArrayList<>();

        // 🚖 Cabs
        list.add(new RideOption("Ola", "Cab 🚖", "3–6 min", 120));
        list.add(new RideOption("Uber", "Cab 🚖", "4–7 min", 130));

        // 🛺 Autos
        list.add(new RideOption("Namma Yatri", "Auto 🛺", "5–8 min", 90));

        // 🛵 Bikes
        list.add(new RideOption("Rapido", "Bike 🛵", "2–4 min", 60));

        return list;
    }
}
