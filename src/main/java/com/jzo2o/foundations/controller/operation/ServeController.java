package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * author peng
 * description
 * date 2024/6/18
 */
@RestController("operationServeController")
@RequestMapping("/operation/serve")
@Api(tags = "运营端 - 区域服务相关接口")
public class ServeController {

    @Resource
    private IServeService serveService;

    @GetMapping("/page")
    @ApiOperation("区域服务分页查询")
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
        return serveService.page(servePageQueryReqDTO);
    }

    @PostMapping("/batch")
    @ApiOperation("区域服务批量添加")
    public void add(@RequestBody List<ServeUpsertReqDTO> serveUpsertReqDTO){
        serveService.batchAdd(serveUpsertReqDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("区域服务价格修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "price", value = "价格", required = true, dataTypeClass = BigDecimal.class)
    })
    public void update( @PathVariable("id") Long id,
                        @RequestParam("price") BigDecimal price) {
        serveService.update(id, price);
    }

    @PutMapping("/onSale/{id}")
    @ApiOperation("区域服务上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void onSale(@PathVariable("id") Long id) {
        serveService.onSale(id);
    }
}
