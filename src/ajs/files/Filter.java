package ajs.files;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Creates a FilenameFilter for use in {@link ajs.files.FileInformation} and other files
 * @author arunjitsingh
 *
 */
public class Filter {
	private FilenameFilter filter;
	
	public Filter(final String filterRE) {
		this.filter = new FilenameFilter() {
			public boolean accept(File file, String name) {
				return ajs.util.Regex.matches(filterRE, name);
			}
		};
	}
	
	public FilenameFilter get() {
		return this.filter;
	}
	
	public static FilenameFilter defaultFilter() {
		return new Filter(FS.RE_NO_SPECIALS).get();
	}
}

/*

new Filter("^[^\.]").get();
new Filter(FS.NO_SPC_RE).get();


*/