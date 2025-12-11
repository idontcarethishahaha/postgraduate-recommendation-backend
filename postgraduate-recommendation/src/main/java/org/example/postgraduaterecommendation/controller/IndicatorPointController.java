package org.example.postgraduaterecommendation.controller;

/*
 * @author wuwenjin
 */


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.IndicatorPoint;
import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.example.postgraduaterecommendation.dto.IndicatorPointDTO;
import org.example.postgraduaterecommendation.dto.IndicatorPointTreeDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.service.IndicatorPointService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collegeadmin/indicatorpoints")
@RequiredArgsConstructor
public class IndicatorPointController {
    private final IndicatorPointService indicatorPointService;
    private final CollegeService collegeService;

    @GetMapping("/categories")
    public ResultVO getCategories(HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        List<MajorCategory> categories = collegeService.listAllMajorCategories(cid);

        List<Map<String, Object>> result = categories.stream()
                .map(category -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", category.getId().toString());
                    map.put("name", category.getName());
                    map.put("calculationRule", category.getCalculationRule());
                    map.put("createTime", category.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());

        return ResultVO.success(result);
    }

    @GetMapping("/byCategory")
    public ResultVO getIndicatorsByCategory(@RequestParam String majorCategoryId,
                                            HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");

        //用字符串id调用Service
        List<IndicatorPointTreeDTO> tree = indicatorPointService.getIndicatorTree(Long.valueOf(majorCategoryId));
        List<Map<String, Object>> result = tree.stream()
                .map(this::convertTreeToMap)
                .collect(Collectors.toList());

        //获取类别名
        String majorCategoryName = getCategoryNameByIdString(majorCategoryId, cid);

        Map<String, Object> response = new HashMap<>();
        response.put("majorCategoryId", majorCategoryId);
        response.put("majorCategoryName", majorCategoryName);
        response.put("indicators", result);

        return ResultVO.success(response);
    }

    //根据字符串ID获取类别名称
    private String getCategoryNameByIdString(String majorCategoryId, Long collegeId) {
        List<MajorCategory> categories = collegeService.listAllMajorCategories(collegeId);
        return categories.stream()
                .filter(cat -> cat.getId().toString().equals(majorCategoryId))
                .findFirst()
                .map(MajorCategory::getName)
                .orElse("未知类别");
    }

    // id转换，防止精度丢失
    private Long parseId(String idStr) {
        try {
            if (idStr.contains("E") || idStr.contains("e")) {
                return Double.valueOf(idStr).longValue();
            }
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("ID格式错误: " + idStr)
                    .build();
        }
    }

    //实体转Map，id转换成string
    private Map<String, Object> convertToMap(IndicatorPoint point) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", point.getId().toString());
        map.put("majorCategoryId", point.getMajorCategoryId().toString());
        map.put("name", point.getName());
        map.put("level", point.getLevel());
        map.put("description", point.getDescription());
        map.put("maxScore", point.getMaxScore());
        map.put("itemUpperLimit", point.getItemUpperLimit());
        map.put("parentId", point.getParentId() != null ? point.getParentId().toString() : null);
        map.put("isLeaf", point.getIsLeaf());
        map.put("createTime", point.getCreateTime());
        map.put("updateTime", point.getUpdateTime());
        return map;
    }

    //树形节点转换
    private Map<String, Object> convertTreeToMap(IndicatorPointTreeDTO node) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", node.getId().toString());
        map.put("majorCategoryId", node.getMajorCategoryId().toString());
        map.put("name", node.getName());
        map.put("level", node.getLevel());
        map.put("description", node.getDescription());
        map.put("maxScore", node.getMaxScore());
        map.put("itemUpperLimit", node.getItemUpperLimit());
        map.put("isLeaf", node.getIsLeaf());
        map.put("createTime", node.getCreateTime());
        map.put("updateTime", node.getUpdateTime());

        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            map.put("children", node.getChildren().stream()
                    .map(this::convertTreeToMap)
                    .collect(Collectors.toList()));
        }
        return map;
    }

    @PostMapping
    public ResultVO addIndicatorPoint(
            @RequestBody IndicatorPointDTO dto,
            HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        validateCollegePermission(dto.getMajorCategoryId(), cid);

        IndicatorPoint point = indicatorPointService.addIndicatorPoint(dto);
        return ResultVO.success(convertToMap(point));
    }

    @GetMapping("/tree")
    public ResultVO getIndicatorTree(@RequestParam String majorCategoryId,
                                     HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        Long categoryId = parseId(majorCategoryId);
        validateCollegePermission(categoryId, cid);

        List<IndicatorPointTreeDTO> tree = indicatorPointService.getIndicatorTree(categoryId);
        List<Map<String, Object>> result = tree.stream()
                .map(this::convertTreeToMap)
                .collect(Collectors.toList());
        return ResultVO.success(result);
    }

    @GetMapping("/{id}/children")
    public ResultVO getChildren(@PathVariable String id,
                                HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        Long pointId = parseId(id);

        List<IndicatorPoint> children = indicatorPointService.getChildren(pointId);
        List<Map<String, Object>> result = children.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
        return ResultVO.success(result);
    }

    @DeleteMapping("/{id}")
    public ResultVO removeIndicatorPoint(@PathVariable String id,
                                         HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        Long pointId = parseId(id);

        indicatorPointService.removeIndicatorPoint(pointId);
        return ResultVO.ok();
    }

    @PutMapping("/{id}")
    public ResultVO updateIndicatorPoint(@PathVariable String id,
                                         @RequestBody IndicatorPointDTO dto,
                                         HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        Long pointId = parseId(id);
        validateCollegePermission(dto.getMajorCategoryId(), cid);

        IndicatorPoint updated = indicatorPointService.updateIndicatorPoint(pointId, dto);
        return ResultVO.success(convertToMap(updated));
    }

    //验证学院权限
    private void validateCollegePermission(Long majorCategoryId, Long collegeId) {
        if (collegeId == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }
        //权限验证逻辑
    }
}