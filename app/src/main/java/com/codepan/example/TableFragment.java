package com.codepan.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.widget.table.TableView;
import com.codepan.widget.table.model.CellData;
import com.codepan.widget.table.model.ColumnData;
import com.codepan.widget.table.model.RowData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TableFragment extends Fragment {

	private ArrayList<ColumnData> columnList;
	private ArrayList<RowData> rowList;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnList = new ArrayList<>();
		for (int cn = 1; cn <= 5; cn++) {
			String name = "Column " + cn;
			columnList.add(new ColumnData(getContext(), name, R.dimen.one_hundred));
		}
		rowList = new ArrayList<>();
		for (int rn = 1; rn <= 50; rn++) {
			ArrayList<CellData> cellList = new ArrayList<>();
			for (ColumnData column : columnList) {
				int ci = columnList.indexOf(column) + 1;
				cellList.add(new CellData("" + rn, "Cell(" + rn + ", " + ci + ")"));
			}
			rowList.add(new RowData(rn, cellList));
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.table_view_layout, container, false);
		TableView tvTable = view.findViewById(R.id.tvTable);
		tvTable.setColumnList(columnList, false);
		tvTable.setFreezeFirstColumn(true);
		tvTable.setRowList(rowList);
		view.findViewById(R.id.btnTable).setOnClickListener(v -> {
			ArrayList<CellData> dataList = new ArrayList<>();
			dataList.add(new CellData("1", "Replaced 1"));
			dataList.add(new CellData("2", "Replaced 2"));
			dataList.add(new CellData("3", "Replaced 3"));
			dataList.add(new CellData("4", "Replaced 4"));
			dataList.add(new CellData("5", "Replaced 5"));
			dataList.add(new CellData("6", "Replaced 6"));
			tvTable.updateCellsInColumn(dataList, 3);
		});
		return view;
	}
}
