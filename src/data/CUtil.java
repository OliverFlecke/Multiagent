package data;

import java.util.Map;
import java.util.stream.Collectors;

import massim.scenario.city.data.Job;

public class CUtil {

    
    public static Map<String, Integer> extractItems(Job job) {
		return job.getRequiredItems().toItemAmountData().stream()
				.collect(Collectors.toMap(x -> x.getName(), x -> x.getAmount()));
    }

}
