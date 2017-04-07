package model;

import java.util.ArrayList;
import java.util.List;

public class Tuple<T> {

	private List<T> content;
	
	@SafeVarargs
	public Tuple(T... args) {
		this.content = new ArrayList<T>();
		
		for (T arg : args)
			content.add(arg);
	}
	
	public T getContent(int index) {
		return content.get(index);
	}
	
	public void setContent(int index, T element) {
		content.set(index, element);
	}
}
