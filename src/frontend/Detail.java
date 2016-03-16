package frontend;

public class Detail {

	private float[] box;  		// outline for the region on the paga
	private String detailName;	// name of this detail
	private String aliases[];	// other names which can be used for this detail
	private int pageNumber;  	// page number this detail is on
	
	public Detail(int pageNumber){
		this.pageNumber = pageNumber;
	}
	
	public float[] getBox() {
		return box;
	}
	public void setBox(float[] rect) {
		this.box = rect;
	}
	public String getDetailName() {
		return detailName;
	}
	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}
	
	public boolean match(String s){
		boolean matches = false;
		if (detailName.equals(s))
			matches = true;
		else{
			for (String a : aliases)
				if (a.equals(s))
					matches = true;
		}
		return matches;
	}

	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPagenumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

}
