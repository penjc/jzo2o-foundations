package com.jzo2o.foundations.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Serve;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务类
 * </p>
 *
 * @author peng
 * @since 2024-06-18
 */
public interface IServeService extends IService<Serve> {
    /**
     * 分页查询服务列表
     * @param servePageQueryReqDTO
     * @return
     */
    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 批量添加区域服务
     * @param serveUpsertReqDTOList
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    /**
     * 服务价格修改
     *
     * @param id    服务id
     * @param price 价格
     * @return 服务
     */
    Serve update(Long id, BigDecimal price);

    /**
     * 区域服务上架
     * @param id
     * @return
     */
    Serve onSale(Long id);
}
