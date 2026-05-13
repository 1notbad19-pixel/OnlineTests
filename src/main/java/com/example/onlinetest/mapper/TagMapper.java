package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import com.example.onlinetest.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.name());
        return tag;
    }

    public TagResponse toResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagResponse(
            tag.getId(),
            tag.getName()
        );
    }

    public void update(Tag tag, TagRequest request) {
        if (request.name() != null) {
            tag.setName(request.name());
        }
    }
}