package com.jzo2o.foundations.service.impl;

import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务实现类
 * </p>
 *
 * @author peng
 * @since 2024-06-18
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

    @Resource
    private ServeItemMapper serveItemMapper;
    @Resource
    private RegionMapper regionMapper;

    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        //调用mapper查询数据，这里由于继承了ServiceImpl<ServeMapper, Serve>，使用baseMapper相当于使用ServeMapper
        return PageHelperUtils.selectPage(servePageQueryReqDTO, () -> baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
    }

    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        for (ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            //合法性校验，是否存在并且启用服务项
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if(ObjectUtils.isNull(serveItem) || serveItem.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
                throw new ForbiddenOperationException("服务项不存在或服务项为启动不允许添加");
            }
            //同一区域下不能有相同服务
            Integer count = lambdaQuery()
                    .eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                    .count();
            if(count > 0){
                throw new ForbiddenOperationException(serveItem.getName() + "服务已存在");
            }
            //组装数据
            Serve serve = BeanUtils.toBean(serveUpsertReqDTO, Serve.class);
            Long regionId = serve.getRegionId();
            String cityCode = regionMapper.selectById(regionId).getCityCode();
            serve.setCityCode(cityCode);
            //插入
            baseMapper.insert(serve);
        }
    }

    @Override
    @Transactional
    public Serve update(Long id, BigDecimal price) {
        //1.更新服务价格
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getPrice, price)
                .update();
        if(!update){
            throw new CommonException("修改服务价格失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve onSale(Long id) {
        //根据id查询该服务
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtils.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }

        //合法性校验，如果服务状态为草稿或下架则可以上架
        Integer saleStatus = serve.getSaleStatus();
        if(!(saleStatus == FoundationStatusEnum.INIT.getStatus() || saleStatus == FoundationStatusEnum.DISABLE.getStatus())){
            throw new ForbiddenOperationException("草稿或下架状态方可上架");
        }
        //服务项状态为启用才可以上架
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtils.isNull(serveItem)){
            throw new ForbiddenOperationException("服务项不存在");
        }

        if(serveItem.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务项为启用才可以上架");

        }
        //上架
        boolean update = lambdaUpdate().eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus())
                .update();
        if(!update){
            throw new CommonException("上架失败");
        }
        return baseMapper.selectById(id);
    }
}
