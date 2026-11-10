package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.attendance.entity.NfcTag;
import lombok.Getter;

@Getter
public class NfcTagResponse {

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("tag_uid")
    private final String tagUid;

    public NfcTagResponse(NfcTag tag) {
        this.id = tag.getId();
        this.classId = tag.getClassId();
        this.tagUid = tag.getTagUid();
    }
}
