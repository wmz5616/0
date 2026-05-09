package com.zemcho.guzhe.util;

import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List Tree互转通过方法
 * 1.树不能有环，否则会陷入死循环
 * 2.节点列表不能有null的节点，否则会抛出NPE
 */
public class ListTreeConvertUtil {
    /**
     * 获取最上层节点
     *
     * @param list        节点列表
     * @param parentField 父节点id字段名称
     * @param idField     当前节点id字段名称
     * @param <T>         节点类型
     * @return 最上层节点
     */
    public static <T> T getTopElement(List<T> list, String parentField, String idField) {
        Assert.notEmpty(list, "list is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        Set<Object> ids = list.stream()
                .map(o -> getFieldValue(o, idField))
                .filter(o -> o != null)
                .collect(Collectors.toSet());
        for (T element : list) {
            if (!ids.contains(getFieldValue(element, parentField))) {
                return element;
            }
        }
        throw new RuntimeException("can not get top element!");
    }

    /**
     * 多节点-获取最上层节点
     *
     * @param list        节点列表
     * @param parentField 父节点id字段名称
     * @param idField     当前节点id字段名称
     * @param <T>         节点类型
     * @return 最上层节点
     */
    public static <T> List<T> getTopElementByMultiNode(List<T> list, String parentField, String idField) {
        Assert.notEmpty(list, "list is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        Set<Object> ids = list.stream()
                .map(o -> getFieldValue(o, idField))
                .filter(o -> o != null)
                .collect(Collectors.toSet());
        List<T> topElementList = new ArrayList<>();
        for (T element : list) {
            if (!ids.contains(getFieldValue(element, parentField))) {
                topElementList.add(element);
            }
        }
        if (topElementList.isEmpty()) {
            throw new RuntimeException("can not get top element!");
        }

        return topElementList;
    }

    /**
     * 获取所有上级节点
     *
     * @param list           list
     * @param currentElement 当前节点
     * @param parentField    父节点id字段名称
     * @param idField        当前节点id字段名称
     * @param <T>            节点类型
     * @return 所有上级节点
     */
    public static <T> List<T> getParentElements(List<T> list, T currentElement, String parentField, String idField) {
        Assert.notNull(list, "list is required!");
        Assert.notNull(currentElement, "currentElement is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        List<T> parentElements = new ArrayList<>();
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        if (list.isEmpty()) {
            return parentElements;
        }

        Map<Object, T> idElementMap = list.stream()
                .collect(Collectors.toMap(o -> getFieldValue(o, idField), Function.identity()));
        T topElement = getTopElement(list, parentField, idField);
        Object topElementId = getFieldValue(topElement, idField);
        T parentElement = idElementMap.get(getFieldValue(currentElement, parentField));
        while (parentElement != null) {
            parentElements.add(parentElement);
            if (topElementId.equals(getFieldValue(parentElement, idField))) {
                break;
            }
            parentElement = idElementMap.get(getFieldValue(parentElement, parentField));
        }
        return parentElements;
    }

    /**
     * 获取所有下级节点
     *
     * @param list           list
     * @param currentElement 当前节点
     * @param parentField    父节点id字段名称
     * @param idField        当前节点id字段名称
     * @param <T>            节点类型
     * @return 所有下级节点
     */
    public static <T> List<T> getChildElements(List<T> list, T currentElement, String parentField, String idField) {
        Assert.notNull(list, "list is required!");
        Assert.notNull(currentElement, "currentElement is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        List<T> childElements = new ArrayList<>();
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        if (list.isEmpty()) {
            return childElements;
        }
        Map<Object, List<T>> parentChildMap = getParentChildMap(list, parentField);
        doGetChildElements(currentElement, idField, parentChildMap, childElements);
        return childElements;
    }

    /**
     * 递归获取当前节点的所有子节点
     *
     * @param currentElement 当前节点
     * @param idField        当前节点id字段名称
     * @param parentChildMap 父节点id -> 子节点 Map
     * @param childElements  子节点列表
     * @param <T>            节点类型
     */
    private static <T> void doGetChildElements(T currentElement, String idField, Map<Object, List<T>> parentChildMap,
                                               List<T> childElements) {
        List<T> childs = parentChildMap.get(getFieldValue(currentElement, idField));
        if (childs == null) {
            return;
        }
        childElements.addAll(childs);
        for (T child : childs) {
            doGetChildElements(child, idField, parentChildMap, childElements);
        }
    }

    /**
     * tree转list
     *
     * @param root       tree的根节点
     * @param childField 子节点列表字段名称
     * @param <T>        节点类型
     * @return list
     */
    public static <T> List<T> treeToList(T root, String childField) {
        Assert.notNull(root, "root is required!");
        Assert.hasText(childField, "childField is required!");
        List<T> list = new ArrayList<>();
        doGetList(root, childField, list);
        // 将子节点列表的字段值置空
        List<Object> emptyList = new ArrayList<>();
        for (T element : list) {
            setFieldValue(element, childField, emptyList);
        }
        return list;
    }

    /**
     * 多节点-tree转list
     *
     * @param treeList   树形数据
     * @param childField 子节点列表字段名称
     * @param <T>        节点类型
     * @return
     */
    public static <T> List<T> treeToListByMultiNode(List<T> treeList, String childField) {
        Assert.notEmpty(treeList, "treeList is required!");
        Assert.hasText(childField, "childField is required!");
        List<T> list = new ArrayList<>();
        for (T root : treeList) {
            List<T> listTemp = treeToList(root, childField);
            if (!listTemp.isEmpty()) {
                list.addAll(listTemp);
            }
        }
        return list;
    }

    /**
     * 递归获取子节点
     *
     * @param root       根节点
     * @param childField 子节点列表字段名称
     * @param list       list
     * @param <T>        节点类型
     */
    private static <T> void doGetList(T root, String childField, List<T> list) {
        if (root == null) {
            return;
        }
        list.add(root);
        List<T> childs = (List<T>) getFieldValue(root, childField);
        if (childs == null) {
            return;
        }
        for (T child : childs) {
            if (child == null) {
                continue;
            }
            doGetList(child, childField, list);
        }
    }

    /**
     * list转tree
     *
     * @param list        list
     * @param root        tree的根节点
     * @param parentField 父节点id字段名称
     * @param idField     当前节点id字段名称
     * @param childField  子节点列表字段名称
     * @param <T>         节点类型
     */
    public static <T> void listToTree(List<T> list, T root, String parentField, String idField, String childField) {
        Assert.notEmpty(list, "list is required!");
        Assert.notNull(root, "root is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        Assert.hasText(childField, "childField is required!");
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        Map<Object, List<T>> parentChildMap = getParentChildMap(list, parentField);
        if (parentChildMap.isEmpty()) {
            return;
        }
        doGetTree(root, idField, childField, parentChildMap);
    }

    /**
     * 多节点-list转tree
     *
     * @param list        list
     * @param parentField 父节点id字段名称
     * @param idField     当前节点id字段名称
     * @param childField  子节点列表字段名称
     * @param <T>         节点类型
     * @return 树结构数据
     */
    public static <T> List<T> listToTreeByMultiNode(List<T> list, String parentField, String idField,
                                                    String childField) {
        Assert.notEmpty(list, "list is required!");
        Assert.hasText(parentField, "parentField is required!");
        Assert.hasText(idField, "idField is required!");
        Assert.hasText(childField, "childField is required!");

        List<T> treeList = new ArrayList<>();
        List<T> topElementList = getTopElementByMultiNode(list, parentField, idField);
        for (T topElement : topElementList) {
            List<T> listTemp = list;
            listToTree(listTemp, topElement, parentField, idField, childField);
            treeList.add(topElement);
        }

        return treeList;
    }

    /**
     * 递归设置子节点属性
     *
     * @param root           根节点
     * @param idField        当前节点id字段名称
     * @param childField     子节点列表字段名称
     * @param parentChildMap 父节点id -> 子节点 Map
     * @param <T>            节点类型
     */
    private static <T> void doGetTree(T root, String idField, String childField, Map<Object, List<T>> parentChildMap) {
        Object rootId = getFieldValue(root, idField);
        List<T> childs = parentChildMap.get(rootId);
        if (childs == null) {
            childs = new ArrayList<>();
        }
        setFieldValue(root, childField, childs);
        for (T child : childs) {
            doGetTree(child, idField, childField, parentChildMap);
        }
    }

    /**
     * 获取父节点id -> 子节点 Map
     *
     * @param elements    节点列表
     * @param parentField 父节点id字段名称
     * @param <T>         节点类型
     * @return 父节点id -> 子节点 Map
     */
    private static <T> Map<Object, List<T>> getParentChildMap(List<T> elements, String parentField) {
        Map<Object, List<T>> parentChildMap = new HashMap<>();
        for (T element : elements) {
            Object parentId = getFieldValue(element, parentField);
            if (parentId == null) {
                continue;
            }
            List<T> childs = parentChildMap.get(parentId);
            if (childs == null) {
                childs = new ArrayList<>();
                parentChildMap.put(parentId, childs);
            }
            childs.add(element);
        }
        return parentChildMap;
    }

    /**
     * 获取字段值
     *
     * @param element   待获取值的对象
     * @param fieldName 字段名称
     * @param <T>       对象类型
     * @return 字段值
     * @throws NoSuchFieldException   字段不存在
     * @throws IllegalAccessException 没有字段访问权限
     */
    public static <T> Object getFieldValue(T element, String fieldName) {
        try {
            Field field = element.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(element);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * 设置字段值
     *
     * @param element   待设置值的对象
     * @param fieldName 字段名称
     * @param value     字段值
     * @param <T>       对象类型
     * @throws NoSuchFieldException   字段不存在
     * @throws IllegalAccessException 没有字段访问权限
     */
    private static <T> void setFieldValue(T element, String fieldName, Object value) {
        try {
            Field field = element.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(element, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}