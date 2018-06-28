/*
 * 文件名：Column.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月7日
 * 修改内容：
 */

package com.zjtelcom.cpct.util;


import java.text.Collator;


/**
 * 列对象
 * @author taowenwu
 * @version 1.0
 * @see Column
 * @since
 */

public class Column implements Comparable<Column> {
    private String name;

    private String tableAlias;

    public Column() {
        super();
    }

    public Column(String name, String tableAlias) {
        super();
        this.name = name;
        this.tableAlias = tableAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Column o) {
        Collator collator = Collator.getInstance();
        return collator.compare(this.getName(), o.getName());
    }

}
