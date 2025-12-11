package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.IndicatorPoint;
import org.example.postgraduaterecommendation.dto.IndicatorPointDTO;
import org.example.postgraduaterecommendation.dto.IndicatorPointTreeDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.postgraduaterecommendation.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorPointService {
    private final IndicatorPointRepository indicatorPointRepository;

    //添加指标点
    @Transactional
    public IndicatorPoint addIndicatorPoint(IndicatorPointDTO dto) {
        if (indicatorPointRepository.existsByParentIdAndName(dto.getParentId(), dto.getName())) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("同级下已存在同名指标点")
                    .build();
        }

        //根据父节点自动计算当前节点在树形结构中的层级深度
        //当前节点的层级为父节点层级加1
        Integer level = dto.getParentId() == null ? 1 :
                indicatorPointRepository.findById(dto.getParentId())
                        .map(p -> p.getLevel() + 1)
                        .orElseThrow(() -> XException.builder()
                                .number(Code.ERROR)
                                .message("父节点不存在")
                                .build());

        if (Boolean.TRUE.equals(dto.getIsLeaf()) && dto.getParentId() != null) {
            IndicatorPoint parent = indicatorPointRepository.findById(dto.getParentId())
                    .orElseThrow(() -> XException.builder()
                            .number(Code.ERROR)
                            .message("父节点不存在")
                            .build());

            if (Boolean.TRUE.equals(parent.getIsLeaf())) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("不能在叶子节点下添加子节点")
                        .build();
            }
        }

        //如果前端有传值就用，没有的话默认是叶子节点
        Boolean isLeaf = dto.getIsLeaf() != null ? dto.getIsLeaf() : true;

        //构建指标点
        IndicatorPoint point = IndicatorPoint.builder()
                .majorCategoryId(dto.getMajorCategoryId())
                .name(dto.getName())
                .level(level)
                .description(dto.getDescription())
                .maxScore(dto.getMaxScore())
                .itemUpperLimit(dto.getItemUpperLimit())//申报上限
                .parentId(dto.getParentId())
                .isLeaf(isLeaf)
                .build();

        IndicatorPoint saved = indicatorPointRepository.save(point);

        return saved;
    }

    //更新指标点
    @Transactional
    public IndicatorPoint updateIndicatorPoint(Long id, IndicatorPointDTO dto) {
        IndicatorPoint point = indicatorPointRepository.findById(id)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("指标点不存在")
                        .build());

        // 验证同级同名（排除自身）
        boolean existsSameName = indicatorPointRepository.findByParentId(point.getParentId()) // 使用原parentId
                .stream()
                .filter(p -> !p.getId().equals(id))
                .anyMatch(p -> p.getName().equals(dto.getName()));

        if (existsSameName) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("同级下已存在同名指标点")
                    .build();
        }

        // 验证叶子节点状态变更
        if (Boolean.TRUE.equals(dto.getIsLeaf()) && !Boolean.TRUE.equals(point.getIsLeaf())) {
            // 检查是否有子节点
            if (indicatorPointRepository.existsByParentId(id)) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("该指标点存在子节点，不能改为叶子节点")
                        .build();
            }
        }

        //只更新允许修改的字段，不更新层级和父节点
        point.setName(dto.getName());
        point.setDescription(dto.getDescription());
        point.setMaxScore(dto.getMaxScore());
        point.setItemUpperLimit(dto.getItemUpperLimit());
        point.setIsLeaf(dto.getIsLeaf());

        return indicatorPointRepository.save(point);
    }


    @Transactional
    public void removeIndicatorPoint(Long id) {
        IndicatorPoint point = indicatorPointRepository.findById(id)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("指标点不存在")
                        .build());

        //检查是否有子节点
        if (indicatorPointRepository.existsByParentId(id)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该指标点存在子节点，无法删除")
                    .build();
        }

        indicatorPointRepository.deleteById(id);

        // 检查父节点是否变成叶子节点
        if (point.getParentId() != null &&
                !indicatorPointRepository.existsByParentId(point.getParentId())) {
            indicatorPointRepository.updateLeafStatus(point.getParentId(), true);
        }
    }

    public List<IndicatorPointTreeDTO> getIndicatorTree(Long majorCategoryId) {
        //查询所有相关指标点
        List<IndicatorPoint> allPoints = indicatorPointRepository.findByMajorCategoryId(majorCategoryId);

        //构建父节点ID到子节点列表的映射
        Map<Long, List<IndicatorPoint>> childrenMap = allPoints.stream()
                .filter(point -> point.getParentId() != null)
                .collect(Collectors.groupingBy(IndicatorPoint::getParentId));

        //找出所有根节点（parentId为null的节点）
        return allPoints.stream()
                .filter(point -> point.getParentId() == null)
                .map(root -> convertToTreeDTO(root, childrenMap))
                .collect(Collectors.toList());
    }



    private IndicatorPointTreeDTO convertToTreeDTO(IndicatorPoint point,
                                                   Map<Long, List<IndicatorPoint>> childrenMap) {
        IndicatorPointTreeDTO dto = IndicatorPointTreeDTO.builder()
                .id(point.getId())
                .majorCategoryId(point.getMajorCategoryId())
                .name(point.getName())
                .level(point.getLevel())
                .description(point.getDescription())
                .maxScore(point.getMaxScore())
                .itemUpperLimit(point.getItemUpperLimit())
                .isLeaf(point.getIsLeaf())
                .createTime(point.getCreateTime())
                .updateTime(point.getUpdateTime())
                .build();

        //递归构建子节点
        List<IndicatorPoint> children = childrenMap.get(point.getId());
        if (children != null && !children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(child -> convertToTreeDTO(child, childrenMap))
                    .collect(Collectors.toList()));
        } else {
            dto.setChildren(Collections.emptyList());
        }

        return dto;
    }

    public List<IndicatorPoint> getChildren(Long parentId) {
        return indicatorPointRepository.findByParentId(parentId);
    }
}
