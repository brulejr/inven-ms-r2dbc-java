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
package io.jrb.labs.invenms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import io.jrb.labs.common.service.crud.CrudServiceSupport;
import io.jrb.labs.invenms.model.EntityType;
import io.jrb.labs.invenms.model.Item;
import io.jrb.labs.invenms.model.LookupValue;
import io.jrb.labs.invenms.model.LookupValueType;
import io.jrb.labs.invenms.repository.ItemRepository;
import io.jrb.labs.invenms.repository.LookupValueRepository;
import io.jrb.labs.invenms.resource.ItemResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
public class ItemServiceImpl extends CrudServiceSupport<Item, Item.ItemBuilder> implements ItemService {

    private final ItemRepository itemRepository;
    private final LookupValueRepository lookupValueRepository;

    public ItemServiceImpl(
            final ItemRepository itemRepository,
            final LookupValueRepository lookupValueRepository,
            final ObjectMapper objectMapper
    ) {
        super(Item.class, itemRepository, objectMapper);
        this.itemRepository = itemRepository;
        this.lookupValueRepository = lookupValueRepository;
    }

    @Override
    @Transactional
    public Mono<ItemResource> createItem(final ItemResource item) {
        return createEntity(Item.fromResource(item))
                .flatMap(itemEntity -> {
                    final long itemId = itemEntity.getId();
                    return Mono.zip(
                            Mono.just(itemEntity),
                            createLookupValues(itemId, LookupValueType.GROUP, item.getGroups()),
                            createLookupValues(itemId, LookupValueType.TAG, item.getTags())
                    );
                })
                .map(tuple -> ItemResource.fromEntity(tuple.getT1())
                        .groups(tuple.getT2())
                        .tags(tuple.getT3())
                        .build());
    }

    @Override
    @Transactional
    public Mono<Void> deleteItem(final UUID itemGuid) {
        return deleteEntity(itemGuid, itemEntity -> {
            final long itemId = itemEntity.getId();
            return lookupValueRepository.deleteByEntityTypeAndEntityId(EntityType.ITEM, itemId)
                    .then(itemRepository.deleteById(itemId));
        });
    }

    @Override
    @Transactional
    public Mono<ItemResource> findItemByGuid(final UUID itemGuid) {
        return findEntityByGuid(itemGuid)
                .zipWhen(item -> findItemValueList(item.getId()))
                .map(tuple -> {
                    final ItemResource.ItemResourceBuilder builder = ItemResource.fromEntity(tuple.getT1());
                    tuple.getT2().forEach(lookupValue -> {
                        final String value = lookupValue.getValue();
                        switch (lookupValue.getValueType()) {
                            case GROUP:
                                builder.group(value);
                                break;
                            case TAG:
                                builder.tag(value);
                                break;
                        }
                    });
                    return builder.build();
                });
    }

    @Override
    @Transactional
    public Flux<ItemResource> listAllItems() {
        return retrieveEntities()
                .map(entity -> ItemResource.fromEntity(entity).build());
    }

    @Override
    @Transactional
    public Mono<ItemResource> updateItem(final UUID guid, final JsonPatch patch) {
        return updateEntity(guid, entity -> {
            final ItemResource resource = ItemResource.fromEntity(entity).build();
            final ItemResource updatedResource = applyPatch(guid, patch, resource, ItemResource.class);
            return Item.fromResource(updatedResource);
        }).flatMap(entity -> findItemByGuid(entity.getGuid()));
    }

    private Mono<List<String>> createLookupValues(
            final long itemId,
            final LookupValueType type,
            final List<String> values
    ) {
        return Flux.fromIterable(values)
                .map(value -> LookupValue.builder()
                        .entityType(EntityType.ITEM)
                        .entityId(itemId)
                        .valueType(type)
                        .value(value)
                        .build())
                .flatMap(lookupValueRepository::save)
                .map(LookupValue::getValue)
                .collectList();
    }

    private Mono<List<LookupValue>> findItemValueList(final long itemId) {
        return lookupValueRepository.findByEntityTypeAndEntityId(EntityType.ITEM, itemId)
                .collectList();
    }

}
