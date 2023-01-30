package com.example.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 * @author lx362
 */
public interface TreeNode extends Serializable, Comparable<TreeNode> {

	/**
	 * ID
	 *
	 * @return
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	Long getId();

	/**
	 * 获取父节点ID
	 *
	 * @return
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	Long getParentId();

	/**
	 * 获取上一级表ID
	 *
	 * @return
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	default Long getPreviousId() {
		return 0L;
	}

	/**
	 * 获取名字
	 *
	 * @return
	 */
	String getName();

	/**
	 * 获取排序
	 *
	 * @return
	 */
	default int getSort() {
		return 0;
	}


	/**
	 * 设置下一个表的列表
	 * @param nextList
	 */
	default void setNextList(List<TreeNode> nextList) {

	}

	/**
	 * 获取下一个表的列表
	 * @return
	 */
	default List<TreeNode> getNextList() {
		return new ArrayList<>();
	}

	/**
	 * 设置孩子节点
	 * @param childList
	 */
	default void setChildList(List<TreeNode> childList) {

	}

	/**
	 * 获取孩子节点
	 *
	 * @return
	 */
	List<TreeNode> getChildList();

	/**
	 * 复制节点并忽略其孩子列表
	 *
	 * @return
	 */
	TreeNode copyWithoutTreeInfo();

	/**
	 * 是否有下一张表节点
	 *
	 * @return
	 */
	default boolean getHasNext() {
		return false;
	}

	/**
	 * 设置是否有下一张表的节点
	 * @param hasNext
	 */
	default void setHasNext(boolean hasNext) {

	}


	/**
	 * 设置选择状态
	 * @param choice
	 */
	default void setChoice(String choice) {

	}

	/**
	 * 是否有孩子节点
	 *
	 * @return
	 */
	boolean getHasChild();

	/**
	 * 设置是否有孩子节点
	 * @param hasChild
	 */
	void setHasChild(boolean hasChild);

	/**
	 * 获取叶子个数
	 *
	 * @return count
	 */
	default int getCountLeaf() {
		return 0;
	}

	/**
	 * 设置叶子个数
	 *
	 * @param count
	 */
	default void setCountLeaf(int count) {
	}

//	/**
//	 * 实现比较接口
//	 * @param o
//	 * @return
//	 */
//	@Override
//	default int compareTo(TreeNode o) {
//		if (null == o) {
//			return -1;
//		}
//		int nodeTypeSort = TreeUtil.nodeTypeSort(this.getClass()) - TreeUtil.nodeTypeSort(o.getClass());
//		if (0 != nodeTypeSort) {
//			return nodeTypeSort;
//		}
//		return this.getSort() - o.getSort();
//	}
}
