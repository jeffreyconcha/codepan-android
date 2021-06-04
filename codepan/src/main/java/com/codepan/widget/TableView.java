package com.codepan.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepan.R;
import com.codepan.callback.Interface.OnTableAddRowCallback;
import com.codepan.callback.Interface.OnTableCellClickCallback;
import com.codepan.callback.Interface.OnTableCellCreatedCallback;
import com.codepan.callback.Interface.OnTableColumnClickCallback;
import com.codepan.callback.Interface.OnTableRowClickCallback;
import com.codepan.model.CellData;
import com.codepan.model.ColumnData;
import com.codepan.model.FilterData;
import com.codepan.utils.CodePanUtils;

import java.util.ArrayList;
import java.util.Collections;

public class TableView extends FrameLayout {

	private final int LIMIT = 50;
	private final String[] limits = {
		"30", "50", "100"
	};
	private final String DELIMITER = " - ";
	private final String PARENT_TAG = "parent";
	private LinearLayout llMainTable, llContentTable, llTopTable, llLeftTable, llParentTable;
	private CodePanButton btnNextTable, btnPreviousTable, btnAddTable;
	private boolean isInitialized, withRowNumbers, isRowFlexible;
	private OnTableCellCreatedCallback tableCellCreatedCallback;
	private OnTableColumnClickCallback tableColumnClickCallback;
	private OnTableCellClickCallback tableCellClickCallback;
	private OnTableRowClickCallback tableRowClickCallback;
	private TableScrollView svLeftTable, svContentTable;
	private OnTableAddRowCallback tableAddRowCallback;
	private ArrayList<ArrayList<CellData>> mapList;
	private final ArrayList<FilterData> filterList;
	private Spinner spinLimitTable, spinPageTable;
	private boolean freezeFirstColumn = true;
	private ArrayList<ColumnData> columnList;
	private View vPreviousTable, vNextTable;
	private final LayoutInflater inflater;
	private CodePanLabel tvTotalTable;
	private final Context context;
	private final int numWidth;
	private int current, count;
	private int limit = LIMIT;
	private int[] cellResIds;
	private int headerResId;

	public TableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context, attrs);
		inflater = LayoutInflater.from(context);
		filterList = new ArrayList<>();
		numWidth = getResources().getDimensionPixelSize(R.dimen.fifty);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.table);
		headerResId = ta.getResourceId(R.styleable.table_headerLayout, R.layout.table_header_item);
		int cellId = ta.getResourceId(R.styleable.table_cellLayouts, 0);
		if(cellId != 0) {
			final TypedArray ra = getResources().obtainTypedArray(cellId);
			cellResIds = new int[ra.length()];
			for(int i = 0; i < ra.length(); i++) {
				final int resId = ra.getResourceId(i, R.layout.table_cell_item);
				cellResIds[i] = resId;
			}
			ra.recycle();
		}
		ta.recycle();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final View view = inflater.inflate(R.layout.table_layout, this, false);
		llMainTable = view.findViewById(R.id.llMainTable);
		llContentTable = view.findViewById(R.id.llContentTable);
		llTopTable = view.findViewById(R.id.llTopTable);
		llLeftTable = view.findViewById(R.id.llLeftTable);
		llParentTable = view.findViewById(R.id.llParentTable);
		svContentTable = view.findViewById(R.id.svContentTable);
		svLeftTable = view.findViewById(R.id.svLeftTable);
		spinLimitTable = view.findViewById(R.id.spinLimitTable);
		spinPageTable = view.findViewById(R.id.spinPageTable);
		tvTotalTable = view.findViewById(R.id.tvTotalTable);
		btnNextTable = view.findViewById(R.id.btnNextTable);
		btnPreviousTable = view.findViewById(R.id.btnPreviousTable);
		btnAddTable = view.findViewById(R.id.btnAddTable);
		vNextTable = view.findViewById(R.id.vNextTable);
		vPreviousTable = view.findViewById(R.id.vPreviousTable);
		svLeftTable.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		ArrayAdapter<String> limitAdapter = new ArrayAdapter<>(context, R.layout.table_spinner_selected_item);
		limitAdapter.setDropDownViewResource(R.layout.table_spinner_selection_item);
		limitAdapter.addAll(limits);
		spinLimitTable.setAdapter(limitAdapter);
		spinLimitTable.setSelection(1);
		spinLimitTable.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(isInitialized && mapList != null) {
					String text = limits[position];
					limit = Integer.parseInt(text);
					populateContent(mapList, 0);
					setPageList();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		svContentTable.setOnScrollChangeCallback((l, t, ol, ot) -> {
			svLeftTable.scrollTo(0, t);
		});
		svLeftTable.setOnScrollChangeCallback((l, t, ol, ot) -> {
			svContentTable.scrollTo(0, t);
		});
		btnNextTable.setOnClickListener(v -> {
			int next = current + 1;
			if (next < count) {
				spinPageTable.setSelection(next);
			}
		});
		btnPreviousTable.setOnClickListener(v -> {
			int previous = current - 1;
			if (previous >= 0) {
				spinPageTable.setSelection(previous);
			}
		});
		btnAddTable.setOnClickListener(v -> {
			if (isInitialized && mapList != null) {
				if (tableAddRowCallback != null) {
					tableAddRowCallback.onTableAddRow();
				}
			}
		});
		if (isRowFlexible) {
			btnAddTable.setParentVisibility(View.VISIBLE);
		}
		update();
		addView(view);
	}

	public void update() {
		this.current = 0;
		updateAndRetainPage();
	}

	public void updateAndRetainPage() {
		ArrayList<ArrayList<CellData>> mapList = getFilteredMapList();
		if(mapList != null && columnList != null) {
			View child = llParentTable.getChildAt(0);
			Object object = child.getTag();
			if(object instanceof String) {
				String tag = (String) object;
				if(tag.equals(PARENT_TAG)) {
					llParentTable.removeView(child);
				}
			}
			llTopTable.removeAllViews();
			for(final ColumnData h : columnList) {
				final int resId = h.headerResId != 0 ? h.headerResId : headerResId;
				View header = inflater.inflate(resId, this, false);
				CodePanLabel tvTableTextHeader = header.findViewById(R.id.tvTableTextHeader);
				View vFilterTextHeader = header.findViewById(R.id.vFilterTextHeader);
				tvTableTextHeader.setText(h.value);
				if(vFilterTextHeader != null) {
					if(h.isFilterEnabled) {
						vFilterTextHeader.setVisibility(View.VISIBLE);
						int mci = columnList.indexOf(h);
						ArrayList<String> filter = getFilterItemList(mci);
						vFilterTextHeader.setEnabled(filter != null && !filter.isEmpty());
						header.setOnClickListener(v -> {
							if (tableColumnClickCallback != null) {
								int mci1 = columnList.indexOf(h);
								ArrayList<String> list = getDistinctItems(h);
								tableColumnClickCallback.onTableColumnClick(mci1, list);
							}
						});
					}
					else {
						vFilterTextHeader.setVisibility(View.GONE);
					}
				}
				header.getLayoutParams().width = h.width;
				if(columnList.indexOf(h) != 0) {
					llTopTable.addView(header);
				}
				else {
					if(withRowNumbers()) {
						View number = inflater.inflate(resId, this, false);
						CodePanLabel text = number.findViewById(R.id.tvTableTextHeader);
						View filter = number.findViewById(R.id.vFilterTextHeader);
						if(filter != null) {
							filter.setVisibility(View.GONE);
						}
						text.setText("#");
						number.getLayoutParams().width = numWidth;
						if(freezeFirstColumn) {
							final int totalWidth = numWidth + h.width;
							final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(totalWidth,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							final LinearLayout layout = new LinearLayout(context);
							layout.setOrientation(LinearLayout.HORIZONTAL);
							layout.setGravity(Gravity.CENTER_VERTICAL);
							layout.addView(number);
							layout.addView(header);
							layout.setTag(PARENT_TAG);
							layout.setLayoutParams(params);
							llParentTable.addView(layout, 0);
						}
						else {
							number.setTag(PARENT_TAG);
							llParentTable.addView(number, 0);
							llTopTable.addView(header);
						}
					}
					else {
						header.setTag(PARENT_TAG);
						llParentTable.addView(header, 0);
					}
				}
			}
			String total = context.getString(R.string.of) + " " + mapList.size();
			tvTotalTable.setText(total);
			btnAddTable.setParentVisibility(isRowFlexible ? View.VISIBLE : View.GONE);
			setPageList();
		}
	}

	private ArrayList<String> getDistinctItems(ColumnData column) {
		if(column != null) {
			ArrayList<String> list = new ArrayList<>();
			int columnIndex = columnList.indexOf(column);
			for(ArrayList<CellData> map : mapList) {
				CellData cell = map.get(columnIndex);
				if(cell.name != null) {
					list.add(cell.name);
				}
			}
			Collections.sort(list);
			return CodePanUtils.removeDuplicateEntry(list);
		}
		return null;
	}

	private void setPageList() {
		final ArrayList<ArrayList<CellData>> mapList = getFilteredMapList();
		if(mapList != null) {
			final ArrayAdapter<String> pageAdapter = new ArrayAdapter<>(context, R.layout.table_spinner_selected_item);
			pageAdapter.setDropDownViewResource(R.layout.table_spinner_selection_item);
			final int size = mapList.size();
			if(size > limit) {
				count = size / limit;
				int remainder = size % limit;
				count = remainder != 0 ? count + 1 : count;
				for(int i = 0; i < count; i++) {
					int start = (i * limit) + 1;
					if(i < count - 1) {
						int end = start + limit - 1;
						pageAdapter.add(start + DELIMITER + end);
					}
					else {
						pageAdapter.add(start + DELIMITER + size);
					}
				}
			}
			else {
				if(size > 0) {
					pageAdapter.add("1" + DELIMITER + size);
				}
				else {
					pageAdapter.add("0" + DELIMITER + "0");
				}
				count = 0;
			}
			spinPageTable.setAdapter(pageAdapter);
			spinPageTable.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String page = pageAdapter.getItem(position);
					if(page != null) {
						String[] array = page.split(DELIMITER);
						int start = Integer.parseInt(array[0]) - 1;
						populateContent(mapList, start);
					}
					current = position;
					if(current == 0) {
						btnPreviousTable.setEnabled(false);
						vPreviousTable.setEnabled(false);
					}
					else {
						btnPreviousTable.setEnabled(true);
						vPreviousTable.setEnabled(true);
					}
					boolean hasPrevious = current != 0;
					boolean hasNext = current < count - 1;
					btnPreviousTable.setEnabled(hasPrevious);
					vPreviousTable.setEnabled(hasPrevious);
					btnNextTable.setEnabled(hasNext);
					vNextTable.setEnabled(hasNext);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			spinPageTable.setSelection(current);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private void populateContent(final ArrayList<ArrayList<CellData>> mapList, int start) {
		llLeftTable.removeAllViews();
		llContentTable.removeAllViews();
		if(mapList != null && !mapList.isEmpty()) {
			int size = mapList.size();
			int end = start + limit;
			end = Math.min(end, size);
			ArrayList<ArrayList<CellData>> loadedList = new ArrayList<>();
			int rowIndex = 0;
			for(int i = start; i < end; i++) {
				final ArrayList<CellData> row = mapList.get(i);
				final LinearLayout llRow = new LinearLayout(context);
				llRow.setOrientation(LinearLayout.HORIZONTAL);
				llRow.setGravity(Gravity.CENTER_VERTICAL);
				final int ri = rowIndex;
				final int mri = mapList.indexOf(row);
				llRow.setTag(mri);
				for (final CellData c : row) {
					final int mci = row.indexOf(c);
					ColumnData column = columnList.get(mci);
					c.column = column;
					int defaultId = column.cellResId != 0 ? column.cellResId : R.layout.table_cell_item;
					int resId = cellResIds != null ? cellResIds[mci] : defaultId;
					final View cell = inflater.inflate(resId, this, false);
					TextView tvTableTextCell = cell.findViewById(R.id.tvTableTextCell);
					if (c.name != null) {
						tvTableTextCell.setText(c.name);
					}
					cell.getLayoutParams().width = column.width;
					if (tableCellClickCallback != null) {
						cell.setOnClickListener(v -> {
							tableCellClickCallback.onTableCellClick(cell, ri, mri, mci);
						});
					}
					if (tableCellCreatedCallback != null) {
						tableCellCreatedCallback.onTableCellCreated(cell, ri, mri, mci);
					}
					if (mci != 0) {
						llRow.addView(cell);
					}
					else {
						if (tableRowClickCallback != null) {
							cell.setOnTouchListener((v, event) -> {
								switch (event.getAction()) {
									case MotionEvent.ACTION_DOWN:
										v.setPressed(true);
										llRow.setPressed(true);
										break;
									case MotionEvent.ACTION_UP:
										v.setPressed(false);
										llRow.setPressed(false);
										tableRowClickCallback.onTableCellClick(cell, ri, mri);
										break;
									case MotionEvent.ACTION_CANCEL:
										v.setPressed(false);
										llRow.setPressed(false);
										break;
								}
								performClick();
								return true;
							});
						}
						if(withRowNumbers()) {
							final View number = inflater.inflate(R.layout.table_row_number_item, this, false);
							CodePanLabel text = number.findViewById(R.id.tvTableRowNumber);
							if (c.rowID != null) {
								text.setText(c.rowID);
							}
							else {
								String rowNo = String.valueOf(mri + 1);
								text.setText(rowNo);
							}
							number.getLayoutParams().width = numWidth;
							if (freezeFirstColumn) {
								final int totalWidth = numWidth + column.width;
								final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(totalWidth,
									LinearLayout.LayoutParams.WRAP_CONTENT);
								final LinearLayout layout = new LinearLayout(context);
								layout.setOrientation(LinearLayout.HORIZONTAL);
								layout.setGravity(Gravity.CENTER_VERTICAL);
								layout.addView(number);
								layout.addView(cell);
								layout.setLayoutParams(params);
								layout.setTag(mri);
								llLeftTable.addView(layout);
							}
							else {
								llLeftTable.addView(number);
								llRow.addView(cell);
							}
						}
						else {
							cell.setTag(mri);
							llLeftTable.addView(cell);
						}
					}
				}
				if(tableRowClickCallback != null) {
					llRow.setOnTouchListener((v, event) -> {
						View cell = llLeftTable.getChildAt(mri);
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								v.setPressed(true);
								cell.setPressed(true);
								break;
							case MotionEvent.ACTION_UP:
								v.setPressed(false);
								cell.setPressed(false);
								tableRowClickCallback.onTableCellClick(cell, ri, mri);
								break;
							case MotionEvent.ACTION_CANCEL:
								v.setPressed(false);
								cell.setPressed(false);
								break;
						}
						performClick();
						return true;
					});
				}
				llContentTable.addView(llRow);
				loadedList.add(row);
				rowIndex++;
			}
			if (llMainTable.getVisibility() == View.GONE) {
				llMainTable.setVisibility(View.VISIBLE);
			}
			equalizeHeight(loadedList);
			isInitialized = true;
		}
	}

	private void equalizeHeight(final ArrayList<ArrayList<CellData>> loadedList) {
		if(mapList != null && loadedList != null && !loadedList.isEmpty()) {
			final ViewTreeObserver vto = getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					vto.removeOnGlobalLayoutListener(this);
					for(ArrayList<CellData> map : loadedList) {
						equalizeContentHeight(loadedList.indexOf(map));
					}
					View xy = llParentTable.getChildAt(0);
					int th = llTopTable.getHeight();
					int ph = xy.getHeight();
					if(ph != th) {
						if(th > ph) {
							xy.getLayoutParams().height = th;
							xy.requestLayout();
						}
						else {
							llTopTable.getLayoutParams().height = ph;
							llTopTable.requestLayout();
						}
					}
				}
			});
		}
	}

	private void equalizeContentHeight(int ri) {
		View row = llContentTable.getChildAt(ri);
		View left = llLeftTable.getChildAt(ri);
		int rh = row.getHeight();
		int lh = left.getHeight();
		if(rh != lh) {
			if(rh > lh) {
				left.getLayoutParams().height = rh;
				left.requestLayout();
			}
			else {
				row.getLayoutParams().height = lh;
				row.requestLayout();
			}
		}
	}

	public void equalizeHeight(View cell, final int ri) {
		final ViewTreeObserver vto = cell.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				vto.removeOnGlobalLayoutListener(this);
				equalizeContentHeight(ri);
			}
		});
	}

	/**
	 * @param dataList Data set of cells in columns. Each cell must have a rowID
	 * @param mci      Column index to be updated.
	 */
	public void updateCellsInColumn(ArrayList<CellData> dataList, int mci) {
		final ArrayList<CellData> cellList = new ArrayList<>(dataList);
		for (int ri = 0; ri < mapList.size(); ri++) {
			final ArrayList<CellData> row = mapList.get(ri);
			if (row != null && !row.isEmpty()) {
				final String rowID = row.get(0).rowID;
				final CellData cell = getCell(rowID, cellList);
				if (cell != null) {
					final int index = cellList.indexOf(cell);
					row.set(mci, cell);
					cellList.remove(index);
				}
			}
		}

		if (mci != 0 || !freezeFirstColumn) {
			int count = llContentTable.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = llContentTable.getChildAt(i);
				int mri = (int) child.getTag();
				CellData cell = mapList.get(mri).get(mci);
				TextView tvTableTextCell = child.findViewById(R.id.tvTableTextCell);
				tvTableTextCell.setText(cell.name);
			}
		}
		else {
			int count = llLeftTable.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = llLeftTable.getChildAt(i);
				int mri = (int) child.getTag();
				CellData cell = mapList.get(mri).get(0);
				TextView tvTableTextCell = child.findViewById(R.id.tvTableTextCell);
				tvTableTextCell.setText(cell.name);
			}
		}
	}

	private CellData getCell(String rowID, ArrayList<CellData> cellList) {
		for (CellData cell : cellList) {
			if (rowID != null && rowID.equals(cell.rowID)) {
				return cell;
			}
		}
		return null;
	}

	public void setMapList(ArrayList<ArrayList<CellData>> mapList) {
		this.mapList = mapList;
	}

	public void addRowItems(int count) {
		if (mapList != null && !mapList.isEmpty()) {
			int lastIndex = mapList.size() - 1;
			CellData last = mapList.get(lastIndex).get(0);
			int lastId = Integer.parseInt(last.rowID);
			for (int ri = 0; ri < count; ri++) {
				ArrayList<CellData> map = new ArrayList<>();
				for (ColumnData c : columnList) {
					String rowID = String.valueOf(lastId + ri + 1);
					final CellData cell = new CellData(rowID, null);
					map.add(cell);
				}
				mapList.add(map);
			}
		}
		updateAndRetainPage();
	}

	public void setColumnList(ArrayList<ColumnData> columnList, boolean withRowNumbers) {
		this.columnList = columnList;
		this.withRowNumbers = withRowNumbers;
	}

	public void setOnTableCellClickCallback(OnTableCellClickCallback tableCellClickCallback) {
		this.tableCellClickCallback = tableCellClickCallback;
	}

	public void setOnTableCellCreatedCallback(OnTableCellCreatedCallback tableCellCreatedCallback) {
		this.tableCellCreatedCallback = tableCellCreatedCallback;
	}

	public void setOnTableRowClickCallback(OnTableRowClickCallback tableRowClickCallback) {
		this.tableRowClickCallback = tableRowClickCallback;
	}

	public void setOnTableColumnClickCallback(OnTableColumnClickCallback tableColumnClickCallback) {
		this.tableColumnClickCallback = tableColumnClickCallback;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public View getView(int x, int y) {
		if (x < llContentTable.getChildCount()) {
			if (y != 0) {
				int index = y - 1;
				LinearLayout row = (LinearLayout) llContentTable.getChildAt(x);
				if (index < row.getChildCount()) {
					return row.getChildAt(index);
				}
			}
			else {
				return llLeftTable.getChildAt(x);
			}
		}
		return null;
	}

	public void clearAllFilter() {
		filterList.clear();
	}

	public void clearFilter(int mci) {
		if(hasFilter()) {
			FilterData item = null;
			for(FilterData data : filterList) {
				if(data.mci == mci) {
					item = data;
					break;
				}
			}
			if(item != null) {
				filterList.remove(item);
			}
		}
	}


	public boolean hasFilter() {
		return !filterList.isEmpty();
	}

	public void addFilter(int mci, ArrayList<String> itemList) {
		boolean isExisting = false;
		for(FilterData data : filterList) {
			if(data.mci == mci) {
				data.itemList = itemList;
				isExisting = true;
				break;
			}
		}
		if(!isExisting) {
			FilterData filter = new FilterData();
			filter.mci = mci;
			filter.itemList = itemList;
			filterList.add(filter);
		}
	}

	public ArrayList<String> getFilterItemList(int mci) {
		if(hasFilter()) {
			for(FilterData data : filterList) {
				if(data.mci == mci) {
					return data.itemList;
				}
			}
		}
		return null;
	}

	public ArrayList<FilterData> getFilterList() {
		return this.filterList;
	}

	public ArrayList<ArrayList<CellData>> getFilteredMapList() {
		if(hasFilter()) {
			ArrayList<ArrayList<CellData>> filteredList = new ArrayList<>();
			int size = columnList.size();
			for(ArrayList<CellData> row : mapList) {
				int count = 0;
				for (CellData cell : row) {
					int mci = row.indexOf(cell);
					ArrayList<String> filter = getFilterItemList(mci);
					if (filter == null || filter.contains(cell.name)) {
						count++;
					}
				}
				if (size == count) {
					filteredList.add(row);
				}
			}
			return filteredList;
		}
		return mapList;
	}

	public void freezeFirstColumn() {
		this.freezeFirstColumn = true;
		llMainTable.setVisibility(View.GONE);
		updateAndRetainPage();
	}

	public void unfreezeFirstColumn() {
		this.freezeFirstColumn = false;
		llMainTable.setVisibility(View.GONE);
		updateAndRetainPage();
	}

	/**
	 * @param freezeFirstColumn Set to true if you want the first column to freeze. <br/>
	 *                          Will automatically add row numbers if set to false. <br/>
	 */
	public void setFreezeFirstColumn(boolean freezeFirstColumn) {
		this.freezeFirstColumn = freezeFirstColumn;
	}

	public boolean isFreezeFirstColumn() {
		return this.freezeFirstColumn;
	}

	public boolean withRowNumbers() {
		return withRowNumbers || !freezeFirstColumn;
	}

	public void setOnTableAddRowCallback(OnTableAddRowCallback tableAddRowCallback) {
		this.tableAddRowCallback = tableAddRowCallback;
	}

	public void setRowFlexible(boolean isRowFlexible) {
		this.isRowFlexible = isRowFlexible;
	}
}
