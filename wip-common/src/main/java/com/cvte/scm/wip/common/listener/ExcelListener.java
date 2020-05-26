package com.cvte.scm.wip.common.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import javax.sql.rowset.BaseRowSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel文件的监听器，该监视器功能较为单一。
 * <p>
 * 如果希望有更多定制化操作，可以继承该类，并重写invoke方法。
 *
 * @param <T> 数据实例对象
 * @author : jf
 * Date    : 2019.11.06
 * Time    : 19:24
 * Email   ：jiangfeng7128@cvte.com
 */
public class ExcelListener<T extends BaseRowSet> extends AnalysisEventListener<T> {

    private final List<T> data;

    public ExcelListener(List<T> data) {
        this.data = data;
    }

    public ExcelListener() {
        this(new ArrayList<>());
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        data.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<T> getData() {
        return data;
    }
}
