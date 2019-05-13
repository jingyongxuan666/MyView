package com.jyx.pickerview;

import java.util.List;

/**
 * File description.
 *
 * @author Jingyongxuan
 * @date 2019/5/13
 */
public class CityBean {

    /**
     * label : 北京
     * value : 1
     * children : [{"label":"东城区","value":1},{"label":"西城区","value":2},{"label":"崇文区","value":3},{"label":"宣武区","value":4},{"label":"朝阳区","value":5},{"label":"丰台区","value":6},{"label":"石景山区","value":7},{"label":"海淀区","value":8},{"label":"门头沟区","value":9},{"label":"房山区","value":10},{"label":"通州区","value":11},{"label":"顺义区","value":12},{"label":"昌平区","value":13},{"label":"大兴区","value":14},{"label":"平谷区","value":15},{"label":"怀柔区","value":16},{"label":"密云县","value":17},{"label":"延庆县","value":18}]
     */

    private String label;
    private int value;
    private List<ChildrenBean> children;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    public static class ChildrenBean {
        /**
         * label : 东城区
         * value : 1
         */

        private String label;
        private int value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
