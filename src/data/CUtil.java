package data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import massim.scenario.city.data.Item;
import massim.scenario.city.data.Job;

public class CUtil {

    
    public static Map<String, Integer> extractItems(Job job) {
		return job.getRequiredItems().toItemAmountData().stream()
				.collect(Collectors.toMap(x -> x.getName(), x -> x.getAmount()));
    }

	public static Map<String, Integer> toStringMap(Map<Item, Integer> items) {
		return items.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().getName(), Entry::getValue));
	}

}
