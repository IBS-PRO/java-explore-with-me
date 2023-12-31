package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHit;

@UtilityClass
public class StatMapper {

    public static StatSvc mapToStat(EndpointHit dto) {
        StatSvc newStat = new StatSvc();
        newStat.setApp(dto.getApp());
        newStat.setUri(dto.getUri());
        newStat.setIp(dto.getIp());
        newStat.setTimestamp(dto.getTimestamp());
        return newStat;
    }
}
