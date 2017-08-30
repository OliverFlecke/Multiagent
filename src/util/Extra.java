
private AgentInfo getAssembler(List<AgentInfo> agents, Map<String, Integer> items) {
	Set<String> tools = iInfo.getBaseTools(items);		
	return agents.stream().min(Comparator.comparingInt(a -> {
		Map<String, Integer> missingItems 		= a.getMissingItems(items);
		Map<String, Integer> baseItems	  		= iInfo.getBaseItems(missingItems);
		Map<String, Integer> missingBaseItems 	= a.getMissingItems(baseItems);
		Set<String>			 usableTools		= a.getUsableTools(tools);
		Set<String>			 missingTools 		= a.getMissingTools(usableTools);
		int 				 volume 			= iInfo.getVolume(missingBaseItems) 
												+ iInfo.getVolume(missingTools);
		return a.getCapacity() - Math.min(volume, a.getCapacity());
	})).get();
}