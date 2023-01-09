package org.apache.skyline.admin.server.utils;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.boot.context.properties.PropertyMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author hejianbing
 * @version @Id: PageCommonUtils.java, v 0.1 2023年01月08日 22:30 hejianbing Exp $
 */
public class PageCommonUtils {

    public static <S,R> PageBean<R> convert(Page<S> page, Function<List<S>,List<R>> convert) {
        PageBean<R> pageBean = new PageBean<>();
        pageBean.setPageNum((int) page.getCurrent());
        pageBean.setPageSize((int)page.getSize());
        pageBean.setTotalCount((int)page.getTotal());

        List<S> records = page.getRecords();

        PropertyMapper propertyMapper = PropertyMapper.get();

        propertyMapper.from(records).when(CollectionUtils::isNotEmpty)
                .as(rs -> convert.apply(rs)).to(items->{
                    pageBean.setData(items);
                });

        return pageBean;
    }

    public static <S,R> PageBean<R> convert(PageBean<S> page,Function<List<S>,List<R>> convert) {
        PageBean<R> pageBean = new PageBean<>();
        pageBean.setPageNum( page.getPageNum());
        pageBean.setPageSize(page.getPageSize());
        pageBean.setTotalCount(page.getTotalCount());

        List<S> records = page.getData();

        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(records).when(CollectionUtils::isNotEmpty)
                .as(rs -> convert.apply(rs)).to(items->{
                    pageBean.setData(items);
                });

        return pageBean;

    }


}