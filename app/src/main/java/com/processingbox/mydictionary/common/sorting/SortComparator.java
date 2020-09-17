package com.processingbox.mydictionary.common.sorting;

import java.util.Comparator;

/**
 * Comparator that uses {@link ISortObject#getComparableValue()} for sorting elements
 * @author: Cédric BERTRAND
 * @date: 20/09/2016.
 */
public class SortComparator implements Comparator<ISortObject> {
  @Override
  public int compare(final ISortObject o1, final ISortObject o2) {
    return Double.compare(o1.getComparableValue(), o2.getComparableValue());
  }
}
