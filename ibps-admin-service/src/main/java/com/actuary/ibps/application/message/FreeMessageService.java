package com.actuary.ibps.application.message;

import com.actuary.ibps.domain.common.PageResult;
import com.actuary.ibps.domain.message.FreeMessage;
import com.actuary.ibps.domain.message.FreeMessageQuery;
import com.actuary.ibps.infrastructure.entity.FreeMessageEntity;
import com.actuary.ibps.infrastructure.mapper.FreeMessageMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreeMessageService {

    private static final ThreadLocal<SimpleDateFormat> DATE_FMT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private final FreeMessageMapper freeMessageMapper;

    public FreeMessageService(FreeMessageMapper freeMessageMapper) {
        this.freeMessageMapper = freeMessageMapper;
    }

    public PageResult<FreeMessage> queryByPage(FreeMessageQuery query) {
        String startDate = query.getStartDate() != null ? DATE_FMT.get().format(query.getStartDate()) : null;
        String endDate = query.getEndDate() != null ? DATE_FMT.get().format(query.getEndDate()) : null;

        long total = freeMessageMapper.countByQuery(query.getDirection(), startDate, endDate);
        if (total == 0) {
            return PageResult.empty();
        }

        List<FreeMessageEntity> entities = freeMessageMapper.selectByPage(
                query.getDirection(), startDate, endDate, query.getOffset(), query.getPageSize());

        List<FreeMessage> list = new ArrayList<>(entities.size());
        for (FreeMessageEntity entity : entities) {
            list.add(toDomain(entity));
        }

        return new PageResult<>(list, total, query.getPageNum(), query.getPageSize());
    }

    private FreeMessage toDomain(FreeMessageEntity entity) {
        FreeMessage msg = new FreeMessage();
        msg.setId(entity.getId());
        msg.setBusiSerial(entity.getBusiSerial());
        msg.setMsgDirection(entity.getMsgDirection());
        msg.setSenderCode(entity.getSenderCode());
        msg.setReceiverCode(entity.getReceiverCode());
        msg.setMsgContent(entity.getMsgContent());
        msg.setPriority(entity.getPriority());
        msg.setSendStatus(entity.getSendStatus());
        msg.setMsgRef(entity.getMsgRef());
        msg.setCrossBorderType(entity.getCrossBorderType());
        msg.setCrossBorderOrigSerial(entity.getCrossBorderOrigSerial());
        msg.setCreateTime(entity.getCreateTime());
        msg.setUpdateTime(entity.getUpdateTime());
        return msg;
    }
}
