package de.adesso.iliasdownloader2.util;

@Deprecated
public interface SyncProgressListener {

	void progress(int percent);

	void fileLoadStart(FileObject fileObject);

	void fileLoadEnd(FileObject fileObject);

}
