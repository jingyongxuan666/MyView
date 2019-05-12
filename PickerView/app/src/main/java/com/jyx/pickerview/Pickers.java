package com.jyx.pickerview;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author zengtao 2015年5月20日下午7:18:14
 *
 */
public class Pickers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String showContent;
    private String showId;
    private List<Pickers> pickerList;

    public List<Pickers> getPickerList() {
        return pickerList;
    }

    public void setPickerList(List<Pickers> pickerList) {
        this.pickerList = pickerList;
    }

    public String getShowContent() {
        return showContent;
    }

    public String getShowId() {
        return showId;
    }

    public Pickers(String showConetnt, String showId) {
        super();
        this.showContent = showConetnt;
        this.showId = showId;
    }

    public Pickers(String showContent, String showId, List<Pickers> pickerList) {
        this.showContent = showContent;
        this.showId = showId;
        this.pickerList = pickerList;
    }

    public Pickers() {
        super();
    }

}
