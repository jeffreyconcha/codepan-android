package com.codepan.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.widget.CodePanTextField;
import com.codepan.widget.table.TableView;
import com.codepan.widget.table.model.CellData;
import com.codepan.widget.table.model.ColumnData;
import com.codepan.widget.table.model.RowData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TableSample extends Fragment {

	private ArrayList<ColumnData> columnList;
	private ArrayList<RowData> rowList;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnList = new ArrayList<>();
		for(int cn = 1; cn <= 5; cn++) {
			String name = "Column " + cn;
			columnList.add(new ColumnData(getContext(), name, com.codepan.R.dimen.one_hundred, R.layout.table_edittext_item));
		}
		rowList = new ArrayList<>();
		for(int rn = 1; rn <= 50; rn++) {
			ArrayList<CellData> cellList = new ArrayList<>();
			for(ColumnData column : columnList) {
				int ci = columnList.indexOf(column) + 1;
				cellList.add(new CellData("" + rn, "Cell(" + rn + ", " + ci + ")"));
			}
			rowList.add(new RowData(rn, cellList));
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.table_sample_layout, container, false);
		TableView tvTable = view.findViewById(R.id.tvTable);
		tvTable.setColumnList(columnList, false);
		tvTable.setFreezeFirstColumn(true);
		tvTable.setRowList(rowList);
		view.findViewById(R.id.btnTable).setOnClickListener(v -> {
			ArrayList<CellData> dataList = new ArrayList<>();
			dataList.add(new CellData("6", "Replaced 6"));
			tvTable.updateCellsInColumn(3, dataList, (cell, ri, mri, mci) -> {
				CellData data = rowList.get(mri).get(mci);
				CodePanTextField tvTableTextCell = cell.findViewById(R.id.tvTableTextCell);
				tvTableTextCell.setText(data.name);
			});
		});
		return view;
	}
}
