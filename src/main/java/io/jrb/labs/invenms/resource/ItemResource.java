/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.jrb.labs.invenms.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.jrb.labs.invenms.model.Item;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = ItemResource.ItemResourceBuilder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemResource {

    UUID guid;
    String name;
    String description;
    String createdBy;
    Instant createdOn;
    String modifiedBy;
    Instant modifiedOn;

    @Singular
    List<String> groups;

    @Singular
    List<String> tags;

    public static ItemResource.ItemResourceBuilder fromEntity(final Item item) {
        return ItemResource.builder()
                .guid(item.getGuid())
                .name(item.getName())
                .description(item.getDescription())
                .createdBy(item.getCreatedBy())
                .createdOn(item.getCreatedOn())
                .modifiedBy(item.getModifiedBy())
                .modifiedOn(item.getModifiedOn());
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemResourceBuilder {
    }

}
