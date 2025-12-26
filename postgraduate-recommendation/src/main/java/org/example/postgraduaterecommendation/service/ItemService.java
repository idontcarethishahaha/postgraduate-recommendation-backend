package org.example.postgraduaterecommendation.service;


import org.example.postgraduaterecommendation.dox.Item;
import org.example.postgraduaterecommendation.dto.ItemDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    //添加指标项（清缓存）
    @CacheEvict(value = "items", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void addItem(Item item) {
        itemRepository.save(item);
    }

    // 移除指标项
    @Transactional
    public void removeItem(@PathVariable long itemid) {
        if(!itemRepository.existsById(itemid)){
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("指标不存在")
                    .build();
        }
        itemRepository.deleteById(itemid);
    }

    //查询顶级指标项（缓存）
    @Cacheable(value = "items", key = "'top-' + #mcid")
    public List<Item> listTopItems(long mcid) {
        return itemRepository.findTopByMajorCategoryId(mcid);
    }

    //学生,基于类别+父指标加载封装的二级指标（缓存）
    @Cacheable(value = "items", key = "#catid+#parentid")
    public ItemDTO listItems(long mcid, long parentid) {
        List<Item> items = itemRepository.findByMajorCategoryIdAndParentId(mcid, parentid);
        return convertToItemDTO(items, parentid);
    }

    //指标项转DTO（按父节点封装层级）
    private ItemDTO convertToItemDTO(List<Item> items, long parentid) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        // 建立 id -> DTO 映射
        Map<Long, ItemDTO> itemMap = items.stream()
                .map(item -> {
                    ItemDTO dto = new ItemDTO();
                    BeanUtils.copyProperties(item, dto);
                    return dto;
                })
                .collect(Collectors.toMap(ItemDTO::getId, dto -> dto));

        // 分组：parentId -> 所有子节点列表
        Map<Long, List<ItemDTO>> childrenMap = itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() != null)
                .collect(Collectors.groupingBy(ItemDTO::getParentId));

        // 将子节点挂到对应的父节点上
        childrenMap.forEach((id, children) -> {
            ItemDTO parent = itemMap.get(id);
            if (parent != null) {
                children.sort(Comparator.comparing(ItemDTO::getId));
                parent.setItems(children);
            }
        });
        // 返回根节点
        return itemMap
                .values()
                .stream()
                .filter(dto -> dto.getId() == parentid)
                .findFirst()
                .orElse(null);
    }

    //指标项转DTO
    private List<ItemDTO> convertToItemDTO(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        // 建立 id -> DTO 映射
        Map<Long, ItemDTO> itemMap = items.stream()
                .map(item -> {
                    ItemDTO dto = new ItemDTO();
                    BeanUtils.copyProperties(item, dto);
                    return dto;
                })
                .collect(Collectors.toMap(ItemDTO::getId, dto -> dto));

        // 分组：parentId -> 所有子节点列表
        Map<Long, List<ItemDTO>> childrenMap = itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() != null)
                .collect(Collectors.groupingBy(ItemDTO::getParentId));

        // 将子节点挂到对应的父节点上
        childrenMap.forEach((id, children) -> {
            ItemDTO parent = itemMap.get(id);
            if (parent != null) {
                children.sort(Comparator.comparing(ItemDTO::getId));
                parent.setItems(children);
            }
        });
        // 根节点：parentId 为 null 或父节点不在 map 中
        return itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() == null || !itemMap.containsKey(dto.getParentId()))
                .sorted(Comparator.comparing(ItemDTO::getId))
                .collect(Collectors.toList());
    }

    //基于类别加载全部指标项（缓存）
    @Cacheable(value = "items", key = "#mcid")
    public List<ItemDTO> listItems(long mcid) {
        List<Item> items = itemRepository.findByMajorCategoryId(mcid);
        return convertToItemDTO(items);
    }

    //查询单个指标项
    public Item getItem(long id, long mcid) {
        Optional<Item> item = itemRepository.findByIdAndMajorCategoryId(id, mcid);
        if (item.isEmpty()) {
            throw new RuntimeException("指标项不存在");
        }
        return item.get();
    }
}
