package com.actuary.ibps.api.message;

import com.actuary.ibps.application.message.FreeMessageService;
import com.actuary.ibps.common.result.R;
import com.actuary.ibps.domain.common.PageResult;
import com.actuary.ibps.domain.message.FreeMessage;
import com.actuary.ibps.domain.message.FreeMessageQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/ibps/message")
public class FreeMessageController {

    private final FreeMessageService freeMessageService;

    public FreeMessageController(FreeMessageService freeMessageService) {
        this.freeMessageService = freeMessageService;
    }

    @GetMapping("/list")
    public R<PageResult<FreeMessage>> list(
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        FreeMessageQuery query = new FreeMessageQuery();
        query.setDirection(direction);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        PageResult<FreeMessage> result = freeMessageService.queryByPage(query);
        return R.ok(result);
    }
}
