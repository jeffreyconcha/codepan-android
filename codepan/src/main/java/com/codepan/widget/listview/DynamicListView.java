/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepan.widget.listview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.codepan.widget.listview.Callback.OnSwapElementCallback;

import java.util.ArrayList;

public class DynamicListView<T> extends ListView {

	private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
	private final int INVALID_POINTER_ID = -1;
	private final int MOVE_DURATION = 300;
	private final int INVALID_ID = -1;

	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private int activePointerID = INVALID_POINTER_ID;
	private int smoothScrollAmountAtEdge = 0;
	private int lastEventY = -1;
	private int totalOffset = 0;
	private int downY = -1;
	private int downX = -1;

	private boolean isWaitingForScrollFinish, isMobileScrolling, isCellMobile;
	private Rect hoverCellCurrentBounds, hoverCellOriginalBounds;
	private OnSwapElementCallback swapElementCallback;
	private long mobileItemID = INVALID_ID;
	private long belowItemID = INVALID_ID;
	private long aboveItemID = INVALID_ID;
	private BitmapDrawable hoverCell;
	public ArrayList<T> itemList;
	private Resources res;

	public DynamicListView(Context context) {
		super(context);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		setOnItemLongClickListener(itemLongClickListener);
		setOnScrollListener(scrollListener);
		res = context.getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		smoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
	}

	/**
	 * Listens for long clicks on any items in the listview. When a cell has
	 * been selected, the hover cell is created and set up.
	 */
	private OnItemLongClickListener itemLongClickListener =
			new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
					totalOffset = 0;
					int position = pointToPosition(downX, downY);
					int itemNum = position - getFirstVisiblePosition();
					View selectedView = getChildAt(itemNum);
					mobileItemID = getAdapter().getItemId(position);
					hoverCell = getAndAddHoverView(selectedView);
					selectedView.setVisibility(INVISIBLE);
					isCellMobile = true;
					updateNeighborViewsForID(mobileItemID);
					return true;
				}
			};

	/**
	 * Creates the hover cell with the appropriate bitmap and of appropriate
	 * size. The hover cell's BitmapDrawable is drawn on top of the bitmap every
	 * single time an invalidate call is made.
	 */
	private BitmapDrawable getAndAddHoverView(View v) {
		int w = v.getWidth();
		int h = v.getHeight();
		int top = v.getTop();
		int left = v.getLeft();
		Bitmap b = getBitmapFromView(v);
		BitmapDrawable drawable = new BitmapDrawable(res, b);
		hoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
		hoverCellCurrentBounds = new Rect(hoverCellOriginalBounds);
		drawable.setBounds(hoverCellCurrentBounds);
		return drawable;
	}

	/**
	 * Returns a bitmap showing a screenshot of the view passed in.
	 */
	private Bitmap getBitmapFromView(View v) {
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		v.draw(canvas);
		return bitmap;
	}

	/**
	 * Stores a reference to the views above and below the item currently
	 * corresponding to the hover cell. It is important to note that if this
	 * item is either at the top or bottom of the list, aboveItemID or belowItemID
	 * may be invalid.
	 */
	private void updateNeighborViewsForID(long itemID) {
		int position = getPositionForID(itemID);
		BaseAdapter adapter = ((BaseAdapter) getAdapter());
		mobileItemID = adapter.getItemId(position);
		aboveItemID = adapter.getItemId(position - 1);
		belowItemID = adapter.getItemId(position + 1);
	}

	/**
	 * Retrieves the view in the list corresponding to itemID
	 */
	public View getViewForID(long itemID) {
		int firstVisiblePosition = getFirstVisiblePosition();
		BaseAdapter adapter = ((BaseAdapter) getAdapter());
		for(int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			int position = firstVisiblePosition + i;
			long id = adapter.getItemId(position);
			if(id == itemID) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Retrieves the position in the list corresponding to itemID
	 */
	public int getPositionForID(long itemID) {
		View v = getViewForID(itemID);
		if(v == null) {
			return -1;
		}
		else {
			return getPositionForView(v);
		}
	}

	/**
	 * dispatchDraw gets invoked when all the child views are about to be drawn.
	 * By overriding this method, the hover cell (BitmapDrawable) can be drawn
	 * over the listview's items whenever the listview is redrawn.
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(hoverCell != null) {
			hoverCell.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) event.getX();
				downY = (int) event.getY();
				activePointerID = event.getPointerId(0);
				break;
			case MotionEvent.ACTION_MOVE:
				if(activePointerID == INVALID_POINTER_ID) {
					break;
				}
				int pointerIndex = event.findPointerIndex(activePointerID);
				lastEventY = (int) event.getY(pointerIndex);
				int deltaY = lastEventY - downY;
				if(isCellMobile) {
					hoverCellCurrentBounds.offsetTo(hoverCellOriginalBounds.left,
							hoverCellOriginalBounds.top + deltaY + totalOffset);
					hoverCell.setBounds(hoverCellCurrentBounds);
					invalidate();
					handleCellSwitch();
					isMobileScrolling = false;
					handleMobileCellScroll();
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				touchEventsEnded();
				break;
			case MotionEvent.ACTION_CANCEL:
				touchEventsCancelled();
				break;
			case MotionEvent.ACTION_POINTER_UP:
				pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
						MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = event.getPointerId(pointerIndex);
				if(pointerId == activePointerID) {
					touchEventsEnded();
				}
				break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * This method determines whether the hover cell has been shifted far enough
	 * to invoke a cell swap. If so, then the respective cell swap candidate is
	 * determined and the data set is changed. Upon posting a notification of the
	 * data set change, a layout is invoked to place the cells in the right place.
	 * Using a ViewTreeObserver and a corresponding OnPreDrawListener, we can
	 * offset the cell being swapped to where it previously was and then animate it to
	 * its new position.
	 */
	private void handleCellSwitch() {
		final int deltaY = lastEventY - downY;
		int deltaYTotal = hoverCellOriginalBounds.top + totalOffset + deltaY;
		final View aboveView = getViewForID(aboveItemID);
		final View mobileView = getViewForID(mobileItemID);
		final View belowView = getViewForID(belowItemID);
		boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());
		boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());
		if(isBelow || isAbove) {
			final View switchView = isBelow ? belowView : aboveView;
			swapElements(itemList, getPositionForView(mobileView), getPositionForView(switchView));
			((BaseAdapter) getAdapter()).notifyDataSetChanged();
			downY = lastEventY;
			mobileView.setVisibility(View.VISIBLE);
			switchView.setVisibility(View.INVISIBLE);
			updateNeighborViewsForID(getPositionForView(switchView));
			final ViewTreeObserver observer = getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					observer.removeOnPreDrawListener(this);
					totalOffset += deltaY;
					int delta = switchView.getTop() - mobileView.getTop();
					switchView.setTranslationY(delta);
					ObjectAnimator animator = ObjectAnimator.ofFloat(switchView, View.TRANSLATION_Y, 0);
					animator.setDuration(MOVE_DURATION);
					animator.start();
					return true;
				}
			});
		}
	}

	private void swapElements(ArrayList<T> arrayList, int indexOne, int indexTwo) {
		T temp = arrayList.get(indexOne);
		arrayList.set(indexOne, arrayList.get(indexTwo));
		arrayList.set(indexTwo, temp);
	}

	/**
	 * Resets all the appropriate fields to a default state while also animating
	 * the hover cell back to its correct location.
	 */
	private void touchEventsEnded() {
		final View mobileView = getViewForID(mobileItemID);
		if(isCellMobile || isWaitingForScrollFinish) {
			isCellMobile = false;
			isWaitingForScrollFinish = false;
			isMobileScrolling = false;
			activePointerID = INVALID_POINTER_ID;
			// If the autoscroller has not completed scrolling, we need to wait for it to
			// finish in order to determine the final location of where the hover cell
			// should be animated to.
			if(scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
				isWaitingForScrollFinish = true;
				return;
			}
			hoverCellCurrentBounds.offsetTo(hoverCellOriginalBounds.left, mobileView.getTop());
			ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(hoverCell, "bounds",
					sBoundEvaluator, hoverCellCurrentBounds);
			hoverViewAnimator.addUpdateListener(valueAnimator -> invalidate());
			hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					setEnabled(false);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					aboveItemID = INVALID_ID;
					mobileItemID = INVALID_ID;
					belowItemID = INVALID_ID;
					mobileView.setVisibility(VISIBLE);
					hoverCell = null;
					setEnabled(true);
					invalidate();
				}
			});
			hoverViewAnimator.start();
			if(swapElementCallback != null) {
				swapElementCallback.onSwapElement();
			}
		}
		else {
			touchEventsCancelled();
		}
	}

	/**
	 * Resets all the appropriate fields to a default state.
	 */
	private void touchEventsCancelled() {
		View mobileView = getViewForID(mobileItemID);
		if(isCellMobile) {
			aboveItemID = INVALID_ID;
			mobileItemID = INVALID_ID;
			belowItemID = INVALID_ID;
			mobileView.setVisibility(VISIBLE);
			hoverCell = null;
			invalidate();
		}
		isCellMobile = false;
		isMobileScrolling = false;
		activePointerID = INVALID_POINTER_ID;
	}

	/**
	 * This TypeEvaluator is used to animate the BitmapDrawable back to its
	 * final location when the user lifts his finger by modifying the
	 * BitmapDrawable's bounds.
	 */
	private final static TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
		public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
			return new Rect(interpolate(startValue.left, endValue.left, fraction),
					interpolate(startValue.top, endValue.top, fraction),
					interpolate(startValue.right, endValue.right, fraction),
					interpolate(startValue.bottom, endValue.bottom, fraction));
		}

		private int interpolate(int start, int end, float fraction) {
			return (int) (start + fraction * (end - start));
		}
	};

	/**
	 * Determines whether this listview is in a scrolling state invoked
	 * by the fact that the hover cell is out of the bounds of the listview;
	 */
	private void handleMobileCellScroll() {
		isMobileScrolling = handleMobileCellScroll(hoverCellCurrentBounds);
	}

	/**
	 * This method is in charge of determining if the hover cell is above
	 * or below the bounds of the listview. If so, the listview does an appropriate
	 * upward or downward smooth scroll so as to reveal new items.
	 */
	public boolean handleMobileCellScroll(Rect r) {
		int offset = computeVerticalScrollOffset();
		int height = getHeight();
		int extent = computeVerticalScrollExtent();
		int range = computeVerticalScrollRange();
		int hoverViewTop = r.top;
		int hoverHeight = r.height();
		if(hoverViewTop <= 0 && offset > 0) {
			smoothScrollBy(-smoothScrollAmountAtEdge, 0);
			return true;
		}
		if(hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
			smoothScrollBy(smoothScrollAmountAtEdge, 0);
			return true;
		}
		return false;
	}

	public void setItemList(ArrayList<T> itemList) {
		this.itemList = itemList;
	}

	/**
	 * This scroll listener is added to the listview in order to handle cell swapping
	 * when the cell is either at the top or bottom edge of the listview. If the hover
	 * cell is at either edge of the listview, the listview will begin scrolling. As
	 * scrolling takes place, the listview continuously checks if new cells became visible
	 * and determines whether they are potential candidates for a cell swap.
	 */
	private OnScrollListener scrollListener = new OnScrollListener() {

		private int mPreviousFirstVisibleItem = -1;
		private int mPreviousVisibleItemCount = -1;
		private int mCurrentFirstVisibleItem;
		private int mCurrentVisibleItemCount;
		private int mCurrentScrollState;

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
							 int totalItemCount) {
			mCurrentFirstVisibleItem = firstVisibleItem;
			mCurrentVisibleItemCount = visibleItemCount;
			mPreviousFirstVisibleItem = (mPreviousFirstVisibleItem == -1) ? mCurrentFirstVisibleItem
					: mPreviousFirstVisibleItem;
			mPreviousVisibleItemCount = (mPreviousVisibleItemCount == -1) ? mCurrentVisibleItemCount
					: mPreviousVisibleItemCount;
			checkAndHandleFirstVisibleCellChange();
			checkAndHandleLastVisibleCellChange();
			mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
			mPreviousVisibleItemCount = mCurrentVisibleItemCount;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int state) {
			mCurrentScrollState = state;
			scrollState = state;
			isScrollCompleted();
		}

		/**
		 * This method is in charge of invoking 1 of 2 actions. Firstly, if the listview
		 * is in a state of scrolling invoked by the hover cell being outside the bounds
		 * of the listview, then this scrolling event is continued. Secondly, if the hover
		 * cell has already been released, this invokes the animation for the hover cell
		 * to return to its correct position after the listview has entered an idle scroll
		 * state.
		 */
		private void isScrollCompleted() {
			if(mCurrentVisibleItemCount > 0 && mCurrentScrollState == SCROLL_STATE_IDLE) {
				if(isCellMobile && isMobileScrolling) {
					handleMobileCellScroll();
				}
				else if(isWaitingForScrollFinish) {
					touchEventsEnded();
				}
			}
		}

		/**
		 * Determines if the listview scrolled up enough to reveal a new cell at the
		 * top of the list. If so, then the appropriate parameters are updated.
		 */
		public void checkAndHandleFirstVisibleCellChange() {
			if(mCurrentFirstVisibleItem != mPreviousFirstVisibleItem) {
				if(isCellMobile && mobileItemID != INVALID_ID) {
					updateNeighborViewsForID(mobileItemID);
					handleCellSwitch();
				}
			}
		}

		/**
		 * Determines if the listview scrolled down enough to reveal a new cell at the
		 * bottom of the list. If so, then the appropriate parameters are updated.
		 */
		public void checkAndHandleLastVisibleCellChange() {
			int currentLastVisibleItem = mCurrentFirstVisibleItem + mCurrentVisibleItemCount;
			int previousLastVisibleItem = mPreviousFirstVisibleItem + mPreviousVisibleItemCount;
			if(currentLastVisibleItem != previousLastVisibleItem) {
				if(isCellMobile && mobileItemID != INVALID_ID) {
					updateNeighborViewsForID(mobileItemID);
					handleCellSwitch();
				}
			}
		}
	};

	public void setOnSwapElementCallback(OnSwapElementCallback swapElementCallback) {
		this.swapElementCallback = swapElementCallback;
	}
}