package scenario.data.facility;

public class ResourceNode extends Facility {

	String resource;
	
	public ResourceNode(Facility facility, String resource) {
		super(facility);
		this.resource = resource;
	}

}
