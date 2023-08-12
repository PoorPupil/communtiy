package com.nowcoder.community.entity;


public class Page {

    // 当前页码, 默认为1
    private int current = 1;

    // 显示上限，默认为10
    private int limit = 10;

    // 数据总数
    private int rows;

    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current > 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit > 0 && limit < 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页起始行
     * @return
     */
    public int getOffset(){
        // current * limit -limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal(){
        // 如果能整除就是整除结果，否则+1
        if(rows % limit == 0){
            return rows / limit ;
        }else {
            return rows / limit + 1;
        }
    }

    /**
     *  起始页码
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 2 ? 1: from;
    }

    /**
     * 终止页码
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
