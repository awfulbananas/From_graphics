package files;

//represents an svg file as a list of paths
//currently only holds path data
public class SVGFile {
	protected SplinePath[] paths;
	
	protected SVGFile() {}
	
	public int size() {
		return paths.length;
	}
	
	public SplinePath get(int n) {
		return paths[n];
	}
}
