package com.processingbox.mydictionary.common.sorting;

/**
 * Object than can be compared.
 * @author: Cédric BERTRAND
 * @date: 20/09/2016.
 */
public interface ISortObject {

  /**
   * @return the double value that will be used to compare this object with another
   */
  double getComparableValue();
}
