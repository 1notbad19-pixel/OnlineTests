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
        return new TagResponse(
        tag.getId(),
        tag.getName()
    );
    }
}