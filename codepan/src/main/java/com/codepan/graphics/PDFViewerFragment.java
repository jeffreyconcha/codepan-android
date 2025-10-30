package com.codepan.graphics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.R;
import com.codepan.app.CPFragment;
import com.codepan.widget.CodePanButton;
import com.infomaniak.lib.pdfview.PDFView;

import java.io.File;

import androidx.annotation.NonNull;

public class PDFViewerFragment extends CPFragment {

	public void setDeleteConfirmationCallback(OnConfirmDeleteCallback callback) {
		this.callback = callback;
	}

	public interface OnConfirmDeleteCallback {
		void onConfirmDelete(PDFViewerFragment viewer);
	}

	private OnConfirmDeleteCallback callback;
	private PDFView pdfViewer;
	private String path;
	private File file;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(path != null) {
			file = new File(path);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pdf_viewer_layout, container, false);
		CodePanButton btnClosePDFViewer = view.findViewById(R.id.btnClosePDFViewer);
		btnClosePDFViewer.setOnClickListener(v -> onBackPressed());
		btnClosePDFViewer.setOnLongClickListener(v -> {
			if(callback != null) {
				callback.onConfirmDelete(this);
			}
			return true;
		});
		pdfViewer = view.findViewById(R.id.pdfViewer);
		if(file != null && file.exists()) {
			pdfViewer.fromFile(file)
				.enableSwipe(true) // allows to block changing pages using swipe
				.swipeHorizontal(false)
				.enableDoubletap(true)
				.defaultPage(0).load();
		}
		return view;
	}

	public boolean delete() {
		manager.popBackStack();
		if(file != null) {
			return file.delete();
		}
		return false;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
