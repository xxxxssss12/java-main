package xs.spider.base.pager;

/**
 * Created by hasee on 2016/12/24.
 */
public class PageContext {
    //定义两个threadLocal变量：pageNum和pageSize
    private static ThreadLocal<Integer> pageNum = new ThreadLocal<Integer>();
    private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();
    /*
     * offset ：get、set、remove
     */
    public static int getPageNum() {
        Integer os =pageNum.get();
        if (os == null) {
            return 0;
        }
        return os;
    }
    public static void initPage(int pageNum, int pageSize) {
        setPageNum(pageNum);
        setPageSize(pageSize);
    }
    public static int getStartNum() {
        int pagenum = getPageNum();
        int pagesize = getPageSize();
        int startnum = (pagenum-1) * pagesize;
        if (startnum > 0) {
            return startnum;
        } else {
            return 0;
        }
    }
    public static void setPageNum(int pageNumValue) {
        pageNum.set(pageNumValue);
    }
    public static void removePageNum(){
        pageNum.remove();
    }
    /*
     * pageSize ：get、set、remove
     */
    public static int getPageSize() {
        Integer ps = pageSize.get();
        if (ps == null) {
            return 0;
        }
        return ps;
    }
    public static void setPageSize(int pageSizeValue) {
        pageSize.set(pageSizeValue);
    }
    public static void removePageSize(){
        pageSize.remove();
    }
}
